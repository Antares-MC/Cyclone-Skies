"""Route the GUI texture pipeline argument through dev.valence.compat.Pipes.

Pre-1.21.6 DrawContext texture calls take a Function<Identifier, RenderPipeline> instead of
a RenderPipeline, so the constant cannot be spelled inline in shared sources.
"""

import glob
import io
import re

IMPORT = 'import dev.valence.compat.Pipes;'
LINE_RE = re.compile(r'^import .*;$')


def fix_imports(lines, nl):
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
    # keep the dev.valence block sorted
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


n = 0
for path in glob.glob('cyclone/src/main/java/**/*.java', recursive=True):
    text = io.open(path, encoding='utf8', newline='').read()
    if 'class_10799.field_56883' not in text:
        continue
    new = text.replace('class_10799.field_56883', 'Pipes.guiTextured()')
    new = new.replace('RenderPipeline pipe = Pipes.guiTextured();', 'var pipe = Pipes.guiTextured();')
    new = re.sub(r'\r?\nimport net\.minecraft\.class_10799;', '', new)
    lines = new.splitlines(keepends=True)
    nl = '\r\n' if text.count('\r\n') else '\n'
    new = ''.join(fix_imports(lines, nl))
    if new != text:
        io.open(path, 'w', encoding='utf8', newline='').write(new)
        n += 1
        print('updated', path.replace('\\', '/'))
print('files:', n)
