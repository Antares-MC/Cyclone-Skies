"""Validate xlate's auto-renames: for each source id -> target id, show both sides' owner,
yarn name and descriptor shape so mismatched guesses stand out."""

import os
import sys

sys.path.insert(0, os.path.join(os.environ['TEMP'], 'opencode'))
import importlib.util

spec = importlib.util.spec_from_file_location(
    'xlate', os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'tools', 'xlate.py'))
xlate = importlib.util.module_from_spec(spec)
spec.loader.exec_module(xlate)

frm, to = sys.argv[1], sys.argv[2]
src = xlate.Side(frm)
dst = xlate.Side(to)
used = xlate.usage_map(xlate.DEFAULT_SRC)
same, retype, rename, manual = xlate.classify(src, used, dst)

print('%-34s %-40s -> %-34s %s' % ('source', 'owner/source name', 'target', 'owner/target name'))
for k, v in sorted(rename.items()):
    o1, n1, s1 = src.mem_id.get(k, ('?', '?', '?'))
    o2, n2, s2 = dst.mem_id.get(v, ('?', '?', '?'))
    flag = '' if (o1 == o2 and n1 == n2 and s1 == s2) else '   <-- CHECK'
    print('%-14s %-19s %-28s -> %-14s %-19s %-20s%s' % (
        k, '', o1.rsplit('/', 1)[-1] + '/' + n1, v, '', o2.rsplit('/', 1)[-1] + '/' + n2, flag))
    if flag:
        print('     shape %s -> %s' % (s1, s2))
