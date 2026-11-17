package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Ice age: cold as a physical force. Frost ferns creep in from every edge, the frame
 * desaturates, ice shards grow from the bottom, the name is etched in frost, and then the
 * whole sheet cracks and shatters.
 */
final class IceAgeAlert extends StageBase {
    static final String[] VARIATIONS = {"Frost", "Shatter", "Glacier"};

    private static final float FREEZE = 1.2f;
    private static final float END = 7.6f;
    private static final int SHARDS = 42;

    private final Shard[] shards = new Shard[SHARDS];
    private final float[] crackX = new float[26];
    private final float[] crackY = new float[26];
    private float snowTimer;
    private boolean shatterDone;

    private static final class Shard {
        float x, width, height, tilt, delay, vy, yOff, rot, spin;
        int rgb;
    }

    IceAgeAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < SHARDS; i++) {
            Shard s = new Shard();
            s.x = (i + 0.5f) / SHARDS + rnd(-0.012f, 0.012f);
            s.width = rnd(0.02f, 0.05f);
            s.height = look() == 2 ? rnd(0.3f, 0.62f) : rnd(0.10f, 0.34f);
            s.tilt = rnd(-0.22f, 0.22f);
            s.delay = rnd(0f, 1.1f);
            s.yOff = rnd(-0.02f, 0.05f);
            s.rot = 0f;
            s.spin = rnd(-1.6f, 1.6f);
            s.rgb = chance(0.4f) ? 0xBFE8FF : (chance(0.5f) ? 0xDCF6FF : 0x8FD0F0);
            this.shards[i] = s;
        }
        for (int i = 0; i < this.crackX.length; i++) {
            float ang = rnd(-1.9f, -1.2f);
            float len = rnd(0.15f, 1f);
            this.crackX[i] = (float) Math.cos(ang) * len;
            this.crackY[i] = (float) Math.sin(ang) * len;
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

    private float shatterAt() {
        return look() == 1 ? 3.6f : 5.0f;
    }

    @Override
    protected void cue() {
        this.cues.sound(0.1f, AlertSounds.BLACKHOLE_CHARGE, 0.55f, 1.2f)
                .sound(FREEZE - 0.1f, AlertSounds.NOVA_COLLAPSE, 0.7f, 1.1f)
                .sound(FREEZE, AlertSounds.BLACKHOLE_COLLAPSE, 0.7f, 0.9f)
                .sound(2.4f, AlertSounds.COIN_PICKUP, 0.35f, 1.4f)
                .sound(3.6f, AlertSounds.COIN_PICKUP, 0.3f, 1.2f)
                .sound(shatterAt(), AlertSounds.NUKE_SHOCKWAVE, 0.7f, 1.2f)
                .sound(shatterAt() + 0.1f, AlertSounds.BLACKHOLE_BANG, 0.5f, 1.1f);
        ScreenShake.add(FREEZE, 0.25f, 0.2f);
        ScreenShake.add(shatterAt(), 0.4f, 0.5f);
    }

    @Override
    protected void advance(float t) {
        if (t > 1.0f && t < 6.8f) {
            this.snowTimer += this.dt;
            while (this.snowTimer > 0.02f) {
                this.snowTimer -= 0.02f;
                Particles2D.P p = spawn(Particles2D.Kind.SPARK, rnd(0f, this.w), rnd(-20f, 0f),
                        rnd(-160f, -60f), rnd(30f, 90f), rnd(1.8f, 3.4f), rnd(0.8f, 2f),
                        0xDCF6FF, 0x6A90A8);
                p.gravity = 6f;
                p.drag = 0.4f;
                p.twinkle = true;
            }
        }
        if (!this.shatterDone && t >= shatterAt()) {
            this.shatterDone = true;
            for (Shard s : this.shards) {
                s.vy = rnd(60f, 260f);
                s.spin = rnd(-2.4f, 2.4f);
            }
        }
        if (this.shatterDone) {
            for (Shard s : this.shards) {
                s.vy += 900f * this.dt;
                s.yOff += s.vy * this.dt / this.h;
                s.rot += s.spin * this.dt;
            }
        }
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.9f);
        float cold = ramp(t, 0f, 1.0f);
        int ice = 0xDCF6FF;

        Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.72f * fade, 0x071018));
        Draw2D.gradientV(g, 0f, 0f, w, h, Draw2D.argb(0.25f * cold * fade, 0x0A2440),
                Draw2D.argb(0.55f * cold * fade, 0x0F3450));

        // Frost creep from all four edges.
        float creep = Easing.outCubic(ramp(t, 0f, FREEZE + 0.4f));
        fernEdge(g, w, h, creep, fade, 0, ice);
        fernEdge(g, w, h, creep, fade, 1, ice);
        fernEdge(g, w, h, creep, fade, 2, ice);
        fernEdge(g, w, h, creep, fade, 3, ice);

        // Ice shards growing up from the bottom.
        for (Shard s : this.shards) {
            float grow = Easing.outBack(ramp(t, FREEZE + s.delay, 1.1f));
            if (grow <= 0f) {
                continue;
            }
            float sx = s.x * w + s.tilt * 40f;
            float baseY = h * (1.02f + s.yOff) + (this.shatterDone ? 0f : 0f);
            float topY = baseY - s.height * h * grow;
            float halfW = s.width * w * 0.5f;
            float a = 0.55f * fade * Math.min(1f, grow);
            Draw2D.triangle(g, sx - halfW, baseY, Draw2D.argb(a * 0.7f, s.rgb),
                    sx, topY, Draw2D.argb(a, 0xF4FCFF),
                    sx + halfW, baseY, Draw2D.argb(a * 0.7f, s.rgb));
            Draw2D.softLine(g, sx - halfW * 0.4f, baseY, sx, topY, Math.max(1f, halfW * 0.16f),
                    a * 0.7f, 0xFFFFFF);
            Draw2D.softLine(g, sx, topY, sx + halfW * 0.3f, baseY, Math.max(1f, halfW * 0.1f),
                    a * 0.4f, 0x8FD0F0);
        }

        // Freeze flash.
        float flash = 1f - ramp(t, FREEZE, 0.35f);
        if (flash > 0f) {
            Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(flash * 0.5f * fade, ice));
        }

        // Etched name and amount.
        float etch = ramp(t, 1.5f, 1.2f);
        if (etch > 0f && t < shatterAt() + 0.9f) {
            float out = 1f - ramp(t, shatterAt() + 0.3f, 0.6f);
            Component nameText = TextFx.plain(this.payment.name(), ice, true);
            float scale = TextFx.fitScale(font, nameText, w * 0.8f, 3.8f);
            Draw2D.glow(g, w * 0.5f, h * 0.24f + 12f * scale, w * 0.3f, 0.16f * etch * out * fade, 0x6AC8F0);
            TextFx.centered(g, font, nameText, w * 0.5f, h * 0.24f, scale, etch * out * fade);
            Component amountText = TextFx.plain(amountPrefix() + this.payment.displayAmount(), 0xBFE8FF, true);
            TextFx.centered(g, font, amountText, w * 0.5f, h * 0.24f + 32f * scale,
                    TextFx.fitScale(font, amountText, w * 0.7f, 1.8f), etch * out * 0.9f * fade);
        }

        // Crack web and shatter.
        if (t >= shatterAt()) {
            float p = ramp(t, shatterAt(), 0.5f);
            float cx = w * 0.5f;
            float cy = h * 0.72f;
            for (int i = 0; i < this.crackX.length; i++) {
                float len = Math.min(w, h) * 0.55f * p;
                Draw2D.softLine(g, cx, cy, cx + this.crackX[i] * len, cy + this.crackY[i] * len,
                        Math.max(1f, (1f - p) * 3f), (1f - p * 0.5f) * 0.7f * fade, 0xFFFFFF);
            }
            for (Shard s : this.shards) {
                float sx = s.x * w + s.tilt * 40f;
                float a = 0.5f * fade * (1f - ramp(t, shatterAt() + 0.7f, 1.2f));
                Draw2D.rotatedRect(g, sx, h * (1f + s.yOff), s.width * w, s.height * h * 0.5f,
                        s.rot, Draw2D.argb(Math.max(0f, a), s.rgb));
            }
            for (int i = 0; i < 10; i++) {
                float sx = (i + 0.5f) / 10f * w;
                Draw2D.softLine(g, sx, h * 0.4f, sx + wave(t, 3f, i) * 30f, h,
                        2f, 0.16f * (1f - ramp(t, shatterAt(), 1.4f)) * fade, 0x9FDCFF);
            }
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    private void fernEdge(GuiGraphics g, int w, int h, float creep, float fade, int edge, int color) {
        int count = 5;
        for (int i = 0; i < count; i++) {
            float u = (i + 0.5f) / count;
            float x;
            float y;
            float ang;
            float maxLen = Math.min(w, h) * 0.22f;
            switch (edge) {
                case 0 -> {
                    x = u * w;
                    y = -4f;
                    ang = 1.5f + rnd(-0.25f, 0.25f);
                }
                case 1 -> {
                    x = w + 4f;
                    y = u * h;
                    ang = 3.1f + rnd(-0.25f, 0.25f);
                }
                case 2 -> {
                    x = u * w;
                    y = h + 4f;
                    ang = -1.5f + rnd(-0.25f, 0.25f);
                }
                default -> {
                    x = -4f;
                    y = u * h;
                    ang = 0.05f + rnd(-0.25f, 0.25f);
                }
            }
            fern(g, x, y, ang, maxLen * creep, Math.max(1.4f, maxLen * 0.035f), 3,
                    0.55f * fade * Math.min(1f, creep * 1.6f), color);
        }
    }

    private void fern(GuiGraphics g, float x, float y, float ang, float len, float thick,
                      int depth, float alpha, int color) {
        if (depth <= 0 || len < 3f || alpha < 0.01f) {
            return;
        }
        float x2 = x + (float) Math.cos(ang) * len;
        float y2 = y + (float) Math.sin(ang) * len;
        Draw2D.softLine(g, x, y, x2, y2, thick, alpha, color);
        Draw2D.softLine(g, x, y, x2, y2, Math.max(1f, thick * 0.25f), alpha * 0.6f, 0xFFFFFF);
        fern(g, x2, y2, ang - 0.55f, len * 0.58f, thick * 0.6f, depth - 1, alpha * 0.85f, color);
        fern(g, x2, y2, ang + 0.5f, len * 0.55f, thick * 0.55f, depth - 1, alpha * 0.8f, color);
        fern(g, x2, y2, ang + 0.02f, len * 0.62f, thick * 0.5f, depth - 1, alpha * 0.6f, color);
    }

    @Override
    protected boolean drawsBanner() {
        return false;
    }
}
