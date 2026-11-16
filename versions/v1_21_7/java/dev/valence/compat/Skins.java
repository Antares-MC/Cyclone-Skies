package dev.valence.compat;

import net.minecraft.class_2960;
import net.minecraft.class_742;
import net.minecraft.class_8685;

/**
 * Player skin lookups.
 *
 * <p>1.21.9 wrapped the skin's texture in a small value object, so the identifier moved
 * one accessor deeper. Everything that needs to draw the player with their own skin
 * goes through here.
 *
 * <p>This is the 1.21.2 - 1.21.8 implementation, where the texture is the record's
 * first component.
 */
public final class Skins {
   private Skins() {
   }

   /** The skin texture currently worn by this player. */
   public static class_2960 texture(class_742 player) {
      return player.method_52814().comp_1626();
   }

   /** Whether this player uses the slim (Alex) arm width. */
   public static boolean slim(class_742 player) {
      return player.method_52814().comp_1629() == class_8685.class_7920.field_41122;
   }
}
