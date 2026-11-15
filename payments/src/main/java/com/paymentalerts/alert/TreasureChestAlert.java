package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.MoneyParticles;
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
import org.joml.Matrix3x2fStack;

final class TreasureChestAlert implements StageAlert {
    static final String[] VARIATIONS = {"Wooden chest", "Ender chest", "Golden chest"};
    private static final Look[] LOOKS = {new Look(10512174, 7028761, 16765514, 11565586, 2758152, 16767082, 16757504, 16774328, new MoneyParticles.Kind[]{MoneyParticles.Kind.COIN, MoneyParticles.Kind.COIN, MoneyParticles.Kind.BAR, MoneyParticles.Kind.DIAMOND, MoneyParticles.Kind.EMERALD}), new Look(2046515, 924697, 4054170, 1474650, 462862, 11562239, 6094792, 14203135, new MoneyParticles.Kind[]{MoneyParticles.Kind.EMERALD, MoneyParticles.Kind.EMERALD, MoneyParticles.Kind.DIAMOND}), new Look(16765514, 12618258, 16777215, 13161696, 5913094, 9434610, 16770442, 9434610, new MoneyParticles.Kind[]{MoneyParticles.Kind.DIAMOND, MoneyParticles.Kind.DIAMOND, MoneyParticles.Kind.BAR, MoneyParticles.Kind.COIN})};
    private static final float END = 9.6f;
    private static final float FADE = 1.0f;
    private static final float DROP_START = 0.2f;
    private static final float LAND = 0.8f;
    private static final float RATTLE_START = 1.25f;
    private static final float OPEN = 2.5f;
    private static final float OPEN_TIME = 0.28f;
    private static final float SPRAY_END = 5.0f;
    private final Payment payment;
    private final long startNanos;
    private final Look look;
    private final boolean ender;
    private final SoundCues cues = new SoundCues();
    private final Particles2D fx = new Particles2D();
    private final MoneyParticles loot = new MoneyParticles();
    private boolean landed;
    private boolean opened;
    private float lootAccumulator;

    private static final record Look(int woodLight, int woodDark, int trimLight, int trimDark, int inside, int glow, int nameA, int nameB, MoneyParticles.Kind[] loot) {


        

        

        









    }

    TreasureChestAlert(Payment payment, long j, int i) {
        this.payment = payment;
        this.startNanos = j;
        int pick = Variations.pick(i, VARIATIONS.length);
        this.look = LOOKS[pick];
        this.ender = pick == 1;
        Random random = new Random();
        this.cues.sound(LAND, AlertSounds.CHEST_LAND, 0.55f, 0.55f).sound(0.82f, AlertSounds.NUKE_BLAST, 1.6f, 0.25f).sound(1.35f, AlertSounds.CHEST_RATTLE, 0.85f, LAND).sound(1.75f, AlertSounds.CHEST_RATTLE, 0.95f, LAND).sound(2.1f, AlertSounds.CHEST_RATTLE, 1.05f, 0.85f).sound(OPEN, this.ender ? AlertSounds.CHEST_ENDER_OPEN : AlertSounds.CHEST_OPEN, 0.75f, FADE).sound(2.52f, AlertSounds.CHEST_SHIMMER, FADE, 0.9f).sound(2.55f, AlertSounds.MONEY_LEVELUP, FADE, 0.6f).sound(2.6f, AlertSounds.JACKPOT_BELL, 1.4f, 0.6f).sound(2.8f, AlertSounds.CONFETTI_TADA, 1.1f, 0.5f);
        for (int i2 = 0; i2 < 4; i2++) {
            this.cues.sound(2.65f + (i2 * 0.55f), AlertSounds.CHEST_EJECT, 0.9f + (random.nextFloat() * 0.4f), 0.6f);
        }
        for (int i3 = 0; i3 < 16; i3++) {
            this.cues.sound(2.7f + (random.nextFloat() * 3.4f), AlertSounds.MONEY_COIN, LAND + (random.nextFloat() * LAND), 0.3f);
        }
        ScreenShake.add(LAND, 0.35f, 0.4f);
        ScreenShake.add(OPEN, 0.4f, 0.35f);
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
        float f2;
        float elapsed = elapsed(j);
        float max = elapsed > 8.6f ? Math.max(0.0f, (END - elapsed) / FADE) : FADE;
        float outCubic = Easing.outCubic(elapsed / 0.4f);
        Draw2D.rect(guiGraphics, 0.0f, 0.0f, i, i2, Draw2D.argb(0.35f * outCubic * max, 525834));
        Draw2D.vignette(guiGraphics, i, i2, 0.3f, 0.5f * outCubic * max, 0);
        float min = Math.min(i * 0.3f, i2 * 0.5f);
        float f3 = min * 0.5f;
        float f4 = min * DROP_START;
        float f5 = i / 2.0f;
        float f6 = i2 * 0.84f;
        if (elapsed < DROP_START) {
            f2 = -i2;
        } else if (elapsed < LAND) {
            float f7 = (elapsed - DROP_START) / 0.6f;
            f2 = (-(FADE - (f7 * f7))) * (f6 + (f4 * 2.0f));
        } else {
            f2 = 0.0f;
        }
        float f8 = 1.0f;
        if (elapsed >= LAND && elapsed < 1.15f) {
            float f9 = (elapsed - LAND) / 0.35f;
            f8 = FADE - ((0.16f * ((float) Math.sin(f9 * 3.141592653589793d))) * (FADE - f9));
        }
        float f10 = 0.0f;
        if (elapsed >= RATTLE_START && elapsed < OPEN) {
            f10 = ((float) Math.sin(elapsed * 46.0f)) * 0.045f * (0.3f + ((elapsed - RATTLE_START) / RATTLE_START));
        }
        if (!this.landed && elapsed >= LAND) {
            this.landed = true;
            for (int i3 = 0; i3 < 22; i3++) {
                float f11 = i3 % 2 == 0 ? -1.0f : FADE;
                Particles2D.P add = this.fx.add(Particles2D.Kind.SMOKE, f5 + (f11 * min * this.fx.rand(DROP_START, 0.55f)), f6 - 2.0f, f11 * this.fx.rand(30.0f, 110.0f), -this.fx.rand(SPRAY_END, 25.0f), this.fx.rand(0.7f, 1.3f), min * 0.06f, 12101776, 9075304);
                add.grow = min * DROP_START;
                add.alpha = 0.4f;
                add.drag = OPEN;
            }
        }
        float min2 = elapsed < OPEN ? 0.0f : Math.min(FADE, (elapsed - OPEN) / OPEN_TIME) * 1.85f;
        if (!this.opened && elapsed >= OPEN) {
            this.opened = true;
            for (int i4 = 0; i4 < 70; i4++) {
                float rand = (-1.5707964f) + this.fx.rand(-0.9f, 0.9f);
                float rand2 = i2 * this.fx.rand(0.4f, 1.1f);
                Particles2D.P add2 = this.fx.add(Particles2D.Kind.SPARK, f5, f6 - f3, ((float) Math.cos(rand)) * rand2, ((float) Math.sin(rand)) * rand2, this.fx.rand(LAND, 1.6f), this.fx.rand(LAND, 1.6f), 16777215, this.look.glow());
                add2.drag = 2.2f;
                add2.gravity = i2 * 0.35f;
                add2.twinkle = true;
            }
        }
        float min3 = Math.min(FADE, Math.max(0.0f, (elapsed - OPEN) / 0.5f));
        float f12 = (f6 - (f3 * f8)) + f2;
        if (min3 > 0.0f) {
            drawRays(guiGraphics, f5, f12, i2, elapsed, min3 * max);
            Draw2D.glow(guiGraphics, f5, f12, min * (0.9f + (0.1f * ((float) Math.sin(elapsed * SPRAY_END)))), 0.55f * min3 * max, this.look.glow());
        }
        if (elapsed >= OPEN && elapsed < SPRAY_END) {
            this.lootAccumulator += f * 70.0f * (FADE - ((0.6f * (elapsed - OPEN)) / OPEN));
            while (this.lootAccumulator >= FADE) {
                this.lootAccumulator -= FADE;
                this.loot.burst(f5, f12 - 2.0f, 1, i2, this.look.loot(), 0.0f, 0.7f, 1.12f);
            }
        }
        this.fx.update(f);
        this.fx.render(guiGraphics, max);
        Matrix3x2fStack pose = guiGraphics.pose();
        pose.pushMatrix();
        pose.translate(f5, f6 + f2);
        pose.rotate(f10);
        pose.scale(FADE + ((FADE - f8) * 0.6f), f8);
        drawChest(guiGraphics, min, f3, f4, min2, elapsed, max);
        pose.popMatrix();
        this.loot.update(f, i, i2);
        this.loot.render(guiGraphics, max);
        Draw2D.flush(guiGraphics);
        if (elapsed >= 2.75f) {
            float f13 = elapsed - 2.75f;
            float min4 = Math.min(FADE, f13 / 0.25f) * max;
            float outBack = 0.6f + (0.4f * Easing.outBack(f13 / 0.45f));
            MutableComponent colored = TextFx.colored(this.payment.name(), true, i5 -> {
                return Draw2D.lerpRgb(this.look.nameA(), this.look.nameB(), 0.5f + (0.5f * ((float) Math.sin((elapsed * 4.0f) - (i5 * 0.5f)))));
            });
            float fitScale = TextFx.fitScale(font, colored, i * LAND, 3.0f);
            float f14 = i2 * 0.12f;
            TextFx.centered(guiGraphics, font, colored, i / 2.0f, f14, fitScale * outBack, min4);
            TextFx.centered(guiGraphics, font, TextFx.plain("paid you " + this.payment.displayAmount(), 16777215, true), i / 2.0f, f14 + (9.0f * fitScale) + 6.0f, 1.5f, min4);
        }
    }

    private void drawRays(GuiGraphics guiGraphics, float f, float f2, int i, float f3, float f4) {
        float f5 = i * 1.1f;
        for (int i2 = 0; i2 < 9; i2++) {
            float sin = (-1.5707964f) + ((i2 - ((9 - 1) / 2.0f)) * DROP_START) + (((float) Math.sin((f3 * LAND) + i2)) * 0.04f);
            float sin2 = 0.045f + (0.02f * ((float) Math.sin((f3 * 2.0f) + (i2 * 1.7f))));
            Draw2D.triangle(guiGraphics, f, f2, Draw2D.argb(0.45f * f4, this.look.glow()), f + (((float) Math.cos(sin - sin2)) * f5), f2 + (((float) Math.sin(sin - sin2)) * f5), Draw2D.argb(0.0f, this.look.glow()), f + (((float) Math.cos(sin + sin2)) * f5), f2 + (((float) Math.sin(sin + sin2)) * f5), Draw2D.argb(0.0f, this.look.glow()));
        }
    }

    private void drawChest(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = f / 2.0f;
        float f8 = -f2;
        Draw2D.ellipse(guiGraphics, 0.0f, 2.0f, f7 * 1.15f, f2 * 0.12f, Draw2D.argb(0.45f * f6, 0), Draw2D.argb(0.0f, 0));
        Draw2D.gradientV(guiGraphics, -f7, f8, f7, 0.0f, Draw2D.argb(f6, this.look.woodLight()), Draw2D.argb(f6, this.look.woodDark()));
        for (int i = 1; i < 4; i++) {
            float f9 = f8 + ((f2 * i) / 4.0f);
            Draw2D.rect(guiGraphics, -f7, f9 - 0.6f, f7, f9 + 0.6f, Draw2D.argb(0.5f * f6, Draw2D.scaleRgb(this.look.woodDark(), 0.6f)));
        }
        float f10 = f * 0.06f;
        int argb = Draw2D.argb(f6, this.look.trimLight());
        int argb2 = Draw2D.argb(f6, this.look.trimDark());
        Draw2D.gradientV(guiGraphics, -f7, f8, (-f7) + f10, 0.0f, argb, argb2);
        Draw2D.gradientV(guiGraphics, f7 - f10, f8, f7, 0.0f, argb, argb2);
        Draw2D.gradientV(guiGraphics, ((-f) * 0.22f) - (f10 / 2.0f), f8, ((-f) * 0.22f) + (f10 / 2.0f), 0.0f, argb, argb2);
        Draw2D.gradientV(guiGraphics, (f * 0.22f) - (f10 / 2.0f), f8, (f * 0.22f) + (f10 / 2.0f), 0.0f, argb, argb2);
        Draw2D.rect(guiGraphics, -f7, (-f10) * LAND, f7, 0.0f, argb2);
        if (f5 >= RATTLE_START && f4 <= 0.0f) {
            float f11 = (f5 - RATTLE_START) / RATTLE_START;
            Draw2D.softLine(guiGraphics, -f7, f8, f7, f8, 3.0f + (4.0f * f11), 0.9f * f11 * (0.6f + (0.4f * ((float) Math.sin(f5 * 31.0f)))) * f6, this.look.glow());
        }
        float sin = (float) Math.sin(f4);
        float cos = (float) Math.cos(f4);
        float max = f * 0.42f * Math.max(0.0f, sin);
        if (max > 0.3f) {
            Draw2D.gradientV(guiGraphics, -f7, f8 - max, f7, f8, Draw2D.argb(f6, this.look.inside()), Draw2D.argb(f6, Draw2D.scaleRgb(this.look.woodDark(), 0.7f)));
            Draw2D.rect(guiGraphics, -f7, f8 - max, (-f7) + f10, f8, argb2);
            Draw2D.rect(guiGraphics, f7 - f10, f8 - max, f7, f8, argb2);
        }
        if (sin > DROP_START) {
            float min = Math.min(FADE, (sin - DROP_START) / 0.6f);
            int i2 = this.look.loot()[0] == MoneyParticles.Kind.EMERALD ? 4054122 : this.look.loot()[0] == MoneyParticles.Kind.DIAMOND ? 9434610 : 16765514;
            dome(guiGraphics, 0.0f, f8 + FADE, f7 * 0.82f, f2 * 0.3f * min, Draw2D.argb(f6, Draw2D.lerpRgb(i2, 16777215, 0.45f)), Draw2D.argb(f6, Draw2D.scaleRgb(i2, 0.75f)));
            for (int i3 = 0; i3 < 9; i3++) {
                Draw2D.glow(guiGraphics, ((i3 - 4) * f7 * 0.17f) + (((float) Math.sin(i3 * 7.3f)) * f7 * 0.05f), (f8 - (((f2 * 0.18f) * min) * (FADE - (Math.abs(i3 - 4) / SPRAY_END)))) + (((float) Math.cos(i3 * 3.1f)) * 2.0f), (f * 0.03f) + ((0.5f + (0.5f * ((float) Math.sin((f5 * 6.0f) + (i3 * 1.9f))))) * f * 0.02f), LAND * f6 * min, 16777215);
            }
        }
        float f12 = f3 * cos;
        if (f12 > 0.3f) {
            float f13 = f8 - max;
            float f14 = f13 - f12;
            Draw2D.gradientV(guiGraphics, -f7, f14, f7, f13, Draw2D.argb(f6, Draw2D.scaleRgb(this.look.woodLight(), 1.15f)), Draw2D.argb(f6, this.look.woodLight()));
            dome(guiGraphics, 0.0f, f14, f7, f12 * 0.9f, Draw2D.argb(f6, Draw2D.scaleRgb(this.look.woodLight(), RATTLE_START)), Draw2D.argb(f6, this.look.woodLight()));
            Draw2D.rect(guiGraphics, -f7, f14, (-f7) + f10, f13, argb);
            Draw2D.rect(guiGraphics, f7 - f10, f14, f7, f13, argb);
            Draw2D.rect(guiGraphics, ((-f) * 0.22f) - (f10 / 2.0f), f14, ((-f) * 0.22f) + (f10 / 2.0f), f13, argb);
            Draw2D.rect(guiGraphics, (f * 0.22f) - (f10 / 2.0f), f14, (f * 0.22f) + (f10 / 2.0f), f13, argb);
            Draw2D.rect(guiGraphics, -f7, f13 - (f10 * 0.6f), f7, f13, argb2);
        }
        float f15 = f4 > 0.0f ? (f5 - OPEN) * (f5 - OPEN) * 900.0f : 0.0f;
        if (f15 < f2 * 2.0f) {
            float f16 = f * 0.11f;
            float f17 = f * 0.13f;
            float f18 = (f8 - (f17 * 0.15f)) + f15;
            if (this.ender) {
                Draw2D.ellipse(guiGraphics, 0.0f, f18 + (f17 * 0.5f), f16 * 0.55f, f17 * 0.5f, Draw2D.argb(f6, 10289112), Draw2D.argb(f6, 1474650));
                Draw2D.ellipse(guiGraphics, 0.0f, f18 + (f17 * 0.5f), f16 * DROP_START, f17 * 0.22f, Draw2D.argb(f6, 662036), Draw2D.argb(f6, 662036));
            } else {
                Draw2D.gradientV(guiGraphics, (-f16) / 2.0f, f18, f16 / 2.0f, f18 + f17, argb, argb2);
                Draw2D.ellipse(guiGraphics, 0.0f, f18 + (f17 * 0.42f), f16 * 0.14f, f16 * 0.14f, Draw2D.argb(f6, 1708040), Draw2D.argb(f6, 1708040));
                Draw2D.rect(guiGraphics, (-f16) * 0.05f, f18 + (f17 * 0.42f), f16 * 0.05f, f18 + (f17 * 0.75f), Draw2D.argb(f6, 1708040));
            }
        }
    }

    private static void dome(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, int i, int i2) {
        float f5 = f - f3;
        float f6 = f2;
        for (int i3 = 1; i3 <= 20; i3++) {
            double d = 3.141592653589793d + ((3.141592653589793d * i3) / 20);
            float cos = f + (((float) Math.cos(d)) * f3);
            float sin = f2 + (((float) Math.sin(d)) * f4);
            Draw2D.triangle(guiGraphics, f, f2, i2, f5, f6, i, cos, sin, i);
            f5 = cos;
            f6 = sin;
        }
    }
}
