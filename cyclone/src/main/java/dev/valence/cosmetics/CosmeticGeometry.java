package dev.valence.cosmetics;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Receives triangles for a cosmetic, already placed in player-local block space:
 * origin at the player's feet, +Y up, +Z in front of the player (where they look).
 */
@Environment(EnvType.CLIENT)
public interface CosmeticGeometry {
   void tri(
      float ax, float ay, float az,
      float bx, float by, float bz,
      float cx, float cy, float cz,
      int argb, boolean emissive
   );

   /**
    * A textured triangle. Implementations that cannot bind textures fall back to flat shading, so
    * every caller can emit model geometry regardless of where it is being drawn.
    */
   default void triTex(
      float ax, float ay, float az, float au, float av,
      float bx, float by, float bz, float bu, float bv,
      float cx, float cy, float cz, float cu, float cv,
      CosmeticTexture tex, int argb, boolean emissive
   ) {
      this.tri(ax, ay, az, bx, by, bz, cx, cy, cz, argb, emissive);
   }

   default void quad(
      float ax, float ay, float az,
      float bx, float by, float bz,
      float cx, float cy, float cz,
      float dx, float dy, float dz,
      int argb, boolean emissive
   ) {
      this.tri(ax, ay, az, bx, by, bz, cx, cy, cz, argb, emissive);
      this.tri(ax, ay, az, cx, cy, cz, dx, dy, dz, argb, emissive);
   }

   /** Emits the quad and its mirror so the surface is visible from both sides. */
   default void quad2(
      float ax, float ay, float az,
      float bx, float by, float bz,
      float cx, float cy, float cz,
      float dx, float dy, float dz,
      int argb, boolean emissive
   ) {
      this.quad(ax, ay, az, bx, by, bz, cx, cy, cz, dx, dy, dz, argb, emissive);
      this.quad(dx, dy, dz, cx, cy, cz, bx, by, bz, ax, ay, az, argb, emissive);
   }

   default void box(float x0, float y0, float z0, float x1, float y1, float z1, int argb, boolean emissive) {
      this.quad2(x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1, argb, emissive);
      this.quad2(x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0, argb, emissive);
      this.quad2(x0, y0, z0, x1, y0, z0, x1, y1, z0, x0, y1, z0, argb, emissive);
      this.quad2(x1, y0, z1, x0, y0, z1, x0, y1, z1, x1, y1, z1, argb, emissive);
      this.quad2(x0, y0, z1, x0, y0, z0, x0, y1, z0, x0, y1, z1, argb, emissive);
      this.quad2(x1, y0, z0, x1, y0, z1, x1, y1, z1, x1, y1, z0, argb, emissive);
   }

   /** Axis-aligned box rotated around the Y axis by {@code rot}, centred on (cx, cy, cz). */
   default void plate(float cx, float cy, float cz, float hx, float hy, float hz, float rot, int argb, boolean emissive) {
      float c = (float)Math.cos(rot);
      float s = (float)Math.sin(rot);
      float[] px = new float[]{-hx, hx, hx, -hx};
      float[] pz = new float[]{-hz, -hz, hz, hz};
      float[] wx = new float[4];
      float[] wz = new float[4];

      for (int i = 0; i < 4; i++) {
         wx[i] = cx + px[i] * c - pz[i] * s;
         wz[i] = cz + px[i] * s + pz[i] * c;
      }

      float y0 = cy - hy;
      float y1 = cy + hy;
      this.quad2(wx[0], y1, wz[0], wx[1], y1, wz[1], wx[2], y1, wz[2], wx[3], y1, wz[3], argb, emissive);
      this.quad2(wx[3], y0, wz[3], wx[2], y0, wz[2], wx[1], y0, wz[1], wx[0], y0, wz[0], argb, emissive);
      this.quad2(wx[0], y0, wz[0], wx[1], y0, wz[1], wx[1], y1, wz[1], wx[0], y1, wz[0], argb, emissive);
      this.quad2(wx[1], y0, wz[1], wx[2], y0, wz[2], wx[2], y1, wz[2], wx[1], y1, wz[1], argb, emissive);
      this.quad2(wx[2], y0, wz[2], wx[3], y0, wz[3], wx[3], y1, wz[3], wx[2], y1, wz[2], argb, emissive);
      this.quad2(wx[3], y0, wz[3], wx[0], y0, wz[0], wx[0], y1, wz[0], wx[3], y1, wz[3], argb, emissive);
   }

   /** Thin double-sided line segment (a camera-independent ribbon). */
   default void ribbon(
      float ax, float ay, float az, float bx, float by, float bz, float width, int argb, boolean emissive
   ) {
      float dx = bx - ax;
      float dy = by - ay;
      float dz = bz - az;
      float len = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
      if (len < 1.0E-5F) {
         return;
      }

      dx /= len;
      dy /= len;
      dz /= len;
      float ux = -dy;
      float uy = dx;
      if (Math.abs(dx) < 0.5F) {
         ux = 0.0F;
         uy = dz;
      }

      float ul = (float)Math.sqrt(ux * ux + uy * uy + dz * dz);
      ux = ux / ul * width;
      float uz = -uy / ul * width;
      uy = uy / ul * width;
      this.quad2(ax - ux, ay - uy, az - uz, ax + ux, ay + uy, az + uz, bx + ux, by + uy, bz + uz, bx - ux, by - uy, bz - uz, argb, emissive);
   }
}
