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
 * Pixel storm: everything is hard-edged. A blocky level bar fills, the screen splits into
 * a cell grid that flips through pixel sprites, then pixel rain falls in integer steps at
 * 12 fps. No smooth circles anywhere.
 */
final class PixelStormAlert extends StageBase {
    static final String[] VARIATIONS = {"8-bit", "16-bit", "Glitch"};

    private static final float SPLIT = 0.6f;
    private static final float SETTLE = 1.65f;
    private static final float END = 6.1f;
    private static final int COLS = 16;
    private static final int ROWS = 9;
    private static final int MAX_PIX = 220;
    private static final int[] PALETTE = {
            0x10101E, 0xE8452E, 0xFFC23D, 0x2FB7A8, 0x4C6FFF, 0xFF5FA2, 0xFFFFFF, 0x9BFF3D,
    };

    private final int[] sprite = new int[COLS * ROWS];
    private final float[] delay = new float[COLS * ROWS];
    private final Pix[] rain = new Pix[MAX_PIX];
    private int rainCursor;
    private float frameTimer;

    private static final class Pix {
        float x, y, vx, vy;
        int size;
        int color;
        boolean live;
    }

    PixelStormAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.sprite.length; i++) {
            this.sprite[i] = this.rng.nextInt(4);
            this.delay[i] = rnd(0f, 0.5f);
        }
        for (int i = 0; i < this.rain.length; i++) {
            this.rain[i] = new Pix();
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
        this.cues.sound(0.05f, AlertSounds.JACKPOT_TICK, 0.5f, 1.6f)
                .sound(SPLIT, AlertSounds.MONEY_LEVELUP, 0.9f, 1.0f);
        for (int i = 0; i < 9; i++) {
            this.cues.sound(SPLIT + i * 0.12f, AlertSounds.JACKPOT_TICK, 0.5f,
                    1.2f + (i % 4) * 0.12f);
        }
        this.cues.sound(2.4f, AlertSounds.COIN_PICKUP, 0.6f, 1.4f)
                .sound(3.4f, AlertSounds.COIN_PICKUP, 0.5f, 1.2f)
                .sound(4.4f, AlertSounds.JACKPOT_STOP, 0.7f, 1.0f);
    }

    @Override
    protected void advance(float t) {
        this.frameTimer += this.dt;
        boolean step = this.frameTimer >= 1f / 12f;
        if (!step) {
            return;
        }
        this.frameTimer %= 1f / 12f;
        if (t > SPLIT + 0.2f && t < 4.6f) {
            int burst = t < 2.8f ? 3 : 1;
            for (int i = 0; i < burst; i++) {
                Pix p = this.rain[this.rainCursor];
                this.rainCursor = (this.rainCursor + 1) % this.rain.length;
                int size = look() == 1 ? 4 : 7;
                p.size = size;
                p.x = 8f + 8f * (float) Math.floor(rnd(0f, COLS)) + (float) Math.floor(rnd(0f, 2f)) * 4f;
                p.y = -8f;
                p.vx = (float) Math.floor(rnd(-2f, 2f)) * 8f;
                p.vy = 120f + (float) Math.floor(rnd(0f, 6f)) * 40f;
                p.color = PALETTE[1 + this.rng.nextInt(PALETTE.length - 1)];
                p.live = true;
            }
        }
        for (Pix p : this.rain) {
            if (!p.live) {
                continue;
            }
            p.x += p.vx / 12f;
            p.y += p.vy / 12f;
            if (p.y > this.h + 12f) {
                p.live = false;
            }
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.7f);
        Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.82f * fade, 0x0A0A14));

        levelBar(g, font, w, h, t, fade);
        if (t >= SPLIT) {
            grid(g, w, h, t, fade);
        }
        drawRain(g, w, h, t, fade);

        if (look() == 2 && t > 1.0f) {
            int bands = 3;
            for (int i = 0; i < bands; i++) {
                float at = 1.0f + i * 0.9f + (float) Math.floor((t * 7f + i) % 3f) * 0.2f;
                if (t > at && t < at + 0.16f) {
                    float y = h * (0.2f + 0.25f * i);
                    Draw2D.rect(g, 0f, y, w, y + h * 0.04f, Draw2D.argb(0.5f * fade, PALETTE[5]));
                }
            }
        }
        fadeParticles(fade);
    }

    private void levelBar(GuiGraphics g, Font font, int w, int h, float t, float fade) {
        float p = ramp(t, 0.05f, 0.55f);
        if (p <= 0f) {
            return;
        }
        float barW = w * 0.6f;
        float x0 = (w - barW) * 0.5f;
        float y0 = h * 0.72f;
        float cell = barW / 24f;
        Draw2D.rect(g, x0 - 4f, y0 - 4f, x0 + barW + 4f, y0 + cell + 4f,
                Draw2D.argb(0.9f * fade, 0x10101E));
        for (int i = 0; i < 24; i++) {
            float fill = ramp(p, i / 24f * 0.6f, 0.15f);
            if (fill <= 0f) {
                continue;
            }
            int col = i % 4 == 0 ? PALETTE[6] : PALETTE[3];
            Draw2D.rect(g, x0 + i * cell + 1f, y0 + 1f, x0 + (i + 1) * cell - 1f, y0 + cell - 1f,
                    Draw2D.argb(fill * 0.95f * fade, col));
        }
        if (p >= 1f) {
            Component up = TextFx.plain("LEVEL UP", PALETTE[2], true);
            float s = TextFx.fitScale(font, up, w * 0.3f, 2.2f);
            float blink = 0.55f + 0.45f * (float) Math.abs(Math.sin(t * 12f));
            TextFx.centered(g, font, up, w * 0.5f, y0 + cell + 8f, s, blink * fade);
        }
    }

    private void grid(GuiGraphics g, int w, int h, float t, float fade) {
        float cw = w / (float) COLS;
        float ch = h / (float) ROWS;
        float cover = ramp(t, SPLIT, 0.25f) * fade;
        int glitch = look() == 2 ? 1 : 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int idx = row * COLS + col;
                float x = col * cw;
                float y = row * ch;
                if (glitch == 1 && this.sprite[idx] % 5 == 0) {
                    x += wave(t, 3.1f, idx) * 18f;
                }
                int spriteId;
                if (t < SETTLE) {
                    int flip = (int) Math.floor((t - SPLIT) * 14f + this.delay[idx]) & 3;
                    spriteId = flip;
                } else {
                    spriteId = this.sprite[idx];
                }
                int tint = PALETTE[1 + (idx % 7)];
                float alpha = 0.16f * cover;
                Draw2D.rect(g, x + 1f, y + 1f, x + cw - 1f, y + ch - 1f,
                        Draw2D.argb(alpha, tint));
                if (t > SPLIT + this.delay[idx] * 0.4f) {
                    pixelSprite(g, x + cw * 0.5f, y + ch * 0.5f, Math.min(cw, ch) * 0.3f,
                            spriteId, cover * 0.9f, tint);
                }
            }
        }
        // scanline separation between cells
        for (int col = 1; col < COLS; col++) {
            Draw2D.rect(g, col * cw - 1f, 0f, col * cw, h, Draw2D.argb(0.45f * cover, 0x05050A));
        }
        for (int row = 1; row < ROWS; row++) {
            Draw2D.rect(g, 0f, row * ch - 1f, w, row * ch, Draw2D.argb(0.45f * cover, 0x05050A));
        }
    }

    private void pixelSprite(GuiGraphics g, float cx, float cy, float r, int id, float alpha, int tint) {
        float px = Math.max(1.5f, r * 0.34f);
        int base = Draw2D.argb(alpha, 0xFFFFFF);
        int col = Draw2D.argb(alpha, tint);
        if (id == 0) {
            // coin: 3x3 ring with a notch
            for (int i = -1; i <= 1; i++) {
                Draw2D.rect(g, cx + i * px - px * 0.45f, cy - px * 1.4f,
                        cx + i * px + px * 0.45f, cy - px * 0.8f, base);
                Draw2D.rect(g, cx + i * px - px * 0.45f, cy + px * 0.8f,
                        cx + i * px + px * 0.45f, cy + px * 1.4f, col);
            }
            Draw2D.rect(g, cx - px * 1.45f, cy - px * 0.8f, cx - px * 0.8f, cy + px * 0.8f, col);
            Draw2D.rect(g, cx + px * 0.8f, cy - px * 0.8f, cx + px * 1.45f, cy + px * 0.8f, col);
        } else if (id == 1) {
            // heart
            Draw2D.rect(g, cx - px * 1.5f, cy - px * 0.6f, cx - px * 0.5f, cy + px * 0.2f, col);
            Draw2D.rect(g, cx + px * 0.5f, cy - px * 0.6f, cx + px * 1.5f, cy + px * 0.2f, col);
            Draw2D.rect(g, cx - px * 0.5f, cy + px * 0.2f, cx + px * 0.5f, cy + px * 0.9f, col);
            Draw2D.rect(g, cx - px * 0.9f, cy - px * 0.1f, cx + px * 0.9f, cy + px * 0.5f, base);
        } else if (id == 2) {
            // star
            Draw2D.rect(g, cx - px * 0.4f, cy - px * 1.6f, cx + px * 0.4f, cy + px * 1.6f, col);
            Draw2D.rect(g, cx - px * 1.6f, cy - px * 0.4f, cx + px * 1.6f, cy + px * 0.4f, col);
            Draw2D.rect(g, cx - px * 0.4f, cy - px * 0.4f, cx + px * 0.4f, cy + px * 0.4f, base);
        } else {
            // up arrow
            Draw2D.rect(g, cx - px * 0.35f, cy - px * 1.4f, cx + px * 0.35f, cy + px * 1.4f, col);
            Draw2D.rect(g, cx - px * 1.2f, cy + px * 0.3f, cx - px * 0.5f, cy + px, col);
            Draw2D.rect(g, cx + px * 0.5f, cy + px * 0.3f, cx + px * 1.2f, cy + px, col);
        }
    }

    private void drawRain(GuiGraphics g, int w, int h, float t, float fade) {
        for (Pix p : this.rain) {
            if (!p.live) {
                continue;
            }
            float x = Math.round(p.x / 4f) * 4f;
            float y = Math.round(p.y / 4f) * 4f;
            Draw2D.rect(g, x, y, x + p.size, y + p.size, Draw2D.argb(0.95f * fade, p.color));
            Draw2D.rect(g, x, y, x + p.size, y + 3f,
                    Draw2D.argb(0.8f * fade, Draw2D.lerpRgb(p.color, 0xFFFFFF, 0.6f)));
        }
    }

    @Override
    protected String tagline() {
        return "LEVEL UP";
    }
}
