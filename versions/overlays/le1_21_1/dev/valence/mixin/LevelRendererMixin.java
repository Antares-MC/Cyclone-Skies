package dev.valence.mixin;

import dev.valence.config.ValenceConfig;
import dev.valence.sky.ValenceSky;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_761;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hides the vanilla clouds while a Cyclone sky is active.
 *
 * <p>This is the 1.21 and 1.21.1 version, where the cloud pass is a single method on the
 * world renderer instead of a pipeline stage.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_761.class)
public abstract class LevelRendererMixin {
   @Inject(method = "method_3259", at = @At("HEAD"), cancellable = true)
   private void valence$hideClouds(class_4587 pose, Matrix4f positionMatrix, Matrix4f projectionMatrix,
                                   float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
      if (ValenceConfig.get().skyHideClouds && ValenceSky.visible()) {
         ci.cancel();
      }
   }
}
