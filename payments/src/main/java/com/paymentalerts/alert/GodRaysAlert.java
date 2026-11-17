package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * God rays: no burst at all. A cloud deck parts, twelve volumetric shafts tilt into place
 * one after another, dust turns slowly inside the beams, and a halo rotates around the
 * name before everything settles into a still, bright frame.
 */
final class GodRaysAlert extends StageBase {
    static final String[] VARIATIONS = {"Dawn", "Sanctuary", "Judgement"};

    private static final float PART = 1.0f;
    private static final float SHAFTS = 1.4f;
    private static final float HALO = 2.6f;
    private static final float END = 8.4f;

    private static final int RAYS = 12;
    private static final int MOTES = 110;
    private static final int CLOUDS = 22;

    private static final int[][] PALETTES = {
            {0xFFF2C8, 0xE8B860, 0x2A2016, 0xFFD890},
            {0xE8F4FF, 0x9AC8E8, 0x121A26, 0xBFE0FF},
            {0xFFE8C0, 0xFF9A40, 0x1A0A08, 0xFFC070},
    };

    private final float[] rayAngle = new float[RAYS];
    private final float[] rayWidth = new float[RAYS];
    private final float[] rayLen = new float[RAYS];
    private final float[] rayDelay = new float[RAYS];
    private final float[] rayTilt = new float[RAYS];
    private final float[] cloudX = new float[CLOUDS];
    private final float[] cloudY = new float[CLOUDS];
    private final float[] cloudR = new float[CLOUDS];
    private final float[] cloudSpeed = new float[CLOUDS];
    private final float[] moteX = new float[MOTES];
    private final float[] moteY = new float[MOTES];
    private final float[] moteSeed = new float[MOTES];
    private final float[] moteSize = new float[MOTES];

    GodRaysAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < RAYS; i++) {
            // Fan out from just below the crown, biased downward.
            this.rayAngle[i] = 1.5708f + rnd(-0.85f, 0.85f);
            this.rayWidth[i] = rnd(0.020f, 0.075f);
            this.rayLen[i] = rnd(0.65f, 1.25f);
            this.rayDelay[i] = (i % 6) * 0.11f + rnd(0f, 0.10f);
            this.rayTilt[i] = rnd(-0.03f, 0.03f);
        }
        for (int i = 0; i < CLOUDS; i++) {
            this.cloudX[i] = rnd(-0.1f, 1.1f);
            this.cloudY[i] = rnd(-0.05f, 0.16f);
            this.cloudR[i] = rnd(0.10f, 0.26f);
            this.cloudSpeed[i] = rnd(0.004f, 0.016f);
        }
        for (int i = 0; i < MOTES; i++) {
            this.moteX[i] = rnd(0f, 1f);
            this.moteY[i] = rnd(0.10f, 1f);
            this.moteSeed[i] = rnd(0f, 6.283f);
            this.moteSize[i] = rnd(0.7f, 2.4f);
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
        this.cues.sound(0.05f, AlertSounds.BLACKHOLE_CHARGE, 0.4f, 1.4f)
                .sound(PART - 0.2f, AlertSounds.CHEST_SHIMMER, 0.5f, 1.0f)
                .sound(PART, AlertSounds.GOD_ACTIVATE, 0.9f, 1.0f)
                .sound(PART + 0.1f, AlertSounds.GOD_CONDUIT, 0.8f, 1.0f)
                .sound(PART, AlertSounds.GOD_BELL, 0.7f, 1.0f)
                .sound(SHAFTS + 0.9f, AlertSounds.GOD_FANFARE, 0.8f, 1.0f)
                .sound(HALO, AlertSounds.GOD_TOTEM, 0.7f, 1.05f)
                .sound(4.2f, AlertSounds.NOVA_SHIMMER, 0.4f, 1.0f)
                .sound(6.2f, AlertSounds.GOD_BELL, 0.5f, 0.9f)
                .sound(7.4f, AlertSounds.CHEST_SHIMMER, 0.35f, 1.1f);
    }

    @Override
    protected void advance(float t) {
        for (int i = 0; i < CLOUDS; i++) {
            this.cloudX[i] += this.cloudSpeed[i] * this.dt;
            if (this.cloudX[i] > 1.2f) {
                this.cloudX[i] = -0.2f;
                this.cloudY[i] = rnd(-0.05f, 0.16f);
            }
        }
        if (t > PART && t < END - 1.2f) {
            // Motes drift upward inside the beams.
            for (int i = 0; i < MOTES; i++) {
                this.moteY[i] -= (6f + (i % 5) * 2f) * this.dt / this.h;
                this.moteX[i] += wave(t * 0.5f, 1.3f, this.moteSeed[i]) * 0.008f * this.dt;
                if (this.moteY[i] < 0.02f) {
                    this.moteY[i] = 1.02f;
                    this.moteX[i] = rnd(0f, 1f);
                }
            }
            if (this.rng.nextFloat() < 0.1f) {
                Particles2D.P p = spawn(Particles2D.Kind.SPARK, rnd(0f, this.w), rnd(this.h * 0.3f, this.h),
                        rnd(-6f, 6f), rnd(-20f, -5f), rnd(1.4f, 3.2f), rnd(1f, 2f),
                        0xFFFFFF, 0x8A7040);
                p.drag = 0.4f;
                p.gravity = -4f;
                p.twinkle = true;
            }
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 1.0f);
        float cx = w * 0.5f;
        float unit = Math.min(w, h);
        int[] pal = palette();
        int light = pal[0];
        int warm = pal[1];
        int dark = pal[2];

        // Ground: a bright sky above, a dim floor below.
        Draw2D.gradientV(g, 0f, 0f, w, h, Draw2D.argb(fade, dark),
                Draw2D.argb(0.85f * fade, Draw2D.lerpRgb(dark, light, 0.35f)));
        float open = Easing.outCubic(ramp(t, 0f, PART));
        Draw2D.glow(g, cx, h * 0.16f, w * 0.45f, 0.22f * open * fade, light);

        // Crown: the light source, a small hot disc with a soft edge.
        float crownR = unit * (0.005f + 0.016f * open);
        if (open > 0.01f) {
            Draw2D.glow(g, cx, h * 0.14f, crownR * 9f, 0.30f * open * fade, light);
            Draw2D.circle(g, cx, h * 0.14f, crownR, Draw2D.argb(fade, 0xFFFFFF), Draw2D.argb(fade, warm));
        }

        // Volumetric shafts: each lays down a trapezoid from the crown to the floor.
        for (int i = 0; i < RAYS; i++) {
            float grow = ramp(t, PART + this.rayDelay[i], SHAFTS);
            if (grow <= 0f) {
                continue;
            }
            float ease = Easing.outCubic(grow);
            float ang = this.rayAngle[i] + this.rayTilt[i] * wave(t, 0.6f, i * 1.4f)
                    + wave(t, 0.25f, i * 2.1f) * 0.02f;
            float len = unit * this.rayLen[i] * 1.5f * ease;
            float halfTop = unit * this.rayWidth[i] * 0.22f;
            float halfBot = unit * this.rayWidth[i] * (0.9f + 0.4f * (i % 3)) * ease;
            float dx = (float) Math.cos(ang);
            float dy = (float) Math.sin(ang);
            float px = -dy;
            float py = dx;
            float topX = cx;
            float topY = h * 0.14f;
            float botX = cx + dx * len;
            float botY = h * 0.14f + dy * len;

            float strength = 0.10f + 0.07f * Math.abs(wave(t, 0.8f, i));
            int cTop = Draw2D.argb(strength * 1.5f * fade, light);
            int cBot = Draw2D.argb(0f, light);
            Draw2D.quad(g,
                    topX + px * halfTop, topY + py * halfTop, cTop,
                    botX + px * halfBot, botY + py * halfBot, cBot,
                    botX - px * halfBot, botY - py * halfBot, cBot,
                    topX - px * halfTop, topY - py * halfTop, cTop);
            // Hot core line down the middle of the shaft.
            Draw2D.softLine(g, topX, topY, botX, botY,
                    Math.max(1f, unit * this.rayWidth[i] * 0.30f),
                    strength * 0.9f * fade, Draw2D.lerpRgb(light, 0xFFFFFF, 0.6f));
            // Floor pool where the shaft lands.
            if (botY > h * 0.6f) {
                Draw2D.ellipse(g, botX, Math.min(h * 1.02f, botY), halfBot * 1.8f, halfBot * 0.34f,
                        Draw2D.argb(0.16f * ease * fade, light), Draw2D.argb(0f, light));
            }
        }

        // Dust motes, only inside the shaft cone.
        float cone = ramp(t, PART, SHAFTS);
        if (cone > 0.01f) {
            for (int i = 0; i < MOTES; i++) {
                float mx = this.moteX[i] * w;
                float my = this.moteY[i] * h;
                // Cone width grows with depth from the crown.
                float depth = Math.max(0f, (my - h * 0.14f) / (h * 0.86f));
                float halfW = unit * 0.34f * depth + w * 0.02f;
                float lateral = Math.abs(mx - cx) / halfW;
                if (lateral > 1f || my < h * 0.12f) {
                    continue;
                }
                float a = (1f - lateral) * 0.55f * cone * fade;
                float tw = 0.5f + 0.5f * Math.abs(wave(t * 2.2f, 1.7f, this.moteSeed[i]));
                Draw2D.circle(g, mx, my, this.moteSize[i],
                        Draw2D.argb(a * tw, light), Draw2D.argb(0f, light));
            }
        }

        // Cloud deck across the top, drifting and parting around the crown.
        for (int i = 0; i < CLOUDS; i++) {
            float x = this.cloudX[i] * w;
            float y = this.cloudY[i] * h;
            float r = this.cloudR[i] * w;
            float part = Math.max(0f, 1f - Math.abs(x - cx) / (w * 0.30f));
            float push = part * open;
            float yy = y - push * h * 0.06f;
            float rr = r * (1f + push * 0.10f);
            Draw2D.ellipse(g, x, yy, rr, rr * 0.45f,
                    Draw2D.argb(0.30f * fade * (1f - push * 0.45f), Draw2D.lerpRgb(dark, warm, 0.35f)),
                    Draw2D.argb(0f, dark));
            Draw2D.ellipse(g, x + rr * 0.2f, yy - rr * 0.10f, rr * 0.6f, rr * 0.26f,
                    Draw2D.argb(0.10f * fade, light), Draw2D.argb(0f, light));
        }

        // Halo around the name.
        float halo = ramp(t, HALO, 1.2f) * (1f - ramp(t, END - 1.6f, 1.2f));
        if (halo > 0.01f) {
            float hr = unit * 0.175f;
            float hy = h * 0.36f;
            for (int i = 0; i < 3; i++) {
                float spin = t * (0.5f + i * 0.22f) + i * 2.1f;
                Draw2D.ringArc(g, cx, hy, hr * (0.9f + i * 0.12f), hr * (0.9f + i * 0.12f),
                        Math.max(1.2f, unit * 0.0035f), halo * (0.7f - i * 0.16f) * fade,
                        Draw2D.lerpRgb(warm, light, 0.5f), spin, spin + 4.2f);
                for (int k = 0; k < 5; k++) {
                    float ang = spin + k * 1.256f;
                    float r = hr * (0.9f + i * 0.12f);
                    star4(g, cx + (float) Math.cos(ang) * r, hy + (float) Math.sin(ang) * r,
                            unit * 0.008f, halo * 0.7f * fade, 0xFFFFFF);
                }
            }
            Draw2D.glow(g, cx, hy, hr * 2.2f, 0.10f * halo * fade, light);
        }

        // Name, gently, no pop.
        float nameIn = ramp(t, HALO + 0.4f, 1.1f);
        if (nameIn > 0.01f) {
            float a = nameIn * fade;
            this.name(g, font, cx, h * 0.23f, w * 0.72f, 3.2f, t, a);
            this.amount(g, font, cx, h * 0.23f + unit * 0.048f, w * 0.6f, 1.7f, warm, a * 0.9f);
            Draw2D.gradientH(g, cx - unit * 0.12f, h * 0.23f + unit * 0.038f,
                    cx + unit * 0.12f, h * 0.23f + unit * 0.038f,
                    Draw2D.argb(0f, warm), Draw2D.argb(0.6f * a, warm));
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected boolean drawsBanner() {
        return false;
    }
}
