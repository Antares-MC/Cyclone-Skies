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
 * EMP blast: a charged pulse fires, and everything it touches glitches. The frame tears
 * into scanline blocks, colours invert for a few frames, luminance drops out, then systems
 * reboot with a sweep.
 */
final class EmpBlastAlert extends StageBase {
    static final String[] VARIATIONS = {"Pulse", "Ring", "Grid kill"};

    private static final float FIRE = 0.9f;
    private static final float END = 6.1f;

    private static final int[][] PALETTES = {
            {0x2EE6FF, 0xFFFFFF, 0x1E3A5A},
            {0x8A7CFF, 0xD8F0FF, 0x241A4A},
            {0x9BFF3D, 0xE8FFD0, 0x14301A},
    };

    private final float[] bandY = new float[14];
    private final float[] bandH = new float[14];
    private final float[] bandShift = new float[14];
    private final float[] bandAt = new float[14];
    private boolean fired;
    private float glitchFrame;

    EmpBlastAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.bandY.length; i++) {
            this.bandY[i] = rnd(0.05f, 0.95f);
            this.bandH[i] = rnd(0.008f, 0.06f);
            this.bandShift[i] = rnd(-0.18f, 0.18f);
            this.bandAt[i] = rnd(0f, 2.6f);
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
        this.cues.sound(0.05f, AlertSounds.LASER_CHARGE, 0.6f, 0.9f)
                .sound(0.45f, AlertSounds.LASER_HUM, 0.5f, 1.2f)
                .sound(FIRE, AlertSounds.NUKE_SHOCKWAVE, 0.9f, 1.2f)
                .sound(FIRE, AlertSounds.LASER_FIRE, 0.8f, 0.9f)
                .sound(1.3f, AlertSounds.BLACKHOLE_COLLAPSE, 0.5f, 1.3f)
                .sound(2.0f, AlertSounds.STORM_IMPACT, 0.5f, 1.1f)
                .sound(3.0f, AlertSounds.MONEY_LEVELUP, 0.7f, 1.0f)
                .sound(4.6f, AlertSounds.CHEST_SHIMMER, 0.4f, 1.0f);
        ScreenShake.add(FIRE, 0.5f, 0.9f);
    }

    @Override
    protected void advance(float t) {
        this.glitchFrame -= this.dt;
        if (!this.fired && t >= FIRE) {
            this.fired = true;
            this.glitchFrame = 0.4f;
            for (int i = 0; i < 60; i++) {
                float ang = rnd(0f, 6.283f);
                float speed = rnd(400f, 1400f);
                Particles2D.P p = spawn(Particles2D.Kind.SPARK, this.w * 0.5f, this.h * 0.42f,
                        (float) Math.cos(ang) * speed, (float) Math.sin(ang) * speed,
                        rnd(0.3f, 0.9f), rnd(1f, 2.6f), palette()[0], 0xFFFFFF);
                p.drag = 1.6f;
                p.gravity = 0f;
            }
        }
        if (t > 1.4f && t < 3.0f && this.rng.nextFloat() < 0.2f) {
            Particles2D.P p = spawn(Particles2D.Kind.SPARK, rnd(0f, this.w), rnd(0f, this.h),
                    rnd(-20f, 20f), rnd(-20f, 20f), rnd(0.2f, 0.5f), rnd(1f, 2f),
                    palette()[1], palette()[0]);
            p.twinkle = true;
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.8f);
        int[] pal = palette();
        float cx = w * 0.5f;
        float cy = h * 0.42f;

        Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.75f * fade, 0x03060A));
        Draw2D.glow(g, cx, cy, Math.min(w, h) * 0.4f, 0.10f * fade * ramp(t, 0.1f, 0.8f), pal[2]);

        if (t < FIRE) {
            float charge = ramp(t, 0f, FIRE);
            float r = Math.min(w, h) * (0.02f + 0.10f * Easing.inCubic(charge));
            Draw2D.ring(g, cx, cy, r, Math.max(1.5f, 3f + charge * 4f), 0.6f * fade, pal[0]);
            Draw2D.ring(g, cx, cy, r * 0.7f, 2f, 0.35f * fade, pal[1]);
            Draw2D.glow(g, cx, cy, r * 2.2f, 0.2f * fade * charge, pal[0]);
            for (int i = 0; i < 24; i++) {
                float ang = i * 6.283f / 24f + t * 3f;
                float inner = r * 1.5f;
                float outer = r * 2.6f;
                Draw2D.softLine(g, cx + (float) Math.cos(ang) * inner, cy + (float) Math.sin(ang) * inner,
                        cx + (float) Math.cos(ang) * outer, cy + (float) Math.sin(ang) * outer,
                        Math.max(1f, 2f * charge), 0.3f * charge * fade, pal[1]);
            }
        } else {
            for (int i = 0; i < (look() == 1 ? 1 : 3); i++) {
                float p = ramp(t, FIRE + i * 0.22f, 1.0f);
                if (p <= 0f || p >= 1f) {
                    continue;
                }
                float r = Math.min(w, h) * (0.04f + Easing.outCubic(p) * 0.9f);
                Draw2D.ring(g, cx, cy, r, Math.max(1.5f, 8f * (1f - p)),
                        (1f - p) * 0.7f * fade, pal[0]);
                Draw2D.ring(g, cx, cy, r * 0.92f, Math.max(1f, 3f * (1f - p)),
                        (1f - p) * 0.4f * fade, pal[1]);
            }
        }

        // Glitch: displaced bands, channel split, inversion frames.
        float glitch = ramp(t, FIRE, 0.06f) * (1f - ramp(t, FIRE + (look() == 2 ? 2.4f : 0.9f), 1.0f));
        if (glitch > 0.01f) {
            for (int i = 0; i < this.bandY.length; i++) {
                if (t < FIRE + this.bandAt[i] * 0.6f) {
                    continue;
                }
                float y = this.bandY[i] * h;
                float bh = this.bandH[i] * h;
                float shift = this.bandShift[i] * w * glitch;
                Draw2D.rect(g, shift, y, w + shift, y + bh,
                        Draw2D.argb(0.5f * glitch * fade, pal[i % 2 == 0 ? 0 : 1]));
                Draw2D.rect(g, shift * 0.6f, y, w + shift * 0.6f, y + bh * 0.4f,
                        Draw2D.argb(0.22f * glitch * fade, 0x000000));
            }
            float frame = (float) Math.abs(Math.sin(t * 46f));
            if (frame > 0.85f) {
                Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.35f * glitch * fade, pal[1]));
            }
            Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.12f * glitch * fade, 0x8A8A8A));
        }

        // Dead pixel grid flickering back on.
        if (t > 1.6f && t < 3.4f) {
            float gridA = 0.12f * fade * (1f - ramp(t, 2.6f, 0.8f));
            int step = 48;
            for (int y = 0; y < h; y += step) {
                for (int x = 0; x < w; x += step) {
                    int hash = (x * 31 + y * 17) ^ (int) (t * 8f);
                    if ((hash & 7) != 0) {
                        continue;
                    }
                    Draw2D.rect(g, x + 4f, y + 4f, x + step - 4f, y + step - 4f,
                            Draw2D.argb(gridA, pal[(hash & 8) == 0 ? 0 : 1]));
                }
            }
        }

        // Boot-up sweep.
        if (t > 3.0f) {
            float boot = ramp(t, 3.0f, 1.6f);
            float y = h * 0.1f + boot * h * 0.8f;
            Draw2D.softLine(g, w * 0.1f, y, w * 0.9f, y, Math.max(1f, h * 0.003f),
                    (1f - boot * 0.6f) * 0.6f * fade, pal[0]);
            Draw2D.glow(g, w * 0.5f, y, w * 0.3f, 0.10f * fade * (1f - boot), pal[1]);
            Component status = TextFx.plain(boot < 0.55f ? "SYSTEMS OFFLINE" : "SYSTEMS ONLINE", pal[0], false);
            TextFx.centered(g, font, status, w * 0.5f, h * 0.9f,
                    TextFx.fitScale(font, status, w * 0.3f, 1.1f), 0.6f * fade * (1f - boot * 0.5f));
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected String tagline() {
        return "SYSTEMS DOWN";
    }
}
