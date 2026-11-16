package dev.valence.compat;

import dev.valence.compat.Pose;
import net.minecraft.class_10017;
import net.minecraft.class_10042;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_746;
import net.minecraft.class_897;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Draws the local player's back inside a GUI, for the cosmetics and cape previews.
 *
 * <p>The GUI entity render entry point is the part of the screen API that changed
 * shape most between 1.21.8 and 1.21.11, so the whole call is kept here rather than
 * duplicated in the page that wants it.
 *
 * <p>This is the 1.21.9 - 1.21.11 implementation, which builds a render state by
 * hand and hands it to the GUI entity renderer.
 */
public final class PlayerPreview {
   private PlayerPreview() {
   }

   /** Draws the local player turned away from the camera, filling the given box. */
   public static void drawBack(class_332 g, int x0, int y0, int x1, int y1) {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         try {
            class_897<? super class_746, ?> renderer = mc.method_1561().method_3953(mc.field_1724);
            class_10017 state = renderer.method_62425(mc.field_1724, 1.0F);
            state.field_61820 = 15728880;
            state.field_61823.clear();
            state.field_61821 = 0;
            float turn = (float)Math.sin(System.currentTimeMillis() / 1400.0) * 28.0F;
            if (state instanceof class_10042 l) {
               l.field_53446 = turn;
               l.field_53447 = 0.0F;
               l.field_53448 = 6.0F;
               l.field_53329 = l.field_53329 / l.field_53453;
               l.field_53330 = l.field_53330 / l.field_53453;
               l.field_53453 = 1.0F;
            }

            Vector2f a = Pose.project(g, new Vector2f(x0, y0));
            Vector2f b = Pose.project(g, new Vector2f(x1, y1));
            x0 = Math.round(a.x);
            y0 = Math.round(a.y);
            x1 = Math.round(b.x);
            y1 = Math.round(b.y);
            int size = (int)Math.min((y1 - y0) / 2.5F, (x1 - x0) / 1.25F);
            Quaternionf q = new Quaternionf().rotateZ((float)Math.PI);
            Quaternionf q2 = new Quaternionf().rotateX((float)Math.toRadians(-8.0));
            q.mul(q2);
            Vector3f off = new Vector3f(0.0F, state.field_53330 / 2.0F + 0.0625F, 0.0F);
            g.method_70856(state, size, off, q, q2, x0, y0, x1, y1);
         } catch (Throwable var16) {
         }
      }
   }
}
