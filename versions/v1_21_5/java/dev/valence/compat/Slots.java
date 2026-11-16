package dev.valence.compat;

import net.minecraft.class_1661;

/**
 * Player inventory facade for 1.21.5, where the selected hotbar slot became a getter. Before
 * 1.21.5 the overlay of this class reads the public field instead.
 */
public final class Slots {
   private Slots() {
   }

   public static int selected(class_1661 inventory) {
      return inventory.method_67532();
   }
}
