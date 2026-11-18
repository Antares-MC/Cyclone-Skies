package com.paymentalerts.screen;

import com.paymentalerts.alert.Tier;
import com.paymentalerts.payment.AmountFormat;
import com.paymentalerts.payment.PaymentParser;
import com.paymentalerts.ui.Ui;
import com.paymentalerts.ui.UiButton;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

final class EffectsList extends ContainerObjectSelectionList<EffectsList.Row> {
    private static final int ROW_WIDTH = 340;

    EffectsList(Minecraft minecraft, int i, int i2, int i3, Consumer<Tier> consumer) {
        super(minecraft, i, i2, i3, 24);
        for (Tier tier : Tier.EFFECTS) {
            addEntry(new Row(tier, consumer));
        }
    }

    public int getRowWidth() {
        return ROW_WIDTH;
    }

    static final class Row extends ContainerObjectSelectionList.Entry<Row> {
        private final Tier tier;
        private final EditBox amount;
        private final UiButton look;
        private final UiButton onOff;
        private final UiButton preview;
        private final List<AbstractWidget> widgets = new ArrayList<>();

        Row(Tier tier, Consumer<Tier> consumer) {
            this.tier = tier;
            String[] variations = tier.variations();

            this.amount = new EditBox(Ui.font(), 0, 0, 42, 16, Component.literal(tier.label + " amount"));
            this.amount.setBordered(false);
            this.amount.setMaxLength(14);
            this.amount.setValue(AmountFormat.compact(tier.threshold()).substring(1));
            this.amount.setTooltip(Tooltip.create(Component.literal("Smallest payment that plays this effect, e.g. 1m, 250m, 2.5b")));
            this.amount.setResponder(str -> {
                OptionalDouble parseAmount = PaymentParser.parseAmount(str);
                if (parseAmount.isPresent() && parseAmount.getAsDouble() >= 1.0d && parseAmount.getAsDouble() < 9.0E18d) {
                    tier.setThreshold((long) parseAmount.getAsDouble());
                    this.amount.setTextColor(Ui.GOOD);
                } else {
                    this.amount.setTextColor(Ui.BAD);
                }
            });

            this.look = new UiButton(0, 0, 112, 20, Component.literal(lookLabel(variations, tier.look())), b -> {
            }) {
                @Override
                public void onPress(InputWithModifiers input) {
                    int next = tier.look() + 1;
                    if (next >= variations.length) {
                        next = -1;
                    }
                    tier.setLook(next);
                    setMessage(Component.literal(lookLabel(variations, next)));
                }
            };
            this.look.setTooltip(Tooltip.create(Component.literal("Click to cycle through the " + variations.length + " looks, or Random to pick each time.")));

            this.onOff = new UiButton(0, 0, 40, 20, Component.literal(tier.isEnabled() ? "On" : "Off"), b -> {
            }) {
                @Override
                public void onPress(InputWithModifiers input) {
                    boolean next = !tier.isEnabled();
                    tier.setEnabled(next);
                    setMessage(Component.literal(next ? "On" : "Off"));
                    selected(next);
                }
            };
            this.onOff.selected(tier.isEnabled());
            this.onOff.setTooltip(Tooltip.create(Component.literal("Off means payments in this range play the next effect below that is on.")));

            this.preview = new UiButton(0, 0, 20, 20, Component.literal("\u25B6"), b -> consumer.accept(tier));
            this.preview.setTooltip(Tooltip.create(Component.literal("Preview " + tier.label)));
            this.preview.accent(true);

            this.widgets.add(this.amount);
            this.widgets.add(this.look);
            this.widgets.add(this.onOff);
            this.widgets.add(this.preview);
        }

        private static String lookLabel(String[] variations, int look) {
            return look < 0 || look >= variations.length ? "Random look" : variations[look];
        }

        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean z, float f) {
            int contentX = getContentX();
            int contentY = getContentY();
            int cardRight = contentX + ROW_WIDTH;
            boolean hover = mouseX >= contentX && mouseX < cardRight && mouseY >= contentY && mouseY < contentY + 22;

            Ui.card(guiGraphics, contentX, contentY, cardRight, contentY + 22, hover ? 1.0f : 0.0f, false);
            Ui.round(guiGraphics, contentX + 3, contentY + 7, contentX + 5, contentY + 15, 0xFF000000 | this.tier.color, 1);
            Ui.label(guiGraphics, Ui.font(), this.tier.label, contentX + 9, contentY + 7, 84,
                    this.tier.isEnabled() ? Ui.TEXT : Ui.TEXT_MUTED);

            if (mouseX >= contentX && mouseX < contentX + 96 && mouseY >= contentY && mouseY < contentY + 22) {
                guiGraphics.setTooltipForNextFrame(Ui.font().split(Component.literal(this.tier.description), 200), mouseX, mouseY);
            }

            // Amount field: our card, a borderless EditBox inside it.
            int amountX = contentX + 98;
            Ui.round(guiGraphics, amountX, contentY + 3, amountX + 50, contentY + 19, Ui.CARD, 4);
            Ui.outline(guiGraphics, amountX, contentY + 3, amountX + 50, contentY + 19, Ui.BORDER, 4);
            this.amount.setX(amountX + 4);
            this.amount.setY(contentY + 7);

            this.look.setX(contentX + 152);
            this.look.setY(contentY + 1);
            this.onOff.setX(contentX + 268);
            this.onOff.setY(contentY + 1);
            this.preview.setX(contentX + 312);
            this.preview.setY(contentY + 1);

            Iterator<AbstractWidget> it = this.widgets.iterator();
            while (it.hasNext()) {
                it.next().render(guiGraphics, mouseX, mouseY, f);
            }
        }

        public List<? extends GuiEventListener> children() {
            return this.widgets;
        }

        public List<? extends NarratableEntry> narratables() {
            return this.widgets;
        }
    }
}
