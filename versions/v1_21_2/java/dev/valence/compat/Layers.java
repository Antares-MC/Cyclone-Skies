package dev.valence.compat;

import net.minecraft.class_1921;
import net.minecraft.class_2960;

/**
 * Version-neutral render-layer factories, Minecraft 1.21.1 - 1.21.10.
 *
 * <p>On these versions the entity and text layer getters are statics on
 * {@code RenderLayer} rather than on the separate {@code RenderLayers} holder
 * that 1.21.11 introduced. The ones that take a boolean pass
 * {@code affectsOutline}; the 1.21.11 one-argument helpers delegate with
 * {@code true}, so the same value is used here to keep glyph and cosmetic
 * outlines identical to the 1.21.11 build.
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
      return class_1921.method_65217(texture);
   }
}
