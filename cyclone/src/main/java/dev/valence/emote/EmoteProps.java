package dev.valence.emote;

import dev.valence.compat.EmoteBatch;
import dev.valence.compat.Layers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4608;
import net.minecraft.class_7833;
import net.minecraft.class_4587.class_4665;

@Environment(EnvType.CLIENT)
public final class EmoteProps {
   private static final class_2960 WHITE = class_2960.method_60655("valence", "textures/emote/white.png");
   private static final int FULL_BRIGHT = 15728880;
   private static final String[] SIX = new String[]{"111", "100", "111", "101", "111"};
   private static final String[] SEVEN = new String[]{"111", "001", "011", "010", "010"};
   private static float[][] ICO_V;
   private static int[][] ICO_F;
   private static float[][] PENT;

   private EmoteProps() {
   }

   public static EmoteProps.Emitter worldEmitter(EmoteBatch col, int light) {
      return (ps, g) -> {
         col.submit(ps, Layers.cutout(WHITE), (pose, vc) -> g.draw(pose, new EmoteProps.VcSink(vc, light, false)));
         col.submit(ps, Layers.translucentEmissive(WHITE), (pose, vc) -> g.draw(pose, new EmoteProps.VcSink(vc, 15728880, true)));
      };
   }

   public static void world(class_4587 ps, EmoteProps.Emitter out, EmotePose p, float w, float time) {
      switch (p.prop) {
         case 2:
            float grow = Math.max(0.001F, p.propB * w);
            ps.method_22903();
            ps.method_22905(1.0F, grow, 1.0F);
            out.emit(ps, EmoteProps::lamp);
            ps.method_22909();
         case 3:
         case 4:
         case 5:
         case 7:
         case 8:
         default:
            break;
         case 6: {
            float k = p.propB * w;
            if (k > 0.01F) {
               ps.method_22903();
               ps.method_46416(0.0F, 0.0F, 1.2F);
               ps.method_22905(1.0F, k, 1.0F);
               out.emit(ps, EmoteProps::grave);
               ps.method_22909();
            }
            break;
         }
         case 9: {
            float k = Math.max(0.001F, p.propB * w);
            ps.method_22903();
            ps.method_22905(k, k, k);
            out.emit(ps, (pose, sink) -> cozy(pose, sink, time));
            ps.method_22909();
            break;
         }
         case 10: {
            float k = Math.max(0.001F, p.propB * w);
            ps.method_22903();
            ps.method_46416(0.4F, 0.0F, -0.95F);
            ps.method_22905(k, k, k);
            ps.method_22907(class_7833.field_40716.rotationDegrees(time * 45.0F));
            out.emit(ps, EmoteProps::doner);
            ps.method_22909();
         }
      }
   }

   public static void body(class_4587 ps, EmoteProps.Emitter out, EmotePose p, float w, float time, boolean slim) {
      switch (p.prop) {
         case 1: {
            float k = Math.max(0.001F, p.propB * w);
            float flash = p.propA * w;
            ps.method_22903();
            ps.method_46416(0.0F, -1.66F, -0.52F);
            ps.method_22905(k, k, k);
            out.emit(ps, (pose, sink) -> camera(pose, sink, flash, time));
            ps.method_22909();
         }
         case 2:
         case 6:
         default:
            break;
         case 3: {
            float k = Math.max(0.001F, p.propB * w);
            float lift = p.lift * w;
            float spin = p.propA;
            ps.method_22903();
            ps.method_46416(0.0F, lift, 0.0F);
            ps.method_22905(k, k, k);
            out.emit(ps, (pose, sink) -> wheelchair(pose, sink, spin));
            ps.method_22909();
            break;
         }
         case 4: {
            float k = p.propA * w;
            if (k > 0.02F) {
               out.emit(ps, (pose, sink) -> sparks(pose, sink, time, k));
            }
            break;
         }
         case 5: {
            float k = p.propB * w;
            if (k > 0.01F) {
               float b = p.propA;
               ps.method_22903();
               ps.method_46416(0.0F, -2.15F, 0.0F);
               ps.method_22905(k, k, k);
               out.emit(ps, (pose, sink) -> {
                  digit(pose, sink, SIX, -0.36F, -0.06F * b, -742954);
                  digit(pose, sink, SEVEN, 0.1F, 0.06F * b, -7937038);
               });
               ps.method_22909();
            }
            break;
         }
         case 7: {
            float x = p.propPos[0];
            float y = p.propPos[1];
            float z = p.propPos[2];
            float spin = time * 540.0F;
            ps.method_22903();
            ps.method_46416(x, -y, z);
            ps.method_22907(class_7833.field_40714.rotationDegrees(spin));
            ps.method_22907(class_7833.field_40718.rotationDegrees(spin * 0.4F));
            ps.method_22905(w, w, w);
            out.emit(ps, EmoteProps::ball);
            ps.method_22909();
            break;
         }
         case 8:
            float whistle = p.propA * w;
            float card = p.propB * w;
            if (whistle > 0.01F) {
               ps.method_22903();
               ps.method_46416(0.0F, -1.55F, -0.3F);
               ps.method_22905(whistle, whistle, whistle);
               out.emit(ps, EmoteProps::whistle);
               ps.method_22909();
            }

            if (card > 0.01F) {
               hand(ps, p, w, slim, 0, out, (pose, sink) -> redCard(pose, sink, card));
            }
            break;
         case 9:
            hand(ps, p, w, slim, 0, out, (pose, sink) -> mug(pose, sink, time));
            break;
         case 10:
            hand(ps, p, w, slim, 0, out, (pose, sink) -> knife(pose, sink));
            break;
         case 11:
            float up = p.propA;
            hand(ps, p, w, slim, 0, out, (pose, sink) -> trophy(pose, sink, up));
      }
   }

   private static void hand(class_4587 ps, EmotePose p, float w, boolean slim, int limb, EmoteProps.Emitter out, EmoteProps.Geometry g) {
      EmotePose copy = new EmotePose();
      copy.fromArray(p.toArray(new float[57]));
      ps.method_22903();
      ps.method_46416(0.0F, -1.501F, 0.0F);
      EmoteLimbs.toHand(ps, copy, w, slim, limb);
      out.emit(ps, g);
      ps.method_22909();
   }

   private static void digit(class_4665 pose, EmoteProps.Sink s, String[] rows, float x, float dy, int color) {
      float v = 0.085F;

      for (int r = 0; r < 5; r++) {
         for (int c = 0; c < 3; c++) {
            if (rows[r].charAt(c) == '1') {
               float x0 = x + c * v;
               float y0 = dy - (5 - r) * v;
               s.box(pose, x0, y0, -v / 2.0F, x0 + v, y0 + v, v / 2.0F, color, false);
            }
         }
      }
   }

   private static void grave(class_4665 pose, EmoteProps.Sink s) {
      int stone = -7565162;
      int dark = -10723226;
      s.box(pose, -0.3F, -0.05F, -0.12F, 0.3F, 0.0F, 0.12F, dark, false);
      s.box(pose, -0.24F, -0.62F, -0.07F, 0.24F, -0.05F, 0.07F, stone, false);
      s.box(pose, -0.18F, -0.7F, -0.07F, 0.18F, -0.62F, 0.07F, stone, false);
      s.box(pose, -0.025F, -0.55F, -0.075F, 0.025F, -0.2F, -0.07F, dark, false);
      s.box(pose, -0.1F, -0.47F, -0.075F, 0.1F, -0.42F, -0.07F, dark, false);
   }

   private static void octo(class_4665 base, EmoteProps.Sink s, float y0, float y1, float r, int color) {
      s.box(base, -r, y0, -r, r, y1, r, color, false);
      class_4587 ps = new class_4587();
      ps.method_23760().method_23761().set(base.method_23761());
      ps.method_22907(class_7833.field_40716.rotationDegrees(45.0F));
      s.box(ps.method_23760(), -r, y0, -r, r, y1, r, color, false);
   }

   private static void ball(class_4665 pose, EmoteProps.Sink s) {
      float r = 0.14F;
      if (ICO_V == null) {
         buildIco();
      }

      if (!s.triangles()) {
         int n = 7;
         float c = 2.0F * r / n;

         for (int ix = 0; ix < n; ix++) {
            for (int iy = 0; iy < n; iy++) {
               for (int iz = 0; iz < n; iz++) {
                  float x = -r + (ix + 0.5F) * c;
                  float y = -r + (iy + 0.5F) * c;
                  float z = -r + (iz + 0.5F) * c;
                  float l = (float)Math.sqrt(x * x + y * y + z * z);
                  if (!(l > r) && !(l < r - 2.2F * c)) {
                     boolean black = false;

                     for (float[] q : PENT) {
                        if ((x * q[0] + y * q[1] + z * q[2]) / l > 0.9F) {
                           black = true;
                           break;
                        }
                     }

                     s.box(pose, x - c / 2.0F, y - c / 2.0F, z - c / 2.0F, x + c / 2.0F, y + c / 2.0F, z + c / 2.0F, black ? -15329770 : -592142, false);
                  }
               }
            }
         }
      } else {
         if (ICO_V == null) {
            buildIco();
         }

         float[] a = new float[3];
         float[] b = new float[3];
         float[] c = new float[3];

         for (int[] f : ICO_F) {
            float[] p0 = ICO_V[f[0]];
            float[] p1 = ICO_V[f[1]];
            float[] p2 = ICO_V[f[2]];
            float mx = p0[0] + p1[0] + p2[0];
            float my = p0[1] + p1[1] + p2[1];
            float mz = p0[2] + p1[2] + p2[2];
            float ml = (float)Math.sqrt(mx * mx + my * my + mz * mz);
            boolean black = false;

            for (float[] q : PENT) {
               if ((mx * q[0] + my * q[1] + mz * q[2]) / ml > 0.93F) {
                  black = true;
                  break;
               }
            }

            for (int k = 0; k < 3; k++) {
               a[k] = p0[k] * r;
               b[k] = p1[k] * r;
               c[k] = p2[k] * r;
            }

            s.tri(pose, a, c, b, black ? -15329770 : -592142);
         }
      }
   }

   private static void buildIco() {
      float t = (float)((1.0 + Math.sqrt(5.0)) / 2.0);
      float[][] v = new float[][]{
         {-1.0F, t, 0.0F},
         {1.0F, t, 0.0F},
         {-1.0F, -t, 0.0F},
         {1.0F, -t, 0.0F},
         {0.0F, -1.0F, t},
         {0.0F, 1.0F, t},
         {0.0F, -1.0F, -t},
         {0.0F, 1.0F, -t},
         {t, 0.0F, -1.0F},
         {t, 0.0F, 1.0F},
         {-t, 0.0F, -1.0F},
         {-t, 0.0F, 1.0F}
      };
      int[][] f = new int[][]{
         {0, 11, 5},
         {0, 5, 1},
         {0, 1, 7},
         {0, 7, 10},
         {0, 10, 11},
         {1, 5, 9},
         {5, 11, 4},
         {11, 10, 2},
         {10, 7, 6},
         {7, 1, 8},
         {3, 9, 4},
         {3, 4, 2},
         {3, 2, 6},
         {3, 6, 8},
         {3, 8, 9},
         {4, 9, 5},
         {2, 4, 11},
         {6, 2, 10},
         {8, 6, 7},
         {9, 8, 1}
      };
      List<float[]> verts = new ArrayList<>();

      for (float[] p : v) {
         verts.add(norm(p));
      }

      PENT = verts.toArray(new float[0][]);
      List<int[]> faces = new ArrayList<>(Arrays.asList(f));

      for (int it = 0; it < 2; it++) {
         Map<Long, Integer> mid = new HashMap<>();
         List<int[]> next = new ArrayList<>();

         for (int[] tr : faces) {
            int ab = midpoint(verts, mid, tr[0], tr[1]);
            int bc = midpoint(verts, mid, tr[1], tr[2]);
            int ca = midpoint(verts, mid, tr[2], tr[0]);
            next.add(new int[]{tr[0], ab, ca});
            next.add(new int[]{tr[1], bc, ab});
            next.add(new int[]{tr[2], ca, bc});
            next.add(new int[]{ab, bc, ca});
         }

         faces = next;
      }

      ICO_V = verts.toArray(new float[0][]);
      ICO_F = faces.toArray(new int[0][]);
   }

   private static int midpoint(List<float[]> verts, Map<Long, Integer> cache, int a, int b) {
      long key = a < b ? (long)a << 32 | b : (long)b << 32 | a;
      Integer i = cache.get(key);
      if (i != null) {
         return i;
      }

      float[] p = verts.get(a);
      float[] q = verts.get(b);
      verts.add(norm(new float[]{p[0] + q[0], p[1] + q[1], p[2] + q[2]}));
      cache.put(key, verts.size() - 1);
      return verts.size() - 1;
   }

   private static float[] norm(float[] p) {
      float l = (float)Math.sqrt(p[0] * p[0] + p[1] * p[1] + p[2] * p[2]);
      return new float[]{p[0] / l, p[1] / l, p[2] / l};
   }

   private static void trophy(class_4665 base, EmoteProps.Sink s, float up) {
      int gold = -669638;
      int light = -7558;
      int deep = -3633124;
      int wood = -12966890;
      float o = up;
      octo(base, s, o - 0.02F, o + 0.07F, 0.085F, wood);
      octo(base, s, o + 0.07F, o + 0.085F, 0.075F, deep);
      octo(base, s, o + 0.085F, o + 0.12F, 0.045F, gold);
      octo(base, s, o + 0.12F, o + 0.2F, 0.022F, gold);
      octo(base, s, o + 0.16F, o + 0.185F, 0.04F, light);
      float[] prof = new float[]{0.035F, 0.06F, 0.08F, 0.095F, 0.105F, 0.112F, 0.116F};

      for (int i = 0; i < prof.length; i++) {
         float y0 = o + 0.2F + i * 0.03F;
         octo(base, s, y0, y0 + 0.031F, prof[i], i % 3 == 1 ? light : gold);
      }

      float top = o + 0.2F + prof.length * 0.03F;
      octo(base, s, top, top + 0.018F, 0.128F, light);

      for (float sd : new float[]{-1.0F, 1.0F}) {
         s.box(base, sd * 0.11F, top - 0.035F, -0.012F, sd * 0.165F, top - 0.012F, 0.012F, deep, false);
         s.box(base, sd * 0.15F - 0.012F, top - 0.13F, -0.012F, sd * 0.15F + 0.012F, top - 0.02F, 0.012F, deep, false);
         s.box(base, sd * 0.1F, top - 0.15F, -0.012F, sd * 0.155F, top - 0.125F, 0.012F, deep, false);
      }

      float sy = o + 0.31F;
      s.box(base, -0.022F, sy - 0.045F, -0.125F, 0.022F, sy + 0.045F, -0.118F, -2360, false);
      s.box(base, -0.05F, sy - 0.012F, -0.125F, 0.05F, sy + 0.022F, -0.118F, -2360, false);
   }

   private static void mug(class_4665 base, EmoteProps.Sink s, float t) {
      float z = -0.11F;
      s.box(base, -0.085F, -0.11F, z - 0.085F, 0.085F, 0.09F, z + 0.085F, -1448742, false);
      s.box(base, -0.087F, -0.04F, z - 0.087F, 0.087F, 0.01F, z + 0.087F, -4179669, false);
      s.box(base, -0.07F, -0.114F, z - 0.07F, 0.07F, -0.105F, z + 0.07F, -11916776, false);
      s.box(base, 0.085F, -0.07F, z - 0.015F, 0.12F, -0.06F, z + 0.015F, -1448742, false);
      s.box(base, 0.11F, -0.07F, z - 0.015F, 0.12F, 0.05F, z + 0.015F, -1448742, false);
      s.box(base, 0.085F, 0.04F, z - 0.015F, 0.12F, 0.05F, z + 0.015F, -1448742, false);

      for (int i = 0; i < 3; i++) {
         float k = (t * 0.6F + i / 3.0F) % 1.0F;
         float a = (1.0F - k) * 0.5F;
         float x = (float)Math.sin(t * 2.0F + i * 2) * 0.03F;
         float sz = 0.02F + 0.03F * k;
         s.box(base, x - sz, -0.13F - k * 0.3F - sz, z - sz, x + sz, -0.13F - k * 0.3F + sz, z + sz, (int)(a * 255.0F) << 24 | 16777215, true);
      }
   }

   private static void knife(class_4665 base, EmoteProps.Sink s) {
      s.box(base, -0.03F, -0.09F, -0.03F, 0.03F, 0.08F, 0.03F, -15000800, false);
      s.box(base, -0.032F, -0.04F, -0.01F, 0.032F, -0.03F, 0.01F, -4670270, false);
      s.box(base, -0.032F, 0.03F, -0.01F, 0.032F, 0.04F, 0.01F, -4670270, false);
      s.box(base, -0.02F, 0.08F, -0.05F, 0.02F, 0.095F, 0.04F, -7696747, false);
      s.box(base, -0.008F, 0.095F, -0.055F, 0.008F, 0.5F, 0.02F, -3551789, false);
      s.box(base, -0.008F, 0.5F, -0.045F, 0.008F, 0.58F, 0.012F, -3551789, false);
      s.box(base, -0.009F, 0.095F, -0.058F, 0.009F, 0.56F, -0.05F, -723208, false);
   }

   private static void whistle(class_4665 base, EmoteProps.Sink s) {
      s.box(base, -0.045F, -0.04F, -0.11F, 0.045F, 0.04F, -0.02F, -2499102, false);
      s.box(base, -0.05F, -0.03F, -0.1F, 0.05F, 0.03F, -0.03F, -2499102, false);
      s.box(base, -0.02F, -0.018F, -0.02F, 0.02F, 0.018F, 0.05F, -5327942, false);
      s.box(base, -0.015F, -0.042F, -0.08F, 0.015F, -0.035F, -0.05F, -14539478, false);
      s.box(base, -0.008F, 0.04F, -0.1F, 0.008F, 0.07F, -0.085F, -7696747, false);
   }

   private static void redCard(class_4665 base, EmoteProps.Sink s, float k) {
      float w = 0.12F * k;
      float h = 0.34F * k;
      s.box(base, -w, -0.02F, -0.012F, w, h, 0.012F, -3926498, false);
      s.box(base, -w + 0.012F, 0.0F, -0.0135F, w - 0.012F, h - 0.02F, 0.0135F, -1563604, false);
      s.box(base, -w + 0.02F, h - 0.08F, -0.014F, -w + 0.05F, h - 0.03F, -0.0135F, 1728053247, true);
   }

   private static void cozy(class_4665 base, EmoteProps.Sink s, float t) {
      int bark = -11652064;
      int barkL = -10074832;
      int wood = -4616872;
      int ring = -7576776;
      class_4587 ps = new class_4587();
      ps.method_23760().method_23761().set(base.method_23761());
      ps.method_22903();
      ps.method_46416(0.0F, -0.2F, 0.05F);
      ps.method_22907(class_7833.field_40718.rotationDegrees(90.0F));
      octo(ps.method_23760(), s, -0.55F, 0.55F, 0.19F, bark);

      for (int i = 0; i < 5; i++) {
         float y = -0.45F + i * 0.22F;
         octo(ps.method_23760(), s, y, y + 0.04F, 0.195F, barkL);
      }

      octo(ps.method_23760(), s, -0.56F, -0.55F, 0.165F, wood);
      octo(ps.method_23760(), s, 0.55F, 0.56F, 0.165F, wood);
      octo(ps.method_23760(), s, -0.562F, -0.56F, 0.1F, ring);
      octo(ps.method_23760(), s, 0.56F, 0.562F, 0.1F, ring);
      octo(ps.method_23760(), s, -0.564F, -0.562F, 0.04F, wood);
      octo(ps.method_23760(), s, 0.562F, 0.564F, 0.04F, wood);
      ps.method_22909();
      s.box(base, -0.3F, -0.4F, -0.05F, 0.05F, -0.38F, 0.18F, -11568594, false);
      s.box(base, 0.36F, -0.55F, 0.0F, 0.39F, -0.39F, 0.03F, -10863326, false);
      s.box(base, 0.31F, -0.66F, -0.05F, 0.45F, -0.53F, 0.08F, -10772422, false);
      float fz = -1.05F;

      for (int i = 0; i < 8; i++) {
         double a = i * Math.PI / 4.0;
         float x = (float)Math.cos(a) * 0.36F;
         float z = fz + (float)Math.sin(a) * 0.36F;
         int c = i % 2 == 0 ? -8749694 : -9999761;
         s.box(base, x - 0.07F, -0.09F, z - 0.07F, x + 0.07F, 0.0F, z + 0.07F, c, false);
      }

      ps.method_22903();
      ps.method_46416(0.0F, -0.07F, fz);

      for (int i = 0; i < 4; i++) {
         ps.method_22903();
         ps.method_22907(class_7833.field_40716.rotationDegrees(i * 45.0F + 20.0F));
         ps.method_22907(class_7833.field_40718.rotationDegrees(14.0F));
         octo(ps.method_23760(), s, -0.035F, 0.035F, 0.04F, bark);
         s.box(ps.method_23760(), -0.26F, -0.035F, -0.035F, 0.26F, 0.035F, 0.035F, i % 2 == 0 ? bark : barkL, false);
         ps.method_22909();
      }

      ps.method_22909();
      s.box(base, -0.14F, -0.06F, fz - 0.14F, 0.14F, -0.02F, fz + 0.14F, -38400, true);
      int[] cols = new int[]{-218107744, -285229008, -419460592, -570467832};

      for (int i = 0; i < 9; i++) {
         double ph = t * (5.0 + i * 0.7) + i * 2.1;
         float flick = 0.5F + 0.5F * (float)Math.sin(ph) * (float)Math.sin(ph * 0.37 + 1.0);
         float h = (0.18F + 0.22F * flick) * (i < 3 ? 1.35F : 1.0F);
         double a = i * 2.4;
         float rr = i < 3 ? 0.03F : 0.1F;
         float x = (float)Math.cos(a) * rr;
         float z = fz + (float)Math.sin(a) * rr;
         float wd = i < 3 ? 0.045F : 0.06F;
         s.box(base, x - wd, -0.08F - h, z - wd, x + wd, -0.06F, z + wd, cols[(i + (int)(t * 8.0F)) % 4], true);
      }

      for (int i = 0; i < 6; i++) {
         float k = (t * 0.7F + i / 6.0F) % 1.0F;
         float x = (float)Math.sin(i * 3.1 + t) * 0.12F;
         float z = fz + (float)Math.cos(i * 1.7 + t) * 0.1F;
         float y = -0.2F - k * 0.9F;
         int a = (int)(255.0F * (1.0F - k));
         s.box(base, x - 0.012F, y - 0.012F, z - 0.012F, x + 0.012F, y + 0.012F, z + 0.012F, a << 24 | 16756800, true);
      }

      s.box(base, -0.55F, -0.004F, fz - 0.55F, 0.55F, -0.002F, fz + 0.55F, 822057520, true);
      s.box(base, 0.56F, -0.55F, fz - 0.02F, 0.59F, 0.0F, fz + 0.01F, -9549270, false);
      s.box(base, 0.53F, -0.62F, fz - 0.02F, 0.56F, -0.53F, fz + 0.01F, -9549270, false);
      s.box(base, 0.59F, -0.62F, fz - 0.02F, 0.62F, -0.53F, fz + 0.01F, -9549270, false);
      ps.method_22903();
      ps.method_46416(0.0F, -0.56F, fz);
      ps.method_22907(class_7833.field_40714.rotationDegrees(t * 70.0F));
      s.box(ps.method_23760(), -0.12F, -0.012F, -0.012F, 0.62F, 0.012F, 0.012F, -7708616, false);
      float toast = 0.5F + 0.5F * (float)Math.sin(t * 0.8F);
      int mcol = lerp(-527640, -2581942, toast);
      s.box(ps.method_23760(), -0.12F, -0.05F, -0.05F, -0.02F, 0.05F, 0.05F, mcol, false);
      s.box(ps.method_23760(), -0.11F, -0.052F, -0.04F, -0.03F, -0.05F, 0.04F, lerp(-527640, -7712222, toast), false);
      ps.method_22909();
   }

   private static int lerp(int a, int b, float k) {
      int r = (int)((a >> 16 & 0xFF) + ((b >> 16 & 0xFF) - (a >> 16 & 0xFF)) * k);
      int g = (int)((a >> 8 & 0xFF) + ((b >> 8 & 0xFF) - (a >> 8 & 0xFF)) * k);
      int bl = (int)((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * k);
      return 0xFF000000 | r << 16 | g << 8 | bl;
   }

   private static void doner(class_4665 base, EmoteProps.Sink s) {
      octo(base, s, -0.04F, 0.0F, 0.28F, -11184034);
      octo(base, s, -0.45F, -0.04F, 0.03F, -4670270);
      octo(base, s, -2.0F, -1.77F, 0.025F, -4670270);
      octo(base, s, -2.0F, -1.96F, 0.11F, -11184034);

      for (int i = 0; i < 12; i++) {
         float y0 = -0.45F - i * 0.11F;
         float r = 0.17F + 0.012F * i;
         octo(base, s, y0 - 0.11F, y0, r, i % 2 == 0 ? -8762842 : -7448018);
         octo(base, s, y0 - 0.02F, y0, r + 0.004F, -10734568);
      }
   }

   private static void lamp(class_4665 pose, EmoteProps.Sink s) {
      int pole = -12894395;
      int dark = -14013135;
      s.box(pose, 0.88F, -0.08F, -0.22F, 1.32F, 0.0F, 0.22F, dark, false);
      s.box(pose, 1.02F, -2.6F, -0.08F, 1.18F, -0.08F, 0.08F, pole, false);
      s.box(pose, 1.0F, -2.74F, -0.09F, 1.2F, -2.6F, 0.09F, pole, false);
      s.box(pose, -0.42F, -2.72F, -0.07F, 1.0F, -2.6F, 0.07F, pole, false);
      s.box(pose, -0.82F, -2.78F, -0.2F, -0.36F, -2.6F, 0.2F, dark, false);
      s.box(pose, -0.78F, -2.6F, -0.16F, -0.4F, -2.585F, 0.16F, -5720, true);
      s.box(pose, -0.88F, -2.585F, -0.26F, -0.3F, -2.575F, 0.26F, 1090510986, true);
   }

   private static void camera(class_4665 pose, EmoteProps.Sink s, float flash, float time) {
      int body = -14934750;
      int top = -14013389;
      int grip = -15527145;
      int silver = -6446422;
      int barrel = -14276563;
      int ring = -12104877;
      s.box(pose, -0.24F, -0.13F, -0.1F, 0.24F, 0.13F, 0.1F, body, false);
      s.box(pose, -0.235F, -0.145F, -0.09F, 0.235F, -0.13F, 0.09F, top, false);
      s.box(pose, -0.31F, -0.12F, -0.14F, -0.22F, 0.13F, 0.07F, grip, false);
      s.box(pose, -0.11F, -0.1F, -0.14F, 0.11F, 0.12F, -0.1F, silver, false);
      s.box(pose, -0.09F, -0.08F, -0.27F, 0.09F, 0.1F, -0.14F, barrel, false);
      s.box(pose, -0.097F, -0.087F, -0.215F, 0.097F, 0.107F, -0.185F, ring, false);
      s.box(pose, -0.096F, -0.086F, -0.282F, 0.096F, 0.106F, -0.27F, silver, false);
      s.box(pose, -0.07F, -0.06F, -0.286F, 0.07F, 0.08F, -0.282F, -15059625, false);
      s.box(pose, 0.015F, -0.05F, -0.289F, 0.045F, -0.02F, -0.286F, -7355649, false);
      s.box(pose, -0.065F, -0.2F, -0.05F, 0.065F, -0.145F, 0.08F, body, false);
      s.box(pose, -0.05F, -0.19F, 0.08F, 0.05F, -0.155F, 0.115F, grip, false);
      s.box(pose, -0.2F, -0.165F, -0.035F, -0.14F, -0.145F, 0.025F, silver, false);
      s.box(pose, 0.12F, -0.16F, 0.0F, 0.2F, -0.145F, 0.07F, ring, false);
      s.box(pose, 0.1F, -0.26F, -0.06F, 0.22F, -0.16F, 0.04F, body, false);
      s.box(pose, 0.11F, -0.255F, -0.066F, 0.21F, -0.175F, -0.06F, -2565152, false);
      s.box(pose, 0.24F, -0.06F, -0.012F, 0.255F, 0.24F, 0.012F, -12965342, false);
      s.box(pose, -0.255F, -0.06F, -0.012F, -0.24F, 0.24F, 0.012F, -12965342, false);
      float blink = 0.5F + 0.5F * (float)Math.sin(time * 6.0);
      s.box(pose, 0.15F, -0.09F, -0.106F, 0.185F, -0.06F, -0.1F, (int)(120.0F + 135.0F * blink) << 24 | 16723285, true);
      if (flash > 0.02F) {
         float f = Math.min(1.0F, flash);
         s.box(pose, 0.11F, -0.255F, -0.072F, 0.21F, -0.175F, -0.066F, (int)(255.0F * f) << 24 | 16777215, true);

         for (int i = 1; i <= 3; i++) {
            float g = (0.06F + 0.16F * f) * i;
            int a = (int)(90.0F * f / i);
            s.box(pose, 0.16F - g, -0.215F - g * 0.8F, -0.08F - g * 0.5F, 0.16F + g, -0.215F + g * 0.8F, -0.07F, a << 24 | 16776170, true);
         }
      }
   }

   private static void wheelchair(class_4665 base, EmoteProps.Sink s, float spin) {
      int frame = -6643544;
      int dark = -14803164;
      int seat = -10805460;
      class_4587 ps = new class_4587();
      ps.method_23760().method_23761().set(base.method_23761());
      class_4665 pose = ps.method_23760();
      s.box(pose, -0.3F, -0.47F, -0.32F, 0.3F, -0.4F, 0.22F, seat, false);
      s.box(pose, -0.3F, -1.05F, 0.22F, 0.3F, -0.47F, 0.28F, seat, false);

      for (float side : new float[]{-1.0F, 1.0F}) {
         float x = 0.33F * side;
         s.box(pose, x - 0.02F, -0.42F, -0.62F, x + 0.02F, -0.38F, 0.3F, frame, false);
         s.box(pose, x - 0.025F, -0.76F, -0.28F, x + 0.025F, -0.72F, 0.2F, dark, false);
         s.box(pose, x - 0.02F, -0.72F, -0.02F, x + 0.02F, -0.42F, 0.02F, frame, false);
         s.box(pose, x - 0.02F, -1.08F, 0.28F, x + 0.02F, -0.4F, 0.32F, frame, false);
         s.box(pose, x - 0.025F, -1.1F, 0.3F, x + 0.025F, -1.06F, 0.44F, dark, false);
         s.box(pose, x - 0.015F, -0.4F, -0.6F, x + 0.015F, -0.12F, -0.56F, frame, false);
         s.box(pose, x - 0.03F, -0.12F, -0.64F, x + 0.03F, 0.0F, -0.52F, dark, false);
         s.box(pose, x * 0.7F - 0.015F, -0.4F, -0.66F, x * 0.7F + 0.015F, -0.15F, -0.62F, frame, false);
      }

      s.box(pose, -0.26F, -0.16F, -0.84F, 0.26F, -0.13F, -0.62F, dark, false);

      for (float side : new float[]{-1.0F, 1.0F}) {
         ps.method_22903();
         ps.method_46416(0.43F * side, -0.36F, 0.04F);
         ps.method_22907(class_7833.field_40714.rotation(spin));
         int seg = 16;
         float r = 0.35F;
         float half = (float)(Math.PI * r / seg) + 0.008F;

         for (int i = 0; i < seg; i++) {
            ps.method_22903();
            ps.method_22907(class_7833.field_40714.rotation((float)(i * 2 * Math.PI / seg)));
            s.box(ps.method_23760(), -0.035F, -r - 0.025F, -half, 0.035F, -r + 0.025F, half, dark, false);
            s.box(ps.method_23760(), -0.04F * side - 0.006F, -r + 0.03F, -half * 0.9F, -0.04F * side + 0.006F, -r + 0.05F, half * 0.9F, frame, false);
            ps.method_22909();
         }

         for (int i = 0; i < 3; i++) {
            ps.method_22903();
            ps.method_22907(class_7833.field_40714.rotation((float)(i * Math.PI / 3.0)));
            s.box(ps.method_23760(), -0.006F, -r + 0.03F, -0.006F, 0.006F, r - 0.03F, 0.006F, frame, false);
            ps.method_22909();
         }

         s.box(ps.method_23760(), -0.05F, -0.04F, -0.04F, 0.05F, 0.04F, 0.04F, frame, false);
         ps.method_22909();
      }
   }

   private static void sparks(class_4665 pose, EmoteProps.Sink s, float t, float k) {
      for (int i = 0; i < 14; i++) {
         double seed = i * 2.399963;
         double ang = seed + t * (0.6 + i % 3 * 0.25);
         float r = 0.55F + 0.25F * (i % 4) / 3.0F;
         float h = 0.2F + (i * 0.37F + t * 0.25F) % 1.0F * 1.9F;
         float x = (float)Math.cos(ang) * r;
         float z = (float)Math.sin(ang) * r;
         float tw = 0.5F + 0.5F * (float)Math.sin(t * 5.0 + i * 1.7);
         float sz = 0.025F + 0.02F * tw;
         int a = (int)(230.0F * k * (0.35F + 0.65F * tw));
         int col = i % 2 == 0 ? 16774084 : 14075903;
         s.box(pose, x - sz, -h - sz, z - sz, x + sz, -h + sz, z + sz, a << 24 | col, true);
      }
   }

   static void vcBox(class_4665 pose, class_4588 vc, float x0, float y0, float z0, float x1, float y1, float z1, int argb, int light) {
      quad(pose, vc, argb, light, 0.0F, -1.0F, 0.0F, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1);
      quad(pose, vc, argb, light, 0.0F, 1.0F, 0.0F, x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0);
      quad(pose, vc, argb, light, 0.0F, 0.0F, -1.0F, x0, y1, z0, x1, y1, z0, x1, y0, z0, x0, y0, z0);
      quad(pose, vc, argb, light, 0.0F, 0.0F, 1.0F, x1, y1, z1, x0, y1, z1, x0, y0, z1, x1, y0, z1);
      quad(pose, vc, argb, light, -1.0F, 0.0F, 0.0F, x0, y1, z1, x0, y1, z0, x0, y0, z0, x0, y0, z1);
      quad(pose, vc, argb, light, 1.0F, 0.0F, 0.0F, x1, y1, z0, x1, y1, z1, x1, y0, z1, x1, y0, z0);
   }

   private static void quad(
      class_4665 pose,
      class_4588 vc,
      int argb,
      int light,
      float nx,
      float ny,
      float nz,
      float ax,
      float ay,
      float az,
      float bx,
      float by,
      float bz,
      float cx,
      float cy,
      float cz,
      float dx,
      float dy,
      float dz
   ) {
      v(pose, vc, ax, ay, az, argb, light, nx, ny, nz, 0.0F, 0.0F);
      v(pose, vc, bx, by, bz, argb, light, nx, ny, nz, 1.0F, 0.0F);
      v(pose, vc, cx, cy, cz, argb, light, nx, ny, nz, 1.0F, 1.0F);
      v(pose, vc, dx, dy, dz, argb, light, nx, ny, nz, 0.0F, 1.0F);
   }

   private static void v(class_4665 pose, class_4588 vc, float x, float y, float z, int argb, int light, float nx, float ny, float nz, float u, float vv) {
      vc.method_56824(pose, x, y, z)
         .method_39415(argb)
         .method_22913(u, vv)
         .method_22922(class_4608.field_21444)
         .method_60803(light)
         .method_60831(pose, nx, ny, nz);
   }

   @Environment(EnvType.CLIENT)
   public interface Emitter {
      void emit(class_4587 var1, EmoteProps.Geometry var2);
   }

   @Environment(EnvType.CLIENT)
   public interface Geometry {
      void draw(class_4665 var1, EmoteProps.Sink var2);
   }

   @Environment(EnvType.CLIENT)
   public interface Sink {
      void box(class_4665 var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, boolean var9);

      default boolean triangles() {
         return false;
      }

      default void tri(class_4665 pose, float[] a, float[] b, float[] c, int argb) {
      }
   }

   @Environment(EnvType.CLIENT)
   private record VcSink(class_4588 vc, int light, boolean glowPass) implements EmoteProps.Sink {
      @Override
      public void box(class_4665 pose, float x0, float y0, float z0, float x1, float y1, float z1, int argb, boolean glow) {
         if (glow == this.glowPass) {
            EmoteProps.vcBox(pose, this.vc, x0, y0, z0, x1, y1, z1, argb, this.light);
         }
      }

      @Override
      public boolean triangles() {
         return true;
      }

      @Override
      public void tri(class_4665 pose, float[] a, float[] b, float[] c, int argb) {
         if (!this.glowPass) {
            float ux = b[0] - a[0];
            float uy = b[1] - a[1];
            float uz = b[2] - a[2];
            float wx = c[0] - a[0];
            float wy = c[1] - a[1];
            float wz = c[2] - a[2];
            float nx = uy * wz - uz * wy;
            float ny = uz * wx - ux * wz;
            float nz = ux * wy - uy * wx;
            float l = (float)Math.sqrt(nx * nx + ny * ny + nz * nz);
            if (!(l < 1.0E-9F)) {
               nx /= l;
               ny /= l;
               nz /= l;
               EmoteProps.v(pose, this.vc, a[0], a[1], a[2], argb, this.light, nx, ny, nz, 0.0F, 0.0F);
               EmoteProps.v(pose, this.vc, b[0], b[1], b[2], argb, this.light, nx, ny, nz, 1.0F, 0.0F);
               EmoteProps.v(pose, this.vc, c[0], c[1], c[2], argb, this.light, nx, ny, nz, 1.0F, 1.0F);
               EmoteProps.v(pose, this.vc, c[0], c[1], c[2], argb, this.light, nx, ny, nz, 0.0F, 1.0F);
            }
         }
      }
   }
}
