"""Build the pre-1.21.6 modules from a layered overlay set.

Every module in 1.21.2 - 1.21.5 starts from the same overlay, `versions/v1_21_5/java`,
which is the pre-1.21.6 compatibility template. A few files diverge again on older
versions; those live in `versions/overlays/<range>/` and are applied on top, newest
range first so the oldest wins:

    le1_21_4   applies to 1.21, 1.21.1, 1.21.2, 1.21.3, 1.21.4
    le1_21_3   applies to 1.21, 1.21.1, 1.21.2, 1.21.3
    le1_21_1   applies to 1.21, 1.21.1

usage: python versions/tools/sweep5.py 1.21.2 1.21.3 1.21.4
"""

import os
import shutil
import subprocess
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
SRC = os.path.join(ROOT, 'versions', 'v1_21_5', 'java')
RANGES = [
    ('le1_21_4', (1, 21, 4)),
    ('le1_21_3', (1, 21, 3)),
    ('le1_21_1', (1, 21, 1)),
]


def module(ver):
    return 'v' + ver.replace('.', '_')


def key(ver):
    parts = [int(p) for p in ver.split('.')]
    while len(parts) < 3:
        parts.append(0)
    return tuple(parts)


def copy_tree(src, dst):
    for root, _d, files in os.walk(src):
        for f in files:
            if not f.endswith('.java'):
                continue
            rel = os.path.relpath(os.path.join(root, f), src)
            out = os.path.join(dst, rel)
            if os.path.abspath(out) == os.path.abspath(os.path.join(root, f)):
                continue
            os.makedirs(os.path.dirname(out), exist_ok=True)
            shutil.copyfile(os.path.join(root, f), out)


def main():
    versions = sys.argv[1:]
    if not versions:
        raise SystemExit('usage: sweep5.py <version> [...]')
    for ver in versions:
        mod = module(ver)
        dst = os.path.join(ROOT, 'versions', mod, 'java')
        if not os.path.isdir(dst):
            raise SystemExit('no module ' + mod)

        copy_tree(SRC, dst)
        applied = ['v1_21_5']
        # Oldest range last so it overwrites the newer ones.
        for name, ceiling in reversed(RANGES):
            if key(ver) <= ceiling:
                layer = os.path.join(ROOT, 'versions', 'overlays', name)
                if os.path.isdir(layer):
                    copy_tree(layer, dst)
                    applied.append(name)

        gen = os.path.join(ROOT, 'versions', 'gen', mod, 'java')
        subprocess.run([sys.executable, os.path.join(ROOT, 'versions', 'tools', 'xlate.py'),
                        '1.21.11', ver, '--emit', gen, '--overlay', dst],
                       cwd=ROOT, check=True, stdout=subprocess.DEVNULL)
        log = os.path.join(os.environ['TEMP'], 'opencode', 'c5.log')
        with open(log, 'w') as fh:
            subprocess.run(['cmd', '/c', 'gradlew.bat -p versions :%s:compileJava --no-daemon -q' % mod],
                           cwd=ROOT, stdout=fh, stderr=subprocess.STDOUT)
        print('\n### %s  (layers: %s)' % (ver, ', '.join(applied)))
        subprocess.run([sys.executable, os.path.join(ROOT, 'versions', 'tools', 'errsum.py'), log],
                       cwd=ROOT)


if __name__ == '__main__':
    main()
