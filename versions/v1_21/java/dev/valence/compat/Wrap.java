package dev.valence.compat;

import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5348;

/**
 * Word-wrapped text facade for 1.21 - 1.21.3, where {@code DrawContext#drawWrappedText} takes
 * no shadow flag and always draws flat. The shadow argument is accepted and ignored so the
 * call sites stay identical.
 */
public final class Wrap {
   private Wrap() {
   }

   public static void draw(class_332 g, class_327 font, class_5348 text, int x, int y, int width, int color, boolean shadow) {
      g.method_51440(font, text, x, y, width, color);
   }
}
