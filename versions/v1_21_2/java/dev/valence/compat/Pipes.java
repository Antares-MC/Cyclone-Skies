package dev.valence.compat;

import java.util.function.Function;
import net.minecraft.class_1921;
import net.minecraft.class_2960;

/**
 * GUI pipeline facade for 1.21.2 - 1.21.5, where the texture drawing methods take a
 * {@code Function<Identifier, RenderLayer>} that resolves the render layer per texture.
 * {@code RenderLayer.GUI_TEXTURED} is that function.
 */
public final class Pipes {
   private Pipes() {
   }

   public static Function<class_2960, class_1921> guiTextured() {
      return class_1921::method_62277;
   }
}
