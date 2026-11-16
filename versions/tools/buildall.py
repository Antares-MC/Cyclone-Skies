"""Build a set of version modules and copy the remapped jars to the Downloads folder.

    python versions/tools/buildall.py 1.21 1.21.1 1.21.2 ...

Every module's generated tree is refreshed from the shared sources first, so a jar is always
built from the current code. Modules at or below 1.21.5 also get their overlay template and
range overlays refreshed, exactly as sweep5.py does. The jar is named
cycloneskies-1.9.9-<version>.jar and its size and SHA-256 are printed for the release notes.
"""

import glob
import hashlib
import os
import shutil
import subprocess
import sys

TOOLS = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(os.path.dirname(TOOLS))
DOWNLOADS = os.path.join(os.path.expanduser('~'), 'Downloads')
MOD_VERSION = '1.9.9'
TEMPLATE_CEILING = (1, 21, 5)

sys.path.insert(0, TOOLS)
import sweep5  # noqa: E402


def module(ver):
    return 'v' + ver.replace('.', '_')


def refresh_overlay(ver):
    """Rebuild the overlay tree for a module that uses the pre-1.21.6 template."""
    if sweep5.key(ver) > TEMPLATE_CEILING:
        return
    dst = os.path.join(ROOT, 'versions', module(ver), 'java')
    sweep5.copy_tree(sweep5.SRC, dst)
    for name, ceiling in reversed(sweep5.RANGES):
        if sweep5.key(ver) <= ceiling:
            layer = os.path.join(ROOT, 'versions', 'overlays', name)
            if os.path.isdir(layer):
                sweep5.copy_tree(layer, dst)


def emit(ver):
    mod = module(ver)
    gen = os.path.join(ROOT, 'versions', 'gen', mod, 'java')
    overlay = os.path.join(ROOT, 'versions', mod, 'java')
    subprocess.run([sys.executable, os.path.join(TOOLS, 'xlate.py'),
                    '1.21.11', ver, '--emit', gen, '--overlay', overlay],
                   cwd=ROOT, check=True, stdout=subprocess.DEVNULL)


def sha256(path):
    digest = hashlib.sha256()
    with open(path, 'rb') as fh:
        for chunk in iter(lambda: fh.read(1 << 20), b''):
            digest.update(chunk)
    return digest.hexdigest().upper()


def main():
    versions = sys.argv[1:]
    if not versions:
        raise SystemExit('usage: buildall.py <version> [...]')

    for ver in versions:
        refresh_overlay(ver)
        emit(ver)

    tasks = [':%s:build' % module(v) for v in versions]
    log = os.path.join(os.environ['TEMP'], 'opencode', 'buildall.log')
    with open(log, 'w') as fh:
        cmd = ['cmd', '/c', 'gradlew.bat -p versions --no-daemon -q'] + tasks
        code = subprocess.run(cmd, cwd=ROOT, stdout=fh, stderr=subprocess.STDOUT).returncode
    print('gradle exit', code)

    for ver in versions:
        mod = module(ver)
        libs = glob.glob(os.path.join(ROOT, 'versions', mod, 'build', 'libs', '*.jar'))
        libs = [p for p in libs if 'sources' not in os.path.basename(p)]
        if not libs:
            print('%-8s  NO JAR' % ver)
            continue
        src = max(libs, key=os.path.getmtime)
        dest = os.path.join(DOWNLOADS, 'cycloneskies-%s-%s.jar' % (MOD_VERSION, ver))
        shutil.copyfile(src, dest)
        print('%-8s  %12d  %s  %s' % (ver, os.path.getsize(dest), sha256(dest),
                                      os.path.basename(dest)))


if __name__ == '__main__':
    main()
