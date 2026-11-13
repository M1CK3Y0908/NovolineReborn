package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenHills extends BiomeGenBase {
   private WorldGenerator theWorldGenerator = new WorldGenMinable(
      Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, net.minecraft.block.BlockSilverfish.EnumType.STONE), 9
   );
   private WorldGenTaiga2 taigaTreeGenerator = new WorldGenTaiga2(false);
   private int defaultHillsType = 0;
   private int extraTreesType = 1;
   private int mutatedHillsType = 2;
   private int hillsType = this.defaultHillsType;

   @Override
   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      this.topBlock = Blocks.grass.getDefaultState();
      this.fillerBlock = Blocks.dirt.getDefaultState();
      if ((noiseVal < -1.0 || noiseVal > 2.0) && this.hillsType == this.mutatedHillsType) {
         this.topBlock = Blocks.gravel.getDefaultState();
         this.fillerBlock = Blocks.gravel.getDefaultState();
      } else if (noiseVal > 1.0 && this.hillsType != this.extraTreesType) {
         this.topBlock = Blocks.stone.getDefaultState();
         this.fillerBlock = Blocks.stone.getDefaultState();
      }

      this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
   }

   @Override
   protected BiomeGenBase createMutatedBiome(int id) {
      return new BiomeGenHills(id, false).mutateHills(this);
   }

   private BiomeGenHills mutateHills(BiomeGenBase baseBiome) {
      this.hillsType = this.mutatedHillsType;
      this.setBiomeColor(baseBiome.color, true);
      this.setBiomeName(baseBiome.biomeName + " M");
      this.setHeight(new BiomeGenBase.Height(baseBiome.minHeight, baseBiome.maxHeight));
      this.setTemperatureRainfall(baseBiome.temperature, baseBiome.rainfall);
      return this;
   }

   protected BiomeGenHills(int id, boolean extraTrees) {
      super(id);
      if (extraTrees) {
         this.theBiomeDecorator.treesPerChunk = 3;
         this.hillsType = this.extraTreesType;
      }
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      super.decorate(worldIn, rand, pos);
      int i = 3 + rand.nextInt(6);

      for (int i2 = 0; i2 < i; i2++) {
         int i3 = rand.nextInt(16);
         int i4 = rand.nextInt(28) + 4;
         int i5 = rand.nextInt(16);
         BlockPos blockPos = pos.add(i3, i4, i5);
         if (worldIn.getBlockState(blockPos).getBlock() == Blocks.stone) {
            worldIn.setBlockState(blockPos, Blocks.emerald_ore.getDefaultState(), 2);
         }
      }

      for (int i6 = 0; i6 < 7; i6++) {
         int i7 = rand.nextInt(16);
         int i8 = rand.nextInt(64);
         int i9 = rand.nextInt(16);
         this.theWorldGenerator.generate(worldIn, rand, pos.add(i7, i8, i9));
      }
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return rand.nextInt(3) > 0 ? this.taigaTreeGenerator : super.genBigTreeChance(rand);
   }
}
