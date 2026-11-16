package dev.valence.mixin;

import dev.valence.emote.EmoteState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_742;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Carries the active emote on the player itself.
 *
 * <p>This is the 1.21 and 1.21.1 version. These versions draw entities straight from the
 * entity, with no intermediate render state, so the player is the state the emote model and
 * body hooks read from. From 1.21.2 on the same fields live on the player render state
 * instead, because the entity is no longer available during the model pass.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_742.class)
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
}
