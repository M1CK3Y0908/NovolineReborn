package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.EnumDifficulty;

public class EntityAIBreakDoor extends EntityAIDoorInteract {
   private int previousBreakProgress = -1;
   private int breakingTime;

   @Override
   public void updateTask() {
      super.updateTask();
      if (this.theEntity.getRNG().nextInt(20) == 0) {
         this.theEntity.worldObj.playAuxSFX(1010, this.doorPosition, 0);
      }

      this.breakingTime++;
      int i = (int)(this.breakingTime / 240.0F * 10.0F);
      if (i != this.previousBreakProgress) {
         this.theEntity.worldObj.sendBlockBreakProgress(this.theEntity.getEntityId(), this.doorPosition, i);
         this.previousBreakProgress = i;
      }

      if (this.breakingTime == 240 && this.theEntity.worldObj.getDifficulty() == EnumDifficulty.HARD) {
         this.theEntity.worldObj.setBlockToAir(this.doorPosition);
         this.theEntity.worldObj.playAuxSFX(1012, this.doorPosition, 0);
         this.theEntity.worldObj.playAuxSFX(2001, this.doorPosition, Block.getIdFromBlock(this.doorBlock));
      }
   }

   @Override
   public void resetTask() {
      super.resetTask();
      this.theEntity.worldObj.sendBlockBreakProgress(this.theEntity.getEntityId(), this.doorPosition, -1);
   }

   @Override
   public boolean shouldExecute() {
      if (!super.shouldExecute()) {
         return false;
      } else {
         return !this.theEntity.worldObj.getGameRules().getBoolean("mobGriefing") ? false : !BlockDoor.isOpen(this.theEntity.worldObj, this.doorPosition);
      }
   }

   public EntityAIBreakDoor(EntityLiving entityIn) {
      super(entityIn);
   }

   @Override
   public void startExecuting() {
      super.startExecuting();
      this.breakingTime = 0;
   }

   @Override
   public boolean continueExecuting() {
      double distanceSq = this.theEntity.getDistanceSq(this.doorPosition);
      return this.breakingTime <= 240 && !BlockDoor.isOpen(this.theEntity.worldObj, this.doorPosition) && distanceSq < 4.0;
   }
}
