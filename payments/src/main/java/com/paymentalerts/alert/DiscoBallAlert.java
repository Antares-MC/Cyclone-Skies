package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Disco ball: a mirrored ball descends on a chain, swings on a damped pendulum, then
 * spins up while rotating light cones sweep the screen and a dance floor pulses below.
 */
final class DiscoBallAlert extends StageBase {
    static final String[] VARIATIONS = {"70s", "Neon", "Strobe"};

    private static final float LAND = 1.4f;
    private static final float SPIN_END = 4.6f;
    private static final float END = 7.2f;

    private static final int[][] PALETTES = {
            {0xFFD36E, 0xE8452E, 0x2FB7A8, 0x8A4FFF},
            {0xFF2E9A, 0x2EE6FF, 0x9BFF3D, 0xFFFFFF},
            {0xFFFFFF, 0xFF2E9A, 0x2EE6FF, 0xFFD36E},
    };

    private float confettiTimer;

    DiscoBallAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
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
        this.cues.sound(0.1f, AlertSounds.CHEST_RATTLE, 0.5f, 1.3f)
                .sound(LAND, AlertSounds.JACKPOT_BELL, 0.9f, 1.0f)
                .sound(LAND + 0.25f, AlertSounds.JACKPOT_PLING, 0.7f, 1.2f)
                .sound(SPIN_END, AlertSounds.JACKPOT_PLING, 0.8f, 0.9f)
                .sound(SPIN_END + 0.2f, AlertSounds.CONFETTI_HARP, 0.5f, 1.0f);
        for (float at = LAND; at < SPIN_END; at += 0.5f) {
            this.cues.sound(at, AlertSounds.MONEY_COIN, 0.55f, 1.15f);
        }
        for (float at = LAND + 0.8f; at < SPIN_END; at += 1.0f) {
            this.cues.sound(at, AlertSounds.JACKPOT_TICK, 0.4f, 1.4f);
        }
    }

    @Override
    protected void advance(float t) {
        if (t < LAND || t > SPIN_END + 0.8f) {
            return;
        }
        this.confettiTimer += this.dt;
        float rate = t > SPIN_END - 0.5f ? 0.08f : 0.035f;
        while (this.confettiTimer > rate) {
            this.confettiTimer -= rate;
            float ang = t * 1.9f + rnd(-0.5f, 0.5f);
            float r = Math.min(this.w, this.h) * rnd(0.16f, 0.3f);
            int[] pal = palette();
            Particles2D.P p = spawn(Particles2D.Kind.CONFETTI,
                    this.w * 0.5f + (float) Math.cos(ang) * r,
                    this.h * 0.3f + (float) Math.sin(ang) * r * 0.5f,
                    (float) Math.cos(ang + 1.57f) * 70f, rnd(20f, 90f),
                    rnd(1.6f, 3.4f), rnd(2.5f, 5f), pal[rng.nextInt(pal.length)], 0xFFFFFF);
            p.gravity = 120f;
            p.drag = 0.7f;
            p.angle = rnd(0f, 6.28f);
            p.spin = rnd(-8f, 8f);
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.8f);
        float cx = w * 0.5f;
        float unit = Math.min(w, h);
        int[] pal = palette();
        boolean strobe = look() == 2;
        float beat = t * (strobe ? 9.5f : 6.5f);

        // Room darkness and floor.
        Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.55f * fade * ramp(t, 0f, 0.5f), 0x07060A));
        Draw2D.gradientV(g, 0f, h * 0.78f, w, h, Draw2D.argb(0f, 0x120A18),
                Draw2D.argb(0.85f * fade, 0x120A18));

        float descend = Easing.outCubic(ramp(t, 0f, LAND));
        float rise = Easing.inCubic(ramp(t, SPIN_END, END - SPIN_END - 0.2f));
        float swing = (float) Math.sin(t * 2.4f) * (1f - ramp(t, LAND, 1.2f)) * w * 0.07f;
        float ballY = -unit * 0.2f + (h * 0.24f + unit * 0.2f) * descend - rise * h * 0.55f;
        float ballX = cx + swing;
        float radius = unit * 0.075f * (1f + 0.03f * (float) Math.sin(beat));

        // Chain.
        if (t < LAND + 1.0f) {
            Draw2D.softLine(g, cx, 0f, ballX, ballY - radius, 2.4f, 0.5f * fade, 0x9AA0A8);
            for (float y = 0f; y < ballY - radius; y += 14f) {
                Draw2D.circle(g, cx + (ballX - cx) * (y / Math.max(1f, ballY)), y, 2.4f,
                        Draw2D.argb(0.6f * fade, 0xB8BEC6), Draw2D.argb(0.2f * fade, 0x3A3E44));
            }
        }

        // Beams: only once the ball is down and spinning.
        float spin = ramp(t, LAND, 1.2f) * (t - LAND);
        if (t > LAND) {
            int beams = 12;
            for (int i = 0; i < beams; i++) {
                float ang = i * 6.283f / beams + spin * 1.6f;
                float spread = 0.16f + 0.10f * (float) Math.sin(beat + i);
                float len = h * 1.25f;
                int col = pal[i % pal.length];
                float a = (0.10f + 0.05f * (float) Math.sin(beat * 2f + i * 1.3f)) * ramp(t, LAND, 0.6f);
                Draw2D.triangle(g, ballX, ballY,
                        Draw2D.argb(a * fade, col),
                        ballX + (float) Math.cos(ang - spread) * len, ballY + (float) Math.sin(ang - spread) * len,
                        Draw2D.argb(0f, col),
                        ballX + (float) Math.cos(ang + spread) * len, ballY + (float) Math.sin(ang + spread) * len,
                        Draw2D.argb(0f, col));
            }
            // Floor patches where the beams land.
            for (int i = 0; i < 8; i++) {
                float ang = i * 6.283f / 8 + spin * 1.1f;
                float fx = cx + (float) Math.cos(ang) * w * 0.31f;
                float fy = h * 0.90f + (float) Math.sin(ang) * h * 0.02f;
                Draw2D.ellipse(g, fx, fy, w * 0.055f, h * 0.022f,
                        Draw2D.argb(0.22f * fade * ramp(t, LAND, 0.7f), pal[i % pal.length]),
                        Draw2D.argb(0f, pal[i % pal.length]));
            }
        }

        drawBall(g, ballX, ballY, radius, spin, t, fade, pal);

        if (strobe && t > LAND && t < SPIN_END) {
            float strobeAlpha = (float) Math.pow(Math.max(0f, Math.sin(beat * 1.7f)), 6f) * 0.16f;
            Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(strobeAlpha * fade, 0xFFFFFF));
        }

        if (t > LAND + 0.4f && t < SPIN_END) {
            float ringP = (t * 1.6f) % 1f;
            Draw2D.ringEllipse(g, cx, h * 0.93f, w * (0.12f + ringP * 0.2f), h * 0.035f,
                    2f, (1f - ringP) * 0.2f * fade, pal[0]);
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    private void drawBall(GuiGraphics g, float cx, float cy, float r, float spin, float t,
                          float fade, int[] pal) {
        Draw2D.glow(g, cx, cy, r * 1.7f, 0.18f * fade, 0xBFC6D0);
        Draw2D.circle(g, cx, cy, r, Draw2D.argb(fade, 0xD8DEE8), Draw2D.argb(fade, 0x5A6068));
        int rows = 5;
        for (int lat = 0; lat < rows; lat++) {
            float v = -0.8f + lat * (1.6f / (rows - 1));
            float ringR = r * (float) Math.sqrt(Math.max(0f, 1f - v * v));
            int cols = Math.max(4, Math.round(10 * (float) Math.sqrt(Math.max(0.15f, 1f - v * v))));
            for (int lon = 0; lon < cols; lon++) {
                float u = lon / (float) cols + spin * 0.08f;
                float sx = cx + (float) Math.cos(u * 6.283f) * ringR;
                float sy = cy + v * r + (float) Math.sin(u * 6.283f) * ringR * 0.18f;
                float lit = 0.45f + 0.55f * Math.max(0f, (float) Math.sin(u * 6.283f + spin * 0.9f));
                int base = pal[(lat + lon) % pal.length];
                int col = Draw2D.lerpRgb(0x7A818C, base, lit * 0.7f);
                float s = Math.max(1.4f, r * 0.16f);
                Draw2D.rotatedRect(g, sx, sy, s, s * 0.72f, u * 3f, Draw2D.argb(fade, col));
                if (lit > 0.92f) {
                    star4(g, sx, sy, s, 0.6f * fade, 0xFFFFFF);
                }
            }
        }
        Draw2D.circle(g, cx, cy - r * 0.3f, r * 0.22f,
                Draw2D.argb(0.35f * fade, 0xFFFFFF), Draw2D.argb(0f, 0xFFFFFF));
        Draw2D.ring(g, cx, cy, r, Math.max(1f, r * 0.05f), 0.7f * fade, 0xF0F4FF);
    }
}
