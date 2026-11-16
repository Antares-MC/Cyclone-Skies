package dev.valence.compat;

import java.util.function.Consumer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;

/**
 * World-render callbacks, Minecraft 1.21.10.
 *
 * <p>Same event API as 1.21.11, but {@code Camera} still has a {@code getPos()}
 * method rather than the public position field.
 */
public final class WorldRenderHook {
   public record Frame(class_4587 matrices, class_4597 consumers, class_243 camera) {
   }

   private WorldRenderHook() {
   }

   public static void afterEntities(Consumer<Frame> callback) {
      WorldRenderEvents.AFTER_ENTITIES.register(ctx -> callback.accept(frame(ctx)));
   }

   public static void endMain(Consumer<Frame> callback) {
      WorldRenderEvents.END_MAIN.register(ctx -> callback.accept(frame(ctx)));
   }

   private static Frame frame(WorldRenderContext ctx) {
      class_310 mc = class_310.method_1551();
      class_243 cam = mc.field_1773.method_19418().method_19326();
      return new Frame(ctx.matrices(), ctx.consumers(), cam);
   }
}
