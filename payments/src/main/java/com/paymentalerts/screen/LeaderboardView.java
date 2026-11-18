package com.paymentalerts.screen;

import com.paymentalerts.payment.AmountFormat;
import com.paymentalerts.skin.Dolls;
import com.paymentalerts.skin.SkinDoll;
import com.paymentalerts.skin.SkinFetcher;
import com.paymentalerts.stats.PaymentStats;
import com.paymentalerts.stats.StatsFormat;
import com.paymentalerts.ui.Ui;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;

final class LeaderboardView {
    private static final int PODIUM_H = 166;
    private static final int PODIUM_BASE = 134;
    private static final int HEADER_H = 22;
    private static final int ROW_H = 22;
    private static final int PAD = 8;
    private static final int STAT_H = 25;
    private final List<PaymentStats.Payer> payers;
    private final List<PaymentStats.Session> sessions;
    private final Consumer<PaymentStats.Session> onSession;
    private final String emptyText;
    private final SkinFetcher.Skin[] skins;
    private double scroll;
    private int maxScroll;
    private int panelTop;
    private int panelBottom;
    private int panelLeft;
    private int panelRight;
    static final int GOLD = Ui.WARN;
    private static final int[] MEDAL = {GOLD, -2563862, -3110340};
    private static final String[] PLACE = {"1st", "2nd", "3rd"};
    private static final int[] BLOCK_H = {44, 32, 24};
    private static final int SESSION_H = 26;
    private static final int[] DOLL_SIZE = {30, SESSION_H, SESSION_H};
    private static final int[] COLUMNS = {1, 0, 2};
    private final long openedNanos = System.nanoTime();
    private final SkinDoll[] dolls = new SkinDoll[3];
    private int sessionsTop = Integer.MIN_VALUE;

    static final record Stat(String label, String value, int color) {


        

        

        




        Stat(String str, String str2) {
            this(str, str2, -1);
        }
    }

    LeaderboardView(List<PaymentStats.Payer> list, List<PaymentStats.Session> list2, Consumer<PaymentStats.Session> consumer, String str, double d) {
        this.payers = list;
        this.sessions = list2;
        this.onSession = consumer;
        this.emptyText = str;
        this.skins = new SkinFetcher.Skin[list.size()];
        this.scroll = d;
    }

    double scroll() {
        return this.scroll;
    }

    void scrollBy(double d) {
        this.scroll = Math.max(0.0d, Math.min(this.maxScroll, this.scroll - (d * 24.0d)));
    }

    private SkinFetcher.Skin skin(int i) {
        if (this.skins[i] == null) {
            String str = this.payers.get(i).name;
            this.skins[i] = SkinFetcher.get((str.startsWith(".") || str.startsWith("*")) ? str.substring(1) : str);
        }
        return this.skins[i];
    }

    private int contentHeight() {
        int max = 192 + (Math.max(1, this.payers.size() - 3) * 22) + 6;
        if (this.sessions != null) {
            max += 22 + (Math.max(1, this.sessions.size()) * SESSION_H);
        }
        return max + PAD;
    }

    void render(GuiGraphics guiGraphics, Font font, int i, int i2, int i3, int i4, int i5, int i6) {
        int i7 = i4 - i2;
        this.maxScroll = Math.max(0, contentHeight() - i7);
        this.panelTop = i2;
        this.panelBottom = i4;
        this.panelLeft = i;
        this.panelRight = i3;
        this.scroll = Math.max(0.0d, Math.min(this.maxScroll, this.scroll));
        Ui.glass(guiGraphics, i, i2, i3, i4);
        ScreenRectangle screenRectangle = new ScreenRectangle(i + 1, i2 + 1, (i3 - i) - 2, i7 - 2);
        boolean z = i5 > i && i5 < i3 && i6 > i2 && i6 < i4;
        guiGraphics.enableScissor(i + 1, i2 + 1, i3 - 1, i4 - 1);
        int rows = rows(guiGraphics, font, i, i3, header(guiGraphics, font, "Everyone else", i, i3, podium(guiGraphics, font, i, i3, (i2 + 4) - ((int) Math.round(this.scroll)), i2, i4, i5, i6, z, screenRectangle)), i2, i4, i5, i6, z) + 6;
        if (this.sessions != null) {
            int header = header(guiGraphics, font, "Your sessions", i, i3, rows);
            this.sessionsTop = header;
            sessionRows(guiGraphics, font, i, i3, header, i2, i4, i5, i6, z);
        }
        guiGraphics.disableScissor();
        if (this.maxScroll > 0) {
            int max = Math.max(18, ((i7 - 4) * i7) / (i7 + this.maxScroll));
            int round = i2 + 2 + ((int) Math.round((((i7 - 4) - max) * this.scroll) / this.maxScroll));
            Ui.pill(guiGraphics, i3 - 5, i2 + 2, i3 - 2, i4 - 2, Ui.TRACK);
            Ui.pill(guiGraphics, i3 - 5, round, i3 - 2, round + max, Ui.withAlpha(Ui.ACCENT, 0.55f));
        }
    }

    private int podium(GuiGraphics guiGraphics, Font font, int i, int i2, int i3, int i4, int i5, int i6, int i7, boolean z, ScreenRectangle screenRectangle) {
        int i8 = (i + i2) / 2;
        int min = Math.min(76, ((i2 - i) - 24) / 3);
        int i9 = i3 + PODIUM_BASE;
        float nanoTime = (System.nanoTime() - this.openedNanos) / 1.0E9f;
        String str = null;
        for (int i10 = 0; i10 < 3; i10++) {
            int i11 = COLUMNS[i10];
            int i12 = (i8 - ((min * 3) / 2)) + (i10 * min);
            int i13 = i12 + (min / 2);
            int i14 = i9 - BLOCK_H[i11];
            int i15 = MEDAL[i11];
            if (i11 == 0) {
                Ui.glow(guiGraphics, i12, i14, i12 + min, i9, i15, 0.14f);
            }
            Ui.round(guiGraphics, i12, i14, i12 + min, i9, Ui.alpha(i15, 52), 5);
            Ui.round(guiGraphics, i12, i14, i12 + min, i14 + 5, Ui.alpha(i15, 108), 4);
            Ui.outline(guiGraphics, i12, i14, i12 + min, i9, Ui.alpha(i15, 132), 5);
            if (i10 > 0) {
                guiGraphics.fill(i12, i14 + 5, i12 + 1, i9, 1342177280);
            }
            Matrix3x2fStack pose = guiGraphics.pose();
            pose.pushMatrix();
            pose.translate(i13, ((i14 + 5) + ((BLOCK_H[i11] - 5) / 2.0f)) - 6.0f);
            pose.scale(1.5f, 1.5f);
            guiGraphics.drawCenteredString(font, Component.literal(PLACE[i11]), 0, 0, Ui.TEXT);
            pose.popMatrix();
            if (i11 >= this.payers.size()) {
                guiGraphics.drawCenteredString(font, Component.literal("?"), i13, i14 - 14, Ui.TEXT_MUTED);
            } else {
                PaymentStats.Payer payer = this.payers.get(i11);
                int i16 = DOLL_SIZE[i11];
                float f = i14 + 1;
                int round = Math.round(f - (i16 * 1.9f));
                if (this.dolls[i11] == null && !Dolls.isBroken()) {
                    this.dolls[i11] = Dolls.create(skin(i11), payer.name);
                }
                boolean z2 = false;
                if (this.dolls[i11] != null) {
                    if (i11 == 0) {
                        this.dolls[i11].dance(nanoTime, 3);
                    } else {
                        this.dolls[i11].setCrouching(false);
                    }
                    z2 = Dolls.drawOnScreen(guiGraphics, this.dolls[i11], i11, i13, f, i16, z ? i6 : i13, screenRectangle);
                }
                if (!z2) {
                    Dolls.face(guiGraphics, skin(i11), i13 - 12, i14 - 28, 24);
                    round = i14 - 28;
                }
                int i17 = round - 14;
                if (i11 == 0) {
                    crown(guiGraphics, i13, round - 11);
                    i17 = round - STAT_H;
                }
                String plainSubstrByWidth = font.plainSubstrByWidth(payer.name, min + 30);
                int width = font.width(plainSubstrByWidth);
                guiGraphics.fill((i13 - (width / 2)) - 2, i17 - 2, ((i13 + width) - (width / 2)) + 2, i17 + 9, Ui.alpha(Ui.PANEL, 200));
                guiGraphics.drawString(font, Component.literal(plainSubstrByWidth), i13 - (width / 2), i17, i11 == 0 ? GOLD : Ui.TEXT);
                String compact = AmountFormat.compact(payer.total);
                guiGraphics.drawCenteredString(font, Component.literal(compact), i13, i9 + 5, i15);
                guiGraphics.drawCenteredString(font, Component.literal(StatsFormat.payments(payer.count)), i13, i9 + 16, Ui.TEXT_MUTED);
                if (z && i6 >= i12 && i6 < i12 + min && i7 >= i17 - 2 && i7 < i9 + SESSION_H) {
                    str = payer.name + " paid you " + compact + " in " + StatsFormat.payments(payer.count) + ". Last paid you " + StatsFormat.dateTime(payer.last);
                }
            }
        }
        guiGraphics.fill((i8 - ((min * 3) / 2)) - PAD, i9, i8 + ((min * 3) / 2) + PAD, i9 + 1, Ui.PANEL_BORDER);
        if (str != null) {
            guiGraphics.setTooltipForNextFrame(font, Component.literal(str), i6, i7);
        }
        return i3 + PODIUM_H;
    }

    private static void crown(GuiGraphics guiGraphics, int i, int i2) {
        int i3 = i - 7;
        guiGraphics.fill(i3, i2, i3 + 2, i2 + 2, GOLD);
        guiGraphics.fill(i3 + 6, i2, i3 + PAD, i2 + 2, GOLD);
        guiGraphics.fill(i3 + 12, i2, i3 + 14, i2 + 2, GOLD);
        guiGraphics.fill(i3, i2 + 2, i3 + 2, i2 + 4, GOLD);
        guiGraphics.fill(i3 + 4, i2 + 2, i3 + 10, i2 + 4, GOLD);
        guiGraphics.fill(i3 + 12, i2 + 2, i3 + 14, i2 + 4, GOLD);
        guiGraphics.fill(i3, i2 + 4, i3 + 14, i2 + 6, GOLD);
        guiGraphics.fill(i3, i2 + 6, i3 + 14, i2 + PAD, -3565026);
        guiGraphics.fill(i3 + 6, i2 + 4, i3 + PAD, i2 + 6, -1559474);
    }

    private static int header(GuiGraphics guiGraphics, Font font, String str, int i, int i2, int i3) {
        int i4 = (i + i2) / 2;
        int width = font.width(str);
        int i5 = i3 + 10;
        guiGraphics.fill(i + PAD, i5, (i4 - (width / 2)) - 6, i5 + 1, Ui.withAlpha(Ui.ACCENT, 0.32f));
        guiGraphics.fill(((i4 + width) - (width / 2)) + 6, i5, i2 - PAD, i5 + 1, Ui.withAlpha(Ui.ACCENT, 0.32f));
        guiGraphics.drawCenteredString(font, Component.literal(str), i4, i3 + 6, Ui.TEXT_DIM);
        return i3 + 22;
    }

    private int rows(GuiGraphics guiGraphics, Font font, int i, int i2, int i3, int i4, int i5, int i6, int i7, boolean z) {
        if (this.payers.size() <= 3) {
            guiGraphics.drawCenteredString(font, Component.literal(this.payers.isEmpty() ? this.emptyText : "Nobody else yet."), (i + i2) / 2, i3 + 7, Ui.TEXT_MUTED);
            return i3 + 22;
        }
        double max = Math.max(1.0d, this.payers.get(0).total);
        int i8 = i + PAD;
        int i9 = (i2 - PAD) - 6;
        for (int i10 = 3; i10 < this.payers.size(); i10++) {
            int i11 = i3 + ((i10 - 3) * 22);
            if (i11 + 22 >= i4 && i11 <= i5) {
                PaymentStats.Payer payer = this.payers.get(i10);
                boolean z2 = z && i7 >= i11 && i7 < i11 + 22 && i6 >= i8 && i6 < i2 - PAD;
                Ui.card(guiGraphics, i8, i11, i2 - PAD, (i11 + 22) - 2, z2 ? 1.0f : 0.0f, false);
                guiGraphics.drawString(font, Component.literal("#" + (i10 + 1)), i8 + 4, i11 + 6, Ui.TEXT_MUTED);
                Dolls.face(guiGraphics, skin(i10), i8 + 30, i11 + 2, 16);
                String compact = AmountFormat.compact(payer.total);
                String payments = StatsFormat.payments(payer.count);
                int i12 = i8 + 52;
                int max2 = Math.max(20, ((i9 - Math.max(font.width(compact), font.width(payments))) - PAD) - i12);
                guiGraphics.drawString(font, Component.literal(font.plainSubstrByWidth(payer.name, max2)), i12, i11 + 2, Ui.TEXT);
                guiGraphics.drawString(font, Component.literal(compact), i9 - font.width(compact), i11 + 2, GOLD);
                guiGraphics.drawString(font, Component.literal(payments), i9 - font.width(payments), i11 + 11, Ui.TEXT_MUTED);
                int max3 = Math.max(2, (int) Math.round(max2 * Math.min(1.0d, payer.total / max)));
                guiGraphics.fill(i12, i11 + 13, i12 + max2, i11 + 16, Ui.TRACK);
                guiGraphics.fill(i12, i11 + 13, i12 + max3, i11 + 16, Ui.withAlpha(Ui.ACCENT, 0.80f));
                if (z2) {
                    guiGraphics.setTooltipForNextFrame(font, Component.literal("Last paid you " + StatsFormat.dateTime(payer.last)), i6, i7);
                }
            }
        }
        return i3 + ((this.payers.size() - 3) * 22);
    }

    static int statsBar(GuiGraphics guiGraphics, Font font, int i, int i2, int i3, List<Stat> list, int i4) {
        int size = ((list.size() + i4) - 1) / i4;
        int i5 = ((i3 - i) - (4 * (i4 - 1))) / i4;
        int bottom = ((i2 + (size * (STAT_H + 4))) - 4) + 4;
        Ui.round(guiGraphics, i - 4, i2 - 4, i3 + 4, bottom, Ui.PANEL, 7);
        Ui.outline(guiGraphics, i - 4, i2 - 4, i3 + 4, bottom, Ui.PANEL_BORDER, 7);
        for (int i6 = 0; i6 < list.size(); i6++) {
            Stat stat = list.get(i6);
            int i7 = i + ((i6 % i4) * (i5 + 4));
            int i8 = i2 + ((i6 / i4) * (STAT_H + 4));
            Ui.card(guiGraphics, i7, i8, i7 + i5, i8 + STAT_H, Ui.CARD, Ui.BORDER);
            int tint = stat.color() == -1 ? Ui.ACCENT : stat.color();
            Ui.round(guiGraphics, i7, i8, i7 + 2, i8 + STAT_H, tint, 1);
            guiGraphics.drawString(font, Component.literal(font.plainSubstrByWidth(stat.label(), i5 - 9)), i7 + 6, i8 + 4, Ui.TEXT_MUTED);
            guiGraphics.drawString(font, Component.literal(font.plainSubstrByWidth(stat.value(), i5 - 9)), i7 + 6, i8 + 14, stat.color());
        }
        return (size * (STAT_H + 4)) - 4;
    }

    static int statsBarHeight(int i, int i2) {
        return ((((i + i2) - 1) / i2) * 29) - 4;
    }

    private void sessionRows(GuiGraphics guiGraphics, Font font, int i, int i2, int i3, int i4, int i5, int i6, int i7, boolean z) {
        if (this.sessions.isEmpty()) {
            guiGraphics.drawCenteredString(font, Component.literal("No sessions yet. One starts each time you join a world or server."), (i + i2) / 2, i3 + PAD, Ui.TEXT_MUTED);
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        int i8 = i + PAD;
        int i9 = (i2 - PAD) - 6;
        for (int i10 = 0; i10 < this.sessions.size(); i10++) {
            int i11 = i3 + (i10 * SESSION_H);
            if (i11 + SESSION_H >= i4 && i11 <= i5) {
                PaymentStats.Session session = this.sessions.get(i10);
                boolean isOpen = session.isOpen();
                boolean z2 = z && i7 >= i11 && i7 < (i11 + SESSION_H) - 2 && i6 >= i8 && i6 < i9;
                Ui.card(guiGraphics, i8, i11, i9, (i11 + SESSION_H) - 2, z2 ? 1.0f : 0.0f, false);
                String compact = AmountFormat.compact(session.total);
                int width = (i9 - 4) - font.width(compact);
                guiGraphics.drawString(font, Component.literal(compact), width, i11 + 3, session.total > 0.0d ? GOLD : Ui.TEXT_MUTED);
                guiGraphics.drawString(font, Component.literal(font.plainSubstrByWidth(payerNames(session), Math.max(20, (width - i8) - 12))), i8 + 4, i11 + 3, session.count == 0 ? Ui.TEXT_MUTED : isOpen ? Ui.ACCENT_LIGHT : Ui.TEXT);
                guiGraphics.drawString(font, Component.literal(font.plainSubstrByWidth(StatsFormat.span(session.joined, session.end(currentTimeMillis), isOpen) + "  (" + StatsFormat.duration(session.end(currentTimeMillis) - session.joined) + (isOpen ? ", playing now" : "") + ")  " + StatsFormat.payments(session.count), (i9 - i8) - PAD)), i8 + 4, i11 + 13, Ui.TEXT_MUTED);
                if (z2) {
                    String str = "Click to see who paid you in this session";
                    if (session.biggestFrom != null) {
                        str = "Biggest: " + AmountFormat.compact(session.biggestAmount) + " from " + session.biggestFrom + ". " + str;
                    }
                    guiGraphics.setTooltipForNextFrame(font, Component.literal(str), i6, i7);
                }
            }
        }
    }

    private static String payerNames(PaymentStats.Session session) {        List<PaymentStats.Payer> pVar = PaymentStats.top(session.payers);
        if (pVar.isEmpty()) {
            return "No payments";
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < Math.min(3, pVar.size())) {
            sb.append(i > 0 ? ", " : "").append(pVar.get(i).name);
            i++;
        }
        if (pVar.size() > 3) {
            sb.append(" +").append(pVar.size() - 3).append(" more");
        }
        return sb.toString();
    }

    boolean click(double d, double d2) {
        if (this.sessions == null || this.onSession == null || this.sessionsTop == Integer.MIN_VALUE || d2 < this.panelTop || d2 >= this.panelBottom || d < this.panelLeft + PAD || d >= (this.panelRight - PAD) - 6) {
            return false;
        }
        int floor = (int) Math.floor((d2 - this.sessionsTop) / 26.0d);
        if (d2 < this.sessionsTop || floor < 0 || floor >= this.sessions.size() || (d2 - this.sessionsTop) - (floor * SESSION_H) >= 24.0d) {
            return false;
        }
        this.onSession.accept(this.sessions.get(floor));
        return true;
    }
}
