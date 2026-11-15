package com.paymentalerts.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.paymentalerts.fx.ScreenShake;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public abstract class GameRendererMixin {
    // require = 0: if this signature does not match 1.21.11, only the camera shake is lost (no crash).
    @Inject(method = {"bobHurt"}, at = {@At("HEAD")}, require = 0)
    private void paymentalerts$shakeCamera(PoseStack poseStack, float partialTick, CallbackInfo callbackInfo) {
        ScreenShake.applyToCamera(poseStack);
    }
}
