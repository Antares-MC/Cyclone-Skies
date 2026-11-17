"""Why did each symbol stay unresolved, and what are the candidate names?

    python versions/tools/why26.py 26.1.2

Prints one line per MANUAL symbol: the id, the 26 owner, the 26 descriptor, the
names that share that exact descriptor, and whether the 1.21.11 name is known
and present. This is the table a `members` pin is chosen from.
"""

import os
import re
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)

import jidx  # noqa: E402
import mojemit  # noqa: E402

ver = sys.argv[1]
res = mojemit.Resolver(ver)

log = open(os.path.join(os.environ['TEMP'], 'opencode', 'moj26.log'), 'rb').read()
try:
    log = log.decode('utf8')
except UnicodeDecodeError:
    log = log.decode('utf-16', 'replace')

ids = [m.group(2) for m in re.finditer(r'^MANUAL (method|field) (\S+?)\r?$', log, re.M)]
for name in ids:
    owner_obf = res.owner_of.get(name)
    desc = res.desc_of.get(name)
    if owner_obf is None or desc is None:
        print('%-14s  no tiny row' % name)
        continue
    owner = res.cls(res.obf_to_inter[owner_obf])
    kind = 'm' if name.startswith('method_') else 'f'
    want = res.desc26(desc) if kind == 'm' else res.type26(desc)
    hits = jidx.find(ver, owner, kind=kind, desc=want)
    names = sorted(set(h[1] for h in hits))
    known = res.name11.get(name)
    present = known in names if known else None
    print('%-14s %-58s %-52s %s' % (name, owner.rsplit('.', 1)[-1], want,
                                    ('known=%s present=%s' % (known, present))
                                    if known else 'names=' + ','.join(names[:6])))
