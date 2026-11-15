package dev.valence.cosmetics;

import dev.valence.config.ValenceConfig;
import dev.valence.license.License;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;

@Environment(EnvType.CLIENT)
public final class Capes {
   public static final List<Capes.Cape> ALL = List.of(
      new Capes.Cape("crown_black", "Crown Cardboard Black", "Street", 1, 8.0F, false, 16),
      new Capes.Cape("crown_white", "Crown Cardboard White", "Street", 1, 8.0F, false, 16),
      new Capes.Cape("crown_cardboard", "Crown Cardboard", "Street", 1, 8.0F, false, 16),
      new Capes.Cape("error_white", "Error White", "Street", 8, 6.0F, false, 10),
      new Capes.Cape("error_black", "Error Black", "Street", 8, 6.0F, false, 10),
      new Capes.Cape("pixel_heart", "Pixel Heart", "Street", 1, 8.0F, false, 16),
      new Capes.Cape("love_note", "Love Note", "Street", 1, 8.0F, false, 16),
      new Capes.Cape("full_house", "Full House", "Street", 1, 8.0F, false, 16),
      new Capes.Cape("ace_spades", "Ace of Spades", "Street", 1, 8.0F, false, 16),
      new Capes.Cape("job_application", "Job Application", "Street", 1, 8.0F, false, 16),
      new Capes.Cape("glitch_kanji", "Glitch Kanji", "Asia", 8, 6.0F, false, 10),
      new Capes.Cape("lantern_night", "Lantern Night", "Asia", 8, 4.0F, true, 10),
      new Capes.Cape("lucky_cat", "Lucky Cat", "Asia", 8, 6.0F, true, 8),
      new Capes.Cape("golden_pattern", "Golden Pattern", "Asia", 1, 8.0F, false, 16),
      new Capes.Cape("red_sun", "Red Sun Mountain", "Asia", 1, 8.0F, false, 16),
      new Capes.Cape("sun_boat", "Rising Sun Boat", "Asia", 1, 8.0F, false, 16),
      new Capes.Cape("sakura_temple", "Sakura Temple", "Asia", 8, 6.0F, false, 16),
      new Capes.Cape("great_wave", "Great Wave", "Asia", 1, 8.0F, false, 16),
      new Capes.Cape("fire_ring", "Fire Ring", "Asia", 8, 8.0F, false, 10),
      new Capes.Cape("samurai_moon", "Samurai", "Asia", 8, 6.0F, false, 16),
      new Capes.Cape("dragon_kanji", "Dragon Kanji", "Asia", 1, 8.0F, false, 16),
      new Capes.Cape("neon_koi", "Neon Koi", "Asia", 12, 8.0F, false, 10),
      new Capes.Cape("neon_lucky_cat", "Neon Lucky Cat", "Asia", 8, 6.0F, true, 10),
      new Capes.Cape("tarot_moon", "The Moon", "Tarot", 1, 8.0F, false, 16),
      new Capes.Cape("tarot_sun", "The Sun", "Tarot", 1, 8.0F, false, 16),
      new Capes.Cape("tarot_star", "The Star", "Tarot", 1, 8.0F, false, 16),
      new Capes.Cape("tarot_seer", "The Seer", "Tarot", 1, 8.0F, false, 16),
      new Capes.Cape("tarot_emperor", "The Emperor", "Tarot", 1, 8.0F, false, 16),
      new Capes.Cape("tarot_wheel", "Wheel of Fortune", "Tarot", 1, 8.0F, false, 16),
      new Capes.Cape("angel_black", "Angel Wings Black", "Effects", 10, 8.0F, true, 10),
      new Capes.Cape("angel_white", "Angel Wings White", "Effects", 10, 8.0F, true, 10),
      new Capes.Cape("neon_butterfly", "Neon Butterfly", "Effects", 10, 10.0F, true, 10),
      new Capes.Cape("neon_butterfly_blue", "Neon Butterfly Blue", "Effects", 10, 10.0F, true, 10),
      new Capes.Cape("neon_heart", "Neon Heart", "Effects", 8, 8.0F, false, 10),
      new Capes.Cape("neon_bolt", "Neon Bolt", "Effects", 8, 10.0F, false, 10),
      new Capes.Cape("pixel_space", "Pixel Space", "Effects", 8, 6.0F, false, 10),
      new Capes.Cape("pixel_lava", "Pixel Lava", "Effects", 8, 6.0F, false, 10),
      new Capes.Cape("onebit_skull", "1-Bit Skull", "Effects", 1, 8.0F, false, 16),
      new Capes.Cape("battle_skull", "Battle Skull", "Effects", 1, 8.0F, false, 16),
      new Capes.Cape("lightning_white", "Lightning White", "Effects", 8, 8.0F, false, 10),
      new Capes.Cape("lightning_purple", "Lightning Purple", "Effects", 8, 8.0F, false, 10),
      new Capes.Cape("lightning_green", "Lightning Green", "Effects", 8, 8.0F, false, 10),
      new Capes.Cape("lightning_red", "Lightning Red", "Effects", 8, 8.0F, false, 10),
      new Capes.Cape("synthwave", "Synthwave", "Effects", 8, 8.0F, false, 10),
      new Capes.Cape("matrix", "Matrix Rain", "Effects", 8, 10.0F, false, 10),
      new Capes.Cape("aurora", "Aurora", "Effects", 10, 8.0F, false, 10),
      new Capes.Cape("galaxy", "Galaxy", "Effects", 1, 8.0F, false, 16),
      new Capes.Cape("rainbow", "Pastel Rainbow", "Effects", 1, 8.0F, false, 16),
      new Capes.Cape("pixel_heart_red", "Pixel Heart Red", "Effects", 1, 8.0F, false, 16)
   );
   private static Capes.Cape preview;
   private static long previewAt;

   private Capes() {
   }

   public static Capes.Cape byId(String id) {
      if (id != null && !id.isEmpty()) {
         for (Capes.Cape c : ALL) {
            if (c.id.equals(id)) {
               return c;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static void setPreview(Capes.Cape c) {
      preview = c;
      previewAt = System.currentTimeMillis();
   }

   public static Capes.Cape active() {
      if (!License.active()) {
         return null;
      } else {
         return preview != null && System.currentTimeMillis() - previewAt < 250L ? preview : byId(ValenceConfig.get().cape);
      }
   }

   @Environment(EnvType.CLIENT)
   public record Cape(String id, String name, String group, int frames, float fps, boolean pingPong, int scale) {
      public class_2960 texture(int frame) {
         return class_2960.method_60655("valence", "textures/capes/" + this.id + "/" + frame + ".png");
      }

      public int frameNow() {
         if (this.frames <= 1) {
            return 0;
         }

         long step = (long)(System.nanoTime() / 1.0E9 * this.fps);
         if (!this.pingPong) {
            return (int)(step % this.frames);
         }

         int cycle = this.frames * 2 - 2;
         int k = (int)(step % cycle);
         return k < this.frames ? k : cycle - k;
      }

      public class_2960 textureNow() {
         return this.texture(this.frameNow());
      }
   }
}
