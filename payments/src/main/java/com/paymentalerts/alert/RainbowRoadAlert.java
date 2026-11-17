package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Rainbow road: one-point perspective. A chevron road recedes to a vanishing point that
 * drifts, the camera rises along it, hue rotates along the road's length, and the whole
 * track banks into a shallow arc as it climbs off the top of the frame.
 */
final class RainbowRoadAlert extends StageBase {
    static final String[] VARIATIONS = {"Neon", "Pastel", "Retro"};

    private static final float LAY = 0.9f;
    private static final float CLIMB = 2.4f;
    private static final float END = 6.8f;

    private static final int ROWS = 22;
    private static final int SEGMENTS = 12;

    private final float[] starX = new float[90];
    private final float[] starY = new float[90];
    private final float[] starP = new float[90];
    private float sparkTimer;

    RainbowRoadAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.starX.length; i++) {
            this.starX[i] = rnd(0f, 1f);
            this.starY[i] = rnd(0f, 0.62f);
            this.starP[i] = rnd(0f, 6.283f);
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
    protected void cue() {
        this.cues.sound(0.05f, AlertSounds.JACKPOT_TICK, 0.4f, 1.7f)
                .sound(LAY, AlertSounds.CONFETTI_HARP, 0.7f, 1.0f)
                .sound(1.0f, AlertSounds.CONFETTI_HARP, 0.6f, 1.15f)
                .sound(1.0f, AlertSounds.JACKPOT_CHIME, 0.7f, 1.0f)
                .sound(2.6f, AlertSounds.MONEY_LEVELUP, 0.8f, 1.0f)
                .sound(3.6f, AlertSounds.COIN_SPARKLE, 0.5f, 1.3f)
                .sound(4.6f, AlertSounds.CONFETTI_TADA, 0.7f, 1.05f)
                .sound(5.4f, AlertSounds.JACKPOT_PLING, 0.5f, 0.9f);
        for (int i = 0; i < 10; i++) {
            this.cues.sound(0.4f + i * 0.42f, AlertSounds.COIN_PICKUP, 0.34f,
                    0.95f + (i % 4) * 0.12f);
        }
    }

    @Override
    protected void advance(float t) {
        if (t < LAY || t > 5.6f) {
            return;
        }
        this.sparkTimer += this.dt;
        while (this.sparkTimer > 0.03f) {
            this.sparkTimer -= 0.03f;
            // Sparks rise off the road's edges.
            boolean left = chance(0.5f);
            float x = this.w * (left ? 0.5f - 0.30f : 0.5f + 0.30f) + rnd(-30f, 30f);
            float y = this.h * rnd(0.72f, 1.02f);
            Particles2D.P p = spawn(Particles2D.Kind.SPARK, x, y, rnd(-14f, 14f), rnd(-130f, -40f),
                    rnd(0.6f, 1.4f), rnd(1f, 2.8f),
                    Draw2D.hsv(rnd(0f, 1f), 0.45f, 1f), 0x201040);
            p.drag = 0.9f;
            p.gravity = -30f;
            p.twinkle = true;
        }
    }

    private int roadColor(float u, float t) {
        float speed = look() == 2 ? 0.16f : 0.10f;
        float hue = u * 1.1f - t * speed;
        return switch (look()) {
            case 1 -> Draw2D.hsv(hue, 0.30f, 1f);
            case 2 -> Draw2D.hsv(hue, 0.95f, 1f);
            default -> Draw2D.hsv(hue, 0.72f, 1f);
        };
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.9f);
        float lay = Easing.outCubic(ramp(t, 0f, LAY));
        float climb = Easing.inCubic(ramp(t, CLIMB, END - CLIMB - 0.4f));
        float bank = ramp(t, LAY, 2.2f);

        // Sky: deep gradient with a bright band at the vanishing point.
        Draw2D.gradientV(g, 0f, 0f, w, h, Draw2D.argb(fade, 0x06060F),
                Draw2D.argb(fade, 0x1A1040));

        // Vanishing point drifts on a slow figure-eight and rises on the climb.
        float vpx = w * (0.5f + 0.06f * wave(t, 0.42f, 0f) * bank);
        float vpy = h * (0.40f + 0.03f * wave(t, 0.31f, 1.7f) * bank) - climb * h * 0.24f;

        for (int i = 0; i < this.starX.length; i++) {
            float sx = this.starX[i] * w;
            float sy = this.starY[i] * h;
            float tw = 0.4f + 0.6f * Math.abs(wave(t * (1.1f + (i % 4) * 0.2f), 1f, this.starP[i]));
            Draw2D.circle(g, sx, sy, 0.9f + (i % 3) * 0.55f,
                    Draw2D.argb(0.45f * tw * fade, 0xD8E8FF), Draw2D.argb(0f, 0xD8E8FF));
        }

        Draw2D.glow(g, vpx, vpy, w * 0.26f, 0.16f * lay * fade, 0x8A6AFF);
        Draw2D.glow(g, vpx, vpy, w * 0.06f, 0.35f * lay * fade, 0xFFFFFF);

        // The road: bands that recede, widening toward the viewer.
        float horizonY = vpy;
        float bottomY = h * 1.02f;
        for (int row = 0; row < ROWS; row++) {
            float near = row / (float) ROWS;
            float far = (row + 1) / (float) ROWS;
            // Non-linear depth: dense near the horizon.
            float y0 = horizonY + (bottomY - horizonY) * (near * near);
            float y1 = horizonY + (bottomY - horizonY) * (far * far);
            if (y1 - y0 < 0.4f) {
                continue;
            }
            float hw0 = w * (0.012f + 0.62f * near * near) * lay;
            float hw1 = w * (0.012f + 0.62f * far * far) * lay;
            float scroll = (t * 0.9f + near * 2.4f) % 1f;
            int railCol = roadColor(near * 0.5f + scroll * 0.18f, t);
            for (int seg = 0; seg < SEGMENTS; seg++) {
                float s0 = seg / (float) SEGMENTS;
                float s1 = (seg + 1) / (float) SEGMENTS;
                float x00 = vpx + (s0 * 2f - 1f) * hw0;
                float x01 = vpx + (s1 * 2f - 1f) * hw0;
                float x10 = vpx + (s0 * 2f - 1f) * hw1;
                float x11 = vpx + (s1 * 2f - 1f) * hw1;
                int col = roadColor((s0 + near) * 0.5f + scroll * 0.18f, t);
                float a = (0.30f + 0.55f * near) * lay * fade;
                if ((seg + row) % 2 == 0) {
                    a *= 1.35f;
                }
                Draw2D.quad(g, x00, y0, Draw2D.argb(a, col),
                        x01, y0, Draw2D.argb(a, col),
                        x11, y1, Draw2D.argb(a * 1.1f, col),
                        x10, y1, Draw2D.argb(a * 1.1f, col));
            }
            // Chevron across the band.
            float cxw = hw0 * 0.86f;
            float cxw2 = hw1 * 0.86f;
            float chevY = y0 + (y1 - y0) * 0.42f;
            int chevCol = 0xFFFFFF;
            float ca = 0.55f * lay * fade * (0.3f + near);
            Draw2D.softLine(g, vpx - cxw, chevY, vpx, chevY - (y1 - y0) * 0.55f,
                    Math.max(1.5f, (y1 - y0) * 0.16f), ca, chevCol);
            Draw2D.softLine(g, vpx + cxw, chevY, vpx, chevY - (y1 - y0) * 0.55f,
                    Math.max(1.5f, (y1 - y0) * 0.16f), ca, chevCol);
            // Side rails.
            Draw2D.softLine(g, vpx - hw0, y0, vpx - hw1, y1, Math.max(1.2f, (y1 - y0) * 0.10f),
                    0.7f * lay * fade, Draw2D.lerpRgb(railCol, 0xFFFFFF, 0.6f));
            Draw2D.softLine(g, vpx + hw0, y0, vpx + hw1, y1, Math.max(1.2f, (y1 - y0) * 0.10f),
                    0.7f * lay * fade, Draw2D.lerpRgb(railCol, 0xFFFFFF, 0.6f));
            // Overlapping arc rails as the road banks.
            if (bank > 0.3f && row < ROWS - 2) {
                float lift = (float) Math.sin((row / (float) ROWS) * 3.1416f) * h * 0.03f * bank;
                Draw2D.softLine(g, vpx - hw0, y0 - lift, vpx - hw1, y1 - lift * 0.8f,
                        Math.max(1f, (y1 - y0) * 0.06f), 0.25f * bank * fade,
                        Draw2D.lerpRgb(railCol, 0xFFFFFF, 0.8f));
                Draw2D.softLine(g, vpx + hw0, y0 - lift, vpx + hw1, y1 - lift * 0.8f,
                        Math.max(1f, (y1 - y0) * 0.06f), 0.25f * bank * fade,
                        Draw2D.lerpRgb(railCol, 0xFFFFFF, 0.8f));
            }
            // Far rows also need a wall of light receding behind them.
            if (row < 4) {
                float v = 1f - row / 4f;
                Draw2D.rect(g, 0f, y0, w, y1, Draw2D.argb(0.06f * v * lay * fade, 0x8A6AFF));
            }
        }

        // Vanishing point gate: a small hexagonal aperture the road feeds into.
        if (lay > 0.05f) {
            float gr = w * 0.012f + w * 0.02f * lay;
            for (int i = 0; i < 6; i++) {
                float a0 = i * 6.283f / 6f + t * 0.3f;
                float a1 = (i + 1) * 6.283f / 6f + t * 0.3f;
                Draw2D.softLine(g, vpx + (float) Math.cos(a0) * gr, vpy + (float) Math.sin(a0) * gr,
                        vpx + (float) Math.cos(a1) * gr, vpy + (float) Math.sin(a1) * gr,
                        Math.max(1.2f, gr * 0.14f), 0.8f * lay * fade, 0xFFFFFF);
            }
            star4(g, vpx, vpy, gr * 1.6f, 0.55f * lay * fade, 0xFFFFFF);
        }

        // Climb: a bright wash pulls the whole frame off the top.
        if (climb > 0.01f) {
            Draw2D.gradientV(g, 0f, h - climb * h * 0.9f, w, h,
                    Draw2D.argb(0f, 0xFFFFFF), Draw2D.argb(0.35f * climb * fade, 0xE8D8FF));
            Draw2D.rect(g, 0f, 0f, w, h * climb * 0.4f, Draw2D.argb(0.4f * climb * fade, 0xFFFFFF));
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected String tagline() {
        return "Follow the road";
    }
}
