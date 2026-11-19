package com.paymentalerts.command;

import com.paymentalerts.PaymentAlertsClient;
import com.paymentalerts.config.PaymentAlertsConfig;
import com.paymentalerts.payment.PaymentParser;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;

/** Prompts before sending unusually large player-to-player payments. */
public final class PaymentCommandGuard {
    private static final Pattern PAY_COMMAND = Pattern.compile("^\\s*pay\\s+(\\S+)\\s+(\\S+)(?:\\s+.*)?$", Pattern.CASE_INSENSITIVE);
    private static String pendingCommand;
    private static String pendingRecipient;
    private static double pendingAmount;
    private static volatile String authorizedCommand;

    private PaymentCommandGuard() {
    }

    public static void register() {
        ClientSendMessageEvents.ALLOW_COMMAND.register(PaymentCommandGuard::allowCommand);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            String command = pendingCommand;
            if (command == null || client.player == null) {
                return;
            }
            String recipient = pendingRecipient;
            double amount = pendingAmount;
            pendingCommand = null;
            pendingRecipient = null;
            client.setScreen(new ConfirmScreen(confirmed -> {
                if (confirmed && client.player != null && client.player.connection != null) {
                    authorizedCommand = command;
                    client.player.connection.sendCommand(command);
                }
            }, Component.literal("Confirm large payment"), Component.literal(
                    "Send " + PaymentAlertsClient.formatPaymentAmount(amount) + " to " + recipient + "?")));
        });
    }

    private static boolean allowCommand(String command) {
        if (command != null && command.equals(authorizedCommand)) {
            authorizedCommand = null;
            return true;
        }
        PaymentAlertsConfig config = PaymentAlertsConfig.get();
        if (!config.verifyLargePayments || command == null) {
            return true;
        }
        Matcher matcher = PAY_COMMAND.matcher(command);
        if (!matcher.matches()) {
            return true;
        }
        var parsed = PaymentParser.parseAmount(matcher.group(2));
        if (parsed.isEmpty() || parsed.getAsDouble() < config.largePaymentThreshold) {
            return true;
        }
        String recipient = matcher.group(1).replaceAll("[^A-Za-z0-9_.]", "");
        if (recipient.isEmpty()) {
            return true;
        }
        pendingCommand = command;
        pendingRecipient = recipient;
        pendingAmount = parsed.getAsDouble();
        return false;
    }
}
