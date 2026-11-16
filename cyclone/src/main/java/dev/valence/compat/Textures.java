package dev.valence.compat;

import java.util.function.Supplier;
import net.minecraft.class_1011;
import net.minecraft.class_1043;

/**
 * Dynamic texture creation facade.
 *
 * <p>The name supplier was added to {@code NativeImageBackedTexture} in 1.21.5; before that
 * the texture name is not configurable and the plain constructors are used. Keeping the name
 * in the shared source makes the debug texture dump readable on the versions that can do it.
 */
public final class Textures {
   private Textures() {
   }

   public static class_1043 named(String label, class_1011 image) {
      Supplier<String> name = () -> label;
      return new class_1043(name, image);
   }

   public static class_1043 blank(String label, int width, int height, boolean stb) {
      Supplier<String> name = () -> label;
      return new class_1043(name, width, height, stb);
   }
}
