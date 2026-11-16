"""Drop the RenderTickCounter parameter from the HUD element entry points.

1.21.6+ hands HUD elements a RenderTickCounter, older versions only hand
HudRenderCallback a float, so the facade calls them with just the draw context.
SkyRenderer is the only element that used the counter and now reads it from the
client like WorldSky and CosmeticRender already do.
"""

import io
import os
import re

ROOT = 'cyclone/src/main/java/'
FILES = [
    'dev/valence/hud/ValenceHud.java',
    'dev/valence/cord/hud/BeatGlow.java',
    'dev/valence/cord/hud/Island.java',
    'dev/valence/cord/hud/SongCard.java',
    'dev/valence/cord/hud/ScreenLyrics.java',
    'dev/valence/cord/sky/SkyRenderer.java',
]

for rel in FILES:
    p = ROOT + rel
    s = io.open(p, encoding='utf8', newline='').read()
    orig = s
    s = s.replace('public static void render(class_332 g, class_9779 delta) {',
                  'public static void render(class_332 g) {')
    s = re.sub(r'\r?\nimport net\.minecraft\.class_9779;', '', s)
    if rel.endswith('SkyRenderer.java'):
        s = s.replace('float pt = delta.method_60637(false);',
                      'float pt = class_310.method_1551().method_61966().method_60637(false);')
    if s != orig:
        io.open(p, 'w', encoding='utf8', newline='').write(s)
        print('updated', rel)
    else:
        print('UNCHANGED', rel)
