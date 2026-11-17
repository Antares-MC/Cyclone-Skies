package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Volcano: ground-up eruption. A mountain rises, the vent glows, then a two-layer column
 * (ash shell, lava core) fires straight up with ballistic lava bombs and falling ash.
 */
final class VolcanoAlert extends StageBase {
    static final String[] VARIATIONS = {"Lava", "Ash", "Eruption"};

    private static final float ERUPT = 1.8f;
    private static final float END = 8.2f;
    private static final int MAX_BOMBS = 40;

    private final float[] ridge = {-0.52f, 1.0f, -0.34f, 0.54f, -0.22f, 0.68f, -0.08f, 0.38f,
            0.02f, 0.28f, 0.11f, 0.4f, 0.22f, 0.62f, 0.36f, 0.5f, 0.52f, 1.0f};
    private final Bomb[] bombs = new Bomb[MAX_BOMBS];
    private final float[] ventSpark = new float[8];
    private int bombCursor;
    private float bombTimer;
    private float ashTimer;
    private boolean erupted;

    private static final class Bomb {
        float x, y, vx, vy, size, life;
        int rgb;
        boolean live;
    }

    VolcanoAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.bombs.length; i++) {
            this.bombs[i] = new Bomb();
        }
        for (int i = 0; i < this.ventSpark.length; i++) {
            this.ventSpark[i] = rnd(0f, 6.283f);
        }
        ScreenShake.addRising(0.2f, ERUPT, 0.16f);
        ScreenShake.add(ERUPT, 0.7f, 0.9f);
    }

    @Override
    protected float duration() {
        return END;
    }

    @Override
    protected String[] looks() {
        return VARIATIONS;
    }

    private int lavaColor() {
        return look() == 1 ? 0xC85A20 : (look() == 2 ? 0xFFB030 : 0xFF7A18);
    }

    @Override
    protected void cue() {
        this.cues.sound(0.1f, AlertSounds.NUKE_RUMBLE, 0.6f, 0.7f)
                .sound(1.0f, AlertSounds.METEOR_RUMBLE, 0.5f, 0.8f)
                .sound(ERUPT, AlertSounds.NUKE_BLAST, 0.9f, 1.0f)
                .sound(ERUPT, AlertSounds.METEOR_BIG, 0.8f, 0.9f)
                .sound(2.6f, AlertSounds.NUKE_FALLOUT, 0.6f, 0.7f)
                .sound(4.0f, AlertSounds.METEOR_IMPACT, 0.5f, 0.9f)
                .sound(5.2f, AlertSounds.NUKE_RUMBLE, 0.5f, 0.6f)
                .sound(6.4f, AlertSounds.NUKE_FALLOUT, 0.4f, 0.6f);
    }

    @Override
    protected void advance(float t) {
        if (!this.erupted && t >= ERUPT) {
            this.erupted = true;
            for (int i = 0; i < 28; i++) {
                float ang = -1.5708f + rnd(-0.55f, 0.55f);
                float speed = rnd(300f, 820f);
                Bomb b = this.bombs[this.bombCursor];
                this.bombCursor = (this.bombCursor + 1) % this.bombs.length;
                b.x = this.w * (0.5f + rnd(-0.03f, 0.03f));
                b.y = this.h * 0.58f;
                b.vx = (float) Math.cos(ang) * speed;
                b.vy = (float) Math.sin(ang) * speed;
                b.size = rnd(3f, 9f);
                b.life = 4f;
                b.rgb = lavaColor();
                b.live = true;
            }
        }
        if (t > ERUPT && t < 5.8f) {
            this.bombTimer += this.dt;
            while (this.bombTimer > (look() == 2 ? 0.11f : 0.2f)) {
                this.bombTimer -= (look() == 2 ? 0.11f : 0.2f);
                Bomb b = this.bombs[this.bombCursor];
                this.bombCursor = (this.bombCursor + 1) % this.bombs.length;
                float ang = -1.5708f + rnd(-0.4f, 0.4f);
                float speed = rnd(300f, 700f);
                b.x = this.w * (0.5f + rnd(-0.02f, 0.02f));
                b.y = this.h * 0.56f;
                b.vx = (float) Math.cos(ang) * speed;
                b.vy = (float) Math.sin(ang) * speed;
                b.size = rnd(2.5f, 7f);
                b.life = 4f;
                b.rgb = lavaColor();
                b.live = true;
            }
        }
        for (Bomb b : this.bombs) {
            if (!b.live) {
                continue;
            }
            b.vy += 420f * this.dt;
            b.x += b.vx * this.dt;
            b.y += b.vy * this.dt;
            b.life -= this.dt;
            if (b.life <= 0f || b.y > this.h * 0.97f || b.x < -60f || b.x > this.w + 60f) {
                b.live = false;
                if (b.y > this.h * 0.7f) {
                    for (int i = 0; i < 5; i++) {
                        Particles2D.P p = spawn(Particles2D.Kind.SPARK, b.x, this.h * 0.94f,
                                rnd(-150f, 150f), rnd(-200f, -30f), rnd(0.4f, 0.9f),
                                rnd(1f, 2.4f), b.rgb, 0x2A1008);
                        p.gravity = 360f;
                    }
                }
            }
        }
        if (t > ERUPT + 0.4f && t < 7.2f) {
            this.ashTimer += this.dt;
            while (this.ashTimer > 0.05f) {
                this.ashTimer -= 0.05f;
                Particles2D.P p = spawn(Particles2D.Kind.SMOKE, rnd(0f, this.w), rnd(-20f, 0f),
                        rnd(-30f, 30f), rnd(20f, 70f), rnd(3f, 6f), rnd(1f, 3.2f),
                        0x4A3A32, 0x1A1210);
                p.gravity = 12f;
                p.drag = 0.25f;
            }
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.9f);
        float cx = w * 0.5f;
        float ventY = h * 0.58f;
        float baseY = h * 0.98f;
        int lava = lavaColor();
        float rise = Easing.outCubic(ramp(t, 0f, 1.8f));
        float collapse = ramp(t, 6.0f, 2.0f);

        Draw2D.gradientV(g, 0f, 0f, w, h, Draw2D.argb(fade, 0x140608),
                Draw2D.argb(fade, look() == 1 ? 0x2A2220 : 0x3A1408));

        // Mountain silhouette.
        float mountainW = w * 1.25f;
        float mountainH = h * 0.46f * rise;
        float shake = this.erupted ? wave(t * 20f, 35f, 0f) * 2f * (1f - ramp(t, ERUPT + 1f, 2f)) : 0f;
        for (int i = 0; i + 3 < this.ridge.length; i += 2) {
            float x1 = cx + this.ridge[i] * mountainW * 0.5f;
            float y1 = baseY - (1f - this.ridge[i + 1]) * mountainH + shake;
            float x2 = cx + this.ridge[i + 2] * mountainW * 0.5f;
            float y2 = baseY - (1f - this.ridge[i + 3]) * mountainH + shake;
            Draw2D.quad(g, x1, baseY, Draw2D.argb(fade, 0x120A0A),
                    x1, y1, Draw2D.argb(fade, 0x2A1614),
                    x2, y2, Draw2D.argb(fade, 0x2A1614),
                    x2, baseY, Draw2D.argb(fade, 0x120A0A));
        }
        // Vent glow.
        Draw2D.glow(g, cx, ventY + shake, w * 0.09f, (0.25f + 0.25f * ramp(t, 0.4f, 1.4f)) * fade, lava);
        Draw2D.ellipse(g, cx, ventY + shake, w * 0.035f, h * 0.014f,
                Draw2D.argb(0.9f * fade, lava), Draw2D.argb(0.5f * fade, 0x8A2A08));

        if (t >= ERUPT) {
            // Ash shell.
            float column = (h * 0.62f) * Easing.outCubic(ramp(t, ERUPT, 1.6f)) * (1f - collapse * 0.7f);
            for (int i = 0; i < 24; i++) {
                float u = i / 23f;
                float y = ventY - u * column;
                float r = w * (0.035f + u * 0.10f) * (0.9f + 0.2f * wave(t, 2.2f, i));
                Draw2D.ellipse(g, cx + wave(t, 1.1f, i) * r * 0.25f, y, r, r * 0.6f,
                        Draw2D.argb((0.20f - u * 0.05f) * fade, 0x3A2A24), Draw2D.argb(0f, 0x3A2A24));
            }
            // Lava core.
            Draw2D.gradientV(g, cx - w * 0.02f, ventY - column * 0.75f, cx + w * 0.02f, ventY,
                    Draw2D.argb(0.75f * fade, 0xFFE08A), Draw2D.argb(0.9f * fade, lava));
            Draw2D.gradientV(g, cx - w * 0.045f, ventY - column * 0.6f, cx + w * 0.045f, ventY,
                    Draw2D.argb(0.25f * fade, lava), Draw2D.argb(0.5f * fade, lava));
            // Pressure ring.
            float p = ramp(t, ERUPT, 1.1f);
            if (p > 0f && p < 1f) {
                Draw2D.ringEllipse(g, cx, baseY - h * 0.02f, w * (0.1f + p * 0.7f),
                        h * (0.02f + p * 0.1f), Math.max(1f, 8f * (1f - p)),
                        (1f - p) * 0.4f * fade, 0xD8A060);
            }
            // Glowing ground cracks.
            float vein = ramp(t, ERUPT + 0.6f, 2.0f) * (1f - collapse);
            for (int i = 0; i < 9; i++) {
                float ang = 3.1416f + (i - 4) * 0.22f + wave(t, 0.3f, i) * 0.05f;
                float len = w * (0.1f + (i % 4) * 0.06f) * vein;
                float x = cx + (float) Math.cos(ang) * len;
                float y = baseY - h * 0.02f + (float) Math.sin(ang) * len * 0.12f;
                Draw2D.softLine(g, cx, baseY - h * 0.02f, x, y, Math.max(1.5f, h * 0.003f),
                        vein * 0.7f * fade, lava);
                Draw2D.softLine(g, cx, baseY - h * 0.02f, x, y, Math.max(1f, h * 0.0012f),
                        vein * 0.9f * fade, 0xFFE08A);
            }
        }

        for (Bomb b : this.bombs) {
            if (!b.live) {
                continue;
            }
            Draw2D.circle(g, b.x, b.y, b.size, Draw2D.argb(fade, 0xFFF0B0), Draw2D.argb(fade, b.rgb));
            Draw2D.glow(g, b.x, b.y, b.size * 3f, 0.3f * fade, b.rgb);
            for (int i = 1; i <= 3; i++) {
                Draw2D.circle(g, b.x - b.vx * 0.006f * i, b.y - b.vy * 0.006f * i,
                        b.size * (0.7f - i * 0.12f), Draw2D.argb(0.2f * fade, 0x6A5A50),
                        Draw2D.argb(0f, 0x6A5A50));
            }
        }

        // Ground haze.
        Draw2D.gradientV(g, 0f, baseY - h * 0.12f, w, baseY + h * 0.02f,
                Draw2D.argb(0f, 0x2A1008), Draw2D.argb(0.35f * fade, 0x2A1008));

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected String tagline() {
        return "ERUPTION";
    }
}
