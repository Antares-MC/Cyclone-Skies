package dev.valence.cosmetics;

import dev.valence.cord.cosmetic.Accessories;
import dev.valence.license.License;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.class_12249;
import net.minecraft.class_1944;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_746;
import net.minecraft.class_765;
import org.joml.Matrix4f;

/**
 * Draws the equipped hat and wings on the local player. Uses the camera-relative vertex space the
 * mod already renders skies and props in, so no per-frame matrix stack juggling is needed.
 */
@Environment(EnvType.CLIENT)
public final class CosmeticRender {
   private static boolean registered;

   private CosmeticRender() {
   }

   public static void register() {
      if (!registered) {
         registered = true;
         WorldRenderEvents.AFTER_ENTITIES.register(CosmeticRender::render);
      }
   }

   private static void render(WorldRenderContext ctx) {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 == null || mc.field_1687 == null) {
         return;
      }

      if (ctx.matrices() == null || ctx.consumers() == null) {
         return;
      }

      if (!License.active()) {
         return;
      }

      Hats.Hat hat = Cosmetics.hat();
      Wings.Wing wing = Cosmetics.wings();
      if (hat == null && wing == null) {
         return;
      }

      class_746 pl = mc.field_1724;
      if (pl.method_5767() || pl.method_6113() || pl.method_5681() || pl.method_6128()) {
         return;
      }

      if (mc.field_1690.method_31044().method_31034()) {
         return;
      }

      if (!Accessories.whiteTex()) {
         return;
      }

      try {
         Matrix4f base = new Matrix4f(ctx.matrices().method_23760().method_23761());
         class_4597 buffers = ctx.consumers();
         class_243 cam = ctx.worldState().field_63082.field_63078;
         float pt = mc.method_61966().method_60637(false);
         class_243 pos = pl.method_30950(pt);
         float yaw = class_3532.method_17821(pt, pl.field_6241, pl.field_6259);
         float pitch = pl.method_5695(pt);
         int light = light(mc, pos);
         float time = (float)(System.nanoTime() / 1.0E9);
         class_4588 vc = buffers.method_73477(class_12249.method_75994(Accessories.WHITE));
         CosmeticRender.Emitter e = new CosmeticRender.Emitter(
            base,
            buffers,
            vc,
            light,
            time,
            (float)(pos.field_1352 - cam.field_1352),
            (float)(pos.field_1351 - cam.field_1351),
            (float)(pos.field_1350 - cam.field_1350),
            (float)Math.cos(-Math.toRadians(yaw)),
            (float)Math.sin(-Math.toRadians(yaw)),
            (float)Math.cos(Math.toRadians(pitch)),
            (float)Math.sin(Math.toRadians(pitch))
         );
         if (hat != null) {
            Hats.emit(Hats.ALL.indexOf(hat), time, e.pivot(Hats.ANCHOR_Y));
         }

         if (wing != null) {
            Wings.emit(wing, time, e.pivot(Wings.ANCHOR_Y));
         }
      } catch (Throwable var18) {
      }
   }

   private static int light(class_310 mc, class_243 pos) {
      try {
         class_2338 bp = class_2338.method_49637(pos.field_1352, pos.field_1351 + 1.4, pos.field_1350);
         return class_765.method_23687(mc.field_1687.method_8314(class_1944.field_9282, bp), mc.field_1687.method_8314(class_1944.field_9284, bp));
      } catch (Throwable var3) {
         return 15728880;
      }
   }

   @Environment(EnvType.CLIENT)
   private static final class Emitter implements CosmeticGeometry {
      private final Matrix4f base;
      private final class_4597 buffers;
      private final class_4588 vc;
      private final int light;
      private final float time;
      private final float ox;
      private final float oy;
      private final float oz;
      private final float cy;
      private final float sy;
      private final float cp;
      private final float sp;
      private final Map<class_2960, class_4588> textured = new HashMap<>();
      private final float[] v = new float[9];
      private float pivot;

      Emitter(
         Matrix4f base,
         class_4597 buffers,
         class_4588 vc,
         int light,
         float time,
         float ox,
         float oy,
         float oz,
         float cy,
         float sy,
         float cp,
         float sp
      ) {
         this.base = base;
         this.buffers = buffers;
         this.vc = vc;
         this.light = light;
         this.time = time;
         this.ox = ox;
         this.oy = oy;
         this.oz = oz;
         this.cy = cy;
         this.sy = sy;
         this.cp = cp;
         this.sp = sp;
      }

      private CosmeticRender.Emitter pivot(float y) {
         this.pivot = y;
         return this;
      }

      private void place(int i, float x, float y, float z) {
         float py = y - this.pivot;
         float y1 = py * this.cp - z * this.sp;
         float z1 = py * this.sp + z * this.cp;
         this.v[i] = this.ox + x * this.cy + z1 * this.sy;
         this.v[i + 1] = this.oy + y1 + this.pivot;
         this.v[i + 2] = this.oz - x * this.sy + z1 * this.cy;
      }

      @Override
      public void tri(
         float ax, float ay, float az,
         float bx, float by, float bz,
         float cx, float cy, float cz,
         int argb, boolean emissive
      ) {
         this.place(0, ax, ay, az);
         this.place(3, bx, by, bz);
         this.place(6, cx, cy, cz);
         this.flush(null, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, argb, emissive);
      }

      @Override
      public void triTex(
         float ax, float ay, float az, float au, float av,
         float bx, float by, float bz, float bu, float bv,
         float cx, float cy, float cz, float cu, float cv,
         CosmeticTexture tex, int argb, boolean emissive
      ) {
         this.place(0, ax, ay, az);
         this.place(3, bx, by, bz);
         this.place(6, cx, cy, cz);
         this.flush(tex, au, av, bu, bv, cu, cv, argb, emissive);
      }

      private void flush(
         CosmeticTexture tex,
         float au, float av,
         float bu, float bv,
         float cu, float cv,
         int argb,
         boolean emissive
      ) {
         float ux = this.v[3] - this.v[0];
         float uy = this.v[4] - this.v[1];
         float uz = this.v[5] - this.v[2];
         float vx = this.v[6] - this.v[0];
         float vy = this.v[7] - this.v[1];
         float vz = this.v[8] - this.v[2];
         float nx = uy * vz - uz * vy;
         float ny = uz * vx - ux * vz;
         float nz = ux * vy - uy * vx;
         float nl = (float)Math.sqrt(nx * nx + ny * ny + nz * nz);
         if (nl < 1.0E-6F) {
            return;
         }

         nx /= nl;
         ny /= nl;
         nz /= nl;
         class_4588 out = tex == null
            ? this.vc
            : this.textured.computeIfAbsent(
               CosmeticTextures.id(tex), id -> this.buffers.method_73477(class_12249.method_75994(id))
            );
         int frames = tex == null ? 1 : Math.max(1, tex.frames());
         float inv = 1.0F / frames;
         float off = 0.0F;
         if (frames > 1) {
            float fps = 20.0F / Math.max(0.05F, tex.frametime());
            off = (int)(this.time * fps) % frames;
         }

         int li = emissive ? 15728880 : this.light;
         this.vertex(out, 0, au, (av + off) * inv, argb, li, nx, ny, nz);
         this.vertex(out, 3, bu, (bv + off) * inv, argb, li, nx, ny, nz);
         this.vertex(out, 6, cu, (cv + off) * inv, argb, li, nx, ny, nz);
      }

      private void vertex(class_4588 out, int i, float u, float w, int argb, int li, float nx, float ny, float nz) {
         out.method_22918(this.base, this.v[i], this.v[i + 1], this.v[i + 2])
            .method_39415(argb)
            .method_22913(u, w)
            .method_22922(class_4608.field_21444)
            .method_60803(li)
            .method_22914(nx, ny, nz);
      }
   }
}
