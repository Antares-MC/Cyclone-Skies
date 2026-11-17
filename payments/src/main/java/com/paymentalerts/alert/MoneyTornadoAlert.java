package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Money tornado: a real vortex built from helix-sampled bills. The funnel walks across the
 * screen, throws bills out of its top, and then thins out and collapses.
 */
final class MoneyTornadoAlert extends StageBase {
    static final String[] VARIATIONS = {"Bills", "Coins", "Gems"};

    private static final float FORM = 1.2f;
    private static final float END = 7.2f;
    private static final int COUNT = 170;

    private final Flake[] flakes = new Flake[COUNT];
    private float throwTimer;

    private static final class Flake {
        float seed;
        float speed;
        float size;
        float tilt;
        int rgb;
    }

    MoneyTornadoAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.flakes.length; i++) {
            Flake f = new Flake();
            f.seed = rnd(0f, 1f);
            f.speed = rnd(1.4f, 3.6f) * (chance(0.5f) ? 1f : -1f);
            f.size = rnd(7f, 15f);
            f.tilt = rnd(-8f, 8f);
            f.rgb = switch (look()) {
                case 1 -> chance(0.5f) ? 0xFFD75E : 0xF2B52E;
                case 2 -> Draw2D.hsv(rnd(0.45f, 0.85f), 0.6f, 1f);
                default -> chance(0.25f) ? 0xE8D9A8 : (chance(0.5f) ? 0x86C98A : 0x69B978);
            };
            this.flakes[i] = f;
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
        this.cues.sound(0.1f, AlertSounds.NUKE_RUMBLE, 0.6f, 0.8f)
                .sound(1.2f, AlertSounds.MONEY_COIN, 0.7f, 1.0f)
                .sound(2.0f, AlertSounds.NUKE_RUMBLE, 0.45f, 0.9f)
                .sound(3.0f, AlertSounds.MONEY_LEVELUP, 0.6f, 1.1f)
                .sound(4.2f, AlertSounds.NUKE_RUMBLE, 0.4f, 0.7f)
                .sound(5.4f, AlertSounds.COIN_SPARKLE, 0.4f, 0.9f);
        for (float at = 1.4f; at < 5.2f; at += 0.6f) {
            this.cues.sound(at, AlertSounds.MONEY_COIN, 0.4f, 1.0f + this.rng.nextFloat() * 0.3f);
        }
    }

    @Override
    protected void advance(float t) {
        if (t < FORM || t > 5.6f) {
            return;
        }
        this.throwTimer += this.dt;
        while (this.throwTimer > 0.05f) {
            this.throwTimer -= 0.05f;
            float cx = funnelX(t);
            float topY = this.h * (0.16f + 0.05f * wave(t, 1.1f, 0f));
            int col = this.flakes[this.rng.nextInt(this.flakes.length)].rgb;
            Particles2D.P p = spawn(Particles2D.Kind.CONFETTI,
                    cx + rnd(-20f, 20f), topY, rnd(-260f, 260f), rnd(-380f, -120f),
                    rnd(1.4f, 2.8f), rnd(2f, 5f), col, 0xFFFFFF);
            p.gravity = 380f;
            p.drag = 0.7f;
            p.angle = rnd(0f, 6.28f);
            p.spin = rnd(-12f, 12f);
        }
    }

    private float funnelX(float t) {
        float sway = (float) Math.sin(t * 0.75f - 0.6f) * 0.17f
                + (float) Math.sin(t * 0.31f) * 0.06f;
        return this.w * (0.5f + sway * Math.min(1f, t / FORM));
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.9f);
        float cx = funnelX(t);
        float ground = h * 0.90f;
        float form = Easing.outCubic(ramp(t, 0f, FORM));
        float collapse = ramp(t, 5.0f, 2.0f);
        float colHeight = h * 0.72f * form * (1f - collapse * 0.55f);
        float baseRadius = w * 0.035f * form * (1f - collapse * 0.85f);

        Draw2D.gradientV(g, 0f, h * 0.72f, w, h, Draw2D.argb(0f, 0x1A1408),
                Draw2D.argb(0.45f * fade, 0x1A1408));

        // Dust column silhouette.
        int steps = 22;
        for (int i = 0; i < steps; i++) {
            float u = i / (float) (steps - 1);
            float y = ground - u * colHeight;
            float r = baseRadius * (0.35f + u * 1.5f) * (0.9f + 0.1f * wave(t, 3f, i));
            Draw2D.ellipse(g, cx + wave(t, 1.4f, i) * r * 0.2f, y, r, r * 0.28f,
                    Draw2D.argb(0.055f * fade, 0xD8C8A8), Draw2D.argb(0f, 0xD8C8A8));
        }

        // Bills: back half first, then front, so the helix reads in 2.5D.
        for (int pass = 0; pass < 2; pass++) {
            for (Flake f : this.flakes) {
                float hgt = 0.5f + 0.5f * (float) Math.sin(t * 1.3f + f.seed * 6.283f);
                float y = ground - hgt * colHeight;
                float radius = baseRadius * (0.4f + hgt * 1.5f);
                float ang = f.seed * 6.283f + t * f.speed;
                float depth = (float) Math.sin(ang);
                if ((pass == 0) != (depth < 0f)) {
                    continue;
                }
                float x = cx + (float) Math.cos(ang) * radius;
                float alpha = (0.55f + 0.4f * (depth * 0.5f + 0.5f)) * fade * form;
                float size = f.size * (0.6f + 0.6f * (depth * 0.5f + 0.5f));
                if (look() == 1) {
                    coin(g, x, y, size * 0.5f, ang * 1.4f, alpha, f.rgb);
                } else if (look() == 2) {
                    Draw2D.rotatedRect(g, x, y, size * 0.7f, size * 0.7f, ang,
                            Draw2D.argb(alpha * 0.8f, f.rgb));
                } else {
                    bill(g, x, y, size * 1.1f, ang * 1.3f + f.tilt, alpha, f.rgb);
                }
            }
        }

        // Funnel base dust ring.
        Draw2D.ringEllipse(g, cx, ground, baseRadius * 2.2f, baseRadius * 0.55f,
                Math.max(1.5f, baseRadius * 0.12f), 0.35f * fade * form, 0xD8C8A8);

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected String tagline() {
        return "Whirlwind of cash";
    }
}
