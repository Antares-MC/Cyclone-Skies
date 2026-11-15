package dev.valence.cosmetics;

import dev.valence.cord.cosmetic.Accessories;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_2960;
import net.minecraft.class_310;

/**
 * Uploads the texture files shipped next to the baked models.
 *
 * <p>The resource manager only serves this mod's {@code assets/} tree from the jar at install time,
 * so binding a pack texture by path leaves it pointing at the missing-texture checker while the
 * menu preview (which reads the classpath directly) looks fine. Decoding the same PNG the preview
 * uses into a dynamic texture keeps both views identical.
 */
@Environment(EnvType.CLIENT)
final class CosmeticTextures {
   private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("CycloneSkies");
   private static final Map<String, class_2960> BOUND = new HashMap<>();
   private static final Set<String> FAILED = new HashSet<>();
   private static int serial;

   private CosmeticTextures() {
   }

   static class_2960 id(CosmeticTexture tex) {
      if (tex == null) {
         return Accessories.WHITE;
      }

      class_2960 cached = BOUND.get(tex.asset());
      if (cached != null) {
         return cached;
      }

      class_2960 bound = bind(tex.asset());
      BOUND.put(tex.asset(), bound);
      return bound;
   }

   private static class_2960 bind(String asset) {
      String name = asset.substring(asset.lastIndexOf(47) + 1);
      if (name.endsWith(".png")) {
         name = name.substring(0, name.length() - 4);
      }

      class_2960 id = class_2960.method_60655("valence_cord", "cosmetic/" + name);
      if (!FAILED.contains(asset)) {
         try (InputStream in = CosmeticTextures.class.getResourceAsStream(asset)) {
            if (in == null) {
               LOGGER.warn("[cosmetics] texture {} not on the classpath", asset);
               FAILED.add(asset);
               return Accessories.WHITE;
            }

            byte[] bytes = in.readAllBytes();
            class_1011 image = class_1011.method_49277(bytes);
            String label = "cosmetic_" + serial++;
            class_310.method_1551().method_1531().method_4616(id, new class_1043(() -> label, image));
            return id;
         } catch (Throwable var7) {
            LOGGER.warn("[cosmetics] could not upload {}", asset, var7);
            FAILED.add(asset);
            return Accessories.WHITE;
         }
      }

      return Accessories.WHITE;
   }
}
