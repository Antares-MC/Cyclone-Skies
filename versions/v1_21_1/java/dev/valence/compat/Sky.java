package dev.valence.compat;

import net.minecraft.class_2960;
import net.minecraft.class_638;

/**
 * Dimension sky classification.
 *
 * <p>Whether a dimension draws the normal celestial sky used to be expressed through
 * {@code DimensionSpecialEffects} on older versions. 1.21.11 replaced that with a
 * {@code Skybox} enum on the dimension type. Both the preview screen, the lyrics
 * placement and the fog tint need the same two questions answered, so they ask here
 * instead of naming version-specific types.
 *
 * <p>This is the 1.21.2 - 1.21.8 implementation, which classifies by the dimension
 * type's effects id: the Nether has the hell effects and the End the end effects.
 */
public final class Sky {
   private static final class_2960 THE_NETHER = class_2960.method_60655("minecraft", "the_nether");
   private static final class_2960 THE_END = class_2960.method_60655("minecraft", "the_end");

   private Sky() {
   }

   /** True when the dimension draws no celestial sky at all (the Nether). */
   public static boolean noSky(class_638 level) {
      return level != null && THE_NETHER.equals(level.method_8597().comp_655());
   }

   /** True for the End's starfield sky. */
   public static boolean endSky(class_638 level) {
      return level != null && THE_END.equals(level.method_8597().comp_655());
   }
}
