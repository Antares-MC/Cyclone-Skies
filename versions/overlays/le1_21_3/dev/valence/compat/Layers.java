package dev.valence.compat;

import net.minecraft.class_1921;
import net.minecraft.class_2960;

/**
 * Render-layer factories for 1.21 - 1.21.3.
 *
 * <p>{@code RenderLayer.getFireScreenEffect} only appears in 1.21.4; before that the vanilla
 * fire overlay builds its quads on the plain entity translucent layer, so the low-fire mixin
 * gets that layer instead.
 */
public final class Layers {
   private Layers() {
   }

   /** Entity cutout, no culling. */
   public static class_1921 cutout(class_2960 texture) {
      return class_1921.method_24293(texture, true);
   }

   /** Entity translucent. */
   public static class_1921 translucent(class_2960 texture) {
      return class_1921.method_24294(texture, true);
   }

   /** Entity translucent, full-bright. */
   public static class_1921 translucentEmissive(class_2960 texture) {
      return class_1921.method_42600(texture);
   }

   /** Textured quads with alpha testing. */
   public static class_1921 text(class_2960 texture) {
      return class_1921.method_23028(texture);
   }

   /** Textured quads, no depth write. */
   public static class_1921 textSeeThrough(class_2960 texture) {
      return class_1921.method_23030(texture);
   }

   /** Untextured debug quads. */
   public static class_1921 quads() {
      return class_1921.method_49042();
   }

   /** Beacon beam, translucent or solid. */
   public static class_1921 beacon(class_2960 texture, boolean beam) {
      return class_1921.method_23592(texture, beam);
   }

   /** Fire overlay, drawn when the player is burning. */
   public static class_1921 fire(class_2960 texture) {
      return class_1921.method_24294(texture, true);
   }
}
