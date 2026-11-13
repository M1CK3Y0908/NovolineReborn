package wtf.novoline.utils.render.shape;

import net.minecraft.client.renderer.GlStateManager;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.shader.FloatSupplier;

public class ShaderRoundedRectRenderer extends RoundedRectRenderer implements FloatSupplier {
   private final CFontRenderer cFontRenderer;

   @Override
   public void draw() {
      super.draw();
      GlStateManager.popMatrix();
   }

   public ShaderRoundedRectRenderer(CFontRenderer cFontRenderer2) {
      super(cFontRenderer2.imgSize, cFontRenderer2.imgSize, true);
      this.cFontRenderer = cFontRenderer2;
      GlStateManager.bindTexture(cFontRenderer2.dynamicTexture.getGlTextureId());
      GlStateManager.pushMatrix();
      GlStateManager.scale(0.5, 0.5, 0.5);
   }

   @Override
   public float drawStringAnchored(String text, float f, float f2, int i, float f3, float f4) {
      float f5 = f - (f3 > 0.0F ? f3 * this.cFontRenderer.getStringWidth(text) : 0.0F);
      float f6 = f2 - (f4 > 0.0F ? f4 * this.cFontRenderer.getHeight() : 0.0F);
      return this.cFontRenderer.drawStringToRenderer(this, text, f5, f6, i);
   }
}
