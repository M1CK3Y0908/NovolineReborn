package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerDeepOcean extends GenLayer {
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
            int i5 = intArray[innerIndex + 1 + (index + 1 - 1) * (areaWidth + 2)];
            int i6 = intArray[innerIndex + 1 + 1 + (index + 1) * (areaWidth + 2)];
            int i7 = intArray[innerIndex + 1 - 1 + (index + 1) * (areaWidth + 2)];
            int i8 = intArray[innerIndex + 1 + (index + 1 + 1) * (areaWidth + 2)];
            int i9 = intArray[innerIndex + 1 + (index + 1) * i3];
            int i10 = 0;
            if (i5 == 0) {
               i10++;
            }

            if (i6 == 0) {
               i10++;
            }

            if (i7 == 0) {
               i10++;
            }

            if (i8 == 0) {
               i10++;
            }

            if (i9 == 0 && i10 > 3) {
               intArray1[innerIndex + index * areaWidth] = BiomeGenBase.deepOcean.biomeID;
            } else {
               intArray1[innerIndex + index * areaWidth] = i9;
            }
         }
      }

      return intArray1;
   }

   public GenLayerDeepOcean(long l, GenLayer genLayer) {
      super(l);
      this.parent = genLayer;
   }
}
