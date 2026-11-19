package dev.valence.emote;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface EmoteState {
   int valence$emote();

   float valence$time();

   float valence$weight();

   void valence$setEmote(int var1, float var2, float var3);

   void valence$setEmoteOverride(int id, float time, float weight);
}
