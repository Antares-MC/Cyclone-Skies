"""Rewrite the GUI matrix idioms onto the dev.valence.compat.Pose facade.

The shared sources were written against 1.21.6+ where DrawContext#getMatrices()
returns org.joml.Matrix3x2fStack.  Up to 1.21.5 the same method returns a
PoseStack, whose push/pop/translate/scale use different names and signatures.
Routing every call through Pose keeps a single generated tree working on both.

    g.method_51448().pushMatrix()        -> Pose.push(g)
    g.method_51448().popMatrix()         -> Pose.pop(g)
    g.method_51448().translate(x, y)     -> Pose.translate(g, x, y)
    g.method_51448().scale(x, y)         -> Pose.scale(g, x, y)
    g.method_51448().rotate(a)           -> Pose.rotate(g, a)
    g.method_51448().mul(m)              -> Pose.mul(g, m)
    g.method_51448().transformPosition(v)-> Pose.project(g, v)
"""

import glob
import io
import os
import re
import sys

NAMES = {
    'pushMatrix': 'push',
    'popMatrix': 'pop',
    'translate': 'translate',
    'scale': 'scale',
    'rotate': 'rotate',
    'mul': 'mul',
    'transformPosition': 'project',
}

CALL = re.compile(r'([A-Za-z_]\w*)\.method_51448\(\)\.(\w+)\(')


def split_args(text, start):
    """Return (args_string, index_after_closing_paren) for text[start:]."""
    depth = 1
    i = start
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
        elif ch == "'":
            i += 1
            while i < len(text):
                if text[i] == '\\':
                    i += 2
                    continue
                if text[i] == "'":
                    break
                i += 1
        elif ch == '(':
            depth += 1
        elif ch == ')':
            depth -= 1
            if depth == 0:
                return text[start:i], i + 1
        i += 1
    raise ValueError('unbalanced call')


def rewrite(text, path, stats):
    out = []
    pos = 0
    while True:
        m = CALL.search(text, pos)
        if not m:
            out.append(text[pos:])
            break
        receiver, name = m.group(1), m.group(2)
        if name not in NAMES:
            raise SystemExit('%s: unknown matrix call %s' % (path, name))
        args, end = split_args(text, m.end())
        new = 'Pose.%s(%s%s)' % (NAMES[name], receiver, (', ' + args.strip()) if args.strip() else '')
        out.append(text[pos:m.start()])
        out.append(new)
        stats[name] += 1
        pos = end
    return ''.join(out)


keep = re.compile(r'\bPose\b')


def main():
    roots = sys.argv[1:] or ['cyclone/src/main/java']
    stats = {k: 0 for k in NAMES}
    touched = 0
    for root in roots:
        for path in glob.glob(os.path.join(root, '**', '*.java'), recursive=True):
            text = io.open(path, encoding='utf8').read()
            if 'method_51448()' not in text:
                continue
            new = rewrite(text, path, stats)
            if new != text:
                io.open(path, 'w', encoding='utf8', newline='').write(new)
                touched += 1
                print('rewrote', path.replace(os.sep, '/'))
    print('files:', touched, dict(stats))


if __name__ == '__main__':
    main()
