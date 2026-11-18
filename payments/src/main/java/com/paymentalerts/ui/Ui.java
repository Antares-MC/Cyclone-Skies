package com.paymentalerts.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The payment screens are part of the Cyclone Skies jar, so they are drawn in the same
 * language as the Cyclone menu instead of with the vanilla widget chrome: a deep navy
 * scrim, one translucent glass panel, hairline borders, and a single cyan accent.
 *
 * <p>The palette is Cyclone's default theme after its colour remap, which is why the
 * numbers below look arbitrary -- they are the mapped values, so the payments screens and
 * the menu are literally the same colours.
 *
 * <p>Everything is drawn with rounded spans. No textures, no sprites, no nine-slice
 * metadata, so the screens survive a resource reload and cannot pull a missing asset.
 */
public final class Ui {
    public static final int PANEL = 0xB8061019;
    public static final int SIDEBAR = 0x1AE6FBFF;
    public static final int CARD = 0x12E6FBFF;
    public static final int CARD_HOVER = 0x22E6FBFF;
    public static final int CARD_ACTIVE = 0x4425D9F2;
    public static final int BORDER = 0x1FE6FBFF;
    public static final int BORDER_HOVER = 0x559AD9F0;
    public static final int PANEL_BORDER = 0x409AD9F0;
    public static final int TRACK = 0x33E6FBFF;
    public static final int ACCENT = 0xFF25D9F2;
    public static final int ACCENT_LIGHT = 0xFFA6F3FF;
    public static final int ACCENT_DARK = 0xFF0B6E86;
    public static final int TEXT = 0xFFF0FCFF;
    public static final int TEXT_DIM = 0xFFA2C8DC;
    public static final int TEXT_MUTED = 0xFF6E93AB;
    public static final int GOOD = 0xFF35E39B;
    public static final int WARN = 0xFFFFC857;
    public static final int BAD = 0xFFFF6A85;
    public static final int SCRIM_TOP = 0x8C040C14;
    public static final int SCRIM_BOTTOM = 0xB3061220;
    /** One step of the scale the menu uses for section titles. */
    public static final int HEADER = 11;

    private Ui() {
    }

    // ---------- chrome ----------

    public static void scrim(GuiGraphics g, int width, int height) {
        g.fillGradient(0, 0, width, height, SCRIM_TOP, SCRIM_BOTTOM);
    }

    /** A panel with a shadow, a hairline border and a lit top edge. */
    public static void glass(GuiGraphics g, int x1, int y1, int x2, int y2, int fill, int border) {
        glow(g, x1, y1, x2, y2, ACCENT, 0.10f);
        round(g, x1, y1, x2, y2, fill, 8);
        outline(g, x1, y1, x2, y2, border, 8);
        round(g, x1 + 1, y1 + 1, x2 - 1, y1 + 2, withAlpha(ACCENT_LIGHT, 0.35f), 3);
    }

    public static void glass(GuiGraphics g, int x1, int y1, int x2, int y2) {
        glass(g, x1, y1, x2, y2, PANEL, PANEL_BORDER);
    }

    public static void card(GuiGraphics g, int x1, int y1, int x2, int y2, float hover, boolean selected) {
        int fill = selected ? lerp(CARD_ACTIVE, withAlpha(ACCENT, 0.42f), hover) : lerp(CARD, CARD_HOVER, hover);
        int border = selected ? lerp(withAlpha(ACCENT, 0.45f), BORDER_HOVER, hover) : lerp(BORDER, BORDER_HOVER, hover);
        if (hover > 0.01f || selected) {
            glow(g, x1, y1, x2, y2, ACCENT, selected ? 0.16f + 0.12f * hover : 0.09f * hover);
        }
        round(g, x1, y1, x2, y2, fill, 5);
        outline(g, x1, y1, x2, y2, border, 5);
    }

    public static void card(GuiGraphics g, int x1, int y1, int x2, int y2, int fill, int border) {
        round(g, x1, y1, x2, y2, fill, 5);
        outline(g, x1, y1, x2, y2, border, 5);
    }

    public static void divider(GuiGraphics g, int x1, int x2, int y) {
        g.fill(x1, y, x2, y + 1, BORDER);
    }

    // ---------- shapes ----------

    public static void round(GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
        round(g, x1, y1, x2, y2, color, 5);
    }

    public static void round(GuiGraphics g, int x1, int y1, int x2, int y2, int color, int radius) {
        if (x2 <= x1 || y2 <= y1 || (color >>> 24) == 0) {
            return;
        }
        int a = color >>> 24;
        int r = Math.max(1, Math.min(radius, Math.min((x2 - x1) / 2, (y2 - y1) / 2)));
        for (int y = 0; y < y2 - y1; y++) {
            int dy = y < r ? r - y - 1 : (y >= y2 - y1 - r ? y - (y2 - y1 - r) : 0);
            int inset = 0;
            if (dy > 0) {
                inset = r - (int) Math.round(Math.sqrt((double) (r * r - dy * dy)));
            }
            int alpha = a;
            if (dy > 0) {
                // Soften the outermost corner row so the curve does not show stair steps.
                double edge = Math.sqrt((double) (r * r - dy * dy));
                alpha = (int) (a * Math.min(1.0, edge / r + 0.15));
            }
            g.fill(x1 + inset, y1 + y, x2 - inset, y1 + y + 1, alpha << 24 | color & 0xFFFFFF);
        }
    }

    public static void outline(GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
        outline(g, x1, y1, x2, y2, color, 5);
    }

    public static void outline(GuiGraphics g, int x1, int y1, int x2, int y2, int color, int radius) {
        if (x2 <= x1 || y2 <= y1 || (color >>> 24) == 0) {
            return;
        }
        int r = Math.max(1, Math.min(radius, Math.min((x2 - x1) / 2, (y2 - y1) / 2)));
        int h = y2 - y1;
        for (int y = 0; y < h; y++) {
            int dy = y < r ? r - y - 1 : (y >= h - r ? y - (h - r) : 0);
            int inset = dy > 0 ? r - (int) Math.round(Math.sqrt((double) (r * r - dy * dy))) : 0;
            if (y == 0 || y == h - 1) {
                g.fill(x1 + inset, y1 + y, x2 - inset, y1 + y + 1, color);
            } else {
                g.fill(x1 + inset, y1 + y, x1 + inset + 1, y1 + y + 1, color);
                g.fill(x2 - inset - 1, y1 + y, x2 - inset, y1 + y + 1, color);
            }
        }
    }

    public static void pill(GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
        round(g, x1, y1, x2, y2, color, Math.min(9, (y2 - y1) / 2));
    }

    public static void circle(GuiGraphics g, int cx, int cy, int radius, int color) {
        for (int y = -radius; y <= radius; y++) {
            int dx = (int) Math.round(Math.sqrt((double) (radius * radius - y * y)));
            g.fill(cx - dx, cy + y, cx + dx, cy + y + 1, color);
        }
    }

    /** Four expanding rings of decreasing alpha; enough to read as lift, cheap to draw. */
    public static void glow(GuiGraphics g, int x1, int y1, int x2, int y2, int color, float strength) {
        if (strength <= 0.01f) {
            return;
        }
        for (int i = 4; i >= 1; i--) {
            float a = strength * (0.30f / i);
            round(g, x1 - i, y1 - i, x2 + i, y2 + i, withAlpha(color, a), 9 + i);
        }
    }

    // ---------- text ----------

    /** {@code AbstractWidget} has no font field, so widgets ask here instead. */
    public static Font font() {
        return Minecraft.getInstance().font;
    }

    public static String fit(Font font, String text, int width) {
        return font.width(text) <= width ? text : font.plainSubstrByWidth(text, Math.max(0, width - 6)) + "..";
    }

    public static void label(GuiGraphics g, Font font, String text, int x, int y, int width, int color) {
        g.drawString(font, Component.literal(fit(font, text, width)), x, y, color);
    }

    public static void centered(GuiGraphics g, Font font, String text, int cx, int y, int color) {
        g.drawCenteredString(font, Component.literal(text), cx, y, color);
    }

    public static void header(GuiGraphics g, Font font, String text, int cx, int y) {
        centered(g, font, text, cx, y, TEXT);
        int w = font.width(text);
        g.fill(cx - w / 2, y + 12, cx + w / 2, y + 13, withAlpha(ACCENT, 0.55f));
    }

    // ---------- colour ----------

    public static int withAlpha(int color, float alpha) {
        int a = (int) (((color >>> 24) & 0xFF) * Math.max(0.0f, Math.min(1.0f, alpha)));
        return a << 24 | color & 0xFFFFFF;
    }

    public static int alpha(int color, int alpha) {
        return Math.max(0, Math.min(255, alpha)) << 24 | color & 0xFFFFFF;
    }

    public static int lerp(int a, int b, float t) {
        t = Math.max(0.0f, Math.min(1.0f, t));
        int aa = a >>> 24 & 0xFF;
        int ar = a >> 16 & 0xFF;
        int ag = a >> 8 & 0xFF;
        int ab = a & 0xFF;
        int ba = b >>> 24 & 0xFF;
        int br = b >> 16 & 0xFF;
        int bg = b >> 8 & 0xFF;
        int bb = b & 0xFF;
        return (int) (aa + (ba - aa) * t) << 24
                | (int) (ar + (br - ar) * t) << 16
                | (int) (ag + (bg - ag) * t) << 8
                | (int) (ab + (bb - ab) * t);
    }
}
