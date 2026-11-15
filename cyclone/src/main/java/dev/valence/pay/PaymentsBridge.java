package dev.valence.pay;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_310;
import net.minecraft.class_437;

/**
 * Talks to the bundled paymentalerts mod through reflection, so CycloneSkies keeps compiling
 * (Intermediary names) while paymentalerts is compiled separately (Mojang names).
 */
@Environment(EnvType.CLIENT)
public final class PaymentsBridge {
   private static final String BASE = "com.paymentalerts.";

   private PaymentsBridge() {
   }

   public static boolean available() {
      return FabricLoader.getInstance().isModLoaded("paymentalerts");
   }

   private static Class<?> cls(String name) throws ReflectiveOperationException {
      return Class.forName(BASE + name, true, PaymentsBridge.class.getClassLoader());
   }

   public static boolean enabled() {
      try {
         Object cfg = cls("config.PaymentAlertsConfig").getMethod("get").invoke(null);
         return cfg.getClass().getField("enabled").getBoolean(cfg);
      } catch (Throwable t) {
         return false;
      }
   }

   public static void setEnabled(boolean value) {
      try {
         Class<?> c = cls("config.PaymentAlertsConfig");
         Object cfg = c.getMethod("get").invoke(null);
         cfg.getClass().getField("enabled").setBoolean(cfg, value);
         c.getMethod("save").invoke(null);
         if (!value) {
            cls("alert.AlertManager").getMethod("clear").invoke(null);
         }
      } catch (Throwable ignored) {
      }
   }

   /** @param screenClass "screen.SettingsScreen" or "screen.StatsScreen" */
   public static void open(String screenClass) {
      try {
         class_310 mc = class_310.method_1551();
         Object screen = cls(screenClass).getConstructor(class_437.class).newInstance(mc.field_1755);
         mc.method_1507((class_437)screen);
      } catch (Throwable ignored) {
      }
   }
}
