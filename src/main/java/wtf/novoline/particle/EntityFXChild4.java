package wtf.novoline.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFXChild4 extends EntityFX {
   float baseScale;

   @Override
   public void renderParticle(WorldRenderer worldRenderer, Entity entity2, float f, float f2, float f3, float f4, float f5, float f6) {
      float f7 = (this.particleAge + f) / this.particleMaxAge * 32.0F;
      f7 = MathHelper.clamp_float(f7, 0.0F, 1.0F);
      this.particleScale = this.baseScale * f7;
      super.renderParticle(worldRenderer, entity2, f, f2, f3, f4, f5, f6);
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }

      this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
      this.motionY -= 0.03;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.99F;
      this.motionY *= 0.99F;
      this.motionZ *= 0.99F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }
   }

   protected EntityFXChild4(World world2, double d, double d2, double d3, double d4, double d5, double d6, float f) {
      super(world2, d, d2, d3, d4, d5, d6);
      this.motionX *= 0.1F;
      this.motionY *= 0.1F;
      this.motionZ *= 0.1F;
      this.motionX += d4;
      this.motionY += d5;
      this.motionZ += d6;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F - (float)(Math.random() * 0.3F);
      this.particleScale *= 0.75F;
      this.particleScale *= f;
      this.baseScale = this.particleScale;
      this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
      this.particleMaxAge = (int)(this.particleMaxAge * f);
      this.noClip = false;
   }

   protected EntityFXChild4(World world2, double d, double d2, double d3, double d4, double d5, double d6) {
      this(world2, d, d2, d3, d4, d5, d6, 1.0F);
   }

   public static class IParticleFactoryImpl implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         return new EntityFXChild4(world2, d, d2, d3, d4, d5, d6);
      }
   }
}
