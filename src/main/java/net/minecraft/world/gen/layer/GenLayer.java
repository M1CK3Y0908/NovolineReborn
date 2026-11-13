package net.minecraft.world.gen.layer;

import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.ChunkProviderSettings;

public abstract class GenLayer {
   protected long baseSeed;
   protected GenLayer parent;
   private long worldGenSeed;
   private long chunkSeed;

   public abstract int[] getInts(int i, int i2, int i3, int i4);

   public void initWorldGenSeed(long seed) {
      this.worldGenSeed = seed;
      if (this.parent != null) {
         this.parent.initWorldGenSeed(seed);
      }

      this.worldGenSeed = this.worldGenSeed * (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
      this.worldGenSeed = this.worldGenSeed + this.baseSeed;
      this.worldGenSeed = this.worldGenSeed * (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
      this.worldGenSeed = this.worldGenSeed + this.baseSeed;
      this.worldGenSeed = this.worldGenSeed * (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
      this.worldGenSeed = this.worldGenSeed + this.baseSeed;
   }

   protected int nextInt(int bound) {
      int i = (int)((this.chunkSeed >> 24) % bound);
      if (i < 0) {
         i += bound;
      }

      this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
      this.chunkSeed = this.chunkSeed + this.worldGenSeed;
      return i;
   }

   public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType worldType, String options) {
      GenLayerIsland genLayerIsland = new GenLayerIsland(1L);
      GenLayerFuzzyZoom genLayerFuzzyZoom = new GenLayerFuzzyZoom(2000L, genLayerIsland);
      GenLayerAddIsland genLayerAddIsland = new GenLayerAddIsland(1L, genLayerFuzzyZoom);
      GenLayerZoom genLayerZoom = new GenLayerZoom(2001L, genLayerAddIsland);
      GenLayerAddIsland genLayerAddIsland2 = new GenLayerAddIsland(2L, genLayerZoom);
      genLayerAddIsland2 = new GenLayerAddIsland(50L, genLayerAddIsland2);
      genLayerAddIsland2 = new GenLayerAddIsland(70L, genLayerAddIsland2);
      GenLayerRemoveTooMuchOcean genLayerRemoveTooMuchOcean = new GenLayerRemoveTooMuchOcean(2L, genLayerAddIsland2);
      GenLayerAddSnow genLayerAddSnow = new GenLayerAddSnow(2L, genLayerRemoveTooMuchOcean);
      GenLayerAddIsland genLayerAddIsland3 = new GenLayerAddIsland(3L, genLayerAddSnow);
      GenLayerEdge genLayerEdge = new GenLayerEdge(2L, genLayerAddIsland3, GenLayerEdge.Mode.COOL_WARM);
      genLayerEdge = new GenLayerEdge(2L, genLayerEdge, GenLayerEdge.Mode.HEAT_ICE);
      genLayerEdge = new GenLayerEdge(3L, genLayerEdge, GenLayerEdge.Mode.SPECIAL);
      GenLayerZoom genLayerZoom2 = new GenLayerZoom(2002L, genLayerEdge);
      genLayerZoom2 = new GenLayerZoom(2003L, genLayerZoom2);
      GenLayerAddIsland genLayerAddIsland4 = new GenLayerAddIsland(4L, genLayerZoom2);
      GenLayerAddMushroomIsland genLayerAddMushroomIsland = new GenLayerAddMushroomIsland(5L, genLayerAddIsland4);
      GenLayer genLayer = GenLayerZoom.createGenLayer(new GenLayerDeepOcean(4L, genLayerAddMushroomIsland), 0);
      int i = 4;
      int i2 = 4;
      if (worldType == WorldType.CUSTOMIZED && options.length() > 0) {
         ChunkProviderSettings chunkProviderSettings = net.minecraft.world.gen.ChunkProviderSettings.Factory.jsonToFactory(options).build();
         i = chunkProviderSettings.biomeSize;
         i2 = chunkProviderSettings.riverSize;
      }

      if (worldType == WorldType.LARGE_BIOMES) {
         i = 6;
      }

      GenLayer genLayer2 = GenLayerZoom.createGenLayer(genLayer, 0);
      GenLayerRiverInit genLayerRiverInit = new GenLayerRiverInit(100L, genLayer2);
      GenLayer genLayer3 = GenLayerZoom.createGenLayer(new GenLayerBiome(200L, genLayer, worldType, options), 2);
      GenLayerBiomeEdge genLayerBiomeEdge = new GenLayerBiomeEdge(1000L, genLayer3);
      GenLayer genLayer4 = GenLayerZoom.createGenLayer(genLayerRiverInit, 2);
      GenLayer genLayer5 = new GenLayerHills(1000L, genLayerBiomeEdge, genLayer4);
      GenLayer genLayer6 = GenLayerZoom.createGenLayer(genLayerRiverInit, 2);
      genLayer6 = GenLayerZoom.createGenLayer(genLayer6, i2);
      GenLayerRiver genLayerRiver = new GenLayerRiver(1L, genLayer6);
      GenLayerSmooth genLayerSmooth = new GenLayerSmooth(1000L, genLayerRiver);
      genLayer5 = new GenLayerRareBiome(1001L, genLayer5);

      for (int i3 = 0; i3 < i; i3++) {
         genLayer5 = new GenLayerZoom(1000 + i3, genLayer5);
         if (i3 == 0) {
            genLayer5 = new GenLayerAddIsland(3L, genLayer5);
         }

         if (i3 == 1 || i == 1) {
            genLayer5 = new GenLayerShore(1000L, genLayer5);
         }
      }

      GenLayerSmooth genLayerSmooth2 = new GenLayerSmooth(1000L, genLayer5);
      GenLayerRiverMix genLayerRiverMix = new GenLayerRiverMix(100L, genLayerSmooth2, genLayerSmooth);
      GenLayerVoronoiZoom genLayerVoronoiZoom = new GenLayerVoronoiZoom(10L, genLayerRiverMix);
      genLayerRiverMix.initWorldGenSeed(seed);
      genLayerVoronoiZoom.initWorldGenSeed(seed);
      return new GenLayer[]{genLayerRiverMix, genLayerVoronoiZoom, genLayerRiverMix};
   }

   protected int selectModeOrRandom(int value1, int value2, int value3, int value4) {
      return value2 == value3 && value3 == value4
         ? value2
         : (
            value1 == value2 && value1 == value3
               ? value1
               : (
                  value1 == value2 && value1 == value4
                     ? value1
                     : (
                        value1 == value3 && value1 == value4
                           ? value1
                           : (
                              value1 == value2 && value3 != value4
                                 ? value1
                                 : (
                                    value1 == value3 && value2 != value4
                                       ? value1
                                       : (
                                          value1 == value4 && value2 != value3
                                             ? value1
                                             : (
                                                value2 == value3 && value1 != value4
                                                   ? value2
                                                   : (
                                                      value2 == value4 && value1 != value3
                                                         ? value2
                                                         : (value3 == value4 && value1 != value2 ? value3 : this.selectRandom(value1, value2, value3, value4))
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

   protected int selectRandom(int... values) {
      return values[this.nextInt(values.length)];
   }

   public GenLayer(long seed) {
      this.baseSeed = seed;
      this.baseSeed = this.baseSeed * (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
      this.baseSeed += seed;
      this.baseSeed = this.baseSeed * (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
      this.baseSeed += seed;
      this.baseSeed = this.baseSeed * (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
      this.baseSeed += seed;
   }

   protected static boolean isBiomeOceanic(int biomeId) {
      return biomeId == BiomeGenBase.ocean.biomeID || biomeId == BiomeGenBase.deepOcean.biomeID || biomeId == BiomeGenBase.frozenOcean.biomeID;
   }

   protected static boolean biomesEqualOrMesaPlateau(int biomeIDA, int biomeIDB) {
      if (biomeIDA == biomeIDB) {
         return true;
      }

      if (biomeIDA != BiomeGenBase.mesaPlateau_F.biomeID && biomeIDA != BiomeGenBase.mesaPlateau.biomeID) {
         final BiomeGenBase biome2 = BiomeGenBase.getBiome(biomeIDA);
         final BiomeGenBase biome3 = BiomeGenBase.getBiome(biomeIDB);

         try {
            return biome2 != null && biome3 != null ? biome2.isEqualTo(biome3) : false;
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Comparing biomes");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Biomes being compared");
            crashReportCategory.addCrashSection("Biome A ID", biomeIDA);
            crashReportCategory.addCrashSection("Biome B ID", biomeIDB);
            crashReportCategory.addCrashSectionCallable("Biome A", new Callable<String>() {
               public String call() throws Exception {
                  return String.valueOf(biome2);
               }
            });
            crashReportCategory.addCrashSectionCallable("Biome B", new Callable<String>() {
               public String call() throws Exception {
                  return String.valueOf(biome3);
               }
            });
            throw new ReportedException(crashReport);
         }
      } else {
         return biomeIDB == BiomeGenBase.mesaPlateau_F.biomeID || biomeIDB == BiomeGenBase.mesaPlateau.biomeID;
      }
   }

   public void initChunkSeed(long x, long y) {
      this.chunkSeed = this.worldGenSeed;
      this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
      this.chunkSeed += x;
      this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
      this.chunkSeed += y;
      this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
      this.chunkSeed += x;
      this.chunkSeed = this.chunkSeed * (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
      this.chunkSeed += y;
   }
}
