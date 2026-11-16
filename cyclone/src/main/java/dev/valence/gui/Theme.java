package dev.valence.gui;

import dev.valence.compat.Draw;
import dev.valence.compat.Pipes;
import dev.valence.config.ValenceConfig;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_332;

@Environment(EnvType.CLIENT)
public final class Theme {
   public static final int PANEL = -1207105001;
   public static final int SIDEBAR = 452984831;
   public static final int CARD = 318767103;
   public static final int CARD_HOVER = 587202559;
   public static final int CARD_ACTIVE = 1149983990;
   public static final int BORDER = 536870911;
   public static final int BORDER_HOVER = 1439281407;
   public static final int PANEL_BORDER = 1086959871;
   public static final int TRACK = 872415231;
   public static final int ACCENT = -7643914;
   public static final int ACCENT_LIGHT = -4745985;
   public static final int ACCENT_DARK = -11785570;
   public static final int TEXT = -724737;
   public static final int TEXT_DIM = -4608298;
   public static final int TEXT_MUTED = -8095322;
   public static final int GOOD = -11870592;
   public static final int WARN = -278748;
   public static final int BAD = -495247;
   public static final class_2960 LOGO = id("textures/gui/logo.png");
   public static final class_2960 SPOTLIGHT = id("textures/gui/spotlight.png");
   public static final class_2960 BANNER = id("textures/gui/banner.png");
   private static final class_2960[] FILL = new class_2960[]{sprite("r3"), sprite("r5"), sprite("r8")};
   private static final class_2960[] LINE = new class_2960[]{sprite("r3_line"), sprite("r5_line"), sprite("r8_line")};
   private static final class_2960[] SHEEN = new class_2960[]{sprite("r3_sheen"), sprite("r5_sheen"), sprite("r8_sheen")};
   private static final class_2960 PILL = sprite("pill");
   private static final class_2960 PILL_LINE = sprite("pill_line");
   private static final class_2960 SHADOW = sprite("shadow");
   private static final class_2960 GLOW = sprite("glow");
   public static final int SCRIM_TOP = -1945697265;
   public static final int SCRIM_BOTTOM = -1291320302;
   private static final int R_WHITE = 0;
   private static final int R_ACCENT = 1;
   private static final int R_ACCENT_LIGHT = 2;
   private static final int R_ACCENT_DARK = 3;
   private static final int R_BORDER_TINT = 4;
   private static final int R_PANEL = 5;
   private static final int R_MUTED = 6;
   private static final int R_DIM = 7;
   private static final int R_TEXT = 8;
   private static final int R_GOOD = 9;
   private static final int R_WARN = 10;
   private static final int R_BAD = 11;
   private static final int R_SCRIM_TOP = 12;
   private static final int R_SCRIM_BOTTOM = 13;
   private static final int[] BASE = new int[]{
      16777215,
      9133302,
      12031231,
      4991646,
      13218047,
      854551,
      8681894,
      12168918,
      16052479,
      4906624,
      16498468,
      16281969,
      459791,
      525330
   };
   private static final float BASE_ACCENT_HUE = 258.0F;
   private static final float BASE_ACCENT_SAT = 0.631F;
   private static final float BASE_ACCENT_VAL = 0.965F;
   public static final List<Theme.Palette> PALETTES = List.of(
      Theme.Palette.of(
         "cyclone",
         15137791,
         2480626,
         10941439,
         749190,
         10148336,
         397337,
         7246763,
         10668252,
         15793407,
         3531675,
         16762967,
         16738949,
         265236,
         397856,
         199.0F,
         0.36F
      ),
      Theme.Palette.of(
         "nebula",
         16777215,
         9133302,
         12031231,
         4991646,
         13218047,
         854551,
         8681894,
         12168918,
         16052479,
         4906624,
         16498468,
         16281969,
         459791,
         525330,
         258.0F,
         0.0F
      ),
      Theme.Palette.of(
         "ember",
         16773346,
         16742959,
         16760970,
         9055750,
         16759178,
         1378821,
         11042662,
         14069395,
         16774120,
         5888394,
         16761421,
         16735309,
         984323,
         1509638,
         24.0F,
         0.32F
      ),
      Theme.Palette.of(
         "jade",
         15335412,
         2876300,
         11075538,
         686666,
         9693117,
         332558,
         7314566,
         10801083,
         15794168,
         8254107,
         16765517,
         16739194,
         199945,
         333072,
         158.0F,
         0.32F
      )
   );
   private static Theme.Palette resolved;
   private static String resolvedId;
   private static int resolvedRevision = -1;

   private Theme() {
   }

   public static Theme.Palette active() {
      ValenceConfig cfg = ValenceConfig.get();
      String id = cfg.theme == null ? "cyclone" : cfg.theme;
      int rev = ValenceConfig.revision();
      if (resolved != null && id.equals(resolvedId) && rev == resolvedRevision) {
         return resolved;
      }

      for (Theme.Palette p : PALETTES) {
         if (p.id.equals(id)) {
            resolved = p;
            resolvedId = id;
            resolvedRevision = rev;
            return p;
         }
      }

      resolved = PALETTES.get(0);
      resolvedId = resolved.id;
      resolvedRevision = rev;
      return resolved;
   }

   public static void select(String id) {
      for (Theme.Palette p : PALETTES) {
         if (p.id.equals(id)) {
            ValenceConfig.get().theme = id;
            return;
         }
      }
   }

   public static Theme.Palette byId(String id) {
      if (id != null) {
         for (Theme.Palette p : PALETTES) {
            if (p.id.equals(id)) {
               return p;
            }
         }
      }

      return null;
   }

   public static int accent() {
      return active().color(R_ACCENT);
   }

   public static int accentLight() {
      return active().color(R_ACCENT_LIGHT);
   }

   public static int accentDark() {
      return active().color(R_ACCENT_DARK);
   }

   public static int map(int color) {
      int a = color >>> 24;
      if (a == 0) {
         return color;
      }

      Theme.Palette p = active();
      if (p.stock) {
         return color;
      }

      int rgb = color & 16777215;

      for (int i = 0; i < BASE.length; i++) {
         if (BASE[i] == rgb) {
            return a << 24 | p.color(i);
         }
      }

      float h = hue(rgb);
      float s = sat(rgb);
      float v = val(rgb);
      if (s >= 0.16F && hueDistance(h, BASE_ACCENT_HUE) <= 42.0F) {
         float as = sat(p.color(R_ACCENT));
         float av = val(p.color(R_ACCENT));
         float outS = Math.min(1.0F, s * (as / BASE_ACCENT_SAT));
         float outV = Math.min(1.0F, v * (av / BASE_ACCENT_VAL));
         return a << 24 | hsv(hue(p.color(R_ACCENT)), outS, outV);
      }

      if (s < 0.16F && v > 0.07F && v < 0.93F) {
         return a << 24 | hsv(p.neutralHue, Math.min(1.0F, s + p.neutralSat), v);
      }

      return color;
   }

   private static float hue(int rgb) {
      float r = (rgb >> 16 & 0xFF) / 255.0F;
      float g = (rgb >> 8 & 0xFF) / 255.0F;
      float b = (rgb & 0xFF) / 255.0F;
      float max = Math.max(r, Math.max(g, b));
      float min = Math.min(r, Math.min(g, b));
      float d = max - min;
      if (d < 1.0E-6F) {
         return 0.0F;
      }

      float h;
      if (max == r) {
         h = (g - b) / d % 6.0F;
      } else if (max == g) {
         h = (b - r) / d + 2.0F;
      } else {
         h = (r - g) / d + 4.0F;
      }

      h *= 60.0F;
      return h < 0.0F ? h + 360.0F : h;
   }

   private static float sat(int rgb) {
      int r = rgb >> 16 & 0xFF;
      int g = rgb >> 8 & 0xFF;
      int b = rgb & 0xFF;
      int max = Math.max(r, Math.max(g, b));
      int min = Math.min(r, Math.min(g, b));
      return max == 0 ? 0.0F : (float)(max - min) / max;
   }

   private static float val(int rgb) {
      return Math.max(rgb >> 16 & 0xFF, Math.max(rgb >> 8 & 0xFF, rgb & 0xFF)) / 255.0F;
   }

   private static float hueDistance(float a, float b) {
      float d = Math.abs(a - b) % 360.0F;
      return d > 180.0F ? 360.0F - d : d;
   }

   private static int hsv(float h, float s, float v) {
      float c = v * s;
      float x = c * (1.0F - Math.abs(h / 60.0F % 2.0F - 1.0F));
      float m = v - c;
      float r;
      float g;
      float b;
      int seg = (int)(h / 60.0F) % 6;
      switch (seg) {
         case 0:
            r = c;
            g = x;
            b = 0.0F;
            break;
         case 1:
            r = x;
            g = c;
            b = 0.0F;
            break;
         case 2:
            r = 0.0F;
            g = c;
            b = x;
            break;
         case 3:
            r = 0.0F;
            g = x;
            b = c;
            break;
         case 4:
            r = x;
            g = 0.0F;
            b = c;
            break;
         default:
            r = c;
            g = 0.0F;
            b = x;
      }

      return Math.round((r + m) * 255.0F) << 16 | Math.round((g + m) * 255.0F) << 8 | Math.round((b + m) * 255.0F);
   }

   @Environment(EnvType.CLIENT)
   public static final class Palette {
      public final String id;
      private final int[] colors;
      private final boolean stock;
      final float neutralHue;
      final float neutralSat;

      private Palette(String id, int[] colors, boolean stock, float neutralHue, float neutralSat) {
         this.id = id;
         this.colors = colors;
         this.stock = stock;
         this.neutralHue = neutralHue;
         this.neutralSat = neutralSat;
      }

      static Theme.Palette of(
         String id,
         int white,
         int accent,
         int accentLight,
         int accentDark,
         int borderTint,
         int panel,
         int muted,
         int dim,
         int text,
         int good,
         int warn,
         int bad,
         int scrimTop,
         int scrimBottom,
         float neutralHue,
         float neutralSat
      ) {
         int[] c = new int[]{white, accent, accentLight, accentDark, borderTint, panel, muted, dim, text, good, warn, bad, scrimTop, scrimBottom};
         return new Theme.Palette(id, c, java.util.Arrays.equals(c, BASE), neutralHue, neutralSat);
      }

      public int color(int role) {
         return this.colors[role];
      }

      public String nameKey() {
         return "valence.theme." + this.id;
      }

      public int accent() {
         return this.colors[R_ACCENT];
      }

      public int accentLight() {
         return this.colors[R_ACCENT_LIGHT];
      }

      public int accentDark() {
         return this.colors[R_ACCENT_DARK];
      }

      public int panel() {
         return this.colors[R_PANEL];
      }

      public int text() {
         return this.colors[R_TEXT];
      }

      public int good() {
         return this.colors[R_GOOD];
      }

      public int bad() {
         return this.colors[R_BAD];
      }

      public int scrimTop() {
         return this.colors[R_SCRIM_TOP];
      }

      public int scrimBottom() {
         return this.colors[R_SCRIM_BOTTOM];
      }
   }

   public static class_2960 id(String path) {
      return class_2960.method_60655("valence", path);
   }

   private static class_2960 sprite(String name) {
      return id("glass/" + name);
   }

   public static class_2960 icon(String name) {
      return id("textures/gui/icons/" + name + ".png");
   }

   private static int radiusIndex(int w, int h) {
      int m = Math.min(w, h);
      if (m >= 44) {
         return 2;
      } else {
         return m >= 14 ? 1 : 0;
      }
   }

   public static void round(class_332 g, int x1, int y1, int x2, int y2, int color) {
      int w = x2 - x1;
      int h = y2 - y1;
      color = map(color);
      if (w > 0 && h > 0 && color >>> 24 != 0) {
         if (w >= 4 && h >= 4) {
            Draw.guiTexture(g, FILL[radiusIndex(w, h)], x1, y1, w, h, color);
         } else {
            g.method_25294(x1, y1, x2, y2, color);
         }
      }
   }

   public static void outline(class_332 g, int x1, int y1, int x2, int y2, int color) {
      int w = x2 - x1;
      int h = y2 - y1;
      color = map(color);
      if (w >= 4 && h >= 4 && color >>> 24 != 0) {
         Draw.guiTexture(g, LINE[radiusIndex(w, h)], x1, y1, w, h, color);
      }
   }

   public static void sheen(class_332 g, int x1, int y1, int x2, int y2, float strength) {
      int w = x2 - x1;
      int h = y2 - y1;
      if (w >= 4 && h >= 4 && !(strength <= 0.01F)) {
         Draw.guiTexture(g, SHEEN[radiusIndex(w, h)], x1, y1, w, h, map(withAlpha(-1, strength)));
      }
   }

   public static void card(class_332 g, int x1, int y1, int x2, int y2, int fill, int border) {
      round(g, x1, y1, x2, y2, fill);
      outline(g, x1, y1, x2, y2, border);
   }

   public static void glass(class_332 g, int x1, int y1, int x2, int y2, int fill, int border, float shadow) {
      if (shadow > 0.0F) {
         Draw.guiTexture(g, SHADOW, x1 - 14, y1 - 12, x2 - x1 + 28, y2 - y1 + 28, withAlpha(-1, shadow));
      }

      round(g, x1, y1, x2, y2, fill);
      sheen(g, x1, y1, x2, y2, 0.9F);
      outline(g, x1, y1, x2, y2, border);
   }

   public static void glow(class_332 g, int x1, int y1, int x2, int y2, int color, float strength) {
      if (!(strength <= 0.01F)) {
         Draw.guiTexture(g, GLOW, x1 - 14, y1 - 14, x2 - x1 + 28, y2 - y1 + 28, map(withAlpha(color, strength)));
      }
   }

   public static void pill(class_332 g, int x1, int y1, int x2, int y2, int color) {
      color = map(color);
      if (x2 - x1 >= 2 && y2 - y1 >= 2 && color >>> 24 != 0) {
         Draw.guiTexture(g, PILL, x1, y1, x2 - x1, y2 - y1, color);
      }
   }

   public static void pillLine(class_332 g, int x1, int y1, int x2, int y2, int color) {
      color = map(color);
      if (x2 - x1 >= 2 && y2 - y1 >= 2 && color >>> 24 != 0) {
         Draw.guiTexture(g, PILL_LINE, x1, y1, x2 - x1, y2 - y1, color);
      }
   }

   public static void swatch(class_332 g, int x1, int y1, int x2, int y2, int color) {
      if (x2 - x1 >= 2 && y2 - y1 >= 2 && color >>> 24 != 0) {
         Draw.guiTexture(g, PILL, x1, y1, x2 - x1, y2 - y1, color);
      }
   }

   public static void swatchLine(class_332 g, int x1, int y1, int x2, int y2, int color) {
      if (x2 - x1 >= 2 && y2 - y1 >= 2 && color >>> 24 != 0) {
         Draw.guiTexture(g, PILL_LINE, x1, y1, x2 - x1, y2 - y1, color);
      }
   }

   public static void swatchGlow(class_332 g, int x1, int y1, int x2, int y2, int color, float strength) {
      if (!(strength <= 0.01F)) {
         Draw.guiTexture(g, GLOW, x1 - 14, y1 - 14, x2 - x1 + 28, y2 - y1 + 28, withAlpha(color, strength));
      }
   }

   public static void card(class_332 g, int x1, int y1, int x2, int y2, float hover, boolean selected) {
      int fill = selected ? lerp(1149983990, 1519082742, hover) : lerp(318767103, 587202559, hover);
      int border = selected ? lerp(-1282712330, -4745985, hover) : lerp(536870911, 1439281407, hover);
      if (selected) {
         glow(g, x1, y1, x2, y2, -7643914, 0.35F + 0.25F * hover);
      } else {
         glow(g, x1, y1, x2, y2, -7643914, 0.18F * hover);
      }

      round(g, x1, y1, x2, y2, fill);
      if (hover > 0.01F || selected) {
         sheen(g, x1, y1, x2, y2, 0.35F + 0.4F * hover);
      }

      outline(g, x1, y1, x2, y2, border);
   }

   public static void icon(class_332 g, String name, int x, int y, int size, int color) {
      Draw.region(g, icon(name), x, y, 0.0F, 0.0F, size, size, 16, 16, 16, 16, map(color));
   }

   public static void image(class_332 g, class_2960 tex, int x, int y, int w, int h, int texW, int texH, int color) {      Draw.region(g, tex, x, y, 0.0F, 0.0F, w, h, texW, texH, texW, texH, color);
   }

   public static int withAlpha(int color, float alpha) {
      int a = (int)((color >>> 24 & 0xFF) * Math.max(0.0F, Math.min(1.0F, alpha)));
      return a << 24 | color & 16777215;
   }

   public static int lerp(int a, int b, float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      int aa = a >>> 24 & 0xFF;
      int ar = a >> 16 & 0xFF;
      int ag = a >> 8 & 0xFF;
      int ab = a & 0xFF;
      int ba = b >>> 24 & 0xFF;
      int br = b >> 16 & 0xFF;
      int bg = b >> 8 & 0xFF;
      int bb = b & 0xFF;
      return (int)(aa + (ba - aa) * t) << 24 | (int)(ar + (br - ar) * t) << 16 | (int)(ag + (bg - ag) * t) << 8 | (int)(ab + (bb - ab) * t);
   }

   public static float easeInOutCubic(float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      return t < 0.5F ? 4.0F * t * t * t : 1.0F - (float)Math.pow(-2.0F * t + 2.0F, 3.0) / 2.0F;
   }

   public static float easeOutCubic(float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      float k = 1.0F - t;
      return 1.0F - k * k * k;
   }
}
