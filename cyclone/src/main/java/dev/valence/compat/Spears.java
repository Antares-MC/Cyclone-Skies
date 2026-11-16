package dev.valence.compat;

import net.minecraft.class_1792;
import net.minecraft.class_1802;

/**
 * The spear item, introduced in 1.21.11.
 *
 * <p>Older versions have no spear at all, so the styles page asks here for the
 * category icon and simply omits the category when there is none.
 */
public final class Spears {
   private Spears() {
   }

   /** The netherite spear, or {@code null} when this version has no spears. */
   public static class_1792 icon() {
      return class_1802.field_63390;
   }
}
