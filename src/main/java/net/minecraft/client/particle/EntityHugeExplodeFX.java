package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityHugeExplodeFX extends EntityFX {
   private int timeSinceStart;
   private int maximumTime = 8;

   @Override
   public void onUpdate() {
      for (int index = 0; index < 6; index++) {
         double xCoordinate = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
         double yCoordinate = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
         double zCoordinate = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
         this.worldObj
            .spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, xCoordinate, yCoordinate, zCoordinate, (float)this.timeSinceStart / this.maximumTime, 0.0, 0.0);
      }

      this.timeSinceStart++;
      if (this.timeSinceStart == this.maximumTime) {
         this.setDead();
      }
   }

   @Override
   public int getFXLayer() {
      return 1;
   }

   protected EntityHugeExplodeFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double d, double d2, double d3) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
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
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(World world2, double d, double d2, double d3, double d4, double d5, double d6, int... iArray) {
         return new EntityHugeExplodeFX(world2, d, d2, d3, d4, d5, d6);
      }
   }
}
