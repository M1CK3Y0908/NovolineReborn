package wtf.novoline.utils.render;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RoundedPolygonRenderer extends PolygonRenderer {
   private final float height;
   private final float inverseWidth;
   private final float inverseHeight;

   @Override
   protected void addVertex(float f, float f2, float f3, float f4, int i, int i2, int i3, int i4) {
      this.worldRenderer.pos(f, f2, 0.0).tex(f * this.inverseWidth, (this.height - f2) * this.inverseHeight).endVertex();
   }

   public RoundedPolygonRenderer(float f, float f2) {
      this.height = f2;
      this.inverseWidth = 1.0F / f;
      this.inverseHeight = 1.0F / f2;
      this.worldRenderer.begin(9, DefaultVertexFormats.POSITION_TEX);
   }
}
