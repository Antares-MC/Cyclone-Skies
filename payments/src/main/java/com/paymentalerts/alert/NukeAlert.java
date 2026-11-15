package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import com.paymentalerts.sound.SoundCues;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import org.joml.Matrix3x2fStack;

final class NukeAlert implements StageAlert {
    static final String[] VARIATIONS = {"Classic", "Radioactive", "Plasma", "Hellfire"};
    private static final Palette[] PALETTES = {new Palette(16773296, 16742938, 16742954, 16738842, 16751162, 16765562, 16742938, 16773040, 16726538, 6181452, 16756832, 14192730, 16757575, 16726544, 16770140, 16730642, 15909424, 16774364), new Palette(15794096, 8191802, 5963562, 5955626, 10157914, 14745504, 4906778, 15728560, 3866378, 5134920, 10543216, 9486432, 11796295, 3866384, 14221148, 4914962, 8191802, 15269852), new Palette(14741759, 8019199, 5929727, 6970111, 9083647, 14213375, 6966015, 14741759, 5913343, 5001318, 8425696, 8028352, 10531071, 6961919, 10543359, 9067775, 5949695, 15001855), new Palette(16765088, 16722448, 16722448, 16718346, 16730666, 16756864, 13638154, 16765088, 12585482, 5913142, 13656122, 11554874, 16738874, 12587024, 16756828, 13636114, 13639712, 16769232)};
    private static final float FALL_START = 0.35f;
    private static final float IMPACT = 2.6f;
    private static final float END = 11.200001f;
    private final Payment payment;
    private final long startNanos;
    private final SoundCues cues = new SoundCues();
    private final Particles2D fx = new Particles2D();
    private final Puff[] cap;
    private final Puff[] dome;
    private final Puff[] stem;
    private final Puff[] skirt;
    private final float[] capDepth;
    private final Integer[] capOrder;
    private boolean exploded;
    private float trailTimer;
    private float emberTimer;
    private final Palette palette;

    private static final record Palette(int hotStart, int hot, int under, int tint, int fireGlow, int fireCore, int fireEdge, int sparkA, int sparkB, int smoke, int surge, int skirt, int emberA, int emberB, int nameA, int nameB, int band, int ring) {


        

        

        


















    }

    private static final record Puff(float a, float b, float size, float seed) {


        

        

        




    }

    NukeAlert(Payment payment, long j, int i) {
        this.payment = payment;
        this.startNanos = j;
        this.palette = PALETTES[Variations.pick(i, VARIATIONS.length)];
        Random random = new Random();
        this.cap = new Puff[70];
        for (int i2 = 0; i2 < this.cap.length; i2++) {
            this.cap[i2] = new Puff(random.nextFloat() * 6.2832f, random.nextFloat() * 6.2832f, 0.26f + (random.nextFloat() * 0.16f), random.nextFloat());
        }
        this.dome = new Puff[30];
        for (int i3 = 0; i3 < this.dome.length; i3++) {
            this.dome[i3] = new Puff((random.nextFloat() * 2.0f) - 1.0f, (random.nextFloat() * 2.0f) - 1.0f, 0.3f + (random.nextFloat() * 0.15f), random.nextFloat());
        }
        this.stem = new Puff[26];
        for (int i4 = 0; i4 < this.stem.length; i4++) {
            this.stem[i4] = new Puff(i4 / this.stem.length, (random.nextFloat() * 2.0f) - 1.0f, random.nextFloat(), random.nextFloat());
        }
        this.skirt = new Puff[26];
        for (int i5 = 0; i5 < this.skirt.length; i5++) {
            this.skirt[i5] = new Puff((random.nextFloat() * 2.0f) - 1.0f, random.nextFloat(), random.nextFloat(), random.nextFloat());
        }
        this.capDepth = new float[this.cap.length];
        this.capOrder = new Integer[this.cap.length];
        ScreenShake.addRising(FALL_START, 2.25f, 0.14f);
        ScreenShake.add(IMPACT, 6.0f, 1.1f);
        ScreenShake.add(3.8f, 1.6f, 0.4f);
        ScreenShake.add(4.8f, 2.2f, 0.3f);
        this.cues.sound(0.0f, AlertSounds.NUKE_SIREN, 0.9f, 1.0f).action(FALL_START, () -> {
            AlertSounds.playSliding(AlertSounds.NUKE_WHISTLE, 2.25f, 2.0f, 0.6f, 0.25f, 1.0f);
        }).sound(1.6999999f, AlertSounds.NUKE_SWOOP, 0.6f, 0.9f).sound(IMPACT, AlertSounds.NUKE_SHOCKWAVE, 0.5f, 1.0f).sound(IMPACT, AlertSounds.NUKE_BLAST, 0.5f, 1.0f).sound(2.6799998f, AlertSounds.NUKE_THUNDER, 0.6f, 1.0f).sound(2.8999999f, AlertSounds.NUKE_RUMBLE, 0.5f, 1.0f).sound(3.1999998f, AlertSounds.NUKE_FALLOUT, 0.5f, 0.55f).sound(3.8f, AlertSounds.NUKE_BLAST, 0.6f, 0.6f).sound(4.8f, AlertSounds.NUKE_RUMBLE, 0.55f, 0.6f);
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
        float f2 = i / 2.0f;
        float f3 = i2 * 0.72f;
        this.fx.update(f);
        if (elapsed < IMPACT) {
            renderIncoming(guiGraphics, font, i, i2, elapsed, f, f2, f3);
        } else {
            renderExplosion(guiGraphics, font, i, i2, elapsed, elapsed - IMPACT, f, f2, f3);
        }
        Draw2D.flush(guiGraphics);
    }

    private void renderIncoming(GuiGraphics guiGraphics, Font font, int i, int i2, float f, float f2, float f3, float f4) {
        float min = Math.min(1.0f, f / 0.4f);
        float sin = 0.5f + (0.5f * ((float) Math.sin(f * 9.0f)));
        Draw2D.vignette(guiGraphics, i, i2, 0.24f, (0.22f + (0.28f * sin)) * min, 13635600);
        Draw2D.rect(guiGraphics, 0.0f, 0.0f, i, i2, Draw2D.argb(0.05f * sin * min, 16711680));
        float sin2 = (i2 * 0.07f) + (3.0f * ((float) Math.sin(f * 8.0f)));
        Draw2D.ellipse(guiGraphics, f3, f4, sin2 * 1.3f, sin2 * 0.55f, Draw2D.argb(FALL_START * sin * min, 16719904), Draw2D.argb(0.0f, 16719904));
        Draw2D.ringEllipse(guiGraphics, f3, f4, sin2, sin2 * 0.42f, 4.0f, min, 16722474);
        Draw2D.ringEllipse(guiGraphics, f3, f4, sin2 * 0.45f, sin2 * 0.19f, 3.0f, min, 16722474);
        Draw2D.rect(guiGraphics, f3 - (sin2 * 1.7f), f4 - 1.0f, f3 - (sin2 * 0.6f), f4 + 1.0f, Draw2D.argb(min, 16722474));
        Draw2D.rect(guiGraphics, f3 + (sin2 * 0.6f), f4 - 1.0f, f3 + (sin2 * 1.7f), f4 + 1.0f, Draw2D.argb(min, 16722474));
        float max = Math.max(0.6f, Math.min(1.5f, i2 / 380.0f));
        if (f >= FALL_START) {
            float f5 = (f - FALL_START) / 2.25f;
            float f6 = max * (0.85f + (0.75f * f5));
            float f7 = (-60.0f) + (((f4 - (20.0f * f6)) + 60.0f) * f5 * f5);
            float sin3 = f3 + (((float) Math.sin(f5 * 5.0f)) * 8.0f * (1.0f - f5));
            float sin4 = ((float) Math.sin(f * 7.0f)) * 5.0f * (1.0f - f5);
            this.trailTimer += f2;
            while (this.trailTimer > 0.022f) {
                this.trailTimer -= 0.022f;
                Particles2D.P add = this.fx.add(Particles2D.Kind.SMOKE, sin3 + this.fx.rand(-2.0f, 2.0f), f7 - (28.0f * f6), this.fx.rand(-6.0f, 6.0f), this.fx.rand(-12.0f, -3.0f), 1.1f, 2.5f * f6, 15132390, 10132122);
                add.grow = 10.0f * f6;
                add.alpha = 0.5f;
                add.drag = 1.2f;
            }
            this.fx.render(guiGraphics, 1.0f);
            Matrix3x2fStack pose = guiGraphics.pose();
            pose.pushMatrix();
            pose.translate(sin3, f7);
            pose.rotate((float) Math.toRadians(sin4));
            pose.scale(f6, f6);
            drawBomb(guiGraphics, this.palette.band());
            pose.popMatrix();
        }
        Draw2D.flush(guiGraphics);
        MutableComponent plain = TextFx.plain("⚠ NUKE INCOMING ⚠", ((int) (f * 4.0f)) % 2 == 0 ? 16726843 : 16777215, true);
        float max2 = Math.max(6.0f, i2 * 0.06f);
        TextFx.centered(guiGraphics, font, plain, f3, max2, TextFx.fitScale(font, plain, i * 0.8f, 2.4f), min);
        TextFx.centered(guiGraphics, font, TextFx.plain("from ", 16777215, false).append(TextFx.plain(this.payment.name(), 16765773, true)), f3, max2 + 26.0f, 1.4f, min);
    }

    private static void drawBomb(GuiGraphics guiGraphics, int i) {
        int argb = Draw2D.argb(1.0f, 4674094);
        int argb2 = Draw2D.argb(1.0f, 3028256);
        Draw2D.triangle(guiGraphics, -5.0f, -12.0f, argb, -14.0f, -31.0f, argb, -5.0f, -26.0f, argb);
        Draw2D.triangle(guiGraphics, 5.0f, -12.0f, argb, 14.0f, -31.0f, argb, 5.0f, -26.0f, argb);
        Draw2D.rect(guiGraphics, -14.5f, -33.0f, 14.5f, -30.0f, argb2);
        Draw2D.quad(guiGraphics, -7.5f, -11.0f, Draw2D.argb(1.0f, 5595194), -3.6f, -28.0f, argb2, 3.6f, -28.0f, argb2, 7.5f, -11.0f, Draw2D.argb(1.0f, 5595194));
        Draw2D.rect(guiGraphics, -1.4f, -33.0f, 1.4f, -12.0f, Draw2D.argb(1.0f, 6187584));
        Draw2D.ellipse(guiGraphics, 0.0f, 3.0f, 9.5f, 18.0f, Draw2D.argb(1.0f, 9346147), Draw2D.argb(1.0f, 3818533));
        Draw2D.ellipse(guiGraphics, -3.6f, -1.0f, 2.3f, 10.0f, Draw2D.argb(FALL_START, 16777215), Draw2D.argb(0.0f, 16777215));
        int argb3 = Draw2D.argb(1.0f, i);
        Draw2D.quad(guiGraphics, -8.2f, -6.0f, argb3, -9.05f, -2.5f, argb3, 9.05f, -2.5f, argb3, 8.2f, -6.0f, argb3);
        Draw2D.circle(guiGraphics, 0.0f, 8.0f, 4.7f, argb3, argb3);
        int argb4 = Draw2D.argb(1.0f, 1118481);
        for (int i2 = 0; i2 < 3; i2++) {
            double radians = Math.toRadians(90 + (i2 * 120));
            for (int i3 = 0; i3 < 4; i3++) {
                double radians2 = (radians - Math.toRadians(30.0d)) + (Math.toRadians(15.0d) * i3);
                double radians3 = radians2 + Math.toRadians(15.0d);
                Draw2D.quad(guiGraphics, ((float) Math.cos(radians2)) * 1.5f, 8.0f + (((float) Math.sin(radians2)) * 1.5f), argb4, ((float) Math.cos(radians2)) * 4.1f, 8.0f + (((float) Math.sin(radians2)) * 4.1f), argb4, ((float) Math.cos(radians3)) * 4.1f, 8.0f + (((float) Math.sin(radians3)) * 4.1f), argb4, ((float) Math.cos(radians3)) * 1.5f, 8.0f + (((float) Math.sin(radians3)) * 1.5f), argb4);
            }
        }
        Draw2D.circle(guiGraphics, 0.0f, 8.0f, 0.95f, argb4, argb4);
        Draw2D.ellipse(guiGraphics, 0.0f, 17.5f, 5.5f, 3.8f, Draw2D.argb(0.55f, 1975314), Draw2D.argb(0.0f, 1975314));
    }

    private void drawCapCore(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, int i, int i2, float f5, float f6) {
        for (Puff puff : this.dome) {
            Draw2D.glow(guiGraphics, f + (f3 * 0.75f * puff.a), (f2 - (f4 * FALL_START)) - ((f4 * 0.5f) * puff.b), f3 * puff.size, 0.9f * f6, Draw2D.scaleRgb(Draw2D.lerpRgb(i, i2, f5), 0.95f + ((0.15f * (1.0f - puff.b)) / 2.0f)));
        }
    }

    private void renderExplosion(GuiGraphics guiGraphics, Font font, int i, int i2, float f, float f2, float f3, float f4, float f5) {
        if (!this.exploded) {
            this.exploded = true;
            for (int i3 = 0; i3 < 110; i3++) {
                float rand = this.fx.rand(-3.4915926f, FALL_START);
                float rand2 = i2 * this.fx.rand(FALL_START, 1.15f);
                Particles2D.P add = this.fx.add(Particles2D.Kind.SPARK, f4, f5 - 6.0f, ((float) Math.cos(rand)) * rand2, ((float) Math.sin(rand)) * rand2, this.fx.rand(0.8f, 1.9f), this.fx.rand(0.8f, 1.7f), this.palette.sparkA(), this.palette.sparkB());
                add.drag = 1.4f;
                add.gravity = i2 * 0.9f;
            }
        }
        float max = f2 < 6.0f ? 1.0f : Math.max(0.0f, 1.0f - ((f2 - 6.0f) / 2.4f));
        float max2 = Math.max(0.0f, Math.min(1.0f, (f2 - 0.5f) / 3.0f));
        Draw2D.rect(guiGraphics, 0.0f, 0.0f, i, i2, Draw2D.argb(0.38f * Math.max(0.0f, 1.0f - (f2 / 4.5f)), this.palette.tint()));
        float exp = f5 - ((i2 * 0.4f) * (1.0f - ((float) Math.exp((-0.85f) * f2))));
        float exp2 = (i2 * 0.19f * (1.0f - ((float) Math.exp((-1.0f) * f2)))) + 4.0f;
        float f6 = exp2 * 0.55f;
        float exp3 = (i2 * 0.032f * (1.0f - ((float) Math.exp((-1.4f) * f2)))) + 2.0f;
        int lerpRgb = Draw2D.lerpRgb(this.palette.hotStart(), this.palette.hot(), Math.min(1.0f, f2 / 0.8f));
        int smoke = this.palette.smoke();
        float exp4 = (i * 0.6f * (1.0f - ((float) Math.exp((-2.2f) * f2)))) + 10.0f;
        float exp5 = (i2 * 0.055f * (1.0f - ((float) Math.exp((-2.0f) * f2)))) + 4.0f;
        float max3 = 0.75f * max * (f2 < 4.0f ? 1.0f : Math.max(0.0f, 1.0f - ((f2 - 4.0f) / 2.0f)));
        int lerpRgb2 = Draw2D.lerpRgb(this.palette.surge(), 9072728, Math.min(1.0f, f2 / 2.5f));
        Draw2D.ellipse(guiGraphics, f4, f5 + 4.0f, exp4, exp5, Draw2D.argb(max3, lerpRgb2), Draw2D.argb(0.0f, lerpRgb2));
        Draw2D.ring(guiGraphics, f4, f5 - (i2 * 0.06f), i2 * 1.6f * ((float) Math.pow(f2, 0.8d)), 6.0f + (14.0f * f2), 0.75f * Math.max(0.0f, 1.0f - (f2 / 1.4f)), this.palette.ring());
        if (f2 > 0.1f) {
            float pow = i2 * 1.2f * ((float) Math.pow(f2 - 0.1f, 0.8d));
            Draw2D.ringEllipse(guiGraphics, f4, f5, pow, pow * 0.28f, 5.0f + (10.0f * f2), 0.6f * Math.max(0.0f, 1.0f - ((f2 - 0.1f) / 1.8f)), this.palette.ring());
        }
        for (Puff puff : this.stem) {
            float f7 = (puff.a + (f2 * 0.22f)) % 1.0f;
            Draw2D.glow(guiGraphics, f4 + (puff.b * exp3 * 0.7f) + (((float) Math.sin((f2 * 1.3f) + (puff.seed * 9.0f))) * exp3 * 0.15f), (f5 - 4.0f) + ((((exp + (f6 * 0.4f)) - f5) + 4.0f) * f7), exp3 * (0.9f + (0.5f * puff.size)) * (1.0f + (FALL_START * f7)), 0.92f * max, Draw2D.lerpRgb(lerpRgb, smoke, max2 * (0.6f + (0.4f * f7))));
        }
        for (Puff puff2 : this.skirt) {
            Draw2D.glow(guiGraphics, f4 + (puff2.a * exp4 * 0.7f), (f5 - (((puff2.b * i2) * 0.03f) * (1.0f - ((float) Math.exp((-2.0f) * f2))))) - 2.0f, i2 * (0.025f + (0.025f * puff2.size)) * (0.4f + (0.6f * (1.0f - ((float) Math.exp((-1.5f) * f2))))), 0.8f * max, Draw2D.lerpRgb(this.palette.skirt(), 8284762, max2));
        }
        float f8 = (-f2) * 0.8f;
        for (int i4 = 0; i4 < this.cap.length; i4++) {
            Puff puff3 = this.cap[i4];
            this.capDepth[i4] = (exp2 + (f6 * ((float) Math.cos(puff3.a + f8)))) * ((float) Math.sin(puff3.b));
            this.capOrder[i4] = Integer.valueOf(i4);
        }
        Arrays.sort(this.capOrder, (num, num2) -> {
            return Float.compare(this.capDepth[num.intValue()], this.capDepth[num2.intValue()]);
        });
        boolean z = false;
        for (Integer num3 : this.capOrder) {
            int intValue = num3.intValue();
            if (!z && this.capDepth[intValue] >= 0.0f) {
                drawCapCore(guiGraphics, f4, exp, exp2, f6, lerpRgb, smoke, max2, max);
                z = true;
            }
            Puff puff4 = this.cap[intValue];
            float f9 = puff4.a + f8;
            Draw2D.glow(guiGraphics, f4 + ((exp2 + (f6 * ((float) Math.cos(f9)))) * ((float) Math.cos(puff4.b))), exp - ((f6 * 0.75f) * ((float) Math.sin(f9))), exp2 * puff4.size * (0.85f + (0.15f * ((float) Math.cos(f9)))), 0.92f * max, Draw2D.lerpRgb(Draw2D.scaleRgb(Draw2D.lerpRgb(lerpRgb, smoke, max2), 0.72f + (0.38f * (((this.capDepth[intValue] / (exp2 + f6)) + 1.0f) / 2.0f))), this.palette.under(), (1.0f - max2) * 0.55f * Math.max(0.0f, -((float) Math.sin(f9)))));
        }
        if (!z) {
            drawCapCore(guiGraphics, f4, exp, exp2, f6, lerpRgb, smoke, max2, max);
        }
        Draw2D.glow(guiGraphics, f4, exp + (f6 * 0.6f), exp2 * 1.4f, 0.45f * (1.0f - max2) * max, this.palette.tint());
        float max4 = f2 < 0.5f ? 1.0f : Math.max(0.0f, 1.0f - ((f2 - 0.5f) / 1.6f));
        if (max4 > 0.0f) {
            float exp6 = f5 - ((i2 * 0.1f) * (1.0f - ((float) Math.exp((-1.2f) * f2))));
            float exp7 = (i2 * 0.14f * (1.0f - ((float) Math.exp((-8.0f) * f2)))) + 3.0f;
            Draw2D.glow(guiGraphics, f4, exp6, exp7 * 2.0f, 0.6f * max4, this.palette.fireGlow());
            Draw2D.circle(guiGraphics, f4, exp6, exp7 * 1.15f, Draw2D.argb(max4, this.palette.fireCore()), Draw2D.argb(0.0f, this.palette.fireEdge()));
            Draw2D.glow(guiGraphics, f4, exp6, exp7 * 0.7f, max4, 16777204);
        }
        if (f2 > 0.8f && f2 < 6.0f) {
            this.emberTimer += f3;
            while (this.emberTimer > 0.071428575f) {
                this.emberTimer -= 0.071428575f;
                Particles2D.P add2 = this.fx.add(Particles2D.Kind.SPARK, f4 + this.fx.rand(-exp2, exp2), exp + this.fx.rand(-f6, f6), this.fx.rand(-6.0f, 6.0f), -this.fx.rand(8.0f, 22.0f), this.fx.rand(1.5f, IMPACT), this.fx.rand(0.6f, 1.1f), this.palette.emberA(), this.palette.emberB());
                add2.drag = 0.3f;
                add2.gravity = -4.0f;
            }
        }
        this.fx.render(guiGraphics, max);
        float max5 = f2 < 0.07f ? 1.0f : Math.max(0.0f, 1.0f - ((f2 - 0.07f) / 0.95f));
        if (max5 > 0.0f) {
            Draw2D.rect(guiGraphics, 0.0f, 0.0f, i, i2, Draw2D.argb(max5, 16777215));
        }
        Draw2D.flush(guiGraphics);
        if (f2 >= 0.25f) {
            float min = Math.min(1.0f, (f2 - 0.25f) / 0.2f) * max;
            float outBack = 0.6f + (0.4f * Easing.outBack((f2 - 0.25f) / 0.45f));
            MutableComponent colored = TextFx.colored(this.payment.name(), true, i5 -> {
                return Draw2D.lerpRgb(this.palette.nameA(), this.palette.nameB(), 0.5f + (0.5f * ((float) Math.sin((f * 9.0f) + (i5 * 0.8f)))));
            });
            float max6 = Math.max(6.0f, i2 * 0.06f);
            TextFx.centered(guiGraphics, font, colored, f4, max6, TextFx.fitScale(font, colored, i * 0.86f, 3.4f) * outBack, min);
            TextFx.centered(guiGraphics, font, TextFx.plain("dropped a " + this.payment.displayAmount() + " nuke on you", 16770764, false), f4, max6 + 34.0f, 1.4f, min);
        }
    }
}
