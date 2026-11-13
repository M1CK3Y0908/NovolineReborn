package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class WorldChunkManager {
   private BiomeCache biomeCache = new BiomeCache(this);
   private String generatorOptions = "";
   private GenLayer biomeIndexLayer;
   private GenLayer genBiomes;
   private List<BiomeGenBase> biomesToSpawnIn = Lists.newArrayList();

   public void cleanupCache() {
      this.biomeCache.cleanupCache();
   }

   public float getFloat(float f) {
      return f;
   }

   public BiomeGenBase getBiomeGenerator(BlockPos pos, BiomeGenBase biomeGenBaseIn) {
      return this.biomeCache.getBiomeAt(pos.getX(), pos.getZ(), biomeGenBaseIn);
   }

   public float[] getArray2(float[] fArray, int i, int i2) {
      IntCache.resetIntCache();
      if (fArray == null || fArray.length < 256) {
         fArray = new float[256];
      }

      int[] ints = this.biomeIndexLayer.getInts(i, i2, 16, 16);

      for (int i3 = 0; i3 < 256; i3++) {
         try {
            float f = BiomeGenBase.getBiomeFromBiomeList(ints[i3], BiomeGenBase.DEFAULT_BIOME).getIntRainfall() / 65536.0F;
            if (f > 1.0F) {
               f = 1.0F;
            }

            fArray[i3] = f;
         } catch (Throwable throwable) {
            CrashReport crashReport2 = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashReportCategory2 = crashReport2.makeCategory("DownfallBlock");
            crashReportCategory2.addCrashSection("biome id", i3);
            crashReportCategory2.addCrashSection("downfalls[] size", fArray.length);
            crashReportCategory2.addCrashSection("x", i);
            crashReportCategory2.addCrashSection("z", i2);
            crashReportCategory2.addCrashSection("w", 16);
            crashReportCategory2.addCrashSection("h", 16);
            throw new ReportedException(crashReport2);
         }
      }

      return fArray;
   }

   public BlockPos findBiomePosition(int x, int z, int range, List<BiomeGenBase> biomes, Random random) {
      IntCache.resetIntCache();
      int i = x - range >> 2;
      int i2 = z - range >> 2;
      int i3 = x + range >> 2;
      int i4 = z + range >> 2;
      int i5 = i3 - i + 1;
      int i6 = i4 - i2 + 1;
      int[] intArray = this.genBiomes.getInts(i, i2, i5, i6);
      BlockPos blockPos = null;
      int i7 = 0;

      for (int outerIndex = 0; outerIndex < i5 * i6; outerIndex++) {
         int i8 = i + outerIndex % i5 << 2;
         int i9 = i2 + outerIndex / i5 << 2;
         BiomeGenBase biomeGenBase = BiomeGenBase.getBiome(intArray[outerIndex]);
         if (biomes.contains(biomeGenBase) && (blockPos == null || random.nextInt(i7 + 1) == 0)) {
            blockPos = new BlockPos(i8, 0, i9);
            i7++;
         }
      }

      return blockPos;
   }

   public BiomeGenBase[] getArray(BiomeGenBase[] biomeGenBases, int i, int i2, boolean flag) {
      IntCache.resetIntCache();
      if (biomeGenBases == null || biomeGenBases.length < 256) {
         biomeGenBases = new BiomeGenBase[256];
      }

      if (flag && (i & 15) == 0 && (i2 & 15) == 0) {
         BiomeGenBase[] cachedBiomes = this.biomeCache.getCachedBiomes(i, i2);
         System.arraycopy(cachedBiomes, 0, biomeGenBases, 0, 256);
         return biomeGenBases;
      }

      int[] ints = this.biomeIndexLayer.getInts(i, i2, 16, 16);

      for (int i3 = 0; i3 < 256; i3++) {
         biomeGenBases[i3] = BiomeGenBase.getBiomeFromBiomeList(ints[i3], BiomeGenBase.DEFAULT_BIOME);
      }

      return biomeGenBases;
   }

   public boolean areBiomesViable(int x, int z, int radius, List<BiomeGenBase> allowedBiomes) {
      IntCache.resetIntCache();
      int i = x - radius >> 2;
      int i2 = z - radius >> 2;
      int i3 = x + radius >> 2;
      int i4 = z + radius >> 2;
      int i5 = i3 - i + 1;
      int i6 = i4 - i2 + 1;
      int[] intArray = this.genBiomes.getInts(i, i2, i5, i6);

      try {
         for (int nestedIndex = 0; nestedIndex < i5 * i6; nestedIndex++) {
            BiomeGenBase biomeGenBase = BiomeGenBase.getBiome(intArray[nestedIndex]);
            if (!allowedBiomes.contains(biomeGenBase)) {
               return false;
            }
         }

         return true;
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Layer");
         crashReportCategory.addCrashSection("Layer", this.genBiomes.toString());
         crashReportCategory.addCrashSection("x", x);
         crashReportCategory.addCrashSection("z", z);
         crashReportCategory.addCrashSection("radius", radius);
         crashReportCategory.addCrashSection("allowed", allowedBiomes);
         throw new ReportedException(crashReport);
      }
   }

   public WorldChunkManager(World worldIn) {
      this(worldIn.getSeed(), worldIn.getWorldInfo().getTerrainType(), worldIn.getWorldInfo().getGeneratorOptions());
   }

   public BiomeGenBase[] getArray5(BiomeGenBase[] biomeGenBases, int i, int i2) {
      IntCache.resetIntCache();
      if (biomeGenBases == null || biomeGenBases.length < 100) {
         biomeGenBases = new BiomeGenBase[100];
      }

      int[] ints = this.genBiomes.getInts(i, i2, 10, 10);

      try {
         for (int i3 = 0; i3 < 100; i3++) {
            biomeGenBases[i3] = BiomeGenBase.getBiomeFromBiomeList(ints[i3], BiomeGenBase.DEFAULT_BIOME);
         }

         return biomeGenBases;
      } catch (Throwable throwable) {
         CrashReport crashReport2 = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
         CrashReportCategory crashReportCategory2 = crashReport2.makeCategory("RawBiomeBlock");
         crashReportCategory2.addCrashSection("biomes[] size", biomeGenBases.length);
         crashReportCategory2.addCrashSection("x", i);
         crashReportCategory2.addCrashSection("z", i2);
         crashReportCategory2.addCrashSection("w", 10);
         crashReportCategory2.addCrashSection("h", 10);
         throw new ReportedException(crashReport2);
      }
   }

   protected WorldChunkManager() {
      this.biomesToSpawnIn.add(BiomeGenBase.forest);
      this.biomesToSpawnIn.add(BiomeGenBase.plains);
      this.biomesToSpawnIn.add(BiomeGenBase.taiga);
      this.biomesToSpawnIn.add(BiomeGenBase.taigaHills);
      this.biomesToSpawnIn.add(BiomeGenBase.forestHills);
      this.biomesToSpawnIn.add(BiomeGenBase.jungle);
      this.biomesToSpawnIn.add(BiomeGenBase.jungleHills);
   }

   public List<BiomeGenBase> getBiomesToSpawnIn() {
      return this.biomesToSpawnIn;
   }

   public WorldChunkManager(long seed, WorldType worldTypeIn, String options) {
      this();
      this.generatorOptions = options;
      GenLayer[] agenlayer = GenLayer.initializeAllBiomeGenerators(seed, worldTypeIn, options);
      this.genBiomes = agenlayer[0];
      this.biomeIndexLayer = agenlayer[1];
   }

   public BiomeGenBase getBiomeGenerator(BlockPos pos) {
      return this.getBiomeGenerator(pos, (BiomeGenBase)null);
   }

   public BiomeGenBase[] getArray3(BiomeGenBase[] biomeGenBases, int i, int i2) {
      return this.getArray(biomeGenBases, i, i2, true);
   }
}
