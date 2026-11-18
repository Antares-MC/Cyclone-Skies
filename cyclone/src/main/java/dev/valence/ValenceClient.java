package dev.valence;

import dev.valence.compat.Hud;
import dev.valence.compat.Keys;
import dev.valence.config.ValenceConfig;
import dev.valence.cord.NeonLyrics;
import dev.valence.cord.live.LiveNotifier;
import dev.valence.cord.lyrics.SpotifyClient;
import dev.valence.emote.EmoteManager;
import dev.valence.emote.EmoteWheelScreen;
import dev.valence.gui.ValenceScreen;
import dev.valence.hud.RandomCoords;
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
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_3675;
import net.minecraft.class_437;
import net.minecraft.class_3675.class_307;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class ValenceClient implements ClientModInitializer {
   public static final String MOD_ID = "cycloneskies";
   public static final String VERSION = "1.9.9";
   public static final Logger LOGGER = LoggerFactory.getLogger("CycloneSkies");
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
      OPEN_MENU = Keys.bind("key.valence.open_menu", 75);
      MEDIA_NEXT = Keys.bind("key.valence.media_next", 295);
      MEDIA_PREV = Keys.bind("key.valence.media_prev", -1);
      MEDIA_PLAY = Keys.bind("key.valence.media_play", -1);
      EMOTE = Keys.bind("key.valence.emote", 66);
      // Any emote state left over from a previous world would keep the camera pinned, so both
      // sides of a connection reset it. The coordinate offset is drawn per world.
      ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
         EmoteManager.hardReset();
         RandomCoords.refresh();
      });
      ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> EmoteManager.hardReset());
      ClientCommandRegistrationCallback.EVENT.register(
         (dispatcher, access) -> dispatcher.register(
            ClientCommandManager.literal("cyclone")
               .then(
                  ClientCommandManager.literal("randomcoords")
                     .executes(ctx -> {
                        randomCoords(ctx.getSource(), !RandomCoords.enabled());
                        return 1;
                     })
                     .then(
                        ClientCommandManager.literal("on")
                           .executes(ctx -> {
                              randomCoords(ctx.getSource(), true);
                              return 1;
                           })
                     )
                     .then(
                        ClientCommandManager.literal("off")
                           .executes(ctx -> {
                              randomCoords(ctx.getSource(), false);
                              return 1;
                           })
                     )
                     .then(
                        ClientCommandManager.literal("new")
                           .executes(ctx -> {
                              RandomCoords.refresh();
                              randomCoords(ctx.getSource(), RandomCoords.enabled());
                              return 1;
                           })
                     )
               )
         )
      );
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
      Hud.afterSubtitles(class_2960.method_60655("valence", "hud"), g -> {
         if (License.active()) {
            ValenceHud.render(g);
            EmoteManager.renderHud(g);
         }
      });
      LOGGER.info("CycloneSkies {} ready", "1.9.9");
   }

   /** Shared by the {@code /cyclone randomcoords} forms: set, report, keep the offset in step. */
   private static void randomCoords(FabricClientCommandSource source, boolean on) {
      ValenceConfig.get().randomCoords = on;
      ValenceConfig.save();
      if (on) {
         RandomCoords.refresh();
      }

      String text = on
         ? "Random coords on: " + RandomCoords.offsetX() + ", " + RandomCoords.offsetZ()
         : "Random coords off: showing real coordinates";
      source.sendFeedback(class_2561.method_43470(text));
   }
}
