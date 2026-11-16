"""javap helper: dump methods of an intermediary class from a loom minecraft jar."""
import subprocess
import sys
import zipfile
import os
import tempfile

mcver = sys.argv[1]
cls = sys.argv[2]
pat = sys.argv[3] if len(sys.argv) > 3 else None

jar = os.path.expanduser(r'~\.gradle\caches\fabric-loom\%s\minecraft-merged.jar' % mcver)
tmp = os.path.join(tempfile.gettempdir(), 'opencode', 'javap')
os.makedirs(tmp, exist_ok=True)
out = os.path.join(tmp, cls.replace('/', '.') + '.class')
with zipfile.ZipFile(jar) as z:
    data = z.read(cls.replace('.', '/') + '.class')
    open(out, 'wb').write(data)
r = subprocess.run(['javap', '-p', out], capture_output=True, text=True)
for line in r.stdout.splitlines():
    if pat is None or pat.lower() in line.lower():
        print(line.strip())
if r.stderr.strip():
    print(r.stderr)
