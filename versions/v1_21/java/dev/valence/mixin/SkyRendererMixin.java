package dev.valence.mixin;

import dev.valence.lyrics.SkyLyrics;
import dev.valence.sky.ValenceSky;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597.class_4598;
import net.minecraft.class_9975;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks the sky pass so the Cyclone sky can draw over it and delete the vanilla discs.
 *
 * <p>This is the 1.21.2 - 1.21.8 version of the sky hooks: up to 1.21.8 the sky
 * renderer still takes a vertex consumer provider and the moon phase as a plain
 * integer, and every helper is small enough to cancel whole.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_9975.class)
public abstract class SkyRendererMixin {
   @Inject(method = "method_62307", at = @At("TAIL"))
   private void valence$drawSky(class_4587 poseStack, class_4598 consumers, float sunAngle, int moonPhase, float rainBrightness, float starBrightness, CallbackInfo ci) {
      ValenceSky.render(rainBrightness, starBrightness);
      SkyLyrics.renderInSky();
   }

   @Inject(method = "method_62302", at = @At("HEAD"), cancellable = true, require = 0)
   private void valence$skipSkyDisc(CallbackInfo ci) {
      if (ValenceSky.replacesOverworld()) {
         ci.cancel();
      }
   }

   @Inject(method = "method_62306", at = @At("HEAD"), cancellable = true, require = 0)
   private void valence$skipSunrise(CallbackInfo ci) {
      if (ValenceSky.replacesOverworld()) {
         ci.cancel();
      }
   }

   @Inject(method = "method_62305", at = @At("HEAD"), cancellable = true, require = 0)
   private void valence$skipDarkDisc(CallbackInfo ci) {
      if (ValenceSky.replacesOverworld()) {
         ci.cancel();
      }
   }

   @Inject(method = "method_62303", at = @At("HEAD"), cancellable = true, require = 0)
   private void valence$skipSun(CallbackInfo ci) {
      if (ValenceSky.replacesOverworld()) {
         ci.cancel();
      }
   }

   @Inject(method = "method_62304", at = @At("HEAD"), cancellable = true, require = 0)
   private void valence$skipMoon(CallbackInfo ci) {
      if (ValenceSky.replacesOverworld()) {
         ci.cancel();
      }
   }

   @Inject(method = "method_62310", at = @At("HEAD"), cancellable = true, require = 0)
   private void valence$skipStars(CallbackInfo ci) {
      if (ValenceSky.replacesOverworld()) {
         ci.cancel();
      }
   }

   @Inject(method = "method_62312", at = @At("HEAD"), cancellable = true, require = 0)
   private void valence$endSkyCover(CallbackInfo ci) {
      if (ValenceSky.replacesEnd() && ValenceSky.replacesOverworld()) {
         ValenceSky.renderEnd();
         SkyLyrics.renderInSky();
         ci.cancel();
      }
   }

   @Inject(method = "method_62312", at = @At("TAIL"), require = 0)
   private void valence$endSkyFade(CallbackInfo ci) {
      if (ValenceSky.replacesEnd()) {
         ValenceSky.renderEnd();
      }

      SkyLyrics.renderInSky();
   }
}
