package com.paymentalerts.alert;

import com.paymentalerts.config.PaymentAlertsConfig;
import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.skin.Dolls;
import com.paymentalerts.skin.SkinDoll;
import com.paymentalerts.skin.SkinFetcher;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

final class CoinPopAlert implements SideAlert {
    static final int HEIGHT = 24;
    private static final int DOLL_WIDTH = 16;
    private static final int GAP = 4;
    private static final int MARGIN = 4;
    private static final float SLIDE_IN = 0.25f;
    private static final float SLIDE_OUT = 0.3f;
    private final SkinFetcher.Skin skin;
    private final String payerName;
    private SkinDoll doll;
    private final long startNanos;
    private final float holdSeconds;
    private final Component nameText;
    private final Component amountText;
    private final int[] colors;
    private final Particles2D sparkles = new Particles2D();
    private long lastNanos;
    private boolean burst;
    static final String[] VARIATIONS = {"Gold coin", "Emerald", "Diamond"};
    private static final int[][] COLORS = {new int[]{16770442, 13208082, 16765773, -868018, 16774856}, new int[]{8188058, 1477182, 7139466, -12723094, 14221280}, new int[]{11072754, 1744816, 9434610, -11541536, 15269887}};

    CoinPopAlert(Payment payment, long j, int i) {
        int pick = Variations.pick(i, VARIATIONS.length);
        this.colors = COLORS[pick];
        this.startNanos = j;
        this.lastNanos = j;
        this.holdSeconds = Math.max(2.0f, PaymentAlertsConfig.get().tabSeconds * 0.6f);
        this.skin = SkinFetcher.get(payment.lookupName());
        this.payerName = payment.name();
        this.nameText = Component.literal(payment.name()).setStyle(Style.EMPTY.withBold(true));
        this.amountText = Component.literal("+" + payment.displayAmount()).setStyle(Style.EMPTY.withBold(true));
        AlertSounds.play(AlertSounds.COIN_PICKUP, 1.5f, 0.6f);
        AlertSounds.play(pick == 0 ? AlertSounds.MONEY_DING : AlertSounds.COIN_SPARKLE, pick == 0 ? 2.0f : 1.4f, 0.55f);
    }

    private float elapsed(long j) {
        return (j - this.startNanos) / 1.0E9f;
    }

    @Override // com.paymentalerts.alert.SideAlert
    public boolean isFinished(long j) {
        return elapsed(j) >= (0.25f + this.holdSeconds) + SLIDE_OUT;
    }

    private float slide(float f) {
        if (f < 0.25f) {
            return Easing.outCubic(f / 0.25f);
        }
        float f2 = 0.25f + this.holdSeconds;
        if (f > f2) {
            return 1.0f - Easing.inCubic((f - f2) / SLIDE_OUT);
        }
        return 1.0f;
    }

    @Override // com.paymentalerts.alert.SideAlert
    public int render(GuiGraphics guiGraphics, Font font, int i, int i2, long j) {
        float elapsed = elapsed(j);
        float min = Math.min(0.1f, (j - this.lastNanos) / 1.0E9f);
        this.lastNanos = j;
        float slide = slide(elapsed);
        int width = 26 + font.width(this.nameText) + 4 + font.width(this.amountText) + 6 + 12 + 5;
        int round = ((i - 4) - width) + Math.round((1.0f - slide) * (width + 4 + 4));
        Draw2D.fill(guiGraphics, round, i2, round + width, i2 + HEIGHT, -669904362);
        Draw2D.fill(guiGraphics, round, i2, round + width, i2 + 1, 822083583);
        Draw2D.fill(guiGraphics, round, i2, round + 2, i2 + HEIGHT, this.colors[3]);
        int i3 = round + 6;
        if (this.doll == null) {
            this.doll = Dolls.create(this.skin, this.payerName);
        }
        boolean z = false;
        if (this.doll != null) {
            this.doll.dance(elapsed - 0.25f, 1);
            z = Dolls.drawInBox(guiGraphics, this.doll, i3, i2 + 1, i3 + DOLL_WIDTH, (i2 + HEIGHT) - 1, 10, i3 + DOLL_WIDTH + 30.0f, i2 + 6.0f);
        }
        if (!z) {
            Dolls.face(guiGraphics, this.skin, i3 + 2, i2 + 6, 12);
        }
        int i4 = i3 + DOLL_WIDTH + 4;
        Draw2D.text(guiGraphics, font, this.nameText, i4, i2 + 8, -1);
        Draw2D.text(guiGraphics, font, this.amountText, i4 + font.width(this.nameText) + 4, i2 + 8, (-16777216) | this.colors[2]);
        float f = (round + width) - 11.0f;
        float f2 = i2 + 12.0f;
        float sin = (elapsed <= 0.2f || elapsed >= 0.7f) ? 0.0f : (float) Math.sin(((elapsed - 0.2f) / 0.5f) * 3.141592653589793d);
        if (!this.burst && elapsed > 0.4f) {
            this.burst = true;
            for (int i5 = 0; i5 < 12; i5++) {
                double d = (6.283185307179586d * i5) / 12.0d;
                this.sparkles.add(Particles2D.Kind.SPARK, f, f2, ((float) Math.cos(d)) * 30.0f, ((float) Math.sin(d)) * 30.0f, 0.5f, 0.8f, this.colors[4], this.colors[0]).drag = 3.5f;
            }
        }
        if (sin > 0.0f) {
            Draw2D.glow(guiGraphics, f, f2, 9.0f + (5.0f * sin), 0.5f * sin * slide, this.colors[4]);
        }
        drawCoin(guiGraphics, f, f2, 5.0f * (1.0f + (0.45f * sin)), elapsed * (2.5f + (14.0f * sin)), slide);
        this.sparkles.update(min);
        this.sparkles.render(guiGraphics, slide);
        return Math.round(28.0f * ((elapsed > (0.25f + this.holdSeconds) ? 1 : (elapsed == (0.25f + this.holdSeconds) ? 0 : -1)) > 0 ? slide : 1.0f));
    }

    private void drawCoin(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, float f5) {
        float cos = (float) Math.cos(f4);
        float max = Math.max(0.8f, Math.abs(cos) * f3);
        Draw2D.ellipse(guiGraphics, f, f2, max + 0.8f, f3 + 0.8f, Draw2D.argb(f5, this.colors[1]), Draw2D.argb(f5, Draw2D.scaleRgb(this.colors[1], 0.7f)));
        Draw2D.ellipse(guiGraphics, f, f2, max, f3, Draw2D.argb(f5, this.colors[0]), Draw2D.argb(f5, Draw2D.lerpRgb(this.colors[0], this.colors[1], 0.55f)));
        if (Math.abs(cos) > 0.55f) {
            Draw2D.glow(guiGraphics, f - (max * SLIDE_OUT), f2 - (f3 * 0.35f), f3 * 0.55f, f5 * 0.8f, 16777215);
        }
    }
}
