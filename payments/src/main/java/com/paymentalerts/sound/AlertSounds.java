package com.paymentalerts.sound;

import com.paymentalerts.PaymentAlertsClient;
import com.paymentalerts.config.PaymentAlertsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public final class AlertSounds {
    public static final SoundEvent FIREWORK_LAUNCH = of("fireworks.launch");
    public static final SoundEvent FIREWORK_BLAST = of("fireworks.blast");
    public static final SoundEvent FIREWORK_LARGE_BLAST = of("fireworks.large_blast");
    public static final SoundEvent FIREWORK_BLAST_FAR = of("fireworks.blast_far");
    public static final SoundEvent FIREWORK_TWINKLE = of("fireworks.twinkle");
    public static final SoundEvent NUKE_SIREN = of("nuke.siren");
    public static final SoundEvent NUKE_WHISTLE = of("nuke.whistle");
    public static final SoundEvent NUKE_SWOOP = of("nuke.swoop");
    public static final SoundEvent NUKE_SHOCKWAVE = of("nuke.shockwave");
    public static final SoundEvent NUKE_BLAST = of("nuke.blast");
    public static final SoundEvent NUKE_THUNDER = of("nuke.thunder");
    public static final SoundEvent NUKE_RUMBLE = of("nuke.rumble");
    public static final SoundEvent NUKE_FALLOUT = of("nuke.fallout");
    public static final SoundEvent MONEY_DING = of("money.ding");
    public static final SoundEvent MONEY_COIN = of("money.coin");
    public static final SoundEvent MONEY_LEVELUP = of("money.levelup");
    public static final SoundEvent JACKPOT_TICK = of("jackpot.tick");
    public static final SoundEvent JACKPOT_STOP = of("jackpot.stop");
    public static final SoundEvent JACKPOT_PLING = of("jackpot.pling");
    public static final SoundEvent JACKPOT_BELL = of("jackpot.bell");
    public static final SoundEvent JACKPOT_CHIME = of("jackpot.chime");
    public static final SoundEvent BLACKHOLE_PORTAL = of("blackhole.portal");
    public static final SoundEvent BLACKHOLE_CHARGE = of("blackhole.charge");
    public static final SoundEvent BLACKHOLE_COLLAPSE = of("blackhole.collapse");
    public static final SoundEvent BLACKHOLE_BANG = of("blackhole.bang");
    public static final SoundEvent BLACKHOLE_TOTEM = of("blackhole.totem");
    public static final SoundEvent BLACKHOLE_SPARKLE = of("blackhole.sparkle");
    public static final SoundEvent STORM_RAIN = of("storm.rain");
    public static final SoundEvent STORM_IMPACT = of("storm.impact");
    public static final SoundEvent STORM_THUNDER = of("storm.thunder");
    public static final SoundEvent STORM_BIG = of("storm.big");
    public static final SoundEvent LASER_ALARM = of("laser.alarm");
    public static final SoundEvent LASER_LOCK = of("laser.lock");
    public static final SoundEvent LASER_CHARGE = of("laser.charge");
    public static final SoundEvent LASER_FIRE = of("laser.fire");
    public static final SoundEvent LASER_HUM = of("laser.hum");
    public static final SoundEvent LASER_BURN = of("laser.burn");
    public static final SoundEvent COIN_CHIME = of("coin.chime");
    public static final SoundEvent COIN_PICKUP = of("coin.pickup");
    public static final SoundEvent COIN_SPARKLE = of("coin.sparkle");
    public static final SoundEvent CONFETTI_POP = of("confetti.pop");
    public static final SoundEvent CONFETTI_BOOM = of("confetti.boom");
    public static final SoundEvent CONFETTI_CHEER = of("confetti.cheer");
    public static final SoundEvent CONFETTI_TADA = of("confetti.tada");
    public static final SoundEvent CONFETTI_HARP = of("confetti.harp");
    public static final SoundEvent CHEST_LAND = of("chest.land");
    public static final SoundEvent CHEST_RATTLE = of("chest.rattle");
    public static final SoundEvent CHEST_OPEN = of("chest.open");
    public static final SoundEvent CHEST_ENDER_OPEN = of("chest.ender_open");
    public static final SoundEvent CHEST_SHIMMER = of("chest.shimmer");
    public static final SoundEvent CHEST_EJECT = of("chest.eject");
    public static final SoundEvent METEOR_WHOOSH = of("meteor.whoosh");
    public static final SoundEvent METEOR_FIRE = of("meteor.fire");
    public static final SoundEvent METEOR_IMPACT = of("meteor.impact");
    public static final SoundEvent METEOR_BIG = of("meteor.big");
    public static final SoundEvent METEOR_RUMBLE = of("meteor.rumble");
    public static final SoundEvent NOVA_HUM = of("nova.hum");
    public static final SoundEvent NOVA_HEARTBEAT = of("nova.heartbeat");
    public static final SoundEvent NOVA_CHARGE = of("nova.charge");
    public static final SoundEvent NOVA_COLLAPSE = of("nova.collapse");
    public static final SoundEvent NOVA_BLAST = of("nova.blast");
    public static final SoundEvent NOVA_ROAR = of("nova.roar");
    public static final SoundEvent NOVA_SHIMMER = of("nova.shimmer");
    public static final SoundEvent GOD_ACTIVATE = of("god.activate");
    public static final SoundEvent GOD_CONDUIT = of("god.conduit");
    public static final SoundEvent GOD_BELL = of("god.bell");
    public static final SoundEvent GOD_FANFARE = of("god.fanfare");
    public static final SoundEvent GOD_TOTEM = of("god.totem");

    private AlertSounds() {
    }

    private static SoundEvent of(String str) {
        return SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(PaymentAlertsClient.MOD_ID, str));
    }

    public static void play(SoundEvent soundEvent, float f, float f2) {
        if (!PaymentAlertsConfig.get().soundsEnabled) {
            return;
        }
        float f3 = f2 * PaymentAlertsConfig.get().soundVolume;
        if (f3 <= 0.001f) {
            return;
        }
        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(soundEvent.location(), SoundSource.MASTER, Math.min(1.0f, f3), Math.max(0.5f, Math.min(2.0f, f)), SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.NONE, 0.0d, 0.0d, 0.0d, true));
    }

    public static void playSliding(SoundEvent soundEvent, float f, float f2, float f3, float f4, float f5) {
        if (!PaymentAlertsConfig.get().soundsEnabled || PaymentAlertsConfig.get().soundVolume <= 0.001f) {
            return;
        }
        Minecraft.getInstance().getSoundManager().play(new SlidingSound(soundEvent, f, f2, f3, f4, f5));
    }
}
