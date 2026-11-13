package wtf.novoline.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFXChild1 extends EntityFX {
   float baseScale;

   protected EntityFXChild1(World world2, double d, double d2, double d3, double d4, double d5, double d6) {
      this(world2, d, d2, d3, d4, d5, d6, 1.0F);
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
      this.particleGreen = (float)(this.particleGreen * 0.96);
      this.particleBlue = (float)(this.particleBlue * 0.9);
      this.motionX *= 0.7F;
      this.motionY *= 0.7F;
      this.motionZ *= 0.7F;
      this.motionY -= 0.02F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }
   }

   protected EntityFXChild1(World world2, double d, double d2, double d3, double d4, double d5, double d6, float f) {
      super(world2, d, d2, d3, 0.0, 0.0, 0.0);
      this.motionX *= 0.1F;
      this.motionY *= 0.1F;
      this.motionZ *= 0.1F;
      this.motionX += d4 * 0.4;
      this.motionY += d5 * 0.4;
      this.motionZ += d6 * 0.4;
      this.particleRed = this.particleGreen = this.particleBlue = (float)(Math.random() * 0.3F + 0.6F);
      this.particleScale *= 0.75F;
      this.particleScale *= f;
      this.baseScale = this.particleScale;
      this.particleMaxAge = (int)(6.0 / (Math.random() * 0.8 + 0.6));
      this.particleMaxAge = (int)(this.particleMaxAge * f);
      this.noClip = false;
      this.setParticleTextureIndex(65);
      this.onUpdate();
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
         return new EntityFXChild1(world2, d, d2, d3, d4, d5, d6);
      }
   }

   public static class IParticleFactoryImpl2 implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         EntityFXChild1 entityFXChild1 = new EntityFXChild1(world2, d, d2, d3, d4, d5, d6);
         entityFXChild1.setRBGColorF(entityFXChild1.getParticleRed() * 0.3F, entityFXChild1.getParticleGreen() * 0.8F, entityFXChild1.getParticleBlue());
         entityFXChild1.nextTextureIndexX();
         return entityFXChild1;
      }
   }
}
