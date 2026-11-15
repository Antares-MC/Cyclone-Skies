package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.MoneyParticles;
import com.paymentalerts.sound.AlertSounds;
import com.paymentalerts.sound.SoundCues;
import java.util.Random;
import net.minecraft.client.gui.GuiGraphics;

final class CoinFountainAlert implements OverlayAlert {
    static final String[] VARIATIONS = {"Gold coins", "Cash & coins", "Gems", "Everything"};
    private static final MoneyParticles.Kind[][] MIXES = {new MoneyParticles.Kind[]{MoneyParticles.Kind.COIN}, new MoneyParticles.Kind[]{MoneyParticles.Kind.BILL, MoneyParticles.Kind.COIN, MoneyParticles.Kind.COIN}, new MoneyParticles.Kind[]{MoneyParticles.Kind.DIAMOND, MoneyParticles.Kind.EMERALD}, new MoneyParticles.Kind[]{MoneyParticles.Kind.BILL, MoneyParticles.Kind.COIN, MoneyParticles.Kind.BAR, MoneyParticles.Kind.DIAMOND, MoneyParticles.Kind.EMERALD}};
    private static final float SPRAY_SECONDS = 2.2f;
    private static final float RATE = 40.0f;
    private static final float HARD_LIMIT = 8.0f;
    private final long startNanos;
    private final MoneyParticles money = new MoneyParticles();
    private final SoundCues cues = new SoundCues();
    private final MoneyParticles.Kind[] mix;
    private float sprayed;
    private float accumulator;

    CoinFountainAlert(long j, int i) {
        this.startNanos = j;
        int pick = Variations.pick(i, VARIATIONS.length);
        this.mix = MIXES[pick];
        Random random = new Random();
        this.cues.sound(0.0f, AlertSounds.COIN_CHIME, 1.2f, 0.6f).sound(0.08f, AlertSounds.COIN_CHIME, 1.6f, 0.6f).sound(0.05f, AlertSounds.MONEY_LEVELUP, 1.7f, 0.35f);
        if (pick == 2) {
            this.cues.sound(0.2f, AlertSounds.COIN_SPARKLE, 1.3f, 0.6f);
        }
        for (int i2 = 0; i2 < 10; i2++) {
            this.cues.sound(0.15f + (random.nextFloat() * 2.6f), AlertSounds.MONEY_COIN, 1.0f + (random.nextFloat() * 0.6f), 0.3f);
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
        return elapsed >= HARD_LIMIT || (elapsed > SPRAY_SECONDS && this.money.isEmpty() && this.cues.isDone());
    }

    @Override // com.paymentalerts.alert.OverlayAlert
    public void render(GuiGraphics guiGraphics, int i, int i2, float f, long j) {
        if (this.sprayed < SPRAY_SECONDS) {
            this.sprayed += f;
            this.accumulator += f * RATE * (1.0f - (0.5f * (this.sprayed / SPRAY_SECONDS)));
            while (this.accumulator >= 1.0f) {
                this.accumulator -= 1.0f;
                this.money.burst(HARD_LIMIT, i2 + 6.0f, 1, i2, this.mix, 0.33f, 0.26f, 1.5f);
                this.money.burst(i - HARD_LIMIT, i2 + 6.0f, 1, i2, this.mix, -0.33f, 0.26f, 1.5f);
            }
        }
        this.money.update(f, i, i2);
        this.money.render(guiGraphics, 1.0f);
        Draw2D.flush(guiGraphics);
    }
}
