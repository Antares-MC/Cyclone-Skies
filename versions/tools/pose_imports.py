"""Add `import dev.valence.compat.Pose;` to every file that now calls Pose.*"""

import glob
import io
import re

IMPORT = 'import dev.valence.compat.Pose;'
LINE_RE = re.compile(r'^import .*;$')


def add(path):
    text = io.open(path, encoding='utf8', newline='').read()
    if IMPORT in text:
        return False
    lines = text.splitlines(keepends=True)
    idx = [i for i, l in enumerate(lines) if LINE_RE.match(l.strip())]
    if not idx:
        raise SystemExit('no imports in ' + path)
    # Prefer to sit right after the other dev.valence.compat facades.
    target = None
    for i in idx:
        if lines[i].strip().startswith('import dev.valence.compat.'):
            target = i
    if target is None:
        for i in idx:
            if lines[i].strip().startswith('import dev.valence.'):
                target = i
    if target is None:
        target = idx[0] - 1
    nl = '\r\n' if lines[0].endswith('\r\n') else '\n'
    lines.insert(target + 1, IMPORT + nl)
    io.open(path, 'w', encoding='utf8', newline='').write(''.join(lines))
    return True


def main():
    n = 0
    for path in glob.glob('cyclone/src/main/java/**/*.java', recursive=True):
        if not re.search(r'\bPose\.', io.open(path, encoding='utf8').read()):
            continue
        if add(path):
            n += 1
            print('import added:', path.replace('\\', '/'))
    print('files:', n)


if __name__ == '__main__':
    main()
