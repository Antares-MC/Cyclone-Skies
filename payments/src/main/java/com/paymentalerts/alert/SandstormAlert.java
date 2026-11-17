package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Sandstorm: visibility is the mechanic. A dust wall advances from the right in stacked
 * scrolling bands, the screen hazes over, sand rips across at three depths, tumbleweeds
 * cross, and the wall exits left.
 */
final class SandstormAlert extends StageBase {
    static final String[] VARIATIONS = {"Desert", "Dust devil", "Haboob"};

    private static final float END = 7.2f;

    private final Tumble[] tumble = new Tumble[4];
    private float streakTimer;

    private static final class Tumble {
        float x, y, rot, spin, speed, size, depth;
    }

    SandstormAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.tumble.length; i++) {
            Tumble t = new Tumble();
            t.x = rnd(0f, 1920f);
            t.y = rnd(0.55f, 0.88f);
            t.rot = rnd(0f, 6.283f);
            t.spin = rnd(-7f, 7f);
            t.speed = rnd(120f, 320f);
            t.size = rnd(14f, 32f);
            t.depth = rnd(0.6f, 1.2f);
            this.tumble[i] = t;
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
        this.cues.sound(0.2f, AlertSounds.STORM_RAIN, 0.6f, 0.7f)
                .sound(1.4f, AlertSounds.NUKE_RUMBLE, 0.5f, 0.8f)
                .sound(2.0f, AlertSounds.NUKE_RUMBLE, 0.7f, 0.7f)
                .sound(3.6f, AlertSounds.METEOR_RUMBLE, 0.5f, 0.9f)
                .sound(5.5f, AlertSounds.METEOR_RUMBLE, 0.4f, 0.8f)
                .sound(6.6f, AlertSounds.STORM_RAIN, 0.35f, 1.0f);
    }

    @Override
    protected void advance(float t) {
        float haze = ramp(t, 0.3f, 1.6f) * (1f - ramp(t, 6.2f, 0.8f));
        for (Tumble tu : this.tumble) {
            tu.x -= tu.speed * tu.depth * this.dt;
            tu.rot += tu.spin * this.dt;
            tu.y += wave(t, 1.3f, tu.rot) * 8f * this.dt;
            if (tu.x < -60f) {
                tu.x = this.w + 60f;
                tu.y = rnd(0.55f, 0.88f) * this.h;
            }
        }
        if (haze > 0.15f) {
            this.streakTimer += this.dt;
            while (this.streakTimer > 0.008f) {
                this.streakTimer -= 0.008f;
                float depth = rnd(0.5f, 1.5f);
                Particles2D.P p = spawn(Particles2D.Kind.STREAK,
                        this.w + 40f, rnd(0f, this.h),
                        -rnd(420f, 1300f) * depth, rnd(-30f, 30f),
                        rnd(0.4f, 0.9f), rnd(1f, 2.6f) * depth,
                        0xD8B878, 0x5A4020);
                p.drag = 0.02f;
            }
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.8f);
        float haze = ramp(t, 0.3f, 1.6f) * (1f - ramp(t, 6.2f, 0.8f));
        boolean devil = look() == 1;
        boolean haboob = look() == 2;

        Draw2D.gradientV(g, 0f, 0f, w, h, Draw2D.argb(fade, 0x241A10),
                Draw2D.argb(fade, 0x6A4A28));
        Draw2D.glow(g, w * 0.75f, h * 0.35f, w * 0.5f, 0.16f * fade, 0xC89A58);

        float wallIn = Easing.outCubic(ramp(t, 0f, haboob ? 1.5f : 2.0f));
        float wallOut = Easing.inCubic(ramp(t, 5.1f, 1.8f));
        float wallX = w * 1.15f - (w * 0.97f) * wallIn - w * 0.55f * wallOut;

        // The wall itself: four stacked scrolling bands of billows.
        for (int band = 0; band < 4; band++) {
            float bandY = h * (0.10f + band * 0.20f);
            float speed = 0.22f + band * 0.09f;
            float scroll = (t * speed * 260f) % 220f;
            for (int i = -3; i < 14; i++) {
                float bx = wallX + i * 160f - scroll;
                float by = bandY + wave(t, 1.4f, i + band) * h * 0.02f;
                float r = 190f + (i % 3) * 40f;
                float a = (0.10f + 0.03f * band) * (0.6f + wallIn * 0.6f) * fade;
                Draw2D.ellipse(g, bx, by, r, r * 0.72f,
                        Draw2D.argb(a, 0xC8A060), Draw2D.argb(0f, 0xC8A060));
            }
            Draw2D.gradientH(g, wallX - 120f, bandY - h * 0.11f, wallX + w * 0.8f, bandY + h * 0.11f,
                    Draw2D.argb(0.30f * fade * wallIn, 0xB88A48),
                    Draw2D.argb(0f, 0xB88A48));
        }
        Draw2D.gradientH(g, wallX - 300f, 0f, wallX + 60f, h,
                Draw2D.argb(0f, 0xD8B878), Draw2D.argb(0.55f * wallIn * fade, 0xD8B878));
        Draw2D.softLine(g, wallX, 0f, wallX, h, 5f, 0.4f * wallIn * fade, 0xF0D8A0);

        // Dust devil / funnel inside the haze.
        if (devil) {
            float cx = w * 0.42f + wave(t, 0.4f, 1f) * w * 0.06f;
            for (int i = 0; i < 30; i++) {
                float u = i / 30f;
                float y = h * 0.86f - u * h * 0.62f;
                float r = w * (0.035f + u * 0.055f);
                float ang = u * 14f + t * 3.2f;
                Draw2D.circle(g, cx + (float) Math.cos(ang) * r, y, 3f + u * 4f,
                        Draw2D.argb(0.16f * fade, 0xE8C888), Draw2D.argb(0f, 0xE8C888));
            }
        }

        if (haze > 0.01f) {
            Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.30f * haze * fade, 0x8A5E30));
            Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.10f * haze * fade, 0x1A1208));
        }

        for (Tumble tu : this.tumble) {
            drawTumble(g, tu, w, h, fade);
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    private void drawTumble(GuiGraphics g, Tumble tu, int w, int h, float fade) {
        float x = tu.x / 1920f * w;
        float y = tu.y / 1080f * h;
        float r = tu.size * (w / 1920f + 0.3f) * tu.depth;
        float a = 0.5f * fade * tu.depth;
        Draw2D.circle(g, x, y, r, Draw2D.argb(a * 0.5f, 0x6A4A24), Draw2D.argb(a * 0.3f, 0x3A2610));
        for (int i = 0; i < 7; i++) {
            float ang = tu.rot + i * 6.283f / 7f;
            Draw2D.softLine(g, x, y, x + (float) Math.cos(ang) * r, y + (float) Math.sin(ang) * r * 0.8f,
                    Math.max(1f, r * 0.08f), a * 0.7f, 0xB89458);
        }
        Draw2D.circle(g, x, y, r * 0.25f, Draw2D.argb(a * 0.6f, 0x8A6A38), Draw2D.argb(0f, 0x8A6A38));
    }

    @Override
    protected String tagline() {
        return "Visibility zero";
    }
}
