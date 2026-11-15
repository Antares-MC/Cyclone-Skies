package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.skin.Dolls;
import com.paymentalerts.skin.SkinDoll;
import com.paymentalerts.skin.SkinFetcher;
import com.paymentalerts.sound.AlertSounds;
import com.paymentalerts.sound.SoundCues;
import java.util.Random;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;

final class GodTierAlert implements StageAlert {
    private static final float DESCEND_START = 1.3f;
    private static final float LANDED = 2.6f;
    private static final float CROWN_DROP = 2.9f;
    private static final float CROWN_ON = 3.25f;
    private static final float END = 12.5f;
    private static final float FADE = 1.3f;
    private static final float HEAD_TOP = 1.89f;
    private static final float CROUCH_DROP = 0.37f;
    private final Payment payment;
    private final long startNanos;
    private final SkinFetcher.Skin skin;
    private final SoundCues cues = new SoundCues();
    private final Particles2D fx = new Particles2D();
    private final Random random = new Random();
    private final float[][] clouds = new float[18][3];
    private final Theme theme;
    private SkinDoll doll;
    private boolean confettiBurst;
    private float sparkleTimer;
    private float confettiTimer;
    static final String[] VARIATIONS = {"Gold", "Diamond", "Emerald", "Netherite"};
    private static final int[] CONFETTI = {16731469, 16762941, 5111688, 5093631, 12865023, 16731602, 16777215};
    private static final Theme[] THEMES = {new Theme("GOD TIER", 16757504, 16774328, 16770976, 16773312, 16765773, 13214247, 16774336, 16762941, 16761370, 16773280, 11565568, 16773280, new int[]{14688298, 2784255, 2015834, 2784255, 14688298}, 16774336), new Theme("DIAMOND GOD", 5235680, 15269887, 12580607, 14744575, 5235680, 2793392, 15269887, 5235680, 5235680, 14221311, 1740703, 16777215, new int[]{16777215, 2784255, 16777215, 2784255, 16777215}, 14221311), new Theme("EMERALD EMPEROR", 4054122, 14221280, 12124104, 14221280, 4054122, 1739322, 14221280, 4054122, 4054122, 12124104, 1014314, 16769136, new int[]{16761370, 1014314, 16761370, 1014314, 16761370}, 14221280), new Theme("NETHERITE KING", 10128032, 15788287, 14731519, 15390975, 9067775, 3811914, 15786239, 10517759, 4866632, 9076872, 2367010, 16761370, new int[]{10502399, 16761370, 10502399, 16761370, 10502399}, 15390975)};

    private static final record Theme(String title, int titleA, int titleB, int rays, int column, int aura, int vignette, int sparkA, int sparkB, int crown, int crownLight, int crownDark, int crownTip, int[] jewels, int amount) {


        

        

        















    }

    GodTierAlert(Payment payment, long j, int i) {
        this.payment = payment;
        this.startNanos = j;
        this.theme = THEMES[Variations.pick(i, VARIATIONS.length)];
        this.skin = SkinFetcher.get(payment.lookupName());
        for (float[] fArr : this.clouds) {
            fArr[0] = (this.random.nextFloat() - 0.5f) * 0.7f;
            fArr[1] = this.random.nextFloat() * 0.14f;
            fArr[2] = 0.07f + (this.random.nextFloat() * 0.06f);
        }
        this.cues.sound(0.0f, AlertSounds.GOD_ACTIVATE, 0.8f, 1.0f).sound(0.8f, AlertSounds.GOD_CONDUIT, 1.0f, 1.0f).sound(1.3f, AlertSounds.GOD_BELL, 1.0f, 0.9f).sound(LANDED, AlertSounds.GOD_BELL, 1.3f, 0.6f).sound(CROWN_ON, AlertSounds.GOD_FANFARE, 1.0f, 1.0f).sound(CROWN_ON, AlertSounds.GOD_TOTEM, 1.2f, 0.6f).sound(3.3f, AlertSounds.MONEY_LEVELUP, 0.8f, 0.7f).sound(3.75f, AlertSounds.BLACKHOLE_SPARKLE, 1.0f, 0.7f).sound(4.65f, AlertSounds.BLACKHOLE_SPARKLE, 1.2f, 0.6f).sound(5.85f, AlertSounds.BLACKHOLE_SPARKLE, 0.9f, 0.6f).sound(6.65f, AlertSounds.GOD_BELL, 1.2f, 0.6f).sound(8.25f, AlertSounds.BLACKHOLE_SPARKLE, 1.1f, 0.5f);
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

    @Override // com.paymentalerts.alert.StageAlert
    public void render(GuiGraphics guiGraphics, Font font, int i, int i2, float f, long j) {
        float elapsed = elapsed(j);
        float max = elapsed > 11.2f ? Math.max(0.0f, (END - elapsed) / 1.3f) : 1.0f;
        float f2 = i / 2.0f;
        float min = Math.min(1.0f, elapsed / 1.0f);
        float f3 = i2 * 0.76f;
        MutableComponent colored = TextFx.colored("♛ " + this.theme.title() + " ♛", true, i3 -> {
            return Draw2D.lerpRgb(this.theme.titleA(), this.theme.titleB(), 0.5f + (0.5f * ((float) Math.sin((elapsed * 4.0f) - (i3 * 0.4f)))));
        });
        float max2 = Math.max(6.0f, i2 * 0.04f);
        float fitScale = TextFx.fitScale(font, colored, i * 0.8f, LANDED);
        MutableComponent colored2 = TextFx.colored(this.payment.name(), true, i4 -> {
            return Draw2D.hsv((elapsed * 0.3f) + (i4 * 0.06f), 0.45f, 1.0f);
        });
        float fitScale2 = TextFx.fitScale(font, colored2, i * 0.8f, 2.0f);
        float f4 = max2 + (9.0f * fitScale) + 6.0f;
        float f5 = f4 + (9.0f * fitScale2) + 5.0f;
        int max3 = Math.max(14, Math.round(Math.min(i2 * 0.2f, ((f3 - (f5 + 11.7f)) - 8.0f) / 2.32f)));
        float outCubic = elapsed < 1.3f ? (-i2) * 0.2f : f3 - ((f3 + (i2 * 0.2f)) * (1.0f - Easing.outCubic((elapsed - 1.3f) / 1.3f)));
        this.fx.update(f);
        Draw2D.rect(guiGraphics, 0.0f, 0.0f, i, i2, Draw2D.argb(0.72f * min * max, 461342));
        Draw2D.vignette(guiGraphics, i, i2, 0.3f, 0.45f * min * max, this.theme.vignette());
        float min2 = Math.min(1.0f, Math.max(0.0f, (elapsed - 0.5f) / 1.2f)) * max;
        if (min2 > 0.0f) {
            float f6 = (-i2) * 0.08f;
            float sin = ((float) Math.sin(elapsed * 0.4f)) * 6.0f;
            for (int i5 = 0; i5 < 16; i5++) {
                double radians = Math.toRadians(25.0f + (i5 * 8.666667f) + sin);
                double radians2 = Math.toRadians(2.2d);
                float f7 = i2 * 1.4f;
                Draw2D.triangle(guiGraphics, f2, f6, Draw2D.argb((i5 % 2 == 0 ? 0.22f : 0.12f) * min2 * (0.85f + (0.15f * ((float) Math.sin((elapsed * 2.0f) + i5)))), this.theme.rays()), f2 + (((float) Math.cos(radians - radians2)) * f7), f6 + (((float) Math.sin(radians - radians2)) * f7), Draw2D.argb(0.0f, this.theme.rays()), f2 + (((float) Math.cos(radians + radians2)) * f7), f6 + (((float) Math.sin(radians + radians2)) * f7), Draw2D.argb(0.0f, this.theme.rays()));
            }
            float f8 = i2 * 0.13f;
            Draw2D.gradientH(guiGraphics, f2 - f8, 0.0f, f2, f3, Draw2D.argb(0.0f, this.theme.column()), Draw2D.argb(0.4f * min2, this.theme.column()));
            Draw2D.gradientH(guiGraphics, f2, 0.0f, f2 + f8, f3, Draw2D.argb(0.4f * min2, this.theme.column()), Draw2D.argb(0.0f, this.theme.column()));
            Draw2D.ellipse(guiGraphics, f2, f3, f8 * 1.6f, f8 * 0.35f, Draw2D.argb(0.5f * min2, this.theme.rays()), Draw2D.argb(0.0f, this.theme.rays()));
        }
        float outCubic2 = Easing.outCubic(Math.max(0.0f, (elapsed - 0.4f) / 1.6f));
        for (float[] fArr : this.clouds) {
            Draw2D.glow(guiGraphics, f2 + (fArr[0] * i) + ((fArr[0] < 0.0f ? -1.0f : 1.0f) * outCubic2 * i * 0.45f), fArr[1] * i2, fArr[2] * i2 * 1.5f, 0.75f * min * max, 15659263);
        }
        Draw2D.glow(guiGraphics, f2, outCubic - (max3 * 0.95f), max3 * 1.6f * (0.85f + (0.15f * ((float) Math.sin(elapsed * 3.0f)))), 0.55f * Math.min(1.0f, Math.max(0.0f, (elapsed - 1.3f) / 0.6f)) * max, this.theme.aura());
        if (elapsed > 1.3f && elapsed < 11.2f) {
            this.sparkleTimer += f;
            while (this.sparkleTimer > 0.04f) {
                this.sparkleTimer -= 0.04f;
                Particles2D.P add = this.fx.add(Particles2D.Kind.SPARK, f2 + (this.fx.rand(-1.4f, 1.4f) * max3), outCubic - (this.fx.rand(0.0f, 2.0f) * max3), this.fx.rand(-6.0f, 6.0f), -this.fx.rand(10.0f, 30.0f), this.fx.rand(1.2f, 2.2f), this.fx.rand(0.6f, 1.2f), this.theme.sparkA(), this.theme.sparkB());
                add.drag = 0.2f;
                add.twinkle = true;
            }
        }
        if (elapsed >= CROWN_ON && !this.confettiBurst) {
            this.confettiBurst = true;
            int i6 = -1;
            while (i6 <= 1) {
                for (int i7 = 0; i7 < 110; i7++) {
                    float radians3 = (float) Math.toRadians(i6 < 0 ? this.fx.rand(-80.0f, -45.0f) : this.fx.rand(-135.0f, -100.0f));
                    float rand = i2 * this.fx.rand(0.8f, 1.6f);
                    spawnConfetti(i6 < 0 ? 0.0f : i, i2, ((float) Math.cos(radians3)) * rand, ((float) Math.sin(radians3)) * rand, i2);
                }
                i6 += 2;
            }
        }
        if (this.confettiBurst && elapsed < 10.2f) {
            this.confettiTimer += f;
            while (this.confettiTimer > 0.045454547f) {
                this.confettiTimer -= 0.045454547f;
                spawnConfetti(this.fx.rand(0.0f, i), -8.0f, this.fx.rand(-10.0f, 10.0f), this.fx.rand(10.0f, 30.0f), i2);
            }
        }
        this.fx.render(guiGraphics, max);
        Draw2D.flush(guiGraphics);
        if (this.doll == null) {
            this.doll = Dolls.create(this.skin, this.payment.name());
        }
        boolean z = false;
        if (elapsed >= 1.3f && max > 0.05f) {
            boolean z2 = false;
            if (this.doll != null) {
                this.doll.dance(elapsed - CROWN_ON);
                z = this.doll.isCrouching();
                z2 = Dolls.draw(guiGraphics, this.doll, f2, outCubic, max3, f2, outCubic - (max3 * 1.6f));
            }
            if (!z2) {
                z = false;
                int round = Math.round(max3 * 0.55f);
                Dolls.face(guiGraphics, this.skin, Math.round(f2 - (round / 2.0f)), Math.round(outCubic - (max3 * HEAD_TOP)), round);
            }
        }
        if (elapsed >= CROWN_DROP) {
            float f9 = outCubic - (max3 * (HEAD_TOP - (z ? CROUCH_DROP : 0.0f)));
            float min3 = Math.min(1.0f, (elapsed - CROWN_DROP) / 0.3499999f);
            drawCrown(guiGraphics, f2, (f9 - (((1.0f - (min3 * min3)) * i2) * 0.6f)) - (elapsed > CROWN_ON ? ((((float) Math.abs(Math.sin((elapsed - CROWN_ON) * 14.0f))) * ((float) Math.exp((-(elapsed - CROWN_ON)) * 6.0f))) * max3) * 0.12f : 0.0f), max3 * 0.62f, max);
            if (elapsed >= CROWN_ON && elapsed < 3.85f) {
                float f10 = (elapsed - CROWN_ON) / 0.6f;
                Draw2D.glow(guiGraphics, f2, f9 - (max3 * 0.15f), max3 * (0.6f + (1.4f * f10)), 0.9f * (1.0f - f10), 16777215);
            }
        }
        Draw2D.flush(guiGraphics);
        TextFx.centered(guiGraphics, font, colored, f2, max2, fitScale, Math.min(1.0f, Math.max(0.0f, (elapsed - 0.8f) / 0.5f)) * max);
        if (elapsed >= CROWN_ON) {
            float f11 = elapsed - CROWN_ON;
            float min4 = Math.min(1.0f, f11 / 0.2f) * max;
            float outBack = 0.6f + (0.4f * Easing.outBack(f11 / 0.45f));
            TextFx.centered(guiGraphics, font, colored2, f2, f4 + (((9.0f * fitScale2) * (1.0f - outBack)) / 2.0f), fitScale2 * outBack, min4);
            TextFx.centered(guiGraphics, font, TextFx.plain("paid you " + this.payment.displayAmount(), this.theme.amount(), true), f2, f5, 1.3f, min4);
        }
    }

    private void spawnConfetti(float f, float f2, float f3, float f4, int i) {
        Particles2D.P add = this.fx.add(Particles2D.Kind.CONFETTI, f, f2, f3, f4, this.fx.rand(3.5f, 5.5f), this.fx.rand(1.6f, 2.4f), CONFETTI[this.random.nextInt(CONFETTI.length)], 0);
        add.fadeRgb = add.rgb;
        add.gravity = i * 0.35f;
        add.drag = 1.6f;
        add.angle = this.fx.rand(0.0f, 6.28f);
        add.spin = this.fx.rand(-9.0f, 9.0f);
    }

    private void drawCrown(GuiGraphics guiGraphics, float f, float f2, float f3, float f4) {
        float f5 = f3 / 2.0f;
        float f6 = f3 * 0.22f;
        float f7 = f3 * 0.42f;
        int argb = Draw2D.argb(f4, this.theme.crown());
        int argb2 = Draw2D.argb(f4, this.theme.crownLight());
        int argb3 = Draw2D.argb(f4, this.theme.crownDark());
        int argb4 = Draw2D.argb(f4, this.theme.crownTip());
        Draw2D.glow(guiGraphics, f, f2 - f6, f3 * 0.9f, 0.35f * f4, this.theme.aura());
        int i = 0;
        while (i < 5) {
            float f8 = (f - f5) + ((f3 * (i + 0.5f)) / 5.0f);
            float f9 = (i == 2 ? 1.15f : (i == 1 || i == 3) ? 0.85f : 1.0f) * f7;
            Draw2D.triangle(guiGraphics, f8 - (f3 * 0.11f), f2 - f6, argb, f8 + (f3 * 0.11f), f2 - f6, argb3, f8, (f2 - f6) - f9, argb2);
            Draw2D.circle(guiGraphics, f8, (f2 - f6) - f9, f3 * 0.045f, argb4, argb);
            i++;
        }
        Draw2D.gradientV(guiGraphics, f - f5, f2 - f6, f + f5, f2, argb2, argb3);
        int[] jewels = this.theme.jewels();
        for (int i2 = 0; i2 < 5; i2++) {
            Draw2D.circle(guiGraphics, (f - f5) + ((f3 * (i2 + 0.5f)) / 5.0f), f2 - (f6 / 2.0f), f6 * 0.28f, Draw2D.argb(f4, Draw2D.lerpRgb(jewels[i2], 16777215, 0.4f)), Draw2D.argb(f4, jewels[i2]));
        }
    }
}
