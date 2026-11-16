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
import net.minecraft.class_1664;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_742;
import net.minecraft.class_8685;
import net.minecraft.class_922;
import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Draws emote props and the emote's skinned limbs on top of a living entity.
 *
 * <p>This is the 1.21 and 1.21.1 version. These versions have no render state: the renderer
 * is handed the entity, the pose stack, the buffer provider and the packed light directly,
 * so the emote data is read from the entity and the layer flags from its skin and model
 * parts.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_922.class)
public abstract class EmoteBodyMixin {
   @Inject(
      method = "method_4054(Lnet/minecraft/class_1309;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_922;method_4042(Lnet/minecraft/class_1309;Lnet/minecraft/class_4587;F)V", shift = Shift.AFTER)
   )
   private void valence$emoteBody(class_1309 entity, float yaw, float tickDelta, class_4587 ps,
                                  class_4597 buffers, int light, CallbackInfo ci) {
      if (!(entity instanceof EmoteState st) || st.valence$emote() < 0) {
         return;
      }

      EmotePose p = EmoteAnimator.pose(st);
      if (p == null) {
         return;
      }

      EmoteBatch col = EmoteBatch.of(buffers);
      float w = st.valence$weight();
      EmoteProps.Emitter out = EmoteProps.worldEmitter(col, light);
      class_8685 skin = entity instanceof class_742 player ? player.method_52814() : null;
      boolean slim = skin != null && skin.comp_1629() == class_8685.class_7920.field_41122;

      try {
         EmoteProps.world(ps, out, p, w, st.valence$time());
      } catch (Throwable var17) {
      }

      EmoteAnimator.applyRoot(ps, p, w);

      try {
         EmoteProps.body(ps, out, p, w, st.valence$time(), slim);
      } catch (Throwable var18) {
      }

      if (entity instanceof class_742 player) {
         EmotePose copy = new EmotePose();
         copy.fromArray(p.toArray(new float[57]));
         boolean[] overlay = new boolean[]{
            player.method_7348(class_1664.field_7570), player.method_7348(class_1664.field_7568),
            player.method_7348(class_1664.field_7565), player.method_7348(class_1664.field_7566)
         };
         ps.method_22903();
         ps.method_46416(0.0F, -1.501F, 0.0F);
         col.submit(ps, Layers.translucent(player.method_52814().comp_1626()), (pose, vc) -> {
            class_4587 local = new class_4587();
            local.method_23760().method_23761().set(pose.method_23761());
            EmoteLimbs.draw(local, copy, w, slim, overlay, EmoteLimbs.vertices(vc, light));
         });
         ps.method_22909();
      }
   }
}
