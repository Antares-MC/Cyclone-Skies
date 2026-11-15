package com.paymentalerts.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paymentalerts.PaymentAlertsClient;
import com.paymentalerts.alert.Tier;
import com.paymentalerts.payment.PaymentParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;

public final class PaymentAlertsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("paymentalerts.json");
    private static PaymentAlertsConfig current = new PaymentAlertsConfig();
    private static PaymentParser parser = new PaymentParser(PaymentParser.DEFAULT_PATTERN);
    public boolean enabled = true;
    public boolean soundsEnabled = true;
    public boolean screenShakeEnabled = true;
    public String paymentPattern = PaymentParser.DEFAULT_PATTERN;
    public float soundVolume = 1.0f;
    public float shakeStrength = 1.0f;
    public float tabSeconds = 6.0f;
    /** Global theme id, or "random", or empty for Classic Gold */
    public String globalThemeId = "classic_gold";
    public boolean useMojangApiForSkins = true;
    public Map<String, TierSettings> tiers = new LinkedHashMap();
    private Boolean randomVariations;
    private Boolean tabEnabled;
    private Boolean moneyRainEnabled;
    private Boolean fireworksEnabled;
    private Boolean lightningEnabled;
    private Boolean jackpotEnabled;
    private Boolean nukeEnabled;
    private Boolean orbitalLaserEnabled;
    private Boolean blackHoleEnabled;
    private Boolean godTierEnabled;
    private Long tabThreshold;
    private Long moneyRainThreshold;
    private Long fireworksThreshold;
    private Long lightningThreshold;
    private Long jackpotThreshold;
    private Long nukeThreshold;
    private Long orbitalLaserThreshold;
    private Long blackHoleThreshold;
    private Long godTierThreshold;

    public static final class TierSettings {
        public long threshold;
        public boolean enabled = true;
        public int look = -1;
        /** Per-tier theme override (null/empty = use global). "random" = pick each time. */
        public String themeId = "";
    }

    public TierSettings tier(Tier tier) {
        return this.tiers.computeIfAbsent(tier.id, str -> {
            TierSettings tierSettings = new TierSettings();
            tierSettings.threshold = tier.defaultThreshold;
            return tierSettings;
        });
    }

    public static PaymentAlertsConfig get() {
        return current;
    }

    public static PaymentParser parser() {
        return parser;
    }

    public static void load() {
        PaymentAlertsConfig paymentAlertsConfig = null;
        if (Files.exists(FILE, new LinkOption[0])) {
            try {
                BufferedReader newBufferedReader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8);
                try {
                    paymentAlertsConfig = (PaymentAlertsConfig) GSON.fromJson(newBufferedReader, PaymentAlertsConfig.class);
                    if (newBufferedReader != null) {
                        newBufferedReader.close();
                    }
                } finally {
                }
            } catch (Exception e) {
                PaymentAlertsClient.LOGGER.error("Could not read {}, using defaults", FILE, e);
            }
        }
        if (paymentAlertsConfig == null) {
            paymentAlertsConfig = new PaymentAlertsConfig();
        }
        paymentAlertsConfig.sanitize();
        current = paymentAlertsConfig;
        parser = new PaymentParser(paymentAlertsConfig.paymentPattern);
        if (parser.isUsingDefault() && !PaymentParser.DEFAULT_PATTERN.equals(paymentAlertsConfig.paymentPattern)) {
            PaymentAlertsClient.LOGGER.warn("paymentPattern in {} is not a valid regex with name/amount groups; using the default", FILE);
        }
        save();
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent(), new FileAttribute[0]);
            BufferedWriter newBufferedWriter = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8, new OpenOption[0]);
            try {
                GSON.toJson(current, newBufferedWriter);
                if (newBufferedWriter != null) {
                    newBufferedWriter.close();
                }
            } finally {
            }
        } catch (IOException e) {
            PaymentAlertsClient.LOGGER.error("Could not write {}", FILE, e);
        }
    }

    private void sanitize() {
        if (this.paymentPattern == null || this.paymentPattern.isBlank()) {
            this.paymentPattern = PaymentParser.DEFAULT_PATTERN;
        }
        this.soundVolume = clamp(this.soundVolume, 0.0f, 1.0f);
        this.shakeStrength = clamp(this.shakeStrength, 0.0f, 2.0f);
        this.tabSeconds = clamp(this.tabSeconds, 1.0f, 60.0f);
        if (this.tiers == null) {
            this.tiers = new LinkedHashMap();
        }
        migrateOldFields();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Tier tier : Tier.EFFECTS) {
            TierSettings tier2 = tier(tier);
            if (tier2.threshold < 1) {
                tier2.threshold = tier.defaultThreshold;
            }
            tier2.look = Math.max(-1, Math.min(tier.variations().length - 1, tier2.look));
            linkedHashMap.put(tier.id, tier2);
        }
        this.tiers = linkedHashMap;
    }

    private void migrateOldFields() {
        migrate(Tier.TAB, this.tabEnabled, this.tabThreshold);
        migrate(Tier.MONEY_RAIN, this.moneyRainEnabled, this.moneyRainThreshold);
        migrate(Tier.FIREWORKS, this.fireworksEnabled, this.fireworksThreshold);
        migrate(Tier.LIGHTNING, this.lightningEnabled, this.lightningThreshold);
        migrate(Tier.JACKPOT, this.jackpotEnabled, this.jackpotThreshold);
        migrate(Tier.NUKE, this.nukeEnabled, this.nukeThreshold);
        migrate(Tier.ORBITAL_LASER, this.orbitalLaserEnabled, this.orbitalLaserThreshold);
        migrate(Tier.BLACK_HOLE, this.blackHoleEnabled, this.blackHoleThreshold);
        migrate(Tier.GOD_TIER, this.godTierEnabled, this.godTierThreshold);
        if (Boolean.FALSE.equals(this.randomVariations)) {
            for (Tier tier : Tier.EFFECTS) {
                tier(tier).look = 0;
            }
        }
        this.randomVariations = null;
        this.jackpotEnabled = null;
        this.lightningEnabled = null;
        this.fireworksEnabled = null;
        this.moneyRainEnabled = null;
        this.tabEnabled = null;
        this.godTierEnabled = null;
        this.blackHoleEnabled = null;
        this.orbitalLaserEnabled = null;
        this.nukeEnabled = null;
        this.jackpotThreshold = null;
        this.lightningThreshold = null;
        this.fireworksThreshold = null;
        this.moneyRainThreshold = null;
        this.tabThreshold = null;
        this.godTierThreshold = null;
        this.blackHoleThreshold = null;
        this.orbitalLaserThreshold = null;
        this.nukeThreshold = null;
    }

    private void migrate(Tier tier, Boolean bool, Long l) {
        if (bool != null) {
            tier(tier).enabled = bool.booleanValue();
        }
        if (l != null && l.longValue() > 0) {
            tier(tier).threshold = l.longValue();
        }
    }

    private static float clamp(float f, float f2, float f3) {
        return Float.isNaN(f) ? f3 : Math.max(f2, Math.min(f3, f));
    }
}
