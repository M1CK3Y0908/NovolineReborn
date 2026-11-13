package wtf.novoline.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class DemonModel extends ModelBase {
   private final ModelRenderer modelRenderer;
   private final ModelRenderer modelRenderer2;
   private final ModelRenderer modelRenderer3;
   private final ModelRenderer modelRenderer4;
   private final ModelRenderer modelRenderer5;
   private final ModelRenderer modelRenderer6;
   private final ModelRenderer modelRenderer7;
   private final ModelRenderer modelRenderer8;
   private final ModelRenderer modelRenderer9;
   private final ModelRenderer modelRenderer10;
   private final ModelRenderer modelRenderer11;
   private final ModelRenderer modelRenderer12;
   private final ModelRenderer modelRenderer13;
   private final ModelRenderer modelRenderer14;
   private final ModelRenderer modelRenderer15;
   private final ModelRenderer modelRenderer16;
   private static final ResourceLocation TEXTURE = new ResourceLocation("novoline", "models/demon/demon.png");
   private final ModelRenderer modelRenderer17;
   private final ModelRenderer modelRenderer18;

   public void setRotation(ModelRenderer modelRenderer19, float f, float f2, float f3) {
      modelRenderer19.rotateAngleX = f;
      modelRenderer19.rotateAngleY = f2;
      modelRenderer19.rotateAngleZ = f3;
   }

   @Override
   public void render(Entity entity2, float f, float f2, float f3, float f4, float f5, float f6) {
      GlStateManager.pushMatrix();
      Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
      this.modelRenderer.render(f6);
      this.modelRenderer4.render(f6);
      this.modelRenderer7.render(f6);
      this.modelRenderer8.render(f6);
      this.modelRenderer9.render(f6);
      this.modelRenderer14.render(f6);
      GlStateManager.popMatrix();
   }

   @Override
   public void setRotationAngles(float f, float f2, float f3, float f4, float f5, float f6, Entity entity2) {
      this.modelRenderer8.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * f2;
      this.modelRenderer9.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f2;
      this.modelRenderer.rotateAngleY = f4 / (180.0F / (float)Math.PI);
      this.modelRenderer.rotateAngleX = f5 / (180.0F / (float)Math.PI);
      this.modelRenderer7.rotateAngleX = MathHelper.cos(f * 0.6662F) * f2;
      this.modelRenderer14.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f2;
   }

   public DemonModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      (this.modelRenderer = new ModelRenderer(this)).setRotationPoint(0.0F, -6.0F, -1.0F);
      this.modelRenderer.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -3.0F, 8, 8, 8, 0.0F);
      (this.modelRenderer2 = new ModelRenderer(this)).setRotationPoint(-8.0F, 8.0F, 0.0F);
      this.modelRenderer.addChild(this.modelRenderer2);
      this.setRotation(this.modelRenderer2, -0.3927F, 0.3927F, -0.5236F);
      this.modelRenderer2.setTextureOffset(32, 8).addBox(13.4346F, -5.2071F, 2.7071F, 6, 2, 2, 0.0F);
      this.modelRenderer2.setTextureOffset(0, 0).addBox(17.4346F, -10.4071F, 2.7071F, 2, 5, 2, 0.0F);
      (this.modelRenderer3 = new ModelRenderer(this)).setRotationPoint(8.0F, 8.0F, 0.0F);
      this.modelRenderer.addChild(this.modelRenderer3);
      this.setRotation(this.modelRenderer3, -0.3927F, -0.3927F, 0.5236F);
      this.modelRenderer3.setTextureOffset(32, 8).addBox(-19.4346F, -5.2071F, 2.7071F, 6, 2, 2, 0.0F);
      this.modelRenderer3.setTextureOffset(0, 0).addBox(-19.4346F, -10.4071F, 2.7071F, 2, 5, 2, 0.0F);
      (this.modelRenderer4 = new ModelRenderer(this)).setRotationPoint(0.5F, -0.1F, -3.5F);
      this.setRotation(this.modelRenderer4, 0.1745F, 0.0F, 0.0F);
      this.modelRenderer4.setTextureOffset(0, 16).addBox(-4.5F, -1.7028F, 1.4696F, 8, 12, 4, 0.0F);
      (this.modelRenderer5 = new ModelRenderer(this)).setRotationPoint(8.25F, -2.0F, 10.0F);
      this.modelRenderer4.addChild(this.modelRenderer5);
      this.setRotation(this.modelRenderer5, 0.0873F, -0.829F, 0.1745F);
      this.modelRenderer5.setTextureOffset(40, 12).addBox(-7.0072F, -0.5972F, 0.7515F, 12, 13, 0, 0.0F);
      (this.modelRenderer6 = new ModelRenderer(this)).setRotationPoint(-9.25F, -2.0F, 10.0F);
      this.modelRenderer4.addChild(this.modelRenderer6);
      this.setRotation(this.modelRenderer6, 0.0873F, 0.829F, -0.1745F);
      this.modelRenderer6.setTextureOffset(40, 12).addBox(-4.9928F, -0.5972F, 0.7515F, 12, 13, 0, 0.0F);
      (this.modelRenderer7 = new ModelRenderer(this)).setRotationPoint(5.4F, -1.25F, -2.0F);
      this.setRotation(this.modelRenderer7, 0.0F, 0.0F, -0.2182F);
      this.modelRenderer7.setTextureOffset(24, 16).addBox(-1.1F, -1.05F, 0.0F, 4, 14, 4, 0.0F);
      (this.modelRenderer8 = new ModelRenderer(this)).setRotationPoint(-5.4F, -1.25F, -2.0F);
      this.setRotation(this.modelRenderer8, 0.0F, 0.0F, 0.2182F);
      this.modelRenderer8.setTextureOffset(24, 16).addBox(-2.9F, -1.05F, 0.0F, 4, 14, 4, 0.0F);
      (this.modelRenderer9 = new ModelRenderer(this)).setRotationPoint(3.0F, 10.0F, 0.0F);
      this.modelRenderer9.setTextureOffset(48, 22).addBox(-3.25F, -2.25F, -1.0F, 4, 9, 4, 0.0F);
      (this.modelRenderer10 = new ModelRenderer(this)).setRotationPoint(-1.7F, -0.1F, -3.55F);
      this.modelRenderer9.addChild(this.modelRenderer10);
      this.setRotation(this.modelRenderer10, -0.5236F, 0.0F, 0.0F);
      this.modelRenderer10.setTextureOffset(34, 34).addBox(0.95F, 4.6F, 8.0511F, 3, 5, 3, 0.0F);
      (this.modelRenderer11 = new ModelRenderer(this)).setRotationPoint(1.4F, 15.0F, 0.25F);
      this.modelRenderer10.addChild(this.modelRenderer11);
      this.setRotation(this.modelRenderer11, 0.5236F, 0.0F, 0.0F);
      this.modelRenderer11.setTextureOffset(26, 0).addBox(-0.7F, -1.15F, 9.3F, 4, 2, 4, 0.0F);
      this.modelRenderer11.setTextureOffset(40, 0).addBox(-0.7F, -1.15F, 7.3F, 4, 2, 2, 0.0F);
      (this.modelRenderer12 = new ModelRenderer(this)).setRotationPoint(-1.0F, 0.0F, -2.0F);
      this.modelRenderer10.addChild(this.modelRenderer12);
      this.setRotation(this.modelRenderer12, 0.0F, -0.0873F, -0.2618F);
      (this.modelRenderer13 = new ModelRenderer(this)).setRotationPoint(1.9F, 12.0F, 0.25F);
      this.modelRenderer12.addChild(this.modelRenderer13);
      this.modelRenderer13.setTextureOffset(16, 34).addBox(-0.7911F, -10.1159F, 8.0029F, 4, 4, 5, 0.0F);
      this.modelRenderer13.setTextureOffset(0, 32).addBox(-0.7911F, -15.1159F, 4.0029F, 4, 9, 4, 0.0F);
      (this.modelRenderer14 = new ModelRenderer(this)).setRotationPoint(-3.0F, 10.0F, 0.0F);
      this.modelRenderer14.setTextureOffset(48, 22).addBox(-0.75F, -2.25F, -1.0F, 4, 9, 4, 0.0F);
      (this.modelRenderer15 = new ModelRenderer(this)).setRotationPoint(1.7F, -0.1F, -3.55F);
      this.modelRenderer14.addChild(this.modelRenderer15);
      this.setRotation(this.modelRenderer15, -0.5236F, 0.0F, 0.0F);
      this.modelRenderer15.setTextureOffset(34, 34).addBox(-3.95F, 4.6F, 8.0511F, 3, 5, 3, 0.0F);
      (this.modelRenderer16 = new ModelRenderer(this)).setRotationPoint(-1.4F, 15.0F, 0.25F);
      this.modelRenderer15.addChild(this.modelRenderer16);
      this.setRotation(this.modelRenderer16, 0.5236F, 0.0F, 0.0F);
      this.modelRenderer16.setTextureOffset(26, 0).addBox(-3.3F, -1.15F, 9.3F, 4, 2, 4, 0.0F);
      this.modelRenderer16.setTextureOffset(40, 0).addBox(-3.3F, -1.15F, 7.3F, 4, 2, 2, 0.0F);
      (this.modelRenderer17 = new ModelRenderer(this)).setRotationPoint(1.0F, 0.0F, -2.0F);
      this.modelRenderer15.addChild(this.modelRenderer17);
      this.setRotation(this.modelRenderer17, 0.0F, 0.0873F, 0.2618F);
      (this.modelRenderer18 = new ModelRenderer(this)).setRotationPoint(-1.9F, 12.0F, 0.25F);
      this.modelRenderer17.addChild(this.modelRenderer18);
      this.modelRenderer18.setTextureOffset(16, 34).addBox(-3.2089F, -10.1159F, 8.0029F, 4, 4, 5, 0.0F);
      this.modelRenderer18.setTextureOffset(0, 32).addBox(-3.2089F, -15.1159F, 4.0029F, 4, 9, 4, 0.0F);
   }
}
