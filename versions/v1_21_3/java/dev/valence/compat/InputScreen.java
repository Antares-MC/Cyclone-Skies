package dev.valence.compat;

import net.minecraft.class_2561;
import net.minecraft.class_437;

/**
 * Screen with version-neutral input hooks.
 *
 * <p>1.21.9 replaced the primitive {@code mouseClicked(double, double, int)} family
 * on {@code Screen} with input event objects ({@code Click}, {@code KeyInput},
 * {@code CharInput}). Every screen in the mod needs the same few handlers, so they
 * are declared once here in primitive form and the raw event is passed along
 * untouched for the calls that still have to reach vanilla widgets.
 *
 * <p>This is the 1.21.2 - 1.21.8 implementation, where the primitives are the real
 * screen methods and the {@code raw} argument is always {@code null}.
 */
public abstract class InputScreen extends class_437 {
   protected InputScreen(class_2561 title) {
      super(title);
   }

   /** Mouse press. */
   protected boolean onClick(double x, double y, int button, Object raw, boolean doubleClick) {
      return false;
   }

   /** Mouse release. */
   protected boolean onRelease(double x, double y, int button, Object raw) {
      return false;
   }

   /** Mouse drag. */
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
      return super.method_25402(x, y, button);
   }

   /** Forwards a release to vanilla widgets with the coordinates already scaled. */
   protected final boolean passRelease(double x, double y, int button, Object raw) {
      return super.method_25406(x, y, button);
   }

   /** Forwards a drag to vanilla widgets with the coordinates already scaled. */
   protected final boolean passDrag(double x, double y, int button, Object raw, double dx, double dy) {
      return super.method_25403(x, y, button, dx, dy);
   }

   /** Forwards a key press to vanilla widgets. */
   protected final boolean passKey(int keyCode, int scanCode, int modifiers) {
      return super.method_25404(keyCode, scanCode, modifiers);
   }

   /** Forwards a typed character to vanilla widgets. */
   protected final boolean passChar(char chr, int modifiers) {
      return super.method_25400(chr, modifiers);
   }

   @Override
   public boolean method_25402(double x, double y, int button) {
      return this.onClick(x, y, button, null, false) || super.method_25402(x, y, button);
   }

   @Override
   public boolean method_25406(double x, double y, int button) {
      return this.onRelease(x, y, button, null) || super.method_25406(x, y, button);
   }

   @Override
   public boolean method_25403(double x, double y, int button, double dx, double dy) {
      return this.onDrag(x, y, button, null, dx, dy) || super.method_25403(x, y, button, dx, dy);
   }

   @Override
   public boolean method_25404(int keyCode, int scanCode, int modifiers) {
      return this.onKey(keyCode, scanCode, modifiers) || super.method_25404(keyCode, scanCode, modifiers);
   }

   @Override
   public boolean method_25400(char chr, int modifiers) {
      return this.onChar(chr, modifiers) || super.method_25400(chr, modifiers);
   }
}
