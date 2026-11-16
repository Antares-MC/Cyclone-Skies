package dev.valence.compat;

import java.util.function.Consumer;
import net.minecraft.class_243;
import net.minecraft.class_4587;
import net.minecraft.class_4597;

/**
 * World-render callbacks, Minecraft 1.21.9.
 *
 * <p>Fabric's world render events were removed in this one release (they return
 * in 1.21.10 under a new package), and the level renderer it removed them in
 * favour of is retained-mode, so there is nowhere to hand an immediate-mode
 * vertex buffer. The world-space sky and the 3D cosmetics are therefore
 * disabled here; every HUD feature still runs. The callbacks are accepted and
 * never invoked so the shared code needs no version check.
 */
public final class WorldRenderHook {
   public record Frame(class_4587 matrices, class_4597 consumers, class_243 camera) {
   }

   private WorldRenderHook() {
   }

   public static void afterEntities(Consumer<Frame> callback) {
   }

   public static void endMain(Consumer<Frame> callback) {
   }
}
