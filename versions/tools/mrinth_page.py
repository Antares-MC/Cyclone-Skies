"""Bring the Modrinth project body in line with what is actually published.

The page still says "for Minecraft 1.21.11" and "one jar"; the listing now carries one
jar per Minecraft version, and the 1.21.11 jar bundles paymentalerts with the stream
alert and the new splash. Edits are targeted replacements, so nothing else in the body
is touched.

    python mrinth_page.py --dry
    python mrinth_page.py
"""

import json
import os
import sys

import requests


def _file_token():
    """Token from MODRINTH_TOKEN, or ~/.modrinth_token when the variable is unset."""
    tok = os.environ.get('MODRINTH_TOKEN')
    if tok:
        return tok.strip()
    path = os.path.join(os.path.expanduser('~'), '.modrinth_token')
    if not os.path.exists(path):
        raise SystemExit('No Modrinth token: set MODRINTH_TOKEN or write ~/.modrinth_token')
    with open(path, encoding='utf-8') as fh:
        return fh.read().strip()


TOKEN = _file_token()
UA = 'cyclone-release/1.0 (github.com/Antares-MC/Cyclone-Skies)'
PROJECT = 'NOxtFhdm'
API = 'https://api.modrinth.com/v2'

REPLACEMENTS = [
    ('for Minecraft 1.21.11 that changes',
     'for Minecraft 1.21 through 26.1.2 that changes'),
    ('payment alerts, now-playing, synced lyrics',
     'payment and stream alerts, now-playing, synced lyrics'),
    ('Everything lives in a single jar and generates',
     'Everything ships in one jar per Minecraft version and generates'),
    # The page separates fields with U+00B7, not a dash.
    ('Client-side only \u00b7 Fabric 1.21.11 \u00b7 one jar',
     'Client-side only \u00b7 Fabric 1.21 \u2013 26.1.2 \u00b7 one jar per version'),
]

SECTION = """

**Versions and downloads**
Every Minecraft version gets its own file: 1.21, 1.21.1-1.21.11, and 26.1, 26.1.1, 26.1.2.

- **1.21.11** bundles paymentalerts: 150+ alert themes, 46 hand-animated effects, `/payalerts`, the title splash *subscribe to cyclonelwk and lumindev*, and a broadcast-style stream alert for youtube.com/@lumindevdsmp (`/payalerts stream`).
- **1.21.9 / 1.21.10** have no Fabric world-render event, so the custom sky and 3D cosmetics are inactive there.
- **26.x** needs Java 25. **26.2** is not published yet: it removed the buffered-geometry and text-batch APIs the sky and cosmetics render through, so it needs a dedicated port.

Fabric Loader 0.19.5 or newer and Fabric API are required.
"""


def headers():
    return {'Authorization': TOKEN, 'User-Agent': UA}


def main():
    dry = '--dry' in sys.argv
    r = requests.get('%s/project/%s' % (API, PROJECT), headers=headers(), timeout=60)
    r.raise_for_status()
    body = r.json()['body']

    new = body
    for old, rep in REPLACEMENTS:
        if old not in new:
            print('WARN  not found: %r' % old[:60])
            continue
        new = new.replace(old, rep)
        print('ok    %r -> %r' % (old[:45], rep[:45]))

    if '**Versions and downloads**' not in new:
        new = new.rstrip() + SECTION
        print('ok    appended versions section')
    else:
        print('skip  versions section already present')

    if dry:
        print('\n--- result (%d -> %d chars) ---' % (len(body), len(new)))
        print(new)
        return

    r = requests.patch('%s/project/%s' % (API, PROJECT), headers={**headers(),
                       'Content-Type': 'application/json'},
                       data=json.dumps({'body': new}), timeout=120)
    print('patch', r.status_code, r.text[:200] if r.status_code >= 300 else 'ok')


if __name__ == '__main__':
    main()
