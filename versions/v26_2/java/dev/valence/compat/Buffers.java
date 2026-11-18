package dev.valence.compat;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.rendertype.RenderType;

/** Vertex buffer acquisition for 26.2. */
public final class Buffers {
    private Buffers() {}

    /** The vertex consumer for {@code layer} in this buffer provider. */
    public static VertexConsumer get(MultiBufferSource buffers, RenderType layer) {
        return buffers.getBuffer(layer);
    }
}
