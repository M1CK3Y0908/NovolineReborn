package net.minecraft.util;

import com.google.common.base.Objects;

public class Vec3i implements Comparable<Vec3i> {
   private final int x;
   private final int y;
   private final int z;
   public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);

   public int getX() {
      return this.x;
   }

   public Vec3i(double xIn, double yIn, double zIn) {
      this(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
   }

   public double distanceSq(double toX, double toY, double toZ) {
      double xCoordinate = this.getX() - toX;
      double yCoordinate = this.getY() - toY;
      double zCoordinate = this.getZ() - toZ;
      return xCoordinate * xCoordinate + yCoordinate * yCoordinate + zCoordinate * zCoordinate;
   }

   public int getY() {
      return this.y;
   }

   public double distanceSq(Vec3i to) {
      return this.distanceSq(to.getX(), to.getY(), to.getZ());
   }

   @Override
   public int hashCode() {
      return (this.getY() + this.getZ() * 31) * 31 + this.getX();
   }

   public Vec3i(int xIn, int yIn, int zIn) {
      this.x = xIn;
      this.y = yIn;
      this.z = zIn;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Vec3i vec3i)) {
         return false;
      } else {
         return this.getX() != vec3i.getX() ? false : (this.getY() != vec3i.getY() ? false : this.getZ() == vec3i.getZ());
      }
   }

   public int compareTo(Vec3i other) {
      return this.getY() == other.getY() ? (this.getZ() == other.getZ() ? this.getX() - other.getX() : this.getZ() - other.getZ()) : this.getY() - other.getY();
   }

   public int getZ() {
      return this.z;
   }

   public double distanceSqToCenter(double xIn, double yIn, double zIn) {
      double xCoordinate = this.getX() + 0.5 - xIn;
      double yCoordinate = this.getY() + 0.5 - yIn;
      double zCoordinate = this.getZ() + 0.5 - zIn;
      return xCoordinate * xCoordinate + yCoordinate * yCoordinate + zCoordinate * zCoordinate;
   }

   public Vec3i crossProduct(Vec3i vec) {
      return new Vec3i(
         this.getY() * vec.getZ() - this.getZ() * vec.getY(),
         this.getZ() * vec.getX() - this.getX() * vec.getZ(),
         this.getX() * vec.getY() - this.getY() * vec.getX()
      );
   }
}
