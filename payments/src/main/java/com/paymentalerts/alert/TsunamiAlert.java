package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;

/**
 * Tsunami: a wall of water with weight. The horizon tilts, a crest builds at the right,
 * rolls across, breaks over the camera with caustics and bubbles, then drains away and
 * leaves foam and debris.
 */
final class TsunamiAlert extends StageBase {
    static final String[] VARIATIONS = {"Ocean", "Storm", "Abyss"};

    private static final float BUILD = 1.6f;
    private static final float BREAK = 3.2f;
    private static final float END = 7.2f;

    private final float[] debrisX = new float[12];
    private final float[] debrisSeed = new float[12];
    private float bubbleTimer;

    TsunamiAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.debrisX.length; i++) {
            this.debrisX[i] = rnd(0f, 1f);
            this.debrisSeed[i] = rnd(0f, 6.283f);
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

    private int deep() {
        return switch (look()) {
            case 1 -> 0x0A2230;
            case 2 -> 0x02080E;
            default -> 0x06283E;
        };
    }

    private int crestColor() {
        return switch (look()) {
            case 1 -> 0x9FD8E8;
            case 2 -> 0x7FFFD0;
            default -> 0xBFEFFF;
        };
    }

    @Override
    protected void cue() {
        this.cues.sound(0.1f, AlertSounds.NUKE_RUMBLE, 0.6f, 0.8f)
                .sound(0.8f, AlertSounds.STORM_RAIN, 0.5f, 0.9f)
                .sound(BUILD, AlertSounds.NUKE_RUMBLE, 0.7f, 0.7f)
                .sound(BREAK, AlertSounds.STORM_BIG, 0.9f, 1.0f)
                .sound(BREAK + 0.1f, AlertSounds.NUKE_SHOCKWAVE, 0.6f, 1.0f)
                .sound(BREAK + 0.9f, AlertSounds.CONFETTI_POP, 0.5f, 0.8f)
                .sound(5.2f, AlertSounds.STORM_RAIN, 0.4f, 1.0f);
        ScreenShake.add(BREAK, 0.6f, 0.7f);
        ScreenShake.addRising(0.4f, 1.2f, 0.12f);
    }

    @Override
    protected void advance(float t) {
        if (t > BREAK - 0.3f && t < BREAK + 1.6f) {
            this.bubbleTimer += this.dt;
            while (this.bubbleTimer > 0.025f) {
                this.bubbleTimer -= 0.025f;
                Particles2D.P p = spawn(Particles2D.Kind.SPARK, rnd(0f, this.w), rnd(this.h * 0.5f, this.h),
                        rnd(-20f, 20f), -rnd(60f, 240f), rnd(0.6f, 1.6f), rnd(1f, 3.6f),
                        0xDFF6FF, 0x3A7A9A);
                p.gravity = -40f;
                p.drag = 0.6f;
            }
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.9f);
        float tilt = wave(t, 0.8f, 0f) * 0.006f * ramp(t, 0f, 0.6f);
        Matrix3x2fStack pose = g.pose();
        pose.pushMatrix();
        pose.translate(w * 0.5f, h * 0.5f);
        pose.rotate(tilt);
        pose.translate(-w * 0.5f, -h * 0.5f);

        int deep = deep();
        int crest = crestColor();
        Draw2D.gradientV(g, 0f, 0f, w, h, Draw2D.argb(0.9f * fade, 0x0A1622),
                Draw2D.argb(0.9f * fade, deep));
        Draw2D.glow(g, w * 0.7f, h * 0.35f, w * 0.5f, 0.12f * fade, crest);

        float horizon = h * 0.62f;
        Draw2D.softLine(g, 0f, horizon, w, horizon, Math.max(1f, h * 0.002f), 0.25f * fade, 0x8AB0C0);

        // Wave shape.
        float build = Easing.outCubic(ramp(t, 0f, BUILD));
        float roll = ramp(t, BUILD - 0.2f, BREAK - BUILD + 0.4f);
        float drain = ramp(t, BREAK + 1.2f, 1.8f);
        float crestX = w * (1.15f - 1.5f * Easing.inCubic(roll)) + w * 0.15f * drain;
        crestX = Math.max(-w * 0.4f, crestX);
        float amp = h * (0.10f + 0.42f * build) * (1f - drain * 0.75f);
        float width = w * (0.28f + 0.3f * build);
        int steps = 64;
        float prevX = -w * 0.1f;
        float prevTop = horizon;
        for (int i = 1; i <= steps; i++) {
            float x = -w * 0.1f + i * (w * 1.2f / steps);
            float d = (x - crestX) / width;
            float prof = (float) Math.exp(-d * d * 1.4f);
            float body = wave(x * 0.004f, 2.2f, t * 1.6f) * h * 0.012f;
            float top = horizon - amp * prof + body;
            // body fill
            Draw2D.quad(g, prevX, prevTop, Draw2D.argb(0.85f * fade, Draw2D.lerpRgb(deep, crest, 0.35f)),
                    prevX, h, Draw2D.argb(0.95f * fade, deep),
                    x, h, Draw2D.argb(0.95f * fade, deep),
                    x, top, Draw2D.argb(0.85f * fade, Draw2D.lerpRgb(deep, crest, 0.35f)));
            // lighter lip
            Draw2D.softLine(g, prevX, prevTop, x, top, Math.max(2f, h * 0.004f),
                    0.55f * fade * build, crest);
            prevX = x;
            prevTop = top;
        }
        // Foam along the lip.
        if (build > 0.05f) {
            for (int i = 0; i < 26; i++) {
                float x = crestX + wave(t, 2.4f, i * 1.7f) * width * 0.66f;
                float d = (x - crestX) / width;
                float top = horizon - amp * (float) Math.exp(-d * d * 1.4f);
                float size = 1.5f + 3f * Math.abs(wave(t, 3.1f, i * 0.9f));
                Draw2D.circle(g, x, top - 2f, size * build,
                        Draw2D.argb(0.5f * fade * build, 0xFFFFFF), Draw2D.argb(0f, 0xFFFFFF));
            }
        }

        pose.popMatrix();

        // Underwater beat.
        float under = ramp(t, BREAK, 0.25f) * (1f - ramp(t, BREAK + 1.1f, 0.8f));
        if (under > 0.01f) {
            Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.55f * under * fade, deep));
            for (int i = 0; i < 9; i++) {
                float y = h * (0.2f + i * 0.09f);
                float px = 0f;
                float py = y;
                for (int s = 1; s <= 30; s++) {
                    float x = s / 30f * w;
                    float yy = y + (float) Math.sin(s * 0.5f + t * 3f + i) * h * 0.012f;
                    Draw2D.softLine(g, px, py, x, yy, Math.max(1f, h * 0.0018f),
                            0.10f * under * fade, 0xBFEFFF);
                    px = x;
                    py = yy;
                }
            }
        }

        // Debris and foam after the break.
        if (t > BREAK) {
            float settle = 1f - ramp(t, BREAK + 2.0f, 1.6f);
            for (int i = 0; i < this.debrisX.length; i++) {
                float x = this.debrisX[i] * w + wave(t, 0.5f, this.debrisSeed[i]) * w * 0.03f;
                float y = h * 0.88f + wave(t, 1.2f, i) * h * 0.02f;
                Draw2D.rotatedRect(g, x, y, w * 0.02f, w * 0.006f,
                        wave(t, 1.4f, i), Draw2D.argb(0.5f * settle * fade, 0x6A4A30));
            }
            for (int i = 0; i < 16; i++) {
                float x = (i / 16f + t * 0.01f) % 1f * w;
                Draw2D.circle(g, x, h * 0.93f, 2f + (i % 3),
                        Draw2D.argb(0.25f * settle * fade, 0xFFFFFF), Draw2D.argb(0f, 0xFFFFFF));
            }
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected String tagline() {
        return "Wave incoming";
    }
}
