package com.paymentalerts.fx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.GuiGraphics;

public final class MoneyParticles {
    public static final Kind[][] STYLES = {new Kind[]{Kind.BILL, Kind.BILL, Kind.BILL, Kind.COIN, Kind.COIN}, new Kind[]{Kind.BAR, Kind.COIN}, new Kind[]{Kind.DIAMOND, Kind.EMERALD}, new Kind[]{Kind.BILL, Kind.COIN, Kind.BAR, Kind.DIAMOND, Kind.EMERALD}};
    private final Random random = new Random();
    private final List<Item> items = new ArrayList();
    private Kind[] rainMix = STYLES[0];
    private float rainLeft;
    private float rainRate;
    private float rainAccumulator;
    private float clock;

    public enum Kind {
        BILL,
        COIN,
        BAR,
        DIAMOND,
        EMERALD
    }

    private static final class Item {
        Kind kind;
        float x;
        float y;
        float vx;
        float vy;
        float gravity;
        float drag;
        float angle;
        float spin;
        float spinSpeed;
        float flutter;
        float flutterSpeed;
        float size;

        private Item() {
        }
    }

    public boolean isEmpty() {
        return this.items.isEmpty() && this.rainLeft <= 0.0f;
    }

    public void startRain(float f, float f2, int i) {
        this.rainLeft = f;
        this.rainRate = f2;
        this.rainMix = STYLES[Math.floorMod(i, STYLES.length)];
    }

    public void fountain(float f, float f2, int i, int i2) {
        for (int i3 = 0; i3 < i; i3++) {
            Item item = item(Kind.COIN, f + rand(-12.0f, 12.0f), f2);
            item.vx = rand(-0.55f, 0.55f) * i2;
            item.vy = (-rand(0.75f, 1.35f)) * i2;
            item.gravity = i2 * 1.7f;
            item.drag = 0.4f;
            this.items.add(item);
        }
    }

    public void burst(float f, float f2, int i, int i2, Kind[] kindArr, float f3, float f4, float f5) {
        for (int i3 = 0; i3 < i; i3++) {
            Kind kind = kindArr[this.random.nextInt(kindArr.length)];
            Item item = item(kind, f + rand(-6.0f, 6.0f), f2 + rand(-3.0f, 3.0f));
            float rand = i2 * rand(0.85f, 1.35f) * f5;
            float atan2 = ((float) Math.atan2(-1.0d, f3)) + rand(-f4, f4);
            item.vx = ((float) Math.cos(atan2)) * rand;
            item.vy = ((float) Math.sin(atan2)) * rand;
            if (kind == Kind.BILL) {
                item.gravity = i2 * 0.9f;
                item.drag = 1.1f;
            } else {
                item.gravity = i2 * 1.7f;
                item.drag = 0.35f;
            }
            this.items.add(item);
        }
    }

    public void update(float f, int i, int i2) {
        this.clock += f;
        if (this.rainLeft > 0.0f) {
            this.rainLeft -= f;
            this.rainAccumulator += f * this.rainRate;
            while (this.rainAccumulator >= 1.0f) {
                this.rainAccumulator -= 1.0f;
                spawnFalling(i, i2);
            }
        }
        Iterator<Item> it = this.items.iterator();
        while (it.hasNext()) {
            Item next = it.next();
            float exp = (float) Math.exp((-next.drag) * f);
            next.vx *= exp;
            next.vy = (next.vy * exp) + (next.gravity * f);
            next.x += (next.vx + (next.kind == Kind.BILL ? ((float) Math.sin((this.clock * next.flutterSpeed) + next.flutter)) * i2 * 0.08f : 0.0f)) * f;
            next.y += next.vy * f;
            next.spin += next.spinSpeed * f;
            if (next.kind == Kind.BILL) {
                next.angle = ((float) Math.sin((this.clock * next.flutterSpeed * 0.8f) + next.flutter)) * 0.9f;
            } else if (next.kind != Kind.COIN) {
                next.angle += next.spinSpeed * 0.25f * f;
            }
            if (next.y > i2 + 24) {
                it.remove();
            }
        }
    }

    private void spawnFalling(int i, int i2) {
        Kind kind = this.rainMix[this.random.nextInt(this.rainMix.length)];
        Item item = item(kind, rand(0.0f, i), -14.0f);
        if (kind == Kind.BILL) {
            item.vy = i2 * rand(0.16f, 0.26f);
        } else {
            item.vy = i2 * rand(0.25f, 0.4f);
            item.gravity = i2 * 0.25f;
            item.drag = 0.6f;
        }
        this.items.add(item);
    }

    private Item item(Kind kind, float f, float f2) {
        Item item = new Item();
        item.kind = kind;
        item.x = f;
        item.y = f2;
        item.angle = rand(-0.6f, 0.6f);
        item.flutter = rand(0.0f, 6.28f);
        item.flutterSpeed = rand(2.2f, 3.6f);
        item.spin = rand(0.0f, 6.28f);
        item.spinSpeed = rand(6.0f, 12.0f) * (this.random.nextBoolean() ? 1 : -1);
        item.size = kind == Kind.COIN ? rand(3.2f, 4.6f) : rand(0.85f, 1.15f);
        if (kind == Kind.BILL) {
            item.spinSpeed = rand(3.0f, 6.0f);
        }
        return item;
    }

    private float rand(float f, float f2) {
        return f + (this.random.nextFloat() * (f2 - f));
    }

    public void render(GuiGraphics guiGraphics, float f) {
        for (Item item : this.items) {
            switch (item.kind.ordinal()) {
                case HudFireworks.CLASSIC /* 0 */:
                    drawBill(guiGraphics, item, f);
                    break;
                case HudFireworks.GOLD /* 1 */:
                    drawCoin(guiGraphics, item, f);
                    break;
                case HudFireworks.RAINBOW_FINALE /* 2 */:
                    drawBar(guiGraphics, item, f);
                    break;
                case 3:
                    drawGem(guiGraphics, item, f, 1.0f, 14221311, 5235680, 1744816, 9434610);
                    break;
                case 4:
                    drawGem(guiGraphics, item, f, 1.45f, 13172692, 4054122, 1018426, 8188058);
                    break;
            }
        }
    }

    private static void drawBill(GuiGraphics guiGraphics, Item item, float f) {
        float max = Math.max(0.25f, Math.abs((float) Math.cos(item.spin * 0.5f)));
        float f2 = 18.0f * item.size;
        float f3 = 9.0f * item.size * max;
        Draw2D.rotatedRect(guiGraphics, item.x, item.y, f2, f3, item.angle, Draw2D.argb(f, 1993530));
        Draw2D.rotatedRect(guiGraphics, item.x, item.y, f2 - 1.6f, f3 - (1.6f * max), item.angle, Draw2D.argb(f, 6275711));
        Draw2D.rotatedRect(guiGraphics, item.x, item.y, f2 - 4.0f, f3 - (3.4f * max), item.angle, Draw2D.argb(f, 9428132));
        Draw2D.ellipse(guiGraphics, item.x, item.y, 1.8f * item.size, 1.8f * item.size * max, Draw2D.argb(f, 3050319), Draw2D.argb(f, 1993530));
    }

    private static void drawCoin(GuiGraphics guiGraphics, Item item, float f) {
        float cos = (float) Math.cos(item.spin);
        float max = Math.max(0.7f, Math.abs(cos) * item.size);
        Draw2D.ellipse(guiGraphics, item.x, item.y, max + 0.7f, item.size + 0.7f, Draw2D.argb(f, 11037184), Draw2D.argb(f, 8014336));
        Draw2D.ellipse(guiGraphics, item.x, item.y, max, item.size, Draw2D.argb(f, 16770442), Draw2D.argb(f, 14721566));
        if (Math.abs(cos) > 0.6f) {
            Draw2D.glow(guiGraphics, item.x - (max * 0.3f), item.y - (item.size * 0.35f), item.size * 0.5f, f * 0.8f, 16777215);
        }
    }

    private static void drawBar(GuiGraphics guiGraphics, Item item, float f) {
        float f2 = 15.0f * item.size;
        float f3 = 7.0f * item.size;
        Draw2D.rotatedRect(guiGraphics, item.x, item.y, f2 + 1.4f, f3 + 1.4f, item.angle, Draw2D.argb(f, 9067008));
        Draw2D.rotatedRect(guiGraphics, item.x, item.y, f2, f3, item.angle, Draw2D.argb(f, 15249440));
        Draw2D.rotatedRect(guiGraphics, item.x, item.y, f2 - 3.0f, f3 - 3.0f, item.angle, Draw2D.argb(f, 16765514));
        Draw2D.rotatedRect(guiGraphics, item.x + (((float) Math.sin(item.angle)) * f3 * 0.28f), item.y - ((((float) Math.cos(item.angle)) * f3) * 0.28f), f2 - 4.0f, 1.2f, item.angle, Draw2D.argb(f, 16774320));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static void drawGem(GuiGraphics guiGraphics, Item item, float f, float f2, int i, int i2, int i3, int i4) {
        float f3 = 5.5f * item.size;
        float cos = (float) Math.cos(item.angle);
        float sin = (float) Math.sin(item.angle);
        float[][] fArr = {new float[]{0.0f, (-f3) * f2}, new float[]{-f3, 0.0f}, new float[]{0.0f, f3 * f2}, new float[]{f3, 0.0f}};
        float[] fArr2 = new float[4];
        float[] fArr3 = new float[4];
        for (int i5 = 0; i5 < 4; i5++) {
            fArr2[i5] = (item.x + (fArr[i5][0] * cos)) - (fArr[i5][1] * sin);
            fArr3[i5] = item.y + (fArr[i5][0] * sin) + (fArr[i5][1] * cos);
        }
        int argb = Draw2D.argb(f, Draw2D.lerpRgb(i, 16777215, 0.5f));
        Draw2D.triangle(guiGraphics, item.x, item.y, argb, fArr2[0], fArr3[0], Draw2D.argb(f, i), fArr2[1], fArr3[1], Draw2D.argb(f, i2));
        Draw2D.triangle(guiGraphics, item.x, item.y, argb, fArr2[1], fArr3[1], Draw2D.argb(f, i2), fArr2[2], fArr3[2], Draw2D.argb(f, i3));
        Draw2D.triangle(guiGraphics, item.x, item.y, argb, fArr2[2], fArr3[2], Draw2D.argb(f, i3), fArr2[3], fArr3[3], Draw2D.argb(f, i4));
        Draw2D.triangle(guiGraphics, item.x, item.y, argb, fArr2[3], fArr3[3], Draw2D.argb(f, i4), fArr2[0], fArr3[0], Draw2D.argb(f, i));
        if (Math.sin(item.spin) > 0.85d) {
            Draw2D.glow(guiGraphics, item.x, item.y, f3 * 1.6f, f * 0.7f, 16777215);
        }
    }
}
