"""Drop the two render-state imports the cosmetics page no longer uses."""

import io

PATH = 'cyclone/src/main/java/dev/valence/gui/pages/CosmeticsPage.java'

text = io.open(PATH, encoding='utf8', newline='').read()
for line in ('import net.minecraft.class_10017;\r\n', 'import net.minecraft.class_10042;\r\n',
             'import net.minecraft.class_10017;\n', 'import net.minecraft.class_10042;\n'):
    text = text.replace(line, '')
io.open(PATH, 'w', encoding='utf8', newline='').write(text)
print('class_10017 refs left:', text.count('class_10017'))
print('class_10042 refs left:', text.count('class_10042'))
