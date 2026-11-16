package dev.valence.compat;

import net.minecraft.class_2558;
import net.minecraft.class_2583;

/**
 * Chat link facade for 1.21 - 1.21.4, where {@code ClickEvent} is a single record taking an
 * action and a value instead of a sealed hierarchy with one subtype per action.
 */
public final class Links {
   private Links() {
   }

   /** A style that opens {@code url} when the text is clicked. */
   public static class_2583 openUrl(String url) {
      return class_2583.field_24360.method_10958(new class_2558(class_2558.class_2559.field_11749, url));
   }
}
