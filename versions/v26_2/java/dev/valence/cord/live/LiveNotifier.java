package dev.valence.cord.live;

import dev.valence.compat.Links;
import dev.valence.cord.NeonLyrics;
import dev.valence.cord.config.Config;
import dev.valence.cord.util.Fx;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public final class LiveNotifier {
   private static final String LIVE_URL = "https://www.youtube.com/@CycloneLwk/live";
   private static long lastPoll = 0L;
   private static String lastNotified = "";
   private static boolean wasLive = false;

   private LiveNotifier() {
   }

   public static void init() {
      ClientTickEvents.END_CLIENT_TICK.register(client -> {
         if (client.level == null || client.player == null) return;
         Config c = NeonLyrics.cfg();
         if (c == null || !c.liveNotify) return;
         long now = System.currentTimeMillis();
         int interval = Math.max(15, c.livePollSeconds) * 1000;
         if (now - lastPoll < interval) return;
         lastPoll = now;
         // Poll in background so we don't freeze the game
         Thread.ofVirtual().start(() -> poll(c));
      });
   }

   private static void poll(Config c) {
      try {
         String html = fetch(LIVE_URL);
         if (html == null || html.isEmpty()) return;
         boolean live = html.contains("isLiveBroadcast") || html.contains("\"isLiveNow\":true")
            || (html.contains("canonical") && html.contains("/watch?v="));
         if (!live) {
            wasLive = false;
            return;
         }
         String title = extractTitle(html);
         String videoId = extractVideoId(html);
         String key = videoId != null ? videoId : title;
         if (key == null || key.isEmpty() || key.equals(lastNotified)) return;
         lastNotified = key;
         wasLive = true;
         // Post on main thread
         net.minecraft.client.Minecraft.getInstance().execute(() -> notifyLive(title, videoId));
      } catch (Exception ignored) {
         // silent on network errors
      }
   }

   private static void notifyLive(String title, String videoId) {
      String msg = "Cyclone is LIVE: " + (title != null ? title : "Live Stream");
      String url = videoId != null ? "https://www.youtube.com/watch?v=" + videoId : LIVE_URL;
      net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
      if (mc.gui != null) {
         net.minecraft.network.chat.MutableComponent prefix = net.minecraft.network.chat.Component.literal("[CycloneSkies] ").withStyle(net.minecraft.ChatFormatting.AQUA);
         net.minecraft.network.chat.Component link = net.minecraft.network.chat.Component.literal(msg + " [Open Stream]")
            .withStyle(net.minecraft.ChatFormatting.GREEN)
            .withStyle(Links.openUrl(url));
         mc.gui.hud.getChat().addClientSystemMessage(prefix.append(link));
      }
      Fx.info("LIVE: " + (title != null ? title : "Stream started"));
      // Sky notification
      dev.valence.cord.sky.FloatWords.showLiveAnnouncement("LIVE: " + (title != null ? title : "Cyclone"));
      NeonLyrics.LOG.info("Cyclone live detected: {} | {}", title, url);
   }

   private static String fetch(String url) throws Exception {
      HttpURLConnection c = (HttpURLConnection) URI.create(url).toURL().openConnection();
      c.setInstanceFollowRedirects(true);
      c.setConnectTimeout(8000);
      c.setReadTimeout(10000);
      c.setRequestProperty("User-Agent", "Mozilla/5.0 (CycloneSkies LiveNotifier)");
      c.setRequestProperty("Accept", "text/html");
      try (InputStream in = c.getInputStream()) {
         ByteArrayOutputStream bo = new ByteArrayOutputStream();
         in.transferTo(bo);
         return bo.toString(StandardCharsets.UTF_8);
      } finally {
         c.disconnect();
      }
   }

   private static String extractTitle(String html) {
      Matcher m = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(html);
      if (m.find()) {
         String t = m.group(1).replaceAll("\\s+", " ").trim();
         // YouTube titles often end with " - YouTube"
         if (t.endsWith(" - YouTube")) t = t.substring(0, t.length() - 10).trim();
         return t;
      }
      return null;
   }

   private static String extractVideoId(String html) {
      Matcher m = Pattern.compile("watch\\?v=([a-zA-Z0-9_-]{11})").matcher(html);
      if (m.find()) return m.group(1);
      m = Pattern.compile("\"videoId\":\"([a-zA-Z0-9_-]{11})\"").matcher(html);
      if (m.find()) return m.group(1);
      return null;
   }
}
