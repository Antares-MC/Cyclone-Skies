package dev.valence.compat;

import net.minecraft.class_11719;
import net.minecraft.class_11719.class_11721;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;

/**
 * Selectable fonts.
 *
 * <p>1.21.11 draws every glyph with the single vanilla text renderer and picks a
 * font by attaching a {@code StyleSpriteSource} to the text style. Before 1.21.9
 * a font was instead a separate {@code TextRenderer} obtained from the font
 * manager, with no per-style override. Both cases are hidden behind this entry
 * object: callers ask for the renderer to measure and draw with, and for the
 * text/style to hand it.
 */
public final class Fonts {
   /** One selectable font. */
   public record Entry(class_11719 source) {
   }

   private Fonts() {
   }

   /** The font declared by {@code assets/<namespace>/font/<path>.json}. */
   public static Entry of(class_2960 id) {
      return new Entry(new class_11721(id));
   }

   /** Applies this font to a text style. */
   public static class_2583 apply(class_2583 style, Entry e) {
      return e == null ? style : style.method_27704(e.source());
   }

   /** Applies this font to a text component. */
   public static class_2561 apply(class_2561 text, Entry e) {
      return e == null ? text : class_2561.method_43470("").method_27696(apply(class_2583.field_24360, e)).method_10852(text);
   }

   /** The renderer that measures and draws this font. */
   public static class_327 renderer(Entry e) {
      return class_310.method_1551().field_1772;
   }
}
