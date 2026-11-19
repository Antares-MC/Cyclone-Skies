package dev.valence.mixin;

import dev.valence.cosmetics.Capes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_1007;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_742;
import net.minecraft.class_8685;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces the local player's cape texture with the selected cosmetic cape, or hides the
 * cape entirely when a pair of wings is worn.
 *
 * <p>This is the 1.21.2 - 1.21.8 version. The cape lean and flap fields were added to
 * the render state after 1.21.8, so here the cape keeps vanilla's own motion and only the
 * texture and visibility are changed.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_1007.class)
public abstract class CapeMixin {
   @Inject(method = "method_62604(Lnet/minecraft/class_742;Lnet/minecraft/class_10055;F)V", at = @At("TAIL"))
   private void valence$cape(class_742 entity, class_10055 state, float partialTick, CallbackInfo ci) {
      class_310 mc = class_310.method_1551();
      boolean local = entity == mc.field_1724;
      dev.valence.cosmetics.Cosmetics.Equipped equipped = local
         ? dev.valence.cosmetics.Cosmetics.localEquipped()
         : dev.valence.cosmetics.Cosmetics.remoteEquipped(entity.method_5667());
      boolean hasWings = local
         ? dev.valence.cosmetics.Cosmetics.wings() != null
         : equipped != null && !equipped.wings().isEmpty();
      if (hasWings) {
         state.field_53532 = false;
         return;
      }
      Capes.Cape cape = local ? Capes.active() : equipped == null ? null : Capes.byId(equipped.cape());
      if (cape != null) {
         class_2960 tex = cape.textureNow();
         class_8685 s = state.field_53520;
         state.field_53520 = new class_8685(s.comp_1626(), s.comp_1911(), tex, s.comp_1628(), s.comp_1629(), s.comp_1630());
         state.field_53532 = true;
      }
   }
}
