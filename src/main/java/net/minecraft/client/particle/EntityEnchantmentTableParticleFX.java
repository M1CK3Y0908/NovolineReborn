package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityEnchantmentTableParticleFX extends EntityFX {
   private double coordX;
   private double coordY;
   private double coordZ;
   private float initialScale;

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      float f = (float)this.particleAge / this.particleMaxAge;
      f = 1.0F - f;
      float f2 = 1.0F - f;
      f2 *= f2;
      f2 *= f2;
      this.posX = this.coordX + this.motionX * f;
      this.posY = this.coordY + this.motionY * f - f2 * 1.2F;
      this.posZ = this.coordZ + this.motionZ * f;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }
   }

   @Override
   public int getBrightnessForRender(float partialTicks) {
      int brightnessForRender = super.getBrightnessForRender(partialTicks);
      float f = (float)this.particleAge / this.particleMaxAge;
      f *= f;
      f *= f;
      int i = brightnessForRender & 0xFF;
      int i2 = brightnessForRender >> 16 & 0xFF;
      i2 += (int)(f * 15.0F * 16.0F);
      if (i2 > 240) {
         i2 = 240;
      }

      return i | i2 << 16;
   }

   protected EntityEnchantmentTableParticleFX(
      World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn
   ) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.motionX = xSpeedIn;
      this.motionY = ySpeedIn;
      this.motionZ = zSpeedIn;
      this.coordX = xCoordIn;
      this.coordY = yCoordIn;
      this.coordZ = zCoordIn;
      this.posX = this.prevPosX = xCoordIn + xSpeedIn;
      this.posY = this.prevPosY = yCoordIn + ySpeedIn;
      this.posZ = this.prevPosZ = zCoordIn + zSpeedIn;
      float f = this.rand.nextFloat() * 0.6F + 0.4F;
      this.initialScale = this.particleScale = this.rand.nextFloat() * 0.5F + 0.2F;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F * f;
      this.particleGreen *= 0.9F;
      this.particleRed *= 0.9F;
      this.particleMaxAge = (int)(Math.random() * 10.0) + 30;
      this.noClip = true;
      this.setParticleTextureIndex((int)(Math.random() * 26.0 + 1.0 + 224.0));
   }

   public static class EnchantmentTable implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         return new EntityEnchantmentTableParticleFX(world2, d, d2, d3, d4, d5, d6);
      }
   }
}
