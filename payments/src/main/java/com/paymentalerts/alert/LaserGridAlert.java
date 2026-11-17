package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Laser grid: a heist vault. A targeting HUD draws itself, a perspective beam grid sweeps
 * in from both sides, snaps into a diamond around the centre, slices the frame, then
 * retracts.
 */
final class LaserGridAlert extends StageBase {
    static final String[] VARIATIONS = {"Cyan", "Magenta", "Green"};

    private static final float SWEEP = 0.8f;
    private static final float LOCK = 2.0f;
    private static final float RETRACT = 5.1f;
    private static final float END = 6.6f;

    private static final int[] COLORS = {0x2EE6FF, 0xFF2E9A, 0x9BFF3D};

    private final float[] ys = new float[10];
    private final float[] phase = new float[10];
    private final float[] cutY = new float[7];
    private float readout;

    LaserGridAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.ys.length; i++) {
            this.ys[i] = 0.14f + i * 0.082f;
            this.phase[i] = rnd(0f, 6.283f);
        }
        for (int i = 0; i < this.cutY.length; i++) {
            this.cutY[i] = rnd(0.2f, 0.8f);
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

    private int color() {
        return COLORS[look()];
    }

    @Override
    protected void cue() {
        this.cues.sound(0.1f, AlertSounds.LASER_HUM, 0.5f, 1.0f)
                .sound(SWEEP, AlertSounds.LASER_ALARM, 0.8f, 1.0f)
                .sound(1.6f, AlertSounds.LASER_CHARGE, 0.6f, 1.1f)
                .sound(LOCK - 0.15f, AlertSounds.LASER_LOCK, 0.9f, 1.0f)
                .sound(LOCK, AlertSounds.LASER_FIRE, 0.7f, 1.0f)
                .sound(3.0f, AlertSounds.LASER_BURN, 0.5f, 1.0f)
                .sound(3.8f, AlertSounds.LASER_FIRE, 0.5f, 1.1f)
                .sound(4.6f, AlertSounds.LASER_BURN, 0.4f, 0.9f)
                .sound(RETRACT, AlertSounds.LASER_ALARM, 0.5f, 0.8f);
    }

    @Override
    protected void advance(float t) {
        this.readout = 12f + ramp(t, SWEEP, 1.2f) * 108f + wave(t, 3f, 0f) * 0.6f;
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.7f);
        float hud = ramp(t, 0f, 0.8f) * fade;
        float retract = 1f - Easing.inCubic(ramp(t, RETRACT, END - RETRACT - 0.3f));
        int col = color();
        float cx = w * 0.5f;
        float cy = h * 0.42f;

        Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.72f * fade, 0x04070C));
        Draw2D.vignette(g, w, h, 0.3f, 0.35f * fade, 0x0A1420);

        // HUD brackets.
        float inset = Math.min(w, h) * 0.06f;
        float arm = Math.min(w, h) * 0.07f;
        hudBracket(g, inset, inset, arm, 0.7f * hud, col, 1f, 1f);
        hudBracket(g, w - inset, inset, arm, 0.7f * hud, col, -1f, 1f);
        hudBracket(g, inset, h - inset, arm, 0.7f * hud, col, 1f, -1f);
        hudBracket(g, w - inset, h - inset, arm, 0.7f * hud, col, -1f, -1f);

        float scan = (t * 0.55f) % 1f;
        Draw2D.softLine(g, inset, h * scan, w - inset, h * scan, Math.max(1f, h * 0.002f),
                0.25f * hud, col);
        Draw2D.glow(g, cx, h * scan, w * 0.2f, 0.06f * hud, col);

        Component read = TextFx.plain("RANGE " + String.format("%.1f", this.readout) + "  LOCK "
                + (t > LOCK ? "ON" : "SEEKING"), col, false);
        TextFx.centered(g, font, read, cx, inset + 4f, TextFx.fitScale(font, read, w * 0.4f, 1.0f),
                0.7f * hud);

        if (t >= SWEEP) {
            float sweepIn = Easing.outCubic(ramp(t, SWEEP, 1.2f));
            float lock = ramp(t, LOCK, 0.35f);
            // Perspective grid: lines converge on a vanishing point.
            float vpx = cx;
            float vpy = h * 0.40f;
            for (int i = 0; i < this.ys.length; i++) {
                float y = h * this.ys[i];
                float reach = w * (0.5f + 0.5f * sweepIn) * retract;
                float a = 0.4f * fade * (0.6f + 0.4f * (float) Math.sin(t * 6f + this.phase[i]));
                int lineCol = i % 3 == 0 ? 0xFFFFFF : col;
                // from left edge and right edge toward the vanishing point
                Draw2D.softLine(g, 0f, y + (vpy - y) * (1f - plane(x(w, y), vpy)), vpx - reach * 0.5f,
                        vpy + (y - vpy) * 0.08f, Math.max(1.5f, h * 0.0028f), a, lineCol);
                Draw2D.softLine(g, w, y + (vpy - y) * (1f - plane(w - x(w, y), vpy)), vpx + reach * 0.5f,
                        vpy + (y - vpy) * 0.08f, Math.max(1.5f, h * 0.0028f), a, lineCol);
                Draw2D.softLine(g, 0f, y + 3f, w, y + 3f, Math.max(1f, h * 0.0012f), a * 0.18f, col);
            }
            // Fan lines to the vanishing point.
            for (int i = 0; i < 8; i++) {
                float ang = -0.7f + i * 0.2f;
                float len = Math.min(w, h) * 0.7f * sweepIn * retract;
                Draw2D.softLine(g, vpx, vpy, vpx + (float) Math.cos(ang) * len,
                        vpy + (float) Math.sin(ang) * len, 1.2f, 0.18f * fade, col);
            }

            if (lock > 0f) {
                float pulse = 0.6f + 0.4f * (float) Math.sin(t * 12f);
                float diamond = Math.min(w, h) * (0.24f + 0.02f * pulse) * retract;
                for (int side = 0; side < 4; side++) {
                    float ax = cx + (side == 0 || side == 3 ? -diamond : diamond);
                    float ay = cy + (side < 2 ? -diamond * 0.62f : diamond * 0.62f);
                    float bx = cx + (side == 1 || side == 2 ? diamond : -diamond);
                    float by = cy + (side == 1 || side == 3 ? -diamond * 0.62f : diamond * 0.62f);
                    Draw2D.softLine(g, ax, ay, bx, by, Math.max(2f, h * 0.004f), lock * pulse * fade, 0xFFFFFF);
                    Draw2D.softLine(g, ax, ay, bx, by, Math.max(4f, h * 0.009f), lock * 0.2f * fade, col);
                }
                star4(g, vpx, vpy, Math.min(w, h) * 0.02f, lock * pulse * fade, 0xFFFFFF);
            }

            // Slices.
            for (int i = 0; i < this.cutY.length; i++) {
                float at = LOCK + 0.4f + i * 0.5f;
                float p = ramp(t, at, 0.22f);
                if (p <= 0f || p >= 1f) {
                    continue;
                }
                float y = this.cutY[i] * h;
                Draw2D.softLine(g, 0f, y, w, y, Math.max(2f, h * 0.005f * (1f - p)),
                        (1f - p) * 0.8f * fade, 0xFFFFFF);
                Draw2D.softLine(g, 0f, y, w, y, Math.max(4f, h * 0.012f * (1f - p)),
                        (1f - p) * 0.25f * fade, col);
            }
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    private float x(int w, float y) {
        return w * (y / (float) this.h);
    }

    private float plane(float x, float vpy) {
        return 0.06f + 0.28f * (1f - Math.min(1f, x / this.w));
    }

    private void hudBracket(GuiGraphics g, float x, float y, float arm, float alpha,
                            int col, float dx, float dy) {
        Draw2D.softLine(g, x, y, x + arm * dx, y, 2.4f, alpha, col);
        Draw2D.softLine(g, x, y, x, y + arm * dy, 2.4f, alpha, col);
        Draw2D.circle(g, x, y, 2f, Draw2D.argb(alpha, col), Draw2D.argb(0f, col));
    }

    @Override
    protected String tagline() {
        return "TARGET LOCKED";
    }
}
