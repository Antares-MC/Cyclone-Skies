"""Tools/patches for the 26.x translation: Mojang field rows and a yarn fallback.

Mojang's mapping rows for fields carry no line numbers:

    net.minecraft.resources.Identifier DEFAULT_FONT -> a

while method rows do:

       9:10:void youJustLostTheGame() -> a

The old regex required the line numbers, so every field row was skipped.
"""

import io

p = 'versions/tools/mojmap.py'
s = io.open(p, encoding='utf8').read()
s = s.replace(r"MOJ_FIELD = re.compile(r'^\s+\d+:\d+:(\S+) (\S+) -> (\S+)$')",
              r"MOJ_FIELD = re.compile(r'^\s+(?:\d+:\d+:)?(\S+) (\S+) -> (\S+)$')")
io.open(p, 'w', encoding='utf8').write(s)
print('field regex patched:', 'MOJ_FIELD' in s)
