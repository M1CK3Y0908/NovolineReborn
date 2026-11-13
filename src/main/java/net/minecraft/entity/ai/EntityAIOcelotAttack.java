package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityAIOcelotAttack extends EntityAIBase {
   EntityLiving theEntity;
   World theWorld;
   EntityLivingBase theVictim;
   int attackCountdown;

   @Override
   public boolean continueExecuting() {
      return !this.theVictim.isEntityAlive()
         ? false
         : (this.theEntity.getDistanceSqToEntity(this.theVictim) > 225.0 ? false : !this.theEntity.getNavigator().noPath() || this.shouldExecute());
   }

   public EntityAIOcelotAttack(EntityLiving theEntityIn) {
      this.theEntity = theEntityIn;
      this.theWorld = theEntityIn.worldObj;
      this.setMutexBits(3);
   }

   @Override
   public void resetTask() {
      this.theVictim = null;
      this.theEntity.getNavigator().clearPathEntity();
   }

   @Override
   public boolean shouldExecute() {
      EntityLivingBase entityLivingBase = this.theEntity.getAttackTarget();
      if (entityLivingBase == null) {
         return false;
      }

      this.theVictim = entityLivingBase;
      return true;
   }

   @Override
   public void updateTask() {
      this.theEntity.getLookHelper().setLookPositionWithEntity(this.theVictim, 30.0F, 30.0F);
      double d = this.theEntity.width * 2.0F * this.theEntity.width * 2.0F;
      double xCoordinate = this.theEntity.getDistanceSq(this.theVictim.posX, this.theVictim.getEntityBoundingBox().minY, this.theVictim.posZ);
      double d2 = 0.8;
      if (xCoordinate > d && xCoordinate < 16.0) {
         d2 = 1.33;
      } else if (xCoordinate < 225.0) {
         d2 = 0.6;
      }

      this.theEntity.getNavigator().tryMoveToEntityLiving(this.theVictim, d2);
      this.attackCountdown = Math.max(this.attackCountdown - 1, 0);
      if (xCoordinate <= d && this.attackCountdown <= 0) {
         this.attackCountdown = 20;
         this.theEntity.attackEntityAsMob(this.theVictim);
      }
   }
}
