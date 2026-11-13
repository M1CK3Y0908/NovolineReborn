package wtf.novoline.utils.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class FilledPolygonRenderer extends PolygonRenderer {
   @Override
   protected void addVertex(float f, float f2, float f3, float f4, int i, int i2, int i3, int i4) {
      this.worldRenderer.pos(f, f2, 0.0).color(i, i2, i3, i4).endVertex();
   }

   @Override
   public void draw() {
      super.draw();
      GlStateManager.enableTexture2D();
   }

   public FilledPolygonRenderer() {
      this.worldRenderer.begin(9, DefaultVertexFormats.POSITION_COLOR);
      GlStateManager.disableTexture2D();
   }
}
