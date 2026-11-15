package dev.valence.mixin;

import dev.valence.emote.EmoteManager;
import dev.valence.emote.EmoteState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_1007;
import net.minecraft.class_11890;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(class_1007.class)
public abstract class EmoteExtractMixin {
   @Inject(method = "method_62604(Lnet/minecraft/class_11890;Lnet/minecraft/class_10055;F)V", at = @At("TAIL"))
   private void valence$emote(class_11890 entity, class_10055 state, float partialTick, CallbackInfo ci) {
      EmoteManager.fill(entity, (EmoteState)state);
      if (((EmoteState)state).valence$emote() >= 0) {
         state.field_55305.method_65605();
         state.field_55307.method_65605();
      }
   }
}
