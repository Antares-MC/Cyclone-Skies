"""One-shot: make mojmap importable and fix the obf->intermediary lookup in mojemit."""

import io

p = 'versions/tools/mojmap.py'
s = io.open(p, encoding='utf8').read()
s = s.replace("\n\nmain()\n", "\n\nif __name__ == '__main__':\n    main()\n")
io.open(p, 'w', encoding='utf8').write(s)

p = 'versions/tools/mojemit.py'
s = io.open(p, encoding='utf8').read()
if 'inter_of_obf' not in s:
    s = s.replace("        self.owner_of = {}",
                  "        self.inter_of_obf = {obf: inter for inter, obf in mapping['obf'].items()}\n"
                  "        self.owner_of = {}")
    s = s.replace("""        owner11 = self.classes.get('net.minecraft.' + owner_obf.lstrip('/'),
                                   self.classes.get(owner_obf))""",
                  """        owner11 = self.classes.get(self.inter_of_obf.get(owner_obf, owner_obf))""")
    io.open(p, 'w', encoding='utf8').write(s)

print('guarded:', 'if __name__' in io.open('versions/tools/mojmap.py', encoding='utf8').read())
print('patched:', 'inter_of_obf' in io.open('versions/tools/mojemit.py', encoding='utf8').read())
