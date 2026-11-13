package net.minecraft.client.renderer.entity.layers;

import java.util.Random;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;

public class LayerArrow implements LayerRenderer<EntityLivingBase> {
   private final RendererLivingEntity renderer;

   @Override
   public void doRenderLayer(
      EntityLivingBase entitylivingbaseIn,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch,
      float scale
   ) {
      int arrowCountInEntity = entitylivingbaseIn.getArrowCountInEntity();
      if (arrowCountInEntity > 0) {
         EntityArrow entityArrow = new EntityArrow(entitylivingbaseIn.worldObj, entitylivingbaseIn.posX, entitylivingbaseIn.posY, entitylivingbaseIn.posZ);
         Random random = new Random(entitylivingbaseIn.getEntityId());
         RenderHelper.disableStandardItemLighting();

         for (int i = 0; i < arrowCountInEntity; i++) {
            GlStateManager.pushMatrix();
            ModelRenderer randomModelBox = this.renderer.getMainModel().getRandomModelBox(random);
            ModelBox modelBox = randomModelBox.cubeList.get(random.nextInt(randomModelBox.cubeList.size()));
            randomModelBox.postRender(0.0625F);
            float f = random.nextFloat();
            float f2 = random.nextFloat();
            float f3 = random.nextFloat();
            float f4 = (modelBox.posX1 + (modelBox.posX2 - modelBox.posX1) * f) / 16.0F;
            float f5 = (modelBox.posY1 + (modelBox.posY2 - modelBox.posY1) * f2) / 16.0F;
            float f6 = (modelBox.posZ1 + (modelBox.posZ2 - modelBox.posZ1) * f3) / 16.0F;
            GlStateManager.translate(f4, f5, f6);
            f = f * 2.0F - 1.0F;
            f2 = f2 * 2.0F - 1.0F;
            f3 = f3 * 2.0F - 1.0F;
            f *= -1.0F;
            f2 *= -1.0F;
            f3 *= -1.0F;
            float f7 = MathHelper.sqrt_float(f * f + f3 * f3);
            entityArrow.prevRotationYaw = entityArrow.rotationYaw = (float)(Math.atan2(f, f3) * 180.0 / Math.PI);
            entityArrow.prevRotationPitch = entityArrow.rotationPitch = (float)(Math.atan2(f2, f7) * 180.0 / Math.PI);
            double d = 0.0;
            double d2 = 0.0;
            double d3 = 0.0;
            this.renderer.getRenderManager().renderEntityWithPosYaw(entityArrow, d, d2, d3, 0.0F, partialTicks);
            GlStateManager.popMatrix();
         }

         RenderHelper.enableStandardItemLighting();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }

   public LayerArrow(RendererLivingEntity rendererIn) {
      this.renderer = rendererIn;
   }
}
