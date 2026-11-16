package dev.valence.compat;

import java.util.function.BiConsumer;
import net.minecraft.class_1921;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4587.class_4665;
import net.minecraft.class_4597;

/**
 * One world-space draw submission.
 *
 * <p>1.21.9 introduced the ordered render command queue, which takes a pose and a
 * render layer and calls back with a vertex consumer later in the pass, so geometry
 * can be submitted from a context that is not allowed to touch the buffer builder
 * directly. Older versions hand out a vertex consumer straight from the buffer
 * provider instead. Both are hidden behind this interface, which is the only thing the
 * emotes and props drawing code knows about.
 *
 * <p>This is the 1.21.2 - 1.21.8 implementation, which takes the consumer eagerly and
 * runs the draw callback immediately.
 */
public interface EmoteBatch {
   /** Runs {@code draw} with the vertex consumer for {@code layer}. */
   void submit(class_4587 pose, class_1921 layer, BiConsumer<class_4665, class_4588> draw);

   /** Wraps the vanilla buffer provider as a batch. */
   static EmoteBatch of(class_4597 buffers) {
      return (pose, layer, draw) -> draw.accept(pose.method_23760(), Buffers.get(buffers, layer));
   }
}
