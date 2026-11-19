package dev.valence.emote;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.valence.ValenceClient;
import dev.valence.config.ValenceConfig;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.class_310;

/** Optional WebSocket relay connection for cross-client emote synchronization. */
public final class EmoteRelay {
   private static final URI DEFAULT_URI = URI.create("wss://78.154.103.29:9469");
   private static final HttpClient HTTP = HttpClient.newBuilder()
         .connectTimeout(Duration.ofSeconds(8))
         .build();
   private static final AtomicReference<WebSocket> SOCKET = new AtomicReference<>();
   private static final StringBuilder FRAGMENTS = new StringBuilder();
   private static volatile boolean connecting;

   private EmoteRelay() {
   }

   public static void connect() {
      ValenceConfig config = ValenceConfig.get();
      if (!config.emoteRelayEnabled || config.emoteRelayToken == null || config.emoteRelayToken.isBlank()
            || connecting || SOCKET.get() != null) {
         return;
      }
      connecting = true;
      HTTP.newWebSocketBuilder().connectTimeout(Duration.ofSeconds(8))
            .buildAsync(DEFAULT_URI, new Listener())
            .whenComplete((socket, error) -> {
               connecting = false;
               if (error != null) {
                  ValenceClient.LOGGER.warn("Emote relay connection failed: {}", error.toString());
               }
            });
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
            JsonArray active = message.has("active") ? message.getAsJsonArray("active") : new JsonArray();
            for (int i = 0; i < active.size(); i++) apply(active.get(i).getAsJsonObject());
         } else if ("start".equals(type) || "stop".equals(type)) {
            apply(message);
         } else if ("error".equals(type)) {
            ValenceClient.LOGGER.warn("Emote relay rejected connection: {}", message.get("reason").getAsString());
         }
      } catch (RuntimeException error) {
         ValenceClient.LOGGER.debug("Ignoring invalid emote relay event: {}", error.toString());
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
         EmoteManager.remoteStop(id);
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
         join.addProperty("name", client.field_1724.method_5667().toString());
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
         EmoteManager.clearRemote();
         return null;
      }

      @Override
      public void onError(WebSocket webSocket, Throwable error) {
         SOCKET.compareAndSet(webSocket, null);
         ValenceClient.LOGGER.debug("Emote relay socket error: {}", error.toString());
      }
   }
}
