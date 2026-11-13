package net.minecraft.pathfinding;

import net.minecraft.util.MathHelper;

public class PathPoint {
   int index = -1;
   public final int xCoord;
   PathPoint previous;
   public final int yCoord;
   float distanceToTarget;
   float totalPathDistance;
   public boolean visited;
   float distanceToNext;
   public final int zCoord;
   private final int hash;

   public float distanceToSquared(PathPoint pathpointIn) {
      float f = pathpointIn.xCoord - this.xCoord;
      float f2 = pathpointIn.yCoord - this.yCoord;
      float f3 = pathpointIn.zCoord - this.zCoord;
      return f * f + f2 * f2 + f3 * f3;
   }

   public boolean isAssigned() {
      return this.index >= 0;
   }

   public PathPoint(int x, int y, int z) {
      this.xCoord = x;
      this.yCoord = y;
      this.zCoord = z;
      this.hash = makeHash(x, y, z);
   }

   @Override
   public boolean equals(Object other) {
      return !(other instanceof PathPoint pathPoint)
         ? false
         : this.hash == pathPoint.hash && this.xCoord == pathPoint.xCoord && this.yCoord == pathPoint.yCoord && this.zCoord == pathPoint.zCoord;
   }

   @Override
   public int hashCode() {
      return this.hash;
   }

   public static int makeHash(int x, int y, int z) {
      return y & 0xFF | (x & 32767) << 8 | (z & 32767) << 24 | (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? 32768 : 0);
   }

   public float distanceTo(PathPoint pathpointIn) {
      float f = pathpointIn.xCoord - this.xCoord;
      float f2 = pathpointIn.yCoord - this.yCoord;
      float f3 = pathpointIn.zCoord - this.zCoord;
      return MathHelper.sqrt_float(f * f + f2 * f2 + f3 * f3);
   }

   @Override
   public String toString() {
      return this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
   }
}
