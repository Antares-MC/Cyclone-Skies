package dev.valence.emote;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4608;
import net.minecraft.class_7833;
import net.minecraft.class_4587.class_4665;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public final class EmoteLimbs {
   private static final EmoteLimbs.Limb[] WIDE = new EmoteLimbs.Limb[]{
      new EmoteLimbs.Limb(-5.0F, 2.0F, 0.0F, -3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 40, 16, 40, 32, 6.0F, false),
      new EmoteLimbs.Limb(5.0F, 2.0F, 0.0F, -1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 32, 48, 48, 48, 6.0F, false),
      new EmoteLimbs.Limb(-1.9F, 12.0F, 0.0F, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0, 16, 0, 32, 6.0F, true),
      new EmoteLimbs.Limb(1.9F, 12.0F, 0.0F, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 16, 48, 0, 48, 6.0F, true)
   };
   private static final EmoteLimbs.Limb[] SLIM = new EmoteLimbs.Limb[]{
      new EmoteLimbs.Limb(-5.0F, 2.5F, 0.0F, -2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, 40, 16, 40, 32, 6.0F, false),
      new EmoteLimbs.Limb(5.0F, 2.5F, 0.0F, -1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, 32, 48, 48, 48, 6.0F, false),
      WIDE[2],
      WIDE[3]
   };

   private EmoteLimbs() {
   }

   public static void draw(class_4587 ps, EmotePose p, float w, boolean slim, boolean[] overlay, EmoteLimbs.Out out) {
      EmoteLimbs.Limb[] limbs = slim ? SLIM : WIDE;
      float[][] rots = new float[][]{p.rArm, p.lArm, p.rLeg, p.lLeg};
      float[][] offs = new float[][]{p.rArmOff, p.lArmOff, p.rLegOff, p.lLegOff};

      for (int i = 0; i < 4; i++) {
         EmoteLimbs.Limb l = limbs[i];
         float[] r = rots[i];
         float[] o = offs[i];
         ps.method_22903();
         ps.method_46416((l.px + o[0] * w) / 16.0F, (l.py + o[1] * w) / 16.0F, (l.pz + o[2] * w) / 16.0F);
         ps.method_22907(new Quaternionf().rotationZYX((float)Math.toRadians(r[2] * w), (float)Math.toRadians(r[1] * w), (float)Math.toRadians(r[0] * w)));
         float s = l.joint;
         box(ps.method_23760(), l, l.u, l.v, 0.0F, 0, (int)s, out);
         if (overlay == null || overlay[i]) {
            box(ps.method_23760(), l, l.ou, l.ov, 0.25F, 0, (int)s, out);
         }

         float jy = (l.y + s) / 16.0F;
         float bend = p.bend[i] * w;
         float axis = p.bendAxis[i];
         ps.method_22903();
         if (Math.abs(bend) > 0.01F) {
            ps.method_46416(0.0F, jy, 0.0F);
            ps.method_22907(class_7833.field_40716.rotationDegrees(axis));
            ps.method_22907(class_7833.field_40714.rotationDegrees(l.leg ? bend : -bend));
            ps.method_22907(class_7833.field_40716.rotationDegrees(-axis));
            ps.method_46416(0.0F, -jy, 0.0F);
         }

         box(ps.method_23760(), l, l.u, l.v, 0.0F, (int)s, (int)l.h, out);
         if (overlay == null || overlay[i]) {
            box(ps.method_23760(), l, l.ou, l.ov, 0.25F, (int)s, (int)l.h, out);
         }

         ps.method_22909();
         ps.method_22909();
      }
   }

   public static void toHand(class_4587 ps, EmotePose p, float w, boolean slim, int i) {
      EmoteLimbs.Limb l = (slim ? SLIM : WIDE)[i];
      float[] r = i == 0 ? p.rArm : (i == 1 ? p.lArm : (i == 2 ? p.rLeg : p.lLeg));
      float[] o = i == 0 ? p.rArmOff : (i == 1 ? p.lArmOff : (i == 2 ? p.rLegOff : p.lLegOff));
      ps.method_46416((l.px + o[0] * w) / 16.0F, (l.py + o[1] * w) / 16.0F, (l.pz + o[2] * w) / 16.0F);
      ps.method_22907(new Quaternionf().rotationZYX((float)Math.toRadians(r[2] * w), (float)Math.toRadians(r[1] * w), (float)Math.toRadians(r[0] * w)));
      float jy = (l.y + l.joint) / 16.0F;
      float bend = p.bend[i] * w;
      if (Math.abs(bend) > 0.01F) {
         ps.method_46416(0.0F, jy, 0.0F);
         ps.method_22907(class_7833.field_40716.rotationDegrees(p.bendAxis[i]));
         ps.method_22907(class_7833.field_40714.rotationDegrees(l.leg ? bend : -bend));
         ps.method_22907(class_7833.field_40716.rotationDegrees(-p.bendAxis[i]));
         ps.method_46416(0.0F, -jy, 0.0F);
      }

      ps.method_46416((l.x + l.w / 2.0F) / 16.0F, (l.y + l.h) / 16.0F, (l.z + l.d / 2.0F) / 16.0F);
   }

   private static void box(class_4665 pose, EmoteLimbs.Limb l, int u, int v, float inf, int s0, int s1, EmoteLimbs.Out out) {
      int w = (int)l.w;
      int h = (int)l.h;
      int d = (int)l.d;
      float x0 = (l.x - inf) / 16.0F;
      float x1 = (l.x + l.w + inf) / 16.0F;
      float z0 = (l.z - inf) / 16.0F;
      float z1 = (l.z + l.d + inf) / 16.0F;
      float y0 = (l.y + s0 - (s0 == 0 ? inf : 0.15F)) / 16.0F;
      float y1 = (l.y + s1 + (s1 >= h ? inf : 0.15F)) / 16.0F;
      int f4 = u;
      int f5 = u + d;
      int f6 = u + d + w;
      int f7 = u + d + w + w;
      int f8 = u + d + w + d;
      int f9 = u + d + w + d + w;
      int f10 = v;
      int f11 = v + d;
      int a = v + d + s0;
      int b = v + d + s1;
      out.face(pose, x0, y0, z1, x1, y0, z1, x0, y0, z0, f5, f10, f6, f11, 0.0F, -1.0F, 0.0F);
      out.face(pose, x0, y1, z0, x1, y1, z0, x0, y1, z1, f6, f11, f7, f10, 0.0F, 1.0F, 0.0F);
      out.face(pose, x0, y0, z1, x0, y0, z0, x0, y1, z1, f4, a, f5, b, -1.0F, 0.0F, 0.0F);
      out.face(pose, x0, y0, z0, x1, y0, z0, x0, y1, z0, f5, a, f6, b, 0.0F, 0.0F, -1.0F);
      out.face(pose, x1, y0, z0, x1, y0, z1, x1, y1, z0, f6, a, f8, b, 1.0F, 0.0F, 0.0F);
      out.face(pose, x1, y0, z1, x0, y0, z1, x1, y1, z1, f8, a, f9, b, 0.0F, 0.0F, 1.0F);
   }

   public static EmoteLimbs.Out vertices(class_4588 vc, int light) {
      return (pose, ax, ay, az, bx, by, bz, dx, dy, dz, u1, v1, u2, v2, nx, ny, nz) -> {
         float cx = bx + dx - ax;
         float cy = by + dy - ay;
         float cz = bz + dz - az;
         vert(vc, pose, bx, by, bz, u2, v1, light, nx, ny, nz);
         vert(vc, pose, ax, ay, az, u1, v1, light, nx, ny, nz);
         vert(vc, pose, dx, dy, dz, u1, v2, light, nx, ny, nz);
         vert(vc, pose, cx, cy, cz, u2, v2, light, nx, ny, nz);
      };
   }

   private static void vert(class_4588 vc, class_4665 pose, float x, float y, float z, int u, int v, int light, float nx, float ny, float nz) {
      vc.method_56824(pose, x, y, z)
         .method_39415(-1)
         .method_22913(u / 64.0F, v / 64.0F)
         .method_22922(class_4608.field_21444)
         .method_60803(light)
         .method_60831(pose, nx, ny, nz);
   }

   @Environment(EnvType.CLIENT)
   private record Limb(
      float px, float py, float pz, float x, float y, float z, float w, float h, float d, int u, int v, int ou, int ov, float joint, boolean leg
   ) {
   }

   @Environment(EnvType.CLIENT)
   public interface Out {
      void face(
         class_4665 var1,
         float var2,
         float var3,
         float var4,
         float var5,
         float var6,
         float var7,
         float var8,
         float var9,
         float var10,
         int var11,
         int var12,
         int var13,
         int var14,
         float var15,
         float var16,
         float var17
      );
   }
}
