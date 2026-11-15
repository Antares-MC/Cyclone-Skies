package dev.valence.cosmetics;

import dev.valence.gui.Theme;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 3D head cosmetics. The first 26 are blocky meshes shipped with the mod; the rest are generated
 * procedurally so they can follow the active theme.
 */
@Environment(EnvType.CLIENT)
public final class Hats {
   public static final float ANCHOR_Y = 1.40F;
   private static final float SCALE = 0.0609375F;

   public static final List<Hats.Hat> ALL = List.of(
      new Hats.Hat("storm_crown", "Storm Crown", "Cyclone", 1),
      new Hats.Hat("volt_halo", "Volt Halo", "Cyclone", 2),
      new Hats.Hat("neon_visor", "Neon Visor", "Cyclone", 3),
      new Hats.Hat("mech_helm", "Mech Helm", "Cyclone", 4),
      new Hats.Hat("top_hat", "Top Hat", "Classic", 0),
      new Hats.Hat("cap", "Cap", "Classic", 0),
      new Hats.Hat("beanie", "Beanie", "Classic", 0),
      new Hats.Hat("visor", "Visor", "Classic", 0),
      new Hats.Hat("headphones", "Headphones", "Classic", 0),
      new Hats.Hat("party_hat", "Party Hat", "Classic", 0),
      new Hats.Hat("cone", "Cone", "Classic", 0),
      new Hats.Hat("crown", "Crown", "Royal", 0),
      new Hats.Hat("flame_crown", "Flame Crown", "Royal", 0),
      new Hats.Hat("halo", "Halo", "Royal", 0),
      new Hats.Hat("viking", "Viking Helm", "Royal", 0),
      new Hats.Hat("witch_hat", "Witch Hat", "Royal", 0),
      new Hats.Hat("unicorn", "Unicorn", "Fantasy", 0),
      new Hats.Hat("devil_horns", "Devil Horns", "Fantasy", 0),
      new Hats.Hat("cat_ears", "Cat Ears", "Fantasy", 0),
      new Hats.Hat("bunny_ears", "Bunny Ears", "Fantasy", 0),
      new Hats.Hat("antennae", "Antennae", "Fantasy", 0),
      new Hats.Hat("flower_crown", "Flower Crown", "Fantasy", 0),
      new Hats.Hat("mushroom", "Mushroom", "Fantasy", 0),
      new Hats.Hat("octopus", "Octopus", "Fun", 0),
      new Hats.Hat("rubber_duck", "Rubber Duck", "Fun", 0),
      new Hats.Hat("disco_ball", "Disco Ball", "Fun", 0),
      new Hats.Hat("note_ring", "Note Ring", "Fun", 0),
      new Hats.Hat("saturn", "Saturn", "Fun", 0),
      new Hats.Hat("storm_cloud", "Storm Cloud", "Fun", 0),
      new Hats.Hat("pumpkin", "Pumpkin", "Fun", 0),
      new Hats.Hat("fairyset_helm", "Fairyset Helm", "Packs", 0, 0),
      new Hats.Hat("emissary_helm", "Emissary Helm", "Packs", 0, 1),
      new Hats.Hat("necros_helm", "Necros Helm", "Packs", 0, 2),
      new Hats.Hat("twilight_crown", "Twilight Crown", "Packs", 0, 3),
      new Hats.Hat("soulreaper_helm_green", "Soul Reaper Helm (Green)", "Packs", 0, 4),
      new Hats.Hat("soulreaper_helm_purple", "Soul Reaper Helm (Purple)", "Packs", 0, 5),
      new Hats.Hat("soulreaper_helm_red", "Soul Reaper Helm (Red)", "Packs", 0, 6)
   );

   private static final float[][] MESH = new float[ALL.size()][];
   private static final boolean[] TRIED = new boolean[ALL.size()];

   private Hats() {
   }

   public static Hats.Hat byId(String id) {
      if (id != null) {
         for (Hats.Hat h : ALL) {
            if (h.id().equals(id)) {
               return h;
            }
         }
      }

      return null;
   }

   private static float[] mesh(int index) {
      if (TRIED[index]) {
         return MESH[index];
      }

      TRIED[index] = true;
      Hats.Hat hat = ALL.get(index);

      try (InputStream in = Hats.class.getResourceAsStream("/assets/valence_cord/models/" + hat.id() + ".bin")) {
         if (in == null) {
            return null;
         }

         ByteBuffer b = ByteBuffer.wrap(in.readAllBytes()).order(ByteOrder.LITTLE_ENDIAN);
         if (b.getInt() != 827149390) {
            return null;
         }

         int n = b.getInt();
         float[] m = new float[n * 14];

         for (int t = 0; t < n; t++) {
            for (int k = 0; k < 12; k++) {
               m[t * 14 + k] = b.getFloat();
            }

            m[t * 14 + 12] = Float.intBitsToFloat(b.getInt());
            m[t * 14 + 13] = b.getInt();
         }

         MESH[index] = m;
         return m;
      } catch (Throwable var9) {
         return null;
      }
   }

   public static void emit(int index, float time, CosmeticGeometry geo) {
      if (index < 0 || index >= ALL.size()) {
         return;
      }

      Hats.Hat hat = ALL.get(index);
      if (hat.model() >= 0) {
         CosmeticModels.emitHat(hat.model(), geo);
         return;
      }

      if (hat.gen() == 0) {
         float[] m = mesh(index);
         if (m == null) {
            return;
         }

         for (int t = 0; t < m.length / 14; t++) {
            int o = t * 14;
            int argb = 0xFF000000 | Float.floatToRawIntBits(m[o + 12]);
            boolean emissive = m[o + 13] > 0.5F;
            geo.tri(
               m[o] * SCALE,
               (m[o + 1] + 4.0F) * SCALE + ANCHOR_Y,
               m[o + 2] * SCALE,
               m[o + 3] * SCALE,
               (m[o + 4] + 4.0F) * SCALE + ANCHOR_Y,
               m[o + 5] * SCALE,
               m[o + 6] * SCALE,
               (m[o + 7] + 4.0F) * SCALE + ANCHOR_Y,
               m[o + 8] * SCALE,
               argb,
               emissive
            );
         }
      } else {
         generated(hat.gen(), geo);
      }
   }

   private static void generated(int code, CosmeticGeometry geo) {
      int accent = Theme.accent();
      int light = Theme.accentLight();
      int dark = Theme.accentDark();
      switch (code) {
         case 1 -> stormCrown(geo, accent, light, dark);
         case 2 -> voltHalo(geo, accent, light, dark);
         case 3 -> neonVisor(geo, accent, light, dark);
         default -> mechHelm(geo, accent, light, dark);
      }
   }

   private static void stormCrown(CosmeticGeometry geo, int accent, int light, int dark) {
      float y0 = 1.86F;
      float y1 = 2.00F;
      float r = 0.255F;
      int n = 14;

      for (int i = 0; i < n; i++) {
         double a0 = i * (Math.PI * 2) / n;
         double a1 = (i + 1) * (Math.PI * 2) / n;
         float x0 = (float)Math.cos(a0) * r;
         float z0 = (float)Math.sin(a0) * r;
         float x1 = (float)Math.cos(a1) * r;
         float z1 = (float)Math.sin(a1) * r;
         int col = i % 2 == 0 ? dark : accent;
         geo.quad2(x0, y0, z0, x1, y0, z1, x1, y1, z1, x0, y1, z0, col, false);
      }

      for (int i = 0; i < 8; i++) {
         double a = i * (Math.PI * 2) / 8 + 0.2;
         float x = (float)Math.cos(a) * 0.21F;
         float z = (float)Math.sin(a) * 0.21F;
         float h = i % 2 == 0 ? 0.30F : 0.20F;
         geo.ribbon(x, y1 - 0.02F, z, x, y1 + h, z, 0.028F, accent, false);
         geo.ribbon(x, y1 + h - 0.03F, z, x, y1 + h + 0.05F, z, 0.018F, light, true);
      }

      geo.ribbon(-0.24F, 2.06F, 0.02F, 0.24F, 2.06F, 0.02F, 0.016F, light, true);
   }

   private static void voltHalo(CosmeticGeometry geo, int accent, int light, int dark) {
      float y = 2.10F;
      float r = 0.30F;
      int n = 20;

      for (int i = 0; i < n; i++) {
         double a0 = i * (Math.PI * 2) / n;
         double a1 = (i + 1) * (Math.PI * 2) / n;
         float x0 = (float)Math.cos(a0) * r;
         float z0 = (float)Math.sin(a0) * r;
         float x1 = (float)Math.cos(a1) * r;
         float z1 = (float)Math.sin(a1) * r;
         geo.ribbon(x0, y, z0, x1, y, z1, 0.022F, light, true);
      }

      for (int i = 0; i < 3; i++) {
         double a = i * (Math.PI * 2) / 3 + 0.4;
         float x = (float)Math.cos(a) * 0.26F;
         float z = (float)Math.sin(a) * 0.26F;
         geo.ribbon(x, y + 0.02F, z, x * 1.25F, y + 0.16F, z * 1.25F, 0.012F, accent, true);
      }

      geo.ribbon(-0.10F, 2.02F, 0.22F, 0.10F, 2.02F, 0.22F, 0.010F, accent, false);
   }

   private static void neonVisor(CosmeticGeometry geo, int accent, int light, int dark) {
      float y0 = 1.60F;
      float y1 = 1.75F;
      float z = 0.235F;

      for (int i = 0; i < 9; i++) {
         float u = i / 8.0F;
         float a = (float)Math.toRadians(-58.0F + 116.0F * u);
         float x = (float)Math.sin(a) * 0.255F;
         float zz = (float)Math.cos(a) * 0.255F;
         geo.ribbon(x, y0, zz, x, y1, zz, 0.020F, dark, false);
      }

      for (int i = 0; i < 8; i++) {
         float u0 = i / 8.0F;
         float u1 = (i + 1) / 8.0F;
         float a0 = (float)Math.toRadians(-58.0F + 116.0F * u0);
         float a1 = (float)Math.toRadians(-58.0F + 116.0F * u1);
         geo.ribbon(
            (float)Math.sin(a0) * 0.252F,
            y1 - 0.02F,
            (float)Math.cos(a0) * 0.252F,
            (float)Math.sin(a1) * 0.252F,
            y1 - 0.02F,
            (float)Math.cos(a1) * 0.252F,
            0.014F,
            light,
            true
         );
      }

      geo.box(-0.16F, y0 - 0.01F, z + 0.02F, 0.16F, y0 + 0.02F, z + 0.05F, accent, false);
   }

   private static void mechHelm(CosmeticGeometry geo, int accent, int light, int dark) {
      float r = 0.26F;
      int n = 12;
      geo.box(-0.245F, 1.80F, -0.245F, 0.245F, 1.98F, 0.245F, 0xFF2B333D, false);

      for (int i = 0; i < n; i++) {
         double a0 = i * (Math.PI * 2) / n;
         double a1 = (i + 1) * (Math.PI * 2) / n;
         float x0 = (float)Math.cos(a0) * r;
         float z0 = (float)Math.sin(a0) * r;
         float x1 = (float)Math.cos(a1) * r;
         float z1 = (float)Math.sin(a1) * r;
         geo.quad2(x0, 1.94F, z0, x1, 1.94F, z1, x1 * 0.55F, 2.03F, z1 * 0.55F, x0 * 0.55F, 2.03F, z0 * 0.55F, 0xFF3B4652, false);
         if (i % 3 == 0) {
            geo.ribbon(x0, 1.86F, z0, x0, 1.93F, z0, 0.010F, accent, true);
         }
      }

      geo.box(-0.265F, 1.72F, 0.20F, 0.265F, 1.80F, 0.30F, 0xFF232A32, false);
      geo.ribbon(-0.18F, 1.76F, 0.305F, 0.18F, 1.76F, 0.305F, 0.014F, light, true);
      geo.ribbon(0.20F, 2.02F, -0.02F, 0.26F, 2.16F, -0.06F, 0.008F, accent, false);
      geo.ribbon(-0.20F, 2.02F, -0.02F, -0.26F, 2.16F, -0.06F, 0.008F, accent, false);
   }

   @Environment(EnvType.CLIENT)
   public record Hat(String id, String name, String group, int gen, int model) {
      public Hat(String id, String name, String group, int gen) {
         this(id, name, group, gen, -1);
      }
   }
}
