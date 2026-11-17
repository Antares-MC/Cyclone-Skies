package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Cash cannon: visible barrels on the lower corners that fire tumbling bills.
 * The shot pattern is what changes between looks — one barrel, two alternating,
 * or a gatling cadence.
 */
final class CashCannonAlert extends StageBase {
    static final String[] VARIATIONS = {"Single barrel", "Double", "Gatling"};

    private static final float END = 6.2f;
    private static final int MAX_BILLS = 260;

    private final float[] shots;
    private final boolean[] sides;
    private final Bill[] bills = new Bill[MAX_BILLS];
    private int billCursor;
    private int shotCursor;
    private float flashAge = 99f;
    private boolean flashLeft;
    private float smokeTimer;

    private static final class Bill {
        float x, y, vx, vy, angle, spin, size;
        int rgb;
        boolean live;
    }

    CashCannonAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < this.bills.length; i++) {
            this.bills[i] = new Bill();
        }
        switch (look()) {
            case 1 -> {
                this.shots = new float[]{0.55f, 1.45f, 2.35f, 3.25f, 4.15f};
                this.sides = new boolean[]{true, false, true, false, true};
            }
            case 2 -> {
                int n = 30;
                this.shots = new float[n];
                this.sides = new boolean[n];
                for (int i = 0; i < n; i++) {
                    this.shots[i] = 0.7f + i * 0.115f;
                    this.sides[i] = i % 2 == 0;
                }
            }
            default -> {
                this.shots = new float[]{0.55f, 1.65f, 2.75f, 3.85f};
                this.sides = new boolean[]{true, true, true, true};
            }
        }
    }

    @Override
    protected float duration() {
        return END;
    }

    @Override
    protected String[] looks() {
        return VARIATIONS;
    }

    @Override
    protected void cue() {
        this.cues.sound(0.2f, AlertSounds.JACKPOT_TICK, 0.5f, 1.6f);
        for (int i = 0; i < this.shots.length; i++) {
            float at = this.shots[i];
            this.cues.sound(at - 0.1f, AlertSounds.JACKPOT_TICK, 0.6f, 1.3f);
            this.cues.sound(at, AlertSounds.CONFETTI_POP, 0.9f, 1.0f);
            this.cues.sound(at, AlertSounds.MONEY_DING, 0.8f, 0.9f + (i % 4) * 0.06f);
        }
        this.cues.sound(1.1f, AlertSounds.MONEY_COIN, 0.5f, 1.1f)
                .sound(4.6f, AlertSounds.MONEY_COIN, 0.5f, 0.8f);
    }

    @Override
    protected void advance(float t) {
        while (this.shotCursor < this.shots.length && t >= this.shots[this.shotCursor]) {
            fire(t, this.sides[this.shotCursor]);
            this.shotCursor++;
        }
        this.flashAge += this.dt;
        this.smokeTimer += this.dt;
        if (this.smokeTimer > 0.08f && t < 4.2f) {
            this.smokeTimer = 0f;
            smokePuff(t);
        }
        updateBills(t);
    }

    private void fire(float t, boolean left) {
        this.flashAge = 0f;
        this.flashLeft = left;
        float[] muzzle = muzzle(left, t);
        float dir = left ? -1f : 1f;
        int count = 14;
        for (int i = 0; i < count; i++) {
            Bill b = nextBill();
            float ang = rnd(-1.15f, -0.45f) + (left ? 0f : -0.2f);
            float speed = rnd(380f, 780f);
            b.x = muzzle[0];
            b.y = muzzle[1];
            b.vx = (float) Math.cos(ang) * speed * dir;
            b.vy = (float) Math.sin(ang) * speed;
            b.angle = rnd(0f, 6.283f);
            b.spin = rnd(-9f, 9f);
            b.size = rnd(11f, 20f);
            b.rgb = chance(0.2f) ? 0xE8D9A8 : (chance(0.5f) ? 0x86C98A : 0x69B978);
            b.live = true;
        }
        for (int i = 0; i < 5; i++) {
            Particles2D.P p = spawn(Particles2D.Kind.SMOKE, muzzle[0], muzzle[1],
                    rnd(-60f, 60f) + (left ? 60f : -60f), rnd(-90f, -20f),
                    rnd(0.5f, 1.0f), rnd(4f, 9f), 0xCFC8B8, 0x2A2A2A);
            p.gravity = -10f;
            p.drag = 1.6f;
        }
    }

    private void smokePuff(float t) {
        if (t < 0.4f) {
            return;
        }
        boolean left = chance(0.5f);
        float[] muzzle = muzzle(left, t);
        Particles2D.P p = spawn(Particles2D.Kind.SMOKE, muzzle[0], muzzle[1],
                rnd(-20f, 20f), rnd(-40f, -10f), rnd(0.4f, 0.9f), rnd(2f, 5f),
                0x9A9488, 0x222018);
        p.drag = 1.4f;
        p.gravity = -8f;
    }

    private float[] muzzle(boolean left, float t) {
        float baseX = left ? this.w * 0.10f : this.w * 0.90f;
        float baseY = this.h * 0.955f;
        float ang = left ? -0.95f : -2.19f;
        float recoil = this.flashAge < 0.14f && this.flashLeft == left
                ? (1f - this.flashAge / 0.14f) * 16f : 0f;
        float len = Math.min(this.w, this.h) * 0.16f;
        float x = baseX + (float) Math.cos(ang) * (len - recoil);
        float y = baseY + (float) Math.sin(ang) * (len - recoil);
        return new float[]{x, y, ang};
    }

    private Bill nextBill() {
        Bill b = this.bills[this.billCursor];
        this.billCursor = (this.billCursor + 1) % this.bills.length;
        return b;
    }

    private void updateBills(float t) {
        float floor = this.h * 0.90f;
        for (Bill b : this.bills) {
            if (!b.live) {
                continue;
            }
            b.vy += 260f * this.dt;
            b.vx *= (float) Math.exp(-0.55f * this.dt);
            b.x += b.vx * this.dt;
            b.y += b.vy * this.dt;
            b.angle += b.spin * this.dt;
            if (b.y > floor) {
                b.y = floor;
                b.vy *= -0.28f;
                b.vx *= 0.62f;
                b.spin *= 0.5f;
            }
            if (t > 4.3f) {
                b.vy -= 1500f * this.dt;
                b.vx += 260f * this.dt;
            }
            if (b.x < -60f || b.x > this.w + 60f || b.y < -80f) {
                b.live = false;
            }
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.8f);

        Draw2D.gradientV(g, 0f, h * 0.72f, w, h, Draw2D.argb(0.28f * fade, 0x10261A),
                Draw2D.argb(0f, 0x10261A));

        for (Bill b : this.bills) {
            if (b.live) {
                bill(g, b.x, b.y, b.size, b.angle, fade, b.rgb);
            }
        }

        drawCannon(g, w, h, t, true);
        drawCannon(g, w, h, t, false);

        if (this.flashAge < 0.16f) {
            float a = (1f - this.flashAge / 0.16f) * fade;
            boolean left = this.flashLeft;
            float[] m = muzzle(left, t);
            float ang = m[2];
            Draw2D.glow(g, m[0], m[1], Math.min(w, h) * 0.09f, a * 0.8f, 0xFFF2B0);
            for (int i = 0; i < 7; i++) {
                float spread = rnd(-0.35f, 0.35f);
                float len = rnd(20f, 70f) * (0.5f + a);
                Draw2D.softLine(g, m[0], m[1],
                        m[0] + (float) Math.cos(ang + spread) * len,
                        m[1] + (float) Math.sin(ang + spread) * len,
                        Math.max(1.5f, 4f * a), a * 0.7f, 0xFFE082);
            }
            star4(g, m[0], m[1], Math.min(w, h) * 0.035f * a, a, 0xFFFFFF);
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    private void drawCannon(GuiGraphics g, int w, int h, float t, boolean left) {
        float baseX = left ? w * 0.10f : w * 0.90f;
        float baseY = h * 0.955f;
        float ang = left ? -0.95f : -2.19f;
        float recoil = this.flashAge < 0.14f && this.flashLeft == left
                ? (1f - this.flashAge / 0.14f) * 16f
                : (float) Math.sin(t * 1.4f + (left ? 0f : 2f)) * 1.2f;
        float len = Math.min(w, h) * 0.16f;
        float x = baseX + (float) Math.cos(ang) * (len - recoil);
        float y = baseY + (float) Math.sin(ang) * (len - recoil);
        float body = Math.min(w, h) * 0.052f;

        Draw2D.rotatedRect(g, (baseX + x) * 0.5f, (baseY + y) * 0.5f, len, body, ang + 1.5708f,
                Draw2D.argb(0.95f, 0x3A3F46));
        Draw2D.rotatedRect(g, (baseX + x) * 0.5f, (baseY + y) * 0.5f, len * 0.9f, body * 0.55f,
                ang + 1.5708f, Draw2D.argb(0.9f, 0x6E7681));
        Draw2D.circle(g, x, y, body * 0.62f, Draw2D.argb(1f, 0x1B1F24), Draw2D.argb(1f, 0x05070A));
        Draw2D.circle(g, x, y, body * 0.38f, Draw2D.argb(1f, 0x0A0C10), Draw2D.argb(1f, 0x000000));

        Draw2D.circle(g, baseX, baseY, body * 0.85f, Draw2D.argb(1f, 0x2A2E33), Draw2D.argb(1f, 0x101214));
        Draw2D.circle(g, baseX, baseY, body * 0.4f, Draw2D.argb(1f, 0x8A929B), Draw2D.argb(1f, 0x3D4249));
        Draw2D.rect(g, baseX - body * 0.9f, baseY, baseX + body * 0.9f, baseY + body * 0.55f,
                Draw2D.argb(1f, 0x22262A));

        if (this.flashAge < 0.14f && this.flashLeft == left) {
            Draw2D.glow(g, x, y, body * 1.4f, 0.6f, 0xFFDF80);
        }
        for (int i = 0; i < 3; i++) {
            float px = baseX + (float) Math.cos(ang) * (len * 0.35f + i * body * 0.9f);
            float py = baseY + (float) Math.sin(ang) * (len * 0.35f + i * body * 0.9f);
            Draw2D.glow(g, px, py, body * 0.24f, 0.5f, 0xC9A24A);
        }
    }
}
