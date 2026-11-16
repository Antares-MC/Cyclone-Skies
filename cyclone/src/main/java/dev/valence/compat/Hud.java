package dev.valence.compat;

import java.util.function.Function;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_9779;

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
      void render(class_332 g);
   }

   public static void addFirst(class_2960 id, Element e) {
      HudElementRegistry.addFirst(id, adapt(e));
   }

   public static void addLast(class_2960 id, Element e) {
      HudElementRegistry.addLast(id, adapt(e));
   }

   public static void afterSubtitles(class_2960 id, Element e) {
      HudElementRegistry.attachElementAfter(VanillaHudElements.SUBTITLES, id, adapt(e));
   }

   public static void replaceHotbar(Function<Element, Element> wrap) {
      HudElementRegistry.replaceElement(VanillaHudElements.HOTBAR, v -> adapt(wrap.apply(g -> v.render(g, live()))));
   }

   public static void replaceCrosshair(Function<Element, Element> wrap) {
      HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, v -> adapt(wrap.apply(g -> v.render(g, live()))));
   }

   private static class_9779 live() {
      return class_310.method_1551().method_61966();
   }

   private static HudElement adapt(Element e) {
      return (g, d) -> e.render(g);
   }
}
