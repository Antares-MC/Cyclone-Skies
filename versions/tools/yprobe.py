"""Probe the yarn mapping jars: names, sizes, and whether a class id appears."""

import glob
import os
import sys
import zipfile

base = os.path.join(os.environ['TEMP'], 'opencode', 'mcv', 'mapcheck')
ver = sys.argv[1]
needle = sys.argv[2] if len(sys.argv) > 2 else 'class_761'

for p in sorted(glob.glob(os.path.join(base, 'yarn-%s_*' % ver))):
    print(os.path.basename(p), os.path.getsize(p))
    try:
        z = zipfile.ZipFile(p)
    except Exception as exc:
        print('   unreadable:', exc)
        continue
    names = z.namelist()
    print('   entries:', names[:12])
    for n in names:
        if not n.endswith('.tiny'):
            continue
        text = z.read(n).decode('utf8', 'replace')
        print('   %s lines=%d has_needle=%s' % (n, text.count('\n'), needle in text))
        for line in text.splitlines():
            if needle in line:
                print('     ', line.replace('\t', ' | '))
                break
