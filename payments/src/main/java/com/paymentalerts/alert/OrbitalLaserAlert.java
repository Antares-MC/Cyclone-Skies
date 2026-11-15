package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import com.paymentalerts.sound.SoundCues;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import org.joml.Matrix3x2fStack;

final class OrbitalLaserAlert implements StageAlert {
    static final String[] VARIATIONS = {"Cyan", "Red death ray", "Purple", "Gold", "Green"};
    private static final Palette[] PALETTES = {new Palette(4182271, 12580607, 15269375, 14679039, 10482687, 15137791, 8382719, 14219007), new Palette(16727871, 16760767, 16771304, 16768991, 16752543, 16770790, 16744319, 16767192), new Palette(11550719, 15122431, 16050431, 15654911, 13672447, 16049919, 13664255, 15784191), new Palette(16758847, 16770751, 16774888, 16773343, 16767135, 16774886, 16765055, 16773336), new Palette(4194154, 12582859, 15269870, 14680038, 10485683, 15138795, 8388506, 14221280)};
    private static final float LOCK = 1.4f;
    private static final float FIRE = 2.4f;
    private static final float CEASE = 4.4f;
    private static final float BOOM = 4.65f;
    private static final float END = 9.5f;
    private static final float FADE = 1.0f;
    private final Payment payment;
    private final long startNanos;
    private float chargeTimer;
    private float burnTimer;
    private float smokeTimer;
    private boolean boomed;
    private final Palette palette;
    private final SoundCues cues = new SoundCues();
    private final Particles2D fx = new Particles2D();
    private float beamX = Float.NaN;
    private final boolean sweep = Variations.chance(0.4f);

    private static final record Palette(int outer, int mid, int core, int rings, int charge, int flash, int name, int sub) {


        

        

        








    }

    OrbitalLaserAlert(Payment payment, long j, int i) {
        this.payment = payment;
        this.startNanos = j;
        this.palette = PALETTES[Variations.pick(i, VARIATIONS.length)];
        for (int i2 = 0; i2 < 6; i2++) {
            this.cues.sound(i2 * 0.24f, AlertSounds.LASER_ALARM, 2.0f, 0.55f);
        }
        this.cues.sound(LOCK, AlertSounds.LASER_LOCK, 1.2f, 0.9f).sound(1.3f, AlertSounds.LASER_CHARGE, FADE, FADE).sound(FIRE, AlertSounds.LASER_FIRE, 0.7f, FADE).sound(FIRE, AlertSounds.NUKE_THUNDER, 1.2f, 0.8f).sound(2.45f, AlertSounds.LASER_HUM, 0.5f, FADE).sound(3.4f, AlertSounds.LASER_HUM, 0.55f, FADE).sound(BOOM, AlertSounds.NUKE_SHOCKWAVE, 0.6f, FADE).sound(BOOM, AlertSounds.NUKE_BLAST, 0.5f, FADE).sound(4.9500003f, AlertSounds.NUKE_RUMBLE, 0.6f, 0.9f);
        float f = 2.5f;
        while (true) {
            float f2 = f;
            if (f2 < CEASE) {
                this.cues.sound(f2, AlertSounds.LASER_BURN, 0.6f + (((f2 * 37.0f) % FADE) * 0.3f), 0.5f);
                f = f2 + 0.28f;
            } else {
                ScreenShake.addRising(LOCK, 1.0000001f, 0.12f);
                ScreenShake.add(FIRE, 2.2f, 0.55f);
                ScreenShake.add(3.4f, 1.2f, 0.3f);
                ScreenShake.add(BOOM, 3.0f, 0.9f);
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
        float max = elapsed > 8.5f ? Math.max(0.0f, (END - elapsed) / FADE) : FADE;
        float f2 = i / 2.0f;
        float f3 = i2 * 0.74f;
        float max2 = Math.max(0.7f, Math.min(1.6f, i2 / 300.0f));
        float outCubic = ((i2 * 0.11f) - (((i2 * 0.2f) + 40.0f) * (FADE - Easing.outCubic(elapsed / 0.6f)))) - (elapsed > 6.0f ? ((elapsed - 6.0f) * i2) * 0.25f : 0.0f);
        float f4 = outCubic + (14.0f * max2);
        this.fx.update(f);
        if (elapsed < FIRE) {
            Draw2D.vignette(guiGraphics, i, i2, 0.22f, 0.3f * (0.5f + (0.5f * ((float) Math.sin(elapsed * 12.0f)))), 13635600);
        }
        Draw2D.rect(guiGraphics, 0.0f, 0.0f, i, i2, Draw2D.argb(0.3f * Math.min(FADE, elapsed / 0.6f) * max, 330264));
        if (elapsed > 0.5f && elapsed < FIRE) {
            float outCubic2 = f2 + ((FADE - Easing.outCubic(Math.min(FADE, (elapsed - 0.5f) / 0.9f))) * i * 0.3f * ((float) Math.sin(elapsed * 3.0f)));
            Draw2D.softLine(guiGraphics, f2, f4, outCubic2, f3, 3.0f, 0.8f * (elapsed >= LOCK ? FADE : 0.6f), 16724016);
            drawReticle(guiGraphics, outCubic2, f3, i2, elapsed, elapsed >= LOCK);
        }
        if (elapsed > 1.3f && elapsed < FIRE) {
            this.chargeTimer += f;
            while (this.chargeTimer > 0.011111111f) {
                this.chargeTimer -= 0.011111111f;
                double random = Math.random() * 3.141592653589793d * 2.0d;
                float f5 = 45.0f * max2;
                this.fx.add(Particles2D.Kind.STREAK, f2 + (((float) Math.cos(random)) * f5), f4 + (((float) Math.sin(random)) * f5), (-((float) Math.cos(random))) * f5 * 3.2f, (-((float) Math.sin(random))) * f5 * 3.2f, 0.3f, 0.9f, this.palette.charge(), 16777215).drag = 0.0f;
            }
        }
        float f6 = 0.0f;
        if (elapsed >= FIRE && elapsed < 4.7000003f) {
            f6 = i * 0.09f * Easing.outCubic((elapsed - FIRE) / 0.12f) * (elapsed > CEASE ? FADE - ((elapsed - CEASE) / 0.3f) : FADE) * (0.92f + (0.08f * ((float) Math.sin(elapsed * 60.0f))));
            this.beamX = f2 + (this.sweep ? ((float) Math.sin((elapsed - FIRE) * 1.7f)) * i * 0.22f * Math.min(FADE, (elapsed - FIRE) / 0.4f) : 0.0f);
            float f7 = this.beamX - f2;
            float f8 = f3 - f4;
            Matrix3x2fStack pose = guiGraphics.pose();
            pose.pushMatrix();
            pose.translate(f2, f4);
            pose.rotate((float) Math.toRadians((float) Math.toDegrees(-Math.atan2(f7, f8))));
            drawBeam(guiGraphics, 0.0f, 0.0f, (float) Math.sqrt((f7 * f7) + (f8 * f8)), f6, elapsed, this.palette);
            pose.popMatrix();
            Draw2D.glow(guiGraphics, this.beamX, f3, f6 * FIRE, 0.8f, this.palette.mid());
            Draw2D.ellipse(guiGraphics, this.beamX, f3 + 2.0f, f6 * 1.6f, f6 * 0.3f, Draw2D.argb(0.7f, 16777215), Draw2D.argb(0.0f, this.palette.outer()));
            this.burnTimer += f;
            while (this.burnTimer > 0.0125f) {
                this.burnTimer -= 0.0125f;
                float rand = this.fx.rand(-2.9915926f, -0.15f);
                float rand2 = i2 * this.fx.rand(0.3f, 0.8f);
                Particles2D.P add = this.fx.add(Particles2D.Kind.SPARK, this.beamX + this.fx.rand((-f6) / 2.0f, f6 / 2.0f), f3, ((float) Math.cos(rand)) * rand2, ((float) Math.sin(rand)) * rand2, this.fx.rand(0.4f, 0.9f), this.fx.rand(0.7f, 1.3f), 16777215, 16747050);
                add.gravity = i2 * 1.1f;
                add.drag = FADE;
            }
        }
        if (elapsed >= FIRE) {
            this.smokeTimer += f;
            while (this.smokeTimer > 0.055555556f && elapsed < 7.15f) {
                this.smokeTimer -= 0.055555556f;
                Particles2D.P add2 = this.fx.add(Particles2D.Kind.SMOKE, (Float.isNaN(this.beamX) ? f2 : this.beamX) + (this.fx.rand(-20.0f, 20.0f) * max2), f3 - 4.0f, this.fx.rand(-10.0f, 10.0f), -this.fx.rand(18.0f, 40.0f), this.fx.rand(2.0f, 3.2f), 4.0f * max2, 6974064, 3816000);
                add2.grow = 14.0f * max2;
                add2.alpha = 0.55f;
                add2.drag = 0.4f;
            }
        }
        if (elapsed >= BOOM) {
            float f9 = elapsed - BOOM;
            float f10 = Float.isNaN(this.beamX) ? f2 : this.beamX;
            if (!this.boomed) {
                this.boomed = true;
                for (int i3 = 0; i3 < 90; i3++) {
                    float rand3 = this.fx.rand(-3.4415927f, 0.3f);
                    float rand4 = i2 * this.fx.rand(0.3f, FADE);
                    Particles2D.P add3 = this.fx.add(Particles2D.Kind.SPARK, f10, f3 - 4.0f, ((float) Math.cos(rand3)) * rand4, ((float) Math.sin(rand3)) * rand4, this.fx.rand(0.8f, 1.7f), this.fx.rand(0.8f, 1.6f), 16773824, 16732176);
                    add3.gravity = i2 * 0.9f;
                    add3.drag = 1.3f;
                }
            }
            Draw2D.ring(guiGraphics, f10, f3 - (i2 * 0.04f), i2 * LOCK * ((float) Math.pow(f9, 0.8d)), 6.0f + (12.0f * f9), 0.7f * Math.max(0.0f, FADE - (f9 / 1.3f)), this.palette.rings());
            float max3 = Math.max(0.0f, FADE - (f9 / 1.6f));
            float exp = (i2 * 0.13f * (FADE - ((float) Math.exp((-7.0f) * f9)))) + 3.0f;
            Draw2D.glow(guiGraphics, f10, f3 - (exp * 0.5f), exp * 2.0f, 0.6f * max3, this.palette.outer());
            Draw2D.glow(guiGraphics, f10, f3 - (exp * 0.5f), exp, max3, 16777215);
        }
        this.fx.render(guiGraphics, max);
        drawSatellite(guiGraphics, f2, outCubic, max2, elapsed);
        if (elapsed < FIRE && elapsed > 1.3f) {
            float f11 = (elapsed - 1.3f) / 1.1000001f;
            Draw2D.glow(guiGraphics, f2, f4, (8.0f + (26.0f * f11)) * max2, 0.5f + (0.5f * f11), this.palette.charge());
            Draw2D.glow(guiGraphics, f2, f4, (3.0f + (8.0f * f11)) * max2, FADE, 16777215);
        } else if (f6 > 0.0f) {
            Draw2D.glow(guiGraphics, f2, f4, f6 * 1.2f, 0.9f, 16777215);
        }
        float f12 = elapsed - FIRE;
        float f13 = elapsed - BOOM;
        float f14 = 0.0f;
        if (f12 >= 0.0f && f12 < 0.5f) {
            f14 = 0.7f * (FADE - (f12 / 0.5f));
        }
        if (f13 >= 0.0f && f13 < FADE) {
            f14 = Math.max(f14, f13 < 0.06f ? FADE : FADE - ((f13 - 0.06f) / 0.94f));
        }
        if (f14 > 0.0f) {
            Draw2D.rect(guiGraphics, 0.0f, 0.0f, i, i2, Draw2D.argb(f14, this.palette.flash()));
        }
        Draw2D.flush(guiGraphics);
        float max4 = Math.max(6.0f, i2 * 0.2f);
        if (elapsed < FIRE) {
            float min = Math.min(FADE, elapsed / 0.3f);
            MutableComponent plain = TextFx.plain("⚠ ORBITAL STRIKE INBOUND ⚠", ((int) (elapsed * 4.0f)) % 2 == 0 ? 16726843 : 16777215, true);
            TextFx.centered(guiGraphics, font, plain, f2, max4, TextFx.fitScale(font, plain, i * 0.8f, 2.2f), min);
            TextFx.centered(guiGraphics, font, TextFx.plain(elapsed >= LOCK ? "TARGET LOCKED - " : "authorised by ", elapsed >= LOCK ? 16736352 : 16777215, elapsed >= LOCK).append(TextFx.plain(this.payment.name(), 16765773, true)), f2, max4 + 24.0f, 1.3f, min);
            return;
        }
        float f15 = elapsed - FIRE;
        float min2 = Math.min(FADE, f15 / 0.2f) * max;
        float outBack = 0.6f + (0.4f * Easing.outBack(f15 / 0.45f));
        MutableComponent colored = TextFx.colored(this.payment.name(), true, i4 -> {
            return Draw2D.lerpRgb(this.palette.name(), 16777215, 0.5f + (0.5f * ((float) Math.sin((elapsed * 10.0f) - (i4 * 0.6f)))));
        });
        float fitScale = TextFx.fitScale(font, colored, i * 0.86f, 3.4f) * outBack;
        MutableComponent plain2 = TextFx.plain("fired a " + this.payment.displayAmount() + " orbital laser at you", this.palette.sub(), false);
        float max5 = Math.max(font.width(colored) * fitScale, font.width(plain2) * LOCK) + 24.0f;
        float f16 = (9.0f * fitScale) + 6.0f + 12.599999f + 14.0f;
        Draw2D.rect(guiGraphics, f2 - (max5 / 2.0f), max4 - 7.0f, f2 + (max5 / 2.0f), (max4 - 7.0f) + f16, Draw2D.argb(0.6f * min2, 135194));
        Draw2D.rect(guiGraphics, f2 - (max5 / 2.0f), max4 - 7.0f, f2 + (max5 / 2.0f), max4 - 6.0f, Draw2D.argb(min2, this.palette.name()));
        Draw2D.rect(guiGraphics, f2 - (max5 / 2.0f), (max4 - 8.0f) + f16, f2 + (max5 / 2.0f), (max4 - 7.0f) + f16, Draw2D.argb(min2, this.palette.name()));
        Draw2D.flush(guiGraphics);
        TextFx.centered(guiGraphics, font, colored, f2, max4, fitScale, min2);
        TextFx.centered(guiGraphics, font, plain2, f2, max4 + (9.0f * fitScale) + 6.0f, LOCK, min2);
    }

    private static void drawBeam(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, float f5, Palette palette) {
        float f6 = f4 / 2.0f;
        int argb = Draw2D.argb(0.0f, palette.outer());
        int argb2 = Draw2D.argb(0.55f, palette.outer());
        Draw2D.quad(guiGraphics, f - (f6 * 2.2f), f2, argb, f - (f6 * 2.2f), f3, argb, f, f3, argb2, f, f2, argb2);
        Draw2D.quad(guiGraphics, f, f2, argb2, f, f3, argb2, f + (f6 * 2.2f), f3, argb, f + (f6 * 2.2f), f2, argb);
        Draw2D.gradientH(guiGraphics, f - f6, f2, f, f3, Draw2D.argb(0.2f, palette.mid()), Draw2D.argb(FADE, palette.core()));
        Draw2D.gradientH(guiGraphics, f, f2, f + f6, f3, Draw2D.argb(FADE, palette.core()), Draw2D.argb(0.2f, palette.mid()));
        Draw2D.rect(guiGraphics, f - (f6 * 0.3f), f2, f + (f6 * 0.3f), f3, Draw2D.argb(FADE, 16777215));
        float f7 = f3 - f2;
        for (int i = 0; i < 6; i++) {
            float f8 = ((f5 * 2.2f) + (i / 6.0f)) % FADE;
            Draw2D.ringEllipse(guiGraphics, f, f2 + (f8 * f7), f6 * 1.5f, f6 * 0.35f, 3.0f, 0.6f * (FADE - (f8 * 0.5f)), palette.rings());
        }
    }

    private static void drawReticle(GuiGraphics guiGraphics, float f, float f2, int i, float f3, boolean z) {
        float sin = (i * (z ? 0.05f : 0.07f)) + (2.0f * ((float) Math.sin(f3 * 9.0f)));
        int i2 = z ? 16719904 : 16740464;
        Draw2D.ringEllipse(guiGraphics, f, f2, sin, sin * 0.42f, 3.0f, 0.95f, i2);
        Draw2D.rect(guiGraphics, f - (sin * 1.6f), f2 - 0.8f, f - (sin * 0.5f), f2 + 0.8f, Draw2D.argb(0.9f, i2));
        Draw2D.rect(guiGraphics, f + (sin * 0.5f), f2 - 0.8f, f + (sin * 1.6f), f2 + 0.8f, Draw2D.argb(0.9f, i2));
        if (z) {
            Draw2D.ellipse(guiGraphics, f, f2, sin * 1.2f, sin * 0.5f, Draw2D.argb(0.35f, 16719904), Draw2D.argb(0.0f, 16719904));
        }
    }

    private static void drawSatellite(GuiGraphics guiGraphics, float f, float f2, float f3, float f4) {
        Matrix3x2fStack pose = guiGraphics.pose();
        pose.pushMatrix();
        pose.translate(f, f2);
        pose.scale(f3, f3);
        int i = -1;
        while (i <= 1) {
            float f5 = i < 0 ? -44.0f : 15.0f;
            float f6 = i < 0 ? -15.0f : 44.0f;
            Draw2D.rect(guiGraphics, Math.min(f5, f6) - FADE, -7.0f, Math.max(f5, f6) + FADE, 7.0f, Draw2D.argb(FADE, 10134445));
            Draw2D.gradientV(guiGraphics, f5, -6.0f, f6, 6.0f, Draw2D.argb(FADE, 3104184), Draw2D.argb(FADE, 1716070));
            for (int i2 = 1; i2 < 4; i2++) {
                float f7 = f5 + (((f6 - f5) * i2) / 4.0f);
                Draw2D.rect(guiGraphics, f7 - 0.4f, -6.0f, f7 + 0.4f, 6.0f, Draw2D.argb(0.7f, 9416959));
            }
            Draw2D.rect(guiGraphics, f5, -0.4f, f6, 0.4f, Draw2D.argb(0.7f, 9416959));
            Draw2D.rect(guiGraphics, i < 0 ? -15.0f : 11.0f, -1.2f, i < 0 ? -11.0f : 15.0f, 1.2f, Draw2D.argb(FADE, 6975866));
            i += 2;
        }
        Draw2D.gradientV(guiGraphics, -11.0f, -10.0f, 11.0f, 9.0f, Draw2D.argb(FADE, 15264751), Draw2D.argb(FADE, 9081500));
        Draw2D.rect(guiGraphics, -11.0f, -2.0f, 11.0f, FADE, Draw2D.argb(FADE, 13214247));
        Draw2D.ellipse(guiGraphics, 0.0f, 11.0f, 8.0f, 3.5f, Draw2D.argb(FADE, 4870490), Draw2D.argb(FADE, 2764597));
        Draw2D.rect(guiGraphics, -2.0f, 9.0f, 2.0f, 14.0f, Draw2D.argb(FADE, 3817544));
        Draw2D.rect(guiGraphics, -0.6f, -17.0f, 0.6f, -10.0f, Draw2D.argb(FADE, 10134445));
        if (((int) (f4 * 3.0f)) % 2 == 0) {
            Draw2D.glow(guiGraphics, 0.0f, -17.5f, 4.0f, FADE, 16724016);
        }
        Draw2D.circle(guiGraphics, 0.0f, -17.5f, 1.3f, Draw2D.argb(FADE, 16732240), Draw2D.argb(FADE, 11538448));
        pose.popMatrix();
    }
}
