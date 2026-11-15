package dev.valence.gui.pages;

import dev.valence.gui.OptionList;
import dev.valence.gui.Page;
import dev.valence.gui.ValenceScreen;
import dev.valence.pay.PaymentsBridge;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_332;

@Environment(EnvType.CLIENT)
public class PaymentsPage extends Page {
   private final OptionList options = new OptionList();

   @Override
   public String nameKey() {
      return "valence.page.payments";
   }

   @Override
   public String subtitleKey() {
      return "valence.page.payments.subtitle";
   }

   @Override
   public String icon() {
      return "bolt";
   }

   private static class_2561 t(String key) {
      return class_2561.method_43471(key);
   }

   @Override
   public void init(ValenceScreen screen, int x, int y, int w, int h) {
      super.init(screen, x, y, w, h);
      this.options.setBounds(x, y, w, h);
      this.options.clear();
      this.options.add(new OptionList.Header(t("valence.payments.header")));
      if (!PaymentsBridge.available()) {
         this.options.add(new OptionList.Info(() -> t("valence.payments.missing"), 2));
         return;
      }

      this.options
         .add(new OptionList.Toggle(t("valence.payments.enabled"), t("valence.payments.enabled.desc"), PaymentsBridge::enabled, PaymentsBridge::setEnabled));
      this.options
         .add(new OptionList.Button(t("valence.payments.settings"), t("valence.payments.open"), () -> PaymentsBridge.open("screen.SettingsScreen")));
      this.options.add(new OptionList.Button(t("valence.payments.stats"), t("valence.payments.open"), () -> PaymentsBridge.open("screen.StatsScreen")));
      this.options.add(new OptionList.Info(() -> t("valence.payments.keys"), 3));
   }

   @Override
   public void render(class_332 g, int mx, int my, float pt) {
      this.options.render(g, mx, my);
   }

   @Override
   public boolean mouseClicked(double mx, double my, int button) {
      return this.options.mouseClicked(mx, my, button);
   }

   @Override
   public boolean mouseDragged(double mx, double my, int button) {
      return this.options.mouseDragged(mx, my);
   }

   @Override
   public boolean mouseReleased(double mx, double my, int button) {
      return this.options.mouseReleased();
   }

   @Override
   public boolean mouseScrolled(double mx, double my, double amount) {
      return this.options.mouseScrolled(mx, my, amount);
   }
}
