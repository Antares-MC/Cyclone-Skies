package com.paymentalerts.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.paymentalerts.PaymentAlertsClient;
import com.paymentalerts.alert.AlertManager;
import com.paymentalerts.alert.Tier;
import com.paymentalerts.config.PaymentAlertsConfig;
import com.paymentalerts.payment.AmountFormat;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.payment.PaymentParser;
import com.paymentalerts.skin.SkinFetcher;
import java.util.Optional;
import java.util.OptionalDouble;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public final class PayAlertsCommand {
    private PayAlertsCommand() {
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> commandDispatcher) {
        commandDispatcher.register(ClientCommandManager.literal("payalerts").then(ClientCommandManager.literal("test").then(ClientCommandManager.argument("amount", StringArgumentType.word()).executes(commandContext -> {
            return test(commandContext, null, -1);
        }).then(ClientCommandManager.argument("player", StringArgumentType.word()).executes(commandContext2 -> {
            return test(commandContext2, StringArgumentType.getString(commandContext2, "player"), -1);
        }).then(ClientCommandManager.argument("variation", IntegerArgumentType.integer(1, 9)).executes(commandContext3 -> {
            return test(commandContext3, StringArgumentType.getString(commandContext3, "player"), IntegerArgumentType.getInteger(commandContext3, "variation") - 1);
        }))))).then(ClientCommandManager.literal("list").executes(PayAlertsCommand::list)).then(ClientCommandManager.literal("simulate").then(ClientCommandManager.argument("message", StringArgumentType.greedyString()).executes(PayAlertsCommand::simulate))).then(ClientCommandManager.literal("toggle").executes(commandContext4 -> {
            PaymentAlertsClient.toggle(((FabricClientCommandSource) commandContext4.getSource()).getClient());
            return 1;
        })).then(ClientCommandManager.literal("settings").executes(commandContext5 -> {
            PaymentAlertsClient.openSettingsSoon();
            return 1;
        })).then(ClientCommandManager.literal("stats").executes(commandContext6 -> {
            PaymentAlertsClient.openStatsSoon();
            return 1;
        })).then(ClientCommandManager.literal("reload").executes(commandContext7 -> {
            PaymentAlertsConfig.load();
            SkinFetcher.clearCache();
            ((FabricClientCommandSource) commandContext7.getSource()).sendFeedback(green("[Donut SMP Payment Addon] Config reloaded."));
            return 1;
        })).then(ClientCommandManager.literal("clear").executes(commandContext8 -> {
            AlertManager.clear();
            ((FabricClientCommandSource) commandContext8.getSource()).sendFeedback(Component.literal("[Donut SMP Payment Addon] Cleared.").setStyle(Style.EMPTY.withColor(11184810)));
            return 1;
        })).then(ClientCommandManager.literal("stream").executes(PayAlertsCommand::stream).then(ClientCommandManager.argument("title", StringArgumentType.greedyString()).executes(PayAlertsCommand::stream))));
    }

    private static int stream(CommandContext<FabricClientCommandSource> commandContext) {
        String title;
        try {
            title = StringArgumentType.getString(commandContext, "title");
        } catch (IllegalArgumentException e) {
            title = "";
        }
        String channel = PaymentAlertsConfig.get().streamChannel;
        AlertManager.stream(channel, title);
        ((FabricClientCommandSource) commandContext.getSource()).sendFeedback(green("[Donut SMP Payment Addon] Stream alert: " + channel + (title.isEmpty() ? "" : " — " + title)));
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int test(CommandContext<FabricClientCommandSource> commandContext, String str, int i) {
        OptionalDouble parseAmount = PaymentParser.parseAmount(StringArgumentType.getString(commandContext, "amount"));
        if (parseAmount.isEmpty()) {
            ((FabricClientCommandSource) commandContext.getSource()).sendError(Component.literal("[Donut SMP Payment Addon] Amount must look like 1m, 25m, 200m, 750m, 2.5b, 50b or 100b."));
            return 0;
        }
        Payment payment = new Payment(str != null ? str : ((FabricClientCommandSource) commandContext.getSource()).getPlayer().getGameProfile().name(), parseAmount.getAsDouble());
        Tier tierFor = AlertManager.tierFor(payment.amount());
        AlertManager.play(tierFor, payment, i);
        report(commandContext, payment, tierFor, i);
        return 1;
    }

    private static int simulate(CommandContext<FabricClientCommandSource> commandContext) {
        Optional<Payment> parse = PaymentAlertsConfig.parser().parse(StringArgumentType.getString(commandContext, "message"));
        if (parse.isEmpty()) {
            ((FabricClientCommandSource) commandContext.getSource()).sendError(Component.literal("[Donut SMP Payment Addon] That line does not look like a payment to you."));
            return 0;
        }
        report(commandContext, parse.get(), AlertManager.trigger(parse.get()), -1);
        return 1;
    }

    private static int list(CommandContext<FabricClientCommandSource> commandContext) {
        ((FabricClientCommandSource) commandContext.getSource()).sendFeedback(green("[Donut SMP Payment Addon] Effects (lowest amount that plays them):"));
        for (Tier tier : Tier.EFFECTS) {
            ((FabricClientCommandSource) commandContext.getSource()).sendFeedback(Component.literal("  " + AmountFormat.compact(tier.threshold()) + "  ").setStyle(Style.EMPTY.withColor(tier.color)).append(Component.literal(tier.label + (tier.isEnabled() ? "" : " (off)")).setStyle(Style.EMPTY.withColor(tier.isEnabled() ? 16777215 : 7829367))));
        }
        return 1;
    }

    private static void report(CommandContext<FabricClientCommandSource> commandContext, Payment payment, Tier tier, int i) {
        String str;
        String[] variations = tier.variations();
        if (i >= 0) {
            str = " (variation " + ((i % variations.length) + 1) + "/" + variations.length + ": " + variations[i % variations.length] + ")";
        } else {
            str = variations.length > 1 ? " (" + variations.length + " variations)" : "";
        }
        ((FabricClientCommandSource) commandContext.getSource()).sendFeedback(green("[Donut SMP Payment Addon] " + payment.name() + " paid " + payment.displayAmount() + " -> " + tier.label.toLowerCase() + str));
    }

    private static MutableComponent green(String str) {
        return Component.literal(str).setStyle(Style.EMPTY.withColor(5635925));
    }
}
