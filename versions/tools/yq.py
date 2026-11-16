"""List the members of a class whose yarn name or intermediary id matches a pattern."""

import glob
import os
import re
import sys
import zipfile

base = os.path.join(os.environ['TEMP'], 'opencode', 'mcv', 'mapcheck')
ver = sys.argv[1]
needle = sys.argv[2]
pat = sys.argv[3] if len(sys.argv) > 3 else None

cands = glob.glob(os.path.join(base, 'yarn-%s_*.jar' % ver))
if not cands:
    raise SystemExit('no yarn jar for %s' % ver)
z = zipfile.ZipFile(sorted(cands, key=len)[0])
text = z.read('mappings/mappings.tiny').decode('utf8', 'replace').splitlines()

for i, line in enumerate(text):
    if not line.startswith('c\t'):
        continue
    parts = line.split('\t')
    if parts[1] != needle and not parts[1].endswith('/' + needle) and not parts[2].endswith('/' + needle):
        continue
    print(line)
    j = i + 1
    while j < len(text) and text[j].startswith('\t'):
        p = text[j].split('\t')
        rendered = text[j]
        if len(p) > 4 and p[1] in ('m', 'f'):
            rendered = '%s %-46s %-22s %s' % (p[1], p[3], p[4], p[2])
        if pat is None or re.search(pat, rendered):
            print('   ', rendered.strip())
        j += 1
