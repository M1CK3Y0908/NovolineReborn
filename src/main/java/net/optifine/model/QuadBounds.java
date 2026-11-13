package net.optifine.model;

import net.minecraft.util.EnumFacing;

public class QuadBounds {
   private float minX = Float.MAX_VALUE;
   private float minY = Float.MAX_VALUE;
   private float minZ = Float.MAX_VALUE;
   private float maxX = -Float.MAX_VALUE;
   private float maxY = -Float.MAX_VALUE;
   private float maxZ = -Float.MAX_VALUE;

   public boolean isFullQuad(EnumFacing face) {
      float f;
      float f2;
      float f3;
      float f4;
      switch (face) {
         case DOWN:
         case UP:
            f = this.getMinX();
            f2 = this.getMaxX();
            f3 = this.getMinZ();
            f4 = this.getMaxZ();
            break;
         case NORTH:
         case SOUTH:
            f = this.getMinX();
            f2 = this.getMaxX();
            f3 = this.getMinY();
            f4 = this.getMaxY();
            break;
         case WEST:
         case EAST:
            f = this.getMinY();
            f2 = this.getMaxY();
            f3 = this.getMinZ();
            f4 = this.getMaxZ();
            break;
         default:
            return false;
      }

      return f == 0.0F && f2 == 1.0F && f3 == 0.0F && f4 == 1.0F;
   }

   public boolean isFaceQuad(EnumFacing face) {
      float f;
      float f2;
      float f3;
      switch (face) {
         case DOWN:
            f = this.getMinY();
            f2 = this.getMaxY();
            f3 = 0.0F;
            break;
         case UP:
            f = this.getMinY();
            f2 = this.getMaxY();
            f3 = 1.0F;
            break;
         case NORTH:
            f = this.getMinZ();
            f2 = this.getMaxZ();
            f3 = 0.0F;
            break;
         case SOUTH:
            f = this.getMinZ();
            f2 = this.getMaxZ();
            f3 = 1.0F;
            break;
         case WEST:
            f = this.getMinX();
            f2 = this.getMaxX();
            f3 = 0.0F;
            break;
         case EAST:
            f = this.getMinX();
            f2 = this.getMaxX();
            f3 = 1.0F;
            break;
         default:
            return false;
      }

      return f == f3 && f2 == f3;
   }

   public float getMaxZ() {
      return this.maxZ;
   }

   public float getMaxY() {
      return this.maxY;
   }

   public float getMinY() {
      return this.minY;
   }

   public float getMinX() {
      return this.minX;
   }

   public QuadBounds(int[] vertexData) {
      int count = vertexData.length / 4;

      for (int innerIndex = 0; innerIndex < 4; innerIndex++) {
         int i = innerIndex * count;
         float f = Float.intBitsToFloat(vertexData[i + 0]);
         float f2 = Float.intBitsToFloat(vertexData[i + 1]);
         float f3 = Float.intBitsToFloat(vertexData[i + 2]);
         if (this.minX > f) {
            this.minX = f;
         }

         if (this.minY > f2) {
            this.minY = f2;
         }

         if (this.minZ > f3) {
            this.minZ = f3;
         }

         if (this.maxX < f) {
            this.maxX = f;
         }

         if (this.maxY < f2) {
            this.maxY = f2;
         }

         if (this.maxZ < f3) {
            this.maxZ = f3;
         }
      }
   }

   public float getMinZ() {
      return this.minZ;
   }

   public float getMaxX() {
      return this.maxX;
   }
}
