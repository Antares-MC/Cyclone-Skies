package dev.valence.sky.classic;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class Skies {
   public static final List<Sky> ALL = List.of(
      new SpaceSkies.Saturn().starry(),
      new NightSkies.Aurora().starry(),
      new SpaceSkies.GalaxyCore().starry(),
      new SpaceSkies.NebulaStorm().starry(),
      new SpaceSkies.Nebula().starry(),
      new SpaceSkies.Meteors().starry(),
      new SpaceSkies.StarTrails(),
      new SpaceSkies.Comet().starry(),
      new SpaceSkies.Hyperspace(),
      new DaySkies.Sunset(),
      new BrightSkies.GoldenSunset(),
      new BrightSkies.PinkSunset(),
      new BrightSkies.BlueSun(),
      new BrightSkies.WhiteSun(),
      new DaySkies.HeavenGate(),
      new DaySkies.CottonCandy(),
      new DaySkies.Sakura(),
      new DaySkies.Desert(),
      new NightSkies.Winter().starry(),
      new NightSkies.Amethyst().starry(),
      new NightSkies.Crystal(),
      new NightSkies.Neon(),
      new NightSkies.DeepSea(),
      new NightSkies.Fireflies(),
      new NightSkies.Storm(),
      new ExtraSkies.BloodMoon().starry(),
      new ExtraSkies.Enchanted().starry(),
      new ExtraSkies.Abyss().starry(),
      new ExtraSkies.TwinMoons().starry(),
      new ExtraSkies.Eclipse().starry(),
      new ExtraSkies.Prism().starry(),
      new ExtraSkies.Spores().starry(),
      new ExtraSkies.Amber().starry(),
      new ExtraSkies.Twilight(),
      new ExtraSkies.CyberCity(),
      new ExtraSkies.Volcano(),
      new ExtraSkies.Sandstorm(),
      new ExtraSkies.Solaris(),
      new ExtraSkies.Ocean(),
      new ExtraSkies.IceAge(),
      new ExtraSkies2.Moonrise().starry(),
      new ExtraSkies2.MilkyWay().starry(),
      new ExtraSkies2.BlackHole().starry(),
      new ExtraSkies2.Pulsar().starry(),
      new ExtraSkies2.Andromeda().starry(),
      new ExtraSkies2.CosmicWeb().starry(),
      new ExtraSkies2.Starfall().starry(),
      new ExtraSkies2.Sundog(),
      new ExtraSkies2.Noon(),
      new ExtraSkies2.Monsoon(),
      new ExtraSkies2.Hurricane(),
      new ExtraSkies2.MistValley(),
      new ExtraSkies2.Ashfall(),
      new ExtraSkies2.AuroraStorm().starry(),
      new ExtraSkies2.Aether(),
      new ExtraSkies2.Lagoon(),
      new ExtraSkies2.Coral(),
      new ExtraSkies2.Tundra(),
      new ExtraSkies2.DesertDusk(),
      new ExtraSkies2.Zenith(),
      new ExtraSkies2.TripleMoon().starry(),
      new ExtraSkies2.MoonShard().starry(),
      new ExtraSkies2.CrimsonForest(),
      new ExtraSkies2.Hyperdrive(),
      new ExtraSkies2.Reef(),
      new ExtraSkies2.Blossom(),
      new ExtraSkies2.Inkwash(),
      new ExtraSkies2.NeonRain(),
      new ExtraSkies2.BinarySun(),
      new ExtraSkies2.GasGiant().starry()
   );

   private Skies() {
   }

   public static Sky byId(String id) {
      if (id == null) {
         return null;
      }

      for (Sky s : ALL) {
         if (s.id.equals(id)) {
            return s;
         }
      }

      return null;
   }
}
