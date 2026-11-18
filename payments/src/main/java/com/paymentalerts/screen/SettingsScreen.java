package com.paymentalerts.screen;

import com.paymentalerts.PaymentAlertsClient;
import com.paymentalerts.alert.AlertManager;
import com.paymentalerts.alert.Tier;
import com.paymentalerts.config.PaymentAlertsConfig;
import com.paymentalerts.theme.ThemeService;
import com.paymentalerts.theme.Themes;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.ui.Ui;
import com.paymentalerts.ui.UiButton;
import com.paymentalerts.ui.UiSlider;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SettingsScreen extends Screen {
    private static Page lastPage = Page.GENERAL;
    private static final int ROW = 24;
    private static final int TOP = 54;
    /** Panel geometry: one glass sheet behind the whole page body. */
    private static final int PANEL_SIDE = 172;
    private static final int PANEL_TOP = 48;
    private static final int PANEL_BOTTOM_MARGIN = 34;
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
        tab(i - 168, "General", Page.GENERAL);
        tab(i - 54, "Effects", Page.EFFECTS);
        tab(i + 60, "Themes (" + Themes.count() + ")", Page.THEMES);
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
        button(i - 154, this.height - 28, 150, "Clear alerts", button3 -> {
            AlertManager.clear();
        }, "Stops every effect that is on screen right now.");
        UiButton done = new UiButton(i + 4, this.height - 28, 150, 20, Component.literal("Done"), button4 -> {
            onClose();
        });
        done.accent(true);
        addRenderableWidget(done);
    }

    private void tab(int x, String label, Page target) {
        UiButton button = new UiButton(x, ROW, 108, 20, Component.literal(label), b -> switchTo(target));
        button.selected(this.page == target);
        addRenderableWidget(button);
    }

    private UiButton button(int x, int y, int width, int height, String label, Button.OnPress press, String tooltip) {
        UiButton button = new UiButton(x, y, width, height, Component.literal(label), press);
        if (tooltip != null) {
            button.setTooltip(Tooltip.create(Component.literal(tooltip)));
        }
        addRenderableWidget(button);
        return button;
    }

    /** Convenience overload for the fixed 150px footer buttons. */
    private UiButton button(int x, int y, int width, String label, Button.OnPress press, String tooltip) {
        return button(x, y, width, 20, label, press, tooltip);
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
        addRenderableWidget(new UiSlider(i3, i4, 160, 20, "Volume", paymentAlertsConfig.soundVolume, d -> {
            return Math.round(d * 100.0d) + "%";
        }, d2 -> {
            paymentAlertsConfig.soundVolume = (float) d2;
        }));
        int i5 = i4 + ROW;
        addRenderableWidget(new UiSlider(i2, i5, 160, 20, "Shake strength", paymentAlertsConfig.shakeStrength / 2.0d, d3 -> {
            return Math.round(d3 * 200.0d) + "%";
        }, d4 -> {
            paymentAlertsConfig.shakeStrength = (float) (d4 * 2.0d);
        }));
        addRenderableWidget(new UiSlider(i3, i5, 160, 20, "Tab time", (paymentAlertsConfig.tabSeconds - 2.0f) / 13.0f, d5 -> {
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
        button(i3, i6, 160, 20, "Effects on: " + i7 + "/" + Tier.EFFECTS.length + " >", button -> {
            switchTo(Page.EFFECTS);
        }, "Pick amounts, looks and previews for each effect.");
        button(i2, i6 + ROW, (160 * 2) + 10, 20, "Payment stats (J) >", button2 -> {
            this.minecraft.setScreen(new StatsScreen(this));
        }, "Top payers and how much you made this session, per session and in total.");
    }

    private void toggle(int i, int i2, int i3, String str, boolean z, Consumer<Boolean> consumer, String str2) {
        UiButton.Toggle toggle = new UiButton.Toggle(i, i2, i3, 20, Component.literal(str), z, consumer);
        toggle.setTooltip(Tooltip.create(Component.literal(str2)));
        addRenderableWidget(toggle);
    }

    private void initThemes(int centerX) {
        PaymentAlertsConfig cfg = PaymentAlertsConfig.get();
        int left = centerX - 160;
        int y = TOP + 8;

        String globalLabel = "Global: " + ThemeService.displayName(cfg.globalThemeId);
        button(left, y, 28, 20, "<", b -> {
            cfg.globalThemeId = ThemeService.prevThemeId(cfg.globalThemeId);
            PaymentAlertsConfig.save();
            switchTo(Page.THEMES);
        }, "Previous theme.");
        button(left + 32, y, 256, 20, globalLabel, b -> {
            cfg.globalThemeId = ThemeService.nextThemeId(cfg.globalThemeId);
            PaymentAlertsConfig.save();
            switchTo(Page.THEMES);
        }, "Theme applied to every effect that has no per-tier override. Click to cycle. " + Themes.count() + " themes available.").accent(true);
        button(left + 292, y, 28, 20, ">", b -> {
            cfg.globalThemeId = ThemeService.nextThemeId(cfg.globalThemeId);
            PaymentAlertsConfig.save();
            switchTo(Page.THEMES);
        }, "Next theme.");

        y += 28;
        button(left, y, 150, 20, "Set global = Random", b -> {
            cfg.globalThemeId = "random";
            PaymentAlertsConfig.save();
            switchTo(Page.THEMES);
        }, "Pick a random theme for each alert.");
        button(left + 160, y, 160, 20, "Reset all tier themes", b -> {
            for (Tier t : Tier.EFFECTS) {
                cfg.tier(t).themeId = "";
            }
            PaymentAlertsConfig.save();
            switchTo(Page.THEMES);
        }, "Clear per-effect overrides so they all use the global theme.");

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
            button(bx, by, 165, 20, label.length() > 28 ? label.substring(0, 26) + ".." : label, b -> {
                String cur = cfg.tier(ft).themeId;
                if (cur == null || cur.isEmpty()) {
                    cfg.tier(ft).themeId = Themes.all().get(0).id;
                } else {
                    cfg.tier(ft).themeId = ThemeService.nextThemeId(cur);
                }
                PaymentAlertsConfig.save();
                switchTo(Page.THEMES);
            }, "Click to cycle theme for " + tier.label + ". Empty uses the global theme.").selected(!tid.isEmpty());
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

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int i2, float f) {
        Ui.scrim(guiGraphics, this.width, this.height);
        Ui.glass(guiGraphics, (this.width / 2) - PANEL_SIDE, PANEL_TOP, (this.width / 2) + PANEL_SIDE,
                this.height - PANEL_BOTTOM_MARGIN);
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
        Ui.header(guiGraphics, this.font, this.title.getString(), this.width / 2, 9);
        if (this.page == Page.GENERAL && this.error == null) {
            Ui.centered(guiGraphics, this.font, "Changes save when you close this screen.", this.width / 2,
                    this.height - PANEL_BOTTOM_MARGIN - 12, Ui.TEXT_MUTED);
        }
        if (this.error != null) {
            guiGraphics.drawCenteredString(this.font, Component.literal("The settings ran into a problem:"), this.width / 2, (this.height / 2) - 16, Ui.BAD);
            guiGraphics.drawCenteredString(this.font, Component.literal(this.font.plainSubstrByWidth(String.valueOf(this.error), this.width - 20)), this.width / 2, this.height / 2, Ui.TEXT);
            guiGraphics.drawCenteredString(this.font, Component.literal("Please send logs/latest.log so it can be fixed."), this.width / 2, (this.height / 2) + 16, Ui.TEXT_MUTED);
        }
    }

    public void onClose() {
        PaymentAlertsConfig.save();
        this.minecraft.setScreen(this.parent);
    }
}
