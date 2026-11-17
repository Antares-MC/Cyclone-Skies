package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Blackout: the opposite of a flashbang. Onset is an instant hard cut to black — no fade —
 * then a single spark, a long spotlight hold where only the name is lit, a dying hum, and
 * a return to light that is deliberately too bright for half a second.
 */
final class BlackoutAlert extends StageBase {
    static final String[] VARIATIONS = {"Grid down", "Candle", "Emergency"};

    private static final float CUT = 0.25f;
    private static final float SPARK = 1.15f;
    private static final float RESTORE = 3.9f;
    private static final float END = 6.2f;

    private final float[] dustX = new float[46];
    private final float[] dustY = new float[46];
    private final float[] dustV = new float[46];
    private final float[] dustS = new float[46];
    private final float[] arcSeed = new float[5];

    private float arcAge = 99f;
    private int sparkStage;

    BlackoutAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.dustX.length; i++) {
            this.dustX[i] = rnd(0f, 1f);
            this.dustY[i] = rnd(0f, 1f);
            this.dustV[i] = rnd(2f, 14f);
            this.dustS[i] = rnd(0.7f, 2.2f);
        }
        for (int i = 0; i < this.arcSeed.length; i++) {
            this.arcSeed[i] = rnd(0f, 6.283f);
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
        this.cues.sound(0.0f, AlertSounds.BLACKHOLE_COLLAPSE, 0.4f, 1.4f)
                .sound(CUT, AlertSounds.JACKPOT_STOP, 0.9f, 0.7f)
                .sound(CUT + 0.04f, AlertSounds.LASER_FIRE, 0.5f, 1.8f)
                .sound(SPARK, AlertSounds.JACKPOT_TICK, 0.9f, 1.1f)
                .sound(SPARK + 0.05f, AlertSounds.COIN_PICKUP, 0.6f, 1.5f)
                .sound(2.2f, AlertSounds.MONEY_DING, 0.8f, 0.8f)
                .sound(3.1f, AlertSounds.MONEY_COIN, 0.4f, 0.7f)
                .sound(RESTORE, AlertSounds.NUKE_SHOCKWAVE, 0.7f, 1.1f)
                .sound(RESTORE + 0.02f, AlertSounds.CONFETTI_BOOM, 0.6f, 1.2f)
                .sound(RESTORE + 1.1f, AlertSounds.CHEST_SHIMMER, 0.5f, 0.9f);
        this.cues.action(CUT, () -> AlertSounds.playSliding(AlertSounds.LASER_HUM, 2.6f,
                0.7f, 0.15f, 0.5f, 0.06f));
    }

    @Override
    protected void advance(float t) {
        this.arcAge += this.dt;
        if (t >= SPARK && this.sparkStage == 0) {
            this.sparkStage = 1;
            this.arcAge = 0f;
        }
        if (t >= 2.2f && this.sparkStage == 1) {
            this.sparkStage = 2;
            this.arcAge = 0f;
        }
        if (t >= 3.1f && this.sparkStage == 2) {
            this.sparkStage = 3;
            this.arcAge = 0f;
        }
        // Dust only becomes visible inside the spotlight cone.
        if (t > SPARK && t < RESTORE + 0.6f) {
            for (int i = 0; i < this.dustX.length; i++) {
                this.dustY[i] -= this.dustV[i] * this.dt / this.h;
                if (this.dustY[i] < -0.05f) {
                    this.dustY[i] = 1.05f;
                    this.dustX[i] = rnd(0f, 1f);
                }
            }
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.8f);
        float cx = w * 0.5f;
        float unit = Math.min(w, h);

        // Before the cut: the normal world, already dimming.
        if (t < CUT) {
            Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.55f * (1f - ramp(t, 0f, CUT)), 0x1A1E26));
            Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.35f * (1f - ramp(t, 0f, CUT)), 0x000000));
            return;
        }

        // The cut itself: absolute black, no gradient, held.
        Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(fade, 0x000000));

        // Faint residual after-image of the room, fading in the first second.
        float ghost = (1f - ramp(t, CUT, 0.75f)) * 0.22f;
        if (ghost > 0.005f) {
            Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(ghost * fade, 0x2A3040));
            Draw2D.gradientV(g, 0f, h * 0.72f, w, h, Draw2D.argb(0f, 0x3A4458),
                    Draw2D.argb(ghost * fade, 0x3A4458));
        }

        // The single spark that proves the power is not fully gone.
        if (t >= SPARK - 0.05f && this.sparkStage >= 1) {
            boolean live = this.arcAge < 1.4f;
            float arcOut = live ? 1f : 0f;
            if (this.sparkStage == 1 || live) {
                float sx = cx + (look() == 1 ? 0f : w * 0.18f);
                float sy = h * 0.34f;
                float flick = 0.55f + 0.45f * Math.abs(wave(t * 26f, 6f, this.arcSeed[0]));
                if (live) {
                    // A six-segment arc from nowhere to nowhere.
                    float px = sx;
                    float py = sy;
                    for (int i = 0; i < 6; i++) {
                        float nx = px + wave(t * 8f, 2.2f, this.arcSeed[i % 5]) * unit * 0.02f
                                + (i - 2.5f) * unit * 0.006f;
                        float ny = py + (i % 2 == 0 ? 1f : -1f) * unit * 0.012f;
                        Draw2D.softLine(g, px, py, nx, ny, Math.max(1.2f, unit * 0.0035f),
                                flick * 0.85f * (1f - this.arcAge / 1.4f), 0xB8E8FF);
                        px = nx;
                        py = ny;
                    }
                    star4(g, sx, sy, unit * 0.012f * flick, flick * (1f - this.arcAge / 1.4f), 0xFFFFFF);
                    Draw2D.glow(g, sx, sy, unit * 0.05f, 0.18f * flick
                            * (1f - this.arcAge / 1.4f), 0x8AC8FF);
                }
            }
        }

        // Spotlight: a cone from above onto the banner area.
        float spot = ramp(t, SPARK - 0.2f, 0.7f) * (1f - ramp(t, RESTORE - 0.1f, 0.35f));
        if (spot > 0.005f) {
            float topY = -h * 0.05f;
            float focusY = h * 0.30f;
            float topW = unit * 0.055f;
            float botW = unit * 0.30f;
            Draw2D.quad(g, cx - topW, topY, Draw2D.argb(0.10f * spot * fade, 0xFFF2C8),
                    cx + topW, topY, Draw2D.argb(0.10f * spot * fade, 0xFFF2C8),
                    cx + botW, focusY + unit * 0.10f, Draw2D.argb(0f, 0xFFF2C8),
                    cx - botW, focusY + unit * 0.10f, Draw2D.argb(0f, 0xFFF2C8));
            Draw2D.glow(g, cx, focusY + unit * 0.06f, unit * 0.30f, 0.14f * spot * fade, 0xFFF2C8);

            // Dust motes visible only inside the cone.
            for (int i = 0; i < this.dustX.length; i++) {
                float dx = this.dustX[i] * w;
                float dy = this.dustY[i] * h;
                float dist = Math.abs(dx - cx) / Math.max(1f, botW);
                float inCone = Math.max(0f, 1f - dist);
                inCone *= Math.max(0f, 1f - Math.max(0f, (dy - focusY) / (h * 0.5f)));
                if (inCone < 0.03f) {
                    continue;
                }
                Draw2D.circle(g, dx, dy, this.dustS[i], Draw2D.argb(0.5f * inCone * spot * fade, 0xFFF6D8),
                        Draw2D.argb(0f, 0xFFF6D8));
            }

            // The name is the only lit object.
            float lit = spot * fade;
            Component nameText = TextFx.plain(this.payment.name(), 0xFFF6DC, true);
            float scale = TextFx.fitScale(font, nameText, w * 0.78f, 3.6f)
                    * (0.96f + 0.04f * wave(t, 2.2f, 0f));
            TextFx.centered(g, font, nameText, cx, h * 0.24f, scale, lit);
            Component sub = TextFx.plain(amountPrefix() + this.payment.displayAmount(), 0xE0C88A, true);
            TextFx.centered(g, font, sub, cx, h * 0.24f + 32f * scale,
                    TextFx.fitScale(font, sub, w * 0.65f, 1.75f), lit * 0.95f);
        }

        // Emergency variant: a red rotating beacon pulses from the corner.
        if (look() == 2 && t > SPARK) {
            float sweep = t * 2.4f;
            float beam = 0.5f + 0.5f * (float) Math.sin(sweep);
            float bx = w * 0.5f;
            float by = h * 0.96f;
            Draw2D.glow(g, bx, by, w * 0.30f, 0.16f * beam * fade * (1f - ramp(t, RESTORE, 0.5f)), 0xFF2020);
            Draw2D.triangle(g, bx, by, Draw2D.argb(0.10f * beam * fade, 0xFF3030),
                    bx - w * 0.5f, by - h * 0.6f, Draw2D.argb(0f, 0xFF3030),
                    bx + w * 0.5f, by - h * 0.6f, Draw2D.argb(0f, 0xFF3030));
        }

        // Restore: deliberately blown out, then settles.
        if (t >= RESTORE) {
            float blow = 1f - Easing.outCubic(ramp(t, RESTORE, 1.0f));
            if (blow > 0.002f) {
                Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(blow * 0.92f * fade, 0xFFFFFF));
            }
            float after = 1f - ramp(t, RESTORE + 0.4f, 0.9f);
            if (after > 0.002f) {
                // Colours bleeding back in from the edges.
                Draw2D.gradientV(g, 0f, 0f, w, h, Draw2D.argb(0.18f * after * fade, 0x2E6AFF),
                        Draw2D.argb(0f, 0x2E6AFF));
                Draw2D.gradientV(g, 0f, 0f, w, h, Draw2D.argb(0f, 0xFF2E4C),
                        Draw2D.argb(0.16f * after * fade, 0xFF2E4C));
            }
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected boolean drawsBanner() {
        return false;
    }
}
