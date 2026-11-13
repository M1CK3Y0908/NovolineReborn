package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerAddMushroomIsland extends GenLayer {
   public GenLayerAddMushroomIsland(long l, GenLayer genLayer) {
      super(l);
      this.parent = genLayer;
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
            int i5 = intArray[innerIndex + 0 + (index + 0) * i3];
            int i6 = intArray[innerIndex + 2 + (index + 0) * i3];
            int i7 = intArray[innerIndex + 0 + (index + 2) * i3];
            int i8 = intArray[innerIndex + 2 + (index + 2) * i3];
            int i9 = intArray[innerIndex + 1 + (index + 1) * i3];
            this.initChunkSeed(innerIndex + areaX, index + areaY);
            if (i9 == 0 && i5 == 0 && i6 == 0 && i7 == 0 && i8 == 0 && this.nextInt(100) == 0) {
               intArray1[innerIndex + index * areaWidth] = BiomeGenBase.mushroomIsland.biomeID;
            } else {
               intArray1[innerIndex + index * areaWidth] = i9;
            }
         }
      }

      return intArray1;
   }
}
