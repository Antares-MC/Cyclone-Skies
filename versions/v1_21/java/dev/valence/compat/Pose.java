package dev.valence.compat;

import net.minecraft.class_332;
import net.minecraft.class_4587;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * GUI matrix facade for Minecraft 1.21 - 1.21.5, where {@code DrawContext#getMatrices()}
 * still returns a {@code PoseStack}. From 1.21.6 on the shared implementation of this
 * class targets {@code Matrix3x2fStack} instead.
 */
public final class Pose {
   private Pose() {
   }

   private static class_4587 stack(class_332 g) {
      return g.method_51448();
   }

   public static void push(class_332 g) {
      stack(g).method_22903();
   }

   public static void pop(class_332 g) {
      stack(g).method_22909();
   }

   public static void translate(class_332 g, float x, float y) {
      stack(g).method_22904(x, y, 0.0);
   }

   public static void scale(class_332 g, float x, float y) {
      stack(g).method_22905(x, y, 1.0F);
   }

   public static void rotate(class_332 g, float radians) {
      stack(g).method_23760().method_23761().rotateZ(radians);
   }

   public static void mul(class_332 g, Matrix3x2f m) {
      Matrix4f dest = new Matrix4f();
      dest.m00(m.m00()).m10(m.m10()).m30(m.m20()).m01(m.m01()).m11(m.m11()).m31(m.m21());
      stack(g).method_34425(dest);
   }

   public static Vector2f project(class_332 g, Vector2f p) {
      Vector4f v = new Vector4f(p.x, p.y, 0.0F, 1.0F);
      stack(g).method_23760().method_23761().transform(v);
      return new Vector2f(v.x, v.y);
   }
}
