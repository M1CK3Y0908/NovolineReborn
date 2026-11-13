package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityParticleEmitter extends EntityFX {
   private int age;
   private Entity attachedEntity;
   private int lifetime;
   private EnumParticleTypes particleTypes;

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
   }

   @Override
   public int getFXLayer() {
      return 3;
   }

   public EntityParticleEmitter(World worldIn, Entity entity2, EnumParticleTypes particleTypesIn) {
      super(worldIn, entity2.posX, entity2.getEntityBoundingBox().minY + entity2.height / 2.0F, entity2.posZ, entity2.motionX, entity2.motionY, entity2.motionZ);
      this.attachedEntity = entity2;
      this.lifetime = 3;
      this.particleTypes = particleTypesIn;
      this.onUpdate();
   }

   @Override
   public void onUpdate() {
      for (int index = 0; index < 16; index++) {
         double d = this.rand.nextFloat() * 2.0F - 1.0F;
         double d2 = this.rand.nextFloat() * 2.0F - 1.0F;
         double d3 = this.rand.nextFloat() * 2.0F - 1.0F;
         if (d * d + d2 * d2 + d3 * d3 <= 1.0) {
            double xCoordinate = this.attachedEntity.posX + d * this.attachedEntity.width / 4.0;
            double d4 = this.attachedEntity.getEntityBoundingBox().minY
               + this.attachedEntity.height / 2.0F
               + d2 * this.attachedEntity.height / 4.0;
            double zCoordinate = this.attachedEntity.posZ + d3 * this.attachedEntity.width / 4.0;
            this.worldObj.spawnParticle(this.particleTypes, false, xCoordinate, d4, zCoordinate, d, d2 + 0.2, d3);
         }
      }

      this.age++;
      if (this.age >= this.lifetime) {
         this.setDead();
      }
   }
}
