package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class Barrier extends EntityFX {
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
      float minU = this.particleIcon.getMinU();
      float maxU = this.particleIcon.getMaxU();
      float minV = this.particleIcon.getMinV();
      float maxV = this.particleIcon.getMaxV();
      float f = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
      float f2 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
      float f3 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
      int brightnessForRender = this.getBrightnessForRender(partialTicks);
      int i = brightnessForRender >> 16 & 65535;
      int i2 = brightnessForRender & 65535;
      worldRendererIn.pos(f - rotationX * 0.5F - rotationXY * 0.5F, f2 - rotationZ * 0.5F, f3 - rotationYZ * 0.5F - rotationXZ * 0.5F)
         .tex(maxU, maxV)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(i, i2)
         .endVertex();
      worldRendererIn.pos(f - rotationX * 0.5F + rotationXY * 0.5F, f2 + rotationZ * 0.5F, f3 - rotationYZ * 0.5F + rotationXZ * 0.5F)
         .tex(maxU, minV)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(i, i2)
         .endVertex();
      worldRendererIn.pos(f + rotationX * 0.5F + rotationXY * 0.5F, f2 + rotationZ * 0.5F, f3 + rotationYZ * 0.5F + rotationXZ * 0.5F)
         .tex(minU, minV)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(i, i2)
         .endVertex();
      worldRendererIn.pos(f + rotationX * 0.5F - rotationXY * 0.5F, f2 - rotationZ * 0.5F, f3 + rotationYZ * 0.5F - rotationXZ * 0.5F)
         .tex(minU, maxV)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(i, i2)
         .endVertex();
   }

   protected Barrier(World worldIn, double d, double d2, double d3, Item item2) {
      super(worldIn, d, d2, d3, 0.0, 0.0, 0.0);
      this.setParticleIcon(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item2));
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.motionX = this.motionY = this.motionZ = 0.0;
      this.particleGravity = 0.0F;
      this.particleMaxAge = 80;
   }

   @Override
   public int getFXLayer() {
      return 1;
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         return new Barrier(world2, d, d2, d3, Item.getItemFromBlock(Blocks.barrier));
      }
   }
}
