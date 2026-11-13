package wtf.novoline.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFXChild5 extends EntityFX {
   float baseScale;

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      if (this.posY == this.prevPosY) {
         this.motionX *= 1.1;
         this.motionZ *= 1.1;
      }

      this.motionX *= 0.86F;
      this.motionY *= 0.86F;
      this.motionZ *= 0.86F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }
   }

   protected EntityFXChild5(World world2, double d, double d2, double d3, double d4, double d5, double d6, float f) {
      super(world2, d, d2, d3, 0.0, 0.0, 0.0);
      this.motionX *= 0.01F;
      this.motionY *= 0.01F;
      this.motionZ *= 0.01F;
      this.motionY += 0.1;
      this.particleScale *= 0.75F;
      this.particleScale *= f;
      this.baseScale = this.particleScale;
      this.particleMaxAge = 16;
      this.noClip = false;
      this.setParticleTextureIndex(80);
   }

   protected EntityFXChild5(World world2, double d, double d2, double d3, double d4, double d5, double d6) {
      this(world2, d, d2, d3, d4, d5, d6, 2.0F);
   }

   @Override
   public void renderParticle(WorldRenderer worldRenderer, Entity entity2, float f, float f2, float f3, float f4, float f5, float f6) {
      float f7 = (this.particleAge + f) / this.particleMaxAge * 32.0F;
      f7 = MathHelper.clamp_float(f7, 0.0F, 1.0F);
      this.particleScale = this.baseScale * f7;
      super.renderParticle(worldRenderer, entity2, f, f2, f3, f4, f5, f6);
   }

   public static class IParticleFactoryImpl implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         EntityFXChild5 entityFXChild5 = new EntityFXChild5(world2, d, d2 + 0.5, d3, d4, d5, d6);
         entityFXChild5.setParticleTextureIndex(81);
         entityFXChild5.setRBGColorF(1.0F, 1.0F, 1.0F);
         return entityFXChild5;
      }
   }

   public static class IParticleFactoryImpl2 implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         return new EntityFXChild5(world2, d, d2, d3, d4, d5, d6);
      }
   }
}
