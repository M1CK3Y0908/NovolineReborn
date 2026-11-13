package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.Vec3;

public class EntityAIWander extends EntityAIBase {
   private double yPosition;
   private EntityCreature entity;
   private double xPosition;
   private double speed;
   private double zPosition;
   private boolean mustUpdate;
   private int executionChance;

   public EntityAIWander(EntityCreature creatureIn, double speedIn, int chance) {
      this.entity = creatureIn;
      this.speed = speedIn;
      this.executionChance = chance;
      this.setMutexBits(1);
   }

   @Override
   public boolean continueExecuting() {
      return !this.entity.getNavigator().noPath();
   }

   @Override
   public void startExecuting() {
      this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
   }

   public void update() {
      this.executionChance = 400;
   }

   public EntityAIWander(EntityCreature creatureIn, double speedIn) {
      this(creatureIn, speedIn, 120);
   }

   public void makeUpdate() {
      this.mustUpdate = true;
   }

   @Override
   public boolean shouldExecute() {
      if (!this.mustUpdate) {
         if (this.entity.getAge() >= 100) {
            return false;
         }

         if (this.entity.getRNG().nextInt(this.executionChance) != 0) {
            return false;
         }
      }

      Vec3 vec3 = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);
      if (vec3 == null) {
         return false;
      }

      this.xPosition = vec3.xCoord;
      this.yPosition = vec3.yCoord;
      this.zPosition = vec3.zCoord;
      this.mustUpdate = false;
      return true;
   }
}
