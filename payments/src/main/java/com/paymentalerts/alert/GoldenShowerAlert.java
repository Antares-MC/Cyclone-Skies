package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Golden shower: a gold seam cracks open along the top of the screen and a curtain of
 * coins falls in three depth layers, stacking into glinting drifts at the bottom.
 */
final class GoldenShowerAlert extends StageBase {
    static final String[] VARIATIONS = {"Coins", "Bars", "Ingots"};

    private static final float OPEN = 0.8f;
    private static final float END = 6.6f;
    private static final int MAX_COINS = 240;

    private final Coin[] coins = new Coin[MAX_COINS];
    private final float[] crack = new float[13];
    private int cursor;
    private float emitTimer;

    private static final class Coin {
        float x, y, vx, vy, spin, depth, size, restAge;
        int rgb;
        boolean rest;
        boolean live;
        int layer;
    }

    GoldenShowerAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.coins.length; i++) {
            this.coins[i] = new Coin();
        }
        for (int i = 0; i < this.crack.length; i++) {
            this.crack[i] = rnd(-1f, 1f);
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
        this.cues.sound(0.1f, AlertSounds.CHEST_OPEN, 0.6f, 1.1f)
                .sound(OPEN, AlertSounds.COIN_CHIME, 0.8f, 1.0f)
                .sound(0.9f, AlertSounds.CHEST_SHIMMER, 0.6f, 1.2f)
                .sound(3.4f, AlertSounds.JACKPOT_CHIME, 0.6f, 1.0f)
                .sound(5.4f, AlertSounds.COIN_SPARKLE, 0.4f, 0.9f);
        for (float at = 0.9f; at < 5.2f; at += 0.18f) {
            this.cues.sound(at, AlertSounds.MONEY_COIN, 0.34f, 0.9f + this.rng.nextFloat() * 0.3f);
        }
    }

    @Override
    protected void advance(float t) {
        if (t < OPEN - 0.1f || t > 5.4f) {
            return;
        }
        this.emitTimer += this.dt;
        float rate = look() == 1 ? 0.05f : 0.018f;
        while (this.emitTimer > rate) {
            this.emitTimer -= rate;
            emit(t);
        }
        for (Coin c : this.coins) {
            if (!c.live) {
                continue;
            }
            if (c.rest) {
                c.restAge += this.dt;
                continue;
            }
            c.vy += 240f * c.depth * this.dt;
            c.x += c.vx * this.dt;
            c.y += c.vy * this.dt;
            c.spin += c.depth * 2.4f * this.dt;
            float floor = this.h * 0.92f - c.layer * 10f;
            if (c.y >= floor) {
                c.y = floor;
                c.rest = true;
                c.vy = 0f;
                c.vx *= 0.2f;
            }
            if (c.x < -40f || c.x > this.w + 40f) {
                c.live = false;
            }
        }
    }

    private void emit(float t) {
        Coin c = this.coins[this.cursor];
        this.cursor = (this.cursor + 1) % this.coins.length;
        c.depth = 0.55f + this.rng.nextFloat() * 1.1f;
        c.layer = (int) ((c.depth - 0.55f) * 2.4f);
        c.x = rnd(this.w * 0.12f, this.w * 0.88f);
        c.y = this.h * 0.04f;
        c.vx = rnd(-40f, 40f) * c.depth;
        c.vy = rnd(40f, 160f) * c.depth;
        c.size = (look() == 1 ? 10f : 6f) * (0.6f + c.depth * 0.55f);
        c.spin = rnd(0f, 6.28f);
        c.restAge = 0f;
        c.live = true;
        c.rest = false;
        int[] tone = {0xFFD75E, 0xF2B52E, 0xFFE9A8};
        c.rgb = tone[this.rng.nextInt(tone.length)];
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.8f);
        float seam = h * 0.075f;
        int gold = 0xFFD75E;

        Draw2D.gradientV(g, 0f, 0f, w, h * 0.7f, Draw2D.argb(0.25f * fade, 0x2A1C06),
                Draw2D.argb(0f, 0x2A1C06));

        // The seam.
        float open = ramp(t, 0f, OPEN);
        if (open > 0f) {
            float prevX = 0f;
            float prevY = seam + this.crack[0] * h * 0.012f;
            for (int i = 1; i < this.crack.length; i++) {
                float x = w * i / (float) (this.crack.length - 1);
                float y = seam + this.crack[i] * h * 0.02f;
                Draw2D.softLine(g, prevX, prevY, x, y, 10f, 0.18f * open * fade, gold);
                Draw2D.softLine(g, prevX, prevY, x, y, 3f, 0.9f * open * fade, 0xFFF6D0);
                prevX = x;
                prevY = y;
            }
        }
        // Light pouring through the seam.
        float pour = ramp(t, 0.2f, 1.2f);
        for (int i = 0; i < 9; i++) {
            float x = w * (i + 0.5f) / 9f;
            float sway = wave(t, 0.7f, i) * w * 0.02f;
            shaft(g, x + sway, seam, h * 0.78f, Math.max(2f, w * 0.004f),
                    w * (0.02f + 0.01f * (i % 3)) * (0.6f + pour),
                    0.18f * pour * fade, gold);
        }

        // Coins.
        for (Coin c : this.coins) {
            if (!c.live) {
                continue;
            }
            float alpha = (0.45f + c.depth * 0.35f) * fade;
            if (c.rest) {
                alpha *= Math.max(0f, 1f - c.restAge / 2.2f);
            }
            if (look() == 1) {
                Draw2D.rotatedRect(g, c.x, c.y, c.size * 1.7f, c.size * 0.9f, c.spin,
                        Draw2D.argb(alpha, c.rgb));
                Draw2D.rotatedRect(g, c.x, c.y, c.size * 1.4f, c.size * 0.55f, c.spin,
                        Draw2D.argb(alpha * 0.6f, 0xFFF6D0));
            } else if (look() == 2) {
                float s = c.size * 0.7f;
                Draw2D.quad(g, c.x - s, c.y + s * 0.5f, Draw2D.argb(alpha, c.rgb),
                        c.x - s * 0.7f, c.y - s * 0.5f, Draw2D.argb(alpha, 0xFFF6D0),
                        c.x + s * 0.7f, c.y - s * 0.5f, Draw2D.argb(alpha, c.rgb),
                        c.x + s, c.y + s * 0.5f, Draw2D.argb(alpha, 0xF2B52E));
            } else {
                coin(g, c.x, c.y, c.size, c.spin, alpha, c.rgb);
            }
            if (c.rest && c.size > 8f && c.restAge < 0.3f) {
                star4(g, c.x, c.y - c.size, c.size, 0.6f * fade, 0xFFFFFF);
            }
        }

        // Drift glow along the floor.
        float drift = ramp(t, 1.4f, 1.4f) * fade;
        Draw2D.gradientV(g, 0f, h * 0.93f, w, h, Draw2D.argb(0f, gold), Draw2D.argb(0.16f * drift, gold));

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected String tagline() {
        return "Make it rain";
    }
}
