package dev.valence;

import dev.valence.config.ValenceConfig;
import dev.valence.cord.NeonLyrics;
import dev.valence.cord.live.LiveNotifier;
import dev.valence.cord.lyrics.SpotifyClient;
import dev.valence.emote.EmoteManager;
import dev.valence.emote.EmoteWheelScreen;
import dev.valence.gui.ValenceScreen;
import dev.valence.hud.ValenceHud;
import dev.valence.license.License;
import dev.valence.license.LicenseScreen;
import dev.valence.lyrics.LyricsBridge;
import dev.valence.sky.SkyRegistry;
import dev.valence.styles.StyleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_3675;
import net.minecraft.class_437;
import net.minecraft.class_304.class_11900;
import net.minecraft.class_3675.class_307;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class ValenceClient implements ClientModInitializer {
   public static final String MOD_ID = "cycloneskies";
   public static final String VERSION = "1.9.9";
   public static final Logger LOGGER = LoggerFactory.getLogger("CycloneSkies");
   public static final class_11900 CATEGORY = class_11900.method_74698(class_2960.method_60655("valence", "main"));
   public static class_304 OPEN_MENU;
   public static class_304 MEDIA_NEXT;
   public static class_304 MEDIA_PREV;
   public static class_304 MEDIA_PLAY;
   public static class_304 EMOTE;
   private static boolean licenseAsked;

   public void onInitializeClient() {
      ValenceConfig.load();
      StyleManager.init();
      SkyRegistry.load();
      NeonLyrics.init();
      LiveNotifier.init();
      OPEN_MENU = KeyBindingHelper.registerKeyBinding(new class_304("key.valence.open_menu", class_307.field_1668, 75, CATEGORY));
      MEDIA_NEXT = KeyBindingHelper.registerKeyBinding(new class_304("key.valence.media_next", class_307.field_1668, 295, CATEGORY));
      MEDIA_PREV = KeyBindingHelper.registerKeyBinding(
         new class_304("key.valence.media_prev", class_307.field_1668, class_3675.field_16237.method_1444(), CATEGORY)
      );
      MEDIA_PLAY = KeyBindingHelper.registerKeyBinding(
         new class_304("key.valence.media_play", class_307.field_1668, class_3675.field_16237.method_1444(), CATEGORY)
      );
      EMOTE = KeyBindingHelper.registerKeyBinding(new class_304("key.valence.emote", class_307.field_1668, 66, CATEGORY));
      ClientTickEvents.END_CLIENT_TICK
         .register(
            (EndTick)client -> {
               while (EMOTE.method_1436()) {
                  if (client.field_1755 == null && client.field_1724 != null && License.active()) {
                     client.method_1507(new EmoteWheelScreen(EMOTE));
                  }
               }

               EmoteManager.tick(client);

               while (OPEN_MENU.method_1436()) {
                  if (client.field_1755 == null) {
                     client.method_1507((class_437)(License.active() ? new ValenceScreen(null) : new LicenseScreen(null, true)));
                  }
               }

               if (!licenseAsked
                  && !License.active()
                  && client.field_1687 != null
                  && client.field_1724 != null
                  && client.field_1755 == null
                  && client.method_18506() == null) {
                  licenseAsked = true;
                  client.method_1507(new LicenseScreen(null, false));
               }

               LyricsBridge.tick();

               while (MEDIA_NEXT.method_1436()) {
                  SpotifyClient.control(SpotifyClient.Cmd.NEXT);
               }

               while (MEDIA_PREV.method_1436()) {
                  SpotifyClient.control(SpotifyClient.Cmd.PREVIOUS);
               }

               while (MEDIA_PLAY.method_1436()) {
                  SpotifyClient.control(SpotifyClient.Cmd.PLAYPAUSE);
               }
            }
         );
      HudElementRegistry.attachElementAfter(VanillaHudElements.SUBTITLES, class_2960.method_60655("valence", "hud"), (g, d) -> {
         if (License.active()) {
            ValenceHud.render(g, d);
            EmoteManager.renderHud(g);
         }
      });
      LOGGER.info("CycloneSkies {} ready", "1.9.9");
   }
}
