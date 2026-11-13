package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenLayerHills extends GenLayer {
   private static final Logger logger = LogManager.getLogger();
   private GenLayer riverLayer;

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] intArray = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
      int[] intArray1 = this.riverLayer.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
      int[] intArray2 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int index = 0; index < areaHeight; index++) {
         for (int innerIndex = 0; innerIndex < areaWidth; innerIndex++) {
            this.initChunkSeed(innerIndex + areaX, index + areaY);
            int i = intArray[innerIndex + 1 + (index + 1) * (areaWidth + 2)];
            int i2 = intArray1[innerIndex + 1 + (index + 1) * (areaWidth + 2)];
            boolean flag = (i2 - 2) % 29 == 0;
            if (i > 255) {
               logger.debug("old! " + i);
            }

            if (i != 0 && i2 >= 2 && (i2 - 2) % 29 == 1 && i < 128) {
               if (BiomeGenBase.getBiome(i + 128) != null) {
                  intArray2[innerIndex + index * areaWidth] = i + 128;
               } else {
                  intArray2[innerIndex + index * areaWidth] = i;
               }
            } else if (this.nextInt(3) != 0 && !flag) {
               intArray2[innerIndex + index * areaWidth] = i;
            } else {
               int i3 = i;
               if (i == BiomeGenBase.desert.biomeID) {
                  i3 = BiomeGenBase.desertHills.biomeID;
               } else if (i == BiomeGenBase.forest.biomeID) {
                  i3 = BiomeGenBase.forestHills.biomeID;
               } else if (i == BiomeGenBase.birchForest.biomeID) {
                  i3 = BiomeGenBase.birchForestHills.biomeID;
               } else if (i == BiomeGenBase.roofedForest.biomeID) {
                  i3 = BiomeGenBase.plains.biomeID;
               } else if (i == BiomeGenBase.taiga.biomeID) {
                  i3 = BiomeGenBase.taigaHills.biomeID;
               } else if (i == BiomeGenBase.megaTaiga.biomeID) {
                  i3 = BiomeGenBase.megaTaigaHills.biomeID;
               } else if (i == BiomeGenBase.coldTaiga.biomeID) {
                  i3 = BiomeGenBase.coldTaigaHills.biomeID;
               } else if (i == BiomeGenBase.plains.biomeID) {
                  if (this.nextInt(3) == 0) {
                     i3 = BiomeGenBase.forestHills.biomeID;
                  } else {
                     i3 = BiomeGenBase.forest.biomeID;
                  }
               } else if (i == BiomeGenBase.icePlains.biomeID) {
                  i3 = BiomeGenBase.iceMountains.biomeID;
               } else if (i == BiomeGenBase.jungle.biomeID) {
                  i3 = BiomeGenBase.jungleHills.biomeID;
               } else if (i == BiomeGenBase.ocean.biomeID) {
                  i3 = BiomeGenBase.deepOcean.biomeID;
               } else if (i == BiomeGenBase.extremeHills.biomeID) {
                  i3 = BiomeGenBase.extremeHillsPlus.biomeID;
               } else if (i == BiomeGenBase.savanna.biomeID) {
                  i3 = BiomeGenBase.savannaPlateau.biomeID;
               } else if (biomesEqualOrMesaPlateau(i, BiomeGenBase.mesaPlateau_F.biomeID)) {
                  i3 = BiomeGenBase.mesa.biomeID;
               } else if (i == BiomeGenBase.deepOcean.biomeID && this.nextInt(3) == 0) {
                  int i4 = this.nextInt(2);
                  if (i4 == 0) {
                     i3 = BiomeGenBase.plains.biomeID;
                  } else {
                     i3 = BiomeGenBase.forest.biomeID;
                  }
               }

               if (flag && i3 != i) {
                  if (BiomeGenBase.getBiome(i3 + 128) != null) {
                     i3 += 128;
                  } else {
                     i3 = i;
                  }
               }

               if (i3 == i) {
                  intArray2[innerIndex + index * areaWidth] = i;
               } else {
                  int i5 = intArray[innerIndex + 1 + (index + 1 - 1) * (areaWidth + 2)];
                  int i6 = intArray[innerIndex + 1 + 1 + (index + 1) * (areaWidth + 2)];
                  int i7 = intArray[innerIndex + 1 - 1 + (index + 1) * (areaWidth + 2)];
                  int i8 = intArray[innerIndex + 1 + (index + 1 + 1) * (areaWidth + 2)];
                  int i9 = 0;
                  if (biomesEqualOrMesaPlateau(i5, i)) {
                     i9++;
                  }

                  if (biomesEqualOrMesaPlateau(i6, i)) {
                     i9++;
                  }

                  if (biomesEqualOrMesaPlateau(i7, i)) {
                     i9++;
                  }

                  if (biomesEqualOrMesaPlateau(i8, i)) {
                     i9++;
                  }

                  if (i9 >= 3) {
                     intArray2[innerIndex + index * areaWidth] = i3;
                  } else {
                     intArray2[innerIndex + index * areaWidth] = i;
                  }
               }
            }
         }
      }

      return intArray2;
   }

   public GenLayerHills(long seed, GenLayer parent, GenLayer riverLayer) {
      super(seed);
      this.parent = parent;
      this.riverLayer = riverLayer;
   }
}
