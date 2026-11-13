package net.minecraft.world.pathfinder;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class SwimNodeProcessor extends NodeProcessor {
   private PathPoint getSafePoint(Entity entityIn, int x, int y, int z) {
      return this.checkWaterBlock(entityIn, x, y, z) == -1 ? this.openPoint(x, y, z) : null;
   }

   @Override
   public void postProcess() {
      super.postProcess();
   }

   @Override
   public PathPoint getPathPointTo(Entity entityIn) {
      return this.openPoint(
         MathHelper.floor_double(entityIn.getEntityBoundingBox().minX),
         MathHelper.floor_double(entityIn.getEntityBoundingBox().minY + 0.5),
         MathHelper.floor_double(entityIn.getEntityBoundingBox().minZ)
      );
   }

   private int checkWaterBlock(Entity entityIn, int x, int y, int z) {
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int x2 = x; x2 < x + this.entitySizeX; x2++) {
         for (int y2 = y; y2 < y + this.entitySizeY; y2++) {
            for (int z2 = z; z2 < z + this.entitySizeZ; z2++) {
               if (this.blockaccess.getBlockState(mutableBlockPos.set(x2, y2, z2)).getBlock().getMaterial() != Material.water) {
                  return 0;
               }
            }
         }
      }

      return -1;
   }

   @Override
   public int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
      int i = 0;

      for (EnumFacing enumFacing : EnumFacing.values()) {
         PathPoint safePoint = this.getSafePoint(
            entityIn,
            currentPoint.xCoord + enumFacing.getFrontOffsetX(),
            currentPoint.yCoord + enumFacing.getFrontOffsetY(),
            currentPoint.zCoord + enumFacing.getFrontOffsetZ()
         );
         if (safePoint != null && !safePoint.visited && safePoint.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = safePoint;
         }
      }

      return i;
   }

   @Override
   public PathPoint getPathPointToCoords(Entity entityIn, double x, double y, double target) {
      return this.openPoint(
         MathHelper.floor_double(x - entityIn.width / 2.0F), MathHelper.floor_double(y + 0.5), MathHelper.floor_double(target - entityIn.width / 2.0F)
      );
   }

   @Override
   public void initProcessor(IBlockAccess iblockaccessIn, Entity entityIn) {
      super.initProcessor(iblockaccessIn, entityIn);
   }
}
