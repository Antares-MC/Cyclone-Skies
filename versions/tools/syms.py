"""Count the Minecraft symbols the mod references, to size a version port."""

import glob
import io
import re

TOK = re.compile(r'\bclass_\d+\b|\bmethod_\d+\b|\bfield_\d+\b')

classes = set()
members = set()
per_file = []

for path in glob.glob('cyclone/src/main/java/**/*.java', recursive=True):
    text = io.open(path, encoding='utf8', errors='replace').read()
    c = set(t for t in TOK.findall(text) if t.startswith('class_'))
    m = set(t for t in TOK.findall(text) if not t.startswith('class_'))
    classes |= c
    members |= m
    per_file.append((len(c) + len(m), path))

print('classes   ', len(classes))
print('members   ', len(members))
print('total refs', len(classes) + len(members))

per_file.sort(reverse=True)
print()
for n, path in per_file[:15]:
    print('%4d  %s' % (n, path.replace('\\', '/').split('/java/')[-1]))

out = io.open('versions/tools/sym.json', 'w', encoding='utf8')
import json
json.dump({'classes': sorted(classes), 'members': sorted(members)}, out, indent=0)
out.close()
print()
print('wrote versions/tools/sym.json')
