package com.paymentalerts.alert;

import com.paymentalerts.config.PaymentAlertsConfig;
import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.HudFireworks;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.skin.Dolls;
import com.paymentalerts.skin.SkinDoll;
import com.paymentalerts.skin.SkinFetcher;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

final class TabAlert implements SideAlert {
    static final int HEIGHT = 40;
    static final int GAP = 4;
    private static final int DOLL_WIDTH = 30;
    private static final int DOLL_SIZE = 17;
    private static final int MARGIN = 4;
    private static final float SLIDE_IN = 0.35f;
    private static final float SLIDE_OUT = 0.35f;
    private final Payment payment;
    private final SkinFetcher.Skin skin;
    private final long startNanos;
    private final float holdSeconds;
    private final Component nameText;
    private final Component amountText;
    private final int accent;
    private final int amountColor;
    private final int danceStyle;
    private SkinDoll doll;
    static final String[] VARIATIONS = {"Steady crouch", "Fast crouch", "Double crouch", "Slow crouch"};
    private static final int[] ACCENTS = {-11738774, -11741471, -5214977, -37688};
    private static final int[] AMOUNTS = {-8586102, -8589060, -3170049, -24356};

    enum TabStyle {
        NORMAL,
        SILVER,
        GOLD
    }

    TabAlert(Payment payment, long j, TabStyle tabStyle, int i) {
        int i2;
        int i3;
        int pick = Variations.pick(i, VARIATIONS.length);
        this.danceStyle = pick;
        switch (tabStyle.ordinal()) {
            case HudFireworks.CLASSIC /* 0 */:
                i2 = ACCENTS[pick];
                break;
            case HudFireworks.GOLD /* 1 */:
                i2 = -3549466;
                break;
            case HudFireworks.RAINBOW_FINALE /* 2 */:
                i2 = -14275;
                break;
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
        this.accent = i2;
        switch (tabStyle.ordinal()) {
            case HudFireworks.CLASSIC /* 0 */:
                i3 = AMOUNTS[pick];
                break;
            case HudFireworks.GOLD /* 1 */:
                i3 = -1774346;
                break;
            case HudFireworks.RAINBOW_FINALE /* 2 */:
                i3 = -11443;
                break;
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
        this.amountColor = i3;
        this.payment = payment;
        this.startNanos = j;
        this.holdSeconds = PaymentAlertsConfig.get().tabSeconds;
        this.skin = SkinFetcher.get(payment.lookupName());
        this.nameText = Component.literal(payment.name()).setStyle(Style.EMPTY.withBold(true));
        this.amountText = Component.literal("paid " + payment.displayAmount());
    }

    private float elapsed(long j) {
        return (j - this.startNanos) / 1.0E9f;
    }

    @Override // com.paymentalerts.alert.SideAlert
    public boolean isFinished(long j) {
        return elapsed(j) >= (0.35f + this.holdSeconds) + 0.35f;
    }

    private float slide(long j) {
        float elapsed = elapsed(j);
        if (elapsed < 0.35f) {
            return Easing.outCubic(elapsed / 0.35f);
        }
        float f = 0.35f + this.holdSeconds;
        if (elapsed > f) {
            return 1.0f - Easing.inCubic((elapsed - f) / 0.35f);
        }
        return 1.0f;
    }

    @Override // com.paymentalerts.alert.SideAlert
    public int render(GuiGraphics guiGraphics, Font font, int i, int i2, long j) {
        float slide = slide(j);
        int max = 39 + Math.max(font.width(this.nameText), font.width(this.amountText)) + 10;
        int round = ((i - 4) - max) + Math.round((1.0f - slide) * (max + 4 + 4));
        Draw2D.fill(guiGraphics, round, i2, round + max, i2 + HEIGHT, -669904362);
        Draw2D.fill(guiGraphics, round, i2, round + max, i2 + 1, 822083583);
        Draw2D.fill(guiGraphics, round, i2, round + 2, i2 + HEIGHT, this.accent);
        int i3 = round + 3;
        int i4 = i3 + DOLL_WIDTH;
        if (this.doll == null) {
            this.doll = Dolls.create(this.skin, this.payment.name());
        }
        boolean z = false;
        if (this.doll != null) {
            this.doll.dance(elapsed(j) - 0.35f, this.danceStyle);
            z = Dolls.drawInBox(guiGraphics, this.doll, i3, i2 + 1, i4, (i2 + HEIGHT) - 1, DOLL_SIZE, ((i3 + i4) / 2.0f) + 26.0f, i2 + 6.0f);
        }
        if (!z) {
            Dolls.face(guiGraphics, this.skin, i3 + 4, i2 + 9, 22);
        }
        int i5 = i4 + 6;
        Draw2D.text(guiGraphics, font, this.nameText, i5, i2 + 9, -11443);
        Draw2D.text(guiGraphics, font, this.amountText, i5, i2 + 23, this.amountColor);
        return Math.round(44.0f * ((elapsed(j) > (0.35f + this.holdSeconds) ? 1 : (elapsed(j) == (0.35f + this.holdSeconds) ? 0 : -1)) > 0 ? slide : 1.0f));
    }
}
