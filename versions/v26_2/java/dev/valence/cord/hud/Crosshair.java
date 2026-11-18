package dev.valence.cord.hud;

import dev.valence.compat.Hud;
import dev.valence.cord.NeonLyrics;
import dev.valence.cord.config.Config;
import dev.valence.cord.lyrics.NowPlaying;
import dev.valence.cord.lyrics.SpotifyClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

@Environment(EnvType.CLIENT)
public final class Crosshair {
   private Crosshair() {
   }

   public static Hud.Element wrap(Hud.Element original) {
      return g -> {
         Config c = NeonLyrics.cfg();
         net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
         if (c.crosshair > 0 && mc.player != null && mc.options.getCameraType().isFirstPerson() && !mc.gui.hud.isHidden()) {
            draw(g, c);
         } else {
            original.render(g);
         }
      };
   }

   /** True while the styled crosshair replaces the vanilla one. */
   public static boolean suppressing() {
      Config c = NeonLyrics.cfg();
      net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
      return c.crosshair > 0 && mc.player != null && mc.options.getCameraType().isFirstPerson() && !mc.gui.hud.isHidden();
   }

   private static void draw(net.minecraft.client.gui.GuiGraphicsExtractor g, Config c) {
      float old = Gfx.alpha;
      Gfx.alpha = 1.0F;
      float cx = g.guiWidth() / 2.0F;
      float cy = g.guiHeight() / 2.0F;
      int col = 16777215;
      int sh = 0;
      switch (c.crosshair) {
         case 1:
            Gfx.aaCircle(g, cx, cy, 2.2F, sh, 0.45F);
            Gfx.aaCircle(g, cx, cy, 1.4F, col, 1.0F);
            break;
         case 2:
            cross(g, cx, cy, 2.5F, 4.5F, col, sh);
            break;
         case 3:
            ring(g, cx, cy, 4.5F, sh, 0.45F, 1.6F);
            ring(g, cx, cy, 4.5F, col, 1.0F, 0.9F);
            Gfx.aaCircle(g, cx, cy, 0.9F, col, 1.0F);
            break;
         case 4:
            NowPlaying np = SpotifyClient.now();
            float beat = np.playing ? np.beatPulse(np.progressNow(c.lyricsOffsetMs)) : 0.0F;
            cross(g, cx, cy, 2.0F + 2.5F * beat, 4.0F, Gfx.lerpColor(col, Theme.accent(), beat), sh);
            Gfx.aaCircle(g, cx, cy, 0.9F, col, 1.0F);
      }

      Gfx.alpha = old;
   }

   private static void cross(net.minecraft.client.gui.GuiGraphicsExtractor g, float cx, float cy, float gap, float len, int col, int sh) {
      float w = 1.2F;

      for (int pass = 0; pass < 2; pass++) {
         float e = pass == 0 ? 0.6F : 0.0F;
         int cc = pass == 0 ? sh : col;
         float a = pass == 0 ? 0.45F : 1.0F;
         Gfx.aaRound(g, cx - w / 2.0F - e, cy - gap - len - e, w + 2.0F * e, len + 2.0F * e, 0.6F, cc, a);
         Gfx.aaRound(g, cx - w / 2.0F - e, cy + gap - e, w + 2.0F * e, len + 2.0F * e, 0.6F, cc, a);
         Gfx.aaRound(g, cx - gap - len - e, cy - w / 2.0F - e, len + 2.0F * e, w + 2.0F * e, 0.6F, cc, a);
         Gfx.aaRound(g, cx + gap - e, cy - w / 2.0F - e, len + 2.0F * e, w + 2.0F * e, 0.6F, cc, a);
      }
   }

   private static void ring(net.minecraft.client.gui.GuiGraphicsExtractor g, float cx, float cy, float r, int col, float a, float dot) {
      int n = 28;

      for (int i = 0; i < n; i++) {
         double an = i * Math.PI * 2.0 / n;
         Gfx.aaCircle(g, (float)(cx + Math.cos(an) * r), (float)(cy + Math.sin(an) * r), dot / 2.0F + 0.3F, col, a);
      }
   }
}
