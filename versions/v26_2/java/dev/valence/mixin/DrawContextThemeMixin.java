package dev.valence.mixin;

import dev.valence.gui.Theme;
import dev.valence.gui.ValenceScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Routes flat fills and text colours drawn inside the mod's own screen through the active theme
 * palette. Restricting to {@link ValenceScreen} keeps vanilla menus, chat and the HUD untouched.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.client.gui.GuiGraphicsExtractor.class)
public class DrawContextThemeMixin {
   private static boolean valence$theming() {
      return net.minecraft.client.Minecraft.getInstance().gui.screen() instanceof ValenceScreen;
   }

   @ModifyVariable(method = "fill", at = @At("HEAD"), argsOnly = true, index = 5)
   private int valence$fill(int color) {
      return valence$theming() ? Theme.map(color) : color;
   }

   @ModifyVariable(method = "fillGradient", at = @At("HEAD"), argsOnly = true, index = 5)
   private int valence$gradientTop(int color) {
      return valence$theming() ? Theme.map(color) : color;
   }

   @ModifyVariable(method = "fillGradient", at = @At("HEAD"), argsOnly = true, index = 6)
   private int valence$gradientBottom(int color) {
      return valence$theming() ? Theme.map(color) : color;
   }

   @ModifyVariable(
      method = {"text", "text", "text", "centeredText"},
      at = @At("HEAD"),
      argsOnly = true,
      index = 5
   )
   private int valence$text(int color) {
      return valence$theming() ? Theme.map(color) : color;
   }
}
