package dev.valence.compat;

import net.minecraft.class_332;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

/**
 * GUI matrix facade.
 *
 * <p>1.21.6 replaced the GUI {@code PoseStack} with JOML's {@code Matrix3x2fStack};
 * the shared sources are written against the newer API, so every push/pop/translate/
 * scale/rotate goes through here. Up to 1.21.5 the overlay of this class applies the
 * same operations to a {@code PoseStack}.
 */
public final class Pose {
   private Pose() {
   }

   public static void push(class_332 g) {
      g.method_51448().pushMatrix();
   }

   public static void pop(class_332 g) {
      g.method_51448().popMatrix();
   }

   public static void translate(class_332 g, float x, float y) {
      g.method_51448().translate(x, y);
   }

   public static void scale(class_332 g, float x, float y) {
      g.method_51448().scale(x, y);
   }

   public static void rotate(class_332 g, float radians) {
      g.method_51448().rotate(radians);
   }

   /** Premultiplies an arbitrary 2D affine transform. */
   public static void mul(class_332 g, Matrix3x2f m) {
      g.method_51448().mul(m);
   }

   /** Maps a screen-space point through the current transform. */
   public static Vector2f project(class_332 g, Vector2f p) {
      return g.method_51448().transformPosition(p);
   }
}
