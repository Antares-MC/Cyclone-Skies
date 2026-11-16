package dev.valence.compat;

import net.minecraft.class_1921;
import net.minecraft.class_2960;

/**
 * Version-neutral render-layer factories.
 *
 * <p>CycloneSkies draws its 3D cosmetics with a handful of vanilla render
 * layers. Where those factories live moved between Minecraft versions: in
 * 1.21.11 they are statics on {@code RenderLayers}, while 1.21.1 - 1.21.10
 * expose them on {@code RenderLayer} under different intermediary ids, and
 * 26.x renamed the class again. Routing every call through this class keeps
 * those differences in one small per-version file instead of scattered across
 * forty call sites.
 *
 * <p>This is the 1.21.11 implementation.
 */
public final class Layers {
   private Layers() {
   }

   /** Entity cutout, no culling. */
   public static class_1921 cutout(class_2960 texture) {
      return net.minecraft.class_12249.method_75994(texture);
   }

   /** Entity translucent. */
   public static class_1921 translucent(class_2960 texture) {
      return net.minecraft.class_12249.method_76000(texture);
   }

   /** Entity translucent, full-bright. */
   public static class_1921 translucentEmissive(class_2960 texture) {
      return net.minecraft.class_12249.method_76002(texture);
   }

   /** Textured quads with alpha testing. */
   public static class_1921 text(class_2960 texture) {
      return net.minecraft.class_12249.method_76022(texture);
   }

   /** Textured quads, no depth write. */
   public static class_1921 textSeeThrough(class_2960 texture) {
      return net.minecraft.class_12249.method_76028(texture);
   }

   /** Untextured debug quads. */
   public static class_1921 quads() {
      return net.minecraft.class_12249.method_76023();
   }

   /** Beacon beam, translucent or solid. */
   public static class_1921 beacon(class_2960 texture, boolean beam) {
      return net.minecraft.class_12249.method_75988(texture, beam);
   }

   /** Fire overlay, drawn when the player is burning. */
   public static class_1921 fire(class_2960 texture) {
      return net.minecraft.class_12249.method_75943(texture);
   }
}
