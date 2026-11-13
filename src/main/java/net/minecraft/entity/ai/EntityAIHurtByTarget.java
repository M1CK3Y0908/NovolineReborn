package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;

public class EntityAIHurtByTarget extends EntityAITarget {
   private boolean entityCallsForHelp;
   private final Class[] targetClasses;
   private int revengeTimerOld;

   public EntityAIHurtByTarget(EntityCreature creatureIn, boolean entityCallsForHelpIn, Class... targetClassesIn) {
      super(creatureIn, false);
      this.entityCallsForHelp = entityCallsForHelpIn;
      this.targetClasses = targetClassesIn;
      this.setMutexBits(1);
   }

   @Override
   public boolean shouldExecute() {
      return this.taskOwner.getRevengeTimer() != this.revengeTimerOld && this.checkEntityLivingBase(this.taskOwner.getAITarget());
   }

   protected void setEntityAttackTarget(EntityCreature creatureIn, EntityLivingBase entityLivingBaseIn) {
      creatureIn.setAttackTarget(entityLivingBaseIn);
   }

   @Override
   public void startExecuting() {
      this.taskOwner.setAttackTarget(this.taskOwner.getAITarget());
      this.revengeTimerOld = this.taskOwner.getRevengeTimer();
      if (this.entityCallsForHelp) {
         double targetDistance = this.getTargetDistance();

         for (EntityCreature entityCreature : this.taskOwner
            .worldObj
            .getEntitiesWithinAABB(
               this.taskOwner.getClass(),
               new AxisAlignedBB(
                     this.taskOwner.posX,
                     this.taskOwner.posY,
                     this.taskOwner.posZ,
                     this.taskOwner.posX + 1.0,
                     this.taskOwner.posY + 1.0,
                     this.taskOwner.posZ + 1.0
                  )
                  .expand(targetDistance, 10.0, targetDistance)
            )) {
            if (this.taskOwner != entityCreature && entityCreature.getAttackTarget() == null && !entityCreature.isOnSameTeam(this.taskOwner.getAITarget())) {
               boolean flag = false;

               for (Class clazz : this.targetClasses) {
                  if (entityCreature.getClass() == clazz) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  this.setEntityAttackTarget(entityCreature, this.taskOwner.getAITarget());
               }
            }
         }
      }

      super.startExecuting();
   }
}
