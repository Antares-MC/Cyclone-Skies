package dev.valence.compat;

import java.net.URI;
import net.minecraft.class_2558;
import net.minecraft.class_2583;

/**
 * Chat link facade.
 *
 * <p>1.21.5 turned {@code ClickEvent} into a sealed record hierarchy, so the open-url event is
 * built through its own subtype. Before that it is the plain {@code (Action, String)} record.
 * Both are wrapped here so the notifier just asks for a clickable style.
 */
public final class Links {
   private Links() {
   }

   /** A style that opens {@code url} when the text is clicked. */
   public static class_2583 openUrl(String url) {
      return class_2583.field_24360.method_10958(new class_2558.class_10608(URI.create(url)));
   }
}
