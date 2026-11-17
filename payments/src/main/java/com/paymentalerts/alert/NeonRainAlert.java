package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Neon rain: a cyberpunk street at night. A wet reflection plane sits over the lower third,
 * rain streaks and smears on it, neon signs flicker in the skyline, and ripples pulse with
 * the beat.
 */
final class NeonRainAlert extends StageBase {
    static final String[] VARIATIONS = {"City", "Blade runner", "Acid"};

    private static final float END = 7.2f;
    private static final int BUILDINGS = 13;
    private static final int MAX_RIPPLES = 22;
    private static final int MAX_RAIN = 150;

    private static final int[][] PALETTES = {
            {0xFF2E9A, 0x2EE6FF, 0x0A0616},
            {0xFF8A2E, 0x2EE6C8, 0x100806},
            {0x9BFF3D, 0x2EE6FF, 0x050A06},
    };

    private final float[] bx = new float[BUILDINGS];
    private final float[] bw = new float[BUILDINGS];
    private final float[] bh = new float[BUILDINGS];
    private final int[] bseed = new int[BUILDINGS];
    private final Ripple[] ripples = new Ripple[MAX_RIPPLES];
    private final Drop[] drops = new Drop[MAX_RAIN];
    private int rippleCursor;
    private int dropCursor;
    private float rainTimer;
    private float rippleTimer;

    private static final class Ripple {
        float x, y, size, life;
        int rgb;
        boolean live;
    }

    private static final class Drop {
        float x, y, speed, length;
        int rgb;
        boolean live;
    }

    NeonRainAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        float x = 0f;
        for (int i = 0; i < BUILDINGS; i++) {
            this.bx[i] = x;
            this.bw[i] = rnd(70f, 190f);
            this.bh[i] = rnd(0.16f, 0.42f);
            this.bseed[i] = this.rng.nextInt(9999);
            x += this.bw[i] + rnd(6f, 26f);
        }
        for (int i = 0; i < this.ripples.length; i++) {
            this.ripples[i] = new Ripple();
        }
        for (int i = 0; i < this.drops.length; i++) {
            this.drops[i] = new Drop();
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
        this.cues.sound(0.1f, AlertSounds.BLACKHOLE_CHARGE, 0.5f, 0.9f)
                .sound(0.4f, AlertSounds.STORM_RAIN, 0.6f, 1.1f)
                .sound(1.1f, AlertSounds.JACKPOT_CHIME, 0.35f, 1.2f)
                .sound(4.0f, AlertSounds.COIN_PICKUP, 0.45f, 1.0f)
                .sound(5.4f, AlertSounds.JACKPOT_CHIME, 0.3f, 1.0f)
                .sound(6.4f, AlertSounds.STORM_RAIN, 0.35f, 0.9f);
    }

    @Override
    protected void advance(float t) {
        this.rainTimer += this.dt;
        while (this.rainTimer > 0.006f) {
            this.rainTimer -= 0.006f;
            Drop d = this.drops[this.dropCursor];
            this.dropCursor = (this.dropCursor + 1) % this.drops.length;
            int[] pal = palette();
            d.x = rnd(-60f, this.w + 60f);
            d.y = -20f;
            d.speed = rnd(900f, 1700f);
            d.length = rnd(14f, 42f);
            d.rgb = chance(0.5f) ? pal[1] : 0xFFFFFF;
            d.live = true;
        }
        for (Drop d : this.drops) {
            if (d.live) {
                d.y += d.speed * this.dt;
                d.x -= 130f * this.dt;
                if (d.y > this.h * 0.94f) {
                    d.live = false;
                }
            }
        }
        this.rippleTimer += this.dt;
        while (this.rippleTimer > 0.09f) {
            this.rippleTimer -= 0.09f;
            Ripple r = this.ripples[this.rippleCursor];
            this.rippleCursor = (this.rippleCursor + 1) % this.ripples.length;
            int[] pal = palette();
            r.x = rnd(0f, this.w);
            r.y = rnd(this.h * 0.80f, this.h * 0.97f);
            r.size = rnd(5f, 18f);
            r.life = 1.0f;
            r.rgb = chance(0.6f) ? pal[0] : pal[1];
            r.live = true;
        }
        for (Ripple r : this.ripples) {
            if (r.live) {
                r.life -= this.dt;
                if (r.life <= 0f) {
                    r.live = false;
                }
            }
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.9f);
        int[] pal = palette();
        float horizon = h * 0.62f;
        float dolly = wave(t, 0.35f, 0f) * h * 0.006f;

        Draw2D.gradientV(g, 0f, 0f, w, horizon + dolly, Draw2D.argb(fade, pal[2]),
                Draw2D.argb(fade, Draw2D.lerpRgb(pal[2], pal[0], 0.18f)));
        Draw2D.glow(g, w * 0.62f, horizon * 0.55f, w * 0.4f, 0.10f * fade, pal[0]);
        Draw2D.glow(g, w * 0.25f, horizon * 0.7f, w * 0.3f, 0.08f * fade, pal[1]);

        // Skyline.
        float scale = w / 1920f;
        for (int i = 0; i < BUILDINGS; i++) {
            float x = this.bx[i] * scale;
            float width = this.bw[i] * scale;
            float height = this.bh[i] * h;
            float top = horizon + dolly - height;
            Draw2D.rect(g, x, top, x + width, horizon + dolly, Draw2D.argb(0.9f * fade, 0x08060E));
            Draw2D.rect(g, x, top, x + width, top + 2f, Draw2D.argb(0.35f * fade, pal[1]));
            int cols = Math.max(1, (int) (width / (16f * scale + 6f)));
            int rows = Math.max(1, (int) (height / (22f * scale + 8f)));
            for (int cx = 0; cx < cols; cx++) {
                for (int ry = 0; ry < rows; ry++) {
                    int hash = (this.bseed[i] + cx * 31 + ry * 17) % 11;
                    if (hash > 5) {
                        continue;
                    }
                    float flicker = hash == 0
                            ? (0.5f + 0.5f * (float) Math.abs(Math.sin(t * (5f + hash) + cx)))
                            : 1f;
                    float wx = x + 4f * scale + cx * (width - 6f * scale) / cols;
                    float wy = top + 8f * scale + ry * (height - 12f * scale) / rows;
                    Draw2D.rect(g, wx, wy, wx + Math.max(2f, 5f * scale), wy + Math.max(2f, 5f * scale),
                            Draw2D.argb(0.7f * flicker * fade, hash % 2 == 0 ? pal[0] : pal[1]));
                }
            }
            // Reflection.
            float mirrorTop = horizon + dolly + 6f;
            float mirrorBottom = Math.min(h, mirrorTop + height * 0.7f);
            Draw2D.gradientV(g, x, mirrorTop, x + width, mirrorBottom,
                    Draw2D.argb(0.22f * fade, 0x08060E), Draw2D.argb(0f, 0x08060E));
            for (int cx = 0; cx < cols; cx += 2) {
                float wx = x + 4f * scale + cx * (width - 6f * scale) / cols;
                Draw2D.gradientV(g, wx, mirrorTop, wx + Math.max(2f, 4f * scale),
                        mirrorTop + height * 0.5f, Draw2D.argb(0.16f * fade, pal[cx % 2]),
                        Draw2D.argb(0f, pal[cx % 2]));
            }
        }

        // Neon signs.
        for (int i = 0; i < 3; i++) {
            float sx = w * (0.16f + i * 0.28f);
            float sy = h * (0.16f + (i % 2) * 0.10f);
            float flick = (float) Math.pow(Math.max(0f, (float) Math.sin(t * (3f + i) + i)), 8f);
            int col = i % 2 == 0 ? pal[0] : pal[1];
            Draw2D.rect(g, sx - w * 0.05f, sy - h * 0.02f, sx + w * 0.05f, sy + h * 0.02f,
                    Draw2D.argb(0.35f * fade, 0x0A0810));
            Draw2D.rect(g, sx - w * 0.05f, sy - h * 0.02f, sx + w * 0.05f, sy - h * 0.018f,
                    Draw2D.argb((0.7f - flick * 0.5f) * fade, col));
            Draw2D.rect(g, sx - w * 0.05f, sy + h * 0.018f, sx + w * 0.05f, sy + h * 0.02f,
                    Draw2D.argb((0.7f - flick * 0.5f) * fade, col));
            Draw2D.softLine(g, sx - w * 0.03f, sy, sx + w * 0.03f, sy,
                    Math.max(2f, h * 0.004f), (0.7f - flick * 0.4f) * fade, col);
            Draw2D.glow(g, sx, sy, w * 0.08f, (0.12f + flick * 0.08f) * fade, col);
        }

        // Wet plane.
        Draw2D.gradientV(g, 0f, horizon + dolly, w, h,
                Draw2D.argb(0.35f * fade, 0x05030A), Draw2D.argb(0.8f * fade, 0x05030A));
        for (int i = 0; i < 7; i++) {
            float y = horizon + (h - horizon) * (i / 7f);
            Draw2D.softLine(g, 0f, y, w, y + wave(t, 0.8f, i) * 4f, Math.max(1f, h * 0.0016f),
                    0.10f * fade, pal[i % 2]);
        }
        for (Ripple r : this.ripples) {
            if (!r.live) {
                continue;
            }
            float p = 1f - r.life;
            Draw2D.ringEllipse(g, r.x, r.y, r.size * (0.4f + p * 2.2f), r.size * (0.1f + p * 0.5f),
                    Math.max(1f, 2.2f * (1f - p)), (1f - p) * 0.5f * fade, r.rgb);
        }
        if (t > 4.0f) {
            float p = (t - 4.0f) % 1.6f / 1.6f;
            Draw2D.ringEllipse(g, w * 0.5f, h * 0.9f, w * (0.08f + p * 0.4f), h * (0.02f + p * 0.1f),
                    Math.max(1f, 4f * (1f - p)), (1f - p) * 0.18f * fade, pal[0]);
        }

        // Rain and its reflection.
        for (Drop d : this.drops) {
            if (!d.live) {
                continue;
            }
            float alpha = d.y < horizon ? 0.55f : 0.3f;
            Draw2D.softLine(g, d.x, d.y, d.x + d.length * 0.12f, d.y + d.length,
                    Math.max(1f, h * 0.0022f), alpha * fade, d.rgb);
            float ry = horizon * 2f - d.y;
            if (ry > horizon && ry < h) {
                Draw2D.softLine(g, d.x, ry, d.x - d.length * 0.08f, ry + d.length * 0.8f,
                        Math.max(1f, h * 0.0018f), 0.14f * fade, d.rgb);
            }
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected String tagline() {
        return "Neon nights";
    }
}
