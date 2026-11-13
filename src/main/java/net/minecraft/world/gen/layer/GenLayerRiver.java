package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRiver extends GenLayer {
   public GenLayerRiver(long seed, GenLayer parent) {
      super(seed);
      super.parent = parent;
   }

   private int normalizeRiverValue(int value) {
      return value >= 2 ? 2 + (value & 1) : value;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int i = areaX - 1;
      int i2 = areaY - 1;
      int i3 = areaWidth + 2;
      int i4 = areaHeight + 2;
      int[] intArray = this.parent.getInts(i, i2, i3, i4);
      int[] intArray1 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int index = 0; index < areaHeight; index++) {
         for (int innerIndex = 0; innerIndex < areaWidth; innerIndex++) {
            int i5 = this.normalizeRiverValue(intArray[innerIndex + 0 + (index + 1) * i3]);
            int i6 = this.normalizeRiverValue(intArray[innerIndex + 2 + (index + 1) * i3]);
            int i7 = this.normalizeRiverValue(intArray[innerIndex + 1 + (index + 0) * i3]);
            int i8 = this.normalizeRiverValue(intArray[innerIndex + 1 + (index + 2) * i3]);
            int i9 = this.normalizeRiverValue(intArray[innerIndex + 1 + (index + 1) * i3]);
            if (i9 == i5 && i9 == i7 && i9 == i6 && i9 == i8) {
               intArray1[innerIndex + index * areaWidth] = -1;
            } else {
               intArray1[innerIndex + index * areaWidth] = BiomeGenBase.river.biomeID;
            }
         }
      }

      return intArray1;
   }
}
