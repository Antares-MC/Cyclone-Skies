package com.paymentalerts.ui;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

/**
 * A slider in the Cyclone style: a recessed track, a cyan fill, a round handle and the
 * label riding on top of it. Vanilla's slider draws a nine-slice frame from
 * {@code widgets/slider}, which does not exist in this jar and does not match the menu.
 */
public class UiSlider extends AbstractSliderButton {
    private final String label;
    private final DoubleFunction<String> format;
    private final DoubleConsumer onChange;

    public UiSlider(int x, int y, int width, int height, String label, double value,
            DoubleFunction<String> format, DoubleConsumer onChange) {
        super(x, y, width, height, Component.literal(label), value);
        this.label = label;
        this.format = format;
        this.onChange = onChange;
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        // Called from the superclass constructor, before the fields below exist.
        if (this.format == null) {
            setMessage(Component.literal(this.label == null ? "" : this.label));
            return;
        }
        setMessage(Component.literal(this.label + "  " + this.format.apply(this.value)));
    }

    @Override
    protected void applyValue() {
        if (this.onChange != null) {
            this.onChange.accept(this.value);
        }
    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        boolean hover = isHoveredOrFocused() && this.active;
        int x = getX();
        int y = getY();
        int w = this.width;
        int h = this.height;

        int fill = Ui.lerp(Ui.CARD, Ui.CARD_HOVER, hover ? 1.0f : 0.0f);
        int border = Ui.lerp(Ui.BORDER, Ui.BORDER_HOVER, hover ? 1.0f : 0.0f);
        if (hover) {
            Ui.glow(g, x, y, x + w, y + h, Ui.ACCENT, 0.10f);
        }
        Ui.round(g, x, y, x + w, y + h, fill, 5);
        Ui.outline(g, x, y, x + w, y + h, border, 5);

        // Label on the left, value on the right, track hugging the bottom edge: the same
        // split the menu's slider rows use, so the knob never sits on the text.
        int textY = y + (h - 8) / 2 - 1;
        String name = this.label == null ? "" : this.label;
        String value = this.format == null ? "" : this.format.apply(this.value);
        g.drawString(Ui.font(), Ui.fit(Ui.font(), name, w / 2), x + 8, textY,
                !this.active ? Ui.TEXT_MUTED : hover ? Ui.TEXT : Ui.TEXT_DIM);
        g.drawString(Ui.font(), value, x + w - 8 - Ui.font().width(value), textY,
                this.active ? Ui.ACCENT_LIGHT : Ui.TEXT_MUTED);

        int ty = y + h - 5;
        int left = x + 8;
        int right = x + w - 8;
        Ui.pill(g, left, ty, right, ty + 2, Ui.TRACK);
        int knobX = left + (int) Math.round((right - left - 2) * this.value) + 1;
        if (knobX > left + 2) {
            Ui.pill(g, left, ty, knobX, ty + 2, Ui.withAlpha(Ui.ACCENT, hover ? 1.0f : 0.85f));
        }
        Ui.circle(g, knobX, ty + 1, hover ? 3 : 2, this.active ? Ui.ACCENT : Ui.TEXT_MUTED);
    }
}
