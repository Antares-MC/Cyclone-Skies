#!/usr/bin/env python3
"""Cross-version intermediary symbol translator for CycloneSkies.

The mod sources are written against 1.21.11 intermediary ids (class_*,
method_*, field_*).  Intermediary ids are not stable across Minecraft versions:
when Mojang renames a class or changes a descriptor, Fabric allocates a fresh
id.  Yarn names are stable, so they are the interlingua:

    1.21.11 id  ->  yarn named  ->  target version id

Per symbol, per target, the classification is:
  SAME     id exists in the target with a compatible descriptor shape
  RETYPE   id exists but the descriptor changed (call sites must be adapted)
  RENAME   id is gone, an equivalent exists under another id (auto-rewritable)
  MANUAL   no equivalent: genuine new API, needs hand-written compatibility code

usage:
  xlate.py <from> <to> [<to> ...]                  report only
  xlate.py <from> <to> --emit <dir>                also write the rewritten tree
  xlate.py <from> <to> --json <file>               machine-readable report
"""
import glob
import json
import os
import re
import shutil
import sys
import urllib.request
import zipfile

CACHE = os.path.join(os.environ["TEMP"], "opencode", "mcv", "mapcheck")
TOKEN = re.compile(r"\b(?:class|method|field)_\d+\b")
ID_RE = re.compile(r"^(?:class|method|field)_\d+$")
ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
DEFAULT_SRC = os.path.join(ROOT, "cyclone", "src", "main", "java")


def fetch(url):
    req = urllib.request.Request(url, headers={"User-Agent": "cyclone-build/1.0"})
    with urllib.request.urlopen(req, timeout=180) as r:
        return r.read()


def yarn_jar(game):
    os.makedirs(CACHE, exist_ok=True)
    have = glob.glob(os.path.join(CACHE, "yarn-%s_build.*-v2.jar" % game))
    if have:
        return have[0]
    xml = fetch("https://maven.fabricmc.net/net/fabricmc/yarn/maven-metadata.xml").decode("utf-8")
    best = None
    for v in re.findall(r"<version>([^<]+)</version>", xml):
        m = re.match(r"^(.+)\+build\.(\d+)$", v)
        if m and m.group(1) == game and (best is None or int(m.group(2)) > best[0]):
            best = (int(m.group(2)), v)
    if best is None:
        raise SystemExit("no yarn build for %s" % game)
    full = best[1]
    data = fetch("https://maven.fabricmc.net/net/fabricmc/yarn/%s/yarn-%s-v2.jar" % (full, full))
    path = os.path.join(CACHE, "yarn-%s-v2.jar" % full.replace("+", "_"))
    with open(path, "wb") as fh:
        fh.write(data)
    return path


class Side:
    """One Minecraft version's symbol table, keyed the way sources use it."""

    def __init__(self, game):
        tiny = zipfile.ZipFile(yarn_jar(game)).read("mappings/mappings.tiny").decode("utf-8", "replace")
        self.game = game
        self.cls = {}         # simple id  -> named
        self.cls_named = {}   # named      -> id (inner form for nested classes)
        self.mem = {}         # (owner named, member named) -> id
        self.mem_id = {}      # id -> (owner named, member named, shape)
        cur_named = None
        for line in tiny.splitlines():
            if line.startswith("c\t"):
                p = line.split("\t")
                sid = p[1].rsplit("/", 1)[-1]
                named = p[2]
                self.cls[sid] = named
                emit = sid.rsplit("$", 1)[-1] if "$" in sid else sid
                if "$" in sid:
                    self.cls.setdefault(emit, named)
                self.cls_named[named] = emit
                cur_named = named
            elif line.startswith("\tm\t") or line.startswith("\tf\t"):
                p = line.split("\t")
                if len(p) < 5:
                    continue
                sid = p[3].rsplit("/", 1)[-1]
                named = p[4]
                shape = re.sub(r"(?:class|method|field)_\d+", "C", p[2]) if p[1] == "m" else p[2]
                self.mem[(cur_named, named)] = sid
                self.mem_id[sid] = (cur_named, named, shape)

    def has_id(self, i):
        if i.startswith("class_"):
            return i in self.cls
        return i in self.mem_id


def usage_map(src):
    used = {}
    for root, _d, files in os.walk(src):
        for f in files:
            if f.endswith(".java"):
                p = os.path.join(root, f)
                rel = os.path.relpath(p, src).replace("\\", "/")
                with open(p, encoding="utf-8", errors="replace") as fh:
                    for m in TOKEN.finditer(fh.read()):
                        used.setdefault(m.group(0), set()).add(rel)
    return used


def norm(name):
    """Member names drift between yarn versions (text -> getText)."""
    short = name.rsplit("/", 1)[-1]
    for prefix in ("get", "is", "has", "set"):
        if short.startswith(prefix) and len(short) > len(prefix) + 1:
            return short[len(prefix):].lower()
    return short.lower()


def classify(src_side, used, target):
    same, retype, rename, manual = [], {}, {}, {}
    for i in sorted(used):
        if i.startswith("class_"):
            if target.has_id(i):
                same.append(i)
                continue
            named = src_side.cls.get(i)
            tid = target.cls_named.get(named) if named else None
            if tid and ID_RE.match(tid) and tid != i:
                rename[i] = tid
            else:
                manual[i] = {"yarn": named, "owner": None, "files": sorted(used[i])}
            continue

        owner, named, shape = src_side.mem_id.get(i, (None, None, None))
        if target.has_id(i):
            theirs = target.mem_id[i][2]
            if shape == theirs:
                same.append(i)
            else:
                retype[i] = {"source_shape": shape, "target_shape": theirs, "yarn": named}
            continue

        tid = target.mem.get((owner, named)) if owner and named else None
        if tid is None and owner and named:
            cands = {v for (o, n), v in target.mem.items() if o == owner and norm(n) == norm(named)}
            tid = cands.pop() if len(cands) == 1 else None
        if tid is None and named:
            hits = {v for (_o, n), v in target.mem.items() if norm(n) == norm(named)}
            tid = hits.pop() if len(hits) == 1 else None
        if tid and ID_RE.match(tid) and tid != i:
            rename[i] = tid
        else:
            manual[i] = {"yarn": named, "owner": owner, "files": sorted(used[i])}
    return same, retype, rename, manual


def emit_tree(src, dst, rename, skip):
    if os.path.isdir(dst):
        shutil.rmtree(dst)
    pattern = re.compile(r"\b(?:%s)\b" % "|".join(sorted(re.escape(k) for k in rename))) if rename else None
    n = 0
    for root, _d, files in os.walk(src):
        for f in files:
            p = os.path.join(root, f)
            rel = os.path.relpath(p, src)
            if rel.replace("\\", "/") in skip:
                continue
            out = os.path.join(dst, rel)
            os.makedirs(os.path.dirname(out), exist_ok=True)
            if f.endswith(".java") and pattern is not None:
                with open(p, encoding="utf-8", errors="replace") as fh:
                    text = fh.read()
                text = pattern.sub(lambda m: rename[m.group(0)], text)
                with open(out, "w", encoding="utf-8", newline="\n") as fh:
                    fh.write(text)
            else:
                shutil.copyfile(p, out)
            n += 1
    return n


def main():
    argv = sys.argv[1:]
    out_json = emit_dir = overlay_dir = None
    if "--json" in argv:
        k = argv.index("--json")
        out_json = argv[k + 1]
        del argv[k:k + 2]
    if "--emit" in argv:
        k = argv.index("--emit")
        emit_dir = argv[k + 1]
        del argv[k:k + 2]
    if "--overlay" in argv:
        k = argv.index("--overlay")
        overlay_dir = argv[k + 1]
        del argv[k:k + 2]
    frm, targets = argv[0], argv[1:]

    src_side = Side(frm)
    used = usage_map(DEFAULT_SRC)
    print("from %s: %d distinct ids in %d files" % (frm, len(used), len({f for s in used.values() for f in s})))

    results = {}
    for to in targets:
        target = Side(to)
        same, retype, rename, manual = classify(src_side, used, target)
        results[to] = {"same": len(same), "retype": retype, "rename": rename, "manual": manual}
        print("\n=== %s : SAME %d  RETYPE %d  RENAME %d  MANUAL %d ===" %
              (to, len(same), len(retype), len(rename), len(manual)))
        if retype:
            print("  -- RETYPE --")
            for k, v in sorted(retype.items()):
                print("     %-12s %-32s %s -> %s" % (k, (v["yarn"] or "?")[:32], v["source_shape"], v["target_shape"]))
        if rename:
            print("  -- RENAME --")
            print("     " + "  ".join("%s->%s" % (k, v) for k, v in sorted(rename.items())))
        if manual:
            print("  -- MANUAL --")
            for k, v in sorted(manual.items()):
                print("     %-12s %-40s %s" % (k, (v["yarn"] or "?")[:40],
                                               ", ".join(f.split("/")[-1] for f in v["files"][:4])))

    if emit_dir:
        to = targets[0]
        r = results[to]
        # Files provided by the hand-written overlay must not be generated:
        # javac would see two definitions of the same class.
        skip = {"dev/valence/ModMenuIntegration.java"}
        if overlay_dir:
            for root, _d, files in os.walk(overlay_dir):
                for f in files:
                    if f.endswith(".java"):
                        skip.add(os.path.relpath(os.path.join(root, f), overlay_dir).replace("\\", "/"))
        n = emit_tree(DEFAULT_SRC, emit_dir, r["rename"], skip)
        print("\nemitted %d files to %s (%d renames applied, %d overlay files skipped)"
              % (n, emit_dir, len(r["rename"]), len(skip) - 1))
    if out_json:
        with open(out_json, "w") as fh:
            json.dump(results, fh, indent=1)
        print("wrote %s" % out_json)


if __name__ == "__main__":
    main()
