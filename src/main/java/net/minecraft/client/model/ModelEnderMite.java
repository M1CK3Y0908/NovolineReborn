package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelEnderMite extends ModelBase {
   private static final int[][] BODY_SIZES = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
   private static final int[][] BODY_TEX_OFFSETS = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
   private static final int BODY_PART_COUNT = BODY_SIZES.length;
   private final ModelRenderer[] bodyParts = new ModelRenderer[BODY_PART_COUNT];

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      this.setRotationAngles(f, f2, f3, f4, f5, scale, entityIn);

      for (int index = 0; index < this.bodyParts.length; index++) {
         this.bodyParts[index].render(scale);
      }
   }

   public ModelEnderMite() {
      float f = -3.5F;

      for (int index = 0; index < this.bodyParts.length; index++) {
         this.bodyParts[index] = new ModelRenderer(this, BODY_TEX_OFFSETS[index][0], BODY_TEX_OFFSETS[index][1]);
         this.bodyParts[index]
            .addBox(BODY_SIZES[index][0] * -0.5F, 0.0F, BODY_SIZES[index][2] * -0.5F, BODY_SIZES[index][0], BODY_SIZES[index][1], BODY_SIZES[index][2]);
         this.bodyParts[index].setRotationPoint(0.0F, 24 - BODY_SIZES[index][1], f);
         if (index < this.bodyParts.length - 1) {
            f += (BODY_SIZES[index][2] + BODY_SIZES[index + 1][2]) * 0.5F;
         }
      }
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      for (int index = 0; index < this.bodyParts.length; index++) {
         this.bodyParts[index].rotateAngleY = MathHelper.cos(ageInTicks * 0.9F + index * 0.15F * (float) Math.PI)
            * (float) Math.PI
            * 0.01F
            * (1 + Math.abs(index - 2));
         this.bodyParts[index].rotationPointX = MathHelper.sin(ageInTicks * 0.9F + index * 0.15F * (float) Math.PI)
            * (float) Math.PI
            * 0.1F
            * Math.abs(index - 2);
      }
   }
}
