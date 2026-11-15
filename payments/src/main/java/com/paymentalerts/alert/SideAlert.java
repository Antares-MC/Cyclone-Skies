package com.paymentalerts.alert;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

interface SideAlert {
    int render(GuiGraphics guiGraphics, Font font, int i, int i2, long j);

    boolean isFinished(long j);
}
