package com.paymentalerts.alert;

import com.paymentalerts.config.PaymentAlertsConfig;

public enum Tier {
    NONE("none", "Nothing", Long.MAX_VALUE, Kind.NONE, 8421504, "Nothing happens."),

    COIN_POP("coin_pop", "Coin pop", 1_000_000L, Kind.SIDE, 0xF2C14E, "A small pill in the top right with the payer's face and a coin."),
    TAB("tab", "Skin tab", 10_000_000L, Kind.SIDE, 0x4CD964, "A tab in the top right with the payer's 3D skin crouch-dancing."),
    COIN_FOUNTAIN("coin_fountain", "Coin fountain", 25_000_000L, Kind.SIDE, 0xC9C9C6, "A silver skin tab while coins shoot up from the bottom corners."),
    MONEY_RAIN("money_rain", "Money rain", 50_000_000L, Kind.SIDE, 0xFFD74D, "A gold skin tab while cash and loot rain down the screen."),

    SPARKLE_BURST("sparkle_burst", "Sparkle burst", 75_000_000L, Kind.STAGE, 0xFFE066, "Soft sparkle explosion with themed colors."),
    CASH_CANNON("cash_cannon", "Cash cannon", 90_000_000L, Kind.STAGE, 0xFFD700, "Bills and coins blast from center."),
    FIREWORKS("fireworks", "Fireworks", 100_000_000L, Kind.STAGE, 0xFF6B3E, "The payer's name in bold at the top with fireworks."),
    PARTY_POPPER("party_popper", "Party popper", 150_000_000L, Kind.STAGE, 0xFF69B4, "Confetti cannons and party fireworks."),
    CONFETTI("confetti", "Confetti cannons", 200_000_000L, Kind.STAGE, 0xFF6B99, "Party cannons blast confetti across the screen."),
    HEART_BOMB("heart_bomb", "Heart bomb", 220_000_000L, Kind.STAGE, 0xFF1493, "A burst of hearts and love confetti."),
    LIGHTNING("lightning", "Lightning storm", 250_000_000L, Kind.STAGE, 0x6F8FFF, "Storm clouds, rain and lightning strikes."),
    THUNDER_CLAP("thunder_clap", "Thunder clap", 300_000_000L, Kind.STAGE, 0xFFD700, "A heavy thunder hit with flash and shake."),
    DISCO_BALL("disco_ball", "Disco ball", 350_000_000L, Kind.STAGE, 0xFF00FF, "Strobe lights, confetti and dance fireworks."),
    JACKPOT("jackpot", "Jackpot", 500_000_000L, Kind.STAGE, 0xFFBF00, "A slot machine spins and hits the jackpot."),
    PIXEL_STORM("pixel_storm", "Pixel storm", 550_000_000L, Kind.STAGE, 0x39FF14, "Retro pixel particles and level-up energy."),
    TREASURE("treasure", "Treasure chest", 750_000_000L, Kind.STAGE, 0xD0A45C, "A treasure chest drops in, bursts open and sprays loot."),
    GOLDEN_SHOWER("golden_shower", "Golden rain", 800_000_000L, Kind.STAGE, 0xFFD700, "Dense golden coin rain from the top of the screen."),
    MONEY_TORNADO("money_tornado", "Money tornado", 900_000_000L, Kind.STAGE, 0xC9A227, "A swirling tornado of cash and coins."),
    NUKE("nuke", "Nuke", 1_000_000_000L, Kind.STAGE, 0xFF6B2E, "A bomb falls, then a flash, a shockwave and a mushroom cloud."),
    CONFETTI_NUKE("confetti_nuke", "Confetti nuke", 1_200_000_000L, Kind.STAGE, 0xFF85C1, "Party nuke — massive confetti + fireworks."),
    STARFALL("starfall", "Starfall", 1_500_000_000L, Kind.STAGE, 0xE0AAFF, "Stars drift down from a dark sky."),
    AURORA("aurora", "Aurora", 1_800_000_000L, Kind.STAGE, 0x00F5D4, "Northern-lights style flowing particles."),
    CHERRY_BLOSSOM("cherry_blossom", "Cherry blossom", 2_000_000_000L, Kind.STAGE, 0xFFB7C5, "Gentle petals in the wind."),
    METEORS("meteors", "Meteor shower", 2_500_000_000L, Kind.STAGE, 0xFF4D1F, "Night falls and meteors rain down, ending with a giant impact."),
    METEOR_MINI("meteor_mini", "Mini meteors", 2_800_000_000L, Kind.STAGE, 0xFF6347, "A smaller but fierce meteor barrage."),
    SANDSTORM("sandstorm", "Sandstorm", 3_000_000_000L, Kind.STAGE, 0xE9C46A, "Blinding desert dust and wind."),
    NEON_RAIN("neon_rain", "Neon rain", 3_500_000_000L, Kind.STAGE, 0x00FFFF, "Cyberpunk rain in neon colors."),
    ICE_AGE("ice_age", "Ice age", 4_000_000_000L, Kind.STAGE, 0xA8DADC, "Frost particles and a freezing flash."),
    ORBITAL_LASER("orbital_laser", "Orbital laser", 5_000_000_000L, Kind.STAGE, 0x4FD1FF, "A satellite locks on and fires a giant laser."),
    LASER_GRID("laser_grid", "Laser grid", 5_500_000_000L, Kind.STAGE, 0xFF00A0, "Scanning laser grid and target lock."),
    EMP_BLAST("emp_blast", "EMP blast", 6_000_000_000L, Kind.STAGE, 0x00E5FF, "Electromagnetic pulse ring and system shock."),
    TSUNAMI("tsunami", "Tsunami", 7_000_000_000L, Kind.STAGE, 0x0077B6, "A wall of water energy rolls across."),
    VOLCANO("volcano", "Volcano", 8_000_000_000L, Kind.STAGE, 0xFF4500, "Eruption of lava sparks and ash."),
    PHOENIX("phoenix", "Phoenix", 9_000_000_000L, Kind.STAGE, 0xFF6B35, "Rebirth in fire — rising sparks and flame."),
    BLACK_HOLE("black_hole", "Black hole", 10_000_000_000L, Kind.STAGE, 0xA06BFF, "A black hole swallows the screen, then a big bang."),
    PORTAL_RIFT("portal_rift", "Portal rift", 12_000_000_000L, Kind.STAGE, 0x9B59B6, "A dimensional rift tears open."),
    GLITCH("glitch", "Glitch", 15_000_000_000L, Kind.STAGE, 0x39FF14, "Corrupted matrix static and hard cuts."),
    BLACKOUT("blackout", "Blackout", 20_000_000_000L, Kind.STAGE, 0x222222, "Screen hard-cuts to black with a sparse burst."),
    RAINBOW_ROAD("rainbow_road", "Rainbow road", 25_000_000_000L, Kind.STAGE, 0xFF6EC7, "Prism confetti road and rainbow fireworks."),
    ROYAL_FANFARE("royal_fanfare", "Royal fanfare", 35_000_000_000L, Kind.STAGE, 0xD4AF37, "Imperial gold celebration."),
    SUPERNOVA("supernova", "Supernova", 50_000_000_000L, Kind.STAGE, 0xFFE29A, "A star swells, collapses and explodes into a nebula."),
    SUPERNOVA_LITE("supernova_lite", "Mini supernova", 60_000_000_000L, Kind.STAGE, 0xFFFFFF, "A condensed star-collapse finale."),
    GOD_RAYS("god_rays", "God rays", 80_000_000_000L, Kind.STAGE, 0xFFF8DC, "Heavenly beams and blessing particles."),
    GOD_TIER("god_tier", "God tier", 100_000_000_000L, Kind.STAGE, 0xFFE44D, "The payer appears giant with a crown, crouch-dancing in a beam of light."),

    /**
     * The developer effect. It sits above god tier on the ladder, but it is really
     * bound to a name: any payment from Lumindev plays it regardless of amount.
     */
    LUMINDEV("lumindev", "Lumindev", 250_000_000_000L, Kind.STAGE, 0xA77BFF, "A rift opens, the payer descends in a beam of light, and the screen collapses into a whiteout."),

    /**
     * Not a payment tier: this one is bound to an event. It fires when the channel goes
     * live (chat detection) or when it is triggered by hand with
     * {@code /payalerts stream}. It never participates in the amount ladder.
     */
    STREAM("stream", "Stream live", 0L, Kind.EVENT, 0xFF0033, "Broadcast takeover: tally lights, a lens opening on the channel, chat chips and a subscribe press.");

    public static final Tier[] EFFECTS;

    static {
        Tier[] all = values();
        EFFECTS = new Tier[all.length - 1];
        System.arraycopy(all, 1, EFFECTS, 0, EFFECTS.length);
    }

    public final String id;
    public final String label;
    public final long defaultThreshold;
    public final Kind kind;
    public final int color;
    public final String description;

    public enum Kind {
        NONE,
        SIDE,
        STAGE,
        /** Not on the amount ladder: only fired explicitly (streams, events). */
        EVENT
    }

    Tier(String id, String label, long defaultThreshold, Kind kind, int color, String description) {
        this.id = id;
        this.label = label;
        this.defaultThreshold = defaultThreshold;
        this.kind = kind;
        this.color = color;
        this.description = description;
    }

    public long threshold() {
        if (this == NONE) {
            return Long.MAX_VALUE;
        }
        return PaymentAlertsConfig.get().tier(this).threshold;
    }

    public void setThreshold(long j) {
        if (this != NONE) {
            PaymentAlertsConfig.get().tier(this).threshold = Math.max(1L, j);
        }
    }

    public boolean isEnabled() {
        return this != NONE && PaymentAlertsConfig.get().tier(this).enabled;
    }

    public void setEnabled(boolean z) {
        if (this != NONE) {
            PaymentAlertsConfig.get().tier(this).enabled = z;
        }
    }

    public int look() {
        if (this == NONE) {
            return 0;
        }
        return PaymentAlertsConfig.get().tier(this).look;
    }

    public void setLook(int i) {
        if (this != NONE) {
            String[] v = variations();
            PaymentAlertsConfig.get().tier(this).look = Math.max(-1, Math.min(v.length - 1, i));
        }
    }

    public String[] variations() {
        if (this == NONE) {
            return new String[]{"-"};
        }
        return switch (this) {
            case COIN_POP -> CoinPopAlert.VARIATIONS;
            case TAB -> TabAlert.VARIATIONS;
            case COIN_FOUNTAIN -> CoinFountainAlert.VARIATIONS;
            case MONEY_RAIN -> MoneyRainAlert.VARIATIONS;
            case FIREWORKS -> FireworksAlert.VARIATIONS;
            case CONFETTI -> ConfettiAlert.VARIATIONS;
            case LIGHTNING -> LightningAlert.VARIATIONS;
            case JACKPOT -> JackpotAlert.VARIATIONS;
            case TREASURE -> TreasureChestAlert.VARIATIONS;
            case NUKE -> NukeAlert.VARIATIONS;
            case METEORS -> MeteorShowerAlert.VARIATIONS;
            case ORBITAL_LASER -> OrbitalLaserAlert.VARIATIONS;
            case BLACK_HOLE -> BlackHoleAlert.VARIATIONS;
            case SUPERNOVA -> SupernovaAlert.VARIATIONS;
            case GOD_TIER -> GodTierAlert.VARIATIONS;
            case LUMINDEV -> LumindevAlert.VARIATIONS;
            case SPARKLE_BURST -> SparkleBurstAlert.VARIATIONS;
            case GOLDEN_SHOWER -> GoldenShowerAlert.VARIATIONS;
            case PARTY_POPPER -> PartyPopperAlert.VARIATIONS;
            case STARFALL -> StarfallAlert.VARIATIONS;
            case HEART_BOMB -> HeartBombAlert.VARIATIONS;
            case LASER_GRID -> LaserGridAlert.VARIATIONS;
            case TSUNAMI -> TsunamiAlert.VARIATIONS;
            case VOLCANO -> VolcanoAlert.VARIATIONS;
            case AURORA -> AuroraAlert.VARIATIONS;
            case DISCO_BALL -> DiscoBallAlert.VARIATIONS;
            case PIXEL_STORM -> PixelStormAlert.VARIATIONS;
            case PHOENIX -> PhoenixAlert.VARIATIONS;
            case GLITCH -> GlitchAlert.VARIATIONS;
            case THUNDER_CLAP -> ThunderClapAlert.VARIATIONS;
            case MONEY_TORNADO -> MoneyTornadoAlert.VARIATIONS;
            case RAINBOW_ROAD -> RainbowRoadAlert.VARIATIONS;
            case BLACKOUT -> BlackoutAlert.VARIATIONS;
            case SUPERNOVA_LITE -> SupernovaLiteAlert.VARIATIONS;
            case CASH_CANNON -> CashCannonAlert.VARIATIONS;
            case PORTAL_RIFT -> PortalRiftAlert.VARIATIONS;
            case EMP_BLAST -> EmpBlastAlert.VARIATIONS;
            case CHERRY_BLOSSOM -> CherryBlossomAlert.VARIATIONS;
            case METEOR_MINI -> MeteorMiniAlert.VARIATIONS;
            case ROYAL_FANFARE -> RoyalFanfareAlert.VARIATIONS;
            case SANDSTORM -> SandstormAlert.VARIATIONS;
            case ICE_AGE -> IceAgeAlert.VARIATIONS;
            case NEON_RAIN -> NeonRainAlert.VARIATIONS;
            case CONFETTI_NUKE -> ConfettiNukeAlert.VARIATIONS;
            case GOD_RAYS -> GodRaysAlert.VARIATIONS;
            case STREAM -> StreamAlert.VARIATIONS;
            default -> new String[]{"Default"};
        };
    }

    public static Tier byId(String str) {
        for (Tier tier : values()) {
            if (tier.id.equalsIgnoreCase(str)) {
                return tier;
            }
        }
        return null;
    }
}
