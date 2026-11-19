package dev.valence.mixin;

import dev.valence.cosmetics.Capes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_1007;
import net.minecraft.class_11890;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_8685;
import net.minecraft.class_12079.class_10726;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(class_1007.class)
public abstract class CapeMixin {
   @Unique
   private static float lean2;
   @Unique
   private static float lean;
   @Unique
   private static float flap;
   @Unique
   private static long lastNanos;

   @Inject(method = "method_62604(Lnet/minecraft/class_11890;Lnet/minecraft/class_10055;F)V", at = @At("TAIL"))
   private void valence$cape(class_11890 entity, class_10055 state, float partialTick, CallbackInfo ci) {
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

      Capes.Cape cape = local
         ? Capes.active()
         : equipped == null ? null : Capes.byId(equipped.cape());
      if (cape == null) return;

      class_2960 tex = cape.textureNow();
      class_8685 s = state.field_53520;
      state.field_53520 = new class_8685(s.comp_1626(), new class_10726(tex, tex), s.comp_1628(), s.comp_1629(), s.comp_1630());
      state.field_53532 = true;
      if (!local) return;
      long now = System.nanoTime();
      float dt = lastNanos == 0L ? 0.016F : Math.min(0.1F, (float)(now - lastNanos) / 1.0E9F);
      lastNanos = now;
      float k = 1.0F - (float)Math.exp(-dt * 9.0F);
      lean2 = lean2 + (state.field_53538 * 0.3F - lean2) * k;
      lean = lean + (state.field_53537 - lean) * k;
      flap = flap + (state.field_53536 - flap) * k;
      state.field_53538 = lean2;
      state.field_53537 = lean;
      state.field_53536 = flap;
   }
}
