package net.minecraft.client.model;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelGhast extends ModelBase {
   ModelRenderer[] tentacles = new ModelRenderer[9];
   ModelRenderer body = new ModelRenderer(this, 0, 0);

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      for (int index = 0; index < this.tentacles.length; index++) {
         this.tentacles[index].rotateAngleX = 0.2F * MathHelper.sin(ageInTicks * 0.3F + index) + 0.4F;
      }
   }

   public ModelGhast() {
      this.body.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
      this.body.rotationPointY += 8;
      Random random = new Random(1660L);

      for (int i = 0; i < this.tentacles.length; i++) {
         this.tentacles[i] = new ModelRenderer(this, 0, 0);
         float f = ((i % 3 - i / 3 % 2 * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
         float f2 = (i / 3 / 2.0F * 2.0F - 1.0F) * 5.0F;
         int i2 = random.nextInt(7) + 8;
         this.tentacles[i].addBox(-1.0F, 0.0F, -1.0F, 2, i2, 2);
         this.tentacles[i].rotationPointX = f;
         this.tentacles[i].rotationPointZ = f2;
         this.tentacles[i].rotationPointY = 15;
      }
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      this.setRotationAngles(f, f2, f3, f4, f5, scale, entityIn);
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, 0.6F, 0.0F);
      this.body.render(scale);
      ModelRenderer[] tentacles2 = this.tentacles;
      int length2 = tentacles2.length;

      for (int i = 0; i < length2; i++) {
         tentacles2[i].render(scale);
      }

      GlStateManager.popMatrix();
   }
}
