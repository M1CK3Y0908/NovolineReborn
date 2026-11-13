package wtf.novoline.utils.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.MathHelper;

public abstract class PolygonRenderer {
   protected final WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

   public void putCustomRoundedRect(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int i) {
      int i2 = i >> 24 & 0xFF;
      int i3 = i >> 16 & 0xFF;
      int i4 = i >> 8 & 0xFF;
      int i5 = i & 0xFF;
      float f9 = f + f3;
      float f10 = f2 + f4;

      for (int i6 = 0; i6 <= 90; i6 += 3) {
         this.addVertex(
            f + f5 + MathHelper.sin((float)(i6 * (Math.PI / 180.0))) * (f5 * -1.0F),
            f2 + f5 + MathHelper.cos((float)(i6 * (Math.PI / 180.0))) * (f5 * -1.0F),
            0.0F,
            0.0F,
            i3,
            i4,
            i5,
            i2
         );
      }

      for (int i7 = 90; i7 <= 180; i7 += 3) {
         this.addVertex(
            f + f6 + MathHelper.sin((float)(i7 * (Math.PI / 180.0))) * (f6 * -1.0F),
            f10 - f6 + MathHelper.cos((float)(i7 * (Math.PI / 180.0))) * (f6 * -1.0F),
            0.0F,
            0.0F,
            i3,
            i4,
            i5,
            i2
         );
      }

      for (int i8 = 0; i8 <= 90; i8 += 3) {
         this.addVertex(
            f9 - f8 + MathHelper.sin((float)(i8 * (Math.PI / 180.0))) * f8,
            f10 - f8 + MathHelper.cos((float)(i8 * (Math.PI / 180.0))) * f8,
            0.0F,
            0.0F,
            i3,
            i4,
            i5,
            i2
         );
      }

      for (int i9 = 90; i9 <= 180; i9 += 3) {
         this.addVertex(
            f9 - f7 + MathHelper.sin((float)(i9 * (Math.PI / 180.0))) * f7,
            f2 + f7 + MathHelper.cos((float)(i9 * (Math.PI / 180.0))) * f7,
            0.0F,
            0.0F,
            i3,
            i4,
            i5,
            i2
         );
      }
   }

   public void putRoundedRect(float f, float f2, float f3, float f4, float f5, int i) {
      this.putCustomRoundedRect(f, f2, f3, f4, f5, f5, f5, f5, i);
   }

   protected abstract void addVertex(float f, float f2, float f3, float f4, int i, int i2, int i3, int i4);

   public PolygonRenderer() {
      GlStateManager.enableBlend();
      RenderUtils.applyBlendFunc();
   }

   public void putOctagon(float f, float f2, float f3, float f4, float f5, float f6, int i) {
      int i2 = i >> 24 & 0xFF;
      int i3 = i >> 16 & 0xFF;
      int i4 = i >> 8 & 0xFF;
      int i5 = i & 0xFF;
      this.addVertex(f + f5, f2, 0.0F, 0.0F, i3, i4, i5, i2);
      this.addVertex(f + f3 - f5, f2, 0.0F, 0.0F, i3, i4, i5, i2);
      this.addVertex(f + f3 - f5, f2, 0.0F, 0.0F, i3, i4, i5, i2);
      this.addVertex(f + f3, f2 + f4 / 2.0F - f6, 0.0F, 0.0F, i3, i4, i5, i2);
      this.addVertex(f + f3, f2 + f4 / 2.0F - f6, 0.0F, 0.0F, i3, i4, i5, i2);
      this.addVertex(f + f3, f2 + f4 / 2.0F + f6, 0.0F, 0.0F, i3, i4, i5, i2);
      this.addVertex(f + f3, f2 + f4 / 2.0F + f6, 0.0F, 0.0F, i3, i4, i5, i2);
      this.addVertex(f + f3 - f5, f2 + f4, 0.0F, 0.0F, i3, i4, i5, i2);
      this.addVertex(f + f3 - f5, f2 + f4, 0.0F, 0.0F, i3, i4, i5, i2);
      this.addVertex(f + f5, f2 + f4, 0.0F, 0.0F, i3, i4, i5, i2);
      this.addVertex(f + f5, f2 + f4, 0.0F, 0.0F, i3, i4, i5, i2);
      this.addVertex(f, f2 + f4 / 2.0F + f6, 0.0F, 0.0F, i3, i4, i5, i2);
      this.addVertex(f, f2 + f4 / 2.0F + f6, 0.0F, 0.0F, i3, i4, i5, i2);
      this.addVertex(f, f2 + f4 / 2.0F - f6, 0.0F, 0.0F, i3, i4, i5, i2);
      this.addVertex(f, f2 + f4 / 2.0F - f6, 0.0F, 0.0F, i3, i4, i5, i2);
      this.addVertex(f + f5, f2, 0.0F, 0.0F, i3, i4, i5, i2);
   }

   public void draw() {
      Tessellator.getInstance().draw();
      GlStateManager.disableBlend();
   }
}
