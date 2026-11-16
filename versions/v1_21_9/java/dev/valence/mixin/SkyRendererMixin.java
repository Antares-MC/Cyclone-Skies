package dev.valence.mixin;

import dev.valence.lyrics.SkyLyrics;
import dev.valence.sky.ValenceSky;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_638;
import net.minecraft.class_9975;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Sky replacement, Minecraft 1.21.1 - 1.21.10.
 *
 * <p>On these versions {@code renderCelestialBodies} takes a moon phase as a
 * plain int, has no separate star-brightness argument, and {@code renderTopSky}
 * takes the three sky angles instead of a packed colour. The sky itself only
 * needs the rain gradient and the star brightness, so those are read from the
 * client world here rather than taken from the call.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_9975.class)
public abstract class SkyRendererMixin {
   @Inject(method = "method_62307", at = @At("TAIL"))
   private void valence$drawSky(class_4587 poseStack, float sunAngle, int moonPhase, float starAngle, float starBrightness, CallbackInfo ci) {
      class_310 mc = class_310.method_1551();
      class_638 level = mc.field_1687;
      if (level != null) {
         float partialTick = mc.method_61966().method_60637(false);
         ValenceSky.render(level.method_8430(partialTick), level.method_23787(partialTick));
      }

      SkyLyrics.renderInSky();
   }

   @Inject(method = "method_62302", at = @At("HEAD"), cancellable = true, require = 0)
   private void valence$skipSkyDisc(float sunAngle, float moonAngle, float starAngle, CallbackInfo ci) {
      if (ValenceSky.replacesOverworld()) {
         ci.cancel();
      }
   }

   @Inject(method = "method_62306", at = @At("HEAD"), cancellable = true, require = 0)
   private void valence$skipSunrise(class_4587 poseStack, float sunAngle, int color, CallbackInfo ci) {
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
   private void valence$skipSun(float alpha, class_4587 poseStack, CallbackInfo ci) {
      if (ValenceSky.replacesOverworld()) {
         ci.cancel();
      }
   }

   @Inject(method = "method_62304", at = @At("HEAD"), cancellable = true, require = 0)
   private void valence$skipMoon(int phase, float alpha, class_4587 poseStack, CallbackInfo ci) {
      if (ValenceSky.replacesOverworld()) {
         ci.cancel();
      }
   }

   @Inject(method = "method_62310", at = @At("HEAD"), cancellable = true, require = 0)
   private void valence$skipStars(float brightness, class_4587 poseStack, CallbackInfo ci) {
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
