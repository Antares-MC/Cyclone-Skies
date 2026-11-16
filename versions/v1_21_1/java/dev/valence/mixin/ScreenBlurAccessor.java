package dev.valence.mixin;

import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Exposes the protected {@code Screen#applyBlur} for {@code dev.valence.compat.ScreenBlur}. */
@Mixin(class_437.class)
public interface ScreenBlurAccessor {
   @Invoker("method_57734")
   void valence$applyBlur();
}
