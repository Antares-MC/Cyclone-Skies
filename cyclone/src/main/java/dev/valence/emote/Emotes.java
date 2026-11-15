package dev.valence.emote;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class Emotes {
   public static final int PROP_NONE = 0;
   public static final int PROP_CAMERA = 1;
   public static final int PROP_LAMP = 2;
   public static final int PROP_WHEELCHAIR = 3;
   public static final int PROP_SPARKS = 4;
   public static final int PROP_67 = 5;
   public static final int PROP_GRAVE = 6;
   public static final int PROP_BALL = 7;
   public static final int PROP_REFEREE = 8;
   public static final int PROP_COZY = 9;
   public static final int PROP_KEBAB = 10;
   public static final int PROP_TROPHY = 11;
   private static final float PI = (float) Math.PI;
   public static final List<Emotes.Emote> ALL = List.of(
      new Emotes.Emote("67", "67", "6", -742954, Emotes::sixSeven),
      new Emotes.Emote("cringe", "Dies of Cringe", "C", -6511697, Emotes::diesOfCringe),
      new Emotes.Emote("football", "Football Freestyle", "F", -657931, Emotes::football),
      new Emotes.Emote("redcard", "Red Card", "R", -2087892, Emotes::redCard),
      new Emotes.Emote("cozy", "Cozy Time", "C", -20448, Emotes::cozy),
      new Emotes.Emote("kebab", "Kebab", "K", -6526406, Emotes::kebab),
      new Emotes.Emote("fivehead", "Fivehead", "5", -137590, Emotes::fivehead),
      new Emotes.Emote("winner", "Winner", "W", -867792, Emotes::winner),
      new Emotes.Emote("swagger", "Vibing", "V", -10556716, Emotes::swagger),
      new Emotes.Emote("caught4k", "Caught in 4K", "4K", -1710101, Emotes::caught4k),
      new Emotes.Emote("bighead", "Big Head", "H", -137590, Emotes::bigHead),
      new Emotes.Emote("hanging", "Hanging Around", "A", -290244, Emotes::hanging),
      new Emotes.Emote("pancake", "Pancake", "C", -208051, Emotes::pancake),
      new Emotes.Emote("wheelie", "Wheelie", "E", -7092739, Emotes::wheelie),
      new Emotes.Emote("backflip", "Backflip", "X", -5796870, Emotes::backflip),
      new Emotes.Emote("headspin", "Headspin", "Y", -415532, Emotes::headspin),
      new Emotes.Emote("zen", "Zen Float", "Z", -10556716, Emotes::zen),
      new Emotes.Emote("robot", "Robot", "R", -7035976, Emotes::robot),
      new Emotes.Emote("tpose", "T-Pose Ascend", "T", -3885571, Emotes::tpose)
   );
   public static final int PER_PAGE = 8;
   private static final Clip TAKE_L = Clip.loop(0.75F)
      .key(0.0F, p -> takeLArms(p.rLeg(-2.0F, 0.0F, 4.0F).lLeg(-2.0F, 0.0F, -4.0F).root(-0.02F, 0.0F, 2.0F, 0.0F, 0.0F).head(14.0F, 0.0F, 0.0F), 2.0F))
      .key(0.19F, p -> takeLArms(p.rLeg(-12.0F, 0.0F, 56.0F).lLeg(0.0F, 0.0F, -4.0F).root(0.06F, -0.04F, 0.0F, 8.0F, -8.0F).head(10.0F, 0.0F, -5.0F), -3.0F))
      .key(0.375F, p -> takeLArms(p.rLeg(-2.0F, 0.0F, 4.0F).lLeg(-2.0F, 0.0F, -4.0F).root(-0.02F, 0.0F, 2.0F, 0.0F, 0.0F).head(14.0F, 0.0F, 0.0F), 2.0F))
      .key(0.565F, p -> takeLArms(p.lLeg(-12.0F, 0.0F, -56.0F).rLeg(0.0F, 0.0F, 4.0F).root(0.06F, 0.04F, 0.0F, -8.0F, 8.0F).head(10.0F, 0.0F, 5.0F), -3.0F));
   private static final Clip SIX_SEVEN = Clip.loop(0.9F)
      .key(
         0.0F,
         p -> p.rArm(-42.0F, -6.0F, 10.0F)
            .lArm(-14.0F, 6.0F, -10.0F)
            .bend(82.0F, 55.0F, 0.0F, 0.0F)
            .head(4.0F, 0.0F, 4.0F)
            .root(0.02F, 0.0F, 0.0F, 0.0F, -2.0F)
      )
      .key(
         0.45F,
         p -> p.rArm(-14.0F, -6.0F, 10.0F)
            .lArm(-42.0F, 6.0F, -10.0F)
            .bend(55.0F, 82.0F, 0.0F, 0.0F)
            .head(4.0F, 0.0F, -4.0F)
            .root(0.02F, 0.0F, 0.0F, 0.0F, 2.0F)
      );
   private static final Clip CRINGE = Clip.loop(6.0F)
      .key(
         0.0F,
         p -> p.rArm(-20.0F, 0.0F, 6.0F).lArm(-20.0F, 0.0F, -6.0F).bend(30.0F, 30.0F, 0.0F, 0.0F).root(0.0F, 0.0F, 12.0F, 0.0F, 0.0F).head(25.0F, 0.0F, 0.0F)
      )
      .key(
         0.3F,
         p -> p.rArm(25.0F, 0.0F, 22.0F)
            .lArm(25.0F, 0.0F, -22.0F)
            .bend(20.0F, 20.0F, 40.0F, 40.0F)
            .rLeg(-20.0F, 0.0F, 4.0F)
            .lLeg(-20.0F, 0.0F, -4.0F)
            .root(-0.1F, 0.0F, 20.0F, 0.0F, 0.0F)
            .head(10.0F, 0.0F, 0.0F)
      )
      .key(
         0.55F,
         p -> p.rArm(-160.0F, 0.0F, 40.0F)
            .lArm(-150.0F, 0.0F, -50.0F)
            .bend(10.0F, 10.0F, 40.0F, 60.0F)
            .rLeg(-80.0F, 0.0F, 6.0F)
            .lLeg(-50.0F, 0.0F, -6.0F)
            .root(0.55F, 0.0F, -50.0F, 0.0F, 0.0F)
            .head(-20.0F, 0.0F, 0.0F)
      )
      .key(
         0.8F,
         p -> p.rArm(-120.0F, 0.0F, 80.0F)
            .lArm(-120.0F, 0.0F, -80.0F)
            .bend(10.0F, 10.0F, 30.0F, 30.0F)
            .rLeg(-60.0F, 0.0F, 10.0F)
            .lLeg(-60.0F, 0.0F, -10.0F)
            .root(0.3F, 0.0F, -95.0F, 0.0F, 0.0F)
            .head(-10.0F, 0.0F, 0.0F)
      )
      .key(
         1.0F,
         p -> p.rArm(0.0F, 0.0F, 78.0F)
            .lArm(0.0F, 0.0F, -78.0F)
            .bend(10.0F, 10.0F, 4.0F, 4.0F)
            .rLeg(0.0F, 0.0F, 18.0F)
            .lLeg(0.0F, 0.0F, -18.0F)
            .root(-0.77F, 0.0F, -90.0F, 0.0F, 0.0F)
            .head(0.0F, 0.0F, 0.0F)
      )
      .key(
         1.15F,
         p -> p.rArm(0.0F, 0.0F, 70.0F)
            .lArm(0.0F, 0.0F, -70.0F)
            .bend(14.0F, 14.0F, 8.0F, 8.0F)
            .rLeg(-6.0F, 0.0F, 16.0F)
            .lLeg(-6.0F, 0.0F, -16.0F)
            .root(-0.71F, 0.0F, -88.0F, 0.0F, 0.0F)
            .head(-6.0F, 0.0F, 0.0F)
      )
      .key(
         1.35F,
         p -> p.rArm(0.0F, 0.0F, 78.0F)
            .lArm(0.0F, 0.0F, -78.0F)
            .bend(10.0F, 10.0F, 4.0F, 4.0F)
            .rLeg(0.0F, 0.0F, 18.0F)
            .lLeg(0.0F, 0.0F, -18.0F)
            .root(-0.77F, 0.0F, -90.0F, 0.0F, 0.0F)
            .head(0.0F, 0.0F, 0.0F)
      );
   private static final Clip FOOTBALL = Clip.loop(2.4F)
      .key(
         0.0F,
         p -> p.rLeg(-65.0F, 0.0F, 4.0F)
            .lLeg(0.0F, 0.0F, -3.0F)
            .bend(30.0F, 30.0F, 80.0F, 8.0F)
            .rArm(-15.0F, 0.0F, 30.0F)
            .lArm(-15.0F, 0.0F, -30.0F)
            .head(30.0F, 0.0F, 0.0F)
            .ball(-0.12F, 0.8F, -0.38F)
      )
      .key(
         0.3F,
         p -> p.rLeg(-4.0F, 0.0F, 4.0F)
            .lLeg(-4.0F, 0.0F, -4.0F)
            .bend(30.0F, 30.0F, 10.0F, 10.0F)
            .rArm(-15.0F, 0.0F, 25.0F)
            .lArm(-15.0F, 0.0F, -25.0F)
            .head(12.0F, 0.0F, 0.0F)
            .ball(-0.03F, 1.25F, -0.4F)
      )
      .key(
         0.6F,
         p -> p.lLeg(-65.0F, 0.0F, -4.0F)
            .rLeg(0.0F, 0.0F, 3.0F)
            .bend(30.0F, 30.0F, 8.0F, 80.0F)
            .rArm(-15.0F, 0.0F, 30.0F)
            .lArm(-15.0F, 0.0F, -30.0F)
            .head(30.0F, 0.0F, 0.0F)
            .ball(0.12F, 0.8F, -0.38F)
      )
      .key(
         0.9F,
         p -> p.rLeg(-4.0F, 0.0F, 4.0F)
            .lLeg(-4.0F, 0.0F, -4.0F)
            .bend(30.0F, 30.0F, 10.0F, 10.0F)
            .rArm(-15.0F, 0.0F, 25.0F)
            .lArm(-15.0F, 0.0F, -25.0F)
            .head(12.0F, 0.0F, 0.0F)
            .ball(0.04F, 1.3F, -0.42F)
      )
      .key(
         1.2F,
         p -> p.rLeg(-50.0F, 0.0F, 6.0F)
            .lLeg(0.0F, 0.0F, -3.0F)
            .bend(30.0F, 30.0F, 15.0F, 12.0F)
            .rArm(-25.0F, 0.0F, 35.0F)
            .lArm(-10.0F, 0.0F, -35.0F)
            .head(35.0F, 0.0F, 0.0F)
            .ball(-0.12F, 0.33F, -0.62F)
      )
      .key(
         1.5F,
         p -> p.rLeg(-4.0F, 0.0F, 4.0F)
            .lLeg(-4.0F, 0.0F, -4.0F)
            .bend(25.0F, 25.0F, 14.0F, 14.0F)
            .rArm(-20.0F, 0.0F, 38.0F)
            .lArm(-20.0F, 0.0F, -38.0F)
            .head(-35.0F, 0.0F, 0.0F)
            .ball(-0.02F, 2.5F, -0.35F)
      )
      .key(
         1.8F,
         p -> p.rLeg(-6.0F, 0.0F, 4.0F)
            .lLeg(-6.0F, 0.0F, -4.0F)
            .bend(25.0F, 25.0F, 22.0F, 22.0F)
            .rArm(-25.0F, 0.0F, 40.0F)
            .lArm(-25.0F, 0.0F, -40.0F)
            .head(-25.0F, 0.0F, 0.0F)
            .root(0.08F, 0.0F, 0.0F, 0.0F, 0.0F)
            .ball(0.0F, 2.08F, -0.12F)
      )
      .key(
         2.1F,
         p -> p.rLeg(-4.0F, 0.0F, 4.0F)
            .lLeg(-4.0F, 0.0F, -4.0F)
            .bend(30.0F, 30.0F, 10.0F, 10.0F)
            .rArm(-15.0F, 0.0F, 28.0F)
            .lArm(-15.0F, 0.0F, -28.0F)
            .head(12.0F, 0.0F, 0.0F)
            .ball(-0.08F, 1.55F, -0.3F)
      );
   private static final Clip RED_CARD = Clip.loop(3.0F)
      .key(
         0.0F,
         p -> p.rArm(-45.0F, -35.0F, 12.0F).lArm(0.0F, 0.0F, -6.0F).bend(110.0F, 10.0F, 0.0F, 0.0F).axis(-15.0F, 0.0F, 0.0F, 0.0F).head(-5.0F, 0.0F, 0.0F)
      )
      .key(
         0.25F,
         p -> p.rArm(-48.0F, -35.0F, 12.0F).lArm(0.0F, 0.0F, -6.0F).bend(112.0F, 10.0F, 0.0F, 0.0F).axis(-15.0F, 0.0F, 0.0F, 0.0F).head(0.0F, 0.0F, 0.0F)
      )
      .key(0.5F, p -> p.rArm(-110.0F, 0.0F, 25.0F).lArm(5.0F, 0.0F, -8.0F).bend(45.0F, 15.0F, 0.0F, 0.0F).head(-8.0F, 0.0F, 0.0F))
      .key(0.8F, p -> p.rArm(-172.0F, 0.0F, 10.0F).lArm(10.0F, 0.0F, -10.0F).bend(0.0F, 10.0F, 0.0F, 0.0F).head(-18.0F, 0.0F, 0.0F))
      .key(1.2F, p -> p.rArm(-168.0F, 0.0F, 14.0F).lArm(10.0F, 0.0F, -10.0F).bend(4.0F, 10.0F, 0.0F, 0.0F).head(-15.0F, 0.0F, 0.0F))
      .key(1.5F, p -> p.rArm(-172.0F, 0.0F, 10.0F).lArm(10.0F, 0.0F, -10.0F).bend(0.0F, 10.0F, 0.0F, 0.0F).head(-18.0F, 0.0F, 0.0F))
      .key(1.8F, p -> p.rArm(5.0F, 0.0F, 12.0F).lArm(0.0F, 0.0F, -8.0F).bend(40.0F, 10.0F, 0.0F, 0.0F).head(0.0F, 0.0F, 0.0F))
      .key(
         2.1F,
         p -> p.rArm(-45.0F, -35.0F, 12.0F)
            .lArm(-75.0F, 0.0F, -70.0F)
            .bend(110.0F, 0.0F, 25.0F, 0.0F)
            .axis(-15.0F, 0.0F, 0.0F, 0.0F)
            .rLeg(-15.0F, 0.0F, 4.0F)
            .head(0.0F, 0.0F, 0.0F)
      )
      .key(
         2.45F,
         p -> p.rArm(-45.0F, -35.0F, 12.0F)
            .lArm(-85.0F, 0.0F, -78.0F)
            .bend(110.0F, 0.0F, 5.0F, 25.0F)
            .axis(-15.0F, 0.0F, 0.0F, 0.0F)
            .lLeg(-15.0F, 0.0F, -4.0F)
            .head(-3.0F, 0.0F, 0.0F)
      )
      .key(
         2.75F,
         p -> p.rArm(-45.0F, -35.0F, 12.0F).lArm(-20.0F, 0.0F, -15.0F).bend(110.0F, 15.0F, 0.0F, 0.0F).axis(-15.0F, 0.0F, 0.0F, 0.0F).head(0.0F, 0.0F, 0.0F)
      );
   private static final Clip COZY = Clip.loop(4.0F)
      .key(
         0.0F,
         p -> sit(p)
            .rArm(-30.0F, -28.0F, 6.0F)
            .lArm(-30.0F, 28.0F, -6.0F)
            .bend(62.0F, 62.0F, 85.0F, 85.0F)
            .axis(-20.0F, 20.0F, 0.0F, 0.0F)
            .head(15.0F, 0.0F, 0.0F)
      )
      .key(
         1.0F,
         p -> sit(p)
            .rArm(-32.0F, -28.0F, 6.0F)
            .lArm(-32.0F, 28.0F, -6.0F)
            .bend(63.0F, 63.0F, 85.0F, 85.0F)
            .axis(-20.0F, 20.0F, 0.0F, 0.0F)
            .head(18.0F, 6.0F, 0.0F)
      )
      .key(
         1.8F,
         p -> sit(p)
            .rArm(-40.0F, -28.0F, 6.0F)
            .lArm(-30.0F, 28.0F, -6.0F)
            .bend(75.0F, 62.0F, 85.0F, 85.0F)
            .axis(-20.0F, 20.0F, 0.0F, 0.0F)
            .head(10.0F, 0.0F, 0.0F)
      )
      .key(
         2.3F,
         p -> sit(p)
            .rArm(-85.0F, -25.0F, 6.0F)
            .lArm(-28.0F, 28.0F, -6.0F)
            .bend(108.0F, 60.0F, 85.0F, 85.0F)
            .axis(-20.0F, 20.0F, 0.0F, 0.0F)
            .head(-12.0F, 0.0F, 0.0F)
      )
      .key(
         2.9F,
         p -> sit(p)
            .rArm(-30.0F, -28.0F, 6.0F)
            .lArm(-30.0F, 28.0F, -6.0F)
            .bend(62.0F, 62.0F, 85.0F, 85.0F)
            .axis(-20.0F, 20.0F, 0.0F, 0.0F)
            .head(15.0F, 0.0F, 0.0F)
      )
      .key(
         3.5F,
         p -> sit(p)
            .rArm(-31.0F, -28.0F, 6.0F)
            .lArm(-31.0F, 28.0F, -6.0F)
            .bend(62.0F, 62.0F, 85.0F, 85.0F)
            .axis(-20.0F, 20.0F, 0.0F, 0.0F)
            .head(16.0F, -6.0F, 0.0F)
      );
   private static final Clip KEBAB = Clip.loop(1.2F)
      .key(
         0.0F,
         p -> p.rArm(-98.0F, -35.0F, 0.0F)
            .lArm(-40.0F, 20.0F, -10.0F)
            .bend(14.0F, 60.0F, 0.0F, 0.0F)
            .root(0.0F, 0.0F, 2.0F, -28.0F, 0.0F)
            .head(14.0F, -6.0F, 0.0F)
      )
      .key(
         0.6F,
         p -> p.rArm(-58.0F, -35.0F, 0.0F)
            .lArm(-42.0F, 20.0F, -10.0F)
            .bend(34.0F, 62.0F, 0.0F, 0.0F)
            .root(-0.02F, 0.0F, 4.0F, -28.0F, 0.0F)
            .head(20.0F, -6.0F, 0.0F)
      );
   private static final Clip FIVEHEAD = Clip.loop(3.4F)
      .key(0.0F, p -> p.headSize(1.0F, 1.7F, 0.5F).rArm(0.0F, 0.0F, 6.0F).lArm(0.0F, 0.0F, -6.0F))
      .key(0.35F, p -> p.headSize(1.0F, 0.8F, 1.3F).rArm(0.0F, 0.0F, 8.0F).lArm(0.0F, 0.0F, -8.0F))
      .key(0.65F, p -> p.headSize(1.0F, 1.1F, 0.9F).rArm(0.0F, 0.0F, 6.0F).lArm(0.0F, 0.0F, -6.0F))
      .key(0.9F, p -> p.headSize(1.0F, 1.0F, 1.0F).rArm(0.0F, 0.0F, 6.0F).lArm(0.0F, 0.0F, -6.0F))
      .key(1.4F, p -> p.headSize(1.0F, 1.0F, 1.0F).rArm(-5.0F, 0.0F, 10.0F).lArm(-5.0F, 0.0F, -10.0F).head(-5.0F, 0.0F, 0.0F))
      .key(1.75F, p -> p.headSize(3.0F, 1.0F, 1.0F).rArm(-10.0F, 0.0F, 22.0F).lArm(-10.0F, 0.0F, -22.0F).bend(20.0F, 20.0F, 0.0F, 0.0F))
      .key(2.05F, p -> p.headSize(2.7F, 1.0F, 1.0F).rArm(-8.0F, 0.0F, 18.0F).lArm(-8.0F, 0.0F, -18.0F).bend(15.0F, 15.0F, 0.0F, 0.0F))
      .key(2.3F, p -> p.headSize(3.0F, 1.0F, 1.0F).rArm(-10.0F, 0.0F, 22.0F).lArm(-10.0F, 0.0F, -22.0F).bend(20.0F, 20.0F, 0.0F, 0.0F))
      .key(3.0F, p -> p.headSize(3.0F, 1.0F, 1.0F).rArm(-10.0F, 0.0F, 20.0F).lArm(-10.0F, 0.0F, -20.0F).bend(20.0F, 20.0F, 0.0F, 0.0F));
   private static final Clip WINNER = Clip.loop(3.0F)
      .key(
         0.0F, p -> p.rArm(-60.0F, -35.0F, 0.0F).lArm(0.0F, 0.0F, -6.0F).bend(70.0F, 10.0F, 0.0F, 0.0F).axis(-10.0F, 0.0F, 0.0F, 0.0F).head(10.0F, 0.0F, 0.0F)
      )
      .key(
         0.35F,
         p -> p.rArm(-35.0F, -25.0F, 0.0F)
            .lArm(0.0F, 0.0F, -6.0F)
            .bend(95.0F, 10.0F, 15.0F, 15.0F)
            .axis(-10.0F, 0.0F, 0.0F, 0.0F)
            .root(-0.05F, 0.0F, 3.0F, 0.0F, 0.0F)
            .head(5.0F, 0.0F, 0.0F)
      )
      .key(
         0.6F,
         p -> p.rArm(-170.0F, 0.0F, 10.0F)
            .lArm(-10.0F, 0.0F, -10.0F)
            .bend(0.0F, 10.0F, 0.0F, 0.0F)
            .head(-15.0F, 0.0F, 0.0F)
            .root(0.02F, 0.0F, 0.0F, 0.0F, 0.0F)
      )
      .key(0.85F, p -> p.rArm(-175.0F, 0.0F, 12.0F).lArm(-10.0F, 0.0F, -10.0F).bend(0.0F, 10.0F, 0.0F, 0.0F).head(-25.0F, 0.0F, 0.0F))
      .key(1.15F, p -> p.rArm(-175.0F, 0.0F, 12.0F).lArm(-10.0F, 0.0F, -10.0F).bend(0.0F, 10.0F, 0.0F, 0.0F).head(-20.0F, 0.0F, 0.0F))
      .key(
         1.5F,
         p -> p.rArm(-160.0F, 0.0F, 15.0F)
            .lArm(-40.0F, 0.0F, -25.0F)
            .bend(15.0F, 90.0F, 0.0F, 0.0F)
            .head(-12.0F, 0.0F, 0.0F)
            .root(0.04F, 0.0F, 0.0F, 0.0F, 0.0F)
      )
      .key(1.8F, p -> p.rArm(-178.0F, 0.0F, 10.0F).lArm(-30.0F, 0.0F, -20.0F).bend(0.0F, 100.0F, 0.0F, 0.0F).head(-18.0F, 0.0F, 0.0F))
      .key(
         2.1F,
         p -> p.rArm(-160.0F, 0.0F, 15.0F)
            .lArm(-40.0F, 0.0F, -25.0F)
            .bend(15.0F, 90.0F, 0.0F, 0.0F)
            .head(-12.0F, 0.0F, 0.0F)
            .root(0.04F, 0.0F, 0.0F, 0.0F, 0.0F)
      )
      .key(2.4F, p -> p.rArm(-178.0F, 0.0F, 10.0F).lArm(-30.0F, 0.0F, -20.0F).bend(0.0F, 100.0F, 0.0F, 0.0F).head(-18.0F, 0.0F, 0.0F))
      .key(2.75F, p -> p.rArm(-90.0F, -30.0F, 0.0F).lArm(-5.0F, 0.0F, -8.0F).bend(50.0F, 15.0F, 0.0F, 0.0F).head(0.0F, 0.0F, 0.0F));
   private static final Clip SWAGGER = Clip.loop(3.6F)
      .key(
         0.0F,
         p -> p.rArm(-25.0F, 0.0F, 80.0F)
            .lArm(5.0F, 0.0F, -8.0F)
            .bend(125.0F, 15.0F, 25.0F, 5.0F)
            .axis(90.0F, 0.0F, 0.0F, 0.0F)
            .rLeg(-10.0F, 0.0F, 4.0F)
            .lLeg(0.0F, 0.0F, -4.0F)
            .root(-0.02F, 0.0F, 2.0F, 5.0F, -3.0F)
            .head(4.0F, 45.0F, 10.0F)
      )
      .key(
         0.4F,
         p -> p.rArm(-20.0F, 0.0F, 86.0F)
            .lArm(-4.0F, 0.0F, -8.0F)
            .bend(132.0F, 15.0F, 5.0F, 25.0F)
            .axis(90.0F, 0.0F, 0.0F, 0.0F)
            .rLeg(0.0F, 0.0F, 4.0F)
            .lLeg(-10.0F, 0.0F, -4.0F)
            .root(-0.02F, 0.0F, 2.0F, 5.0F, 3.0F)
            .head(10.0F, 50.0F, 6.0F)
      )
      .key(
         0.8F,
         p -> p.rArm(-25.0F, 0.0F, 80.0F)
            .lArm(5.0F, 0.0F, -8.0F)
            .bend(125.0F, 15.0F, 25.0F, 5.0F)
            .axis(90.0F, 0.0F, 0.0F, 0.0F)
            .rLeg(-10.0F, 0.0F, 4.0F)
            .lLeg(0.0F, 0.0F, -4.0F)
            .root(-0.02F, 0.0F, 2.0F, 5.0F, -3.0F)
            .head(4.0F, 45.0F, 10.0F)
      )
      .key(
         1.2F,
         p -> p.rArm(-20.0F, 0.0F, 86.0F)
            .lArm(-4.0F, 0.0F, -8.0F)
            .bend(132.0F, 15.0F, 5.0F, 25.0F)
            .axis(90.0F, 0.0F, 0.0F, 0.0F)
            .rLeg(0.0F, 0.0F, 4.0F)
            .lLeg(-10.0F, 0.0F, -4.0F)
            .root(-0.02F, 0.0F, 2.0F, 5.0F, 3.0F)
            .head(10.0F, 50.0F, 6.0F)
      )
      .key(
         1.7F,
         p -> p.rArm(10.0F, 0.0F, 10.0F)
            .lArm(-10.0F, 0.0F, -10.0F)
            .bend(15.0F, 15.0F, 25.0F, 5.0F)
            .rLeg(-10.0F, 0.0F, 4.0F)
            .lLeg(0.0F, 0.0F, -4.0F)
            .root(-0.03F, 0.0F, 2.0F, 0.0F, -2.0F)
            .head(6.0F, 40.0F, 0.0F)
      )
      .key(
         2.2F,
         p -> p.rArm(-15.0F, 0.0F, 8.0F)
            .lArm(12.0F, 0.0F, -8.0F)
            .bend(20.0F, 15.0F, 5.0F, 25.0F)
            .rLeg(0.0F, 0.0F, 4.0F)
            .lLeg(-10.0F, 0.0F, -4.0F)
            .root(-0.03F, 0.0F, 2.0F, 0.0F, 2.0F)
            .head(0.0F, 30.0F, -4.0F)
      )
      .key(
         2.7F,
         p -> p.rArm(10.0F, 0.0F, 10.0F)
            .lArm(-10.0F, 0.0F, -10.0F)
            .bend(15.0F, 15.0F, 25.0F, 5.0F)
            .rLeg(-10.0F, 0.0F, 4.0F)
            .lLeg(0.0F, 0.0F, -4.0F)
            .root(-0.03F, 0.0F, 2.0F, 0.0F, -2.0F)
            .head(6.0F, 45.0F, 4.0F)
      )
      .key(
         3.2F,
         p -> p.rArm(-30.0F, 0.0F, 70.0F)
            .lArm(0.0F, 0.0F, -8.0F)
            .bend(110.0F, 15.0F, 5.0F, 25.0F)
            .axis(90.0F, 0.0F, 0.0F, 0.0F)
            .rLeg(0.0F, 0.0F, 4.0F)
            .lLeg(-10.0F, 0.0F, -4.0F)
            .root(-0.02F, 0.0F, 2.0F, 4.0F, 2.0F)
            .head(4.0F, 45.0F, 8.0F)
      );
   private static final float[][] ROBOT = new float[][]{
      {-90.0F, 0.0F, 0.0F, -5.0F, 0.0F, 0.0F, 0.0F},
      {-90.0F, 0.0F, -90.0F, 0.0F, 40.0F, 0.0F, 0.0F},
      {-180.0F, 0.0F, -90.0F, 0.0F, -40.0F, 30.0F, 0.05F},
      {0.0F, 90.0F, 0.0F, -90.0F, 0.0F, 30.0F, 0.0F},
      {-90.0F, 90.0F, -90.0F, -90.0F, 0.0F, -30.0F, 0.05F},
      {-45.0F, 0.0F, -135.0F, 0.0F, 45.0F, -30.0F, 0.0F},
      {0.0F, 10.0F, -90.0F, 0.0F, -45.0F, 0.0F, 0.05F},
      {-90.0F, 0.0F, -90.0F, 0.0F, 0.0F, 0.0F, 0.0F}
   };

   private Emotes() {
   }

   static float s(float t, float period) {
      return (float)Math.sin(t / period * 2.0 * Math.PI);
   }

   static float c(float t, float period) {
      return (float)Math.cos(t / period * 2.0 * Math.PI);
   }

   static float bounce(float t, float period) {
      return 0.5F - 0.5F * c(t, period);
   }

   static float pos(float v) {
      return Math.max(0.0F, v);
   }

   static float clamp01(float v) {
      return v < 0.0F ? 0.0F : Math.min(v, 1.0F);
   }

   static float smooth(float v) {
      v = clamp01(v);
      return v * v * (3.0F - 2.0F * v);
   }

   static float outBack(float v) {
      v = clamp01(v);
      float c1 = 1.70158F;
      float c3 = c1 + 1.0F;
      float u = v - 1.0F;
      return 1.0F + c3 * u * u * u + c1 * u * u;
   }

   static float outElastic(float v) {
      v = clamp01(v);
      return v != 0.0F && v != 1.0F ? (float)(Math.pow(2.0, -10.0F * v) * Math.sin((v * 10.0F - 0.75) * (Math.PI * 2.0 / 3.0)) + 1.0) : v;
   }

   static float lerp(float a, float b, float k) {
      return a + (b - a) * k;
   }

   static float range(float t, float a, float b) {
      return clamp01((t - a) / (b - a));
   }

   public static int pages() {
      return (ALL.size() + 8 - 1) / 8;
   }

   public static Emotes.Emote get(int id) {
      return id >= 0 && id < ALL.size() ? ALL.get(id) : null;
   }

   private static EmotePose takeLArms(EmotePose p, float bob) {
      p.rArm(-120.0F + bob, 0.0F, -5.0F).lArm(-22.0F, 0.0F, -6.0F);
      p.bend[0] = 96.0F;
      p.bend[1] = 84.0F;
      p.bendAxis[0] = -90.0F;
      p.bendAxis[1] = 45.0F;
      return p;
   }

   private static void takeL(float t, EmotePose p) {
      TAKE_L.sample(t, p);
      p.bend[2] = 8.0F;
      p.bend[3] = 8.0F;
      p.yaw = p.yaw + 22.0F * s(t, 3.0F);
   }

   private static void sixSeven(float t, EmotePose p) {
      SIX_SEVEN.sample(t, p);
      p.lift = p.lift + 0.02F * bounce(t, 0.45F);
      p.prop = 5;
      p.propA = s(t, 0.9F);
      p.propB = outBack(range(t, 0.0F, 0.45F));
   }

   private static void diesOfCringe(float t, EmotePose p) {
      CRINGE.sample(Math.min(t, 1.35F), p);
      p.pivot = 0.9F;
      p.prop = 6;
      p.propB = outBack(range(t, 1.1F, 1.5F));
      if (t > 2.0F) {
         float c = (t - 2.0F) % 2.6F;
         float tw = c < 0.5F ? (float)Math.sin(c / 0.5F * (float) Math.PI) : 0.0F;
         p.rLeg[0] = p.rLeg[0] - 18.0F * tw;
         p.bend[2] = p.bend[2] + 25.0F * tw;
      }
   }

   private static void football(float t, EmotePose p) {
      FOOTBALL.sample(t, p);
      p.prop = 7;
   }

   private static void redCard(float t, EmotePose p) {
      RED_CARD.sample(t, p);
      float c = t % 3.0F;
      p.prop = 8;
      p.propA = 1.0F - smooth(range(c, 0.4F, 0.5F)) + smooth(range(c, 1.95F, 2.05F));
      p.propB = smooth(range(c, 0.5F, 0.65F)) * (1.0F - smooth(range(c, 1.65F, 1.8F)));
   }

   private static EmotePose sit(EmotePose p) {
      return p.rLeg(-85.0F, 6.0F, 6.0F).lLeg(-85.0F, -6.0F, -6.0F).root(-0.28F, 0.0F, -3.0F, 0.0F, 0.0F);
   }

   private static void cozy(float t, EmotePose p) {
      COZY.sample(t, p);
      p.roll = p.roll + 1.5F * s(t, 5.0F);
      p.prop = 9;
      p.propB = outBack(range(t, 0.0F, 0.45F));
   }

   private static void kebab(float t, EmotePose p) {
      KEBAB.sample(t, p);
      p.prop = 10;
      p.propB = outBack(range(t, 0.0F, 0.45F));
   }

   private static void fivehead(float t, EmotePose p) {
      FIVEHEAD.sample(t, p);
   }

   private static void winner(float t, EmotePose p) {
      WINNER.sample(t, p);
      float c = t % 3.0F;
      p.prop = 11;
      p.propA = c > 0.55F && c < 1.2F ? 1.5F * (float)Math.sin(range(c, 0.55F, 1.18F) * (float) Math.PI) : 0.0F;
   }

   private static void swagger(float t, EmotePose p) {
      SWAGGER.sample(t, p);
   }

   private static void caught4k(float t, EmotePose p) {
      float in = smooth(range(t, 0.0F, 0.6F));
      float cyc = t < 0.6F ? -1.0F : (t - 0.6F) % 1.8F;
      float shot = cyc < 0.0F ? 0.0F : (float)Math.exp(-cyc * 7.5F);
      float aim = cyc < 0.0F ? 0.0F : smooth(range(cyc, 0.9F, 1.75F));
      p.prop = 1;
      p.propA = shot;
      p.propB = outBack(range(t, 0.1F, 0.55F));
      p.pivot = 0.9F;
      p.pitch = in * (3.0F * aim - 5.0F * shot);
      p.forward = 0.04F * aim * in;
      p.yaw = 7.0F * s(t, 3.6F) * in;
      p.roll = 1.5F * s(t, 2.1F) * in;
      p.lift = -0.03F * in;
      float hold = -82.0F - 4.0F * shot + 3.0F * s(t, 2.7F);
      p.rArm(lerp(0.0F, hold, in), lerp(0.0F, -18.0F, in), lerp(8.0F, 4.0F, in));
      p.lArm(lerp(0.0F, hold, in), lerp(0.0F, 18.0F, in), lerp(-8.0F, -4.0F, in));
      p.bend(64.0F * in + 6.0F * shot, 64.0F * in + 6.0F * shot, 8.0F * in, 8.0F * in);
      p.axis(-12.0F, 12.0F, 0.0F, 0.0F);
      p.rLeg(-8.0F * in, 0.0F, 7.0F * in);
      p.lLeg(8.0F * in, 0.0F, -7.0F * in);
      p.head(4.0F + 5.0F * shot, -p.yaw * 0.3F, 2.0F * s(t, 2.1F));
   }

   private static void bigHead(float t, EmotePose p) {
      float grow = outElastic(range(t, 0.0F, 0.9F));
      float a = s(t, 0.6F);
      p.headScale = 1.0F + 0.95F * grow + 0.06F * bounce(t, 0.3F) * grow;
      p.lift = 0.05F * bounce(t, 0.3F);
      p.yaw = 10.0F * s(t, 2.4F);
      float sa = 0.5F + 0.5F * a;
      EmotePose.set(p.head, 6.0F * s(t, 0.3F), 22.0F * a, 12.0F * s(t, 1.2F));
      EmotePose.set(p.rArm, -20.0F + 20.0F * a, 0.0F, 55.0F + 25.0F * sa);
      EmotePose.set(p.lArm, -20.0F - 20.0F * a, 0.0F, -55.0F - 25.0F * (1.0F - sa));
      EmotePose.set(p.rLeg, -15.0F * sa, 0.0F, 6.0F);
      EmotePose.set(p.lLeg, -15.0F * (1.0F - sa), 0.0F, -6.0F);
   }

   private static void hanging(float t, EmotePose p) {
      float in = smooth(range(t, 0.0F, 0.5F));
      float swing = s(t, 2.6F) * in;
      p.prop = 2;
      p.propB = in;
      p.pivot = 0.0F;
      p.lift = 2.6F * in;
      p.pitch = 180.0F * in + 12.0F * swing;
      p.roll = 5.0F * s(t, 3.7F) * in;
      EmotePose.set(p.rArm, -180.0F * in + 10.0F * swing + 4.0F * s(t, 1.3F), 0.0F, 12.0F + 6.0F * s(t, 1.7F));
      EmotePose.set(p.lArm, -180.0F * in + 10.0F * swing - 4.0F * s(t, 1.3F), 0.0F, -12.0F - 6.0F * s(t, 1.9F));
      EmotePose.set(p.rLeg, 0.0F, 0.0F, 2.0F);
      EmotePose.set(p.lLeg, 0.0F, 0.0F, -2.0F);
      EmotePose.set(p.head, -15.0F * in, 18.0F * s(t, 4.0F), 0.0F);
   }

   private static void pancake(float t, EmotePose p) {
      float cycle = t % 3.2F;
      float squash = smooth(range(cycle, 0.0F, 0.35F));
      float pop = cycle > 2.3F ? outElastic(range(cycle, 2.3F, 3.1F)) : 0.0F;
      float flat = squash * (1.0F - pop);
      float wob = flat > 0.95F ? 0.04F * s(cycle, 0.5F) : 0.0F;
      p.sy = Math.max(0.07F, 1.0F - 0.93F * flat) + (pop > 0.0F && pop < 1.0F ? 0.15F * s(pop, 0.5F) : 0.0F);
      p.sx = 1.0F + 0.7F * flat + wob;
      p.sz = 1.0F + 0.7F * flat - wob;
      p.lift = cycle > 2.3F ? 0.5F * (float)Math.sin(range(cycle, 2.3F, 2.75F) * (float) Math.PI) : 0.0F;
      EmotePose.set(p.rArm, 0.0F, 0.0F, 85.0F * flat + 15.0F * pop * (1.0F - pop) * 4.0F);
      EmotePose.set(p.lArm, 0.0F, 0.0F, -85.0F * flat - 15.0F * pop * (1.0F - pop) * 4.0F);
      EmotePose.set(p.rLeg, 0.0F, 0.0F, 30.0F * flat);
      EmotePose.set(p.lLeg, 0.0F, 0.0F, -30.0F * flat);
      EmotePose.set(p.head, -20.0F * flat, 0.0F, 0.0F);
   }

   private static void wheelie(float t, EmotePose p) {
      float in = smooth(range(t, 0.0F, 0.35F));
      float cycle = t % 4.0F;
      float tilt = smooth(range(cycle, 0.2F, 0.6F)) * (1.0F - smooth(range(cycle, 3.4F, 3.9F)));
      float spin = smooth(range(cycle, 0.8F, 3.2F));
      p.prop = 3;
      p.propA = spin * 2.0F * (float) Math.PI * 2.2F + t * 0.6F;
      p.propB = in;
      p.lift = -0.3F * in;
      p.pivot = 0.66F;
      p.pitch = -(20.0F + 3.0F * s(t, 0.7F)) * tilt;
      p.yaw = 360.0F * spin;
      float push = s(t, 0.6F);
      EmotePose.set(p.rLeg, -85.0F * in, 8.0F * in, 2.0F);
      EmotePose.set(p.lLeg, -85.0F * in, -8.0F * in, -2.0F);
      float wave = smooth(range(cycle, 1.1F, 1.5F)) * (1.0F - smooth(range(cycle, 2.4F, 2.8F)));
      EmotePose.set(p.rArm, (-20.0F + 25.0F * push) * in * (1.0F - wave) + (-160.0F + 12.0F * s(t, 0.5F)) * wave, 0.0F, 22.0F * in + 10.0F * wave);
      EmotePose.set(p.lArm, (-20.0F - 25.0F * push) * in, 0.0F, -22.0F * in);
      p.bend(30.0F * in * (1.0F - wave), 30.0F * in, 88.0F * in, 88.0F * in);
      EmotePose.set(p.head, -6.0F * tilt, 0.0F, 0.0F);
   }

   private static void tpose(float t, EmotePose p) {
      float in = outBack(range(t, 0.0F, 0.4F));
      float rise = smooth(range(t, 0.4F, 2.6F));
      p.lift = 0.9F * rise + 0.07F * s(t, 1.8F) * rise;
      p.yaw = t > 0.4F ? (t - 0.4F) * 70.0F : 0.0F;
      p.prop = 4;
      p.propA = rise;
      EmotePose.set(p.rArm, 0.0F, 0.0F, 90.0F * in);
      EmotePose.set(p.lArm, 0.0F, 0.0F, -90.0F * in);
      EmotePose.set(p.rLeg, 0.0F, 0.0F, 0.0F);
      EmotePose.set(p.lLeg, 0.0F, 0.0F, 0.0F);
      EmotePose.set(p.head, -10.0F * rise, 0.0F, 0.0F);
   }

   private static void zen(float t, EmotePose p) {
      float in = smooth(range(t, 0.0F, 0.8F));
      float breath = s(t, 3.0F);
      p.lift = (0.55F + 0.08F * breath) * in - 0.25F * in;
      p.yaw = 10.0F * s(t, 6.0F);
      p.prop = 4;
      p.propA = in;
      EmotePose.set(p.rLeg, -85.0F * in, 38.0F * in, 6.0F * in);
      EmotePose.set(p.lLeg, -85.0F * in, -38.0F * in, -6.0F * in);
      EmotePose.set(p.rLegOff, 0.0F, 0.0F, -1.0F * in);
      EmotePose.set(p.lLegOff, 0.0F, 0.0F, -1.0F * in);
      EmotePose.set(p.rArm, -35.0F * in, 0.0F, 25.0F * in + 3.0F * breath);
      EmotePose.set(p.lArm, -35.0F * in, 0.0F, -25.0F * in - 3.0F * breath);
      EmotePose.set(p.head, 12.0F * in - 4.0F * breath, 0.0F, 0.0F);
   }

   private static void headspin(float t, EmotePose p) {
      float in = smooth(range(t, 0.0F, 0.45F));
      p.pivot = 0.0F;
      p.lift = 1.82F * in;
      p.pitch = 180.0F * in;
      float spinT = Math.max(0.0F, t - 0.35F);
      p.yaw = spinT * 540.0F + 120.0F * spinT * spinT / (1.0F + spinT);
      float kick = s(t, 0.5F);
      EmotePose.set(p.rArm, -180.0F * in + 40.0F, 0.0F, 70.0F * in + 10.0F * kick);
      EmotePose.set(p.lArm, -180.0F * in + 40.0F, 0.0F, -70.0F * in - 10.0F * kick);
      EmotePose.set(p.rLeg, 10.0F * kick, 0.0F, 28.0F * in + 8.0F * kick);
      EmotePose.set(p.lLeg, -10.0F * kick, 0.0F, -28.0F * in - 8.0F * kick);
      EmotePose.set(p.head, 0.0F, 0.0F, 0.0F);
   }

   private static void robot(float t, EmotePose p) {
      float step = 0.45F;
      int i = (int)(t / step);
      float k = clamp01((t - i * step) / 0.12F);
      k = outBack(k);
      float[] a = ROBOT[(i + ROBOT.length - 1) % ROBOT.length];
      float[] b = ROBOT[i % ROBOT.length];
      float jitter = (1.0F - clamp01((t - i * step) / 0.2F)) * 1.5F * s(t, 0.04F);
      EmotePose.set(p.rArm, lerp(a[0], b[0], k), 0.0F, lerp(a[1], b[1], k));
      EmotePose.set(p.lArm, lerp(a[2], b[2], k), 0.0F, lerp(a[3], b[3], k));
      EmotePose.set(p.head, jitter, lerp(a[4], b[4], k), 0.0F);
      p.yaw = lerp(a[5], b[5], k);
      p.lift = lerp(a[6], b[6], k);
      EmotePose.set(p.rLeg, 0.0F, 0.0F, 4.0F);
      EmotePose.set(p.lLeg, 0.0F, 0.0F, -4.0F);
      p.bend(
         lerp(a[0] < -40.0F ? 90.0F : 0.0F, b[0] < -40.0F ? 90.0F : 0.0F, k), lerp(a[2] < -40.0F ? 90.0F : 0.0F, b[2] < -40.0F ? 90.0F : 0.0F, k), 0.0F, 0.0F
      );
   }

   private static void backflip(float t, EmotePose p) {
      float cycle = t % 1.6F;
      float crouch = smooth(range(cycle, 0.0F, 0.25F)) * (1.0F - smooth(range(cycle, 0.25F, 0.32F)));
      float air = range(cycle, 0.3F, 1.05F);
      float land = cycle > 1.05F ? (float)Math.sin(range(cycle, 1.05F, 1.5F) * (float) Math.PI) : 0.0F;
      boolean inAir = cycle > 0.3F && cycle < 1.05F;
      p.pivot = 0.9F;
      p.lift = (inAir ? 5.2F * air * (1.0F - air) : 0.0F) - 0.25F * crouch - 0.22F * land;
      p.pitch = inAir ? -360.0F * smooth(air) : 14.0F * crouch + 10.0F * land;
      float tuck = inAir ? (float)Math.sin(air * (float) Math.PI) : 0.0F;
      float up = smooth(range(cycle, 0.2F, 0.4F)) * (1.0F - smooth(range(cycle, 0.95F, 1.2F)));
      float armX = 45.0F * crouch * (1.0F - up) - 170.0F * up * (1.0F - tuck * 0.5F) + 30.0F * land;
      EmotePose.set(p.rArm, armX, 0.0F, 10.0F + 20.0F * land);
      EmotePose.set(p.lArm, armX, 0.0F, -10.0F - 20.0F * land);
      EmotePose.set(p.rLeg, -70.0F * tuck - 40.0F * crouch - 35.0F * land, 0.0F, 5.0F);
      EmotePose.set(p.lLeg, -70.0F * tuck - 40.0F * crouch - 35.0F * land, 0.0F, -5.0F);
      p.bend(20.0F, 20.0F, 110.0F * tuck + 75.0F * crouch + 65.0F * land, 110.0F * tuck + 75.0F * crouch + 65.0F * land);
      EmotePose.set(p.head, 10.0F * crouch - 20.0F * tuck, 0.0F, 0.0F);
   }

   @Environment(EnvType.CLIENT)
   public interface Anim {
      void pose(float var1, EmotePose var2);
   }

   @Environment(EnvType.CLIENT)
   public record Emote(String id, String name, String icon, int color, Emotes.Anim anim) {
   }
}
