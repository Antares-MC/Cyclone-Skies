package com.paymentalerts.screen;

import com.paymentalerts.alert.Tier;
import com.paymentalerts.payment.AmountFormat;
import com.paymentalerts.payment.PaymentParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

final class EffectsList extends ContainerObjectSelectionList<EffectsList.Row> {
    private static final int ROW_WIDTH = 362;

    EffectsList(Minecraft minecraft, int i, int i2, int i3, Consumer<Tier> consumer) {
        super(minecraft, i, i2, i3, 24);
        for (Tier tier : Tier.EFFECTS) {
            addEntry(new Row(minecraft.font, tier, consumer));
        }
    }

    public int getRowWidth() {
        return ROW_WIDTH;
    }

    static final class Row extends ContainerObjectSelectionList.Entry<Row> {
        private static final int TEXT_OK = -2039584;
        private static final int TEXT_BAD = -40864;
        private final Font font;
        private final Tier tier;
        private final Component label;
        private final EditBox amount;
        private final CycleButton<Integer> look;
        private final CycleButton<Boolean> enabled;
        private final Button preview;
        private final List<AbstractWidget> widgets = new ArrayList();

        Row(Font font, Tier tier, Consumer<Tier> consumer) {
            this.font = font;
            this.tier = tier;
            this.label = Component.literal(tier.label);
            this.amount = new EditBox(font, 0, 0, 52, 18, Component.literal(tier.label + " amount"));
            this.amount.setMaxLength(14);
            this.amount.setValue(AmountFormat.compact(tier.threshold()).substring(1));
            this.amount.setTooltip(Tooltip.create(Component.literal("Smallest payment that plays this effect, e.g. 1m, 250m, 2.5b")));
            this.amount.setResponder(str -> {
                OptionalDouble parseAmount = PaymentParser.parseAmount(str);
                if (parseAmount.isPresent() && parseAmount.getAsDouble() >= 1.0d && parseAmount.getAsDouble() < 9.0E18d) {
                    tier.setThreshold((long) parseAmount.getAsDouble());
                    this.amount.setTextColor(TEXT_OK);
                } else {
                    this.amount.setTextColor(TEXT_BAD);
                }
            });
            String[] variations = tier.variations();
            ArrayList<Integer> arrayList = new ArrayList<>();
            for (int i = -1; i < variations.length; i++) {
                arrayList.add(Integer.valueOf(i));
            }
            this.look = CycleButton.<Integer>builder(num -> {
                return Component.literal(num.intValue() < 0 ? "Random look" : variations[num.intValue()]);
            }, Integer.valueOf(tier.look())).withValues(arrayList).displayOnlyValue().withTooltip(num2 -> {
                String str2;
                if (num2.intValue() < 0) {
                    str2 = "Picks one of the " + variations.length + " looks at random each time.";
                } else {
                    str2 = "Always use this look.";
                }
                return Tooltip.create(Component.literal(str2));
            }).create(0, 0, 128, 20, Component.literal("Look"), (cycleButton, num3) -> {
                tier.setLook(num3.intValue());
            });
            this.enabled = CycleButton.onOffBuilder(tier.isEnabled()).displayOnlyValue().withTooltip(bool -> {
                String str2;
                if (bool.booleanValue()) {
                    str2 = "On. Payments in its range play this effect.";
                } else {
                    str2 = "Off. Payments in its range play the next effect below that is on.";
                }
                return Tooltip.create(Component.literal(str2));
            }).create(0, 0, 40, 20, Component.literal("On"), (cycleButton2, bool2) -> {
                tier.setEnabled(bool2.booleanValue());
            });
            this.preview = Button.builder(Component.literal("▶"), button -> {
                consumer.accept(tier);
            }).size(20, 20).tooltip(Tooltip.create(Component.literal("Preview " + tier.label))).build();
            this.widgets.add(this.amount);
            this.widgets.add(this.look);
            this.widgets.add(this.enabled);
            this.widgets.add(this.preview);
        }

        public void renderContent(GuiGraphics guiGraphics, int i, int i2, boolean z, float f) {
            int contentX = getContentX();
            int contentY = getContentY();
            int contentHeight = contentY + ((getContentHeight() - 8) / 2);
            guiGraphics.fill(contentX, contentY + 3, contentX + 3, contentY + 17, (-16777216) | this.tier.color);
            guiGraphics.drawString(this.font, this.label, contentX + 7, contentHeight, this.tier.isEnabled() ? -1 : -8355712);
            if (i >= contentX && i < contentX + 104 && i2 >= contentY && i2 < contentY + 20) {
                guiGraphics.setTooltipForNextFrame(this.font.split(Component.literal(this.tier.description), 200), i, i2);
            }
            this.amount.setX(contentX + 108);
            this.amount.setY(contentY + 1);
            this.look.setX(contentX + 164);
            this.look.setY(contentY);
            this.enabled.setX(contentX + 296);
            this.enabled.setY(contentY);
            this.preview.setX(contentX + 340);
            this.preview.setY(contentY);
            Iterator<AbstractWidget> it = this.widgets.iterator();
            while (it.hasNext()) {
                it.next().render(guiGraphics, i, i2, f);
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
