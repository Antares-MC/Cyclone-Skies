package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Supernova, small budget: an inward collapse you can actually watch. A shell of light
 * draws itself inward over two seconds while the core wobbles and dims, the implosion
 * holds for exactly two frames, then the ring and a short-lived nebula expand and cool.
 */
final class SupernovaLiteAlert extends StageBase {
    static final String[] VARIATIONS = {"Blue giant", "Red dwarf", "Neutron"};

    private static final float DRAW = 2.0f;
    private static final float IMPLODE = 3.0f;
    private static final float END = 6.5f;

    private static final int SHELLS = 90;
    private static final int NEBULA = 60;

    private final float[] shellAngle = new float[SHELLS];
    private final float[] shellRadius = new float[SHELLS];
    private final float[] shellDelay = new float[SHELLS];
    private final float[] shellSize = new float[SHELLS];
    private final float[] nebAngle = new float[NEBULA];
    private final float[] nebDist = new float[NEBULA];
    private final float[] nebSize = new float[NEBULA];
    private final float[] nebHue = new float[NEBULA];

    private float wobble;

    SupernovaLiteAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < SHELLS; i++) {
            this.shellAngle[i] = rnd(0f, 6.283f);
            this.shellRadius[i] = rnd(0.55f, 1.15f);
            this.shellDelay[i] = rnd(0f, 0.55f);
            this.shellSize[i] = rnd(1f, 3.2f);
        }
        for (int i = 0; i < NEBULA; i++) {
            this.nebAngle[i] = rnd(0f, 6.283f);
            this.nebDist[i] = rnd(0.05f, 1f);
            this.nebSize[i] = rnd(0.06f, 0.22f);
            this.nebHue[i] = rnd(0f, 1f);
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

    private int hot() {
        return switch (look()) {
            case 1 -> 0xFF6A3A;
            case 2 -> 0xE8F0FF;
            default -> 0x7AC8FF;
        };
    }

    private int cold() {
        return switch (look()) {
            case 1 -> 0x8A2A10;
            case 2 -> 0x6A80C0;
            default -> 0x2A3A8A;
        };
    }

    @Override
    protected void cue() {
        this.cues.action(0.0f, () -> AlertSounds.playSliding(AlertSounds.NOVA_HUM,
                        3.0f, 0.7f, 1.7f, 0.2f, 0.85f))
                .sound(0.3f, AlertSounds.NOVA_CHARGE, 0.6f, 1.1f)
                .sound(1.4f, AlertSounds.NOVA_HEARTBEAT, 0.7f, 1.0f)
                .sound(2.2f, AlertSounds.NOVA_HEARTBEAT, 0.8f, 1.15f)
                .sound(2.6f, AlertSounds.NOVA_HEARTBEAT, 0.9f, 1.3f)
                .sound(IMPLODE - 0.1f, AlertSounds.NOVA_COLLAPSE, 1.0f, 1.0f)
                .sound(IMPLODE, AlertSounds.NOVA_BLAST, 1.0f, 1.0f)
                .sound(IMPLODE + 0.05f, AlertSounds.NUKE_SHOCKWAVE, 0.8f, 1.1f)
                .sound(IMPLODE + 0.8f, AlertSounds.NOVA_ROAR, 0.6f, 1.0f)
                .sound(4.4f, AlertSounds.NOVA_SHIMMER, 0.5f, 0.9f)
                .sound(5.4f, AlertSounds.BLACKHOLE_SPARKLE, 0.4f, 0.8f);
        ScreenShake.add(IMPLODE, 0.6f, 0.7f);
        ScreenShake.addRising(0.6f, IMPLODE - 0.4f, 0.05f);
    }

    @Override
    protected void advance(float t) {
        this.wobble += this.dt;
        if (t > IMPLODE && t < 4.6f) {
            if (this.rng.nextFloat() < 0.5f) {
                float ang = rnd(0f, 6.283f);
                Particles2D.P p = spawn(Particles2D.Kind.SPARK, this.w * 0.5f, this.h * 0.44f,
                        (float) Math.cos(ang) * rnd(120f, 520f),
                        (float) Math.sin(ang) * rnd(120f, 520f),
                        rnd(0.5f, 1.4f), rnd(1f, 2.6f), hot(), cold());
                p.drag = 0.6f;
                p.gravity = 0f;
            }
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.85f);
        float cx = w * 0.5f;
        float cy = h * 0.44f;
        float unit = Math.min(w, h);
        int hot = hot();
        int cold = cold();
        boolean imploded = t >= IMPLODE;

        Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(fade, 0x03040A));
        Draw2D.gradientV(g, 0f, 0f, w, h, Draw2D.argb(0.25f * fade, 0x0A1220),
                Draw2D.argb(0.4f * fade, 0x04060C));

        // Cooling nebula: appears only after the blast, expanding and dimming.
        if (imploded) {
            float age = t - IMPLODE;
            float expand = Easing.outCubic(Math.min(1f, age / 3.0f));
            float dim = Math.max(0f, 1f - age / 3.0f);
            for (int i = 0; i < NEBULA; i++) {
                float ang = this.nebAngle[i] + age * 0.12f;
                float d = this.nebDist[i] * unit * (0.10f + expand * 0.45f);
                float x = cx + (float) Math.cos(ang) * d;
                float y = cy + (float) Math.sin(ang) * d * 0.82f;
                float r = this.nebSize[i] * unit * (0.5f + expand * 0.8f);
                int col = Draw2D.lerpRgb(hot, cold, Math.min(1f, this.nebHue[i] + age * 0.25f));
                Draw2D.ellipse(g, x, y, r, r * 0.7f,
                        Draw2D.argb(0.10f * dim * fade, col), Draw2D.argb(0f, col));
            }
        }

        if (!imploded) {
            // Pre-implosion: a drawn-in shell of light plus a core that contracts.
            float draw = ramp(t, 0f, DRAW);
            float corePulse = 0.5f + 0.5f * (float) Math.sin(t * (5f + draw * 9f));
            float coreR = unit * (0.055f + 0.02f * corePulse) * (1f - 0.45f * draw * draw);
            Draw2D.glow(g, cx, cy, unit * (0.30f - 0.14f * draw), 0.16f * draw * fade, cold);
            Draw2D.glow(g, cx, cy, coreR * 3.2f, 0.4f * fade, hot);
            Draw2D.circle(g, cx, cy, coreR, Draw2D.argb(fade, 0xFFFFFF), Draw2D.argb(fade, hot));

            // Wobble ring, drawn slightly off-round as the core destabilises.
            for (int ring = 0; ring < 3; ring++) {
                float p = ramp(t, 0.2f + ring * 0.5f, 1.3f);
                if (p <= 0f || p >= 1f) {
                    continue;
                }
                float r = unit * (0.46f - Easing.inCubic(p) * 0.40f);
                float wob = 1f + 0.05f * wave(this.wobble * 7f, 2.4f, ring * 2f);
                Draw2D.ringEllipse(g, cx, cy, r * wob, r * (0.94f + 0.03f * wave(this.wobble * 6f, 3f, ring)),
                        Math.max(1.2f, unit * 0.004f * (0.4f + p)),
                        (0.35f + 0.4f * p) * fade, Draw2D.lerpRgb(hot, 0xFFFFFF, 0.3f));
            }

            // Inbound shell: discrete sparks travelling toward the core with staggered delay.
            float shellIn = ramp(t, 0.15f, DRAW - 0.15f);
            for (int i = 0; i < SHELLS; i++) {
                float p = Easing.inCubic(Math.max(0f, shellIn - this.shellDelay[i] * 0.4f)
                        / Math.max(0.01f, 1f - this.shellDelay[i] * 0.4f));
                if (p <= 0f) {
                    continue;
                }
                float d = unit * this.shellRadius[i] * (1f - p) * 0.9f;
                if (d < coreR * 1.2f) {
                    continue;
                }
                float ang = this.shellAngle[i] + p * 2.2f;
                float x = cx + (float) Math.cos(ang) * d;
                float y = cy + (float) Math.sin(ang) * d * 0.88f;
                float size = this.shellSize[i] * (unit / 1080f + 0.3f) * (0.6f + p * 0.9f);
                // Motion trail pointing back outward.
                Draw2D.softLine(g, x + (float) Math.cos(ang) * size * 3f,
                        y + (float) Math.sin(ang) * size * 2.6f, x, y,
                        Math.max(1f, size * 0.7f), 0.35f * p * fade, cold);
                star4(g, x, y, size, 0.85f * p * fade, 0xFFFFFF);
            }

            // Screen darkens as it feeds.
            float feed = ramp(t, 0.4f, DRAW - 0.2f);
            Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.35f * feed * fade, 0x000000));

            // Two-frame implosion hold: a compressed white point, no gradient.
            if (t > IMPLODE - 0.08f) {
                Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.85f * fade, 0x000000));
                star4(g, cx, cy, unit * 0.05f, fade, 0xFFFFFF);
                Draw2D.circle(g, cx, cy, unit * 0.012f, Draw2D.argb(fade, 0xFFFFFF),
                        Draw2D.argb(fade, 0xFFFFFF));
            }
        } else {
            float age = t - IMPLODE;
            float blast = Math.max(0f, 1f - age / 0.4f);
            if (blast > 0f) {
                Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(blast * 0.9f * fade,
                        Draw2D.lerpRgb(hot, 0xFFFFFF, 0.7f)));
            }
            // Primary ring, then two harmonics with different decay.
            for (int i = 0; i < 3; i++) {
                float p = Math.min(1f, age / (1.5f + i * 0.5f));
                if (p <= 0f || p >= 1f) {
                    continue;
                }
                float r = unit * (0.02f + Easing.outCubic(p) * (0.75f + i * 0.15f));
                Draw2D.ringEllipse(g, cx, cy, r, r * 0.92f,
                        Math.max(1.5f, unit * 0.012f * (1f - p)),
                        (1f - p) * (0.7f - i * 0.18f) * fade,
                        Draw2D.lerpRgb(hot, 0xFFFFFF, 0.5f - i * 0.2f));
            }
            // Core glow decaying on a shorter curve than the rings.
            float coreDim = Math.max(0f, 1f - age / 1.2f);
            if (coreDim > 0f) {
                Draw2D.glow(g, cx, cy, unit * (0.10f + Easing.outCubic(Math.min(1f, age / 0.8f)) * 0.25f),
                        0.55f * coreDim * fade, Draw2D.lerpRgb(hot, 0xFFFFFF, 0.5f));
            }
            // Shockwave distorts the frame edge into a bright rim.
            float rim = Math.max(0f, 1f - age / 1.0f);
            if (rim > 0f) {
                Draw2D.gradientV(g, 0f, 0f, w, h * 0.18f,
                        Draw2D.argb(0.22f * rim * fade, hot), Draw2D.argb(0f, hot));
                Draw2D.gradientV(g, 0f, h * 0.82f, w, h,
                        Draw2D.argb(0f, hot), Draw2D.argb(0.22f * rim * fade, hot));
            }
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected String tagline() {
        return "Going nova";
    }
}
