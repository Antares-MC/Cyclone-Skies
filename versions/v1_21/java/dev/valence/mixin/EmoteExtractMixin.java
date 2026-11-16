package dev.valence.mixin;

import dev.valence.emote.EmoteManager;
import dev.valence.emote.EmoteState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_742;
import net.minecraft.class_1007;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Feeds the active emote into the player before it is posed.
 *
 * <p>This is the 1.21 and 1.21.1 version. From 1.21.2 on the emote is copied while the
 * render state is extracted; these versions pose the model straight from the entity, so the
 * emote is copied at the top of the player render instead.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_1007.class)
public abstract class EmoteExtractMixin {
   @Inject(method = "method_4215", at = @At("HEAD"))
   private void valence$emote(class_742 entity, float yaw, float tickDelta, class_4587 ps,
                              class_4597 buffers, int light, CallbackInfo ci) {
      EmoteManager.fill(entity, (EmoteState)(Object)entity);
   }
}
