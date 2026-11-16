package dev.valence.compat;

import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2680;

/**
 * Block-state queries for 1.21 and 1.21.1, where {@code isOpaqueFullCube} still takes the
 * world and position so that a block can answer differently per location.
 */
public final class States {
   private States() {
   }

   /** Whether {@code state} at {@code pos} completely fills its cube and hides what is behind it. */
   public static boolean opaqueFullCube(class_2680 state, class_1922 world, class_2338 pos) {
      return state.method_26216(world, pos);
   }
}
