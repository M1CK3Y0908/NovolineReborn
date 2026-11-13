package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRiverMix extends GenLayer {
   private GenLayer biomePatternGeneratorChain;
   private GenLayer riverPatternGeneratorChain;

   @Override
   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      int[] intArray = this.biomePatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
      int[] intArray1 = this.riverPatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
      int[] intArray2 = IntCache.getIntCache(areaWidth * areaHeight);

      for (int index = 0; index < areaWidth * areaHeight; index++) {
         if (intArray[index] == BiomeGenBase.ocean.biomeID || intArray[index] == BiomeGenBase.deepOcean.biomeID) {
            intArray2[index] = intArray[index];
         } else if (intArray1[index] == BiomeGenBase.river.biomeID) {
            if (intArray[index] == BiomeGenBase.icePlains.biomeID) {
               intArray2[index] = BiomeGenBase.frozenRiver.biomeID;
            } else if (intArray[index] != BiomeGenBase.mushroomIsland.biomeID && intArray[index] != BiomeGenBase.mushroomIslandShore.biomeID) {
               intArray2[index] = intArray1[index] & 0xFF;
            } else {
               intArray2[index] = BiomeGenBase.mushroomIslandShore.biomeID;
            }
         } else {
            intArray2[index] = intArray[index];
         }
      }

      return intArray2;
   }

   public GenLayerRiverMix(long l, GenLayer genLayer, GenLayer genLayer2) {
      super(l);
      this.biomePatternGeneratorChain = genLayer;
      this.riverPatternGeneratorChain = genLayer2;
   }

   @Override
   public void initWorldGenSeed(long seed) {
      this.biomePatternGeneratorChain.initWorldGenSeed(seed);
      this.riverPatternGeneratorChain.initWorldGenSeed(seed);
      super.initWorldGenSeed(seed);
   }
}
