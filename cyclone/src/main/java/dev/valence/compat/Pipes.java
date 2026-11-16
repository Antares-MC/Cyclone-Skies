package dev.valence.compat;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.class_10799;

/**
 * GUI pipeline facade.
 *
 * <p>The texture drawing methods on {@code DrawContext} take a plain {@code RenderPipeline}
 * from 1.21.6 on, but 1.21.2 - 1.21.5 take a {@code Function<Identifier, RenderPipeline>}
 * that resolves the pipeline for the texture being drawn. The overlay of this class returns
 * that function so the call sites stay identical.
 */
public final class Pipes {
   private Pipes() {
   }

   public static RenderPipeline guiTextured() {
      return class_10799.field_56883;
   }
}
