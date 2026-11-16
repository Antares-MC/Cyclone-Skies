package dev.valence.mixin;

import dev.valence.emote.EmoteManager;
import dev.valence.emote.EmoteState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_1007;
import net.minecraft.class_742;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Feeds the active emote into the player render state.
 *
 * <p>This is the 1.21.2 - 1.21.8 version. Up to 1.21.8 there is no handed-item feature
 * render state to suppress, so the arms are hidden by the limb drawing itself.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_1007.class)
public abstract class EmoteExtractMixin {
   @Inject(method = "method_62604(Lnet/minecraft/class_742;Lnet/minecraft/class_10055;F)V", at = @At("TAIL"))
   private void valence$emote(class_742 entity, class_10055 state, float partialTick, CallbackInfo ci) {
      EmoteManager.fill(entity, (EmoteState)state);
   }
}
