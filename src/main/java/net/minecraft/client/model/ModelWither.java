package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.MathHelper;

public class ModelWither extends ModelBase {
   private ModelRenderer[] bodySegments;
   private ModelRenderer[] heads;

   public ModelWither(float scale) {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.bodySegments = new ModelRenderer[3];
      this.bodySegments[0] = new ModelRenderer(this, 0, 16);
      this.bodySegments[0].addBox(-10.0F, 3.9F, -0.5F, 20, 3, 3, scale);
      this.bodySegments[1] = new ModelRenderer(this).setTextureSize(this.textureWidth, this.textureHeight);
      this.bodySegments[1].setRotationPoint(-2.0F, 6.9F, -0.5F);
      this.bodySegments[1].setTextureOffset(0, 22).addBox(0.0F, 0.0F, 0.0F, 3, 10, 3, scale);
      this.bodySegments[1].setTextureOffset(24, 22).addBox(-4.0F, 1.5F, 0.5F, 11, 2, 2, scale);
      this.bodySegments[1].setTextureOffset(24, 22).addBox(-4.0F, 4.0F, 0.5F, 11, 2, 2, scale);
      this.bodySegments[1].setTextureOffset(24, 22).addBox(-4.0F, 6.5F, 0.5F, 11, 2, 2, scale);
      this.bodySegments[2] = new ModelRenderer(this, 12, 22);
      this.bodySegments[2].addBox(0.0F, 0.0F, 0.0F, 3, 6, 3, scale);
      this.heads = new ModelRenderer[3];
      this.heads[0] = new ModelRenderer(this, 0, 0);
      this.heads[0].addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, scale);
      this.heads[1] = new ModelRenderer(this, 32, 0);
      this.heads[1].addBox(-4.0F, -4.0F, -4.0F, 6, 6, 6, scale);
      this.heads[1].rotationPointX = -8.0F;
      this.heads[1].rotationPointY = 4.0F;
      this.heads[2] = new ModelRenderer(this, 32, 0);
      this.heads[2].addBox(-4.0F, -4.0F, -4.0F, 6, 6, 6, scale);
      this.heads[2].rotationPointX = 10.0F;
      this.heads[2].rotationPointY = 4.0F;
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      float f = MathHelper.cos(ageInTicks * 0.1F);
      this.bodySegments[1].rotateAngleX = (0.065F + 0.05F * f) * (float) Math.PI;
      this.bodySegments[2]
         .setRotationPoint(
            -2.0F, 6.9F + MathHelper.cos(this.bodySegments[1].rotateAngleX) * 10.0F, -0.5F + MathHelper.sin(this.bodySegments[1].rotateAngleX) * 10.0F
         );
      this.bodySegments[2].rotateAngleX = (0.265F + 0.1F * f) * (float) Math.PI;
      this.heads[0].rotateAngleY = netHeadYaw / (180.0F / (float)Math.PI);
      this.heads[0].rotateAngleX = headPitch / (180.0F / (float)Math.PI);
   }

   @Override
   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      ModelRenderer[] heads2 = this.heads;
      int length2 = heads2.length;

      for (int i = 0; i < length2; i++) {
         heads2[i].render(scaleFactor);
      }

      heads2 = this.bodySegments;
      length2 = heads2.length;

      for (int i2 = 0; i2 < length2; i2++) {
         heads2[i2].render(scaleFactor);
      }
   }

   @Override
   public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
      EntityWither entityWither = (EntityWither)entitylivingbaseIn;

      for (int index = 1; index < 3; index++) {
         this.heads[index].rotateAngleY = (entityWither.getHeadYaw(index - 1) - entitylivingbaseIn.renderYawOffset) / (180.0F / (float)Math.PI);
         this.heads[index].rotateAngleX = entityWither.getHeadPitch(index - 1) / (180.0F / (float)Math.PI);
      }
   }
}
