"""Join Fabric intermediary names to Mojang official names for one version.

    python versions/tools/mojmap.py 1.21.11

The two files agree on the obfuscated name, so that is the join key:

    intermediary-v2.tiny      obf -> intermediary      (Fabric, in the Loom cache)
    client_<ver>.txt          official -> obf          (Mojang, in %TEMP%\\opencode)

The result is written to versions/tools/map<ver>.json:

    classes   intermediary (dotted) -> official (dotted)
    methods   "owner#name#desc"     -> official name
    fields    "owner#name"          -> official name

Class entries carry the obf name too, because the 26.x jump has no obf side:
the official name from here is matched against the deobfuscated jar instead.
"""

import io
import json
import os
import re
import sys

TINY = os.path.join(os.path.expanduser('~'), '.gradle', 'caches', 'fabric-loom',
                    '%s', 'intermediary-v2.tiny')
OFFICIAL = os.path.join(os.environ['TEMP'], 'opencode', 'client_%s.txt')

#   9:10:void youJustLostTheGame() -> a
#   13:13:double getTime() -> b
#    5:5:double field -> a
MOJ_METHOD = re.compile(r'^\s+\d+:\d+:(\S+) (\S+?)\((.*)\) -> (\S+)$')
MOJ_FIELD = re.compile(r'^\s+(?:\d+:\d+:)?(\S+) (\S+) -> (\S+)$')
MOJ_CLASS = re.compile(r'^(\S+) -> (\S+):$')


def parse_tiny(path):
    """obf -> intermediary for classes, plus per-class member rows."""
    classes = {}
    methods = []
    fields = []
    with io.open(path, encoding='utf8', errors='replace') as fh:
        header = fh.readline().rstrip('\n').split('\t')
        if header[:1] != ['tiny']:
            raise SystemExit('not a tiny file: %r' % header)
        owner = None
        for line in fh:
            if not line.strip():
                continue
            parts = line.rstrip('\n').split('\t')
            if parts[0] == 'c':
                owner = parts[1]
                classes[owner] = parts[2]
            elif parts[0] == '' and len(parts) > 1 and parts[1] in ('m', 'f'):
                kind, desc, obf, inter = parts[1], parts[2], parts[3], parts[4]
                if kind == 'm':
                    methods.append((owner, desc, obf, inter))
                else:
                    fields.append((owner, desc, obf, inter))
    return classes, methods, fields


def parse_official(path):
    """obf class -> official class, and per obf class a member table."""
    classes = {}
    methods = {}
    fields = {}
    current = None
    with io.open(path, encoding='utf8', errors='replace') as fh:
        for line in fh:
            if line.startswith('#'):
                continue
            m = MOJ_CLASS.match(line.rstrip('\n'))
            if m:
                official, obf = m.group(1), m.group(2)
                classes[obf] = official
                current = obf
                continue
            if current is None:
                continue
            m = MOJ_METHOD.match(line.rstrip('\n'))
            if m:
                ret, name, args, obf = m.groups()
                methods.setdefault(current, {}).setdefault(obf, []).append(
                    (name, ret, [a.strip() for a in args.split(',') if a.strip()]))
                continue
            m = MOJ_FIELD.match(line.rstrip('\n'))
            if m:
                typ, name, obf = m.groups()
                fields.setdefault(current, {}).setdefault(obf, []).append((name, typ))
    return classes, methods, fields


def desc_types(desc):
    """(parameter types, return type) of a JVM descriptor, dotted, no generics."""
    def one(s, i):
        arr = 0
        while s[i] == '[':
            arr += 1
            i += 1
        if s[i] == 'L':
            j = s.index(';', i)
            name = s[i + 1:j].replace('/', '.')
            i = j + 1
        else:
            prim = {'Z': 'boolean', 'B': 'byte', 'C': 'char', 'S': 'short',
                    'I': 'int', 'J': 'long', 'F': 'float', 'D': 'double', 'V': 'void'}
            name = prim[s[i]]
            i += 1
        return name + '[]' * arr, i

    i = 1
    params = []
    while desc[i] != ')':
        name, i = one(desc, i)
        params.append(name)
    ret, i = one(desc, i + 1)
    return params, ret


def simple(name):
    """Compare types without package or generics noise."""
    return name.rsplit('.', 1)[-1]


def main():
    ver = sys.argv[1]
    tiny_path = TINY % ver
    off_path = OFFICIAL % ver.replace('.', '_')
    if not os.path.exists(tiny_path):
        raise SystemExit('missing ' + tiny_path)
    if not os.path.exists(off_path):
        raise SystemExit('missing ' + off_path)

    obf_classes, obf_methods, obf_fields = parse_tiny(tiny_path)
    off_classes, off_methods, off_fields = parse_official(off_path)

    inter_to_off = {}
    obf_of = {}
    for obf, inter in obf_classes.items():
        official = off_classes.get(obf)
        if official is None:
            continue
        dotted = inter.replace('/', '.')
        inter_to_off[dotted] = official
        obf_of[dotted] = obf

    def rename_args(params):
        return [inter_to_off.get(p, p).rsplit('.', 1)[-1] for p in params]

    methods = {}
    ambiguous = []
    for owner, desc, obf, inter in obf_methods:
        official = off_classes.get(owner)
        if official is None:
            continue
        cands = off_methods.get(owner, {}).get(obf)
        if not cands:
            continue
        want, _ret = desc_types(desc)
        want = rename_args(want)
        hit = [n for n, _r, args in cands if [simple(a) for a in args] == want]
        if len(hit) != 1:
            if len(cands) == 1 and not hit:
                hit = [cands[0][0]]
            else:
                ambiguous.append((owner, inter, obf, want, cands))
                continue
        methods['%s#%s#%s' % (official, inter, desc)] = hit[0]

    fields = {}
    for owner, desc, obf, inter in obf_fields:
        official = off_classes.get(owner)
        if official is None:
            continue
        cands = off_fields.get(owner, {}).get(obf)
        if not cands:
            continue
        hit = [n for n, _t in cands]
        if len(hit) != 1:
            ambiguous.append((owner, inter, obf, 'field', cands))
            continue
        fields['%s#%s' % (official, inter)] = hit[0]

    out = {
        'version': ver,
        'classes': inter_to_off,
        'obf': obf_of,
        'methods': methods,
        'fields': fields,
    }
    dest = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'map%s.json' % ver)
    with io.open(dest, 'w', encoding='utf8') as fh:
        json.dump(out, fh, indent=0, sort_keys=True)

    print('classes  %d / %d obf' % (len(inter_to_off), len(obf_classes)))
    print('methods  %d / %d' % (len(methods), len(obf_methods)))
    print('fields   %d / %d' % (len(fields), len(obf_fields)))
    print('ambiguous %d' % len(ambiguous))
    for row in ambiguous[:8]:
        print('   ', row[0], row[1], row[2], row[3], row[4][:3])
    print('wrote', os.path.basename(dest))
    for probe in ('net.minecraft.class_310', 'net.minecraft.class_332',
                  'net.minecraft.class_327', 'net.minecraft.class_2960'):
        print('  %-28s -> %s' % (probe, inter_to_off.get(probe, 'MISSING')))


if __name__ == '__main__':
    main()
