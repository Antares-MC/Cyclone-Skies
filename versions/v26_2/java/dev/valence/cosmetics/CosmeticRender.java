package dev.valence.cosmetics;

import dev.valence.compat.Buffers;
import dev.valence.compat.Layers;
import dev.valence.compat.WorldRenderHook;
import dev.valence.cord.cosmetic.Accessories;
import dev.valence.license.License;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.LightLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.valence.compat.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.LightCoordsUtil;
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
         WorldRenderHook.afterEntities(CosmeticRender::render);
      }
   }

   private static void render(WorldRenderHook.Frame ctx) {
      net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
      if (mc.player == null || mc.level == null) {
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

      net.minecraft.client.player.LocalPlayer pl = mc.player;
      if (pl.isInvisible() || pl.isSleeping() || pl.isSwimming() || pl.isFallFlying()) {
         return;
      }

      if (mc.options.getCameraType().isFirstPerson()) {
         return;
      }

      if (!Accessories.whiteTex()) {
         return;
      }

      try {
         Matrix4f base = new Matrix4f(ctx.matrices().last().pose());
         dev.valence.compat.MultiBufferSource buffers = ctx.consumers();
         net.minecraft.world.phys.Vec3 cam = ctx.camera();
         float pt = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
         net.minecraft.world.phys.Vec3 pos = pl.getPosition(pt);
         float yaw = net.minecraft.util.Mth.rotLerp(pt, pl.yHeadRot, pl.yHeadRotO);
         float pitch = pl.getViewXRot(pt);
         int light = light(mc, pos);
         float time = (float)(System.nanoTime() / 1.0E9);
         com.mojang.blaze3d.vertex.VertexConsumer vc = Buffers.get(buffers, Layers.cutout(Accessories.WHITE));
         CosmeticRender.Emitter e = new CosmeticRender.Emitter(
            base,
            buffers,
            vc,
            light,
            time,
            (float)(pos.x - cam.x),
            (float)(pos.y - cam.y),
            (float)(pos.z - cam.z),
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

   private static int light(net.minecraft.client.Minecraft mc, net.minecraft.world.phys.Vec3 pos) {
      try {
         net.minecraft.core.BlockPos bp = net.minecraft.core.BlockPos.containing(pos.x, pos.y + 1.4, pos.z);
         return net.minecraft.util.LightCoordsUtil.pack(mc.level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, bp), mc.level.getBrightness(net.minecraft.world.level.LightLayer.SKY, bp));
      } catch (Throwable var3) {
         return 15728880;
      }
   }

   @Environment(EnvType.CLIENT)
   private static final class Emitter implements CosmeticGeometry {
      private final Matrix4f base;
      private final dev.valence.compat.MultiBufferSource buffers;
      private final com.mojang.blaze3d.vertex.VertexConsumer vc;
      private final int light;
      private final float time;
      private final float ox;
      private final float oy;
      private final float oz;
      private final float cy;
      private final float sy;
      private final float cp;
      private final float sp;
      private final Map<net.minecraft.resources.Identifier, com.mojang.blaze3d.vertex.VertexConsumer> textured = new HashMap<>();
      private final float[] v = new float[9];
      private float pivot;

      Emitter(
         Matrix4f base,
         dev.valence.compat.MultiBufferSource buffers,
         com.mojang.blaze3d.vertex.VertexConsumer vc,
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
         com.mojang.blaze3d.vertex.VertexConsumer out = tex == null
            ? this.vc
            : this.textured.computeIfAbsent(
               CosmeticTextures.id(tex), id -> Buffers.get(this.buffers, Layers.cutout(id))
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

      private void vertex(com.mojang.blaze3d.vertex.VertexConsumer out, int i, float u, float w, int argb, int li, float nx, float ny, float nz) {
         out.addVertex(this.base, this.v[i], this.v[i + 1], this.v[i + 2])
            .setColor(argb)
            .setUv(u, w)
            .setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
            .setLight(li)
            .setNormal(nx, ny, nz);
      }
   }
}
