package dev.valence.compat;

import java.util.function.Supplier;
import net.minecraft.class_1011;
import net.minecraft.class_1043;

/**
 * Dynamic texture creation facade for 1.21.5, where {@code NativeImageBackedTexture} gained a
 * name supplier. Before 1.21.5 the overlay of this class drops the label and uses the plain
 * constructors.
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
