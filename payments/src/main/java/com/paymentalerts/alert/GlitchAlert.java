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
 * Glitch: the frame is the subject. Bands of the screen detach and slide, block clusters
 * duplicate off-axis, the payer's name is split per glyph into RGB channels and reassembled,
 * then the whole image collapses to a scan line and re-forms.
 */
final class GlitchAlert extends StageBase {
    static final String[] VARIATIONS = {"Datamosh", "Channel split", "Total failure"};

    private static final float ONSET = 0.45f;
    private static final float COLLAPSE = 3.1f;
    private static final float REFORM = 3.8f;
    private static final float END = 6.4f;

    private static final int BANDS = 18;
    private static final int BLOCKS = 26;

    private final float[] bandY = new float[BANDS];
    private final float[] bandH = new float[BANDS];
    private final float[] bandDir = new float[BANDS];
    private final float[] bandSeed = new float[BANDS];
    private final float[] blockX = new float[BLOCKS];
    private final float[] blockY = new float[BLOCKS];
    private final float[] blockW = new float[BLOCKS];
    private final float[] blockH = new float[BLOCKS];
    private final int[] blockColor = new int[BLOCKS];
    private final float[] blockSeed = new float[BLOCKS];

    private static final int[] CHANNELS = {0xFF2E4C, 0x2EFF6A, 0x2E6AFF};

    private float stutter;

    GlitchAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < BANDS; i++) {
            this.bandY[i] = rnd(0.02f, 0.98f);
            this.bandH[i] = rnd(0.006f, 0.055f);
            this.bandDir[i] = chance(0.5f) ? 1f : -1f;
            this.bandSeed[i] = rnd(0f, 6.283f);
        }
        for (int i = 0; i < BLOCKS; i++) {
            this.blockX[i] = rnd(0f, 1f);
            this.blockY[i] = rnd(0f, 1f);
            this.blockW[i] = rnd(0.02f, 0.16f);
            this.blockH[i] = rnd(0.006f, 0.04f);
            this.blockColor[i] = CHANNELS[i % 3];
            this.blockSeed[i] = rnd(0f, 6.283f);
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
        this.cues.sound(0.0f, AlertSounds.JACKPOT_STOP, 0.7f, 0.8f)
                .sound(ONSET, AlertSounds.LASER_FIRE, 0.5f, 1.5f)
                .sound(ONSET + 0.02f, AlertSounds.NUKE_FALLOUT, 0.4f, 1.4f)
                .sound(1.2f, AlertSounds.CHEST_ENDER_OPEN, 0.5f, 1.3f)
                .sound(2.0f, AlertSounds.JACKPOT_TICK, 0.6f, 1.7f)
                .sound(COLLAPSE, AlertSounds.BLACKHOLE_COLLAPSE, 0.8f, 1.2f)
                .sound(REFORM, AlertSounds.MONEY_LEVELUP, 0.8f, 1.0f)
                .sound(REFORM + 0.3f, AlertSounds.JACKPOT_PLING, 0.6f, 1.2f);
        for (int i = 0; i < 12; i++) {
            this.cues.sound(ONSET + i * 0.21f, AlertSounds.JACKPOT_TICK, 0.32f,
                    1.1f + (i % 5) * 0.14f);
        }
        ScreenShake.add(ONSET, 0.6f, 0.25f);
        ScreenShake.add(COLLAPSE, 0.4f, 0.3f);
    }

    @Override
    protected void advance(float t) {
        if (t < ONSET || t > COLLAPSE) {
            return;
        }
        if (this.rng.nextFloat() < 0.35f) {
            Particles2D.P p = spawn(Particles2D.Kind.SPARK, rnd(0f, this.w), rnd(0f, this.h),
                    rnd(-140f, 140f), rnd(-60f, 60f), rnd(0.12f, 0.35f), rnd(1f, 3.5f),
                    CHANNELS[this.rng.nextInt(3)], 0x101010);
            p.drag = 2.4f;
            p.gravity = 0f;
        }
    }

    /** 12 fps holds with sudden 1-frame jumps, so it reads as corrupted, not smooth. */
    private float chaos(float t) {
        if (t < ONSET) {
            return 0f;
        }
        float ramp = Easing.clamp01((t - ONSET) / 0.5f);
        float level = look() == 2 ? 1f : (look() == 1 ? 0.62f : 0.85f);
        float step = (float) (Math.floor(t * 12f) * 0.6180339f);
        return ramp * level * (0.35f + 0.65f * ((step % 1f)));
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.7f);
        boolean collapse = t > COLLAPSE && t < REFORM;
        float collapseAmt = collapse
                ? Easing.inCubic(ramp(t, COLLAPSE, 0.36f))
                : 1f - Easing.outCubic(ramp(t, REFORM, 0.5f));
        float chaos = chaos(t) * (collapse ? 0f : 1f);

        // Base plate. During collapse it squashes to a single line at screen centre.
        float plateH = h * (collapse ? 1f - collapseAmt * 0.988f : 1f);
        float plateY = (h - plateH) * 0.5f;

        Draw2D.rect(g, 0f, plateY, w, plateY + plateH,
                Draw2D.argb(0.88f * fade, 0x0B0A12));
        Draw2D.gradientV(g, 0f, plateY, w, plateY + plateH,
                Draw2D.argb(0.22f * fade, 0x1A0A28), Draw2D.argb(0.10f * fade, 0x08181A));

        if (!collapse) {
            // Duplicated block clusters, offset per channel.
            float blockAlpha = chaos * 0.55f;
            if (blockAlpha > 0.01f) {
                for (int i = 0; i < BLOCKS; i++) {
                    float slide = this.blockX[i] * w + this.bandDir[i] * chaos * w * 0.30f
                            + wave(t * 3f, this.blockSeed[i], i * 1.3f) * 40f;
                    float y = this.blockY[i] * h;
                    float bw = this.blockW[i] * w;
                    float bh = this.blockH[i] * h;
                    int col = this.blockColor[i];
                    Draw2D.rect(g, slide, y, slide + bw, y + bh,
                            Draw2D.argb(blockAlpha * 0.55f * fade, col));
                    Draw2D.rect(g, slide + 6f, y + bh * 0.3f, slide + bw * 0.7f, y + bh,
                            Draw2D.argb(blockAlpha * 0.3f * fade, Draw2D.lerpRgb(col, 0xFFFFFF, 0.4f)));
                }
            }

            // Torn scanline bands: the plate is repainted in strips, each shifted.
            for (int i = 0; i < BANDS; i++) {
                float offset = this.bandDir[i] * chaos * w * (0.06f + 0.22f * Math.abs(wave(t * 2.4f, this.bandSeed[i], i)));
                if (Math.abs(offset) < 1f) {
                    continue;
                }
                float y = this.bandY[i] * h;
                float bh = this.bandH[i] * h;
                float a = 0.60f * fade;
                Draw2D.rect(g, offset, y, w + offset, y + bh, Draw2D.argb(a, 0x0B0A12));
                Draw2D.rect(g, offset, y, w + offset, y + bh * 0.14f,
                        Draw2D.argb(a * 0.5f, CHANNELS[i % 3]));
                // Edge smear at the strip seams.
                Draw2D.rect(g, offset - 18f, y, offset, y + bh,
                        Draw2D.argb(a * 0.35f, CHANNELS[(i + 1) % 3]));
                Draw2D.rect(g, w + offset, y, w + offset + 18f, y + bh,
                        Draw2D.argb(a * 0.35f, CHANNELS[(i + 2) % 3]));
            }

            // Name, drawn three times in channel colours plus a clean read.
            float nameIn = ramp(t, REFORM + 0.1f, 0.6f);
            if (nameIn > 0f) {
                Component clean = TextFx.plain(this.payment.name(), 0xF2F6FF, true);
                float scale = TextFx.fitScale(font, clean, w * 0.8f, 3.9f);
                float splitX = chaos * 26f;
                float splitY = chaos * 6f;
                TextFx.centered(g, font, TextFx.plain(this.payment.name(), CHANNELS[0], true),
                        w * 0.5f - splitX, h * 0.24f - splitY, scale, nameIn * 0.55f * fade);
                TextFx.centered(g, font, TextFx.plain(this.payment.name(), CHANNELS[1], true),
                        w * 0.5f, h * 0.24f, scale, nameIn * 0.55f * fade);
                TextFx.centered(g, font, TextFx.plain(this.payment.name(), CHANNELS[2], true),
                        w * 0.5f + splitX, h * 0.24f + splitY, scale, nameIn * 0.55f * fade);
                float settle = 1f - chaos * 0.75f;
                TextFx.centered(g, font, clean, w * 0.5f, h * 0.24f, scale, nameIn * settle * fade);

                Component sub = TextFx.plain(amountPrefix() + this.payment.displayAmount(), 0xBFC8E0, true);
                float subScale = TextFx.fitScale(font, sub, w * 0.7f, 1.9f);
                TextFx.centered(g, font, sub, w * 0.5f + chaos * 10f, h * 0.24f + 34f * scale,
                        subScale, nameIn * fade);

                // Glyph-level corruption bar over the name.
                if (chaos > 0.05f) {
                    int glyphs = Math.min(28, this.payment.name().length());
                    for (int i = 0; i < glyphs; i++) {
                        float gx = w * 0.5f + (i - glyphs * 0.5f) * 18f * scale;
                        float gh = 44f * scale * (0.4f + Math.abs(wave(t * 6f, i * 1.9f, 0f)));
                        if (wave(t * 9f, i * 2.7f, 0.5f) > 0.6f) {
                            Draw2D.rect(g, gx, h * 0.24f - gh * 0.5f, gx + 16f * scale,
                                    h * 0.24f + gh * 0.5f,
                                    Draw2D.argb(chaos * 0.5f * fade, CHANNELS[i % 3]));
                        }
                    }
                }
            }
        } else {
            // Collapse: a hot white line with a smeared bloom.
            Draw2D.softLine(g, 0f, h * 0.5f, w, h * 0.5f, Math.max(1.5f, h * 0.004f),
                    0.9f * fade, 0xFFFFFF);
            Draw2D.glow(g, w * 0.5f, h * 0.5f, w * 0.4f, 0.25f * fade * collapseAmt, 0x8AC8FF);
        }

        // CRT scanlines over everything, always.
        for (float y = 0f; y < h; y += 4f) {
            Draw2D.rect(g, 0f, y, w, y + 1.4f, Draw2D.argb(0.10f * fade, 0x000000));
        }

        // Roll bar drifting down the frame.
        float roll = ((t * 0.28f) % 1f) * h;
        Draw2D.gradientV(g, 0f, roll - h * 0.06f, w, roll + h * 0.06f,
                Draw2D.argb(0f, 0xFFFFFF), Draw2D.argb(0.05f * fade, 0xFFFFFF));

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected boolean drawsBanner() {
        return false;
    }
}
