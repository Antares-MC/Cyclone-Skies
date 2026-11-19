package dev.valence.mixin;

import dev.valence.emote.EmoteState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(class_10055.class)
public abstract class EmoteRenderStateMixin implements EmoteState {
   @Unique
   private int valence$emote = -1;
   @Unique
   private float valence$time;
   @Unique
   private float valence$weight;

   @Override
   public int valence$emote() {
      return this.valence$emote;
   }

   @Override
   public float valence$time() {
      return this.valence$time;
   }

   @Override
   public float valence$weight() {
      return this.valence$weight;
   }

   @Override
   public void valence$setEmote(int id, float time, float weight) {
      this.valence$emote = id;
      this.valence$time = time;
      this.valence$weight = weight;
   }

   @Override
   public void valence$setEmoteOverride(int id, float time, float weight) {
      this.valence$emote = id;
      this.valence$time = time;
      this.valence$weight = weight;
   }
}
