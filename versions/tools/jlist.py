"""Print the member list of several classes in one jar, for API archaeology."""

import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import jidx  # noqa: E402

ver = sys.argv[1]
pat = sys.argv[2].lower() if len(sys.argv) > 2 else None

for cls in sys.argv[3:]:
    entry = jidx.info(ver, cls)
    if entry.get('missing'):
        print('== %s MISSING' % cls)
        continue
    print('== %s   (super %s)' % (cls, entry.get('super')))
    for m in entry['members']:
        row = '%s %s%s' % (m['kind'], m['name'], m['desc'])
        if pat is None or pat in row.lower():
            print('   ', row)
jidx.save(ver)
