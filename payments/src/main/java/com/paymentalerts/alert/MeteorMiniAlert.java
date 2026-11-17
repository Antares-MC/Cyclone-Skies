package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Mini meteors: the cheap little brother. Reddening sky, nine staggered meteors on short
 * arcs, one bolide splitting into fragments, and small craters that glow and die.
 */
final class MeteorMiniAlert extends StageBase {
    static final String[] VARIATIONS = {"Shower", "Bolide", "Impact"};

    private static final float END = 4.8f;
    private static final int MAX_ROCKS = 26;
    private static final int MAX_CRATERS = 14;

    private final Rock[] rocks = new Rock[MAX_ROCKS];
    private final Crater[] craters = new Crater[MAX_CRATERS];
    private final float[] schedule;
    private final float[] scheduleX;
    private int cursor;
    private boolean bolideDone;
    private int craterCursor;

    private static final class Rock {
        float x, y, vx, vy, size, life;
        int rgb;
        boolean live;
        boolean fragment;
    }

    private static final class Crater {
        float x, y, size, life;
        int rgb;
        boolean live;
    }

    MeteorMiniAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        int n = look() == 2 ? 6 : (look() == 1 ? 7 : 9);
        this.schedule = new float[n];
        this.scheduleX = new float[n];
        for (int i = 0; i < n; i++) {
            this.schedule[i] = 0.5f + i * (look() == 2 ? 0.32f : 0.28f);
            this.scheduleX[i] = rnd(0.35f, 0.95f);
        }
        for (int i = 0; i < this.rocks.length; i++) {
            this.rocks[i] = new Rock();
        }
        for (int i = 0; i < this.craters.length; i++) {
            this.craters[i] = new Crater();
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
        this.cues.sound(0.1f, AlertSounds.METEOR_RUMBLE, 0.5f, 0.9f)
                .sound(0.45f, AlertSounds.METEOR_FIRE, 0.6f, 1.0f)
                .sound(2.0f, AlertSounds.METEOR_BIG, 0.7f, 1.0f)
                .sound(3.2f, AlertSounds.METEOR_RUMBLE, 0.35f, 0.8f);
        for (int i = 0; i < this.schedule.length; i++) {
            this.cues.sound(this.schedule[i] - 0.05f, AlertSounds.METEOR_WHOOSH, 0.5f,
                    0.9f + (i % 4) * 0.08f);
        }
    }

    @Override
    protected void advance(float t) {
        while (this.cursor < this.schedule.length && t >= this.schedule[this.cursor]) {
            spawnRock(this.scheduleX[this.cursor] * this.w, false,
                    look() == 1 ? rnd(8f, 14f) : rnd(4f, 9f));
            this.cursor++;
        }
        if (look() == 1 && !this.bolideDone && t >= 2.0f) {
            this.bolideDone = true;
            float bx = this.w * 0.62f;
            float by = this.h * 0.28f;
            for (int i = 0; i < 8; i++) {
                float ang = i * 6.283f / 8f + rnd(-0.2f, 0.2f);
                Rock r = next();
                r.x = bx;
                r.y = by;
                r.vx = (float) Math.cos(ang) * rnd(160f, 300f) - 90f;
                r.vy = (float) Math.sin(ang) * rnd(120f, 260f) + 120f;
                r.size = rnd(2.5f, 5f);
                r.life = 1.2f;
                r.rgb = 0xFFB060;
                r.live = true;
                r.fragment = true;
            }
            ScreenShake.add(0f, 0.25f, 0.18f);
        }
        for (Rock r : this.rocks) {
            if (!r.live) {
                continue;
            }
            r.vy += 90f * this.dt;
            r.x += r.vx * this.dt;
            r.y += r.vy * this.dt;
            r.life -= this.dt;
            if (r.life <= 0f || r.y > this.h * 0.9f) {
                r.live = false;
                if (r.y > this.h * 0.6f) {
                    Crater c = this.craters[this.craterCursor];
                    this.craterCursor = (this.craterCursor + 1) % this.craters.length;
                    c.x = r.x;
                    c.y = Math.min(this.h * 0.9f, r.y);
                    c.size = r.size * 4.2f;
                    c.life = 1.6f;
                    c.rgb = r.rgb;
                    c.live = true;
                    ScreenShake.add(0f, 0.18f, 0.12f);
                    for (int i = 0; i < 5; i++) {
                        Particles2D.P p = spawn(Particles2D.Kind.SPARK, r.x, c.y,
                                rnd(-110f, 110f), rnd(-180f, -30f), rnd(0.35f, 0.8f),
                                rnd(1f, 2.2f), 0xFFB060, 0x301810);
                        p.gravity = 300f;
                    }
                }
            }
        }
        for (Crater c : this.craters) {
            if (c.live) {
                c.life -= this.dt;
                if (c.life <= 0f) {
                    c.live = false;
                }
            }
        }
    }

    private void spawnRock(float x0, boolean fragment, float size) {
        Rock r = next();
        r.x = x0;
        r.y = -this.h * 0.08f;
        r.vx = rnd(-260f, -120f);
        r.vy = rnd(420f, 720f);
        r.size = size;
        r.life = 1.6f;
        r.rgb = chance(0.4f) ? 0xFFD080 : 0xFF8A3A;
        r.live = true;
        r.fragment = fragment;
    }

    private Rock next() {
        for (int i = 0; i < this.rocks.length; i++) {
            Rock r = this.rocks[(this.cursor * 3 + i) % this.rocks.length];
            if (!r.live) {
                return r;
            }
        }
        Rock r = this.rocks[this.rng.nextInt(this.rocks.length)];
        r.live = false;
        return r;
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.7f);
        float red = ramp(t, 0f, 1.2f) * (0.7f + 0.3f * rampDown(t, 2.6f, 1.8f));
        Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.75f * fade, 0x120607));
        Draw2D.gradientV(g, 0f, h * 0.45f, w, h, Draw2D.argb(0f, 0x3A0E08),
                Draw2D.argb(0.55f * red * fade, 0x3A0E08));
        Draw2D.gradientV(g, 0f, 0f, w, h * 0.4f, Draw2D.argb(0.45f * red * fade, 0x8A2410),
                Draw2D.argb(0f, 0x8A2410));

        for (Crater c : this.craters) {
            if (!c.live) {
                continue;
            }
            float a = Easing.clamp01(c.life / 1.6f);
            Draw2D.ellipse(g, c.x, c.y, c.size, c.size * 0.24f,
                    Draw2D.argb(0.5f * a * fade, 0x1A0806), Draw2D.argb(0f, 0x1A0806));
            Draw2D.ellipse(g, c.x, c.y - c.size * 0.1f, c.size * 0.6f, c.size * 0.12f,
                    Draw2D.argb(0.35f * a * fade, c.rgb), Draw2D.argb(0f, c.rgb));
            Draw2D.glow(g, c.x, c.y - c.size * 0.1f, c.size * 1.2f, 0.16f * a * fade, c.rgb);
        }

        for (Rock r : this.rocks) {
            if (!r.live) {
                continue;
            }
            float len = r.fragment ? 0.05f : 0.075f;
            Draw2D.streak(g, r.x - r.vx * len, r.y - r.vy * len, r.x, r.y,
                    r.size * 1.8f, 0.7f * fade, r.rgb);
            Draw2D.streak(g, r.x - r.vx * 0.025f, r.y - r.vy * 0.025f, r.x, r.y,
                    r.size * 2.8f, 0.28f * fade, 0xFFE0A0);
            Draw2D.circle(g, r.x, r.y, r.size, Draw2D.argb(fade, 0xFFF2D0), Draw2D.argb(fade, r.rgb));
            if (!r.fragment) {
                Draw2D.glow(g, r.x, r.y, r.size * 4f, 0.25f * fade, r.rgb);
            }
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected String tagline() {
        return "Incoming!";
    }
}
