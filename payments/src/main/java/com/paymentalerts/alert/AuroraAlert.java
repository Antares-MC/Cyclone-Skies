package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Aurora: no particles, a shader-like sky. Five curtains flow over an arctic horizon, each
 * driven by layered sines so the motion never visibly loops, with a bright lower hem.
 */
final class AuroraAlert extends StageBase {
    static final String[] VARIATIONS = {"Green", "Purple", "Full spectrum"};

    private static final float FORM = 1.5f;
    private static final float END = 8.6f;
    private static final int CURTAINS = 5;
    private static final int STARS = 120;

    private final float[] phase = new float[CURTAINS];
    private final float[] speed = new float[CURTAINS];
    private final float[] baseY = new float[CURTAINS];
    private final float[] amp = new float[CURTAINS];
    private final float[] thick = new float[CURTAINS];
    private final float[] starX = new float[STARS];
    private final float[] starY = new float[STARS];
    private final float[] starP = new float[STARS];
    private float snowTimer;

    AuroraAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < CURTAINS; i++) {
            this.phase[i] = rnd(0f, 6.283f);
            this.speed[i] = rnd(0.18f, 0.42f) * (chance(0.5f) ? 1f : -1f);
            this.baseY[i] = 0.06f + i * 0.045f;
            this.amp[i] = rnd(0.02f, 0.05f);
            this.thick[i] = rnd(0.14f, 0.22f);
        }
        for (int i = 0; i < STARS; i++) {
            this.starX[i] = rnd(0f, 1f);
            this.starY[i] = rnd(0f, 0.55f);
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
        this.cues.sound(0.3f, AlertSounds.BLACKHOLE_PORTAL, 0.7f, 0.8f)
                .sound(2.2f, AlertSounds.NOVA_SHIMMER, 0.5f, 1.0f)
                .sound(5.0f, AlertSounds.NOVA_SHIMMER, 0.4f, 0.8f)
                .sound(7.2f, AlertSounds.BLACKHOLE_SPARKLE, 0.3f, 1.2f);
    }

    @Override
    protected void advance(float t) {
        if (t < 1.2f || t > 7.4f) {
            return;
        }
        this.snowTimer += this.dt;
        while (this.snowTimer > 0.09f) {
            this.snowTimer -= 0.09f;
            Particles2D.P p = spawn(Particles2D.Kind.SPARK, rnd(0f, this.w), rnd(this.h * 0.35f, this.h),
                    rnd(-14f, 14f), rnd(8f, 30f), rnd(1.4f, 3.0f), rnd(0.8f, 1.8f),
                    0xDFF6FF, 0x6A90A8);
            p.drag = 0.6f;
            p.twinkle = true;
        }
    }

    private int curtainColor(int index, float u) {
        return switch (look()) {
            case 1 -> Draw2D.hsv(0.74f + 0.08f * index + u * 0.08f, 0.55f, 1f);
            case 2 -> Draw2D.hsv(0.30f + index * 0.13f + u * 0.25f, 0.6f, 1f);
            default -> Draw2D.hsv(0.36f + index * 0.015f + u * 0.05f, 0.65f, 1f);
        };
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.9f);
        float form = ramp(t, 0f, FORM);

        Draw2D.gradientV(g, 0f, 0f, w, h, Draw2D.argb(0.95f * form * fade, 0x040A18),
                Draw2D.argb(0.75f * form * fade, 0x0A1A2A));

        for (int i = 0; i < STARS; i++) {
            float a = (0.25f + 0.55f * (0.5f + 0.5f * (float) Math.sin(t * 0.8f + this.starP[i])))
                    * form * fade * 0.7f;
            Draw2D.circle(g, this.starX[i] * w, this.starY[i] * h, 1f,
                    Draw2D.argb(a, 0xFFFFFF), Draw2D.argb(0f, 0xFFFFFF));
        }

        // Horizon band.
        Draw2D.gradientV(g, 0f, h * 0.62f, w, h * 0.9f,
                Draw2D.argb(0f, 0x0A1626), Draw2D.argb(0.7f * form * fade, 0x0A1626));
        Draw2D.gradientV(g, 0f, h * 0.86f, w, h, Draw2D.argb(0.5f * form * fade, 0x081018),
                Draw2D.argb(0.85f * form * fade, 0x04080E));

        int steps = 42;
        for (int i = 0; i < CURTAINS; i++) {
            float yShift = h * this.baseY[i] + wave(t, 0.22f, this.phase[i]) * h * 0.02f;
            float prevX = -w * 0.1f;
            float prevTop = yShift;
            float prevBot = yShift + h * this.thick[i];
            for (int s = 1; s <= steps; s++) {
                float u = s / (float) steps;
                float x = -w * 0.1f + u * w * 1.2f;
                float fold = wave(x * 0.0035f + this.phase[i], 1.7f, t * this.speed[i]) * h * this.amp[i];
                float fold2 = wave(x * 0.011f - this.phase[i], 2.4f, t * this.speed[i] * 2.1f) * h * this.amp[i] * 0.35f;
                float top = yShift + fold + fold2;
                float height = h * this.thick[i] * (0.8f + 0.3f * wave(x * 0.004f, 1.1f, i * 2.2f));
                float bottom = top + height;
                float edge = 1f - Math.abs(u - 0.5f) * 1.4f;
                float a = 0.16f * form * fade * Math.max(0f, edge);
                int col = curtainColor(i, u);
                Draw2D.quad(g, prevX, prevTop, Draw2D.argb(0.015f * form * fade, col),
                        prevX, prevBot, Draw2D.argb(a * 0.7f, col),
                        x, bottom, Draw2D.argb(a * 0.7f, col),
                        x, top, Draw2D.argb(0.015f * form * fade, col));
                // Bright lower hem.
                Draw2D.softLine(g, prevX, prevBot, x, bottom, Math.max(2f, h * 0.004f),
                        a * 1.5f, Draw2D.lerpRgb(col, 0xFFFFFF, 0.35f));
                prevX = x;
                prevTop = top;
                prevBot = bottom;
            }
        }

        // Snow sparkle near the ground.
        float ground = h * 0.9f;
        for (int i = 0; i < 26; i++) {
            float x = (i / 26f + (t * 0.012f)) % 1f * w;
            float y = ground - Math.abs(wave(t, 0.6f, i * 1.7f)) * h * 0.04f;
            float a = 0.3f + 0.4f * Math.abs(wave(t, 2.2f, i));
            star4(g, x, y, Math.min(w, h) * 0.006f, a * form * fade, 0xDFF6FF);
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected String tagline() {
        return "Northern lights";
    }
}
