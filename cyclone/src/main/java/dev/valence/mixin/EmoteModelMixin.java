package dev.valence.mixin;

import dev.valence.emote.EmoteAnimator;
import dev.valence.emote.EmoteState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10034;
import net.minecraft.class_572;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(class_572.class)
public abstract class EmoteModelMixin {
   @Inject(method = "method_17087(Lnet/minecraft/class_10034;)V", at = @At("TAIL"))
   private void valence$emote(class_10034 state, CallbackInfo ci) {
      if (state instanceof EmoteState st && st.valence$emote() >= 0) {
         EmoteAnimator.applyParts((class_572<?>)(Object)this, st);
      }
   }
}
