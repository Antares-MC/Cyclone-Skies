package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.HudFireworks;
import com.paymentalerts.payment.Payment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.joml.Matrix3x2fStack;

final class FireworksAlert implements StageAlert {
    static final String[] VARIATIONS = {"Classic", "Gold", "Rainbow finale", "Hearts"};
    private static final float BANNER_SECONDS = 7.5f;
    private static final float POP_IN = 0.45f;
    private static final float FADE_OUT = 0.6f;
    private static final float HARD_LIMIT = 12.0f;
    private static final int ROCKETS = 14;
    private final Payment payment;
    private final long startNanos;
    private final HudFireworks fireworks;
    private final Component subtitle;
    private final int variation;

    FireworksAlert(Payment payment, long j, int i) {
        this.payment = payment;
        this.startNanos = j;
        this.variation = Variations.pick(i, VARIATIONS.length);
        this.fireworks = new HudFireworks(this.variation == 2 ? 20 : ROCKETS, this.variation);
        this.subtitle = Component.literal("paid you " + payment.displayAmount());
    }

    private float elapsed(long j) {
        return (j - this.startNanos) / 1.0E9f;
    }

    @Override // com.paymentalerts.alert.StageAlert
    public void tick(long j) {
    }

    @Override // com.paymentalerts.alert.StageAlert
    public boolean isFinished(long j) {
        float elapsed = elapsed(j);
        return elapsed >= HARD_LIMIT || (elapsed >= BANNER_SECONDS && this.fireworks.isFinished());
    }

    @Override // com.paymentalerts.alert.StageAlert
    public void render(GuiGraphics guiGraphics, Font font, int i, int i2, float f, long j) {
        float elapsed = elapsed(j);
        if (elapsed > 6.0f) {
            this.fireworks.stopLaunching();
        }
        this.fireworks.update(f, i, i2);
        this.fireworks.render(guiGraphics);
        renderBanner(guiGraphics, font, i, i2, elapsed);
    }

    private void renderBanner(GuiGraphics guiGraphics, Font font, int i, int i2, float f) {
        if (f >= BANNER_SECONDS) {
            return;
        }
        float min = Math.min(1.0f, f / 0.15f);
        if (f > 6.9f) {
            min = 1.0f - ((f - 6.9f) / FADE_OUT);
        }
        if (min * 255.0f < 6.0f) {
            return;
        }
        float outBack = 0.55f + (POP_IN * Easing.outBack(f / POP_IN));
        MutableComponent shimmeringName = shimmeringName(this.payment.name(), f, this.variation);
        int width = font.width(shimmeringName);
        int width2 = font.width(this.subtitle);
        float min2 = Math.min(3.0f, (i * 0.8f) / Math.max(1, width));
        float max = Math.max(width * min2, width2 * 1.5f) + 28.0f;
        float f2 = 9.0f * min2;
        float f3 = 8.0f + f2 + 4.0f + (9.0f * 1.5f) + 8.0f;
        float f4 = i / 2.0f;
        float max2 = Math.max(6.0f, i2 * 0.05f);
        Matrix3x2fStack pose = guiGraphics.pose();
        pose.pushMatrix();
        pose.translate(f4, max2 + (f3 / 2.0f));
        pose.scale(outBack, outBack);
        pose.translate(-f4, -(max2 + (f3 / 2.0f)));
        int round = Math.round(f4 - (max / 2.0f));
        int round2 = Math.round(f4 + (max / 2.0f));
        int round3 = Math.round(max2);
        int round4 = Math.round(max2 + f3);
        Draw2D.fill(guiGraphics, round, round3, round2, round4, Easing.argb(min * 0.5f, 0));
        Draw2D.fill(guiGraphics, round, round3, round2, round3 + 1, Easing.argb(min, 16762941));
        Draw2D.fill(guiGraphics, round, round4 - 1, round2, round4, Easing.argb(min, 16762941));
        pose.pushMatrix();
        pose.translate(f4, max2 + 8.0f);
        pose.scale(min2, min2);
        Draw2D.text(guiGraphics, font, shimmeringName, (-width) / 2, 0, Easing.argb(min, 16777215));
        pose.popMatrix();
        pose.pushMatrix();
        pose.translate(f4, max2 + 8.0f + f2 + 4.0f);
        pose.scale(1.5f, 1.5f);
        Draw2D.text(guiGraphics, font, this.subtitle, (-width2) / 2, 0, Easing.argb(min, 15921906));
        pose.popMatrix();
        pose.popMatrix();
    }

    private static MutableComponent shimmeringName(String str, float f, int i) {
        int lerpRgb;
        MutableComponent empty = Component.empty();
        for (int i2 = 0; i2 < str.length(); i2++) {
            float sin = 0.5f + (0.5f * ((float) Math.sin((f * 4.5f) - (i2 * POP_IN))));
            switch (i) {
                case HudFireworks.GOLD /* 1 */:
                    lerpRgb = Easing.lerpRgb(16751104, 16769136, sin);
                    break;
                case HudFireworks.RAINBOW_FINALE /* 2 */:
                    lerpRgb = Draw2D.hsv((f * 0.4f) + (i2 * 0.07f), 0.55f, 1.0f);
                    break;
                case 3:
                    lerpRgb = Easing.lerpRgb(16727406, 16761048, sin);
                    break;
                default:
                    lerpRgb = Easing.lerpRgb(16757504, 16774328, sin);
                    break;
            }
            empty.append(Component.literal(String.valueOf(str.charAt(i2))).setStyle(Style.EMPTY.withColor(lerpRgb).withBold(true)));
        }
        return empty;
    }
}
