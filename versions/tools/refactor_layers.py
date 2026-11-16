#!/usr/bin/env python3
"""One-shot refactor: route render-layer factories through dev.valence.compat.Layers.

Before: buffers.method_73477(class_12249.method_75994(WHITE))
After:  buffers.method_73477(Layers.cutout(WHITE))

The vanilla factory lives on RenderLayers in 1.21.11 but on RenderLayer in older
versions and under different ids, so the shared source must not name it.
"""
import os
import re
import sys

SRC = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))),
                   "cyclone", "src", "main", "java")

# class_12249.method -> Layers.method
OPS = {
    "method_75994": "cutout",
    "method_76000": "translucent",
    "method_76002": "translucentEmissive",
    "method_76022": "text",
    "method_76028": "textSeeThrough",
    "method_76023": "quads",
    "method_75988": "beacon",
    "method_75943": "fire",
}

CALL = re.compile(
    r"\bclass_12249\.(method_(?:75994|75988|75943|76000|76002|76022|76023|76028))\s*\(")


def split_args(text, start):
    """Return (args_text, end_index_after_close_paren) for a call opening at 'start'."""
    depth = 0
    i = start
    while i < len(text):
        ch = text[i]
        if ch == "(":
            depth += 1
        elif ch == ")":
            depth -= 1
            if depth == 0:
                return text[start + 1:i], i + 1
        elif ch == '"':
            i += 1
            while i < len(text) and text[i] != '"':
                if text[i] == "\\":
                    i += 1
                i += 1
        i += 1
    raise ValueError("unbalanced call")


def main():
    changed = 0
    total = 0
    for root, _d, files in os.walk(SRC):
        for f in files:
            if not f.endswith(".java"):
                continue
            path = os.path.join(root, f)
            if os.sep + "compat" + os.sep in path:
                continue
            with open(path, encoding="utf-8") as fh:
                text = fh.read()
            if "class_12249" not in text:
                continue
            out = []
            pos = 0
            n = 0
            while True:
                m = CALL.search(text, pos)
                if not m:
                    out.append(text[pos:])
                    break
                args, end = split_args(text, m.end() - 1)
                op = OPS[m.group(1)]
                out.append(text[pos:m.start()])
                out.append("Layers.%s(%s)" % (op, args))
                pos = end
                n += 1
            if n:
                new = "".join(out)
                # drop the now-unused import
                new = re.sub(r"^import net\.minecraft\.class_12249;\n", "", new, flags=re.M)
                if "import dev.valence.compat.Layers;" not in new:
                    idx = new.find("\nimport ")
                    if idx < 0:
                        raise SystemExit("no imports in %s" % path)
                    new = new[:idx + 1] + "import dev.valence.compat.Layers;\n" + new[idx + 1:]
                with open(path, "w", encoding="utf-8", newline="\n") as fh:
                    fh.write(new)
                changed += 1
                total += n
                print("%2d  %s" % (n, os.path.relpath(path, SRC).replace("\\", "/")))
    print("\nrewrote %d calls in %d files" % (total, changed))
    leftover = []
    for root, _d, files in os.walk(SRC):
        for f in files:
            if f.endswith(".java"):
                p = os.path.join(root, f)
                if os.sep + "compat" + os.sep in p:
                    continue
                with open(p, encoding="utf-8") as fh:
                    if "class_12249" in fh.read():
                        leftover.append(os.path.relpath(p, SRC).replace("\\", "/"))
    print("files still naming class_12249: %s" % (leftover or "none"))


if __name__ == "__main__":
    main()
