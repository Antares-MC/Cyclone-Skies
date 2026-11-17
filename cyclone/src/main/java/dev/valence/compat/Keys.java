package dev.valence.compat;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.class_304;
import net.minecraft.class_2960;
import net.minecraft.class_304.class_11900;
import net.minecraft.class_3675;
import net.minecraft.class_3675.class_306;
import net.minecraft.class_3675.class_307;

/**
 * Key bindings.
 *
 * <p>1.21.9 turned the key-binding category from a translation-key string into a
 * {@code KeyBinding.Category} record, which changes the constructor {@code KeyBinding}
 * takes. Everything else about a binding is the same on both sides, so the mod
 * builds its bindings here instead of naming a constructor.
 *
 * <p>This is the 1.21.9 - 1.21.11 implementation.
 */
public final class Keys {
   /** The category every Cyclone Skies binding is filed under. */
   public static final class_11900 CATEGORY = class_11900.method_74698(class_2960.method_60655("valence", "main"));

   private Keys() {
   }

   /** Registers a binding filed under the mod's category. */
   public static class_304 bind(String translationKey, int defaultCode) {
      return KeyBindingHelper.registerKeyBinding(new class_304(translationKey, class_307.field_1668, defaultCode, CATEGORY));
   }

   /** The key a binding is currently assigned to, for display. */
   public static class_306 boundKey(class_304 binding) {
      return binding == null ? class_3675.field_16237 : binding.method_1429();
   }

   /** Whether this binding is the given key. */
   public static boolean matches(class_304 binding, int keyCode, int scanCode) {
      return binding != null && binding.method_1417(new net.minecraft.class_11908(keyCode, scanCode, 0));
   }

   /** The key code this binding is currently bound to, or -1 when it has none. */
   public static int code(class_304 binding) {
      return binding == null ? -1 : binding.method_1429().method_1444();
   }

   /** Whether the binding's key is held down right now. */
   public static boolean defaultPressed(class_304 binding) {
      int code = code(binding);
      return code >= 0 && net.minecraft.class_3675.method_15987(net.minecraft.class_310.method_1551().method_22683(), code);
   }
}
