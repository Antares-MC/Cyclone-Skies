"""Find a Modrinth project using the authenticated user's project list.

Usage from the repository root or versions/tools:
    python versions/tools/mrinth_lookup.py [cycloneskies]

Token sources are MODRINTH_TOKEN, versions/.modrinth_token, then
~/.modrinth_token. The token is never printed or written by this script.
"""

import os
import sys

import requests

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
API = 'https://api.modrinth.com/v2'
UA = 'cyclone-release/1.0 (github.com/Antares-MC/Cyclone-Skies)'


def read_token():
    token = os.environ.get('MODRINTH_TOKEN', '').strip()
    if token:
        return token
    paths = [
        os.path.join(ROOT, 'versions', '.modrinth_token'),
        os.path.join(ROOT, '.modrinth_token'),
        os.path.join(os.path.expanduser('~'), '.modrinth_token'),
    ]
    for path in paths:
        if os.path.isfile(path):
            with open(path, encoding='utf-8') as f:
                token = f.read().strip()
            if token:
                return token
    raise SystemExit('No Modrinth token found. Set MODRINTH_TOKEN or save it in versions/.modrinth_token.')


def main():
    query = (sys.argv[1] if len(sys.argv) > 1 else 'cycloneskies').casefold()
    headers = {'Authorization': read_token(), 'User-Agent': UA}
    try:
        response = requests.get(API + '/user/projects', headers=headers, timeout=60)
    except requests.RequestException as exc:
        raise SystemExit('Could not contact Modrinth: %s' % exc)

    if response.status_code == 401:
        raise SystemExit('Modrinth rejected the token (401). Check that it is valid and has project access.')
    if response.status_code == 403:
        raise SystemExit('Modrinth denied access to the user-project list (403). Check token permissions.')
    if not response.ok:
        raise SystemExit('Modrinth returned HTTP %s for /user/projects: %s' %
                         (response.status_code, response.text[:300]))

    try:
        projects = response.json()
    except ValueError:
        raise SystemExit('Modrinth returned invalid JSON for /user/projects.')

    matches = [p for p in projects if query in str(p.get('slug', '')).casefold()
               or query in str(p.get('title', '')).casefold()
               or query in str(p.get('id', '')).casefold()]
    if not matches:
        print('No accessible project matched %r. Authenticated project count: %d' %
              (query, len(projects)))
        print('Available projects:')
        for project in projects:
            print('  %s  %s  (%s)' % (project.get('id'), project.get('slug'), project.get('title')))
        raise SystemExit(1)

    print('Matching accessible Modrinth project(s):')
    for project in matches:
        print('  project_id: %s' % project.get('id'))
        print('  slug:       %s' % project.get('slug'))
        print('  title:      %s' % project.get('title'))
        print('  status:     %s' % project.get('status'))


if __name__ == '__main__':
    main()
