"""Dump one intermediary class from a merged Minecraft jar and javap it.

    python versions/tools/jdump.py 1.21.1 class_8685
"""

import glob
import os
import subprocess
import sys
import zipfile

ver = sys.argv[1]
target = sys.argv[2]

base = os.path.expanduser('~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-intermediary')
cands = glob.glob(os.path.join(base, ver + '-*'))
if not cands:
    raise SystemExit('no merged jar for ' + ver)
jar = glob.glob(os.path.join(cands[0], '*.jar'))[0]

out = os.path.join(os.environ['TEMP'], 'opencode', 'dc121')
os.makedirs(out, exist_ok=True)
inner = 'net/minecraft/' + target + '.class'
with zipfile.ZipFile(jar) as z:
    data = z.read(inner)
with open(os.path.join(out, target + '.class'), 'wb') as fh:
    fh.write(data)

name = target + '.class'
result = subprocess.run(['javap', '-p', name], cwd=out, capture_output=True, text=True)
print(result.stdout)
if result.stderr:
    print('ERR', result.stderr)
