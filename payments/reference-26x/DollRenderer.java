package com.paymentalerts.skin;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public final class DollRenderer extends PictureInPictureRenderer<DollRenderState> {
    private static final int FULL_BRIGHT = 15728880;
    private final Class<? extends DollRenderState> slot;

    public DollRenderer(Class<? extends DollRenderState> cls) {
        this.slot = cls;
    }

    public Class<DollRenderState> getRenderStateClass() {
        return this.slot;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void renderToTexture(DollRenderState dollRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        Minecraft.getInstance().gameRenderer.lighting().setupFor(Lighting.Entry.PLAYER_SKIN);
        RenderSystem.getModelViewStack().rotateAround(Axis.XP.rotationDegrees(dollRenderState.rotationX), 0.0f, dollRenderState.scale() * r0.gameRenderer.gameRenderState().windowRenderState.guiScale * (-dollRenderState.pivotY), 0.0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(-dollRenderState.rotationY));
        poseStack.translate(0.0f, (-1.601f) + (dollRenderState.pose.crouching() ? 0.125f : 0.0f), 0.0f);
        submitNodeCollector.submitModel(dollRenderState.model, dollRenderState.pose, poseStack, dollRenderState.texture, FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0, (ModelFeatureRenderer.CrumblingOverlay) null);
    }

    protected String getTextureLabel() {
        return "payment alerts doll";
    }
}
