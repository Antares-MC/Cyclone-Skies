package dev.valence.mixin;

import dev.valence.compat.Hud;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla HUD suppression for 1.21 - 1.21.5.
 *
 * <p>These versions have no way to replace a single vanilla HUD element, so the styled
 * hotbar and crosshair cancel their vanilla counterparts here and are painted from the tail
 * of {@code InGameHud.render}.
 */
@Mixin(class_329.class)
public abstract class HudSuppressMixin {
   @Inject(method = "method_1759", at = @At("HEAD"), cancellable = true)
   private void valence$hideHotbar(class_332 g, class_9779 delta, CallbackInfo ci) {
      if (Hud.hideHotbar()) {
         ci.cancel();
      }
   }

   @Inject(method = "method_1736", at = @At("HEAD"), cancellable = true)
   private void valence$hideCrosshair(class_332 g, class_9779 delta, CallbackInfo ci) {
      if (Hud.hideCrosshair()) {
         ci.cancel();
      }
   }

   @Inject(method = "method_1753", at = @At("TAIL"))
   private void valence$afterHud(class_332 g, class_9779 delta, CallbackInfo ci) {
      Hud.renderAfter(g);
   }
}
