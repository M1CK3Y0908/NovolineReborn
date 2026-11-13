package wtf.novoline.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.world.World;

public class EntityFXChild3 extends EntityFX {
   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }

      this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
      this.motionY += 0.004;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9F;
      this.motionY *= 0.9F;
      this.motionZ *= 0.9F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }
   }

   protected EntityFXChild3(World world2, double d, double d2, double d3, double d4, double d5, double d6) {
      super(world2, d, d2, d3, d4, d5, d6);
      this.motionX = d4 + (Math.random() * 2.0 - 1.0) * 0.05F;
      this.motionY = d5 + (Math.random() * 2.0 - 1.0) * 0.05F;
      this.motionZ = d6 + (Math.random() * 2.0 - 1.0) * 0.05F;
      this.particleRed = this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.3F + 0.7F;
      this.particleScale = this.rand.nextFloat() * this.rand.nextFloat() * 6.0F + 1.0F;
      this.particleMaxAge = (int)(16.0 / (this.rand.nextFloat() * 0.8 + 0.2)) + 2;
   }

   public static class IParticleFactoryImpl implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         return new EntityFXChild3(world2, d, d2, d3, d4, d5, d6);
      }
   }
}
