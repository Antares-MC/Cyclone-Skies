"""Emit a source tree in the official names of one deobfuscated Minecraft version.

    python versions/tools/mojemit.py 26.1.2 [--out versions/gen/v26_1_2/java]

The shared sources are written against 1.21.11 intermediary names. There is no
intermediary publication for the 26.x line, so every symbol has to be renamed to its
official name. The bridges used are:

    intermediary name   --(intermediary-v2.tiny class rows)--> obf
    obf                 --(client_1_21_11.txt)---------------> official 1.21.11
    official 1.21.11    --(jar lookup, then descriptor)------> official 26.x

A few classes are not reachable that way, because their obf name is not the
`outer$inner` form Mojang's file lists (nested classes are frequently split out), or
because Mojang simply does not map them. Those fall back to the yarn name through the
same package-move tolerant lookup: the class file name is the identity, so a name that
is unique in the jar is good enough.

Members are resolved by descriptor rather than by name, so a method that was renamed
between the two versions still lands on the right target: inside an owner class the
descriptor is the identity. The 1.21.11 name is used only as a fallback when the
descriptor itself moved, and a symbol that resolves to nothing is reported as MANUAL
instead of being guessed.
"""

import glob
import io
import json
import os
import re
import sys
import zipfile

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(os.path.dirname(HERE))
sys.path.insert(0, HERE)

import jidx  # noqa: E402
import mojmap  # noqa: E402

SOURCES = os.path.join(ROOT, 'cyclone', 'src', 'main', 'java')
PINNED = os.path.join(HERE, 'pins26.json')
MAPPING = os.path.join(HERE, 'map1.21.11.json')
YARN = os.path.join(os.environ['TEMP'], 'opencode', 'mcv', 'mapcheck',
                    'yarn-1.21.11_build.6-v2.jar')

TOKEN = re.compile(r'\bclass_\d+(?:\$class_\d+)*\b|\bcomp_\d+\b|\bmethod_\d+\b|\bfield_\d+\b')
IMPORT = re.compile(r'^import[ \t]+net\.minecraft\.(class_\d+(?:\.class_\d+)*);[ \t]*\r?$', re.M)
# A class used through another class (`Outer.Inner`) has to expand once, not twice:
# the outer token already carries the package, so the inner one contributes only
# its simple name. Handled before the single-token pass for that reason.
CHAIN = re.compile(r'\b(?:net\.minecraft\.)?(class_\d+(?:\.class_\d+)+)\b')
# A single qualified name (`net.minecraft.class_332` written out in full) keeps its
# prefix only if the class really lives in that package, which is not the case for
# the ones that expand to `com.mojang...`.
QUALIFIED = re.compile(r'\bnet\.minecraft\.(class_\d+(?:\$class_\d+)*)\b')
PRIM = {'Z': 'boolean', 'B': 'byte', 'C': 'char', 'S': 'short',
        'I': 'int', 'J': 'long', 'F': 'float', 'D': 'double', 'V': 'void'}


def split_desc(desc):
    """JVM descriptor -> (parameter descriptors, return descriptor), raw."""
    i = 1
    params = []
    while desc[i] != ')':
        start = i
        while desc[i] == '[':
            i += 1
        if desc[i] == 'L':
            i = desc.index(';', i) + 1
        else:
            i += 1
        params.append(desc[start:i])
    return params, desc[i + 1:]


def load_yarn(path):
    """intermediary -> named, from a yarn mappings jar.

    Nested classes are rows of their own (`class_327$class_6415`), but the source
    refers to the bare id, so the trailing member is indexed as well.
    """
    out = {}
    with zipfile.ZipFile(path) as z:
        text = z.read('mappings/mappings.tiny').decode('utf8', 'replace').splitlines()
    for line in text:
        if line.startswith('c\t'):
            parts = line.split('\t')
            if len(parts) > 2:
                key = parts[1].replace('/', '.')
                out[key] = parts[2]
                if '$' in key:
                    out.setdefault(key.rsplit('$', 1)[-1], parts[2])
    return out

class Resolver:
    def __init__(self, ver):
        self.ver = ver
        with io.open(MAPPING, encoding='utf8') as fh:
            mapping = json.load(fh)
        self.classes = mapping['classes']
        self.obf_of = mapping['obf']
        self.obf_to_inter = {v: k for k, v in self.obf_of.items()}

        self.name11 = {}
        for key, name in mapping['methods'].items():
            parts = key.split('#')
            if len(parts) == 3:
                self.name11[parts[1]] = name
        for key, name in mapping['fields'].items():
            parts = key.split('#')
            if len(parts) == 2:
                self.name11[parts[1]] = name

        tiny = mojmap.parse_tiny(os.path.join(
            os.path.expanduser('~'), '.gradle', 'caches', 'fabric-loom', '1.21.11',
            'intermediary-v2.tiny'))
        self.owner_of = {}
        self.desc_of = {}
        for rows in (tiny[1], tiny[2]):
            for owner, desc, _obf, inter in rows:
                self.owner_of[inter] = owner
                self.desc_of[inter] = desc

        self.yarn = load_yarn(YARN) if os.path.exists(YARN) else {}
        # Nested classes are tiny rows of their own, but the source names them by bare id.
        self.nested = {}
        for inter in self.classes:
            if '$' in inter:
                self.nested.setdefault('net.minecraft.' + inter.rsplit('$', 1)[-1], inter)
        self.jarnames = set(jidx.class_names(ver))
        self.by_simple = {}
        for n in self.jarnames:
            self.by_simple.setdefault(n.rsplit('.', 1)[-1], []).append(n)

        self.pins = {}
        self.member_pins = {}
        if os.path.exists(PINNED):
            with io.open(PINNED, encoding='utf8') as fh:
                pins = json.load(fh)
            self.pins = pins.get('classes', {})
            self.member_pins = pins.get('members', {})

        self.class_cache = {}
        self.member_cache = {}
        self.manual = {}
        self.notes = []

    # -- classes ---------------------------------------------------------
    def by_official(self, official11):
        """Official 1.21.11 class name -> official name in this version."""
        if official11 in self.class_cache:
            return self.class_cache[official11]
        pin = self.pins.get(official11)
        if pin:
            self.class_cache[official11] = pin
            return pin
        hit = self._locate(official11)
        if hit is None:
            self.manual['class ' + official11] = self._suggest(official11)
            hit = official11
        self.class_cache[official11] = hit
        return hit

    def _locate(self, name):
        if name in self.jarnames:
            return name
        simple = name.rsplit('.', 1)[-1]
        cands = self.by_simple.get(simple, [])
        if len(cands) == 1:
            self.notes.append(('moved', name, cands[0]))
            return cands[0]
        suffix = [n for n in self.jarnames if n.endswith('.' + simple)]
        if len(suffix) == 1:
            self.notes.append(('moved', name, suffix[0]))
            return suffix[0]
        if '$' in simple:
            # A nested class whose outer class was renamed: match on the inner part.
            inner = simple.rsplit('$', 1)[-1]
            nested = [n for n in self.jarnames
                      if n.rsplit('.', 1)[-1].endswith('$' + inner)]
            if len(nested) == 1:
                self.notes.append(('nested', name, nested[0]))
                return nested[0]
        fuzzy = [n for n in self.jarnames
                 if '$' not in n and n.rsplit('.', 1)[-1].startswith(simple)]
        if len(fuzzy) == 1:
            self.notes.append(('fuzzy', name, fuzzy[0]))
            return fuzzy[0]
        return None

    def cls(self, inter):
        """Intermediary class -> official name in this version, dotted for source use."""
        if inter in self.class_cache:
            return self.class_cache[inter]
        inter = self.nested.get(inter, inter)
        official = self.classes.get(inter)
        if official is not None:
            name = self.by_official(official)
            self.class_cache[inter] = name
            return name
        named = self.yarn.get(inter) or self.yarn.get(inter.rsplit('.', 1)[-1])
        if named:
            dotted = named.replace('/', '.')
            name = self._locate(dotted)
            if name is not None:
                self.notes.append(('yarn', inter, name))
                self.class_cache[inter] = name
                return name
        self.manual['class ' + inter] = [self.yarn.get(inter, '?')]
        self.class_cache[inter] = inter
        return inter

    def _suggest(self, name):
        """Near misses, for the report."""
        simple = name.rsplit('.', 1)[-1]
        return [n for n in self.jarnames
                if simple[:6] in n.rsplit('.', 1)[-1]][:6]

    def type26(self, raw):
        """One raw obf-namespace type descriptor -> 26.x raw descriptor."""
        arr = 0
        while raw[arr] == '[':
            arr += 1
        body = raw[arr:]
        if body[0] != 'L':
            return raw
        obf = body[1:-1]
        inter = self.obf_to_inter.get(obf)
        if inter is None:
            return raw          # a library type under its real name
        return 'L' + self.cls(inter).replace('.', '/') + ';' + '[' * arr

    def desc26(self, desc):
        params, ret = split_desc(desc)
        return '(%s)%s' % (''.join(self.type26(p) for p in params), self.type26(ret))

    # -- members ---------------------------------------------------------
    def member(self, inter):
        if inter in self.member_cache:
            return self.member_cache[inter]
        if inter in self.member_pins:
            self.member_cache[inter] = self.member_pins[inter]
            return self.member_pins[inter]
        owner_obf = self.owner_of.get(inter)
        desc = self.desc_of.get(inter)
        if owner_obf is None or desc is None:
            self.manual['unknown ' + inter] = []
            self.member_cache[inter] = inter
            return inter

        owner_inter = self.obf_to_inter.get(owner_obf)
        if owner_inter is None:
            self.manual['owner ' + inter] = [owner_obf]
            self.member_cache[inter] = inter
            return inter
        owner = self.cls(owner_inter)
        kind = 'm' if inter.startswith('method_') else 'f'
        want = self.type26(desc) if kind == 'f' else self.desc26(desc)

        hits = jidx.find(self.ver, owner, kind=kind, desc=want)
        names = set(h[1] for h in hits)
        if len(names) == 1:
            name = names.pop()
            self.member_cache[inter] = name
            return name

        name11 = self.name11.get(inter)
        if name11 and jidx.find(self.ver, owner, kind=kind, name=name11):
            self.notes.append(('byname', inter, name11))
            self.member_cache[inter] = name11
            return name11

        loose = self._loose(owner, kind, want) if kind == 'm' else None
        if loose:
            self.notes.append(('loose', inter, loose))
            self.member_cache[inter] = loose
            return loose

        self.manual['%s %s' % ('method' if kind == 'm' else 'field', inter)] = [
            '%s%s' % (m['name'], m['desc'])
            for m in jidx.info(self.ver, owner).get('members', []) if m['kind'] == kind][:10]
        self.member_cache[inter] = name11 or inter
        return self.member_cache[inter]

    def chain(self, path):
        """`class_a.class_b` -> official dotted name, for source position."""
        parts = path.split('.')
        out = [self.cls('net.minecraft.' + parts[0]).replace('$', '.')]
        for p in parts[1:]:
            name = self.cls('net.minecraft.' + p).replace('$', '.')
            out.append(name.rsplit('.', 1)[-1])
        return '.'.join(out)

    def import_line(self, path):
        """`net.minecraft.class_a.class_b` -> the official dotted import target."""
        return 'import ' + self.chain(path) + ';'

    def _loose(self, owner, kind, want):
        """Same arity and same primitive pattern: survives a renamed parameter type."""
        wparams, wret = split_desc(want)
        wprims = [p for p in wparams if p in PRIM.values()]
        names = set()
        for cls in jidx.hierarchy(self.ver, owner):
            for m in jidx.info(self.ver, cls).get('members', []):
                if m['kind'] != kind:
                    continue
                p, r = split_desc(m['desc'])
                if len(p) != len(wparams) or r != wret:
                    continue
                if [q for q in p if q in PRIM.values()] != wprims:
                    continue
                names.add(m['name'])
        return names.pop() if len(names) == 1 else None


def overlay_files(dirs):
    """Relative paths of the hand-written files that replace generated ones.

    javac would see two definitions of the same class if both trees were emitted,
    so anything an overlay provides is left out of the generated tree.
    """
    skip = set()
    for d in dirs:
        if not os.path.isdir(d):
            continue
        for root, _dirs, files in os.walk(d):
            for f in files:
                if f.endswith('.java'):
                    skip.add(os.path.relpath(os.path.join(root, f), d).replace('\\', '/'))
    return skip


def main():
    ver = sys.argv[1]
    out = os.path.join(ROOT, 'versions', 'gen', 'v' + ver.replace('.', '_'), 'java')
    if '--out' in sys.argv:
        out = os.path.abspath(sys.argv[sys.argv.index('--out') + 1])

    # 26.x releases move far less than 1.21.11 -> 26.x does, so most of the port
    # lives in one overlay shared by the whole line and each version dir only
    # carries what that release changed.
    overlays = [os.path.join(ROOT, 'versions', 'v26_shared', 'java'),
                os.path.join(ROOT, 'versions', 'v' + ver.replace('.', '_'), 'java')]
    for i, arg in enumerate(sys.argv):
        if arg == '--overlay':
            overlays.append(os.path.abspath(sys.argv[i + 1]))
    skip = overlay_files(overlays)
    # ModMenu has no 26.x build to compile against, so the config-screen hook is
    # left out of these modules; the entrypoint is stripped from fabric.mod.json
    # by common26.gradle to match.
    skip.add('dev/valence/ModMenuIntegration.java')

    res = Resolver(ver)
    total = 0
    for src in glob.glob(os.path.join(SOURCES, '**', '*.java'), recursive=True):
        raw = io.open(src, encoding='utf8', newline='').read()
        rel = os.path.relpath(src, SOURCES)
        dest = os.path.join(out, rel)
        if rel.replace('\\', '/') in skip:
            if os.path.exists(dest):
                os.remove(dest)
            continue
        os.makedirs(os.path.dirname(dest), exist_ok=True)

        # A Minecraft class can land on a simple name the file already imports from
        # somewhere else (PoseStack.Pose next to the mod's own Pose). Two single-type
        # imports of the same simple name is a compile error, so those are written
        # out in full at the use site instead of imported.
        taken = set(re.findall(r'^import[ \t]+(?!net\.minecraft\.class_)[\w.]+\.(\w+);',
                               raw, re.M))
        overrides = {}

        def imp(m):
            fqn = res.import_line(m.group(1))
            target = fqn[len('import '):].rstrip(';')
            if target.rsplit('.', 1)[-1] in taken:
                overrides[m.group(1).rsplit('.', 1)[-1]] = target
                return ''
            return fqn

        text = IMPORT.sub(imp, raw)
        # A class reached through another class (`Outer.Inner`) is rewritten as a unit
        # so the outer name is not expanded twice.
        text = CHAIN.sub(lambda m: res.chain(m.group(1)), text)
        text = QUALIFIED.sub(lambda m: res.cls('net.minecraft.' + m.group(1)).replace('$', '.'), text)

        def sub(m):
            tok = m.group(0)
            if tok in overrides:
                return overrides[tok]
            if not tok.startswith('class_'):
                return res.member(tok)
            name = res.cls('net.minecraft.' + tok)
            # Inside a JVM descriptor the name has to stay binary (`Foo$Bar`, slashes);
            # in source it has to be the canonical dotted form (`Foo.Bar`).
            if text[:m.start()].endswith('Lnet/minecraft/') and text[m.end():m.end() + 1] == ';':
                return name.replace('.', '/')
            return name.replace('$', '.')

        new, n = TOKEN.subn(sub, text)
        with io.open(dest, 'w', encoding='utf8', newline='') as fh:
            fh.write(new)
        total += n

    jidx.save(ver)

    kinds = {}
    for kind, _a, _b in res.notes:
        kinds[kind] = kinds.get(kind, 0) + 1
    print('version     ', ver)
    print('output      ', os.path.relpath(out, ROOT).replace('\\', '/'))
    print('symbols     ', total)
    print('manual      ', len(res.manual))
    print('fallbacks   ', kinds)
    print()
    for kind, a, b in res.notes:
        if kind == 'fuzzy':
            print('  fuzzy  %-58s %s' % (a, b))
    print()
    for key in sorted(res.manual):
        print('MANUAL', key)
        for cand in res.manual[key][:6]:
            print('        ', cand)


if __name__ == '__main__':
    main()
