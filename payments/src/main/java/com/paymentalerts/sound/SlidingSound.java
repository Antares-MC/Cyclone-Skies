package com.paymentalerts.sound;

import com.paymentalerts.config.PaymentAlertsConfig;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public final class SlidingSound extends AbstractTickableSoundInstance {
    private final int durationTicks;
    private final float startPitch;
    private final float endPitch;
    private final float startVolume;
    private final float endVolume;
    private int ticks;

    public SlidingSound(SoundEvent soundEvent, float f, float f2, float f3, float f4, float f5) {
        super(soundEvent, SoundSource.MASTER, SoundInstance.createUnseededRandom());
        this.durationTicks = Math.max(1, Math.round(f * 20.0f));
        this.startPitch = f2;
        this.endPitch = f3;
        this.startVolume = f4;
        this.endVolume = f5;
        this.relative = true;
        this.looping = true;
        this.delay = 0;
        this.attenuation = SoundInstance.Attenuation.NONE;
        apply(0.0f);
    }

    private void apply(float f) {
        float f2 = this.startPitch + ((this.endPitch - this.startPitch) * f);
        float f3 = this.startVolume + ((this.endVolume - this.startVolume) * f * f);
        this.pitch = Math.max(0.5f, Math.min(2.0f, f2));
        this.volume = Math.max(0.0f, Math.min(1.0f, f3 * PaymentAlertsConfig.get().soundVolume));
    }

    public void tick() {
        this.ticks++;
        apply(Math.min(1.0f, this.ticks / this.durationTicks));
        if (this.ticks >= this.durationTicks) {
            stop();
        }
    }
}
