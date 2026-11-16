package dev.valence.mixin;

import net.minecraft.class_329;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Vanilla HUD suppression hook.
 *
 * <p>1.21.6 replaced the flat {@code HudRenderCallback} with an element registry that can
 * swap out the vanilla hotbar and crosshair, so on those versions this mixin has nothing to
 * do and carries no injectors. The pre-1.21.6 overlay of this class cancels the two vanilla
 * draws instead and uses the tail of {@code InGameHud.render} to paint the replacements.
 */
@Mixin(class_329.class)
public abstract class HudSuppressMixin {
}
