package dev.valence.compat;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_293;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_757;
import org.joml.Matrix4f;

/**
 * GUI texture drawing facade for 1.21 and 1.21.1.
 *
 * <p>These versions have no tinted blit: {@code DrawContext#drawTexture} takes neither a render
 * layer nor a colour, and the GUI texture entry points resolve their texture through the sprite
 * atlas, which our dynamically registered images are not part of. The mod draws a lot of tinted
 * images, so the four shapes are reimplemented here on top of an immediate textured quad, with
 * the tint carried by the shader colour. Each draw in these versions is submitted on its own, so
 * the colour cannot leak into a batched neighbour.
 */
public final class Draw {
   private Draw() {
   }

   public static void texture(class_332 g, class_2960 texture, int x, int y, float u, float v,
                              int width, int height, int tw, int th, int color) {
      blit(g, texture, x, y, x + width, y + height,
           u / tw, (u + width) / tw, v / th, (v + height) / th, color);
   }

   public static void region(class_332 g, class_2960 texture, int x, int y, float u, float v,
                             int width, int height, int regionWidth, int regionHeight,
                             int tw, int th, int color) {
      blit(g, texture, x, y, x + width, y + height,
           u / tw, (u + regionWidth) / tw, v / th, (v + regionHeight) / th, color);
   }

   public static void guiTexture(class_332 g, class_2960 texture, int x, int y, int width, int height, int color) {
      blit(g, texture, x, y, x + width, y + height, 0.0F, 1.0F, 0.0F, 1.0F, color);
   }

   public static void scaled(class_332 g, class_2960 texture, int x, int y, float u, float v,
                             int width, int height, int regionWidth, int regionHeight, int tw, int th) {
      region(g, texture, x, y, u, v, width, height, regionWidth, regionHeight, tw, th, -1);
   }

   private static void blit(class_332 g, class_2960 texture, int x0, int y0, int x1, int y1,
                            float u0, float u1, float v0, float v1, int color) {
      int alpha = color >>> 24;
      if (x0 == x1 || y0 == y1 || alpha == 0) {
         return;
      }

      RenderSystem.setShaderTexture(0, texture);
      RenderSystem.setShader(class_757::method_34542);
      RenderSystem.setShaderColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F,
                                  (color & 255) / 255.0F, alpha / 255.0F);

      Matrix4f matrix = g.method_51448().method_23760().method_23761();
      class_287 buffer = class_289.method_1348()
         .method_60827(class_293.class_5596.field_27382, class_290.field_1585);
      buffer.method_22918(matrix, (float)x0, (float)y1, 0.0F).method_22913(u0, v1);
      buffer.method_22918(matrix, (float)x1, (float)y1, 0.0F).method_22913(u1, v1);
      buffer.method_22918(matrix, (float)x1, (float)y0, 0.0F).method_22913(u1, v0);
      buffer.method_22918(matrix, (float)x0, (float)y0, 0.0F).method_22913(u0, v0);
      class_286.method_43433(buffer.method_60800());

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
