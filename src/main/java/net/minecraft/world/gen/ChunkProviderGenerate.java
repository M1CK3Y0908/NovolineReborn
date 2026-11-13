package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;

public class ChunkProviderGenerate implements IChunkProvider {
   private Block oceanBlockTmpl = Blocks.water;
   private double[] stoneNoise = new double[256];
   private MapGenBase caveGenerator = new MapGenCaves();
   private MapGenStronghold strongholdGenerator = new MapGenStronghold();
   private MapGenVillage villageGenerator = new MapGenVillage();
   private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
   double[] doubleArray;
   private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();
   private MapGenBase ravineGenerator = new MapGenRavine();
   private StructureOceanMonument oceanMonumentGenerator = new StructureOceanMonument();
   private World worldObj;
   private final boolean mapFeaturesEnabled;
   private WorldType terrainType;
   private Random rand;
   private NoiseGeneratorOctaves lowerLimitNoiseGenerator;
   private NoiseGeneratorOctaves upperLimitNoiseGenerator;
   private BiomeGenBase[] biomesForGeneration;
   private NoiseGeneratorOctaves mainNoiseGenerator;
   private NoiseGeneratorPerlin surfaceNoiseGenerator;
   double[] doubleArray2;
   public NoiseGeneratorOctaves noiseGen5;
   double[] doubleArray3;
   public NoiseGeneratorOctaves noiseGen6;
   public NoiseGeneratorOctaves mobSpawnerNoise;
   private final double[] heightMap;
   private final float[] parabolicField;
   double[] doubleArray4;
   private ChunkProviderSettings settings;

   @Override
   public boolean unloadQueuedChunks() {
      return false;
   }

   @Override
   public boolean canSave() {
      return true;
   }

   public void replaceBlocksForBiome(int x, int z, ChunkPrimer primer, BiomeGenBase[] biomeGens) {
      double d = 0.03125;
      this.stoneNoise = this.surfaceNoiseGenerator.getArray(this.stoneNoise, x * 16, z * 16, d * 2.0, d * 2.0, 1.0);

      for (int i = 0; i < 16; i++) {
         for (int i2 = 0; i2 < 16; i2++) {
            biomeGens[i2 + i * 16]
               .genTerrainBlocks(this.worldObj, this.rand, primer, x * 16 + i, z * 16 + i2, this.stoneNoise[i2 + i * 16]);
         }
      }
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      this.rand.setSeed(x * 341873128712L + z * 132897987541L);
      ChunkPrimer chunkPrimer = new ChunkPrimer();
      this.setBlocksInChunk(x, z, chunkPrimer);
      this.biomesForGeneration = this.worldObj.getWorldChunkManager().getArray3(this.biomesForGeneration, x * 16, z * 16);
      this.replaceBlocksForBiome(x, z, chunkPrimer, this.biomesForGeneration);
      if (this.settings.useCaves) {
         this.caveGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      if (this.settings.useRavines) {
         this.ravineGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
         this.mineshaftGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      if (this.settings.useVillages && this.mapFeaturesEnabled) {
         this.villageGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
         this.strongholdGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      if (this.settings.useTemples && this.mapFeaturesEnabled) {
         this.scatteredFeatureGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      if (this.settings.useMonuments && this.mapFeaturesEnabled) {
         this.oceanMonumentGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      Chunk chunk2 = new Chunk(this.worldObj, chunkPrimer, x, z);
      byte[] byteArray = chunk2.getBiomeArray();

      for (int index = 0; index < byteArray.length; index++) {
         byteArray[index] = (byte)this.biomesForGeneration[index].biomeID;
      }

      chunk2.generateSkylightMap();
      return chunk2;
   }

   private void handleInt(int i, int i2) {
      this.doubleArray3 = this.noiseGen6
         .generateNoiseOctaves(
            this.doubleArray3, i, i2, 5, 5, this.settings.depthNoiseScaleX, this.settings.depthNoiseScaleZ, this.settings.depthNoiseScaleExponent
         );
      float coordinateScale2 = this.settings.coordinateScale;
      float heightScale2 = this.settings.heightScale;
      this.doubleArray2 = this.mainNoiseGenerator
         .generateNoiseOctaves(
            this.doubleArray2,
            i,
            0,
            i2,
            5,
            33,
            5,
            coordinateScale2 / this.settings.mainNoiseScaleX,
            heightScale2 / this.settings.mainNoiseScaleY,
            coordinateScale2 / this.settings.mainNoiseScaleZ
         );
      this.doubleArray4 = this.lowerLimitNoiseGenerator.generateNoiseOctaves(this.doubleArray4, i, 0, i2, 5, 33, 5, coordinateScale2, heightScale2, coordinateScale2);
      this.doubleArray = this.upperLimitNoiseGenerator.generateNoiseOctaves(this.doubleArray, i, 0, i2, 5, 33, 5, coordinateScale2, heightScale2, coordinateScale2);
      int i3 = 0;
      int i4 = 0;

      for (int i5 = 0; i5 < 5; i5++) {
         for (int i6 = 0; i6 < 5; i6++) {
            float f = 0.0F;
            float f2 = 0.0F;
            float f3 = 0.0F;
            BiomeGenBase biomeGenBase2 = this.biomesForGeneration[i5 + 2 + (i6 + 2) * 10];

            for (int i7 = -2; i7 <= 2; i7++) {
               for (int i8 = -2; i8 <= 2; i8++) {
                  BiomeGenBase biomeGenBase3 = this.biomesForGeneration[i5 + i7 + 2 + (i6 + i8 + 2) * 10];
                  float f4 = this.settings.biomeDepthOffSet + biomeGenBase3.minHeight * this.settings.biomeDepthWeight;
                  float f5 = this.settings.biomeScaleOffset + biomeGenBase3.maxHeight * this.settings.biomeScaleWeight;
                  if (this.terrainType == WorldType.AMPLIFIED && f4 > 0.0F) {
                     f4 = 1.0F + f4 * 2.0F;
                     f5 = 1.0F + f5 * 4.0F;
                  }

                  float f6 = this.parabolicField[i7 + 2 + (i8 + 2) * 5] / (f4 + 2.0F);
                  if (biomeGenBase3.minHeight > biomeGenBase2.minHeight) {
                     f6 /= 2.0F;
                  }

                  f += f5 * f6;
                  f2 += f4 * f6;
                  f3 += f6;
               }
            }

            f /= f3;
            f2 /= f3;
            f = f * 0.9F + 0.1F;
            f2 = (f2 * 4.0F - 1.0F) / 8.0F;
            double d = this.doubleArray3[i4] / 8000.0;
            if (d < 0.0) {
               d = -d * 0.3;
            }

            d = d * 3.0 - 2.0;
            if (d < 0.0) {
               d /= 2.0;
               if (d < -1.0) {
                  d = -1.0;
               }

               d /= 1.4;
               d /= 2.0;
            } else {
               if (d > 1.0) {
                  d = 1.0;
               }

               d /= 8.0;
            }

            i4++;
            double d2 = f2;
            double d3 = f;
            d2 += d * 0.2;
            d2 = d2 * this.settings.baseSize / 8.0;
            double d4 = this.settings.baseSize + d2 * 4.0;

            for (int i9 = 0; i9 < 33; i9++) {
               double d5 = (i9 - d4) * this.settings.stretchY * 128.0 / 256.0 / d3;
               if (d5 < 0.0) {
                  d5 *= 4.0;
               }

               double d6 = this.doubleArray4[i3] / this.settings.lowerLimitScale;
               double d7 = this.doubleArray[i3] / this.settings.upperLimitScale;
               double d8 = (this.doubleArray2[i3] / 10.0 + 1.0) / 2.0;
               double d9 = MathHelper.denormalizeClamp(d6, d7, d8) - d5;
               if (i9 > 29) {
                  double d10 = (i9 - 29) / 3.0F;
                  d9 = d9 * (1.0 - d10) + -10.0 * d10;
               }

               this.heightMap[i3] = d9;
               i3++;
            }
         }
      }
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }

   @Override
   public void saveExtraData() {
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      return "Stronghold".equals(structureName) && this.strongholdGenerator != null
         ? this.strongholdGenerator.getClosestStrongholdPos(worldIn, position)
         : null;
   }

   public void setBlocksInChunk(int x, int z, ChunkPrimer primer) {
      this.biomesForGeneration = this.worldObj.getWorldChunkManager().getArray5(this.biomesForGeneration, x * 4 - 2, z * 4 - 2);
      this.handleInt(x * 4, z * 4);

      for (int index = 0; index < 4; index++) {
         int i = index * 5;
         int i2 = (index + 1) * 5;

         for (int outerIndex = 0; outerIndex < 4; outerIndex++) {
            int i3 = (i + outerIndex) * 33;
            int i4 = (i + outerIndex + 1) * 33;
            int i5 = (i2 + outerIndex) * 33;
            int i6 = (i2 + outerIndex + 1) * 33;

            for (int index2 = 0; index2 < 32; index2++) {
               double d = 0.125;
               double d2 = this.heightMap[i3 + index2];
               double d3 = this.heightMap[i4 + index2];
               double d4 = this.heightMap[i5 + index2];
               double d5 = this.heightMap[i6 + index2];
               double d6 = (this.heightMap[i3 + index2 + 1] - d2) * d;
               double d7 = (this.heightMap[i4 + index2 + 1] - d3) * d;
               double d8 = (this.heightMap[i5 + index2 + 1] - d4) * d;
               double d9 = (this.heightMap[i6 + index2 + 1] - d5) * d;

               for (int index3 = 0; index3 < 8; index3++) {
                  double d10 = 0.25;
                  double d11 = d2;
                  double d12 = d3;
                  double d13 = (d4 - d2) * d10;
                  double d14 = (d5 - d3) * d10;

                  for (int index4 = 0; index4 < 4; index4++) {
                     double d15 = 0.25;
                     double d16 = (d12 - d11) * d15;
                     double d17 = d11 - d16;

                     for (int index5 = 0; index5 < 4; index5++) {
                        if ((d17 += d16) > 0.0) {
                           primer.setBlockState(index * 4 + index4, index2 * 8 + index3, outerIndex * 4 + index5, Blocks.stone.getDefaultState());
                        } else if (index2 * 8 + index3 < this.settings.seaLevel) {
                           primer.setBlockState(index * 4 + index4, index2 * 8 + index3, outerIndex * 4 + index5, this.oceanBlockTmpl.getDefaultState());
                        }
                     }

                     d11 += d13;
                     d12 += d14;
                  }

                  d2 += d6;
                  d3 += d7;
                  d4 += d8;
                  d5 += d9;
               }
            }
         }
      }
   }

   @Override
   public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
      boolean conditionMet = false;
      if (this.settings.useMonuments && this.mapFeaturesEnabled && chunkIn.getInhabitedTime() < 3600L) {
         conditionMet = false | this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, new ChunkCoordIntPair(x, z));
      }

      return conditionMet;
   }

   @Override
   public String makeString() {
      return "RandomLevelSource";
   }

   @Override
   public int getLoadedChunkCount() {
      return 0;
   }

   public ChunkProviderGenerate(World worldIn, long seed, boolean generateStructures, String structuresJson) {
      this.worldObj = worldIn;
      this.mapFeaturesEnabled = generateStructures;
      this.terrainType = worldIn.getWorldInfo().getTerrainType();
      this.rand = new Random(seed);
      this.lowerLimitNoiseGenerator = new NoiseGeneratorOctaves(this.rand, 16);
      this.upperLimitNoiseGenerator = new NoiseGeneratorOctaves(this.rand, 16);
      this.mainNoiseGenerator = new NoiseGeneratorOctaves(this.rand, 8);
      this.surfaceNoiseGenerator = new NoiseGeneratorPerlin(this.rand, 4);
      this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
      this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
      this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
      this.heightMap = new double[825];
      this.parabolicField = new float[25];

      for (int index = -2; index <= 2; index++) {
         for (int innerIndex = -2; innerIndex <= 2; innerIndex++) {
            float f = 10.0F / MathHelper.sqrt_float(index * index + innerIndex * innerIndex + 0.2F);
            this.parabolicField[index + 2 + (innerIndex + 2) * 5] = f;
         }
      }

      if (structuresJson != null) {
         this.settings = ChunkProviderSettings.Factory.jsonToFactory(structuresJson).build();
         this.oceanBlockTmpl = this.settings.useLavaOceans ? Blocks.lava : Blocks.water;
         worldIn.setSeaLevel(this.settings.seaLevel);
      }
   }

   @Override
   public boolean checkIProgressUpdate(IProgressUpdate iProgressUpdate) {
      return true;
   }

   @Override
   public boolean chunkExists(int x, int z) {
      return true;
   }

   @Override
   public void recreateStructures(Chunk chunkIn, int x, int z) {
      if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
         this.mineshaftGenerator.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
      }

      if (this.settings.useVillages && this.mapFeaturesEnabled) {
         this.villageGenerator.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
      }

      if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
         this.strongholdGenerator.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
      }

      if (this.settings.useTemples && this.mapFeaturesEnabled) {
         this.scatteredFeatureGenerator.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
      }

      if (this.settings.useMonuments && this.mapFeaturesEnabled) {
         this.oceanMonumentGenerator.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
      }
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
      BlockFalling.fallInstantly = true;
      int i = x * 16;
      int i2 = z * 16;
      BlockPos blockPos = new BlockPos(i, 0, i2);
      BiomeGenBase biomeGenForCoords = this.worldObj.getBiomeGenForCoords(blockPos.add(16, 0, 16));
      this.rand.setSeed(this.worldObj.getSeed());
      long l = this.rand.nextLong() / 2L * 2L + 1L;
      long l2 = this.rand.nextLong() / 2L * 2L + 1L;
      this.rand.setSeed(x * l + z * l2 ^ this.worldObj.getSeed());
      boolean flag = false;
      ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(x, z);
      if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
         this.mineshaftGenerator.generateStructure(this.worldObj, this.rand, chunkCoordIntPair);
      }

      if (this.settings.useVillages && this.mapFeaturesEnabled) {
         flag = this.villageGenerator.generateStructure(this.worldObj, this.rand, chunkCoordIntPair);
      }

      if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
         this.strongholdGenerator.generateStructure(this.worldObj, this.rand, chunkCoordIntPair);
      }

      if (this.settings.useTemples && this.mapFeaturesEnabled) {
         this.scatteredFeatureGenerator.generateStructure(this.worldObj, this.rand, chunkCoordIntPair);
      }

      if (this.settings.useMonuments && this.mapFeaturesEnabled) {
         this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, chunkCoordIntPair);
      }

      if (biomeGenForCoords != BiomeGenBase.desert
         && biomeGenForCoords != BiomeGenBase.desertHills
         && this.settings.useWaterLakes
         && !flag
         && this.rand.nextInt(this.settings.waterLakeChance) == 0) {
         int i3 = this.rand.nextInt(16) + 8;
         int i4 = this.rand.nextInt(256);
         int i5 = this.rand.nextInt(16) + 8;
         new WorldGenLakes(Blocks.water).generate(this.worldObj, this.rand, blockPos.add(i3, i4, i5));
      }

      if (!flag && this.rand.nextInt(this.settings.lavaLakeChance / 10) == 0 && this.settings.useLavaLakes) {
         int i6 = this.rand.nextInt(16) + 8;
         int i7 = this.rand.nextInt(this.rand.nextInt(248) + 8);
         int i8 = this.rand.nextInt(16) + 8;
         if (i7 < this.worldObj.getSeaLevel() || this.rand.nextInt(this.settings.lavaLakeChance / 8) == 0) {
            new WorldGenLakes(Blocks.lava).generate(this.worldObj, this.rand, blockPos.add(i6, i7, i8));
         }
      }

      if (this.settings.useDungeons) {
         for (int i9 = 0; i9 < this.settings.dungeonChance; i9++) {
            int i10 = this.rand.nextInt(16) + 8;
            int i11 = this.rand.nextInt(256);
            int i12 = this.rand.nextInt(16) + 8;
            new WorldGenDungeons().generate(this.worldObj, this.rand, blockPos.add(i10, i11, i12));
         }
      }

      biomeGenForCoords.decorate(this.worldObj, this.rand, new BlockPos(i, 0, i2));
      SpawnerAnimals.handleWorld(this.worldObj, biomeGenForCoords, i + 8, i2 + 8, this.rand);
      blockPos = blockPos.add(8, 0, 8);

      for (int i13 = 0; i13 < 16; i13++) {
         for (int i14 = 0; i14 < 16; i14++) {
            BlockPos precipitationHeight = this.worldObj.getPrecipitationHeight(blockPos.add(i13, 0, i14));
            BlockPos blockPos2 = precipitationHeight.down();
            if (this.worldObj.canBlockFreezeWater(blockPos2)) {
               this.worldObj.setBlockState(blockPos2, Blocks.ice.getDefaultState(), 2);
            }

            if (this.worldObj.canSnowAt(precipitationHeight, true)) {
               this.worldObj.setBlockState(precipitationHeight, Blocks.snow_layer.getDefaultState(), 2);
            }
         }
      }

      BlockFalling.fallInstantly = false;
   }

   @Override
   public List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      BiomeGenBase biomeGenBase = this.worldObj.getBiomeGenForCoords(pos);
      if (this.mapFeaturesEnabled) {
         if (creatureType == EnumCreatureType.MONSTER && this.scatteredFeatureGenerator.isSwampHut(pos)) {
            return this.scatteredFeatureGenerator.getScatteredFeatureSpawnList();
         }

         if (creatureType == EnumCreatureType.MONSTER && this.settings.useMonuments && this.oceanMonumentGenerator.isPositionInStructure(this.worldObj, pos)) {
            return this.oceanMonumentGenerator.getScatteredFeatureSpawnList();
         }
      }

      return biomeGenBase.getSpawnableList(creatureType);
   }
}
