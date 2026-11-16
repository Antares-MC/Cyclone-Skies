"""Find the intermediary id of a yarn class name in one version."""

import glob
import os
import sys
import zipfile

base = os.path.join(os.environ['TEMP'], 'opencode', 'mcv', 'mapcheck')
ver = sys.argv[1]
needle = sys.argv[2].lower()

cands = glob.glob(os.path.join(base, 'yarn-%s_*.jar' % ver))
if not cands:
    raise SystemExit('no yarn jar for ' + ver)
z = zipfile.ZipFile(sorted(cands, key=len)[0])
text = z.read('mappings/mappings.tiny').decode('utf8', 'replace').splitlines()

for line in text:
    if not line.startswith('c\t'):
        continue
    parts = line.split('\t')
    if len(parts) > 2 and needle in parts[2].lower():
        print(parts[1], '->', parts[2])
