package com.paymentalerts.theme;

import com.paymentalerts.alert.Tier;
import com.paymentalerts.config.PaymentAlertsConfig;

/**
 * Resolves which theme to use for a given tier / payment.
 * Order: per-tier override → global theme → Classic Gold.
 */
public final class ThemeService {
    private static volatile Theme active = Themes.CLASSIC_GOLD;

    private ThemeService() {}

    /** Theme bound for the alert currently being built / rendered. */
    public static Theme current() {
        return active != null ? active : Themes.CLASSIC_GOLD;
    }

    public static void bind(Theme theme) {
        active = theme != null ? theme : Themes.CLASSIC_GOLD;
    }

    public static void bindForTier(Tier tier) {
        bind(forTier(tier));
    }

    public static Theme forTier(Tier tier) {
        PaymentAlertsConfig cfg = PaymentAlertsConfig.get();
        String perTier = null;
        if (tier != null && tier != Tier.NONE) {
            PaymentAlertsConfig.TierSettings ts = cfg.tier(tier);
            if (ts.themeId != null && !ts.themeId.isEmpty()) {
                perTier = ts.themeId;
            }
        }
        if (perTier != null) {
            return Themes.resolve(perTier, Themes.CLASSIC_GOLD);
        }
        return Themes.resolve(cfg.globalThemeId, Themes.CLASSIC_GOLD);
    }

    public static Theme global() {
        return Themes.resolve(PaymentAlertsConfig.get().globalThemeId, Themes.CLASSIC_GOLD);
    }

    /** Cycle to next theme id for UI buttons */
    public static String nextThemeId(String current) {
        if (current == null || current.isEmpty() || "random".equalsIgnoreCase(current)) {
            return Themes.all().get(0).id;
        }
        var all = Themes.all();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).id.equalsIgnoreCase(current)) {
                return all.get((i + 1) % all.size()).id;
            }
        }
        return all.get(0).id;
    }

    public static String prevThemeId(String current) {
        if (current == null || current.isEmpty() || "random".equalsIgnoreCase(current)) {
            return Themes.all().get(Themes.count() - 1).id;
        }
        var all = Themes.all();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).id.equalsIgnoreCase(current)) {
                return all.get((i - 1 + all.size()) % all.size()).id;
            }
        }
        return all.get(0).id;
    }

    public static String displayName(String themeId) {
        if (themeId == null || themeId.isEmpty() || "default".equalsIgnoreCase(themeId)) {
            return "Default (Classic Gold)";
        }
        if ("random".equalsIgnoreCase(themeId)) {
            return "Random each alert";
        }
        Theme t = Themes.byId(themeId);
        return t != null ? t.name : themeId;
    }
}
