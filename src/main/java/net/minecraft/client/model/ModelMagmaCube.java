package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;

public class ModelMagmaCube extends ModelBase {
   ModelRenderer[] segments = new ModelRenderer[8];
   ModelRenderer core;

   @Override
   public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float f, float f2, float partialTickTime) {
      EntityMagmaCube entityMagmaCube = (EntityMagmaCube)entitylivingbaseIn;
      float f3 = entityMagmaCube.prevSquishFactor + (entityMagmaCube.squishFactor - entityMagmaCube.prevSquishFactor) * partialTickTime;
      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      for (int index = 0; index < this.segments.length; index++) {
         this.segments[index].rotationPointY = -(4 - index) * f3 * 1.7F;
      }
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      this.setRotationAngles(f, f2, f3, f4, f5, scale, entityIn);
      this.core.render(scale);

      for (int index = 0; index < this.segments.length; index++) {
         this.segments[index].render(scale);
      }
   }

   public ModelMagmaCube() {
      for (int index = 0; index < this.segments.length; index++) {
         int i = 0;
         int index2 = index;
         if (index == 2) {
            i = 24;
            index2 = 10;
         } else if (index == 3) {
            i = 24;
            index2 = 19;
         }

         this.segments[index] = new ModelRenderer(this, i, index2);
         this.segments[index].addBox(-4.0F, 16 + index, -4.0F, 8, 1, 8);
      }

      this.core = new ModelRenderer(this, 0, 16);
      this.core.addBox(-2.0F, 18.0F, -2.0F, 4, 4, 4);
   }
}
