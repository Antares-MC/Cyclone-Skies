package com.paymentalerts.alert;

import com.paymentalerts.payment.Payment;

/**
 * Creates stage / side alerts for each {@link Tier}.
 *
 * <p>Every stage tier maps to a dedicated class. There is no shared preset renderer:
 * each effect owns its own timeline, geometry, motion and palette.
 */
final class EffectFactory {
    private EffectFactory() {}

    static StageAlert createStage(Tier tier, Payment payment, long now, int variation, String note) {
        return switch (tier) {
            case FIREWORKS -> new FireworksAlert(payment, now, variation);
            case CONFETTI -> new ConfettiAlert(payment, now, variation);
            case LIGHTNING -> new LightningAlert(payment, now, variation);
            case JACKPOT -> new JackpotAlert(payment, now, variation);
            case TREASURE -> new TreasureChestAlert(payment, now, variation);
            case NUKE -> new NukeAlert(payment, now, variation);
            case METEORS -> new MeteorShowerAlert(payment, now, variation);
            case ORBITAL_LASER -> new OrbitalLaserAlert(payment, now, variation);
            case BLACK_HOLE -> new BlackHoleAlert(payment, now, variation);
            case SUPERNOVA -> new SupernovaAlert(payment, now, variation);
            case GOD_TIER -> new GodTierAlert(payment, now, variation);
            case LUMINDEV -> new LumindevAlert(payment, now, variation);
            case SPARKLE_BURST -> new SparkleBurstAlert(payment, now, variation);
            case GOLDEN_SHOWER -> new GoldenShowerAlert(payment, now, variation);
            case PARTY_POPPER -> new PartyPopperAlert(payment, now, variation);
            case STARFALL -> new StarfallAlert(payment, now, variation);
            case HEART_BOMB -> new HeartBombAlert(payment, now, variation);
            case LASER_GRID -> new LaserGridAlert(payment, now, variation);
            case TSUNAMI -> new TsunamiAlert(payment, now, variation);
            case VOLCANO -> new VolcanoAlert(payment, now, variation);
            case AURORA -> new AuroraAlert(payment, now, variation);
            case DISCO_BALL -> new DiscoBallAlert(payment, now, variation);
            case PIXEL_STORM -> new PixelStormAlert(payment, now, variation);
            case PHOENIX -> new PhoenixAlert(payment, now, variation);
            case GLITCH -> new GlitchAlert(payment, now, variation);
            case THUNDER_CLAP -> new ThunderClapAlert(payment, now, variation);
            case MONEY_TORNADO -> new MoneyTornadoAlert(payment, now, variation);
            case RAINBOW_ROAD -> new RainbowRoadAlert(payment, now, variation);
            case BLACKOUT -> new BlackoutAlert(payment, now, variation);
            case SUPERNOVA_LITE -> new SupernovaLiteAlert(payment, now, variation);
            case CASH_CANNON -> new CashCannonAlert(payment, now, variation);
            case PORTAL_RIFT -> new PortalRiftAlert(payment, now, variation);
            case EMP_BLAST -> new EmpBlastAlert(payment, now, variation);
            case CHERRY_BLOSSOM -> new CherryBlossomAlert(payment, now, variation);
            case METEOR_MINI -> new MeteorMiniAlert(payment, now, variation);
            case ROYAL_FANFARE -> new RoyalFanfareAlert(payment, now, variation);
            case SANDSTORM -> new SandstormAlert(payment, now, variation);
            case ICE_AGE -> new IceAgeAlert(payment, now, variation);
            case NEON_RAIN -> new NeonRainAlert(payment, now, variation);
            case CONFETTI_NUKE -> new ConfettiNukeAlert(payment, now, variation);
            case GOD_RAYS -> new GodRaysAlert(payment, now, variation);
            case STREAM -> new StreamAlert(payment, now, variation, note);
            default -> new FireworksAlert(payment, now, variation);
        };
    }

    static SideAlert createSide(Tier tier, Payment payment, long now, int variation) {
        return switch (tier) {
            case COIN_POP -> new CoinPopAlert(payment, now, variation);
            case TAB -> new TabAlert(payment, now, TabAlert.TabStyle.NORMAL, variation);
            case COIN_FOUNTAIN -> new TabAlert(payment, now, TabAlert.TabStyle.SILVER, variation);
            case MONEY_RAIN -> new TabAlert(payment, now, TabAlert.TabStyle.GOLD, variation);
            default -> new CoinPopAlert(payment, now, variation);
        };
    }
}
