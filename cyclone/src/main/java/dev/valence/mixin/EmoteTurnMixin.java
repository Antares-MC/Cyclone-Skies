package dev.valence.mixin;

import dev.valence.emote.EmoteManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(class_1297.class)
public abstract class EmoteTurnMixin {
   @Inject(method = "method_5872", at = @At("HEAD"), cancellable = true)
   private void valence$emoteTurn(double dx, double dy, CallbackInfo ci) {
      if (EmoteManager.turn((class_1297)(Object)this, dx, dy)) {
         ci.cancel();
      }
   }
}
