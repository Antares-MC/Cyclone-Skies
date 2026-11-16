"""Copy the pre-1.21.9 Fonts facade into the 1.21.6 - 1.21.8 modules.

Those three modules keep their own copy of the compatibility tree rather than being rebuilt
from the pre-1.21.6 template, so a change to the shared facade has to be mirrored into all
of them by hand.
"""

import io
import os
import shutil

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
SOURCE = os.path.join(ROOT, 'versions', 'v1_21_5', 'java', 'dev', 'valence', 'compat', 'Fonts.java')
TARGETS = ['v1_21_6', 'v1_21_7', 'v1_21_8']

for module in TARGETS:
    dest = os.path.join(ROOT, 'versions', module, 'java', 'dev', 'valence', 'compat', 'Fonts.java')
    shutil.copyfile(SOURCE, dest)
    print('copied ->', os.path.relpath(dest, ROOT))

text = io.open(SOURCE, encoding='utf8').read()
print('has draw:', 'public static void draw(' in text)
