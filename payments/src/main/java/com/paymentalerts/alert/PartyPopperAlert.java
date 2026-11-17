package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Party popper: two handheld poppers with a pull cord, firing curling streamers that arc
 * across the screen and hang from the top before fluttering away.
 */
final class PartyPopperAlert extends StageBase {
    static final String[] VARIATIONS = {"Classic", "Neon", "Pastel"};

    private static final float PULL_END = 0.6f;
    private static final float SECOND_POP = 2.4f;
    private static final float END = 6.4f;
    private static final int MAX_STREAMERS = 120;

    private static final int[][] PALETTES = {
            {0xE8452E, 0xFFC23D, 0x2FB7A8, 0xF4F1E4},
            {0xFF2E9A, 0x2EE6FF, 0x9BFF3D, 0xFFFFFF},
            {0xF7A8C4, 0xA8E6CF, 0xC5B3F0, 0xFFF6E8},
    };

    private final Streamer[] streamers = new Streamer[MAX_STREAMERS];
    private int cursor;
    private float popTimer = 99f;
    private boolean flashTop;
    private boolean secondDone;

    private static final class Streamer {
        float x, y, vx, vy, phase, len, thickness;
        int rgb;
        boolean live;
    }

    PartyPopperAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.streamers.length; i++) {
            this.streamers[i] = new Streamer();
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

    private int[] palette() {
        return PALETTES[look()];
    }

    @Override
    protected void cue() {
        this.cues.sound(0.15f, AlertSounds.JACKPOT_TICK, 0.6f, 1.5f)
                .sound(0.45f, AlertSounds.JACKPOT_TICK, 0.7f, 1.3f)
                .sound(PULL_END - 0.05f, AlertSounds.CONFETTI_POP, 0.8f, 1.2f)
                .sound(PULL_END, AlertSounds.CONFETTI_BOOM, 0.9f, 1.0f)
                .sound(PULL_END + 0.15f, AlertSounds.CONFETTI_CHEER, 0.8f, 1.0f)
                .sound(SECOND_POP, AlertSounds.CONFETTI_POP, 0.8f, 1.35f)
                .sound(SECOND_POP + 0.12f, AlertSounds.CONFETTI_CHEER, 0.6f, 1.15f)
                .sound(4.6f, AlertSounds.CONFETTI_HARP, 0.5f, 1.0f);
    }

    @Override
    protected void advance(float t) {
        this.popTimer += this.dt;
        if (!this.secondDone && t >= SECOND_POP) {
            this.secondDone = true;
            pop(SECOND_POP, true);
        }
        float wind = ramp(t, 4.9f, 1.2f);
        for (Streamer s : this.streamers) {
            if (!s.live) {
                continue;
            }
            s.vx *= (float) Math.exp(-0.9f * this.dt);
            s.vy = s.vy * (float) Math.exp(-0.9f * this.dt) + 105f * this.dt;
            s.vx += (float) Math.sin(t * 2.4f + s.phase) * 26f * this.dt;
            s.x += s.vx * this.dt;
            s.y += s.vy * this.dt;
            if (wind > 0f) {
                s.vy -= 420f * wind * this.dt;
                s.vx += 180f * wind * this.dt;
            }
            if (s.y > this.h + 80f || s.x < -120f || s.x > this.w + 120f || s.y < -200f) {
                s.live = false;
            }
        }
    }

    private void pop(float t, boolean top) {
        this.popTimer = 0f;
        this.flashTop = top;
        int[] pal = palette();
        int count = top ? 34 : 52;
        for (int i = 0; i < count; i++) {
            Streamer s = this.streamers[this.cursor];
            this.cursor = (this.cursor + 1) % this.streamers.length;
            boolean left = i % 2 == 0;
            float ox = top ? (left ? this.w * 0.10f : this.w * 0.90f) : (left ? this.w * 0.10f : this.w * 0.90f);
            float oy = top ? this.h * 0.10f : this.h * 0.90f;
            s.x = ox;
            s.y = oy;
            float ang = top ? (left ? 1.15f : 1.99f) : (left ? -1.05f : -2.09f);
            ang += rnd(-0.35f, 0.35f);
            float speed = rnd(320f, 720f);
            s.vx = (float) Math.cos(ang) * speed;
            s.vy = (float) Math.sin(ang) * speed;
            s.phase = rnd(0f, 6.283f);
            s.len = rnd(48f, 120f);
            s.thickness = rnd(1.6f, 3.4f);
            s.rgb = pal[i % pal.length];
            s.live = true;
        }
        for (int i = 0; i < 70; i++) {
            boolean left = i % 2 == 0;
            float ox = left ? this.w * 0.10f : this.w * 0.90f;
            float oy = top ? this.h * 0.10f : this.h * 0.90f;
            float ang = top ? (left ? 1.15f : 1.99f) : (left ? -1.05f : -2.09f);
            ang += rnd(-0.6f, 0.6f);
            float speed = rnd(120f, 560f);
            Particles2D.P p = spawn(Particles2D.Kind.CONFETTI, ox, oy,
                    (float) Math.cos(ang) * speed, (float) Math.sin(ang) * speed,
                    rnd(1.4f, 3.0f), rnd(3f, 6.5f), pal[i % pal.length], 0xFFFFFF);
            p.gravity = 210f;
            p.drag = 0.9f;
            p.angle = rnd(0f, 6.283f);
            p.spin = rnd(-11f, 11f);
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.8f);

        for (Streamer s : this.streamers) {
            if (!s.live) {
                continue;
            }
            drawStreamer(g, s, t, fade);
        }

        drawPopper(g, w * 0.10f, h * 0.905f, -1.05f, t, true);
        drawPopper(g, w * 0.90f, h * 0.905f, -2.09f, t, false);
        if (t < SECOND_POP + 0.6f) {
            drawPopper(g, w * 0.10f, h * 0.095f, 1.15f, t, true);
            drawPopper(g, w * 0.90f, h * 0.095f, 1.99f, t, false);
        }

        if (this.popTimer < 0.18f) {
            float a = (1f - this.popTimer / 0.18f) * fade;
            float oy = this.flashTop ? h * 0.09f : h * 0.90f;
            for (int side = 0; side < 2; side++) {
                float ox = side == 0 ? w * 0.10f : w * 0.90f;
                Draw2D.glow(g, ox, oy, Math.min(w, h) * 0.11f, a * 0.7f, 0xFFFFFF);
                star4(g, ox, oy, Math.min(w, h) * 0.04f * a, a * 0.9f, 0xFFFFFF);
            }
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    private void drawStreamer(GuiGraphics g, Streamer s, float t, float fade) {
        float seed = s.phase;
        float amp = 5f + s.len * 0.08f;
        float px = s.x;
        float py = s.y;
        for (int k = 1; k <= 9; k++) {
            float back = k / 9f;
            float nx = s.x - s.vx * 0.012f * k
                    + (float) Math.sin(t * 5.2f + seed + k * 0.72f) * amp * back;
            float ny = s.y - s.vy * 0.012f * k
                    + (float) Math.cos(t * 4.1f + seed + k * 0.55f) * amp * 0.75f * back;
            int col = Draw2D.lerpRgb(s.rgb, 0xFFFFFF, 0.2f * back);
            Draw2D.softLine(g, px, py, nx, ny, Math.max(1f, s.thickness * (1f - back * 0.55f)),
                    (0.75f - back * 0.45f) * fade, col);
            px = nx;
            py = ny;
        }
        Draw2D.circle(g, s.x, s.y, s.thickness, Draw2D.argb(0.9f * fade, 0xFFFFFF),
                Draw2D.argb(0.6f * fade, s.rgb));
    }

    private void drawPopper(GuiGraphics g, float x, float y, float ang, float t, boolean left) {
        float pull = Easing.outCubic(ramp(t, 0.1f, 0.42f)) * (1f - ramp(t, PULL_END, 0.15f));
        float scale = Math.min(this.w, this.h) * 0.012f;
        float mouthX = x + (float) Math.cos(ang) * scale * 2.6f;
        float mouthY = y + (float) Math.sin(ang) * scale * 2.6f;
        int body = left ? 0xE8452E : 0x2FB7A8;

        Draw2D.rotatedRect(g, (x + mouthX) * 0.5f, (y + mouthY) * 0.5f, scale * 6f, scale * 2.4f,
                ang + 1.5708f, Draw2D.argb(0.95f, body));
        Draw2D.rotatedRect(g, (x + mouthX) * 0.5f, (y + mouthY) * 0.5f, scale * 5f, scale * 1.1f,
                ang + 1.5708f, Draw2D.argb(0.6f, 0xFFFFFF));
        Draw2D.circle(g, mouthX, mouthY, scale * 1.15f, Draw2D.argb(1f, 0x241E1B),
                Draw2D.argb(1f, 0x5B4A3E));
        Draw2D.softLine(g, mouthX, mouthY,
                mouthX + (float) Math.cos(ang + 1.5708f) * scale * (3.2f + pull * 2.6f),
                mouthY + (float) Math.sin(ang + 1.5708f) * scale * (3.2f + pull * 2.6f),
                Math.max(1f, scale * 0.16f), 0.8f, 0xE8D9A8);
        Draw2D.circle(g, x, y, scale * 1.3f, Draw2D.argb(1f, 0x1A1512), Draw2D.argb(1f, 0x000000));
    }
}
