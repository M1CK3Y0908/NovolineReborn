package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenMesa extends BiomeGenBase {
   private boolean brycePillars;
   private NoiseGeneratorPerlin brycePillarNoise;
   private long worldSeed;
   private boolean forest2;
   private IBlockState[] clayBands;
   private NoiseGeneratorPerlin clayBandOffsetNoise;
   private NoiseGeneratorPerlin brycePillarRoofNoise;

   @Override
   public int getFoliageColorAtPos(BlockPos pos) {
      return 10387789;
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      super.decorate(worldIn, rand, pos);
   }

   @Override
   protected BiomeGenBase createMutatedBiome(int id) {
      boolean flag = this.biomeID == BiomeGenBase.mesa.biomeID;
      BiomeGenMesa biomeGenMesa = new BiomeGenMesa(id, flag, this.forest2);
      if (!flag) {
         biomeGenMesa.setHeight(height_LowHills);
         biomeGenMesa.setBiomeName(this.biomeName + " M");
      } else {
         biomeGenMesa.setBiomeName(this.biomeName + " (Bryce)");
      }

      biomeGenMesa.setBiomeColor(this.color, true);
      return biomeGenMesa;
   }

   private void generateBands(long seed) {
      this.clayBands = new IBlockState[64];
      Arrays.fill(this.clayBands, Blocks.hardened_clay.getDefaultState());
      Random random = new Random(seed);
      this.clayBandOffsetNoise = new NoiseGeneratorPerlin(random, 1);

      for (int i = 0; i < 64; i++) {
         i += random.nextInt(5) + 1;
         if (i < 64) {
            this.clayBands[i] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);
         }
      }

      int i2 = random.nextInt(4) + 2;

      for (int i3 = 0; i3 < i2; i3++) {
         int i4 = random.nextInt(3) + 1;
         int i5 = random.nextInt(64);

         for (int i6 = 0; i5 + i6 < 64 && i6 < i4; i6++) {
            this.clayBands[i5 + i6] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW);
         }
      }

      int i7 = random.nextInt(4) + 2;

      for (int i8 = 0; i8 < i7; i8++) {
         int i9 = random.nextInt(3) + 2;
         int i10 = random.nextInt(64);

         for (int i11 = 0; i10 + i11 < 64 && i11 < i9; i11++) {
            this.clayBands[i10 + i11] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BROWN);
         }
      }

      int i12 = random.nextInt(4) + 2;

      for (int i13 = 0; i13 < i12; i13++) {
         int i14 = random.nextInt(3) + 1;
         int i15 = random.nextInt(64);

         for (int i16 = 0; i15 + i16 < 64 && i16 < i14; i16++) {
            this.clayBands[i15 + i16] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED);
         }
      }

      int i17 = random.nextInt(3) + 3;
      int i18 = 0;

      for (int i19 = 0; i19 < i17; i19++) {
         i18 += random.nextInt(16) + 4;

         for (int i20 = 0; i18 + i20 < 64 && i20 < 1; i20++) {
            this.clayBands[i18 + i20] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE);
            if (i18 + i20 > 1 && random.nextBoolean()) {
               this.clayBands[i18 + i20 - 1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
            }

            if (i18 + i20 < 63 && random.nextBoolean()) {
               this.clayBands[i18 + i20 + 1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
            }
         }
      }
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return this.worldGeneratorTrees;
   }

   private IBlockState getIBlockState(int i, int i2) {
      int i3 = (int)Math.round(this.clayBandOffsetNoise.getValue(i * 1.0 / 512.0, i * 1.0 / 512.0) * 2.0);
      return this.clayBands[(i2 + i3 + 64) % 64];
   }

   @Override
   public int getGrassColorAtPos(BlockPos pos) {
      return 9470285;
   }

   @Override
   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      if (this.clayBands == null || this.worldSeed != worldIn.getSeed()) {
         this.generateBands(worldIn.getSeed());
      }

      if (this.brycePillarNoise == null || this.brycePillarRoofNoise == null || this.worldSeed != worldIn.getSeed()) {
         Random random = new Random(this.worldSeed);
         this.brycePillarNoise = new NoiseGeneratorPerlin(random, 4);
         this.brycePillarRoofNoise = new NoiseGeneratorPerlin(random, 1);
      }

      this.worldSeed = worldIn.getSeed();
      double d = 0.0;
      if (this.brycePillars) {
         int i = (x & -16) + (z & 15);
         int i2 = (z & -16) + (x & 15);
         double d2 = Math.min(Math.abs(noiseVal), this.brycePillarNoise.getValue(i * 0.25, i2 * 0.25));
         if (d2 > 0.0) {
            double d3 = 0.001953125;
            double d4 = Math.abs(this.brycePillarRoofNoise.getValue(i * d3, i2 * d3));
            d = d2 * d2 * 2.5;
            double d5 = Math.ceil(d4 * 50.0) + 14.0;
            if (d > d5) {
               d = d5;
            }

            d += 64.0;
         }
      }

      int i3 = x & 15;
      int i4 = z & 15;
      int seaLevel = worldIn.getSeaLevel();
      IBlockState defaultState = Blocks.stained_hardened_clay.getDefaultState();
      IBlockState fillerBlock2 = this.fillerBlock;
      int i5 = (int)(noiseVal / 3.0 + 3.0 + rand.nextDouble() * 0.25);
      boolean flag = Math.cos(noiseVal / 3.0 * Math.PI) > 0.0;
      int i6 = -1;
      boolean flag2 = false;

      for (int i7 = 255; i7 >= 0; i7--) {
         if (chunkPrimerIn.getBlockState(i4, i7, i3).getBlock().getMaterial() == Material.air && i7 < (int)d) {
            chunkPrimerIn.setBlockState(i4, i7, i3, Blocks.stone.getDefaultState());
         }

         if (i7 <= rand.nextInt(5)) {
            chunkPrimerIn.setBlockState(i4, i7, i3, Blocks.bedrock.getDefaultState());
         } else {
            IBlockState blockState = chunkPrimerIn.getBlockState(i4, i7, i3);
            if (blockState.getBlock().getMaterial() == Material.air) {
               i6 = -1;
            } else if (blockState.getBlock() == Blocks.stone) {
               if (i6 == -1) {
                  flag2 = false;
                  if (i5 <= 0) {
                     defaultState = null;
                     fillerBlock2 = Blocks.stone.getDefaultState();
                  } else if (i7 >= seaLevel - 4 && i7 <= seaLevel + 1) {
                     defaultState = Blocks.stained_hardened_clay.getDefaultState();
                     fillerBlock2 = this.fillerBlock;
                  }

                  if (i7 < seaLevel && (defaultState == null || defaultState.getBlock().getMaterial() == Material.air)) {
                     defaultState = Blocks.water.getDefaultState();
                  }

                  i6 = i5 + Math.max(0, i7 - seaLevel);
                  if (i7 < seaLevel - 1) {
                     chunkPrimerIn.setBlockState(i4, i7, i3, fillerBlock2);
                     if (fillerBlock2.getBlock() == Blocks.stained_hardened_clay) {
                        chunkPrimerIn.setBlockState(
                           i4, i7, i3, fillerBlock2.getBlock().getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE)
                        );
                     }
                  } else if (!this.forest2 || i7 <= 86 + i5 * 2) {
                     if (i7 <= seaLevel + 3 + i5) {
                        chunkPrimerIn.setBlockState(i4, i7, i3, this.topBlock);
                        flag2 = true;
                     } else {
                        IBlockState iBlockState;
                        if (i7 < 64 || i7 > 127) {
                           iBlockState = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);
                        } else if (flag) {
                           iBlockState = Blocks.hardened_clay.getDefaultState();
                        } else {
                           iBlockState = this.getIBlockState(x, i7);
                        }

                        chunkPrimerIn.setBlockState(i4, i7, i3, iBlockState);
                     }
                  } else if (flag) {
                     chunkPrimerIn.setBlockState(
                        i4, i7, i3, Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, net.minecraft.block.BlockDirt.DirtType.COARSE_DIRT)
                     );
                  } else {
                     chunkPrimerIn.setBlockState(i4, i7, i3, Blocks.grass.getDefaultState());
                  }
               } else if (i6 > 0) {
                  i6--;
                  if (flag2) {
                     chunkPrimerIn.setBlockState(
                        i4, i7, i3, Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE)
                     );
                  } else {
                     IBlockState iBlockState2 = this.getIBlockState(x, i7);
                     chunkPrimerIn.setBlockState(i4, i7, i3, iBlockState2);
                  }
               }
            }
         }
      }
   }

   public BiomeGenMesa(int id, boolean brycePillars, boolean forest) {
      super(id);
      this.brycePillars = brycePillars;
      this.forest2 = forest;
      this.setDisableRain();
      this.setTemperatureRainfall(2.0F, 0.0F);
      this.spawnableCreatureList.clear();
      this.topBlock = Blocks.sand.getDefaultState().withProperty(BlockSand.VARIANT, net.minecraft.block.BlockSand.EnumType.RED_SAND);
      this.fillerBlock = Blocks.stained_hardened_clay.getDefaultState();
      this.theBiomeDecorator.treesPerChunk = -999;
      this.theBiomeDecorator.deadBushPerChunk = 20;
      this.theBiomeDecorator.reedsPerChunk = 3;
      this.theBiomeDecorator.cactiPerChunk = 5;
      this.theBiomeDecorator.flowersPerChunk = 0;
      this.spawnableCreatureList.clear();
      if (forest) {
         this.theBiomeDecorator.treesPerChunk = 5;
      }
   }
}
