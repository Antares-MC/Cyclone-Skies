package dev.valence.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_340;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Placeholder for 1.21.8 and older.
 *
 * <p>These versions print the targeted block in the same list as everything else, so
 * {@code DebugCoordsMixin} already covers it and there is nothing separate to do here.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_340.class)
public abstract class DebugCoordsTargetMixin {
}
