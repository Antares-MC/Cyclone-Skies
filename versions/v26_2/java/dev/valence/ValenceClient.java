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
import net.minecraft.client.KeyMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class ValenceClient implements ClientModInitializer {
    public static final String MOD_ID = "cycloneskies";
    public static final String VERSION = "1.9.9";
    public static final Logger LOGGER = LoggerFactory.getLogger("CycloneSkies");
    public static KeyMapping OPEN_MENU;
    public static KeyMapping MEDIA_NEXT;
    public static KeyMapping MEDIA_PREV;
    public static KeyMapping MEDIA_PLAY;
    public static KeyMapping EMOTE;
    private static boolean licenseAsked;

    @Override
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
        ClientTickEvents.END_CLIENT_TICK.register((EndTick) client -> {
            while (EMOTE.consumeClick()) {
                if (client.gui.screen() == null && client.player != null && License.active()) {
                    client.gui.setScreen(new EmoteWheelScreen(EMOTE));
                }
            }

            EmoteManager.tick(client);

            while (OPEN_MENU.consumeClick()) {
                if (client.gui.screen() == null) {
                    client.gui.setScreen(License.active() ? new ValenceScreen(null) : new LicenseScreen(null, true));
                }
            }

            if (!licenseAsked
                    && !License.active()
                    && client.level != null
                    && client.player != null
                    && client.gui.screen() == null
                    && client.gui.overlay() == null) {
                licenseAsked = true;
                client.gui.setScreen(new LicenseScreen(null, false));
            }

            LyricsBridge.tick();

            while (MEDIA_NEXT.consumeClick()) {
                SpotifyClient.control(SpotifyClient.Cmd.NEXT);
            }

            while (MEDIA_PREV.consumeClick()) {
                SpotifyClient.control(SpotifyClient.Cmd.PREVIOUS);
            }

            while (MEDIA_PLAY.consumeClick()) {
                SpotifyClient.control(SpotifyClient.Cmd.PLAYPAUSE);
            }
        });
        Hud.afterSubtitles(net.minecraft.resources.Identifier.fromNamespaceAndPath("valence", "hud"), g -> {
            if (License.active()) {
                ValenceHud.render(g);
                EmoteManager.renderHud(g);
            }
        });
        LOGGER.info("CycloneSkies {} ready", VERSION);
    }
}
