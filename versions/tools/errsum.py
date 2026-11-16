"""Summarize javac errors: unique (message, symbol source line) with files and counts."""
import collections
import io
import re
import sys

path = sys.argv[1]
text = io.open(path, encoding='utf8', errors='replace').read().splitlines()

errs = []
i = 0
pat = re.compile(r'^(.*\.java):(\d+): error: (.*)$')
while i < len(text):
    m = pat.match(text[i])
    if m:
        src = text[i + 1].strip() if i + 1 < len(text) else ''
        errs.append((m.group(1).replace('\\', '/').split('/java/')[-1], m.group(2), m.group(3), src))
        i += 1
    i += 1

print('total errors:', len(errs))
key = collections.Counter()
where = collections.defaultdict(list)
for f, line, msg, src in errs:
    k = (msg, src)
    key[k] += 1
    where[k].append('%s:%s' % (f, line))
print('unique:', len(key))
for (msg, src), n in key.most_common():
    print('---', n, 'x')
    print('   ', msg)
    print('   >', src[:160])
    for w in where[(msg, src)][:6]:
        print('    @', w)
