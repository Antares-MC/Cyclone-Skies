"""Show the bytecode of one method of one intermediary class."""

import glob
import os
import subprocess
import sys
import zipfile

ver = sys.argv[1]
target = sys.argv[2]
want = sys.argv[3] if len(sys.argv) > 3 else None
pattern = sys.argv[4] if len(sys.argv) > 4 else None

base = os.path.expanduser('~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-intermediary')
cands = glob.glob(os.path.join(base, ver + '-*'))
jar = glob.glob(os.path.join(cands[0], '*.jar'))[0]
out = os.path.join(os.environ['TEMP'], 'opencode', 'dc121')
os.makedirs(out, exist_ok=True)
inner = 'net/minecraft/' + target + '.class'
with zipfile.ZipFile(jar) as z:
    data = z.read(inner)
with open(os.path.join(out, target + '.class'), 'wb') as fh:
    fh.write(data)

result = subprocess.run(['javap', '-p', '-c', target + '.class'], cwd=out,
                        capture_output=True, text=True)
lines = result.stdout.splitlines()

if want is None:
    print('\n'.join(lines))
    raise SystemExit

head = [i for i, l in enumerate(lines) if want in l and 'Method ' not in l and '(' in l]
if not head:
    print('no method matching', want)
    for l in lines:
        if '(' in l and 'Method ' not in l:
            print('  ', l.strip())
    raise SystemExit

i = head[0]
body = [lines[i]]
j = i + 1
while j < len(lines):
    if lines[j].startswith('  ') and not lines[j].startswith('   '):
        break
    body.append(lines[j])
    j += 1
if pattern:
    body = [l for l in body if pattern in l]
print('\n'.join(body))
