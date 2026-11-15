package dev.valence.mixin;

import dev.valence.emote.EmoteAnimator;
import dev.valence.emote.EmoteLimbs;
import dev.valence.emote.EmotePose;
import dev.valence.emote.EmoteProps;
import dev.valence.emote.EmoteState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10042;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_12075;
import net.minecraft.class_12249;
import net.minecraft.class_4587;
import net.minecraft.class_7920;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(class_922.class)
public abstract class EmoteBodyMixin {
   @Inject(
      method = "method_4054(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_922;method_4042(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;)V", shift = Shift.AFTER)
   )
   private void valence$emoteBody(class_10042 state, class_4587 ps, class_11659 col, class_12075 cam, CallbackInfo ci) {
      if (state instanceof EmoteState st && st.valence$emote() >= 0) {
         EmotePose p = EmoteAnimator.pose(st);
         if (p != null) {
            float w = st.valence$weight();
            int light = state.field_61820;
            EmoteProps.Emitter out = EmoteProps.worldEmitter(col, light);

            try {
               EmoteProps.world(ps, out, p, w, st.valence$time());
            } catch (Throwable var15) {
            }

            EmoteAnimator.applyRoot(ps, p, w);

            try {
               EmoteProps.body(ps, out, p, w, st.valence$time(), state instanceof class_10055 a0 && a0.field_53520.comp_1629() == class_7920.field_41122);
            } catch (Throwable var16) {
            }

            if (state instanceof class_10055 av) {
               EmotePose copy = new EmotePose();
               copy.fromArray(p.toArray(new float[57]));
               boolean slim = av.field_53520.comp_1629() == class_7920.field_41122;
               boolean[] overlay = new boolean[]{av.field_53531, av.field_53530, av.field_53546, av.field_53545};
               ps.method_22903();
               ps.method_46416(0.0F, -1.501F, 0.0F);
               col.method_73483(ps, class_12249.method_76000(av.field_53520.comp_1626().comp_3627()), (pose, vc) -> {
                  class_4587 local = new class_4587();
                  local.method_23760().method_66521(pose);
                  EmoteLimbs.draw(local, copy, w, slim, overlay, EmoteLimbs.vertices(vc, light));
               });
               ps.method_22909();
            }
         }
      }
   }
}
