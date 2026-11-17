package com.paymentalerts.alert;

import com.paymentalerts.alert.TabAlert;
import com.paymentalerts.alert.Tier;
import com.paymentalerts.theme.ThemeService;
import com.paymentalerts.config.PaymentAlertsConfig;
import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.HudFireworks;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.skin.Dolls;
import com.paymentalerts.skin.SkinFetcher;
import com.paymentalerts.stats.PaymentStats;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public final class AlertManager {
    private static final int MAX_SIDE = 5;
    private static final List<SideAlert> SIDE = new ArrayList();
    private static final ArrayDeque<Queued> SIDE_QUEUE = new ArrayDeque<>();
    private static final List<OverlayAlert> OVERLAYS = new ArrayList();
    private static final ArrayDeque<Queued> STAGE_QUEUE = new ArrayDeque<>();
    private static StageAlert stage;
    private static long lastFrameNanos;

    private AlertManager() {
    }

    private static final record Queued(Tier tier, Payment payment, int variation, String note) {
    }

    public static Optional<Payment> onChatLine(Component component) {
        Optional<Payment> parse = PaymentAlertsConfig.parser().parse(component.getString());
        parse.ifPresent(payment -> {
            PaymentStats.record(payment.name(), payment.amount());
            if (PaymentAlertsConfig.get().enabled) {
                trigger(payment);
            }
        });
        return parse;
    }

    public static Tier tierFor(double d) {
        return pick(d, false);
    }

    public static Tier effectiveTier(double d) {
        return pick(d, true);
    }

    private static Tier pick(double d, boolean z) {
        Tier tier = Tier.NONE;
        long j = Long.MIN_VALUE;
        for (Tier tier2 : Tier.EFFECTS) {
            // Only amount tiers take part in the ladder; event tiers are fired by name.
            if (tier2.kind != Tier.Kind.SIDE && tier2.kind != Tier.Kind.STAGE) {
                continue;
            }
            long threshold = tier2.threshold();
            if (d >= threshold && threshold >= j && (!z || tier2.isEnabled())) {
                tier = tier2;
                j = threshold;
            }
        }
        return tier;
    }

    public static Tier trigger(Payment payment) {
        // The Lumindev effect is bound to a payer, not to an amount: it plays for
        // any payment from that name, and for anything above its ladder threshold.
        Tier effectiveTier = Tier.LUMINDEV.isEnabled() && LumindevAlert.isPayer(payment.name())
                ? Tier.LUMINDEV
                : effectiveTier(payment.amount());
        play(effectiveTier, payment, -1);
        return effectiveTier;
    }

    public static void play(Tier tier, Payment payment, int i) {
        ThemeService.bindForTier(tier);
        long nanoTime = System.nanoTime();
        int look = i >= 0 ? i : tier.look();
        switch (tier.kind) {
            case NONE:
                return;
            case SIDE:
                SkinFetcher.get(payment.lookupName());
                int pick = Variations.pick(look, tier.variations().length);
                SIDE_QUEUE.add(new Queued(tier, payment, pick, null));
                if (tier == Tier.MONEY_RAIN) {
                    OVERLAYS.add(new MoneyRainAlert(nanoTime, pick));
                } else if (tier == Tier.COIN_FOUNTAIN) {
                    OVERLAYS.add(new CoinFountainAlert(nanoTime, pick));
                }
                break;
            case STAGE:
            case EVENT:
                STAGE_QUEUE.add(new Queued(tier, payment, look, null));
                break;
        }
        promote(nanoTime);
    }

    /**
     * Fires the stream alert. This tier is bound to an event rather than an amount, so it
     * is never picked by the ladder — only callers can start it.
     *
     * @param channel display name of the channel that went live
     * @param title   the stream title, or null when it is not known
     */
    public static void stream(String channel, String title) {
        Tier tier = Tier.STREAM;
        if (!tier.isEnabled()) {
            return;
        }
        ThemeService.bindForTier(tier);
        long now = System.nanoTime();
        STAGE_QUEUE.add(new Queued(tier, new Payment(channel, 0.0d), tier.look(), title));
        promote(now);
    }

    public static void clear() {
        SIDE.clear();
        SIDE_QUEUE.clear();
        OVERLAYS.clear();
        STAGE_QUEUE.clear();
        stage = null;
        ScreenShake.clear();
    }

    public static void tick(Minecraft minecraft) {
        long nanoTime = System.nanoTime();
        if (stage != null) {
            stage.tick(nanoTime);
            if (stage.isFinished(nanoTime)) {
                stage = null;
            }
        }
        Iterator<OverlayAlert> it = OVERLAYS.iterator();
        while (it.hasNext()) {
            it.next().tick(nanoTime);
        }
        OVERLAYS.removeIf(overlayAlert -> {
            return overlayAlert.isFinished(nanoTime);
        });
        SIDE.removeIf(sideAlert -> {
            return sideAlert.isFinished(nanoTime);
        });
        promote(nanoTime);
    }

    private static void promote(long j) {
        StageAlert fireworksAlert;
        SideAlert tabAlert;
        while (SIDE.size() < MAX_SIDE && !SIDE_QUEUE.isEmpty()) {
            Queued poll = SIDE_QUEUE.poll();
            SIDE.add(EffectFactory.createSide(poll.tier(), poll.payment(), j, poll.variation()));
        }
        if (stage == null && !STAGE_QUEUE.isEmpty()) {
            Queued poll2 = STAGE_QUEUE.poll();
            stage = EffectFactory.createStage(poll2.tier(), poll2.payment(), j, poll2.variation(), poll2.note());
            stage.tick(j);
        }
    }

    /* renamed from: com.paymentalerts.alert.AlertManager$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$paymentalerts$alert$Tier$Kind;
        static final /* synthetic */ int[] $SwitchMap$com$paymentalerts$alert$Tier = new int[Tier.values().length];

        static {
            try {
                $SwitchMap$com$paymentalerts$alert$Tier[Tier.COIN_POP.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$paymentalerts$alert$Tier[Tier.COIN_FOUNTAIN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$paymentalerts$alert$Tier[Tier.MONEY_RAIN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$paymentalerts$alert$Tier[Tier.CONFETTI.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$paymentalerts$alert$Tier[Tier.LIGHTNING.ordinal()] = AlertManager.MAX_SIDE;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$paymentalerts$alert$Tier[Tier.JACKPOT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$paymentalerts$alert$Tier[Tier.TREASURE.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$paymentalerts$alert$Tier[Tier.NUKE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$paymentalerts$alert$Tier[Tier.METEORS.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$paymentalerts$alert$Tier[Tier.ORBITAL_LASER.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$paymentalerts$alert$Tier[Tier.BLACK_HOLE.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$paymentalerts$alert$Tier[Tier.SUPERNOVA.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$paymentalerts$alert$Tier[Tier.GOD_TIER.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            $SwitchMap$com$paymentalerts$alert$Tier$Kind = new int[Tier.Kind.values().length];
            try {
                $SwitchMap$com$paymentalerts$alert$Tier$Kind[Tier.Kind.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$paymentalerts$alert$Tier$Kind[Tier.Kind.SIDE.ordinal()] = 2;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$paymentalerts$alert$Tier$Kind[Tier.Kind.STAGE.ordinal()] = 3;
            } catch (NoSuchFieldError e16) {
            }
        }
    }

    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        long nanoTime = System.nanoTime();
        float min = lastFrameNanos == 0 ? 0.0f : Math.min(0.1f, (nanoTime - lastFrameNanos) / 1.0E9f);
        lastFrameNanos = nanoTime;
        Dolls.beginFrame();
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui) {
            return;
        }
        if (stage == null && SIDE.isEmpty() && OVERLAYS.isEmpty()) {
            return;
        }
        int guiWidth = guiGraphics.guiWidth();
        int guiHeight = guiGraphics.guiHeight();
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(ScreenShake.hudOffsetX(), ScreenShake.hudOffsetY());
        if (stage != null) {
            stage.render(guiGraphics, minecraft.font, guiWidth, guiHeight, min, nanoTime);
        }
        Iterator<OverlayAlert> it = OVERLAYS.iterator();
        while (it.hasNext()) {
            it.next().render(guiGraphics, guiWidth, guiHeight, min, nanoTime);
        }
        int i = topRightStart(minecraft);
        Iterator<SideAlert> it2 = SIDE.iterator();
        while (it2.hasNext()) {
            SideAlert next = it2.next();
            if (next.isFinished(nanoTime)) {
                it2.remove();
            } else {
                i += next.render(guiGraphics, minecraft.font, guiWidth, i, nanoTime);
            }
        }
        Draw2D.flush(guiGraphics);
        guiGraphics.pose().popMatrix();
    }

    private static int topRightStart(Minecraft minecraft) {
        if (minecraft.player == null) {
            return 4;
        }
        boolean z = false;
        boolean z2 = false;
        for (MobEffectInstance mobEffectInstance : minecraft.player.getActiveEffects()) {
            if (mobEffectInstance.showIcon()) {
                if (((MobEffect) mobEffectInstance.getEffect().value()).isBeneficial()) {
                    z = true;
                } else {
                    z2 = true;
                }
            }
        }
        if (z2) {
            return 56;
        }
        return z ? 30 : 4;
    }
}
