package dev.valence.compat;

import dev.valence.mixin.ScreenBlurAccessor;
import net.minecraft.class_332;
import net.minecraft.class_437;

/**
 * Screen background blur facade for 1.21.2 - 1.21.5, where {@code Screen#applyBlur} takes no
 * arguments; the draw context is only needed from 1.21.6 on.
 */
public final class ScreenBlur {
   private ScreenBlur() {
   }

   public static void apply(class_437 screen, class_332 g) {
      ((ScreenBlurAccessor)screen).valence$applyBlur();
   }
}
