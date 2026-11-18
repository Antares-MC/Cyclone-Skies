package dev.valence.compat;

import java.util.function.Function;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;

/**
 * HUD registration facade.
 *
 * <p>1.21.6 introduced Fabric's {@code HudElementRegistry}, which can order elements and
 * swap out vanilla ones. Older versions only expose the flat {@code HudRenderCallback},
 * so the pre-1.21.6 overlay of this class keeps two ordered lists instead: one drawn
 * before the vanilla HUD and one drawn after it.
 */
public final class Hud {
   private Hud() {
   }

   public interface Element {
      void render(net.minecraft.client.gui.GuiGraphicsExtractor g);
   }

   public static void addFirst(net.minecraft.resources.Identifier id, Element e) {
      HudElementRegistry.addFirst(id, adapt(e));
   }

   public static void addLast(net.minecraft.resources.Identifier id, Element e) {
      HudElementRegistry.addLast(id, adapt(e));
   }

   public static void afterSubtitles(net.minecraft.resources.Identifier id, Element e) {
      HudElementRegistry.attachElementAfter(VanillaHudElements.SUBTITLES, id, adapt(e));
   }

   public static void replaceHotbar(Function<Element, Element> wrap) {
      HudElementRegistry.replaceElement(VanillaHudElements.HOTBAR, v -> adapt(wrap.apply(g -> v.extractRenderState(g, live()))));
   }

   public static void replaceCrosshair(Function<Element, Element> wrap) {
      HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, v -> adapt(wrap.apply(g -> v.extractRenderState(g, live()))));
   }

   private static net.minecraft.client.DeltaTracker live() {
      return net.minecraft.client.Minecraft.getInstance().getDeltaTracker();
   }

   private static HudElement adapt(Element e) {
      return (g, d) -> e.render(g);
   }
}
