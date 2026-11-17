package dev.valence.compat;

import net.minecraft.class_1792;
import net.minecraft.class_1799;

/**
 * Item stack predicates.
 *
 * <p>1.21.9 replaced {@code ItemStack.isOf(Item)} with a holder-based {@code is(...)},
 * so a test against a plain {@code Items} constant needs a different call on each
 * side of that change. Both answers are one line, so they live here.
 *
 * <p>This is the 1.21.9 - 1.21.11 implementation.
 */
public final class Stacks {
   private Stacks() {
   }

   /** Whether {@code stack} is an instance of {@code item}. */
   public static boolean is(class_1799 stack, class_1792 item) {
      return stack != null && stack.method_31574(item);
   }
}
