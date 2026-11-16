package dev.valence.compat;

import net.minecraft.class_1309;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_490;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Draws the local player's back inside a GUI, for the cosmetics and cape previews.
 *
 * <p>This is the 1.21 - 1.21.5 implementation, which still uses the inventory screen's
 * entity drawing helper. That helper places the model by its feet, so the box the caller
 * asks for is converted into a centre point plus a scale here.
 */
public final class PlayerPreview {
   private PlayerPreview() {
   }

   /** Draws the local player turned away from the camera, filling the given box. */
   public static void drawBack(class_332 g, int x0, int y0, int x1, int y1) {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 == null) {
         return;
      }

      try {
         Vector2f a = Pose.project(g, new Vector2f(x0, y0));
         Vector2f b = Pose.project(g, new Vector2f(x1, y1));
         float w = b.x - a.x;
         float h = b.y - a.y;
         if (w <= 0.0F || h <= 0.0F) {
            return;
         }

         float size = Math.min(h / 2.4F, w / 1.1F) * 0.95F;
         if (size <= 0.0F) {
            return;
         }

         float cx = a.x + w / 2.0F;
         float cy = b.y - h * 0.04F;
         Quaternionf q = new Quaternionf().rotateZ((float)Math.PI);
         Quaternionf q2 = new Quaternionf().rotateX((float)Math.toRadians(-8.0));
         q.mul(q2);
         class_1309 player = mc.field_1724;
         Vector3f off = new Vector3f(0.0F, player.method_17682() / 2.0F + 0.0625F, 0.0F);
         class_490.method_48472(g, cx, cy, size, off, q, q2, player);
      } catch (Throwable var14) {
      }
   }
}
