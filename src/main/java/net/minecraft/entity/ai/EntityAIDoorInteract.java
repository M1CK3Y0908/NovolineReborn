package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;

public abstract class EntityAIDoorInteract extends EntityAIBase {
   protected BlockPos doorPosition = BlockPos.ORIGIN;
   protected BlockDoor doorBlock;
   float entityPositionX;
   protected EntityLiving theEntity;
   boolean hasStoppedDoorInteraction;
   float entityPositionZ;

   public EntityAIDoorInteract(EntityLiving entityIn) {
      this.theEntity = entityIn;
      if (!(entityIn.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
      }
   }

   @Override
   public boolean continueExecuting() {
      return !this.hasStoppedDoorInteraction;
   }

   private BlockDoor getBlockDoor(BlockPos pos) {
      Block block = this.theEntity.worldObj.getBlockState(pos).getBlock();
      return block instanceof BlockDoor && block.getMaterial() == Material.wood ? (BlockDoor)block : null;
   }

   @Override
   public void updateTask() {
      float f = (float)(this.doorPosition.getX() + 0.5F - this.theEntity.posX);
      float f2 = (float)(this.doorPosition.getZ() + 0.5F - this.theEntity.posZ);
      float f3 = this.entityPositionX * f + this.entityPositionZ * f2;
      if (f3 < 0.0F) {
         this.hasStoppedDoorInteraction = true;
      }
   }

   @Override
   public boolean shouldExecute() {
      if (!this.theEntity.isCollidedHorizontally) {
         return false;
      }

      PathNavigateGround pathNavigateGround = (PathNavigateGround)this.theEntity.getNavigator();
      PathEntity pathEntity = pathNavigateGround.getPath();
      if (pathEntity != null && !pathEntity.isFinished() && pathNavigateGround.getEnterDoors()) {
         for (int index = 0; index < Math.min(pathEntity.getCurrentPathIndex() + 2, pathEntity.getCurrentPathLength()); index++) {
            PathPoint pathPoint = pathEntity.getPathPointFromIndex(index);
            this.doorPosition = new BlockPos(pathPoint.xCoord, pathPoint.yCoord + 1, pathPoint.zCoord);
            if (this.theEntity.getDistanceSq(this.doorPosition.getX(), this.theEntity.posY, this.doorPosition.getZ()) <= 2.25) {
               this.doorBlock = this.getBlockDoor(this.doorPosition);
               if (this.doorBlock != null) {
                  return true;
               }
            }
         }

         this.doorPosition = new BlockPos(this.theEntity).up();
         this.doorBlock = this.getBlockDoor(this.doorPosition);
         return this.doorBlock != null;
      } else {
         return false;
      }
   }

   @Override
   public void startExecuting() {
      this.hasStoppedDoorInteraction = false;
      this.entityPositionX = (float)(this.doorPosition.getX() + 0.5F - this.theEntity.posX);
      this.entityPositionZ = (float)(this.doorPosition.getZ() + 0.5F - this.theEntity.posZ);
   }
}
