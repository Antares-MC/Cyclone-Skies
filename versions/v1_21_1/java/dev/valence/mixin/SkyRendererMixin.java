package dev.valence.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.valence.lyrics.SkyLyrics;
import dev.valence.sky.ValenceSky;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_761;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks the sky pass so the Cyclone sky can replace it.
 *
 * <p>This is the 1.21 and 1.21.1 version. These versions draw the whole overworld sky,
 * including the sun, moon and stars, in one method on the world renderer, so a custom sky
 * cancels that method outright and draws in its place. The rain and star brightness the
 * sky engine wants are not passed in, so they are read from the client world.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_761.class)
public abstract class SkyRendererMixin {
   @Inject(method = "method_3257", at = @At("HEAD"), cancellable = true)
   private void valence$replaceSky(Matrix4f positionMatrix, Matrix4f projectionMatrix, float tickDelta,
                                   class_4184 camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci) {
      if (ValenceSky.replacesOverworld()) {
         RenderSystem.depthMask(false);
         ValenceSky.render(rainBrightness(tickDelta), starBrightness(tickDelta));
         SkyLyrics.renderInSky();
         RenderSystem.depthMask(true);
         fogCallback.run();
         ci.cancel();
      }
   }

   @Inject(method = "method_3257", at = @At("TAIL"))
   private void valence$drawSky(Matrix4f positionMatrix, Matrix4f projectionMatrix, float tickDelta,
                                class_4184 camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci) {
      if (!ValenceSky.replacesOverworld()) {
         ValenceSky.render(rainBrightness(tickDelta), starBrightness(tickDelta));
         SkyLyrics.renderInSky();
      }
   }

   private static float rainBrightness(float tickDelta) {
      class_638 world = class_310.method_1551().field_1687;
      return world == null ? 1.0F : 1.0F - world.method_8430(tickDelta);
   }

   private static float starBrightness(float tickDelta) {
      class_638 world = class_310.method_1551().field_1687;
      return world == null ? 0.0F : world.method_23787(tickDelta);
   }
}
