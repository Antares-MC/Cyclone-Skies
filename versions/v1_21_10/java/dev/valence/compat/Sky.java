package dev.valence.compat;

import net.minecraft.class_2874;
import net.minecraft.class_2960;
import net.minecraft.class_638;

/**
 * Dimension sky classification, Minecraft 1.21.1 - 1.21.10.
 *
 * <p>Before 1.21.11 a dimension named its sky behaviour with an {@code effects}
 * identifier that selected a {@code DimensionSpecialEffects} implementation,
 * rather than carrying a {@code Skybox} enum. The three vanilla values line up
 * one-to-one: {@code the_nether} is the skybox-less one, {@code the_end} is the
 * starfield one, and anything else (normally {@code overworld}) draws the
 * normal celestial sky.
 */
public final class Sky {
   private static final class_2960 NETHER = class_2960.method_60655("minecraft", "the_nether");
   private static final class_2960 END = class_2960.method_60655("minecraft", "the_end");

   private Sky() {
   }

   /** True when the dimension draws no celestial sky at all (the Nether). */
   public static boolean noSky(class_638 level) {
      return level.method_8597().comp_655().equals(NETHER);
   }

   /** True for the End's starfield sky. */
   public static boolean endSky(class_638 level) {
      return level.method_8597().comp_655().equals(END);
   }
}
