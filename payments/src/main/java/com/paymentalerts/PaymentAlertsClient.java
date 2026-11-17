package com.paymentalerts;

import com.paymentalerts.alert.AlertManager;
import com.paymentalerts.command.PayAlertsCommand;
import com.paymentalerts.config.PaymentAlertsConfig;
import com.paymentalerts.screen.SettingsScreen;
import com.paymentalerts.screen.StatsScreen;
import com.paymentalerts.skin.SkinFetcher;
import com.paymentalerts.stats.PaymentStats;
import com.paymentalerts.stream.StreamWatch;
import java.util.function.Function;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PaymentAlertsClient implements ClientModInitializer {
    private static final int KEY_H = 72;
    private static final int KEY_O = 79;
    private static final int KEY_J = 74;
    private static KeyMapping toggleKey;
    private static KeyMapping settingsKey;
    private static KeyMapping statsKey;
    private static Function<Screen, Screen> openNextTick;
    public static final Logger LOGGER = LoggerFactory.getLogger("PaymentAlerts");
    public static final String MOD_ID = "paymentalerts";
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));

    public void onInitializeClient() {
        PaymentAlertsConfig.load();
        PaymentStats.load();
        ClientReceiveMessageEvents.GAME.register((component, z) -> {
            if (!z) {
                AlertManager.onChatLine(component);
                StreamWatch.handle(component.getString());
            }
        });
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.paymentalerts.toggle", KEY_H, CATEGORY));
        settingsKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.paymentalerts.settings", KEY_O, CATEGORY));
        statsKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.paymentalerts.stats", KEY_J, CATEGORY));
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (toggleKey.consumeClick()) {
                toggle(minecraft);
            }
            while (settingsKey.consumeClick()) {
                openSafely(minecraft, SettingsScreen::new);
            }
            while (statsKey.consumeClick()) {
                openSafely(minecraft, StatsScreen::new);
            }
            if (openNextTick != null) {
                Function<Screen, Screen> function = openNextTick;
                openNextTick = null;
                openSafely(minecraft, function);
            }
            AlertManager.tick(minecraft);
            PaymentStats.tick();
        });
        ClientPlayConnectionEvents.JOIN.register((clientPacketListener, packetSender, minecraft2) -> {
            minecraft2.execute(() -> {
                String[] describeServer = describeServer(minecraft2);
                PaymentStats.startSession(describeServer[0], describeServer[1]);
            });
        });
        ClientPlayConnectionEvents.DISCONNECT.register((clientPacketListener2, minecraft3) -> {
            minecraft3.execute(() -> {
                AlertManager.clear();
                SkinFetcher.clearCache();
                PaymentStats.endSession();
            });
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(minecraft4 -> {
            PaymentStats.endSession();
        });
        ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext) -> {
            PayAlertsCommand.register(commandDispatcher);
        });
        LOGGER.info("Donut SMP Payment Addon ready — originally by cobha, developed by cyclonelwk & Lumina");
    }

    public static void toggle(Minecraft minecraft) {
        PaymentAlertsConfig paymentAlertsConfig = PaymentAlertsConfig.get();
        paymentAlertsConfig.enabled = !paymentAlertsConfig.enabled;
        PaymentAlertsConfig.save();
        if (!paymentAlertsConfig.enabled) {
            AlertManager.clear();
        }
        if (minecraft.player != null) {
            minecraft.player.displayClientMessage(Component.literal("Payment alerts: " + (paymentAlertsConfig.enabled ? "ON" : "OFF")).setStyle(Style.EMPTY.withColor(paymentAlertsConfig.enabled ? 5635925 : 16733525)), true);
        }
    }

    private static void openSafely(Minecraft minecraft, Function<Screen, Screen> function) {
        try {
            minecraft.setScreen(function.apply(minecraft.screen));
        } catch (Throwable th) {
            LOGGER.error("Could not open the screen", th);
            if (minecraft.player != null) {
                minecraft.player.displayClientMessage(Component.literal("[Donut SMP Payment Addon] That screen failed to open: " + String.valueOf(th) + " (details in logs/latest.log)").setStyle(Style.EMPTY.withColor(16733525)), false);
            }
        }
    }

    public static void openSettingsSoon() {
        openNextTick = SettingsScreen::new;
    }

    public static void openStatsSoon() {
        openNextTick = StatsScreen::new;
    }

    private static String[] describeServer(Minecraft minecraft) {
        try {
        } catch (Exception e) {
            LOGGER.warn("Could not tell which server this is", e);
        }
        if (minecraft.hasSingleplayerServer() && minecraft.getSingleplayerServer() != null) {
            String levelName = minecraft.getSingleplayerServer().getWorldData().getLevelName();
            return new String[]{"Singleplayer: " + levelName, "singleplayer:" + levelName};
        }
        ServerData currentServer = minecraft.getCurrentServer();
        if (currentServer != null) {
            if (currentServer.isRealm()) {
                return new String[]{"Realms: " + currentServer.name, "realms:" + currentServer.name};
            }
            String str = currentServer.ip == null ? "" : currentServer.ip;
            String str2 = (currentServer.name == null || currentServer.name.isBlank()) ? str : currentServer.name;
            String[] strArr = new String[2];
            strArr[0] = str2.isBlank() ? "Unknown server" : str2;
            strArr[1] = str;
            return strArr;
        }
        return new String[]{"Unknown server", ""};
    }
}
