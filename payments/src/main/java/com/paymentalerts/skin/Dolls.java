package com.paymentalerts.skin;

import com.paymentalerts.fx.Draw2D;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.player.PlayerSkin;

/**
 * 1.21.11 port: the 3D picture-in-picture skin doll from the 26.x source is not ported yet
 * (see reference-26x/). Every caller already falls back to a flat face when {@link #isBroken()}
 * is true or {@link #create} returns null, so alerts show player faces instead.
 */
public final class Dolls {
    private Dolls() {
    }

    public static boolean isBroken() {
        return true;
    }

    public static void beginFrame() {
    }

    public static SkinDoll create(SkinFetcher.Skin skin, String str) {
        return null;
    }

    public static boolean draw(GuiGraphics guiGraphics, SkinDoll skinDoll, float f, float f2, int i, float f3, float f4) {
        return false;
    }

    public static boolean drawOnScreen(GuiGraphics guiGraphics, SkinDoll skinDoll, int i, float f, float f2, int i2, float f3, ScreenRectangle screenRectangle) {
        return false;
    }

    public static boolean drawInBox(GuiGraphics guiGraphics, SkinDoll skinDoll, int i, int i2, int i3, int i4, int i5, float f, float f2) {
        return false;
    }

    public static void face(GuiGraphics guiGraphics, SkinFetcher.Skin skin, int i, int i2, int i3) {
        Draw2D.flush(guiGraphics);
        try {
            PlayerSkin playerSkin = skin.get();
            if (playerSkin == null) {
                playerSkin = DefaultPlayerSkin.get(skin.profile().id());
            }
            PlayerFaceRenderer.draw(guiGraphics, playerSkin, i, i2, i3);
        } catch (Throwable th) {
        }
    }
}
