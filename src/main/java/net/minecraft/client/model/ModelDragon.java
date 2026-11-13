package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;

public class ModelDragon extends ModelBase {
   private ModelRenderer head;
   private ModelRenderer jaw;
   private ModelRenderer spine;
   private ModelRenderer body;
   private ModelRenderer wing;
   private ModelRenderer wingTip;
   private ModelRenderer frontLeg;
   private ModelRenderer frontLegTip;
   private ModelRenderer frontFoot;
   private ModelRenderer rearLeg;
   private ModelRenderer rearLegTip;
   private ModelRenderer rearFoot;
   private float partialTicks;

   public ModelDragon(float f) {
      this.textureWidth = 256;
      this.textureHeight = 256;
      this.setTextureOffset("body.body", 0, 0);
      this.setTextureOffset("wing.skin", -56, 88);
      this.setTextureOffset("wingtip.skin", -56, 144);
      this.setTextureOffset("rearleg.main", 0, 0);
      this.setTextureOffset("rearfoot.main", 112, 0);
      this.setTextureOffset("rearlegtip.main", 196, 0);
      this.setTextureOffset("head.upperhead", 112, 30);
      this.setTextureOffset("wing.bone", 112, 88);
      this.setTextureOffset("head.upperlip", 176, 44);
      this.setTextureOffset("jaw.jaw", 176, 65);
      this.setTextureOffset("frontleg.main", 112, 104);
      this.setTextureOffset("wingtip.bone", 112, 136);
      this.setTextureOffset("frontfoot.main", 144, 104);
      this.setTextureOffset("neck.box", 192, 104);
      this.setTextureOffset("frontlegtip.main", 226, 138);
      this.setTextureOffset("body.scale", 220, 53);
      this.setTextureOffset("head.scale", 0, 0);
      this.setTextureOffset("neck.scale", 48, 0);
      this.setTextureOffset("head.nostril", 112, 0);
      float f2 = -16.0F;
      this.head = new ModelRenderer(this, "head");
      this.head.addBox("upperlip", -6.0F, -1.0F, -8.0F + f2, 12, 5, 16);
      this.head.addBox("upperhead", -8.0F, -8.0F, 6.0F + f2, 16, 16, 16);
      this.head.mirror = true;
      this.head.addBox("scale", -5.0F, -12.0F, 12.0F + f2, 2, 4, 6);
      this.head.addBox("nostril", -5.0F, -3.0F, -6.0F + f2, 2, 2, 4);
      this.head.mirror = false;
      this.head.addBox("scale", 3.0F, -12.0F, 12.0F + f2, 2, 4, 6);
      this.head.addBox("nostril", 3.0F, -3.0F, -6.0F + f2, 2, 2, 4);
      this.jaw = new ModelRenderer(this, "jaw");
      this.jaw.setRotationPoint(0.0F, 4.0F, 8.0F + f2);
      this.jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16);
      this.head.addChild(this.jaw);
      this.spine = new ModelRenderer(this, "neck");
      this.spine.addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10);
      this.spine.addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6);
      this.body = new ModelRenderer(this, "body");
      this.body.setRotationPoint(0.0F, 4.0F, 8.0F);
      this.body.addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64);
      this.body.addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12);
      this.body.addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12);
      this.body.addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12);
      this.wing = new ModelRenderer(this, "wing");
      this.wing.setRotationPoint(-12.0F, 5.0F, 2.0F);
      this.wing.addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8);
      this.wing.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
      this.wingTip = new ModelRenderer(this, "wingtip");
      this.wingTip.setRotationPoint(-56.0F, 0.0F, 0.0F);
      this.wingTip.addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4);
      this.wingTip.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
      this.wing.addChild(this.wingTip);
      this.frontLeg = new ModelRenderer(this, "frontleg");
      this.frontLeg.setRotationPoint(-12.0F, 20.0F, 2.0F);
      this.frontLeg.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8);
      this.frontLegTip = new ModelRenderer(this, "frontlegtip");
      this.frontLegTip.setRotationPoint(0.0F, 20.0F, -1.0F);
      this.frontLegTip.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6);
      this.frontLeg.addChild(this.frontLegTip);
      this.frontFoot = new ModelRenderer(this, "frontfoot");
      this.frontFoot.setRotationPoint(0.0F, 23.0F, 0.0F);
      this.frontFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16);
      this.frontLegTip.addChild(this.frontFoot);
      this.rearLeg = new ModelRenderer(this, "rearleg");
      this.rearLeg.setRotationPoint(-16.0F, 16.0F, 42.0F);
      this.rearLeg.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16);
      this.rearLegTip = new ModelRenderer(this, "rearlegtip");
      this.rearLegTip.setRotationPoint(0.0F, 32.0F, -4.0F);
      this.rearLegTip.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12);
      this.rearLeg.addChild(this.rearLegTip);
      this.rearFoot = new ModelRenderer(this, "rearfoot");
      this.rearFoot.setRotationPoint(0.0F, 31.0F, 4.0F);
      this.rearFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24);
      this.rearLegTip.addChild(this.rearFoot);
   }

   @Override
   public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float f, float f2, float partialTickTime) {
      this.partialTicks = partialTickTime;
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      GlStateManager.pushMatrix();
      EntityDragon entityDragon = (EntityDragon)entityIn;
      float f6 = entityDragon.prevAnimTime + (entityDragon.animTime - entityDragon.prevAnimTime) * this.partialTicks;
      this.jaw.rotateAngleX = (float)(Math.sin(f6 * (float) Math.PI * 2.0F) + 1.0) * 0.2F;
      float f7 = (float)(Math.sin(f6 * (float) Math.PI * 2.0F - 1.0F) + 1.0);
      f7 = (f7 * f7 * 1.0F + f7 * 2.0F) * 0.05F;
      GlStateManager.translate(0.0F, f7 - 2.0F, -3.0F);
      GlStateManager.rotate(f7 * 2.0F, 1.0F, 0.0F, 0.0F);
      float f8 = 0.0F;
      float f9 = 1.5F;
      double[] movementOffsets = entityDragon.getMovementOffsets(6, this.partialTicks);
      float f10 = this.updateRotations(entityDragon.getMovementOffsets(5, this.partialTicks)[0] - entityDragon.getMovementOffsets(10, this.partialTicks)[0]);
      float f11 = this.updateRotations(entityDragon.getMovementOffsets(5, this.partialTicks)[0] + f10 / 2.0F);
      float f12 = f6 * (float) Math.PI * 2.0F;
      float f13 = 20.0F;
      float f14 = -12.0F;

      for (int i = 0; i < 5; i++) {
         double[] movementOffsets2 = entityDragon.getMovementOffsets(5 - i, this.partialTicks);
         float f15 = (float)Math.cos(i * 0.45F + f12) * 0.15F;
         this.spine.rotateAngleY = this.updateRotations(movementOffsets2[0] - movementOffsets[0]) * (float) Math.PI / 180.0F * f9;
         this.spine.rotateAngleX = f15 + (float)(movementOffsets2[1] - movementOffsets[1]) * (float) Math.PI / 180.0F * f9 * 5.0F;
         this.spine.rotateAngleZ = -this.updateRotations(movementOffsets2[0] - f11) * (float) Math.PI / 180.0F * f9;
         this.spine.rotationPointY = f13;
         this.spine.rotationPointZ = f14;
         this.spine.rotationPointX = f8;
         f13 = (float)(f13 + Math.sin(this.spine.rotateAngleX) * 10.0);
         f14 = (float)(f14 - Math.cos(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0);
         f8 = (float)(f8 - Math.sin(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0);
         this.spine.render(scale);
      }

      this.head.rotationPointY = f13;
      this.head.rotationPointZ = f14;
      this.head.rotationPointX = f8;
      double[] movementOffsets3 = entityDragon.getMovementOffsets(0, this.partialTicks);
      this.head.rotateAngleY = this.updateRotations(movementOffsets3[0] - movementOffsets[0]) * (float) Math.PI / 180.0F * 1.0F;
      this.head.rotateAngleZ = -this.updateRotations(movementOffsets3[0] - f11) * (float) Math.PI / 180.0F * 1.0F;
      this.head.render(scale);
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-f10 * f9 * 1.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(0.0F, -1.0F, 0.0F);
      this.body.rotateAngleZ = 0.0F;
      this.body.render(scale);

      for (int i2 = 0; i2 < 2; i2++) {
         GlStateManager.enableCull();
         float f16 = f6 * (float) Math.PI * 2.0F;
         this.wing.rotateAngleX = 0.125F - (float)Math.cos(f16) * 0.2F;
         this.wing.rotateAngleY = 0.25F;
         this.wing.rotateAngleZ = (float)(Math.sin(f16) + 0.125) * 0.8F;
         this.wingTip.rotateAngleZ = -((float)(Math.sin(f16 + 2.0F) + 0.5)) * 0.75F;
         this.rearLeg.rotateAngleX = 1.0F + f7 * 0.1F;
         this.rearLegTip.rotateAngleX = 0.5F + f7 * 0.1F;
         this.rearFoot.rotateAngleX = 0.75F + f7 * 0.1F;
         this.frontLeg.rotateAngleX = 1.3F + f7 * 0.1F;
         this.frontLegTip.rotateAngleX = -0.5F - f7 * 0.1F;
         this.frontFoot.rotateAngleX = 0.75F + f7 * 0.1F;
         this.wing.render(scale);
         this.frontLeg.render(scale);
         this.rearLeg.render(scale);
         GlStateManager.scale(-1.0F, 1.0F, 1.0F);
         if (i2 == 0) {
            GlStateManager.cullFace(1028);
         }
      }

      GlStateManager.popMatrix();
      GlStateManager.cullFace(1029);
      GlStateManager.disableCull();
      float f17 = -((float)Math.sin(f6 * (float) Math.PI * 2.0F)) * 0.0F;
      f12 = f6 * (float) Math.PI * 2.0F;
      f13 = 10.0F;
      f14 = 60.0F;
      f8 = 0.0F;
      movementOffsets = entityDragon.getMovementOffsets(11, this.partialTicks);

      for (int i3 = 0; i3 < 12; i3++) {
         movementOffsets3 = entityDragon.getMovementOffsets(12 + i3, this.partialTicks);
         f17 = (float)(f17 + Math.sin(i3 * 0.45F + f12) * 0.05F);
         this.spine.rotateAngleY = (this.updateRotations(movementOffsets3[0] - movementOffsets[0]) * f9 + 180.0F) * (float) Math.PI / 180.0F;
         this.spine.rotateAngleX = f17 + (float)(movementOffsets3[1] - movementOffsets[1]) * (float) Math.PI / 180.0F * f9 * 5.0F;
         this.spine.rotateAngleZ = this.updateRotations(movementOffsets3[0] - f11) * (float) Math.PI / 180.0F * f9;
         this.spine.rotationPointY = f13;
         this.spine.rotationPointZ = f14;
         this.spine.rotationPointX = f8;
         f13 = (float)(f13 + Math.sin(this.spine.rotateAngleX) * 10.0);
         f14 = (float)(f14 - Math.cos(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0);
         f8 = (float)(f8 - Math.sin(this.spine.rotateAngleY) * Math.cos(this.spine.rotateAngleX) * 10.0);
         this.spine.render(scale);
      }

      GlStateManager.popMatrix();
   }

   private float updateRotations(double d) {
      while (d >= 180.0) {
         d -= 360.0;
      }

      while (d < -180.0) {
         d += 360.0;
      }

      return (float)d;
   }
}
