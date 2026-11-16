package dev.valence.compat;

import net.minecraft.class_1661;

/**
 * Player inventory facade.
 *
 * <p>The selected hotbar slot gained a getter in 1.21.5; before that it is only reachable as
 * a public field. Both are wrapped here so the HUD code stays version-agnostic.
 */
public final class Slots {
   private Slots() {
   }

   public static int selected(class_1661 inventory) {
      return inventory.method_67532();
   }
}
