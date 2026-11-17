package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.SoundCues;
import com.paymentalerts.theme.Theme;
import com.paymentalerts.theme.ThemeService;
import java.util.Random;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Plumbing shared by the hand-animated stage alerts.
 *
 * <p>This is deliberately only plumbing: a clock, a particle buffer, a sound scheduler and
 * a few draw primitives. Every subclass owns its own timeline, geometry, motion and palette
 * work in {@link #paint}; there is no shared "burst + emitters + banner" recipe left.
 *
 * <p>Subclasses implement:
 * <ul>
 *   <li>{@link #duration()} — total runtime in seconds including its own tail,</li>
 *   <li>{@link #paint} — the whole animation for a frame,</li>
 *   <li>optionally {@link #cue()} for audio and {@link #advance} for per-frame spawns,</li>
 *   <li>optionally {@link #drawsBanner()} false when the effect draws its own text.</li>
 * </ul>
 */
abstract class StageBase implements StageAlert {
    protected final Payment payment;
    protected final long startNanos;
    protected final int variation;
    protected final Particles2D fx = new Particles2D();
    protected final Random rng = new Random();
    protected final SoundCues cues = new SoundCues();

    protected int w = 1920;
    protected int h = 1080;
    protected float dt = 1f / 60f;
    protected Theme theme = ThemeService.current();

    private long lastNanos;
    private boolean primed;

    protected StageBase(Payment payment, long now, int variation) {
        this.payment = payment;
        this.startNanos = now;
        this.lastNanos = now;
        this.variation = variation;
    }

    // ---------- contract ----------

    /** Total runtime, in seconds, including the fade-out tail. */
    protected abstract float duration();

    /** The looks this effect offers; also feeds the settings picker. */
    protected String[] looks() {
        return new String[]{"Default"};
    }

    /** Schedule sounds and one-shot impulses. Runs once, before the first tick. */
    protected void cue() {
    }

    /** Per-frame simulation. Called after {@link #cue()} on the first tick. */
    protected void advance(float t) {
    }

    /** Draw one frame of the effect. The banner is drawn by the base afterwards. */
    protected abstract void paint(GuiGraphics g, Font font, int w, int h, float t, float partial);

    /** True when the effect draws its own text and does not want the standard banner. */
    protected boolean drawsBanner() {
        return true;
    }

    protected float bannerY() {
        return 0.26f;
    }

    protected float bannerAt() {
        return 0.3f;
    }

    protected String amountPrefix() {
        return "paid you ";
    }

    protected String tagline() {
        return null;
    }

    // ---------- clock ----------

    protected final float elapsed(long now) {
        return (now - this.startNanos) / 1.0E9f;
    }

    protected final int look() {
        return Variations.pick(this.variation, Math.max(1, looks().length));
    }

    protected final float ramp(float t, float at, float len) {
        return Easing.clamp01((t - at) / Math.max(0.0001f, len));
    }

    protected final float rampDown(float t, float at, float len) {
        return 1f - ramp(t, at, len);
    }

    /** 1 until {@code runtime - len}, then linear to 0. */
    protected final float tail(float t, float len) {
        return Easing.clamp01((duration() - t) / Math.max(0.0001f, len));
    }

    protected final float wave(float t, float speed, float phase) {
        return (float) Math.sin(t * speed + phase);
    }

    protected final float rnd(float a, float b) {
        return this.fx.rand(a, b);
    }

    protected final boolean chance(float p) {
        return this.rng.nextFloat() < p;
    }

    // ---------- frame ----------

    @Override
    public void tick(long now) {
        prime();
        float t = elapsed(now);
        float step = Math.max(0f, Math.min(0.1f, (now - this.lastNanos) / 1.0E9f));
        this.lastNanos = now;
        if (step > 0f) {
            this.dt = step;
        }
        this.theme = ThemeService.current();
        this.cues.update(t);
        advance(t);
        this.fx.update(step);
    }

    @Override
    public void render(GuiGraphics g, Font font, int w, int h, float partial, long now) {
        prime();
        this.w = w;
        this.h = h;
        this.theme = ThemeService.current();
        float t = elapsed(now);
        paint(g, font, w, h, t, partial);
        if (drawsBanner()) {
            banner(g, font, w, h, t);
        }
    }

    @Override
    public boolean isFinished(long now) {
        return elapsed(now) >= duration() + 0.4f;
    }

    private void prime() {
        if (this.primed) {
            return;
        }
        this.primed = true;
        this.theme = ThemeService.current();
        cue();
    }

    // ---------- standard banner ----------

    protected void banner(GuiGraphics g, Font font, int w, int h, float t) {
        float in = Easing.outBack(ramp(t, bannerAt(), 0.5f));
        float alpha = in * tail(t, 0.75f);
        if (alpha <= 0.02f) {
            return;
        }
        float y = h * bannerY();
        float cx = w * 0.5f;
        MutableComponent title = TextFx.colored(this.payment.name(), true, i -> {
            float mix = 0.5f + 0.5f * (float) Math.sin(t * 6f + i * 0.4f);
            return Theme.mixRgb(this.theme.primary, this.theme.accent, mix);
        });
        float scale = TextFx.fitScale(font, title, w * 0.86f, 3.8f) * in;
        TextFx.centered(g, font, title, cx, y, scale, alpha);

        Component sub = TextFx.plain(amountPrefix() + this.payment.displayAmount(), this.theme.secondary, true);
        float subScale = TextFx.fitScale(font, sub, w * 0.7f, 1.8f) * in;
        TextFx.centered(g, font, sub, cx, y + 30f * scale, subScale, alpha * 0.95f);

        String tag = tagline();
        if (tag != null && !tag.isEmpty()) {
            Component line = TextFx.plain(tag, this.theme.glow, false);
            TextFx.centered(g, font, line, cx, y + 56f * scale,
                    TextFx.fitScale(font, line, w * 0.55f, 1.2f), alpha * 0.85f);
        }
    }

    /** Utility for effects that place their own name/amount, usually in a custom spot. */
    protected final void name(GuiGraphics g, Font font, float cx, float y, float maxWidth, float maxSize,
                              float t, float alpha) {
        if (alpha <= 0.02f) {
            return;
        }
        Component text = TextFx.plain(this.payment.name(), 0xFFFFFF, true);
        TextFx.centered(g, font, text, cx, y, TextFx.fitScale(font, text, maxWidth, maxSize), alpha);
    }

    protected final void amount(GuiGraphics g, Font font, float cx, float y, float maxWidth, float maxSize,
                                int rgb, float alpha) {
        if (alpha <= 0.02f) {
            return;
        }
        Component text = TextFx.plain(amountPrefix() + this.payment.displayAmount(), rgb, true);
        TextFx.centered(g, font, text, cx, y, TextFx.fitScale(font, text, maxWidth, maxSize), alpha);
    }

    // ---------- shared primitives ----------

    protected final Particles2D.P spawn(Particles2D.Kind kind, float x, float y, float vx, float vy,
                                        float life, float size, int rgb, int fadeRgb) {
        return this.fx.add(kind, x, y, vx, vy, life, size, rgb, fadeRgb);
    }

    protected final void fadeParticles(float alpha) {
        if (alpha >= 0.999f) {
            return;
        }
        for (Particles2D.P p : this.fx.particlesView()) {
            p.alpha = Math.min(p.alpha, alpha);
        }
    }

    /** A coin, foreshortened by spin. */
    protected final void coin(GuiGraphics g, float cx, float cy, float r, float spin, float alpha, int rgb) {
        float squash = 0.18f + 0.82f * Math.abs((float) Math.cos(spin));
        int face = Draw2D.lerpRgb(rgb, 0xFFFFFF, 0.22f);
        int rim = Draw2D.scaleRgb(rgb, 0.62f);
        Draw2D.ellipse(g, cx, cy, Math.max(0.8f, r * squash), r, Draw2D.argb(alpha * 0.5f, rim), 0);
        Draw2D.ellipse(g, cx, cy, Math.max(0.6f, r * squash * 0.82f), r * 0.82f,
                Draw2D.argb(alpha, face), Draw2D.argb(alpha, rgb));
        if (squash > 0.45f) {
            Draw2D.glow(g, cx - r * 0.25f, cy - r * 0.3f, r * 0.55f, alpha * 0.35f, 0xFFFFFF);
        }
    }

    /** A banknote tumbling in 2D. */
    protected final void bill(GuiGraphics g, float cx, float cy, float size, float angle, float alpha, int rgb) {
        float wdt = size * (0.55f + 0.45f * Math.abs((float) Math.cos(angle * 1.7f)));
        Draw2D.rotatedRect(g, cx, cy, wdt, size * 0.5f, angle, Draw2D.argb(alpha, rgb));
        Draw2D.rotatedRect(g, cx, cy, wdt * 0.72f, size * 0.26f, angle,
                Draw2D.argb(alpha * 0.55f, Draw2D.scaleRgb(rgb, 0.55f)));
    }

    /** Four-point star / sparkle. */
    protected final void star4(GuiGraphics g, float cx, float cy, float size, float alpha, int rgb) {
        if (alpha <= 0.01f || size <= 0.2f) {
            return;
        }
        Draw2D.glow(g, cx, cy, size * 1.7f, alpha * 0.28f, rgb);
        int c = Draw2D.argb(alpha, rgb);
        int e = Draw2D.argb(0f, rgb);
        Draw2D.triangle(g, cx, cy - size, c, cx - size * 0.16f, cy, e, cx + size * 0.16f, cy, e);
        Draw2D.triangle(g, cx, cy + size, c, cx - size * 0.16f, cy, e, cx + size * 0.16f, cy, e);
        Draw2D.triangle(g, cx - size, cy, c, cx, cy - size * 0.16f, e, cx, cy + size * 0.16f, e);
        Draw2D.triangle(g, cx + size, cy, c, cx, cy - size * 0.16f, e, cx, cy + size * 0.16f, e);
    }

    /** Two-lobed heart. */
    protected final void heart(GuiGraphics g, float cx, float cy, float r, float alpha, int rgb) {
        if (alpha <= 0.01f) {
            return;
        }
        int c = Draw2D.argb(alpha, rgb);
        Draw2D.circle(g, cx - r * 0.5f, cy - r * 0.25f, r * 0.55f, c, c);
        Draw2D.circle(g, cx + r * 0.5f, cy - r * 0.25f, r * 0.55f, c, c);
        Draw2D.triangle(g, cx - r * 1.02f, cy - r * 0.1f, c, cx + r * 1.02f, cy - r * 0.1f, c,
                cx, cy + r * 1.05f, c);
    }

    /** Outline of a heart, for shockwaves. */
    protected final void heartRing(GuiGraphics g, float cx, float cy, float r, float thick, float alpha, int rgb) {
        int c = Draw2D.argb(alpha, rgb);
        Draw2D.ring(g, cx - r * 0.52f, cy - r * 0.25f, Math.max(0.8f, r * 0.56f), thick, alpha, rgb);
        Draw2D.ring(g, cx + r * 0.52f, cy - r * 0.25f, Math.max(0.8f, r * 0.56f), thick, alpha, rgb);
        Draw2D.softLine(g, cx - r * 1.06f, cy - r * 0.06f, cx, cy + r * 1.06f, thick, alpha, c);
        Draw2D.softLine(g, cx + r * 1.06f, cy - r * 0.06f, cx, cy + r * 1.06f, thick, alpha, c);
        Draw2D.softLine(g, cx - r * 1.06f, cy - r * 0.06f, cx - r * 0.52f, cy - r * 0.62f, thick, alpha, c);
        Draw2D.softLine(g, cx + r * 1.06f, cy - r * 0.06f, cx + r * 0.52f, cy - r * 0.62f, thick, alpha, c);
    }

    /** A soft vertical shaft of light. */
    protected final void shaft(GuiGraphics g, float x, float top, float bottom, float topWidth, float bottomWidth,
                               float alpha, int rgb) {
        Draw2D.quad(g, x - topWidth, top, Draw2D.argb(alpha * 0.55f, rgb),
                x + topWidth, top, Draw2D.argb(alpha * 0.55f, rgb),
                x + bottomWidth, bottom, Draw2D.argb(0f, rgb),
                x - bottomWidth, bottom, Draw2D.argb(0f, rgb));
    }

    /** Full-screen wash. */
    protected final void wash(GuiGraphics g, float alpha, int rgb) {
        if (alpha > 0.003f) {
            Draw2D.rect(g, 0f, 0f, this.w, this.h, Draw2D.argb(alpha, rgb));
        }
    }

    /** Horizontal band of light. */
    protected final void band(GuiGraphics g, float y, float height, float alpha, int rgb) {
        Draw2D.gradientV(g, 0f, y, this.w, y + height, Draw2D.argb(0f, rgb), Draw2D.argb(alpha, rgb));
        Draw2D.gradientV(g, 0f, y + height, this.w, y + height * 2f, Draw2D.argb(alpha, rgb), Draw2D.argb(0f, rgb));
    }
}
