package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class EntityAILeapAtTarget extends EntityAIBase {
   EntityLivingBase leapTarget;
   EntityLiving leaper;
   float leapMotionY;

   @Override
   public boolean continueExecuting() {
      return !this.leaper.onGround;
   }

   public EntityAILeapAtTarget(EntityLiving leapingEntity, float leapMotionYIn) {
      this.leaper = leapingEntity;
      this.leapMotionY = leapMotionYIn;
      this.setMutexBits(5);
   }

   @Override
   public void startExecuting() {
      double xCoordinate = this.leapTarget.posX - this.leaper.posX;
      double zCoordinate = this.leapTarget.posZ - this.leaper.posZ;
      float f = MathHelper.sqrt_double(xCoordinate * xCoordinate + zCoordinate * zCoordinate);
      this.leaper.motionX = this.leaper.motionX + (xCoordinate / f * 0.5 * 0.8F + this.leaper.motionX * 0.2F);
      this.leaper.motionZ = this.leaper.motionZ + (zCoordinate / f * 0.5 * 0.8F + this.leaper.motionZ * 0.2F);
      this.leaper.motionY = this.leapMotionY;
   }

   @Override
   public boolean shouldExecute() {
      this.leapTarget = this.leaper.getAttackTarget();
      if (this.leapTarget == null) {
         return false;
      }

      double distanceSqToEntity = this.leaper.getDistanceSqToEntity(this.leapTarget);
      return !(distanceSqToEntity >= 4.0) || !(distanceSqToEntity <= 16.0) ? false : (!this.leaper.onGround ? false : this.leaper.getRNG().nextInt(5) == 0);
   }
}
