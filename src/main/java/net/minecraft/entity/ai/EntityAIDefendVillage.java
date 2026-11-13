package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.village.Village;

public class EntityAIDefendVillage extends EntityAITarget {
   EntityLivingBase villageAgressorTarget;
   EntityIronGolem irongolem;

   @Override
   public boolean shouldExecute() {
      Village village = this.irongolem.getVillage();
      if (village == null) {
         return false;
      }

      this.villageAgressorTarget = village.findNearestVillageAggressor(this.irongolem);
      if (this.villageAgressorTarget instanceof EntityCreeper) {
         return false;
      }

      if (!this.checkEntityLivingBase(this.villageAgressorTarget)) {
         if (this.taskOwner.getRNG().nextInt(20) == 0) {
            this.villageAgressorTarget = village.getNearestTargetPlayer(this.irongolem);
            return this.checkEntityLivingBase(this.villageAgressorTarget);
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public EntityAIDefendVillage(EntityIronGolem ironGolemIn) {
      super(ironGolemIn, false, true);
      this.irongolem = ironGolemIn;
      this.setMutexBits(1);
   }

   @Override
   public void startExecuting() {
      this.irongolem.setAttackTarget(this.villageAgressorTarget);
      super.startExecuting();
   }
}
