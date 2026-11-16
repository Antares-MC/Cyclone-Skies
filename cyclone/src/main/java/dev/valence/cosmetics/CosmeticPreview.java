package dev.valence.cosmetics;

import dev.valence.compat.Textures;
import dev.valence.gui.Theme;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;

/**
 * Tiny software renderer used to show 3D cosmetics inside the menu. Triangles are projected
 * orthographically with auto framing, shaded with a single directional light and painted back to
 * front, then uploaded once per cosmetic (wings re-upload a few times a second to animate).
 */
@Environment(EnvType.CLIENT)
public final class CosmeticPreview {
   private static final int SIZE = 80;
   /** Sample offsets (in UV units) used to find colour inside sparse, wispy pack art. */
   private static final float[] STENCIL = new float[]{-0.035F, -0.035F, 0.035F, -0.035F, -0.035F, 0.035F, 0.035F, 0.035F, 0.0F, 0.06F, 0.0F, -0.06F};
   private static final Map<String, CosmeticPreview.Tile> TILES = new HashMap<>();
   private static final Map<String, CosmeticPreview.Image> IMAGES = new HashMap<>();
   private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("CycloneSkies");

   private CosmeticPreview() {
   }

   public static void draw(
      class_332 g,
      String key,
      float yawDeg,
      boolean animated,
      Consumer<CosmeticGeometry> source,
      int x,
      int y,
      int w,
      int h
   ) {
      String themeId = dev.valence.config.ValenceConfig.get().theme;
      String cacheKey = key + "@" + Math.round(yawDeg) + "@" + (themeId == null ? "" : themeId);
      CosmeticPreview.Tile t = TILES.get(cacheKey);
      if (t == null) {
         t = new CosmeticPreview.Tile();
         t.key = cacheKey;
         TILES.put(cacheKey, t);
      }

      if (t.id == null) {
         try {
            t.id = class_2960.method_60655("valence", "preview/" + t.key.replace(':', '_').replace('.', '_').replace('@', '_').toLowerCase());
            t.tex = Textures.blank("valence_preview", SIZE, SIZE, false);
            class_310.method_1551().method_1531().method_4616(t.id, t.tex);
         } catch (Throwable var11) {
            LOGGER.warn("[preview] register failed for {}", key, var11);
            return;
         }
      }

      long now = System.currentTimeMillis();
      if (t.stamp == 0L || animated && now - t.stamp > 125L) {
         t.stamp = now;
         render(t, yawDeg, source, animated ? (float)(now / 1000.0) : 0.0F);
      }

      Theme.image(g, t.id, x, y, w, h, SIZE, SIZE, -1);
   }

   private static void render(CosmeticPreview.Tile t, float yawDeg, Consumer<CosmeticGeometry> source, float time) {
      if (t.tex == null) {
         return;
      }

      try {
         CosmeticPreview.Gather gather = new CosmeticPreview.Gather();
         source.accept(gather);
         int n = gather.count;
         if (n == 0) {
            if (t.logged.compareAndSet(false, true)) {
               LOGGER.warn("[preview] no triangles for {}", t.id);
            }

            return;
         }

         float yaw = (float)Math.toRadians(yawDeg);
         float pitch = (float)Math.toRadians(10.0);
         float cy = (float)Math.cos(yaw);
         float sy = (float)Math.sin(yaw);
         float cp = (float)Math.cos(pitch);
         float sp = (float)Math.sin(pitch);
         float[] px = new float[n * 3];
         float[] py = new float[n * 3];
         float[] pz = new float[n * 3];
         float minX = Float.MAX_VALUE;
         float maxX = -Float.MAX_VALUE;
         float minY = Float.MAX_VALUE;
         float maxY = -Float.MAX_VALUE;

         for (int i = 0; i < n * 3; i++) {
            float x = gather.pos[i * 3];
            float y = gather.pos[i * 3 + 1];
            float z = gather.pos[i * 3 + 2];
            float x1 = x * cy - z * sy;
            float z1 = x * sy + z * cy;
            float y2 = y * cp - z1 * sp;
            float z2 = y * sp + z1 * cp;
            px[i] = x1;
            py[i] = y2;
            pz[i] = z2;
            if (x1 < minX) {
               minX = x1;
            }

            if (x1 > maxX) {
               maxX = x1;
            }

            if (y2 < minY) {
               minY = y2;
            }

            if (y2 > maxY) {
               maxY = y2;
            }
         }

         float dx = Math.max(0.001F, maxX - minX);
         float dy = Math.max(0.001F, maxY - minY);
         float scale = Math.min((SIZE - 8) / dx, (SIZE - 8) / dy);
         float ox = (SIZE - dx * scale) / 2.0F - minX * scale;
         float oy = (SIZE - dy * scale) / 2.0F + maxY * scale;
         int[] buf = new int[SIZE * SIZE];
         float[] depth = new float[n];

         for (int i = 0; i < n; i++) {
            depth[i] = (pz[i * 3] + pz[i * 3 + 1] + pz[i * 3 + 2]) / 3.0F;
         }

         List<Integer> sorted = new ArrayList<>(n);
         for (int i = 0; i < n; i++) {
            sorted.add(i);
         }

         sorted.sort((a, b) -> Float.compare(depth[a], depth[b]));
         float lx = -0.42F;
         float ly = 0.62F;
         float lz = 0.66F;

         for (int idx : sorted) {
            int i0 = idx * 3;
            float ax = px[i0] * scale + ox;
            float ay = oy - py[i0] * scale;
            float bx = px[i0 + 1] * scale + ox;
            float by = oy - py[i0 + 1] * scale;
            float cx = px[i0 + 2] * scale + ox;
            float cyy = oy - py[i0 + 2] * scale;
            float nxp = px[i0 + 1] - px[i0];
            float nyp = py[i0 + 1] - py[i0];
            float nzp = pz[i0 + 1] - pz[i0];
            float mx = px[i0 + 2] - px[i0];
            float my = py[i0 + 2] - py[i0];
            float mz = pz[i0 + 2] - pz[i0];
            float nx = nyp * mz - nzp * my;
            float ny = nzp * mx - nxp * mz;
            float nz = nxp * my - nyp * mx;
            float nl = (float)Math.sqrt(nx * nx + ny * ny + nz * nz);
            if (nl < 1.0E-6F) {
               continue;
            }

            nx /= nl;
            ny /= nl;
            nz /= nl;
            float lam = Math.abs(nx * lx + ny * ly + nz * lz);
            float lum = gather.emissive[idx] ? 1.0F : 0.55F + 0.62F * lam;
            lum = Math.min(1.22F, lum);
            int col = gather.color[idx];
            if (col >>> 24 < 64) {
               continue;
            }

            int r = Math.min(255, Math.round((col >> 16 & 0xFF) * lum));
            int gg = Math.min(255, Math.round((col >> 8 & 0xFF) * lum));
            int b = Math.min(255, Math.round((col & 0xFF) * lum));
            int argb = 0xFF000000 | r << 16 | gg << 8 | b;
            fill(buf, ax, ay, bx, by, cx, cyy, argb);
         }

         class_1011 img = new class_1011(SIZE, SIZE, false);

         for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
               int c = buf[y * SIZE + x];
               if (c != 0) {
                  img.method_61941(x, y, c);
               }
            }
         }

         t.tex.method_4526(img);
         t.tex.method_4524();
      } catch (Throwable var44) {
         if (t.logged.compareAndSet(false, true)) {
            LOGGER.warn("[preview] render failed for {}", t.id, var44);
         }
      }
   }

   private static CosmeticPreview.Image image(String asset) {
      if (asset == null) {
         return null;
      }

      CosmeticPreview.Image cached = IMAGES.get(asset);
      if (cached == null) {
         try (InputStream in = CosmeticPreview.class.getResourceAsStream(asset)) {
            if (in != null) {
               BufferedImage img = ImageIO.read(in);
               if (img != null) {
                  int w = img.getWidth();
                  int h = img.getHeight();
                  cached = new CosmeticPreview.Image(w, h, img.getRGB(0, 0, w, h, null, 0, w));
               }
            }
         } catch (Throwable var8) {
            LOGGER.warn("[preview] texture {} failed", asset, var8);
         }

         if (cached == null) {
            cached = CosmeticPreview.Image.EMPTY;
         }

         IMAGES.put(asset, cached);
      }

      return cached.w == 0 ? null : cached;
   }

   private static int pixel(CosmeticPreview.Image img, int w, int h, float u, float v) {
      int x = (int)(u * w);
      int y = (int)(v * h);
      x = x < 0 ? 0 : (x >= w ? w - 1 : x);
      y = y < 0 ? 0 : (y >= h ? h - 1 : y);
      return img.px[y * img.w + x];
   }

   private static int blend(int a, int b, int c) {
      int alpha = Math.min(a >>> 24, Math.min(b >>> 24, c >>> 24));
      int r = ((a >> 16 & 0xFF) + (b >> 16 & 0xFF) + (c >> 16 & 0xFF)) / 3;
      int g = ((a >> 8 & 0xFF) + (b >> 8 & 0xFF) + (c >> 8 & 0xFF)) / 3;
      int bl = ((a & 0xFF) + (b & 0xFF) + (c & 0xFF)) / 3;
      return alpha << 24 | r << 16 | g << 8 | bl;
   }

   /**
    * The packs paint their wings and glows with sparse, wispy art, so a single sample in the middle
    * of a face often lands in a hole and the whole surface disappears from the menu. Sample a small
    * stencil and keep the most opaque hit instead.
    */
   private static int hit(CosmeticPreview.Image img, int w, int h, float u, float v) {
      int best = CosmeticPreview.pixel(img, w, h, u, v);
      float bestAlpha = (float)(best >>> 24);
      if (bestAlpha >= 250.0F) {
         return best;
      }

      for (int i = 0; i < CosmeticPreview.STENCIL.length; i += 2) {
         int c = CosmeticPreview.pixel(img, w, h, u + CosmeticPreview.STENCIL[i], v + CosmeticPreview.STENCIL[i + 1]);
         float a = (float)(c >>> 24);
         if (a > bestAlpha) {
            bestAlpha = a;
            best = c;
         }
      }

      return best;
   }

   private static void fill(int[] buf, float ax, float ay, float bx, float by, float cx, float cy, int argb) {
      int minX = Math.max(0, (int)Math.floor(Math.min(ax, Math.min(bx, cx))));
      int maxX = Math.min(SIZE - 1, (int)Math.ceil(Math.max(ax, Math.max(bx, cx))));
      int minY = Math.max(0, (int)Math.floor(Math.min(ay, Math.min(by, cy))));
      int maxY = Math.min(SIZE - 1, (int)Math.ceil(Math.max(ay, Math.max(by, cy))));
      float area = (bx - ax) * (cy - ay) - (by - ay) * (cx - ax);
      if (Math.abs(area) < 1.0E-6F) {
         return;
      }

      float inv = 1.0F / area;

      for (int y = minY; y <= maxY; y++) {
         float py = y + 0.5F;
         int row = y * SIZE;

         for (int x = minX; x <= maxX; x++) {
            float px = x + 0.5F;
            float w0 = ((bx - px) * (cy - py) - (by - py) * (cx - px)) * inv;
            if (!(w0 < 0.0F)) {
               float w1 = ((cx - px) * (ay - py) - (cy - py) * (ax - px)) * inv;
               if (!(w1 < 0.0F)) {
                  float w2 = 1.0F - w0 - w1;
                  if (!(w2 < 0.0F)) {
                     buf[row + x] = argb;
                  }
               }
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   private static final class Gather implements CosmeticGeometry {
      float[] pos = new float[256 * 3];
      int[] color = new int[256];
      boolean[] emissive = new boolean[256];
      int count;

      private void grow(int need) {
         if (need * 9 > this.pos.length) {
            int cap = Math.max(need + 32, this.color.length * 2);
            float[] p = new float[cap * 9];
            System.arraycopy(this.pos, 0, p, 0, this.count * 9);
            this.pos = p;
            int[] c = new int[cap];
            System.arraycopy(this.color, 0, c, 0, this.count);
            this.color = c;
            boolean[] e = new boolean[cap];
            System.arraycopy(this.emissive, 0, e, 0, this.count);
            this.emissive = e;
         }
      }

      @Override
      public void tri(
         float ax, float ay, float az,
         float bx, float by, float bz,
         float cx, float cy, float cz,
         int argb, boolean emissive
      ) {
         this.grow(this.count + 1);
         int i = this.count * 9;
         this.pos[i] = ax;
         this.pos[i + 1] = ay;
         this.pos[i + 2] = az;
         this.pos[i + 3] = bx;
         this.pos[i + 4] = by;
         this.pos[i + 5] = bz;
         this.pos[i + 6] = cx;
         this.pos[i + 7] = cy;
         this.pos[i + 8] = cz;
         this.color[this.count] = argb;
         this.emissive[this.count] = emissive;
         this.count++;
      }

      @Override
      public void triTex(
         float ax, float ay, float az, float au, float av,
         float bx, float by, float bz, float bu, float bv,
         float cx, float cy, float cz, float cu, float cv,
         CosmeticTexture tex, int argb, boolean emissive
      ) {
         this.tri(ax, ay, az, bx, by, bz, cx, cy, cz, argb, emissive);
         CosmeticPreview.Image img = CosmeticPreview.image(tex.asset());
         if (img != null) {
            int frames = Math.max(1, tex.frames());
            int w = img.w;
            int h = Math.max(1, img.h / frames);
            this.color[this.count - 1] = CosmeticPreview.blend(
               CosmeticPreview.hit(img, w, h, au, av),
               CosmeticPreview.hit(img, w, h, bu, bv),
               CosmeticPreview.hit(img, w, h, cu, cv)
            );
         }
      }
   }

   @Environment(EnvType.CLIENT)
   private static final class Image {
      static final CosmeticPreview.Image EMPTY = new CosmeticPreview.Image(0, 0, new int[0]);
      final int w;
      final int h;
      final int[] px;

      Image(int w, int h, int[] px) {
         this.w = w;
         this.h = h;
         this.px = px;
      }
   }

   @Environment(EnvType.CLIENT)
   private static final class Tile {
      class_1043 tex;
      class_2960 id;
      String key;
      long stamp;
      final java.util.concurrent.atomic.AtomicBoolean logged = new java.util.concurrent.atomic.AtomicBoolean();
   }
}
