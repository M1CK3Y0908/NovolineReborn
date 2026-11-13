package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.ResourceLocation;

public class RenderLightningBolt extends Render<EntityLightningBolt> {
   public void doRender2(EntityLightningBolt entity, double x, double y, double z, float entityYaw, float partialTicks) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.disableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 1);
      double[] dArray = new double[8];
      double[] dArray2 = new double[8];
      double d = 0.0;
      double d2 = 0.0;
      Random random = new Random(entity.boltVertex);

      for (int i = 7; i >= 0; i--) {
         dArray[i] = d;
         dArray2[i] = d2;
         d += random.nextInt(11) - 5;
         d2 += random.nextInt(11) - 5;
      }

      for (int i2 = 0; i2 < 4; i2++) {
         Random random2 = new Random(entity.boltVertex);

         for (int i3 = 0; i3 < 3; i3++) {
            int i4 = 7;
            int i5 = 0;
            if (i3 > 0) {
               i4 = 7 - i3;
            }

            if (i3 > 0) {
               i5 = i4 - 2;
            }

            double d3 = dArray[i4] - d;
            double d4 = dArray2[i4] - d2;

            for (int i6 = i4; i6 >= i5; i6--) {
               double d5 = d3;
               double d6 = d4;
               if (i3 == 0) {
                  d3 += random2.nextInt(11) - 5;
                  d4 += random2.nextInt(11) - 5;
               } else {
                  d3 += random2.nextInt(31) - 15;
                  d4 += random2.nextInt(31) - 15;
               }

               worldRenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);
               double d7 = 0.1 + i2 * 0.2;
               if (i3 == 0) {
                  d7 *= i6 * 0.1 + 1.0;
               }

               double d8 = 0.1 + i2 * 0.2;
               if (i3 == 0) {
                  d8 *= (i6 - 1) * 0.1 + 1.0;
               }

               for (int i7 = 0; i7 < 5; i7++) {
                  double d9 = x + 0.5 - d7;
                  double d10 = z + 0.5 - d7;
                  if (i7 == 1 || i7 == 2) {
                     d9 += d7 * 2.0;
                  }

                  if (i7 == 2 || i7 == 3) {
                     d10 += d7 * 2.0;
                  }

                  double d11 = x + 0.5 - d8;
                  double d12 = z + 0.5 - d8;
                  if (i7 == 1 || i7 == 2) {
                     d11 += d8 * 2.0;
                  }

                  if (i7 == 2 || i7 == 3) {
                     d12 += d8 * 2.0;
                  }

                  worldRenderer.pos(d11 + d3, y + i6 * 16, d12 + d4).color(0.45F, 0.45F, 0.5F, 0.3F).endVertex();
                  worldRenderer.pos(d9 + d5, y + (i6 + 1) * 16, d10 + d6).color(0.45F, 0.45F, 0.5F, 0.3F).endVertex();
               }

               instance.draw();
            }
         }
      }

      GlStateManager.disableBlend();
      GlStateManager.enableLighting();
      GlStateManager.enableTexture2D();
   }

   public RenderLightningBolt(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   protected ResourceLocation getEntityTexture(EntityLightningBolt entity) {
      return null;
   }
}
