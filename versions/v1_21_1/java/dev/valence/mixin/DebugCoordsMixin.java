package dev.valence.mixin;

import dev.valence.hud.RandomCoords;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_340;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Random coordinates on the debug screen, 1.21.8 and older.
 *
 * <p>These versions build the whole F3 text up front as two string lists, so the shift is
 * applied to the finished lists. The position entry of 1.21.9 and newer is handled by the
 * same-named mixin in the shared tree.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_340.class)
public abstract class DebugCoordsMixin {
   @Inject(method = "method_1835", at = @At("RETURN"), require = 0)
   private void valence$left(CallbackInfoReturnable<List<String>> cir) {
      valence$rewrite(cir);
   }

   @Inject(method = "method_1839", at = @At("RETURN"), require = 0)
   private void valence$right(CallbackInfoReturnable<List<String>> cir) {
      valence$rewrite(cir);
   }

   private static void valence$rewrite(CallbackInfoReturnable<List<String>> cir) {
      List<String> lines = cir.getReturnValue();
      if (lines == null || !RandomCoords.enabled()) {
         return;
      }

      List<String> out = new ArrayList<>(lines.size());
      for (String line : lines) {
         out.add(RandomCoords.spoof(line));
      }

      cir.setReturnValue(out);
   }
}
