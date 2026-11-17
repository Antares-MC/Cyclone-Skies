package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Stream alert: the channel goes live, and the frame behaves like a broadcast control room.
 *
 * <p>The beats are all broadcast hardware rather than particles: a standby tally that blinks
 * on the test bars, a hard cut to air with one frame of pure red, an eight-blade lens
 * aperture that rotates open to reveal the channel, a lower third that slides in and holds,
 * chat chips that stack up the right edge, a viewer counter that counts on, a subscribe
 * button that is actually pressed (scale dip, ripple, bell flash), then a red wipe to a
 * closing card with the URL.
 *
 * <p>No burst, no confetti: this effect is typography and panel motion.
 */
final class StreamAlert extends StageBase {
    static final String[] VARIATIONS = {"YouTube", "Studio", "Hype"};

    /** Channel identity shown by the alert. */
    static final String HANDLE = "@lumindevdsmp";
    static final String URL = "youtube.com/@lumindevdsmp";

    private static final float STANDBY = 0.0f;
    private static final float LIGHT = 1.05f;
    private static final float APERTURE = 1.30f;
    private static final float THIRD = 2.40f;
    private static final float CHAT = 3.05f;
    private static final float SUB = 5.45f;
    private static final float PRESS = 6.05f;
    private static final float WIPE = 7.55f;
    private static final float END = 9.40f;

    private static final int CHIPS = 6;
    private static final int BLADES = 8;

    private static final int[][] PALETTES = {
            // fill, accent, text, dim, panel
            {0xFF0033, 0xFF4D6A, 0xFFFFFF, 0xB0B4BC, 0x141418},
            {0x00D9FF, 0x7DF9FF, 0x0B1014, 0x5A6A78, 0x0A0E12},
            {0xB44DFF, 0xFF5CC8, 0xFFFFFF, 0xC0A0D0, 0x160A20},
    };

    private final String title;

    private final float[] chipY = new float[CHIPS];
    private final float[] chipWidth = new float[CHIPS];
    private final float[] chipLine1 = new float[CHIPS];
    private final float[] chipLine2 = new float[CHIPS];
    private final float[] chipHue = new float[CHIPS];
    private final float[] barX = new float[7];

    private float viewers;

    StreamAlert(Payment payment, long now, int variation, String title) {
        super(payment, now, variation);
        this.title = title == null ? "" : title;
        for (int i = 0; i < CHIPS; i++) {
            this.chipY[i] = 0.23f + i * 0.062f;
            this.chipWidth[i] = rnd(0.16f, 0.27f);
            this.chipLine1[i] = rnd(0.45f, 0.82f);
            this.chipLine2[i] = rnd(0.25f, 0.66f);
            this.chipHue[i] = rnd(0f, 1f);
        }
        for (int i = 0; i < this.barX.length; i++) {
            this.barX[i] = i / (float) this.barX.length;
        }
    }

    @Override
    protected float duration() {
        return END;
    }

    @Override
    protected String[] looks() {
        return VARIATIONS;
    }

    @Override
    protected boolean drawsBanner() {
        return false;
    }

    private int[] palette() {
        return PALETTES[look()];
    }

    private int fill() {
        return palette()[0];
    }

    private int accent() {
        return palette()[1];
    }

    private int text() {
        return palette()[2];
    }

    private int dim() {
        return palette()[3];
    }

    private int panel() {
        return palette()[4];
    }

    @Override
    protected void cue() {
        // Standby ticks: slow, mechanical, four of them before the cut.
        for (int i = 0; i < 4; i++) {
            this.cues.sound(STANDBY + 0.10f + i * 0.23f, AlertSounds.JACKPOT_TICK, 0.30f,
                    1.55f + i * 0.07f);
        }
        this.cues.action(0.15f, () -> AlertSounds.playSliding(AlertSounds.LASER_HUM,
                        0.75f, 0.55f, 0.9f, 0.10f, 0.22f))
                .sound(LIGHT, AlertSounds.JACKPOT_STOP, 0.85f, 1.05f)
                .sound(LIGHT, AlertSounds.CONFETTI_POP, 0.55f, 1.25f)
                .sound(LIGHT + 0.05f, AlertSounds.LASER_FIRE, 0.45f, 1.45f)
                .sound(APERTURE + 0.15f, AlertSounds.CHEST_SHIMMER, 0.55f, 1.10f)
                .sound(THIRD, AlertSounds.MONEY_DING, 0.60f, 1.10f)
                .sound(THIRD + 0.06f, AlertSounds.JACKPOT_PLING, 0.55f, 1.05f)
                .sound(SUB, AlertSounds.CONFETTI_TADA, 0.65f, 1.05f)
                .sound(SUB, AlertSounds.GOD_BELL, 0.45f, 1.15f)
                .sound(PRESS, AlertSounds.COIN_CHIME, 0.65f, 1.05f)
                .sound(PRESS + 0.03f, AlertSounds.CHEST_SHIMMER, 0.45f, 1.25f)
                .sound(WIPE, AlertSounds.BLACKHOLE_COLLAPSE, 0.55f, 1.35f)
                .sound(END - 0.5f, AlertSounds.JACKPOT_PLING, 0.35f, 0.85f);
        for (int i = 0; i < CHIPS; i++) {
            this.cues.sound(CHAT + i * 0.42f, AlertSounds.COIN_PICKUP, 0.26f,
                    1.05f + (i % 4) * 0.10f);
        }
        ScreenShake.add(LIGHT, 0.22f, 0.16f);
    }

    @Override
    protected void advance(float t) {
        // A real viewer counter never lands on a round number.
        float target = 1847f + (this.rng.nextInt(300));
        this.viewers = target * Easing.outCubic(ramp(t, LIGHT + 0.10f, 1.7f));
    }

    /** Horizontal test bars across the top, used only while the feed is dark. */
    private void bars(GuiGraphics g, int w, float alpha) {
        float top = 0f;
        float height = this.h * 0.085f;
        int[] cols = {0xC0C0C0, 0xC0C000, 0x00C0C0, 0x00C000, 0xC000C0, 0xC00000, 0x0000C0};
        float span = w / 7f;
        for (int i = 0; i < 7; i++) {
            Draw2D.rect(g, this.barX[i] * w, top, this.barX[i] * w + span, top + height,
                    Draw2D.argb(alpha, cols[i]));
        }
        Draw2D.rect(g, 0f, top + height, w, top + height + 2f, Draw2D.argb(alpha * 0.6f, 0x101010));
    }

    private void standby(GuiGraphics g, Font font, float t, float fade, float unit) {
        Draw2D.rect(g, 0f, 0f, this.w, this.h, Draw2D.argb(fade, 0x08080A));
        bars(g, this.w, 0.10f * fade);
        // CRT scanlines, faint.
        for (float y = 0f; y < this.h; y += 4f) {
            Draw2D.rect(g, 0f, y, this.w, y + 1.2f, Draw2D.argb(0.07f * fade, 0x000000));
        }
        float blink = 0.35f + 0.65f * Math.abs(wave(t, 7.5f, 0f));
        float tx = this.w * 0.5f;
        float ty = this.h * 0.44f;
        Draw2D.circle(g, tx, ty, unit * 0.012f, Draw2D.argb(blink * fade, fill()),
                Draw2D.argb(blink * fade, accent()));
        Draw2D.ring(g, tx, ty, unit * 0.024f + unit * 0.004f * blink, Math.max(1f, unit * 0.0022f),
                blink * 0.45f * fade, fill());
        Component ready = TextFx.plain("STANDBY", dim(), true);
        TextFx.centered(g, font, ready, tx, ty + unit * 0.045f,
                TextFx.fitScale(font, ready, this.w * 0.4f, 1.4f), 0.55f * fade);
        Component small = TextFx.plain("waiting for uplink", 0x6A6E76, false);
        TextFx.centered(g, font, small, tx, ty + unit * 0.072f,
                TextFx.fitScale(font, small, this.w * 0.4f, 0.9f), 0.4f * fade);
    }

    /** Eight blade wedges that rotate and retract, revealing the frame behind them. */
    private void aperture(GuiGraphics g, float t, float fade, float unit) {
        float open = Easing.outCubic(ramp(t, LIGHT, APERTURE - LIGHT + 0.75f));
        if (open >= 0.999f) {
            return;
        }
        float cx = this.w * 0.5f;
        float cy = this.h * 0.40f;
        float inner = unit * 0.62f * (1f - open) * (1f - open);
        float outer = unit * 1.35f;
        float span = 6.28318f / BLADES;
        float gap = span * 0.012f;
        float spin = open * 0.55f;
        for (int i = 0; i < BLADES; i++) {
            float a0 = i * span + spin + gap;
            float a1 = (i + 1) * span + spin - gap;
            float c0 = (float) Math.cos(a0);
            float s0 = (float) Math.sin(a0);
            float c1 = (float) Math.cos(a1);
            float s1 = (float) Math.sin(a1);
            int dark = Draw2D.argb(fade, 0x05050A);
            int edge = Draw2D.argb(0.35f * fade, Draw2D.lerpRgb(panel(), fill(), 0.55f));
            Draw2D.quad(g,
                    cx + c0 * inner, cy + s0 * inner, dark,
                    cx + c1 * inner, cy + s1 * inner, dark,
                    cx + c1 * outer, cy + s1 * outer, edge,
                    cx + c0 * outer, cy + s0 * outer, edge);
            // Blade hinge line.
            Draw2D.softLine(g, cx + c0 * inner, cy + s0 * inner, cx + c0 * outer, cy + s0 * outer,
                    Math.max(1f, unit * 0.0016f), 0.5f * fade, Draw2D.lerpRgb(accent(), 0xFFFFFF, 0.3f));
        }
        // Iris glow leaking through the opening.
        Draw2D.glow(g, cx, cy, unit * (0.10f + 0.35f * open), 0.30f * open * fade, accent());
    }

    private void pill(GuiGraphics g, float x, float y, float wdt, float hgt, float alpha, int body) {
        if (wdt <= hgt || alpha <= 0.004f) {
            return;
        }
        float r = hgt * 0.5f;
        Draw2D.rect(g, x + r, y, x + wdt - r, y + hgt, Draw2D.argb(alpha, body));
        Draw2D.circle(g, x + r, y + r, r, Draw2D.argb(alpha, body), 0);
        Draw2D.circle(g, x + wdt - r, y + r, r, Draw2D.argb(alpha, body), 0);
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.85f);
        float unit = Math.min(w, h);

        if (t < LIGHT) {
            standby(g, font, t, fade, unit);
            this.fx.render(g, 1f);
            fadeParticles(fade);
            return;
        }

        // --- body ---
        Draw2D.gradientV(g, 0f, 0f, w, h, Draw2D.argb(fade, panel()),
                Draw2D.argb(fade, Draw2D.scaleRgb(panel(), 0.45f)));
        // Soft key light from above the centre.
        Draw2D.glow(g, w * 0.5f, h * 0.20f, w * 0.55f, 0.16f * fade, accent());

        // One frame of pure broadcast red on the cut.
        float cut = 1f - ramp(t, LIGHT, 0.12f);
        if (cut > 0f) {
            Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(cut * 0.92f * fade, fill()));
        }

        aperture(g, t, fade, unit);

        // --- tally pill, top left ---
        float pillIn = Easing.outBack(ramp(t, LIGHT, 0.5f));
        float px = w * 0.045f - (1f - pillIn) * w * 0.20f;
        float py = h * 0.075f;
        float ph = unit * 0.042f;
        float pw = ph * 3.15f;
        pill(g, px, py, pw, ph, 0.95f * fade, fill());
        Draw2D.circle(g, px + ph * 0.52f, py + ph * 0.5f, ph * 0.19f,
                Draw2D.argb(0.6f + 0.4f * Math.abs(wave(t, 3.2f, 0f)) * fade, 0xFFFFFF),
                Draw2D.argb(0f, 0xFFFFFF));
        Component live = TextFx.plain("LIVE", 0xFFFFFF, true);
        TextFx.centered(g, font, live, px + pw * 0.60f, py + ph * 0.5f - ph * 0.30f,
                TextFx.fitScale(font, live, pw * 0.55f, 1.35f), 0.95f * fade);

        // Viewer counter, riding to the right of the pill.
        if (t > LIGHT + 0.15f) {
            Component count = TextFx.plain(viewerText() + " watching", dim(), false);
            TextFx.centered(g, font, count, px + pw + w * 0.10f, py + ph * 0.5f - unit * 0.010f,
                    TextFx.fitScale(font, count, w * 0.26f, 1.05f),
                    ramp(t, LIGHT + 0.15f, 0.5f) * 0.9f * fade);
        }

        // --- identity block ---
        float identIn = Easing.outCubic(ramp(t, APERTURE + 0.10f, 0.85f));
        if (identIn > 0.01f) {
            Component name = TextFx.plain(this.payment.name(), text(), true);
            float nameScale = TextFx.fitScale(font, name, w * 0.70f, 4.6f) * identIn;
            TextFx.centered(g, font, name, w * 0.5f, h * 0.20f, nameScale, identIn * fade);
            Component handle = TextFx.plain(HANDLE, accent(), true);
            TextFx.centered(g, font, handle, w * 0.5f, h * 0.20f + 34f * nameScale,
                    TextFx.fitScale(font, handle, w * 0.5f, 1.6f) * identIn, identIn * fade);
            Component url = TextFx.plain(URL, dim(), false);
            TextFx.centered(g, font, url, w * 0.5f, h * 0.20f + 56f * nameScale,
                    TextFx.fitScale(font, url, w * 0.55f, 1.0f) * identIn, 0.8f * identIn * fade);
        }

        // --- lower third ---
        float thirdIn = Easing.outCubic(ramp(t, THIRD, 0.7f));
        float thirdOut = 1f - Easing.inCubic(ramp(t, WIPE - 0.35f, 0.4f));
        float third = thirdIn * thirdOut;
        if (third > 0.01f) {
            float slide = (1f - thirdIn) * -w * 0.55f;
            float barH = unit * 0.135f;
            float barY = h * 0.72f;
            float barX = w * 0.055f + slide;
            float barW = w * 0.58f;
            Draw2D.rect(g, barX, barY, barX + barW, barY + barH,
                    Draw2D.argb(0.86f * third * fade, 0x0A0A0C));
            Draw2D.rect(g, barX, barY, barX + Math.max(2f, unit * 0.008f), barY + barH,
                    Draw2D.argb(0.95f * third * fade, fill()));
            // Name.
            Component name = TextFx.plain(this.payment.name(), text(), true);
            float ns = TextFx.fitScale(font, name, barW * 0.78f, 2.3f);
            TextFx.centered(g, font, name, barX + barW * 0.46f, barY + barH * 0.16f, ns,
                    third * fade);
            // "is live now" / title.
            String sub = this.title.isEmpty() ? "is live now" : "is live: " + this.title;
            Component subText = TextFx.plain(sub, dim(), false);
            TextFx.centered(g, font, subText, barX + barW * 0.46f, barY + barH * 0.60f,
                    TextFx.fitScale(font, subText, barW * 0.82f, 1.15f), third * 0.92f * fade);
            // A thin red rule that grows along the bottom of the third.
            Draw2D.rect(g, barX, barY + barH - Math.max(1.5f, unit * 0.003f),
                    barX + barW * third, barY + barH, Draw2D.argb(third * fade, fill()));
        }

        // --- broadcast progress line ---
        if (t > THIRD) {
            float lineY = h * 0.962f;
            Draw2D.rect(g, 0f, lineY, w, lineY + Math.max(1.5f, unit * 0.0035f),
                    Draw2D.argb(0.35f * fade, Draw2D.scaleRgb(fill(), 0.7f)));
            float loop = ((t - THIRD) * 0.38f) % 1f;
            Draw2D.rect(g, 0f, lineY, w * loop, lineY + Math.max(1.5f, unit * 0.0035f),
                    Draw2D.argb(0.95f * fade, fill()));
            float headX = w * loop;
            Draw2D.glow(g, headX, lineY, unit * 0.02f, 0.5f * fade, accent());
            Draw2D.circle(g, headX, lineY + unit * 0.0018f, unit * 0.004f,
                    Draw2D.argb(fade, 0xFFFFFF), Draw2D.argb(fade, fill()));
        }

        // --- chat chips down the right edge ---
        if (t > CHAT - 0.2f) {
            float headerIn = ramp(t, CHAT - 0.2f, 0.4f) * fade;
            Component chatHead = TextFx.plain("live chat", dim(), false);
            TextFx.centered(g, font, chatHead, w * 0.855f, h * 0.185f,
                    TextFx.fitScale(font, chatHead, w * 0.14f, 0.95f), 0.55f * headerIn);
            for (int i = 0; i < CHIPS; i++) {
                float in = Easing.outCubic(ramp(t, CHAT + i * 0.42f, 0.45f));
                if (in <= 0.01f) {
                    continue;
                }
                float cw = this.chipWidth[i] * w;
                float ch = unit * 0.045f;
                float cx = w * 0.985f - cw * in;
                float cy = this.chipY[i] * h;
                Draw2D.rect(g, cx, cy, cx + cw, cy + ch, Draw2D.argb(0.72f * in * fade, 0x101014));
                Draw2D.rect(g, cx, cy, cx + Math.max(1.5f, unit * 0.0022f), cy + ch,
                        Draw2D.argb(0.85f * in * fade, Draw2D.hsv(this.chipHue[i], 0.55f, 1f)));
                // Avatar dot.
                Draw2D.circle(g, cx + ch * 0.62f, cy + ch * 0.5f, ch * 0.28f,
                        Draw2D.argb(0.95f * in * fade, Draw2D.hsv(this.chipHue[i], 0.45f, 0.95f)), 0);
                // Two text bars: name line and message line.
                float barStart = cx + ch * 1.15f;
                float barEnd = cx + ch * 1.15f + (cw - ch * 1.45f) * this.chipLine1[i];
                Draw2D.rect(g, barStart, cy + ch * 0.30f, barEnd, cy + ch * 0.42f,
                        Draw2D.argb(0.55f * in * fade, text()));
                float bar2 = barStart + (cw - ch * 1.45f) * this.chipLine2[i] * 0.75f;
                Draw2D.rect(g, barStart, cy + ch * 0.60f, bar2, cy + ch * 0.70f,
                        Draw2D.argb(0.30f * in * fade, text()));
            }
        }

        // --- subscribe button ---
        float subIn = Easing.outBack(ramp(t, SUB, 0.55f));
        float subOut = 1f - Easing.inCubic(ramp(t, WIPE - 0.30f, 0.35f));
        float subA = Math.max(0f, Math.min(1f, subIn)) * subOut;
        if (subA > 0.01f) {
            float bw = w * 0.26f;
            float bh = unit * 0.085f;
            float bx = w * 0.5f - bw * 0.5f;
            float by = h * 0.845f;
            // Press dip: 0.94 scale for a moment after PRESS.
            float press = ramp(t, PRESS, 0.10f) * (1f - ramp(t, PRESS + 0.16f, 0.22f));
            float scale = 1f - 0.07f * press;
            float cxb = bx + bw * 0.5f;
            float cyb = by + bh * 0.5f;
            float hw = bw * 0.5f * scale;
            float hh = bh * 0.5f * scale;
            // Press ripple.
            if (t > PRESS) {
                float rp = ramp(t, PRESS, 0.55f);
                if (rp < 1f) {
                    Draw2D.ring(g, cxb, cyb, hw * (1f + 0.55f * rp), Math.max(1f, unit * 0.004f),
                            (1f - rp) * 0.55f * fade, accent());
                }
            }
            float rx = hh;
            Draw2D.rect(g, cxb - hw + rx, cyb - hh, cxb + hw - rx, cyb + hh,
                    Draw2D.argb(0.95f * subA * fade, fill()));
            Draw2D.circle(g, cxb - hw + rx, cyb, rx, Draw2D.argb(0.95f * subA * fade, fill()), 0);
            Draw2D.circle(g, cxb + hw - rx, cyb, rx, Draw2D.argb(0.95f * subA * fade, fill()), 0);
            // Bell on the left, drawn from primitives.
            float bellX = cxb - hw * 0.62f;
            float bellY = cyb;
            float bellR = hh * 0.42f;
            Draw2D.circle(g, bellX, bellY - bellR * 0.1f, bellR,
                    Draw2D.argb(0.92f * subA * fade, 0xFFFFFF), Draw2D.argb(0f, 0xFFFFFF));
            Draw2D.rect(g, bellX - bellR * 1.15f, bellY + bellR * 0.32f,
                    bellX + bellR * 1.15f, bellY + bellR * 0.62f,
                    Draw2D.argb(0.92f * subA * fade, 0xFFFFFF));
            Draw2D.circle(g, bellX, bellY + bellR * 1.02f, bellR * 0.30f,
                    Draw2D.argb(0.92f * subA * fade, 0xFFFFFF), Draw2D.argb(0f, 0xFFFFFF));
            if (press > 0f) {
                star4(g, bellX, bellY, bellR * 2.4f * press, press * subA * fade, 0xFFFFFF);
            }
            Component subLabel = TextFx.plain("SUBSCRIBE", 0xFFFFFF, true);
            TextFx.centered(g, font, subLabel, cxb + hw * 0.18f, cyb - hh * 0.36f,
                    TextFx.fitScale(font, subLabel, hw * 1.55f, 1.45f) * scale, subA * fade);
        }

        // --- wipe to the end card ---
        if (t > WIPE) {
            float wipe = Easing.outCubic(ramp(t, WIPE, END - WIPE - 0.35f));
            float edge = w * wipe;
            Draw2D.rect(g, 0f, 0f, edge, h, Draw2D.argb(fade, 0x08080A));
            if (edge > 1f) {
                Draw2D.rect(g, edge - Math.max(2f, unit * 0.006f), 0f, edge, h,
                        Draw2D.argb(0.9f * fade, fill()));
                Draw2D.glow(g, edge, h * 0.5f, unit * 0.06f, 0.30f * fade, accent());
            }
            float card = ramp(t, WIPE + 0.15f, 0.5f) * fade;
            if (card > 0.01f) {
                float cx = w * 0.5f;
                Draw2D.circle(g, cx, h * 0.40f, unit * 0.010f, Draw2D.argb(card, fill()),
                        Draw2D.argb(card, accent()));
                Component on = TextFx.plain("LIVE NOW", text(), true);
                TextFx.centered(g, font, on, cx, h * 0.44f,
                        TextFx.fitScale(font, on, w * 0.4f, 1.6f), card);
                Component ch = TextFx.plain(this.payment.name() + " " + HANDLE, accent(), true);
                TextFx.centered(g, font, ch, cx, h * 0.50f,
                        TextFx.fitScale(font, ch, w * 0.55f, 1.3f), card);
                Component link = TextFx.plain(URL, dim(), false);
                TextFx.centered(g, font, link, cx, h * 0.55f,
                        TextFx.fitScale(font, link, w * 0.5f, 0.95f), card * 0.85f);
            }
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    private String viewerText() {
        int count = (int) this.viewers;
        if (count >= 1000) {
            return String.format(java.util.Locale.ROOT, "%.1fK", count / 1000f);
        }
        return Integer.toString(count);
    }
}
