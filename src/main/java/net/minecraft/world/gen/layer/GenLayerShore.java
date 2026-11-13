package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenJungle;
import net.minecraft.world.biome.BiomeGenMesa;

public class GenLayerShore extends GenLayer {
   private boolean isJungleCompatibleBiome(int biomeId) {
      return BiomeGenBase.getBiome(biomeId) != null && BiomeGenBase.getBiome(biomeId).getBiomeClass() == BiomeGenJungle.class
         ? true
         : biomeId == BiomeGenBase.jungleEdge.biomeID
            || biomeId == BiomeGenBase.jungle.biomeID
            || biomeId == BiomeGenBase.jungleHills.biomeID
            || biomeId == BiomeGenBase.forest.biomeID
            || biomeId == BiomeGenBase.taiga.biomeID
            || isBiomeOceanic(biomeId);
   }

   public GenLayerShore(long seed, GenLayer parent) {
      super(seed);
      this.parent = parent;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] intArray = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
      int[] intArray1 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int index = 0; index < areaHeight; index++) {
         for (int innerIndex = 0; innerIndex < areaWidth; innerIndex++) {
            this.initChunkSeed(innerIndex + areaX, index + areaY);
            int i = intArray[innerIndex + 1 + (index + 1) * (areaWidth + 2)];
            BiomeGenBase biomeGenBase = BiomeGenBase.getBiome(i);
            if (i == BiomeGenBase.mushroomIsland.biomeID) {
               int i2 = intArray[innerIndex + 1 + (index + 1 - 1) * (areaWidth + 2)];
               int i3 = intArray[innerIndex + 1 + 1 + (index + 1) * (areaWidth + 2)];
               int i4 = intArray[innerIndex + 1 - 1 + (index + 1) * (areaWidth + 2)];
               int i5 = intArray[innerIndex + 1 + (index + 1 + 1) * (areaWidth + 2)];
               if (i2 != BiomeGenBase.ocean.biomeID
                  && i3 != BiomeGenBase.ocean.biomeID
                  && i4 != BiomeGenBase.ocean.biomeID
                  && i5 != BiomeGenBase.ocean.biomeID) {
                  intArray1[innerIndex + index * areaWidth] = i;
               } else {
                  intArray1[innerIndex + index * areaWidth] = BiomeGenBase.mushroomIslandShore.biomeID;
               }
            } else if (biomeGenBase != null && biomeGenBase.getBiomeClass() == BiomeGenJungle.class) {
               int i6 = intArray[innerIndex + 1 + (index + 1 - 1) * (areaWidth + 2)];
               int i7 = intArray[innerIndex + 1 + 1 + (index + 1) * (areaWidth + 2)];
               int i8 = intArray[innerIndex + 1 - 1 + (index + 1) * (areaWidth + 2)];
               int i9 = intArray[innerIndex + 1 + (index + 1 + 1) * (areaWidth + 2)];
               if (!this.isJungleCompatibleBiome(i6)
                  || !this.isJungleCompatibleBiome(i7)
                  || !this.isJungleCompatibleBiome(i8)
                  || !this.isJungleCompatibleBiome(i9)) {
                  intArray1[innerIndex + index * areaWidth] = BiomeGenBase.jungleEdge.biomeID;
               } else if (!isBiomeOceanic(i6) && !isBiomeOceanic(i7) && !isBiomeOceanic(i8) && !isBiomeOceanic(i9)) {
                  intArray1[innerIndex + index * areaWidth] = i;
               } else {
                  intArray1[innerIndex + index * areaWidth] = BiomeGenBase.beach.biomeID;
               }
            } else if (i == BiomeGenBase.extremeHills.biomeID
               || i == BiomeGenBase.extremeHillsPlus.biomeID
               || i == BiomeGenBase.extremeHillsEdge.biomeID) {
               this.applyBeach(intArray, intArray1, innerIndex, index, areaWidth, i, BiomeGenBase.stoneBeach.biomeID);
            } else if (biomeGenBase != null && biomeGenBase.isSnowyBiome()) {
               this.applyBeach(intArray, intArray1, innerIndex, index, areaWidth, i, BiomeGenBase.coldBeach.biomeID);
            } else if (i == BiomeGenBase.mesa.biomeID || i == BiomeGenBase.mesaPlateau_F.biomeID) {
               int i10 = intArray[innerIndex + 1 + (index + 1 - 1) * (areaWidth + 2)];
               int i11 = intArray[innerIndex + 1 + 1 + (index + 1) * (areaWidth + 2)];
               int i12 = intArray[innerIndex + 1 - 1 + (index + 1) * (areaWidth + 2)];
               int i13 = intArray[innerIndex + 1 + (index + 1 + 1) * (areaWidth + 2)];
               if (isBiomeOceanic(i10) || isBiomeOceanic(i11) || isBiomeOceanic(i12) || isBiomeOceanic(i13)) {
                  intArray1[innerIndex + index * areaWidth] = i;
               } else if (this.isMesaBiome(i10)
                  && this.isMesaBiome(i11)
                  && this.isMesaBiome(i12)
                  && this.isMesaBiome(i13)) {
                  intArray1[innerIndex + index * areaWidth] = i;
               } else {
                  intArray1[innerIndex + index * areaWidth] = BiomeGenBase.desert.biomeID;
               }
            } else if (i != BiomeGenBase.ocean.biomeID
               && i != BiomeGenBase.deepOcean.biomeID
               && i != BiomeGenBase.river.biomeID
               && i != BiomeGenBase.swampland.biomeID) {
               int i14 = intArray[innerIndex + 1 + (index + 1 - 1) * (areaWidth + 2)];
               int i15 = intArray[innerIndex + 1 + 1 + (index + 1) * (areaWidth + 2)];
               int i16 = intArray[innerIndex + 1 - 1 + (index + 1) * (areaWidth + 2)];
               int i17 = intArray[innerIndex + 1 + (index + 1 + 1) * (areaWidth + 2)];
               if (!isBiomeOceanic(i14) && !isBiomeOceanic(i15) && !isBiomeOceanic(i16) && !isBiomeOceanic(i17)) {
                  intArray1[innerIndex + index * areaWidth] = i;
               } else {
                  intArray1[innerIndex + index * areaWidth] = BiomeGenBase.beach.biomeID;
               }
            } else {
               intArray1[innerIndex + index * areaWidth] = i;
            }
         }
      }

      return intArray1;
   }

   private boolean isMesaBiome(int biomeId) {
      return BiomeGenBase.getBiome(biomeId) instanceof BiomeGenMesa;
   }

   private void applyBeach(int[] parentBiomes, int[] outputBiomes, int x, int y, int width, int biomeId, int beachBiomeId) {
      if (isBiomeOceanic(biomeId)) {
         outputBiomes[x + y * width] = biomeId;
      } else {
         int i = parentBiomes[x + 1 + (y + 1 - 1) * (width + 2)];
         int i2 = parentBiomes[x + 1 + 1 + (y + 1) * (width + 2)];
         int i3 = parentBiomes[x + 1 - 1 + (y + 1) * (width + 2)];
         int i4 = parentBiomes[x + 1 + (y + 1 + 1) * (width + 2)];
         if (!isBiomeOceanic(i) && !isBiomeOceanic(i2) && !isBiomeOceanic(i3) && !isBiomeOceanic(i4)) {
            outputBiomes[x + y * width] = biomeId;
         } else {
            outputBiomes[x + y * width] = beachBiomeId;
         }
      }
   }
}
