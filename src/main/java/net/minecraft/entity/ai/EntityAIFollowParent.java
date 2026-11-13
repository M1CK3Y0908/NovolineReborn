package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.entity.passive.EntityAnimal;

public class EntityAIFollowParent extends EntityAIBase {
   private int delayCounter;
   EntityAnimal childAnimal;
   double moveSpeed;
   EntityAnimal parentAnimal;

   @Override
   public boolean shouldExecute() {
      if (this.childAnimal.getGrowingAge() >= 0) {
         return false;
      }

      List entitiesWithinAABB = this.childAnimal.worldObj.getEntitiesWithinAABB(this.childAnimal.getClass(), this.childAnimal.getEntityBoundingBox().expand(8.0, 4.0, 8.0));
      EntityAnimal entityAnimal = null;
      double d = Double.MAX_VALUE;

      for (EntityAnimal entityAnimal2 : (Iterable<EntityAnimal>) entitiesWithinAABB) {
         if (entityAnimal2.getGrowingAge() >= 0) {
            double distanceSqToEntity = this.childAnimal.getDistanceSqToEntity(entityAnimal2);
            if (distanceSqToEntity <= d) {
               d = distanceSqToEntity;
               entityAnimal = entityAnimal2;
            }
         }
      }

      if (entityAnimal == null) {
         return false;
      }

      if (d < 9.0) {
         return false;
      }

      this.parentAnimal = entityAnimal;
      return true;
   }

   @Override
   public void startExecuting() {
      this.delayCounter = 0;
   }

   @Override
   public boolean continueExecuting() {
      if (this.childAnimal.getGrowingAge() >= 0) {
         return false;
      }

      if (!this.parentAnimal.isEntityAlive()) {
         return false;
      }

      double distanceSqToEntity = this.childAnimal.getDistanceSqToEntity(this.parentAnimal);
      return distanceSqToEntity >= 9.0 && distanceSqToEntity <= 256.0;
   }

   @Override
   public void resetTask() {
      this.parentAnimal = null;
   }

   @Override
   public void updateTask() {
      if (--this.delayCounter <= 0) {
         this.delayCounter = 10;
         this.childAnimal.getNavigator().tryMoveToEntityLiving(this.parentAnimal, this.moveSpeed);
      }
   }

   public EntityAIFollowParent(EntityAnimal animal, double speed) {
      this.childAnimal = animal;
      this.moveSpeed = speed;
   }
}
