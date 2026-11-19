package dev.valence.cosmetics;

import dev.valence.config.ValenceConfig;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** Selection state for the 3D cosmetics. */
@Environment(EnvType.CLIENT)
public final class Cosmetics {
   private static final Map<UUID, Equipped> REMOTE = new ConcurrentHashMap<>();

   public record Equipped(String hat, String wings, String cape) {
      public Equipped {
         hat = validHat(hat);
         wings = validWings(wings);
         cape = validCape(cape);
      }
   }

   private Cosmetics() {
   }

   public static Hats.Hat hat() {
      return Hats.byId(ValenceConfig.get().hat);
   }

   public static Wings.Wing wings() {
      return Wings.byId(ValenceConfig.get().wings);
   }

   public static void selectHat(Hats.Hat hat) {
      ValenceConfig.get().hat = hat == null ? "" : hat.id();
   }

   public static void selectWings(Wings.Wing wing) {
      ValenceConfig.get().wings = wing == null ? "" : wing.id();
   }

   public static Equipped localEquipped() {
      ValenceConfig config = ValenceConfig.get();
      return new Equipped(config.hat, config.wings, config.cape);
   }

   public static void remoteEquipped(UUID playerId, String hat, String wings, String cape) {
      if (playerId == null) return;
      Equipped equipped = new Equipped(hat, wings, cape);
      if (equipped.hat().isEmpty() && equipped.wings().isEmpty() && equipped.cape().isEmpty()) {
         REMOTE.remove(playerId);
      } else {
         REMOTE.put(playerId, equipped);
      }
   }

   public static Equipped remoteEquipped(UUID playerId) {
      return playerId == null ? null : REMOTE.get(playerId);
   }

   public static void clearRemote() {
      REMOTE.clear();
   }

   public static String validHat(String id) {
      return Hats.byId(id) == null ? "" : id;
   }

   public static String validWings(String id) {
      return Wings.byId(id) == null ? "" : id;
   }

   public static String validCape(String id) {
      return Capes.byId(id) == null ? "" : id;
   }

   public static boolean any() {
      return hat() != null || wings() != null;
   }
}
