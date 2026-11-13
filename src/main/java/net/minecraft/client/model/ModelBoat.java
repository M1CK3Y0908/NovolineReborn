package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelBoat extends ModelBase {
   public ModelRenderer[] boatSides = new ModelRenderer[5];

   public ModelBoat() {
      this.boatSides[0] = new ModelRenderer(this, 0, 8);
      this.boatSides[1] = new ModelRenderer(this, 0, 0);
      this.boatSides[2] = new ModelRenderer(this, 0, 0);
      this.boatSides[3] = new ModelRenderer(this, 0, 0);
      this.boatSides[4] = new ModelRenderer(this, 0, 0);
      this.boatSides[0].addBox(-12, -8, -3.0F, 24, 16, 4, 0.0F);
      this.boatSides[0].setRotationPoint(0.0F, 4, 0.0F);
      this.boatSides[1].addBox(-10, -7, -1.0F, 20, 6, 2, 0.0F);
      this.boatSides[1].setRotationPoint(-11, 4, 0.0F);
      this.boatSides[2].addBox(-10, -7, -1.0F, 20, 6, 2, 0.0F);
      this.boatSides[2].setRotationPoint(11, 4, 0.0F);
      this.boatSides[3].addBox(-10, -7, -1.0F, 20, 6, 2, 0.0F);
      this.boatSides[3].setRotationPoint(0.0F, 4, -9);
      this.boatSides[4].addBox(-10, -7, -1.0F, 20, 6, 2, 0.0F);
      this.boatSides[4].setRotationPoint(0.0F, 4, 9);
      this.boatSides[0].rotateAngleX = (float) (Math.PI / 2);
      this.boatSides[1].rotateAngleY = (float) (Math.PI * 3.0 / 2.0);
      this.boatSides[2].rotateAngleY = (float) (Math.PI / 2);
      this.boatSides[3].rotateAngleY = (float) Math.PI;
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      for (int index = 0; index < 5; index++) {
         this.boatSides[index].render(scale);
      }
   }
}
