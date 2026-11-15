package dev.valence.sky.classic;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
final class ExtraSkies {
   private ExtraSkies() {
   }

   @Environment(EnvType.CLIENT)
   static final class Amber extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -6.0F, 0.0F, 8.0F, 22.0F, 45.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x140A04, 0x24120A, 0x3C2010, 0x6A3A16, 0x4A2A12, 0x2A180C, 0x160D06, 0x0A0603};
      private final StarField sparkles = new StarField(5301, 160, 12.0F);

      Amber() {
         super("amber", 0x120802, 0xE08A3C);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 300.0F, -3.0F, 18.0F, 0x90C06A24);
         float breath = 0.85F + 0.15F * G.sin(t * 0.22);
         g.ring(SkyTex.NEBULA1, (float)(t * 0.35), 6.0F, 34.0F, 0.0F, 1.0F, G.fade(0x70904A18, breath), 0.2F, 0.4F);
         g.ring(SkyTex.NEBULA2, (float)(-t * 0.24 + 90.0), 14.0F, 52.0F, 0.2F, 1.0F, G.fade(0x50702E10, breath), 0.25F, 0.45F);
         g.cap(SkyTex.NEBULA1, 0.0F, 90.0F, 74.0F, (float)(t * 0.5), 1.3F, 0x80402208, 0.5F);
         g.sprite(SkyTex.GLOW, 40.0F, 16.0F, 70.0F, 0.0F, G.fade(0x40FFB060, breath));
         g.sprite(SkyTex.GLOW, -120.0F, 10.0F, 52.0F, 0.0F, G.fade(0x30FF9A40, breath));
         this.sparkles.drawSparkles(g, c.count(120, c.particles), 0.55F, 0.75F, 0xFFFFC070);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Abyss extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -5.0F, 0.0F, 8.0F, 20.0F, 40.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x02060E, 0x041018, 0x0A2434, 0x11506A, 0x0E3E56, 0x08283A, 0x051520, 0x020A10};
      private final float[] a = new float[3];
      private final float[] b = new float[3];

      Abyss() {
         super("abyss", 0x02080E, 0x40D8E8);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.cap(SkyTex.CAUSTICS, 0.0F, 90.0F, 84.0F, (float)(t * 0.5), 1.2F, 0x5060D0E8, 0.55F);
         g.cap(SkyTex.CAUSTICS, 0.0F, 90.0F, 78.0F, (float)(-t * 0.7 + 30.0), 1.0F, 0x3820A0C0, 0.6F);
         g.horizonGlow(0.0F, 250.0F, -2.0F, 14.0F, 0x4A30C0D0);
         int jelly = c.quality == 0 ? 5 : 9;

         for (int i = 0; i < jelly; i++) {
            float depth = 0.35F + 0.65F * G.rnd(161, i);
            float baseAz = 360.0F * G.rnd(162, i);
            float baseEl = 8.0F + 55.0F * G.rnd(163, i);
            float drift = 0.6F + 0.8F * G.rnd(164, i);
            float az = baseAz + 7.0F * G.sin(t * 0.12 * drift + i * 1.9);
            float el = baseEl + 5.0F * G.sin(t * 0.17 * drift + i * 2.7);
            float size = (2.2F + 3.4F * G.rnd(165, i)) * (0.5F + 0.5F * depth);
            float pulse = 0.65F + 0.35F * G.sin(t * (0.5 + 0.4 * depth) + i);
            int col = G.rnd(166, i) < 0.5F ? 0xFF58D8FF : 0xFFA86BFF;
            g.sprite(SkyTex.GLOW, az, el, size * 3.2F, 0.0F, G.fade(col, 0.16F * pulse * depth));
            g.sprite(SkyTex.GLOW, az, el, size * 1.6F, 0.0F, G.fade(col, 0.38F * pulse * depth));
            g.sprite(SkyTex.DISC, az, el, size, 0.0F, G.fade(col, 0.72F * depth));
            int strands = c.quality == 0 ? 2 : 4;

            for (int k = 0; k < strands; k++) {
               float spread = (k - (strands - 1) / 2.0F) * size * 0.6F;
               float sway = 2.4F * G.sin(t * 0.9 + i * 2.0 + k * 1.3);
               float len = size * (1.8F + 1.6F * G.rnd(167 + k, i));
               G.dir(az + spread, el - 1.0F, this.a);
               G.dir(az + spread * 1.7F + sway, el - len, this.b);
               g.segment(SkyTex.GLOW, this.a, this.b, size * 0.10F, size * 0.02F, G.fade(col, 0.30F * depth * pulse), G.fade(col, 0.0F), false);
               G.dir(az + spread, el - 0.6F, this.a);
               G.dir(az + spread * 1.6F + sway, el - len * 0.88F, this.b);
               g.segment(SkyTex.LINE, this.a, this.b, 0.10F, 0.02F, G.fade(col, 0.75F * depth * pulse), G.fade(col, 0.05F), false);
            }
         }

         int n = c.count(60, c.particles);

         for (int i = 0; i < n; i++) {
            float depth = G.rnd(171, i);
            float sp = 0.015F + 0.03F * depth;
            float q = G.fract(t * sp + G.rnd(172, i));
            float az = 360.0F * G.rnd(173, i) + 2.0F * G.sin(t * 0.7 + i * 2.1);
            float el = -2.0F + q * 80.0F;
            float alpha = G.smooth(0.0F, 0.12F, q) * G.smooth(1.0F, 0.8F, q) * (0.35F + 0.65F * depth);
            g.sprite(SkyTex.BUBBLE, az, el, 0.7F + 1.2F * depth, 0.0F, G.fade(0xFF9FE8FF, alpha));
         }
      }
   }

   @Environment(EnvType.CLIENT)
   static final class BloodMoon extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 5.0F, 14.0F, 32.0F, 60.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x080404, 0x120606, 0x2A0808, 0x5C1010, 0x8E1A14, 0x38101A, 0x150708, 0x080405};
      private final StarField stars = new StarField(3501, 700, 15.0F);
      private final float[] tmp = new float[3];

      BloodMoon() {
         super("bloodmoon", 0x0E0304, 0xFF2A1A);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         float b = 0.82F + 0.18F * G.sin(t * 0.31);
         this.stars.draw(g, c.count(700, c.stars), 0.75F * b, 1.0F, 0.5F, 0xFFFF8060, 0.45F);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 20.0F, 0x80480808);
         g.sprite(SkyTex.GLOW, -25.0F, 34.0F, 96.0F, 0.0F, G.fade(0x50FF3018, b));
         g.sprite(SkyTex.GLOW, -25.0F, 34.0F, 46.0F, 0.0F, G.fade(0x90E02010, b));
         g.sprite(SkyTex.GLOW, -25.0F, 34.0F, 18.0F, 0.0F, G.fade(0xB0FF5030, b));
         g.sprite(SkyTex.DISC, -25.0F, 34.0F, 14.0F, 0.0F, 0xFFC0301A);
         g.sprite(SkyTex.MOON, -25.0F, 34.0F, 13.0F, 0.0F, 0xC0B02818);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.5 + 30.0), 24.0F, 48.0F, 0.0F, 1.0F, 0x70100608, 0.3F, 0.45F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.32 + 120.0), 10.0F, 30.0F, 0.2F, 1.0F, 0x60140A0A, 0.25F, 0.4F);
         Fx.shootingStars(g, 2, 18.0F, 0.45F, 0xFFFF6040, 3521, this.tmp);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class CyberCity extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 5.0F, 12.0F, 28.0F, 55.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x04030E, 0x0A0720, 0x2A0F4A, 0x63199A, 0xB62A96, 0x3E1668, 0x140B2E, 0x05040F};
      private final StarField stars = new StarField(4301, 260, 20.0F);

      CyberCity() {
         super("cybercity", 0x0A0418, 0xFF3AA8);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         float pulse = 0.85F + 0.15F * G.sin(t * 0.7);
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(260, c.stars), 0.5F, 1.0F, 0.6F, 0xFFB0FF, 0.4F);
         g.horizonGlow(0.0F, 360.0F, -2.0F, 11.0F, 0xA0FF2A9A);
         g.sprite(SkyTex.GLOW, 0.0F, 9.0F, 110.0F, 0.0F, G.fade(0x60FF2A9A, pulse));
         g.sprite(SkyTex.GLOW, 0.0F, 9.0F, 44.0F, 0.0F, G.fade(0x90FF3AA8, pulse));
         g.sprite(SkyTex.SYNTHSUN, 0.0F, 9.0F, 26.0F, 0.0F, 0xFFFFB0E0);
         g.ring(SkyTex.NEBULA2, (float)(t * 0.4), 16.0F, 44.0F, 0.0F, 1.0F, 0x602A0A48, 0.25F, 0.4F);
         int pillars = c.quality == 0 ? 6 : 11;

         for (int i = 0; i < pillars; i++) {
            float az = 360.0F * G.rnd(151, i) + 1.5F * G.sin(t * 0.2 + i);
            float w = 0.8F + 2.4F * G.rnd(152, i);
            float h = 14.0F + 34.0F * G.rnd(153, i);
            float alpha = (0.3F + 0.45F * G.rnd(154, i)) * (0.75F + 0.25F * G.sin(t * 0.8 + i * 2.0));
            int col = G.rnd(155, i) < 0.5F ? 0xFF2AF0FF : 0xFFFF3AA8;
            g.band(SkyTex.BEAM, az - w, az + w, -2.0F, h, 0.0F, 1.0F, 0.0F, 1.0F, G.fade(col, alpha * 0.45F), 0.35F, 0.55F, 0.15F);
            g.hline(az - w, az + w, h, 0.45F, G.fade(col, alpha));
         }

         int bars = c.quality == 0 ? 4 : 7;

         for (int i = 0; i < bars; i++) {
            float q = G.fract((float)i / bars + t * 0.03);
            float el = 4.0F + 30.0F * q;
            float alpha = G.smooth(0.0F, 0.2F, q) * G.smooth(1.0F, 0.6F, q);
            g.hline(0.0F, 360.0F, el, 0.35F + 0.3F * q, G.fade(0xFF6AF8FF, alpha * 0.5F));
         }

         int n = c.count(40, c.particles);

         for (int i = 0; i < n; i++) {
            float az = 360.0F * G.rnd(156, i) + (float)(t * (2.0 + 3.0 * G.rnd(157, i)));
            float el = 6.0F + 40.0F * G.rnd(158, i);
            float alpha = 0.3F + 0.4F * G.sin(t * 1.4 + i * 1.9);
            g.sprite(SkyTex.DOT, az, el, 0.45F, 0.0F, G.fade(0xFF7AF8FF, alpha));
         }
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Eclipse extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 6.0F, 14.0F, 34.0F, 64.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x06060A, 0x0A0A12, 0x16161F, 0x3A2E28, 0x8A6A48, 0x2A2028, 0x0C0A12, 0x050408};
      private final StarField stars = new StarField(4401, 900, 10.0F);

      Eclipse() {
         super("eclipse", 0x07060A, 0xFFE0B0);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(900, c.stars), 0.85F, 1.0F, 0.45F, 0, 0.0F);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 16.0F, 0x70483A20);
         float spin = (float)(t * 0.35);
         g.sprite(SkyTex.GLOW, 0.0F, 46.0F, 78.0F, 0.0F, G.fade(0x28FFE0B0, 0.9F));
         g.sprite(SkyTex.RAYS, 0.0F, 46.0F, 210.0F, spin, G.fade(0x14FFE8C0, 0.9F));
         g.sprite(SkyTex.RAYS, 0.0F, 46.0F, 150.0F, -spin * 0.6F + 22.0F, G.fade(0x10FFFFFF, 0.9F));
         g.sprite(SkyTex.GLOW, 0.0F, 46.0F, 30.0F, 0.0F, G.fade(0x80FFF0D0, 0.95F));
         g.sprite(SkyTex.GLOW, 0.0F, 46.0F, 15.0F, 0.0F, G.fade(0x50FFFFFF, 0.95F));
         g.sprite(SkyTex.DISC, 0.0F, 46.0F, 12.5F, 0.0F, 0xFF05050A);
         float twinkle = G.smooth(0.55F, 1.0F, 0.5F + 0.5F * G.sin(t * 1.6));
         g.sprite(SkyTex.GLOW, 13.2F, 45.0F, 6.0F, 0.0F, G.fade(0xFFFFF4D8, 0.35F + 0.65F * twinkle));
         g.sprite(SkyTex.DOT, 13.2F, 45.0F, 1.4F, 0.0F, G.fade(0xFFFFFFFF, 0.55F + 0.45F * twinkle));
         g.ring(SkyTex.CLOUDS, (float)(t * 0.28), 4.0F, 18.0F, 0.0F, 1.0F, 0x60301C10, 0.3F, 0.45F);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Enchanted extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -6.0F, 0.0F, 6.0F, 16.0F, 34.0F, 60.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x04120E, 0x0A2A20, 0x14473A, 0x2C7A5C, 0x22605A, 0x123C44, 0x0A1E28, 0x05100E};
      private final StarField sparkles = new StarField(4601, 220, 8.0F);

      Enchanted() {
         super("enchanted", 0x05140F, 0x60FFC0);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 340.0F, -2.0F, 12.0F, 0x7040C080);
         g.cap(SkyTex.NEBULA1, 0.0F, 90.0F, 86.0F, (float)(t * 0.15), 1.3F, 0xE00A1414, 0.45F);
         g.cap(SkyTex.NEBULA2, 0.0F, 90.0F, 80.0F, (float)(-t * 0.1 + 40.0), 1.1F, 0xD0061010, 0.5F);
         g.sprite(SkyTex.GLOW, 28.0F, 42.0F, 62.0F, 0.0F, G.fade(0x40CFFFE0, 0.9F));
         g.sprite(SkyTex.GLOW, 28.0F, 42.0F, 24.0F, 0.0F, G.fade(0x70E8FFF4, 0.9F));
         g.sprite(SkyTex.MOON, 28.0F, 42.0F, 8.6F, 0.0F, 0xF0E8FFF4);
         int shafts = c.quality == 0 ? 3 : 6;

         for (int i = 0; i < shafts; i++) {
            float az = 360.0F * G.rnd(181, i) + 4.0F * G.sin(t * 0.08 + i);
            float w = 2.0F + 3.0F * G.rnd(182, i);
            float alpha = 0.12F + 0.12F * G.rnd(183, i) * (0.7F + 0.3F * G.sin(t * 0.4 + i * 1.7));
            g.band(SkyTex.BEAM, az - w, az + w, 6.0F, 70.0F, 0.0F, 1.0F, 1.0F, 0.0F, G.fade(0xFF9EF0C0, alpha), 0.3F, 0.5F, 0.1F);
         }

         int n = c.count(120, c.particles);

         for (int i = 0; i < n; i++) {
            float depth = G.rnd(184, i);
            float sp = 0.01F + 0.025F * depth;
            float q = G.fract(t * sp + G.rnd(185, i));
            float el = 4.0F + q * 66.0F;
            float az = 360.0F * G.rnd(186, i) + 5.0F * G.sin(t * (0.25 + 0.4 * depth) + i * 2.3);
            float alpha = G.smooth(0.0F, 0.12F, q) * G.smooth(1.0F, 0.8F, q);
            int col = G.mix(0xFF8CFFC0, 0xFFB0E8FF, G.rnd(187, i));
            g.sprite(SkyTex.GLOW, az, el, (2.4F + 3.0F * depth) * (0.8F + 0.2F * G.sin(t * 1.3 + i)), 0.0F, G.fade(col, alpha * 0.35F));
            g.sprite(SkyTex.DOT, az, el, 0.55F + 0.5F * depth, 0.0F, G.fade(col, alpha));
         }

         this.sparkles.drawSparkles(g, c.count(60, c.particles), 0.6F, 0.9F, 0xFFB8FFD8);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class IceAge extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 8.0F, 20.0F, 45.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x6E96B0, 0x9CC4DA, 0xD6ECF6, 0xEAF8FF, 0xCDE7F4, 0xB2D4E6, 0x9BBED6};
      private final StarField sparkles = new StarField(4701, 90, 20.0F);

      IceAge() {
         super("iceage", 0x9CC4DA, 0xD8F4FF);
         this.fadeTop = 9.0F;
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -2.0F, 12.0F, 0x50FFFFFF);
         g.sprite(SkyTex.GLOW, 35.0F, 28.0F, 84.0F, 0.0F, 0x50FFFFFF);
         g.sprite(SkyTex.GLOW, 35.0F, 28.0F, 30.0F, 0.0F, 0x80FFFFFF);
         g.sprite(SkyTex.DISC, 35.0F, 28.0F, 5.6F, 0.0F, 0xFFFFFDF4);
         g.cap(SkyTex.CRYSTAL, 0.0F, 90.0F, 88.0F, (float)(-t * 0.4 + 20.0), 1.6F, 0x60BCE0FF, 0.5F);
         g.cap(SkyTex.CRYSTAL, 0.0F, 90.0F, 86.0F, (float)(t * 0.55), 1.0F, 0x48E8F8FF, 0.55F);
         g.cap(SkyTex.GLOW, 0.0F, 90.0F, 62.0F + 26.0F * G.fract(t * 0.06), 0.0F, 1.0F, G.fade(0x40FFFFFF, 1.0F - G.fract(t * 0.06)), 0.5F);
         int n = c.count(170, c.particles);

         for (int i = 0; i < n; i++) {
            float depth = G.rnd(191, i);
            float sp = 0.012F + 0.035F * depth;
            float q = G.fract(t * sp + G.rnd(192, i));
            float el = 84.0F - q * 98.0F;
            float az = 360.0F * G.rnd(193, i) + 6.0F * G.sin(t * (0.3 + 0.4 * depth) + i * 2.1);
            float alpha = G.smooth(0.0F, 0.1F, q) * G.smooth(1.0F, 0.85F, q) * (0.5F + 0.5F * depth);
            g.sprite(SkyTex.FLAKE, az, el, 0.7F + 1.5F * depth, (float)(t * 22.0 * (G.rnd(194, i) - 0.5F)), G.fade(0xE8FFFFFF, alpha));
         }

         this.sparkles.drawSparkles(g, c.count(60, c.particles), 0.8F, 1.0F, 0xFFFFFFFF);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Ocean extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -10.0F, 0.0F, 8.0F, 25.0F, 50.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x2E86A8, 0x3E9CC0, 0x76C8DC, 0xBEE8F0, 0x8FD4E8, 0x6ABAD8, 0x4E9CC4};
      private final StarField sparkles = new StarField(4801, 70, 30.0F);

      Ocean() {
         super("ocean", 0x8FD0E4, 0xC8F4FF);
         this.fadeTop = 10.0F;
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 300.0F, -2.0F, 10.0F, 0x60FFFFFF);
         g.sprite(SkyTex.GLOW, 20.0F, 52.0F, 96.0F, 0.0F, 0x40FFFFFF);
         g.sprite(SkyTex.GLOW, 20.0F, 52.0F, 34.0F, 0.0F, 0x70FFFFFF);
         g.sprite(SkyTex.DISC, 20.0F, 52.0F, 5.4F, 0.0F, 0xFFFFFDF0);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.22 + 40.0), 2.0F, 12.0F, 0.0F, 1.0F, 0x90FFFFFF, 0.2F, 0.4F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.16 + 150.0), 9.0F, 26.0F, 0.2F, 1.0F, 0x60E8FBFF, 0.25F, 0.45F);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.31 + 260.0), 4.0F, 18.0F, 0.1F, 0.9F, 0x70F4FFFF, 0.2F, 0.4F);
         this.sparkles.drawSparkles(g, c.count(40, c.particles), 0.7F, 1.1F, 0xFFFFFFFF);
         Fx.birds(g, 62.0F, 22.0F, t);
         Fx.birds(g, -140.0F, 16.0F, t * 0.8);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Prism extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -6.0F, 0.0F, 10.0F, 30.0F, 60.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x0A0618, 0x120A28, 0x1E1240, 0x2E1A5C, 0x20123F, 0x100A24, 0x06040F};
      private final StarField stars = new StarField(4901, 420, 18.0F);

      Prism() {
         super("prism", 0x0C0820, 0xB06BFF);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(420, c.stars), 0.7F, 1.0F, 0.5F, 0, 0.0F);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 14.0F, 0x50383090);
         float breath = 0.85F + 0.15F * G.sin(t * 0.45);
         g.cap(SkyTex.RAINBOW, 180.0F, 2.0F, 46.0F, 0.0F, 1.0F, G.fade(0xE8FFFFFF, 0.55F * breath), 0.35F);
         g.cap(SkyTex.RAINBOW, 20.0F, 6.0F, 28.0F, 0.0F, 1.0F, G.fade(0xC0FFFFFF, 0.22F * breath), 0.45F);
         g.cap(SkyTex.RAINBOW, 0.0F, 90.0F, 58.0F, (float)(t * 2.0), 1.0F, G.fade(0x60FFFFFF, 0.3F), 0.2F);
         g.sprite(SkyTex.GLOW, 180.0F, 2.0F, 92.0F, 0.0F, G.fade(0x3020A0FF, breath));
         g.ring(SkyTex.NEBULA2, (float)(t * 0.4), 20.0F, 46.0F, 0.0F, 1.0F, 0x4814082C, 0.25F, 0.4F);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Sandstorm extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -6.0F, 0.0F, 6.0F, 18.0F, 40.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x6A4A22, 0x8A5E28, 0xC08A44, 0xD9A860, 0xC4924C, 0x9A6E32, 0x7E5426, 0x5E3E1C};

      Sandstorm() {
         super("sandstorm", 0xB4863C, 0xFFDCA0);
         this.fadeTop = 11.0F;
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 22.0F, 0xA0E0A860);
         g.sprite(SkyTex.GLOW, 10.0F, 34.0F, 120.0F, 0.0F, 0x40FFE8B0);
         g.sprite(SkyTex.GLOW, 10.0F, 34.0F, 46.0F, 0.0F, 0x60FFD890);
         g.sprite(SkyTex.DISC, 10.0F, 34.0F, 6.0F, 0.0F, 0xE0FFEDC0);

         for (int i = 0; i < (c.quality == 0 ? 3 : 6); i++) {
            float sp = 1.1F + 1.5F * G.rnd(201, i);
            float el0 = 1.0F + 10.0F * G.rnd(202, i);
            float el1 = el0 + 8.0F + 16.0F * G.rnd(203, i);
            float alpha = 0.22F + 0.3F * G.rnd(204, i);
            g.ring(SkyTex.CLOUDS, (float)(t * sp + i * 47.0), el0, el1, 0.0F, 1.0F, G.fade(0xFFFFD9A0, alpha), 0.2F, 0.45F);
         }

         g.cap(SkyTex.NEBULA1, 0.0F, 90.0F, 78.0F, (float)(t * 1.6), 1.1F, 0x80A87838, 0.5F);
         int n = c.count(190, c.particles);

         for (int i = 0; i < n; i++) {
            float depth = G.rnd(205, i);
            float sp = 0.25F + 0.5F * depth;
            float az = 360.0F * G.fract(t * sp * 0.06 + G.rnd(206, i));
            float el = 1.0F + 55.0F * G.rnd(207, i) * G.rnd(208, i) + 1.6F * G.sin(t * 0.6 + i);
            float alpha = (0.25F + 0.5F * depth) * (0.6F + 0.4F * G.sin(t * 2.0 + i * 1.7));
            g.sprite(SkyTex.DOT, az, el, 0.5F + 0.6F * depth, 0.0F, G.fade(0xFFFFE6B8, alpha));
         }
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Solaris extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 6.0F, 18.0F, 40.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x4A1606, 0x7A2A08, 0xD0620E, 0xFF9A2E, 0xFFC46A, 0xC2701E, 0x8A3A0C, 0x5A1E06};
      private final StarField sparkles = new StarField(5001, 60, 40.0F);

      Solaris() {
         super("solaris", 0xC05A10, 0xFFD070);
         this.fadeTop = 8.0F;
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 16.0F, 0xA0FFB050);
         float breath = 0.88F + 0.12F * G.sin(t * 0.35);
         g.sprite(SkyTex.GLOW, 0.0F, 24.0F, 165.0F, 0.0F, G.fade(0x40FFB050, breath));
         g.sprite(SkyTex.RAYS, 0.0F, 24.0F, 236.0F, (float)(t * 0.5), G.fade(0x38FFD080, breath));
         g.sprite(SkyTex.RAYS, 0.0F, 24.0F, 176.0F, (float)(-t * 0.34 + 17.0), G.fade(0x28FFFFFF, breath));
         g.sprite(SkyTex.GLOW, 0.0F, 24.0F, 72.0F, 0.0F, G.fade(0x90FFC860, breath));
         g.sprite(SkyTex.GLOW, 0.0F, 24.0F, 32.0F, 0.0F, G.fade(0xC0FFE0A0, breath));
         g.sprite(SkyTex.DISC, 0.0F, 24.0F, 10.5F, 0.0F, 0xFFFFF8E0);
         g.cap(SkyTex.NEBULA2, 0.0F, 90.0F, 82.0F, (float)(t * 0.7), 1.2F, 0x70A04010, 0.45F);
         g.ring(SkyTex.NEBULA1, (float)(-t * 0.5), 12.0F, 34.0F, 0.0F, 1.0F, 0x50281004, 0.25F, 0.4F);

         for (int i = 0; i < (c.quality == 0 ? 4 : 8); i++) {
            float q = G.fract((float)i / 8.0F + (float)(t * 0.05));
            float el = 0.5F + 14.0F * q;
            float alpha = G.smooth(0.0F, 0.25F, q) * G.smooth(1.0F, 0.55F, q);
            g.hline(0.0F, 360.0F, el, 0.5F + 1.2F * q, G.fade(0xFFFFE0A0, alpha * 0.35F));
         }

         this.sparkles.drawSparkles(g, c.count(28, c.particles), 0.5F, 1.0F, 0xFFFFF0C0);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Spores extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -6.0F, 0.0F, 8.0F, 20.0F, 38.0F, 62.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x0A0616, 0x140A22, 0x22103A, 0x3A2058, 0x2A1844, 0x180E2A, 0x0C0718, 0x06030C};
      private final StarField stars = new StarField(5101, 320, 24.0F);

      Spores() {
         super("spores", 0x0C0718, 0x8CFFC0);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(320, c.stars), 0.55F, 1.0F, 0.55F, 0, 0.0F);
         g.horizonGlow(0.0F, 360.0F, -2.0F, 16.0F, 0x70403080);
         int blooms = c.quality == 0 ? 3 : 6;

         for (int i = 0; i < blooms; i++) {
            float az = 360.0F * G.rnd(211, i);
            float el = 1.0F + 6.0F * G.rnd(212, i);
            float size = 26.0F + 34.0F * G.rnd(213, i);
            float pulse = 0.7F + 0.3F * G.sin(t * (0.3 + 0.4 * G.rnd(214, i)) + i);
            int col = G.rnd(215, i) < 0.5F ? 0xFF6CFFC0 : 0xFFC06CFF;
            g.sprite(SkyTex.GLOW, az, el, size, 0.0F, G.fade(col, 0.16F * pulse));
            g.sprite(SkyTex.GLOW, az, el, size * 0.35F, 0.0F, G.fade(col, 0.28F * pulse));
         }

         g.ring(SkyTex.CLOUDS, (float)(t * 0.12), 0.0F, 14.0F, 0.0F, 1.0F, 0x40201638, 0.3F, 0.45F);
         int n = c.count(170, c.particles);

         for (int i = 0; i < n; i++) {
            float depth = G.rnd(216, i);
            float sp = 0.008F + 0.022F * depth;
            float q = G.fract(t * sp + G.rnd(217, i));
            float el = 1.0F + q * 74.0F;
            float az = 360.0F * G.rnd(218, i) + 8.0F * G.sin(t * (0.18 + 0.3 * depth) + i * 1.8);
            float alpha = G.smooth(0.0F, 0.15F, q) * G.smooth(1.0F, 0.75F, q);
            int col = G.mix(0xFF9CFFC8, 0xFFB8E8FF, G.rnd(219, i));
            g.sprite(SkyTex.GLOW, az, el, (2.0F + 3.4F * depth) * (0.8F + 0.2F * G.sin(t * 1.1 + i)), 0.0F, G.fade(col, alpha * 0.3F));
            g.sprite(SkyTex.DOT, az, el, 0.5F + 0.6F * depth, 0.0F, G.fade(col, alpha));
         }
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Twilight extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -6.0F, 0.0F, 4.0F, 12.0F, 28.0F, 55.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x120B22, 0x1E1233, 0x3B1F52, 0x7B3568, 0xC95A63, 0xA0587A, 0x4E2E63, 0x241640};
      private final StarField stars = new StarField(5201, 500, 26.0F);
      private final float[] tmp = new float[3];

      Twilight() {
         super("twilight", 0x0B0722, 0xFF7A4A);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         float pulse = 0.9F + 0.1F * G.sin(t * 0.2);
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 210.0F, -3.0F, 15.0F, 0xA0FF9E5E);
         this.stars.draw(g, c.count(500, c.stars), 0.55F, 1.0F, 0.55F, 0, 0.0F);
         g.sprite(SkyTex.GLOW, 0.0F, 1.5F, 96.0F, 0.0F, G.fade(0x40FFB070, pulse));
         g.sprite(SkyTex.GLOW, 0.0F, 1.0F, 34.0F, 0.0F, G.fade(0x80FFC890, pulse));
         g.sprite(SkyTex.DISC, 0.0F, 0.6F, 7.0F, 0.0F, 0xFFFFE6C0);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.35 + 70.0), 2.0F, 13.0F, 0.0F, 1.0F, 0x88FFB488, 0.25F, 0.4F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.22 + 10.0), 9.0F, 30.0F, 0.1F, 0.9F, 0x70B078B4, 0.3F, 0.45F);
         g.ring(SkyTex.NEBULA2, (float)(t * 0.5), 18.0F, 44.0F, 0.2F, 1.0F, 0x502C1848, 0.25F, 0.4F);
         Fx.birds(g, -18.0F, 14.0F, t);
         Fx.shootingStars(g, 1, 22.0F, 0.35F, 0xFFFFD8B0, 5211, this.tmp);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class TwinMoons extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 8.0F, 22.0F, 45.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x060A14, 0x0C1424, 0x16263E, 0x1E3450, 0x18283C, 0x0E1826, 0x080E18, 0x04070E};
      private final StarField stars = new StarField(5301, 800, 12.0F);
      private final float[] tmp = new float[3];

      TwinMoons() {
         super("twinmoons", 0x0A1220, 0x9FC4FF);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         float azA = -40.0F + (float)(t * 0.15);
         float azB = 30.0F - (float)(t * 0.1);
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(800, c.stars), 0.8F, 1.0F, 0.5F, 0, 0.0F);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 18.0F, 0x50304A78);
         g.sprite(SkyTex.GLOW, azA, 32.0F, 74.0F, 0.0F, G.fade(0x50C0D8FF, 0.9F));
         g.sprite(SkyTex.GLOW, azA, 32.0F, 28.0F, 0.0F, G.fade(0x80E0ECFF, 0.9F));
         g.sprite(SkyTex.MOON, azA, 32.0F, 11.0F, 0.0F, 0xFFF2F6FF);
         g.sprite(SkyTex.GLOW, azB, 46.0F, 34.0F, 0.0F, G.fade(0x406AA8FF, 0.9F));
         g.sprite(SkyTex.GLOW, azB, 46.0F, 12.0F, 0.0F, G.fade(0x7090C0FF, 0.9F));
         g.sprite(SkyTex.MOON, azB, 46.0F, 5.0F, 0.0F, 0xD0B8D4FF);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.18 + 60.0), 6.0F, 20.0F, 0.0F, 1.0F, 0x50203050, 0.25F, 0.45F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.12 + 200.0), 12.0F, 32.0F, 0.2F, 1.0F, 0x40283058, 0.25F, 0.4F);
         Fx.shootingStars(g, 2, 16.0F, 0.4F, 0xFFD8ECFF, 5321, this.tmp);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Volcano extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -6.0F, 0.0F, 5.0F, 14.0F, 30.0F, 55.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x080404, 0x130708, 0x3A0E08, 0x7A1E08, 0xB23A0A, 0x571A0C, 0x1C0A08, 0x0A0505};

      Volcano() {
         super("volcano", 0x120404, 0xFF5A14);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -4.0F, 24.0F, 0x90FF5A14);
         g.cap(SkyTex.NEBULA1, 0.0F, 90.0F, 82.0F, (float)(t * 0.9), 1.2F, 0xE0140A08, 0.45F);
         g.cap(SkyTex.NEBULA2, 0.0F, 90.0F, 76.0F, (float)(-t * 0.7 + 40.0), 1.0F, 0xC00C0605, 0.5F);
         g.ring(SkyTex.CLOUDS, (float)(t * 1.4), 8.0F, 34.0F, 0.0F, 1.0F, 0x70100604, 0.15F, 0.35F);
         g.sprite(SkyTex.GLOW, 26.0F, 1.0F, 84.0F, 0.0F, G.fade(0x70FF4A10, 0.85F));
         g.sprite(SkyTex.GLOW, 205.0F, 2.0F, 68.0F, 0.0F, G.fade(0x60FF5A18, 0.75F));
         int n = c.count(150, c.particles);

         for (int i = 0; i < n; i++) {
            float depth = G.rnd(141, i);
            float sp = 0.02F + 0.05F * depth;
            float q = G.fract(t * sp + G.rnd(142, i));
            float el = -2.0F + q * 70.0F;
            float az = 360.0F * G.rnd(143, i) + 8.0F * G.sin(t * (0.3 + 0.5 * depth) + i * 1.7);
            float alpha = G.smooth(0.0F, 0.12F, q) * G.smooth(1.0F, 0.7F, q);
            float flick = 0.55F + 0.45F * G.sin(t * (2.5 + 3.0 * depth) + i * 3.3);
            int col = G.mix(0xFFFF4A0A, 0xFFFFD060, G.rnd(144, i));
            if (depth < 0.45F) {
               g.sprite(SkyTex.GLOW, az, el, 1.2F + 3.4F * depth, 0.0F, G.fade(col, alpha * flick * 0.45F));
            }

            g.sprite(SkyTex.DOT, az, el, 0.5F + 0.6F * depth, 0.0F, G.fade(col, alpha * flick));
         }

         int ash = c.count(70, c.particles);

         for (int i = 0; i < ash; i++) {
            float depth = G.rnd(145, i);
            float sp = 0.014F + 0.02F * depth;
            float q = G.fract(t * sp + G.rnd(146, i));
            float el = 76.0F - q * 88.0F;
            float az = 360.0F * G.rnd(147, i) + 10.0F * G.sin(t * 0.4 + i * 2.2) + q * 18.0F;
            float alpha = G.smooth(0.0F, 0.12F, q) * G.smooth(1.0F, 0.85F, q);
            g.sprite(SkyTex.DOT, az, el, 0.45F + 0.5F * depth, 0.0F, G.fade(0xFF40281C, alpha * 0.7F));
         }
      }
   }
}
