package dev.valence.compat;

import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_327.class_6415;
import net.minecraft.class_4597;
import org.joml.Matrix4f;

/**
 * Selectable fonts for 1.21 and 1.21.1.
 *
 * <p>These versions cannot select a font at all: a {@code TextRenderer} is built from a
 * storage lookup the font manager keeps private, and there is no per-style override, so the
 * font picker is inert rather than drawing glyphs from the wrong atlas.
 *
 * <p>The glyph submitter also still takes a plain string here, and carries a trailing
 * see-through flag that later versions folded into the layer argument.
 */
public final class Fonts {
   /** One selectable font, by resource id. */
   public record Entry(class_2960 source) {
   }

   private Fonts() {
   }

   /** The font declared by {@code assets/<namespace>/font/<path>.json}. */
   public static Entry of(class_2960 id) {
      return new Entry(id);
   }

   /** Applies this font to a text style. */
   public static class_2583 apply(class_2583 style, Entry e) {
      return style;
   }

   /** Applies this font to a text component. */
   public static class_2561 apply(class_2561 text, Entry e) {
      return text;
   }

   /** The renderer that measures and draws this font. */
   public static class_327 renderer(Entry e) {
      return class_310.method_1551().field_1772;
   }

   /** Draws one text component with the given renderer. */
   public static void draw(class_327 font, class_2561 text, float x, float y, int color, boolean shadow,
                           Matrix4f matrix, class_4597 buffers, class_6415 layer, int light) {
      font.method_27522(text.getString(), x, y, color, shadow, matrix, buffers, layer, 0, light, false);
   }
}
