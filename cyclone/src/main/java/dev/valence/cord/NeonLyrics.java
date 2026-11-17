package dev.valence.cord;

import dev.valence.ValenceClient;
import dev.valence.compat.Hud;
import dev.valence.compat.Keys;
import dev.valence.compat.WorldRenderHook;
import dev.valence.cord.config.Config;
import dev.valence.cord.config.Storage;
import dev.valence.cord.cosmetic.DeathFx;
import dev.valence.cord.hud.BeatGlow;
import dev.valence.cord.hud.Crosshair;
import dev.valence.cord.hud.GlassHotbar;
import dev.valence.cord.hud.Island;
import dev.valence.cord.hud.StartupIntro;
import dev.valence.cord.lyrics.Favorites;
import dev.valence.cord.lyrics.SpotifyClient;
import dev.valence.cord.sky.SkyRenderer;
import dev.valence.cord.sky.WorldSky;
import dev.valence.cord.util.AutoPerf;
import dev.valence.cord.util.Fx;
import dev.valence.cord.util.Profiles;
import dev.valence.license.License;
import dev.valence.lyrics.SkyLyrics;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.class_1269;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675.class_307;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public final class NeonLyrics {
   public static final String MOD_ID = "valence_cord";
   public static final Logger LOG = ValenceClient.LOGGER;
   private static Config config = new Config();
   public static class_304 keyLyrics;
   public static class_304 keySync;
   public static class_304 keyFav;
   public static class_304 keyClip;
   public static class_304 keyProfile;
   private static boolean loaded;

   private NeonLyrics() {
   }

   public static Config cfg() {
      return config;
   }

   public static void saveConfig() {
      if (loaded) {
         config.clamp();
         Storage.save("cord.json", config);
      }
   }

   public static void init() {
      Storage.migrateFromLyricsCord();
      config = Storage.load("cord.json", Config.class, Config::new);
      loaded = true;
      config.clamp();
      saveConfig();
      keyFav = Keys.bind("key.valence.fav", 78);
      keyClip = Keys.bind("key.valence.clip", 299);
      keyProfile = Keys.bind("key.valence.profile", 297);
      keySync = Keys.bind("key.valence.sync", -1);
      keyLyrics = Keys.bind("key.valence.lyrics", -1);
      Hud.addFirst(class_2960.method_60655("valence_cord", "sky"), gated(SkyRenderer::render));
      Hud.addLast(class_2960.method_60655("valence_cord", "island"), gated(Island::render));
      Hud.addLast(class_2960.method_60655("valence_cord", "screen_lyrics"), gated(SkyLyrics::renderScreen));
      Hud.replaceHotbar(v -> gatedOr(GlassHotbar.wrap(v), v));
      Hud.replaceCrosshair(v -> gatedOr(Crosshair.wrap(v), v));
      Hud.addLast(class_2960.method_60655("valence_cord", "beatglow"), gated(BeatGlow::render));
      WorldRenderHook.afterEntities(WorldSky::render);
      WorldRenderHook.endMain(WorldSky::renderLyrics);
      dev.valence.cosmetics.CosmeticRender.register();
      SpotifyClient.init();
      StartupIntro.register();
      AttackEntityCallback.EVENT.register((AttackEntityCallback)(player, level, hand, entity, hit) -> {
         DeathFx.onAttack(entity);
         return class_1269.field_5811;
      });
      ClientTickEvents.END_CLIENT_TICK.register(NeonLyrics::tick);
   }

   private static void tick(class_310 mc) {
      AutoPerf.tick(mc);
      DeathFx.tick(mc);
      SkyRenderer.tick(mc);

      while (keySync.method_1436()) {
         SpotifyClient.syncHere();
      }

      while (keyFav.method_1436()) {
         Favorites.saveCurrent();
      }

      while (keyClip.method_1436()) {
         mc.field_1690.field_1842 = !mc.field_1690.field_1842;
      }

      while (keyProfile.method_1436()) {
         Profiles.toggle();
      }

      while (keyLyrics.method_1436()) {
         config.lyricsSky = !config.lyricsSky;
         saveConfig();
         Island.notify(Fx.t(config.lyricsSky ? "neonlyrics.media.lyricson" : "neonlyrics.media.lyricsoff"), config.lyricsSky);
      }
   }

   private static Hud.Element gated(Hud.Element e) {
      return g -> {
         if (License.active()) {
            e.render(g);
         }
      };
   }

   private static Hud.Element gatedOr(Hud.Element mine, Hud.Element vanilla) {
      return g -> (License.active() ? mine : vanilla).render(g);
   }
}
