package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtTarget extends EntityAITarget {
   EntityTameable theEntityTameable;
   private int timestamp;
   EntityLivingBase theTarget;

   @Override
   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.theTarget);
      EntityLivingBase entityLivingBase = this.theEntityTameable.getOwner();
      if (entityLivingBase != null) {
         this.timestamp = entityLivingBase.getLastAttackerTime();
      }

      super.startExecuting();
   }

   public EntityAIOwnerHurtTarget(EntityTameable theEntityTameableIn) {
      super(theEntityTameableIn, false);
      this.theEntityTameable = theEntityTameableIn;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      if (!this.theEntityTameable.isTamed()) {
         return false;
      }

      EntityLivingBase owner = this.theEntityTameable.getOwner();
      if (owner == null) {
         return false;
      }

      this.theTarget = owner.getLastAttacker();
      return owner.getLastAttackerTime() != this.timestamp
         && this.checkEntityLivingBase(this.theTarget)
         && this.theEntityTameable.shouldAttackEntity(this.theTarget, owner);
   }
}
