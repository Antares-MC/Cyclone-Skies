package dev.valence.mixin;

import dev.valence.hud.RandomCoords;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11621;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Random coordinates on the debug screen, 1.21.9 and newer.
 *
 * <p>Since 1.21.9 the debug screen is assembled from independent entries that each format their
 * own lines; the position entry is the one that prints {@code XYZ}, {@code Block} and
 * {@code Chunk}. Rather than reimplement the entry, every {@code String.format} it performs is
 * passed through {@link RandomCoords#spoof}, which only rewrites lines that carry a position.
 *
 * <p>Older versions have no such entry and use {@code DebugCoordsLegacyMixin} instead.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_11621.class)
public abstract class DebugCoordsMixin {
   private static String valence$spoof(Locale locale, String format, Object[] args) {
      return RandomCoords.spoof(String.format(locale, format, args));
   }

   @Redirect(
      method = "method_72751",
      at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", ordinal = 0),
      require = 0
   )
   private static String valence$spoof0(Locale locale, String format, Object[] args) {
      return valence$spoof(locale, format, args);
   }

   @Redirect(
      method = "method_72751",
      at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", ordinal = 1),
      require = 0
   )
   private static String valence$spoof1(Locale locale, String format, Object[] args) {
      return valence$spoof(locale, format, args);
   }

   @Redirect(
      method = "method_72751",
      at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", ordinal = 2),
      require = 0
   )
   private static String valence$spoof2(Locale locale, String format, Object[] args) {
      return valence$spoof(locale, format, args);
   }

   @Redirect(
      method = "method_72751",
      at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", ordinal = 3),
      require = 0
   )
   private static String valence$spoof3(Locale locale, String format, Object[] args) {
      return valence$spoof(locale, format, args);
   }
}
