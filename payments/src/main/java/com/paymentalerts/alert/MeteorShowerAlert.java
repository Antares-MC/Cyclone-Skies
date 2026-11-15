package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import com.paymentalerts.sound.SoundCues;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;

final class MeteorShowerAlert implements StageAlert {
    static final String[] VARIATIONS = {"Fire", "Ice comets", "Toxic", "Cosmic"};
    private static final Palette[] PALETTES = {new Palette(16773824, 16742943, 16730640, 16756800, 16734751, 16767050, 2756104), new Palette(15268863, 6280959, 3116031, 10479871, 5219327, 14219007, 398376), new Palette(15728592, 8191802, 3129120, 13172586, 5431418, 15138698, 793094), new Palette(16771327, 16736210, 10112511, 16754928, 13135103, 16751328, 1706024)};
    private static final float END = 10.2f;
    private static final float FADE = 1.2f;
    private static final float SHOWER_START = 0.8f;
    private static final float SHOWER_END = 4.6f;
    private static final float GIANT_SPAWN = 4.5f;
    private static final float GIANT_FALL = 0.95f;
    private static final float GIANT_HIT = 5.45f;
    private final Payment payment;
    private final long startNanos;
    private final Palette palette;
    private final SoundCues cues = new SoundCues();
    private final Random random = new Random();
    private final Particles2D fx = new Particles2D();
    private final List<Meteor> meteors = new ArrayList();
    private final List<Crater> craters = new ArrayList();
    private final float[][] stars = new float[110][3];

    private static final record Palette(int head, int trail, int glow, int ember, int nameA, int nameB, int sky) {


        

        

        







    }

    private static final class Meteor {
        final float spawn;
        final float fall;
        final float size;
        final float fromX;
        final float fromY;
        final float toX;
        final float toY;
        final boolean giant;
        boolean hit;

        Meteor(float f, float f2, float f3, float f4, float f5, float f6, float f7, boolean z) {
            this.spawn = f;
            this.fall = f2;
            this.size = f3;
            this.fromX = f4;
            this.fromY = f5;
            this.toX = f6;
            this.toY = f7;
            this.giant = z;
        }
    }

    private static final record Crater(float x, float y, float size, float born) {


        

        

        




    }

    MeteorShowerAlert(Payment payment, long j, int i) {
        this.payment = payment;
        this.startNanos = j;
        this.palette = PALETTES[Variations.pick(i, VARIATIONS.length)];
        for (float[] fArr : this.stars) {
            fArr[0] = this.random.nextFloat();
            fArr[1] = this.random.nextFloat() * 0.7f;
            fArr[2] = this.random.nextFloat() * 6.28f;
        }
        float f = SHOWER_START;
        while (true) {
            float f2 = f;
            if (f2 < SHOWER_END) {
                float nextFloat = 0.05f + (this.random.nextFloat() * 0.9f);
                float nextFloat2 = 0.72f + (this.random.nextFloat() * 0.16f);
                float nextFloat3 = 0.55f + (this.random.nextFloat() * 0.35f);
                this.meteors.add(new Meteor(f2, nextFloat3, 0.7f + (this.random.nextFloat() * 0.6f), nextFloat + ((nextFloat2 - (-0.08f)) * (0.45f + (this.random.nextFloat() * 0.25f))), -0.08f, nextFloat, nextFloat2, false));
                if (this.random.nextFloat() < 0.5f) {
                    this.cues.sound(f2, AlertSounds.METEOR_WHOOSH, 0.5f + (this.random.nextFloat() * 0.3f), 0.18f);
                }
                this.cues.sound(f2 + nextFloat3, AlertSounds.METEOR_IMPACT, 0.9f + (this.random.nextFloat() * 0.5f), 0.32f);
                ScreenShake.add(f2 + nextFloat3, 0.25f, 0.13f);
                f = f2 + (0.32f - (0.22f * ((f2 - SHOWER_START) / 3.8f))) + (this.random.nextFloat() * 0.08f);
            } else {
                this.meteors.add(new Meteor(GIANT_SPAWN, GIANT_FALL, 1.0f, 1.15f, -0.25f, 0.5f, SHOWER_START, true));
                this.cues.sound(0.0f, AlertSounds.STORM_RAIN, 0.6f, 0.15f).sound(4.4f, AlertSounds.METEOR_FIRE, 0.5f, SHOWER_START).sound(GIANT_SPAWN, AlertSounds.NUKE_SWOOP, 0.6f, 0.6f).sound(GIANT_HIT, AlertSounds.METEOR_BIG, 0.7f, 1.0f).sound(GIANT_HIT, AlertSounds.METEOR_IMPACT, 0.55f, 1.0f).sound(5.5f, AlertSounds.METEOR_RUMBLE, SHOWER_START, 0.9f).sound(5.5499997f, AlertSounds.NUKE_SHOCKWAVE, 1.3f, 0.45f);
                ScreenShake.addRising(GIANT_SPAWN, GIANT_FALL, 0.35f);
                ScreenShake.add(GIANT_HIT, 1.4f, 0.85f);
                return;
            }
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
        float max = elapsed > 9.0f ? Math.max(0.0f, (END - elapsed) / FADE) : 1.0f;
        float outCubic = Easing.outCubic(elapsed / 1.0f) * max;
        Draw2D.gradientV(guiGraphics, 0.0f, 0.0f, i, i2, Draw2D.argb(0.86f * outCubic, 197903), Draw2D.argb(SHOWER_START * outCubic, this.palette.sky()));
        for (float[] fArr : this.stars) {
            Draw2D.glow(guiGraphics, fArr[0] * i, fArr[1] * i2, 1.6f, (0.45f + (0.55f * ((float) Math.abs(Math.sin((elapsed * 2.2f) + fArr[2]))))) * outCubic, 16777215);
        }
        Draw2D.gradientV(guiGraphics, 0.0f, i2 * 0.6f, i, i2, Draw2D.argb(0.0f, this.palette.glow()), Draw2D.argb(((0.25f * Math.min(1.0f, Math.max(0.0f, (elapsed - SHOWER_START) / 3.0f))) + (0.4f * (elapsed >= GIANT_HIT ? Math.max(0.0f, 1.0f - ((elapsed - GIANT_HIT) / 3.0f)) : 0.0f))) * max, this.palette.glow()));
        for (Crater crater : this.craters) {
            float max2 = Math.max(0.0f, 1.0f - ((elapsed - crater.born()) / 3.5f));
            if (max2 > 0.0f) {
                float x = crater.x() * i;
                float y = crater.y() * i2;
                float size = crater.size() * i2;
                Draw2D.ellipse(guiGraphics, x, y, size * 1.6f, size * 0.45f, Draw2D.argb(0.75f * max2 * max, this.palette.ember()), Draw2D.argb(0.0f, this.palette.glow()));
                Draw2D.glow(guiGraphics, x, y - (size * 0.2f), size * FADE, 0.35f * max2 * max, this.palette.glow());
            }
        }
        for (Meteor meteor : this.meteors) {
            if (elapsed >= meteor.spawn) {
                float f2 = (elapsed - meteor.spawn) / meteor.fall;
                if (f2 >= 1.0f) {
                    if (!meteor.hit) {
                        meteor.hit = true;
                        impact(meteor, i, i2, elapsed);
                    }
                } else {
                    float f3 = (f2 * f2 * 0.35f) + (f2 * 0.65f);
                    float f4 = (meteor.fromX + ((meteor.toX - meteor.fromX) * f3)) * i;
                    float f5 = (meteor.fromY + ((meteor.toY - meteor.fromY) * f3)) * i2;
                    float f6 = (meteor.toX - meteor.fromX) * i;
                    float f7 = (meteor.toY - meteor.fromY) * i2;
                    float sqrt = (float) Math.sqrt((f6 * f6) + (f7 * f7));
                    float f8 = f6 / sqrt;
                    float f9 = f7 / sqrt;
                    float f10 = (meteor.giant ? i2 * 0.07f : i2 * 0.016f) * meteor.size;
                    float f11 = meteor.giant ? i2 * 0.65f : i2 * 0.26f * meteor.size;
                    Draw2D.streak(guiGraphics, f4 - (f8 * f11), f5 - (f9 * f11), f4, f5, f10 * 3.2f, 0.55f * max, this.palette.glow());
                    Draw2D.streak(guiGraphics, f4 - ((f8 * f11) * 0.7f), f5 - ((f9 * f11) * 0.7f), f4, f5, f10 * 1.6f, 0.9f * max, this.palette.trail());
                    Draw2D.glow(guiGraphics, f4, f5, f10 * 3.5f, 0.6f * max, this.palette.glow());
                    Draw2D.circle(guiGraphics, f4, f5, f10, Draw2D.argb(max, 16777215), Draw2D.argb(max, this.palette.head()));
                    int i3 = meteor.giant ? 6 : 1;
                    for (int i4 = 0; i4 < i3; i4++) {
                        this.fx.add(Particles2D.Kind.SPARK, f4 + this.fx.rand(-f10, f10), f5 + this.fx.rand(-f10, f10), ((-f8) * i2 * this.fx.rand(0.05f, 0.2f)) + this.fx.rand(-20.0f, 20.0f), ((-f9) * i2 * this.fx.rand(0.05f, 0.2f)) + this.fx.rand(-20.0f, 20.0f), this.fx.rand(0.3f, 0.7f), this.fx.rand(0.6f, FADE) * (meteor.giant ? 1.8f : 1.0f), this.palette.head(), this.palette.glow()).drag = 2.5f;
                    }
                    if (meteor.giant && this.fx.random.nextFloat() < 0.6f) {
                        Particles2D.P add = this.fx.add(Particles2D.Kind.SMOKE, f4, f5, 0.0f, 0.0f, this.fx.rand(SHOWER_START, 1.4f), f10 * SHOWER_START, 3811882, 1709078);
                        add.grow = f10 * 1.5f;
                        add.alpha = 0.35f;
                    }
                }
            }
        }
        this.fx.update(f);
        this.fx.render(guiGraphics, max);
        if (elapsed >= GIANT_HIT) {
            float f12 = elapsed - GIANT_HIT;
            float f13 = 0.5f * i;
            float f14 = SHOWER_START * i2;
            if (f12 < 0.6f) {
                Draw2D.rect(guiGraphics, 0.0f, 0.0f, i, i2, Draw2D.argb(0.9f * (1.0f - (f12 / 0.6f)), 16777215));
            }
            int i5 = 0;
            while (i5 < 3) {
                float f15 = f12 - (i5 * 0.18f);
                if (f15 > 0.0f && f15 < 1.4f) {
                    float f16 = f15 * i2 * (1.1f - (i5 * 0.2f));
                    Draw2D.ringEllipse(guiGraphics, f13, f14, f16, f16 * 0.35f, i2 * 0.035f, (1.0f - (f15 / 1.4f)) * SHOWER_START * max, i5 == 0 ? 16777215 : this.palette.trail());
                }
                i5++;
            }
            float max3 = Math.max(0.0f, 1.0f - (f12 / 2.5f));
            Draw2D.glow(guiGraphics, f13, f14, i2 * 0.35f * (0.6f + (0.4f * Math.min(1.0f, f12 * 3.0f))), SHOWER_START * max3 * max, this.palette.glow());
            Draw2D.glow(guiGraphics, f13, f14, i2 * 0.12f, 0.9f * max3 * max, this.palette.head());
        }
        Draw2D.flush(guiGraphics);
        if (elapsed >= 5.7999997f) {
            float f17 = elapsed - 5.7999997f;
            float min = Math.min(1.0f, f17 / 0.25f) * max;
            float outBack = 0.6f + (0.4f * Easing.outBack(f17 / 0.45f));
            MutableComponent colored = TextFx.colored(this.payment.name(), true, i6 -> {
                return Draw2D.lerpRgb(this.palette.nameA(), this.palette.nameB(), 0.5f + (0.5f * ((float) Math.sin((elapsed * 5.0f) - (i6 * 0.6f)))));
            });
            float fitScale = TextFx.fitScale(font, colored, i * SHOWER_START, 3.2f);
            float f18 = i2 * 0.18f;
            TextFx.centered(guiGraphics, font, colored, i / 2.0f, f18, fitScale * outBack, min);
            TextFx.centered(guiGraphics, font, TextFx.plain("rained " + this.payment.displayAmount() + " down on you", 16777215, true), i / 2.0f, f18 + (9.0f * fitScale) + 6.0f, 1.5f, min);
        }
    }

    private void impact(Meteor meteor, int i, int i2, float f) {
        float f2 = meteor.toX * i;
        float f3 = meteor.toY * i2;
        float f4 = meteor.giant ? 4.0f : meteor.size;
        int i3 = meteor.giant ? 160 : 18;
        for (int i4 = 0; i4 < i3; i4++) {
            float rand = (-1.5707964f) + this.fx.rand(-1.3f, 1.3f);
            float rand2 = i2 * this.fx.rand(0.15f, 0.55f) * (meteor.giant ? 2.2f : 1.0f);
            Particles2D.P add = this.fx.add(Particles2D.Kind.SPARK, f2, f3, ((float) Math.cos(rand)) * rand2, ((float) Math.sin(rand)) * rand2, this.fx.rand(0.5f, FADE) * (meteor.giant ? 1.6f : 1.0f), this.fx.rand(0.7f, 1.4f) * (meteor.giant ? 1.5f : 1.0f), this.palette.head(), this.palette.glow());
            add.drag = 1.6f;
            add.gravity = i2 * 0.9f;
        }
        int i5 = meteor.giant ? 26 : 3;
        for (int i6 = 0; i6 < i5; i6++) {
            Particles2D.P add2 = this.fx.add(Particles2D.Kind.SMOKE, f2 + (this.fx.rand(-8.0f, 8.0f) * f4), f3 - (this.fx.rand(0.0f, 6.0f) * f4), this.fx.rand(-40.0f, 40.0f) * f4, (-this.fx.rand(10.0f, 40.0f)) * f4, this.fx.rand(1.0f, 2.2f) * (meteor.giant ? 1.5f : 1.0f), i2 * 0.02f * f4, 4864570, 1972764);
            add2.grow = i2 * 0.05f * f4;
            add2.alpha = 0.4f;
            add2.drag = 1.8f;
        }
        Particles2D.P add3 = this.fx.add(Particles2D.Kind.SMOKE, f2, f3, 0.0f, 0.0f, meteor.giant ? 0.9f : 0.4f, i2 * 0.03f * f4, this.palette.head(), this.palette.glow());
        add3.grow = i2 * 0.12f * f4;
        add3.alpha = 0.9f;
        this.craters.add(new Crater(meteor.toX, meteor.toY, (meteor.giant ? 0.09f : 0.018f) * meteor.size, f));
    }
}
