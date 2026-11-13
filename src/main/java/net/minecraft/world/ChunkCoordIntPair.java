package net.minecraft.world;

import net.minecraft.util.BlockPos;

public class ChunkCoordIntPair {
   private int cachedHashCode = 0;
   public final int chunkXPos;
   public final int chunkZPos;

   public int getXStart() {
      return this.chunkXPos << 4;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else {
         return !(other instanceof ChunkCoordIntPair chunkCoordIntPair)
            ? false
            : this.chunkXPos == chunkCoordIntPair.chunkXPos && this.chunkZPos == chunkCoordIntPair.chunkZPos;
      }
   }

   public int getInt2() {
      return (this.chunkXPos << 4) + 8;
   }

   public int getZStart() {
      return this.chunkZPos << 4;
   }

   public int getInt() {
      return (this.chunkZPos << 4) + 8;
   }

   public BlockPos getBlock(int x, int y, int z) {
      return new BlockPos((this.chunkXPos << 4) + x, y, (this.chunkZPos << 4) + z);
   }

   public BlockPos getBlockPos() {
      return new BlockPos(this.getInt2(), 64, this.getInt());
   }

   @Override
   public int hashCode() {
      if (this.cachedHashCode == 0) {
         int i = 1664525 * this.chunkXPos + 1013904223;
         int i2 = 1664525 * (this.chunkZPos ^ -559038737) + 1013904223;
         this.cachedHashCode = i ^ i2;
      }

      return this.cachedHashCode;
   }

   public ChunkCoordIntPair(int x, int z) {
      this.chunkXPos = x;
      this.chunkZPos = z;
   }

   @Override
   public String toString() {
      return "[" + this.chunkXPos + ", " + this.chunkZPos + "]";
   }

   public int getXEnd() {
      return (this.chunkXPos << 4) + 15;
   }

   public static long chunkXZ2Int(int x, int z) {
      return x & 4294967295L | (z & 4294967295L) << 32;
   }

   public int getZEnd() {
      return (this.chunkZPos << 4) + 15;
   }
}
