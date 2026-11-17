package com.paymentalerts.mixin;

import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SplashManager.class})
public abstract class SplashManagerMixin {
    @Inject(method = {"getSplash"}, at = {@At("HEAD")}, cancellable = true, require = 0)
    private void paymentalerts$splash(CallbackInfoReturnable<SplashRenderer> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(new SplashRenderer(Component.literal("subscribe to cyclonelwk and lumindev").setStyle(Style.EMPTY.withColor(16776960))));
    }
}
