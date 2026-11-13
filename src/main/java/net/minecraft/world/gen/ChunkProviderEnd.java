package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderEnd implements IChunkProvider {
   double[] doubleArray;
   private World endWorld;
   double[] doubleArray2;
   double[] doubleArray3;
   double[] doubleArray4;
   double[] doubleArray5;
   private Random endRNG;
   private BiomeGenBase[] biomesForGeneration;
   private NoiseGeneratorOctaves noiseGen1;
   private NoiseGeneratorOctaves noiseGen2;
   private NoiseGeneratorOctaves noiseGen3;
   public NoiseGeneratorOctaves noiseGen4;
   public NoiseGeneratorOctaves noiseGen5;
   private double[] densities;

   @Override
   public void saveExtraData() {
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      return null;
   }

   @Override
   public boolean chunkExists(int x, int z) {
      return true;
   }

   @Override
   public List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      return this.endWorld.getBiomeGenForCoords(pos).getSpawnableList(creatureType);
   }

   public ChunkProviderEnd(World worldIn, long seed) {
      this.endWorld = worldIn;
      this.endRNG = new Random(seed);
      this.noiseGen1 = new NoiseGeneratorOctaves(this.endRNG, 16);
      this.noiseGen2 = new NoiseGeneratorOctaves(this.endRNG, 16);
      this.noiseGen3 = new NoiseGeneratorOctaves(this.endRNG, 8);
      this.noiseGen4 = new NoiseGeneratorOctaves(this.endRNG, 10);
      this.noiseGen5 = new NoiseGeneratorOctaves(this.endRNG, 16);
   }

   public void setBlocksInChunk(int chunkX, int chunkZ, ChunkPrimer primer) {
      this.densities = this.getArray(this.densities, chunkX * 2, chunkZ * 2);

      for (int i = 0; i < 2; i++) {
         for (int i2 = 0; i2 < 2; i2++) {
            for (int i3 = 0; i3 < 32; i3++) {
               double d = 0.25;
               double d2 = this.densities[((i + 0) * 3 + i2 + 0) * 33 + i3 + 0];
               double d3 = this.densities[((i + 0) * 3 + i2 + 1) * 33 + i3 + 0];
               double d4 = this.densities[((i + 1) * 3 + i2 + 0) * 33 + i3 + 0];
               double d5 = this.densities[((i + 1) * 3 + i2 + 1) * 33 + i3 + 0];
               double d6 = (this.densities[((i + 0) * 3 + i2 + 0) * 33 + i3 + 1] - d2) * d;
               double d7 = (this.densities[((i + 0) * 3 + i2 + 1) * 33 + i3 + 1] - d3) * d;
               double d8 = (this.densities[((i + 1) * 3 + i2 + 0) * 33 + i3 + 1] - d4) * d;
               double d9 = (this.densities[((i + 1) * 3 + i2 + 1) * 33 + i3 + 1] - d5) * d;

               for (int i4 = 0; i4 < 4; i4++) {
                  double d10 = 0.125;
                  double d11 = d2;
                  double d12 = d3;
                  double d13 = (d4 - d2) * d10;
                  double d14 = (d5 - d3) * d10;

                  for (int i5 = 0; i5 < 8; i5++) {
                     double d15 = 0.125;
                     double d16 = d11;
                     double d17 = (d12 - d11) * d15;

                     for (int i6 = 0; i6 < 8; i6++) {
                        IBlockState iBlockState = null;
                        if (d16 > 0.0) {
                           iBlockState = Blocks.end_stone.getDefaultState();
                        }

                        int i7 = i5 + i * 8;
                        int i8 = i4 + i3 * 4;
                        int i9 = i6 + i2 * 8;
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
   public Chunk provideChunk(int x, int z) {
      this.endRNG.setSeed(x * 341873128712L + z * 132897987541L);
      ChunkPrimer chunkPrimer = new ChunkPrimer();
      this.biomesForGeneration = this.endWorld.getWorldChunkManager().getArray3(this.biomesForGeneration, x * 16, z * 16);
      this.setBlocksInChunk(x, z, chunkPrimer);
      this.replaceBiomeBlocks(chunkPrimer);
      Chunk chunk2 = new Chunk(this.endWorld, chunkPrimer, x, z);
      byte[] byteArray = chunk2.getBiomeArray();

      for (int index = 0; index < byteArray.length; index++) {
         byteArray[index] = (byte)this.biomesForGeneration[index].biomeID;
      }

      chunk2.generateSkylightMap();
      return chunk2;
   }

   private double[] getArray(double[] dArray, int i, int i2) {
      if (dArray == null) {
         dArray = new double[297];
      }

      double d = 684.412;
      double d2 = 684.412;
      this.doubleArray2 = this.noiseGen4.generateNoiseOctaves(this.doubleArray2, i, i2, 3, 3, 1.121, 1.121, 0.5);
      this.doubleArray5 = this.noiseGen5.generateNoiseOctaves(this.doubleArray5, i, i2, 3, 3, 200.0, 200.0, 0.5);
      d *= 2.0;
      this.doubleArray4 = this.noiseGen3.generateNoiseOctaves(this.doubleArray4, i, 0, i2, 3, 33, 3, d / 80.0, d2 / 160.0, d / 80.0);
      this.doubleArray = this.noiseGen1.generateNoiseOctaves(this.doubleArray, i, 0, i2, 3, 33, 3, d, d2, d);
      this.doubleArray3 = this.noiseGen2.generateNoiseOctaves(this.doubleArray3, i, 0, i2, 3, 33, 3, d, d2, d);
      int i3 = 0;

      for (int i4 = 0; i4 < 3; i4++) {
         for (int i5 = 0; i5 < 3; i5++) {
            float f = (i4 + i) / 1.0F;
            float f2 = (i5 + i2) / 1.0F;
            float f3 = 100.0F - MathHelper.sqrt_float(f * f + f2 * f2) * 8.0F;
            if (f3 > 80.0F) {
               f3 = 80.0F;
            }

            if (f3 < -100.0F) {
               f3 = -100.0F;
            }

            for (int i6 = 0; i6 < 33; i6++) {
               double d3 = this.doubleArray[i3] / 512.0;
               double d4 = this.doubleArray3[i3] / 512.0;
               double d5 = (this.doubleArray4[i3] / 10.0 + 1.0) / 2.0;
               double d6;
               if (d5 < 0.0) {
                  d6 = d3;
               } else if (d5 > 1.0) {
                  d6 = d4;
               } else {
                  d6 = d3 + (d4 - d3) * d5;
               }

               d6 -= 8.0;
               d6 += f3;
               if (i6 > 14) {
                  double d7 = (i6 - 14) / 64.0F;
                  d7 = MathHelper.clamp_double(d7, 0.0, 1.0);
                  d6 = d6 * (1.0 - d7) + -3000.0 * d7;
               }

               if (i6 < 8) {
                  double d8 = (8 - i6) / (8 - 1.0F);
                  d6 = d6 * (1.0 - d8) + -30.0 * d8;
               }

               dArray[i3] = d6;
               i3++;
            }
         }
      }

      return dArray;
   }

   @Override
   public int getLoadedChunkCount() {
      return 0;
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }

   @Override
   public boolean unloadQueuedChunks() {
      return false;
   }

   public void replaceBiomeBlocks(ChunkPrimer primer) {
      for (int index2 = 0; index2 < 16; index2++) {
         for (int i = 0; i < 16; i++) {
            int i2 = -1;
            IBlockState defaultState = Blocks.end_stone.getDefaultState();
            IBlockState defaultState2 = Blocks.end_stone.getDefaultState();

            for (int i3 = 127; i3 >= 0; i3--) {
               IBlockState blockState = primer.getBlockState(index2, i3, i);
               if (blockState.getBlock().getMaterial() == Material.air) {
                  i2 = -1;
               } else if (blockState.getBlock() == Blocks.stone) {
                  if (i2 == -1) {
                     i2 = 1;
                     if (i3 >= 0) {
                        primer.setBlockState(index2, i3, i, defaultState);
                     } else {
                        primer.setBlockState(index2, i3, i, defaultState2);
                     }
                  } else if (i2 > 0) {
                     i2--;
                     primer.setBlockState(index2, i3, i, defaultState2);
                  }
               }
            }
         }
      }
   }

   @Override
   public String makeString() {
      return "RandomLevelSource";
   }

   @Override
   public void recreateStructures(Chunk chunkIn, int x, int z) {
   }

   @Override
   public boolean checkIProgressUpdate(IProgressUpdate iProgressUpdate) {
      return true;
   }

   @Override
   public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
      return false;
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
      BlockFalling.fallInstantly = true;
      BlockPos blockPos = new BlockPos(x * 16, 0, z * 16);
      this.endWorld.getBiomeGenForCoords(blockPos.add(16, 0, 16)).decorate(this.endWorld, this.endWorld.rand, blockPos);
      BlockFalling.fallInstantly = false;
   }

   @Override
   public boolean canSave() {
      return true;
   }
}
