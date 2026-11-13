package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAIRestrictSun extends EntityAIBase {
   private EntityCreature theEntity;

   @Override
   public void resetTask() {
      ((PathNavigateGround)this.theEntity.getNavigator()).setAvoidSun(false);
   }

   public EntityAIRestrictSun(EntityCreature creature) {
      this.theEntity = creature;
   }

   @Override
   public void startExecuting() {
      ((PathNavigateGround)this.theEntity.getNavigator()).setAvoidSun(true);
   }

   @Override
   public boolean shouldExecute() {
      return this.theEntity.worldObj.isDaytime();
   }
}
