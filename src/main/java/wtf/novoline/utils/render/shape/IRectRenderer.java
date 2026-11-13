package wtf.novoline.utils.render.shape;

import net.minecraft.client.renderer.OpenGlHelper;

public interface IRectRenderer {
   long UNUSED_LONG_CONSTANT = 73436956697219L;

   default void addRect(float f, float f2, float f3, float f4, int i) {
      if (OpenGlHelper.shadersSupported) {
         this.putRect(f, f2, f3, f4, f, f2, f3, f4, i);
      }
   }

   void putRect(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int i);

   default void drawTexturedRect(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int i) {
      this.putRect(f, f2, f3, f4, f5, f6, f7, f8, i);
   }
}
