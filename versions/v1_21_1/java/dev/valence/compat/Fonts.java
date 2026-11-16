package dev.valence.compat;

import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;

/**
 * Selectable fonts.
 *
 * <p>1.21.11 draws every glyph with the single vanilla text renderer and picks a font
 * by attaching a {@code StyleSpriteSource} to the text style. From 1.21.9 on the font
 * manager can also build a separate {@code TextRenderer} per font id. Before 1.21.9
 * neither is true: a {@code TextRenderer} is built from a storage lookup that the font
 * manager keeps private, and there is no per-style override, so a font cannot be
 * selected at all.
 *
 * <p>This is the 1.21.2 - 1.21.8 implementation. The entry remembers which font was
 * asked for, but every lookup resolves to the vanilla renderer and every style is
 * returned unchanged, so the font picker is inert on these versions rather than
 * drawing glyphs from the wrong atlas.
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
}
