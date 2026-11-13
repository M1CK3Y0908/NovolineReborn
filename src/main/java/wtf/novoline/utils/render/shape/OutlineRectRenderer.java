package wtf.novoline.utils.render.shape;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class OutlineRectRenderer extends RectRenderer {
   private final float alphaScale;

   @Override
   protected void addVertex(float f, float f2, float f3, float f4, int i, int i2, int i3, int i4) {
      this.worldRenderer.pos(f, f2, 0.0).color(i, i2, i3, (int)(i4 * this.alphaScale)).endVertex();
   }

   public OutlineRectRenderer(float f) {
      this.alphaScale = f;
      this.worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      GlStateManager.disableTexture2D();
   }

   @Override
   public void draw() {
      super.draw();
      GlStateManager.enableTexture2D();
   }
}
