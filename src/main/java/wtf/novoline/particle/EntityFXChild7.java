package wtf.novoline.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.world.World;

public class EntityFXChild7 extends EntityFX {
   protected EntityFXChild7(World world2, double d, double d2, double d3, double d4, double d5, double d6) {
      super(world2, d, d2, d3, 0.0, 0.0, 0.0);
      this.motionX *= 0.3F;
      this.motionY = Math.random() * 0.2F + 0.1F;
      this.motionZ *= 0.3F;
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.setParticleTextureIndex(19);
      this.setSize(0.01F, 0.01F);
      this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
      this.particleGravity = 0.0F;
      this.motionX = d4;
      this.motionY = d5;
      this.motionZ = d6;
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY = this.motionY - this.particleGravity;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.98F;
      this.motionY *= 0.98F;
      this.motionZ *= 0.98F;
      int i = 60 - this.particleMaxAge;
      float f = i * 0.001F;
      this.setSize(f, f);
      this.setParticleTextureIndex(19 + i % 4);
      if (this.particleMaxAge-- <= 0) {
         this.setDead();
      }
   }

   public static class IParticleFactoryImpl implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         return new EntityFXChild7(world2, d, d2, d3, d4, d5, d6);
      }
   }
}
