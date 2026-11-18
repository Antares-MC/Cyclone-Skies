package dev.valence.mixin;

import dev.valence.compat.Buffers;
import dev.valence.compat.Layers;
import dev.valence.config.ValenceConfig;
import dev.valence.license.License;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.valence.compat.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import com.mojang.math.Axis;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.client.renderer.ScreenEffectRenderer.class)
public abstract class LowFireMixin {
   @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
   private static void valence$lowFire(com.mojang.blaze3d.vertex.PoseStack pose, dev.valence.compat.MultiBufferSource buffers, net.minecraft.client.renderer.texture.TextureAtlasSprite sprite, CallbackInfo ci) {
      float h = Math.max(0.1F, Math.min(1.0F, ValenceConfig.get().fireHeight));
      if (!(h >= 0.999F) && License.active()) {
         ci.cancel();
         com.mojang.blaze3d.vertex.VertexConsumer vc = Buffers.get(buffers, Layers.fire(sprite.atlasLocation()));
         float u0 = sprite.getU0();
         float u1 = sprite.getU1();
         float v0 = sprite.getV0();
         float v1 = sprite.getV1();
         float drop = (1.0F - h) * 0.55F;
         float alpha = 0.9F;

         for (int i = 0; i < 2; i++) {
            pose.pushPose();
            pose.translate(-(i * 2 - 1) * 0.24F, -0.3F - drop, 0.0F);
            pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees((i * 2 - 1) * 10.0F));
            Matrix4f m = pose.last().pose();
            vc.addVertex(m, -0.5F, -0.5F, -0.5F).setUv(u1, v1).setColor(1.0F, 1.0F, 1.0F, alpha);
            vc.addVertex(m, 0.5F, -0.5F, -0.5F).setUv(u0, v1).setColor(1.0F, 1.0F, 1.0F, alpha);
            vc.addVertex(m, 0.5F, 0.5F, -0.5F).setUv(u0, v0).setColor(1.0F, 1.0F, 1.0F, alpha);
            vc.addVertex(m, -0.5F, 0.5F, -0.5F).setUv(u1, v0).setColor(1.0F, 1.0F, 1.0F, alpha);
            pose.popPose();
         }
      }
   }
}
