package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Portal rift: a vertical eye tears open in the air. Everything nearby is drawn warped
 * toward it, the payer's silhouette steps through the pupil, then the lid snaps shut and
 * throws one last ring outward.
 *
 * <p>No radial burst anywhere: the motion is an inward pull, a hold, and a closure.
 */
final class PortalRiftAlert extends StageBase {
    static final String[] VARIATIONS = {"Violet", "Void", "Prismatic"};

    private static final float TEAR = 1.1f;
    private static final float ARRIVE = 2.7f;
    private static final float SHUT = 4.6f;
    private static final float END = 6.9f;

    private static final int SPARKS = 90;

    private final float[] moteAngle = new float[SPARKS];
    private final float[] moteRadius = new float[SPARKS];
    private final float[] moteSpeed = new float[SPARKS];
    private final float[] moteSize = new float[SPARKS];
    private final float[] lidJag = new float[15];
    private float pullTimer;

    PortalRiftAlert(Payment payment, long now, int variation) {
        super(payment, now, variation);
        for (int i = 0; i < SPARKS; i++) {
            this.moteAngle[i] = rnd(0f, 6.283f);
            this.moteRadius[i] = rnd(0.25f, 1.15f);
            this.moteSpeed[i] = rnd(0.25f, 1.1f);
            this.moteSize[i] = rnd(1f, 3.4f);
        }
        for (int i = 0; i < this.lidJag.length; i++) {
            this.lidJag[i] = rnd(-1f, 1f);
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

    private int rim() {
        return switch (look()) {
            case 1 -> 0x6A20A8;
            case 2 -> Draw2D.hsv(0.62f, 0.45f, 1f);
            default -> 0x9B5BFF;
        };
    }

    private int core() {
        return switch (look()) {
            case 1 -> 0x0A0014;
            case 2 -> Draw2D.hsv(0.72f, 0.7f, 0.9f);
            default -> 0xC8A0FF;
        };
    }

    @Override
    protected void cue() {
        this.cues.action(0.0f, () -> AlertSounds.playSliding(AlertSounds.BLACKHOLE_PORTAL,
                        1.6f, 0.8f, 1.6f, 0.2f, 0.9f))
                .sound(0.15f, AlertSounds.CHEST_ENDER_OPEN, 0.8f, 1.1f)
                .sound(TEAR - 0.05f, AlertSounds.BLACKHOLE_CHARGE, 0.7f, 1.2f)
                .sound(TEAR, AlertSounds.BLACKHOLE_PORTAL, 0.8f, 0.9f)
                .sound(ARRIVE - 0.4f, AlertSounds.BLACKHOLE_CHARGE, 0.5f, 1.4f)
                .sound(ARRIVE, AlertSounds.GOD_TOTEM, 0.9f, 1.0f)
                .sound(ARRIVE, AlertSounds.BLACKHOLE_SPARKLE, 0.6f, 1.3f)
                .sound(SHUT, AlertSounds.BLACKHOLE_COLLAPSE, 0.9f, 1.1f)
                .sound(SHUT + 0.08f, AlertSounds.NUKE_SHOCKWAVE, 0.6f, 1.3f)
                .sound(SHUT + 0.9f, AlertSounds.BLACKHOLE_SPARKLE, 0.4f, 0.8f);
        ScreenShake.add(SHUT, 0.4f, 0.5f);
    }

    @Override
    protected void advance(float t) {
        if (t < TEAR || t > SHUT + 0.6f) {
            return;
        }
        this.pullTimer += this.dt;
        float rate = t < ARRIVE ? 0.03f : 0.06f;
        while (this.pullTimer > rate) {
            this.pullTimer -= rate;
            float ang = rnd(0f, 6.283f);
            float start = Math.min(this.w, this.h) * rnd(0.42f, 0.62f);
            Particles2D.P p = spawn(Particles2D.Kind.SPARK,
                    this.w * 0.5f + (float) Math.cos(ang) * start,
                    this.h * 0.46f + (float) Math.sin(ang) * start * 0.85f,
                    -(float) Math.cos(ang) * start * 1.6f,
                    -(float) Math.sin(ang) * start * 1.4f,
                    rnd(0.5f, 1.1f), rnd(1f, 2.6f), rim(), core());
            p.drag = 0.1f;
            p.gravity = 0f;
            p.twinkle = true;
        }
    }

    /** Radial squeeze applied to anything drawn near the rift. */
    private float warp(float radius, float t) {
        float strength = ramp(t, 0.2f, TEAR) * (1f - ramp(t, SHUT - 0.3f, 0.5f));
        return radius * (1f - 0.16f * strength);
    }

    private float openAmount(float t) {
        float tear = Easing.outCubic(ramp(t, 0.2f, TEAR));
        float shut = Easing.inCubic(ramp(t, SHUT - 0.55f, 0.55f));
        return Math.max(0f, tear * (1f - shut));
    }

    @Override
    protected void paint(GuiGraphics g, Font font, int w, int h, float t, float partial) {
        float fade = tail(t, 0.85f);
        float cx = w * 0.5f;
        float cy = h * 0.46f;
        float unit = Math.min(w, h);
        int rim = rim();
        int core = core();
        float open = openAmount(t);

        Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(0.55f * fade * ramp(t, 0f, 0.8f), 0x07040E));

        // Pulled-in background strata: concentric arcs that compress toward the eye.
        float pull = ramp(t, 0.1f, TEAR) * (1f - ramp(t, SHUT, 0.9f));
        if (pull > 0.01f) {
            for (int i = 0; i < 7; i++) {
                float base = unit * (0.16f + i * 0.075f);
                float r = warp(base * (1f + 0.03f * wave(t, 1.6f, i * 2.1f)), t);
                Draw2D.ringArc(g, cx, cy, r, r * 0.86f, Math.max(1f, unit * 0.0016f),
                        0.10f * pull * fade, Draw2D.lerpRgb(rim, 0xFFFFFF, 0.15f),
                        0.4f + wave(t, 0.5f, i) * 0.5f, 2.6f + wave(t, 0.4f, i * 1.7f));
            }
        }

        // The eye itself: two lids meeting at a horizontal seam, plus a bright rim.
        if (open > 0.004f) {
            float rx = unit * 0.30f * open;
            float ry = unit * 0.16f * open;
            // Sclera / void body built from stacked chords so the shape is an eye, not an oval.
            int chords = 26;
            for (int i = 0; i < chords; i++) {
                float u = -1f + 2f * (i / (float) (chords - 1));
                float half = (float) Math.sqrt(Math.max(0f, 1f - u * u)) * ry;
                float x = cx + u * rx;
                float y = cy + this.lidJag[i % this.lidJag.length] * half * 0.16f;
                float a = 0.85f * open * fade;
                Draw2D.softLine(g, x, y - half, x, y + half,
                        Math.max(1.5f, rx * 0.12f), a, core);
                Draw2D.softLine(g, x, y - half, x, y + half,
                        Math.max(1f, rx * 0.035f), a * 0.7f, rim);
            }

            // Lid edges: upper and lower curves with jagged tears.
            float prevX = cx - rx;
            float prevTop = cy;
            float prevBot = cy;
            for (int i = 1; i <= 24; i++) {
                float u = -1f + 2f * (i / 24f);
                float half = (float) Math.sqrt(Math.max(0f, 1f - u * u)) * ry;
                float x = cx + u * rx;
                float jag = this.lidJag[i % this.lidJag.length] * half * 0.18f;
                float top = cy - half * (1f + 0.06f * wave(t, 3.4f, i * 0.6f)) + jag;
                float bot = cy + half * (1f + 0.06f * wave(t, 3.1f, i * 0.7f)) - jag;
                Draw2D.softLine(g, prevX, prevTop, x, top, Math.max(1.5f, ry * 0.09f),
                        0.9f * open * fade, Draw2D.lerpRgb(rim, 0xFFFFFF, 0.45f));
                Draw2D.softLine(g, prevX, prevTop, x, top, ry * 0.42f,
                        0.18f * open * fade, rim);
                Draw2D.softLine(g, prevX, prevBot, x, bot, Math.max(1.5f, ry * 0.09f),
                        0.9f * open * fade, Draw2D.lerpRgb(rim, 0xFFFFFF, 0.45f));
                Draw2D.softLine(g, prevX, prevBot, x, bot, ry * 0.42f,
                        0.18f * open * fade, rim);
                prevX = x;
                prevTop = top;
                prevBot = bot;
            }

            // Pupil and its bloom.
            float pr = ry * 0.78f;
            Draw2D.glow(g, cx, cy, pr * 2.4f, 0.30f * open * fade, rim);
            Draw2D.ellipse(g, cx, cy, pr * 0.62f, pr, Draw2D.argb(0.95f * open * fade, 0x000000),
                    Draw2D.argb(0.75f * open * fade, rim));
            Draw2D.ellipse(g, cx, cy, pr * 0.30f, pr * 0.5f,
                    Draw2D.argb(0.55f * open * fade, Draw2D.lerpRgb(rim, 0xFFFFFF, 0.6f)),
                    Draw2D.argb(0f, rim));

            // Horizon line of the far side, glimpsed through the pupil.
            float slice = open * (1f - ramp(t, SHUT - 0.2f, 0.3f));
            Draw2D.softLine(g, cx - pr * 0.55f, cy + pr * 0.16f, cx + pr * 0.55f, cy + pr * 0.16f,
                    Math.max(1f, pr * 0.06f), 0.5f * slice * fade, 0xFFFFFF);
        }

        // Silhouette stepping through.
        float body = ramp(t, ARRIVE - 0.6f, 0.8f) * (1f - ramp(t, SHUT, 0.5f));
        if (body > 0.01f) {
            float step = Easing.outCubic(ramp(t, ARRIVE - 0.6f, 1.4f));
            float sy = cy + unit * 0.03f - step * unit * 0.02f;
            float scale = unit * 0.0016f;
            float sink = ramp(t, SHUT - 0.7f, 0.7f);
            float alpha = body * fade * (1f - sink);
            int dark = 0x06030C;
            // Head, shoulders, torso: a backlit figure only.
            float headR = 26f * scale * (1f - sink * 0.2f);
            Draw2D.circle(g, cx, sy - 78f * scale, headR, Draw2D.argb(alpha, dark), Draw2D.argb(alpha, dark));
            Draw2D.quad(g, cx - 62f * scale, sy + 40f * scale, Draw2D.argb(alpha, dark),
                    cx - 40f * scale, sy - 54f * scale, Draw2D.argb(alpha, dark),
                    cx + 40f * scale, sy - 54f * scale, Draw2D.argb(alpha, dark),
                    cx + 62f * scale, sy + 40f * scale, Draw2D.argb(alpha, dark));
            // Rim light traced down the silhouette.
            Draw2D.softLine(g, cx - 40f * scale, sy - 54f * scale, cx - 62f * scale, sy + 40f * scale,
                    Math.max(1f, 3f * scale + 1f), alpha * 0.8f, Draw2D.lerpRgb(rim, 0xFFFFFF, 0.6f));
            Draw2D.softLine(g, cx + 40f * scale, sy - 54f * scale, cx + 62f * scale, sy + 40f * scale,
                    Math.max(1f, 3f * scale + 1f), alpha * 0.8f, Draw2D.lerpRgb(rim, 0xFFFFFF, 0.6f));
            Draw2D.ringArc(g, cx, sy - 78f * scale, headR, headR, Math.max(1f, 2f * scale + 1f),
                    alpha * 0.8f, 0xFFFFFF, -1.2f, 1.2f);
            Draw2D.glow(g, cx, sy - 20f * scale, unit * 0.16f, 0.16f * alpha, rim);
        }

        // Inbound motes, drawn along their spiral.
        for (int i = 0; i < SPARKS; i++) {
            float phase = this.moteAngle[i] + t * this.moteSpeed[i] * 1.6f;
            float r = warp(unit * this.moteRadius[i] * (1f - 0.55f * ramp(t, TEAR, 3.2f)), t);
            if (r < unit * 0.02f) {
                continue;
            }
            float x = cx + (float) Math.cos(phase) * r;
            float y = cy + (float) Math.sin(phase) * r * 0.86f;
            float a = 0.5f * fade * open;
            star4(g, x, y, this.moteSize[i] * (unit / 1080f + 0.3f), a, Draw2D.lerpRgb(rim, 0xFFFFFF, 0.5f));
        }

        // Closure: the lid seam collapses to a line, then one shockwave.
        if (t >= SHUT - 0.6f) {
            float seamed = ramp(t, SHUT - 0.6f, 0.6f);
            float width = unit * 0.34f * (1f - seamed);
            if (width > 1f) {
                Draw2D.gradientH(g, cx - width, cy - unit * 0.004f, cx + width, cy + unit * 0.004f,
                        Draw2D.argb(0f, 0xFFFFFF), Draw2D.argb(seamed * fade, 0xFFFFFF));
                Draw2D.gradientH(g, cx - width, cy - unit * 0.004f, cx + width, cy + unit * 0.004f,
                        Draw2D.argb(seamed * fade, 0xFFFFFF), Draw2D.argb(0f, 0xFFFFFF));
            }
            float p = ramp(t, SHUT, 1.4f);
            if (p > 0f && p < 1f) {
                float r = unit * (0.04f + Easing.outCubic(p) * 0.75f);
                Draw2D.ringEllipse(g, cx, cy, r, r * 0.86f, Math.max(1.5f, unit * 0.008f * (1f - p)),
                        (1f - p) * 0.5f * fade, 0xFFFFFF);
                Draw2D.ringEllipse(g, cx, cy, r * 0.94f, r * 0.8f, Math.max(1f, unit * 0.004f * (1f - p)),
                        (1f - p) * 0.3f * fade, rim);
            }
            float flash = 1f - ramp(t, SHUT, 0.25f);
            if (flash > 0f) {
                Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(flash * 0.30f * fade, Draw2D.lerpRgb(rim, 0xFFFFFF, 0.7f)));
            }
        }

        this.fx.render(g, 1f);
        fadeParticles(fade);
    }

    @Override
    protected String tagline() {
        return "The rift answers";
    }
}
