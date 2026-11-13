package wtf.novoline.utils.render.shape;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RoundedRectRenderer extends RectRenderer {
   private final float inverseTextureWidth;
   private final float inverseTextureHeight;
   private final boolean hasColor;

   @Override
   protected void addVertex(float f, float f2, float f3, float f4, int i, int i2, int i3, int i4) {
      this.worldRenderer.pos(f, f2, 0.0).tex(f3 * this.inverseTextureWidth, f4 * this.inverseTextureHeight);
      if (this.hasColor) {
         this.worldRenderer.color(i, i2, i3, i4);
      }

      this.worldRenderer.endVertex();
   }

   public RoundedRectRenderer(float f, float f2, boolean flag) {
      this.inverseTextureWidth = 1.0F / f;
      this.inverseTextureHeight = 1.0F / f2;
      this.hasColor = flag;
      this.worldRenderer.begin(7, flag ? DefaultVertexFormats.POSITION_TEX_COLOR : DefaultVertexFormats.POSITION_TEX);
   }

   public RoundedRectRenderer(float f, float f2) {
      this(f, f2, false);
   }
}
