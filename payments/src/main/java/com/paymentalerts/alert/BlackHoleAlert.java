package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.HudFireworks;
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

final class BlackHoleAlert implements StageAlert {
    static final String[] VARIATIONS = {"Classic", "Blue giant", "Crimson", "Emerald"};
    private static final Palette[] PALETTES = {new Palette(8011007, 16727220, 3973375, 16757575, 16774358, 8011007, 16742972, 16769192, 16757852, 3803758, 0.7f), new Palette(2776319, 3987711, 6982399, 9099519, 15793151, 2772223, 3978495, 14218495, 10150143, 666222, 0.55f), new Palette(12589626, 16734746, 9050714, 16747050, 16773312, 10492458, 16730650, 16765088, 16747082, 5900810, 0.95f), new Palette(1744986, 1759412, 5955610, 13168730, 16056288, 1739338, 5955706, 14745544, 10547322, 674346, 0.35f)};
    private static final float FORM = 1.2f;
    private static final float COLLAPSE = 4.0f;
    private static final float BANG = 4.6f;
    private static final float END = 12.0f;
    private static final float TILT = 0.36f;
    private final Payment payment;
    private final long startNanos;
    private final SoundCues cues = new SoundCues();
    private final Random random = new Random();
    private final List<Orbiter> disk = new ArrayList();
    private final Particles2D stars = new Particles2D();
    private final float[][] nebula = new float[7][4];
    private float spawnTimer;
    private boolean banged;
    private final Palette palette;

    private static final record Palette(int a, int b, int c, int mid, int hot, int glowOuter, int glowMid, int glowInner, int ring, int vignette, float nebulaHue) {


        

        

        











    }

    private static final class Orbiter {
        float radius;
        float angle;
        float speed;
        int color;

        private Orbiter() {
        }
    }

    BlackHoleAlert(Payment payment, long j, int i) {
        this.payment = payment;
        this.startNanos = j;
        this.palette = PALETTES[Variations.pick(i, VARIATIONS.length)];
        for (float[] fArr : this.nebula) {
            fArr[0] = (this.random.nextFloat() - 0.5f) * 0.5f;
            fArr[1] = (this.random.nextFloat() - 0.5f) * 0.3f;
            fArr[2] = 0.18f + (this.random.nextFloat() * 0.22f);
            fArr[3] = this.random.nextFloat();
        }
        ScreenShake.addRising(0.5f, 4.1f, 0.35f);
        ScreenShake.add(BANG, 6.0f, 1.3f);
        ScreenShake.add(5.8f, 2.0f, 0.4f);
        this.cues.sound(0.0f, AlertSounds.BLACKHOLE_PORTAL, 0.5f, 0.9f).sound(1.6f, AlertSounds.BLACKHOLE_CHARGE, 0.5f, 0.9f).sound(3.9f, AlertSounds.BLACKHOLE_COLLAPSE, 0.5f, 1.0f).sound(BANG, AlertSounds.BLACKHOLE_BANG, 0.7f, 1.0f).sound(BANG, AlertSounds.NUKE_SHOCKWAVE, 0.5f, 1.0f).sound(BANG, AlertSounds.NUKE_BLAST, 0.5f, 1.0f).sound(4.65f, AlertSounds.BLACKHOLE_TOTEM, 0.8f, 0.7f).sound(5.0f, AlertSounds.NUKE_RUMBLE, 0.5f, 1.0f).sound(5.4f, AlertSounds.BLACKHOLE_SPARKLE, 0.8f, 0.8f).sound(6.0f, AlertSounds.BLACKHOLE_SPARKLE, 1.0f, 0.7f).sound(6.7f, AlertSounds.BLACKHOLE_SPARKLE, FORM, 0.6f);
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

    private static float horizon(float f, float f2) {
        if (f < FORM) {
            return f2 * Easing.outCubic(f / FORM);
        }
        if (f < COLLAPSE) {
            return f2 * (1.0f + (0.04f * ((float) Math.sin(f * 6.0f))));
        }
        if (f < BANG) {
            float f3 = (f - COLLAPSE) / 0.5999999f;
            return f2 * (1.0f - ((f3 * f3) * f3));
        }
        return 0.0f;
    }

    @Override // com.paymentalerts.alert.StageAlert
    public void render(GuiGraphics guiGraphics, Font font, int i, int i2, float f, long j) {
        float max;
        float elapsed = elapsed(j);
        float f2 = i / 2.0f;
        float f3 = i2 * 0.45f;
        float f4 = i2 * 0.09f;
        float horizon = horizon(elapsed, f4);
        if (elapsed < BANG) {
            max = (0.62f * Math.min(1.0f, elapsed / 1.0f)) + (elapsed > COLLAPSE ? (0.25f * (elapsed - COLLAPSE)) / 0.5999999f : 0.0f);
        } else {
            max = 0.87f * Math.max(0.0f, 1.0f - ((elapsed - BANG) / 1.9f));
        }
        Draw2D.rect(guiGraphics, 0.0f, 0.0f, i, i2, Draw2D.argb(max, 327951));
        Draw2D.vignette(guiGraphics, i, i2, 0.35f, max * 0.6f, this.palette.vignette());
        if (elapsed < BANG) {
            updateDisk(f, elapsed, f2, f3, f4, i2);
            float min = Math.min(1.0f, elapsed / FORM) * (elapsed > COLLAPSE ? 1.0f - ((elapsed - COLLAPSE) / 0.5999999f) : 1.0f);
            drawDiskGlow(guiGraphics, f2, f3, horizon, min, true);
            drawDisk(guiGraphics, f2, f3, horizon, true);
            Draw2D.glow(guiGraphics, f2, f3, Math.max(horizon, 1.0f) * 2.1f, 0.85f, 0);
            Draw2D.circle(guiGraphics, f2, f3, horizon, Draw2D.argb(1.0f, 0), Draw2D.argb(1.0f, 0));
            float sin = 0.75f + (0.25f * ((float) Math.sin(elapsed * 7.0f)));
            float f5 = elapsed > COLLAPSE ? (elapsed - COLLAPSE) / 0.5999999f : 0.0f;
            Draw2D.ring(guiGraphics, f2, f3, (horizon * 1.08f) + 0.5f, 2.2f + (horizon * 0.08f), ((0.8f * sin) + f5) * Math.min(1.0f, elapsed / FORM), Draw2D.lerpRgb(this.palette.ring(), 16777215, f5));
            drawDiskGlow(guiGraphics, f2, f3, horizon, min, false);
            drawDisk(guiGraphics, f2, f3, horizon, false);
            if (elapsed > COLLAPSE) {
                float f6 = (elapsed - COLLAPSE) / 0.5999999f;
                Draw2D.glow(guiGraphics, f2, f3, (i2 * 0.05f * (1.0f - f6)) + 2.0f, f6, 16777215);
            }
        } else {
            if (!this.banged) {
                this.banged = true;
                this.disk.clear();
                for (int i3 = 0; i3 < 280; i3++) {
                    double nextDouble = this.random.nextDouble() * 3.141592653589793d * 2.0d;
                    float rand = i2 * this.stars.rand(0.25f, 1.6f);
                    Particles2D.P add = this.stars.add(Particles2D.Kind.SPARK, f2, f3, ((float) Math.cos(nextDouble)) * rand, ((float) Math.sin(nextDouble)) * rand, this.stars.rand(1.6f, 3.4f), this.stars.rand(0.7f, 2.0f), Draw2D.hsv(this.random.nextFloat(), 0.55f, 1.0f), 16777215);
                    add.drag = 1.5f;
                    add.twinkle = this.random.nextFloat() < 0.4f;
                }
            }
            float f7 = elapsed - BANG;
            float max2 = elapsed > 10.8f ? Math.max(0.0f, (END - elapsed) / FORM) : 1.0f;
            for (float[] fArr : this.nebula) {
                float exp = 1.0f - ((float) Math.exp((-0.6f) * f7));
                Draw2D.glow(guiGraphics, f2 + (fArr[0] * i2 * (0.6f + exp)), f3 + (fArr[1] * i2 * (0.6f + exp)), fArr[2] * i2 * (0.4f + exp), 0.28f * Math.min(1.0f, f7 / 0.6f) * max2, Draw2D.hsv(this.palette.nebulaHue() + (fArr[3] * 0.3f), 0.7f, 1.0f));
            }
            float[] fArr2 = {0.0f, 0.1f, 0.22f};
            float[] fArr3 = {1.9f, 1.5f, 1.15f};
            for (int i4 = 0; i4 < 3; i4++) {
                float f8 = f7 - fArr2[i4];
                if (f8 > 0.0f) {
                    Draw2D.ring(guiGraphics, f2, f3, i2 * fArr3[i4] * ((float) Math.pow(f8, 0.8d)), 8.0f + (END * f8), 0.8f * Math.max(0.0f, 1.0f - (f8 / 1.6f)), Draw2D.hsv((i4 / 3.0f) + (f7 * 0.6f), 0.7f, 1.0f));
                }
            }
            this.stars.update(f);
            this.stars.render(guiGraphics, max2);
            float max3 = f7 < 0.06f ? 1.0f : Math.max(0.0f, 1.0f - ((f7 - 0.06f) / 0.8f));
            if (max3 > 0.0f) {
                Draw2D.rect(guiGraphics, 0.0f, 0.0f, i, i2, Draw2D.argb(max3, 16777215));
            }
            Draw2D.flush(guiGraphics);
            if (f7 > 0.4f) {
                float min2 = Math.min(1.0f, (f7 - 0.4f) / 0.3f) * max2;
                float outBack = 0.6f + (0.4f * Easing.outBack((f7 - 0.4f) / 0.5f));
                MutableComponent colored = TextFx.colored(this.payment.name(), true, i5 -> {
                    return Draw2D.hsv((elapsed * 0.35f) + (i5 * 0.07f), 0.6f, 1.0f);
                });
                float max4 = Math.max(6.0f, i2 * 0.08f);
                TextFx.centered(guiGraphics, font, colored, f2, max4, TextFx.fitScale(font, colored, i * 0.86f, 3.6f) * outBack, min2);
                TextFx.centered(guiGraphics, font, TextFx.plain("paid you " + this.payment.displayAmount(), 16777215, true), f2, max4 + 36.0f, 1.6f, min2);
                TextFx.centered(guiGraphics, font, TextFx.plain("and created a new universe", 13158655, false), f2, max4 + 56.0f, FORM, min2 * 0.9f);
            }
        }
        Draw2D.flush(guiGraphics);
    }

    private void updateDisk(float f, float f2, float f3, float f4, float f5, int i) {
        float f6;
        float f7;
        float nextFloat;
        float f8;
        if (f2 < 4.2f) {
            this.spawnTimer += f;
            while (this.spawnTimer > 0.004166667f) {
                this.spawnTimer -= 0.004166667f;
                Orbiter orbiter = new Orbiter();
                if (this.random.nextFloat() < 0.25f) {
                    f6 = i;
                    f7 = 0.55f;
                    nextFloat = this.random.nextFloat();
                    f8 = 0.35f;
                } else {
                    f6 = f5;
                    f7 = 2.0f;
                    nextFloat = this.random.nextFloat();
                    f8 = 4.5f;
                }
                orbiter.radius = f6 * (f7 + (nextFloat * f8));
                orbiter.angle = this.random.nextFloat() * 6.2832f;
                orbiter.speed = 0.8f + (this.random.nextFloat() * 0.6f);
                orbiter.color = this.random.nextInt(3);
                this.disk.add(orbiter);
            }
        }
        float f9 = f2 > COLLAPSE ? 1.0f + ((7.0f * (f2 - COLLAPSE)) / 0.5999999f) : 1.0f;
        float max = Math.max(1.0f, horizon(f2, f5));
        Iterator<Orbiter> it = this.disk.iterator();
        while (it.hasNext()) {
            Orbiter next = it.next();
            float max2 = Math.max(next.radius, 1.0f);
            next.angle += next.speed * 2.2f * ((float) Math.pow((f5 * 3.0f) / max2, 1.2d)) * f;
            next.radius -= (((i * 0.05f) * (1.0f + ((3.0f * f5) / max2))) * f9) * f;
            if (next.radius < max * 1.02f) {
                it.remove();
            }
        }
    }

    private void drawDiskGlow(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, boolean z) {
        if (f4 <= 0.0f || f3 < 1.0f) {
            return;
        }
        float f5 = z ? 3.1415927f : 0.0f;
        float f6 = f5 + 3.1415927f;
        Draw2D.ringArc(guiGraphics, f, f2, f3 * 3.4f, f3 * 3.4f * TILT, f3 * 3.6f, 0.35f * f4, this.palette.glowOuter(), f5, f6);
        Draw2D.ringArc(guiGraphics, f, f2, f3 * 2.2f, f3 * 2.2f * TILT, f3 * 1.8f, 0.55f * f4, this.palette.glowMid(), f5, f6);
        Draw2D.ringArc(guiGraphics, f, f2, f3 * 1.45f, f3 * 1.45f * TILT, f3 * 0.6f, 0.85f * f4, this.palette.glowInner(), f5, f6);
    }

    private void drawDisk(GuiGraphics guiGraphics, float f, float f2, float f3, boolean z) {
        int c;
        for (Orbiter orbiter : this.disk) {
            float sin = (float) Math.sin(orbiter.angle);
            if ((sin < 0.0f) == z) {
                float cos = (float) Math.cos(orbiter.angle);
                float f4 = f + (cos * orbiter.radius);
                float f5 = f2 + (sin * orbiter.radius * TILT);
                float max = Math.max(0.0f, Math.min(1.0f, 1.0f - ((orbiter.radius - f3) / (f3 * 5.0f))));
                switch (orbiter.color) {
                    case HudFireworks.CLASSIC /* 0 */:
                        c = this.palette.a();
                        break;
                    case HudFireworks.GOLD /* 1 */:
                        c = this.palette.b();
                        break;
                    default:
                        c = this.palette.c();
                        break;
                }
                int lerpRgb = max > 0.6f ? Draw2D.lerpRgb(this.palette.mid(), this.palette.hot(), (max - 0.6f) / 0.4f) : Draw2D.lerpRgb(c, this.palette.mid(), max / 0.6f);
                float f6 = 5.0f + (9.0f * max);
                Draw2D.streak(guiGraphics, f4 - ((-sin) * f6), f5 - ((cos * f6) * TILT), f4, f5, 2.4f + (max * 1.6f), z ? 0.8f : 1.0f, lerpRgb);
            }
        }
    }
}
