package dev.valence.mixin;

import dev.valence.emote.EmoteAnimator;
import dev.valence.emote.EmoteState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1309;
import net.minecraft.class_572;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Poses the player model for the active emote.
 *
 * <p>This is the 1.21 and 1.21.1 version, where the model is posed from the entity itself
 * rather than from a render state.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_572.class)
public abstract class EmoteModelMixin {
   @Inject(method = "method_17087(Lnet/minecraft/class_1309;FFFFF)V", at = @At("TAIL"))
   private void valence$emote(class_1309 entity, float limbAngle, float limbDistance, float animationProgress,
                              float headYaw, float headPitch, CallbackInfo ci) {
      if (entity instanceof EmoteState st && st.valence$emote() >= 0) {
         EmoteAnimator.applyParts((class_572<?>)(Object)this, st);
      }
   }
}
