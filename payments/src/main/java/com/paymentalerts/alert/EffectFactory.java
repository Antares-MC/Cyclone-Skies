package com.paymentalerts.alert;

import com.paymentalerts.payment.Payment;

/**
 * Creates stage / side alerts for each {@link Tier}.
 * Original handcrafted effects keep their classes; new ones use {@link PresetStageAlert}.
 */
final class EffectFactory {
    private EffectFactory() {}

    static StageAlert createStage(Tier tier, Payment payment, long now, int variation) {
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
            // Preset-driven expansions
            case SPARKLE_BURST -> new PresetStageAlert(payment, now, EffectStyle.SPARKLE_BURST, variation);
            case GOLDEN_SHOWER -> new PresetStageAlert(payment, now, EffectStyle.GOLDEN_SHOWER, variation);
            case PARTY_POPPER -> new PresetStageAlert(payment, now, EffectStyle.PARTY_POPPER, variation);
            case STARFALL -> new PresetStageAlert(payment, now, EffectStyle.STARFALL, variation);
            case HEART_BOMB -> new PresetStageAlert(payment, now, EffectStyle.HEART_BOMB, variation);
            case LASER_GRID -> new PresetStageAlert(payment, now, EffectStyle.LASER_GRID, variation);
            case TSUNAMI -> new PresetStageAlert(payment, now, EffectStyle.TSUNAMI, variation);
            case VOLCANO -> new PresetStageAlert(payment, now, EffectStyle.VOLCANO, variation);
            case AURORA -> new PresetStageAlert(payment, now, EffectStyle.AURORA, variation);
            case DISCO_BALL -> new PresetStageAlert(payment, now, EffectStyle.DISCO_BALL, variation);
            case PIXEL_STORM -> new PresetStageAlert(payment, now, EffectStyle.PIXEL_STORM, variation);
            case PHOENIX -> new PresetStageAlert(payment, now, EffectStyle.PHOENIX, variation);
            case GLITCH -> new PresetStageAlert(payment, now, EffectStyle.GLITCH, variation);
            case THUNDER_CLAP -> new PresetStageAlert(payment, now, EffectStyle.THUNDER_CLAP, variation);
            case MONEY_TORNADO -> new PresetStageAlert(payment, now, EffectStyle.MONEY_TORNADO, variation);
            case RAINBOW_ROAD -> new PresetStageAlert(payment, now, EffectStyle.RAINBOW_ROAD, variation);
            case BLACKOUT -> new PresetStageAlert(payment, now, EffectStyle.BLACKOUT, variation);
            case SUPERNOVA_LITE -> new PresetStageAlert(payment, now, EffectStyle.SUPERNOVA_LITE, variation);
            case CASH_CANNON -> new PresetStageAlert(payment, now, EffectStyle.CASH_CANNON, variation);
            case PORTAL_RIFT -> new PresetStageAlert(payment, now, EffectStyle.PORTAL_RIFT, variation);
            case EMP_BLAST -> new PresetStageAlert(payment, now, EffectStyle.EMP_BLAST, variation);
            case CHERRY_BLOSSOM -> new PresetStageAlert(payment, now, EffectStyle.CHERRY_BLOSSOM, variation);
            case METEOR_MINI -> new PresetStageAlert(payment, now, EffectStyle.METEOR_MINI, variation);
            case ROYAL_FANFARE -> new PresetStageAlert(payment, now, EffectStyle.ROYAL_FANFARE, variation);
            case SANDSTORM -> new PresetStageAlert(payment, now, EffectStyle.SANDSTORM, variation);
            case ICE_AGE -> new PresetStageAlert(payment, now, EffectStyle.ICE_AGE, variation);
            case NEON_RAIN -> new PresetStageAlert(payment, now, EffectStyle.NEON_RAIN, variation);
            case CONFETTI_NUKE -> new PresetStageAlert(payment, now, EffectStyle.CONFETTI_NUKE, variation);
            case GOD_RAYS -> new PresetStageAlert(payment, now, EffectStyle.GOD_RAYS, variation);
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

    static EffectStyle styleFor(Tier tier) {
        return switch (tier) {
            case SPARKLE_BURST -> EffectStyle.SPARKLE_BURST;
            case GOLDEN_SHOWER -> EffectStyle.GOLDEN_SHOWER;
            case PARTY_POPPER -> EffectStyle.PARTY_POPPER;
            case STARFALL -> EffectStyle.STARFALL;
            case HEART_BOMB -> EffectStyle.HEART_BOMB;
            case LASER_GRID -> EffectStyle.LASER_GRID;
            case TSUNAMI -> EffectStyle.TSUNAMI;
            case VOLCANO -> EffectStyle.VOLCANO;
            case AURORA -> EffectStyle.AURORA;
            case DISCO_BALL -> EffectStyle.DISCO_BALL;
            case PIXEL_STORM -> EffectStyle.PIXEL_STORM;
            case PHOENIX -> EffectStyle.PHOENIX;
            case GLITCH -> EffectStyle.GLITCH;
            case THUNDER_CLAP -> EffectStyle.THUNDER_CLAP;
            case MONEY_TORNADO -> EffectStyle.MONEY_TORNADO;
            case RAINBOW_ROAD -> EffectStyle.RAINBOW_ROAD;
            case BLACKOUT -> EffectStyle.BLACKOUT;
            case SUPERNOVA_LITE -> EffectStyle.SUPERNOVA_LITE;
            case CASH_CANNON -> EffectStyle.CASH_CANNON;
            case PORTAL_RIFT -> EffectStyle.PORTAL_RIFT;
            case EMP_BLAST -> EffectStyle.EMP_BLAST;
            case CHERRY_BLOSSOM -> EffectStyle.CHERRY_BLOSSOM;
            case METEOR_MINI -> EffectStyle.METEOR_MINI;
            case ROYAL_FANFARE -> EffectStyle.ROYAL_FANFARE;
            case SANDSTORM -> EffectStyle.SANDSTORM;
            case ICE_AGE -> EffectStyle.ICE_AGE;
            case NEON_RAIN -> EffectStyle.NEON_RAIN;
            case CONFETTI_NUKE -> EffectStyle.CONFETTI_NUKE;
            case GOD_RAYS -> EffectStyle.GOD_RAYS;
            default -> null;
        };
    }
}
