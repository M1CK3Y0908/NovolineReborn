package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.pathfinder.NodeProcessor;

public class PathFinder {
   private Path path = new Path();
   private PathPoint[] pathOptions = new PathPoint[32];
   private NodeProcessor nodeProcessor;

   public PathEntity createEntityPathTo(IBlockAccess blockAccess, Entity entityFrom, Entity entityTo, float dist) {
      return this.createEntityPathTo(blockAccess, entityFrom, entityTo.posX, entityTo.getEntityBoundingBox().minY, entityTo.posZ, dist);
   }

   public PathEntity createEntityPathTo(IBlockAccess blockAccess, Entity entityIn, BlockPos targetPos, float dist) {
      return this.createEntityPathTo(blockAccess, entityIn, targetPos.getX() + 0.5F, targetPos.getY() + 0.5F, targetPos.getZ() + 0.5F, dist);
   }

   private PathEntity createEntityPathTo(IBlockAccess blockAccess, Entity entityIn, double x, double y, double z, float distance) {
      this.path.clearPath();
      this.nodeProcessor.initProcessor(blockAccess, entityIn);
      PathPoint pathPoint = this.nodeProcessor.getPathPointTo(entityIn);
      PathPoint pathPoint2 = this.nodeProcessor.getPathPointToCoords(entityIn, x, y, z);
      PathEntity pathEntity = this.addToPath(entityIn, pathPoint, pathPoint2, distance);
      this.nodeProcessor.postProcess();
      return pathEntity;
   }

   private PathEntity createEntityPath(PathPoint start, PathPoint end) {
      int i = 1;

      for (PathPoint pathPoint = end; pathPoint.previous != null; pathPoint = pathPoint.previous) {
         i++;
      }

      PathPoint[] apathpoint = new PathPoint[i];
      PathPoint pathPoint2 = end;

      for (apathpoint[--i] = end; pathPoint2.previous != null; apathpoint[--i] = pathPoint2) {
         pathPoint2 = pathPoint2.previous;
      }

      return new PathEntity(apathpoint);
   }

   private PathEntity addToPath(Entity entityIn, PathPoint pathpointStart, PathPoint pathpointEnd, float maxDistance) {
      pathpointStart.totalPathDistance = 0.0F;
      pathpointStart.distanceToNext = pathpointStart.distanceToSquared(pathpointEnd);
      pathpointStart.distanceToTarget = pathpointStart.distanceToNext;
      this.path.clearPath();
      this.path.addPoint(pathpointStart);
      PathPoint pathpointStart2 = pathpointStart;

      while (!this.path.isPathEmpty()) {
         PathPoint pathPoint2 = this.path.dequeue();
         if (pathPoint2.equals(pathpointEnd)) {
            return this.createEntityPath(pathpointStart, pathpointEnd);
         }

         if (pathPoint2.distanceToSquared(pathpointEnd) < pathpointStart2.distanceToSquared(pathpointEnd)) {
            pathpointStart2 = pathPoint2;
         }

         pathPoint2.visited = true;
         int pathOptions2 = this.nodeProcessor.findPathOptions(this.pathOptions, entityIn, pathPoint2, pathpointEnd, maxDistance);

         for (int innerIndex = 0; innerIndex < pathOptions2; innerIndex++) {
            PathPoint pathPoint3 = this.pathOptions[innerIndex];
            float f = pathPoint2.totalPathDistance + pathPoint2.distanceToSquared(pathPoint3);
            if (f < maxDistance * 2.0F && (!pathPoint3.isAssigned() || f < pathPoint3.totalPathDistance)) {
               pathPoint3.previous = pathPoint2;
               pathPoint3.totalPathDistance = f;
               pathPoint3.distanceToNext = pathPoint3.distanceToSquared(pathpointEnd);
               if (pathPoint3.isAssigned()) {
                  this.path.changeDistance(pathPoint3, pathPoint3.totalPathDistance + pathPoint3.distanceToNext);
               } else {
                  pathPoint3.distanceToTarget = pathPoint3.totalPathDistance + pathPoint3.distanceToNext;
                  this.path.addPoint(pathPoint3);
               }
            }
         }
      }

      return pathpointStart2 == pathpointStart ? null : this.createEntityPath(pathpointStart, pathpointStart2);
   }

   public PathFinder(NodeProcessor nodeProcessorIn) {
      this.nodeProcessor = nodeProcessorIn;
   }
}
