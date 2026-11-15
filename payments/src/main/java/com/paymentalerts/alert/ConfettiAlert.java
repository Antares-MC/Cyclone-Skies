package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import com.paymentalerts.sound.SoundCues;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;

final class ConfettiAlert implements StageAlert {
    private static final float END = 8.6f;
    private static final float FADE = 1.0f;
    private static final float POPPER_IN = 0.45f;
    private final Payment payment;
    private final long startNanos;
    private final int[] palette;
    private int nextVolley;
    static final String[] VARIATIONS = {"Rainbow", "Gold & white", "Pastel", "Neon"};
    private static final int[][] PALETTES = {new int[]{16732013, 16757051, 16769357, 5431418, 4172287, 11824383}, new int[]{16765773, 16774344, 16777215, 15249440, 16770442, 16775656}, new int[]{16757703, 16767400, 16774056, 12120776, 11065599, 14268671}, new int[]{16722902, 2883569, 13041451, 16747051, 8019199, 16777215}};
    private static final float[] VOLLEYS = {0.65f, 2.25f, 3.85f};
    private final SoundCues cues = new SoundCues();
    private final Particles2D fx = new Particles2D();
    private float lastVolley = -10.0f;

    ConfettiAlert(Payment payment, long j, int i) {
        this.payment = payment;
        this.startNanos = j;
        this.palette = PALETTES[Variations.pick(i, VARIATIONS.length)];
        for (int i2 = 0; i2 < VOLLEYS.length; i2++) {
            float f = VOLLEYS[i2];
            this.cues.sound(f, AlertSounds.CONFETTI_POP, 0.85f, 0.9f).sound(f + 0.03f, AlertSounds.CONFETTI_POP, 1.1f, 0.8f).sound(f + 0.02f, AlertSounds.CONFETTI_BOOM, FADE + (i2 * 0.08f), 0.55f).sound(f + 0.15f, AlertSounds.CONFETTI_CHEER, FADE + (i2 * 0.1f), 0.7f);
            ScreenShake.add(f, 0.25f, 0.12f);
        }
        this.cues.sound(0.85f, AlertSounds.CONFETTI_TADA, FADE, 0.65f);
        float[] fArr = {FADE, 1.26f, 1.5f, 2.0f};
        for (int i3 = 0; i3 < fArr.length; i3++) {
            this.cues.sound(0.9f + (i3 * 0.13f), AlertSounds.CONFETTI_HARP, fArr[i3], 0.7f);
        }
        this.cues.sound(2.4f, AlertSounds.CONFETTI_HARP, 1.5f, 0.5f).sound(2.52f, AlertSounds.CONFETTI_HARP, 2.0f, 0.5f).sound(4.0f, AlertSounds.CONFETTI_HARP, 1.26f, 0.5f).sound(4.12f, AlertSounds.CONFETTI_HARP, 1.5f, 0.5f).sound(4.24f, AlertSounds.CONFETTI_HARP, 2.0f, 0.55f);
    }

    private float elapsed(long j) {
        return (j - this.startNanos) / 1.0E9f;
    }

    @Override // com.paymentalerts.alert.StageAlert
    public void tick(long j) {
        this.cues.update(elapsed(j));
    }

    @Override // com.paymentalerts.alert.StageAlert
    public boolean isFinished(long j) {
        return elapsed(j) >= END && this.cues.isDone();
    }

    @Override // com.paymentalerts.alert.StageAlert
    public void render(GuiGraphics guiGraphics, Font font, int i, int i2, float f, long j) {
        float elapsed = elapsed(j);
        float max = elapsed > 7.6000004f ? Math.max(0.0f, (END - elapsed) / FADE) : FADE;
        Draw2D.gradientV(guiGraphics, 0.0f, i2 * 0.55f, i, i2, Draw2D.argb(0.0f, this.palette[0]), Draw2D.argb(0.22f * Easing.outCubic(elapsed / 0.5f) * max, this.palette[0]));
        drawBunting(guiGraphics, i, i2, elapsed, max);
        float min = Math.min(i, i2) * 0.19f;
        float outBack = Easing.outBack(elapsed / POPPER_IN);
        float f2 = ((-min) * 1.2f) + (outBack * min * 1.45f);
        float f3 = (i + (min * 1.2f)) - ((outBack * min) * 1.45f);
        float f4 = i2 + (min * 0.15f);
        float f5 = elapsed - this.lastVolley;
        float f6 = f5 < 0.35f ? FADE - (f5 / 0.35f) : 0.0f;
        while (this.nextVolley < VOLLEYS.length && elapsed >= VOLLEYS[this.nextVolley]) {
            fire(f2, f4, FADE, min, i2, this.nextVolley);
            fire(f3, f4, -1.0f, min, i2, this.nextVolley);
            this.lastVolley = VOLLEYS[this.nextVolley];
            this.nextVolley++;
        }
        this.fx.update(f);
        this.fx.render(guiGraphics, max);
        drawPopper(guiGraphics, f2, f4, FADE, min, f6, max);
        drawPopper(guiGraphics, f3, f4, -1.0f, min, f6, max);
        Draw2D.flush(guiGraphics);
        float f7 = VOLLEYS[0] + 0.2f;
        if (elapsed >= f7) {
            float f8 = elapsed - f7;
            float min2 = Math.min(FADE, f8 / 0.2f) * max;
            float outBack2 = 0.55f + (POPPER_IN * Easing.outBack(f8 / 0.5f));
            float abs = ((float) Math.abs(Math.sin(f8 * 5.5f))) * ((float) Math.exp((-f8) * 1.6f)) * 6.0f;
            MutableComponent colored = TextFx.colored(this.payment.name(), true, i3 -> {
                return this.palette[Math.floorMod(i3 + ((int) (elapsed * 6.0f)), this.palette.length)];
            });
            float fitScale = TextFx.fitScale(font, colored, i * 0.8f, 3.2f);
            float f9 = (i2 * 0.2f) - abs;
            TextFx.centered(guiGraphics, font, colored, i / 2.0f, f9, fitScale * outBack2, min2);
            TextFx.centered(guiGraphics, font, TextFx.plain("paid you " + this.payment.displayAmount(), 16777215, true), i / 2.0f, f9 + (9.0f * fitScale) + 7.0f, 1.6f, min2);
        }
    }

    private void drawBunting(GuiGraphics guiGraphics, int i, int i2, float f, float f2) {
        float outBack = Easing.outBack((f - 0.1f) / 0.6f);
        if (outBack <= 0.0f) {
            return;
        }
        float f3 = i2 * 0.07f;
        int max = Math.max(8, i / 26);
        int i3 = 0;
        while (i3 < 2) {
            float f4 = (-6.0f) + (outBack * (6.0f + (i3 * i2 * 0.035f)));
            float f5 = f3 * (i3 == 0 ? FADE : 1.35f);
            float f6 = (i / max) * 1.15f;
            float sin = ((float) Math.sin((f * 2.2f) + i3)) * 1.2f;
            for (int i4 = 0; i4 < max; i4++) {
                float f7 = (i4 + 0.08f) / max;
                float f8 = (i4 + 0.92f) / max;
                float f9 = (i4 + 0.5f) / max;
                float f10 = f7 * i;
                float f11 = f8 * i;
                float f12 = (f9 * i) + sin;
                float f13 = f4 + (f5 * 4.0f * f7 * (FADE - f7));
                float f14 = f4 + (f5 * 4.0f * f8 * (FADE - f8));
                float f15 = f4 + (f5 * 4.0f * f9 * (FADE - f9)) + f6;
                int i5 = this.palette[(i4 + (i3 * 3)) % this.palette.length];
                Draw2D.triangle(guiGraphics, f10, f13, Draw2D.argb(f2, i5), f11, f14, Draw2D.argb(f2, i5), f12, f15, Draw2D.argb(f2, Draw2D.scaleRgb(i5, 0.75f)));
            }
            for (int i6 = 0; i6 < 24; i6++) {
                float f16 = i6 / 24;
                float f17 = (i6 + 1) / 24;
                Draw2D.softLine(guiGraphics, f16 * i, f4 + (f5 * 4.0f * f16 * (FADE - f16)), f17 * i, f4 + (f5 * 4.0f * f17 * (FADE - f17)), 1.4f, 0.8f * f2, 16051933);
            }
            i3++;
        }
    }

    private void drawPopper(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, float f5, float f6) {
        float radians = (float) Math.toRadians(-55.0d);
        float cos = ((float) Math.cos(radians)) * f3;
        float sin = (float) Math.sin(radians);
        float f7 = -sin;
        float f8 = f5 * f4 * 0.12f;
        float f9 = f - (cos * f8);
        float f10 = f2 - (sin * f8);
        float f11 = f4 * 1.25f;
        float f12 = f9 + (cos * f11);
        float f13 = f10 + (sin * f11);
        float f14 = f4 * 0.42f;
        int[] iArr = {this.palette[0], 16777215, this.palette[2], 16777215, this.palette[4]};
        int length = iArr.length;
        for (int i = 0; i < length; i++) {
            float f15 = i / length;
            float f16 = (i + 1) / length;
            float f17 = f9 + (cos * f11 * f15);
            float f18 = f10 + (sin * f11 * f15);
            float f19 = f9 + (cos * f11 * f16);
            float f20 = f10 + (sin * f11 * f16);
            float f21 = f14 * f15;
            float f22 = f14 * f16;
            int argb = Draw2D.argb(f6, iArr[i]);
            int argb2 = Draw2D.argb(f6, Draw2D.scaleRgb(iArr[i], 0.7f));
            Draw2D.quad(guiGraphics, f17 + (f7 * f21), f18 + (cos * f21), argb, f19 + (f7 * f22), f20 + (cos * f22), argb, f19 - (f7 * f22), f20 - (cos * f22), argb2, f17 - (f7 * f21), f18 - (cos * f21), argb2);
        }
        Draw2D.ellipse(guiGraphics, f12, f13, f14 * 0.55f, f14 * 0.55f, Draw2D.argb(f6, 2759216), Draw2D.argb(f6, 4862032));
        if (f5 > 0.0f) {
            Draw2D.glow(guiGraphics, f12 + (cos * f4 * 0.3f), f13 + (sin * f4 * 0.3f), f4 * (0.6f + (0.6f * (FADE - f5))), f5 * 0.7f * f6, 16774344);
        }
    }

    private void fire(float f, float f2, float f3, float f4, int i, int i2) {
        float radians = (float) Math.toRadians(-55.0d);
        float cos = ((float) Math.cos(radians)) * f3;
        float sin = (float) Math.sin(radians);
        float f5 = f + (cos * f4 * 1.25f);
        float f6 = f2 + (sin * f4 * 1.25f);
        int i3 = i2 == 0 ? 190 : 150;
        for (int i4 = 0; i4 < i3; i4++) {
            float atan2 = ((float) Math.atan2(sin, cos)) + this.fx.rand(-0.45f, POPPER_IN);
            float rand = i * this.fx.rand(2.2f, 4.0f);
            int i5 = this.palette[this.fx.random.nextInt(this.palette.length)];
            Particles2D.P add = this.fx.add(Particles2D.Kind.CONFETTI, f5, f6, ((float) Math.cos(atan2)) * rand, ((float) Math.sin(atan2)) * rand, this.fx.rand(3.6f, 5.2f), this.fx.rand(1.6f, 2.6f), i5, i5);
            add.drag = this.fx.rand(2.1f, 2.9f);
            add.gravity = i * this.fx.rand(0.2f, 0.32f);
            add.spin = this.fx.rand(-9.0f, 9.0f);
            add.angle = this.fx.rand(0.0f, 6.28f);
        }
        for (int i6 = 0; i6 < 40; i6++) {
            float atan22 = ((float) Math.atan2(sin, cos)) + this.fx.rand(-0.5f, 0.5f);
            float rand2 = i * this.fx.rand(1.4f, 3.0f);
            Particles2D.P add2 = this.fx.add(Particles2D.Kind.SPARK, f5, f6, ((float) Math.cos(atan22)) * rand2, ((float) Math.sin(atan22)) * rand2, this.fx.rand(FADE, 2.0f), this.fx.rand(0.6f, 1.1f), 16777215, this.palette[this.fx.random.nextInt(this.palette.length)]);
            add2.drag = 2.6f;
            add2.gravity = i * 0.25f;
            add2.twinkle = true;
        }
        for (int i7 = 0; i7 < 6; i7++) {
            Particles2D.P add3 = this.fx.add(Particles2D.Kind.SMOKE, f5, f6, cos * this.fx.rand(20.0f, 60.0f), sin * this.fx.rand(20.0f, 60.0f), this.fx.rand(0.6f, FADE), f4 * 0.25f, 15261936, 12103872);
            add3.grow = f4 * 0.6f;
            add3.alpha = 0.35f;
            add3.drag = 3.0f;
        }
    }
}
