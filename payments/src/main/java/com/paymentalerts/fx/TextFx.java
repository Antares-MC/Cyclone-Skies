package com.paymentalerts.fx;

import java.util.function.IntUnaryOperator;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.joml.Matrix3x2fStack;

public final class TextFx {
    private TextFx() {
    }

    public static MutableComponent colored(String str, boolean z, IntUnaryOperator intUnaryOperator) {
        MutableComponent empty = Component.empty();
        for (int i = 0; i < str.length(); i++) {
            Style withColor = Style.EMPTY.withColor(intUnaryOperator.applyAsInt(i) & 16777215);
            if (z) {
                withColor = withColor.withBold(true);
            }
            empty.append(Component.literal(String.valueOf(str.charAt(i))).setStyle(withColor));
        }
        return empty;
    }

    public static MutableComponent plain(String str, int i, boolean z) {
        Style withColor = Style.EMPTY.withColor(i & 16777215);
        return Component.literal(str).setStyle(z ? withColor.withBold(true) : withColor);
    }

    public static void centered(GuiGraphics guiGraphics, Font font, Component component, float f, float f2, float f3, float f4) {
        if (f4 < 0.025f || f3 <= 0.01f) {
            return;
        }
        int width = font.width(component);
        Matrix3x2fStack pose = guiGraphics.pose();
        pose.pushMatrix();
        pose.translate(f, f2);
        pose.scale(f3, f3);
        Draw2D.text(guiGraphics, font, component, (-width) / 2, 0, Draw2D.argb(f4, 16777215));
        pose.popMatrix();
    }

    public static float fitScale(Font font, Component component, float f, float f2) {
        return Math.min(f2, f / Math.max(1, font.width(component)));
    }
}
