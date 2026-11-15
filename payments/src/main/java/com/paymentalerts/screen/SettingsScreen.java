package com.paymentalerts.screen;

import com.paymentalerts.PaymentAlertsClient;
import com.paymentalerts.alert.AlertManager;
import com.paymentalerts.alert.Tier;
import com.paymentalerts.config.PaymentAlertsConfig;
import com.paymentalerts.theme.Theme;
import com.paymentalerts.theme.ThemeService;
import com.paymentalerts.theme.Themes;
import com.paymentalerts.payment.Payment;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SettingsScreen extends Screen {
    private static Page lastPage = Page.GENERAL;
    private static final int ROW = 24;
    private static final int TOP = 54;
    private final Screen parent;
    private Page page;
    private EffectsList effectsList;
    private Throwable error;

        private enum Page {
        GENERAL,
        EFFECTS,
        THEMES
    }

    public SettingsScreen(Screen screen) {
        super(Component.literal("Donut SMP Payment Addon"));
        this.page = lastPage;
        this.parent = screen;
    }

    protected void init() {
        int i = this.width / 2;
        Button build = Button.builder(Component.literal("General"), button -> {
            switchTo(Page.GENERAL);
        }).bounds(i - 168, ROW, 108, 20).build();
        Button build2 = Button.builder(Component.literal("Effects"), button2 -> {
            switchTo(Page.EFFECTS);
        }).bounds(i - 54, ROW, 108, 20).build();
        Button buildThemes = Button.builder(Component.literal("Themes (" + Themes.count() + ")"), button3 -> {
            switchTo(Page.THEMES);
        }).bounds(i + 60, ROW, 108, 20).build();
        build.active = this.page != Page.GENERAL;
        build2.active = this.page != Page.EFFECTS;
        buildThemes.active = this.page != Page.THEMES;
        addRenderableWidget(build);
        addRenderableWidget(build2);
        addRenderableWidget(buildThemes);
        this.error = null;
        try {
            if (this.page == Page.GENERAL) {
                initGeneral(i);
            } else if (this.page == Page.THEMES) {
                initThemes(i);
            } else {
                this.effectsList = new EffectsList(this.minecraft, this.width, (this.height - TOP) - 36, TOP, this::preview);
                addRenderableWidget(this.effectsList);
            }
        } catch (Throwable th) {
            this.error = th;
            PaymentAlertsClient.LOGGER.error("The payment settings failed to open", th);
        }
        addRenderableWidget(Button.builder(Component.literal("Clear alerts"), button3 -> {
            AlertManager.clear();
        }).bounds(i - 154, this.height - 28, 150, 20).tooltip(Tooltip.create(Component.literal("Stops every effect that is on screen right now."))).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), button4 -> {
            onClose();
        }).bounds(i + 4, this.height - 28, 150, 20).build());
    }

    private void initGeneral(int i) {
        PaymentAlertsConfig paymentAlertsConfig = PaymentAlertsConfig.get();
        int i2 = (i - 160) - 5;
        int i3 = i + 5;
        toggle(i2, 58, 160, "Alerts", paymentAlertsConfig.enabled, bool -> {
            paymentAlertsConfig.enabled = bool.booleanValue();
        }, "Master switch for everything (also the K key).");
        toggle(i3, 58, 160, "Sounds", paymentAlertsConfig.soundsEnabled, bool2 -> {
            paymentAlertsConfig.soundsEnabled = bool2.booleanValue();
        }, "Play the sound effects. Only you hear them.");
        int i4 = 58 + ROW;
        toggle(i2, i4, 160, "Screen shake", paymentAlertsConfig.screenShakeEnabled, bool3 -> {
            paymentAlertsConfig.screenShakeEnabled = bool3.booleanValue();
        }, "Shake the camera and HUD on big hits (lightning, nuke, meteors, ...).");
        addRenderableWidget(new Slider(i3, i4, 160, "Volume", paymentAlertsConfig.soundVolume, d -> {
            return Math.round(d * 100.0d) + "%";
        }, d2 -> {
            paymentAlertsConfig.soundVolume = (float) d2;
        }));
        int i5 = i4 + ROW;
        addRenderableWidget(new Slider(i2, i5, 160, "Shake strength", paymentAlertsConfig.shakeStrength / 2.0d, d3 -> {
            return Math.round(d3 * 200.0d) + "%";
        }, d4 -> {
            paymentAlertsConfig.shakeStrength = (float) (d4 * 2.0d);
        }));
        addRenderableWidget(new Slider(i3, i5, 160, "Tab time", (paymentAlertsConfig.tabSeconds - 2.0f) / 13.0f, d5 -> {
            return String.format(Locale.ROOT, "%.1fs", Double.valueOf(2.0d + (d5 * 13.0d)));
        }, d6 -> {
            paymentAlertsConfig.tabSeconds = (float) (2.0d + (d6 * 13.0d));
        }));
        int i6 = i5 + ROW;
        toggle(i2, i6, 160, "Skin lookup", paymentAlertsConfig.useMojangApiForSkins, bool4 -> {
            paymentAlertsConfig.useMojangApiForSkins = bool4.booleanValue();
        }, "If the payer isn't in the tab list, fetch their skin from Mojang.");
        int i7 = 0;
        for (Tier tier : Tier.EFFECTS) {
            if (tier.isEnabled()) {
                i7++;
            }
        }
        addRenderableWidget(Button.builder(Component.literal("Effects on: " + i7 + "/" + Tier.EFFECTS.length + " >"), button -> {
            switchTo(Page.EFFECTS);
        }).bounds(i3, i6, 160, 20).tooltip(Tooltip.create(Component.literal("Pick amounts, looks and previews for each effect."))).build());
        addRenderableWidget(Button.builder(Component.literal("Payment stats (J) >"), button2 -> {
            this.minecraft.setScreen(new StatsScreen(this));
        }).bounds(i2, i6 + ROW, (160 * 2) + 10, 20).tooltip(Tooltip.create(Component.literal("Top payers and how much you made this session, per session and in total."))).build());
    }

    private void toggle(int i, int i2, int i3, String str, boolean z, Consumer<Boolean> consumer, String str2) {
        addRenderableWidget(CycleButton.onOffBuilder(z).withTooltip(bool -> {
            return Tooltip.create(Component.literal(str2));
        }).create(i, i2, i3, 20, Component.literal(str), (cycleButton, bool2) -> {
            consumer.accept(bool2);
        }));
    }


    private void initThemes(int centerX) {
        PaymentAlertsConfig cfg = PaymentAlertsConfig.get();
        int left = centerX - 160;
        int y = TOP + 8;

        String globalLabel = "Global: " + ThemeService.displayName(cfg.globalThemeId);
        addRenderableWidget(Button.builder(Component.literal("<"), b -> {
            cfg.globalThemeId = ThemeService.prevThemeId(cfg.globalThemeId);
            PaymentAlertsConfig.save();
            switchTo(Page.THEMES);
        }).bounds(left, y, 28, 20).build());
        addRenderableWidget(Button.builder(Component.literal(globalLabel), b -> {
            cfg.globalThemeId = ThemeService.nextThemeId(cfg.globalThemeId);
            PaymentAlertsConfig.save();
            switchTo(Page.THEMES);
        }).bounds(left + 32, y, 256, 20).tooltip(Tooltip.create(Component.literal(
            "Theme applied to every effect that has no per-tier override. Click to cycle. " + Themes.count() + " themes available."
        ))).build());
        addRenderableWidget(Button.builder(Component.literal(">"), b -> {
            cfg.globalThemeId = ThemeService.nextThemeId(cfg.globalThemeId);
            PaymentAlertsConfig.save();
            switchTo(Page.THEMES);
        }).bounds(left + 292, y, 28, 20).build());

        y += 28;
        addRenderableWidget(Button.builder(Component.literal("Set global = Random"), b -> {
            cfg.globalThemeId = "random";
            PaymentAlertsConfig.save();
            switchTo(Page.THEMES);
        }).bounds(left, y, 150, 20).tooltip(Tooltip.create(Component.literal("Pick a random theme for each alert."))).build());
        addRenderableWidget(Button.builder(Component.literal("Reset all tier themes"), b -> {
            for (Tier t : Tier.EFFECTS) {
                cfg.tier(t).themeId = "";
            }
            PaymentAlertsConfig.save();
            switchTo(Page.THEMES);
        }).bounds(left + 160, y, 160, 20).tooltip(Tooltip.create(Component.literal("Clear per-effect overrides so they all use the global theme."))).build());

        y += 32;
        int col = 0;
        for (Tier tier : Tier.EFFECTS) {
            PaymentAlertsConfig.TierSettings ts = cfg.tier(tier);
            String tid = ts.themeId == null || ts.themeId.isEmpty() ? "" : ts.themeId;
            String label = tier.label + ": " + (tid.isEmpty() ? "(global)" : ThemeService.displayName(tid));
            int bx = left + (col % 2) * 170;
            int by = y + (col / 2) * 22;
            if (by + 22 > this.height - 40) break;
            final Tier ft = tier;
            addRenderableWidget(Button.builder(Component.literal(label.length() > 28 ? label.substring(0, 26) + ".." : label), b -> {
                String cur = cfg.tier(ft).themeId;
                if (cur == null || cur.isEmpty()) {
                    cfg.tier(ft).themeId = Themes.all().get(0).id;
                } else {
                    cfg.tier(ft).themeId = ThemeService.nextThemeId(cur);
                }
                PaymentAlertsConfig.save();
                switchTo(Page.THEMES);
            }).bounds(bx, by, 165, 20).tooltip(Tooltip.create(Component.literal(
                "Click to cycle theme for " + tier.label + ". Empty uses the global theme."
            ))).build());
            col++;
        }
    }

    private void switchTo(Page page) {
        this.page = page;
        lastPage = page;
        rebuildWidgets();
    }

    private void preview(Tier tier) {
        String name = this.minecraft.player != null ? this.minecraft.player.getGameProfile().name() : "Steve";
        onClose();
        AlertManager.play(tier, new Payment(name, tier.threshold()), -1);
    }

    public void render(GuiGraphics guiGraphics, int i, int i2, float f) {
        try {
            super.render(guiGraphics, i, i2, f);
        } catch (Throwable th) {
            if (this.error == null) {
                PaymentAlertsClient.LOGGER.error("The payment settings failed to draw", th);
            }
            this.error = th;
        }
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 9, -1);
        if (this.error != null) {
            guiGraphics.drawCenteredString(this.font, Component.literal("The settings ran into a problem:"), this.width / 2, (this.height / 2) - 16, -40864);
            guiGraphics.drawCenteredString(this.font, Component.literal(this.font.plainSubstrByWidth(String.valueOf(this.error), this.width - 20)), this.width / 2, this.height / 2, -1);
            guiGraphics.drawCenteredString(this.font, Component.literal("Please send logs/latest.log so it can be fixed."), this.width / 2, (this.height / 2) + 16, -6250336);
        }
    }

    public void onClose() {
        PaymentAlertsConfig.save();
        this.minecraft.setScreen(this.parent);
    }

        private static final class Slider extends AbstractSliderButton {
        private final String label;
        private final DoubleFunction<String> format;
        private final DoubleConsumer apply;

        Slider(int i, int i2, int i3, String str, double d, DoubleFunction<String> doubleFunction, DoubleConsumer doubleConsumer) {
            super(i, i2, i3, 20, Component.empty(), Math.max(0.0d, Math.min(1.0d, d)));
            this.label = str;
            this.format = doubleFunction;
            this.apply = doubleConsumer;
            updateMessage();
        }

        protected void updateMessage() {
            setMessage(Component.literal(this.label + ": " + this.format.apply(this.value)));
        }

        protected void applyValue() {
            this.apply.accept(this.value);
        }
    }
}
