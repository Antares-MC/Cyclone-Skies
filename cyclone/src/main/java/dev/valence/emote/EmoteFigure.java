package dev.valence.emote;

import dev.valence.compat.Skins;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_742;
import net.minecraft.class_7833;
import net.minecraft.class_4587.class_4665;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public final class EmoteFigure {
   private static final float VIEW_PITCH = 10.0F;
   private static final List<EmoteFigure.Face> FACES = new ArrayList<>();
   private static final Map<Integer, float[]> FIT = new HashMap<>();
   private static final EmotePose POSE = new EmotePose();
   private static final Vector3f L = new Vector3f(0.35F, -0.55F, -0.75F).normalize();
   private static int used;
   private static final EmoteFigure.Part[] WIDE = parts(false);
   private static final EmoteFigure.Part[] SLIM = parts(true);
   private static final Vector3f A = new Vector3f();
   private static final Vector3f B = new Vector3f();
   private static final Vector3f D = new Vector3f();
   private static final Vector3f N = new Vector3f();
   private static final Vector3f C = new Vector3f();

   private EmoteFigure() {
   }

   private static EmoteFigure.Face next() {
      if (used == FACES.size()) {
         FACES.add(new EmoteFigure.Face());
      }

      return FACES.get(used++);
   }

   private static EmoteFigure.Part[] parts(boolean slim) {
      float aw = slim ? 3.0F : 4.0F;
      float ay = slim ? 2.5F : 2.0F;
      return new EmoteFigure.Part[]{
         new EmoteFigure.Part(0, 0.0F, 0.0F, 0.0F, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0, 0, 32, 0, 0.5F),
         new EmoteFigure.Part(1, 0.0F, 0.0F, 0.0F, -4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 16, 16, 16, 32, 0.25F),
         new EmoteFigure.Part(2, -5.0F, ay, 0.0F, slim ? -2.0F : -3.0F, -2.0F, -2.0F, aw, 12.0F, 4.0F, 40, 16, 40, 32, 0.25F),
         new EmoteFigure.Part(3, 5.0F, ay, 0.0F, -1.0F, -2.0F, -2.0F, aw, 12.0F, 4.0F, 32, 48, 48, 48, 0.25F),
         new EmoteFigure.Part(4, -1.9F, 12.0F, 0.0F, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0, 16, 0, 32, 0.25F),
         new EmoteFigure.Part(5, 1.9F, 12.0F, 0.0F, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 16, 48, 0, 48, 0.25F)
      };
   }

   public static void draw(class_332 g, float x0, float y0, float x1, float y1, int id, float t, float yaw, float alpha) {
      Emotes.Emote e = Emotes.get(id);
      class_310 mc = class_310.method_1551();
      if (e != null && mc.field_1724 != null && !(alpha <= 0.01F)) {
         class_742 pl = mc.field_1724;
         class_2960 skin = Skins.texture(pl);
         boolean slim = Skins.slim(pl);
         float[] fit = FIT.computeIfAbsent(id * 31 + Math.round(yaw), k -> fit(e, yaw, slim));
         float bw = fit[2] - fit[0];
         float bh = fit[3] - fit[1];
         float scale = Math.min((x1 - x0) / Math.max(0.1F, bw), (y1 - y0) / Math.max(0.1F, bh));
         float cx = (x0 + x1) / 2.0F - (fit[0] + fit[2]) / 2.0F * scale;
         float cy = (y0 + y1) / 2.0F - (fit[1] + fit[3]) / 2.0F * scale;
         used = 0;
         collect(e, t, yaw, slim, skin);
         FACES.subList(0, used).sort((ax, b) -> Float.compare(b.depth, ax.depth));
         int a = Math.round(255.0F * Math.min(1.0F, alpha));

         for (int i = 0; i < used; i++) {
            EmoteFigure.Face f = FACES.get(i);
            Matrix3x2f m = new Matrix3x2f(f.ex * scale, f.ey * scale, f.fx * scale, f.fy * scale, cx + f.ox * scale, cy + f.oy * scale);
            int col = (f.color >>> 24) * a / 255 << 24 | f.color & 16777215;
            g.method_51448().pushMatrix();
            g.method_51448().mul(m);
            if (f.tex != null) {
               g.method_25293(class_10799.field_56883, f.tex, 0, 0, f.u, f.v, f.w, f.h, f.w, f.h, 64, 64, col);
            } else {
               g.method_25294(0, 0, 1, 1, col);
            }

            g.method_51448().popMatrix();
         }
      }
   }

   private static float[] fit(Emotes.Emote e, float yaw, boolean slim) {
      float[] b = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};

      for (int s = 0; s < 40; s++) {
         used = 0;
         collect(e, s * 0.29F, yaw, slim, null);

         for (int i = 0; i < used; i++) {
            EmoteFigure.Face f = FACES.get(i);
            float[] xs = new float[]{
               f.ox, f.ox + f.ex * Math.max(1, f.w), f.ox + f.fx * Math.max(1, f.h), f.ox + f.ex * Math.max(1, f.w) + f.fx * Math.max(1, f.h)
            };
            float[] ys = new float[]{
               f.oy, f.oy + f.ey * Math.max(1, f.w), f.oy + f.fy * Math.max(1, f.h), f.oy + f.ey * Math.max(1, f.w) + f.fy * Math.max(1, f.h)
            };

            for (int k = 0; k < 4; k++) {
               b[0] = Math.min(b[0], xs[k]);
               b[1] = Math.min(b[1], ys[k]);
               b[2] = Math.max(b[2], xs[k]);
               b[3] = Math.max(b[3], ys[k]);
            }
         }
      }

      used = 0;
      float px = (b[2] - b[0]) * 0.04F;
      float py = (b[3] - b[1]) * 0.04F;
      return new float[]{b[0] - px, b[1] - py, b[2] + px, b[3] + py};
   }

   private static void collect(Emotes.Emote e, float t, float yaw, boolean slim, class_2960 skin) {
      EmotePose p = POSE.reset();
      e.anim().pose(t, p);
      class_4587 ps = new class_4587();
      ps.method_22907(class_7833.field_40714.rotationDegrees(10.0F));
      ps.method_22907(class_7833.field_40716.rotationDegrees(yaw));
      EmoteProps.Emitter out = (stack, geo) -> geo.draw(stack.method_23760(), EmoteFigure::colorBox);
      EmoteProps.world(ps, out, p, 1.0F, t);
      EmoteAnimator.applyRoot(ps, p, 1.0F);
      EmoteProps.body(ps, out, p, 1.0F, t, slim);
      ps.method_46416(0.0F, -1.501F, 0.0F);
      EmoteFigure.Part[] parts = slim ? SLIM : WIDE;
      EmoteLimbs.draw(
         ps,
         p,
         1.0F,
         slim,
         null,
         (pose, ax, ay, az, bx, by, bz, dx, dy, dz, u1, v1, u2, v2, nx, ny, nz) -> face(
            pose.method_23761(), skin, ax, ay, az, bx, by, bz, dx, dy, dz, u1, v1, u2, v2, nx, ny, nz, 0.0F
         )
      );

      for (EmoteFigure.Part part : parts) {
         if (part.id < 2) {
            float[] rot;
            float[] off;
            switch (part.id) {
               case 0:
                  rot = p.head;
                  off = p.headOff;
                  break;
               case 1:
                  rot = p.body;
                  off = null;
                  break;
               case 2:
                  rot = p.rArm;
                  off = p.rArmOff;
                  break;
               case 3:
                  rot = p.lArm;
                  off = p.lArmOff;
                  break;
               case 4:
                  rot = p.rLeg;
                  off = p.rLegOff;
                  break;
               default:
                  rot = p.lLeg;
                  off = p.lLegOff;
            }

            ps.method_22903();
            float ox = off == null ? 0.0F : off[0];
            float oy = off == null ? 0.0F : off[1];
            float oz = off == null ? 0.0F : off[2];
            ps.method_46416((part.px + ox) / 16.0F, (part.py + oy) / 16.0F, (part.pz + oz) / 16.0F);
            ps.method_22907(new Quaternionf().rotationZYX((float)Math.toRadians(rot[2]), (float)Math.toRadians(rot[1]), (float)Math.toRadians(rot[0])));
            if (part.id == 0) {
               ps.method_22905(p.headScale * p.headSX, p.headScale * p.headSY, p.headScale * p.headSX);
            }

            Matrix4f m = ps.method_23760().method_23761();
            skinBox(m, part, part.u, part.v, 0.0F, skin, 0.0F);
            skinBox(m, part, part.ou, part.ov, part.inflate, skin, -5.0E-4F);
            ps.method_22909();
         }
      }
   }

   private static void skinBox(Matrix4f m, EmoteFigure.Part part, int u, int v, float inf, class_2960 skin, float bias) {
      float x0 = (part.x - inf) / 16.0F;
      float y0 = (part.y - inf) / 16.0F;
      float z0 = (part.z - inf) / 16.0F;
      float x1 = (part.x + part.w + inf) / 16.0F;
      float y1 = (part.y + part.h + inf) / 16.0F;
      float z1 = (part.z + part.d + inf) / 16.0F;
      int w = (int)part.w;
      int h = (int)part.h;
      int d = (int)part.d;
      int f4 = u;
      int f5 = u + d;
      int f6 = u + d + w;
      int f7 = u + d + w + w;
      int f8 = u + d + w + d;
      int f9 = u + d + w + d + w;
      int f10 = v;
      int f11 = v + d;
      int f12 = v + d + h;
      face(m, skin, x0, y0, z1, x1, y0, z1, x0, y0, z0, f5, f10, f6, f11, 0.0F, -1.0F, 0.0F, bias);
      face(m, skin, x0, y1, z0, x1, y1, z0, x0, y1, z1, f6, f11, f7, f10, 0.0F, 1.0F, 0.0F, bias);
      face(m, skin, x0, y0, z1, x0, y0, z0, x0, y1, z1, f4, f11, f5, f12, -1.0F, 0.0F, 0.0F, bias);
      face(m, skin, x0, y0, z0, x1, y0, z0, x0, y1, z0, f5, f11, f6, f12, 0.0F, 0.0F, -1.0F, bias);
      face(m, skin, x1, y0, z0, x1, y0, z1, x1, y1, z0, f6, f11, f8, f12, 1.0F, 0.0F, 0.0F, bias);
      face(m, skin, x1, y0, z1, x0, y0, z1, x1, y1, z1, f8, f11, f9, f12, 0.0F, 0.0F, 1.0F, bias);
   }

   private static void face(
      Matrix4f m,
      class_2960 skin,
      float ax,
      float ay,
      float az,
      float bx,
      float by,
      float bz,
      float dx,
      float dy,
      float dz,
      int u1,
      int v1,
      int u2,
      int v2,
      float nx,
      float ny,
      float nz,
      float bias
   ) {
      m.transformDirection(N.set(nx, ny, nz)).normalize();
      if (!(N.z >= -0.001F)) {
         m.transformPosition(A.set(ax, ay, az));
         m.transformPosition(B.set(bx, by, bz));
         m.transformPosition(D.set(dx, dy, dz));
         int du = u2 - u1;
         int dv = v2 - v1;
         if (du != 0 && dv != 0) {
            float exx = (B.x - A.x) / du;
            float exy = (B.y - A.y) / du;
            float eyx = (D.x - A.x) / dv;
            float eyy = (D.y - A.y) / dv;
            int umin = Math.min(u1, u2);
            int vmin = Math.min(v1, v2);
            EmoteFigure.Face f = next();
            f.ox = A.x + exx * (umin - u1) + eyx * (vmin - v1);
            f.oy = A.y + exy * (umin - u1) + eyy * (vmin - v1);
            f.ex = exx;
            f.ey = exy;
            f.fx = eyx;
            f.fy = eyy;
            f.tex = skin;
            f.u = umin;
            f.v = vmin;
            f.w = Math.abs(du);
            f.h = Math.abs(dv);
            C.set(A).add(B).add(D).mul(0.33333334F);
            f.depth = (A.z + B.z + D.z + (B.z + D.z - A.z)) / 4.0F + bias;
            f.color = shade(-1, N);
            grow(f, 0.012F);
         }
      }
   }

   private static void grow(EmoteFigure.Face f, float k) {
      float cxp = f.ox + (f.ex * f.w + f.fx * f.h) * 0.5F;
      float cyp = f.oy + (f.ey * f.w + f.fy * f.h) * 0.5F;
      f.ex *= 1.0F + k;
      f.ey *= 1.0F + k;
      f.fx *= 1.0F + k;
      f.fy *= 1.0F + k;
      f.ox = cxp - (f.ex * f.w + f.fx * f.h) * 0.5F;
      f.oy = cyp - (f.ey * f.w + f.fy * f.h) * 0.5F;
   }

   private static void colorBox(class_4665 pose, float x0, float y0, float z0, float x1, float y1, float z1, int argb, boolean glow) {
      Matrix4f m = pose.method_23761();
      cface(m, x0, y0, z1, x1, y0, z1, x0, y0, z0, 0.0F, -1.0F, 0.0F, argb, glow);
      cface(m, x0, y1, z0, x1, y1, z0, x0, y1, z1, 0.0F, 1.0F, 0.0F, argb, glow);
      cface(m, x0, y0, z1, x0, y0, z0, x0, y1, z1, -1.0F, 0.0F, 0.0F, argb, glow);
      cface(m, x0, y0, z0, x1, y0, z0, x0, y1, z0, 0.0F, 0.0F, -1.0F, argb, glow);
      cface(m, x1, y0, z0, x1, y0, z1, x1, y1, z0, 1.0F, 0.0F, 0.0F, argb, glow);
      cface(m, x1, y0, z1, x0, y0, z1, x1, y1, z1, 0.0F, 0.0F, 1.0F, argb, glow);
   }

   private static void cface(
      Matrix4f m,
      float ax,
      float ay,
      float az,
      float bx,
      float by,
      float bz,
      float dx,
      float dy,
      float dz,
      float nx,
      float ny,
      float nz,
      int argb,
      boolean glow
   ) {
      m.transformDirection(N.set(nx, ny, nz)).normalize();
      if (!(N.z >= -0.001F)) {
         m.transformPosition(A.set(ax, ay, az));
         m.transformPosition(B.set(bx, by, bz));
         m.transformPosition(D.set(dx, dy, dz));
         EmoteFigure.Face f = next();
         f.ox = A.x;
         f.oy = A.y;
         f.ex = B.x - A.x;
         f.ey = B.y - A.y;
         f.fx = D.x - A.x;
         f.fy = D.y - A.y;
         f.tex = null;
         f.w = 1;
         f.h = 1;
         f.depth = (A.z + B.z + D.z + (B.z + D.z - A.z)) / 4.0F - (glow ? 0.002F : 0.0F);
         f.color = glow ? argb : shade(argb, N);
         grow(f, 0.02F);
      }
   }

   private static int shade(int argb, Vector3f n) {
      float l = 0.62F + 0.38F * Math.max(0.0F, n.dot(L));
      int r = Math.min(255, Math.round((argb >> 16 & 0xFF) * l));
      int g = Math.min(255, Math.round((argb >> 8 & 0xFF) * l));
      int b = Math.min(255, Math.round((argb & 0xFF) * l));
      return argb & 0xFF000000 | r << 16 | g << 8 | b;
   }

   @Environment(EnvType.CLIENT)
   private static final class Face {
      float depth;
      float ox;
      float oy;
      float ex;
      float ey;
      float fx;
      float fy;
      class_2960 tex;
      int u;
      int v;
      int w;
      int h;
      int color;
   }

   @Environment(EnvType.CLIENT)
   private record Part(int id, float px, float py, float pz, float x, float y, float z, float w, float h, float d, int u, int v, int ou, int ov, float inflate) {
   }
}
