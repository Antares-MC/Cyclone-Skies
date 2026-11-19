package dev.valence.mixin;

import dev.valence.cosmetics.Capes;
import dev.valence.emote.EmoteManager;
import dev.valence.emote.EmoteState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_742;
import net.minecraft.class_8685;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Swaps in the cosmetic cape by rewriting the player's skin lookup.
 *
 * <p>This is the 1.21 and 1.21.1 version. These versions have no render state to attach
 * per-frame data to, and the cape feature renderer reads the cape texture from the skin
 * record on every frame. Replacing the record's cape entry therefore keeps vanilla's own
 * lean and flap motion while drawing our texture, and an empty cape hides it outright
 * when a pair of wings is worn.
 *
 * <p>The same lookup is the only per-frame hook the player renderer makes, so the active
 * emote is refreshed here as well.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_742.class)
public abstract class CapeMixin {
   @Inject(method = "method_52814", at = @At("RETURN"), cancellable = true)
   private void valence$cape(CallbackInfoReturnable<class_8685> cir) {
      class_742 self = (class_742)(Object)this;
      EmoteManager.fill(self, (EmoteState)(Object)self);
      class_310 mc = class_310.method_1551();
      boolean local = self == mc.field_1724;
      dev.valence.cosmetics.Cosmetics.Equipped equipped = local
         ? dev.valence.cosmetics.Cosmetics.localEquipped()
         : dev.valence.cosmetics.Cosmetics.remoteEquipped(self.method_5667());
      class_8685 skin = cir.getReturnValue();
      boolean hasWings = local
         ? dev.valence.cosmetics.Cosmetics.wings() != null
         : equipped != null && !equipped.wings().isEmpty();
      if (hasWings) {
         cir.setReturnValue(new class_8685(skin.comp_1626(), skin.comp_1911(), (class_2960)null,
                                            skin.comp_1628(), skin.comp_1629(), skin.comp_1630()));
         return;
      }

      Capes.Cape cape = local
         ? Capes.active()
         : equipped == null ? null : Capes.byId(equipped.cape());
      if (cape != null) {
         cir.setReturnValue(new class_8685(skin.comp_1626(), skin.comp_1911(), cape.textureNow(),
                                            skin.comp_1628(), skin.comp_1629(), skin.comp_1630()));
      }
   }
}
