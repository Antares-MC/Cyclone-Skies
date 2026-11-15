package dev.valence.sky;

import dev.valence.sky.classic.Sky;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class SkyDefinition {
   public final String id;
   public final Sky sky;
   public final float[] view;
   private static final Map<String, float[]> VIEWS = Map.ofEntries(
      Map.entry("saturn", new float[]{28.0F, 28.0F}),
      Map.entry("aurora", new float[]{0.0F, 20.0F}),
      Map.entry("galaxy", new float[]{0.0F, 25.0F}),
      Map.entry("nebula", new float[]{90.0F, 30.0F}),
      Map.entry("nebulacloud", new float[]{30.0F, 30.0F}),
      Map.entry("meteors", new float[]{60.0F, 30.0F}),
      Map.entry("startrails", new float[]{0.0F, 35.0F}),
      Map.entry("comet", new float[]{-12.0F, 25.0F}),
      Map.entry("hyperspace", new float[]{0.0F, 15.0F}),
      Map.entry("sunset", new float[]{0.0F, 10.0F}),
      Map.entry("golden_sunset", new float[]{0.0F, 12.0F}),
      Map.entry("pink_sunset", new float[]{0.0F, 10.0F}),
      Map.entry("blue_sun", new float[]{0.0F, 28.0F}),
      Map.entry("white_sun", new float[]{0.0F, 25.0F}),
      Map.entry("heavengate", new float[]{0.0F, 35.0F}),
      Map.entry("desert", new float[]{-12.0F, 15.0F}),
      Map.entry("winter", new float[]{-20.0F, 20.0F}),
      Map.entry("neon", new float[]{-10.0F, 10.0F}),
      Map.entry("bloodmoon", new float[]{-25.0F, 34.0F}),
      Map.entry("enchanted", new float[]{28.0F, 40.0F}),
      Map.entry("abyss", new float[]{0.0F, 30.0F}),
      Map.entry("twinmoons", new float[]{-20.0F, 36.0F}),
      Map.entry("eclipse", new float[]{0.0F, 46.0F}),
      Map.entry("prism", new float[]{180.0F, 25.0F}),
      Map.entry("spores", new float[]{0.0F, 20.0F}),
      Map.entry("amber", new float[]{0.0F, 25.0F}),
      Map.entry("twilight", new float[]{0.0F, 10.0F}),
      Map.entry("cybercity", new float[]{0.0F, 12.0F}),
      Map.entry("volcano", new float[]{25.0F, 6.0F}),
      Map.entry("sandstorm", new float[]{0.0F, 15.0F}),
      Map.entry("solaris", new float[]{0.0F, 28.0F}),
      Map.entry("ocean", new float[]{10.0F, 40.0F}),
      Map.entry("iceage", new float[]{0.0F, 30.0F}),
      Map.entry("moonrise", new float[]{-18.0F, 6.0F}),
      Map.entry("milkyway", new float[]{205.0F, 58.0F}),
      Map.entry("blackhole", new float[]{24.0F, 42.0F}),
      Map.entry("pulsar", new float[]{0.0F, 56.0F}),
      Map.entry("andromeda", new float[]{8.0F, 68.0F}),
      Map.entry("cosmicweb", new float[]{0.0F, 40.0F}),
      Map.entry("starfall", new float[]{210.0F, 40.0F}),
      Map.entry("sundog", new float[]{0.0F, 18.0F}),
      Map.entry("noon", new float[]{40.0F, 68.0F}),
      Map.entry("monsoon", new float[]{0.0F, 20.0F}),
      Map.entry("hurricane", new float[]{0.0F, 60.0F}),
      Map.entry("mistvalley", new float[]{20.0F, 32.0F}),
      Map.entry("ashfall", new float[]{30.0F, 3.0F}),
      Map.entry("aurorastorm", new float[]{20.0F, 25.0F}),
      Map.entry("aether", new float[]{-30.0F, 55.0F}),
      Map.entry("lagoon", new float[]{15.0F, 38.0F}),
      Map.entry("coral", new float[]{8.0F, 3.0F}),
      Map.entry("tundra", new float[]{-25.0F, 12.0F}),
      Map.entry("desertdusk", new float[]{-6.0F, 4.0F}),
      Map.entry("zenith", new float[]{0.0F, 60.0F}),
      Map.entry("triplemoon", new float[]{-20.0F, 34.0F}),
      Map.entry("moonshard", new float[]{22.0F, 38.0F}),
      Map.entry("crimsonforest", new float[]{0.0F, 10.0F}),
      Map.entry("hyperdrive", new float[]{0.0F, 34.0F}),
      Map.entry("reef", new float[]{0.0F, 45.0F}),
      Map.entry("blossom", new float[]{30.0F, 30.0F}),
      Map.entry("inkwash", new float[]{0.0F, 22.0F}),
      Map.entry("neonrain", new float[]{20.0F, 6.0F}),
      Map.entry("binarysun", new float[]{-14.0F, 12.0F}),
      Map.entry("gasgiant", new float[]{12.0F, 52.0F})
   );

   SkyDefinition(Sky sky) {
      this.id = sky.id;
      this.sky = sky;
      this.view = VIEWS.getOrDefault(sky.id, new float[]{0.0F, 25.0F});
   }

   public int fogColor() {
      return this.sky.fogColor & 16777215;
   }

   public boolean starry() {
      return this.sky.isStarry();
   }

   public static float[] rgb(int rgb) {
      return new float[]{(rgb >> 16 & 0xFF) / 255.0F, (rgb >> 8 & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F};
   }
}
