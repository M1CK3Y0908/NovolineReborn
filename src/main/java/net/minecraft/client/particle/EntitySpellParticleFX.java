package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySpellParticleFX extends EntityFX {
   private static final Random RANDOM = new Random();
   private int baseSpellTextureIndex = 128;

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
      float f = (this.particleAge + partialTicks) / this.particleMaxAge * 32.0F;
      MathHelper.clamp_float(f, 0.0F, 1.0F);
      super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
   }

   public void update() {
      this.baseSpellTextureIndex = 144;
   }

   protected EntitySpellParticleFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double d, double d2, double d3) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.5 - RANDOM.nextDouble(), d2, 0.5 - RANDOM.nextDouble());
      this.motionY *= 0.2F;
      if (d == 0.0 && d3 == 0.0) {
         this.motionX *= 0.1F;
         this.motionZ *= 0.1F;
      }

      this.particleScale *= 0.75F;
      this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
      this.noClip = false;
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setDead();
      }

      this.setParticleTextureIndex(this.baseSpellTextureIndex + (7 - this.particleAge * 8 / this.particleMaxAge));
      this.motionY += 0.004;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      if (this.posY == this.prevPosY) {
         this.motionX *= 1.1;
         this.motionZ *= 1.1;
      }

      this.motionX *= 0.96F;
      this.motionY *= 0.96F;
      this.motionZ *= 0.96F;
      if (this.onGround) {
         this.motionX *= 0.7F;
         this.motionZ *= 0.7F;
      }
   }

   public static class AmbientMobFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         EntitySpellParticleFX entitySpellParticleFX = new EntitySpellParticleFX(world2, d, d2, d3, d4, d5, d6);
         entitySpellParticleFX.setAlphaF(0.15F);
         entitySpellParticleFX.setRBGColorF((float)d4, (float)d5, (float)d6);
         return entitySpellParticleFX;
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         return new EntitySpellParticleFX(world2, d, d2, d3, d4, d5, d6);
      }
   }

   public static class InstantFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         EntitySpellParticleFX entitySpellParticleFX = new EntitySpellParticleFX(world2, d, d2, d3, d4, d5, d6);
         entitySpellParticleFX.update();
         return entitySpellParticleFX;
      }
   }

   public static class MobFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         EntitySpellParticleFX entitySpellParticleFX = new EntitySpellParticleFX(world2, d, d2, d3, d4, d5, d6);
         entitySpellParticleFX.setRBGColorF((float)d4, (float)d5, (float)d6);
         return entitySpellParticleFX;
      }
   }

   public static class WitchFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         EntitySpellParticleFX entitySpellParticleFX = new EntitySpellParticleFX(world2, d, d2, d3, d4, d5, d6);
         entitySpellParticleFX.update();
         float f = world2.rand.nextFloat() * 0.5F + 0.35F;
         entitySpellParticleFX.setRBGColorF(1.0F * f, 0.0F * f, 1.0F * f);
         return entitySpellParticleFX;
      }
   }
}
