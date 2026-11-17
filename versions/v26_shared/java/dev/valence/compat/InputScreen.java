package dev.valence.compat;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;

/**
 * Screen with version-neutral input hooks.
 *
 * <p>26.x collapsed the 1.21.9 input events into records with single-argument
 * accessors: {@code MouseButtonEvent} carries a {@code MouseButtonInfo} instead of
 * a raw button code, {@code KeyEvent} kept its three components, and
 * {@code CharacterEvent} lost its modifiers and now carries only a codepoint.
 * Every screen in the mod needs the same few handlers, so they are declared once
 * here in primitive form and the raw event is passed along untouched for the calls
 * that still have to reach vanilla widgets.
 *
 * <p>This is the 26.x implementation.
 */
public abstract class InputScreen extends net.minecraft.client.gui.screens.Screen {
   protected InputScreen(Component title) {
      super(title);
   }

   /** Mouse press. {@code raw} is the original {@code MouseButtonEvent}. */
   protected boolean onClick(double x, double y, int button, Object raw, boolean doubleClick) {
      return false;
   }

   /** Mouse release. {@code raw} is the original {@code MouseButtonEvent}. */
   protected boolean onRelease(double x, double y, int button, Object raw) {
      return false;
   }

   /** Mouse drag. {@code raw} is the original {@code MouseButtonEvent}. */
   protected boolean onDrag(double x, double y, int button, Object raw, double dx, double dy) {
      return false;
   }

   /** Key press. */
   protected boolean onKey(int keyCode, int scanCode, int modifiers) {
      return false;
   }

   /** Typed character. */
   protected boolean onChar(char chr, int modifiers) {
      return false;
   }

   /** Forwards a press to vanilla widgets with the coordinates already scaled. */
   protected final boolean passClick(double x, double y, int button, Object raw, boolean doubleClick) {
      if (raw instanceof MouseButtonEvent click) {
         return super.mouseClicked(new MouseButtonEvent(x, y, click.buttonInfo()), doubleClick);
      } else {
         return false;
      }
   }

   /** Forwards a release to vanilla widgets with the coordinates already scaled. */
   protected final boolean passRelease(double x, double y, int button, Object raw) {
      if (raw instanceof MouseButtonEvent click) {
         return super.mouseReleased(new MouseButtonEvent(x, y, click.buttonInfo()));
      } else {
         return false;
      }
   }

   /** Forwards a drag to vanilla widgets with the coordinates already scaled. */
   protected final boolean passDrag(double x, double y, int button, Object raw, double dx, double dy) {
      if (raw instanceof MouseButtonEvent click) {
         return super.mouseDragged(new MouseButtonEvent(x, y, click.buttonInfo()), dx, dy);
      } else {
         return false;
      }
   }

   /** Forwards a key press to vanilla widgets. */
   protected final boolean passKey(int keyCode, int scanCode, int modifiers) {
      return super.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
   }

   /** Forwards a typed character to vanilla widgets. */
   protected final boolean passChar(char chr, int modifiers) {
      return super.charTyped(new CharacterEvent(chr));
   }

   @Override
   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
      return this.onClick(event.x(), event.y(), event.button(), event, doubleClick)
         || super.mouseClicked(event, doubleClick);
   }

   @Override
   public boolean mouseReleased(MouseButtonEvent event) {
      return this.onRelease(event.x(), event.y(), event.button(), event) || super.mouseReleased(event);
   }

   @Override
   public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
      return this.onDrag(event.x(), event.y(), event.button(), event, dx, dy)
         || super.mouseDragged(event, dx, dy);
   }

   @Override
   public boolean keyPressed(KeyEvent event) {
      return this.onKey(event.key(), event.scancode(), event.modifiers()) || super.keyPressed(event);
   }

   @Override
   public boolean charTyped(CharacterEvent event) {
      return this.onChar((char)event.codepoint(), 0) || super.charTyped(event);
   }
}
