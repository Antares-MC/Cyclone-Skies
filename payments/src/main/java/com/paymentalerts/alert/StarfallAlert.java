package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import java.util.Locale;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Starfall: night sky first, then the fall. One bright star sheds meteors with long tails
 * that leave ground glows, and the field ends on the payer's initial as a constellation.
 */
final class StarfallAlert extends StageBase {
    static final String[] VARIATIONS = {"Night", "Dawn", "Galaxy"};

    private static final float IGNITE = 1.0f;
    private static final float END = 7.6f;
    private static final int STARS = 170;
    private static final int MAX_METEORS = 56;
    private static final int MAX_GLOWS = 30;

    private static final String[] GLYPHS = {
            "010101111101101", "110101110101110", "011100100100011", "110101101101110",
            "111100110100111", "111100110100100", "011100101101011", "101101111101101",
            "111010010010111", "001001001101010", "101101110101101", "100100100100111",
            "101111111101101", "101111111111101", "010101101101010", "110101110100100",
            "010101101110011", "110101110101101", "011100010001110", "111010010010010",
            "101101101101111", "101101101101010", "101101111111101", "101101010101101",
            "101101010010010", "111001010100111",
    };

    private final float[] sx = new float[STARS];
    private final float[] sy = new float[STARS];
    private final float[] sb = new float[STARS];
    private final Meteor[] meteors = new Meteor[MAX_METEORS];
    private final Glow[] glows = new Glow[MAX_GLOWS];
    private int meteorCursor;
    private int glowCursor;
    private float spawnTimer;

    private static final class Meteor {
        float x, y, vx, vy, size, life;
        int rgb;
        boolean live;
    }

    private static final class Glow {
        float x, y, size, life;
        int rgb;
        boolean live;
    }

    StarfallAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        boolean galaxy = look() == 2;
        for (int i = 0; i < STARS; i++) {
            if (galaxy) {
                float r = (float) Math.sqrt(i / (float) STARS) * 0.75f;
                float ang = i * 2.399f;
                this.sx[i] = 0.5f + (float) Math.cos(ang) * r * 1.1f;
                this.sy[i] = 0.36f + (float) Math.sin(ang) * r * 0.7f;
            } else {
                this.sx[i] = rnd(0f, 1f);
                this.sy[i] = rnd(0f, 0.8f);
            }
            this.sb[i] = rnd(0f, 6.283f);
        }
        for (int i = 0; i < this.meteors.length; i++) {
            this.meteors[i] = new Meteor();
        }
        for (int i = 0; i < this.glows.length; i++) {
            this.glows[i] = new Glow();
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
        this.cues.sound(0.15f, AlertSounds.BLACKHOLE_CHARGE, 0.5f, 1.2f)
                .sound(IGNITE, AlertSounds.JACKPOT_CHIME, 0.8f, 1.0f)
                .sound(IGNITE + 0.3f, AlertSounds.METEOR_WHOOSH, 0.5f, 1.0f)
                .sound(2.4f, AlertSounds.METEOR_WHOOSH, 0.4f, 0.9f)
                .sound(3.4f, AlertSounds.METEOR_WHOOSH, 0.4f, 1.1f)
                .sound(4.2f, AlertSounds.METEOR_IMPACT, 0.4f, 1.0f)
                .sound(5.2f, AlertSounds.BLACKHOLE_SPARKLE, 0.5f, 1.1f)
                .sound(6.4f, AlertSounds.NOVA_SHIMMER, 0.4f, 0.9f);
    }

    @Override
    protected void advance(float t) {
        if (t > IGNITE && t < 5.0f) {
            this.spawnTimer += this.dt;
            while (this.spawnTimer > 0.055f) {
                this.spawnTimer -= 0.055f;
                spawnMeteor(t);
            }
        }
        for (Meteor m : this.meteors) {
            if (!m.live) {
                continue;
            }
            m.x += m.vx * this.dt;
            m.y += m.vy * this.dt;
            m.life -= this.dt;
            if (m.y > this.h * 0.88f || m.life <= 0f) {
                m.live = false;
                if (m.y > this.h * 0.7f) {
                    Glow gr = this.glows[this.glowCursor];
                    this.glowCursor = (this.glowCursor + 1) % this.glows.length;
                    gr.x = m.x;
                    gr.y = this.h * 0.88f;
                    gr.size = m.size * 3.4f;
                    gr.life = 1.8f;
                    gr.rgb = m.rgb;
                    gr.live = true;
                    for (int i = 0; i < 6; i++) {
                        Particles2D.P p = spawn(Particles2D.Kind.SPARK, m.x, gr.y,
                                rnd(-90f, 90f), rnd(-180f, -40f), rnd(0.4f, 0.9f),
                                rnd(1f, 2.4f), m.rgb, 0x332211);
                        p.gravity = 240f;
                    }
                }
            }
        }
        for (Glow gr : this.glows) {
            if (gr.live) {
                gr.life -= this.dt;
                if (gr.life <= 0f) {
                    gr.live = false;
                }
            }
        }
    }

    private void spawnMeteor(float t) {
        Meteor m = this.meteors[this.meteorCursor];
        this.meteorCursor = (this.meteorCursor + 1) % this.meteors.length;
        float speed = rnd(520f, 900f);
        float ang = rnd(1.02f, 1.32f);
        m.x = rnd(this.w * 0.35f, this.w * 1.1f);
        m.y = rnd(-this.h * 0.15f, this.h * 0.25f);
        m.vx = -(float) Math.cos(ang) * speed;
        m.vy = (float) Math.sin(ang) * speed;
        m.size = rnd(2.5f, 6f);
        m.life = 2.2f;
        m.rgb = look() == 1 ? 0xFFE0A8 : (chance(0.3f) ? 0xC8B0FF : 0xFFFFFF);
        m.live = true;
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.9f);
        float dark = ramp(t, 0f, 1.0f);
        int top = look() == 2 ? 0x0B0618 : (look() == 1 ? 0x2A2038 : 0x050A18);
        int horizon = look() == 1 ? 0x8A5A4A : (look() == 2 ? 0x1A0B30 : 0x0A1430);
        Draw2D.gradientV(g, 0f, 0f, w, h * 0.92f, Draw2D.argb(0.92f * dark * fade, top),
                Draw2D.argb(0.75f * dark * fade, horizon));
        if (look() == 1) {
            Draw2D.gradientV(g, 0f, h * 0.55f, w, h * 0.9f, Draw2D.argb(0f, 0xE8A070),
                    Draw2D.argb(0.28f * dark * fade, 0xE8A070));
        }

        for (int i = 0; i < STARS; i++) {
            float tw = 0.35f + 0.65f * (0.5f + 0.5f * (float) Math.sin(t * (1.1f + (i % 5) * 0.3f) + this.sb[i]));
            float a = tw * 0.8f * dark * fade;
            Draw2D.circle(g, this.sx[i] * w, this.sy[i] * h, 0.9f + (i % 3) * 0.5f,
                    Draw2D.argb(a, 0xFFFFFF), Draw2D.argb(a * 0.4f, 0xB8C8FF));
        }

        float heroX = w * 0.64f;
        float heroY = h * 0.16f;
        float heroPulse = 0.5f + 0.5f * (float) Math.sin(t * 3.2f);
        float heroDim = 1f - ramp(t, IGNITE + 0.2f, 1.2f) * 0.6f;
        star4(g, heroX, heroY, (8f + heroPulse * 5f) * (w / 1920f + 0.35f), (0.7f + heroPulse * 0.3f) * heroDim * fade, 0xFFFFFF);
        Draw2D.glow(g, heroX, heroY, w * 0.035f, 0.25f * heroPulse * heroDim * fade, 0xA8C8FF);

        for (Glow gr : this.glows) {
            if (!gr.live) {
                continue;
            }
            float a = Easing.clamp01(gr.life / 1.8f);
            Draw2D.ellipse(g, gr.x, gr.y, gr.size, gr.size * 0.22f,
                    Draw2D.argb(0.28f * a * fade, gr.rgb), Draw2D.argb(0f, gr.rgb));
            Draw2D.glow(g, gr.x, gr.y - gr.size * 0.1f, gr.size * 0.9f, 0.16f * a * fade, gr.rgb);
        }

        for (Meteor m : this.meteors) {
            if (!m.live) {
                continue;
            }
            float tail = 0.09f;
            float tx = m.x - m.vx * tail;
            float ty = m.y - m.vy * tail;
            Draw2D.streak(g, tx, ty, m.x, m.y, m.size * 1.6f, 0.75f * fade, m.rgb);
            Draw2D.streak(g, m.x - m.vx * 0.02f, m.y - m.vy * 0.02f, m.x, m.y,
                    m.size * 2.6f, 0.3f * fade, m.rgb);
            Draw2D.glow(g, m.x, m.y, m.size * 3.2f, 0.5f * fade, m.rgb);
            Draw2D.circle(g, m.x, m.y, m.size * 0.7f, Draw2D.argb(fade, 0xFFFFFF),
                    Draw2D.argb(fade, m.rgb));
        }

        float con = ramp(t, 5.0f, 1.4f);
        if (con > 0f) {
            constellation(g, w * 0.5f, h * 0.30f, Math.min(w, h) * 0.022f, con * fade, 0xFFF2C8);
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    private void constellation(GuiGraphics g, float cx, float cy, float size, float alpha, int rgb) {
        String name = this.payment.name().toUpperCase(Locale.ROOT);
        char ch = name.isEmpty() ? 'A' : name.charAt(0);
        String bits = ch >= 'A' && ch <= 'Z' ? GLYPHS[ch - 'A'] : GLYPHS[0];
        float x0 = cx - size;
        float y0 = cy - size * 2f;
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 3; col++) {
                if (bits.charAt(row * 3 + col) != '1') {
                    continue;
                }
                float x = x0 + col * size;
                float y = y0 + row * size;
                if (col < 2 && bits.charAt(row * 3 + col + 1) == '1') {
                    Draw2D.softLine(g, x, y, x + size, y, Math.max(1f, size * 0.05f), alpha * 0.35f, rgb);
                }
                if (row < 4 && bits.charAt((row + 1) * 3 + col) == '1') {
                    Draw2D.softLine(g, x, y, x, y + size, Math.max(1f, size * 0.05f), alpha * 0.35f, rgb);
                }
                star4(g, x, y, size * 0.4f, alpha, rgb);
            }
        }
    }

    @Override
    protected String tagline() {
        return "Wish upon a star";
    }
}
