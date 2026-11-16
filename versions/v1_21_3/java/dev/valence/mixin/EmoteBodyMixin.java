package dev.valence.mixin;

import dev.valence.compat.EmoteBatch;
import dev.valence.compat.Layers;
import dev.valence.emote.EmoteAnimator;
import dev.valence.emote.EmoteLimbs;
import dev.valence.emote.EmotePose;
import dev.valence.emote.EmoteProps;
import dev.valence.emote.EmoteState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10042;
import net.minecraft.class_10055;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_8685;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Draws emote props and the emote's skinned limbs on top of a living entity.
 *
 * <p>This is the 1.21.2 - 1.21.8 version. Here the renderer still receives the vertex
 * consumer provider and the packed light value directly, so both are taken from the
 * target method instead of from the render state.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_922.class)
public abstract class EmoteBodyMixin {
   @Inject(
      method = "method_4054(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_922;method_4042(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;)V", shift = Shift.AFTER)
   )
   private void valence$emoteBody(class_10042 state, class_4587 ps, class_4597 buffers, int light, CallbackInfo ci) {
      if (state instanceof EmoteState st && st.valence$emote() >= 0) {
         EmotePose p = EmoteAnimator.pose(st);
         if (p != null) {
            EmoteBatch col = EmoteBatch.of(buffers);
            float w = st.valence$weight();
            EmoteProps.Emitter out = EmoteProps.worldEmitter(col, light);

            try {
               EmoteProps.world(ps, out, p, w, st.valence$time());
            } catch (Throwable var16) {
            }

            EmoteAnimator.applyRoot(ps, p, w);

            try {
               EmoteProps.body(ps, out, p, w, st.valence$time(), state instanceof class_10055 a0 && a0.field_53520.comp_1629() == class_8685.class_7920.field_41122);
            } catch (Throwable var17) {
            }

            if (state instanceof class_10055 av) {
               EmotePose copy = new EmotePose();
               copy.fromArray(p.toArray(new float[57]));
               boolean slim = av.field_53520.comp_1629() == class_8685.class_7920.field_41122;
               boolean[] overlay = new boolean[]{av.field_53531, av.field_53530, av.field_53546, av.field_53545};
               ps.method_22903();
               ps.method_46416(0.0F, -1.501F, 0.0F);
               col.submit(ps, Layers.translucent(av.field_53520.comp_1626()), (pose, vc) -> {
                  class_4587 local = new class_4587();
                  local.method_23760().method_23761().set(pose.method_23761());
                  EmoteLimbs.draw(local, copy, w, slim, overlay, EmoteLimbs.vertices(vc, light));
               });
               ps.method_22909();
            }
         }
      }
   }
}
