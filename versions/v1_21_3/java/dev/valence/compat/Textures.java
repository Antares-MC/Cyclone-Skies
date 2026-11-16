package dev.valence.compat;

import net.minecraft.class_1011;
import net.minecraft.class_1043;

/**
 * Dynamic texture creation facade for 1.21 - 1.21.4, where {@code NativeImageBackedTexture} has
 * no name supplier. The label is accepted and dropped so no call site has to change.
 */
public final class Textures {
   private Textures() {
   }

   public static class_1043 named(String label, class_1011 image) {
      return new class_1043(image);
   }

   public static class_1043 blank(String label, int width, int height, boolean stb) {
      return new class_1043(width, height, stb);
   }
}
