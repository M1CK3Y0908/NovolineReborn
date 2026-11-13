package wtf.novoline.utils.render.shape;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import wtf.novoline.utils.render.RenderUtils;

public abstract class RectRenderer implements IRectRenderer {
   protected final WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

   public RectRenderer() {
      GlStateManager.enableBlend();
      RenderUtils.applyBlendFunc();
   }

   @Override
   public void putRect(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int i) {
      int i2 = i >> 24 & 0xFF;
      int i3 = i >> 16 & 0xFF;
      int i4 = i >> 8 & 0xFF;
      int i5 = i & 0xFF;
      this.addVertex(f + 0.0F, f2 + f4, f5 + 0.0F, f6 + f8, i3, i4, i5, i2);
      this.addVertex(f + f3, f2 + f4, f5 + f7, f6 + f8, i3, i4, i5, i2);
      this.addVertex(f + f3, f2 + 0.0F, f5 + f7, f6 + 0.0F, i3, i4, i5, i2);
      this.addVertex(f + 0.0F, f2 + 0.0F, f5 + 0.0F, f6 + 0.0F, i3, i4, i5, i2);
   }

   protected abstract void addVertex(float f, float f2, float f3, float f4, int i, int i2, int i3, int i4);

   public void draw() {
      Tessellator.getInstance().draw();
      GlStateManager.disableBlend();
   }
}
