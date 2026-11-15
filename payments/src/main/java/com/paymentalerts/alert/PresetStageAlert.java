package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.HudFireworks;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.sound.AlertSounds;
import com.paymentalerts.theme.Theme;
import com.paymentalerts.theme.ThemeService;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Parametric stage alert driven by {@link EffectStyle}.
 */
final class PresetStageAlert implements StageAlert {
    private final Payment payment;
    private final long startNanos;
    private final EffectStyle style;
    private final int variation;
    private final Particles2D particles = new Particles2D();
    private final HudFireworks fireworks;
    private boolean burstDone;
    private boolean soundDone;
    private long lastNanos;
    private int screenW = 1920;
    private int screenH = 1080;

    PresetStageAlert(Payment payment, long startNanos, EffectStyle style, int variation) {
        this.payment = payment;
        this.startNanos = startNanos;
        this.style = style;
        this.variation = Variations.pick(variation, Math.max(1, style.variationNames.length));
        this.lastNanos = startNanos;
        this.fireworks = style.fireworks
                ? new HudFireworks(style.rocketCount, this.variation % 4)
                : null;
        if (style.shake > 0f) {
            ScreenShake.add(0f, style.shakeDuration, style.shake);
        }
    }

    private float elapsed(long now) {
        return (now - startNanos) / 1.0E9f;
    }

    @Override
    public void tick(long now) {
        float dt = Math.min(0.05f, (now - lastNanos) / 1.0E9f);
        lastNanos = now;
        float t = elapsed(now);
        Theme theme = ThemeService.current();

        if (!soundDone && t >= 0.05f) {
            soundDone = true;
            if (style.soundKey != null) {
                AlertSounds.play(style.soundKey, 1.0f, 1.0f);
            }
        }

        if (fireworks != null) {
            if (t > style.duration * 0.65f) {
                fireworks.stopLaunching();
            }
            fireworks.update(dt, screenW, screenH);
        }

        if (!burstDone && t >= style.burstAt) {
            burstDone = true;
            spawnBurst(theme);
        }

        if (t < style.duration * 0.85f && style.particleRate > 0) {
            int n = Math.max(1, (int) (style.particleRate * dt * 60f));
            for (int i = 0; i < n; i++) {
                float x = particles.rand(0.05f, 0.95f) * screenW;
                float y = style.particlesFromTop
                        ? particles.rand(-20f, screenH * 0.15f)
                        : particles.rand(screenH * 0.35f, screenH * 0.95f);
                float vx = particles.rand(-80f, 80f) * style.particleSpread;
                float vy = style.particlesFromTop
                        ? particles.rand(40f, 180f)
                        : particles.rand(-220f, -40f);
                int rgb = pickColor(theme);
                Particles2D.Kind kind = style.confetti
                        ? Particles2D.Kind.CONFETTI
                        : (style.smoke ? Particles2D.Kind.SMOKE : Particles2D.Kind.SPARK);
                Particles2D.P p = particles.add(kind, x, y, vx, vy,
                        particles.rand(0.6f, style.particleLife),
                        particles.rand(1.2f, 3.5f) * style.particleSize,
                        rgb, theme.secondary);
                p.gravity = style.particleGravity;
                p.drag = style.particleDrag;
                p.twinkle = style.twinkle;
            }
        }

        particles.update(dt);
    }

    private void spawnBurst(Theme theme) {
        float cx = screenW * 0.5f;
        float cy = screenH * 0.45f;
        for (int i = 0; i < style.burstCount; i++) {
            float ang = (float) (i * (Math.PI * 2.0 / style.burstCount) + particles.random.nextFloat() * 0.3f);
            float speed = particles.rand(120f, 420f) * style.burstPower;
            float vx = (float) Math.cos(ang) * speed;
            float vy = (float) Math.sin(ang) * speed;
            int rgb = pickColor(theme);
            Particles2D.P p = particles.add(
                    style.confetti ? Particles2D.Kind.CONFETTI : Particles2D.Kind.SPARK,
                    cx, cy, vx, vy,
                    particles.rand(0.8f, 1.8f),
                    particles.rand(1.5f, 4f) * style.particleSize,
                    rgb, theme.glow);
            p.gravity = style.particleGravity * 0.6f;
            p.twinkle = true;
        }
        if (style.shake > 0f) {
            ScreenShake.add(0f, 0.25f, style.shake * 0.7f);
        }
    }

    private int pickColor(Theme theme) {
        return switch (particles.random.nextInt(4)) {
            case 0 -> theme.primary;
            case 1 -> theme.secondary;
            case 2 -> theme.accent;
            default -> theme.glow;
        };
    }

    @Override
    public boolean isFinished(long now) {
        float t = elapsed(now);
        if (t < style.duration) return false;
        if (fireworks != null && !fireworks.isFinished()) return false;
        return particles.isEmpty() || t > style.duration + 2.5f;
    }

    @Override
    public void render(GuiGraphics g, Font font, int w, int h, float partial, long now) {
        this.screenW = w;
        this.screenH = h;
        float t = elapsed(now);
        Theme theme = ThemeService.current();

        if (style.flash && t < 0.35f) {
            float a = (1f - t / 0.35f) * 0.55f;
            Draw2D.fill(g, 0, 0, w, h, Draw2D.argb(a, theme.glow));
        }
        if (style.darken > 0f) {
            float a = style.darken * Math.min(1f, t * 2f);
            if (t > style.duration - 0.5f) {
                a *= Math.max(0f, (style.duration - t) * 2f);
            }
            if (a > 0.02f) {
                Draw2D.fill(g, 0, 0, w, h, Draw2D.argb(a, 0x000000));
            }
        }

        if (fireworks != null) {
            fireworks.update(0f, w, h);
            fireworks.render(g);
        }

        particles.render(g, 1f);

        float appear = Easing.outBack(Math.min(1f, t / 0.45f));
        float fade = t > style.duration - 0.7f
                ? Math.max(0f, (style.duration - t) / 0.7f)
                : 1f;
        float alpha = appear * fade;
        if (alpha > 0.02f) {
            float y = h * style.bannerY;
            MutableComponent title = TextFx.colored(payment.name(), true, i -> {
                float wave = (float) Math.sin(t * 6f + i * 0.4f) * 0.5f + 0.5f;
                return Theme.mixRgb(theme.primary, theme.accent, wave);
            });
            float scale = TextFx.fitScale(font, title, w * 0.86f, 3.8f) * appear;
            TextFx.centered(g, font, title, w * 0.5f, y, scale, alpha);

            Component sub = TextFx.plain(style.subtitlePrefix + payment.displayAmount(), theme.secondary, true);
            float subScale = TextFx.fitScale(font, sub, w * 0.7f, 1.8f) * appear;
            TextFx.centered(g, font, sub, w * 0.5f, y + 28f * scale, subScale, alpha * 0.95f);

            if (style.tagline != null && !style.tagline.isEmpty()) {
                Component tag = TextFx.plain(style.tagline, theme.glow, false);
                TextFx.centered(g, font, tag, w * 0.5f, y + 52f * scale,
                        TextFx.fitScale(font, tag, w * 0.55f, 1.2f), alpha * 0.85f);
            }
        }
    }
}
