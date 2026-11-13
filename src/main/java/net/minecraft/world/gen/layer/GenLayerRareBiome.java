package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRareBiome extends GenLayer {
   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] intArray = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
      int[] intArray1 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int index = 0; index < areaHeight; index++) {
         for (int innerIndex = 0; innerIndex < areaWidth; innerIndex++) {
            this.initChunkSeed(innerIndex + areaX, index + areaY);
            int i = intArray[innerIndex + 1 + (index + 1) * (areaWidth + 2)];
            if (this.nextInt(57) == 0) {
               if (i == BiomeGenBase.plains.biomeID) {
                  intArray1[innerIndex + index * areaWidth] = BiomeGenBase.plains.biomeID + 128;
               } else {
                  intArray1[innerIndex + index * areaWidth] = i;
               }
            } else {
               intArray1[innerIndex + index * areaWidth] = i;
            }
         }
      }

      return intArray1;
   }

   public GenLayerRareBiome(long l, GenLayer genLayer) {
      super(l);
      this.parent = genLayer;
   }
}
