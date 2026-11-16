package dev.valence.compat;

import net.minecraft.class_2874;
import net.minecraft.class_638;

/**
 * Dimension sky classification.
 *
 * <p>Whether a dimension draws the normal celestial sky used to be expressed
 * through {@code DimensionSpecialEffects} on older versions. 1.21.11 replaced
 * that with a {@code Skybox} enum on the dimension type. Both the preview
 * screen, the lyrics placement and the fog tint need the same two questions
 * answered, so they ask here instead of naming version-specific types.
 *
 * <p>This is the 1.21.11 implementation.
 */
public final class Sky {
   private Sky() {
   }

   /** True when the dimension draws no celestial sky at all (the Nether). */
   public static boolean noSky(class_638 level) {
      return level.method_8597().comp_5220() == class_2874.class_12326.field_64385;
   }

   /** True for the End's starfield sky. */
   public static boolean endSky(class_638 level) {
      return level.method_8597().comp_5220() == class_2874.class_12326.field_64387;
   }
}
