package dev.valence.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.world.phys.Vec3;

/**
 * Version-neutral world-render callbacks.
 *
 * <p>26.2 removed {@code MultiBufferSource} and the level render context now hands
 * out a {@code SubmitNodeCollector}. The callbacks in this mod still draw with the
 * buffered-geometry call surface, so each frame gets a recording
 * {@link MultiBufferSource} that submits its geometry to the collector when the
 * callback returns.
 */
public final class WorldRenderHook {
    /** Pose stack, recording buffer provider and camera position of one world render pass. */
    public record Frame(PoseStack matrices, MultiBufferSource consumers, Vec3 camera) {}

    private WorldRenderHook() {}

    /** Runs after entities are drawn, before the sky and translucent geometry. */
    public static void afterEntities(Consumer<Frame> callback) {
        LevelRenderEvents.AFTER_SOLID_FEATURES.register(ctx -> run(ctx, callback));
    }

    /** Runs at the very end of the level pass, over everything. */
    public static void endMain(Consumer<Frame> callback) {
        LevelRenderEvents.END_MAIN.register(ctx -> run(ctx, callback));
    }

    private static void run(LevelRenderContext ctx, Consumer<Frame> callback) {
        MultiBufferSource buffers = new MultiBufferSource(ctx.submitNodeCollector(), ctx.poseStack());
        try {
            callback.accept(new Frame(ctx.poseStack(), buffers, ctx.levelState().cameraRenderState.pos));
        } finally {
            buffers.flush();
        }
    }
}
