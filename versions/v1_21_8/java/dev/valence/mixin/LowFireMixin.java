package dev.valence.mixin;

import dev.valence.config.ValenceConfig;
import dev.valence.license.License;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4603;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Shortens the first-person fire overlay.
 *
 * <p>This is the 1.21.2 - 1.21.8 version. Up to 1.21.8 the overlay fetches its own fire
 * sprite internally instead of taking one as a parameter, so rather than rebuilding the
 * quads this squashes the pose stack around the vanilla draw and lets vanilla place the
 * texture.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_4603.class)
public abstract class LowFireMixin {
   @Unique
   private boolean valence$squashed;

   @Inject(method = "method_23070", at = @At("HEAD"))
   private void valence$lowFireStart(class_4587 pose, class_4597 buffers, CallbackInfo ci) {
      this.valence$squashed = false;
      float h = Math.max(0.1F, Math.min(1.0F, ValenceConfig.get().fireHeight));
      if (h < 0.999F && License.active()) {
         pose.method_22903();
         pose.method_22905(1.0F, h, 1.0F);
         this.valence$squashed = true;
      }
   }

   @Inject(method = "method_23070", at = @At("RETURN"))
   private void valence$lowFireEnd(class_4587 pose, class_4597 buffers, CallbackInfo ci) {
      if (this.valence$squashed) {
         pose.method_22909();
         this.valence$squashed = false;
      }
   }
}
