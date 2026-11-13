package net.minecraft.util;

public class AxisAlignedBB {
   public final double minX;
   public final double minY;
   public final double minZ;
   public final double maxX;
   public final double maxY;
   public final double maxZ;

   public double calculateXOffset(AxisAlignedBB other, double offsetX) {
      if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
         if (offsetX > 0.0 && other.maxX <= this.minX) {
            double d = this.minX - other.maxX;
            if (d < offsetX) {
               offsetX = d;
            }
         } else if (offsetX < 0.0 && other.minX >= this.maxX) {
            double d2 = this.maxX - other.minX;
            if (d2 > offsetX) {
               offsetX = d2;
            }
         }

         return offsetX;
      } else {
         return offsetX;
      }
   }

   public AxisAlignedBB expand(double x, double y, double z) {
      double d = this.minX - x;
      double d2 = this.minY - y;
      double d3 = this.minZ - z;
      double d4 = this.maxX + x;
      double d5 = this.maxY + y;
      double d6 = this.maxZ + z;
      return new AxisAlignedBB(d, d2, d3, d4, d5, d6);
   }

   public AxisAlignedBB addCoord(double x, double y, double z) {
      double minX2 = this.minX;
      double minY2 = this.minY;
      double minZ2 = this.minZ;
      double maxX2 = this.maxX;
      double maxY2 = this.maxY;
      double maxZ2 = this.maxZ;
      if (x < 0.0) {
         minX2 += x;
      } else if (x > 0.0) {
         maxX2 += x;
      }

      if (y < 0.0) {
         minY2 += y;
      } else if (y > 0.0) {
         maxY2 += y;
      }

      if (z < 0.0) {
         minZ2 += z;
      } else if (z > 0.0) {
         maxZ2 += z;
      }

      return new AxisAlignedBB(minX2, minY2, minZ2, maxX2, maxY2, maxZ2);
   }

   public boolean hasNaN() {
      return Double.isNaN(this.minX)
         || Double.isNaN(this.minY)
         || Double.isNaN(this.minZ)
         || Double.isNaN(this.maxX)
         || Double.isNaN(this.maxY)
         || Double.isNaN(this.maxZ);
   }

   public AxisAlignedBB offset(double x, double y, double z) {
      return new AxisAlignedBB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
   }

   public AxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
      this.minX = Math.min(x1, x2);
      this.minY = Math.min(y1, y2);
      this.minZ = Math.min(z1, z2);
      this.maxX = Math.max(x1, x2);
      this.maxY = Math.max(y1, y2);
      this.maxZ = Math.max(z1, z2);
   }

   public boolean isVecInside(Vec3 vec) {
      return !(vec.xCoord > this.minX) || !(vec.xCoord < this.maxX)
         ? false
         : (vec.yCoord > this.minY && vec.yCoord < this.maxY ? vec.zCoord > this.minZ && vec.zCoord < this.maxZ : false);
   }

   public double calculateYOffset(AxisAlignedBB other, double offsetY) {
      if (other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ) {
         if (offsetY > 0.0 && other.maxY <= this.minY) {
            double d = this.minY - other.maxY;
            if (d < offsetY) {
               offsetY = d;
            }
         } else if (offsetY < 0.0 && other.minY >= this.maxY) {
            double d2 = this.maxY - other.minY;
            if (d2 > offsetY) {
               offsetY = d2;
            }
         }

         return offsetY;
      } else {
         return offsetY;
      }
   }

   public MovingObjectPosition calculateIntercept(Vec3 vecA, Vec3 vecB) {
      Vec3 vec3 = vecA.getIntermediateWithXValue(vecB, this.minX);
      Vec3 vec37 = vecA.getIntermediateWithXValue(vecB, this.maxX);
      Vec3 vec38 = vecA.getIntermediateWithYValue(vecB, this.minY);
      Vec3 vec39 = vecA.getIntermediateWithYValue(vecB, this.maxY);
      Vec3 vec310 = vecA.getIntermediateWithZValue(vecB, this.minZ);
      Vec3 vec311 = vecA.getIntermediateWithZValue(vecB, this.maxZ);
      if (!this.isVecInYZ(vec3)) {
         vec3 = null;
      }

      if (!this.isVecInYZ(vec37)) {
         vec37 = null;
      }

      if (!this.isVecInXZ(vec38)) {
         vec38 = null;
      }

      if (!this.isVecInXZ(vec39)) {
         vec39 = null;
      }

      if (!this.isVecInXY(vec310)) {
         vec310 = null;
      }

      if (!this.isVecInXY(vec311)) {
         vec311 = null;
      }

      Vec3 vec32 = null;
      if (vec3 != null) {
         vec32 = vec3;
      }

      if (vec37 != null && (vec32 == null || vecA.squareDistanceTo(vec37) < vecA.squareDistanceTo(vec32))) {
         vec32 = vec37;
      }

      if (vec38 != null && (vec32 == null || vecA.squareDistanceTo(vec38) < vecA.squareDistanceTo(vec32))) {
         vec32 = vec38;
      }

      if (vec39 != null && (vec32 == null || vecA.squareDistanceTo(vec39) < vecA.squareDistanceTo(vec32))) {
         vec32 = vec39;
      }

      if (vec310 != null && (vec32 == null || vecA.squareDistanceTo(vec310) < vecA.squareDistanceTo(vec32))) {
         vec32 = vec310;
      }

      if (vec311 != null && (vec32 == null || vecA.squareDistanceTo(vec311) < vecA.squareDistanceTo(vec32))) {
         vec32 = vec311;
      }

      if (vec32 == null) {
         return null;
      }

      EnumFacing facing;
      if (vec32 == vec3) {
         facing = EnumFacing.WEST;
      } else if (vec32 == vec37) {
         facing = EnumFacing.EAST;
      } else if (vec32 == vec38) {
         facing = EnumFacing.DOWN;
      } else if (vec32 == vec39) {
         facing = EnumFacing.UP;
      } else if (vec32 == vec310) {
         facing = EnumFacing.NORTH;
      } else {
         facing = EnumFacing.SOUTH;
      }

      return new MovingObjectPosition(vec32, facing);
   }

   private boolean isVecInXY(Vec3 vec) {
      return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.yCoord >= this.minY && vec.yCoord <= this.maxY;
   }

   private boolean isVecInYZ(Vec3 vec) {
      return vec == null ? false : vec.yCoord >= this.minY && vec.yCoord <= this.maxY && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
   }

   private boolean isVecInXZ(Vec3 vec) {
      return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
   }

   public boolean intersectsWith(AxisAlignedBB other) {
      return !(other.maxX > this.minX) || !(other.minX < this.maxX)
         ? false
         : (other.maxY > this.minY && other.minY < this.maxY ? other.maxZ > this.minZ && other.minZ < this.maxZ : false);
   }

   public AxisAlignedBB union(AxisAlignedBB other) {
      double d = Math.min(this.minX, other.minX);
      double d2 = Math.min(this.minY, other.minY);
      double d3 = Math.min(this.minZ, other.minZ);
      double d4 = Math.max(this.maxX, other.maxX);
      double d5 = Math.max(this.maxY, other.maxY);
      double d6 = Math.max(this.maxZ, other.maxZ);
      return new AxisAlignedBB(d, d2, d3, d4, d5, d6);
   }

   public double getAverageEdgeLength() {
      double d = this.maxX - this.minX;
      double d2 = this.maxY - this.minY;
      double d3 = this.maxZ - this.minZ;
      return (d + d2 + d3) / 3.0;
   }

   public double calculateZOffset(AxisAlignedBB other, double offsetZ) {
      if (other.maxX > this.minX && other.minX < this.maxX && other.maxY > this.minY && other.minY < this.maxY) {
         if (offsetZ > 0.0 && other.maxZ <= this.minZ) {
            double d = this.minZ - other.maxZ;
            if (d < offsetZ) {
               offsetZ = d;
            }
         } else if (offsetZ < 0.0 && other.minZ >= this.maxZ) {
            double d2 = this.maxZ - other.minZ;
            if (d2 > offsetZ) {
               offsetZ = d2;
            }
         }

         return offsetZ;
      } else {
         return offsetZ;
      }
   }

   public static AxisAlignedBB fromBounds(double x1, double y1, double z1, double x2, double y2, double z2) {
      double d = Math.min(x1, x2);
      double d2 = Math.min(y1, y2);
      double d3 = Math.min(z1, z2);
      double d4 = Math.max(x1, x2);
      double d5 = Math.max(y1, y2);
      double d6 = Math.max(z1, z2);
      return new AxisAlignedBB(d, d2, d3, d4, d5, d6);
   }

   public AxisAlignedBB(BlockPos pos1, BlockPos pos2) {
      this.minX = pos1.getX();
      this.minY = pos1.getY();
      this.minZ = pos1.getZ();
      this.maxX = pos2.getX();
      this.maxY = pos2.getY();
      this.maxZ = pos2.getZ();
   }

   @Override
   public String toString() {
      double maxZ2 = this.maxZ;
      double maxY2 = this.maxY;
      double maxX2 = this.maxX;
      double minZ2 = this.minZ;
      double minY2 = this.minY;
      double minX2 = this.minX;
      return "box[" + minX2 + ", " + minY2 + ", " + minZ2 + " -> " + maxX2 + ", " + maxY2 + ", " + maxZ2 + "]";
   }

   public AxisAlignedBB contract(double x, double y, double z) {
      double d = this.minX + x;
      double d2 = this.minY + y;
      double d3 = this.minZ + z;
      double d4 = this.maxX - x;
      double d5 = this.maxY - y;
      double d6 = this.maxZ - z;
      return new AxisAlignedBB(d, d2, d3, d4, d5, d6);
   }
}
