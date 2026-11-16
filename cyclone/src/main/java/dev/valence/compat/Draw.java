package dev.valence.compat;

import net.minecraft.class_2960;
import net.minecraft.class_332;

/**
 * GUI texture drawing facade.
 *
 * <p>The four {@code DrawContext} blit methods used by the mod changed shape twice: 1.21.6
 * replaced the {@code RenderLayer} resolver with a {@code RenderPipeline} and, before 1.21.2,
 * the methods took neither a layer nor a tint colour. Routing every textured draw through this
 * class keeps those differences in one file per version band.
 */
public final class Draw {
   private Draw() {
   }

   /**
    * Draws a {@code width x height} texel region of {@code texture} at {@code x, y}.
    *
    * @param u  left texel of the region
    * @param v  top texel of the region
    * @param tw full texture width, used to normalise the texel offsets
    * @param th full texture height
    */
   public static void texture(class_332 g, class_2960 texture, int x, int y, float u, float v,
                              int width, int height, int tw, int th, int color) {
      g.method_25291(Pipes.guiTextured(), texture, x, y, u, v, width, height, tw, th, color);
   }

   /**
    * Draws a {@code regionWidth x regionHeight} texel region scaled to {@code width x height}.
    */
   public static void region(class_332 g, class_2960 texture, int x, int y, float u, float v,
                             int width, int height, int regionWidth, int regionHeight,
                             int tw, int th, int color) {
      g.method_25293(Pipes.guiTextured(), texture, x, y, u, v, width, height,
                     regionWidth, regionHeight, tw, th, color);
   }

   /** Draws a whole texture scaled to {@code width x height}. */
   public static void guiTexture(class_332 g, class_2960 texture, int x, int y, int width, int height, int color) {
      g.method_52707(Pipes.guiTextured(), texture, x, y, width, height, color);
   }

   /** Draws a texel region scaled to an integer size, without tinting. */
   public static void scaled(class_332 g, class_2960 texture, int x, int y, float u, float v,
                             int width, int height, int regionWidth, int regionHeight, int tw, int th) {
      g.method_25302(Pipes.guiTextured(), texture, x, y, u, v, width, height,
                     regionWidth, regionHeight, tw, th);
   }
}
