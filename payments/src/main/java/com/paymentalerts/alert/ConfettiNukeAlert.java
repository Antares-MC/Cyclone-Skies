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
 * Confetti nuke: the nuke silhouette with a party payload. A bomb falls on a streamer
 * trail, detonates into a confetti mushroom cloud, and rains confetti while PARTY stamps
 * in one letter at a time.
 */
final class ConfettiNukeAlert extends StageBase {
    static final String[] VARIATIONS = {"Party", "Wedding", "Carnival"};

    private static final float IMPACT = 1.3f;
    private static final float END = 8.2f;

    private static final int[][] PALETTES = {
            {0xFF5FA2, 0x4C6FFF, 0xFFD75E, 0x2FB7A8, 0xE8452E},
            {0xFFFFFF, 0xF7C6D9, 0xE8C8FF, 0xFFF2E0, 0xD8A8C8},
            {0xFF2E9A, 0xFFC23D, 0x2EE6FF, 0x9BFF3D, 0x8A4FFF},
    };

    private float rainTimer;

    ConfettiNukeAlert(Payment payment, long now, int variation) {
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
        this.cues.action(0.0f, () -> AlertSounds.playSliding(AlertSounds.NUKE_WHISTLE,
                        1.3f, 2.0f, 0.7f, 0.25f, 1.0f))
                .sound(IMPACT - 0.1f, AlertSounds.JACKPOT_TICK, 0.6f, 1.5f)
                .sound(IMPACT, AlertSounds.CONFETTI_BOOM, 1.0f, 1.0f)
                .sound(IMPACT, AlertSounds.NUKE_SHOCKWAVE, 0.7f, 1.1f)
                .sound(IMPACT + 0.2f, AlertSounds.CONFETTI_TADA, 0.9f, 1.0f)
                .sound(IMPACT + 0.7f, AlertSounds.CONFETTI_CHEER, 0.8f, 1.0f)
                .sound(2.6f, AlertSounds.CONFETTI_HARP, 0.5f, 1.0f)
                .sound(4.2f, AlertSounds.CONFETTI_TADA, 0.6f, 1.1f);
        ScreenShake.add(IMPACT, 0.7f, 0.8f);
        ScreenShake.addRising(0.2f, 1.1f, 0.1f);
    }

    @Override
    protected void advance(float t) {
        if (t < IMPACT || t > 6.2f) {
            return;
        }
        this.rainTimer += this.dt;
        while (this.rainTimer > 0.02f) {
            this.rainTimer -= 0.02f;
            int[] pal = palette();
            Particles2D.P p = spawn(Particles2D.Kind.CONFETTI, rnd(0f, this.w), rnd(-30f, 0f),
                    rnd(-30f, 30f), rnd(50f, 130f), rnd(2.4f, 4.2f), rnd(2.5f, 5.5f),
                    pal[this.rng.nextInt(pal.length)], 0xFFFFFF);
            p.gravity = 60f;
            p.drag = 0.4f;
            p.twinkle = true;
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.9f);
        float cx = w * 0.5f;
        float ground = h * 0.80f;
        int[] pal = palette();

        Draw2D.gradientV(g, 0f, 0f, w, h, Draw2D.argb(0.30f * fade, 0x1A0B20),
                Draw2D.argb(0f, 0x1A0B20));

        if (t < IMPACT) {
            float drop = Easing.inCubic(ramp(t, 0f, IMPACT));
            float y = -h * 0.1f + (ground - h * 0.1f) * drop;
            float bob = (float) Math.sin(t * 9f) * 3f;
            drawBomb(g, cx + bob, y, Math.min(w, h) * 0.05f, t, fade, pal);
        }

        if (t >= IMPACT) {
            float flash = 1f - ramp(t, IMPACT, 0.4f);
            if (flash > 0f) {
                Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(flash * 0.55f * fade, 0xFFE0F0));
            }
            // Stem and cap silhouettes under the confetti mass.
            float grow = Easing.outCubic(ramp(t, IMPACT, 1.2f));
            float capY = ground - h * 0.42f * grow;
            float capR = w * 0.24f * grow;
            Draw2D.ellipse(g, cx, capY, capR, capR * 0.52f,
                    Draw2D.argb(0.16f * fade, pal[0]), Draw2D.argb(0f, pal[0]));
            Draw2D.quad(g, cx - capR * 0.22f, capY, Draw2D.argb(0.14f * fade, pal[1]),
                    cx - capR * 0.34f, ground, Draw2D.argb(0.06f * fade, pal[1]),
                    cx + capR * 0.34f, ground, Draw2D.argb(0.06f * fade, pal[1]),
                    cx + capR * 0.22f, capY, Draw2D.argb(0.14f * fade, pal[1]));
            for (int i = 0; i < 12; i++) {
                float ang = i * 6.283f / 12f;
                Draw2D.ellipse(g, cx + (float) Math.cos(ang) * capR * 0.7f,
                        capY + (float) Math.sin(ang) * capR * 0.34f,
                        capR * 0.28f, capR * 0.16f,
                        Draw2D.argb(0.13f * fade, pal[i % pal.length]), Draw2D.argb(0f, pal[i % pal.length]));
            }
            // Streamer shockwave.
            for (int i = 0; i < 3; i++) {
                float p = ramp(t, IMPACT + i * 0.1f, 1.3f);
                if (p <= 0f || p >= 1f) {
                    continue;
                }
                float r = w * (0.06f + Easing.outCubic(p) * 0.55f);
                Draw2D.ring(g, cx, ground, r, Math.max(1.5f, 8f * (1f - p)),
                        (1f - p) * 0.35f * fade, pal[i % pal.length]);
            }
            party(g, font, w, h, t, fade, pal);
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    private void drawBomb(GuiGraphics g, float x, float y, float r, float t, float fade, int[] pal) {
        // Streamer trail.
        for (int i = 0; i < 7; i++) {
            float back = i / 7f;
            float px = x + wave(t * 2f, 3.4f, i) * r * 0.5f;
            float py = y - r - back * r * 3.4f;
            Draw2D.softLine(g, px, py, x + wave(t, 2.8f, i + 1) * r * 0.4f, py - r * 0.5f,
                    Math.max(1.2f, r * 0.09f), (1f - back) * 0.6f * fade, pal[i % pal.length]);
        }
        Draw2D.rotatedRect(g, x, y, r * 1.25f, r * 1.75f, 0f, Draw2D.argb(fade, 0x2A2430));
        Draw2D.rotatedRect(g, x, y - r * 0.95f, r * 0.7f, r * 0.45f, 0f, Draw2D.argb(fade, 0x4A4048));
        Draw2D.triangle(g, x - r * 0.62f, y + r * 0.15f, Draw2D.argb(fade, 0x2A2430),
                x - r * 1.35f, y + r * 1.05f, Draw2D.argb(fade, 0x2A2430),
                x - r * 0.5f, y + r * 1.15f, Draw2D.argb(fade, 0x2A2430));
        Draw2D.triangle(g, x + r * 0.62f, y + r * 0.15f, Draw2D.argb(fade, 0x2A2430),
                x + r * 1.35f, y + r * 1.05f, Draw2D.argb(fade, 0x2A2430),
                x + r * 0.5f, y + r * 1.15f, Draw2D.argb(fade, 0x2A2430));
        Draw2D.softLine(g, x, y - r * 0.95f, x + r * 0.35f, y - r * 1.5f,
                Math.max(1.2f, r * 0.1f), fade, 0x8A7A60);
        star4(g, x + r * 0.35f, y - r * 1.5f, r * 0.28f, 0.9f * fade, 0xFFE082);
        Draw2D.glow(g, x + r * 0.35f, y - r * 1.5f, r * 0.7f, 0.4f * fade, 0xFFC23D);
    }

    private void party(GuiGraphics g, Font font, int w, int h, float t, float fade, int[] pal) {
        String word = "PARTY";
        int shown = Math.min(word.length(), (int) (ramp(t, IMPACT + 0.4f, 1.4f) * word.length()) + 1);
        if (shown <= 0) {
            return;
        }
        String part = word.substring(0, shown);
        Component text = TextFx.plain(part, 0xFFFFFF, true);
        float scale = TextFx.fitScale(font, text, w * 0.6f, 5.5f);
        float bob = wave(t, 3.2f, 0f) * 4f;
        TextFx.centered(g, font, text, w * 0.5f, h * 0.20f + bob, scale, fade);
        for (int i = 0; i < shown; i++) {
            float pop = 1f - ramp(t, IMPACT + 0.4f + i * 0.28f, 0.3f);
            if (pop > 0f) {
                Draw2D.glow(g, w * 0.5f + (i - 2f) * w * 0.09f, h * 0.20f, w * 0.05f,
                        0.5f * pop * fade, pal[i % pal.length]);
            }
        }
    }

    @Override
    protected boolean drawsBanner() {
        return false;
    }
}
