package com.paymentalerts.skin;

import com.google.common.collect.LinkedHashMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.paymentalerts.PaymentAlertsClient;
import com.paymentalerts.config.PaymentAlertsConfig;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.player.PlayerSkin;

public final class SkinFetcher {
    private static final Pattern VALID_NAME = Pattern.compile("^[A-Za-z0-9_]{1,16}$");
    private static final Map<String, Skin> CACHE = new ConcurrentHashMap();
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).followRedirects(HttpClient.Redirect.NORMAL).build();

    private SkinFetcher() {
    }

    public static final class Skin implements Supplier<PlayerSkin> {
        private volatile Supplier<PlayerSkin> source;
        private volatile GameProfile profile;

        private Skin(String str) {
            this.profile = new GameProfile(UUIDUtil.createOfflinePlayerUUID(str), str);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.function.Supplier
        public PlayerSkin get() {
            Supplier<PlayerSkin> supplier = this.source;
            if (supplier == null) {
                return null;
            }
            return supplier.get();
        }

        public GameProfile profile() {
            return this.profile;
        }
    }

    public static Skin get(String str) {
        return CACHE.computeIfAbsent(str.toLowerCase(Locale.ROOT), str2 -> {
            return load(str);
        });
    }

    public static void clearCache() {
        CACHE.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Skin load(String str) {
        Skin skin = new Skin(str);
        PlayerInfo findInTabList = findInTabList(str);
        if (findInTabList != null) {
            skin.profile = findInTabList.getProfile();
            Objects.requireNonNull(findInTabList);
            skin.source = findInTabList::getSkin;
            return skin;
        }
        if (PaymentAlertsConfig.get().useMojangApiForSkins && VALID_NAME.matcher(str).matches()) {
            fetchFromMojang(str).thenAccept(optional -> {
                optional.ifPresent(gameProfile -> {
                    Minecraft minecraft = Minecraft.getInstance();
                    minecraft.execute(() -> {
                        skin.profile = gameProfile;
                        skin.source = minecraft.getSkinManager().createLookup(gameProfile, false);
                    });
                });
            });
        }
        return skin;
    }

    private static PlayerInfo findInTabList(String str) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) {
            return null;
        }
        PlayerInfo playerInfo = connection.getPlayerInfo(str);
        return playerInfo != null ? playerInfo : connection.getPlayerInfoIgnoreCase(str);
    }

    private static CompletableFuture<Optional<GameProfile>> fetchFromMojang(String str) {
        String encode = URLEncoder.encode(str, StandardCharsets.UTF_8);
        return getJson("https://api.mojang.com/users/profiles/minecraft/" + encode).thenCompose(optional -> {
            if (optional.isPresent()) {
                return CompletableFuture.completedFuture(optional);
            }
            return getJson("https://api.minecraftservices.com/minecraft/profile/lookup/name/" + encode);
        }).thenCompose(optional2 -> {
            if (optional2.isEmpty() || !optional2.get().has("id")) {
                PaymentAlertsClient.LOGGER.info("No Mojang account found for {}, using a default skin", str);
                return CompletableFuture.completedFuture(Optional.<GameProfile>empty());
            }
            String asString = optional2.get().get("id").getAsString();
            return getJson("https://sessionserver.mojang.com/session/minecraft/profile/" + asString + "?unsigned=false").thenApply(optional3 -> {
                return optional3.map(jsonObject -> {
                    return toProfile(jsonObject, asString, str);
                });
            });
        }).exceptionally(th -> {
            PaymentAlertsClient.LOGGER.warn("Skin lookup for {} failed: {}", str, th.toString());
            return Optional.empty();
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static GameProfile toProfile(JsonObject jsonObject, String str, String str2) {
        String asString = jsonObject.has("name") ? jsonObject.get("name").getAsString() : str2;
        LinkedHashMultimap create = LinkedHashMultimap.create();
        if (jsonObject.has("properties") && jsonObject.get("properties").isJsonArray()) {
            Iterator it = jsonObject.getAsJsonArray("properties").iterator();
            while (it.hasNext()) {
                JsonObject asJsonObject = ((JsonElement) it.next()).getAsJsonObject();
                if ("textures".equals(asJsonObject.get("name").getAsString())) {
                    create.put("textures", new Property("textures", asJsonObject.get("value").getAsString(), asJsonObject.has("signature") ? asJsonObject.get("signature").getAsString() : null));
                }
            }
        }
        return new GameProfile(parseUndashed(str), asString, new PropertyMap(create));
    }

    private static CompletableFuture<Optional<JsonObject>> getJson(String str) {
        return HTTP.sendAsync(HttpRequest.newBuilder(URI.create(str)).timeout(Duration.ofSeconds(8L)).header("Accept", "application/json").header("User-Agent", "PaymentAlerts-Minecraft-Mod/1.0").GET().build(), HttpResponse.BodyHandlers.ofString()).thenApply(httpResponse -> {
            if (httpResponse.statusCode() == 200) {
                return Optional.of(JsonParser.parseString((String) httpResponse.body()).getAsJsonObject());
            }
            return Optional.<JsonObject>empty();
        }).exceptionally(th -> {
            return Optional.<JsonObject>empty();
        });
    }

    private static UUID parseUndashed(String str) {
        String replace = str.replace("-", "");
        return new UUID(Long.parseUnsignedLong(replace.substring(0, 16), 16), Long.parseUnsignedLong(replace.substring(16, 32), 16));
    }
}
