package dev.valence.compat;

import java.util.function.Consumer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;

/**
 * Version-neutral world-render callbacks.
 *
 * <p>The world-space parts of the mod (the sky engine and the 3D cosmetics) need
 * to run inside the level render pass with a pose stack, a vertex consumer
 * provider and the camera position. Fabric has moved that event three times:
 * the old {@code rendering.v1.WorldRenderEvents} through 1.21.8, no such event
 * at all in 1.21.9, and {@code rendering.v1.world.WorldRenderEvents} from
 * 1.21.10 on. The callbacks take this small frame record so the drawing code
 * never names any of them.
 *
 * <p>This is the 1.21.10 / 1.21.11 implementation.
 */
public final class WorldRenderHook {
   /** Pose stack, buffer provider and camera position of one world render pass. */
   public record Frame(class_4587 matrices, class_4597 consumers, class_243 camera) {
   }

   private WorldRenderHook() {
   }

   /** Runs after entities are drawn, before the sky and translucent geometry. */
   public static void afterEntities(Consumer<Frame> callback) {
      WorldRenderEvents.AFTER_ENTITIES.register(ctx -> callback.accept(frame(ctx)));
   }

   /** Runs at the very end of the level pass, over everything. */
   public static void endMain(Consumer<Frame> callback) {
      WorldRenderEvents.END_MAIN.register(ctx -> callback.accept(frame(ctx)));
   }

   private static Frame frame(WorldRenderContext ctx) {
      return new Frame(ctx.matrices(), ctx.consumers(), ctx.worldState().field_63082.field_63078);
   }
}
