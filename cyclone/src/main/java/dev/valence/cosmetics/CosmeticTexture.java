package dev.valence.cosmetics;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;

/**
 * A texture used by a baked cosmetic model.
 *
 * <p>Vanilla-style model files address UVs in a {@code texture_size} space and some packs stack
 * their animation frames into one tall sheet, so {@code frames} and {@code frametime} describe how
 * the sheet is laid out: the world renderer advances the frame with the render clock, the menu
 * preview always samples frame zero. {@code asset} is the jar path the preview reads colours from.
 */
@Environment(EnvType.CLIENT)
public record CosmeticTexture(class_2960 id, String asset, int frames, float frametime, boolean glow) {
}
