package com.paymentalerts.theme;

/**
 * A named color / style palette applied to payment alerts.
 */
public final class Theme {
    public final String id;
    public final String name;
    /** Primary RGB 0xRRGGBB */
    public final int primary;
    public final int secondary;
    public final int accent;
    public final int glow;
    /** Style family hint: gold, neon, cyber, nature, etc. */
    public final String style;

    public Theme(String id, String name, int primary, int secondary, int accent, int glow, String style) {
        this.id = id;
        this.name = name;
        this.primary = primary & 0xFFFFFF;
        this.secondary = secondary & 0xFFFFFF;
        this.accent = accent & 0xFFFFFF;
        this.glow = glow & 0xFFFFFF;
        this.style = style != null ? style : "default";
    }

    public int primaryArgb() {
        return Themes.argb(primary);
    }

    public int secondaryArgb() {
        return Themes.argb(secondary);
    }

    public int accentArgb() {
        return Themes.argb(accent);
    }

    public int glowArgb() {
        return Themes.argb(glow);
    }

    public int primaryArgb(float alpha) {
        return Themes.argb(primary, alpha);
    }

    public int secondaryArgb(float alpha) {
        return Themes.argb(secondary, alpha);
    }

    public int accentArgb(float alpha) {
        return Themes.argb(accent, alpha);
    }

    public int glowArgb(float alpha) {
        return Themes.argb(glow, alpha);
    }

    /** Mix primary toward secondary by t in [0,1] */
    public int mix(float t) {
        return mixRgb(primary, secondary, t);
    }

    public static int mixRgb(int a, int b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int ar = (a >> 16) & 0xFF, ag = (a >> 8) & 0xFF, ab = a & 0xFF;
        int br = (b >> 16) & 0xFF, bg = (b >> 8) & 0xFF, bb = b & 0xFF;
        int r = Math.round(ar + (br - ar) * t);
        int g = Math.round(ag + (bg - ag) * t);
        int bl = Math.round(ab + (bb - ab) * t);
        return (r << 16) | (g << 8) | bl;
    }

    @Override
    public String toString() {
        return name;
    }
}
