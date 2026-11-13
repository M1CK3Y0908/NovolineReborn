package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerBiomeEdge extends GenLayer {
   private boolean replaceBiomeEdge(int[] parentBiomes, int[] outputBiomes, int x, int y, int width, int biomeId, int targetBiomeId, int replacementBiomeId) {
      if (biomeId != targetBiomeId) {
         return false;
      }

      int i = parentBiomes[x + 1 + (y + 1 - 1) * (width + 2)];
      int i2 = parentBiomes[x + 1 + 1 + (y + 1) * (width + 2)];
      int i3 = parentBiomes[x + 1 - 1 + (y + 1) * (width + 2)];
      int i4 = parentBiomes[x + 1 + (y + 1 + 1) * (width + 2)];
      if (biomesEqualOrMesaPlateau(i, targetBiomeId)
         && biomesEqualOrMesaPlateau(i2, targetBiomeId)
         && biomesEqualOrMesaPlateau(i3, targetBiomeId)
         && biomesEqualOrMesaPlateau(i4, targetBiomeId)) {
         outputBiomes[x + y * width] = biomeId;
      } else {
         outputBiomes[x + y * width] = replacementBiomeId;
      }

      return true;
   }

   public GenLayerBiomeEdge(long l, GenLayer genLayer) {
      super(l);
      this.parent = genLayer;
   }

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] intArray = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
      int[] intArray1 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int index = 0; index < areaHeight; index++) {
         for (int innerIndex = 0; innerIndex < areaWidth; innerIndex++) {
            this.initChunkSeed(innerIndex + areaX, index + areaY);
            int i = intArray[innerIndex + 1 + (index + 1) * (areaWidth + 2)];
            if (!this.replaceBiomeEdgeIfNecessary(
                  intArray, intArray1, innerIndex, index, areaWidth, i, BiomeGenBase.extremeHills.biomeID, BiomeGenBase.extremeHillsEdge.biomeID
               )
               && !this.replaceBiomeEdge(
                  intArray, intArray1, innerIndex, index, areaWidth, i, BiomeGenBase.mesaPlateau_F.biomeID, BiomeGenBase.mesa.biomeID
               )
               && !this.replaceBiomeEdge(
                  intArray, intArray1, innerIndex, index, areaWidth, i, BiomeGenBase.mesaPlateau.biomeID, BiomeGenBase.mesa.biomeID
               )
               && !this.replaceBiomeEdge(
                  intArray, intArray1, innerIndex, index, areaWidth, i, BiomeGenBase.megaTaiga.biomeID, BiomeGenBase.taiga.biomeID
               )) {
               if (i == BiomeGenBase.desert.biomeID) {
                  int i2 = intArray[innerIndex + 1 + (index + 1 - 1) * (areaWidth + 2)];
                  int i3 = intArray[innerIndex + 1 + 1 + (index + 1) * (areaWidth + 2)];
                  int i4 = intArray[innerIndex + 1 - 1 + (index + 1) * (areaWidth + 2)];
                  int i5 = intArray[innerIndex + 1 + (index + 1 + 1) * (areaWidth + 2)];
                  if (i2 != BiomeGenBase.icePlains.biomeID
                     && i3 != BiomeGenBase.icePlains.biomeID
                     && i4 != BiomeGenBase.icePlains.biomeID
                     && i5 != BiomeGenBase.icePlains.biomeID) {
                     intArray1[innerIndex + index * areaWidth] = i;
                  } else {
                     intArray1[innerIndex + index * areaWidth] = BiomeGenBase.extremeHillsPlus.biomeID;
                  }
               } else if (i == BiomeGenBase.swampland.biomeID) {
                  int i6 = intArray[innerIndex + 1 + (index + 1 - 1) * (areaWidth + 2)];
                  int i7 = intArray[innerIndex + 1 + 1 + (index + 1) * (areaWidth + 2)];
                  int i8 = intArray[innerIndex + 1 - 1 + (index + 1) * (areaWidth + 2)];
                  int i9 = intArray[innerIndex + 1 + (index + 1 + 1) * (areaWidth + 2)];
                  if (i6 == BiomeGenBase.desert.biomeID
                     || i7 == BiomeGenBase.desert.biomeID
                     || i8 == BiomeGenBase.desert.biomeID
                     || i9 == BiomeGenBase.desert.biomeID
                     || i6 == BiomeGenBase.coldTaiga.biomeID
                     || i7 == BiomeGenBase.coldTaiga.biomeID
                     || i8 == BiomeGenBase.coldTaiga.biomeID
                     || i9 == BiomeGenBase.coldTaiga.biomeID
                     || i6 == BiomeGenBase.icePlains.biomeID
                     || i7 == BiomeGenBase.icePlains.biomeID
                     || i8 == BiomeGenBase.icePlains.biomeID
                     || i9 == BiomeGenBase.icePlains.biomeID) {
                     intArray1[innerIndex + index * areaWidth] = BiomeGenBase.plains.biomeID;
                  } else if (i6 != BiomeGenBase.jungle.biomeID
                     && i9 != BiomeGenBase.jungle.biomeID
                     && i7 != BiomeGenBase.jungle.biomeID
                     && i8 != BiomeGenBase.jungle.biomeID) {
                     intArray1[innerIndex + index * areaWidth] = i;
                  } else {
                     intArray1[innerIndex + index * areaWidth] = BiomeGenBase.jungleEdge.biomeID;
                  }
               } else {
                  intArray1[innerIndex + index * areaWidth] = i;
               }
            }
         }
      }

      return intArray1;
   }

   private boolean canBiomesBeNeighbors(int biomeIdA, int biomeIdB) {
      if (biomesEqualOrMesaPlateau(biomeIdA, biomeIdB)) {
         return true;
      } else {
         BiomeGenBase biomeGenBase = BiomeGenBase.getBiome(biomeIdA);
         BiomeGenBase biomeGenBase2 = BiomeGenBase.getBiome(biomeIdB);
         if (biomeGenBase != null && biomeGenBase2 != null) {
            net.minecraft.world.biome.BiomeGenBase.TempCategory biomegenbase$tempcategory = biomeGenBase.getTempCategory();
            net.minecraft.world.biome.BiomeGenBase.TempCategory biomegenbase$tempcategory1 = biomeGenBase2.getTempCategory();
            return biomegenbase$tempcategory == biomegenbase$tempcategory1
               || biomegenbase$tempcategory == net.minecraft.world.biome.BiomeGenBase.TempCategory.MEDIUM
               || biomegenbase$tempcategory1 == net.minecraft.world.biome.BiomeGenBase.TempCategory.MEDIUM;
         } else {
            return false;
         }
      }
   }

   private boolean replaceBiomeEdgeIfNecessary(
      int[] parentBiomes, int[] outputBiomes, int x, int y, int width, int biomeId, int targetBiomeId, int replacementBiomeId
   ) {
      if (!biomesEqualOrMesaPlateau(biomeId, targetBiomeId)) {
         return false;
      }

      int i = parentBiomes[x + 1 + (y + 1 - 1) * (width + 2)];
      int i2 = parentBiomes[x + 1 + 1 + (y + 1) * (width + 2)];
      int i3 = parentBiomes[x + 1 - 1 + (y + 1) * (width + 2)];
      int i4 = parentBiomes[x + 1 + (y + 1 + 1) * (width + 2)];
      if (this.canBiomesBeNeighbors(i, targetBiomeId)
         && this.canBiomesBeNeighbors(i2, targetBiomeId)
         && this.canBiomesBeNeighbors(i3, targetBiomeId)
         && this.canBiomesBeNeighbors(i4, targetBiomeId)) {
         outputBiomes[x + y * width] = biomeId;
      } else {
         outputBiomes[x + y * width] = replacementBiomeId;
      }

      return true;
   }
}
