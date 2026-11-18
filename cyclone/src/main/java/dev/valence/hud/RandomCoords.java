package dev.valence.hud;

import dev.valence.config.ValenceConfig;
import java.util.Locale;
import java.util.Random;

/**
 * Random coordinates for the debug screen.
 *
 * <p>The F3 screen prints the player's exact position, which is how a screenshot gives away a
 * base. With this on, every coordinate line is shifted by one offset that is drawn when the
 * world loads: the numbers still move the way the player moves, so the screen stays usable,
 * but the absolute position belongs to a chunk of world that nobody has ever built in.
 *
 * <p>The offset is chunk aligned, so the XYZ, Block and Chunk lines stay consistent with each
 * other and the shifted block position still lands inside the shifted chunk.
 */
public final class RandomCoords {
   /** Chunk-aligned, a few hundred thousand blocks out: plausible, never anywhere near a base. */
   private static final int MIN_CHUNK = 3_750;
   private static final int MAX_CHUNK = 15_000;

   private static final Random RNG = new Random();
   private static int blockX;
   private static int blockZ;
   private static boolean ready;

   private RandomCoords() {
   }

   /** Draws a new offset. Called on world join and from the menu. */
   public static void refresh() {
      blockX = 16 * pick();
      blockZ = 16 * pick();
      ready = true;
   }

   private static int pick() {
      int chunk = MIN_CHUNK + RNG.nextInt(MAX_CHUNK - MIN_CHUNK);
      return RNG.nextBoolean() ? chunk : -chunk;
   }

   public static boolean enabled() {
      ValenceConfig cfg = ValenceConfig.get();
      return cfg != null && cfg.randomCoords;
   }

   public static int offsetX() {
      if (!ready) {
         refresh();
      }

      return blockX;
   }

   public static int offsetZ() {
      if (!ready) {
         refresh();
      }

      return blockZ;
   }

   /** Rewrites one debug line, or returns it untouched when it carries no position. */
   public static String spoof(String line) {
      if (line == null || !enabled()) {
         return line;
      }

      try {
         if (line.startsWith("XYZ: ")) {
            String[] parts = line.substring(5).split(" / ");
            if (parts.length == 3) {
               double x = Double.parseDouble(parts[0].trim()) + offsetX();
               double y = Double.parseDouble(parts[1].trim());
               double z = Double.parseDouble(parts[2].trim()) + offsetZ();
               return String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", x, y, z);
            }
         } else if (line.startsWith("Block: ")) {
            String[] parts = line.substring(7).trim().split(" ");
            if (parts.length >= 3) {
               int x = Integer.parseInt(parts[0]) + offsetX();
               int z = Integer.parseInt(parts[2]) + offsetZ();
               return "Block: " + x + " " + parts[1] + " " + z;
            }
         } else if (line.startsWith("Chunk: ")) {
            String[] parts = line.substring(7).trim().split(" ");
            if (parts.length >= 3) {
               int x = Integer.parseInt(parts[0]) + offsetX() / 16;
               int z = Integer.parseInt(parts[2]) + offsetZ() / 16;
               return "Chunk: " + x + " " + parts[1] + " " + z;
            }
         } else if (line.startsWith("Targeted Block: ")) {
            String[] parts = line.substring(16).split(",");
            if (parts.length == 3) {
               int x = Integer.parseInt(parts[0].trim()) + offsetX();
               int z = Integer.parseInt(parts[2].trim()) + offsetZ();
               return "Targeted Block: " + x + ", " + parts[1].trim() + ", " + z;
            }
         }
      } catch (RuntimeException ignored) {
         // A line that does not parse is a line this version formats differently; leave it.
      }

      return line;
   }
}
