package dev.valence.compat;

import net.minecraft.class_1661;

/**
 * Player inventory facade for 1.21 - 1.21.4, where the selected hotbar slot is a plain field
 * rather than a getter. From 1.21.5 on the shared implementation calls the getter.
 */
public final class Slots {
   private Slots() {
   }

   public static int selected(class_1661 inventory) {
      return inventory.field_7545;
   }
}
