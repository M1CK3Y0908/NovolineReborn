package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFlameFX extends EntityFX {
   private float flameScale;

   protected EntityFlameFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.motionX = this.motionX * 0.01F + xSpeedIn;
      this.motionY = this.motionY * 0.01F + ySpeedIn;
      this.motionZ = this.motionZ * 0.01F + zSpeedIn;
      this.posX = this.posX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
      this.posY = this.posY + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
      this.posZ = this.posZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
      this.flameScale = this.particleScale;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
      this.noClip = true;
      this.setParticleTextureIndex(48);
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
      float f = (this.particleAge + partialTicks) / this.particleMaxAge;
      this.particleScale = this.flameScale * (1.0F - f * f * 0.5F);
      super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.96F;
      this.motionY *= 0.96F;
      this.motionZ *= 0.96F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      float f = (this.particleAge + partialTicks) / this.particleMaxAge;
      f = MathHelper.clamp_float(f, 0.0F, 1.0F);
      int brightnessForRender = super.getBrightnessForRender(partialTicks);
      int i = brightnessForRender & 0xFF;
      int i2 = brightnessForRender >> 16 & 0xFF;
      i += (int)(f * 15.0F * 16.0F);
      if (i > 240) {
         i = 240;
      }

      return i | i2 << 16;
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         return new EntityFlameFX(world2, d, d2, d3, d4, d5, d6);
      }
   }
}
