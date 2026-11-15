package dev.valence.sky.classic;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
final class ExtraSkies2 {
   private ExtraSkies2() {
   }

   @Environment(EnvType.CLIENT)
   static final class Moonrise extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 6.0F, 16.0F, 34.0F, 60.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x0A1020, 0x101828, 0x1E2C44, 0x3A4E70, 0x6A86AC, 0x2E3E5C, 0x121B2C, 0x080C16};
      private final StarField stars = new StarField(6101, 800, 16.0F);
      private final float[] tmp = new float[3];

      Moonrise() {
         super("moonrise", 0x0C1424, 0xD8E4FF);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(800, c.stars), 0.8F, 1.0F, 0.5F, 0, 0.0F);
         g.horizonGlow(0.0F, 300.0F, -3.0F, 18.0F, 0x60406090);
         float pulse = 0.85F + 0.15F * G.sin(t * 0.25);
         float az = -18.0F;
         float el = 5.0F;
         g.sprite(SkyTex.GLOW, az, el, 96.0F, 0.0F, G.fade(0x40C0D8FF, pulse));
         g.sprite(SkyTex.GLOW, az, el, 40.0F, 0.0F, G.fade(0x70E0ECFF, pulse));
         g.sprite(SkyTex.MOON, az, el, 13.0F, 0.0F, 0xFFF2F6FF);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.3 + 40.0), 1.0F, 9.0F, 0.0F, 1.0F, 0x60C8D8F0, 0.2F, 0.4F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.22 + 150.0), 8.0F, 22.0F, 0.1F, 0.9F, 0x40586890, 0.25F, 0.45F);
         g.ring(SkyTex.NEBULA2, (float)(t * 0.14), 26.0F, 52.0F, 0.2F, 0.8F, 0x30141C34, 0.3F, 0.45F);
         Fx.shootingStars(g, 2, 20.0F, 0.4F, 0xFFD8E8FF, 6111, this.tmp);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class MilkyWay extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -10.0F, 0.0F, 8.0F, 24.0F, 50.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x04050E, 0x070A18, 0x0E1428, 0x1A2340, 0x121A32, 0x0A0F1E, 0x05060D};
      private final StarField stars = new StarField(6201, 1600, 0.0F);
      private final float[] tmp = new float[3];

      MilkyWay() {
         super("milkyway", 0x05070F, 0xC8D4FF);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 14.0F, 0x40202C50);
         this.stars.draw(g, c.count(1600, c.stars), 0.9F, 1.0F, 0.45F, 0, 0.0F);
         g.cap(SkyTex.MILKYWAY, 205.0F, 58.0F, 62.0F, (float)(t * 0.4), 1.4F, 0x60C0CCFF, 0.4F);
         g.cap(SkyTex.NEBULA2, 205.0F, 58.0F, 30.0F, (float)(-t * 0.7), 0.9F, 0x40203060, 0.5F);
         g.ring(SkyTex.MILKYWAY, (float)(t * 0.12), 22.0F, 54.0F, 0.05F, 0.95F, 0x50AAB8E8, 0.25F, 0.45F);
         g.ring(SkyTex.NEBULA1, (float)(-t * 0.09 + 70.0), 28.0F, 60.0F, 0.1F, 0.9F, 0x301C2448, 0.3F, 0.45F);
         g.sprite(SkyTex.GLOW, 205.0F, 58.0F, 54.0F, 0.0F, G.fade(0x30E0E8FF, 0.85F + 0.15F * G.sin(t * 0.2)));
         g.sprite(SkyTex.GLOW, 205.0F, 58.0F, 22.0F, 0.0F, G.fade(0x50FFFFFF, 0.9F));
         Fx.shootingStars(g, 3, 13.0F, 0.5F, 0xFFE8F0FF, 6211, this.tmp);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class BlackHole extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -6.0F, 0.0F, 8.0F, 20.0F, 45.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x020208, 0x04040A, 0x07060E, 0x0C0912, 0x0A0812, 0x06050C, 0x030307};
      private final StarField stars = new StarField(6301, 900, 0.0F);
      private final float[] a = new float[3];
      private final float[] b = new float[3];
      private final float[] tmp = new float[3];

      BlackHole() {
         super("blackhole", 0x040409, 0xFFD9A0);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         float az = 24.0F;
         float el = 42.0F;
         float rot = (float)(t * 0.45);
         float pulse = 0.9F + 0.1F * G.sin(t * 0.3);
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(900, c.stars), 0.75F, 1.0F, 0.5F, 0xFFFFE8C0, 0.25F);
         g.horizonGlow(0.0F, 300.0F, -4.0F, 16.0F, 0x40281810);
         g.sprite(SkyTex.GLOW, az, el, 120.0F, 0.0F, G.fade(0x26FFD8A0, pulse));
         g.sprite(SkyTex.GLOW, az, el, 62.0F, 0.0F, G.fade(0x38FFC080, pulse));
         g.sprite(SkyTex.RING, az, el, 48.0F, rot, G.fade(0xC8FFE8C0, pulse));
         g.sprite(SkyTex.RING, az, el, 40.0F, -rot * 0.6F + 30.0F, G.fade(0x90FFB070, pulse));
         g.sprite(SkyTex.RING, az, el, 26.0F, rot * 1.4F, G.fade(0xE0FFFFFF, pulse));
         g.sprite(SkyTex.DISC, az, el, 15.0F, 0.0F, 0xFF000000);
         g.sprite(SkyTex.DISC, az, el, 9.0F, 0.0F, 0xFF000000);
         int n = c.quality == 0 ? 3 : 7;

         for (int i = 0; i < n; i++) {
            float ph = G.rnd(6311, i) * 360.0F;
            G.dir(az + ph * 0.0F + (float)(t * (6.0 + 4.0 * G.rnd(6312, i))), el + 8.0F + 22.0F * G.rnd(6313, i), this.a);
            G.dir(az + ph * 0.06F, el + 6.0F, this.b);
            g.segment(
               SkyTex.GLOW,
               this.a,
               this.b,
               2.4F + 2.0F * G.rnd(6314, i),
               0.2F,
               G.fade(0xFFFFC080, 0.10F),
               G.fade(0xFFFF8040, 0.0F),
               false
            );
         }

         int m = c.quality == 0 ? 10 : 22;

         for (int i = 0; i < m; i++) {
            float ang = 360.0F * G.rnd(6321, i) + (float)(t * 9.0);
            float rad = 17.0F + 7.0F * G.rnd(6322, i);
            g.sprite(SkyTex.DOT, az + rad * G.cos(G.rad(ang)), el + rad * 0.55F * G.sin(G.rad(ang)), 0.35F, 0.0F, G.fade(0xFFFFE0B0, 0.35F));
         }

         Fx.shootingStars(g, 1, 26.0F, 0.3F, 0xFFFFE0C0, 6321, this.tmp);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Pulsar extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 6.0F, 18.0F, 40.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x03040A, 0x050710, 0x0A1024, 0x162448, 0x101A36, 0x080C1C, 0x04060E, 0x020308};
      private final StarField stars = new StarField(6401, 1000, 0.0F);
      private final float[] c0 = new float[3];
      private final float[] a = new float[3];
      private final float[] b = new float[3];

      Pulsar() {
         super("pulsar", 0x040610, 0xA0D8FF);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         float az = 0.0F;
         float el = 56.0F;
         float spin = (float)(t * 46.0);
         float pulse = 0.55F + 0.45F * G.sin(t * 3.1);
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(1000, c.stars), 0.7F, 1.0F, 0.55F, 0xFFFFF0D0, 0.2F);
         g.horizonGlow(0.0F, 340.0F, -3.0F, 12.0F, 0x30204070);
         g.sprite(SkyTex.RAYS, az, el, 320.0F, spin, G.fade(0x40A0D8FF, pulse));
         g.sprite(SkyTex.RAYS, az, el, 240.0F, -spin * 0.6F + 15.0F, G.fade(0x22FFFFFF, pulse));
         G.dir(az, el, this.c0);

         for (int k = 0; k < 2; k++) {
            G.dir(az + spin + k * 180.0F, el + (k == 0 ? 30.0F : -30.0F), this.a);
            G.dir(az + spin + k * 180.0F, el + (k == 0 ? 62.0F : -58.0F), this.b);
            g.segment(SkyTex.GLOW, this.c0, this.a, 5.0F, 1.2F, G.fade(0x80A8E8FF, pulse), G.fade(0x40A8E8FF, pulse * 0.5F), false);
            g.segment(SkyTex.LINE, this.a, this.b, 1.6F, 0.15F, G.fade(0xC0E8FFFF, pulse), G.fade(0x00E8FFFF, 0.0F), false);
         }

         g.sprite(SkyTex.GLOW, az, el, 40.0F, 0.0F, G.fade(0x60A0D8FF, pulse));
         g.sprite(SkyTex.GLOW, az, el, 15.0F, 0.0F, G.fade(0xC0E0FFFF, pulse));
         g.sprite(SkyTex.DISC, az, el, 3.4F, 0.0F, 0xFFFFFFFF);
         g.sprite(SkyTex.GLOW, az, el, 220.0F, 0.0F, G.fade(0x14A0C8FF, pulse));
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Andromeda extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 8.0F, 22.0F, 45.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x05030C, 0x080512, 0x0E0A1E, 0x1A1234, 0x241840, 0x140D28, 0x080514, 0x04030A};
      private final StarField stars = new StarField(6501, 1300, 0.0F);
      private final float[] tmp = new float[3];

      Andromeda() {
         super("andromeda", 0x070512, 0xC0B0FF);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(1300, c.stars), 0.85F, 1.0F, 0.5F, 0, 0.0F);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 15.0F, 0x40241C50);
         g.push();
         g.rotZ(-32.0F);
         g.rotY((float)(t * 0.03));
         g.cap(SkyTex.MILKYWAY, 0.0F, 66.0F, 46.0F, (float)(t * 2.4), 0.85F, 0x78C8C0FF, 0.38F);
         g.cap(SkyTex.NEBULA2, 0.0F, 66.0F, 30.0F, (float)(-t * 3.6), 0.7F, 0x60303070, 0.45F);
         g.cap(SkyTex.MILKYWAY, 0.0F, 66.0F, 14.0F, (float)(t * 5.0), 1.2F, 0x70FFFFFF, 0.4F);
         g.pop();
         g.sprite(SkyTex.GLOW, 8.0F, 68.0F, 44.0F, 0.0F, G.fade(0x40E0D8FF, 0.9F));
         g.sprite(SkyTex.GLOW, 8.0F, 68.0F, 16.0F, 0.0F, G.fade(0x70FFFFFF, 0.95F));
         g.sprite(SkyTex.DISC, 8.0F, 68.0F, 2.2F, 0.0F, 0xFFFFFFFF);
         g.cap(SkyTex.NEBULA1, 150.0F, 26.0F, 12.0F, (float)(t * 1.2), 0.8F, 0x40506AA0, 0.45F);
         g.cap(SkyTex.NEBULA2, -110.0F, 34.0F, 8.0F, (float)(-t * 1.5), 0.9F, 0x40806040, 0.5F);
         Fx.shootingStars(g, 2, 17.0F, 0.45F, 0xFFE0E8FF, 6511, this.tmp);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class CosmicWeb extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 8.0F, 24.0F, 50.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x03040A, 0x05060F, 0x090C1C, 0x101733, 0x0C1228, 0x06091A, 0x030409};
      private final StarField stars = new StarField(6601, 700, 0.0F);
      private final float[] a = new float[3];
      private final float[] b = new float[3];

      CosmicWeb() {
         super("cosmicweb", 0x05060F, 0x8CA8FF);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(700, c.stars), 0.6F, 1.0F, 0.5F, 0, 0.0F);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 14.0F, 0x40202850);
         int n = c.quality == 0 ? 12 : 22;
         float breathe = 0.7F + 0.3F * G.sin(t * 0.18);

         for (int i = 0; i < n; i++) {
            float az = 360.0F * G.rnd(6611, i);
            float el = 6.0F + 76.0F * G.rnd(6612, i);
            int j = (i * 7 + 3) % n;
            float az2 = 360.0F * G.rnd(6611, j);
            float el2 = 6.0F + 76.0F * G.rnd(6612, j);
            G.dir(az, el, this.a);
            G.dir(az2, el2, this.b);
            float w = 1.2F + 2.4F * G.rnd(6613, i);
            g.segment(SkyTex.GLOW, this.a, this.b, w, w * 0.6F, G.fade(0xFF3050A0, 0.10F), G.fade(0xFF20386A, 0.10F), false);
            g.segment(SkyTex.LINE, this.a, this.b, 0.14F, 0.10F, G.fade(0xFF8CA0E8, 0.35F), G.fade(0xFF6A80C8, 0.25F), false);
            g.sprite(SkyTex.GLOW, az, el, 4.0F + 8.0F * G.rnd(6614, i), 0.0F, G.fade(0xFFA8C0FF, 0.18F * breathe));
            g.sprite(SkyTex.DOT, az, el, 0.7F + 0.5F * G.rnd(6615, i), 0.0F, G.fade(0xFFFFFFFF, 0.55F));
         }

         g.sprite(SkyTex.GLOW, 0.0F, 90.0F, 90.0F, 0.0F, G.fade(0x1C4060C0, 0.8F));
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Starfall extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 6.0F, 16.0F, 36.0F, 64.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x0A0E18, 0x0C1220, 0x121A2C, 0x1C283E, 0x22304A, 0x161E30, 0x0C1220, 0x06090F};
      private final StarField stars = new StarField(6701, 1200, 0.0F);
      private final float[] tmp = new float[3];

      Starfall() {
         super("starfall", 0x090D16, 0xFFD8A0);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(1200, c.stars), 0.85F, 1.0F, 0.5F, 0, 0.0F);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 16.0F, 0x50283040);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.2 + 20.0), 2.0F, 12.0F, 0.0F, 1.0F, 0x50202838, 0.25F, 0.45F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.14 + 190.0), 9.0F, 20.0F, 0.1F, 0.9F, 0x40182030, 0.25F, 0.4F);
         g.sprite(SkyTex.GLOW, 210.0F, 40.0F, 80.0F, 0.0F, G.fade(0x30FFE0B0, 0.8F + 0.2F * G.sin(t * 0.5)));
         Fx.meteorShower(g, c.quality == 0 ? 4 : 10, 210.0F, 40.0F, t, this.tmp);
         Fx.shootingStars(g, 3, 7.0F, 0.7F, 0xFFFFE8C0, 6711, this.tmp);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Sundog extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 10.0F, 26.0F, 50.0F, 75.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x9CB4CC, 0xB8CCE0, 0xD4E2F0, 0xE4EFFA, 0xCFE0F4, 0xAFC8E8, 0x94B4DE, 0x86A8D6};
      private final StarField sparkles = new StarField(6801, 60, 30.0F);

      Sundog() {
         super("sundog", 0xC8D8EC, 0xFFFFFF);
         this.fadeTop = 9.0F;
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -2.0F, 12.0F, 0x50FFFFFF);
         float az = 0.0F;
         float el = 18.0F;
         float breathe = 0.85F + 0.15F * G.sin(t * 0.3);
         g.sprite(SkyTex.GLOW, az, el, 150.0F, 0.0F, G.fade(0x38FFFFFF, breathe));
         g.sprite(SkyTex.GLOW, az, el, 54.0F, 0.0F, G.fade(0x70FFFFFF, breathe));
         g.sprite(SkyTex.DISC, az, el, 6.0F, 0.0F, 0xFFFFFBEE);
         g.sprite(SkyTex.RING, az, el, 46.0F, 0.0F, G.fade(0x50FFFFFF, 0.9F));
         g.sprite(SkyTex.RING, az, el, 60.0F, 0.0F, G.fade(0x28FFE8C0, 0.9F));

         for (int k = -1; k <= 1; k += 2) {
            float paz = az + k * 22.0F;
            g.sprite(SkyTex.GLOW, paz, el, 26.0F, 0.0F, G.fade(0x70FFFFFF, breathe));
            g.sprite(SkyTex.GLOW, paz, el, 9.0F, 0.0F, G.fade(0xB0FFFFFF, breathe));
            g.sprite(SkyTex.DISC, paz, el, 2.4F, 0.0F, 0xFFFFF8E8);
         }

         g.ring(SkyTex.CLOUDS, (float)(t * 0.16 + 60.0), 24.0F, 36.0F, 0.0F, 1.0F, 0x40FFFFFF, 0.15F, 0.4F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.1 + 170.0), 30.0F, 44.0F, 0.1F, 0.9F, 0x30F0F8FF, 0.2F, 0.4F);
         this.sparkles.drawSparkles(g, c.count(24, c.particles), 0.5F, 1.0F, 0xFFFFFFFF);
         Fx.birds(g, 120.0F, 14.0F, t * 0.8);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Noon extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -6.0F, 0.0F, 6.0F, 16.0F, 40.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0xA8C8E8, 0xC0D8F0, 0xD8E8FA, 0xC8DEF6, 0x9FC4F0, 0x6A9EE8, 0x4A82DC, 0x3C74D0};
      private final StarField sparkles = new StarField(6901, 40, 40.0F);

      Noon() {
         super("noon", 0xD2E2F4, 0xFFFFFF);
         this.fadeTop = 8.0F;
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -2.0F, 10.0F, 0x60FFFFFF);
         float az = 40.0F;
         float el = 68.0F;
         g.sprite(SkyTex.GLOW, az, el, 110.0F, 0.0F, 0x40FFFFFF);
         g.sprite(SkyTex.GLOW, az, el, 34.0F, 0.0F, 0x80FFFFFF);
         g.sprite(SkyTex.DISC, az, el, 5.4F, 0.0F, 0xFFFFFFFF);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.12 + 30.0), 4.0F, 13.0F, 0.0F, 1.0F, 0xA8F4FAFF, 0.15F, 0.35F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.09 + 130.0), 12.0F, 22.0F, 0.1F, 0.9F, 0x88E8F2FF, 0.2F, 0.4F);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.07 + 250.0), 21.0F, 29.0F, 0.2F, 0.8F, 0x60DCE8FA, 0.25F, 0.45F);
         this.sparkles.drawSparkles(g, c.count(20, c.particles), 0.4F, 1.0F, 0xFFFFFFFF);
         Fx.birds(g, -60.0F, 26.0F, t);
         Fx.birds(g, 150.0F, 18.0F, t * 0.7);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Monsoon extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 6.0F, 16.0F, 34.0F, 60.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x2A2E36, 0x363C46, 0x46505C, 0x5A6470, 0x4E5864, 0x39414C, 0x2A3038, 0x22262C};
      private final float[] a = new float[3];
      private final float[] b = new float[3];

      Monsoon() {
         super("monsoon", 0x3E4652, 0xC8D8E8);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         float flash = G.smooth(0.94F, 1.0F, G.sin(t * 0.9)) * (0.4F + 0.6F * G.smooth(0.2F, 0.8F, G.sin(t * 0.13)));
         g.sprite(SkyTex.GLOW, 120.0F, 40.0F, 340.0F, 0.0F, G.fade(0x60D8E4F0, flash));
         g.sprite(SkyTex.GLOW, -70.0F, 30.0F, 280.0F, 0.0F, G.fade(0x40D0DCE8, flash * 0.7F));
         g.horizonGlow(0.0F, 360.0F, -3.0F, 20.0F, 0x60505C68);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.5), 4.0F, 26.0F, 0.0F, 1.0F, 0x90404A56, 0.15F, 0.4F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.35 + 110.0), 18.0F, 44.0F, 0.1F, 0.9F, 0x80343C48, 0.2F, 0.45F);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.22 + 240.0), 34.0F, 58.0F, 0.2F, 0.8F, 0x702A323C, 0.25F, 0.45F);
         int n = c.count(220, c.particles);

         for (int i = 0; i < n; i++) {
            float q = G.fract(t * (0.5 + 0.4 * G.rnd(6911, i)) + G.rnd(6912, i));
            float az = 360.0F * G.rnd(6913, i) + q * 26.0F;
            float el = 74.0F - q * 92.0F;
            float alpha = G.smooth(0.0F, 0.1F, q) * G.smooth(1.0F, 0.86F, q) * 0.5F;
            G.dir(az, el, this.a);
            G.dir(az - 1.2F, el - 2.6F, this.b);
            g.segment(SkyTex.LINE, this.a, this.b, 0.07F, 0.03F, G.fade(0xC8D4E4, alpha), G.fade(0xC8D4E4, 0.0F), false);
         }

         g.ring(SkyTex.CLOUDS, (float)(t * 0.6 + 60.0), 0.0F, 8.0F, 0.0F, 1.0F, 0x70606C78, 0.2F, 0.45F);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Hurricane extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 6.0F, 16.0F, 34.0F, 62.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x161C22, 0x1C242C, 0x26323C, 0x33424E, 0x2C3A46, 0x222C36, 0x181F26, 0x12171C};
      private final float[] a = new float[3];
      private final float[] b = new float[3];

      Hurricane() {
         super("hurricane", 0x28323C, 0x9FC0D8);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -4.0F, 22.0F, 0x60425060);
         float spin = (float)(t * 0.55);
         g.cap(SkyTex.CLOUDS, 0.0F, 90.0F, 118.0F, spin, 0.55F, 0x70B4C6DA, 0.35F);
         g.cap(SkyTex.CLOUDS, 0.0F, 90.0F, 92.0F, -spin * 1.4F, 0.7F, 0x60485A6E, 0.45F);
         int arms = c.quality == 0 ? 3 : 6;

         for (int i = 0; i < arms; i++) {
            float off = spin * 34.0F + i * (360.0F / arms);
            float el0 = 4.0F + 7.0F * i;
            float alpha = 0.20F + 0.06F * G.sin(t * 0.4 + i);
            g.ring(SkyTex.CLOUDS, off, el0, el0 + 12.0F, 0.05F, 0.95F, G.fade(0xFFA8BCD0, alpha), 0.25F, 0.45F);
            g.ring(SkyTex.CLOUDS, off + 22.0F, el0 + 4.0F, el0 + 16.0F, 0.15F, 0.85F, G.fade(0xFF3E4C5C, alpha * 1.4F), 0.25F, 0.45F);
         }

         g.sprite(SkyTex.DISC, 0.0F, 90.0F, 9.0F, 0.0F, 0xFF101820);
         g.sprite(SkyTex.GLOW, 0.0F, 90.0F, 22.0F, 0.0F, G.fade(0x40C0D8E8, 0.85F + 0.15F * G.sin(t * 0.6)));
         int n = c.count(180, c.particles);

         for (int i = 0; i < n; i++) {
            float q = G.fract(t * (0.6 + 0.5 * G.rnd(6931, i)) + G.rnd(6932, i));
            float az = 360.0F * G.rnd(6933, i) + q * 60.0F;
            float el = 76.0F - q * 96.0F;
            float alpha = G.smooth(0.0F, 0.1F, q) * G.smooth(1.0F, 0.85F, q) * 0.55F;
            G.dir(az, el, this.a);
            G.dir(az - 3.0F, el - 3.4F, this.b);
            g.segment(SkyTex.LINE, this.a, this.b, 0.08F, 0.03F, G.fade(0xD0DEEA, alpha), G.fade(0xD0DEEA, 0.0F), false);
         }
      }
   }

   @Environment(EnvType.CLIENT)
   static final class MistValley extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 8.0F, 22.0F, 44.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x8C9490, 0xA4ACA4, 0xBCC4BC, 0xCFD6CC, 0xC4CCC2, 0xB0B8AE, 0x9EA69C, 0x969E94};
      private final StarField sparkles = new StarField(6941, 40, 20.0F);

      MistValley() {
         super("mistvalley", 0xC0C8BE, 0xF0F4E8);
         this.fadeTop = 18.0F;
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 18.0F, 0x60F4F8EC);
         g.sprite(SkyTex.GLOW, 20.0F, 32.0F, 190.0F, 0.0F, G.fade(0x38FFFCE8, 0.7F + 0.3F * G.sin(t * 0.1)));
         g.sprite(SkyTex.GLOW, 20.0F, 32.0F, 60.0F, 0.0F, G.fade(0x40FFFFFF, 0.8F));
         g.sprite(SkyTex.DISC, 20.0F, 32.0F, 3.2F, 0.0F, G.fade(0xFFFFFBEC, 0.55F));
         int n = c.quality == 0 ? 2 : 4;

         for (int i = 0; i < n; i++) {
            float el = 1.0F + 6.0F * i;
            g.ring(
               SkyTex.CLOUDS,
               (float)(t * (0.08 + 0.05 * i) + i * 60.0),
               el,
               el + 9.0F,
               0.0F,
               1.0F,
               G.fade(0xFFF0F4EC, 0.30F - 0.05F * i),
               0.15F,
               0.45F
            );
            g.ring(
               SkyTex.CLOUDS,
               (float)(-t * (0.06 + 0.04 * i) + i * 110.0),
               el + 4.0F,
               el + 14.0F,
               0.1F,
               0.9F,
               G.fade(0xFFDCE4D8, 0.18F),
               0.2F,
               0.5F
            );
         }

         this.sparkles.drawSparkles(g, c.count(16, c.particles), 0.35F, 1.4F, 0xFFFFFEF0);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Ashfall extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 6.0F, 16.0F, 34.0F, 60.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x1A1614, 0x241E1A, 0x322822, 0x4A3A2E, 0x5A4234, 0x3A2C22, 0x201814, 0x120E0C};

      Ashfall() {
         super("ashfall", 0x241E1A, 0xFF8A3C);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 300.0F, -4.0F, 20.0F, 0x80C05A18);
         g.horizonGlow(180.0F, 140.0F, -4.0F, 14.0F, 0x40A0400C);
         g.sprite(SkyTex.GLOW, 30.0F, 3.0F, 140.0F, 0.0F, G.fade(0x50FF7A20, 0.7F + 0.3F * G.sin(t * 0.4)));
         g.ring(SkyTex.CLOUDS, (float)(t * 0.3), 6.0F, 30.0F, 0.0F, 1.0F, 0x80342A22, 0.2F, 0.45F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.2 + 120.0), 22.0F, 48.0F, 0.1F, 0.9F, 0x70281E18, 0.25F, 0.45F);
         int n = c.count(150, c.particles);

         for (int i = 0; i < n; i++) {
            float depth = G.rnd(6951, i);
            float sp = 0.05F + 0.09F * depth;
            float q = G.fract(t * sp + G.rnd(6952, i));
            float az = 360.0F * G.rnd(6953, i) + 6.0F * G.sin(t * 0.3 + i);
            float el = 76.0F - q * 90.0F;
            float alpha = G.smooth(0.0F, 0.1F, q) * G.smooth(1.0F, 0.86F, q) * (0.3F + 0.5F * depth);
            g.sprite(SkyTex.DOT, az, el, 0.4F + 0.5F * depth, 0.0F, G.fade(0xFF8A8278, alpha));
         }

         int m = c.count(70, c.particles);

         for (int i = 0; i < m; i++) {
            float depth = G.rnd(6954, i);
            float sp = 0.02F + 0.04F * depth;
            float q = G.fract(t * sp + G.rnd(6955, i));
            float el = 1.0F + q * 60.0F;
            float az = 360.0F * G.rnd(6956, i) + 8.0F * G.sin(t * 0.5 + i * 1.7);
            float alpha = G.smooth(0.0F, 0.12F, q) * G.smooth(1.0F, 0.8F, q);
            float flick = 0.5F + 0.5F * G.sin(t * (2.0 + 3.0 * depth) + i * 2.4);
            g.sprite(SkyTex.GLOW, az, el, 1.0F + 2.0F * depth, 0.0F, G.fade(0xFFFF6A18, alpha * flick * 0.35F));
            g.sprite(SkyTex.DOT, az, el, 0.4F + 0.4F * depth, 0.0F, G.fade(0xFFFF9A40, alpha * flick));
         }
      }
   }

   @Environment(EnvType.CLIENT)
   static final class AuroraStorm extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 6.0F, 18.0F, 40.0F, 65.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x040A0C, 0x061214, 0x0A1E1E, 0x0E2A24, 0x0C201E, 0x081614, 0x040C0C, 0x020606};
      private final StarField stars = new StarField(6961, 700, 0.0F);
      private final Curtain[] curtains = new Curtain[]{
         new Curtain(21, 20.0F, 160.0F, 10.0F, 34.0F, 0xFF2EFFB0, 0xFF10C89A, 0xFF8A6BFF).alpha(0.95F).wave(1.4F),
         new Curtain(22, -60.0F, 130.0F, 14.0F, 30.0F, 0xFF4AFFD0, 0xFF18B8D8, 0xFFB06BFF).alpha(0.8F).lean(-5.0F).wave(1.1F),
         new Curtain(23, 120.0F, 145.0F, 8.0F, 40.0F, 0xFF20E8FF, 0xFF2A9CFF, 0xFF6AFFB0).alpha(0.75F).lean(7.0F).wave(1.6F),
         new Curtain(24, 250.0F, 150.0F, 12.0F, 36.0F, 0xFF9A5CFF, 0xFF4A6BFF, 0xFF2EFFC0).alpha(0.7F),
         new Curtain(25, 0.0F, 200.0F, 6.0F, 46.0F, 0xFF12FF8A, 0xFF0AA86A, 0xFF40D8FF).alpha(0.6F).wave(0.9F),
         new Curtain(26, 190.0F, 120.0F, 16.0F, 28.0F, 0xFFFF6BD8, 0xFF8A4AFF, 0xFF8AD8FF).alpha(0.45F).lean(4.0F)
      };
      private final float[] tmp = new float[3];

      AuroraStorm() {
         super("aurorastorm", 0x04100E, 0x3CFFB0);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 240.0F, -3.0F, 14.0F, 0x7040E0A0);
         g.horizonGlow(150.0F, 160.0F, -3.0F, 10.0F, 0x405040C0);
         this.stars.draw(g, c.count(700, c.stars), 0.7F, 1.0F, 0.5F, 0, 0.0F);
         g.ring(SkyTex.NEBULA2, (float)(t * 0.4), 10.0F, 58.0F, 0.05F, 0.95F, 0x40204050, 0.35F, 0.4F);
         float strength = 1.0F - 0.4F * c.rain;
         int n = c.quality == 0 ? 3 : this.curtains.length;

         for (int i = 0; i < n; i++) {
            this.curtains[i].draw(g, strength);
         }

         g.sprite(SkyTex.GLOW, 0.0F, 26.0F, 150.0F, 0.0F, G.fade(0x3020E090, 0.8F + 0.2F * G.sin(t * 0.3)));
         Fx.shootingStars(g, 1, 12.0F, 0.45F, 0xFFB0FFD8, 6962, this.tmp);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Aether extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 8.0F, 22.0F, 44.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x8C9CC8, 0xA8B8DC, 0xC8D4EC, 0xE8E4DC, 0xF0E8D8, 0xD8D8E8, 0xB8C8E8, 0xA8BCE0};
      private final StarField sparkles = new StarField(6971, 80, 24.0F);

      Aether() {
         super("aether", 0xD8DCE8, 0xFFF0D0);
         this.fadeTop = 11.0F;
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -2.0F, 14.0F, 0x50FFF4E0);
         float az = -30.0F;
         float el = 55.0F;
         float breathe = 0.85F + 0.15F * G.sin(t * 0.18);
         g.sprite(SkyTex.GLOW, az, el, 170.0F, 0.0F, G.fade(0x40FFF0D0, breathe));
         g.sprite(SkyTex.GLOW, az, el, 56.0F, 0.0F, G.fade(0x70FFF8E8, breathe));
         g.sprite(SkyTex.DISC, az, el, 4.6F, 0.0F, 0xFFFFFDF2);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.1 + 40.0), 26.0F, 34.0F, 0.0F, 1.0F, 0x50FFFFFF, 0.15F, 0.4F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.08 + 150.0), 34.0F, 44.0F, 0.1F, 0.9F, 0x40FFFFFF, 0.2F, 0.45F);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.06 + 260.0), 20.0F, 28.0F, 0.2F, 0.8F, 0x30FFEFD8, 0.2F, 0.4F);
         this.sparkles.drawSparkles(g, c.count(30, c.particles), 0.5F, 1.2F, 0xFFFFF4D8);
         Fx.birds(g, 70.0F, 30.0F, t * 0.9);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Lagoon extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 8.0F, 22.0F, 45.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x2E8AA0, 0x46A2B4, 0x6AC2CC, 0x9FE0E0, 0xB8ECEA, 0x7ACAD4, 0x5AAEC4, 0x4C9CB4};
      private final StarField sparkles = new StarField(6981, 90, 30.0F);

      Lagoon() {
         super("lagoon", 0xA8DCE0, 0xFFFFFF);
         this.fadeTop = 10.0F;
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -2.0F, 12.0F, 0x60E8FFFF);
         g.sprite(SkyTex.GLOW, 15.0F, 38.0F, 130.0F, 0.0F, 0x38FFFFFF);
         g.sprite(SkyTex.GLOW, 15.0F, 38.0F, 42.0F, 0.0F, 0x70FFFFFF);
         g.sprite(SkyTex.DISC, 15.0F, 38.0F, 6.0F, 0.0F, 0xFFFFFCEE);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.16 + 50.0), 3.0F, 12.0F, 0.0F, 1.0F, 0xA0FFFFFF, 0.15F, 0.4F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.12 + 160.0), 12.0F, 24.0F, 0.1F, 0.9F, 0x80E8FFFF, 0.2F, 0.45F);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.08 + 270.0), 24.0F, 34.0F, 0.2F, 0.8F, 0x50FFFFFF, 0.25F, 0.45F);
         this.sparkles.drawSparkles(g, c.count(40, c.particles), 0.6F, 1.2F, 0xFFFFFFFF);
         Fx.birds(g, -30.0F, 20.0F, t);
         Fx.birds(g, 130.0F, 14.0F, t * 0.8);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Coral extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 5.0F, 14.0F, 32.0F, 58.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x3A1430, 0x4E1A38, 0x7A2A48, 0xB84A5C, 0xE88078, 0xF0A088, 0x8A4260, 0x5A2A48};
      private final StarField stars = new StarField(6991, 260, 34.0F);
      private final float[] tmp = new float[3];

      Coral() {
         super("coral", 0x8A3A58, 0xFFC0A8);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(260, c.stars), 0.35F, 1.0F, 0.4F, 0xFFFFD8C0, 0.4F);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 16.0F, 0x90FF9A78);
         float az = 8.0F;
         float el = 3.0F;
         float breathe = 0.88F + 0.12F * G.sin(t * 0.22);
         g.sprite(SkyTex.GLOW, az, el, 150.0F, 0.0F, G.fade(0x60FFB088, breathe));
         g.sprite(SkyTex.GLOW, az, el, 52.0F, 0.0F, G.fade(0x90FFD8B8, breathe));
         g.sprite(SkyTex.DISC, az, el, 8.5F, 0.0F, 0xFFFFF0D8);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.22 + 30.0), 2.0F, 11.0F, 0.0F, 1.0F, 0x90FFC0A0, 0.2F, 0.4F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.15 + 140.0), 10.0F, 24.0F, 0.1F, 0.9F, 0x70905070, 0.25F, 0.45F);
         g.ring(SkyTex.NEBULA2, (float)(t * 0.4), 22.0F, 46.0F, 0.2F, 0.8F, 0x40281C44, 0.3F, 0.45F);
         Fx.birds(g, -90.0F, 12.0F, t);
         Fx.shootingStars(g, 1, 24.0F, 0.35F, 0xFFFFD0C0, 6992, this.tmp);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Tundra extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 8.0F, 22.0F, 44.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x8E9AA6, 0xA4B0BA, 0xBCC8D0, 0xD2DCE2, 0xC8D2DA, 0xB4BEC8, 0xA2ACB6, 0x9AA4AE};
      private final StarField sparkles = new StarField(7001, 50, 24.0F);

      Tundra() {
         super("tundra", 0xC4CEd6, 0xF4FAFF);
         this.fadeTop = 16.0F;
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 18.0F, 0x60F4FAFF);
         g.sprite(SkyTex.GLOW, -25.0F, 12.0F, 190.0F, 0.0F, G.fade(0x40FFFFFF, 0.75F));
         g.sprite(SkyTex.GLOW, -25.0F, 12.0F, 62.0F, 0.0F, G.fade(0x50FFFFFF, 0.7F));
         g.sprite(SkyTex.DISC, -25.0F, 12.0F, 3.0F, 0.0F, G.fade(0xFFFFFCEE, 0.45F));
         int n = c.quality == 0 ? 2 : 3;

         for (int i = 0; i < n; i++) {
            float el = 20.0F + i * 10.0F;
            g.ring(SkyTex.CLOUDS, (float)(t * (0.05 + 0.03 * i) + i * 90.0), el, el + 12.0F, 0.0F, 1.0F, G.fade(0xFFE8F0F6, 0.30F), 0.2F, 0.5F);
         }

         int m = c.count(150, c.particles);

         for (int i = 0; i < m; i++) {
            float depth = G.rnd(7002, i);
            float sp = 0.03F + 0.05F * depth;
            float q = G.fract(t * sp + G.rnd(7003, i));
            float az = 360.0F * G.rnd(7004, i) + 8.0F * G.sin(t * (0.3 + 0.4 * depth) + i);
            float el = 78.0F - q * 92.0F;
            float alpha = G.smooth(0.0F, 0.1F, q) * G.smooth(1.0F, 0.85F, q) * (0.35F + 0.5F * depth);
            g.sprite(SkyTex.FLAKE, az, el, 0.6F + 1.2F * depth, (float)(t * 18.0 * (G.rnd(7005, i) - 0.5F)), G.fade(0xF0FFFFFF, alpha));
         }

         this.sparkles.drawSparkles(g, c.count(24, c.particles), 0.5F, 1.6F, 0xFFFFFFFF);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class DesertDusk extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 5.0F, 14.0F, 32.0F, 60.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x2A1208, 0x4A1E0A, 0x8A3A0E, 0xD8701C, 0xF0A040, 0xC07028, 0x6A3410, 0x3A1C0A};
      private final StarField stars = new StarField(7011, 300, 40.0F);

      DesertDusk() {
         super("desertdusk", 0x8A4416, 0xFFC878);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(300, c.stars), 0.3F, 1.0F, 0.4F, 0xFFFFE0B0, 0.35F);
         g.horizonGlow(0.0F, 320.0F, -4.0F, 20.0F, 0xA0FFB060);
         float az = -6.0F;
         float el = 4.0F;
         float breathe = 0.9F + 0.1F * G.sin(t * 0.19);
         g.sprite(SkyTex.GLOW, az, el, 170.0F, 0.0F, G.fade(0x60FFC070, breathe));
         g.sprite(SkyTex.GLOW, az, el, 62.0F, 0.0F, G.fade(0x90FFE0A8, breathe));
         g.sprite(SkyTex.DISC, az, el, 9.0F, 0.0F, 0xFFFFF6D8);
         int n = c.quality == 0 ? 3 : 6;

         for (int i = 0; i < n; i++) {
            float el0 = 1.0F + i * 3.0F;
            g.ring(
               SkyTex.CLOUDS,
               (float)(t * (0.25 + 0.1 * i) + i * 55.0),
               el0,
               el0 + 6.0F,
               0.0F,
               1.0F,
               G.fade(0xFFFFD8A0, 0.22F - 0.02F * i),
               0.2F,
               0.45F
            );
         }

         g.cap(SkyTex.NEBULA1, 0.0F, 90.0F, 80.0F, (float)(t * 0.5), 1.2F, 0x60A86018, 0.5F);
         int m = c.count(120, c.particles);

         for (int i = 0; i < m; i++) {
            float depth = G.rnd(7012, i);
            float sp = 0.02F + 0.04F * depth;
            float q = G.fract(t * sp + G.rnd(7013, i));
            float az2 = 360.0F * G.rnd(7014, i) + 10.0F * G.sin(t * 0.4 + i);
            float el2 = 1.0F + 40.0F * G.rnd(7015, i) * G.rnd(7016, i);
            float alpha = (0.2F + 0.4F * depth) * (0.6F + 0.4F * G.sin(t * 1.6 + i));
            g.sprite(SkyTex.DOT, az2, el2, 0.4F + 0.5F * depth, 0.0F, G.fade(0xFFFFDCA8, alpha));
         }
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Zenith extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -6.0F, 0.0F, 6.0F, 16.0F, 40.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x9FC4E8, 0xB8D4F0, 0xD0E4F6, 0xE0EEFA, 0xC8DEF4, 0x9CBEE8, 0x6E9CE0, 0x5A8CD8};
      private final StarField sparkles = new StarField(7021, 30, 50.0F);

      Zenith() {
         super("zenith", 0xD6E6F6, 0xFFFFFF);
         this.fadeTop = 7.0F;
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -2.0F, 9.0F, 0x60FFFFFF);
         g.sprite(SkyTex.GLOW, 0.0F, 78.0F, 96.0F, 0.0F, 0x38FFFFFF);
         g.sprite(SkyTex.GLOW, 0.0F, 78.0F, 30.0F, 0.0F, 0x70FFFFFF);
         g.sprite(SkyTex.DISC, 0.0F, 78.0F, 4.4F, 0.0F, 0xFFFFFFFF);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.09 + 20.0), 30.0F, 34.0F, 0.0F, 1.0F, 0x40FFFFFF, 0.1F, 0.4F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.07 + 130.0), 38.0F, 42.0F, 0.1F, 0.9F, 0x30FFFFFF, 0.15F, 0.45F);
         this.sparkles.drawSparkles(g, c.count(14, c.particles), 0.35F, 1.0F, 0xFFFFFFFF);
         Fx.birds(g, 200.0F, 22.0F, t * 0.8);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class TripleMoon extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 8.0F, 22.0F, 45.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x080C18, 0x0C1222, 0x14203A, 0x1E3050, 0x182844, 0x0E1830, 0x080C1C, 0x05070E};
      private final StarField stars = new StarField(7031, 900, 0.0F);
      private final float[] tmp = new float[3];

      TripleMoon() {
         super("triplemoon", 0x0A1020, 0xD8E4FF);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(900, c.stars), 0.8F, 1.0F, 0.5F, 0, 0.0F);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 16.0F, 0x50304068);
         float a = 0.85F + 0.15F * G.sin(t * 0.2);
         float b = 0.8F + 0.2F * G.sin(t * 0.27 + 2.0);
         float az1 = -35.0F + 4.0F * G.sin(t * 0.06);
         float az2 = 10.0F - 3.0F * G.sin(t * 0.05);
         float az3 = 48.0F + 5.0F * G.sin(t * 0.07);
         g.sprite(SkyTex.GLOW, az1, 30.0F, 80.0F, 0.0F, G.fade(0x50C8D8FF, a));
         g.sprite(SkyTex.GLOW, az1, 30.0F, 26.0F, 0.0F, G.fade(0x80E8F0FF, a));
         g.sprite(SkyTex.MOON, az1, 30.0F, 12.0F, 0.0F, 0xFFF4F8FF);
         g.sprite(SkyTex.GLOW, az2, 48.0F, 40.0F, 0.0F, G.fade(0x4080B0FF, b));
         g.sprite(SkyTex.GLOW, az2, 48.0F, 14.0F, 0.0F, G.fade(0x70A8C8FF, b));
         g.sprite(SkyTex.MOON, az2, 48.0F, 6.4F, 0.0F, 0xFFD0E0FF);
         g.sprite(SkyTex.GLOW, az3, 17.0F, 28.0F, 0.0F, G.fade(0x40FFC880, b));
         g.sprite(SkyTex.GLOW, az3, 17.0F, 10.0F, 0.0F, G.fade(0x70FFD8A0, b));
         g.sprite(SkyTex.MOON, az3, 17.0F, 4.6F, 0.0F, 0xFFFFE8C0);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.16 + 50.0), 4.0F, 16.0F, 0.0F, 1.0F, 0x50203048, 0.25F, 0.45F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.11 + 200.0), 12.0F, 30.0F, 0.1F, 0.9F, 0x40182034, 0.25F, 0.4F);
         Fx.shootingStars(g, 2, 18.0F, 0.4F, 0xFFE0ECFF, 7032, this.tmp);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class MoonShard extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 8.0F, 22.0F, 45.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x07080F, 0x0B0D18, 0x121728, 0x1B243C, 0x161E34, 0x0C1226, 0x070A16, 0x04050B};
      private final StarField stars = new StarField(7041, 1000, 0.0F);
      private final float[] tmp = new float[3];

      MoonShard() {
         super("moonshard", 0x090C16, 0xFFE8C8);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(1000, c.stars), 0.85F, 1.0F, 0.5F, 0, 0.0F);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 15.0F, 0x40283048);
         float az = 22.0F;
         float el = 38.0F;
         float breathe = 0.9F + 0.1F * G.sin(t * 0.22);
         g.sprite(SkyTex.GLOW, az, el, 96.0F, 0.0F, G.fade(0x40FFE8C8, breathe));
         g.sprite(SkyTex.GLOW, az, el, 40.0F, 0.0F, G.fade(0x60FFF4E0, breathe));
         int n = c.quality == 0 ? 4 : 7;

         for (int i = 0; i < n; i++) {
            float off = (i - (n - 1) / 2.0F) * 3.4F;
            float daz = off + 2.0F * G.sin(t * 0.13 + i * 1.7);
            float del = off * 0.7F - 2.0F + 1.6F * G.cos(t * 0.11 + i * 2.1);
            float size = 7.5F - 0.7F * Math.abs(off);
            g.sprite(SkyTex.MOON, az + daz, el + del, size, off * 9.0F, 0xFFF6EEDC);
            g.sprite(SkyTex.GLOW, az + daz, el + del, size * 1.9F, 0.0F, G.fade(0x30FFE8C8, 0.5F));
         }

         int m = c.count(120, c.particles);

         for (int i = 0; i < m; i++) {
            float depth = G.rnd(7042, i);
            float q = G.fract(t * (0.02 + 0.03 * depth) + G.rnd(7043, i));
            float rad = 12.0F + q * 34.0F;
            float ang = G.rnd(7044, i) * 360.0F + (float)(t * 3.0);
            float alpha = G.smooth(0.0F, 0.1F, q) * (1.0F - q) * 0.6F;
            g.sprite(SkyTex.DOT, az + rad * G.cos(G.rad(ang)), el + rad * 0.6F * G.sin(G.rad(ang)), 0.4F + 0.5F * depth, 0.0F, G.fade(0xFFFFF0D8, alpha));
         }

         g.ring(SkyTex.CLOUDS, (float)(t * 0.14 + 70.0), 6.0F, 18.0F, 0.0F, 1.0F, 0x401C2438, 0.25F, 0.45F);
         Fx.shootingStars(g, 2, 21.0F, 0.4F, 0xFFFFE8D0, 7045, this.tmp);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class CrimsonForest extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 6.0F, 18.0F, 38.0F, 64.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x2A0608, 0x3C080A, 0x5C0C10, 0x8A1418, 0x6E1014, 0x460A10, 0x2A060A, 0x180406};
      private final StarField stars = new StarField(7051, 60, 60.0F);

      CrimsonForest() {
         super("crimsonforest", 0x3A080C, 0xFF5A48);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -4.0F, 26.0F, 0x80C03020);
         g.sprite(SkyTex.GLOW, 0.0F, 4.0F, 180.0F, 0.0F, G.fade(0x40FF6A40, 0.8F + 0.2F * G.sin(t * 0.25)));
         g.cap(SkyTex.NEBULA1, 0.0F, 90.0F, 88.0F, (float)(t * 0.4), 1.4F, 0x50100406, 0.45F);
         int trunks = c.quality == 0 ? 6 : 12;

         for (int i = 0; i < trunks; i++) {
            float az = 360.0F * G.rnd(7052, i);
            float w = 0.6F + 1.8F * G.rnd(7053, i);
            float h = 12.0F + 34.0F * G.rnd(7054, i);
            float sway = 1.5F * G.sin(t * 0.15 + i);
            g.band(
               SkyTex.BEAM,
               az - w + sway,
               az + w + sway,
               -2.0F,
               h,
               0.0F,
               1.0F,
               0.0F,
               1.0F,
               G.fade(0xFF3A0A0C, 0.55F),
               0.3F,
               0.5F,
               0.1F
            );
            g.sprite(SkyTex.GLOW, az + sway, h * 0.7F, 6.0F + 6.0F * G.rnd(7055, i), 0.0F, G.fade(0x40FF3020, 0.35F));
         }

         int n = c.count(170, c.particles);

         for (int i = 0; i < n; i++) {
            float depth = G.rnd(7056, i);
            float sp = 0.012F + 0.028F * depth;
            float q = G.fract(t * sp + G.rnd(7057, i));
            float el = 1.0F + q * 62.0F;
            float az = 360.0F * G.rnd(7058, i) + 7.0F * G.sin(t * (0.2 + 0.3 * depth) + i);
            float alpha = G.smooth(0.0F, 0.14F, q) * G.smooth(1.0F, 0.8F, q);
            float flick = 0.55F + 0.45F * G.sin(t * (1.6 + 2.2 * depth) + i * 2.2);
            g.sprite(SkyTex.GLOW, az, el, 1.6F + 2.4F * depth, 0.0F, G.fade(0xFFFF4A30, alpha * flick * 0.3F));
            g.sprite(SkyTex.DOT, az, el, 0.4F + 0.4F * depth, 0.0F, G.fade(0xFFFF8070, alpha * flick));
         }

         this.stars.drawSparkles(g, c.count(20, c.particles), 0.3F, 1.0F, 0xFFFF9070);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Hyperdrive extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 6.0F, 18.0F, 40.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x05060E, 0x080A18, 0x0E1428, 0x1A2440, 0x243256, 0x101830, 0x060A16, 0x03040A};
      private final StarField stars = new StarField(7061, 500, 0.0F);
      private final float[] a = new float[3];
      private final float[] b = new float[3];

      Hyperdrive() {
         super("hyperdrive", 0x070A18, 0xB8D8FF);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         float az = 0.0F;
         float el = 34.0F;
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(500, c.stars), 0.5F, 1.0F, 0.5F, 0, 0.0F);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 14.0F, 0x40304070);
         g.sprite(SkyTex.GLOW, az, el, 130.0F, 0.0F, G.fade(0x50A8D8FF, 0.85F + 0.15F * G.sin(t * 2.0)));
         g.sprite(SkyTex.GLOW, az, el, 44.0F, 0.0F, G.fade(0x90FFFFFF, 1.0F));
         g.sprite(SkyTex.GLOW, az, el, 16.0F, 0.0F, G.fade(0xE0FFFFFF, 1.0F));
         int n = c.count(190, c.particles);

         for (int i = 0; i < n; i++) {
            float spread = 1.0F + 40.0F * G.rnd(7062, i);
            float ang = 360.0F * G.rnd(7063, i) + (float)(t * (30.0 + 60.0 * G.rnd(7064, i)));
            float len = 0.25F + 0.5F * G.rnd(7065, i);
            float oa = spread * G.cos(G.rad(ang));
            float oe = spread * 0.55F * G.sin(G.rad(ang));
            float q = G.fract(t * (0.5 + 0.7 * G.rnd(7066, i)) + G.rnd(7067, i));
            float fade = G.smooth(0.0F, 0.15F, q) * G.smooth(1.0F, 0.7F, q);
            G.dir(az + oa, el + oe, this.a);
            G.dir(az + oa * (1.0F - len), el + oe * (1.0F - len), this.b);
            int col = G.rnd(7068, i) < 0.35F ? 0xFFA8D0FF : 0xFFFFFFFF;
            g.segment(SkyTex.LINE, this.a, this.b, 0.08F, 0.02F, G.fade(col, 0.55F * fade), G.fade(col, 0.0F), false);
            if (spread < 12.0F) {
               g.segment(SkyTex.GLOW, this.a, this.b, 0.5F, 0.1F, G.fade(col, 0.14F * fade), G.fade(col, 0.0F), false);
            }
         }

         g.sprite(SkyTex.RING, az, el, 60.0F, (float)(t * 30.0), G.fade(0x40C0E8FF, 0.8F));
         g.sprite(SkyTex.RING, az, el, 96.0F, (float)(-t * 20.0), G.fade(0x28A8D0FF, 0.8F));
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Reef extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 8.0F, 22.0F, 45.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x0B3A46, 0x10505C, 0x1A6A74, 0x2A8A90, 0x1E7078, 0x125258, 0x0A343C, 0x062228};
      private final float[] a = new float[3];
      private final float[] b = new float[3];

      Reef() {
         super("reef", 0x14545C, 0xA8F0E0);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 14.0F, 0x5020A0B0);
         g.cap(SkyTex.CAUSTICS, 0.0F, 90.0F, 110.0F, (float)(t * 0.6), 1.3F, 0x60A8F0E0, 0.45F);
         g.cap(SkyTex.CAUSTICS, 0.0F, 90.0F, 90.0F, (float)(-t * 0.9 + 40.0), 1.0F, 0x4820C0C8, 0.5F);
         int shafts = c.quality == 0 ? 3 : 6;

         for (int i = 0; i < shafts; i++) {
            float az = 360.0F * G.rnd(7071, i) + 3.0F * G.sin(t * 0.12 + i);
            float w = 2.0F + 3.6F * G.rnd(7072, i);
            float alpha = 0.10F + 0.10F * G.rnd(7073, i) * (0.7F + 0.3F * G.sin(t * 0.35 + i));
            g.band(SkyTex.BEAM, az - w, az + w, 4.0F, 82.0F, 0.0F, 1.0F, 1.0F, 0.0F, G.fade(0xFFB0F4E8, alpha), 0.35F, 0.55F, 0.15F);
         }

         int n = c.count(150, c.particles);

         for (int i = 0; i < n; i++) {
            float depth = G.rnd(7074, i);
            float sp = 0.02F + 0.05F * depth;
            float q = G.fract(t * sp + G.rnd(7075, i));
            float el = 1.0F + q * 80.0F;
            float az = 360.0F * G.rnd(7076, i) + 4.0F * G.sin(t * 1.2 + i * 2.0);
            float alpha = G.smooth(0.0F, 0.12F, q) * G.smooth(1.0F, 0.8F, q) * (0.3F + 0.5F * depth);
            g.sprite(SkyTex.BUBBLE, az, el, 0.6F + 1.4F * depth, 0.0F, G.fade(0xFFC8FFF4, alpha));
         }

         g.sprite(SkyTex.GLOW, 0.0F, 88.0F, 140.0F, 0.0F, G.fade(0x30A0F0E0, 0.9F));
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Blossom extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 8.0F, 22.0F, 45.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x6A8CC8, 0x86A4D8, 0xA8C0E8, 0xC8D8F4, 0xDCE4F8, 0xB8C4EC, 0x96A8E0, 0x8498D8};
      private final StarField sparkles = new StarField(7081, 60, 20.0F);

      Blossom() {
         super("blossom", 0xC8D8F4, 0xFFD8E8);
         this.fadeTop = 10.0F;
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 16.0F, 0x50FFD8E8);
         g.sprite(SkyTex.GLOW, 30.0F, 30.0F, 130.0F, 0.0F, 0x38FFFFFF);
         g.sprite(SkyTex.GLOW, 30.0F, 30.0F, 42.0F, 0.0F, 0x60FFF4F8);
         g.sprite(SkyTex.DISC, 30.0F, 30.0F, 5.4F, 0.0F, 0xFFFFF6F0);
         g.ring(SkyTex.CLOUDS, (float)(t * 0.14 + 40.0), 6.0F, 16.0F, 0.0F, 1.0F, 0x80FFFFFF, 0.2F, 0.4F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.1 + 150.0), 16.0F, 28.0F, 0.1F, 0.9F, 0x60F4E8FF, 0.25F, 0.45F);
         int n = c.count(150, c.particles);

         for (int i = 0; i < n; i++) {
            float depth = G.rnd(7082, i);
            float sp = 0.03F + 0.05F * depth;
            float q = G.fract(t * sp + G.rnd(7083, i));
            float az = 360.0F * G.rnd(7084, i) + 14.0F * G.sin(t * (0.4 + 0.5 * depth) + i * 1.7);
            float el = 74.0F - q * 84.0F;
            float alpha = G.smooth(0.0F, 0.1F, q) * G.smooth(1.0F, 0.86F, q) * (0.4F + 0.5F * depth);
            int col = G.rnd(7085, i) < 0.5F ? 0xFFFFC0D8 : 0xFFFFE0EC;
            g.sprite(SkyTex.PETAL, az, el, 0.8F + 1.6F * depth, (float)(t * 60.0 * (G.rnd(7086, i) - 0.5F)), G.fade(col, alpha));
         }

         this.sparkles.drawSparkles(g, c.count(20, c.particles), 0.45F, 1.0F, 0xFFFFF0F8);
         Fx.birds(g, -120.0F, 18.0F, t * 0.9);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class Inkwash extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 8.0F, 22.0F, 44.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x14161A, 0x1C2024, 0x2A2E34, 0x3C4048, 0x4A4E56, 0x383C44, 0x24282E, 0x1A1D22};
      private final StarField sparkles = new StarField(7091, 40, 30.0F);
      private final float[] a = new float[3];
      private final float[] b = new float[3];

      Inkwash() {
         super("inkwash", 0x22262C, 0xE8ECF0);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 18.0F, 0x40C8D0D8);
         g.sprite(SkyTex.GLOW, 0.0F, 20.0F, 170.0F, 0.0F, G.fade(0x30FFFFFF, 0.8F));
         g.sprite(SkyTex.DISC, 0.0F, 20.0F, 5.0F, 0.0F, G.fade(0xFFF4F8FC, 0.75F));
         int caps = c.quality == 0 ? 3 : 6;

         for (int i = 0; i < caps; i++) {
            float az = 360.0F * G.rnd(7092, i);
            float el = 12.0F + 60.0F * G.rnd(7093, i);
            float size = 16.0F + 30.0F * G.rnd(7094, i);
            float alpha = 0.16F + 0.16F * G.rnd(7095, i);
            g.cap(SkyTex.CLOUDS, az, el, size, (float)(t * (2.0 + 3.0 * G.rnd(7096, i))), 0.7F, G.fade(0xFFE8ECF0, alpha), 0.35F);
            g.cap(SkyTex.CLOUDS, az, el, size * 0.6F, (float)(-t * 3.0), 0.9F, G.fade(0xFFF4F8FC, alpha * 0.8F), 0.45F);
         }

         int strokes = c.quality == 0 ? 3 : 7;

         for (int i = 0; i < strokes; i++) {
            float az0 = 360.0F * G.rnd(7097, i);
            float el0 = 8.0F + 50.0F * G.rnd(7098, i);
            float len = 12.0F + 30.0F * G.rnd(7099, i);
            G.dir(az0, el0, this.a);
            G.dir(az0 + len, el0 - 6.0F + 12.0F * G.rnd(7101, i), this.b);
            g.segment(SkyTex.LINE, this.a, this.b, 0.9F, 0.15F, G.fade(0xFFDCE0E8, 0.30F), G.fade(0xFFDCE0E8, 0.0F), false);
            g.segment(SkyTex.GLOW, this.a, this.b, 3.2F, 0.6F, G.fade(0xFF9AA0A8, 0.10F), G.fade(0xFF9AA0A8, 0.0F), false);
         }

         this.sparkles.drawSparkles(g, c.count(14, c.particles), 0.25F, 1.0F, 0xFFFFFFFF);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class NeonRain extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 5.0F, 12.0F, 28.0F, 55.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x08040E, 0x0E0618, 0x1A0A28, 0x2E1044, 0x4A1460, 0x220C36, 0x0E0618, 0x06030A};
      private final StarField stars = new StarField(7111, 200, 30.0F);
      private final float[] a = new float[3];
      private final float[] b = new float[3];

      NeonRain() {
         super("neonrain", 0x140A22, 0xFF3AA8);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         float pulse = 0.8F + 0.2F * G.sin(t * 0.6);
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(200, c.stars), 0.4F, 1.0F, 0.5F, 0xFFFFB0E8, 0.4F);
         g.horizonGlow(0.0F, 200.0F, -3.0F, 14.0F, 0x90FF2A9A);
         g.horizonGlow(180.0F, 160.0F, -3.0F, 12.0F, 0x602A8AFF);
         g.sprite(SkyTex.GLOW, 20.0F, 4.0F, 120.0F, 0.0F, G.fade(0x60FF3AA8, pulse));
         g.sprite(SkyTex.GLOW, 210.0F, 4.0F, 96.0F, 0.0F, G.fade(0x502AB8FF, pulse));
         int beams = c.quality == 0 ? 5 : 10;

         for (int i = 0; i < beams; i++) {
            float az = 360.0F * G.rnd(7112, i);
            float w = 0.7F + 2.0F * G.rnd(7113, i);
            float h = 10.0F + 30.0F * G.rnd(7114, i);
            int col = G.rnd(7115, i) < 0.5F ? 0xFFFF3AA8 : 0xFF2AF0FF;
            g.band(SkyTex.BEAM, az - w, az + w, -2.0F, h, 0.0F, 1.0F, 0.0F, 1.0F, G.fade(col, 0.16F), 0.35F, 0.55F, 0.15F);
            g.hline(az - w, az + w, h, 0.4F, G.fade(col, 0.35F));
         }

         int n = c.count(200, c.particles);

         for (int i = 0; i < n; i++) {
            float q = G.fract(t * (0.7 + 0.5 * G.rnd(7116, i)) + G.rnd(7117, i));
            float az = 360.0F * G.rnd(7118, i) + q * 20.0F;
            float el = 72.0F - q * 88.0F;
            float alpha = G.smooth(0.0F, 0.1F, q) * G.smooth(1.0F, 0.86F, q) * 0.55F;
            G.dir(az, el, this.a);
            G.dir(az - 2.0F, el - 3.4F, this.b);
            int col = G.rnd(7119, i) < 0.5F ? 0xFFFF8AD0 : 0xFF8AE8FF;
            g.segment(SkyTex.LINE, this.a, this.b, 0.07F, 0.03F, G.fade(col, alpha), G.fade(col, 0.0F), false);
         }

         g.ring(SkyTex.CLOUDS, (float)(t * 0.4), 8.0F, 26.0F, 0.0F, 1.0F, 0x50180A28, 0.25F, 0.45F);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class BinarySun extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 8.0F, 22.0F, 44.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x7A3A18, 0x9A4C1E, 0xC06A28, 0xE08C40, 0xF0AC68, 0xC08040, 0x8A5228, 0x6A3E1E};
      private final StarField sparkles = new StarField(7121, 40, 40.0F);

      BinarySun() {
         super("binarysun", 0xC07A38, 0xFFE0A0);
         this.fadeTop = 10.0F;
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 16.0F, 0x70FFC070);
         float breathe = 0.88F + 0.12F * G.sin(t * 0.24);
         g.sprite(SkyTex.GLOW, -14.0F, 12.0F, 170.0F, 0.0F, G.fade(0x50FFB060, breathe));
         g.sprite(SkyTex.GLOW, -14.0F, 12.0F, 56.0F, 0.0F, G.fade(0x80FFD090, breathe));
         g.sprite(SkyTex.DISC, -14.0F, 12.0F, 9.0F, 0.0F, 0xFFFFF0C8);
         g.sprite(SkyTex.GLOW, 22.0F, 24.0F, 74.0F, 0.0F, G.fade(0x40C0E0FF, breathe));
         g.sprite(SkyTex.GLOW, 22.0F, 24.0F, 24.0F, 0.0F, G.fade(0x80E8F4FF, breathe));
         g.sprite(SkyTex.DISC, 22.0F, 24.0F, 4.4F, 0.0F, 0xFFF4FBFF);
         g.sprite(SkyTex.RING, -14.0F, 12.0F, 46.0F, (float)(t * 6.0), G.fade(0x30FFE0A0, breathe));
         g.sprite(SkyTex.RING, -14.0F, 12.0F, 68.0F, (float)(-t * 4.0), G.fade(0x20C0D8FF, breathe));
         g.ring(SkyTex.CLOUDS, (float)(t * 0.18 + 40.0), 3.0F, 12.0F, 0.0F, 1.0F, 0x80FFD8A0, 0.2F, 0.4F);
         g.ring(SkyTex.CLOUDS, (float)(-t * 0.13 + 160.0), 14.0F, 26.0F, 0.1F, 0.9F, 0x60D8A070, 0.25F, 0.45F);
         this.sparkles.drawSparkles(g, c.count(18, c.particles), 0.4F, 1.0F, 0xFFFFF0D0);
         Fx.birds(g, 110.0F, 16.0F, t * 0.85);
      }
   }

   @Environment(EnvType.CLIENT)
   static final class GasGiant extends Sky {
      private static final float[] DOME_EL = new float[]{-90.0F, -8.0F, 0.0F, 8.0F, 22.0F, 45.0F, 70.0F, 90.0F};
      private static final int[] DOME_COL = new int[]{0x06080E, 0x0A0E18, 0x101826, 0x18243A, 0x1C2C48, 0x101A2C, 0x080D16, 0x04060A};
      private final StarField stars = new StarField(7131, 900, 0.0F);
      private final float[] tmp = new float[3];

      GasGiant() {
         super("gasgiant", 0x0A1018, 0xE8C890);
      }

      @Override
      public void render(G g, SkyCtx c) {
         double t = c.time;
         g.dome(DOME_EL, DOME_COL);
         this.stars.draw(g, c.count(900, c.stars), 0.85F, 1.0F, 0.5F, 0, 0.0F);
         g.horizonGlow(0.0F, 360.0F, -3.0F, 15.0F, 0x50304058);
         float az = 12.0F + 2.0F * G.sin(t * 0.03);
         float el = 52.0F;
         g.sprite(SkyTex.GLOW, az, el, 120.0F, 0.0F, G.fade(0x30FFE8C0, 0.85F + 0.15F * G.sin(t * 0.2)));
         Fx.planetWithRing(g, az, el, 30.0F, -18.0F + (float)(t * 0.4), 16.0F, this.tmp);
         g.sprite(SkyTex.GLOW, az + 6.0F, el - 8.0F, 40.0F, 0.0F, G.fade(0x28FFD8A0, 0.9F));
         float maz = az - 54.0F;
         float mel = el - 22.0F;
         g.sprite(SkyTex.GLOW, maz, mel, 26.0F, 0.0F, G.fade(0x40C0D0FF, 0.9F));
         g.sprite(SkyTex.MOON, maz, mel, 4.6F, 0.0F, 0xFFDCE8FF);
         g.ring(SkyTex.NEBULA1, (float)(t * 0.2), 20.0F, 52.0F, 0.0F, 1.0F, 0x30102038, 0.3F, 0.45F);
         Fx.shootingStars(g, 2, 19.0F, 0.45F, 0xFFFFE8C8, 7132, this.tmp);
      }
   }
}
