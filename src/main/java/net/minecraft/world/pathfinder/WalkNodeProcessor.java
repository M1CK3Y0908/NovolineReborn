package net.minecraft.world.pathfinder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class WalkNodeProcessor extends NodeProcessor {
   private boolean avoidsWater;
   private boolean canEnterDoors;
   private boolean shouldAvoidWater;
   private boolean canSwim;
   private boolean canBreakDoors;

   @Override
   public void initProcessor(IBlockAccess iblockaccessIn, Entity entityIn) {
      super.initProcessor(iblockaccessIn, entityIn);
      this.shouldAvoidWater = this.avoidsWater;
   }

   public void update() {
      this.canSwim = true;
   }

   @Override
   public PathPoint getPathPointTo(Entity entityIn) {
      int i;
      if (this.canSwim && entityIn.isInWater()) {
         i = (int)entityIn.getEntityBoundingBox().minY;
         net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos(MathHelper.floor_double(entityIn.posX), i, MathHelper.floor_double(entityIn.posZ));

         for (Block block2 = this.blockaccess.getBlockState(mutableBlockPos).getBlock();
            block2 == Blocks.flowing_water || block2 == Blocks.water;
            block2 = this.blockaccess.getBlockState(mutableBlockPos).getBlock()
         ) {
            mutableBlockPos.set(MathHelper.floor_double(entityIn.posX), ++i, MathHelper.floor_double(entityIn.posZ));
         }

         this.avoidsWater = false;
      } else {
         i = MathHelper.floor_double(entityIn.getEntityBoundingBox().minY + 0.5);
      }

      return this.openPoint(MathHelper.floor_double(entityIn.getEntityBoundingBox().minX), i, MathHelper.floor_double(entityIn.getEntityBoundingBox().minZ));
   }

   public void setEnterDoors(boolean canEnterDoorsIn) {
      this.canEnterDoors = canEnterDoorsIn;
   }

   public void setAvoidsWater(boolean avoidsWaterIn) {
      this.avoidsWater = avoidsWaterIn;
   }

   public void setBreakDoors(boolean canBreakDoorsIn) {
      this.canBreakDoors = canBreakDoorsIn;
   }

   @Override
   public void postProcess() {
      super.postProcess();
      this.avoidsWater = this.shouldAvoidWater;
   }

   @Override
   public int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
      int i = 0;
      int i2 = 0;
      if (this.getVerticalOffset(entityIn, currentPoint.xCoord, currentPoint.yCoord + 1, currentPoint.zCoord) == 1) {
         i2 = 1;
      }

      PathPoint pathPoint = this.getSafePoint(entityIn, currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord + 1, i2);
      PathPoint pathPoint2 = this.getSafePoint(entityIn, currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord, i2);
      PathPoint pathPoint3 = this.getSafePoint(entityIn, currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord, i2);
      PathPoint pathPoint4 = this.getSafePoint(entityIn, currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord - 1, i2);
      if (pathPoint != null && !pathPoint.visited && pathPoint.distanceTo(targetPoint) < maxDistance) {
         i++;
         pathOptions[0] = pathPoint;
      }

      if (pathPoint2 != null && !pathPoint2.visited && pathPoint2.distanceTo(targetPoint) < maxDistance) {
         pathOptions[i++] = pathPoint2;
      }

      if (pathPoint3 != null && !pathPoint3.visited && pathPoint3.distanceTo(targetPoint) < maxDistance) {
         pathOptions[i++] = pathPoint3;
      }

      if (pathPoint4 != null && !pathPoint4.visited && pathPoint4.distanceTo(targetPoint) < maxDistance) {
         pathOptions[i++] = pathPoint4;
      }

      return i;
   }

   public static int getPathableStatus(
      IBlockAccess blockaccessIn,
      Entity entityIn,
      int x,
      int y,
      int z,
      int sizeX,
      int sizeY,
      int sizeZ,
      boolean avoidWater,
      boolean breakDoors,
      boolean enterDoors
   ) {
      int index = 0;
      BlockPos blockPos = new BlockPos(entityIn);
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int x2 = x; x2 < x + sizeX; x2++) {
         for (int y2 = y; y2 < y + sizeY; y2++) {
            for (int z2 = z; z2 < z + sizeZ; z2++) {
               mutableBlockPos.set(x2, y2, z2);
               Block block2 = blockaccessIn.getBlockState(mutableBlockPos).getBlock();
               if (block2.getMaterial() != Material.air) {
                  if (block2 == Blocks.trapdoor || block2 == Blocks.iron_trapdoor) {
                     index = 1;
                  } else if (block2 != Blocks.flowing_water && block2 != Blocks.water) {
                     if (!enterDoors && block2 instanceof BlockDoor && block2.getMaterial() == Material.wood) {
                        return 0;
                     }
                  } else {
                     if (avoidWater) {
                        return -1;
                     }

                     index = 1;
                  }

                  if (entityIn.worldObj.getBlockState(mutableBlockPos).getBlock() instanceof BlockRailBase) {
                     if (!(entityIn.worldObj.getBlockState(blockPos).getBlock() instanceof BlockRailBase)
                        && !(entityIn.worldObj.getBlockState(blockPos.down()).getBlock() instanceof BlockRailBase)) {
                        return -3;
                     }
                  } else if (!block2.isPassable(blockaccessIn, mutableBlockPos) && (!breakDoors || !(block2 instanceof BlockDoor) || block2.getMaterial() != Material.wood)) {
                     if (block2 instanceof BlockFence || block2 instanceof BlockFenceGate || block2 instanceof BlockWall) {
                        return -3;
                     }

                     if (block2 == Blocks.trapdoor || block2 == Blocks.iron_trapdoor) {
                        return -4;
                     }

                     if (block2.getMaterial() != Material.lava) {
                        return 0;
                     }

                     if (!entityIn.isInLava()) {
                        return -2;
                     }
                  }
               }
            }
         }
      }

      return index != 0 ? 2 : 1;
   }

   @Override
   public PathPoint getPathPointToCoords(Entity entityIn, double x, double y, double target) {
      return this.openPoint(
         MathHelper.floor_double(x - entityIn.width / 2.0F), MathHelper.floor_double(y), MathHelper.floor_double(target - entityIn.width / 2.0F)
      );
   }

   private PathPoint getSafePoint(Entity entityIn, int x, int y, int z, int upwardOffset) {
      PathPoint pathPoint = null;
      int verticalOffset = this.getVerticalOffset(entityIn, x, y, z);
      if (verticalOffset == 2) {
         return this.openPoint(x, y, z);
      }

      if (verticalOffset == 1) {
         pathPoint = this.openPoint(x, y, z);
      }

      if (pathPoint == null && upwardOffset > 0 && verticalOffset != -3 && verticalOffset != -4 && this.getVerticalOffset(entityIn, x, y + upwardOffset, z) == 1) {
         pathPoint = this.openPoint(x, y + upwardOffset, z);
         y += upwardOffset;
      }

      if (pathPoint != null) {
         int i = 0;
         int i2 = 0;

         while (y > 0) {
            i2 = this.getVerticalOffset(entityIn, x, y - 1, z);
            if (this.avoidsWater && i2 == -1) {
               return null;
            }

            if (i2 != 1) {
               break;
            }

            if (i++ >= entityIn.getMaxFallHeight()) {
               return null;
            }

            if (--y <= 0) {
               return null;
            }

            pathPoint = this.openPoint(x, y, z);
         }

         if (i2 == -2) {
            return null;
         }
      }

      return pathPoint;
   }

   public boolean getCanSwim() {
      return this.canSwim;
   }

   private int getVerticalOffset(Entity entityIn, int x, int y, int z) {
      return getPathableStatus(
         this.blockaccess, entityIn, x, y, z, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.avoidsWater, this.canBreakDoors, this.canEnterDoors
      );
   }

   public boolean getEnterDoors() {
      return this.canEnterDoors;
   }

   public boolean getAvoidsWater() {
      return this.avoidsWater;
   }
}
