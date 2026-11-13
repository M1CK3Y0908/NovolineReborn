package net.minecraft.client.model;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.utils.render.RenderUtils;

public class ModelElytra extends ModelBase {
   private final ModelRenderer leftWing;
   private static final ResourceLocation resourceLocation = new ResourceLocation("novoline", "models/amongus/amogus_white.png");
   private final ModelRenderer modelRenderer;
   private final ModelRenderer modelRenderer2;
   private static final ResourceLocation resourceLocation2 = new ResourceLocation("novoline", "models/amongus/amogus_red.png");
   private static final ResourceLocation resourceLocation3 = new ResourceLocation("novoline", "models/amongus/amogus_glass.png");
   private final ModelRenderer rightWing;

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      float f = 1.0F;
      if (f < 1.0F) {
         f = 1.0F;
      }

      this.modelRenderer2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
      this.modelRenderer.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount / f;
      this.modelRenderer2.rotateAngleY = 0.0F;
      this.modelRenderer.rotateAngleY = 0.0F;
      this.modelRenderer2.rotateAngleZ = 0.0F;
      this.modelRenderer.rotateAngleZ = 0.0F;
   }

   @Override
   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      GlStateManager.pushMatrix();
      GlStateManager.scale(1.5F, 1.5F, 1.5F);
      GlStateManager.translate(0.0, -0.5, 0.0);
      int currentElement = ModuleManager.customModel.colorType.getCurrentElement();
      if (currentElement == 0) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation2);
         this.leftWing.render(scale);
      } else {
         Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
         if (currentElement == 1) {
            RenderUtils.setColor(ModuleManager.customModel.amongUsColor.getColor());
            this.leftWing.render(scale);
         } else if (currentElement == 2) {
            RenderUtils.setColor(Color.getHSBColor(entityIn.hashCode() * 20 % 100 / 100.0F, 0.8F, 0.8F).getRGB());
            this.leftWing.render(scale);
         } else if (currentElement == 3) {
            RenderUtils.setColor(
               RenderUtils.indexedHsbColor(entityIn.hashCode() * 20 % 100, 100, 0.7F, 1.0F, (long)ModuleManager.customModel.cycleTime.getCurrentValue())
            );
            this.leftWing.render(scale);
         } else if (currentElement == 4) {
            RenderUtils.setColor(
               RenderUtils.indexedHsbColorMirrored(entityIn.hashCode() * 20 % 100, 100, 0.7F, 1.0F, (long)ModuleManager.customModel.cycleTime.getCurrentValue())
            );
            this.leftWing.render(scale);
         }

         RenderUtils.resetColor();
         Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation3);
         this.leftWing.render(scale);
      }

      GlStateManager.popMatrix();
   }

   public void handleModelRenderer(ModelRenderer modelRenderer3, float f, float f2, float f3) {
      modelRenderer3.rotateAngleX = f;
      modelRenderer3.rotateAngleY = f2;
      modelRenderer3.rotateAngleZ = f3;
   }

   public ModelElytra() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.leftWing = new ModelRenderer(this);
      this.leftWing.setRotationPoint(0.0F, 24.0F, 0.0F);
      this.modelRenderer = new ModelRenderer(this);
      this.modelRenderer.setRotationPoint(3.0F, -4.0F, 0.0F);
      this.leftWing.addChild(this.modelRenderer);
      this.modelRenderer.cubeList.add(new ModelBox(this.modelRenderer, 0, 0, -2.0F, 0.0F, -2.0F, 4, 4, 4, 0.0F, false));
      this.modelRenderer2 = new ModelRenderer(this);
      this.modelRenderer2.setRotationPoint(-3.0F, -4.0F, 0.0F);
      this.leftWing.addChild(this.modelRenderer2);
      this.modelRenderer2.cubeList.add(new ModelBox(this.modelRenderer2, 0, 0, -2.0F, 0.0F, -2.0F, 4, 4, 4, 0.0F, false));
      this.rightWing = new ModelRenderer(this);
      this.rightWing.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.leftWing.addChild(this.rightWing);
      this.rightWing.cubeList.add(new ModelBox(this.rightWing, 16, 0, -5.0F, -16.0F, -3.0F, 10, 12, 6, 0.0F, false));
      this.rightWing.cubeList.add(new ModelBox(this.rightWing, 48, 0, -3.0F, -14.0F, -4.0F, 6, 4, 1, 0.0F, false));
      this.rightWing.cubeList.add(new ModelBox(this.rightWing, 16, 18, -3.0F, -13.0F, 3.0F, 6, 7, 3, 0.0F, false));
   }
}
