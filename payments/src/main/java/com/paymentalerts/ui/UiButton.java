package com.paymentalerts.ui;

import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

/**
 * A menu button in the Cyclone style: a translucent card that warms up under the pointer,
 * one hairline border, and a cyan rim when it is the current choice.
 */
public class UiButton extends Button {
    private boolean selected;
    private boolean accent;

    public UiButton(int i, int j, int k, int l, Component component, OnPress onPress) {
        super(i, j, k, l, component, onPress, DEFAULT_NARRATION);
    }

    /** Marks this button as the current tab or the current value. */
    public UiButton selected(boolean z) {
        this.selected = z;
        return this;
    }

    /** Draws the label in the accent colour even when not selected. */
    public UiButton accent(boolean z) {
        this.accent = z;
        return this;
    }

    public boolean isSelected() {
        return this.selected;
    }

    protected void renderContents(GuiGraphics guiGraphics, int i, int j, float f) {
        boolean hover = this.isHoveredOrFocused() && this.active;
        int fill = this.selected ? Ui.lerp(Ui.CARD_ACTIVE, Ui.withAlpha(Ui.ACCENT, 0.40f), hover ? 1.0f : 0.0f)
                : Ui.lerp(Ui.CARD, Ui.CARD_HOVER, hover ? 1.0f : 0.0f);
        int border = this.selected ? Ui.withAlpha(Ui.ACCENT, 0.55f)
                : Ui.lerp(Ui.BORDER, Ui.BORDER_HOVER, hover ? 1.0f : 0.0f);
        if (!this.active) {
            fill = Ui.alpha(Ui.CARD, 10);
            border = Ui.alpha(Ui.BORDER, 20);
        }
        if (hover || this.selected) {
            Ui.glow(guiGraphics, getX(), getY(), getX() + this.width, getY() + this.height, Ui.ACCENT,
                    this.selected ? 0.20f : 0.10f);
        }
        Ui.round(guiGraphics, getX(), getY(), getX() + this.width, getY() + this.height, fill, 5);
        Ui.outline(guiGraphics, getX(), getY(), getX() + this.width, getY() + this.height, border, 5);

        int color = !this.active ? Ui.TEXT_MUTED
                : this.selected || this.accent ? Ui.ACCENT_LIGHT
                : hover ? Ui.TEXT : Ui.TEXT_DIM;
        String text = Ui.fit(Ui.font(), getMessage().getString(), this.width - 8);
        guiGraphics.drawCenteredString(Ui.font(), text, getX() + this.width / 2, getY() + (this.height - 8) / 2, color);
    }

    /** A switch instead of a push button: the label stays, a pill on the right shows state. */
    public static final class Toggle extends UiButton {
        private boolean value;
        private final Consumer<Boolean> onChange;

        public Toggle(int i, int j, int k, int l, Component component, boolean z, Consumer<Boolean> consumer) {
            super(i, j, k, l, component, button -> {
            });
            this.value = z;
            this.onChange = consumer;
        }

        public boolean value() {
            return this.value;
        }

        public void value(boolean z) {
            this.value = z;
        }

        public void onPress(InputWithModifiers inputWithModifiers) {
            this.value = !this.value;
            if (this.onChange != null) {
                this.onChange.accept(this.value);
            }
        }

        protected void renderContents(GuiGraphics guiGraphics, int i, int j, float f) {
            boolean hover = this.isHoveredOrFocused() && this.active;
            int fill = Ui.lerp(Ui.CARD, Ui.CARD_HOVER, hover ? 1.0f : 0.0f);
            int border = this.value ? Ui.withAlpha(Ui.ACCENT, 0.45f)
                    : Ui.lerp(Ui.BORDER, Ui.BORDER_HOVER, hover ? 1.0f : 0.0f);
            if (hover) {
                Ui.glow(guiGraphics, getX(), getY(), getX() + this.width, getY() + this.height, Ui.ACCENT, 0.10f);
            }
            Ui.round(guiGraphics, getX(), getY(), getX() + this.width, getY() + this.height, fill, 5);
            Ui.outline(guiGraphics, getX(), getY(), getX() + this.width, getY() + this.height, border, 5);

            int textColor = this.value ? Ui.TEXT : Ui.TEXT_DIM;
            String text = Ui.fit(Ui.font(), getMessage().getString(), this.width - 44);
            guiGraphics.drawString(Ui.font(), text, getX() + 8, getY() + (this.height - 8) / 2, textColor);

            int sw = 22;
            int sx = getX() + this.width - sw - 8;
            int sy = getY() + (this.height - 11) / 2;
            Ui.pill(guiGraphics, sx, sy, sx + sw, sy + 11, this.value ? Ui.withAlpha(Ui.ACCENT, 0.40f) : Ui.withAlpha(Ui.TEXT_MUTED, 0.35f));
            int knob = this.value ? sx + sw - 9 : sx + 1;
            Ui.pill(guiGraphics, knob, sy + 1, knob + 9, sy + 10, this.value ? Ui.ACCENT : Ui.TEXT_MUTED);
        }
    }
}
