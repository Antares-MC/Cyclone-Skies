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
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
import net.minecraft.class_310;

@Environment(EnvType.CLIENT)
public final class LiveNotifier {
   private static final String LIVE_URL = "https://www.youtube.com/@CycloneLwk/live";
   private static long lastPoll = 0L;
   private static boolean wasLive = false;

   private LiveNotifier() {
   }

   public static void init() {
      ClientTickEvents.END_CLIENT_TICK.register(client -> {
         if (client.field_1687 == null || client.field_1724 == null) return;
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
            synchronized (LiveNotifier.class) {
               wasLive = false;
            }
            return;
         }
         String title = extractTitle(html);
         String videoId = extractVideoId(html);
         synchronized (LiveNotifier.class) {
            if (wasLive) return;
            wasLive = true;
         }
         // Post once on the main thread for each observed offline-to-live transition.
         class_310.method_1551().execute(() -> notifyLive(title, videoId));
      } catch (Exception ignored) {
         // silent on network errors
      }
   }

   private static void notifyLive(String title, String videoId) {
      String msg = "Cyclone is LIVE: " + (title != null ? title : "Live Stream");
      String url = videoId != null ? "https://www.youtube.com/watch?v=" + videoId : LIVE_URL;
      class_310 mc = class_310.method_1551();
      if (mc.field_1705 != null) {
         class_5250 prefix = class_2561.method_43470("[CycloneSkies] ").method_27692(class_124.field_1075);
         class_2561 link = class_2561.method_43470(msg + " [Open Stream]")
            .method_27692(class_124.field_1060)
            .method_27696(Links.openUrl(url));
         mc.field_1705.method_1743().method_1812(prefix.method_10852(link));
      }
      Fx.info("LIVE: " + (title != null ? title : "Stream started"));
      // Trigger the same broadcast takeover used by chat-based stream announcements.
      dev.valence.pay.PaymentsBridge.stream("Cyclone", title != null ? title : "is live now");
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
