package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Royal fanfare: theatre, not explosion. Two velvet curtains draw apart on a brass rail,
 * a crest is painted stroke by stroke, gold leaf falls slowly, and two banner columns
 * unfurl beside the name.
 */
final class RoyalFanfareAlert extends StageBase {
    static final String[] VARIATIONS = {"Crimson", "Imperial", "Silver"};

    private static final float PART = 0.9f;
    private static final float CREST = 1.5f;
    private static final float LEAF = 3.0f;
    private static final float END = 8.0f;

    private static final int[][] PALETTES = {
            {0x8A1224, 0xC8A24A, 0xF2E4C0, 0x2A0810},
            {0x3A1460, 0xD8B860, 0xFFF0D0, 0x120620},
            {0x2A2E3A, 0xC8D0DC, 0xFFFFFF, 0x0C0E14},
    };

    private final float[] leafX = new float[70];
    private final float[] leafY = new float[70];
    private final float[] leafSpin = new float[70];
    private final float[] leafSpeed = new float[70];
    private final float[] leafSize = new float[70];
    private final float[] foldPhase = new float[9];

    private float railFlash = 99f;

    RoyalFanfareAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.leafX.length; i++) {
            this.leafX[i] = rnd(0f, 1f);
            this.leafY[i] = rnd(-0.4f, 0.3f);
            this.leafSpin[i] = rnd(0f, 6.283f);
            this.leafSpeed[i] = rnd(0.02f, 0.07f);
            this.leafSize[i] = rnd(1.6f, 4.2f);
        }
        for (int i = 0; i < this.foldPhase.length; i++) {
            this.foldPhase[i] = rnd(0f, 6.283f);
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

    private int[] palette() {
        return PALETTES[look()];
    }

    @Override
    protected void cue() {
        this.cues.sound(0.05f, AlertSounds.CHEST_RATTLE, 0.4f, 1.2f)
                .sound(PART, AlertSounds.GOD_ACTIVATE, 0.8f, 1.0f)
                .sound(PART + 0.05f, AlertSounds.GOD_FANFARE, 1.0f, 1.0f)
                .sound(PART + 0.9f, AlertSounds.GOD_BELL, 0.8f, 1.0f)
                .sound(CREST + 0.4f, AlertSounds.CHEST_SHIMMER, 0.6f, 1.1f)
                .sound(CREST + 1.1f, AlertSounds.GOD_FANFARE, 0.7f, 1.12f)
                .sound(LEAF, AlertSounds.GOD_CONDUIT, 0.6f, 1.0f)
                .sound(4.4f, AlertSounds.GOD_BELL, 0.6f, 0.9f)
                .sound(6.0f, AlertSounds.GOD_FANFARE, 0.4f, 0.85f);
        ScreenShake.add(PART, 0.3f, 0.2f);
    }

    @Override
    protected void advance(float t) {
        this.railFlash += this.dt;
        if (t < PART - 0.1f || t > END - 1.4f) {
            return;
        }
        // Gold leaf drifts down, never fast.
        for (int i = 0; i < this.leafX.length; i++) {
            this.leafY[i] += this.leafSpeed[i] * this.dt;
            this.leafX[i] += wave(t * 0.5f, 1.4f, this.leafSpin[i]) * 0.006f * this.dt * 12f;
            this.leafSpin[i] += this.dt * 1.6f;
            if (this.leafY[i] > 1.15f) {
                this.leafY[i] = -0.15f;
                this.leafX[i] = rnd(0f, 1f);
            }
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 1.0f);
        float cx = w * 0.5f;
        float unit = Math.min(w, h);
        int[] pal = palette();
        int velvet = pal[0];
        int gold = pal[1];
        int cream = pal[2];

        // Stage: deep floor, warm top light.
        Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(fade, pal[3]));
        Draw2D.gradientV(g, 0f, 0f, w, h * 0.6f, Draw2D.argb(0.30f * fade, 0x2A1E10),
                Draw2D.argb(0f, 0x2A1E10));

        // Curtains: two panels that slide out on the opening, with vertical folds.
        float part = Easing.outCubic(ramp(t, 0f, PART));
        float closeIn = Easing.inCubic(ramp(t, END - 1.5f, 1.4f));
        float edge = w * 0.5f * part * (1f - closeIn) + w * 0.02f;
        drawCurtain(g, w, h, t, fade, velvet, gold, -1f, edge, unit);
        drawCurtain(g, w, h, t, fade, velvet, gold, 1f, edge, unit);

        // Brass rail along the top, with finials.
        float railY = h * 0.055f;
        Draw2D.gradientV(g, 0f, railY - h * 0.008f, w, railY + h * 0.008f,
                Draw2D.argb(fade, gold), Draw2D.argb(fade, Draw2D.scaleRgb(gold, 0.55f)));
        Draw2D.softLine(g, 0f, railY, w, railY, Math.max(1.5f, h * 0.003f), 0.7f * fade, cream);
        for (int i = 0; i <= 2; i++) {
            float x = w * (i / 2f);
            Draw2D.circle(g, x, railY, h * 0.014f, Draw2D.argb(fade, gold),
                    Draw2D.argb(fade, Draw2D.scaleRgb(gold, 0.5f)));
            star4(g, x, railY, h * 0.024f, 0.6f * fade, cream);
        }
        float flash = 1f - ramp(this.railFlash, 0f, 0.4f);
        if (this.railFlash < 0.4f) {
            Draw2D.gradientV(g, 0f, railY - h * 0.05f, w, railY + h * 0.05f,
                    Draw2D.argb(0f, 0xFFFFFF), Draw2D.argb(0.22f * flash * fade, 0xFFFFFF));
        }

        // Crest behind the name: shield, chevrons, laurel, crown.
        float crestIn = ramp(t, PART + 0.25f, CREST);
        if (crestIn > 0.005f) {
            crest(g, cx, h * 0.24f, unit * 0.085f, crestIn, t, fade, gold, cream);
        }

        // Name and amount sit inside the crest, revealed stroke by stroke.
        float reveal = ramp(t, CREST - 0.5f, 0.9f);
        if (reveal > 0.01f) {
            float pop = Easing.outBack(reveal);
            Draw2D.rect(g, 0f, h * 0.24f + unit * 0.026f, w, h * 0.24f + unit * 0.026f + 2f,
                    Draw2D.argb(0.25f * reveal * fade, gold));
            this.name(g, font, cx, h * 0.24f - unit * 0.026f, w * 0.62f, 2.9f, t, reveal * fade);
            Component amountText = TextFx.plain(this.payment.displayAmount(), cream, true);
            float as = TextFx.fitScale(font, amountText, w * 0.5f, 1.9f) * pop;
            TextFx.centered(g, font, amountText, cx, h * 0.24f + unit * 0.040f, as,
                    reveal * 0.95f * fade);
            Component line = TextFx.plain("by royal decree", gold, false);
            TextFx.centered(g, font, line, cx, h * 0.24f + unit * 0.068f,
                    TextFx.fitScale(font, line, w * 0.4f, 1.1f) * pop, reveal * 0.7f * fade);
        }

        // Banner columns unfurl on either side.
        float unfurl = Easing.outCubic(ramp(t, LEAF, 1.4f));
        if (unfurl > 0.01f) {
            banner(g, w * 0.16f, h * 0.30f, unit * 0.055f, unit * 0.30f * unfurl, t, fade, gold, velvet, cream, 0);
            banner(g, w * 0.84f, h * 0.30f, unit * 0.055f, unit * 0.30f * unfurl, t, fade, gold, velvet, cream, 1);
        }

        // Gold leaf.
        for (int i = 0; i < this.leafX.length; i++) {
            float x = this.leafX[i] * w;
            float y = this.leafY[i] * h;
            float a = 0.55f * fade * ramp(t, PART + 0.4f, 0.8f);
            if (a <= 0.01f || y < 0f || y > h) {
                continue;
            }
            float s = this.leafSize[i] * (unit / 1080f + 0.4f);
            Draw2D.rotatedRect(g, x, y, s, s * 0.42f, this.leafSpin[i],
                    Draw2D.argb(a, gold));
            if (wave(t * 8f, 3f, this.leafSpin[i]) > 0.7f) {
                star4(g, x, y, s * 1.4f, a * 0.8f, cream);
            }
        }

        // Curtain close vignette.
        if (closeIn > 0.01f) {
            Draw2D.gradientH(g, 0f, 0f, w * 0.5f * closeIn, h,
                    Draw2D.argb(0.85f * closeIn * fade, pal[3]), Draw2D.argb(0f, pal[3]));
            Draw2D.gradientH(g, w - w * 0.5f * closeIn, 0f, w, h,
                    Draw2D.argb(0f, pal[3]), Draw2D.argb(0.85f * closeIn * fade, pal[3]));
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    private void drawCurtain(GuiGraphics g, int w, int h, float t, float fade, int velvet, int gold,
                             float side, float edge, float unit) {
        // side -1 = left panel, +1 = right panel. `edge` is the inner edge distance from centre.
        float inner = w * 0.5f + side * -edge;
        float outer = side < 0 ? 0f : w;
        float x0 = Math.min(inner, outer);
        float x1 = Math.max(inner, outer);
        if (x1 - x0 < 1f) {
            return;
        }
        Draw2D.rect(g, x0, 0f, x1, h, Draw2D.argb(fade, velvet));
        int folds = 9;
        float span = x1 - x0;
        for (int i = 0; i < folds; i++) {
            float u = (i + 0.5f) / folds;
            float fx = x0 + span * u + wave(t * 0.4f, 1.1f, this.foldPhase[i]) * span * 0.006f;
            float fw = span / folds * 0.42f;
            float bright = 0.25f + 0.45f * Math.abs(wave(t * 0.6f, 1.7f, this.foldPhase[i]));
            Draw2D.gradientH(g, fx - fw, 0f, fx + fw, h,
                    Draw2D.argb(0.22f * fade, Draw2D.lerpRgb(velvet, 0x000000, 0.6f)),
                    Draw2D.argb(bright * 0.22f * fade, Draw2D.lerpRgb(velvet, 0xFFFFFF, 0.35f)));
            Draw2D.softLine(g, fx, 0f, fx, h, Math.max(1f, span * 0.002f),
                    0.20f * fade, Draw2D.lerpRgb(velvet, 0x000000, 0.7f));
        }
        // Gold trim on the inner edge.
        Draw2D.rect(g, inner - side * span * 0.012f - 4f, 0f,
                inner - side * span * 0.012f + 4f, h, Draw2D.argb(0.85f * fade, gold));
        Draw2D.softLine(g, inner, 0f, inner, h, Math.max(1.5f, unit * 0.0018f),
                0.6f * fade, Draw2D.lerpRgb(gold, 0xFFFFFF, 0.5f));
        // Tassels.
        for (int i = 0; i < 5; i++) {
            float ty = h * (0.2f + i * 0.15f);
            Draw2D.circle(g, inner - side * span * 0.012f, ty, unit * 0.006f,
                    Draw2D.argb(0.9f * fade, gold), Draw2D.argb(0.5f * fade, Draw2D.scaleRgb(gold, 0.6f)));
        }
    }

    private void crest(GuiGraphics g, float cx, float cy, float r, float p, float t, float fade,
                       int gold, int cream) {
        float draw = Easing.outCubic(p);
        int c = Draw2D.argb(draw * fade, gold);
        int hi = Draw2D.argb(draw * fade, cream);
        // Shield body: a point-down hexagon drawn as two quads.
        Draw2D.quad(g, cx - r, cy - r * 0.85f, Draw2D.argb(0.18f * draw * fade, gold),
                cx + r, cy - r * 0.85f, Draw2D.argb(0.18f * draw * fade, gold),
                cx + r * 0.7f, cy + r * 0.55f, Draw2D.argb(0.10f * draw * fade, gold),
                cx, cy + r * 1.15f, Draw2D.argb(0.10f * draw * fade, gold));
        Draw2D.quad(g, cx - r, cy - r * 0.85f, Draw2D.argb(0.18f * draw * fade, gold),
                cx, cy + r * 1.15f, Draw2D.argb(0.10f * draw * fade, gold),
                cx - r * 0.7f, cy + r * 0.55f, Draw2D.argb(0.10f * draw * fade, gold),
                cx - r, cy - r * 0.85f, Draw2D.argb(0.18f * draw * fade, gold));
        // Outline, drawn progressively.
        float seg = draw * 6.283f;
        Draw2D.ringArc(g, cx, cy - r * 0.02f, r * 1.02f, r * 0.95f, Math.max(1.5f, r * 0.06f),
                draw * fade * 0.8f, gold, -1.9f, -1.9f + seg);
        // Chevron strokes.
        Draw2D.softLine(g, cx - r * 0.55f, cy + r * 0.05f, cx, cy - r * 0.35f,
                Math.max(1.5f, r * 0.09f), Math.min(1f, draw * 1.6f) * fade, hi);
        Draw2D.softLine(g, cx + r * 0.55f, cy + r * 0.05f, cx, cy - r * 0.35f,
                Math.max(1.5f, r * 0.09f), Math.min(1f, draw * 1.6f) * fade, hi);
        Draw2D.softLine(g, cx - r * 0.4f, cy + r * 0.42f, cx, cy + r * 0.05f,
                Math.max(1.2f, r * 0.06f), Math.min(1f, draw * 1.4f) * fade * 0.8f, c);
        Draw2D.softLine(g, cx + r * 0.4f, cy + r * 0.42f, cx, cy + r * 0.05f,
                Math.max(1.2f, r * 0.06f), Math.min(1f, draw * 1.4f) * fade * 0.8f, c);
        // Crown above.
        float crown = Math.max(0f, Math.min(1f, (p - 0.55f) / 0.45f));
        if (crown > 0f) {
            float cw = r * 0.72f;
            float by = cy - r * 1.02f;
            Draw2D.quad(g, cx - cw, by, Draw2D.argb(0.7f * crown * fade, gold),
                    cx - cw, by - r * 0.30f, Draw2D.argb(0.9f * crown * fade, gold),
                    cx + cw, by - r * 0.30f, Draw2D.argb(0.9f * crown * fade, gold),
                    cx + cw, by, Draw2D.argb(0.7f * crown * fade, gold));
            for (int i = -1; i <= 1; i++) {
                float px = cx + i * cw * 0.8f;
                Draw2D.triangle(g, px - cw * 0.16f, by - r * 0.28f,
                        Draw2D.argb(0.85f * crown * fade, gold),
                        px, by - r * 0.62f, Draw2D.argb(0.95f * crown * fade, cream),
                        px + cw * 0.16f, by - r * 0.28f, Draw2D.argb(0.85f * crown * fade, gold));
                star4(g, px, by - r * 0.62f, r * 0.08f, crown * fade, cream);
            }
            Draw2D.glow(g, cx, by - r * 0.2f, r * 0.9f, 0.18f * crown * fade, gold);
        }
        // Laurel: mirrored leaf pairs sweeping up the sides.
        int leaves = 6;
        for (int side = -1; side <= 1; side += 2) {
            for (int i = 0; i < leaves; i++) {
                float u = i / (float) (leaves - 1);
                float grow = Math.max(0f, Math.min(1f, p * 1.5f - u * 0.5f));
                if (grow <= 0f) {
                    continue;
                }
                float ang = -2.2f + u * 1.5f;
                float lx = cx + side * (r * 1.18f + u * r * 0.12f);
                float ly = cy + r * 0.5f - u * r * 1.5f;
                float lr = r * 0.13f * grow;
                Draw2D.ellipse(g, lx + (float) Math.cos(ang) * lr, ly + (float) Math.sin(ang) * lr,
                        lr, lr * 0.5f, Draw2D.argb(0.55f * grow * fade, gold),
                        Draw2D.argb(0f, gold));
                Draw2D.softLine(g, lx, ly, lx + (float) Math.cos(ang) * lr * 2f,
                        ly + (float) Math.sin(ang) * lr * 2f, Math.max(1f, lr * 0.3f),
                        0.5f * grow * fade, hi);
            }
        }
    }

    private void banner(GuiGraphics g, float x, float y, float halfW, float height, float t,
                        float fade, int gold, int velvet, int cream, int seed) {
        float sway = wave(t * 0.8f, 1.2f, seed * 2.1f) * halfW * 0.06f;
        // Pole.
        Draw2D.softLine(g, x, y - height * 0.2f, x, y + height * 0.1f, Math.max(2f, halfW * 0.08f),
                0.8f * fade, gold);
        star4(g, x, y - height * 0.2f, halfW * 0.22f, 0.7f * fade, cream);
        // Cloth, with a notched bottom.
        float top = y;
        float bot = y + height;
        Draw2D.quad(g, x - halfW + sway, top, Draw2D.argb(0.85f * fade, velvet),
                x + halfW + sway, top, Draw2D.argb(0.85f * fade, velvet),
                x + halfW * 0.7f + sway, bot, Draw2D.argb(0.85f * fade, velvet),
                x - halfW * 0.7f + sway, bot, Draw2D.argb(0.85f * fade, velvet));
        Draw2D.triangle(g, x - halfW * 0.7f + sway, bot, Draw2D.argb(0.85f * fade, velvet),
                x + sway, bot - halfW * 0.5f, Draw2D.argb(0.85f * fade, velvet),
                x + halfW * 0.7f + sway, bot, Draw2D.argb(0.85f * fade, velvet));
        // Trim and emblem.
        Draw2D.rect(g, x - halfW + sway, top, x + halfW + sway, top + Math.max(1.5f, halfW * 0.10f),
                Draw2D.argb(0.9f * fade, gold));
        Draw2D.rect(g, x - halfW + sway, top, x - halfW + sway + Math.max(1f, halfW * 0.05f), bot,
                Draw2D.argb(0.7f * fade, gold));
        Draw2D.rect(g, x + halfW + sway - Math.max(1f, halfW * 0.05f), top, x + halfW + sway, bot,
                Draw2D.argb(0.7f * fade, gold));
        star4(g, x + sway, top + height * 0.35f, halfW * 0.34f, 0.7f * fade, cream);
        Draw2D.circle(g, x + sway, top + height * 0.35f, halfW * 0.5f,
                Draw2D.argb(0.10f * fade, gold), Draw2D.argb(0.10f * fade, gold));
    }

    @Override
    protected boolean drawsBanner() {
        return false;
    }
}
