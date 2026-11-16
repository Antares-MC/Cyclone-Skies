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
 * <p>The GUI entity render entry point is the part of the screen API that changed shape
 * most between 1.21.8 and 1.21.11, so the whole call is kept here rather than
 * duplicated in the page that wants it.
 *
 * <p>This is the 1.21.2 - 1.21.8 implementation. Up to 1.21.8 the inventory screen
 * still exposes the full drawing call with an explicit rotation pair and entity, so the
 * render state never has to be built by hand.
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
         Vector2f a = g.method_51448().transformPosition(new Vector2f(x0, y0));
         Vector2f b = g.method_51448().transformPosition(new Vector2f(x1, y1));
         int ax = Math.round(a.x);
         int ay = Math.round(a.y);
         int bx = Math.round(b.x);
         int by = Math.round(b.y);
         int size = (int)Math.round(Math.min((by - ay) / 2.5F, (bx - ax) / 1.25F));
         if (size <= 0) {
            return;
         }

         Quaternionf q = new Quaternionf().rotateZ((float)Math.PI);
         Quaternionf q2 = new Quaternionf().rotateX((float)Math.toRadians(-8.0));
         q.mul(q2);
         class_1309 player = mc.field_1724;
         Vector3f off = new Vector3f(0.0F, player.method_17682() / 2.0F + 0.0625F, 0.0F);
         class_490.method_48472(g, ax, ay, bx, by, (float)size, off, q, q2, player);
      } catch (Throwable var12) {
      }
   }
}
