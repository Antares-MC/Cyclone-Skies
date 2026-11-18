package dev.valence.hud;

import dev.valence.config.ValenceConfig;
import dev.valence.gui.Theme;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

@Environment(EnvType.CLIENT)
public final class ValenceHud {
   private ValenceHud() {
   }

   public static void render(net.minecraft.client.gui.GuiGraphicsExtractor g) {
      net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
      if (!mc.gui.hud.isHidden()) {
         ValenceConfig cfg = ValenceConfig.get();
         if (cfg.hudLogo) {
            int size = Math.round(18.0F * cfg.hudLogoScale);
            Theme.image(g, Theme.LOGO, 4, 4, size, size, 128, 128, -285212673);
         }
      }
   }

   public static String formatTime(long ms) {
      long s = Math.max(0L, ms / 1000L);
      return String.format("%d:%02d", s / 60L, s % 60L);
   }
}
