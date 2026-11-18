package dev.valence.mixin;

import dev.valence.hud.RandomCoords;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.debug.DebugEntryLookingAt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Random coordinates for the {@code Targeted Block} line, 1.21.9 and newer.
 *
 * <p>Looking at a block prints its absolute position in the right-hand column. That is the same
 * leak as the {@code XYZ} line, just for the block under the crosshair, so it goes through the
 * same shift.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.client.gui.components.debug.DebugEntryLookingAt.class)
public abstract class DebugCoordsTargetMixin {
   private static String valence$spoof(Locale locale, String format, Object[] args) {
      return RandomCoords.spoof(String.format(locale, format, args));
   }

   @Redirect(
      method = "display",
      at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", ordinal = 0),
      require = 0
   )
   private static String valence$spoof0(Locale locale, String format, Object[] args) {
      return valence$spoof(locale, format, args);
   }

   @Redirect(
      method = "display",
      at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", ordinal = 1),
      require = 0
   )
   private static String valence$spoof1(Locale locale, String format, Object[] args) {
      return valence$spoof(locale, format, args);
   }

   @Redirect(
      method = "display",
      at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", ordinal = 2),
      require = 0
   )
   private static String valence$spoof2(Locale locale, String format, Object[] args) {
      return valence$spoof(locale, format, args);
   }
}
