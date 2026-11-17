"""Where do a few class ids live? Intermediary tiny (plain), yarn tiny (jar), Mojang."""

import io
import os
import zipfile

TEMP = os.path.join(os.environ['TEMP'], 'opencode')
TINY = os.path.join(os.path.expanduser('~'), '.gradle', 'caches', 'fabric-loom',
                    '1.21.11', 'intermediary-v2.tiny')
YARN = os.path.join(TEMP, 'mcv', 'mapcheck', 'yarn-1.21.11_build.6-v2.jar')
MOJ = os.path.join(TEMP, 'client_1_21_11.txt')

WANT = ['class_6415', 'class_2339', 'class_306', 'class_307', 'class_4665',
        'class_4598', 'class_11721', 'class_10608', 'class_10726', 'class_11792',
        'class_11900', 'class_12326']

inter = io.open(TINY, encoding='utf8', errors='replace').read().splitlines()
with zipfile.ZipFile(YARN) as z:
    yarn = z.read('mappings/mappings.tiny').decode('utf8', 'replace').splitlines()
moj = io.open(MOJ, encoding='utf8', errors='replace').read()

classes_i = [l.split('\t') for l in inter if l.startswith('c\t')]
classes_y = [l.split('\t') for l in yarn if l.startswith('c\t')]

for want in WANT:
    hit = [p for p in classes_i if p[2].rsplit('/', 1)[-1] == want]
    yhit = [p for p in classes_y if p[2].rsplit('/', 1)[-1] == want]
    if not hit:
        print('%-14s NOT IN INTERMEDIARY' % want)
        continue
    p = hit[0]
    obf = p[1]
    print('%-14s obf=%-10s inter=%s' % (want, obf, p[2]))
    print('               yarn=%s' % (yhit[0][2] if yhit else '?'))
    print('               in mojang file: %s' % ('-> %s:' % obf in moj))
