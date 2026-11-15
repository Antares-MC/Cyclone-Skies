package dev.valence.gui.pages;

import dev.valence.config.ValenceConfig;
import dev.valence.cosmetics.Capes;
import dev.valence.cosmetics.CosmeticPreview;
import dev.valence.cosmetics.Cosmetics;
import dev.valence.cosmetics.Hats;
import dev.valence.cosmetics.Wings;
import dev.valence.gui.Anim;
import dev.valence.gui.Page;
import dev.valence.gui.Theme;
import dev.valence.gui.ValenceScreen;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10017;
import net.minecraft.class_10042;
import net.minecraft.class_10799;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5481;
import net.minecraft.class_746;
import net.minecraft.class_897;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class CosmeticsPage extends Page {
   private static final int LIST_W = 112;
   private static final int TILE_W = 48;
   private static final int TILE_H = 66;
   private static final int TILE_GAP = 6;
   private static final int PANEL_W = 124;
   private static final class_1799 CAPE_ICON = new class_1799(class_1802.field_8833);
   private float gridScroll;
   private float gridTarget;
   private CosmeticsPage.Tab tab = CosmeticsPage.Tab.HATS;
   private int hoverIndex = -1;

   @Override
   public String nameKey() {
      return "valence.page.cosmetics";
   }

   @Override
   public String subtitleKey() {
      return "valence.page.cosmetics.subtitle";
   }

   @Override
   public String icon() {
      return "hat";
   }

   @Override
   public void init(ValenceScreen screen, int x, int y, int w, int h) {
      super.init(screen, x, y, w, h);
      this.gridScroll = 0.0F;
      this.gridTarget = 0.0F;
   }

   private int count() {
      return switch (this.tab) {
         case CAPES -> Capes.ALL.size();
         case HATS -> Hats.ALL.size();
         default -> Wings.ALL.size();
      };
   }

   private int previewWidth() {
      return this.w - LIST_W - 7 > 260 ? PANEL_W : 0;
   }

   private int gridX() {
      return this.x + LIST_W + 7;
   }

   private int gridW() {
      int pw = this.previewWidth();
      return this.x + this.w - this.gridX() - (pw > 0 ? pw + 7 : 0);
   }

   private int columns() {
      return Math.max(1, (this.gridW() + TILE_GAP) / (TILE_W + TILE_GAP));
   }

   private int tileX(int i) {
      int cols = this.columns();
      int used = cols * TILE_W + (cols - 1) * TILE_GAP;
      return this.gridX() + (this.gridW() - used) / 2 + i % cols * (TILE_W + TILE_GAP);
   }

   private int tileY(int i) {
      return this.y + 16 + i / this.columns() * (TILE_H + TILE_GAP) - Math.round(this.gridScroll);
   }

   @Override
   public void render(class_332 g, int mx, int my, float pt) {
      class_327 f = this.font();
      Theme.card(g, this.x, this.y, this.x + LIST_W, this.y + this.h - 2, 234881023, 352321535);
      String[] names = new String[]{
         class_2561.method_43471("valence.styles.capes").getString(),
         class_2561.method_43471("valence.styles.hats").getString(),
         class_2561.method_43471("valence.styles.wings").getString()
      };
      int[] counts = new int[]{Capes.ALL.size(), Hats.ALL.size(), Wings.ALL.size()};
      int[] selected = new int[]{
         ValenceConfig.get().cape.isEmpty() ? 0 : 1,
         ValenceConfig.get().hat.isEmpty() ? 0 : 1,
         ValenceConfig.get().wings.isEmpty() ? 0 : 1
      };
      CosmeticsPage.Tab[] tabs = CosmeticsPage.Tab.values();

      for (int i = 0; i < tabs.length; i++) {
         int ry = this.y + 3 + i * 22;
         boolean on = tabs[i] == this.tab;
         boolean hov = this.inside(mx, my, this.x + 3, ry, this.x + LIST_W - 3, ry + 20);
         float hv = Anim.hover(Anim.key(31, i), hov);
         if (on) {
            Theme.round(g, this.x + 3, ry, this.x + LIST_W - 3, ry + 20, 1384865014);
            Theme.outline(g, this.x + 3, ry, this.x + LIST_W - 3, ry + 20, -1718919946);
            Theme.pill(g, this.x + 1, ry + 4, this.x + 3, ry + 16, -4745985);
         } else if (hv > 0.01F) {
            Theme.round(g, this.x + 3, ry, this.x + LIST_W - 3, ry + 20, Theme.withAlpha(-1, 0.05F * hv));
         }

         if (i == 0) {
            g.method_51448().pushMatrix();
            g.method_51448().translate(this.x + 7, ry + 2);
            g.method_51448().scale(0.95F, 0.95F);
            g.method_51427(CAPE_ICON, 0, 0);
            g.method_51448().popMatrix();
         } else {
            Theme.icon(g, i == 1 ? "hat" : "wings", this.x + 8, ry + 3, 13, on ? -4745985 : -8095322);
         }

         g.method_51433(f, names[i], this.x + 26, ry + 6, on ? -1 : -4608298, false);
         String c = String.valueOf(counts[i]);
         boolean used = selected[i] == 1;
         g.method_51433(f, c, this.x + LIST_W - 8 - f.method_1727(c), ry + 6, used ? -4745985 : -8095322, false);
      }

      this.renderGrid(g, f, mx, my);
      this.renderPanel(g, f, mx, my);
   }

   private void renderGrid(class_332 g, class_327 f, int mx, int my) {
      int gx = this.gridX();
      int gw = this.gridW();
      int top = this.y + 16;
      int gridH = this.h - 16 - 2;
      int n = this.count() + 1;
      int cols = this.columns();
      int rows = (n + cols - 1) / cols;
      int contentH = rows * (TILE_H + TILE_GAP);
      float max = Math.max(0, contentH - gridH);
      this.gridTarget = Math.max(0.0F, Math.min(max, this.gridTarget));
      this.gridScroll = this.gridScroll + (this.gridTarget - this.gridScroll) * Math.min(1.0F, Anim.dt() * 16.0F);
      String title = class_2561.method_43471(
            this.tab == CosmeticsPage.Tab.CAPES ? "valence.styles.capes" : this.tab == CosmeticsPage.Tab.HATS ? "valence.styles.hats" : "valence.styles.wings"
         )
         .getString();
      g.method_51433(f, title, gx, this.y + 2, -724737, false);
      class_2561 countText = class_2561.method_43469("valence.styles.count", new Object[]{this.count()});
      g.method_51439(f, countText, gx + gw - f.method_27525(countText) - 2, this.y + 2, -8095322, false);
      this.hoverIndex = -1;
      g.method_44379(gx, top, gx + gw, top + gridH);

      for (int i = 0; i < n; i++) {
         int tx = this.tileX(i);
         int ty = this.tileY(i);
         if (ty + TILE_H >= top && ty <= top + gridH) {
            boolean sel = this.isSelected(i);
            boolean hover = this.inside(mx, my, tx, ty, tx + TILE_W, ty + TILE_H) && my >= top && my < top + gridH;
            float hv = Anim.hover(Anim.key(57, i), hover);
            Theme.card(g, tx, ty, tx + TILE_W, ty + TILE_H, hv, sel);
            if (hover) {
               this.hoverIndex = i;
            }

            if (i == 0) {
               Theme.icon(g, "close", tx + TILE_W / 2 - 7, ty + 20, 14, -8095322);
               class_2561 none = class_2561.method_43471("valence.styles.cosmetics.none");
               g.method_51448().pushMatrix();
               g.method_51448().translate(tx + TILE_W / 2.0F, ty + 42.0F);
               g.method_51448().scale(0.7F, 0.7F);
               g.method_51439(f, none, -f.method_27525(none) / 2, 0, -4608298, false);
               g.method_51448().popMatrix();
            } else {
               this.drawTile(g, f, i, tx, ty, hover || sel);
            }

            if (sel) {
               Theme.pill(g, tx + TILE_W - 13, ty + 3, tx + TILE_W - 3, ty + 13, -7643914);
               Theme.icon(g, "check", tx + TILE_W - 11, ty + 4, 7, -1);
            }
         }
      }

      g.method_44380();
   }

   private void drawTile(class_332 g, class_327 f, int i, int tx, int ty, boolean animate) {
      switch (this.tab) {
         case CAPES -> {
            Capes.Cape cape = Capes.ALL.get(i - 1);
            float grow = 1.0F + 0.05F * 0.0F;
            int aw = 36;
            int ah = 58;
            g.method_51448().pushMatrix();
            g.method_51448().translate(tx + TILE_W / 2.0F, ty + 3 + ah / 2.0F);
            g.method_51448().scale(grow, grow);
            drawCapeArt(g, cape, -aw / 2, -ah / 2, aw, ah, animate);
            g.method_51448().popMatrix();
            if (cape.frames() > 1) {
               Theme.pill(g, tx + 3, ty + 3, tx + 9, ty + 9, -7643914);
            }
         }
         case HATS -> {
            Hats.Hat hat = Hats.ALL.get(i - 1);
            int index = i - 1;
            Theme.round(g, tx + 2, ty + 2, tx + TILE_W - 2, ty + TILE_W - 2, 352321535);
            CosmeticPreview.draw(g, "hat:" + hat.id(), 206.0F, false, geo -> Hats.emit(index, 0.0F, geo), tx + 2, ty + 2, TILE_W - 4, TILE_W - 4);
            this.label(g, f, hat.name(), tx, ty);
         }
         default -> {
            Wings.Wing wing = Wings.ALL.get(i - 1);
            Theme.round(g, tx + 2, ty + 2, tx + TILE_W - 2, ty + TILE_W - 2, 352321535);
            CosmeticPreview.draw(g, "wing:" + wing.id(), 32.0F, animate, geo -> Wings.emit(wing, (float)(System.currentTimeMillis() / 1000.0), geo), tx + 2, ty + 2, TILE_W - 4, TILE_W - 4);
            this.label(g, f, wing.name(), tx, ty);
         }
      }
   }

   private void label(class_332 g, class_327 f, String name, int tx, int ty) {
      String s = f.method_27523(name, TILE_W - 6);
      g.method_51448().pushMatrix();
      g.method_51448().translate(tx + TILE_W / 2.0F, ty + TILE_H - 13);
      g.method_51448().scale(0.7F, 0.7F);
      g.method_51433(f, s, -f.method_1727(s) / 2, 0, -4608298, false);
      g.method_51448().popMatrix();
   }

   private boolean isSelected(int i) {
      return switch (this.tab) {
         case CAPES -> i == 0 ? ValenceConfig.get().cape.isEmpty() : Capes.ALL.get(i - 1).id().equals(ValenceConfig.get().cape);
         case HATS -> i == 0 ? ValenceConfig.get().hat.isEmpty() : Hats.ALL.get(i - 1).id().equals(ValenceConfig.get().hat);
         default -> i == 0 ? ValenceConfig.get().wings.isEmpty() : Wings.ALL.get(i - 1).id().equals(ValenceConfig.get().wings);
      };
   }

   private void renderPanel(class_332 g, class_327 f, int mx, int my) {
      int pw = this.previewWidth();
      if (pw > 0) {
         int px = this.x + this.w - pw;
         Theme.glass(g, px, this.y, px + pw, this.y + this.h - 2, 1712130583, 1086959871, 0.4F);
         switch (this.tab) {
            case CAPES -> this.renderCapePanel(g, f, px, pw);
            case HATS -> this.renderHatPanel(g, f, px, pw);
            default -> this.renderWingPanel(g, f, px, pw);
         }
      }
   }

   private void renderCapePanel(class_332 g, class_327 f, int px, int pw) {
      String current = ValenceConfig.get().cape;
      boolean hoverNone = this.hoverIndex == 0;
      Capes.Cape show = this.hoverIndex > 0 ? Capes.ALL.get(this.hoverIndex - 1) : Capes.byId(current);
      this.drawPlayerBack(g, px + 6, this.y + 8, px + pw - 6, this.y + this.h - 54);
      String name = hoverNone ? class_2561.method_43471("valence.styles.cosmetics.none").getString() : (show == null ? "" : show.name());
      if (!name.isEmpty()) {
         String n = f.method_27523(name, pw - 10);
         g.method_51433(f, n, px + pw / 2 - f.method_1727(n) / 2, this.y + this.h - 48, -1, true);
      }

      if (show != null && !hoverNone) {
         String sub = show.group() + (show.frames() > 1 ? "  â€¢  Animated" : "");
         this.subtitle(g, f, sub, px + pw / 2.0F, this.y + this.h - 36, -8095322);
      }

      this.hint(g, f, "valence.styles.capes.hint", px, pw);
   }

   private void renderHatPanel(class_332 g, class_327 f, int px, int pw) {
      Hats.Hat show = this.hoverIndex > 0 ? Hats.ALL.get(this.hoverIndex - 1) : Cosmetics.hat();
      int size = Math.min(pw - 20, this.h - 96);
      size = Math.max(48, size);
      int art = px + (pw - size) / 2;
      Theme.round(g, art - 4, this.y + 10, art + size + 4, this.y + 18 + size, 352321535);
      if (show == null) {
         Theme.icon(g, "close", px + pw / 2 - 12, this.y + 26, 24, -8095322);
      } else {
         int index = Hats.ALL.indexOf(show);
         CosmeticPreview.draw(
            g, "hat:" + show.id(), 26.0F, false, geo -> Hats.emit(index, 0.0F, geo), px + (pw - size) / 2, this.y + 14, size, size
         );
         String n = f.method_27523(show.name(), pw - 10);
         g.method_51433(f, n, px + pw / 2 - f.method_1727(n) / 2, this.y + this.h - 46, -1, true);
         this.subtitle(g, f, show.group(), px + pw / 2.0F, this.y + this.h - 34, -8095322);
      }

      this.hint(g, f, "valence.styles.hats.hint", px, pw);
   }

   private void renderWingPanel(class_332 g, class_327 f, int px, int pw) {
      Wings.Wing show = this.hoverIndex > 0 ? Wings.ALL.get(this.hoverIndex - 1) : Cosmetics.wings();
      int size = Math.min(pw - 20, this.h - 96);
      size = Math.max(48, size);
      int art = px + (pw - size) / 2;
      Theme.round(g, art - 4, this.y + 10, art + size + 4, this.y + 18 + size, 352321535);
      if (show == null) {
         Theme.icon(g, "close", px + pw / 2 - 12, this.y + 26, 24, -8095322);
      } else {
         CosmeticPreview.draw(
            g,
            "wing:" + show.id(),
            208.0F,
            true,
            geo -> Wings.emit(show, (float)(System.currentTimeMillis() / 1000.0), geo),
            px + (pw - size) / 2,
            this.y + 14,
            size,
            size
         );
         String n = f.method_27523(show.name(), pw - 10);
         g.method_51433(f, n, px + pw / 2 - f.method_1727(n) / 2, this.y + this.h - 46, -1, true);
         this.subtitle(g, f, show.group() + "  â€¢  Animated", px + pw / 2.0F, this.y + this.h - 34, -8095322);
      }

      this.hint(g, f, "valence.styles.wings.hint", px, pw);
   }

   private void subtitle(class_332 g, class_327 f, String text, float cx, int y, int color) {
      g.method_51448().pushMatrix();
      g.method_51448().translate(cx, y);
      g.method_51448().scale(0.75F, 0.75F);
      g.method_51433(f, text, -f.method_1727(text) / 2, 0, color, false);
      g.method_51448().popMatrix();
   }

   private void hint(class_332 g, class_327 f, String key, int px, int pw) {
      class_2561 hint = class_2561.method_43471(key);
      List<class_5481> lines = f.method_1728(hint, (int)((pw - 12) / 0.7F));

      for (int i = 0; i < lines.size(); i++) {
         g.method_51448().pushMatrix();
         g.method_51448().translate(px + pw / 2.0F, this.y + this.h - 8 - (lines.size() - i) * 7);
         g.method_51448().scale(0.7F, 0.7F);
         g.method_51430(f, lines.get(i), -f.method_30880(lines.get(i)) / 2, 0, -8095322, false);
         g.method_51448().popMatrix();
      }
   }

   private static void drawCapeArt(class_332 g, Capes.Cape cape, int x0, int y0, int w, int h, boolean animate) {
      int s = cape.scale();
      class_2960 tex = animate ? cape.textureNow() : cape.texture(0);
      g.method_51448().pushMatrix();
      g.method_51448().translate(x0, y0);
      g.method_51448().scale(w / (10.0F * s), h / (16.0F * s));
      g.method_25302(class_10799.field_56883, tex, 0, 0, s, s, 10 * s, 16 * s, 10 * s, 16 * s, 64 * s, 32 * s);
      g.method_51448().popMatrix();
   }

   private void drawPlayerBack(class_332 g, int x0, int y0, int x1, int y1) {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         try {
            class_897<? super class_746, ?> renderer = mc.method_1561().method_3953(mc.field_1724);
            class_10017 state = renderer.method_62425(mc.field_1724, 1.0F);
            state.field_61820 = 15728880;
            state.field_61823.clear();
            state.field_61821 = 0;
            float turn = (float)Math.sin(System.currentTimeMillis() / 1400.0) * 28.0F;
            if (state instanceof class_10042 l) {
               l.field_53446 = turn;
               l.field_53447 = 0.0F;
               l.field_53448 = 6.0F;
               l.field_53329 = l.field_53329 / l.field_53453;
               l.field_53330 = l.field_53330 / l.field_53453;
               l.field_53453 = 1.0F;
            }

            Vector2f a = g.method_51448().transformPosition(new Vector2f(x0, y0));
            Vector2f b = g.method_51448().transformPosition(new Vector2f(x1, y1));
            x0 = Math.round(a.x);
            y0 = Math.round(a.y);
            x1 = Math.round(b.x);
            y1 = Math.round(b.y);
            int size = (int)Math.min((y1 - y0) / 2.5F, (x1 - x0) / 1.25F);
            Quaternionf q = new Quaternionf().rotateZ((float) Math.PI);
            Quaternionf q2 = new Quaternionf().rotateX((float)Math.toRadians(-8.0));
            q.mul(q2);
            Vector3f off = new Vector3f(0.0F, state.field_53330 / 2.0F + 0.0625F, 0.0F);
            g.method_70856(state, size, off, q, q2, x0, y0, x1, y1);
         } catch (Throwable var16) {
         }
      }
   }

   @Override
   public boolean mouseClicked(double mx, double my, int button) {
      CosmeticsPage.Tab[] tabs = CosmeticsPage.Tab.values();

      for (int i = 0; i < tabs.length; i++) {
         int ry = this.y + 3 + i * 22;
         if (this.inside(mx, my, this.x + 3, ry, this.x + LIST_W - 3, ry + 20)) {
            if (this.tab != tabs[i]) {
               this.tab = tabs[i];
               this.gridTarget = 0.0F;
               this.gridScroll = 0.0F;
               this.hoverIndex = -1;
               click();
            }

            return true;
         }
      }

      int top = this.y + 16;
      int gridH = this.h - 16 - 2;
      int n = this.count() + 1;

      for (int i = 0; i < n; i++) {
         int tx = this.tileX(i);
         int ty = this.tileY(i);
         if (this.inside(mx, my, tx, ty, tx + TILE_W, ty + TILE_H) && my >= top && my < top + gridH) {
            this.pick(i);
            return true;
         }
      }

      return false;
   }

   private void pick(int i) {
      ValenceConfig cfg = ValenceConfig.get();
      switch (this.tab) {
         case CAPES -> {
            String id = i == 0 ? "" : Capes.ALL.get(i - 1).id();
            cfg.cape = id.equals(cfg.cape) ? "" : id;
         }
         case HATS -> {
            String id = i == 0 ? "" : Hats.ALL.get(i - 1).id();
            cfg.hat = id.equals(cfg.hat) ? "" : id;
         }
         default -> {
            String id = i == 0 ? "" : Wings.ALL.get(i - 1).id();
            cfg.wings = id.equals(cfg.wings) ? "" : id;
         }
      }

      ValenceConfig.save();
      click();
   }

   @Override
   public boolean mouseScrolled(double mx, double my, double amount) {
      if (mx >= this.gridX()) {
         this.gridTarget -= (float)amount * 30.0F;
      }

      return true;
   }

   @Environment(EnvType.CLIENT)
   private enum Tab {
      CAPES,
      HATS,
      WINGS
   }
}
