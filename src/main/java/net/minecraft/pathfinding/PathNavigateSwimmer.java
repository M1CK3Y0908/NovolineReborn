package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.SwimNodeProcessor;

public class PathNavigateSwimmer extends PathNavigate {
   @Override
   protected boolean isDirectPathBetweenPoints(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
      MovingObjectPosition movingObjectPosition = this.worldObj
         .rayTraceBlocks(posVec31, new Vec3(posVec32.xCoord, posVec32.yCoord + this.theEntity.height * 0.5, posVec32.zCoord), false, true, false);
      return movingObjectPosition == null || movingObjectPosition.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.MISS;
   }

   @Override
   protected boolean canNavigate() {
      return this.isInLiquid();
   }

   @Override
   protected PathFinder getPathFinder() {
      return new PathFinder(new SwimNodeProcessor());
   }

   public PathNavigateSwimmer(EntityLiving entitylivingIn, World worldIn) {
      super(entitylivingIn, worldIn);
   }

   @Override
   protected Vec3 getEntityPosition() {
      return new Vec3(this.theEntity.posX, this.theEntity.posY + this.theEntity.height * 0.5, this.theEntity.posZ);
   }

   @Override
   protected void pathFollow() {
      Vec3 entityPosition = this.getEntityPosition();
      float f = this.theEntity.width * this.theEntity.width;
      if (entityPosition.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex())) < f) {
         this.currentPath.incrementPathIndex();
      }

      for (int i = Math.min(this.currentPath.getCurrentPathIndex() + 6, this.currentPath.getCurrentPathLength() - 1);
         i > this.currentPath.getCurrentPathIndex();
         i--
      ) {
         Vec3 vectorFromIndex = this.currentPath.getVectorFromIndex(this.theEntity, i);
         if (vectorFromIndex.squareDistanceTo(entityPosition) <= 36.0 && this.isDirectPathBetweenPoints(entityPosition, vectorFromIndex, 0, 0, 0)) {
            this.currentPath.setCurrentPathIndex(i);
            break;
         }
      }

      this.checkForStuck(entityPosition);
   }

   @Override
   protected void removeSunnyPath() {
      super.removeSunnyPath();
   }
}
