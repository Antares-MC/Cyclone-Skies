"""Publish the CycloneSkies jar matrix to Modrinth.

Project: https://modrinth.com/mod/cycloneskies (id NOxtFhdm, owner LuminaPlugins).

    python versions/tools/mrinth_release.py --dry
    python versions/tools/mrinth_release.py --versions 26.2
    MODRINTH_TOKEN=... python versions/tools/mrinth_release.py

Set MODRINTH_TOKEN, or save it in versions/.modrinth_token (git-ignored).
By default, existing Modrinth version numbers are skipped; --clean additionally
prompts before deleting legacy bare-1.9.9 entries.

Each Minecraft version gets its own Modrinth version entry named
`1.9.9+mc<ver>`, carrying exactly the jar that was published to GitHub.
"""

import argparse
import json
import os
import sys
import zipfile

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


def headers(token):
    return {'Authorization': token, 'User-Agent': UA}


def jar_for(ver):
    candidates = [
        os.path.join(JAR_DIR, 'cycloneskies-%s-%s.jar' % (MOD_VERSION, ver)),
        os.path.join(JAR_DIR, 'cycloneskies-%s-mc%s.jar' % (MOD_VERSION, ver)),
        os.path.join(LEGACY_JAR_DIR, 'cycloneskies-%s-mc%s.jar' % (MOD_VERSION, ver)),
        os.path.join(ROOT, 'versions', 'v' + ver.replace('.', '_'), 'build', 'libs',
                     'cycloneskies-%s-%s.jar' % (ver, MOD_VERSION)),
    ]
    return next((path for path in candidates if os.path.isfile(path)), None)


def validate_jar(ver, path):
    if os.path.getsize(path) == 0:
        raise ValueError('empty jar: ' + path)
    with zipfile.ZipFile(path) as jar:
        metadata = json.loads(jar.read('fabric.mod.json'))
    expected_java = '>=25' if ver.startswith('26.') else '>=21'
    if metadata.get('id') != 'cycloneskies':
        raise ValueError('%s has unexpected mod id %r' % (path, metadata.get('id')))
    depends = metadata.get('depends', {})
    if depends.get('minecraft') != '~' + ver:
        raise ValueError('%s targets Minecraft %r, expected ~%s' %
                         (path, depends.get('minecraft'), ver))
    if depends.get('java') != expected_java:
        raise ValueError('%s requires Java %r, expected %s' %
                         (path, depends.get('java'), expected_java))


def parse_args():
    parser = argparse.ArgumentParser(description='Upload CycloneSkies jars to Modrinth.')
    parser.add_argument('--dry', action='store_true', help='show the upload plan without changing Modrinth')
    parser.add_argument('--versions', nargs='+', choices=VERSIONS, metavar='MC_VERSION',
                        help='Minecraft versions to process; default: all supported versions')
    parser.add_argument('--clean', action='store_true',
                        help='remove legacy bare 1.9.9 entries after upload (asks for confirmation)')
    return parser.parse_args()


def existing_versions(token):
    r = requests.get('%s/project/%s/version' % (API, PROJECT), headers=headers(token), timeout=60)
    r.raise_for_status()
    return r.json()


def publish(ver, path, dry, token):
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
        r = requests.post('%s/version' % API, headers=headers(token), files=files, timeout=900)
    if r.status_code not in (200, 201):
        print('%-8s FAILED %s %s' % (ver, r.status_code, r.text[:300]))
        return None
    data = r.json()
    file_info = data['files'][0]
    print('%-8s ok  %-12s %9d  %s' % (ver, data['id'], file_info['size'], file_info['filename']))
    return data


def main():
    args = parse_args()
    selected = args.versions or VERSIONS

    # Resolve and validate every local artifact before making any API request. This
    # prevents partial releases caused by a missing or mislabelled jar later in the matrix.
    artifacts = {}
    for ver in selected:
        path = jar_for(ver)
        if path is None:
            raise SystemExit('%s: no jar found; build it first' % ver)
        try:
            validate_jar(ver, path)
        except (OSError, ValueError, KeyError, zipfile.BadZipFile, json.JSONDecodeError) as exc:
            raise SystemExit('%s: invalid artifact: %s' % (ver, exc))
        artifacts[ver] = path

    if args.dry:
        for ver, path in artifacts.items():
            publish(ver, path, True, token=None)
        return

    token = _file_token()
    current = existing_versions(token)
    numbers = {v['version_number'] for v in current}
    print('existing versions:', len(current))
    for v in current:
        print('  %-22s %-20s %s' % (v['version_number'], ','.join(v['game_versions']),
                                    [f['filename'] for f in v['files']]))

    uploaded = []
    for ver in selected:
        number = '%s+mc%s' % (MOD_VERSION, ver)
        if number in numbers:
            print('%-8s exists   %s' % (ver, number))
            continue
        path = artifacts[ver]
        name = os.path.basename(path)
        data = publish(ver, path, args.dry, token)
        if data:
            uploaded.append(data)

    if args.dry or not args.clean:
        return

    legacy = [v for v in existing_versions(token) if v['version_number'] == MOD_VERSION]
    if legacy:
        answer = input('Delete %d legacy bare %s version(s) from Modrinth? [y/N] ' %
                       (len(legacy), MOD_VERSION)).strip().lower()
        if answer != 'y':
            print('legacy versions left unchanged')
            return
        for v in legacy:
            r = requests.delete('%s/version/%s' % (API, v['id']), headers=headers(token), timeout=120)
            r.raise_for_status()
            print('deleted legacy %-14s %s' % (v['id'], [f['filename'] for f in v['files']]))
    print('final versions:', len(existing_versions(token)))


if __name__ == '__main__':
    main()
