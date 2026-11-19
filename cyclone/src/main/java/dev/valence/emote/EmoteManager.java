package dev.valence.emote;

import dev.valence.license.License;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_1297;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_332;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_5498;
import net.minecraft.class_746;

@Environment(EnvType.CLIENT)
public final class EmoteManager {
   private static final long FADE_IN = 350000000L;
   private static final long FADE_OUT = 320000000L;
   private static int active = -1;
   private static long startNanos;
   private static long stopNanos;
   private static class_5498 savedCamera;
   private static boolean camera;
   private static float camYaw;
   private static float camPitch;
   private static float camDist;
   private static int lastFlash = -1;
   private static float lastHealth = -1.0F;
   private static int preview = -1;
   private static long previewStart;
   private static final Map<UUID, RemoteEmote> REMOTE = new ConcurrentHashMap<>();

   private record RemoteEmote(int id, long startedAtMillis) {
   }

   private EmoteManager() {
   }

   public static boolean playing() {
      return active >= 0 && stopNanos == 0L;
   }

   /**
    * Drops the emote and puts the camera back, without any of the fade-out bookkeeping.
    *
    * <p>Used when the state cannot be trusted any more -- a world change, a disconnect, or
    * another screen taking over input -- because leaving {@code active} set there would keep
    * the camera pinned to a player who is no longer being drawn, or leave the view stuck in
    * third person.
    */
   public static void hardReset() {
      restoreCamera();
      if (active >= 0 && stopNanos == 0L) {
         EmoteRelay.sendStop();
      }
      active = -1;
      stopNanos = 0L;
      REMOTE.clear();
      dev.valence.cosmetics.Cosmetics.clearRemote();
   }

   public static void start(int id) {
      class_310 mc = class_310.method_1551();
      class_746 p = mc.field_1724;
      if (mc.field_1755 == null
         && p != null
         && Emotes.get(id) != null
         && !p.method_5765()
         && !p.method_6113()
         && !p.method_6128()
         && !p.method_5681()) {
         if (!camera) {
            savedCamera = mc.field_1690.method_31044();
         }

         active = id;
         startNanos = System.nanoTime();
         EmoteRelay.sendStart(id);
         stopNanos = 0L;
         camera = true;
         camYaw = p.method_36454() + 180.0F + 25.0F;
         camPitch = 12.0F;
         camDist = 1.4F;
         lastFlash = -1;
         lastHealth = p.method_6032();
         mc.field_1690.method_31043(class_5498.field_26665);
      }
   }

   public static void stop() {
      if (active >= 0 && stopNanos == 0L) {
         stopNanos = System.nanoTime();
         EmoteRelay.sendStop();
         restoreCamera();
      }
   }

   private static void restoreCamera() {
      if (camera) {
         camera = false;
         class_310.method_1551().field_1690.method_31043(savedCamera == null ? class_5498.field_26664 : savedCamera);
      }
   }

   public static void tick(class_310 mc) {
      EmoteRelay.connect();
      EmoteRelay.tickCosmetics();
      if (active >= 0) {
         // A screen takes the keyboard, so the player cannot walk out of the pose and the emote
         // would sit there holding the camera with nothing able to end it. Drop it instead.
         if (mc.field_1755 != null) {
            hardReset();
            return;
         }

         class_746 p = mc.field_1724;
         if (stopNanos != 0L) {
            if (System.nanoTime() - stopNanos > 320000000L) {
               active = -1;
               stopNanos = 0L;
            }
         } else if (p != null && mc.field_1687 != null && p.method_5805() && !p.method_5765() && !p.method_6113() && License.active()) {
            class_315 o = mc.field_1690;
            boolean moving = o.field_1894.method_1434()
               || o.field_1881.method_1434()
               || o.field_1913.method_1434()
               || o.field_1849.method_1434()
               || o.field_1903.method_1434()
               || o.field_1832.method_1434()
               || o.field_1886.method_1434()
               || o.field_1904.method_1434();
            boolean hurt = p.method_6032() < lastHealth - 0.01F;
            lastHealth = p.method_6032();
            if (!moving && !hurt && !p.method_5681() && !p.method_6128()) {
               if (o.method_31044() != class_5498.field_26665) {
                  o.method_31043(class_5498.field_26665);
               }

               Emotes.Emote e = Emotes.get(active);
               if (e != null && "caught4k".equals(e.id())) {
                  float t = time();
                  int flash = t < 0.6F ? -1 : (int)((t - 0.6F) / 1.8F);
                  if (flash != lastFlash && flash >= 0) {
                     lastFlash = flash;
                     mc.method_1483().method_4873(class_1109.method_4757((class_3414)class_3417.field_15015.comp_349(), 1.9F, 1.0F));
                     mc.method_1483().method_4873(class_1109.method_4757(class_3417.field_17484, 1.25F, 1.0F));
                     mc.field_1687.method_8486(
                           p.method_23317(), p.method_23318() + 1.6, p.method_23321(), class_3417.field_17484, class_3419.field_15248, 1.6F, 1.4F, false
                        );
                  }
               }
            } else {
               stop();
            }
         } else if (p == null || mc.field_1687 == null) {
            hardReset();
         } else {
            stop();
         }
      }
   }

   public static void renderHud(class_332 g) {
      Emotes.Emote e = Emotes.get(active);
      if (e != null && "caught4k".equals(e.id())) {
         float t = time();
         if (!(t < 0.6F)) {
            float cyc = (t - 0.6F) % 1.8F;
            float f = (float)Math.exp(-cyc * 9.0) * weight();
            if (f > 0.01F) {
               g.method_25294(0, 0, g.method_51421(), g.method_51443(), (int)(150.0F * f) << 24 | 16777215);
            }
         }
      }
   }

   public static float time() {
      return (float)(System.nanoTime() - startNanos) / 1.0E9F;
   }

   private static float weight() {
      long now = System.nanoTime();
      float in = Math.min(1.0F, (float)(now - startNanos) / 3.5E8F);
      float out = stopNanos == 0L ? 1.0F : Math.max(0.0F, 1.0F - (float)(now - stopNanos) / 3.2E8F);
      in = in * in * (3.0F - 2.0F * in);
      out = out * out * (3.0F - 2.0F * out);
      return Math.min(in, out);
   }

   public static void fill(class_1297 entity, EmoteState state) {
      if (entity != class_310.method_1551().field_1724) {
         UUID playerId = entity.method_5667();
         RemoteEmote remote = REMOTE.get(playerId);
         if (remote != null) {
            float elapsed = Math.max(0f, (System.currentTimeMillis() - remote.startedAtMillis()) / 1000f);
            if (elapsed < 120f && Emotes.get(remote.id()) != null) {
               state.valence$setEmoteOverride(remote.id(), elapsed, 1f);
               return;
            }
            REMOTE.remove(playerId, remote);
         }
      }
      if (preview >= 0) {
         state.valence$setEmote(preview, (float)(System.nanoTime() - previewStart) / 1.0E9F, 1.0F);
      } else if (active >= 0 && entity == class_310.method_1551().field_1724) {
         state.valence$setEmote(active, time(), weight());
      } else {
         state.valence$setEmote(-1, 0.0F, 0.0F);
      }
   }

   public static void beginPreview(int id, long since) {
      preview = id;
      previewStart = since;
   }

   public static void endPreview() {
      preview = -1;
   }

   public static void remoteStart(UUID playerId, int emoteId, long startedAtMillis) {
      class_310 client = class_310.method_1551();
      if (playerId != null && Emotes.get(emoteId) != null
            && (client.field_1724 == null || !playerId.equals(client.field_1724.method_5667()))) {
         REMOTE.put(playerId, new RemoteEmote(emoteId, startedAtMillis));
      }
   }

   public static void remoteStop(UUID playerId) {
      if (playerId != null) {
         REMOTE.remove(playerId);
      }
   }

   public static void clearRemote() {
      REMOTE.clear();
   }

   public static boolean cameraActive(class_1297 entity) {
      return camera && active >= 0 && entity == class_310.method_1551().field_1724;
   }

   public static boolean turn(class_1297 entity, double dx, double dy) {
      if (!cameraActive(entity)) {
         return false;
      }

      camYaw += (float)dx * 0.15F;
      camPitch = Math.max(-75.0F, Math.min(80.0F, camPitch + (float)dy * 0.15F));
      return true;
   }

   public static float camYaw() {
      return camYaw;
   }

   public static float camPitch() {
      return camPitch;
   }

   public static float camDistance(float target) {
      camDist = camDist + (target - camDist) * 0.12F;
      return camDist;
   }
}
