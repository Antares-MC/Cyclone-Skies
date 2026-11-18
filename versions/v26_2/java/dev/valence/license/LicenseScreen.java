package dev.valence.license;

import dev.valence.compat.InputScreen;
import dev.valence.compat.Pose;
import dev.valence.gui.Anim;
import dev.valence.gui.OptionList;
import dev.valence.gui.Theme;
import dev.valence.gui.UiSounds;
import dev.valence.gui.ValenceScreen;
import dev.valence.styles.StyleManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.core.Holder;

@Environment(EnvType.CLIENT)
public class LicenseScreen extends InputScreen {
   private static final int W = 280;
   private static final int H = 190;
   private static final int OK_COLOR = -9508952;
   private static final int BAD_COLOR = -495247;
   private final net.minecraft.client.gui.screens.Screen parent;
   private final boolean openMenuAfter;
   private net.minecraft.client.gui.components.EditBox field;
   private long openedAt;
   private boolean busy;
   private long busySince;
   private String message = "";
   private int messageColor = -8095322;
   private long messageAt;
   private long shakeAt;
   private long successAt;
   private String lastText = "";

   public LicenseScreen(net.minecraft.client.gui.screens.Screen parent, boolean openMenuAfter) {
      super(net.minecraft.network.chat.Component.translatable("valence.license.title"));
      this.parent = parent;
      this.openMenuAfter = openMenuAfter;
   }

   private int px() {
      return (this.width - 280) / 2;
   }

   private int py() {
      return (this.height - 190) / 2;
   }

   protected void init() {
      if (this.openedAt == 0L) {
         this.openedAt = System.currentTimeMillis();
      }

      String keep = this.field != null ? this.field.getValue() : "";
      this.field = new net.minecraft.client.gui.components.EditBox(this.font, this.px() + 58, this.py() + 100, 180, 12, net.minecraft.network.chat.Component.translatable("valence.license.field"));
      this.field.setBordered(false);
      this.field.setMaxLength(9);
      this.field.setTextColor(this.successAt != 0L ? -9508952 : -1);
      this.field.setValue(keep);
      this.field.setHint(net.minecraft.network.chat.Component.literal("XXXX-XXXX").withColor(-9542008));
      this.field.setResponder(this::format);
      this.addRenderableWidget(this.field);
      this.setInitialFocus(this.field);
   }

   private void format(String s) {
      String n = License.normalize(s);
      if (n.length() > 8) {
         n = n.substring(0, 8);
      }

      String f = n.length() > 4 ? n.substring(0, 4) + "-" + n.substring(4) : n;
      if (!f.equals(s)) {
         this.field.setValue(f);
         this.field.moveCursorToEnd(false);
      }

      if (!f.equals(this.lastText)) {
         this.lastText = f;
         if (!this.busy && this.successAt == 0L && this.messageColor == -495247) {
            this.message = "";
         }
      }
   }

   private void redeem() {
      if (!this.busy && this.successAt == 0L) {
         String code = this.field.getValue();
         if (License.normalize(code).length() != 8) {
            this.fail(net.minecraft.network.chat.Component.translatable("valence.license.short").getString());
         } else {
            this.busy = true;
            this.busySince = System.currentTimeMillis();
            UiSounds.click();
            License.redeem(code).thenAccept(r -> net.minecraft.client.Minecraft.getInstance().execute(() -> this.done(r)));
         }
      }
   }

   private void done(License.Result r) {
      this.busy = false;
      switch (r) {
         case OK:
            this.successAt = System.currentTimeMillis();
            this.message = net.minecraft.network.chat.Component.translatable("valence.license.ok").getString();
            this.messageColor = -9508952;
            this.messageAt = this.successAt;
            this.field.setTextColor(-9508952);
            this.field.setTextColorUneditable(-9508952);
            this.field.setEditable(false);
            play(net.minecraft.sounds.SoundEvents.NOTE_BLOCK_BELL, 1.5F, 0.9F);
            play(net.minecraft.sounds.SoundEvents.NOTE_BLOCK_CHIME, 2.0F, 0.5F);

            try {
               StyleManager.markDirty();
               StyleManager.apply();
            } catch (Throwable var3) {
            }
            break;
         case USED:
            this.fail(net.minecraft.network.chat.Component.translatable("valence.license.used").getString());
            break;
         case OFFLINE:
            this.fail(net.minecraft.network.chat.Component.translatable("valence.license.offline").getString());
            break;
         case INVALID:
            this.fail(net.minecraft.network.chat.Component.translatable("valence.license.invalid").getString());
            break;
         default:
            this.fail(net.minecraft.network.chat.Component.translatable("valence.license.error").getString());
      }
   }

   private void fail(String text) {
      this.message = text;
      this.messageColor = -495247;
      this.messageAt = System.currentTimeMillis();
      this.shakeAt = this.messageAt;
      play(net.minecraft.sounds.SoundEvents.NOTE_BLOCK_BASS, 0.75F, 1.0F);
      play(net.minecraft.sounds.SoundEvents.NOTE_BLOCK_BASS, 0.6F, 0.7F);
   }

   private static void play(net.minecraft.core.Holder<net.minecraft.sounds.SoundEvent> sound, float pitch, float volume) {
      try {
         net.minecraft.client.Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI((net.minecraft.sounds.SoundEvent)sound.value(), pitch, volume));
      } catch (Throwable var4) {
      }
   }

   private float closing(long now) {
      return this.successAt == 0L ? 0.0F : Theme.easeOutCubic(Math.max(0.0F, Math.min(1.0F, (float)(now - this.successAt - 700L) / 420.0F)));
   }

   public void tick() {
      super.tick();
      if (this.successAt != 0L && System.currentTimeMillis() - this.successAt > 1150L) {
         net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
         mc.gui.setScreen(this.openMenuAfter ? new ValenceScreen(this.parent) : this.parent);
      }
   }

   public void extractBackground(net.minecraft.client.gui.GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
      if (this.minecraft != null && this.minecraft.level != null) {
         super.extractBackground(g, mouseX, mouseY, partialTick);
      }

      long now = System.currentTimeMillis();
      float e = Theme.easeOutCubic(Math.min(1.0F, (float)(now - this.openedAt) / 260.0F)) * (1.0F - this.closing(now));
      g.fillGradient(0, 0, this.width, this.height, Theme.withAlpha(Theme.map(Theme.SCRIM_TOP), e), Theme.withAlpha(Theme.map(Theme.SCRIM_BOTTOM), e));
   }

   private void centered(net.minecraft.client.gui.GuiGraphicsExtractor g, net.minecraft.client.gui.Font f, net.minecraft.network.chat.Component text, int cx, int y, float scale, int maxW, int color) {
      List<net.minecraft.util.FormattedCharSequence> lines = f.split(text, Math.round(maxW / scale));

      for (int i = 0; i < lines.size(); i++) {
         Pose.push(g);
         Pose.translate(g, cx, y + i * 10 * scale);
         Pose.scale(g, scale, scale);
         g.text(f, lines.get(i), 0, 0, color);
         Pose.pop(g);
      }
   }

   public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor g, int mx, int my, float pt) {
      long now = System.currentTimeMillis();
      float close = this.closing(now);
      float open = Theme.easeOutCubic(Math.min(1.0F, (float)(now - this.openedAt) / 360.0F)) * (1.0F - close);
      net.minecraft.client.gui.Font f = this.font;
      int x1 = this.px();
      int y1 = this.py();
      int lx = x1 + 140;
      float shake = this.shakeAt == 0L ? 0.0F : Math.max(0.0F, 1.0F - (float)(now - this.shakeAt) / 420.0F);
      float fsx = (float)Math.sin((now - this.shakeAt) / 22.0) * 6.0F * shake * shake;
      float good = this.successAt == 0L ? 0.0F : Math.min(1.0F, (float)(now - this.successAt) / 250.0F);
      Pose.push(g);
      float sc = 0.94F + 0.06F * open;
      Pose.translate(g, this.width / 2.0F, this.height / 2.0F + (1.0F - open) * 10.0F);
      Pose.scale(g, sc, sc);
      Pose.translate(g, -this.width / 2.0F, -this.height / 2.0F);
      Theme.glass(g, x1, y1, x1 + 280, y1 + 190, Theme.withAlpha(-1207105001, 0.4F + 0.6F * open), Theme.withAlpha(1086959871, open), 0.55F * open);
      Theme.sheen(g, x1, y1, x1 + 280, y1 + 40, 0.35F * open);
      float pulse = 0.3F + 0.1F * (float)Math.sin(now / 600.0);
      Theme.glow(g, lx - 15, y1 + 14, lx + 15, y1 + 44, -7643914, pulse * open);
      Theme.image(g, Theme.LOGO, lx - 14, y1 + 15, 28, 28, 128, 128, Theme.withAlpha(-1, open));
      Pose.push(g);
      Pose.translate(g, lx, y1 + 50);
      Pose.scale(g, 1.6F, 1.6F);
      g.centeredText(f, net.minecraft.network.chat.Component.literal("CYCLONESKIES").withStyle(s -> s.withBold(true)), 0, 0, Theme.withAlpha(-1, open));
      Pose.pop(g);
      this.centered(g, f, net.minecraft.network.chat.Component.translatable("valence.license.subtitle"), lx, y1 + 69, 0.8F, 250, Theme.withAlpha(-4608298, open));
      int fx1 = x1 + 34;
      int fy1 = y1 + 92;
      int fx2 = x1 + 280 - 34;
      int fy2 = fy1 + 24;
      float fh = Anim.approach(Anim.key(9100, 0), this.field.isFocused() ? 1.0F : 0.0F, 12.0F);
      int line = Theme.lerp(872415231, -1433707274, fh);
      line = Theme.lerp(line, -495247, shake);
      line = Theme.lerp(line, -9508952, good);
      Pose.push(g);
      Pose.translate(g, fsx, 0.0F);
      if (fh > 0.01F && good == 0.0F && shake == 0.0F) {
         Theme.glow(g, fx1, fy1, fx2, fy2, -7643914, 0.25F * fh * open);
      }

      if (shake > 0.01F) {
         Theme.glow(g, fx1, fy1, fx2, fy2, -495247, 0.4F * shake);
      }

      if (good > 0.01F) {
         Theme.glow(g, fx1, fy1, fx2, fy2, -9508952, 0.4F * good * open);
      }

      Theme.pill(g, fx1, fy1, fx2, fy2, Theme.withAlpha(1073741824, open));
      Theme.pillLine(g, fx1, fy1, fx2, fy2, Theme.withAlpha(line, open));
      int iconCol = good > 0.0F ? -9508952 : Theme.lerp(Theme.lerp(-8095322, -4745985, fh), -495247, shake);
      Theme.icon(g, good > 0.0F ? "check" : "star", fx1 + 8, fy1 + 7, 10, Theme.withAlpha(iconCol, open));
      Pose.pop(g);
      this.field.setY(Math.round(fx1 + 24 + fsx));
      this.field.setX(fy1 + 8);
      this.field.setWidth(fx2 - fx1 - 32);
      this.field.setAlpha(open);
      super.extractRenderState(g, mx, my, pt);
      int bw = 124;
      int bx1 = lx - bw / 2;
      int by1 = y1 + 128;
      net.minecraft.network.chat.Component label;
      if (this.successAt != 0L) {
         label = net.minecraft.network.chat.Component.translatable("valence.license.done");
      } else if (this.busy) {
         int dots = (int)((now - this.busySince) / 300L % 4L);
         label = net.minecraft.network.chat.Component.literal(net.minecraft.network.chat.Component.translatable("valence.license.checking").getString() + ".".repeat(dots));
      } else {
         label = net.minecraft.network.chat.Component.translatable("valence.license.redeem");
      }

      if (open > 0.05F) {
         OptionList.accentButton(g, bx1, by1, bx1 + bw, by1 + 20, label, mx, my, Anim.key(9100, 1));
      }

      int ty = y1 + 158;
      if (!this.message.isEmpty()) {
         float ma = Math.min(1.0F, (float)(now - this.messageAt) / 200.0F) * open;
         Pose.push(g);
         Pose.translate(g, fsx, (1.0F - ma) * 3.0F);
         this.centered(g, f, net.minecraft.network.chat.Component.literal(this.message), lx, ty, 0.8F, 250, Theme.withAlpha(this.messageColor, ma));
         Pose.pop(g);
      } else {
         this.centered(g, f, net.minecraft.network.chat.Component.translatable("valence.license.hint"), lx, ty, 0.7F, 250, Theme.withAlpha(-8095322, open));
      }

      Pose.pop(g);
   }

   private boolean overButton(double mx, double my) {
      int lx = this.px() + 140;
      int by1 = this.py() + 128;
      return mx >= lx - 62 && mx < lx + 62 && my >= by1 && my < by1 + 20;
   }

   protected boolean onClick(double x, double y, int button, Object raw, boolean doubleClick) {
      if (button == 0 && this.overButton(x, y)) {
         this.redeem();
         return true;
      } else {
         return false;
      }
   }

   protected boolean onKey(int keyCode, int scanCode, int modifiers) {
      if (keyCode != 257 && keyCode != 335) {
         return false;
      }

      this.redeem();
      return true;
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void onClose() {
      net.minecraft.client.Minecraft.getInstance().gui.setScreen(this.parent);
   }
}
