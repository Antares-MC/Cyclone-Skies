package com.paymentalerts.alert;

import com.paymentalerts.sound.AlertSounds;
import net.minecraft.sounds.SoundEvent;

/**
 * Visual/audio recipe for a {@link PresetStageAlert}.
 * Dozens of named presets let us ship many effects without unique classes.
 */
final class EffectStyle {
    final String id;
    final String[] variationNames;
    final float duration;
    final float burstAt;
    final int burstCount;
    final float burstPower;
    final float particleRate;
    final float particleLife;
    final float particleSize;
    final float particleGravity;
    final float particleDrag;
    final float particleSpread;
    final boolean particlesFromTop;
    final boolean confetti;
    final boolean smoke;
    final boolean twinkle;
    final boolean fireworks;
    final int rocketCount;
    final boolean flash;
    final float darken;
    final float shake;
    final float shakeDuration;
    final float bannerY;
    final String subtitlePrefix;
    final String tagline;
    final SoundEvent soundKey;

    EffectStyle(
            String id,
            String[] variationNames,
            float duration,
            float burstAt,
            int burstCount,
            float burstPower,
            float particleRate,
            float particleLife,
            float particleSize,
            float particleGravity,
            float particleDrag,
            float particleSpread,
            boolean particlesFromTop,
            boolean confetti,
            boolean smoke,
            boolean twinkle,
            boolean fireworks,
            int rocketCount,
            boolean flash,
            float darken,
            float shake,
            float shakeDuration,
            float bannerY,
            String subtitlePrefix,
            String tagline,
            SoundEvent soundKey
    ) {
        this.id = id;
        this.variationNames = variationNames != null ? variationNames : new String[]{"Default"};
        this.duration = duration;
        this.burstAt = burstAt;
        this.burstCount = burstCount;
        this.burstPower = burstPower;
        this.particleRate = particleRate;
        this.particleLife = particleLife;
        this.particleSize = particleSize;
        this.particleGravity = particleGravity;
        this.particleDrag = particleDrag;
        this.particleSpread = particleSpread;
        this.particlesFromTop = particlesFromTop;
        this.confetti = confetti;
        this.smoke = smoke;
        this.twinkle = twinkle;
        this.fireworks = fireworks;
        this.rocketCount = rocketCount;
        this.flash = flash;
        this.darken = darken;
        this.shake = shake;
        this.shakeDuration = shakeDuration;
        this.bannerY = bannerY;
        this.subtitlePrefix = subtitlePrefix;
        this.tagline = tagline;
        this.soundKey = soundKey;
    }

    // ---------- presets ----------

    static final EffectStyle SPARKLE_BURST = of("sparkle_burst",
            new String[]{"Soft", "Bright", "Rainbow"},
            5.5f, 0.2f, 48, 1.0f,
            8f, 1.2f, 1.0f, 80f, 1.4f, 1.0f,
            false, false, false, true,
            false, 0, true, 0.15f, 0.3f, 0.2f,
            0.28f, "paid you ", "Sparkle!", AlertSounds.MONEY_DING);

    static final EffectStyle GOLDEN_SHOWER = of("golden_shower",
            new String[]{"Coins", "Bars", "Ingots"},
            6.5f, 0.15f, 36, 0.7f,
            18f, 1.6f, 1.3f, 220f, 1.2f, 1.2f,
            true, false, false, true,
            false, 0, false, 0.1f, 0.2f, 0.15f,
            0.22f, "paid you ", "Make it rain", AlertSounds.MONEY_COIN);

    static final EffectStyle PARTY_POPPER = of("party_popper",
            new String[]{"Classic", "Neon", "Pastel"},
            6.0f, 0.1f, 80, 1.3f,
            14f, 1.4f, 1.4f, 160f, 1.1f, 1.4f,
            false, true, false, false,
            true, 10, true, 0.2f, 0.4f, 0.25f,
            0.3f, "paid you ", "Party time!", AlertSounds.JACKPOT_PLING);

    static final EffectStyle STARFALL = of("starfall",
            new String[]{"Night", "Dawn", "Galaxy"},
            7.5f, 0.4f, 24, 0.5f,
            12f, 2.0f, 1.1f, 90f, 0.9f, 0.8f,
            true, false, false, true,
            false, 0, false, 0.35f, 0.15f, 0.2f,
            0.25f, "paid you ", "Wish upon a star", AlertSounds.JACKPOT_CHIME);

    static final EffectStyle HEART_BOMB = of("heart_bomb",
            new String[]{"Pink", "Red", "Rainbow"},
            5.8f, 0.25f, 40, 1.1f,
            10f, 1.5f, 1.5f, 120f, 1.3f, 1.0f,
            false, true, false, true,
            false, 0, true, 0.12f, 0.35f, 0.2f,
            0.3f, "paid you ", "<3", AlertSounds.MONEY_LEVELUP);

    static final EffectStyle LASER_GRID = of("laser_grid",
            new String[]{"Cyan", "Magenta", "Green"},
            6.5f, 0.35f, 16, 1.8f,
            6f, 0.9f, 0.8f, 40f, 0.7f, 1.5f,
            false, false, false, false,
            false, 0, true, 0.4f, 0.5f, 0.3f,
            0.2f, "paid you ", "TARGET LOCKED", AlertSounds.BLACKHOLE_CHARGE);

    static final EffectStyle TSUNAMI = of("tsunami",
            new String[]{"Ocean", "Storm", "Abyss"},
            7.0f, 0.5f, 30, 0.9f,
            20f, 1.8f, 1.6f, 60f, 1.0f, 1.3f,
            false, false, true, false,
            false, 0, false, 0.25f, 0.6f, 0.4f,
            0.35f, "paid you ", "Wave incoming", AlertSounds.NUKE_RUMBLE);

    static final EffectStyle VOLCANO = of("volcano",
            new String[]{"Lava", "Ash", "Eruption"},
            8.0f, 0.6f, 55, 1.4f,
            16f, 1.7f, 1.4f, -40f, 1.2f, 1.1f,
            false, false, true, false,
            true, 8, true, 0.3f, 0.8f, 0.45f,
            0.25f, "paid you ", "ERUPTION", AlertSounds.NUKE_BLAST);

    static final EffectStyle AURORA = of("aurora",
            new String[]{"Green", "Purple", "Full spectrum"},
            8.5f, 0.3f, 20, 0.4f,
            9f, 2.2f, 1.2f, 30f, 0.6f, 0.7f,
            true, false, false, true,
            false, 0, false, 0.2f, 0.1f, 0.15f,
            0.2f, "paid you ", "Northern lights", AlertSounds.BLACKHOLE_PORTAL);

    static final EffectStyle DISCO_BALL = of("disco_ball",
            new String[]{"70s", "Neon", "Strobe"},
            7.0f, 0.15f, 60, 1.2f,
            15f, 1.3f, 1.2f, 100f, 1.1f, 1.3f,
            false, true, false, true,
            true, 16, true, 0.15f, 0.35f, 0.2f,
            0.28f, "paid you ", "Let's dance", AlertSounds.JACKPOT_BELL);

    static final EffectStyle PIXEL_STORM = of("pixel_storm",
            new String[]{"8-bit", "16-bit", "Glitch"},
            6.0f, 0.2f, 64, 1.0f,
            22f, 1.0f, 1.8f, 150f, 1.5f, 1.0f,
            true, false, false, false,
            false, 0, true, 0.2f, 0.25f, 0.2f,
            0.3f, "paid you ", "LEVEL UP", AlertSounds.MONEY_LEVELUP);

    static final EffectStyle PHOENIX = of("phoenix",
            new String[]{"Fire", "Ash rebirth", "Golden"},
            8.0f, 0.45f, 42, 1.3f,
            11f, 1.6f, 1.3f, -30f, 1.0f, 1.0f,
            false, false, true, true,
            true, 12, true, 0.25f, 0.55f, 0.35f,
            0.24f, "paid you ", "From the ashes", AlertSounds.NUKE_THUNDER);

    static final EffectStyle GLITCH = of("glitch",
            new String[]{"Static", "Corrupt", "Matrix"},
            5.5f, 0.1f, 28, 1.5f,
            18f, 0.7f, 1.0f, 20f, 0.5f, 1.6f,
            false, false, false, false,
            false, 0, true, 0.45f, 0.4f, 0.25f,
            0.32f, "paid you ", "ERROR 200 OK", AlertSounds.BLACKHOLE_COLLAPSE);

    static final EffectStyle SNOW_GLOBE = of("snow_globe",
            new String[]{"Gentle", "Blizzard", "Holiday"},
            7.5f, 0.5f, 12, 0.3f,
            16f, 2.5f, 1.0f, 40f, 0.8f, 0.6f,
            true, false, false, true,
            false, 0, false, 0.1f, 0.1f, 0.1f,
            0.26f, "paid you ", "Winter is here", AlertSounds.JACKPOT_CHIME);

    static final EffectStyle THUNDER_CLAP = of("thunder_clap",
            new String[]{"Single", "Rolling", "God storm"},
            6.5f, 0.3f, 8, 2.0f,
            5f, 0.8f, 0.9f, 50f, 0.9f, 1.2f,
            true, false, true, false,
            false, 0, true, 0.5f, 1.0f, 0.5f,
            0.22f, "paid you ", "BOOM", AlertSounds.NUKE_THUNDER);

    static final EffectStyle MONEY_TORNADO = of("money_tornado",
            new String[]{"Bills", "Coins", "Gems"},
            7.0f, 0.25f, 50, 1.1f,
            20f, 1.5f, 1.2f, 30f, 0.85f, 1.4f,
            false, false, false, true,
            false, 0, false, 0.15f, 0.45f, 0.3f,
            0.27f, "paid you ", "Whirlwind of cash", AlertSounds.MONEY_COIN);

    static final EffectStyle RAINBOW_ROAD = of("rainbow_road",
            new String[]{"Mario", "Prism", "Neon path"},
            7.5f, 0.2f, 36, 0.8f,
            14f, 1.8f, 1.1f, 70f, 1.0f, 1.0f,
            true, true, false, true,
            true, 14, false, 0.1f, 0.2f, 0.2f,
            0.25f, "paid you ", "Follow the rainbow", AlertSounds.JACKPOT_PLING);

    static final EffectStyle BLACKOUT = of("blackout",
            new String[]{"Cut", "Flicker", "Hard cut"},
            5.0f, 0.4f, 10, 0.5f,
            2f, 0.6f, 0.7f, 10f, 0.4f, 0.5f,
            false, false, true, false,
            false, 0, true, 0.75f, 0.3f, 0.2f,
            0.4f, "paid you ", "...", AlertSounds.BLACKHOLE_BANG);

    static final EffectStyle SUPERNOVA_LITE = of("supernova_lite",
            new String[]{"White", "Blue", "Gold"},
            9.0f, 0.7f, 70, 1.6f,
            8f, 1.4f, 1.5f, 40f, 0.9f, 1.2f,
            false, false, false, true,
            true, 18, true, 0.4f, 0.7f, 0.4f,
            0.2f, "paid you ", "STAR COLLAPSE", AlertSounds.NUKE_BLAST);

    static final EffectStyle CASH_CANNON = of("cash_cannon",
            new String[]{"Single barrel", "Double", "Gatling"},
            6.0f, 0.15f, 44, 1.4f,
            12f, 1.3f, 1.3f, 180f, 1.2f, 1.5f,
            false, false, false, false,
            false, 0, true, 0.1f, 0.5f, 0.25f,
            0.3f, "paid you ", "Ka-ching!", AlertSounds.MONEY_DING);

    static final EffectStyle PORTAL_RIFT = of("portal_rift",
            new String[]{"Purple", "Green", "Void"},
            7.5f, 0.5f, 32, 1.0f,
            10f, 1.5f, 1.0f, 20f, 0.7f, 1.1f,
            false, false, true, true,
            false, 0, true, 0.35f, 0.4f, 0.3f,
            0.25f, "paid you ", "Rift opened", AlertSounds.BLACKHOLE_PORTAL);

    static final EffectStyle EMP_BLAST = of("emp_blast",
            new String[]{"Pulse", "Ring", "Grid kill"},
            6.0f, 0.35f, 20, 1.7f,
            7f, 1.0f, 0.9f, 10f, 0.6f, 1.3f,
            false, false, false, false,
            false, 0, true, 0.3f, 0.7f, 0.35f,
            0.28f, "paid you ", "SYSTEMS DOWN", AlertSounds.NUKE_SHOCKWAVE);

    static final EffectStyle CHERRY_BLOSSOM = of("cherry_blossom",
            new String[]{"Gentle", "Wind", "Festival"},
            8.0f, 0.4f, 18, 0.4f,
            14f, 2.4f, 1.2f, 50f, 0.85f, 0.7f,
            true, true, false, true,
            false, 0, false, 0.08f, 0.1f, 0.1f,
            0.24f, "paid you ", "Spring arrives", AlertSounds.JACKPOT_CHIME);

    static final EffectStyle METEOR_MINI = of("meteor_mini",
            new String[]{"Shower", "Bolide", "Impact"},
            7.0f, 0.55f, 14, 1.5f,
            8f, 1.2f, 1.4f, 200f, 1.0f, 1.0f,
            true, false, true, false,
            false, 0, true, 0.35f, 0.9f, 0.4f,
            0.22f, "paid you ", "Incoming!", AlertSounds.NUKE_SWOOP);

    static final EffectStyle ROYAL_FANFARE = of("royal_fanfare",
            new String[]{"Gold", "Purple", "Empire"},
            7.5f, 0.3f, 28, 0.9f,
            9f, 1.6f, 1.2f, 90f, 1.1f, 0.9f,
            false, false, false, true,
            true, 12, false, 0.12f, 0.3f, 0.2f,
            0.26f, "paid you ", "All hail", AlertSounds.JACKPOT_BELL);

    static final EffectStyle SANDSTORM = of("sandstorm",
            new String[]{"Desert", "Dust devil", "Haboob"},
            7.0f, 0.35f, 22, 0.8f,
            24f, 1.8f, 1.0f, 40f, 0.9f, 1.4f,
            false, false, true, false,
            false, 0, false, 0.2f, 0.5f, 0.35f,
            0.3f, "paid you ", "Visibility zero", AlertSounds.NUKE_RUMBLE);

    static final EffectStyle ICE_AGE = of("ice_age",
            new String[]{"Frost", "Shatter", "Glacier"},
            7.5f, 0.4f, 26, 0.7f,
            12f, 2.0f, 1.3f, 80f, 1.0f, 0.8f,
            true, false, false, true,
            false, 0, true, 0.25f, 0.35f, 0.25f,
            0.25f, "paid you ", "Absolute zero", AlertSounds.BLACKHOLE_COLLAPSE);

    static final EffectStyle NEON_RAIN = of("neon_rain",
            new String[]{"City", "Blade runner", "Acid"},
            7.0f, 0.2f, 20, 0.6f,
            18f, 1.5f, 0.9f, 160f, 0.95f, 0.9f,
            true, false, false, true,
            false, 0, false, 0.3f, 0.2f, 0.15f,
            0.28f, "paid you ", "Neon nights", AlertSounds.BLACKHOLE_CHARGE);

    static final EffectStyle CONFETTI_NUKE = of("confetti_nuke",
            new String[]{"Party", "Wedding", "Carnival"},
            8.0f, 0.5f, 100, 1.5f,
            16f, 1.8f, 1.5f, 140f, 1.1f, 1.3f,
            false, true, false, false,
            true, 20, true, 0.2f, 0.6f, 0.35f,
            0.25f, "paid you ", "PARTY NUKE", AlertSounds.JACKPOT_PLING);

    static final EffectStyle GOD_RAYS = of("god_rays",
            new String[]{"Heaven", "Judgement", "Blessing"},
            8.5f, 0.35f, 16, 0.5f,
            6f, 2.0f, 1.0f, -20f, 0.7f, 0.6f,
            false, false, false, true,
            false, 0, true, 0.15f, 0.2f, 0.2f,
            0.2f, "paid you ", "Divine favor", AlertSounds.MONEY_LEVELUP);

    private static EffectStyle of(
            String id, String[] vars, float duration, float burstAt, int burstCount, float burstPower,
            float particleRate, float particleLife, float particleSize, float particleGravity,
            float particleDrag, float particleSpread, boolean fromTop, boolean confetti, boolean smoke,
            boolean twinkle, boolean fireworks, int rockets, boolean flash, float darken,
            float shake, float shakeDur, float bannerY, String subPrefix, String tagline, SoundEvent sound
    ) {
        return new EffectStyle(id, vars, duration, burstAt, burstCount, burstPower, particleRate,
                particleLife, particleSize, particleGravity, particleDrag, particleSpread, fromTop,
                confetti, smoke, twinkle, fireworks, rockets, flash, darken, shake, shakeDur,
                bannerY, subPrefix, tagline, sound);
    }
}
