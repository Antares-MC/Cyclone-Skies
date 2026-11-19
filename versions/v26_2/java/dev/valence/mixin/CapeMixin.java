package dev.valence.mixin;

import dev.valence.cosmetics.Capes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.Avatar;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.core.ClientAsset.ResourceTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AvatarRenderer.class)
public abstract class CapeMixin {
   @Unique
   private static float lean2;
   @Unique
   private static float lean;
   @Unique
   private static float flap;
   @Unique
   private static long lastNanos;

   @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
   private void valence$cape(Avatar entity, AvatarRenderState state, float partialTick, CallbackInfo ci) {
      Minecraft mc = Minecraft.getInstance();
      boolean local = entity == mc.player;
      dev.valence.cosmetics.Cosmetics.Equipped equipped = local
         ? dev.valence.cosmetics.Cosmetics.localEquipped()
         : dev.valence.cosmetics.Cosmetics.remoteEquipped(entity.getUUID());
      boolean hasWings = local
         ? dev.valence.cosmetics.Cosmetics.wings() != null
         : equipped != null && !equipped.wings().isEmpty();
      if (hasWings) {
         state.showCape = false;
         return;
      }
      Capes.Cape cape = local ? Capes.active() : equipped == null ? null : Capes.byId(equipped.cape());
      if (cape == null) return;
      Identifier tex = cape.textureNow();
      PlayerSkin s = state.skin;
      state.skin = new PlayerSkin(s.body(), new ResourceTexture(tex, tex), s.elytra(), s.model(), s.secure());
      state.showCape = true;
      if (!local) return;
      long now = System.nanoTime();
      float dt = lastNanos == 0L ? 0.016F : Math.min(0.1F, (float)(now - lastNanos) / 1.0E9F);
      lastNanos = now;
      float k = 1.0F - (float)Math.exp(-dt * 9.0F);
      lean2 = lean2 + (state.capeLean2 * 0.3F - lean2) * k;
      lean = lean + (state.capeLean - lean) * k;
      flap = flap + (state.capeFlap - flap) * k;
      state.capeLean2 = lean2;
      state.capeLean = lean;
      state.capeFlap = flap;
   }
}
