package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelSquid extends ModelBase {
   ModelRenderer[] squidTentacles = new ModelRenderer[8];
   ModelRenderer squidBody = new ModelRenderer(this, 0, 0);

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      this.setRotationAngles(f, f2, f3, f4, f5, scale, entityIn);
      this.squidBody.render(scale);

      for (int index = 0; index < this.squidTentacles.length; index++) {
         this.squidTentacles[index].render(scale);
      }
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      ModelRenderer[] squidTentacles2 = this.squidTentacles;
      int length2 = squidTentacles2.length;

      for (int i = 0; i < length2; i++) {
         squidTentacles2[i].rotateAngleX = ageInTicks;
      }
   }

   public ModelSquid() {
      this.squidBody.addBox(-6.0F, -8.0F, -6.0F, 12, 16, 12);
      this.squidBody.rotationPointY += 8;

      for (int i = 0; i < this.squidTentacles.length; i++) {
         this.squidTentacles[i] = new ModelRenderer(this, 48, 0);
         double d = i * Math.PI * 2.0 / this.squidTentacles.length;
         float f = (float)Math.cos(d) * 5.0F;
         float f2 = (float)Math.sin(d) * 5.0F;
         this.squidTentacles[i].addBox(-1.0F, 0.0F, -1.0F, 2, 18, 2);
         this.squidTentacles[i].rotationPointX = f;
         this.squidTentacles[i].rotationPointZ = f2;
         this.squidTentacles[i].rotationPointY = 15;
         d = i * Math.PI * -2.0 / this.squidTentacles.length + (Math.PI / 2);
         this.squidTentacles[i].rotateAngleY = (float)d;
      }
   }
}
