package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Cherry blossom: wind, not an explosion. Petals fall on a gusting field, flutter on two
 * axes in three depth layers, settle briefly on an implied ground line, then blow away.
 */
final class CherryBlossomAlert extends StageBase {
    static final String[] VARIATIONS = {"Gentle", "Wind", "Festival"};

    private static final float END = 8.2f;
    private static final int PETALS = 260;

    private final Petal[] petals = new Petal[PETALS];
    private final int[] lanternX = new int[5];
    private final float[] lanternSeed = new float[5];

    private static final class Petal {
        float x, y, vx, vy, rot, spin, size, depth, settle;
        int rgb;
        boolean rest;
    }

    CherryBlossomAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < PETALS; i++) {
            Petal p = new Petal();
            p.depth = rnd(0.45f, 1.4f);
            p.size = rnd(3.5f, 9f) * p.depth;
            p.x = rnd(-40f, 2200f);
            p.y = rnd(-1200f, 900f);
            p.vx = rnd(-20f, 20f);
            p.vy = rnd(18f, 55f) * p.depth;
            p.rot = rnd(0f, 6.283f);
            p.spin = rnd(-2.5f, 2.5f);
            p.rgb = chance(0.25f) ? 0xFFE3EE : (chance(0.4f) ? 0xFFB7D0 : 0xFF8FB4);
            this.petals[i] = p;
        }
        for (int i = 0; i < this.lanternX.length; i++) {
            this.lanternX[i] = (int) rnd(0f, 1920f);
            this.lanternSeed[i] = rnd(0f, 6.283f);
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
        this.cues.sound(0.6f, AlertSounds.JACKPOT_CHIME, 0.5f, 1.3f)
                .sound(1.2f, AlertSounds.CONFETTI_HARP, 0.4f, 1.0f)
                .sound(3.6f, AlertSounds.JACKPOT_CHIME, 0.35f, 1.1f)
                .sound(6.0f, AlertSounds.CONFETTI_HARP, 0.3f, 0.9f);
    }

    private float gust(float t) {
        float base = look() == 1 ? 70f : 34f;
        float slow = wave(t, 0.55f, 0.4f) * base;
        float spike = 0f;
        if (look() != 0) {
            float s = (float) Math.sin(t * 1.35f + 0.8f);
            if (s > 0.85f) {
                spike = (s - 0.85f) / 0.15f * (look() == 1 ? 260f : 120f);
            }
        }
        return base * 0.5f + slow + spike;
    }

    @Override
    protected void advance(float t) {
        float wind = gust(t);
        float ground = this.h * 0.93f;
        for (Petal p : this.petals) {
            if (p.rest) {
                p.settle += this.dt;
                if (p.settle > 1.8f || wind > 140f) {
                    p.rest = false;
                    p.vy = -rnd(30f, 90f);
                    p.vx = wind * 1.6f;
                }
                continue;
            }
            p.vx += (wind - p.vx) * 0.9f * this.dt;
            p.vy += (26f * p.depth - p.vy) * 0.6f * this.dt;
            p.vy += wave(t * 1.7f, 2.2f, p.rot) * 14f * this.dt;
            p.x += p.vx * this.dt;
            p.y += p.vy * this.dt;
            p.rot += p.spin * this.dt;
            if (p.y >= ground) {
                p.y = ground;
                p.rest = true;
                p.settle = 0f;
            }
            if (p.x > this.w + 60f) {
                p.x = -60f;
                p.y = rnd(-200f, this.h * 0.7f);
            }
            if (p.y < -200f) {
                p.y = this.h * 0.8f;
            }
        }
        if (t > 6.4f) {
            for (Petal p : this.petals) {
                p.rest = false;
                p.vx += 60f * this.dt;
            }
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 1.0f);
        float form = ramp(t, 0f, 1.0f);

        // Soft pink haze and a pale sky.
        Draw2D.gradientV(g, 0f, 0f, w, h, Draw2D.argb(0.55f * form * fade, 0xF6DDE6),
                Draw2D.argb(0.35f * form * fade, 0xF2C8D8));
        Draw2D.glow(g, w * 0.2f, h * 0.1f, w * 0.5f, 0.14f * form * fade, 0xFFE3EE);

        float enter = Easing.outCubic(ramp(t, 0f, 1.1f));
        if (look() == 2) {
            for (int i = 0; i < this.lanternX.length; i++) {
                float lx = this.lanternX[i] / 1920f * w + wave(t, 0.3f, this.lanternSeed[i]) * w * 0.02f;
                float ly = h * (0.18f + i * 0.07f) + wave(t, 0.45f, i) * h * 0.01f;
                float s = Math.min(w, h) * 0.022f;
                Draw2D.glow(g, lx, ly, s * 2.6f, 0.22f * form * fade, 0xFFB060);
                Draw2D.ellipse(g, lx, ly, s * 0.6f, s,
                        Draw2D.argb(0.9f * form * fade, 0xFF9A4A), Draw2D.argb(0.6f * form * fade, 0xC84A20));
                Draw2D.softLine(g, lx, ly - s, lx, ly + s, 1f, 0.4f * form, 0x8A4A20);
            }
        }

        if (t < 3.2f) {
            branch(g, -w * 0.12f + enter * w * 0.12f, h * 0.06f, 0.35f,
                    Math.min(w, h) * 0.22f, Math.max(4f, Math.min(w, h) * 0.012f), 4,
                    (1f - ramp(t, 2.2f, 1.0f)) * form * fade, t);
        }

        // Petals: far layer first, then near.
        for (int layer = 0; layer < 3; layer++) {
            for (Petal p : this.petals) {
                int l = p.depth < 0.75f ? 0 : (p.depth < 1.05f ? 1 : 2);
                if (l != layer) {
                    continue;
                }
                float alpha = (0.45f + p.depth * 0.35f) * form * fade;
                petal(g, p.x, p.y, p.size, p.rot, p.depth, alpha, p.rgb);
            }
        }

        // Ground line and accumulated petals.
        Draw2D.gradientV(g, 0f, h * 0.93f, w, h, Draw2D.argb(0f, 0x6A3A44),
                Draw2D.argb(0.18f * form * fade, 0x6A3A44));

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    private void petal(GuiGraphics g, float cx, float cy, float size, float rot, float depth,
                       float alpha, int rgb) {
        float c = (float) Math.cos(rot);
        float s = (float) Math.sin(rot);
        float flutter = 0.35f + 0.65f * Math.abs((float) Math.cos(rot * 1.7f));
        float px = size * flutter;
        float light = depth > 1.05f ? 1f : 0.75f;
        int c0 = Draw2D.argb(alpha * 0.55f, Draw2D.lerpRgb(rgb, 0xFFFFFF, 0.3f * light));
        int c1 = Draw2D.argb(alpha, rgb);
        float p1x = cx - px * s;
        float p1y = cy + px * c;
        float p2x = cx + px * s;
        float p2y = cy - px * c;
        Draw2D.quad(g,
                cx + size * 0.9f * c, cy + size * 0.9f * s, c0,
                p1x, p1y, c1,
                cx - size * 1.1f * c, cy - size * 1.1f * s, c0,
                p2x, p2y, c1);
    }

    private void branch(GuiGraphics g, float x, float y, float ang, float len, float thick,
                        int depth, float alpha, float t) {
        if (depth <= 0 || len < 6f || alpha <= 0.01f) {
            return;
        }
        float x2 = x + (float) Math.cos(ang) * len;
        float y2 = y + (float) Math.sin(ang) * len;
        Draw2D.softLine(g, x, y, x2, y2, thick, alpha * 0.9f, 0x3A2028);
        Draw2D.softLine(g, x, y, x2, y2, thick * 0.32f, alpha * 0.5f, 0x6E4048);
        float sway = wave(t, 0.8f, ang) * 0.05f;
        branch(g, x2, y2, ang - 0.55f + sway, len * 0.68f, thick * 0.62f, depth - 1, alpha * 0.9f, t);
        branch(g, x2, y2, ang + 0.42f + sway, len * 0.62f, thick * 0.56f, depth - 1, alpha * 0.85f, t);
        branch(g, x2, y2, ang + 0.08f, len * 0.48f, thick * 0.44f, depth - 1, alpha * 0.7f, t);
    }

    @Override
    protected String tagline() {
        return "Spring arrives";
    }
}
