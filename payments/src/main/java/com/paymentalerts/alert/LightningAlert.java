package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import com.paymentalerts.sound.SoundCues;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;

final class LightningAlert implements StageAlert {
    private static final float END = 8.5f;
    private static final float FADE = 1.2f;
    private final Payment payment;
    private final long startNanos;
    private int nextStrike;
    private float rainTimer;
    private boolean lastBig;
    private final Palette palette;
    static final String[] VARIATIONS = {"Blue", "Purple", "Red", "Gold"};
    private static final Palette[] PALETTES = {new Palette(7314431, 12571903, 12110064, 15266047, 5219327, 8379391, 3963903, 14214911, 11059455), new Palette(11563007, 14729215, 14203120, 15919359, 11554815, 14712831, 9059583, 15390975, 13152511), new Palette(16731983, 16760767, 15775928, 16771304, 16731983, 16752511, 16727100, 16767192, 16756912), new Palette(16758863, 16770728, 15786160, 16774880, 16761423, 16769151, 16754748, 16773328, 16769192)};
    private static final float[] STRIKES = {0.9f, 1.55f, 1.95f, 2.6f, 3.05f, 3.5f, 4.3f, 4.9f, 5.6f, 6.2f};
    private final SoundCues cues = new SoundCues();
    private final Random random = new Random();
    private final Particles2D fx = new Particles2D();
    private final List<Bolt> bolts = new ArrayList();
    private final float[][] clouds = new float[46][4];
    private float lastFlash = -10.0f;

    private static final record Palette(int glow, int mid, int lit, int flash, int nameA, int nameB, int haze, int sub, int rain) {


        

        

        









    }

    private static final class Bolt {
        final List<float[]> main;
        final List<List<float[]>> branches = new ArrayList();
        final float born;
        final boolean big;

        Bolt(List<float[]> list, float f, boolean z) {
            this.main = list;
            this.born = f;
            this.big = z;
        }
    }

    LightningAlert(Payment payment, long j, int i) {
        this.payment = payment;
        this.startNanos = j;
        this.palette = PALETTES[Variations.pick(i, VARIATIONS.length)];
        for (float[] fArr : this.clouds) {
            fArr[0] = (this.random.nextFloat() * 1.1f) - 0.05f;
            fArr[1] = this.random.nextFloat() * 0.16f;
            fArr[2] = 0.07f + (this.random.nextFloat() * 0.07f);
            fArr[3] = this.random.nextFloat();
        }
        this.cues.sound(0.0f, AlertSounds.STORM_RAIN, 1.0f, 0.6f).sound(2.6f, AlertSounds.STORM_RAIN, 0.95f, 0.6f).sound(5.2f, AlertSounds.STORM_RAIN, 1.0f, 0.5f);
        int i2 = 0;
        while (i2 < STRIKES.length) {
            boolean z = i2 == 0;
            float f = STRIKES[i2];
            this.cues.sound(f, AlertSounds.STORM_IMPACT, 0.9f + (this.random.nextFloat() * 0.4f), z ? 0.9f : 0.55f);
            this.cues.sound(f + 0.05f, AlertSounds.STORM_THUNDER, 0.8f + (this.random.nextFloat() * 0.3f), z ? 1.0f : 0.55f);
            if (z) {
                this.cues.sound(f, AlertSounds.STORM_BIG, 0.9f, 1.0f);
                ScreenShake.add(f, FADE, 0.45f);
            } else {
                ScreenShake.add(f, 0.5f, 0.15f);
            }
            i2++;
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

    @Override // com.paymentalerts.alert.StageAlert
    public void render(GuiGraphics guiGraphics, Font font, int i, int i2, float f, long j) {
        float elapsed = elapsed(j);
        float max = elapsed > 7.3f ? Math.max(0.0f, (END - elapsed) / FADE) : 1.0f;
        float outCubic = Easing.outCubic(elapsed / 0.8f);
        float f2 = i2 * 0.72f;
        while (this.nextStrike < STRIKES.length && elapsed >= STRIKES[this.nextStrike]) {
            strike(i, i2, f2, STRIKES[this.nextStrike], this.nextStrike == 0);
            this.nextStrike++;
        }
        float f3 = elapsed - this.lastFlash;
        float f4 = f3 < 0.2f ? (this.lastBig ? 0.6f : 0.3f) * (1.0f - (f3 / 0.2f)) : 0.0f;
        Draw2D.rect(guiGraphics, 0.0f, 0.0f, i, i2, Draw2D.argb(0.42f * outCubic * max, 659496));
        if (elapsed < 7.7f) {
            this.rainTimer += f;
            while (this.rainTimer > 0.007142857f) {
                this.rainTimer -= 0.007142857f;
                Particles2D.P add = this.fx.add(Particles2D.Kind.STREAK, this.fx.rand(0.0f, i * 1.15f), this.fx.rand(-20.0f, i2 * 0.2f), (-i2) * 0.18f, i2 * 1.25f, 0.7f, 0.7f, this.palette.rain(), this.palette.rain());
                add.drag = 0.0f;
                add.alpha = 0.35f * outCubic;
            }
        }
        this.fx.update(f);
        this.fx.render(guiGraphics, max);
        for (Bolt bolt : this.bolts) {
            float f5 = elapsed - bolt.born;
            float max2 = f5 < 0.06f ? 1.0f : f5 < 0.1f ? 0.25f : f5 < 0.16f ? 1.0f : Math.max(0.0f, 1.0f - ((f5 - 0.16f) / 0.3f));
            if (max2 > 0.0f) {
                float f6 = bolt.big ? 1.7f : 1.0f;
                drawPath(guiGraphics, bolt.main, f6, max2 * max, this.palette);
                Iterator<List<float[]>> it = bolt.branches.iterator();
                while (it.hasNext()) {
                    drawPath(guiGraphics, it.next(), f6 * 0.55f, max2 * 0.85f * max, this.palette);
                }
                float[] fArr = bolt.main.get(bolt.main.size() - 1);
                Draw2D.glow(guiGraphics, fArr[0], fArr[1], 22.0f * f6, 0.6f * max2 * max, this.palette.mid());
            }
        }
        this.bolts.removeIf(bolt2 -> {
            return elapsed - bolt2.born > 0.6f;
        });
        float f7 = (((-i2) * 0.28f) * (1.0f - outCubic)) - (elapsed > 7.3f ? (i2 * 0.3f) * (1.0f - max) : 0.0f);
        for (float[] fArr2 : this.clouds) {
            Draw2D.glow(guiGraphics, fArr2[0] * i, (fArr2[1] * i2) + f7, fArr2[2] * i2 * 1.4f, 0.95f, Draw2D.lerpRgb(Draw2D.lerpRgb(2501432, 3817810, fArr2[3]), this.palette.lit(), f4 * 1.4f));
        }
        if (f4 > 0.0f) {
            Draw2D.rect(guiGraphics, 0.0f, 0.0f, i, i2, Draw2D.argb(f4 * max, this.palette.flash()));
        }
        Draw2D.flush(guiGraphics);
        if (elapsed >= STRIKES[0] + 0.05f) {
            float f8 = elapsed - STRIKES[0];
            float min = Math.min(1.0f, f8 / 0.15f) * max;
            float outBack = 0.6f + (0.4f * Easing.outBack(f8 / 0.4f));
            MutableComponent colored = TextFx.colored(this.payment.name(), true, charIndex -> {
                int slot = (((int) (elapsed * 18.0f)) + (charIndex * 7)) % 5;
                if (slot == 0) {
                    return this.palette.nameA();
                }
                if (slot == 1) {
                    return this.palette.nameB();
                }
                return 16777215;
            });
            float fitScale = TextFx.fitScale(font, colored, i * 0.8f, 3.2f) * outBack;
            float f9 = i2 * 0.27f;
            float width = font.width(colored) * fitScale;
            Draw2D.ellipse(guiGraphics, i / 2.0f, f9 + (4.5f * fitScale), width * 0.65f, 12.0f * fitScale, Draw2D.argb(0.35f * min * (0.7f + (0.3f * ((float) Math.sin(elapsed * 20.0f)))), this.palette.haze()), Draw2D.argb(0.0f, this.palette.haze()));
            for (int i4 = 0; i4 < 3; i4++) {
                if (this.random.nextFloat() < 0.5f) {
                    float nextFloat = (i / 2.0f) + ((this.random.nextFloat() - 0.5f) * width * 1.1f);
                    float nextFloat2 = f9 + (this.random.nextFloat() * 9.0f * fitScale);
                    drawPath(guiGraphics, jagged(nextFloat, nextFloat2, nextFloat + ((this.random.nextFloat() - 0.5f) * 30.0f), nextFloat2 + ((this.random.nextFloat() - 0.5f) * 24.0f), 3, 0.35f), 0.45f, min, this.palette);
                }
            }
            Draw2D.flush(guiGraphics);
            TextFx.centered(guiGraphics, font, colored, i / 2.0f, f9, fitScale, min);
            TextFx.centered(guiGraphics, font, TextFx.plain("⚡ paid you " + this.payment.displayAmount() + " ⚡", this.palette.sub(), true), i / 2.0f, f9 + (9.0f * fitScale) + 6.0f, 1.5f, min);
        }
    }

    private void strike(int i, int i2, float f, float f2, boolean z) {
        float nextFloat = z ? i * (0.45f + (this.random.nextFloat() * 0.1f)) : i * (0.08f + (this.random.nextFloat() * 0.84f));
        Bolt bolt = new Bolt(jagged(nextFloat, i2 * 0.12f, nextFloat + ((this.random.nextFloat() - 0.5f) * i * 0.25f), f, 6, 0.22f), f2, z);
        int i3 = z ? 4 : 2;
        for (int i4 = 0; i4 < i3; i4++) {
            float[] fArr = bolt.main.get(4 + this.random.nextInt(Math.max(1, ((bolt.main.size() * 2) / 3) - 4)));
            double nextFloat2 = 1.5707963267948966d + ((this.random.nextBoolean() ? 1 : -1) * (0.45d + (this.random.nextFloat() * 0.5d)));
            float nextFloat3 = i2 * (0.12f + (this.random.nextFloat() * 0.2f));
            bolt.branches.add(jagged(fArr[0], fArr[1], fArr[0] + (((float) Math.cos(nextFloat2)) * nextFloat3), fArr[1] + (((float) Math.sin(nextFloat2)) * nextFloat3), 4, 0.3f));
        }
        this.bolts.add(bolt);
        this.lastFlash = f2;
        this.lastBig = z;
        float[] fArr2 = bolt.main.get(bolt.main.size() - 1);
        int i5 = 0;
        while (true) {
            if (i5 < (z ? 40 : 16)) {
                float rand = this.fx.rand(-3.1415927f, 0.0f);
                float rand2 = i2 * this.fx.rand(0.2f, 0.6f);
                Particles2D.P add = this.fx.add(Particles2D.Kind.SPARK, fArr2[0], fArr2[1], ((float) Math.cos(rand)) * rand2, ((float) Math.sin(rand)) * rand2, this.fx.rand(0.4f, 0.9f), this.fx.rand(0.7f, 1.3f), 16777215, this.palette.glow());
                add.gravity = i2 * FADE;
                add.drag = FADE;
                i5++;
            } else {
                return;
            }
        }
    }

    private List<float[]> jagged(float f, float f2, float f3, float f4, int i, float f5) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new float[]{f, f2});
        arrayList.add(new float[]{f3, f4});
        for (int i2 = 0; i2 < i; i2++) {
            ArrayList arrayList2 = new ArrayList();
            for (int i3 = 0; i3 < arrayList.size() - 1; i3++) {
                float[] fArr = (float[]) arrayList.get(i3);
                float[] fArr2 = (float[]) arrayList.get(i3 + 1);
                float f6 = fArr2[0] - fArr[0];
                float f7 = fArr2[1] - fArr[1];
                float sqrt = (float) Math.sqrt((f6 * f6) + (f7 * f7));
                float nextFloat = (this.random.nextFloat() - 0.5f) * 2.0f * sqrt * f5;
                float f8 = sqrt > 0.0f ? (-f7) / sqrt : 0.0f;
                float f9 = sqrt > 0.0f ? f6 / sqrt : 0.0f;
                arrayList2.add(fArr);
                arrayList2.add(new float[]{((fArr[0] + fArr2[0]) / 2.0f) + (f8 * nextFloat), ((fArr[1] + fArr2[1]) / 2.0f) + (f9 * nextFloat)});
            }
            arrayList2.add((float[]) arrayList.get(arrayList.size() - 1));
            arrayList = arrayList2;
        }
        return arrayList;
    }

    private static void drawPath(GuiGraphics guiGraphics, List<float[]> list, float f, float f2, Palette palette) {
        for (int i = 0; i < list.size() - 1; i++) {
            float[] fArr = list.get(i);
            float[] fArr2 = list.get(i + 1);
            Draw2D.softLine(guiGraphics, fArr[0], fArr[1], fArr2[0], fArr2[1], 11.0f * f, 0.22f * f2, palette.glow());
            Draw2D.softLine(guiGraphics, fArr[0], fArr[1], fArr2[0], fArr2[1], 4.5f * f, 0.6f * f2, palette.mid());
            Draw2D.softLine(guiGraphics, fArr[0], fArr[1], fArr2[0], fArr2[1], 2.0f * f, f2, 16777215);
        }
    }
}
