"""Add the States import to the occlusion probe."""

import io

PATH = 'cyclone/src/main/java/dev/valence/perf/Occlusion.java'
IMPORT = 'import dev.valence.compat.States;'

text = io.open(PATH, encoding='utf8', newline='').read()
if IMPORT not in text:
    if 'import dev.valence.compat.Pose;' in text:
        text = text.replace('import dev.valence.compat.Pose;',
                            'import dev.valence.compat.Pose;\r\n' + IMPORT, 1)
    else:
        idx = text.index('import ')
        text = text[:idx] + IMPORT + '\r\n' + text[idx:]
    io.open(PATH, 'w', encoding='utf8', newline='').write(text)
    print('updated')
else:
    print('already present')
