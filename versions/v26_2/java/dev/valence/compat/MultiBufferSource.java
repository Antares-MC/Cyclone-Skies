package dev.valence.compat;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * Recording replacement for {@code MultiBufferSource}, removed in Minecraft 26.2.
 *
 * <p>26.2 replaced the immediate buffered-geometry pipeline with retained-mode
 * submission: world geometry is now handed to a {@code SubmitNodeCollector}, which
 * builds it later. The mod's sky, cosmetic and lyric renderers draw through the old
 * {@code getBuffer(layer) -> VertexConsumer} surface, so this class records every
 * vertex per render type and replays the batch into
 * {@code submitCustomGeometry} when the frame's callback returns.
 *
 * <p>Replay writes raw positions: callers already pre-multiply their vertices by the
 * world pose, so applying the submitted pose a second time would transform them
 * twice.
 */
public class MultiBufferSource {
    private final SubmitNodeCollector collector;
    private final PoseStack pose;
    private final Map<RenderType, Recorder> layers = new LinkedHashMap<>();

    public MultiBufferSource(SubmitNodeCollector collector, PoseStack pose) {
        this.collector = collector;
        this.pose = pose;
    }

    /** Compatibility factory: the pixel builder only sized the old immediate buffer. */
    public static BufferSource immediate(ByteBufferBuilder builder) {
        return new BufferSource(null, null);
    }
    /** Compatibility factory used by the version-neutral world render hook. */
    public static MultiBufferSource create(ByteBufferBuilder builder) {
        return new MultiBufferSource(null, null);
    }

    public VertexConsumer getBuffer(RenderType layer) {
        Recorder recorder = layers.get(layer);
        if (recorder == null) {
            recorder = new Recorder();
            layers.put(layer, recorder);
        }
        return recorder;
    }

    /** Ends the current batch and submits every recorded layer. */
    public void endBatch() {
        flush();
    }

    /** Submits the recorded geometry to the node collector, then clears the batch. */
    public void flush() {
        if (collector == null || pose == null) {
            layers.clear();
            return;
        }
        for (Map.Entry<RenderType, Recorder> entry : layers.entrySet()) {
            Recorder recorder = entry.getValue();
            if (recorder.count == 0) {
                continue;
            }
            collector.submitCustomGeometry(pose, entry.getKey(), (submitted, consumer) -> recorder.replay(consumer));
        }
        layers.clear();
    }

    /** Alias kept for the 26.x call surface. */
    public static class BufferSource extends MultiBufferSource {
        BufferSource(SubmitNodeCollector collector, PoseStack pose) {
            super(collector, pose);
        }
    }

    private static final class Recorder implements VertexConsumer {
        private static final int STRIDE = 17;
        private float[] data = new float[STRIDE * 256];
        private int count;
        private float red = 1.0F;
        private float green = 1.0F;
        private float blue = 1.0F;
        private float alpha = 1.0F;
        private float u;
        private float v;
        private int uv1u;
        private int uv1v;
        private int uv2u;
        private int uv2v;
        private float nx;
        private float ny = 1.0F;
        private float nz;
        private float lineWidth = 1.0F;

        private void ensure() {
            if ((count + 1) * STRIDE > data.length) {
                float[] bigger = new float[data.length * 2];
                System.arraycopy(data, 0, bigger, 0, data.length);
                data = bigger;
            }
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            ensure();
            int i = count * STRIDE;
            data[i] = x;
            data[i + 1] = y;
            data[i + 2] = z;
            data[i + 3] = red;
            data[i + 4] = green;
            data[i + 5] = blue;
            data[i + 6] = alpha;
            data[i + 7] = u;
            data[i + 8] = v;
            data[i + 9] = uv1u;
            data[i + 10] = uv1v;
            data[i + 11] = uv2u;
            data[i + 12] = uv2v;
            data[i + 13] = nx;
            data[i + 14] = ny;
            data[i + 15] = nz;
            data[i + 16] = lineWidth;
            count++;
            return this;
        }

        @Override
        public VertexConsumer setColor(int r, int g, int b, int a) {
            red = r / 255.0F;
            green = g / 255.0F;
            blue = b / 255.0F;
            alpha = a / 255.0F;
            return this;
        }

        @Override
        public VertexConsumer setColor(int argb) {
            return setColor(argb >> 16 & 255, argb >> 8 & 255, argb & 255, argb >> 24 & 255);
        }

        @Override
        public VertexConsumer setColor(float r, float g, float b, float a) {
            red = r;
            green = g;
            blue = b;
            alpha = a;
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            this.u = u;
            this.v = v;
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            this.uv1u = u;
            this.uv1v = v;
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            this.uv2u = u;
            this.uv2v = v;
            return this;
        }

        @Override
        public VertexConsumer setLight(int packed) {
            this.uv1u = packed & 0xFFFF;
            this.uv1v = packed >> 16 & 0xFFFF;
            return this;
        }

        @Override
        public VertexConsumer setOverlay(int packed) {
            this.uv2u = packed & 0xFFFF;
            this.uv2v = packed >> 16 & 0xFFFF;
            return this;
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            this.nx = x;
            this.ny = y;
            this.nz = z;
            return this;
        }

        @Override
        public VertexConsumer setLineWidth(float width) {
            this.lineWidth = width;
            return this;
        }

        private void replay(VertexConsumer out) {
            for (int n = 0; n < count; n++) {
                int i = n * STRIDE;
                out.setColor(data[i + 3], data[i + 4], data[i + 5], data[i + 6]);
                out.setUv(data[i + 7], data[i + 8]);
                out.setUv1((int) data[i + 9], (int) data[i + 10]);
                out.setUv2((int) data[i + 11], (int) data[i + 12]);
                out.setNormal(data[i + 13], data[i + 14], data[i + 15]);
                out.setLineWidth(data[i + 16]);
                out.addVertex(data[i], data[i + 1], data[i + 2]);
            }
        }
    }
}
