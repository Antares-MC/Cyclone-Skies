package dev.valence.emote;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_572;
import net.minecraft.class_630;
import net.minecraft.class_7833;

@Environment(EnvType.CLIENT)
public final class EmoteAnimator {
   private static final float RAD = (float) (Math.PI / 180.0);

   private EmoteAnimator() {
   }

   public static EmotePose pose(EmoteState st) {
      Emotes.Emote e = Emotes.get(st.valence$emote());
      if (e != null && !(st.valence$weight() <= 0.001F)) {
         // Render submissions can be deferred until after other entities are extracted.
         // A shared mutable pose lets a later player's animation overwrite this player's
         // pose before those queued submissions execute.
         EmotePose pose = new EmotePose();
         e.anim().pose(st.valence$time(), pose);
         return pose;
      } else {
         return null;
      }
   }

   public static void applyParts(class_572<?> m, EmoteState st) {
      EmotePose p = pose(st);
      if (p != null) {
         float w = st.valence$weight();
         part(m.field_3398, p.head, p.headOff, w);
         part(m.field_3391, p.body, null, w);
         part(m.field_3401, p.rArm, p.rArmOff, w);
         part(m.field_27433, p.lArm, p.lArmOff, w);
         part(m.field_3392, p.rLeg, p.rLegOff, w);
         part(m.field_3397, p.lLeg, p.lLegOff, w);
         m.field_3401.field_3665 = false;
         m.field_27433.field_3665 = false;
         m.field_3392.field_3665 = false;
         m.field_3397.field_3665 = false;
         if (p.headScale != 1.0F || p.headSX != 1.0F || p.headSY != 1.0F) {
            m.field_3398.field_37938 = 1.0F + (p.headScale * p.headSX - 1.0F) * w;
            m.field_3398.field_37939 = 1.0F + (p.headScale * p.headSY - 1.0F) * w;
            m.field_3398.field_37940 = 1.0F + (p.headScale * p.headSX - 1.0F) * w;
         }
      }
   }

   private static void part(class_630 part, float[] rot, float[] off, float w) {
      part.field_3654 = lerp(part.field_3654, rot[0] * (float) (Math.PI / 180.0), w);
      part.field_3675 = lerp(part.field_3675, rot[1] * (float) (Math.PI / 180.0), w);
      part.field_3674 = lerp(part.field_3674, rot[2] * (float) (Math.PI / 180.0), w);
      if (off != null) {
         part.field_3657 = part.field_3657 + off[0] * w;
         part.field_3656 = part.field_3656 + off[1] * w;
         part.field_3655 = part.field_3655 + off[2] * w;
      }
   }

   private static float lerp(float a, float b, float k) {
      return a + (b - a) * k;
   }

   public static void applyRoot(class_4587 ps, EmotePose p, float w) {
      ps.method_46416(p.side * w, -p.lift * w, -p.forward * w);
      ps.method_22907(class_7833.field_40716.rotationDegrees(p.yaw * w));
      if (p.pitch != 0.0F || p.roll != 0.0F) {
         ps.method_46416(0.0F, -p.pivot, 0.0F);
         ps.method_22907(class_7833.field_40714.rotationDegrees(p.pitch * w));
         ps.method_22907(class_7833.field_40718.rotationDegrees(p.roll * w));
         ps.method_46416(0.0F, p.pivot, 0.0F);
      }

      if (p.sx != 1.0F || p.sy != 1.0F || p.sz != 1.0F) {
         ps.method_22905(lerp(1.0F, p.sx, w), lerp(1.0F, p.sy, w), lerp(1.0F, p.sz, w));
      }
   }
}
