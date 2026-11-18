"""Refuse to ship anything that is not Cyclone Skies.

The mod is cosmetic + informational only. It registers a handful of its own
key bindings and draws. It does not move the player, aim, reveal containers,
detach the camera, or automate combat, and the released jar must not contain
another mod that does.

Run this against a built jar (or a directory of jars) before publishing:

    python versions/tools/verify_clean.py C:\\Users\\cjsmi\\Downloads\\CycloneJars

Exit code 0 = clean, 1 = a forbidden module, key binding or bundled jar was
found. The checks read the artifact, not the sources, so they also catch an
overlay file that slipped into a per-version module.
"""

from __future__ import annotations

import io
import json
import os
import re
import sys
import zipfile

# Every key binding the mod is allowed to register. Anything else in a lang
# file means a feature was added that the release notes do not describe.
ALLOWED_KEYS = {
    "key.valence.open_menu",
    "key.valence.media_next",
    "key.valence.media_prev",
    "key.valence.media_play",
    "key.valence.emote",
    "key.valence.lyrics",
    "key.valence.sync",
    "key.valence.fav",
    "key.valence.clip",
    "key.valence.profile",
    "key.paymentalerts.toggle",
    "key.paymentalerts.settings",
    "key.paymentalerts.stats",
}

# Module names that must never appear in a class path or asset path.
FORBIDDEN = [
    r"freecam",
    r"free_cam",
    r"xray",
    r"x_ray",
    r"wallhack",
    r"aimbot",
    r"aim_assist",
    r"killaura",
    r"kill_aura",
    r"autoclicker",
    r"auto_clicker",
    r"triggerbot",
    r"tracers?",
    r"noclip",
    r"no_clip",
    r"scaffold",
    r"fullbright",
    r"full_bright",
    r"antiknockback",
    r"anti_knockback",
    r"autototem",
    r"auto_totem",
    r"crystalaura",
    r"crystal_aura",
    r"holeesp",
    r"hole_esp",
    r"nametags",
]

# `esp` only counts as its own token: response/respective must not trip it.
FORBIDDEN_TOKEN = re.compile(
    r"(?:^|[/_.$])(?:esp|chestesp|storageesp|playeresp|mobesp|entityesp|blockesp)(?:[/_.$]|$)",
    re.IGNORECASE,
)

ALLOWED_NESTED = {"paymentalerts"}

MOD_ID = "cycloneskies"


def fail(lines: list[str], msg: str) -> None:
    lines.append("  FAIL " + msg)


def scan_keybinds(zf: zipfile.ZipFile, lines: list[str]) -> None:
    for name in zf.namelist():
        if not re.fullmatch(r"assets/[^/]+/lang/en_us\.json", name):
            continue
        try:
            data = json.loads(zf.read(name).decode("utf-8-sig"))
        except Exception as exc:  # pragma: no cover - malformed lang file
            fail(lines, f"{name}: unreadable ({exc})")
            continue
        for key in data:
            if key.startswith("key.category.") and key.split(".")[2] in {
                "valence",
                "paymentalerts",
            }:
                # The header the bindings are filed under, not a binding.
                continue
            if key.startswith("key.") and key not in ALLOWED_KEYS:
                fail(lines, f"{name}: unexpected key binding {key!r}")


def scan_names(zf: zipfile.ZipFile, lines: list[str]) -> None:
    for name in zf.namelist():
        for pattern in FORBIDDEN:
            if re.search(pattern, name, re.IGNORECASE):
                fail(lines, f"forbidden module name in {name!r} ({pattern})")
                break
        if FORBIDDEN_TOKEN.search(name):
            fail(lines, f"forbidden module name in {name!r} (esp token)")


def scan_nested(zf: zipfile.ZipFile, lines: list[str]) -> None:
    for name in zf.namelist():
        if not name.startswith("META-INF/jars/") or not name.endswith(".jar"):
            continue
        base = os.path.basename(name)[: -len(".jar")]
        stem = re.split(r"[-+]", base, maxsplit=1)[0]
        if stem not in ALLOWED_NESTED:
            fail(lines, f"unexpected bundled jar {name!r}")
            continue
        try:
            inner = zipfile.ZipFile(io.BytesIO(zf.read(name)))
        except Exception as exc:
            fail(lines, f"{name}: unreadable nested jar ({exc})")
            continue
        meta = "fabric.mod.json"
        if meta not in inner.namelist():
            fail(lines, f"{name}: no fabric.mod.json")
        else:
            info = json.loads(inner.read(meta).decode("utf-8-sig"))
            if info.get("id") != stem:
                fail(lines, f"{name}: id {info.get('id')!r} != {stem!r}")
        scan_names(inner, lines)
        scan_keybinds(inner, lines)


def scan_metadata(zf: zipfile.ZipFile, lines: list[str]) -> None:
    if "fabric.mod.json" not in zf.namelist():
        fail(lines, "no fabric.mod.json")
        return
    info = json.loads(zf.read("fabric.mod.json").decode("utf-8-sig"))
    if info.get("id") != MOD_ID:
        fail(lines, f"fabric.mod.json id {info.get('id')!r} != {MOD_ID!r}")
    hooks = info.get("custom", {}).get("fabric-client-gametest-api-v1")
    if hooks:
        fail(lines, "client gametest hook in release metadata")


def check(path: str) -> list[str]:
    lines: list[str] = []
    try:
        zf = zipfile.ZipFile(path)
    except Exception as exc:
        return [f"  FAIL unreadable jar ({exc})"]
    scan_metadata(zf, lines)
    scan_names(zf, lines)
    scan_keybinds(zf, lines)
    scan_nested(zf, lines)
    return lines


def main(argv: list[str]) -> int:
    targets = argv[1:] or ["."]
    jars: list[str] = []
    for target in targets:
        if os.path.isdir(target):
            jars.extend(
                os.path.join(target, n)
                for n in sorted(os.listdir(target))
                if n.endswith(".jar")
            )
        else:
            jars.append(target)

    bad = 0
    for jar in jars:
        problems = check(jar)
        name = os.path.basename(jar)
        if problems:
            bad += 1
            print(f"FAIL {name}")
            for line in problems:
                print(line)
        else:
            print(f"PASS {name}")

    print(f"\n{len(jars) - bad}/{len(jars)} clean")
    return 1 if bad else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))
