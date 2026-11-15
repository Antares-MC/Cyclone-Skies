package dev.valence.cosmetics;

import dev.valence.gui.Theme;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 3D back cosmetics. Geometry is authored in player-local block space (feet origin, +Y up, +Z in
 * front of the player) and is animated from the render clock.
 *
 * <p>Every style is built on a shared "arm" curve that leaves the shoulder blades, sweeps outward
 * and slightly up, and trails back behind the body, so the wings sit on the back instead of ringing
 * the head.
 */
@Environment(EnvType.CLIENT)
public final class Wings {
   public static final float ANCHOR_Y = 1.40F;
   private static final float ROOT_Y = 1.28F;

   public static final List<Wings.Wing> ALL = List.of(
      new Wings.Wing("angel_white", "Angel White", "Celestial", Wings.Style.FEATHER, 0xFFF1F5FF, 0xFFFFFFFF, 0xFFFFE6A8, 1.08F, 1.5F, 0.17F, false),
      new Wings.Wing("seraph_gold", "Seraph Gold", "Celestial", Wings.Style.FEATHER, 0xFFFFE0A0, 0xFFFFF6DC, 0xFFFFC24D, 1.04F, 1.4F, 0.18F, false),
      new Wings.Wing("frost", "Frostbite", "Celestial", Wings.Style.FEATHER, 0xFFBFE6FF, 0xFFEAFBFF, 0xFF39C8FF, 1.02F, 1.6F, 0.15F, false),
      new Wings.Wing("phoenix", "Phoenix", "Elemental", Wings.Style.FEATHER, 0xFFFF7A2F, 0xFFFFD08A, 0xFFFF3B1F, 1.14F, 1.9F, 0.20F, false),
      new Wings.Wing("raven", "Raven", "Elemental", Wings.Style.FEATHER, 0xFF241E36, 0xFF6A5A9E, 0xFF8B5CF6, 1.10F, 1.7F, 0.17F, false),
      new Wings.Wing("dragon_crimson", "Dragon Crimson", "Draconic", Wings.Style.MEMBRANE, 0xFF8E1A22, 0xFFE0473C, 0xFFFF6B4A, 1.12F, 1.3F, 0.22F, false),
      new Wings.Wing("dragon_void", "Dragon Void", "Draconic", Wings.Style.MEMBRANE, 0xFF13111E, 0xFF3B3D6C, 0xFF25D9F2, 1.16F, 1.25F, 0.24F, false),
      new Wings.Wing("night_bat", "Night Bat", "Draconic", Wings.Style.MEMBRANE, 0xFF2B2230, 0xFF6E5A78, 0xFFA88BD6, 0.92F, 2.2F, 0.26F, false),
      new Wings.Wing("fairy", "Fairy Dust", "Spirit", Wings.Style.INSECT, 0xFFC8F5FF, 0xFFFFFFFF, 0xFF8BE8FF, 0.86F, 2.6F, 0.30F, false),
      new Wings.Wing("moon_moth", "Moon Moth", "Spirit", Wings.Style.INSECT, 0xFFE8DCC0, 0xFFFFF6DD, 0xFFFFE3A0, 0.94F, 2.2F, 0.22F, false),
      new Wings.Wing("cyber_edge", "Cyber Edge", "Tech", Wings.Style.BLADE, 0xFF101A24, 0xFF25D9F2, 0xFF9BF3FF, 1.06F, 1.1F, 0.10F, false),
      new Wings.Wing("mech_frame", "Mech Frame", "Tech", Wings.Style.BLADE, 0xFF6E7784, 0xFFB8C2CE, 0xFFFFC857, 0.98F, 1.0F, 0.08F, false),
      new Wings.Wing("storm", "Cyclone Storm", "Tech", Wings.Style.BLADE, 0, 0, 0, 1.10F, 2.0F, 0.16F, true),
      new Wings.Wing("emissary_wings", "Emissary Wings", "Packs", Wings.Style.MODEL, 0, 0, 0, 1.0F, 1.0F, 0.1F, false, 0),
      new Wings.Wing("necros_wings", "Necros Wings", "Packs", Wings.Style.MODEL, 0, 0, 0, 1.0F, 1.0F, 0.1F, false, 1),
      new Wings.Wing("twilight_wings", "Twilight Wings", "Packs", Wings.Style.MODEL, 0, 0, 0, 1.0F, 1.0F, 0.1F, false, 2),
      new Wings.Wing("soulreaper_wings_green", "Soul Reaper Wings (Green)", "Packs", Wings.Style.MODEL, 0, 0, 0, 1.0F, 1.0F, 0.1F, false, 3),
      new Wings.Wing("soulreaper_wings_purple", "Soul Reaper Wings (Purple)", "Packs", Wings.Style.MODEL, 0, 0, 0, 1.0F, 1.0F, 0.1F, false, 4),
      new Wings.Wing("soulreaper_wings_red", "Soul Reaper Wings (Red)", "Packs", Wings.Style.MODEL, 0, 0, 0, 1.0F, 1.0F, 0.1F, false, 5)
   );

   private Wings() {
   }

   public static Wings.Wing byId(String id) {
      if (id != null && !id.isEmpty()) {
         for (Wings.Wing w : ALL) {
            if (w.id().equals(id)) {
               return w;
            }
         }
      }

      return null;
   }

   private static int base(Wings.Wing w) {
      return w.theme() ? Theme.accentDark() : w.base();
   }

   private static int edge(Wings.Wing w) {
      return w.theme() ? Theme.accentLight() : w.edge();
   }

   private static int glow(Wings.Wing w) {
      return w.theme() ? Theme.accent() : w.glow();
   }

   public static void emit(Wings.Wing w, float time, CosmeticGeometry geo) {
      if (w == null) {
         return;
      }

      if (w.model() >= 0) {
         CosmeticModels.emitWing(w.model(), geo);
         return;
      }

      switch (w.style()) {
         case FEATHER -> feathers(w, time, geo);
         case MEMBRANE -> membrane(w, time, geo);
         case BLADE -> blades(w, time, geo);
         default -> insect(w, time, geo);
      }
   }

   private static float flap(Wings.Wing w, float time, float u, float phase) {
      return w.amp() * (float)Math.sin(time * w.speed() + u * 1.4F + phase);
   }

   /** Point on the leading edge, {@code u} running from the shoulder blade to the wing tip. */
   private static float armX(float s, Wings.Wing w, float u) {
      return s * (0.10F + 0.56F * w.span() * u);
   }

   private static float armY(Wings.Wing w, float u, float lift) {
      return ROOT_Y + lift * (float)Math.sin(Math.min(1.0F, u) * 1.32F);
   }

   private static float armZ(float u) {
      return -0.15F - 0.10F * u;
   }

   private static void feathers(Wings.Wing w, float time, CosmeticGeometry geo) {
      int rows = 9;
      int base = base(w);
      int edge = edge(w);
      int glow = glow(w);
      float lift = 0.40F + 0.10F * w.span();

      for (int s = -1; s <= 1; s += 2) {
         float[] ax = new float[rows];
         float[] ay = new float[rows];
         float[] az = new float[rows];
         float[] ang = new float[rows];
         float[] len = new float[rows];

         for (int r = 0; r < rows; r++) {
            float u = r / (float)(rows - 1);
            float a = 0.16F - 1.22F * u + flap(w, time, u, 0.0F);
            ax[r] = armX(s, w, u);
            ay[r] = armY(w, u, lift) + 0.04F * (float)Math.sin(time * w.speed() * 0.8F + u * 2.0F);
            az[r] = armZ(u);
            ang[r] = s > 0 ? a : (float)Math.PI - a;
            len[r] = w.span() * (0.52F + 0.46F * (float)Math.sin(Math.PI * (0.22F + 0.68F * u)));
         }

         for (int r = 0; r < rows; r++) {
            float sweep = -0.10F - 0.10F * (r / (float)(rows - 1));
            feather(geo, ax[r], ay[r], az[r], ang[r], len[r], 0.105F, 0.026F, sweep, mix(base, 0.78F), edge, false);
            if (r >= 3) {
               feather(
                  geo,
                  ax[r] - s * 0.03F,
                  ay[r] - 0.03F,
                  az[r] + 0.02F,
                  ang[r] + s * 0.06F,
                  len[r] * 0.58F,
                  0.075F,
                  0.022F,
                  sweep * 0.7F,
                  mix(base, 0.55F),
                  mix(edge, 0.86F),
                  false
               );
            }
         }

         for (int r = 0; r < rows - 1; r++) {
            geo.ribbon(ax[r], ay[r] + 0.015F, az[r], ax[r + 1], ay[r + 1] + 0.015F, az[r + 1], 0.030F, mix(edge, 0.92F), false);
         }

         for (int r = 1; r < rows - 1; r += 2) {
            float dx = (float)Math.cos(ang[r]);
            float dy = (float)Math.sin(ang[r]);
            float t = len[r] * 0.7F;
            geo.ribbon(
               ax[r] + dx * len[r] * 0.3F,
               ay[r] + dy * len[r] * 0.3F,
               az[r] - 0.02F,
               ax[r] + dx * t,
               ay[r] + dy * t,
               az[r] - 0.06F,
               0.006F,
               glow,
               true
            );
         }

         geo.box(s * 0.02F, ROOT_Y - 0.06F, -0.22F, s * 0.20F, ROOT_Y + 0.16F, -0.06F, mix(base, 0.7F), false);
      }
   }

   private static void feather(
      CosmeticGeometry geo,
      float rx,
      float ry,
      float rz,
      float ang,
      float len,
      float w0,
      float w1,
      float sweep,
      int colRoot,
      int colTip,
      boolean emissive
   ) {
      segment(geo, rx, ry, rz, ang, 0.0F, 0.55F, len, w0, w0 * 0.72F, sweep * 0.5F, colRoot, emissive);
      segment(geo, rx, ry, rz, ang, 0.55F, 1.0F, len, w0 * 0.72F, w1, sweep, colTip, emissive);
   }

   private static void segment(
      CosmeticGeometry geo,
      float rx,
      float ry,
      float rz,
      float ang,
      float from,
      float to,
      float len,
      float w0,
      float w1,
      float sweep,
      int col,
      boolean emissive
   ) {
      float dx = (float)Math.cos(ang);
      float dy = (float)Math.sin(ang);
      float px = -dy;
      float py = dx;
      float ax = rx + dx * len * from;
      float ay = ry + dy * len * from;
      float az = rz + sweep * from;
      float bx = rx + dx * len * to;
      float by = ry + dy * len * to;
      float bz = rz + sweep * to;
      geo.quad2(
         ax + px * w0, ay + py * w0, az,
         ax - px * w0, ay - py * w0, az,
         bx - px * w1, by - py * w1, bz,
         bx + px * w1, by + py * w1, bz,
         col,
         emissive
      );
   }

   private static void membrane(Wings.Wing w, float time, CosmeticGeometry geo) {
      int struts = 5;
      int base = base(w);
      int edge = edge(w);
      int glow = glow(w);
      float lift = 0.46F + 0.10F * w.span();

      for (int s = -1; s <= 1; s += 2) {
         float rootX = s * 0.09F;
         float rootY = ROOT_Y + 0.02F;
         float rootZ = -0.17F;
         float[] tx = new float[struts];
         float[] ty = new float[struts];
         float[] tz = new float[struts];

         for (int k = 0; k < struts; k++) {
            float u = k / (float)(struts - 1);
            float a = 0.52F - 1.62F * u + flap(w, time, u, 0.3F);
            float ang = s > 0 ? a : (float)Math.PI - a;
            float len = w.span() * (0.86F + 0.10F * (float)Math.sin(Math.PI * u));
            tx[k] = rootX + (float)Math.cos(ang) * len;
            ty[k] = rootY + (float)Math.sin(ang) * len * 0.92F + lift * 0.42F * u;
            tz[k] = rootZ - 0.20F - 0.22F * u;
         }

         for (int k = 0; k < struts - 1; k++) {
            geo.tri(rootX, rootY, rootZ, tx[k], ty[k], tz[k], tx[k + 1], ty[k + 1], tz[k + 1], mix(base, 0.9F), false);
            geo.tri(rootX, rootY, rootZ, tx[k + 1], ty[k + 1], tz[k + 1], tx[k], ty[k], tz[k], mix(base, 0.62F), false);
            geo.ribbon(tx[k], ty[k], tz[k], tx[k + 1], ty[k + 1], tz[k + 1], 0.010F, mix(edge, 0.8F), false);
         }

         for (int k = 0; k < struts; k++) {
            geo.ribbon(rootX, rootY, rootZ, tx[k], ty[k], tz[k], 0.020F, edge, false);
         }

         geo.ribbon(rootX, rootY, rootZ, tx[0], ty[0], tz[0], 0.012F, glow, true);
         geo.box(s * 0.02F, ROOT_Y - 0.08F, -0.24F, s * 0.21F, ROOT_Y + 0.16F, -0.08F, mix(base, 0.8F), false);
         geo.ribbon(rootX, rootY + 0.02F, rootZ + 0.02F, rootX + s * 0.02F, rootY + 0.20F, rootZ - 0.02F, 0.012F, glow, true);
      }
   }

   private static void blades(Wings.Wing w, float time, CosmeticGeometry geo) {
      int base = base(w);
      int edge = edge(w);
      int glow = glow(w);
      float lift = 0.34F + 0.08F * w.span();

      for (int s = -1; s <= 1; s += 2) {
         float[] ax = new float[4];
         float[] ay = new float[4];
         float[] az = new float[4];

         for (int k = 0; k < 4; k++) {
            float u = k / 3.0F;
            ax[k] = armX(s, w, u) * 0.86F;
            ay[k] = armY(w, u, lift) + 0.02F;
            az[k] = armZ(u);
         }

         for (int k = 0; k < 3; k++) {
            float u = k / 2.0F;
            float a = -0.18F - 0.62F * u + flap(w, time, u, 0.5F);
            float ang = s > 0 ? a : (float)Math.PI - a;
            float len = w.span() * (0.86F - 0.10F * u);
            segment(geo, ax[k + 1], ay[k + 1], az[k + 1], ang, 0.0F, 1.0F, len, 0.070F, 0.014F, -0.12F - 0.06F * u, base, false);
            float dx = (float)Math.cos(ang);
            float dy = (float)Math.sin(ang);
            geo.ribbon(
               ax[k + 1] + dx * len * 0.10F,
               ay[k + 1] + dy * len * 0.10F,
               az[k + 1] - 0.02F,
               ax[k + 1] + dx * len,
               ay[k + 1] + dy * len,
               az[k + 1] - 0.16F - 0.06F * u,
               0.008F,
               k == 0 ? glow : edge,
               true
            );
            if (k == 0) {
               geo.ribbon(ax[k + 1], ay[k + 1], az[k + 1] - 0.03F, ax[k + 1] + dx * len * 0.6F, ay[k + 1] + dy * len * 0.6F, az[k + 1] - 0.10F, 0.010F, edge, false);
            }
         }

         for (int k = 0; k < 3; k++) {
            geo.ribbon(ax[k], ay[k], az[k], ax[k + 1], ay[k + 1], az[k + 1], 0.032F, mix(edge, 0.85F), false);
         }

         geo.box(s * 0.02F, ROOT_Y - 0.10F, -0.22F, s * 0.18F, ROOT_Y + 0.14F, -0.06F, mix(edge, 0.7F), false);
      }
   }

   private static void insect(Wings.Wing w, float time, CosmeticGeometry geo) {
      int base = base(w);
      int edge = edge(w);
      int glow = glow(w);
      int n = 11;
      float lift = 0.30F + 0.06F * w.span();

      for (int s = -1; s <= 1; s += 2) {
         float rootX = s * 0.09F;
         float rootY = ROOT_Y + 0.04F;
         float rootZ = -0.16F;
         float[] px = new float[n];
         float[] py = new float[n];
         float[] pz = new float[n];

         for (int i = 0; i < n; i++) {
            float u = i / (float)(n - 1);
            float a = 0.62F - 1.62F * u;
            float ang = s > 0 ? a : (float)Math.PI - a;
            float rr = w.span() * (0.62F + 0.46F * (float)Math.sin(Math.PI * (0.16F + 0.84F * u)));
            rr *= 1.0F + 0.05F * flap(w, time, u, 0.8F);
            px[i] = rootX + (float)Math.cos(ang) * rr;
            py[i] = rootY + (float)Math.sin(ang) * rr * 0.85F + lift * 0.30F * u;
            pz[i] = rootZ - 0.12F - 0.12F * (float)Math.sin(Math.PI * u);
         }

         for (int i = 0; i < n - 1; i++) {
            geo.tri(rootX, rootY, rootZ, px[i], py[i], pz[i], px[i + 1], py[i + 1], pz[i + 1], mix(base, 0.86F), false);
            geo.tri(rootX, rootY, rootZ, px[i + 1], py[i + 1], pz[i + 1], px[i], py[i], pz[i], mix(base, 0.58F), false);
         }

         for (int i = 1; i < n - 1; i += 2) {
            geo.ribbon(rootX, rootY, rootZ, px[i], py[i], pz[i] - 0.02F, 0.005F, mix(edge, 0.75F), false);
         }

         for (int i = 0; i < n - 1; i++) {
            geo.ribbon(px[i], py[i], pz[i], px[i + 1], py[i + 1], pz[i + 1], 0.007F, glow, true);
         }

         geo.box(s * 0.02F, ROOT_Y - 0.08F, -0.20F, s * 0.13F, ROOT_Y + 0.12F, -0.07F, mix(base, 0.6F), false);
      }
   }

   static int mix(int color, float f) {
      int r = Math.round((color >> 16 & 0xFF) * f);
      int g = Math.round((color >> 8 & 0xFF) * f);
      int b = Math.round((color & 0xFF) * f);
      return 0xFF000000 | Math.min(255, r) << 16 | Math.min(255, g) << 8 | Math.min(255, b);
   }

   @Environment(EnvType.CLIENT)
   public enum Style {
      FEATHER,
      MEMBRANE,
      BLADE,
      INSECT,
      MODEL
   }

   @Environment(EnvType.CLIENT)
   public record Wing(
      String id, String name, String group, Wings.Style style, int base, int edge, int glow, float span, float speed, float amp, boolean theme, int model
   ) {
      public Wing(
         String id, String name, String group, Wings.Style style, int base, int edge, int glow, float span, float speed, float amp, boolean theme
      ) {
         this(id, name, group, style, base, edge, glow, span, speed, amp, theme, -1);
      }
   }
}
