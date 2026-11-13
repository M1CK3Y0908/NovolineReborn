package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenGlowStone2;
import net.minecraft.world.gen.feature.WorldGenHellLava;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenNetherBridge;

public class ChunkProviderHell implements IChunkProvider {
   private double[] slowsandNoise = new double[256];
   private double[] gravelNoise = new double[256];
   private double[] netherrackExclusivityNoise = new double[256];
   private final WorldGenFire fireGenerator = new WorldGenFire();
   private final WorldGenGlowStone1 glowstoneClusterGenerator = new WorldGenGlowStone1();
   private double[] noiseField;
   private final WorldGenGlowStone2 glowstoneGenerator = new WorldGenGlowStone2();
   private final WorldGenerator quartzOreGenerator = new WorldGenMinable(Blocks.quartz_ore.getDefaultState(), 14, BlockHelper.forBlock(Blocks.netherrack));
   private final WorldGenHellLava lavaPocketGenerator = new WorldGenHellLava(Blocks.flowing_lava, true);
   private final WorldGenHellLava lavaFallGenerator = new WorldGenHellLava(Blocks.flowing_lava, false);
   double[] doubleArray;
   private final GeneratorBushFeature brownMushroomGenerator = new GeneratorBushFeature(Blocks.brown_mushroom);
   private final GeneratorBushFeature redMushroomGenerator = new GeneratorBushFeature(Blocks.red_mushroom);
   private final MapGenNetherBridge genNetherBridge = new MapGenNetherBridge();
   double[] doubleArray2;
   double[] doubleArray3;
   double[] doubleArray4;
   private final MapGenBase netherCaveGenerator = new MapGenCavesHell();
   double[] doubleArray5;
   private final World worldObj;
   private final boolean mapFeaturesEnabled;
   private final Random hellRNG;
   private final NoiseGeneratorOctaves netherNoiseGen1;
   private final NoiseGeneratorOctaves netherNoiseGen2;
   private final NoiseGeneratorOctaves netherNoiseGen3;
   private final NoiseGeneratorOctaves slowsandGravelNoiseGen;
   private final NoiseGeneratorOctaves netherrackExculsivityNoiseGen;
   public final NoiseGeneratorOctaves netherNoiseGen6;
   public final NoiseGeneratorOctaves netherNoiseGen7;

   @Override
   public boolean checkIProgressUpdate(IProgressUpdate iProgressUpdate) {
      return true;
   }

   public void replaceBiomeBlocks(int chunkX, int chunkZ, ChunkPrimer primer) {
      int i = this.worldObj.getSeaLevel() + 1;
      double d = 0.03125;
      this.slowsandNoise = this.slowsandGravelNoiseGen
         .generateNoiseOctaves(this.slowsandNoise, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, d, d, 1.0);
      this.gravelNoise = this.slowsandGravelNoiseGen
         .generateNoiseOctaves(this.gravelNoise, chunkX * 16, 109, chunkZ * 16, 16, 1, 16, d, 1.0, d);
      this.netherrackExclusivityNoise = this.netherrackExculsivityNoiseGen
         .generateNoiseOctaves(this.netherrackExclusivityNoise, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, d * 2.0, d * 2.0, d * 2.0);

      for (int innerIndex = 0; innerIndex < 16; innerIndex++) {
         for (int nestedIndex = 0; nestedIndex < 16; nestedIndex++) {
            boolean flag = this.slowsandNoise[innerIndex + nestedIndex * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
            boolean flag2 = this.gravelNoise[innerIndex + nestedIndex * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
            int i2 = (int)(this.netherrackExclusivityNoise[innerIndex + nestedIndex * 16] / 3.0 + 3.0 + this.hellRNG.nextDouble() * 0.25);
            int i3 = -1;
            IBlockState blockState = Blocks.netherrack.getDefaultState();
            IBlockState blockState2 = Blocks.netherrack.getDefaultState();

            for (int innerIndex2 = 127; innerIndex2 >= 0; innerIndex2--) {
               if (innerIndex2 < 127 - this.hellRNG.nextInt(5) && innerIndex2 > this.hellRNG.nextInt(5)) {
                  IBlockState blockState3 = primer.getBlockState(nestedIndex, innerIndex2, innerIndex);
                  if (blockState3.getBlock() == null || blockState3.getBlock().getMaterial() == Material.air) {
                     i3 = -1;
                  } else if (blockState3.getBlock() == Blocks.netherrack) {
                     if (i3 == -1) {
                        if (i2 <= 0) {
                           blockState = null;
                           blockState2 = Blocks.netherrack.getDefaultState();
                        } else if (innerIndex2 >= i - 4 && innerIndex2 <= i + 1) {
                           blockState = Blocks.netherrack.getDefaultState();
                           blockState2 = Blocks.netherrack.getDefaultState();
                           if (flag2) {
                              blockState = Blocks.gravel.getDefaultState();
                              blockState2 = Blocks.netherrack.getDefaultState();
                           }

                           if (flag) {
                              blockState = Blocks.soul_sand.getDefaultState();
                              blockState2 = Blocks.soul_sand.getDefaultState();
                           }
                        }

                        if (innerIndex2 < i && (blockState == null || blockState.getBlock().getMaterial() == Material.air)) {
                           blockState = Blocks.lava.getDefaultState();
                        }

                        i3 = i2;
                        if (innerIndex2 >= i - 1) {
                           primer.setBlockState(nestedIndex, innerIndex2, innerIndex, blockState);
                        } else {
                           primer.setBlockState(nestedIndex, innerIndex2, innerIndex, blockState2);
                        }
                     } else if (i3 > 0) {
                        i3--;
                        primer.setBlockState(nestedIndex, innerIndex2, innerIndex, blockState2);
                     }
                  }
               } else {
                  primer.setBlockState(nestedIndex, innerIndex2, innerIndex, Blocks.bedrock.getDefaultState());
               }
            }
         }
      }
   }

   @Override
   public List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      if (creatureType == EnumCreatureType.MONSTER) {
         if (this.genNetherBridge.isInsideStructure(pos)) {
            return this.genNetherBridge.getSpawnList();
         }

         if (this.genNetherBridge.isPositionInStructure(this.worldObj, pos) && this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.nether_brick) {
            return this.genNetherBridge.getSpawnList();
         }
      }

      return this.worldObj.getBiomeGenForCoords(pos).getSpawnableList(creatureType);
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      this.hellRNG.setSeed(x * 341873128712L + z * 132897987541L);
      ChunkPrimer chunkPrimer = new ChunkPrimer();
      this.setBlocksInChunk(x, z, chunkPrimer);
      this.replaceBiomeBlocks(x, z, chunkPrimer);
      this.netherCaveGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      if (this.mapFeaturesEnabled) {
         this.genNetherBridge.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      Chunk chunk2 = new Chunk(this.worldObj, chunkPrimer, x, z);
      BiomeGenBase[] abiomegenbase = this.worldObj.getWorldChunkManager().getArray3((BiomeGenBase[])null, x * 16, z * 16);
      byte[] byteArray = chunk2.getBiomeArray();

      for (int index = 0; index < byteArray.length; index++) {
         byteArray[index] = (byte)abiomegenbase[index].biomeID;
      }

      chunk2.resetRelightChecks();
      return chunk2;
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      return null;
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
      BlockFalling.fallInstantly = true;
      BlockPos blockPos = new BlockPos(x * 16, 0, z * 16);
      ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(x, z);
      this.genNetherBridge.generateStructure(this.worldObj, this.hellRNG, chunkCoordIntPair);

      for (int index = 0; index < 8; index++) {
         this.lavaFallGenerator
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
      }

      for (int innerIndex = 0; innerIndex < this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1) + 1; innerIndex++) {
         this.fireGenerator
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
      }

      for (int nestedIndex = 0; nestedIndex < this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1); nestedIndex++) {
         this.glowstoneClusterGenerator
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
      }

      for (int outerIndex = 0; outerIndex < 10; outerIndex++) {
         this.glowstoneGenerator
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
      }

      if (this.hellRNG.nextBoolean()) {
         this.brownMushroomGenerator
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
      }

      if (this.hellRNG.nextBoolean()) {
         this.redMushroomGenerator
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
      }

      for (int index2 = 0; index2 < 16; index2++) {
         this.quartzOreGenerator
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16), this.hellRNG.nextInt(108) + 10, this.hellRNG.nextInt(16)));
      }

      for (int innerIndex2 = 0; innerIndex2 < 16; innerIndex2++) {
         this.lavaPocketGenerator
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16), this.hellRNG.nextInt(108) + 10, this.hellRNG.nextInt(16)));
      }

      BlockFalling.fallInstantly = false;
   }

   public void setBlocksInChunk(int chunkX, int chunkZ, ChunkPrimer primer) {
      int outerIndex = this.worldObj.getSeaLevel() / 2 + 1;
      this.noiseField = this.getArray(this.noiseField, chunkX * 4, chunkZ * 4);

      for (int i = 0; i < 4; i++) {
         for (int i2 = 0; i2 < 4; i2++) {
            for (int i3 = 0; i3 < 16; i3++) {
               double d = 0.125;
               double d2 = this.noiseField[((i + 0) * 5 + i2 + 0) * 17 + i3 + 0];
               double d3 = this.noiseField[((i + 0) * 5 + i2 + 1) * 17 + i3 + 0];
               double d4 = this.noiseField[((i + 1) * 5 + i2 + 0) * 17 + i3 + 0];
               double d5 = this.noiseField[((i + 1) * 5 + i2 + 1) * 17 + i3 + 0];
               double d6 = (this.noiseField[((i + 0) * 5 + i2 + 0) * 17 + i3 + 1] - d2) * d;
               double d7 = (this.noiseField[((i + 0) * 5 + i2 + 1) * 17 + i3 + 1] - d3) * d;
               double d8 = (this.noiseField[((i + 1) * 5 + i2 + 0) * 17 + i3 + 1] - d4) * d;
               double d9 = (this.noiseField[((i + 1) * 5 + i2 + 1) * 17 + i3 + 1] - d5) * d;

               for (int i4 = 0; i4 < 8; i4++) {
                  double d10 = 0.25;
                  double d11 = d2;
                  double d12 = d3;
                  double d13 = (d4 - d2) * d10;
                  double d14 = (d5 - d3) * d10;

                  for (int i5 = 0; i5 < 4; i5++) {
                     double d15 = 0.25;
                     double d16 = d11;
                     double d17 = (d12 - d11) * d15;

                     for (int i6 = 0; i6 < 4; i6++) {
                        IBlockState iBlockState = null;
                        if (i3 * 8 + i4 < outerIndex) {
                           iBlockState = Blocks.lava.getDefaultState();
                        }

                        if (d16 > 0.0) {
                           iBlockState = Blocks.netherrack.getDefaultState();
                        }

                        int i7 = i5 + i * 4;
                        int i8 = i4 + i3 * 8;
                        int i9 = i6 + i2 * 4;
                        primer.setBlockState(i7, i8, i9, iBlockState);
                        d16 += d17;
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
   public String makeString() {
      return "HellRandomLevelSource";
   }

   public ChunkProviderHell(World worldIn, boolean mapFeaturesEnabled, long seed) {
      this.worldObj = worldIn;
      this.mapFeaturesEnabled = mapFeaturesEnabled;
      this.hellRNG = new Random(seed);
      this.netherNoiseGen1 = new NoiseGeneratorOctaves(this.hellRNG, 16);
      this.netherNoiseGen2 = new NoiseGeneratorOctaves(this.hellRNG, 16);
      this.netherNoiseGen3 = new NoiseGeneratorOctaves(this.hellRNG, 8);
      this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
      this.netherrackExculsivityNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
      this.netherNoiseGen6 = new NoiseGeneratorOctaves(this.hellRNG, 10);
      this.netherNoiseGen7 = new NoiseGeneratorOctaves(this.hellRNG, 16);
      worldIn.setSeaLevel(63);
   }

   @Override
   public boolean unloadQueuedChunks() {
      return false;
   }

   @Override
   public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
      return false;
   }

   @Override
   public void saveExtraData() {
   }

   private double[] getArray(double[] dArray, int i, int i2) {
      if (dArray == null) {
         dArray = new double[425];
      }

      double d = 684.412;
      double d2 = 2053.236;
      this.doubleArray2 = this.netherNoiseGen6.generateNoiseOctaves(this.doubleArray2, i, 0, i2, 5, 1, 5, 1.0, 0.0, 1.0);
      this.doubleArray3 = this.netherNoiseGen7.generateNoiseOctaves(this.doubleArray3, i, 0, i2, 5, 1, 5, 100.0, 0.0, 100.0);
      this.doubleArray = this.netherNoiseGen3.generateNoiseOctaves(this.doubleArray, i, 0, i2, 5, 17, 5, d / 80.0, d2 / 60.0, d / 80.0);
      this.doubleArray5 = this.netherNoiseGen1.generateNoiseOctaves(this.doubleArray5, i, 0, i2, 5, 17, 5, d, d2, d);
      this.doubleArray4 = this.netherNoiseGen2.generateNoiseOctaves(this.doubleArray4, i, 0, i2, 5, 17, 5, d, d2, d);
      int i3 = 0;
      double[] dArray2 = new double[17];

      for (int i4 = 0; i4 < 17; i4++) {
         dArray2[i4] = Math.cos(i4 * Math.PI * 6.0 / 17) * 2.0;
         double d3 = i4;
         if (i4 > 8) {
            d3 = 16 - i4;
         }

         if (d3 < 4.0) {
            d3 = 4.0 - d3;
            dArray2[i4] -= d3 * d3 * d3 * 10.0;
         }
      }

      for (int i5 = 0; i5 < 5; i5++) {
         for (int i6 = 0; i6 < 5; i6++) {
            double d4 = 0.0;

            for (int i7 = 0; i7 < 17; i7++) {
               double d5 = dArray2[i7];
               double d6 = this.doubleArray5[i3] / 512.0;
               double d7 = this.doubleArray4[i3] / 512.0;
               double d8 = (this.doubleArray[i3] / 10.0 + 1.0) / 2.0;
               double d9;
               if (d8 < 0.0) {
                  d9 = d6;
               } else if (d8 > 1.0) {
                  d9 = d7;
               } else {
                  d9 = d6 + (d7 - d6) * d8;
               }

               d9 -= d5;
               if (i7 > 13) {
                  double d10 = (i7 - 13) / 3.0F;
                  d9 = d9 * (1.0 - d10) + -10.0 * d10;
               }

               if (i7 < d4) {
                  double d11 = (d4 - i7) / 4.0;
                  d11 = MathHelper.clamp_double(d11, 0.0, 1.0);
                  d9 = d9 * (1.0 - d11) + -10.0 * d11;
               }

               dArray[i3] = d9;
               i3++;
            }
         }
      }

      return dArray;
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }

   @Override
   public void recreateStructures(Chunk chunkIn, int x, int z) {
      this.genNetherBridge.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
   }

   @Override
   public int getLoadedChunkCount() {
      return 0;
   }

   @Override
   public boolean chunkExists(int x, int z) {
      return true;
   }
}
