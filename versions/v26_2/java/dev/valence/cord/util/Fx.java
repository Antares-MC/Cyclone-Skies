package dev.valence.cord.util;

import dev.valence.gui.UiSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public final class Fx {
   private Fx() {
   }

   public static String t(String key, Object... args) {
      return net.minecraft.client.resources.language.I18n.get(key, args);
   }

   public static void sound(Fx.Sfx s) {
      switch (s) {
         case CLICK:
            UiSounds.click();
            break;
         case WIN:
            UiSounds.toggle(true);
            break;
         case ERROR:
            UiSounds.toggle(false);
            break;
         case TICK:
            UiSounds.hover();
      }
   }

   public static void info(String text) {
      net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
      if (mc.gui != null) {
         mc.gui.hud
            .getChat()
            .addClientSystemMessage(
               net.minecraft.network.chat.Component.literal("[CycloneSkies] ")
                  .withStyle(net.minecraft.ChatFormatting.AQUA)
                  .append(net.minecraft.network.chat.Component.literal(text).withStyle(net.minecraft.ChatFormatting.GRAY))
            );
      }
   }

   public static void error(String text) {
      net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
      if (mc.gui != null) {
         mc.gui.hud
            .getChat()
            .addClientSystemMessage(
               net.minecraft.network.chat.Component.literal("[CycloneSkies] ")
                  .withStyle(net.minecraft.ChatFormatting.DARK_RED)
                  .append(net.minecraft.network.chat.Component.literal(text).withStyle(net.minecraft.ChatFormatting.RED))
            );
         sound(Fx.Sfx.ERROR);
      }
   }

   public static void clipboard(String s) {
      net.minecraft.client.Minecraft.getInstance().keyboardHandler.setClipboard(s);
   }

   public static String myName() {
      return net.minecraft.client.Minecraft.getInstance().getUser().getName();
   }

   @Environment(EnvType.CLIENT)
   public enum Sfx {
      CLICK,
      WIN,
      ERROR,
      TICK;
   }
}
