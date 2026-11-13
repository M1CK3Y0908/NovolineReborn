package net.minecraft.pathfinding;

public class Path {
   private int count;
   private PathPoint[] pathPoints = new PathPoint[1024];

   private void sortBack(int i) {
      PathPoint pathPoint = this.pathPoints[i];
      float distanceToTarget2 = pathPoint.distanceToTarget;

      while (i > 0) {
         int i2 = i - 1 >> 1;
         PathPoint pathPoint2 = this.pathPoints[i2];
         if (distanceToTarget2 >= pathPoint2.distanceToTarget) {
            break;
         }

         this.pathPoints[i] = pathPoint2;
         pathPoint2.index = i;
         i = i2;
      }

      this.pathPoints[i] = pathPoint;
      pathPoint.index = i;
   }

   public PathPoint addPoint(PathPoint point) {
      if (point.index >= 0) {
         throw new IllegalStateException("OW KNOWS!");
      }

      if (this.count == this.pathPoints.length) {
         PathPoint[] apathpoint = new PathPoint[this.count << 1];
         System.arraycopy(this.pathPoints, 0, apathpoint, 0, this.count);
         this.pathPoints = apathpoint;
      }

      this.pathPoints[this.count] = point;
      point.index = this.count;
      this.sortBack(this.count++);
      return point;
   }

   public PathPoint dequeue() {
      PathPoint pathPoint = this.pathPoints[0];
      this.pathPoints[0] = this.pathPoints[--this.count];
      this.pathPoints[this.count] = null;
      if (this.count > 0) {
         this.sortForward(0);
      }

      pathPoint.index = -1;
      return pathPoint;
   }

   private void sortForward(int i) {
      PathPoint pathPoint = this.pathPoints[i];
      float distanceToTarget2 = pathPoint.distanceToTarget;

      while (true) {
         int i2 = 1 + (i << 1);
         int i3 = i2 + 1;
         if (i2 >= this.count) {
            break;
         }

         PathPoint pathPoint2 = this.pathPoints[i2];
         float distanceToTarget3 = pathPoint2.distanceToTarget;
         PathPoint pathPoint3;
         float f;
         if (i3 >= this.count) {
            pathPoint3 = null;
            f = Float.POSITIVE_INFINITY;
         } else {
            pathPoint3 = this.pathPoints[i3];
            f = pathPoint3.distanceToTarget;
         }

         if (distanceToTarget3 < f) {
            if (distanceToTarget3 >= distanceToTarget2) {
               break;
            }

            this.pathPoints[i] = pathPoint2;
            pathPoint2.index = i;
            i = i2;
         } else {
            if (f >= distanceToTarget2) {
               break;
            }

            this.pathPoints[i] = pathPoint3;
            pathPoint3.index = i;
            i = i3;
         }
      }

      this.pathPoints[i] = pathPoint;
      pathPoint.index = i;
   }

   public void clearPath() {
      this.count = 0;
   }

   public void changeDistance(PathPoint pathPoint, float f) {
      float distanceToTarget2 = pathPoint.distanceToTarget;
      pathPoint.distanceToTarget = f;
      if (f < distanceToTarget2) {
         this.sortBack(pathPoint.index);
      } else {
         this.sortForward(pathPoint.index);
      }
   }

   public boolean isPathEmpty() {
      return this.count == 0;
   }
}
