package com.paymentalerts.mixin;

import com.paymentalerts.alert.AlertManager;
import com.paymentalerts.fx.ScreenShake;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Gui.class})
public abstract class HudMixin {

    @Unique
    private boolean paymentalerts$pushed;

    @Inject(method = {"render"}, at = {@At("HEAD")}, require = 0)
    private void paymentalerts$beginShake(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
        this.paymentalerts$pushed = false;
        if (ScreenShake.isActive()) {
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(ScreenShake.hudOffsetX(), ScreenShake.hudOffsetY());
            this.paymentalerts$pushed = true;
        }
    }

    @Inject(method = {"render"}, at = {@At("RETURN")})
    private void paymentalerts$endShake(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
        if (this.paymentalerts$pushed) {
            guiGraphics.pose().popMatrix();
            this.paymentalerts$pushed = false;
        }
        AlertManager.render(guiGraphics, deltaTracker);
    }
}
