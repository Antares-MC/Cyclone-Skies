"""Query yarn mappings: list members of a class across a version.

    python versions/tools/mq.py 1.21.5 class_4587
    python versions/tools/mq.py 1.21.5 class_332 method_252

Version keys match the yarn jars cached in %TEMP%\opencode\mcv\mapcheck
(yarn-<ver>_build.*-v2.jar); pass the file name prefix without "yarn-".
"""
import glob
import os
import re
import sys
import zipfile

base = os.path.join(os.environ['TEMP'], 'opencode', 'mcv', 'mapcheck')
ver = sys.argv[1]
needle = sys.argv[2]
pat = sys.argv[3] if len(sys.argv) > 3 else None

cands = [p for p in glob.glob(os.path.join(base, 'yarn-%s_*.jar' % ver))]
if not cands:
    raise SystemExit('no yarn jar for %s: %s' % (ver, cands))
z = zipfile.ZipFile(sorted(cands, key=len)[0])
text = z.read('mappings/mappings.tiny').decode('utf8', 'replace').splitlines()

hits = []
for i, line in enumerate(text):
    if line.startswith('c\t'):
        parts = line.split('\t')
        if parts[1] == needle or parts[2] == needle or parts[2].endswith('/' + needle) or parts[1].endswith('/' + needle):
            hits.append(i)

for i in hits:
    print(text[i])
    j = i + 1
    while j < len(text) and text[j].startswith('\t'):
        p = text[j].split('\t')
        if len(p) > 3 and p[1] in ('m', 'f'):
            line = '%s %-42s %-18s %s' % (p[1], p[3], p[4] if len(p) > 4 else '', p[2])
        else:
            line = text[j]
        if pat is None or re.search(pat, line):
            print('   ', line.strip())
        j += 1
if not hits:
    print('class not found:', needle)
