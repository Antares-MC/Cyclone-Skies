"""Rewrite `new class_1043(() -> label, args...)` onto dev.valence.compat.Textures."""

import glob
import io
import re

CALL = 'new class_1043('
IMPORT = 'import dev.valence.compat.Textures;'
LINE_RE = re.compile(r'^import .*;$')


def split_args(text, start):
    depth = 1
    i = start
    args = []
    cur = start
    while i < len(text):
        ch = text[i]
        if ch == '"':
            i += 1
            while i < len(text) and text[i] != '"':
                i += 2 if text[i] == '\\' else 1
        elif ch == '(':
            depth += 1
        elif ch == ')':
            depth -= 1
            if depth == 0:
                args.append(text[cur:i])
                return args, i + 1
        elif ch == ',' and depth == 1:
            args.append(text[cur:i])
            cur = i + 1
        i += 1
    raise ValueError('unbalanced')


def rewrite(text, stats):
    out = []
    pos = 0
    while True:
        idx = text.find(CALL, pos)
        if idx < 0:
            out.append(text[pos:])
            break
        args, end = split_args(text, idx + len(CALL))
        label = args[0].strip()
        if not label.startswith('() ->'):
            out.append(text[pos:end])
            pos = end
            continue
        label = label[len('() ->'):].strip()
        rest = [a.strip() for a in args[1:]]
        kind = 'named' if len(rest) == 1 else 'blank'
        new = 'Textures.%s(%s)' % (kind, ', '.join([label] + rest))
        out.append(text[pos:idx])
        out.append(new)
        stats[kind] += 1
        pos = end
    return ''.join(out)


def add_import(lines, nl):
    if any(l.strip() == IMPORT for l in lines):
        return lines
    idx = [i for i, l in enumerate(lines) if LINE_RE.match(l.strip())]
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
    lines.insert(target + 1, IMPORT + nl)
    i = 0
    while i < len(lines):
        if lines[i].strip().startswith('import dev.valence.'):
            j = i
            while j < len(lines) and lines[j].strip().startswith('import dev.valence.'):
                j += 1
            lines[i:j] = sorted(lines[i:j], key=lambda l: l.strip())
            i = j
        else:
            i += 1
    return lines


stats = {'named': 0, 'blank': 0}
for path in glob.glob('cyclone/src/main/java/**/*.java', recursive=True):
    text = io.open(path, encoding='utf8', newline='').read()
    if CALL not in text:
        continue
    new = rewrite(text, stats)
    if new != text:
        nl = '\r\n' if '\r\n' in text else '\n'
        new = ''.join(add_import(new.splitlines(keepends=True), nl))
        io.open(path, 'w', encoding='utf8', newline='').write(new)
        print('updated', path.replace('\\', '/'))
print(stats)
