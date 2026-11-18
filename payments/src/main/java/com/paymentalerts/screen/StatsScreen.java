package com.paymentalerts.screen;

import com.paymentalerts.PaymentAlertsClient;
import com.paymentalerts.payment.AmountFormat;
import com.paymentalerts.screen.LeaderboardView;
import com.paymentalerts.stats.PaymentStats;
import com.paymentalerts.stats.StatsFormat;
import com.paymentalerts.ui.Ui;
import com.paymentalerts.ui.UiButton;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class StatsScreen extends Screen {
    private static final int PANEL_TOP = 28;
    private static final int PANEL_MAX_WIDTH = 340;
    private static final int BAR_MAX_WIDTH = 520;
    private final Screen parent;
    private PaymentStats.Session detail;
    private LeaderboardView board;
    private double scroll;
    private double mainScroll;
    private int shownCount;
    private Throwable error;

    public StatsScreen(Screen screen) {
        super(Component.literal("Payment Leaderboard"));
        this.parent = screen;
    }

    protected void init() {
        this.shownCount = PaymentStats.get().count;
        if (this.detail != null) {
            addRenderableWidget(new UiButton(6, 4, 66, 20, Component.literal("< Back"), button -> {
                openDetail(null);
            }));
        } else {
            addRenderableWidget(new UiButton(6, 4, 66, 20, Component.literal("Settings"), button2 -> {
                this.minecraft.setScreen(new SettingsScreen(this));
            }));
            UiButton reset = new UiButton(76, 4, 66, 20, Component.literal("Reset..."), button3 -> {
                confirmReset();
            });
            reset.setTooltip(Tooltip.create(Component.literal("Forget all payment stats and sessions.")));
            addRenderableWidget(reset);
        }
        UiButton done = new UiButton(this.width - 72, 4, 66, 20, Component.literal("Done"), button4 -> {
            onClose();
        });
        done.accent(true);
        addRenderableWidget(done);
        try {
            if (this.detail == null) {
                this.board = new LeaderboardView(PaymentStats.top(PaymentStats.get().payers), PaymentStats.newestFirst(), this::openDetail, "No payments counted yet.", this.scroll);
            } else {
                this.board = new LeaderboardView(PaymentStats.top(this.detail.payers), null, null, "Nobody paid you in this session.", this.scroll);
            }
        } catch (Throwable th) {
            fail(th);
        }
    }

    private void fail(Throwable th) {
        if (this.error == null) {
            PaymentAlertsClient.LOGGER.error("The payment leaderboard failed", th);
        }
        this.error = th;
        this.board = null;
    }

    private List<LeaderboardView.Stat> stats() {
        long currentTimeMillis = System.currentTimeMillis();
        ArrayList arrayList = new ArrayList();
        if (this.detail != null) {
            PaymentStats.Session session = this.detail;
            long end = session.end(currentTimeMillis) - session.joined;
            arrayList.add(new LeaderboardView.Stat("Made", AmountFormat.compact(session.total), LeaderboardView.GOLD));
            arrayList.add(new LeaderboardView.Stat("Per hour", perHour(session.total, end), LeaderboardView.GOLD));
            arrayList.add(new LeaderboardView.Stat("Payments", String.valueOf(session.count)));
            arrayList.add(new LeaderboardView.Stat("People who paid", String.valueOf(session.payers.size())));
            arrayList.add(new LeaderboardView.Stat("Biggest", biggest(session.count, session.biggestAmount, session.biggestFrom), LeaderboardView.GOLD));
            arrayList.add(new LeaderboardView.Stat("Average", session.count > 0 ? AmountFormat.compact(session.total / session.count) : "-", LeaderboardView.GOLD));
            arrayList.add(new LeaderboardView.Stat("Joined", StatsFormat.dateTime(session.joined)));
            arrayList.add(new LeaderboardView.Stat(session.isOpen() ? "Playing for" : "Length", StatsFormat.duration(end)));
            return arrayList;
        }
        PaymentStats paymentStats = PaymentStats.get();
        PaymentStats.Session activeSession = PaymentStats.activeSession();
        long j = activeSession == null ? 0L : currentTimeMillis - activeSession.joined;
        arrayList.add(new LeaderboardView.Stat("This session", AmountFormat.compact(activeSession == null ? 0.0d : activeSession.total), LeaderboardView.GOLD));
        arrayList.add(new LeaderboardView.Stat("All time", AmountFormat.compact(paymentStats.total), LeaderboardView.GOLD));
        arrayList.add(new LeaderboardView.Stat("Per hour", activeSession == null ? "-" : perHour(activeSession.total, j), LeaderboardView.GOLD));
        arrayList.add(new LeaderboardView.Stat("Time on", activeSession == null ? "-" : StatsFormat.duration(j)));
        arrayList.add(new LeaderboardView.Stat("Payments", (activeSession == null ? 0 : activeSession.count) + " (" + paymentStats.count + " total)"));
        arrayList.add(new LeaderboardView.Stat("People who paid", String.valueOf(paymentStats.payers.size())));
        arrayList.add(new LeaderboardView.Stat("Biggest", biggest(paymentStats.count, paymentStats.biggestAmount, paymentStats.biggestFrom), LeaderboardView.GOLD));
        arrayList.add(new LeaderboardView.Stat("Average", paymentStats.count > 0 ? AmountFormat.compact(paymentStats.total / paymentStats.count) : "-", LeaderboardView.GOLD));
        return arrayList;
    }

    private static String biggest(int i, double d, String str) {
        return (i <= 0 || str == null) ? "-" : AmountFormat.compact(d) + " by " + str;
    }

    private static String perHour(double d, long j) {
        if (j < 60000 || d <= 0.0d) {
            return "-";
        }
        return AmountFormat.compact(d / (j / 3600000.0d)) + " / h";
    }

    private int perRow() {
        return barWidth() >= 400 ? 4 : 2;
    }

    private int barWidth() {
        return Math.min(BAR_MAX_WIDTH, this.width - 20);
    }

    private int panelBottom() {
        return ((this.height - 10) - LeaderboardView.statsBarHeight(8, perRow())) - 8;
    }

    public void tick() {
        super.tick();
        if (PaymentStats.get().count != this.shownCount) {
            if (this.board != null) {
                this.scroll = this.board.scroll();
            }
            rebuildWidgets();
        }
    }

    private void openDetail(PaymentStats.Session session) {
        if (session != null && this.detail == null) {
            this.mainScroll = this.board != null ? this.board.scroll() : 0.0d;
            this.scroll = 0.0d;
        } else if (session == null) {
            this.scroll = this.mainScroll;
        }
        this.detail = session;
        rebuildWidgets();
    }

    private void confirmReset() {
        this.minecraft.setScreen(new ConfirmScreen(z -> {
            if (z) {
                PaymentStats.reset();
                this.scroll = 0.0d;
            }
            this.minecraft.setScreen(this);
        }, Component.literal("Reset payment stats?"), Component.literal("This forgets every payment, payer and session the mod has counted. It cannot be undone.")));
    }

    private int panelLeft() {
        return (this.width / 2) - (Math.min(PANEL_MAX_WIDTH, this.width - 20) / 2);
    }

    private int panelRight() {
        return panelLeft() + Math.min(PANEL_MAX_WIDTH, this.width - 20);
    }

    public boolean mouseScrolled(double d, double d2, double d3, double d4) {
        if (this.board != null && d >= panelLeft() && d < panelRight() && d2 >= 28.0d && d2 < panelBottom()) {
            this.board.scrollBy(d4);
            return true;
        }
        return super.mouseScrolled(d, d2, d3, d4);
    }

    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean z) {
        if (super.mouseClicked(mouseButtonEvent, z)) {
            return true;
        }
        try {
            if (this.board != null && mouseButtonEvent.button() == 0) {
                if (this.board.click(mouseButtonEvent.x(), mouseButtonEvent.y())) {
                    return true;
                }
            }
            return false;
        } catch (Throwable th) {
            fail(th);
            return false;
        }
    }

    public void render(GuiGraphics guiGraphics, int i, int i2, float f) {
        try {
            super.render(guiGraphics, i, i2, f);
        } catch (Throwable th) {
            fail(th);
        }
        int i3 = this.width / 2;
        String heading = this.detail != null ? "Session: " + StatsFormat.dateTime(this.detail.joined) : this.title.getString();
        Ui.header(guiGraphics, this.font, this.font.plainSubstrByWidth(heading, this.width - 280), i3, 10);
        if (this.error == null) {
            try {
                if (this.board != null) {
                    this.board.render(guiGraphics, this.font, panelLeft(), PANEL_TOP, panelRight(), panelBottom(), i, i2);
                }
                int barWidth = i3 - (barWidth() / 2);
                LeaderboardView.statsBar(guiGraphics, this.font, barWidth, panelBottom() + 8, barWidth + barWidth(), stats(), perRow());
            } catch (Throwable th2) {
                fail(th2);
            }
        }
        if (this.error != null) {
            guiGraphics.drawCenteredString(this.font, Component.literal("The leaderboard ran into a problem:"), i3, (this.height / 2) - 16, Ui.BAD);
            guiGraphics.drawCenteredString(this.font, Component.literal(this.font.plainSubstrByWidth(String.valueOf(this.error), this.width - 20)), i3, this.height / 2, Ui.TEXT);
            guiGraphics.drawCenteredString(this.font, Component.literal("Please send logs/latest.log so it can be fixed."), i3, (this.height / 2) + 16, Ui.TEXT_MUTED);
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int i2, float f) {
        Ui.scrim(guiGraphics, this.width, this.height);
    }

    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
