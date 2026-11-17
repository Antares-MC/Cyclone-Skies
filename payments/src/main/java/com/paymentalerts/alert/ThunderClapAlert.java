package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Thunder clap: one enormous strike, drawn as a real three-pass bolt with branches,
 * a desaturated beat before it lands, a ground ring, and two re-flickers.
 */
final class ThunderClapAlert extends StageBase {
    static final String[] VARIATIONS = {"Single", "Rolling", "God storm"};

    private static final float STRIKE = 0.35f;
    private static final float END = 6.6f;
    private static final int SEGMENTS = 17;

    private final float[] jag = new float[SEGMENTS];
    private final float[] branchDir = new float[4];
    private final float[] branchAt = new float[4];
    private final float[] strikes;
    private final float[] strikeShift;
    private float rainTimer;

    ThunderClapAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.jag.length; i++) {
            this.jag[i] = rnd(-1f, 1f);
        }
        for (int i = 0; i < this.branchAt.length; i++) {
            this.branchAt[i] = rnd(0.15f, 0.8f);
            this.branchDir[i] = chance(0.5f) ? rnd(0.5f, 1.1f) : rnd(-1.1f, -0.5f);
        }
        switch (look()) {
            case 1 -> {
                this.strikes = new float[]{STRIKE, 1.22f, 2.05f};
                this.strikeShift = new float[]{0f, -0.16f, 0.12f};
            }
            case 2 -> {
                this.strikes = new float[]{STRIKE, 0.95f, 1.5f, 2.2f};
                this.strikeShift = new float[]{0f, 0.09f, -0.13f, 0.05f};
            }
            default -> {
                this.strikes = new float[]{STRIKE};
                this.strikeShift = new float[]{0f};
            }
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
        this.cues.sound(0.05f, AlertSounds.STORM_RAIN, 0.5f, 0.9f)
                .sound(0.1f, AlertSounds.BLACKHOLE_CHARGE, 0.5f, 1.4f);
        for (float at : this.strikes) {
            this.cues.sound(at, AlertSounds.NUKE_THUNDER, 0.95f, 0.85f)
                    .sound(at + 0.05f, AlertSounds.STORM_BIG, 0.9f, 1.0f)
                    .sound(at + 0.55f, AlertSounds.NUKE_RUMBLE, 0.6f, 0.9f);
        }
        this.cues.sound(2.6f, AlertSounds.STORM_THUNDER, 0.5f, 0.8f)
                .sound(3.4f, AlertSounds.NUKE_RUMBLE, 0.4f, 0.7f);
        ScreenShake.add(STRIKE, 0.5f, 0.85f);
        ScreenShake.addRising(0.05f, 0.3f, 0.1f);
    }

    @Override
    protected void advance(float t) {
        if (t > 0.55f && t < 4.8f) {
            this.rainTimer += this.dt;
            while (this.rainTimer > 0.012f) {
                this.rainTimer -= 0.012f;
                float x = rnd(-40f, this.w + 40f);
                Particles2D.P p = spawn(Particles2D.Kind.STREAK, x, rnd(-40f, -5f),
                        -70f, rnd(900f, 1500f), rnd(0.55f, 1.0f), rnd(1.2f, 2.6f),
                        0x9FB6C6, 0x4A5C6A);
                p.gravity = 120f;
                p.drag = 0.02f;
            }
        }
    }

    private float pulse(float t, float at, float decay) {
        if (t < at) {
            return 0f;
        }
        return Math.max(0f, 1f - (t - at) / decay);
    }

    private float boltAlpha(float t) {
        float a = 0f;
        for (float at : this.strikes) {
            a = Math.max(a, pulse(t, at, 0.4f));
        }
        return a;
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.9f);
        float horizon = h * 0.74f;
        float cx = w * 0.5f;

        // Storm sky: darker before the strike, still heavy afterwards.
        float dark = 0.45f * ramp(t, 0f, 0.3f) * (0.75f + 0.25f * rampDown(t, 2.0f, 2.4f));
        Draw2D.gradientV(g, 0f, 0f, w, h, Draw2D.argb(dark * fade, 0x0B111C),
                Draw2D.argb(0f, 0x0B111C));
        float pre = ramp(t, 0f, 0.32f) * (1f - ramp(t, STRIKE, 0.1f));
        if (pre > 0f) {
            Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(pre * 0.18f * fade, 0x8090A0));
        }

        if (look() == 2) {
            cloudDeck(g, w, h, t, fade);
        }

        float flash = boltAlpha(t) * fade;
        if (flash > 0.02f) {
            Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(flash * 0.16f, 0xC8D8FF));
        }
        for (int s = 0; s < this.strikes.length; s++) {
            float at = this.strikes[s];
            float grow = Easing.outCubic(ramp(t, at - 0.2f, 0.2f));
            float a = Math.max(0f, 1f - ramp(t, at, 0.45f)) * fade;
            if (a <= 0.01f || grow <= 0f) {
                continue;
            }
            drawBolt(g, cx + this.strikeShift[s] * w, -20f, horizon, grow, a, h);
        }

        // Ground ring at each strike.
        for (float at : this.strikes) {
            float p = ramp(t, at, 0.9f);
            if (p > 0f && p < 1f) {
                float r = w * 0.05f + Easing.outCubic(p) * w * 0.45f;
                Draw2D.ringEllipse(g, cx, horizon, r, r * 0.12f,
                        Math.max(1f, 7f * (1f - p)), (1f - p) * 0.4f * fade, 0xBFD4FF);
            }
        }

        // Horizon haze.
        Draw2D.gradientV(g, 0f, horizon - h * 0.1f, w, horizon + h * 0.12f,
                Draw2D.argb(0f, 0x1A2430), Draw2D.argb(0.5f * fade, 0x1A2430));

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    private void cloudDeck(GuiGraphics g, int w, int h, float t, float fade) {
        for (int i = 0; i < 16; i++) {
            float seed = i / 16f;
            float x = ((seed * 1.4f + t * 0.012f) % 1.4f - 0.2f) * w;
            float y = h * (0.02f + seed * 0.16f) + wave(t, 0.3f, i) * h * 0.01f;
            Draw2D.ellipse(g, x, y, w * (0.10f + seed * 0.09f), h * (0.03f + seed * 0.025f),
                    Draw2D.argb(0.32f * fade, 0x2A3340), Draw2D.argb(0.04f * fade, 0x2A3340));
        }
    }

    private void drawBolt(GuiGraphics g, float cx, float top, float bottom, float grow, float alpha, int h) {
        float prevX = cx + this.jag[0] * 8f;
        float prevY = top;
        float usable = (bottom - top) * grow;
        int count = 0;
        for (int i = 1; i < SEGMENTS; i++) {
            float ny = top + usable * (i / (float) (SEGMENTS - 1));
            if (ny <= prevY) {
                continue;
            }
            float nx = cx + this.jag[i] * this.w * 0.085f;
            // three passes: wide glow, mid, white core
            Draw2D.softLine(g, prevX, prevY, nx, ny, 22f, alpha * 0.10f, 0x8FB4FF);
            Draw2D.softLine(g, prevX, prevY, nx, ny, 11f, alpha * 0.28f, 0xC8DCFF);
            Draw2D.softLine(g, prevX, prevY, nx, ny, 3.2f, alpha * 0.95f, 0xFFFFFF);
            if (count < this.branchAt.length && i / (float) SEGMENTS > this.branchAt[count]) {
                float len = h * 0.10f + this.rng.nextFloat() * h * 0.07f;
                float bx = nx + this.branchDir[count] * len;
                float by = ny + len * 0.55f;
                Draw2D.softLine(g, nx, ny, (nx + bx) * 0.5f + this.jag[i] * 12f, (ny + by) * 0.5f,
                        7f, alpha * 0.22f, 0xB8D0FF);
                Draw2D.softLine(g, nx, ny, bx, by, 1.6f, alpha * 0.7f, 0xFFFFFF);
                count++;
            }
            prevX = nx;
            prevY = ny;
        }
    }

    @Override
    protected String tagline() {
        return "BOOM";
    }
}
