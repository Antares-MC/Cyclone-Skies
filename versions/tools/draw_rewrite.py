"""Route the DrawContext blit calls through dev.valence.compat.Draw.

    g.method_25291(pipe, tex, x, y, u, v, w, h, tw, th, col)        -> Draw.texture(g, ...)
    g.method_25293(pipe, tex, x, y, u, v, w, h, rw, rh, tw, th, c)  -> Draw.region(g, ...)
    g.method_52707(pipe, tex, x, y, w, h, col)                      -> Draw.guiTexture(g, ...)
    g.method_25302(pipe, tex, x, y, u, v, w, h, rw, rh, tw, th)     -> Draw.scaled(g, ...)

The pipeline argument is dropped: the facade supplies it per version.
"""

import glob
import io
import re
import sys

NAMES = {
    'method_25291': 'texture',
    'method_25293': 'region',
    'method_52707': 'guiTexture',
    'method_25302': 'scaled',
}

CALL = re.compile(r'([A-Za-z_]\w*)\.(method_25291|method_25293|method_52707|method_25302)\(')
IMPORT = 'import dev.valence.compat.Draw;'
LINE_RE = re.compile(r'^import .*;$')


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


def drop_pipe(args):
    """The first argument is the pipeline/layer; Gfx hoists it into a local."""
    out = list(args)
    first = out[0].strip()
    del out[0]
    return first, out


def rewrite(text, path):
    out = []
    pos = 0
    n = 0
    while True:
        m = CALL.search(text, pos)
        if not m:
            out.append(text[pos:])
            break
        receiver, name = m.group(1), m.group(2)
        args, end = split_args(text, m.end())
        pipe, rest = drop_pipe(args)
        if pipe not in ('Pipes.guiTextured()', 'pipe'):
            raise SystemExit('%s: unexpected pipeline argument %r' % (path, pipe))
        joined = ', '.join(a.strip() for a in rest)
        out.append(text[pos:m.start()])
        out.append('Draw.%s(%s, %s)' % (NAMES[name], receiver, joined))
        n += 1
        pos = end
    return ''.join(out), n


def add_import(text):
    if IMPORT in text:
        return text
    nl = '\r\n' if '\r\n' in text else '\n'
    lines = text.splitlines(keepends=True)
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
    return ''.join(lines)


total = 0
for path in glob.glob('cyclone/src/main/java/**/*.java', recursive=True):
    text = io.open(path, encoding='utf8', newline='').read()
    if not CALL.search(text):
        continue
    new, n = rewrite(text, path)
    if n:
        new = add_import(new)
        io.open(path, 'w', encoding='utf8', newline='').write(new)
        total += n
        print('%-58s %d' % (path.replace('\\', '/').split('/java/')[-1], n))
print('call sites:', total)
