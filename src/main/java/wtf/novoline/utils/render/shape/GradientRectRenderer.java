package wtf.novoline.utils.render.shape;

public class GradientRectRenderer extends RoundedRectRenderer {
   private final float height;

   @Override
   protected void addVertex(float f, float f2, float f3, float f4, int i, int i2, int i3, int i4) {
      super.addVertex(f, f2, f3, this.height - f4, i, i2, i3, i4);
   }

   public GradientRectRenderer(float f, float f2) {
      super(f, f2);
      this.height = f2;
   }

   public GradientRectRenderer(float f, float f2, boolean flag) {
      super(f, f2, flag);
      this.height = f2;
   }
}
