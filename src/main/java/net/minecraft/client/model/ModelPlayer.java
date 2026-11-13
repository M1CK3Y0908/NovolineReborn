package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import wtf.novoline.model.DemonModel;
import wtf.novoline.model.SonicModel;
import wtf.novoline.module.ModuleManager;

public class ModelPlayer extends ModelBiped {
   private final ModelElytra modelElytra = new ModelElytra();
   private final SonicModel sonicModel = new SonicModel();
   private final DemonModel demonModel = new DemonModel();
   private boolean smallArms;
   private ModelRenderer bipedDeadmau5Head;
   private ModelRenderer bipedCape;
   public ModelRenderer bipedLeftArmwear;
   public ModelRenderer bipedRightArmwear;
   public ModelRenderer bipedLeftLegwear;
   public ModelRenderer bipedRightLegwear;
   public ModelRenderer bipedBodyWear;

   @Override
   public void setInvisible(boolean invisible) {
      super.setInvisible(invisible);
      this.bipedLeftArmwear.showModel = invisible;
      this.bipedRightArmwear.showModel = invisible;
      this.bipedLeftLegwear.showModel = invisible;
      this.bipedRightLegwear.showModel = invisible;
      this.bipedBodyWear.showModel = invisible;
      this.bipedCape.showModel = invisible;
      this.bipedDeadmau5Head.showModel = invisible;
   }

   public void renderCape(float f) {
      this.bipedCape.render(f);
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      if (ModuleManager.customModel.isEnabled() && ModuleManager.customModel.appliesTo(entityIn)) {
         int currentElement = ModuleManager.customModel.customModel.getCurrentElement();
         if (currentElement == 0) {
            if (this.modelElytra != null) {
               this.modelElytra.render(entityIn, f, f2, f3, f4, f5, scale);
               return;
            }
         } else if (currentElement == 1) {
            if (this.sonicModel != null) {
               this.sonicModel.render(entityIn, f, f2, f3, f4, f5, scale);
               return;
            }
         } else if (currentElement == 2 && this.demonModel != null) {
            this.demonModel.render(entityIn, f, f2, f3, f4, f5, scale);
            return;
         }
      }

      super.render(entityIn, f, f2, f3, f4, f5, scale);
      GlStateManager.pushMatrix();
      if (this.isChild) {
         float f6 = 2.0F;
         GlStateManager.scale(1.0F / f6, 1.0F / f6, 1.0F / f6);
         GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
         this.bipedLeftLegwear.render(scale);
         this.bipedRightLegwear.render(scale);
         this.bipedLeftArmwear.render(scale);
         this.bipedRightArmwear.render(scale);
         this.bipedBodyWear.render(scale);
      } else {
         if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         this.bipedLeftLegwear.render(scale);
         this.bipedRightLegwear.render(scale);
         this.bipedLeftArmwear.render(scale);
         this.bipedRightArmwear.render(scale);
         this.bipedBodyWear.render(scale);
      }

      GlStateManager.popMatrix();
   }

   public ModelPlayer(float f, boolean flag) {
      super(f, 0.0F, 64);
      this.smallArms = flag;
      this.bipedDeadmau5Head = new ModelRenderer(this, 24, 0);
      this.bipedDeadmau5Head.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, f);
      this.bipedCape = new ModelRenderer(this, 0, 0);
      this.bipedCape.setTextureSize(64, 32);
      this.bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, f);
      if (flag) {
         this.bipedLeftArm = new ModelRenderer(this, 32, 48);
         this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, f);
         this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
         this.bipedRightArm = new ModelRenderer(this, 40, 16);
         this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, f);
         this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
         this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
         this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, f + 0.25F);
         this.bipedLeftArmwear.setRotationPoint(5.0F, 2.5F, 0.0F);
         this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
         this.bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, f + 0.25F);
         this.bipedRightArmwear.setRotationPoint(-5.0F, 2.5F, 10.0F);
      } else {
         this.bipedLeftArm = new ModelRenderer(this, 32, 48);
         this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
         this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
         this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
         this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f + 0.25F);
         this.bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
         this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
         this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, f + 0.25F);
         this.bipedRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
      }

      this.bipedLeftLeg = new ModelRenderer(this, 16, 48);
      this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
      this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
      this.bipedLeftLegwear = new ModelRenderer(this, 0, 48);
      this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.25F);
      this.bipedLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
      this.bipedRightLegwear = new ModelRenderer(this, 0, 32);
      this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.25F);
      this.bipedRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
      this.bipedBodyWear = new ModelRenderer(this, 16, 32);
      this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f + 0.25F);
      this.bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
   }

   public void renderLeftArm() {
      this.bipedLeftArm.render(0.0625F);
      this.bipedLeftArmwear.render(0.0625F);
   }

   public void renderDeadmau5Head(float f) {
      copyModelAngles(this.bipedHead, this.bipedDeadmau5Head);
      this.bipedDeadmau5Head.rotationPointX = 0.0F;
      this.bipedDeadmau5Head.rotationPointY = 0.0F;
      this.bipedDeadmau5Head.render(f);
   }

   public void renderRightArm() {
      this.bipedRightArm.render(0.0625F);
      this.bipedRightArmwear.render(0.0625F);
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      if (ModuleManager.customModel.isEnabled() && ModuleManager.customModel.appliesTo(entityIn)) {
         int currentElement = ModuleManager.customModel.customModel.getCurrentElement();
         if (currentElement == 0) {
            if (this.modelElytra != null) {
               this.modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
            }
         } else if (currentElement == 1) {
            if (this.sonicModel != null) {
               this.sonicModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
               return;
            }
         } else if (currentElement == 2 && this.demonModel != null) {
            this.demonModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
            return;
         }
      }

      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      copyModelAngles(this.bipedLeftLeg, this.bipedLeftLegwear);
      copyModelAngles(this.bipedRightLeg, this.bipedRightLegwear);
      copyModelAngles(this.bipedLeftArm, this.bipedLeftArmwear);
      copyModelAngles(this.bipedRightArm, this.bipedRightArmwear);
      copyModelAngles(this.bipedBody, this.bipedBodyWear);
   }

   @Override
   public void postRenderArm(float scale) {
      if (this.smallArms) {
         this.bipedRightArm.rotationPointX++;
         this.bipedRightArm.postRender(scale);
         this.bipedRightArm.rotationPointX--;
      } else {
         this.bipedRightArm.postRender(scale);
      }
   }
}
