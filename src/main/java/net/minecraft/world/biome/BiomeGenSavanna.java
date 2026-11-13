package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;

public class BiomeGenSavanna extends BiomeGenBase {
   private static final WorldGenSavannaTree SAVANNA_TREE = new WorldGenSavannaTree(false);

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return rand.nextInt(5) > 0 ? SAVANNA_TREE : this.worldGeneratorTrees;
   }

   protected BiomeGenSavanna(int id) {
      super(id);
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityHorse.class, 1, 2, 6));
      this.theBiomeDecorator.treesPerChunk = 1;
      this.theBiomeDecorator.flowersPerChunk = 4;
      this.theBiomeDecorator.grassPerChunk = 20;
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      DOUBLE_PLANT_GENERATOR.setPlantType(net.minecraft.block.BlockDoublePlant.EnumPlantType.GRASS);

      for (int index = 0; index < 7; index++) {
         int i = rand.nextInt(16) + 8;
         int i2 = rand.nextInt(16) + 8;
         int yCoordinate = rand.nextInt(worldIn.getHeight(pos.add(i, 0, i2)).getY() + 32);
         DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(i, yCoordinate, i2));
      }

      super.decorate(worldIn, rand, pos);
   }

   @Override
   protected BiomeGenBase createMutatedBiome(int id) {
      BiomeGenSavanna.Mutated mutated = new BiomeGenSavanna.Mutated(id, this);
      mutated.temperature = (this.temperature + 1.0F) * 0.5F;
      mutated.minHeight = this.minHeight * 0.5F + 0.3F;
      mutated.maxHeight = this.maxHeight * 0.5F + 1.2F;
      return mutated;
   }

   public static class Mutated extends BiomeGenMutated {
      @Override
      public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
         this.topBlock = Blocks.grass.getDefaultState();
         this.fillerBlock = Blocks.dirt.getDefaultState();
         if (noiseVal > 1.75) {
            this.topBlock = Blocks.stone.getDefaultState();
            this.fillerBlock = Blocks.stone.getDefaultState();
         } else if (noiseVal > -0.5) {
            this.topBlock = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.COARSE_DIRT);
         }

         this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
      }

      public Mutated(int id, BiomeGenBase baseBiome) {
         super(id, baseBiome);
         this.theBiomeDecorator.treesPerChunk = 2;
         this.theBiomeDecorator.flowersPerChunk = 2;
         this.theBiomeDecorator.grassPerChunk = 5;
      }

      @Override
      public void decorate(World worldIn, Random rand, BlockPos pos) {
         this.theBiomeDecorator.decorate(worldIn, rand, this, pos);
      }
   }
}
