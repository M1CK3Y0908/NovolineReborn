package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class EntityBreakingFX extends EntityFX {
   @Override
   public int getFXLayer() {
      return 1;
   }

   protected EntityBreakingFX(World worldIn, double posXIn, double posYIn, double posZIn, Item item2, int i) {
      super(worldIn, posXIn, posYIn, posZIn, 0.0, 0.0, 0.0);
      this.setParticleIcon(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item2, i));
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.particleGravity = Blocks.snow.blockParticleGravity;
      this.particleScale /= 2.0F;
   }

   protected EntityBreakingFX(World worldIn, double posXIn, double posYIn, double posZIn, Item item2) {
      this(worldIn, posXIn, posYIn, posZIn, item2, 0);
   }

   protected EntityBreakingFX(
      World worldIn, double posXIn, double posYIn, double posZIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, Item item2, int i
   ) {
      this(worldIn, posXIn, posYIn, posZIn, item2, i);
      this.motionX *= 0.1F;
      this.motionY *= 0.1F;
      this.motionZ *= 0.1F;
      this.motionX += xSpeedIn;
      this.motionY += ySpeedIn;
      this.motionZ += zSpeedIn;
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
      float f = (this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
      float f2 = f + 0.015609375F;
      float f3 = (this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
      float f4 = f3 + 0.015609375F;
      float f5 = 0.1F * this.particleScale;
      if (this.particleIcon != null) {
         f = this.particleIcon.getInterpolatedU(this.particleTextureJitterX / 4.0F * 16.0F);
         f2 = this.particleIcon.getInterpolatedU((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F);
         f3 = this.particleIcon.getInterpolatedV(this.particleTextureJitterY / 4.0F * 16.0F);
         f4 = this.particleIcon.getInterpolatedV((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F);
      }

      float f6 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
      float f7 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
      float f8 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
      int brightnessForRender = this.getBrightnessForRender(partialTicks);
      int i = brightnessForRender >> 16 & 65535;
      int i2 = brightnessForRender & 65535;
      worldRendererIn.pos(
            f6 - rotationX * f5 - rotationXY * f5,
            f7 - rotationZ * f5,
            f8 - rotationYZ * f5 - rotationXZ * f5
         )
         .tex(f, f4)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(i, i2)
         .endVertex();
      worldRendererIn.pos(
            f6 - rotationX * f5 + rotationXY * f5,
            f7 + rotationZ * f5,
            f8 - rotationYZ * f5 + rotationXZ * f5
         )
         .tex(f, f3)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(i, i2)
         .endVertex();
      worldRendererIn.pos(
            f6 + rotationX * f5 + rotationXY * f5,
            f7 + rotationZ * f5,
            f8 + rotationYZ * f5 + rotationXZ * f5
         )
         .tex(f2, f3)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(i, i2)
         .endVertex();
      worldRendererIn.pos(
            f6 + rotationX * f5 - rotationXY * f5,
            f7 - rotationZ * f5,
            f8 + rotationYZ * f5 - rotationXZ * f5
         )
         .tex(f2, f4)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(i, i2)
         .endVertex();
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         int i = iArray.length > 1 ? iArray[1] : 0;
         return new EntityBreakingFX(world2, d, d2, d3, d4, d5, d6, Item.getItemById(iArray[0]), i);
      }
   }

   public static class SlimeFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         return new EntityBreakingFX(world2, d, d2, d3, Items.slime_ball);
      }
   }

   public static class SnowballFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         return new EntityBreakingFX(world2, d, d2, d3, Items.snowball);
      }
   }
}
