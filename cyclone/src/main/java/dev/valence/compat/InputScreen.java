package dev.valence.compat;

import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
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
 * <p>This is the 1.21.9 - 1.21.11 implementation.
 */
public abstract class InputScreen extends class_437 {
   protected InputScreen(class_2561 title) {
      super(title);
   }

   /** Mouse press. {@code raw} is the original {@code Click}. */
   protected boolean onClick(double x, double y, int button, Object raw, boolean doubleClick) {
      return false;
   }

   /** Mouse release. {@code raw} is the original {@code Click}. */
   protected boolean onRelease(double x, double y, int button, Object raw) {
      return false;
   }

   /** Mouse drag. {@code raw} is the original {@code Click}. */
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
      if (raw instanceof class_11909 click) {
         return super.method_25402(new class_11909(x, y, click.comp_4800()), doubleClick);
      } else {
         return false;
      }
   }

   /** Forwards a release to vanilla widgets with the coordinates already scaled. */
   protected final boolean passRelease(double x, double y, int button, Object raw) {
      if (raw instanceof class_11909 click) {
         return super.method_25406(new class_11909(x, y, click.comp_4800()));
      } else {
         return false;
      }
   }

   /** Forwards a drag to vanilla widgets with the coordinates already scaled. */
   protected final boolean passDrag(double x, double y, int button, Object raw, double dx, double dy) {
      if (raw instanceof class_11909 click) {
         return super.method_25403(new class_11909(x, y, click.comp_4800()), dx, dy);
      } else {
         return false;
      }
   }

   /** Forwards a key press to vanilla widgets. */
   protected final boolean passKey(int keyCode, int scanCode, int modifiers) {
      return super.method_25404(new class_11908(keyCode, scanCode, modifiers));
   }

   /** Forwards a typed character to vanilla widgets. */
   protected final boolean passChar(char chr, int modifiers) {
      return super.method_25400(new class_11905(chr, modifiers));
   }

   @Override
   public boolean method_25402(class_11909 event, boolean doubleClick) {
      return this.onClick(event.comp_4798(), event.comp_4799(), event.method_74245(), event, doubleClick)
         || super.method_25402(event, doubleClick);
   }

   @Override
   public boolean method_25406(class_11909 event) {
      return this.onRelease(event.comp_4798(), event.comp_4799(), event.method_74245(), event) || super.method_25406(event);
   }

   @Override
   public boolean method_25403(class_11909 event, double dx, double dy) {
      return this.onDrag(event.comp_4798(), event.comp_4799(), event.method_74245(), event, dx, dy)
         || super.method_25403(event, dx, dy);
   }

   @Override
   public boolean method_25404(class_11908 event) {
      return this.onKey(event.comp_4795(), event.comp_4796(), event.comp_4797()) || super.method_25404(event);
   }

   @Override
   public boolean method_25400(class_11905 event) {
      return this.onChar((char)event.comp_4793(), event.comp_4794()) || super.method_25400(event);
   }
}
