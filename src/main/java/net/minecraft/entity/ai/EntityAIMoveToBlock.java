package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class EntityAIMoveToBlock extends EntityAIBase {
   private int maxStayTicks;
   private boolean isAboveDestination;
   protected BlockPos destinationBlock = BlockPos.ORIGIN;
   private final EntityCreature theEntity;
   protected int runDelay;
   private int timeoutCounter;
   private final double movementSpeed;
   private int searchLength;

   @Override
   public void startExecuting() {
      this.theEntity
         .getNavigator()
         .tryMoveToXYZ((double)((float)this.destinationBlock.getX()) + 0.5, this.destinationBlock.getY() + 1, (double)((float)this.destinationBlock.getZ()) + 0.5, this.movementSpeed);
      this.timeoutCounter = 0;
      this.maxStayTicks = this.theEntity.getRNG().nextInt(this.theEntity.getRNG().nextInt(1200) + 1200) + 1200;
   }

   @Override
   public void updateTask() {
      if (this.theEntity.getDistanceSqToCenter(this.destinationBlock.up()) > 1.0) {
         this.isAboveDestination = false;
         this.timeoutCounter++;
         if (this.timeoutCounter % 40 == 0) {
            this.theEntity
               .getNavigator()
               .tryMoveToXYZ((double)((float)this.destinationBlock.getX()) + 0.5, this.destinationBlock.getY() + 1, (double)((float)this.destinationBlock.getZ()) + 0.5, this.movementSpeed);
         }
      } else {
         this.isAboveDestination = true;
         this.timeoutCounter--;
      }
   }

   @Override
   public boolean shouldExecute() {
      if (this.runDelay > 0) {
         this.runDelay--;
         return false;
      } else {
         this.runDelay = 200 + this.theEntity.getRNG().nextInt(200);
         return this.searchForDestination();
      }
   }

   public EntityAIMoveToBlock(EntityCreature creature, double speedIn, int length) {
      this.theEntity = creature;
      this.movementSpeed = speedIn;
      this.searchLength = length;
      this.setMutexBits(5);
   }

   @Override
   public boolean continueExecuting() {
      return this.timeoutCounter >= -this.maxStayTicks && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.theEntity.worldObj, this.destinationBlock);
   }

   protected abstract boolean shouldMoveTo(World world2, BlockPos blockPos);

   @Override
   public void resetTask() {
   }

   protected boolean getIsAboveDestination() {
      return this.isAboveDestination;
   }

   private boolean searchForDestination() {
      int searchLength2 = this.searchLength;
      BlockPos blockPos = new BlockPos(this.theEntity);

      for (int i = 0; i <= 1; i = i > 0 ? -i : 1 - i) {
         for (int i2 = 0; i2 < searchLength2; i2++) {
            for (int i3 = 0; i3 <= i2; i3 = i3 > 0 ? -i3 : 1 - i3) {
               for (int i4 = i3 < i2 && i3 > -i2 ? i2 : 0; i4 <= i2; i4 = i4 > 0 ? -i4 : 1 - i4) {
                  BlockPos blockPos2 = blockPos.add(i3, i - 1, i4);
                  if (this.theEntity.isWithinHomeDistanceFromPosition(blockPos2) && this.shouldMoveTo(this.theEntity.worldObj, blockPos2)) {
                     this.destinationBlock = blockPos2;
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }
}
