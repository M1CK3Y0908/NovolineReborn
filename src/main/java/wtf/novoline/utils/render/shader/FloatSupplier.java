package wtf.novoline.utils.render.shader;

public interface FloatSupplier {
   long UNUSED_LONG_CONSTANT = 85272024331088L;

   default float drawStringLeftMiddle(String text, float f, float f2, int i) {
      return this.drawStringAnchored(text, f, f2, i, 0.0F, 0.5F);
   }

   default float drawStringRightTop(String text, float f, float f2, int i) {
      return this.drawStringAnchored(text, f, f2, i, 1.0F, 0.0F);
   }

   default float drawStringCentered(String text, float f, float f2, int i) {
      return this.drawStringAnchored(text, f, f2, i, 0.5F, 0.5F);
   }

   float drawStringAnchored(String text, float f, float f2, int i, float f3, float f4);

   default float drawStringTopLeft(String text, float f, float f2, int i) {
      return this.drawStringAnchored(text, f, f2, i, 0.0F, 0.0F);
   }

   default float drawStringTopCentered(String text, float f, float f2, int i) {
      return this.drawStringAnchored(text, f, f2, i, 0.5F, 0.0F);
   }
}
