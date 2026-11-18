package dev.valence.compat;

import dev.valence.compat.Pose;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Draws the local player's back inside a GUI, for the cosmetics and cape previews.
 *
 * <p>The GUI entity render entry point is the part of the screen API that changed
 * shape most between 1.21.8 and 1.21.11, so the whole call is kept here rather than
 * duplicated in the page that wants it.
 *
 * <p>This is the 1.21.9 - 1.21.11 implementation, which builds a render state by
 * hand and hands it to the GUI entity renderer.
 */
public final class PlayerPreview {
   private PlayerPreview() {
   }

   /** Draws the local player turned away from the camera, filling the given box. */
   public static void drawBack(net.minecraft.client.gui.GuiGraphicsExtractor g, int x0, int y0, int x1, int y1) {
      net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
      if (mc.player != null) {
         try {
            net.minecraft.client.renderer.entity.EntityRenderer<? super net.minecraft.client.player.LocalPlayer, ?> renderer = mc.getEntityRenderDispatcher().getRenderer(mc.player);
            net.minecraft.client.renderer.entity.state.EntityRenderState state = renderer.createRenderState(mc.player, 1.0F);
            state.lightCoords = 15728880;
            state.shadowPieces.clear();
            state.outlineColor = 0;
            float turn = (float)Math.sin(System.currentTimeMillis() / 1400.0) * 28.0F;
            if (state instanceof net.minecraft.client.renderer.entity.state.LivingEntityRenderState l) {
               l.bodyRot = turn;
               l.yRot = 0.0F;
               l.xRot = 6.0F;
               l.boundingBoxWidth = l.boundingBoxWidth / l.scale;
               l.boundingBoxHeight = l.boundingBoxHeight / l.scale;
               l.scale = 1.0F;
            }

            Vector2f a = Pose.project(g, new Vector2f(x0, y0));
            Vector2f b = Pose.project(g, new Vector2f(x1, y1));
            x0 = Math.round(a.x);
            y0 = Math.round(a.y);
            x1 = Math.round(b.x);
            y1 = Math.round(b.y);
            int size = (int)Math.min((y1 - y0) / 2.5F, (x1 - x0) / 1.25F);
            Quaternionf q = new Quaternionf().rotateZ((float)Math.PI);
            Quaternionf q2 = new Quaternionf().rotateX((float)Math.toRadians(-8.0));
            q.mul(q2);
            Vector3f off = new Vector3f(0.0F, state.boundingBoxHeight / 2.0F + 0.0625F, 0.0F);
            g.entity(state, size, off, q, q2, x0, y0, x1, y1);
         } catch (Throwable var16) {
         }
      }
   }
}
