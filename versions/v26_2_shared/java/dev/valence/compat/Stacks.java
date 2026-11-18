package dev.valence.compat;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Item stack predicates.
 *
 * <p>26.x dropped the plain-{@code Item} overload of {@code ItemStack.is} in favour
 * of holder predicates, so identity against an {@code Items} constant is a direct
 * item comparison again.
 *
 * <p>This is the 26.x implementation.
 */
public final class Stacks {
   private Stacks() {
   }

   /** Whether {@code stack} is an instance of {@code item}. */
   public static boolean is(ItemStack stack, Item item) {
      return stack != null && stack.getItem() == item;
   }
}
