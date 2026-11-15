package dev.valence.mixin;

import dev.valence.emote.EmoteManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(class_4184.class)
public abstract class EmoteCameraMixin {
   @Shadow
   protected abstract void method_19325(float var1, float var2);

   @Shadow
   protected abstract void method_19327(double var1, double var3, double var5);

   @Shadow
   protected abstract void method_19324(float var1, float var2, float var3);

   @Shadow
   private float method_19318(float distance) {
      throw new AssertionError();
   }

   @Inject(method = "method_19321", at = @At("TAIL"))
   private void valence$emoteCamera(class_1937 level, class_1297 entity, boolean detached, boolean mirrored, float pt, CallbackInfo ci) {
      if (detached && EmoteManager.cameraActive(entity)) {
         this.method_19325(EmoteManager.camYaw(), EmoteManager.camPitch());
         this.method_19327(
            class_3532.method_16436(pt, entity.field_6014, entity.method_23317()),
            class_3532.method_16436(pt, entity.field_6036, entity.method_23318()) + entity.method_17682() * 0.62,
            class_3532.method_16436(pt, entity.field_5969, entity.method_23321())
         );
         this.method_19324(-this.method_19318(EmoteManager.camDistance(3.6F)), 0.0F, 0.0F);
      }
   }
}
