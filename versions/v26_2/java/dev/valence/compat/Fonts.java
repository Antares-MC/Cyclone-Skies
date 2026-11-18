package dev.valence.compat;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.Font.GlyphVisitor;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;

/**
 * Selectable fonts for 26.2.
 *
 * <p>26.2 removed {@code Font.drawInBatch}: text is prepared into glyph renderables
 * and those are drawn into whatever vertex consumer their render type needs. This
 * facade keeps the old {@code draw(font, text, ..., matrix, buffers, mode, light)}
 * call surface by preparing the text and feeding every glyph into the recording
 * buffer.
 */
public final class Fonts {
    /** One selectable font. */
    public record Entry(FontDescription source) {}

    private Fonts() {}

    /** The font declared by {@code assets/<namespace>/font/<path>.json}. */
    public static Entry of(Identifier id) {
        return new Entry(new FontDescription.Resource(id));
    }

    /** Applies this font to a text style. */
    public static Style apply(Style style, Entry e) {
        return e == null ? style : style.withFont(e.source());
    }

    /** Applies this font to a text component. */
    public static Component apply(Component text, Entry e) {
        return e == null ? text : Component.literal("").withStyle(apply(Style.EMPTY, e)).append(text);
    }

    /** The renderer that measures and draws this font. */
    public static Font renderer(Entry e) {
        return Minecraft.getInstance().font;
    }

    /** Draws one text component with the given renderer. */
    public static void draw(Font font, Component text, float x, float y, int color, boolean shadow,
                            Matrix4f matrix, MultiBufferSource buffers, DisplayMode layer, int light) {
        draw(font, text.getVisualOrderText(), x, y, color, shadow, matrix, buffers, layer, light);
    }

    /** Draws one pre-ordered text sequence with the given renderer. */
    public static void draw(Font font, net.minecraft.util.FormattedCharSequence text, float x, float y, int color,
                            boolean shadow, Matrix4f matrix, MultiBufferSource buffers, DisplayMode layer, int light) {
        Font.PreparedText prepared = font.prepareText(text, x, y, color, shadow, false, 0);
        prepared.visit(new GlyphVisitor() {
            @Override
            public void acceptRenderable(TextRenderable renderable) {
                RenderType type = renderable.renderType(layer);
                VertexConsumer consumer = buffers.getBuffer(type);
                renderable.render(matrix, consumer, light, false);
            }
        });
    }
}
