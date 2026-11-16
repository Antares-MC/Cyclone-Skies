package dev.valence.compat;

import net.minecraft.class_1921;

/**
 * GUI pipeline facade for 1.21 and 1.21.1.
 *
 * <p>The texture drawing methods on {@code DrawContext} took no layer at all before 1.21.2:
 * the tinted blits did not exist and the plain ones resolved their own layer. The {@code Draw}
 * overlay for these versions therefore draws its own immediate quads and never asks for a
 * pipeline. This stub keeps the type available for anything that still wants the GUI layer.
 */
public final class Pipes {
   private Pipes() {
   }

   public static class_1921 guiTextured() {
      return class_1921.method_51784();
   }
}
