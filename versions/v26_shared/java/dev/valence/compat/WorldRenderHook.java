package dev.valence.compat;

import java.util.function.Consumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Version-neutral world-render callbacks.
 *
 * <p>The world-space parts of the mod (the sky engine and the 3D cosmetics) need
 * to run inside the level render pass with a pose stack, a vertex consumer
 * provider and the camera position. Fabric has moved that event four times: the
 * old {@code rendering.v1.WorldRenderEvents} through 1.21.8, no such event at all
 * in 1.21.9, {@code rendering.v1.world.WorldRenderEvents} from 1.21.10, and the
 * retained-mode {@code rendering.v1.level.LevelRenderEvents} on 26.x. The
 * callbacks take this small frame record so the drawing code never names any of
 * them.
 *
 * <p>This is the 26.x implementation.
 */
public final class WorldRenderHook {
   /** Pose stack, buffer provider and camera position of one world render pass. */
   public record Frame(PoseStack matrices, MultiBufferSource consumers, Vec3 camera) {
   }

   private WorldRenderHook() {
   }

   /** Runs after entities are drawn, before the sky and translucent geometry. */
   public static void afterEntities(Consumer<Frame> callback) {
      LevelRenderEvents.AFTER_SOLID_FEATURES.register(ctx -> callback.accept(frame(ctx)));
   }

   /** Runs at the very end of the level pass, over everything. */
   public static void endMain(Consumer<Frame> callback) {
      LevelRenderEvents.END_MAIN.register(ctx -> callback.accept(frame(ctx)));
   }

   private static Frame frame(LevelRenderContext ctx) {
      return new Frame(ctx.poseStack(), ctx.bufferSource(), ctx.levelState().cameraRenderState.pos);
   }
}
