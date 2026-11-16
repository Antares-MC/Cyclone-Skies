package dev.valence.compat;

import java.net.URI;
import net.minecraft.class_2558;
import net.minecraft.class_2583;

/**
 * Chat link facade for 1.21.5, where {@code ClickEvent} became a sealed hierarchy with one
 * record per action. Before 1.21.5 the overlay of this class builds the plain
 * {@code (Action, String)} record instead.
 */
public final class Links {
   private Links() {
   }

   /** A style that opens {@code url} when the text is clicked. */
   public static class_2583 openUrl(String url) {
      return class_2583.field_24360.method_10958(new class_2558.class_10608(URI.create(url)));
   }
}
