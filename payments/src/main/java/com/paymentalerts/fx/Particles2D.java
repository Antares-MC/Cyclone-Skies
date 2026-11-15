package com.paymentalerts.fx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.GuiGraphics;

public final class Particles2D {
    private static final int MAX = 4000;
    public final Random random = new Random();
    private final List<P> particles = new ArrayList();

    public enum Kind {
        SPARK,
        SMOKE,
        STREAK,
        CONFETTI
    }

    public static final class P {
        public float x;
        public float y;
        public float vx;
        public float vy;
        public float age;
        public float life;
        public float grow;
        public float gravity;
        public boolean twinkle;
        public float angle;
        public float spin;
        public float size = 1.5f;
        public float drag = 1.5f;
        public float alpha = 1.0f;
        public int rgb = 16777215;
        public int fadeRgb = 16777215;
        public Kind kind = Kind.SPARK;
    }

    public P add(Kind kind, float f, float f2, float f3, float f4, float f5, float f6, int i, int i2) {
        P p = new P();
        p.kind = kind;
        p.x = f;
        p.y = f2;
        p.vx = f3;
        p.vy = f4;
        p.life = f5;
        p.size = f6;
        p.rgb = i;
        p.fadeRgb = i2;
        if (this.particles.size() < MAX) {
            this.particles.add(p);
        }
        return p;
    }

    public float rand(float f, float f2) {
        return f + (this.random.nextFloat() * (f2 - f));
    }

    public boolean isEmpty() {
        return this.particles.isEmpty();
    }

    public void clear() {
        this.particles.clear();
    }

    public java.util.List<P> particlesView() {
        return this.particles;
    }

    public void update(float f) {
        Iterator<P> it = this.particles.iterator();
        while (it.hasNext()) {
            P next = it.next();
            float exp = (float) Math.exp((-next.drag) * f);
            next.vx *= exp;
            next.vy = (next.vy * exp) + (next.gravity * f);
            next.x += next.vx * f;
            next.y += next.vy * f;
            next.age += f;
            next.angle += next.spin * f;
            if (next.age >= next.life) {
                it.remove();
            }
        }
    }

    public void render(GuiGraphics guiGraphics, float f) {
        for (P p : this.particles) {
            float f2 = p.age / p.life;
            float f3 = (f2 < 0.6f ? 1.0f : 1.0f - ((f2 - 0.6f) / 0.4f)) * p.alpha * f;
            if (p.twinkle && f2 > 0.25f && this.random.nextFloat() < 0.5f) {
                f3 *= 0.2f;
            }
            if (f3 > 0.004f) {
                int lerpRgb = Draw2D.lerpRgb(p.rgb, p.fadeRgb, f2);
                switch (p.kind.ordinal()) {
                    case HudFireworks.CLASSIC /* 0 */:
                        Draw2D.glow(guiGraphics, p.x, p.y, p.size * 3.0f, f3 * 0.3f, lerpRgb);
                        Draw2D.circle(guiGraphics, p.x, p.y, p.size, Draw2D.argb(f3, Draw2D.lerpRgb(lerpRgb, 16777215, 0.35f)), Draw2D.argb(f3 * 0.6f, lerpRgb));
                        break;
                    case HudFireworks.GOLD /* 1 */:
                        Draw2D.glow(guiGraphics, p.x, p.y, p.size + (p.grow * p.age), p.alpha * f * (f2 < 0.15f ? f2 / 0.15f : 1.0f - ((f2 - 0.15f) / 0.85f)), lerpRgb);
                        break;
                    case HudFireworks.RAINBOW_FINALE /* 2 */:
                        Draw2D.streak(guiGraphics, p.x - (p.vx * 0.06f), p.y - (p.vy * 0.06f), p.x, p.y, p.size * 2.0f, f3, lerpRgb);
                        break;
                    case 3:
                        float abs = Math.abs((float) Math.cos(p.angle * 1.7f));
                        Draw2D.rotatedRect(guiGraphics, p.x, p.y, p.size * 2.2f, p.size * (0.3f + (1.1f * abs)), p.angle, Draw2D.argb(f3, Draw2D.scaleRgb(p.rgb, 0.75f + (0.35f * abs))));
                        break;
                }
            }
        }
    }
}
