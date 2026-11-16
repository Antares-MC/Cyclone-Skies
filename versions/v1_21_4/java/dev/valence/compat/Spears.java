package dev.valence.compat;

import net.minecraft.class_1792;

/**
 * The spear item, introduced in 1.21.11.
 *
 * <p>Older versions have no spear at all, so the styles page asks here for the
 * category icon and simply omits the category when there is none.
 *
 * <p>This is the 1.21.2 - 1.21.8 implementation: there is no spear, so the icon is
 * always {@code null}.
 */
public final class Spears {
   private Spears() {
   }

   /** The netherite spear, or {@code null} when this version has no spears. */
   public static class_1792 icon() {
      return null;
   }
}
