"""On-demand index of one deobfuscated Minecraft jar.

    python versions/tools/jidx.py 26.1.2 net.minecraft.client.gui.GuiGraphicsExtractor

Exposes a small API for the other tools:

    info('26.1.2', 'net.minecraft.client.gui.Font') -> {'super': ..., 'members': [...]}
    find('26.1.2', cls, kind='m', desc='(I)I')     -> [(owner, name, desc, static)]

The index is built with javap and cached in %TEMP%\\opencode\\jidx<ver>.json, so the
emitter can resolve members by descriptor without loading a Java symbol solver.
"""

import glob
import io
import json
import os
import re
import subprocess
import sys
import zipfile

BASE = os.path.expanduser('~/.gradle/caches/fabric-loom')
TMP = os.path.join(os.environ['TEMP'], 'opencode')

_tmp = {}
_cache = None


def jar_for(ver):
    path = os.path.join(BASE, ver, 'minecraft-merged.jar')
    if not os.path.exists(path):
        raise SystemExit('no merged jar for ' + ver + ': ' + path)
    return path


def class_names(ver):
    global _tmp
    if ver not in _tmp:
        with zipfile.ZipFile(jar_for(ver)) as z:
            names = [n[:-6].replace('/', '.') for n in z.namelist()
                     if n.endswith('.class') and not n.startswith('META-INF')]
        _tmp[ver] = sorted(names)
    return _tmp[ver]


def _cache_path(ver):
    return os.path.join(TMP, 'jidx%s.json' % ver)


def _load(ver):
    global _cache
    if _cache is not None and _cache.get('ver') == ver:
        return _cache
    path = _cache_path(ver)
    data = {'ver': ver, 'classes': {}}
    if os.path.exists(path):
        try:
            with io.open(path, encoding='utf8') as fh:
                loaded = json.load(fh)
            if loaded.get('ver') == ver:
                data = loaded
        except ValueError:
            pass
    _cache = data
    return data


def save(ver):
    data = _load(ver)
    with io.open(_cache_path(ver), 'w', encoding='utf8') as fh:
        json.dump(data, fh)


DECL = re.compile(r'^  (?:public |protected |private |static |final |abstract |native |synchronized |transient |volatile |default |strictfp )*'
                  r'([\w.$\[\]<>?, ]+?)\s+([\w$]+)\((.*)\)\s*;?$')
FIELD = re.compile(r'^  (?:public |protected |private |static |final |abstract |native |synchronized |transient |volatile |default |strictfp )*'
                   r'([\w.$\[\]<>?, ]+?)\s+([\w$]+)\s*;?$')
HEAD = re.compile(r'^(?:public |protected |private |static |final |abstract |sealed |non-sealed |strictfp )*'
                  r'(class|interface|enum|record)\s+([\w.$]+)(.*)$')


def info(ver, cls):
    """Parse one class with javap -p -s, caching the result."""
    data = _load(ver)
    hit = data['classes'].get(cls)
    if hit is not None:
        return hit

    res = subprocess.run(['javap', '-p', '-s', '-classpath', jar_for(ver), cls],
                         capture_output=True, text=True, errors='replace')
    text = res.stdout
    if 'Error' in text and not text.strip().startswith('Compiled'):
        entry = {'missing': True, 'super': None, 'members': []}
        data['classes'][cls] = entry
        return entry

    members = []
    supername = None
    lines = text.splitlines()
    for i, line in enumerate(lines):
        m = HEAD.match(line)
        if m and supername is None and (' extends ' in line or True):
            rest = m.group(3)
            ext = re.search(r'extends ([\w.$]+)', rest)
            if ext:
                supername = ext.group(1)
        if i + 1 < len(lines) and lines[i + 1].strip().startswith('descriptor:'):
            desc = lines[i + 1].strip().split('descriptor:', 1)[1].strip()
            body = line.strip()
            if '(' in body:
                name = body[:body.index('(')].split()[-1]
                kind = 'm'
            else:
                name = body.rstrip(';').split()[-1]
                kind = 'f'
            if name and name not in ('static', '{}'):
                members.append({
                    'kind': kind,
                    'name': name,
                    'desc': desc,
                    'static': ' static ' in ' ' + body + ' ',
                })

    entry = {'missing': False, 'super': supername, 'members': members}
    data['classes'][cls] = entry
    return entry


def hierarchy(ver, cls, limit=12):
    """The class and its superclasses, nearest first."""
    out = []
    seen = set()
    cur = cls
    while cur and cur not in seen and len(out) < limit:
        seen.add(cur)
        entry = info(ver, cur)
        if entry.get('missing'):
            break
        out.append(cur)
        cur = entry.get('super')
    return out


def find(ver, cls, kind=None, name=None, desc=None):
    """Declared-then-inherited member search."""
    for owner in hierarchy(ver, cls):
        entry = info(ver, owner)
        hits = [m for m in entry['members']
                if (kind is None or m['kind'] == kind)
                and (name is None or m['name'] == name)
                and (desc is None or m['desc'] == desc)]
        if hits:
            return [(owner, h['name'], h['desc'], h['static']) for h in hits]
    return []


if __name__ == '__main__':
    ver = sys.argv[1]
    cls = sys.argv[2]
    if cls == '--has':
        name = sys.argv[3]
        print(name, 'present' if name in set(class_names(ver)) else 'missing')
    else:
        entry = info(ver, cls)
        print('super:', entry.get('super'), 'members:', len(entry.get('members', [])))
        for m in entry.get('members', []):
            print('  %s %-40s %s' % (m['kind'], m['name'] + m['desc'], 'static' if m['static'] else ''))
        save(ver)
