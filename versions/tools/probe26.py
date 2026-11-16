"""Probe a deobfuscated Minecraft jar: does it contain a class, and what are its members.

    python versions/tools/probe26.py 26.1.2 net/minecraft/client/gui/GuiGraphics
    python versions/tools/probe26.py 26.1.2 --list gui

Prints the class header and, when a member pattern is given, the matching members.
"""

import glob
import os
import re
import subprocess
import sys
import zipfile

BASE = os.path.expanduser('~/.gradle/caches/fabric-loom')
TMP = os.path.join(os.environ['TEMP'], 'opencode', 'dc26')


def jar_for(ver):
    cands = glob.glob(os.path.join(BASE, ver, 'minecraft-merged.jar'))
    if not cands:
        raise SystemExit('no merged jar for ' + ver)
    return cands[0]


def main():
    ver = sys.argv[1]
    arg = sys.argv[2]
    pattern = sys.argv[3] if len(sys.argv) > 3 else None
    jar = jar_for(ver)
    os.makedirs(TMP, exist_ok=True)

    with zipfile.ZipFile(jar) as z:
        names = z.namelist()

        if arg == '--list':
            needle = pattern or ''
            hits = [n for n in names if needle.lower() in n.lower() and n.endswith('.class')]
            for n in hits[:200]:
                print(n)
            print('total', len(hits))
            return

        if arg == '--find':
            needle = (pattern or '').lower()
            hits = [n for n in names if needle in n.rsplit('/', 1)[-1].lower() and n.endswith('.class')]
            for n in hits[:100]:
                print(n)
            print('total', len(hits))
            return

        inner = arg.replace('.', '/') + '.class'
        if inner not in names:
            print('MISSING', inner)
            hits = [n for n in names if arg.rsplit('/', 1)[-1] in n]
            for n in hits[:20]:
                print('  maybe', n)
            return
        data = z.read(inner)

    local = os.path.join(TMP, 'probe.class')
    with open(local, 'wb') as fh:
        fh.write(data)
    result = subprocess.run(['javap', '-p', 'probe.class'], cwd=TMP, capture_output=True, text=True)
    lines = result.stdout.splitlines()
    if pattern:
        rx = re.compile(pattern)
        lines = [l for l in lines if rx.search(l)]
    print('\n'.join(lines))


main()
