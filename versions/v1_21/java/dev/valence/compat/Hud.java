package dev.valence.compat;

import dev.valence.cord.hud.Crosshair;
import dev.valence.cord.hud.GlassHotbar;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.class_2960;
import net.minecraft.class_332;

/**
 * HUD registration facade for Minecraft 1.21 - 1.21.5.
 *
 * <p>These versions only expose {@code HudRenderCallback}, which fires once, before the
 * vanilla HUD. Ordering is approximated by two lists: regular elements are drawn from the
 * callback at the head of {@code InGameHud.render}, and the hotbar/crosshair replacements
 * from the mixin at its tail, so they sit on top of everything the vanilla HUD drew. The
 * vanilla hotbar and crosshair are cancelled by the same mixin while a styled replacement
 * is active, so the replaced elements really are replacements rather than overlays.
 */
@Environment(EnvType.CLIENT)
public final class Hud {
   private Hud() {
   }

   public interface Element {
      void render(class_332 g);
   }

   private static final List<Element> before = new ArrayList<>();
   private static final List<Element> after = new ArrayList<>();
   private static boolean registered;

   public static void addFirst(class_2960 id, Element e) {
      ensureRegistered();
      before.add(0, e);
   }

   public static void addLast(class_2960 id, Element e) {
      ensureRegistered();
      before.add(e);
   }

   public static void afterSubtitles(class_2960 id, Element e) {
      addLast(id, e);
   }

   public static void replaceHotbar(Function<Element, Element> wrap) {
      ensureRegistered();
      after.add(wrap.apply(g -> {
      }));
   }

   public static void replaceCrosshair(Function<Element, Element> wrap) {
      ensureRegistered();
      after.add(wrap.apply(g -> {
      }));
   }

   /** Called by HudSuppressMixin at the head of {@code InGameHud.render}. */
   public static void renderBefore(class_332 g) {
      for (int i = 0; i < before.size(); i++) {
         before.get(i).render(g);
      }
   }

   /** Called by HudSuppressMixin at the tail of {@code InGameHud.render}. */
   public static void renderAfter(class_332 g) {
      for (int i = 0; i < after.size(); i++) {
         after.get(i).render(g);
      }
   }

   /** True when the vanilla hotbar must not draw because a style replaces it. */
   public static boolean hideHotbar() {
      return GlassHotbar.suppressing();
   }

   /** True when the vanilla crosshair must not draw because a style replaces it. */
   public static boolean hideCrosshair() {
      return Crosshair.suppressing();
   }

   private static void ensureRegistered() {
      if (registered) {
         return;
      }
      registered = true;
      HudRenderCallback.EVENT.register((g, delta) -> renderBefore(g));
   }
}
