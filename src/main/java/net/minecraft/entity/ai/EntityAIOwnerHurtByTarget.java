package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtByTarget extends EntityAITarget {
   EntityTameable theDefendingTameable;
   EntityLivingBase theOwnerAttacker;
   private int timestamp;

   @Override
   public boolean shouldExecute() {
      if (!this.theDefendingTameable.isTamed()) {
         return false;
      }

      EntityLivingBase owner = this.theDefendingTameable.getOwner();
      if (owner == null) {
         return false;
      }

      this.theOwnerAttacker = owner.getAITarget();
      return owner.getRevengeTimer() != this.timestamp
         && this.checkEntityLivingBase(this.theOwnerAttacker)
         && this.theDefendingTameable.shouldAttackEntity(this.theOwnerAttacker, owner);
   }

   @Override
   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.theOwnerAttacker);
      EntityLivingBase entityLivingBase = this.theDefendingTameable.getOwner();
      if (entityLivingBase != null) {
         this.timestamp = entityLivingBase.getRevengeTimer();
      }

      super.startExecuting();
   }

   public EntityAIOwnerHurtByTarget(EntityTameable theDefendingTameableIn) {
      super(theDefendingTameableIn, false);
      this.theDefendingTameable = theDefendingTameableIn;
      this.setMutexBits(1);
   }
}
