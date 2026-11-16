package dev.valence.compat;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;

/**
 * Key bindings.
 *
 * <p>1.21.9 turned the key-binding category from a translation-key string into a
 * {@code KeyBinding.Category} record, which changed the constructor {@code KeyBinding}
 * takes and the shape {@code matchesKey} expects. Everything else about a binding is
 * the same on both sides, so the mod builds its bindings here instead of naming a
 * constructor.
 *
 * <p>This is the 1.21.2 - 1.21.8 implementation, where the category is a plain
 * translation key and key matching takes a raw code pair.
 */
public final class Keys {
   private Keys() {
   }

   /** Registers a binding filed under the mod's category. */
   public static class_304 bind(String translationKey, int defaultCode) {
      return KeyBindingHelper.registerKeyBinding(new class_304(translationKey, defaultCode, "key.categories.valence"));
   }

   /** Whether this binding is the given key. */
   public static boolean matches(class_304 binding, int keyCode, int scanCode) {
      return binding != null && binding.method_1417(keyCode, scanCode);
   }

   /** The key code this binding is currently bound to, or -1 when it has none. */
   public static int code(class_304 binding) {
      return binding == null ? -1 : binding.method_1429().method_1444();
   }

   /** Whether the binding's key is held down right now. */
   public static boolean defaultPressed(class_304 binding) {
      int code = code(binding);
      return code >= 0 && class_3675.method_15987(class_310.method_1551().method_22683().method_4490(), code);
   }
}
