package dev.valence.mixin;

import dev.valence.gui.Theme;
import dev.valence.gui.ValenceScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Routes flat fills and text colours drawn inside the mod's own screen through the active theme
 * palette. Restricting to {@link ValenceScreen} keeps vanilla menus, chat and the HUD untouched.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_332.class)
public class DrawContextThemeMixin {
   private static boolean valence$theming() {
      return class_310.method_1551().field_1755 instanceof ValenceScreen;
   }

   @ModifyVariable(method = "method_25294", at = @At("HEAD"), argsOnly = true, index = 5)
   private int valence$fill(int color) {
      return valence$theming() ? Theme.map(color) : color;
   }

   @ModifyVariable(method = "method_25296", at = @At("HEAD"), argsOnly = true, index = 5)
   private int valence$gradientTop(int color) {
      return valence$theming() ? Theme.map(color) : color;
   }

   @ModifyVariable(method = "method_25296", at = @At("HEAD"), argsOnly = true, index = 6)
   private int valence$gradientBottom(int color) {
      return valence$theming() ? Theme.map(color) : color;
   }

   @ModifyVariable(
      method = {"method_51433", "method_51430", "method_51439", "method_27534"},
      at = @At("HEAD"),
      argsOnly = true,
      index = 5
   )
   private int valence$text(int color) {
      return valence$theming() ? Theme.map(color) : color;
   }
}
