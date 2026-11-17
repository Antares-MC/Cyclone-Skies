package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Sparkle burst: a shimmer rosette rather than a radial puff.
 *
 * <p>A single point of light appears, blooms into petals that open on a stagger, then the
 * petals dissolve into slow motes that hang in the air instead of flying outward.
 */
final class SparkleBurstAlert extends StageBase {
    static final String[] VARIATIONS = {"Soft", "Bright", "Rainbow"};

    private static final float OPEN = 0.4f;
    private static final float DISSOLVE = 1.25f;
    private static final float END = 5.6f;

    private final float[] spin = new float[3];
    private float moteTimer;

    SparkleBurstAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.spin.length; i++) {
            this.spin[i] = rnd(0f, 6.283f);
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
        this.cues.sound(0.0f, AlertSounds.MONEY_DING, 0.9f, 1.0f)
                .sound(0.45f, AlertSounds.BLACKHOLE_SPARKLE, 0.7f, 1.25f)
                .sound(1.2f, AlertSounds.MONEY_DING, 0.7f, 1.5f)
                .sound(2.1f, AlertSounds.COIN_SPARKLE, 0.5f, 1.4f)
                .sound(3.2f, AlertSounds.COIN_SPARKLE, 0.4f, 1.6f);
    }

    @Override
    protected void advance(float t) {
        if (t < DISSOLVE || t > 4.6f) {
            return;
        }
        this.moteTimer += this.dt;
        float rate = t < 3f ? 0.035f : 0.09f;
        while (this.moteTimer > rate) {
            this.moteTimer -= rate;
            float ang = rnd(0f, 6.283f);
            float radius = rnd(0f, Math.min(this.w, this.h) * 0.22f);
            Particles2D.P p = spawn(Particles2D.Kind.SPARK,
                    this.w * 0.5f + (float) Math.cos(ang) * radius,
                    this.h * 0.45f + (float) Math.sin(ang) * radius * 0.75f,
                    rnd(-9f, 9f), rnd(-16f, 4f), rnd(1.8f, 3.6f), rnd(1f, 2.4f),
                    moteColor(ang), this.theme.glow);
            p.gravity = -6f;
            p.drag = 2.2f;
            p.twinkle = true;
        }
    }

    private int moteColor(float angle) {
        return switch (look()) {
            case 1 -> Draw2D.lerpRgb(0xFFFFFF, this.theme.accent, (float) Math.abs(Math.sin(angle)));
            case 2 -> Draw2D.hsv(angle / 6.283f + 0.1f, 0.4f, 1f);
            default -> Draw2D.lerpRgb(this.theme.primary, 0xFFFFFF, 0.45f);
        };
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.8f);
        float cx = w * 0.5f;
        float cy = h * 0.45f;
        float unit = Math.min(w, h);
        int look = look();

        Draw2D.glow(g, cx, cy, unit * 0.32f, 0.20f * fade * ramp(t, 0.1f, 0.5f), this.theme.primary);
        Draw2D.glow(g, cx, cy, unit * 0.12f, 0.35f * fade, this.theme.accent);

        int petals = look == 0 ? 5 : 8;
        float baseLen = unit * (look == 1 ? 0.30f : 0.26f);
        float width = unit * (look == 0 ? 0.085f : 0.035f);
        for (int i = 0; i < petals; i++) {
            float open = Easing.outBack(ramp(t, OPEN + i * 0.055f, 0.55f));
            float close = 1f - ramp(t, DISSOLVE + i * 0.02f, 0.85f);
            float a = open * close * fade;
            if (a <= 0.01f) {
                continue;
            }
            float ang = (i * 6.283f / petals) - 1.5708f + wave(t, 0.4f, i) * 0.06f;
            float dirX = (float) Math.cos(ang);
            float dirY = (float) Math.sin(ang);
            float len = baseLen * (0.35f + 0.65f * open) * (1f + 0.04f * wave(t, 2.2f, i * 1.7f));
            int col = look == 2
                    ? Draw2D.hsv((i / (float) petals) + t * 0.05f, 0.55f, 1f)
                    : Draw2D.lerpRgb(i % 2 == 0 ? this.theme.primary : this.theme.accent, 0xFFFFFF, 0.3f);
            float inner = unit * 0.02f;
            float px = -dirY;
            float py = dirX;
            int c0 = Draw2D.argb(a * 0.75f, 0xFFFFFF);
            int c1 = Draw2D.argb(a * 0.5f, col);
            int c2 = Draw2D.argb(0f, col);
            float mx = cx + dirX * len * 0.55f;
            float my = cy + dirY * len * 0.55f;
            Draw2D.quad(g,
                    cx + dirX * inner, cy + dirY * inner, c0,
                    mx + px * width, my + py * width, c1,
                    cx + dirX * len, cy + dirY * len, c2,
                    mx - px * width, my - py * width, c1);
            Draw2D.softLine(g, cx + dirX * inner, cy + dirY * inner,
                    cx + dirX * len * 0.92f, cy + dirY * len * 0.92f,
                    Math.max(1f, width * 0.16f), a * 0.5f, 0xFFFFFF);
        }

        for (int i = 0; i < 3; i++) {
            float r = unit * 0.13f;
            float ang = this.spin[i] + t * (1.6f + i * 0.5f);
            star4(g, cx + (float) Math.cos(ang) * r, cy + (float) Math.sin(ang) * r * 0.6f,
                    unit * 0.016f, 0.7f * fade * ramp(t, 0.7f, 0.4f), 0xFFFFFF);
        }

        float core = ramp(t, 0f, 0.35f) * (1f - ramp(t, 1.9f, 0.9f));
        Draw2D.glow(g, cx, cy, unit * 0.05f * (1f + 0.25f * wave(t, 5f, 0f)), 0.75f * core * fade, 0xFFFFFF);

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }
}
