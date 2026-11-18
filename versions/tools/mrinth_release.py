"""Publish the CycloneSkies jar matrix to Modrinth.

Project: https://modrinth.com/mod/cycloneskies (id NOxtFhdm, owner LuminaPlugins).

    python mrinth_release.py           # upload what is missing
    python mrinth_release.py --dry     # print the plan only
    python mrinth_release.py --clean   # upload, then drop the legacy bare-1.9.9 entries

Each Minecraft version gets its own Modrinth version entry named
`1.9.9+mc<ver>`, carrying exactly the jar that was published to GitHub.
"""

import json
import os
import sys

import requests


ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


def _file_token():
    """Token from MODRINTH_TOKEN, versions/.modrinth_token, or ~/.modrinth_token."""
    tok = os.environ.get('MODRINTH_TOKEN')
    if tok:
        return tok.strip()
    candidates = [
        os.path.join(ROOT, 'versions', '.modrinth_token'),
        os.path.join(ROOT, '.modrinth_token'),
        os.path.join(os.path.expanduser('~'), '.modrinth_token'),
    ]
    for path in candidates:
        if os.path.exists(path):
            with open(path, encoding='utf-8') as fh:
                return fh.read().strip()
    raise SystemExit('No Modrinth token: set MODRINTH_TOKEN, or write one to versions/.modrinth_token')


TOKEN = _file_token()
UA = 'cyclone-release/1.0 (github.com/Antares-MC/Cyclone-Skies)'
PROJECT = 'NOxtFhdm'
FABRIC_API = 'P7dR8mSH'
MOD_VERSION = '1.9.9'
ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
JAR_DIR = os.path.join(ROOT, 'versions', 'dist')
LEGACY_JAR_DIR = os.path.join(os.path.expanduser('~'), 'Downloads', 'CycloneJars')
API = 'https://api.modrinth.com/v2'

VERSIONS = ['1.21', '1.21.1', '1.21.2', '1.21.3', '1.21.4', '1.21.5',
            '1.21.6', '1.21.7', '1.21.8', '1.21.9', '1.21.10', '1.21.11',
            '26.1', '26.1.1', '26.1.2', '26.2']

PAYMENTS = {'1.21.11'}

HEAD = """\
CycloneSkies {mod} for Minecraft {mc}, built from the same source tree as every other version \
in this release.

**What is in it**
- Cyclone menu theme (default on a fresh install) with Nebula, Ember and Jade palettes.
- Cosmetics: 37 hats, 19 wings that replace the cape, 49 capes, all previewed in 3D in the menu.
- Sky engine: custom overworld and End skies with lyrics, shooting stars and constellations.
- HUD set: glass hotbar, crosshair, song card, cover art, beat glow, island timer, lyrics.
- Emotes with props, visible to every client running the mod.
- Performance: occlusion culling, background framerate cap, low fire, small totems.
"""

PAY = """\
- **paymentalerts** nested inside the jar: 150+ alert themes, 46 hand-animated effects, \
`/payalerts`, and the title splash reading *subscribe to cyclonelwk and lumindev*.
- The addon's screens use the Cyclone theme: same palette, glass panel, card rows and sliders \
as the menu, drawn with no bundled textures.
- **Stream alert** for youtube.com/@lumindevdsmp: a 9.4 s broadcast takeover fired by chat \
detection or `/payalerts stream`.
"""

NOPAY = """\
- paymentalerts is not nested in this jar; the addon is compiled against 1.21.11 names, so it \
has no build for this Minecraft version yet. Everything else is present.
"""

GAP_OLD = """\
**Note:** 1.21.9 / 1.21.10 have no Fabric world-render event, so the custom sky and the 3D \
cosmetics are inactive on this version.
"""

FOOT = """\
Requires Fabric Loader 0.19.5+ and Fabric API. Java {java}.
"""


def java_for(ver):
    return '25 or newer' if ver.startswith('26.') else '21 or newer'


def changelog(ver):
    text = HEAD.format(mod=MOD_VERSION, mc=ver)
    text += PAY if ver in PAYMENTS else NOPAY
    if ver in ('1.21.9', '1.21.10'):
        text += '\n' + GAP_OLD
    text += '\n' + FOOT.format(java=java_for(ver))
    return text


def headers():
    return {'Authorization': TOKEN, 'User-Agent': UA}


def existing_versions():
    r = requests.get('%s/project/%s/version' % (API, PROJECT), headers=headers(), timeout=60)
    r.raise_for_status()
    return r.json()


def publish(ver, path, dry):
    number = '%s+mc%s' % (MOD_VERSION, ver)
    payload = {
        'name': 'Cyclone Skies %s for Minecraft %s' % (MOD_VERSION, ver),
        'version_number': number,
        'changelog': changelog(ver),
        'dependencies': [{'project_id': FABRIC_API, 'dependency_type': 'required'}],
        'game_versions': [ver],
        'version_type': 'release',
        'loaders': ['fabric'],
        'featured': False,
        'project_id': PROJECT,
        'file_parts': ['file'],
        'primary_file': 'file',
        'status': 'listed',
    }
    if dry:
        print('%-8s would upload %-34s as %s' % (ver, os.path.basename(path), number))
        return None
    with open(path, 'rb') as fh:
        files = {
            'data': (None, json.dumps(payload), 'application/json'),
            'file': (os.path.basename(path), fh, 'application/java-archive'),
        }
        r = requests.post('%s/version' % API, headers=headers(), files=files, timeout=900)
    if r.status_code not in (200, 201):
        print('%-8s FAILED %s %s' % (ver, r.status_code, r.text[:300]))
        return None
    data = r.json()
    file_info = data['files'][0]
    print('%-8s ok  %-12s %9d  %s' % (ver, data['id'], file_info['size'], file_info['filename']))
    return data


def main():
    dry = '--dry' in sys.argv
    clean = '--clean' in sys.argv
    force = '--force' in sys.argv

    current = existing_versions()
    have = {(v['version_number'], tuple(v['game_versions'])) for v in current}
    numbers = {v['version_number'] for v in current}
    print('existing versions:', len(current))
    for v in current:
        print('  %-22s %-20s %s' % (v['version_number'], ','.join(v['game_versions']),
                                    [f['filename'] for f in v['files']]))

    # --force replaces the file of a version that is already published, which is what a
    # rebuilt jar needs: Modrinth has no "replace file" call, so the entry is dropped and
    # put back with the same name, changelog and dependency.
    if force and not dry:
        for v in current:
            if v['version_number'] in {'%s+mc%s' % (MOD_VERSION, ver) for ver in VERSIONS}:
                r = requests.delete('%s/version/%s' % (API, v['id']), headers=headers(), timeout=120)
                print('replace  %-22s delete %s' % (v['version_number'], r.status_code))
        current = existing_versions()
        numbers = {v['version_number'] for v in current}

    uploaded = []
    for ver in VERSIONS:
        number = '%s+mc%s' % (MOD_VERSION, ver)
        if number in numbers:
            print('%-8s exists   %s' % (ver, number))
            continue
        candidates = [
            os.path.join(JAR_DIR, 'cycloneskies-%s-%s.jar' % (MOD_VERSION, ver)),
            os.path.join(JAR_DIR, 'cycloneskies-%s-mc%s.jar' % (MOD_VERSION, ver)),
            os.path.join(LEGACY_JAR_DIR, 'cycloneskies-%s-mc%s.jar' % (MOD_VERSION, ver)),
            os.path.join(ROOT, 'versions', 'v' + ver.replace('.', '_'), 'build', 'libs',
                         'cycloneskies-%s-%s.jar' % (ver, MOD_VERSION)),
        ]
        path = next((c for c in candidates if os.path.exists(c)), None)
        if path is None:
            print('%-8s MISSING  %s' % (ver, 'cycloneskies-%s-%s.jar' % (MOD_VERSION, ver)))
            continue
        name = os.path.basename(path)
        data = publish(ver, path, dry)
        if data:
            uploaded.append(data)

    if dry or not clean:
        return

    # Drop the two hand-made 1.9.9 entries: one carries the pre-splash 1.21.11 jar,
    # the other duplicates 26.1.2 under a name that does not say which version it is.
    for v in existing_versions():
        if v['version_number'] == MOD_VERSION:
            r = requests.delete('%s/version/%s' % (API, v['id']), headers=headers(), timeout=120)
            print('deleted legacy %-14s %s %s' % (v['id'], r.status_code,
                                                  [f['filename'] for f in v['files']]))
    print('final versions:', len(existing_versions()))


if __name__ == '__main__':
    main()
