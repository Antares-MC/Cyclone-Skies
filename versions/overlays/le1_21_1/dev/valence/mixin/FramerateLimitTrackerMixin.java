package dev.valence.mixin;

import dev.valence.config.ValenceConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Caps the framerate while the window is in the background.
 *
 * <p>This is the 1.21 and 1.21.1 version. These versions have no framerate limit tracker:
 * the client reads its own effective limit on every frame, so the cap is applied there.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_310.class)
public abstract class FramerateLimitTrackerMixin {
   @Inject(method = "method_16009", at = @At("RETURN"), cancellable = true)
   private void valence$backgroundLimit(CallbackInfoReturnable<Integer> cir) {
      class_310 self = (class_310)(Object)this;
      if (ValenceConfig.get().perfBackgroundFps && !self.method_1569()) {
         cir.setReturnValue(Math.min((Integer)cir.getReturnValue(), 30));
      }
   }
}
