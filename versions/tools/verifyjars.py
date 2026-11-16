"""Check that each built jar declares the Minecraft version it was compiled against."""

import glob
import hashlib
import json
import os
import zipfile

DOWNLOADS = os.path.join(os.path.expanduser('~'), 'Downloads')
MOD_VERSION = '1.9.9'
VERSIONS = ['1.21', '1.21.1', '1.21.2', '1.21.3', '1.21.4', '1.21.5', '1.21.6',
            '1.21.7', '1.21.8', '1.21.9', '1.21.10', '1.21.11']


def sha256(path):
    digest = hashlib.sha256()
    with open(path, 'rb') as fh:
        for chunk in iter(lambda: fh.read(1 << 20), b''):
            digest.update(chunk)
    return digest.hexdigest().upper()


rows = []
for ver in VERSIONS:
    path = os.path.join(DOWNLOADS, 'cycloneskies-%s-%s.jar' % (MOD_VERSION, ver))
    if ver == '1.21.11':
        path = os.path.join(DOWNLOADS, 'cycloneskies-%s.jar' % MOD_VERSION)
    if not os.path.exists(path):
        rows.append((ver, 'MISSING', '', ''))
        continue
    with zipfile.ZipFile(path) as z:
        meta = json.loads(z.read('fabric.mod.json').decode('utf8'))
    dep = meta.get('depends', {}).get('minecraft', '?')
    rows.append((ver, '%d' % os.path.getsize(path), sha256(path), dep))

for ver, size, digest, dep in rows:
    print('%-8s %-10s %-66s %s' % (ver, size, digest, dep))

print()
print('jars:', len(glob.glob(os.path.join(DOWNLOADS, 'cycloneskies-%s*.jar' % MOD_VERSION))))
