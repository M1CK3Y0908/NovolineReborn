package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityLargeExplodeFX extends EntityFX {
   private TextureManager theRenderEngine;
   private static final ResourceLocation EXPLOSION_TEXTURE = new ResourceLocation("textures/entity/explosion.png");
   private int maxAge;
   private float size;
   private int age;
   private static final VertexFormat EXPLOSION_VERTEX_FORMAT = new VertexFormat()
      .addElement(DefaultVertexFormats.POSITION_3F)
      .addElement(DefaultVertexFormats.TEX_2F)
      .addElement(DefaultVertexFormats.COLOR_4UB)
      .addElement(DefaultVertexFormats.TEX_2S)
      .addElement(DefaultVertexFormats.NORMAL_3B)
      .addElement(DefaultVertexFormats.PADDING_1B);

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.age++;
      if (this.age == this.maxAge) {
         this.setDead();
      }
   }

   protected EntityLargeExplodeFX(
      TextureManager renderEngine, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double d, double d2, double d3
   ) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
      this.theRenderEngine = renderEngine;
      this.maxAge = 6 + this.rand.nextInt(4);
      this.particleRed = this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.6F + 0.4F;
      this.size = 1.0F - (float)d * 0.5F;
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      return 61680;
   }

   @Override
   public int getFXLayer() {
      return 3;
   }

   @Override
   public void renderParticle(
      WorldRenderer worldRendererIn,
      Entity entityIn,
      float partialTicks,
      float rotationX,
      float rotationZ,
      float rotationYZ,
      float rotationXY,
      float rotationXZ
   ) {
      int i = (int)((this.age + partialTicks) * 15.0F / this.maxAge);
      if (i <= 15) {
         this.theRenderEngine.bindTexture(EXPLOSION_TEXTURE);
         float f = i % 4 / 4.0F;
         float f2 = f + 0.24975F;
         float f3 = i / 4 / 4.0F;
         float f4 = f3 + 0.24975F;
         float f5 = 2.0F * this.size;
         float f6 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
         float f7 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
         float f8 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableLighting();
         RenderHelper.disableStandardItemLighting();
         worldRendererIn.begin(7, EXPLOSION_VERTEX_FORMAT);
         worldRendererIn.pos(
               f6 - rotationX * f5 - rotationXY * f5,
               f7 - rotationZ * f5,
               f8 - rotationYZ * f5 - rotationXZ * f5
            )
            .tex(f2, f4)
            .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
            .lightmap(0, 240)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
         worldRendererIn.pos(
               f6 - rotationX * f5 + rotationXY * f5,
               f7 + rotationZ * f5,
               f8 - rotationYZ * f5 + rotationXZ * f5
            )
            .tex(f2, f3)
            .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
            .lightmap(0, 240)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
         worldRendererIn.pos(
               f6 + rotationX * f5 + rotationXY * f5,
               f7 + rotationZ * f5,
               f8 + rotationYZ * f5 + rotationXZ * f5
            )
            .tex(f, f3)
            .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
            .lightmap(0, 240)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
         worldRendererIn.pos(
               f6 + rotationX * f5 - rotationXY * f5,
               f7 - rotationZ * f5,
               f8 + rotationYZ * f5 - rotationXZ * f5
            )
            .tex(f, f4)
            .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
            .lightmap(0, 240)
            .normal(0.0F, 1.0F, 0.0F)
            .endVertex();
         Tessellator.getInstance().draw();
         GlStateManager.enableLighting();
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         return new EntityLargeExplodeFX(Minecraft.getMinecraft().getTextureManager(), world2, d, d2, d3, d4, d5, d6);
      }
   }
}
