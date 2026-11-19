"""Publish one GitHub release per built Minecraft version.

    python versions/tools/release.py            # create what is missing
    python versions/tools/release.py --force    # also replace the asset and body
    python versions/tools/release.py --dry      # print the plan only

Each release carries the jar compiled for exactly that Minecraft version, and the
notes repeat the jar's size and SHA-256 so a download can be verified. Auth comes
from the git credential store, so no token is written to disk.
"""

import hashlib
import json
import os
import subprocess
import sys
import urllib.error
import urllib.request

REPO = 'Antares-MC/Cyclone-Skies'
ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
DOWNLOADS = os.path.join(os.path.expanduser('~'), 'Downloads')
JAR_DIRS = [os.path.join(ROOT, 'versions', 'dist'),
            os.path.join(DOWNLOADS, 'CycloneJars'), DOWNLOADS]
MOD_VERSION = '1.9.9'
API = 'https://api.github.com'

VERSIONS = ['1.21', '1.21.1', '1.21.2', '1.21.3', '1.21.4', '1.21.5',
            '1.21.6', '1.21.7', '1.21.8', '1.21.9', '1.21.10', '1.21.11',
            '26.1', '26.1.1', '26.1.2', '26.2']

# Versions whose jar nests paymentalerts inside it.
PAYMENTS = {'1.21.11'}

NOTES_SHARED = """\
**Bug-fix patch:** fixes remote emote stop handling and refreshes emote/cosmetic state when joining
Minecraft servers, preventing remote emotes and capes from becoming stale until both clients rejoin.

CycloneSkies and the bundled paymentalerts mod in one Fabric jar.

**What is in it**
- Cyclone menu theme, default on a fresh install, with Nebula, Ember and Jade palettes.
- Cosmetics: 37 hats, 19 wings (they replace the cape) and 49 capes, all previewed in 3D
  in the menu and rendered in world.
- Sky engine: custom overworld and End skies with lyrics, shooting stars, constellations
  and per-sky settings.
- HUD set: glass hotbar, crosshair, song card, cover art, beat glow, island timer, lyrics.
- Emotes with props, played from the emote wheel and seen by every client running the mod.
- Performance: occlusion culling, background framerate cap, low fire, small totems.
{payments}

**Install**
1. Fabric Loader 0.19.5 or newer for Minecraft {mc}.
2. Fabric API for Minecraft {mc}.
3. Drop the jar in `.minecraft/mods`.
4. Java {java}.

**This build**
`{asset}`
size `{size}` bytes
sha256 `{sha}`
"""

NOTES_PAYMENTS = """\
- paymentalerts inside the jar: 150+ alert themes, 46 hand-animated payment effects,
  `/payalerts`, and 46 bespoke effect classes with no shared preset renderer.
- The addon's screens (settings, effects list, leaderboard) are drawn in the Cyclone theme:
  the same palette, glass panel, card rows and sliders as the menu, with no bundled textures.
- The title-screen splash reads **subscribe to cyclonelwk and lumindev**.
- **Stream alert** for `youtube.com/@lumindevdsmp`: a 9.4 s broadcast takeover (standby
  bars, hard cut to air, lens aperture, live lower third, chat chips, viewer counter,
  subscribe press, wipe to an end card). Fires on chat detection or `/payalerts stream`.
"""

NOTES_NOPAYMENTS = """\
- paymentalerts is not nested in this jar: the addon is compiled against 1.21.11 names, so
  there is no build of it for this Minecraft version yet. Everything else is fully present.
"""

NOTES_GAP = """
**Known gap on 1.21.9 and 1.21.10**: Fabric has no world-render event on those two
releases, so the custom sky and the in-world 3D cosmetics are inactive there. The menu,
cosmetics preview, HUD, emotes UI, paymentalerts and performance work all still run.
"""


def token():
    out = subprocess.run(['git', 'credential', 'fill'], input='protocol=https\nhost=github.com\n\n',
                         capture_output=True, text=True).stdout
    for line in out.splitlines():
        if line.startswith('password='):
            return line.split('=', 1)[1]
    raise SystemExit('no stored credential for github.com')


def call(method, path, tok, body=None, raw=None, ctype='application/json'):
    url = path if path.startswith('http') else API + path
    data = raw if raw is not None else (json.dumps(body).encode() if body is not None else None)
    req = urllib.request.Request(url, data=data, method=method)
    req.add_header('Authorization', 'Bearer ' + tok)
    req.add_header('Accept', 'application/vnd.github+json')
    req.add_header('X-GitHub-Api-Version', '2022-11-28')
    req.add_header('Content-Type', ctype)
    try:
        with urllib.request.urlopen(req) as resp:
            return resp.status, json.loads(resp.read().decode() or '{}')
    except urllib.error.HTTPError as exc:
        return exc.code, json.loads(exc.read().decode() or '{}')


def sha256(path):
    digest = hashlib.sha256()
    with open(path, 'rb') as fh:
        for chunk in iter(lambda: fh.read(1 << 20), b''):
            digest.update(chunk)
    return digest.hexdigest().upper()


def jar_for(ver):
    name = 'cycloneskies-%s-mc%s.jar' % (MOD_VERSION, ver)
    candidates = [os.path.join(base, name) for base in JAR_DIRS]
    candidates += [
        os.path.join(ROOT, 'versions', 'dist', 'cycloneskies-%s-%s.jar' % (MOD_VERSION, ver)),
        os.path.join(ROOT, 'versions', 'v' + ver.replace('.', '_'), 'build', 'libs',
                     'cycloneskies-%s-%s.jar' % (ver, MOD_VERSION)),
    ]
    for path in candidates:
        if os.path.exists(path):
            return os.path.basename(path), path
    return name, None


def java_for(ver):
    return '25 or newer' if ver.startswith('26.') else '21 or newer'


def release_by_tag(tok):
    """tag -> release dict for every release in the repo."""
    out = {}
    page = 1
    while True:
        status, rows = call('GET', '/repos/%s/releases?per_page=100&page=%d' % (REPO, page), tok)
        if status != 200 or not rows:
            break
        for row in rows:
            out[row['tag_name']] = row
        if len(rows) < 100:
            break
        page += 1
    return out


def replace_asset(rel, name, path, tok):
    """Drop the old asset of this name and upload the new file."""
    for asset in rel.get('assets', []):
        if asset['name'] == name:
            call('DELETE', '/repos/%s/releases/assets/%d' % (REPO, asset['id']), tok)
    upload = rel['upload_url'].split('{', 1)[0] + '?name=' + name
    with open(path, 'rb') as fh:
        data = fh.read()
    return call('POST', upload, tok, raw=data, ctype='application/java-archive')


def asset_matches(rel, name, size):
    """True when the release already carries this asset at this byte size."""
    for asset in rel.get('assets', []):
        if asset['name'] == name and asset.get('size') == size:
            return True
    return False


def main():
    dry = '--dry' in sys.argv
    force = '--force' in sys.argv
    tok = None if dry else token()

    existing = {}
    if not dry:
        existing = release_by_tag(tok)
        print('existing releases:', len(existing))

    for ver in VERSIONS:
        name, path = jar_for(ver)
        if path is None:
            print('%-8s MISSING %s' % (ver, name))
            continue
        size = os.path.getsize(path)
        digest = sha256(path)
        tag = 'v%s-mc%s' % (MOD_VERSION, ver)
        body = NOTES_SHARED.format(
            mc=ver, asset=name, size=size, sha=digest, java=java_for(ver),
            payments=(NOTES_PAYMENTS if ver in PAYMENTS else NOTES_NOPAYMENTS).rstrip())
        if ver in ('1.21.9', '1.21.10'):
            body += NOTES_GAP
        title = 'Cyclone Skies %s for Minecraft %s' % (MOD_VERSION, ver)

        if dry:
            print('%-8s %s %s  %s' % (ver, 'would update' if tag in existing else 'would publish', tag, name))
            continue

        if tag in existing:
            rel = existing[tag]
            if not force:
                print('%-8s exists   %s' % (ver, tag))
                continue
            status, patched = call('PATCH', '/repos/%s/releases/%d' % (REPO, rel['id']), tok,
                                   body={'body': body, 'name': title})
            if status not in (200, 201):
                print('%-8s patch failed %s %s' % (ver, status, patched.get('message')))
                continue
            if asset_matches(rel, name, size):
                print('%-8s notes    %12d  %s  %s' % (ver, size, digest[:16], rel['html_url']))
                continue
            status, asset = replace_asset(rel, name, path, tok)
            if status not in (200, 201):
                print('%-8s asset failed %s %s' % (ver, status, asset.get('message')))
                continue
            print('%-8s updated  %12d  %s  %s' % (ver, size, digest[:16], rel['html_url']))
            continue

        status, rel = call('POST', '/repos/%s/releases' % REPO, tok, body={
            'tag_name': tag,
            'target_commitish': 'main',
            'name': title,
            'body': body,
            'draft': False,
            'prerelease': False,
        })
        if status not in (200, 201):
            print('%-8s release failed %s %s' % (ver, status, rel.get('message')))
            continue

        status, asset = replace_asset(rel, name, path, tok)
        if status not in (200, 201):
            print('%-8s asset failed %s %s' % (ver, status, asset.get('message')))
            continue
        print('%-8s %12d  %s  %s' % (ver, size, digest[:16], rel['html_url']))


if __name__ == '__main__':
    main()
