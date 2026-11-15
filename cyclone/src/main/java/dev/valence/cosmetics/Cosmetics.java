package dev.valence.cosmetics;

import dev.valence.config.ValenceConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** Selection state for the 3D cosmetics. */
@Environment(EnvType.CLIENT)
public final class Cosmetics {
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

   public static boolean any() {
      return hat() != null || wings() != null;
   }
}
