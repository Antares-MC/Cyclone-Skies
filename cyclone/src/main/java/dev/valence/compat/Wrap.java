package dev.valence.compat;

import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5348;

/**
 * Word-wrapped text facade.
 *
 * <p>{@code DrawContext#drawWrappedText} gained a shadow flag in 1.21.4; before that the
 * wrapped text is always drawn without one. Routing the call through here keeps the shared
 * pages free of the version split.
 */
public final class Wrap {
   private Wrap() {
   }

   public static void draw(class_332 g, class_327 font, class_5348 text, int x, int y, int width, int color, boolean shadow) {
      g.method_51440(font, text, x, y, width, color, shadow);
   }
}
