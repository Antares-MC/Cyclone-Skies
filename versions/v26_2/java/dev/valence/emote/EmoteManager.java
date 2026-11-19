package dev.valence.emote;

import dev.valence.license.License;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.client.CameraType;
import net.minecraft.client.player.LocalPlayer;

@Environment(EnvType.CLIENT)
public final class EmoteManager {
   private static final long FADE_IN = 350000000L;
   private static final long FADE_OUT = 320000000L;
   private static int active = -1;
   private static long startNanos;
   private static long stopNanos;
   private static net.minecraft.client.CameraType savedCamera;
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
      if (active >= 0 && stopNanos == 0L) EmoteRelay.sendStop();
      active = -1;
      stopNanos = 0L;
      REMOTE.clear();
   }

   public static void start(int id) {
      net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
      net.minecraft.client.player.LocalPlayer p = mc.player;
      if (mc.gui.screen() == null
         && p != null
         && Emotes.get(id) != null
         && !p.isPassenger()
         && !p.isSleeping()
         && !p.isFallFlying()
         && !p.isSwimming()) {
         if (!camera) {
            savedCamera = mc.options.getCameraType();
         }

         active = id;
         startNanos = System.nanoTime();
         EmoteRelay.sendStart(id);
         stopNanos = 0L;
         camera = true;
         camYaw = p.getYRot() + 180.0F + 25.0F;
         camPitch = 12.0F;
         camDist = 1.4F;
         lastFlash = -1;
         lastHealth = p.getHealth();
         mc.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_BACK);
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
         net.minecraft.client.Minecraft.getInstance().options.setCameraType(savedCamera == null ? net.minecraft.client.CameraType.FIRST_PERSON : savedCamera);
      }
   }

   public static void tick(net.minecraft.client.Minecraft mc) {
      EmoteRelay.connect();
      if (active >= 0) {
         // A screen takes the keyboard, so the player cannot walk out of the pose and the emote
         // would sit there holding the camera with nothing able to end it. Drop it instead.
         if (mc.gui.screen() != null) {
            hardReset();
            return;
         }

         net.minecraft.client.player.LocalPlayer p = mc.player;
         if (stopNanos != 0L) {
            if (System.nanoTime() - stopNanos > 320000000L) {
               active = -1;
               stopNanos = 0L;
            }
         } else if (p != null && mc.level != null && p.isAlive() && !p.isPassenger() && !p.isSleeping() && License.active()) {
            net.minecraft.client.Options o = mc.options;
            boolean moving = o.keyUp.isDown()
               || o.keyDown.isDown()
               || o.keyLeft.isDown()
               || o.keyRight.isDown()
               || o.keyJump.isDown()
               || o.keyShift.isDown()
               || o.keyAttack.isDown()
               || o.keyUse.isDown();
            boolean hurt = p.getHealth() < lastHealth - 0.01F;
            lastHealth = p.getHealth();
            if (!moving && !hurt && !p.isSwimming() && !p.isFallFlying()) {
               if (o.getCameraType() != net.minecraft.client.CameraType.THIRD_PERSON_BACK) {
                  o.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_BACK);
               }

               Emotes.Emote e = Emotes.get(active);
               if (e != null && "caught4k".equals(e.id())) {
                  float t = time();
                  int flash = t < 0.6F ? -1 : (int)((t - 0.6F) / 1.8F);
                  if (flash != lastFlash && flash >= 0) {
                     lastFlash = flash;
                     mc.getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI((net.minecraft.sounds.SoundEvent)net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(), 1.9F, 1.0F));
                     mc.getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1.25F, 1.0F));
                     mc.level.playLocalSound(
                           p.getX(), p.getY() + 1.6, p.getZ(), net.minecraft.sounds.SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, net.minecraft.sounds.SoundSource.PLAYERS, 1.6F, 1.4F, false
                        );
                  }
               }
            } else {
               stop();
            }
         } else if (p == null || mc.level == null) {
            hardReset();
         } else {
            stop();
         }
      }
   }

   public static void renderHud(net.minecraft.client.gui.GuiGraphicsExtractor g) {
      Emotes.Emote e = Emotes.get(active);
      if (e != null && "caught4k".equals(e.id())) {
         float t = time();
         if (!(t < 0.6F)) {
            float cyc = (t - 0.6F) % 1.8F;
            float f = (float)Math.exp(-cyc * 9.0) * weight();
            if (f > 0.01F) {
               g.fill(0, 0, g.guiWidth(), g.guiHeight(), (int)(150.0F * f) << 24 | 16777215);
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

   public static void fill(net.minecraft.world.entity.Entity entity, EmoteState state) {
      if (entity != net.minecraft.client.Minecraft.getInstance().player) {
         UUID playerId = entity.getUUID();
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
      } else if (active >= 0 && entity == net.minecraft.client.Minecraft.getInstance().player) {
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
      if (playerId != null && Emotes.get(emoteId) != null
            && !playerId.equals(net.minecraft.client.Minecraft.getInstance().player == null ? null : net.minecraft.client.Minecraft.getInstance().player.getUUID())) {
         REMOTE.put(playerId, new RemoteEmote(emoteId, startedAtMillis));
      }
   }

   public static void remoteStop(UUID playerId) {
      if (playerId != null) REMOTE.remove(playerId);
   }

   public static void clearRemote() {
      REMOTE.clear();
   }

   public static boolean cameraActive(net.minecraft.world.entity.Entity entity) {
      return camera && active >= 0 && entity == net.minecraft.client.Minecraft.getInstance().player;
   }

   public static boolean turn(net.minecraft.world.entity.Entity entity, double dx, double dy) {
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
