package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.HudFireworks;
import com.paymentalerts.fx.MoneyParticles;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import com.paymentalerts.sound.SoundCues;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import org.joml.Matrix3x2fStack;

final class JackpotAlert implements StageAlert {
    private static final float SPIN_START = 0.4f;
    private static final float WIN = 2.4f;
    private static final float END = 9.2f;
    private static final float FADE = 0.7f;
    private static final float DECEL = 0.5f;
    private static final float SPEED = 16.0f;
    private static final float MW = 170.0f;
    private static final float MH = 100.0f;
    private static final float WIN_TOP = 32.0f;
    private static final float WIN_BOTTOM = 76.0f;
    private static final float WIN_MID = 54.0f;
    private static final float CELL = 26.0f;
    private static final float REEL_W = 44.0f;
    private final Payment payment;
    private final long startNanos;
    private boolean fountainA;
    private boolean fountainB;
    static final String[] VARIATIONS = {"$ $ $", "7 7 7", "Stars", "Diamonds", "BAR BAR BAR", "Cherries"};
    private static final float[] STOP = {1.25f, 1.7f, 2.15f};
    private static final float[] REEL_X = {14.0f, 63.0f, 112.0f};
    private static final Symbol[] STRIP = {Symbol.SEVEN, Symbol.STAR, Symbol.CHERRY, Symbol.DOLLAR, Symbol.DIAMOND, Symbol.BAR, Symbol.SEVEN, Symbol.CHERRY, Symbol.STAR, Symbol.DIAMOND, Symbol.DOLLAR, Symbol.BAR};
    private final SoundCues cues = new SoundCues();
    private final MoneyParticles coins = new MoneyParticles();
    private final HudFireworks fireworks = new HudFireworks(10, 1);
    private final float[] startPos = new float[3];
    private float globalAlpha = 1.0f;

    private enum Symbol {
        SEVEN,
        DOLLAR,
        BAR,
        STAR,
        CHERRY,
        DIAMOND
    }

    JackpotAlert(Payment payment, long j, int i) {
        this.payment = payment;
        this.startNanos = j;
        Random random = new Random();
        Symbol symbol = new Symbol[]{Symbol.DOLLAR, Symbol.SEVEN, Symbol.STAR, Symbol.DIAMOND, Symbol.BAR, Symbol.CHERRY}[Variations.pick(i, VARIATIONS.length)];
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < STRIP.length; i2++) {
            if (STRIP[i2] == symbol) {
                arrayList.add(Integer.valueOf(i2));
            }
        }
        for (int i3 = 0; i3 < 3; i3++) {
            this.startPos[i3] = ((Integer) arrayList.get(random.nextInt(arrayList.size()))).intValue() - ((SPEED * ((STOP[i3] - DECEL) - SPIN_START)) + 4.0f);
        }
        float f = SPIN_START;
        while (true) {
            float f2 = f;
            if (f2 >= STOP[2]) {
                break;
            }
            this.cues.sound(f2, AlertSounds.JACKPOT_TICK, 1.7f, 0.35f);
            f = f2 + 0.09f;
        }
        float[] fArr = {1.0f, 1.19f, 1.5f};
        for (int i4 = 0; i4 < 3; i4++) {
            this.cues.sound(STOP[i4], AlertSounds.JACKPOT_STOP, 0.9f, 0.8f);
            this.cues.sound(STOP[i4], AlertSounds.JACKPOT_PLING, fArr[i4], FADE);
        }
        this.cues.sound(WIN, AlertSounds.MONEY_LEVELUP, 1.0f, 0.9f);
        this.cues.sound(WIN, AlertSounds.JACKPOT_BELL, 1.2f, 0.8f);
        float[] fArr2 = {1.0f, 1.26f, 1.5f, 2.0f};
        for (int i5 = 0; i5 < 2; i5++) {
            for (int i6 = 0; i6 < fArr2.length; i6++) {
                this.cues.sound(WIN + (i5 * 0.6f) + (i6 * 0.1f), AlertSounds.JACKPOT_CHIME, fArr2[i6], FADE);
            }
        }
        for (int i7 = 0; i7 < 16; i7++) {
            this.cues.sound(WIN + (random.nextFloat() * 3.0f), AlertSounds.MONEY_COIN, 0.8f + (random.nextFloat() * 0.8f), 0.35f);
        }
    }

    private float elapsed(long j) {
        return (j - this.startNanos) / 1.0E9f;
    }

    @Override // com.paymentalerts.alert.StageAlert
    public void tick(long j) {
        this.cues.update(elapsed(j));
    }

    @Override // com.paymentalerts.alert.StageAlert
    public boolean isFinished(long j) {
        return elapsed(j) >= END && this.cues.isDone();
    }

    private float reelPos(int i, float f) {
        float f2 = STOP[i] - DECEL;
        if (f <= SPIN_START) {
            return this.startPos[i];
        }
        if (f <= f2) {
            return this.startPos[i] + (SPEED * (f - SPIN_START));
        }
        float f3 = this.startPos[i] + (SPEED * (f2 - SPIN_START));
        if (f <= STOP[i]) {
            float f4 = f - f2;
            return f3 + (SPEED * (f4 - ((f4 * f4) / 1.0f)));
        }
        float f5 = f - STOP[i];
        return f3 + 4.0f + (0.18f * ((float) Math.sin(f5 * 18.0f)) * ((float) Math.exp((-f5) * 9.0f)));
    }

    @Override // com.paymentalerts.alert.StageAlert
    public void render(GuiGraphics guiGraphics, Font font, int i, int i2, float f, long j) {
        float elapsed = elapsed(j);
        this.globalAlpha = elapsed > 8.5f ? Math.max(0.0f, (END - elapsed) / FADE) : 1.0f;
        float max = Math.max(0.8f, Math.min(1.8f, Math.min(i / 380.0f, i2 / 240.0f)));
        float f2 = (i / 2.0f) - ((MW * max) / 2.0f);
        float outCubic = 8.0f - (((MH * max) + 20.0f) * (1.0f - Easing.outCubic(elapsed / 0.45f)));
        float f3 = elapsed >= WIN ? 1.0f : 0.0f;
        if (elapsed >= WIN) {
            this.fireworks.update(f, i, i2);
            this.fireworks.render(guiGraphics);
            if (!this.fountainA) {
                this.fountainA = true;
                this.coins.fountain(f2 + 6.0f, outCubic + (MH * max * 0.6f), 50, i2);
                this.coins.fountain((f2 + (MW * max)) - 6.0f, outCubic + (MH * max * 0.6f), 50, i2);
            }
            if (!this.fountainB && elapsed >= 2.9f) {
                this.fountainB = true;
                this.coins.fountain(i / 2.0f, outCubic + (MH * max * DECEL), 70, i2);
            }
        }
        this.coins.update(f, i, i2);
        this.coins.render(guiGraphics, this.globalAlpha);
        Draw2D.flush(guiGraphics);
        Matrix3x2fStack pose = guiGraphics.pose();
        pose.pushMatrix();
        pose.translate(f2, outCubic);
        pose.scale(max, max);
        for (int i3 = 0; i3 < 3; i3++) {
            drawReel(guiGraphics, font, i3, elapsed, f3);
        }
        drawFrame(guiGraphics, font, elapsed, f3);
        pose.popMatrix();
        Draw2D.flush(guiGraphics);
        if (elapsed >= WIN) {
            float f4 = elapsed - WIN;
            float min = Math.min(1.0f, f4 / 0.2f) * this.globalAlpha;
            float outBack = 0.6f + (SPIN_START * Easing.outBack(f4 / 0.45f));
            MutableComponent colored = TextFx.colored(this.payment.name(), true, i4 -> {
                return Draw2D.lerpRgb(16757504, 16774328, DECEL + (DECEL * ((float) Math.sin((elapsed * 4.5f) - (i4 * 0.45f)))));
            });
            float f5 = outCubic + (MH * max) + 10.0f;
            float fitScale = TextFx.fitScale(font, colored, i * 0.8f, 2.6f);
            TextFx.centered(guiGraphics, font, colored, i / 2.0f, f5, fitScale * outBack, min);
            TextFx.centered(guiGraphics, font, TextFx.plain("paid you " + this.payment.displayAmount(), 16777215, true), i / 2.0f, f5 + (9.0f * fitScale) + 6.0f, 1.5f, min);
        }
    }

    private int c(float f, int i) {
        return Draw2D.argb(f * this.globalAlpha, i);
    }

    private void drawReel(GuiGraphics guiGraphics, Font font, int i, float f, float f2) {
        float f3 = REEL_X[i];
        float f4 = f3 + REEL_W;
        float f5 = (f3 + f4) / 2.0f;
        Draw2D.gradientV(guiGraphics, f3, WIN_TOP, f4, WIN_MID, c(1.0f, 12169112), c(1.0f, 16775399));
        Draw2D.gradientV(guiGraphics, f3, WIN_MID, f4, WIN_BOTTOM, c(1.0f, 16775399), c(1.0f, 12169112));
        if (f2 > 0.0f && ((int) (f * 8.0f)) % 2 == 0) {
            Draw2D.rect(guiGraphics, f3, 42.0f, f4, 66.0f, c(0.45f, 16769357));
        }
        float reelPos = reelPos(i, f);
        int floor = (int) Math.floor(reelPos);
        boolean z = f > SPIN_START && f < STOP[i] - 0.15f;
        for (int i2 = floor - 2; i2 <= floor + 2; i2++) {
            float f6 = WIN_MID + ((reelPos - i2) * CELL);
            if (f6 >= SPEED && f6 <= 92.0f) {
                drawSymbol(guiGraphics, font, STRIP[Math.floorMod(i2, STRIP.length)], f5, f6, z ? 0.55f : 1.0f);
            }
        }
    }

    private void drawSymbol(GuiGraphics guiGraphics, Font font, Symbol symbol, float f, float f2, float f3) {
        switch (symbol.ordinal()) {
            case HudFireworks.CLASSIC /* 0 */:
                TextFx.centered(guiGraphics, font, TextFx.plain("7", 14687012, true), f + 1.0f, f2 - 9.0f, 2.2f, f3 * this.globalAlpha);
                break;
            case HudFireworks.GOLD /* 1 */:
                TextFx.centered(guiGraphics, font, TextFx.plain("$", 2006586, true), f + 1.0f, f2 - 9.0f, 2.2f, f3 * this.globalAlpha);
                break;
            case HudFireworks.RAINBOW_FINALE /* 2 */:
                Draw2D.rect(guiGraphics, f - 13.0f, f2 - 6.0f, f + 13.0f, f2 + 6.0f, c(f3, 1447446));
                Draw2D.rect(guiGraphics, f - 11.5f, f2 - 4.5f, f + 11.5f, f2 + 4.5f, c(f3, 2829099));
                TextFx.centered(guiGraphics, font, TextFx.plain("BAR", 16777215, true), f + DECEL, f2 - 4.0f, 1.0f, f3 * this.globalAlpha);
                break;
            case 3:
                int c = c(f3, 16761370);
                int c2 = c(f3, 16773280);
                for (int i = 0; i < 5; i++) {
                    double d = (-1.5707963267948966d) + (((i * 3.141592653589793d) * 2.0d) / 5.0d);
                    double d2 = d - 0.6283185307179586d;
                    double d3 = d + 0.6283185307179586d;
                    float cos = f + (((float) Math.cos(d)) * 10.0f);
                    float sin = f2 + (((float) Math.sin(d)) * 10.0f);
                    float cos2 = f + (((float) Math.cos(d2)) * 4.2f);
                    float sin2 = f2 + (((float) Math.sin(d2)) * 4.2f);
                    float cos3 = f + (((float) Math.cos(d3)) * 4.2f);
                    float sin3 = f2 + (((float) Math.sin(d3)) * 4.2f);
                    Draw2D.triangle(guiGraphics, f, f2, c2, cos2, sin2, c, cos, sin, c);
                    Draw2D.triangle(guiGraphics, f, f2, c2, cos, sin, c, cos3, sin3, c);
                }
                break;
            case 4:
                int c3 = c(f3, 3046706);
                Draw2D.quad(guiGraphics, f - 4.5f, f2 + 1.0f, c3, f - 3.5f, f2 + 1.0f, c3, f + 2.5f, f2 - 9.0f, c3, f + 1.5f, f2 - 9.0f, c3);
                Draw2D.quad(guiGraphics, f + 4.5f, f2 + 2.0f, c3, f + 5.5f, f2 + 2.0f, c3, f + 2.5f, f2 - 9.0f, c3, f + 1.5f, f2 - 9.0f, c3);
                Draw2D.circle(guiGraphics, f - 4.0f, f2 + 4.0f, 4.6f, c(f3, 16734810), c(f3, 11538458));
                Draw2D.circle(guiGraphics, f + 5.0f, f2 + 5.0f, 4.6f, c(f3, 16734810), c(f3, 11538458));
                Draw2D.glow(guiGraphics, f - 5.5f, f2 + 2.5f, 1.6f, f3 * this.globalAlpha, 16777215);
                break;
            case 5:
                Draw2D.quad(guiGraphics, f, f2 - 10.0f, c(f3, 12121087), f - 8.0f, f2, c(f3, 2989275), f, f2 + 10.0f, c(f3, 1402304), f + 8.0f, f2, c(f3, 5688562));
                break;
        }
    }

    private void drawFrame(GuiGraphics guiGraphics, Font font, float f, float f2) {
        MutableComponent plain;
        int c = c(1.0f, 13836589);
        int c2 = c(1.0f, 7999254);
        int c3 = c(1.0f, 11541029);
        int c4 = c(1.0f, 14721566);
        int c5 = c(1.0f, 10250762);
        Draw2D.gradientV(guiGraphics, 3.0f, 3.0f, 167.0f, WIN_TOP, c, c3);
        Draw2D.gradientV(guiGraphics, 3.0f, WIN_BOTTOM, 167.0f, 97.0f, c3, c2);
        Draw2D.rect(guiGraphics, 3.0f, WIN_TOP, REEL_X[0], WIN_BOTTOM, c3);
        Draw2D.rect(guiGraphics, REEL_X[2] + REEL_W, WIN_TOP, 167.0f, WIN_BOTTOM, c3);
        for (int i = 0; i < 2; i++) {
            Draw2D.rect(guiGraphics, REEL_X[i] + REEL_W, WIN_TOP, REEL_X[i + 1], WIN_BOTTOM, c(1.0f, 5900816));
        }
        Draw2D.rect(guiGraphics, 0.0f, 0.0f, MW, 3.0f, c4);
        Draw2D.rect(guiGraphics, 0.0f, 97.0f, MW, MH, c5);
        Draw2D.gradientV(guiGraphics, 0.0f, 3.0f, 3.0f, 97.0f, c4, c5);
        Draw2D.gradientV(guiGraphics, 167.0f, 3.0f, MW, 97.0f, c4, c5);
        Draw2D.rect(guiGraphics, REEL_X[0] - 2.0f, 30.0f, REEL_X[2] + REEL_W + 2.0f, WIN_TOP, c4);
        Draw2D.rect(guiGraphics, REEL_X[0] - 2.0f, WIN_BOTTOM, REEL_X[2] + REEL_W + 2.0f, 78.0f, c5);
        float f3 = f2 > 0.0f ? 0.9f : 0.35f;
        Draw2D.rect(guiGraphics, REEL_X[0] - 6.0f, 53.4f, REEL_X[0] - 1.0f, 54.6f, c(f3, 16719904));
        Draw2D.rect(guiGraphics, REEL_X[2] + REEL_W + 1.0f, 53.4f, REEL_X[2] + REEL_W + 6.0f, 54.6f, c(f3, 16719904));
        Draw2D.rect(guiGraphics, 10.0f, 6.0f, 160.0f, 27.0f, c(1.0f, 2753800));
        int i2 = (int) (f * (f2 > 0.0f ? 14.0f : 8.0f));
        int i3 = 0;
        while (i3 < 2) {
            float f4 = i3 == 0 ? 6.0f : 27.0f;
            for (int i4 = 0; i4 < 16; i4++) {
                float f5 = 14.0f + ((i4 * 142.0f) / 15.0f);
                boolean z = f2 > 0.0f ? ((i4 + i3) + i2) % 2 == 0 : (i4 + i2) % 3 == 0;
                int i5 = (f2 <= 0.0f || i2 % 4 >= 2) ? 16770688 : 16735472;
                if (z) {
                    Draw2D.glow(guiGraphics, f5, f4, 4.0f, FADE * this.globalAlpha, i5);
                }
                Draw2D.circle(guiGraphics, f5, f4, 1.4f, c(1.0f, z ? 16777215 : 6968608), c(1.0f, z ? i5 : 4864528));
            }
            i3++;
        }
        float sin = f2 > 0.0f ? 1.0f + (0.12f * ((float) Math.sin(f * 12.0f))) : 1.0f;
        if (f2 > 0.0f) {
            plain = TextFx.colored("JACKPOT", true, i6 -> {
                return Draw2D.hsv((f * 0.8f) + (i6 * 0.09f), 0.65f, 1.0f);
            });
        } else {
            plain = TextFx.plain("JACKPOT", 16762941, true);
        }
        Draw2D.flush(guiGraphics);
        TextFx.centered(guiGraphics, font, plain, 85.0f, 17.0f - (6.4f * sin), 1.6f * sin, this.globalAlpha);
    }
}
