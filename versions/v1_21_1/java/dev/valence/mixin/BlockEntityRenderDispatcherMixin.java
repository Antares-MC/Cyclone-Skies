package dev.valence.mixin;

import dev.valence.perf.Occlusion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_824;
import net.minecraft.class_827;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Skips block entities the occlusion cache has decided cannot be seen.
 *
 * <p>This is the 1.21.2 - 1.21.8 version, which hooks the dispatch method itself: from
 * 1.21.9 on the visibility decision returns the renderer to use, while here the dispatch
 * simply draws and returns nothing, so the whole call is cancelled instead.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_824.class)
public abstract class BlockEntityRenderDispatcherMixin {
   @Inject(method = "method_3555", at = @At("HEAD"), cancellable = true, require = 0)
   private void valence$occlusion(class_2586 blockEntity, float partialTick, class_4587 pose, class_4597 buffers, CallbackInfo ci) {
      class_310 mc = class_310.method_1551();
      if (mc.field_1773 != null) {
         class_243 cam = mc.field_1773.method_19418().method_19326();
         class_827<?> renderer = ((class_824)(Object)this).method_3550(blockEntity);
         if (renderer != null && !renderer.method_3563()) {
            if (Occlusion.hideBlock(blockEntity.method_11016(), cam.field_1352, cam.field_1351, cam.field_1350)) {
               ci.cancel();
            }
         }
      }
   }
}
