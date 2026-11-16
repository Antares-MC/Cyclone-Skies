package dev.valence.compat;

import net.minecraft.class_1921;
import net.minecraft.class_4588;
import net.minecraft.class_4597;

/**
 * Vertex buffer acquisition.
 *
 * <p>1.21.9 renamed the buffer provider's lookup on a different type with a remapped
 * id; older versions take the same arguments. One call keeps the cosmetics and sky
 * code free of the difference.
 *
 * <p>This is the 1.21.2 - 1.21.8 implementation.
 */
public final class Buffers {
   private Buffers() {
   }

   /** The vertex consumer for {@code layer} in this buffer provider. */
   public static class_4588 get(class_4597 buffers, class_1921 layer) {
      return buffers.getBuffer(layer);
   }
}
