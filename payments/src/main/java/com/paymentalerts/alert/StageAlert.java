package com.paymentalerts.alert;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

interface StageAlert {
    void tick(long j);

    void render(GuiGraphics guiGraphics, Font font, int i, int i2, float f, long j);

    boolean isFinished(long j);
}
