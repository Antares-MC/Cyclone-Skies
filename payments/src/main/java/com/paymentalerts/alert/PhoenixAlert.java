package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Phoenix: two acts and a deliberate dead pause. An ember flame gutters out, the frame
 * holds on nothing, then a spark reignites and a bird of fire beats its wings twice and
 * climbs off the top of the screen.
 */
final class PhoenixAlert extends StageBase {
    static final String[] VARIATIONS = {"Fire", "Ash rebirth", "Golden"};

    private static final float DEATH = 1.5f;
    private static final float REBIRTH = 2.2f;
    private static final float FORMED = 3.4f;
    private static final float END = 9.2f;

    private final float[] featherSeed = new float[26];
    private double lastBeat = -1;
    private float emberTimer;

    PhoenixAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.featherSeed.length; i++) {
            this.featherSeed[i] = rnd(0f, 6.283f);
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

    private int fire() {
        return switch (look()) {
            case 1 -> 0x9A9A9A;
            case 2 -> 0xFFD75E;
            default -> 0xFF7A18;
        };
    }

    private int fireHot() {
        return look() == 1 ? 0xD8D8D8 : 0xFFF0B0;
    }

    @Override
    protected void cue() {
        this.cues.sound(0.1f, AlertSounds.METEOR_FIRE, 0.5f, 0.9f)
                .sound(0.8f, AlertSounds.NUKE_RUMBLE, 0.35f, 0.7f)
                .sound(DEATH - 0.1f, AlertSounds.NOVA_COLLAPSE, 0.4f, 1.2f)
                .sound(REBIRTH, AlertSounds.NOVA_CHARGE, 0.7f, 1.0f)
                .sound(FORMED, AlertSounds.NOVA_ROAR, 0.8f, 1.0f)
                .sound(4.6f, AlertSounds.NUKE_THUNDER, 0.5f, 1.1f)
                .sound(5.6f, AlertSounds.NOVA_SHIMMER, 0.5f, 1.0f)
                .sound(6.6f, AlertSounds.METEOR_WHOOSH, 0.5f, 1.0f);
        ScreenShake.add(FORMED, 0.3f, 0.25f);
        ScreenShake.add(4.6f, 0.2f, 0.2f);
    }

    @Override
    protected void advance(float t) {
        if (t > DEATH && t < 7.4f) {
            this.emberTimer += this.dt;
            float rate = t < FORMED ? 0.08f : 0.03f;
            while (this.emberTimer > rate) {
                this.emberTimer -= rate;
                float cx = this.w * 0.5f;
                Particles2D.P p = spawn(Particles2D.Kind.SPARK,
                        cx + rnd(-this.w * 0.06f, this.w * 0.06f),
                        this.h * 0.45f + rnd(-this.h * 0.05f, this.h * 0.05f),
                        rnd(-40f, 40f), rnd(-160f, -40f), rnd(0.8f, 1.8f), rnd(1f, 2.6f),
                        chance(0.4f) ? fireHot() : fire(), 0x3A1408);
                p.gravity = -50f;
                p.drag = 1.1f;
                p.twinkle = true;
            }
        }
        if (t > 4.4f && t < 5.9f) {
            double beat = Math.floor((t - 4.4f) / 0.65f);
            if (beat != this.lastBeat) {
                this.lastBeat = beat;
                float cx = this.w * 0.5f;
                for (int i = 0; i < 26; i++) {
                    float ang = i * 6.283f / 26f;
                    Particles2D.P p = spawn(Particles2D.Kind.SPARK, cx, this.h * 0.42f,
                            (float) Math.cos(ang) * rnd(200f, 460f),
                            (float) Math.sin(ang) * rnd(140f, 320f),
                            rnd(0.7f, 1.6f), rnd(1.4f, 3.4f), fire(), fireHot());
                    p.drag = 0.9f;
                    p.gravity = 160f;
                }
            }
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 1.0f);
        float cx = w * 0.5f;
        float cy = h * 0.52f;
        int fire = fire();
        int hot = fireHot();

        // Ember-dark backdrop.
        float dark = ramp(t, 0f, 1.0f) * (1f - ramp(t, 7.0f, 1.2f) * 0.5f);
        Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.85f * dark * fade, 0x0A0604));
        Draw2D.gradientV(g, 0f, 0f, w, h, Draw2D.argb(0.15f * fade, 0x2A1208),
                Draw2D.argb(0.35f * fade, 0x0A0604));

        // The guttering flame before death.
        if (t < DEATH + 0.05f) {
            float gutter = 1f - ramp(t, DEATH - 0.35f, 0.4f);
            float flick = 0.7f + 0.3f * (float) Math.sin(t * 19f) + 0.1f * (float) Math.sin(t * 47f);
            float fw = w * 0.018f * flick * gutter;
            float fh = h * 0.09f * gutter;
            Draw2D.glow(g, cx, cy, w * 0.07f, 0.3f * gutter * fade, fire);
            Draw2D.quad(g, cx - fw, cy + fh, Draw2D.argb(0.85f * gutter * fade, fire),
                    cx - fw * 0.4f, cy - fh, Draw2D.argb(0.7f * gutter * fade, hot),
                    cx + fw * 0.4f, cy - fh, Draw2D.argb(0.7f * gutter * fade, hot),
                    cx + fw, cy + fh, Draw2D.argb(0.85f * gutter * fade, fire));
        }

        // Ash drift in the dark.
        for (int i = 0; i < 22; i++) {
            float x = (i / 22f + t * 0.02f * (1f + (i % 3) * 0.2f)) % 1f * w;
            float y = (i * 0.137f + t * 0.03f) % 1f * h;
            Draw2D.circle(g, x, y, 1f + (i % 3) * 0.6f,
                    Draw2D.argb(0.25f * fade, 0x8A8078), Draw2D.argb(0f, 0x8A8078));
        }

        // Rebirth spark and bird.
        if (t >= REBIRTH) {
            float form = Easing.outCubic(ramp(t, REBIRTH, FORMED - REBIRTH));
            float climb = Easing.inCubic(ramp(t, 7.2f, 1.8f));
            float birdY = cy + h * 0.12f - climb * h * 0.85f;
            float beatPhase = (t - FORMED) * 2.6f;
            float flap = t < FORMED ? 0.3f : (float) Math.sin(beatPhase) * 0.9f;
            float span = w * (0.05f + 0.16f * form);
            float grow = 0.5f + 0.5f * form;

            // Reignition burst.
            if (t < REBIRTH + 0.5f) {
                float p = ramp(t, REBIRTH, 0.5f);
                Draw2D.ring(g, cx, cy + h * 0.12f, w * (0.02f + p * 0.2f),
                        Math.max(1f, 5f * (1f - p)), (1f - p) * 0.5f * fade, hot);
            }

            // Wings: feather lines fanning from the body.
            for (int side = -1; side <= 1; side += 2) {
                for (int i = 0; i < 9; i++) {
                    float spread = i / 8f;
                    float ang = 1.5708f + side * (0.25f + spread * 0.55f) + side * flap * 0.35f;
                    float tipX = cx + side * span * (0.55f + spread * 0.8f);
                    float tipY = birdY - span * 0.35f * (1f - spread) + flap * span * 0.22f * spread;
                    Draw2D.softLine(g, cx, birdY, tipX, tipY,
                            Math.max(1.2f, h * 0.0035f), (0.4f + 0.4f * (1f - spread)) * form * fade,
                            spread < 0.5f ? hot : fire);
                    Draw2D.softLine(g, cx, birdY, tipX, tipY, Math.max(3f, h * 0.009f),
                            0.18f * form * fade, fire);
                }
            }
            // Body, head, tail.
            Draw2D.ellipse(g, cx, birdY, span * 0.16f, span * 0.26f,
                    Draw2D.argb(0.9f * form * fade, hot), Draw2D.argb(0.9f * form * fade, fire));
            Draw2D.circle(g, cx + span * 0.2f, birdY - span * 0.24f, span * 0.1f,
                    Draw2D.argb(0.9f * form * fade, hot), Draw2D.argb(0.8f * form * fade, fire));
            Draw2D.triangle(g, cx + span * 0.3f, birdY - span * 0.26f,
                    Draw2D.argb(0.9f * form * fade, 0xFFC23D),
                    cx + span * 0.46f, birdY - span * 0.2f, Draw2D.argb(0f, 0xFFC23D),
                    cx + span * 0.3f, birdY - span * 0.16f, Draw2D.argb(0.9f * form * fade, 0xFFC23D));
            for (int i = -1; i <= 1; i++) {
                Draw2D.softLine(g, cx - span * 0.12f, birdY + span * 0.16f,
                        cx - span * 0.1f + i * span * 0.18f, birdY + span * 0.62f,
                        Math.max(1.2f, h * 0.003f), (0.5f + 0.2f * form) * fade, i == 0 ? hot : fire);
            }
            Draw2D.glow(g, cx, birdY, span * 1.3f, 0.25f * form * fade, fire);
        }

        // Silhouette pause: nothing at all between death and rebirth.
        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected String tagline() {
        return "From the ashes";
    }
}
