package com.paymentalerts.alert;

import com.paymentalerts.fx.Draw2D;
import com.paymentalerts.fx.Particles2D;
import com.paymentalerts.fx.ScreenShake;
import com.paymentalerts.fx.TextFx;
import com.paymentalerts.payment.AmountFormat;
import com.paymentalerts.payment.Payment;
import com.paymentalerts.skin.Dolls;
import com.paymentalerts.skin.SkinDoll;
import com.paymentalerts.skin.SkinFetcher;
import com.paymentalerts.sound.AlertSounds;
import com.paymentalerts.sound.SoundCues;
import java.util.Locale;
import java.util.Random;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * The Lumindev effect: the one that plays when Lumindev pays you.
 *
 * <p>Fifteen seconds in five beats, all of it inside the HUD:
 *
 * <ol>
 *   <li><b>0.0 - 1.05</b> the screen cuts to near-black and a single seam of light
 *       opens down the middle.</li>
 *   <li><b>1.05 - 2.25</b> the seam tears into a rift; the name arrives as a
 *       chromatic-split glitch with aurora ribbons rolling behind it.</li>
 *   <li><b>2.25</b> impact: white flash, expanding shockwave, hard shake.</li>
 *   <li><b>2.25 - 11.2</b> the payer descends in a column of light and dances
 *       above a halo while a helix of coins and embers orbits them and the
 *       amount counts up underneath.</li>
 *   <li><b>11.2 - 15.2</b> everything is pulled back into the seam, the screen
 *       whites out, and a signature line is left behind.</li>
 * </ol>
 *
 * <p>Palette and beat are chosen by {@link #VARIATIONS}, so the same effect can be
 * seen in the mod's Cyclone colours, as an aurora, or as a void rift.
 */
final class LumindevAlert implements StageAlert {
    static final String[] VARIATIONS = {"Cyclone", "Aurora", "Void"};

    // Beats, in seconds from the start.
    private static final float OPEN_END = 1.05f;
    private static final float RIFT_END = 2.25f;
    private static final float IMPACT = RIFT_END;
    private static final float LANDED = 4.4f;
    private static final float SHOW_END = 11.2f;
    private static final float COLLAPSE_END = 13.4f;
    private static final float END = 15.2f;

    // Cyclone violet / blue / cyan, aurora green / mint / gold, void magenta / ice.
    private static final int[][] PALETTES = {
            {0xA77BFF, 0x7C9CFF, 0x5FD8FF, 0x0A0618},
            {0x00F5D4, 0x7CFFB2, 0xFFE29A, 0x04121A},
            {0xBF00FF, 0x5FD8FF, 0xFFFFFF, 0x05000F},
    };
    private static final int GOLD = 0xFFD75E;
    private static final int WHITE = 0xFFFFFF;
    private static final int CONFETTI = 0x7C9CFF;

    private final Payment payment;
    private final long startNanos;
    private final SkinFetcher.Skin skin;
    private final Theme theme;
    private final SoundCues cues = new SoundCues();
    private final Particles2D fx = new Particles2D();
    private final Random random = new Random();
    private final float[] ribbons = new float[18];

    private SkinDoll doll;
    private boolean shook;
    private float sparkTimer;
    private float coinTimer;

    /** One palette, unpacked. */
    private record Theme(int primary, int secondary, int accent, int backdrop) {
    }

    LumindevAlert(Payment payment, long startNanos, int variation) {
        this.payment = payment;
        this.startNanos = startNanos;
        int[] palette = PALETTES[Variations.pick(variation, VARIATIONS.length)];
        this.theme = new Theme(palette[0], palette[1], palette[2], palette[3]);
        this.skin = SkinFetcher.get(payment.lookupName());
        for (int i = 0; i < this.ribbons.length; i++) {
            this.ribbons[i] = this.random.nextFloat();
        }
        this.cues.sound(0.0f, AlertSounds.BLACKHOLE_CHARGE, 0.8f, 0.6f)
                .sound(0.45f, AlertSounds.NOVA_HUM, 1.0f, 0.7f)
                .sound(OPEN_END, AlertSounds.BLACKHOLE_PORTAL, 1.1f, 0.8f)
                .sound(RIFT_END - 0.15f, AlertSounds.NOVA_CHARGE, 1.2f, 1.0f)
                .sound(IMPACT, AlertSounds.NUKE_SHOCKWAVE, 1.3f, 1.0f)
                .sound(IMPACT, AlertSounds.GOD_BELL, 1.2f, 0.8f)
                .sound(IMPACT + 0.35f, AlertSounds.CONFETTI_BOOM, 1.0f, 1.0f)
                .sound(IMPACT + 0.8f, AlertSounds.GOD_CONDUIT, 1.0f, 0.9f)
                .sound(LANDED, AlertSounds.GOD_FANFARE, 1.1f, 1.0f)
                .sound(LANDED + 0.2f, AlertSounds.MONEY_LEVELUP, 0.9f, 0.8f)
                .sound(LANDED + 0.6f, AlertSounds.NOVA_SHIMMER, 1.0f, 0.9f)
                .sound(6.4f, AlertSounds.COIN_CHIME, 1.0f, 0.8f)
                .sound(8.0f, AlertSounds.NOVA_SHIMMER, 0.9f, 0.7f)
                .sound(9.6f, AlertSounds.GOD_BELL, 1.1f, 0.6f)
                .sound(SHOW_END, AlertSounds.NOVA_COLLAPSE, 1.2f, 1.0f)
                .sound(COLLAPSE_END - 0.1f, AlertSounds.NUKE_SHOCKWAVE, 0.9f, 0.7f)
                .sound(COLLAPSE_END, AlertSounds.BLACKHOLE_BANG, 1.0f, 0.7f);
    }

    /** Whether a payer name is the one this effect belongs to. */
    static boolean isPayer(String name) {
        if (name == null) {
            return false;
        }
        String flat = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        return flat.equals("lumindev") || flat.equals("lumin") || flat.equals("lumindevmc");
    }

    private float elapsed(long now) {
        return (now - this.startNanos) / 1.0E9f;
    }

    @Override
    public void tick(long now) {
        this.cues.update(elapsed(now));
    }

    @Override
    public boolean isFinished(long now) {
        return elapsed(now) >= END && this.cues.isDone();
    }

    @Override
    public void render(GuiGraphics g, Font font, int w, int h, float partial, long now) {
        float t = elapsed(now);
        float cx = w * 0.5f;
        float baseY = h * 0.74f;
        float fade = t > END - 0.9f ? Math.max(0f, (END - t) / 0.9f) : 1f;

        this.fx.update(partial);
        backdrop(g, w, h, t, fade);
        if (t >= IMPACT) {
            shockwave(g, w, h, t);
        }
        aura(g, w, h, t, cx, fade);
        if (t >= IMPACT) {
            rays(g, t, cx, h * 0.14f, h, fade);
            column(g, t, cx, baseY, h, fade);
        }
        embersAndCoins(g, t, w, h, cx, baseY, fade);
        this.fx.render(g, 1.0f);
        doll(g, t, cx, baseY, h, fade);
        title(g, font, w, h, t, cx, fade);
        amount(g, font, w, h, t, cx, baseY, fade);
        whiteout(g, w, h, t);
        Draw2D.flush(g);
    }

    // ---------- beats ----------

    private void backdrop(GuiGraphics g, int w, int h, float t, float fade) {
        float a = Math.min(1f, t / 0.55f) * 0.94f * fade;
        if (a <= 0.01f) {
            return;
        }
        Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(a, this.theme.backdrop()));
        Draw2D.vignette(g, w, h, 0.35f, 0.5f * a, this.theme.primary());
        // The seam: two mirrored gradients that slide open.
        float open = Easing.outCubic(Math.min(1f, t / OPEN_END));
        float half = Math.max(1.5f, open * w * 0.5f);
        float mid = w * 0.5f;
        Draw2D.gradientH(g, mid - half, 0f, mid, h * 0.92f,
                Draw2D.argb(0f, this.theme.accent()), Draw2D.argb(0.5f * fade, this.theme.accent()));
        Draw2D.gradientH(g, mid, 0f, mid + half, h * 0.92f,
                Draw2D.argb(0.5f * fade, this.theme.accent()), Draw2D.argb(0f, this.theme.accent()));
        if (t < OPEN_END) {
            float core = Math.max(1f, 2.2f * (1f - open) + 0.6f);
            Draw2D.rect(g, mid - core, 0f, mid + core, h, Draw2D.argb(0.85f * fade, WHITE));
        }
        // Aurora ribbons roll across the top half behind everything else.
        for (int i = 0; i < this.ribbons.length; i++) {
            float seed = this.ribbons[i];
            float y = h * (0.08f + 0.34f * seed)
                    + (float) Math.sin(t * (0.5f + seed * 0.7f) + i) * h * 0.045f;
            float thick = h * (0.012f + 0.02f * seed);
            float shift = (t * (0.06f + seed * 0.09f) + seed) % 1f;
            float x0 = -w * 0.2f + shift * w * 1.4f;
            float x1 = x0 + w * (0.35f + seed * 0.4f);
            int col = i % 2 == 0 ? this.theme.primary() : this.theme.accent();
            Draw2D.gradientH(g, x0, y, x1, y + thick,
                    Draw2D.argb(0f, col), Draw2D.argb(0.10f * fade * Math.min(1f, t), col));
            Draw2D.gradientH(g, x1, y, x0, y + thick,
                    Draw2D.argb(0f, col), Draw2D.argb(0f, col));
        }
    }

    /** Two soft radial washes that give the frame depth behind the dancer. */
    private void aura(GuiGraphics g, int w, int h, float t, float cx, float fade) {
        float a = Math.min(1f, t / 1.6f) * fade;
        if (a <= 0.01f) {
            return;
        }
        Draw2D.glow(g, cx, h * 0.46f, h * 0.6f, 0.18f * a, this.theme.primary());
        Draw2D.glow(g, cx, h * 0.2f, h * 0.34f, 0.12f * a, this.theme.secondary());
    }

    private void shockwave(GuiGraphics g, int w, int h, float t) {
        float p = Math.min(1f, (t - IMPACT) / 1.6f);
        if (p >= 1f) {
            return;
        }
        if (!this.shook) {
            this.shook = true;
            ScreenShake.add(0f, 0.55f, 0.9f);
        }
        float cx = w * 0.5f;
        float cy = h * 0.45f;
        float radius = Easing.outCubic(p) * Math.max(w, h) * 0.75f;
        float alpha = (1f - p) * 0.5f;
        if (radius > 2f && alpha > 0.01f) {
            Draw2D.circle(g, cx, cy, radius, Draw2D.argb(alpha * 0.35f, this.theme.accent()),
                    Draw2D.argb(0f, this.theme.accent()));
            // Punch the middle back out to the backdrop so it reads as a ring.
            Draw2D.circle(g, cx, cy, Math.max(1f, radius * 0.78f),
                    Draw2D.argb(1f, this.theme.backdrop()), Draw2D.argb(1f, this.theme.backdrop()));
        }
        if (t < IMPACT + 0.28f) {
            Draw2D.rect(g, 0f, 0f, w, h,
                    Draw2D.argb((1f - (t - IMPACT) / 0.28f) * 0.75f, WHITE));
        }
    }

    private void rays(GuiGraphics g, float t, float cx, float top, int h, float fade) {
        float a = Math.min(1f, Math.max(0f, (t - IMPACT) / 0.8f)) * 0.6f * fade;
        if (a <= 0.01f) {
            return;
        }
        float spin = (float) Math.toRadians(t * 9f);
        float span = (float) Math.toRadians(3.2f);
        float len = h * 1.25f;
        for (int i = 0; i < 22; i++) {
            double ang = spin + i * (Math.PI * 2.0 / 22.0);
            int col = i % 3 == 0 ? this.theme.accent() : this.theme.primary();
            float alpha = (i % 2 == 0 ? 0.16f : 0.09f) * a
                    * (0.85f + 0.15f * (float) Math.sin(t * 2f + i));
            Draw2D.triangle(g, cx, top,
                    Draw2D.argb(alpha, col),
                    cx + (float) Math.cos(ang - span) * len, top + (float) Math.sin(ang - span) * len,
                    Draw2D.argb(0f, col),
                    cx + (float) Math.cos(ang + span) * len, top + (float) Math.sin(ang + span) * len,
                    Draw2D.argb(0f, col));
        }
    }

    private void column(GuiGraphics g, float t, float cx, float baseY, int h, float fade) {
        float a = Math.min(1f, Math.max(0f, (t - IMPACT) / 0.6f)) * fade;
        if (a <= 0.01f) {
            return;
        }
        float width = Math.max(6f, h * 0.05f * (0.85f + 0.15f * (float) Math.sin(t * 2.6f)));
        Draw2D.gradientH(g, cx - width * 2.2f, 0f, cx, baseY,
                Draw2D.argb(0f, this.theme.secondary()), Draw2D.argb(0.28f * a, this.theme.secondary()));
        Draw2D.gradientH(g, cx, 0f, cx + width * 2.2f, baseY,
                Draw2D.argb(0.28f * a, this.theme.secondary()), Draw2D.argb(0f, this.theme.secondary()));
        Draw2D.gradientV(g, cx - width, 0f, cx + width, baseY,
                Draw2D.argb(0.22f * a, WHITE), Draw2D.argb(0.02f * a, this.theme.accent()));
        // Halo under the dancer.
        float halo = Math.min(1f, Math.max(0f, (t - LANDED) / 0.6f)) * a;
        if (halo > 0.01f) {
            Draw2D.ellipse(g, cx, baseY, h * 0.16f, h * 0.035f,
                    Draw2D.argb(0.35f * halo, this.theme.accent()),
                    Draw2D.argb(0f, this.theme.primary()));
            Draw2D.glow(g, cx, baseY - h * 0.02f, h * 0.2f, 0.22f * halo, this.theme.primary());
        }
    }

    private void embersAndCoins(GuiGraphics g, float t, int w, int h, float cx, float baseY, float fade) {
        if (t > IMPACT && t < SHOW_END) {
            this.sparkTimer += 0.016f;
            float rate = t < LANDED ? 0.012f : 0.02f;
            while (this.sparkTimer > rate) {
                this.sparkTimer -= rate;
                float x = cx + this.fx.rand(-1.2f, 1.2f) * w * 0.12f;
                float y = baseY - this.fx.rand(0f, h * 0.5f);
                Particles2D.P p = this.fx.add(Particles2D.Kind.SPARK,
                        x, y, this.fx.rand(-25f, 25f), -this.fx.rand(40f, 140f),
                        this.fx.rand(1.2f, 2.6f), this.fx.rand(0.8f, 2.0f),
                        this.theme.accent(), this.theme.primary());
                p.twinkle = true;
                p.drag = 0.5f;
            }
        }
        if (t > LANDED && t < SHOW_END) {
            this.coinTimer += 0.016f;
            while (this.coinTimer > 0.03f) {
                this.coinTimer -= 0.03f;
                float ang = t * 2.4f + this.fx.rand(-0.3f, 0.3f);
                float radius = w * 0.11f;
                float x = cx + (float) Math.cos(ang) * radius;
                float y = baseY - h * 0.18f + (float) Math.sin(ang) * h * 0.05f;
                Particles2D.P p = this.fx.add(Particles2D.Kind.CONFETTI,
                        x, y, (float) Math.cos(ang + 1.6f) * 60f, (float) Math.sin(ang + 1.6f) * 24f - 10f,
                        this.fx.rand(0.9f, 1.6f), this.fx.rand(2.0f, 3.4f),
                        random.nextBoolean() ? GOLD : this.theme.accent(), WHITE);
                p.drag = 0.8f;
                p.angle = this.fx.rand(0f, 6.28f);
                p.spin = this.fx.rand(-8f, 8f);
            }
            // A slow confetti drift from the top so the frame is never empty.
            if (this.random.nextFloat() < 0.4f) {
                Particles2D.P p = this.fx.add(Particles2D.Kind.CONFETTI,
                        this.fx.rand(0f, w), -6f, this.fx.rand(-12f, 12f), this.fx.rand(20f, 55f),
                        this.fx.rand(3f, 5f), this.fx.rand(1.2f, 2.2f),
                        random.nextBoolean() ? CONFETTI : this.theme.primary(), WHITE);
                p.gravity = h * 0.18f;
                p.twinkle = true;
            }
        }
        if (t > SHOW_END) {
            // Collapse: everything is dragged back toward the seam.
            float pull = Math.min(1f, (t - SHOW_END) / (COLLAPSE_END - SHOW_END));
            for (Particles2D.P p : this.fx.particlesView()) {
                p.vx += (cx - p.x) * 0.06f * pull;
                p.vy += (baseY - p.y) * 0.06f * pull;
            }
        }
        if (fade < 1f) {
            for (Particles2D.P p : this.fx.particlesView()) {
                p.alpha = Math.min(p.alpha, fade);
            }
        }
    }

    private void doll(GuiGraphics g, float t, float cx, float baseY, int h, float fade) {
        if (t < IMPACT) {
            return;
        }
        float land = Easing.outCubic(Math.min(1f, (t - IMPACT) / (LANDED - IMPACT)));
        float y = baseY - (1f - land) * h * 0.55f
                + (float) Math.sin(t * 2.1f) * h * 0.012f;
        int size = Math.max(28, Math.round(h * 0.2f));
        if (this.doll == null) {
            this.doll = Dolls.create(this.skin, this.payment.name());
        }
        if (this.doll != null) {
            this.doll.dance(t - IMPACT);
            boolean crouch = this.doll.isCrouching();
            if (!Dolls.draw(g, this.doll, cx, y, size, cx, y - size * 1.6f)) {
                this.doll = null;
            } else if (crouch) {
                y += size * 0.2f;
            }
        }
        if (this.doll == null) {
            Dolls.face(g, this.skin, Math.round(cx - size * 0.3f),
                    Math.round(y - size * 1.55f), Math.round(size * 0.6f));
        }
        Draw2D.glow(g, cx, y - size * 1.5f, size * 1.1f,
                0.3f * fade * Math.min(1f, (t - IMPACT) / 0.8f), this.theme.primary());
    }

    private void title(GuiGraphics g, Font font, int w, int h, float t, float cx, float fade) {
        if (t < OPEN_END) {
            return;
        }
        float in = Math.min(1f, (t - OPEN_END) / 0.45f);
        float y = h * 0.13f;
        MutableComponent name = TextFx.colored(this.payment.name(), true, i -> {
            float wave = 0.5f + 0.5f * (float) Math.sin(t * 3.4f - i * 0.45f);
            return Draw2D.lerpRgb(this.theme.primary(), this.theme.accent(), wave);
        });
        float scale = TextFx.fitScale(font, name, w * 0.8f, 6.2f) * Easing.outBack(in) * 1.0f;
        // Chromatic split while the rift is open, settling into a clean draw.
        float jitter = (float) Math.max(0f, 1f - (t - OPEN_END) / 1.6f);
        float glitch = jitter * w * 0.006f * (this.random.nextFloat() - 0.5f);
        Draw2D.flush(g);
        TextFx.centered(g, font, TextFx.plain(this.payment.name(), this.theme.accent(), true),
                cx - 3f * jitter + glitch, y, scale, 0.5f * fade * in);
        TextFx.centered(g, font, TextFx.plain(this.payment.name(), 0xFF2E9A, true),
                cx + 3f * jitter - glitch, y, scale, 0.45f * fade * in);
        TextFx.centered(g, font, name, cx, y, scale, fade * in);

        if (t > LANDED) {
            Component line = TextFx.plain("THE DEV DROPPED BY", this.theme.secondary(), true);
            float lineScale = TextFx.fitScale(font, line, w * 0.4f, 1.3f);
            TextFx.centered(g, font, line, cx, y + 52f * scale * 0.55f + 18f, lineScale,
                    fade * Math.min(1f, (t - LANDED) / 0.5f) * 0.9f);
        }
    }

    private void amount(GuiGraphics g, Font font, int w, int h, float t, float cx, float baseY, float fade) {
        if (t < LANDED) {
            return;
        }
        float in = Math.min(1f, (t - LANDED) / 0.5f);
        float y = baseY + h * 0.035f;
        // Odometer: the amount ticks up to the real payment over ~1.6s.
        float p = Math.min(1f, (t - LANDED) / 1.6f);
        double shown = this.payment.amount() * Easing.outCubic(p);
        String text = "paid you " + AmountFormat.compact(shown);
        Component amount = TextFx.plain(text, WHITE, true);
        float scale = TextFx.fitScale(font, amount, w * 0.7f, 3.4f) * Easing.outBack(in);
        TextFx.centered(g, font, amount, cx, y, scale, fade * in);

        if (t > LANDED + 1.8f) {
            Component sig = TextFx.plain("cyclone skies  ·  lumindev", this.theme.accent(), false);
            TextFx.centered(g, font, sig, cx, y + 34f * scale + 14f,
                    TextFx.fitScale(font, sig, w * 0.45f, 1.1f),
                    fade * Math.min(1f, (t - LANDED - 1.8f) / 0.7f) * 0.8f);
        }
        if (t > SHOW_END) {
            float out = Math.min(1f, (t - SHOW_END) / 0.5f);
            Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(out * 0.55f, this.theme.accent()));
        }
    }

    private void whiteout(GuiGraphics g, int w, int h, float t) {
        if (t < COLLAPSE_END - 0.35f) {
            return;
        }
        float p = Math.min(1f, (t - (COLLAPSE_END - 0.35f)) / 0.35f);
        float shrink = 1f - Math.min(1f, (t - COLLAPSE_END) / 0.5f);
        if (t < COLLAPSE_END) {
            Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(p * 0.9f, WHITE));
            return;
        }
        Draw2D.rect(g, 0f, 0f, w, h, Draw2D.argb(shrink * 0.9f, WHITE));
    }
}
