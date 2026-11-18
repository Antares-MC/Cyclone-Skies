package dev.valence.compat;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.client.KeyMapping.Category;
import com.mojang.blaze3d.platform.InputConstants.Type;

/**
 * Key bindings.
 *
 * <p>26.x renamed the vanilla key-binding classes ({@code KeyBinding} became
 * {@code KeyMapping}) and Fabric's helper with them: {@code KeyBindingHelper}
 * became {@code KeyMappingHelper} and {@code registerKeyBinding} became
 * {@code registerKeyMapping}. Nothing else about a binding changed.
 *
 * <p>This is the 26.x implementation.
 */
public final class Keys {
   /** The category every Cyclone Skies binding is filed under. */
   public static final net.minecraft.client.KeyMapping.Category CATEGORY = net.minecraft.client.KeyMapping.Category.register(net.minecraft.resources.Identifier.fromNamespaceAndPath("valence", "main"));

   private Keys() {
   }

   /** Registers a binding filed under the mod's category. */
   public static net.minecraft.client.KeyMapping bind(String translationKey, int defaultCode) {
      return KeyMappingHelper.registerKeyMapping(new net.minecraft.client.KeyMapping(translationKey, com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM, defaultCode, CATEGORY));
   }

   /** The key a binding is currently assigned to, for display. */
   public static com.mojang.blaze3d.platform.InputConstants.Key boundKey(net.minecraft.client.KeyMapping binding) {
      return binding == null ? com.mojang.blaze3d.platform.InputConstants.UNKNOWN : KeyMappingHelper.getBoundKeyOf(binding);
   }

   /** Whether this binding is the given key. */
   public static boolean matches(net.minecraft.client.KeyMapping binding, int keyCode, int scanCode) {
      return binding != null && binding.matches(new net.minecraft.client.input.KeyEvent(keyCode, scanCode, 0));
   }

   /** The key code this binding is currently bound to, or -1 when it has none. */
   public static int code(net.minecraft.client.KeyMapping binding) {
      return binding == null ? -1 : KeyMappingHelper.getBoundKeyOf(binding).getValue();
   }

   /** Whether the binding's key is held down right now. */
   public static boolean defaultPressed(net.minecraft.client.KeyMapping binding) {
      int code = code(binding);
      return code >= 0 && com.mojang.blaze3d.platform.InputConstants.isKeyDown(net.minecraft.client.Minecraft.getInstance().getWindow(), code);
   }
}
