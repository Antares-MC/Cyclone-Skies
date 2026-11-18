package dev.valence.emote;

import dev.valence.compat.Draw;
import dev.valence.compat.InputScreen;
import dev.valence.compat.Keys;
import dev.valence.compat.Pipes;
import dev.valence.compat.Pose;
import dev.valence.config.ValenceConfig;
import dev.valence.gui.Theme;
import dev.valence.gui.UiSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_332;
import net.minecraft.class_3675;
import net.minecraft.class_437;
import net.minecraft.class_3675.class_306;
import net.minecraft.class_3675.class_307;

@Environment(EnvType.CLIENT)
public final class EmoteWheelScreen extends InputScreen {
   private static final class_2960 WEDGE = class_2960.method_60655("valence", "textures/emote/wedge.png");
   private static final class_2960 RIM = class_2960.method_60655("valence", "textures/emote/rim.png");
   private static final class_2960 DISC = class_2960.method_60655("valence", "textures/emote/disc.png");
   private static final class_2960 CIRCLE = class_2960.method_60655("valence", "textures/emote/circle.png");
   private static int page;
   private static final int NO_PICK = -2;
   private final class_304 key;
   private final long openedAt = System.currentTimeMillis();
   private final long openedNanos = System.nanoTime();
   private boolean clickMode;
   private int hovered = -1;
   private long hoverSince = System.nanoTime();
   private float pageAnim = 1.0F;
   private float open;
   private long lastFrame;
   private int pendingPick = NO_PICK;
   private long closeAt;
   private final float[] hoverAnim = new float[8];

   public EmoteWheelScreen(class_304 key) {
      super(class_2561.method_43470("Emotes"));
      this.key = key;
      if (page >= Emotes.pages()) {
         page = 0;
      }
   }

   public boolean method_25421() {
      return false;
   }

   private boolean keyHeld() {
      try {
         return Keys.defaultPressed(this.key);
      } catch (Throwable e) {
         return false;
      }
   }

   private float radius() {
      return Math.max(64.0F, Math.min(128.0F, Math.min(this.field_22790 * 0.4F, this.field_22789 * 0.25F)));
   }

   private int slotAt(double mx, double my) {
      float r = this.radius();
      double dx = mx - this.field_22789 / 2.0;
      double dy = my - this.field_22790 / 2.0;
      double d = Math.sqrt(dx * dx + dy * dy);
      if (d < r * 0.3) {
         return -1;
      }

      double ang = Math.toDegrees(Math.atan2(dx, -dy));
      if (ang < 0.0) {
         ang += 360.0;
      }

      int slot = (int)Math.round(ang / 45.0) % 8;
      int id = page * 8 + slot;
      return id < Emotes.ALL.size() ? id : -1;
   }

   private void pick(int id) {
      this.requestClose(id);
   }

   /**
    * Closes the wheel and remembers what was chosen.
    *
    * <p>The close has to happen on a client tick, not inside a render pass: the emote is only
    * allowed to start when no screen is open, and swapping the screen mid-render is how the
    * wheel used to end up as a screen that never closed -- which left the player unable to move.
    */
   private void requestClose(int id) {
      if (this.closeAt != 0L) {
         return;
      }

      if (id >= 0) {
         UiSounds.click();
      }

      this.pendingPick = id;
      this.closeAt = System.nanoTime();
   }

   public void method_25419() {
      ValenceConfig.save();
      this.requestClose(-1);
   }

   public void method_25393() {
      if (this.field_22787.field_1724 == null) {
         this.pendingPick = NO_PICK;
         this.closeAt = 0L;
         this.field_22787.method_1507((class_437)null);
         return;
      }

      if (this.closeAt != 0L && System.nanoTime() - this.closeAt >= 150000000L) {
         int id = this.pendingPick;
         this.pendingPick = NO_PICK;
         this.closeAt = 0L;
         this.field_22787.method_1507((class_437)null);
         EmoteManager.start(id);
      }
   }

   public void method_25420(class_332 g, int mouseX, int mouseY, float pt) {
   }

   public void method_25394(class_332 g, int mouseX, int mouseY, float pt) {
      long now = System.nanoTime();
      float dt = this.lastFrame == 0L ? 0.016F : Math.min(0.1F, (float)(now - this.lastFrame) / 1.0E9F);
      this.lastFrame = now;
      this.open = Math.min(1.0F, this.open + dt * 7.0F);
      this.pageAnim = Math.min(1.0F, this.pageAnim + dt * 6.0F);
      if (!this.clickMode && !this.keyHeld()) {
         if (System.currentTimeMillis() - this.openedAt >= 230L) {
            this.pick(this.slotAt(mouseX, mouseY));
            return;
         }

         this.clickMode = true;
      }

      int h = this.slotAt(mouseX, mouseY);
      if (h != this.hovered) {
         this.hovered = h;
         this.hoverSince = now;
         if (h >= 0) {
            UiSounds.hover();
         }
      }

      float ease = 1.0F - (1.0F - this.open) * (1.0F - this.open) * (1.0F - this.open);
      float figureTime = (float)(now - this.openedNanos) / 1.0E9F;
      float r = this.radius() * (0.86F + 0.14F * ease);
      int cx = this.field_22789 / 2;
      int cy = this.field_22790 / 2;
      int alpha = (int)(255.0F * ease);
      g.method_25294(0, 0, this.field_22789, this.field_22790, (int)(70.0F * ease) << 24);
      tex(g, DISC, cx, cy, r * 1.32F, 0.0F, (int)(120.0F * ease) << 24 | 328458);
      float pa = this.pageAnim < 1.0F ? 0.4F + 0.6F * this.pageAnim : 1.0F;

      for (int i = 0; i < 8; i++) {
         int id = page * 8 + i;
         Emotes.Emote e = Emotes.get(id);
         boolean hov = id == this.hovered;
         this.hoverAnim[i] = this.hoverAnim[i] + ((hov ? 1.0F : 0.0F) - this.hoverAnim[i]) * Math.min(1.0F, dt * 14.0F);
         float k = this.hoverAnim[i];
         float ang = (float)Math.toRadians(i * 45.0);
         float rr = r * (1.0F + 0.05F * k);
         int fill;
         if (e == null) {
            fill = (int)(60.0F * ease) << 24 | 789012;
         } else {
            int base = 1380132;
            int col = lerpRgb(base, e.color() & 16777215, 0.55F * k);
            fill = (int)((150.0F + 80.0F * k) * ease * pa) << 24 | col;
         }

         tex(g, WEDGE, cx, cy, rr, ang, fill);
         if (e != null) {
            int rimCol = k > 0.01F ? lerpRgb(9133302, e.color() & 16777215, 0.6F) : 13218047;
            tex(g, RIM, cx, cy, rr, ang, (int)((40.0F + 200.0F * k) * ease * pa) << 24 | rimCol);
            float mid = rr * 0.66F;
            float ix = cx + (float)Math.sin(ang) * mid;
            float iy = cy - (float)Math.cos(ang) * mid;
            int a = (int)(alpha * pa);
            float fw = rr * 0.2F * (1.0F + 0.12F * k);
            float fh = rr * 0.24F * (1.0F + 0.12F * k);
            EmoteFigure.draw(g, ix - fw, iy - fh - rr * 0.04F, ix + fw, iy + fh * 0.55F, id, figureTime, 20.0F, (0.75F + 0.25F * k) * ease * pa);
            Pose.push(g);
            Pose.translate(g, ix, iy + rr * 0.135F);
            float ns = Math.min(0.75F, rr * 0.36F / Math.max(1, this.field_22793.method_1727(e.name())));
            Pose.scale(g, ns, ns);
            String name = e.name();
            int nw = this.field_22793.method_1727(name);
            g.method_51433(this.field_22793, name, -nw / 2, 0, (int)(a * (0.75F + 0.25F * k)) << 24 | (k > 0.5F ? 16777215 : 14998783), true);
            Pose.pop(g);
         }
      }

      tex(g, DISC, cx, cy, r * 0.37F, 0.0F, (int)(215.0F * ease) << 24 | 854551);
      tex(g, CIRCLE, cx, cy, r * 0.37F, 0.0F, (int)(120.0F * ease) << 24 | 13218047);
      Emotes.Emote he = Emotes.get(this.hovered);
      String title = he != null ? he.name() : "Emotes";
      float ts = he != null && this.field_22793.method_1727(title) > r * 0.62F ? r * 0.62F / this.field_22793.method_1727(title) : 1.0F;
      Pose.push(g);
      Pose.translate(g, cx, cy - 10.0F);
      Pose.scale(g, ts, ts);
      g.method_51433(this.field_22793, title, -this.field_22793.method_1727(title) / 2, 0, alpha << 24 | (he != null ? 16777215 : 12168918), true);
      Pose.pop(g);
      String pg = page + 1 + " / " + Emotes.pages();
      Pose.push(g);
      Pose.translate(g, cx, cy + 3.0F);
      Pose.scale(g, 0.75F, 0.75F);
      g.method_51433(this.field_22793, pg, -this.field_22793.method_1727(pg) / 2, 0, (int)(alpha * 0.8F) << 24 | 12168918, false);
      Pose.pop(g);
      int pages = Emotes.pages();

      for (int i = 0; i < pages; i++) {
         int dx = cx - (pages - 1) * 4 + i * 8;
         int col = i == page ? 16777215 : 8681894;
         g.method_25294(dx - 2, cy + 13, dx + 2, cy + 15, alpha << 24 | col);
      }

      String hint = this.clickMode
         ? "Click an emote  •  Scroll for more"
         : "Release " + this.key.method_16007().getString() + " on an emote  •  Scroll for more";
      Pose.push(g);
      Pose.translate(g, cx, cy + r * 1.32F + 6.0F);
      Pose.scale(g, 0.8F, 0.8F);
      g.method_51433(this.field_22793, hint, -this.field_22793.method_1727(hint) / 2, 0, (int)(alpha * 0.75F) << 24 | 12168918, false);
      Pose.pop(g);
      int pw = 104;
      int ph = 150;
      int px = (int)(cx + r * 1.32F + 16.0F);
      if (he != null && px + pw <= this.field_22789 - 6) {
         int py = cy - ph / 2;
         Theme.glass(g, px, py, px + pw, py + ph, -1207105001, 1086959871, 0.6F);
         EmoteFigure.draw(g, px + 8, py + 8, px + pw - 8, py + ph - 22, this.hovered, (float)(now - this.hoverSince) / 1.0E9F, 20.0F, ease);
         String n = he.name();
         g.method_51433(this.field_22793, n, px + pw / 2 - this.field_22793.method_1727(n) / 2, py + ph - 16, alpha << 24 | 16777215, true);
      }
   }

   private static void tex(class_332 g, class_2960 id, float cx, float cy, float r, float angle, int argb) {
      Pose.push(g);
      Pose.translate(g, cx, cy);
      if (angle != 0.0F) {
         Pose.rotate(g, angle);
      }

      Pose.scale(g, r / 128.0F, r / 128.0F);
      Draw.region(g, id, -128, -128, 0.0F, 0.0F, 256, 256, 256, 256, 256, 256, argb);
      Pose.pop(g);
   }

   private static int lerpRgb(int a, int b, float k) {
      k = Math.max(0.0F, Math.min(1.0F, k));
      int r = (int)((a >> 16 & 0xFF) + ((b >> 16 & 0xFF) - (a >> 16 & 0xFF)) * k);
      int gg = (int)((a >> 8 & 0xFF) + ((b >> 8 & 0xFF) - (a >> 8 & 0xFF)) * k);
      int bb = (int)((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * k);
      return r << 16 | gg << 8 | bb;
   }

   public boolean method_25401(double mx, double my, double sx, double sy) {
      int pages = Emotes.pages();
      if (pages > 1 && sy != 0.0) {
         page = Math.floorMod(page + (sy < 0.0 ? 1 : -1), pages);
         this.pageAnim = 0.0F;
         this.hovered = -2;
         UiSounds.click();
      }

      return true;
   }

   protected boolean onClick(double x, double y, int button, Object raw, boolean doubleClick) {
      if (button == 0) {
         this.pick(this.slotAt(x, y));
         return true;
      } else if (button == 1) {
         this.method_25419();
         return true;
      } else {
         return true;
      }
   }

   protected boolean onKey(int keyCode, int scanCode, int modifiers) {
      if (this.clickMode && Keys.matches(this.key, keyCode, scanCode)) {
         this.method_25419();
         return true;
      } else {
         return false;
      }
   }
}
