package dev.valence.compat;

import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2680;

/**
 * Block-state queries whose signatures moved between versions.
 *
 * <p>{@code isOpaqueFullCube} takes the world and position up to 1.21.1, so that a block can
 * answer differently per position, and lost both arguments in 1.21.2 when the per-position
 * data moved into the state itself. The occlusion probe wants the positional answer, so the
 * overlay for the old range passes the position through.
 */
public final class States {
   private States() {
   }

   /** Whether {@code state} at {@code pos} completely fills its cube and hides what is behind it. */
   public static boolean opaqueFullCube(class_2680 state, class_1922 world, class_2338 pos) {
      return state.method_26216();
   }
}
