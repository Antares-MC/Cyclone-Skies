#!/usr/bin/env python3
"""Resolve the render-layer factory ids used by CycloneSkies for a version.

The mod builds vertex-consumer buffers from eight vanilla render layers.  Where
those live changed across versions:

  * 1.21.11  -- statics on RenderLayers  (class_12249)
  * <=1.21.10 -- statics on RenderLayer  (class_1921)

This prints the id to use in the per-version dev.valence.compat.Layers overlay.

usage: layerids.py <version> [...]
"""
import glob
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from xlate import Side, norm  # noqa: E402

OPERATIONS = [
    ("entityCutoutNoCull", "cutout"),
    ("entityTranslucent", "translucent"),
    ("entityTranslucentEmissive", "translucentEmissive"),
    ("text", "text"),
    ("textSeeThrough", "textSeeThrough"),
    ("debugQuads", "quads"),
    ("beaconBeam", "beacon"),
    ("fireScreenEffect", "fire"),
]


def statics(side):
    """named-class -> {normalized member name: (id, shape)} for methods only."""
    out = {}
    for (owner, named), sid in side.mem.items():
        if not sid.startswith("method_"):
            continue
        _owner, _named, shape = side.mem_id[sid]
        if not shape.endswith(")Lnet/minecraft/C;"):
            continue
        out.setdefault(owner, {}).setdefault(norm(named), (sid, shape))
    return out


def main():
    for game in sys.argv[1:]:
        side = Side(game)
        st = statics(side)
        print("=== %s ===" % game)
        for owner in ("net/minecraft/client/render/RenderLayers", "net/minecraft/client/render/RenderLayer"):
            table = st.get(owner, {})
            hits = [(op, table.get(norm(vanilla))) for vanilla, op in OPERATIONS]
            if not any(h[1] for h in hits):
                continue
            print("  owner %s (%s)" % (owner, side.cls_named.get(owner)))
            for op, hit in hits:
                if hit:
                    print("     %-20s %-12s %s" % (op, hit[0], hit[1]))
                else:
                    print("     %-20s MISSING" % op)


if __name__ == "__main__":
    main()
