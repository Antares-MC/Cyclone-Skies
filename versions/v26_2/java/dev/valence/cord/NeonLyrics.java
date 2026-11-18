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
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.Identifier;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants.Type;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public final class NeonLyrics {
   public static final String MOD_ID = "valence_cord";
   public static final Logger LOG = ValenceClient.LOGGER;
   private static Config config = new Config();
   public static net.minecraft.client.KeyMapping keyLyrics;
   public static net.minecraft.client.KeyMapping keySync;
   public static net.minecraft.client.KeyMapping keyFav;
   public static net.minecraft.client.KeyMapping keyClip;
   public static net.minecraft.client.KeyMapping keyProfile;
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
      Hud.addFirst(net.minecraft.resources.Identifier.fromNamespaceAndPath("valence_cord", "sky"), gated(SkyRenderer::render));
      Hud.addLast(net.minecraft.resources.Identifier.fromNamespaceAndPath("valence_cord", "island"), gated(Island::render));
      Hud.addLast(net.minecraft.resources.Identifier.fromNamespaceAndPath("valence_cord", "screen_lyrics"), gated(SkyLyrics::renderScreen));
      Hud.replaceHotbar(v -> gatedOr(GlassHotbar.wrap(v), v));
      Hud.replaceCrosshair(v -> gatedOr(Crosshair.wrap(v), v));
      Hud.addLast(net.minecraft.resources.Identifier.fromNamespaceAndPath("valence_cord", "beatglow"), gated(BeatGlow::render));
      WorldRenderHook.afterEntities(WorldSky::render);
      WorldRenderHook.endMain(WorldSky::renderLyrics);
      // 26.2 submits world geometry through the render hook, so the classic sky engine
      // and the sky-space lyric pass draw there instead of inside the vanilla sky render.
      WorldRenderHook.endMain(dev.valence.sky.ValenceSky::renderWorld);
      WorldRenderHook.endMain(dev.valence.lyrics.SkyLyrics::renderInSkyWorld);
      dev.valence.cosmetics.CosmeticRender.register();
      SpotifyClient.init();
      StartupIntro.register();
      AttackEntityCallback.EVENT.register((AttackEntityCallback)(player, level, hand, entity, hit) -> {
         DeathFx.onAttack(entity);
         return net.minecraft.world.InteractionResult.PASS;
      });
      ClientTickEvents.END_CLIENT_TICK.register(NeonLyrics::tick);
   }

   private static void tick(net.minecraft.client.Minecraft mc) {
      AutoPerf.tick(mc);
      DeathFx.tick(mc);
      SkyRenderer.tick(mc);

      while (keySync.consumeClick()) {
         SpotifyClient.syncHere();
      }

      while (keyFav.consumeClick()) {
         Favorites.saveCurrent();
      }

      while (keyClip.consumeClick()) {
         mc.gui.hud.toggle();
      }

      while (keyProfile.consumeClick()) {
         Profiles.toggle();
      }

      while (keyLyrics.consumeClick()) {
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
