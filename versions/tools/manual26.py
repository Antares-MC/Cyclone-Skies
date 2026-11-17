"""Show where each still-unresolved symbol is used, and what the owner offers.

    python versions/tools/manual26.py 26.1.2

For every `MANUAL` line in the last emitter run this prints the call site, then
the owner's members with the same arity, so a rename can be chosen by eye.
"""

import io
import os
import re
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(os.path.dirname(HERE))
sys.path.insert(0, HERE)

import jidx  # noqa: E402
import mojemit  # noqa: E402

ver = sys.argv[1]
gen = os.path.join(ROOT, 'versions', 'gen', 'v' + ver.replace('.', '_'), 'java')

files = {}
for root, _d, names in os.walk(gen):
    for n in names:
        if n.endswith('.java'):
            p = os.path.join(root, n)
            files[p] = io.open(p, encoding='utf8', errors='replace').read()


def sites(tok):
    out = []
    for p, text in files.items():
        for i, line in enumerate(text.splitlines(), 1):
            if tok in line:
                out.append('%s:%d: %s' % (os.path.relpath(p, gen), i, line.strip()))
    return out


res = mojemit.Resolver(ver)

# Re-derive the manual set from the emitter run rather than guessing at it.
with open(os.path.join(os.environ['TEMP'], 'opencode', 'moj26.log'), 'rb') as fh:
    raw = fh.read()
try:
    log = raw.decode('utf8')
except UnicodeDecodeError:
    log = raw.decode('utf-16', 'replace')

for m in re.finditer(r'^MANUAL (class|method|field) (\S+?)\r?$', log, re.M):
    kind, name = m.group(1), m.group(2)
    print('=' * 78)
    print(kind, name)
    if kind == 'class':
        for s in sites(name.rsplit('.', 1)[-1]):
            print('   ', s)
        continue
    for s in sites(name):
        print('   ', s)
    owner_obf = res.owner_of.get(name)
    desc = res.desc_of.get(name)
    if owner_obf is None:
        print('    (no owner row)')
        continue
    owner = res.cls(res.obf_to_inter[owner_obf])
    want = res.desc26(desc) if name.startswith('method_') else res.type26(desc)
    print('    -> %s%s' % (owner, want))
    arity = mojemit.split_desc(want)[0]
    seen = set()
    for member in jidx.info(ver, owner).get('members', []):
        if member['kind'] != ('m' if name.startswith('method_') else 'f'):
            continue
        p, r = mojemit.split_desc(member['desc']) if member['kind'] == 'm' else ([], '')
        if member['kind'] == 'm' and len(p) != len(arity):
            continue
        row = '%s%s' % (member['name'], member['desc'])
        if row in seen:
            continue
        seen.add(row)
        print('        ', row)
