package net.minecraft.world.gen.structure;

import com.google.common.base.Objects;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;

public class StructureBoundingBox {
   public int minZ;
   public int maxX;
   public int minX;
   public int maxY;
   public int minY;
   public int maxZ;

   public StructureBoundingBox() {
   }

   public static StructureBoundingBox fromCorners(int x1, int y1, int z1, int x2, int y2, int z2) {
      return new StructureBoundingBox(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2), Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
   }

   public StructureBoundingBox(Vec3i vec1, Vec3i vec2) {
      this.minX = Math.min(vec1.getX(), vec2.getX());
      this.minY = Math.min(vec1.getY(), vec2.getY());
      this.minZ = Math.min(vec1.getZ(), vec2.getZ());
      this.maxX = Math.max(vec1.getX(), vec2.getX());
      this.maxY = Math.max(vec1.getY(), vec2.getY());
      this.maxZ = Math.max(vec1.getZ(), vec2.getZ());
   }

   public boolean isVecInside(Vec3i vec) {
      return vec.getX() >= this.minX
         && vec.getX() <= this.maxX
         && vec.getZ() >= this.minZ
         && vec.getZ() <= this.maxZ
         && vec.getY() >= this.minY
         && vec.getY() <= this.maxY;
   }

   public NBTTagIntArray toNBTTagIntArray() {
      return new NBTTagIntArray(new int[]{this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ});
   }

   public static StructureBoundingBox createStructureBoundingBox(
      int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, EnumFacing enumFacing
   ) {
      switch (enumFacing) {
         case NORTH:
            return new StructureBoundingBox(i + i4, i2 + i5, i3 - i8 + 1 + 0, i + i6 - 1 + i4, i2 + i7 - 1 + i5, i3 + 0);
         case SOUTH:
            return new StructureBoundingBox(i + i4, i2 + i5, i3 + 0, i + i6 - 1 + i4, i2 + i7 - 1 + i5, i3 + i8 - 1 + 0);
         case WEST:
            return new StructureBoundingBox(i - i8 + 1 + 0, i2 + i5, i3 + i4, i + 0, i2 + i7 - 1 + i5, i3 + i6 - 1 + i4);
         case EAST:
            return new StructureBoundingBox(i + 0, i2 + i5, i3 + i4, i + i8 - 1 + 0, i2 + i7 - 1 + i5, i3 + i6 - 1 + i4);
         default:
            return new StructureBoundingBox(i + i4, i2 + i5, i3 + 0, i + i6 - 1 + i4, i2 + i7 - 1 + i5, i3 + i8 - 1 + 0);
      }
   }

   public int getZSize() {
      return this.maxZ - this.minZ + 1;
   }

   public StructureBoundingBox(StructureBoundingBox structurebb) {
      this.minX = structurebb.minX;
      this.minY = structurebb.minY;
      this.minZ = structurebb.minZ;
      this.maxX = structurebb.maxX;
      this.maxY = structurebb.maxY;
      this.maxZ = structurebb.maxZ;
   }

   public boolean intersectsWith(StructureBoundingBox structurebb) {
      return this.maxX >= structurebb.minX
         && this.minX <= structurebb.maxX
         && this.maxZ >= structurebb.minZ
         && this.minZ <= structurebb.maxZ
         && this.maxY >= structurebb.minY
         && this.minY <= structurebb.maxY;
   }

   public StructureBoundingBox(int[] coords) {
      if (coords.length == 6) {
         this.minX = coords[0];
         this.minY = coords[1];
         this.minZ = coords[2];
         this.maxX = coords[3];
         this.maxY = coords[4];
         this.maxZ = coords[5];
      }
   }

   public int getXSize() {
      return this.maxX - this.minX + 1;
   }

   public void offset(int x, int y, int z) {
      this.minX += x;
      this.minY += y;
      this.minZ += z;
      this.maxX += x;
      this.maxY += y;
      this.maxZ += z;
   }

   public StructureBoundingBox(int xMin, int zMin, int xMax, int zMax) {
      this.minX = xMin;
      this.minZ = zMin;
      this.maxX = xMax;
      this.maxZ = zMax;
      this.minY = 1;
      this.maxY = 512;
   }

   public int getYSize() {
      return this.maxY - this.minY + 1;
   }

   public Vec3i getCenter() {
      return new BlockPos(this.minX + (this.maxX - this.minX + 1) / 2, this.minY + (this.maxY - this.minY + 1) / 2, this.minZ + (this.maxZ - this.minZ + 1) / 2);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
         .add("x0", this.minX)
         .add("y0", this.minY)
         .add("z0", this.minZ)
         .add("x1", this.maxX)
         .add("y1", this.maxY)
         .add("z1", this.maxZ)
         .toString();
   }

   public static StructureBoundingBox getNewBoundingBox() {
      return new StructureBoundingBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
   }

   public boolean intersectsWith(int minXIn, int minZIn, int maxXIn, int maxZIn) {
      return this.maxX >= minXIn && this.minX <= maxXIn && this.maxZ >= minZIn && this.minZ <= maxZIn;
   }

   public void expandTo(StructureBoundingBox sbb) {
      this.minX = Math.min(this.minX, sbb.minX);
      this.minY = Math.min(this.minY, sbb.minY);
      this.minZ = Math.min(this.minZ, sbb.minZ);
      this.maxX = Math.max(this.maxX, sbb.maxX);
      this.maxY = Math.max(this.maxY, sbb.maxY);
      this.maxZ = Math.max(this.maxZ, sbb.maxZ);
   }

   public Vec3i getLength() {
      return new Vec3i(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
   }

   public StructureBoundingBox(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
      this.minX = xMin;
      this.minY = yMin;
      this.minZ = zMin;
      this.maxX = xMax;
      this.maxY = yMax;
      this.maxZ = zMax;
   }
}
