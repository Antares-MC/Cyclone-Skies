package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Heart bomb: a heart-shaped bomb drops in, its pulse accelerates, and it bursts into
 * three concentric heart-shaped shockwaves and a cloud of floating hearts.
 */
final class HeartBombAlert extends StageBase {
    static final String[] VARIATIONS = {"Pink", "Red", "Rainbow"};

    private static final float LAND = 0.9f;
    private static final float BURST = 1.8f;
    private static final float END = 5.9f;
    private static final int HEARTS = 120;

    private static final int[] COLORS = {0xFF5FA2, 0xFF2E4C, 0xFFFFFF};
    private static final int GOLD = 0xFFD75E;

    private final HeartBit[] hearts = new HeartBit[HEARTS];
    private final float[] crackAngle = new float[9];
    private final float[] crackLen = new float[9];
    private int cursor;
    private boolean burstDone;

    private static final class HeartBit {
        float x, y, vx, vy, size, rot, spin, life;
        int rgb;
        boolean live;
    }

    HeartBombAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.hearts.length; i++) {
            this.hearts[i] = new HeartBit();
        }
        for (int i = 0; i < this.crackAngle.length; i++) {
            this.crackAngle[i] = rnd(-2.6f, -0.5f);
            this.crackLen[i] = rnd(0.35f, 1.0f);
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

    private int mainColor() {
        int look = look();
        return look == 2 ? Draw2D.hsv((float) ((System.nanoTime() / 1.0E9) % 1.0), 0.55f, 1f) : COLORS[look];
    }

    @Override
    protected void cue() {
        this.cues.sound(0.1f, AlertSounds.LASER_LOCK, 0.5f, 1.5f)
                .sound(0.45f, AlertSounds.JACKPOT_TICK, 0.6f, 1.4f)
                .sound(0.85f, AlertSounds.JACKPOT_TICK, 0.7f, 1.3f)
                .sound(1.15f, AlertSounds.LASER_LOCK, 0.6f, 1.6f)
                .sound(1.4f, AlertSounds.JACKPOT_TICK, 0.8f, 1.2f)
                .sound(1.62f, AlertSounds.JACKPOT_TICK, 0.9f, 1.1f)
                .sound(BURST, AlertSounds.NUKE_SHOCKWAVE, 0.9f, 1.0f)
                .sound(BURST, AlertSounds.MONEY_DING, 0.8f, 1.3f)
                .sound(BURST + 0.2f, AlertSounds.MONEY_LEVELUP, 0.8f, 1.0f)
                .sound(2.8f, AlertSounds.COIN_SPARKLE, 0.5f, 1.3f);
        ScreenShake.add(BURST, 0.55f, 0.45f);
    }

    @Override
    protected void advance(float t) {
        if (!this.burstDone && t >= BURST) {
            this.burstDone = true;
            int look = look();
            for (int i = 0; i < 48; i++) {
                HeartBit b = this.hearts[this.cursor];
                this.cursor = (this.cursor + 1) % this.hearts.length;
                float ang = rnd(0f, 6.283f);
                float speed = rnd(90f, 420f);
                b.x = this.w * 0.5f;
                b.y = this.h * 0.46f;
                b.vx = (float) Math.cos(ang) * speed;
                b.vy = (float) Math.sin(ang) * speed - 60f;
                b.size = rnd(7f, 20f);
                b.rot = rnd(-0.4f, 0.4f);
                b.spin = rnd(-2f, 2f);
                b.life = rnd(2.2f, 4.2f);
                b.rgb = look == 2 ? Draw2D.hsv(i / 48f, 0.6f, 1f) : (i % 5 == 0 ? GOLD : COLORS[look]);
                b.live = true;
            }
        }
        for (HeartBit b : this.hearts) {
            if (!b.live) {
                continue;
            }
            b.life -= this.dt;
            b.vy += 120f * this.dt;
            b.vx *= (float) Math.exp(-1.1f * this.dt);
            b.vy *= (float) Math.exp(-0.7f * this.dt);
            b.x += b.vx * this.dt;
            b.y += b.vy * this.dt;
            b.rot += b.spin * this.dt;
            if (t > BURST + 1.3f) {
                b.vy -= 70f * this.dt;
            }
            if (b.life <= 0f) {
                b.live = false;
            }
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.8f);
        float cx = w * 0.5f;
        float unit = Math.min(w, h);
        int color = mainColor();

        Draw2D.glow(g, cx, h * 0.46f, unit * 0.45f, 0.14f * fade * ramp(t, 0f, 1.2f), color);

        for (HeartBit b : this.hearts) {
            if (!b.live) {
                continue;
            }
            float a = Easing.clamp01(b.life / 0.8f) * fade;
            heart(g, b.x, b.y, b.size * (1f + 0.1f * wave(t, 6f, b.rot)), a, b.rgb);
        }

        if (t < BURST + 0.05f) {
            float drop = Easing.outCubic(ramp(t, 0f, LAND));
            float y = -unit * 0.15f + (h * 0.48f + unit * 0.15f) * drop;
            y += (1f - drop) * 0f;
            if (t < LAND) {
                y += (float) Math.sin(t * 18f) * 1.5f;
            }
            float pulse = 0.5f + 0.5f * (float) Math.sin(t * (t < LAND ? 8.8f : 25f) * 2f);
            float grow = 1f + (t > LAND ? 0.32f * ramp(t, LAND, BURST - LAND) : 0.05f) * pulse;
            float r = unit * 0.10f * grow;

            // fuse
            float fuseTop = y - r * 1.55f;
            Draw2D.softLine(g, cx, y - r * 0.9f, cx + r * 0.25f, fuseTop, Math.max(1.4f, r * 0.08f), 0.85f, 0x8A6A4A);
            star4(g, cx + r * 0.25f, fuseTop, r * (0.16f + 0.08f * pulse), 0.9f * fade, 0xFFE082);

            for (int i = 0; i < this.crackAngle.length; i++) {
                float crack = ramp(t, LAND + 0.45f, 0.5f);
                if (crack <= 0f) {
                    continue;
                }
                float ang = this.crackAngle[i];
                float len = r * this.crackLen[i] * (0.5f + crack);
                Draw2D.softLine(g, cx, y, cx + (float) Math.cos(ang) * len,
                        y + (float) Math.sin(ang) * len, Math.max(1f, r * 0.05f),
                        crack * 0.8f * fade, 0xFFFFFF);
            }

            heart(g, cx, y, r, fade, color);
            Draw2D.glow(g, cx - r * 0.3f, y - r * 0.4f, r * 0.9f, 0.25f * fade, 0xFFFFFF);
            Draw2D.softLine(g, cx - r * 0.55f, y - r * 0.35f, cx - r * 0.2f, y - r * 0.5f,
                    Math.max(1f, r * 0.06f), 0.4f * fade, 0xFFFFFF);
        }

        if (t >= BURST) {
            float flash = 1f - ramp(t, BURST, 0.3f);
            if (flash > 0f) {
                Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(flash * 0.45f, color));
            }
            for (int i = 0; i < 3; i++) {
                float p = ramp(t, BURST + i * 0.16f, 1.4f);
                if (p <= 0f || p >= 1f) {
                    continue;
                }
                float r = unit * (0.1f + Easing.outCubic(p) * 0.55f);
                int ringColor = look() == 2 ? Draw2D.hsv(i / 3f + t * 0.1f, 0.5f, 1f) : color;
                heartRing(g, cx, h * 0.46f, r, Math.max(1.5f, unit * 0.006f * (1f - p)),
                        (1f - p) * 0.7f * fade, ringColor);
            }
            for (int i = 0; i < 3; i++) {
                float p = ramp(t, BURST + i * 0.12f, 1.2f);
                if (p > 0f && p < 1f) {
                    Draw2D.ring(g, cx, h * 0.46f, unit * (0.1f + p * 0.5f),
                            Math.max(1f, 3f * (1f - p)), (1f - p) * 0.25f * fade, 0xFFFFFF);
                }
            }
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected String tagline() {
        return "<3";
    }
}
