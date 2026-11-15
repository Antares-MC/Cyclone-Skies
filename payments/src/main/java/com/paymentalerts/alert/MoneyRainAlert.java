package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.HudFireworks;
import com.paymentalerts.fx.MoneyParticles;
import com.paymentalerts.sound.AlertSounds;
import com.paymentalerts.sound.SoundCues;
import java.util.Random;
import net.minecraft.client.gui.GuiGraphics;

final class MoneyRainAlert implements OverlayAlert {
    static final String[] VARIATIONS = {"Cash", "Gold bars", "Diamonds & emeralds", "Everything"};
    private static final float RAIN_SECONDS = 3.4f;
    private static final float HARD_LIMIT = 10.0f;
    private final long startNanos;
    private final MoneyParticles money = new MoneyParticles();
    private final SoundCues cues = new SoundCues();

    MoneyRainAlert(long j, int i) {
        this.startNanos = j;
        int pick = Variations.pick(i, VARIATIONS.length);
        this.money.startRain(RAIN_SECONDS, 34.0f, pick);
        Random random = new Random();
        switch (pick) {
            case HudFireworks.GOLD /* 1 */:
                this.cues.sound(0.0f, AlertSounds.JACKPOT_BELL, 1.6f, 0.7f).sound(0.05f, AlertSounds.MONEY_LEVELUP, 1.2f, 0.5f);
                break;
            case HudFireworks.RAINBOW_FINALE /* 2 */:
                this.cues.sound(0.0f, AlertSounds.BLACKHOLE_SPARKLE, 1.2f, 0.8f).sound(0.15f, AlertSounds.BLACKHOLE_SPARKLE, 1.6f, 0.7f).sound(0.05f, AlertSounds.MONEY_LEVELUP, 1.6f, 0.5f);
                break;
            default:
                this.cues.sound(0.0f, AlertSounds.MONEY_DING, 1.5f, 0.8f).sound(0.12f, AlertSounds.MONEY_DING, 2.0f, 0.8f).sound(0.05f, AlertSounds.MONEY_LEVELUP, 1.4f, 0.5f);
                break;
        }
        float f = pick == 1 ? 0.7f : pick == 2 ? 1.4f : 0.9f;
        for (int i2 = 0; i2 < 12; i2++) {
            this.cues.sound(0.4f + (random.nextFloat() * 3.2f), AlertSounds.MONEY_COIN, f + (random.nextFloat() * 0.6f), 0.35f);
        }
    }

    private float elapsed(long j) {
        return (j - this.startNanos) / 1.0E9f;
    }

    @Override // com.paymentalerts.alert.OverlayAlert
    public void tick(long j) {
        this.cues.update(elapsed(j));
    }

    @Override // com.paymentalerts.alert.OverlayAlert
    public boolean isFinished(long j) {
        float elapsed = elapsed(j);
        return elapsed >= HARD_LIMIT || (elapsed > RAIN_SECONDS && this.money.isEmpty() && this.cues.isDone());
    }

    @Override // com.paymentalerts.alert.OverlayAlert
    public void render(GuiGraphics guiGraphics, int i, int i2, float f, long j) {
        this.money.update(f, i, i2);
        this.money.render(guiGraphics, 1.0f);
        Draw2D.flush(guiGraphics);
    }
}
