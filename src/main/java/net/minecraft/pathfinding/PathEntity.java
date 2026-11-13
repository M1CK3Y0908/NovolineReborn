package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class PathEntity {
   private final PathPoint[] points;
   private int pathLength;
   private int currentPathIndex;

   public boolean isDestinationSame(Vec3 vec) {
      PathPoint pathPoint = this.getFinalPathPoint();
      return pathPoint == null ? false : pathPoint.xCoord == (int)vec.xCoord && pathPoint.zCoord == (int)vec.zCoord;
   }

   public int getCurrentPathLength() {
      return this.pathLength;
   }

   public int getCurrentPathIndex() {
      return this.currentPathIndex;
   }

   public PathEntity(PathPoint[] pathpoints) {
      this.points = pathpoints;
      this.pathLength = pathpoints.length;
   }

   public Vec3 getPosition(Entity entityIn) {
      return this.getVectorFromIndex(entityIn, this.currentPathIndex);
   }

   public void incrementPathIndex() {
      this.currentPathIndex++;
   }

   public PathPoint getFinalPathPoint() {
      return this.pathLength > 0 ? this.points[this.pathLength - 1] : null;
   }

   public PathPoint getPathPointFromIndex(int index) {
      return this.points[index];
   }

   public void setCurrentPathLength(int length) {
      this.pathLength = length;
   }

   public void setCurrentPathIndex(int currentPathIndexIn) {
      this.currentPathIndex = currentPathIndexIn;
   }

   public Vec3 getVectorFromIndex(Entity entityIn, int index) {
      double d = this.points[index].xCoord + (int)(entityIn.width + 1.0F) * 0.5;
      double yCoord2 = this.points[index].yCoord;
      double d2 = this.points[index].zCoord + (int)(entityIn.width + 1.0F) * 0.5;
      return new Vec3(d, yCoord2, d2);
   }

   public boolean isSamePath(PathEntity pathentityIn) {
      if (pathentityIn == null) {
         return false;
      }

      if (pathentityIn.points.length != this.points.length) {
         return false;
      }

      for (int index = 0; index < this.points.length; index++) {
         if (this.points[index].xCoord != pathentityIn.points[index].xCoord
            || this.points[index].yCoord != pathentityIn.points[index].yCoord
            || this.points[index].zCoord != pathentityIn.points[index].zCoord) {
            return false;
         }
      }

      return true;
   }

   public boolean isFinished() {
      return this.currentPathIndex >= this.pathLength;
   }
}
