package com.paymentalerts.fx;

import com.paymentalerts.sound.AlertSounds;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvent;

public final class HudFireworks {
    private static final int MAX_SPARKS = 3000;
    private static final int[][] PALETTE = {new int[]{16728128, 8392720}, new int[]{16754219, 8400912}, new int[]{16771402, 8413200}, new int[]{5111674, 1073200}, new int[]{4577791, 1065056}, new int[]{6061311, 2105456}, new int[]{11885823, 4198512}, new int[]{16735435, 7344192}, new int[]{16777215, 8417360}};
    private final Random random;
    private final List<Rocket> rockets;
    private final List<Spark> sparks;
    private final List<DelayedSound> sounds;
    private final int totalRockets;
    private final int[] palettes;
    private final int style;
    private int launched;
    private float clock;
    private float nextLaunch;
    public static final int CLASSIC = 0;
    public static final int GOLD = 1;
    public static final int RAINBOW_FINALE = 2;
    public static final int HEARTS = 3;

    private enum Burst {
        PEONY(30, true),
        RING(14, false),
        STAR(10, false),
        HEART(8, true),
        WILLOW(14, true),
        CRACKLE(12, false),
        DOUBLE(12, true);

        final int weight;
        final boolean large;

        Burst(int i, boolean z) {
            this.weight = i;
            this.large = z;
        }
    }

    public HudFireworks(int i) {
        this(i, 0);
    }

    public HudFireworks(int i, int i2) {
        int[] iArr;
        this.random = new Random();
        this.rockets = new ArrayList();
        this.sparks = new ArrayList();
        this.sounds = new ArrayList();
        this.totalRockets = i;
        this.style = i2;
        switch (i2) {
            case GOLD /* 1 */:
                iArr = new int[]{1, 2, 8};
                break;
            case 3:
                iArr = new int[]{0, 7, 8};
                break;
            default:
                iArr = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
                break;
        }
        this.palettes = iArr;
    }

    public boolean isFinished() {
        return this.launched >= this.totalRockets && this.rockets.isEmpty() && this.sparks.isEmpty();
    }

    public void stopLaunching() {
        this.launched = this.totalRockets;
    }

    public void update(float f, int i, int i2) {
        this.clock += f;
        if (this.launched < this.totalRockets && this.clock >= this.nextLaunch) {
            int i3 = this.style == 2 && this.totalRockets - this.launched <= 5 ? this.totalRockets - this.launched : 1;
            for (int i4 = 0; i4 < i3; i4++) {
                launch(i, i2);
                this.launched++;
            }
            this.nextLaunch = this.clock + (this.launched < 3 ? 0.12f + (this.random.nextFloat() * 0.12f) : (0.26f + (this.random.nextFloat() * 0.38f)) * (this.style == 2 ? 0.55f : 1.0f));
        }
        Iterator<Rocket> it = this.rockets.iterator();
        while (it.hasNext()) {
            Rocket next = it.next();
            next.vy += next.gravity * f;
            next.x += next.vx * f;
            next.y += next.vy * f;
            next.fuse -= f;
            next.trailTimer += f;
            while (next.trailTimer > 0.014285714f) {
                next.trailTimer -= 0.014285714f;
                Spark spark = spark(next.x, next.y, rand(-8.0f, 8.0f), rand(4.0f, 18.0f), rand(0.22f, 0.45f), 16760944, 7346176);
                spark.size = 1.0f;
                spark.drag = 3.0f;
                spark.gravity = i2 * 0.05f;
                add(spark);
            }
            if (next.fuse <= 0.0f) {
                explode(next, i2);
                it.remove();
            }
        }
        Iterator<Spark> it2 = this.sparks.iterator();
        while (it2.hasNext()) {
            Spark next2 = it2.next();
            float exp = (float) Math.exp((-next2.drag) * f);
            next2.vx *= exp;
            next2.vy = (next2.vy * exp) + (next2.gravity * f);
            next2.x += next2.vx * f;
            next2.y += next2.vy * f;
            next2.age += f;
            if (next2.age >= next2.life) {
                it2.remove();
            }
        }
        Iterator<DelayedSound> it3 = this.sounds.iterator();
        while (it3.hasNext()) {
            DelayedSound next3 = it3.next();
            if (this.clock >= next3.at) {
                AlertSounds.play(next3.sound, next3.pitch, next3.volume);
                it3.remove();
            }
        }
    }

    private void launch(int i, int i2) {
        Rocket rocket = new Rocket();
        rocket.x = i * rand(0.12f, 0.88f);
        rocket.y = i2 + 4.0f;
        rocket.gravity = i2 * 0.5f;
        rocket.fuse = rand(0.85f, 1.25f);
        rocket.vy = (((i2 * rand(0.16f, 0.46f)) - rocket.y) - (((0.5f * rocket.gravity) * rocket.fuse) * rocket.fuse)) / rocket.fuse;
        rocket.vx = i * rand(-0.06f, 0.06f);
        rocket.type = pickBurst();
        rocket.palette = this.palettes[this.random.nextInt(this.palettes.length)];
        this.rockets.add(rocket);
        AlertSounds.play(AlertSounds.FIREWORK_LAUNCH, rand(0.9f, 1.15f), 0.55f);
    }

    private void explode(Rocket rocket, int i) {
        int i2;
        float rand = i * rand(0.36f, 0.5f);
        int[] iArr = PALETTE[rocket.palette];
        int[][] iArr2 = PALETTE;
        if (this.palettes.length > 3) {
            i2 = ((rocket.palette + 2) + this.random.nextInt(PALETTE.length - 3)) % PALETTE.length;
        } else {
            i2 = this.palettes[this.random.nextInt(this.palettes.length)];
        }
        int[] iArr3 = iArr2[i2];
        Spark spark = spark(rocket.x, rocket.y, 0.0f, 0.0f, 0.16f, 16777215, iArr[0]);
        spark.size = 7.0f;
        spark.drag = 0.0f;
        spark.soft = true;
        add(spark);
        switch (rocket.type.ordinal()) {
            case CLASSIC /* 0 */:
                sphere(rocket.x, rocket.y, 90, rand, iArr, false, 1.0f);
                break;
            case GOLD /* 1 */:
                float rand2 = rand(0.35f, 1.0f);
                float rand3 = rand(0.0f, 6.2831855f);
                for (int i3 = 0; i3 < 56; i3++) {
                    float f = (i3 * 6.2831855f) / 56;
                    float cos = (float) Math.cos(f);
                    float sin = ((float) Math.sin(f)) * rand2;
                    add(normal(rocket.x, rocket.y, ((float) ((cos * Math.cos(rand3)) - (sin * Math.sin(rand3)))) * rand, ((float) ((cos * Math.sin(rand3)) + (sin * Math.cos(rand3)))) * rand, iArr, i));
                }
                break;
            case RAINBOW_FINALE /* 2 */:
                float rand4 = rand(0.0f, 6.2831855f);
                for (int i4 = 0; i4 < 100; i4++) {
                    float pow = rand * (0.45f + (0.55f * ((float) Math.pow(Math.abs(Math.cos(2.5d * ((i4 * 6.2831855f) / 100))), 3.0d))));
                    float angle = (i4 * 6.2831855f) / 100;
                    add(normal(rocket.x, rocket.y, ((float) Math.cos(angle + rand4)) * pow, ((float) Math.sin(angle + rand4)) * pow, iArr, i));
                }
                break;
            case 3:
                for (int i5 = 0; i5 < 90; i5++) {
                    double d = ((i5 * 3.141592653589793d) * 2.0d) / 90;
                    add(normal(rocket.x, rocket.y, (((float) (16.0d * Math.pow(Math.sin(d), 3.0d))) / 17.0f) * rand, (((float) (-((((13.0d * Math.cos(d)) - (5.0d * Math.cos(2.0d * d))) - (2.0d * Math.cos(3.0d * d))) - Math.cos(4.0d * d)))) / 17.0f) * rand, new int[]{16732042, 7344176}, i));
                }
                break;
            case 4:
                for (int i6 = 0; i6 < 70; i6++) {
                    float[] sphereDirection = sphereDirection();
                    float rand5 = rand * 0.8f * rand(0.85f, 1.05f);
                    Spark spark2 = spark(rocket.x, rocket.y, sphereDirection[0] * rand5, sphereDirection[1] * rand5, rand(2.3f, 3.0f), 16762976, 5906432);
                    spark2.drag = 1.6f;
                    spark2.gravity = i * 0.2f;
                    spark2.size = 1.3f;
                    add(spark2);
                }
                break;
            case 5:
                sphere(rocket.x, rocket.y, 80, rand * 0.9f, new int[]{16774352, 8413216}, true, 1.1f);
                break;
            case 6:
                sphere(rocket.x, rocket.y, 70, rand, iArr, false, 1.0f);
                sphere(rocket.x, rocket.y, 40, rand * 0.5f, iArr3, false, 0.9f);
                break;
        }
        AlertSounds.play(rocket.type.large ? AlertSounds.FIREWORK_LARGE_BLAST : AlertSounds.FIREWORK_BLAST, rand(0.85f, 1.15f), 0.85f);
        if (this.random.nextFloat() < 0.3f) {
            this.sounds.add(new DelayedSound(this.clock + 0.25f, AlertSounds.FIREWORK_BLAST_FAR, rand(0.9f, 1.1f), 0.45f));
        }
        if (rocket.type == Burst.CRACKLE) {
            this.sounds.add(new DelayedSound(this.clock + 0.45f, AlertSounds.FIREWORK_TWINKLE, rand(0.9f, 1.1f), 0.7f));
        }
    }

    private void sphere(float f, float f2, int i, float f3, int[] iArr, boolean z, float f4) {
        for (int i2 = 0; i2 < i; i2++) {
            float[] sphereDirection = sphereDirection();
            float rand = f3 * rand(0.9f, 1.05f);
            Spark spark = spark(f, f2, sphereDirection[0] * rand, sphereDirection[1] * rand, rand(1.25f, 1.85f) * f4, iArr[0], iArr[1]);
            spark.flicker = z;
            spark.gravity = f3 * 0.28f;
            add(spark);
        }
    }

    private Spark normal(float f, float f2, float f3, float f4, int[] iArr, int i) {
        Spark spark = spark(f, f2, f3, f4, rand(1.25f, 1.8f), iArr[0], iArr[1]);
        spark.gravity = i * 0.12f;
        return spark;
    }

    private float[] sphereDirection() {
        double nextDouble = (this.random.nextDouble() * 2.0d) - 1.0d;
        double nextDouble2 = this.random.nextDouble() * 3.141592653589793d * 2.0d;
        double sqrt = Math.sqrt(1.0d - (nextDouble * nextDouble));
        return new float[]{(float) (sqrt * Math.cos(nextDouble2)), (float) (sqrt * Math.sin(nextDouble2))};
    }

    private Spark spark(float f, float f2, float f3, float f4, float f5, int i, int i2) {
        Spark spark = new Spark();
        spark.x = f;
        spark.y = f2;
        spark.vx = f3;
        spark.vy = f4;
        spark.life = f5;
        spark.rgb = i;
        spark.fadeRgb = i2;
        return spark;
    }

    private void add(Spark spark) {
        if (this.sparks.size() < MAX_SPARKS) {
            this.sparks.add(spark);
        }
    }

    private Burst pickBurst() {
        if (this.style == 3 && this.random.nextFloat() < 0.6f) {
            return Burst.HEART;
        }
        int i = 0;
        for (Burst burst : Burst.values()) {
            i += burst.weight;
        }
        int nextInt = this.random.nextInt(i);
        for (Burst burst2 : Burst.values()) {
            nextInt -= burst2.weight;
            if (nextInt < 0) {
                return burst2;
            }
        }
        return Burst.PEONY;
    }

    private float rand(float f, float f2) {
        return f + (this.random.nextFloat() * (f2 - f));
    }

    public void render(GuiGraphics guiGraphics) {
        if (this.sparks.isEmpty() && this.rockets.isEmpty()) {
            return;
        }
        for (Spark spark : this.sparks) {
            float f = spark.age / spark.life;
            float f2 = f < 0.6f ? 1.0f : 1.0f - ((f - 0.6f) / 0.4f);
            if (spark.flicker && f > 0.3f && this.random.nextFloat() < 0.55f) {
                f2 *= 0.15f;
            }
            int lerp = f < 0.1f ? lerp(16777215, spark.rgb, f / 0.1f) : lerp(spark.rgb, spark.fadeRgb, Math.max(0.0f, (f - 0.45f) / 0.55f));
            if (spark.soft) {
                diamond(guiGraphics, spark.x, spark.y, spark.size * 2.2f, color(f2 * 0.25f, lerp));
                diamond(guiGraphics, spark.x, spark.y, spark.size, color(f2 * 0.55f, lerp));
            } else {
                diamond(guiGraphics, spark.x, spark.y, spark.size * 2.8f, color(f2 * 0.2f, lerp));
                square(guiGraphics, spark.x, spark.y, spark.size, color(f2, lerp));
            }
        }
        for (Rocket rocket : this.rockets) {
            diamond(guiGraphics, rocket.x, rocket.y, 5.0f, color(0.3f, 16767120));
            square(guiGraphics, rocket.x, rocket.y, 1.4f, color(1.0f, 16774872));
        }
    }

    private static void square(GuiGraphics guiGraphics, float f, float f2, float f3, int i) {
        Draw2D.convexQuad(guiGraphics, f - f3, f2 - f3, f - f3, f2 + f3, f + f3, f2 + f3, f + f3, f2 - f3, i);
    }

    private static void diamond(GuiGraphics guiGraphics, float f, float f2, float f3, int i) {
        Draw2D.convexQuad(guiGraphics, f, f2 - f3, f - f3, f2, f, f2 + f3, f + f3, f2, i);
    }

    private static int color(float f, int i) {
        return (Math.round(Math.max(0.0f, Math.min(1.0f, f)) * 255.0f) << 24) | (i & 16777215);
    }

    private static int lerp(int i, int i2, float f) {
        float max = Math.max(0.0f, Math.min(1.0f, f));
        return (Math.round(((i >> 16) & 255) + ((((i2 >> 16) & 255) - ((i >> 16) & 255)) * max)) << 16) | (Math.round(((i >> 8) & 255) + ((((i2 >> 8) & 255) - ((i >> 8) & 255)) * max)) << 8) | Math.round((i & 255) + (((i2 & 255) - (i & 255)) * max));
    }

    private static final class Rocket {
        float x;
        float y;
        float vx;
        float vy;
        float gravity;
        float fuse;
        float trailTimer;
        Burst type;
        int palette;

        private Rocket() {
        }
    }

    private static final class Spark {
        float x;
        float y;
        float vx;
        float vy;
        float age;
        float life;
        float size = 1.5f;
        float drag = 2.3f;
        float gravity;
        int rgb;
        int fadeRgb;
        boolean flicker;
        boolean soft;

        private Spark() {
        }
    }

    private static final record DelayedSound(float at, SoundEvent sound, float pitch, float volume) {


        

        

        




    }
}
