"""Route the in-world glyph submits through dev.valence.compat.Fonts.

    font.method_27522(text, x, y, col, shadow, m, buffers, layer, 0, light)
      -> Fonts.draw(font, text, x, y, col, shadow, m, buffers, layer, light)

The facade drops the background-colour argument, which is always zero here, and lets the
pre-1.21.2 overlay flatten the component to a string.
"""

import glob
import io
import re

CALL = re.compile(r'([A-Za-z_]\w*)\.method_27522\(')
IMPORT = 'import dev.valence.compat.Fonts;'


def split_args(text, start):
    depth = 1
    i = start
    cur = start
    args = []
    while i < len(text):
        ch = text[i]
        if ch == '"':
            i += 1
            while i < len(text):
                if text[i] == '\\':
                    i += 2
                    continue
                if text[i] == '"':
                    break
                i += 1
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
    raise ValueError('unbalanced call')


def rewrite(text, path):
    out = []
    pos = 0
    n = 0
    while True:
        m = CALL.search(text, pos)
        if not m:
            out.append(text[pos:])
            break
        receiver = m.group(1)
        args, end = split_args(text, m.end())
        if len(args) != 10:
            raise SystemExit('%s: expected 10 arguments, got %d' % (path, len(args)))
        if args[8].strip() != '0':
            raise SystemExit('%s: unexpected background colour %r' % (path, args[8]))
        kept = [a.strip() for i, a in enumerate(args) if i != 8]
        out.append(text[pos:m.start()])
        out.append('Fonts.draw(%s, %s)' % (receiver, ', '.join(kept)))
        n += 1
        pos = end
    return ''.join(out), n


def add_import(text):
    if IMPORT in text:
        return text
    nl = '\r\n' if '\r\n' in text else '\n'
    lines = text.splitlines(keepends=True)
    idx = [i for i, l in enumerate(lines) if re.match(r'^import .*;$', l.strip())]
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
    return ''.join(lines)


total = 0
for path in glob.glob('cyclone/src/main/java/**/*.java', recursive=True):
    text = io.open(path, encoding='utf8', newline='').read()
    if not CALL.search(text):
        continue
    new, n = rewrite(text, path)
    new = add_import(new)
    io.open(path, 'w', encoding='utf8', newline='').write(new)
    total += n
    print('%-56s %d' % (path.replace('\\', '/').split('/java/')[-1], n))
print('call sites:', total)
