package dev.valence.compat;

import dev.valence.mixin.ScreenBlurAccessor;
import net.minecraft.class_332;
import net.minecraft.class_437;

/**
 * Screen background blur facade.
 *
 * <p>{@code Screen#applyBlur} is protected, so the screens cannot call it through a plain
 * helper. The accessor mixin exposes it, and this facade hides the signature differences:
 * 1.21 takes a partial-tick float, 1.21.2 - 1.21.5 take nothing, 1.21.6+ take the draw
 * context.
 */
public final class ScreenBlur {
   private ScreenBlur() {
   }

   public static void apply(class_437 screen, class_332 g) {
      ((ScreenBlurAccessor)screen).valence$applyBlur(g);
   }
}
