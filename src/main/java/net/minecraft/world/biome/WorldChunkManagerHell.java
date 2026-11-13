package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.util.BlockPos;

public class WorldChunkManagerHell extends WorldChunkManager {
   private BiomeGenBase biomeGenerator;
   private float rainfall;

   @Override
   public BiomeGenBase[] getArray(BiomeGenBase[] biomeGenBases, int i, int i2, boolean flag) {
      return this.getArray3(biomeGenBases, i, i2);
   }

   public WorldChunkManagerHell(BiomeGenBase biomeGenBase, float f) {
      this.biomeGenerator = biomeGenBase;
      this.rainfall = f;
   }

   @Override
   public boolean areBiomesViable(int x, int z, int radius, List<BiomeGenBase> allowedBiomes) {
      return allowedBiomes.contains(this.biomeGenerator);
   }

   @Override
   public BiomeGenBase[] getArray5(BiomeGenBase[] biomeGenBases, int i, int i2) {
      if (biomeGenBases == null || biomeGenBases.length < 100) {
         biomeGenBases = new BiomeGenBase[100];
      }

      Arrays.fill(biomeGenBases, 0, 100, this.biomeGenerator);
      return biomeGenBases;
   }

   @Override
   public BiomeGenBase getBiomeGenerator(BlockPos pos) {
      return this.biomeGenerator;
   }

   @Override
   public BlockPos findBiomePosition(int x, int z, int range, List<BiomeGenBase> biomes, Random random) {
      return biomes.contains(this.biomeGenerator)
         ? new BlockPos(x - range + random.nextInt(range * 2 + 1), 0, z - range + random.nextInt(range * 2 + 1))
         : null;
   }

   @Override
   public BiomeGenBase[] getArray3(BiomeGenBase[] biomeGenBases, int i, int i2) {
      if (biomeGenBases == null || biomeGenBases.length < 256) {
         biomeGenBases = new BiomeGenBase[256];
      }

      Arrays.fill(biomeGenBases, 0, 256, this.biomeGenerator);
      return biomeGenBases;
   }

   @Override
   public float[] getArray2(float[] fArray, int i, int i2) {
      if (fArray == null || fArray.length < 256) {
         fArray = new float[256];
      }

      Arrays.fill(fArray, 0, 256, this.rainfall);
      return fArray;
   }
}
