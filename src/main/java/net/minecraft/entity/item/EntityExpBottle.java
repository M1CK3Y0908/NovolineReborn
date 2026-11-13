package net.minecraft.entity.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityExpBottle extends EntityThrowable {
   @Override
   protected float getGravityVelocity() {
      return 0.07F;
   }

   public EntityExpBottle(World worldIn) {
      super(worldIn);
   }

   @Override
   protected float getInaccuracy() {
      return -20.0F;
   }

   public EntityExpBottle(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   @Override
   protected void onImpact(MovingObjectPosition movingObjectPosition) {
      if (!this.worldObj.isRemote) {
         this.worldObj.playAuxSFX(2002, new BlockPos(this), 0);
         int i = 3 + this.worldObj.rand.nextInt(5) + this.worldObj.rand.nextInt(5);

         while (i > 0) {
            int xpSplit = EntityXPOrb.getXPSplit(i);
            i -= xpSplit;
            this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, xpSplit));
         }

         this.setDead();
      }
   }

   @Override
   protected float getVelocity() {
      return 0.7F;
   }

   public EntityExpBottle(World worldIn, EntityLivingBase entityLivingBase) {
      super(worldIn, entityLivingBase);
   }
}
