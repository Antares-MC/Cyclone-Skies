package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import com.paymentalerts.sound.SoundCues;
import java.util.Random;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;

final class SupernovaAlert implements StageAlert {
    static final String[] VARIATIONS = {"Classic", "Crimson", "Emerald", "Violet"};
    private static final Palette[] PALETTES = {new Palette(16774872, 16738858, 16742972, 16732064, 5213183, 12575999, 16758863, 8374527), new Palette(16771296, 14163516, 16727130, 11542154, 16747068, 16765144, 16727130, 16752730), new Palette(15269872, 3983466, 3997578, 2011296, 13172570, 13697000, 5431418, 7008511), new Palette(15788287, 9059583, 11824383, 5913855, 16739032, 15259903, 13135103, 8366335)};
    private static final float END = 12.6f;
    private static final float FADE = 1.4f;
    private static final float GROW_START = 1.0f;
    private static final float COLLAPSE = 4.3f;
    private static final float BANG = 4.85f;
    private final Payment payment;
    private final long startNanos;
    private final Palette palette;
    private final SoundCues cues = new SoundCues();
    private final Random random = new Random();
    private final Particles2D fx = new Particles2D();
    private final float[][] stars = new float[140][3];
    private final float[][] clouds = new float[46][5];
    private boolean banged;

    private static final record Palette(int young, int giant, int nebA, int nebB, int nebC, int beam, int nameA, int nameB) {


        

        

        








    }

    SupernovaAlert(Payment payment, long j, int i) {
        this.payment = payment;
        this.startNanos = j;
        this.palette = PALETTES[Variations.pick(i, VARIATIONS.length)];
        for (float[] fArr : this.stars) {
            fArr[0] = this.random.nextFloat();
            fArr[1] = this.random.nextFloat();
            fArr[2] = this.random.nextFloat() * 6.28f;
        }
        for (float[] fArr2 : this.clouds) {
            fArr2[0] = this.random.nextFloat() * 6.28f;
            fArr2[1] = 0.35f + (this.random.nextFloat() * 0.65f);
            fArr2[2] = 0.08f + (this.random.nextFloat() * 0.12f);
            fArr2[3] = this.random.nextInt(3);
            fArr2[4] = this.random.nextFloat() * 6.28f;
        }
        this.cues.sound(0.2f, AlertSounds.NOVA_HUM, 0.6f, 0.8f).sound(GROW_START, AlertSounds.BLACKHOLE_PORTAL, 0.5f, 0.35f);
        for (float f : new float[]{FADE, 2.15f, 2.75f, 3.25f, 3.65f, 3.95f, 4.18f}) {
            this.cues.sound(f, AlertSounds.NOVA_HEARTBEAT, 0.8f + ((f - FADE) * 0.1f), GROW_START);
        }
        this.cues.sound(COLLAPSE, AlertSounds.NOVA_COLLAPSE, 0.6f, GROW_START).sound(4.3500004f, AlertSounds.NOVA_CHARGE, 0.7f, 0.9f).sound(BANG, AlertSounds.NOVA_BLAST, 0.5f, GROW_START).sound(BANG, AlertSounds.NUKE_SHOCKWAVE, 0.8f, 0.8f).sound(4.9f, AlertSounds.NOVA_ROAR, FADE, 0.45f).sound(4.95f, AlertSounds.STORM_BIG, 0.7f, 0.8f).sound(5.45f, AlertSounds.BLACKHOLE_TOTEM, 0.9f, 0.5f).sound(5.75f, AlertSounds.NOVA_SHIMMER, 0.8f, 0.8f).sound(7.35f, AlertSounds.NOVA_SHIMMER, 1.2f, 0.6f).sound(9.049999f, AlertSounds.NOVA_SHIMMER, GROW_START, 0.5f);
        ScreenShake.addRising(2.6f, 1.7000003f, 0.4f);
        ScreenShake.add(BANG, 1.8f, GROW_START);
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
        float max = elapsed > 11.200001f ? Math.max(0.0f, (END - elapsed) / FADE) : GROW_START;
        float outCubic = Easing.outCubic(elapsed / 1.2f) * max;
        float f2 = i / 2.0f;
        float f3 = i2 * 0.55f;
        float min = Math.min(i, i2);
        Draw2D.rect(guiGraphics, 0.0f, 0.0f, i, i2, Draw2D.argb(0.93f * outCubic, 131592));
        for (float[] fArr : this.stars) {
            Draw2D.glow(guiGraphics, fArr[0] * i, fArr[1] * i2, 1.5f, (0.35f + (0.65f * ((float) Math.abs(Math.sin((elapsed * 1.7f) + fArr[2]))))) * outCubic, 16777215);
        }
        if (elapsed < BANG) {
            drawStar(guiGraphics, f2, f3, min, elapsed, outCubic);
        } else {
            if (!this.banged) {
                this.banged = true;
                explode(f2, f3, min);
            }
            float f4 = elapsed - BANG;
            for (float[] fArr2 : this.clouds) {
                float exp = (min * fArr2[1] * (GROW_START - ((float) Math.exp((-f4) * FADE))) * 0.75f) + (min * fArr2[1] * f4 * 0.012f);
                float f5 = fArr2[0] + (f4 * 0.05f);
                Draw2D.glow(guiGraphics, f2 + (((float) Math.cos(f5)) * exp * 1.25f), f3 + (((float) Math.sin(f5)) * exp * 0.8f), min * fArr2[2] * (0.4f + Math.min(1.3f, f4 * 0.6f)), 0.28f * Math.min(GROW_START, f4 * 2.0f) * max, fArr2[3] == 0.0f ? this.palette.nebA() : fArr2[3] == GROW_START ? this.palette.nebB() : this.palette.nebC());
            }
            int i3 = 0;
            while (i3 < 4) {
                float f6 = f4 - (i3 * 0.15f);
                if (f6 > 0.0f && f6 < 2.2f) {
                    Draw2D.ring(guiGraphics, f2, f3, min * f6 * (0.9f - (i3 * 0.15f)), min * 0.04f, (GROW_START - (f6 / 2.2f)) * 0.85f * max, i3 == 0 ? 16777215 : i3 == 1 ? this.palette.nebA() : i3 == 2 ? this.palette.nebB() : this.palette.nebC());
                }
                i3++;
            }
            this.fx.update(f);
            this.fx.render(guiGraphics, max);
            drawPulsar(guiGraphics, f2, f3, min, f4, max);
            if (f4 < GROW_START) {
                Draw2D.rect(guiGraphics, 0.0f, 0.0f, i, i2, Draw2D.argb(Math.min(GROW_START, 1.2f * (GROW_START - f4)), 16777215));
            }
        }
        Draw2D.flush(guiGraphics);
        if (elapsed >= 5.75f) {
            float f7 = elapsed - 5.75f;
            float min2 = Math.min(GROW_START, f7 / 0.4f) * max;
            float outBack = 0.7f + (0.3f * Easing.outBack(f7 / 0.6f));
            TextFx.centered(guiGraphics, font, TextFx.plain("SUPERNOVA", 16777215, true), f2, i2 * 0.08f, 1.6f, min2 * 0.85f);
            MutableComponent colored = TextFx.colored(this.payment.name(), true, i4 -> {
                return Draw2D.lerpRgb(this.palette.nameA(), this.palette.nameB(), 0.5f + (0.5f * ((float) Math.sin((elapsed * 3.0f) - (i4 * 0.5f)))));
            });
            float fitScale = TextFx.fitScale(font, colored, i * 0.8f, 3.4f);
            float f8 = (i2 * 0.08f) + 16.0f;
            TextFx.centered(guiGraphics, font, colored, f2, f8, fitScale * outBack, min2);
            TextFx.centered(guiGraphics, font, TextFx.plain("paid you " + this.payment.displayAmount(), 16777215, true), f2, f8 + (9.0f * fitScale) + 6.0f, 1.5f, min2);
        }
    }

    private void drawStar(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, float f5) {
        float min = Math.min(GROW_START, Math.max(0.0f, (f4 - GROW_START) / 3.3000002f));
        float f6 = f3 * (0.05f + (0.15f * min * min));
        int lerpRgb = Draw2D.lerpRgb(this.palette.young(), this.palette.giant(), min);
        float f7 = 0.0f;
        for (float f8 : new float[]{FADE, 2.15f, 2.75f, 3.25f, 3.65f, 3.95f, 4.18f}) {
            float f9 = f4 - f8;
            if (f9 >= 0.0f && f9 < 0.25f) {
                f7 = Math.max(f7, GROW_START - (f9 / 0.25f));
            }
        }
        if (f4 >= COLLAPSE) {
            float f10 = (f4 - COLLAPSE) / 0.5499997f;
            f6 *= ((GROW_START - f10) * (GROW_START - f10) * 0.95f) + 0.05f;
            lerpRgb = Draw2D.lerpRgb(lerpRgb, 16777215, f10);
            f7 = 0.0f;
        }
        float f11 = f6 * (GROW_START + (f7 * 0.06f));
        Draw2D.glow(guiGraphics, f, f2, f11 * 3.2f, 0.35f * f5, lerpRgb);
        Draw2D.glow(guiGraphics, f, f2, f11 * 1.8f, 0.55f * f5, lerpRgb);
        for (int i = 0; i < 7; i++) {
            float f12 = (i * 0.9f) + (f4 * 0.7f);
            float sin = f11 * 0.25f * ((float) Math.sin((f4 * 3.1f) + (i * 2.3f)));
            Draw2D.glow(guiGraphics, f + (((float) Math.cos(f12)) * sin), f2 + (((float) Math.sin(f12)) * sin), f11 * 1.05f, 0.5f * f5, Draw2D.lerpRgb(lerpRgb, 16777215, 0.2f));
        }
        Draw2D.circle(guiGraphics, f, f2, f11, Draw2D.argb(f5, Draw2D.lerpRgb(lerpRgb, 16777215, 0.55f)), Draw2D.argb(f5, lerpRgb));
        if (f4 >= COLLAPSE) {
            float f13 = (f4 - COLLAPSE) / 0.5499997f;
            Draw2D.glow(guiGraphics, f, f2, f3 * 0.05f * (0.5f + f13), f5 * f13, 16777215);
        }
    }

    private void drawPulsar(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, float f5) {
        float min = Math.min(GROW_START, Math.max(0.0f, (f4 - 0.5f) / 0.8f)) * f5;
        if (min <= 0.0f) {
            return;
        }
        float f6 = f4 * 2.4f;
        float f7 = f3 * 1.2f;
        for (int i = 0; i < 2; i++) {
            float f8 = f6 + (i * 3.1415927f);
            float cos = f + (((float) Math.cos(f8)) * f7);
            float sin = f2 + (((float) Math.sin(f8)) * f7 * 0.75f);
            Draw2D.softLine(guiGraphics, f, f2, cos, sin, f3 * 0.05f, 0.35f * min, this.palette.beam());
            Draw2D.softLine(guiGraphics, f, f2, cos, sin, f3 * 0.015f, 0.8f * min, 16777215);
        }
        Draw2D.glow(guiGraphics, f, f2, f3 * 0.09f, 0.7f * min, this.palette.beam());
        Draw2D.circle(guiGraphics, f, f2, f3 * 0.012f, Draw2D.argb(min, 16777215), Draw2D.argb(min, this.palette.beam()));
    }

    private void explode(float f, float f2, float f3) {
        int[] iArr = {this.palette.nebA(), this.palette.nebB(), this.palette.nebC(), 16777215};
        for (int i = 0; i < 420; i++) {
            float rand = this.fx.rand(0.0f, 6.283f);
            float rand2 = f3 * this.fx.rand(0.2f, 1.6f);
            Particles2D.P add = this.fx.add(Particles2D.Kind.SPARK, f, f2, ((float) Math.cos(rand)) * rand2, ((float) Math.sin(rand)) * rand2 * 0.8f, this.fx.rand(1.2f, 3.2f), this.fx.rand(0.6f, 1.6f), 16777215, iArr[this.fx.random.nextInt(iArr.length)]);
            add.drag = this.fx.rand(1.2f, 2.4f);
            add.twinkle = this.fx.random.nextFloat() < 0.3f;
        }
        for (int i2 = 0; i2 < 60; i2++) {
            float rand3 = this.fx.rand(0.0f, 6.283f);
            float rand4 = f3 * this.fx.rand(GROW_START, 2.2f);
            this.fx.add(Particles2D.Kind.STREAK, f, f2, ((float) Math.cos(rand3)) * rand4, ((float) Math.sin(rand3)) * rand4, this.fx.rand(0.5f, GROW_START), this.fx.rand(0.8f, FADE), 16777215, iArr[this.fx.random.nextInt(3)]).drag = 1.5f;
        }
    }
}
