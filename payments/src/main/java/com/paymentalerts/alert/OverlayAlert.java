package com.paymentalerts.alert;

import net.minecraft.client.gui.GuiGraphics;

interface OverlayAlert {
    void tick(long j);

    void render(GuiGraphics guiGraphics, int i, int i2, float f, long j);

    boolean isFinished(long j);
}
