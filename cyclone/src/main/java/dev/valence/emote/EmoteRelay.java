package dev.valence.emote;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.valence.ValenceClient;
import dev.valence.config.ValenceConfig;
import dev.valence.cosmetics.Cosmetics;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.class_310;

/** Optional WebSocket relay connection for cross-client emote synchronization. */
public final class EmoteRelay {
   // Wispbyte can host the relay without TLS; use ws:// unless a TLS proxy is configured.
   private static final URI DEFAULT_URI = URI.create("ws://78.154.103.29:9469");
   private static final HttpClient HTTP = HttpClient.newBuilder()
         .connectTimeout(Duration.ofSeconds(8))
         .build();
   private static final AtomicReference<WebSocket> SOCKET = new AtomicReference<>();
   private static final StringBuilder FRAGMENTS = new StringBuilder();
   private static final AtomicInteger FAILED_ATTEMPTS = new AtomicInteger();
   private static volatile boolean connecting;
   private static volatile long nextConnectAtMs;
   private static volatile long lastFailureWarningAtMs;
   private static volatile String lastEquipped = "";

   private EmoteRelay() {
   }

   public static void connect() {
      ValenceConfig config = ValenceConfig.get();
      class_310 client = class_310.method_1551();
      long now = System.currentTimeMillis();
      if (!config.emoteRelayEnabled || config.emoteRelayToken == null || config.emoteRelayToken.isBlank()
            || client.field_1724 == null || connecting || SOCKET.get() != null || now < nextConnectAtMs) {
         return;
      }
      connecting = true;
      HTTP.newWebSocketBuilder().connectTimeout(Duration.ofSeconds(8))
            .buildAsync(DEFAULT_URI, new Listener())
            .whenComplete((socket, error) -> {
               connecting = false;
               if (error != null) {
                  scheduleRetry(error);
               }
            });
   }

   private static void scheduleRetry(Throwable error) {
      int failures = FAILED_ATTEMPTS.updateAndGet(previous -> Math.min(previous + 1, 10));
      long delayMs = Math.min(60_000L, 2_000L << Math.min(failures - 1, 5));
      nextConnectAtMs = System.currentTimeMillis() + delayMs;
      long now = System.currentTimeMillis();
      if (now - lastFailureWarningAtMs >= 30_000L) {
         lastFailureWarningAtMs = now;
         ValenceClient.LOGGER.warn("Emote relay connection failed (retrying in {}s): {}", delayMs / 1000L, rootCause(error));
      } else {
         ValenceClient.LOGGER.debug("Emote relay connection failed; retrying in {}s: {}", delayMs / 1000L, rootCause(error));
      }
   }

   private static String rootCause(Throwable error) {
      Throwable cause = error;
      while (cause.getCause() != null && cause.getCause() != cause) {
         cause = cause.getCause();
      }
      return cause.toString();
   }

   public static void sendStart(int id) {
      Emotes.Emote emote = Emotes.get(id);
      class_310 client = class_310.method_1551();
      if (emote == null || client.field_1724 == null) return;
      JsonObject message = new JsonObject();
      message.addProperty("type", "start");
      message.addProperty("emote", emote.id());
      message.addProperty("started_at_ms", System.currentTimeMillis());
      send(message);
   }

   public static void sendStop() {
      JsonObject message = new JsonObject();
      message.addProperty("type", "stop");
      send(message);
   }

   public static void requestSync() {
      JsonObject message = new JsonObject();
      message.addProperty("type", "sync");
      send(message);
   }

   public static void tickCosmetics() {
      class_310 client = class_310.method_1551();
      if (client.field_1724 == null || SOCKET.get() == null) {
         lastEquipped = "";
         return;
      }
      Cosmetics.Equipped equipped = Cosmetics.localEquipped();
      String key = equipped.hat() + "|" + equipped.wings() + "|" + equipped.cape();
      if (key.equals(lastEquipped)) return;
      lastEquipped = key;
      JsonObject message = new JsonObject();
      message.addProperty("type", "cosmetics");
      message.addProperty("hat", equipped.hat());
      message.addProperty("wings", equipped.wings());
      message.addProperty("cape", equipped.cape());
      send(message);
   }

   private static void send(JsonObject event) {
      WebSocket socket = SOCKET.get();
      if (socket != null && !socket.isOutputClosed()) {
         socket.sendText(event.toString(), true).exceptionally(error -> {
            ValenceClient.LOGGER.debug("Emote relay send failed: {}", error.toString());
            return null;
         });
      }
   }

   private static void handle(String text) {
      try {
         JsonObject message = JsonParser.parseString(text).getAsJsonObject();
         String type = message.has("type") ? message.get("type").getAsString() : "";
         if ("ready".equals(type)) {
            EmoteManager.clearRemote();
            Cosmetics.clearRemote();
            JsonArray active = message.has("active") ? message.getAsJsonArray("active") : new JsonArray();
            for (int i = 0; i < active.size(); i++) apply(active.get(i).getAsJsonObject());
            JsonArray cosmetics = message.has("cosmetics") ? message.getAsJsonArray("cosmetics") : new JsonArray();
            for (int i = 0; i < cosmetics.size(); i++) applyCosmetics(cosmetics.get(i).getAsJsonObject());
         } else if ("start".equals(type) || "stop".equals(type)) {
            apply(message);
         } else if ("cosmetics".equals(type)) {
            applyCosmetics(message);
         } else if ("error".equals(type)) {
            String reason = message.has("reason") ? message.get("reason").getAsString() : "unknown rejection";
            scheduleRetry(new IllegalStateException("relay rejected join: " + reason));
         }
      } catch (RuntimeException error) {
         ValenceClient.LOGGER.debug("Ignoring invalid emote relay event: {}", error.toString());
      }
   }

   private static void applyCosmetics(JsonObject message) {
      if (!message.has("player_id")) return;
      try {
         UUID id = UUID.fromString(message.get("player_id").getAsString());
         class_310 client = class_310.method_1551();
         if (client.field_1724 != null && id.equals(client.field_1724.method_5667())) return;
         String hat = message.has("hat") ? message.get("hat").getAsString() : "";
         String wings = message.has("wings") ? message.get("wings").getAsString() : "";
         String cape = message.has("cape") ? message.get("cape").getAsString() : "";
         client.execute(() -> Cosmetics.remoteEquipped(id, hat, wings, cape));
      } catch (RuntimeException ignored) {
      }
   }

   private static void apply(JsonObject message) {
      if (!message.has("player_id")) return;
      UUID id;
      try {
         id = UUID.fromString(message.get("player_id").getAsString());
      } catch (IllegalArgumentException error) {
         return;
      }
      String type = message.has("type") ? message.get("type").getAsString() : "";
      if ("stop".equals(type)) {
         class_310.method_1551().execute(() -> EmoteManager.remoteStop(id));
         return;
      }
      String emoteId = message.has("emote") ? message.get("emote").getAsString() : "";
      int index = -1;
      for (int i = 0; i < Emotes.ALL.size(); i++) {
         if (Emotes.ALL.get(i).id().equals(emoteId)) {
            index = i;
            break;
         }
      }
      if (index < 0) return;
      long start = message.has("started_at_ms") ? message.get("started_at_ms").getAsLong() : System.currentTimeMillis();
      int emoteIndex = index;
      class_310.method_1551().execute(() -> EmoteManager.remoteStart(id, emoteIndex, start));
   }

   private static final class Listener implements WebSocket.Listener {
      @Override
      public void onOpen(WebSocket webSocket) {
         FAILED_ATTEMPTS.set(0);
         nextConnectAtMs = 0L;
         SOCKET.set(webSocket);
         ValenceConfig config = ValenceConfig.get();
         class_310 client = class_310.method_1551();
         if (client.field_1724 == null) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "not in world");
            SOCKET.compareAndSet(webSocket, null);
            return;
         }
         JsonObject join = new JsonObject();
         join.addProperty("type", "join");
         join.addProperty("token", config.emoteRelayToken);
         join.addProperty("room", config.emoteRelayRoom == null || config.emoteRelayRoom.isBlank() ? "public" : config.emoteRelayRoom);
         join.addProperty("player_id", client.field_1724.method_5667().toString());
         // Use the authenticated account name, not a server-decorated display component.
         String displayName = dev.valence.cord.util.Fx.myName();
         if (displayName == null) {
            displayName = "Player";
         }
         displayName = displayName.replaceAll("[^A-Za-z0-9_]", "");
         if (displayName.isBlank() || displayName.length() > 32) {
            displayName = "Player";
         }
         join.addProperty("name", displayName);
         webSocket.sendText(join.toString(), true);
         webSocket.request(1);
      }

      @Override
      public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
         synchronized (FRAGMENTS) {
            FRAGMENTS.append(data);
            if (last) {
               handle(FRAGMENTS.toString());
               FRAGMENTS.setLength(0);
            }
         }
         webSocket.request(1);
         return null;
      }

      @Override
      public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
         SOCKET.compareAndSet(webSocket, null);
         nextConnectAtMs = Math.max(nextConnectAtMs, System.currentTimeMillis() + 2_000L);
         EmoteManager.clearRemote();
         Cosmetics.clearRemote();
         lastEquipped = "";
         return null;
      }

      @Override
      public void onError(WebSocket webSocket, Throwable error) {
         SOCKET.compareAndSet(webSocket, null);
         scheduleRetry(error);
      }
   }
}
