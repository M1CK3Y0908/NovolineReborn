package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenSwamp extends BiomeGenBase {
   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return this.worldGeneratorSwamp;
   }

   @Override
   public int getGrassColorAtPos(BlockPos pos) {
      double xCoordinate = GRASS_COLOR_NOISE.getValue(pos.getX() * 0.0225, pos.getZ() * 0.0225);
      return xCoordinate < -0.1 ? 5011004 : 6975545;
   }

   @Override
   public net.minecraft.block.BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
      return net.minecraft.block.BlockFlower.EnumFlowerType.BLUE_ORCHID;
   }

   protected BiomeGenSwamp(int id) {
      super(id);
      this.theBiomeDecorator.treesPerChunk = 2;
      this.theBiomeDecorator.flowersPerChunk = 1;
      this.theBiomeDecorator.deadBushPerChunk = 1;
      this.theBiomeDecorator.mushroomsPerChunk = 8;
      this.theBiomeDecorator.reedsPerChunk = 10;
      this.theBiomeDecorator.clayPerChunk = 1;
      this.theBiomeDecorator.waterlilyPerChunk = 4;
      this.theBiomeDecorator.sandPerChunk2 = 0;
      this.theBiomeDecorator.sandPerChunk = 0;
      this.theBiomeDecorator.grassPerChunk = 5;
      this.waterColorMultiplier = 14745518;
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 1, 1, 1));
   }

   @Override
   public int getFoliageColorAtPos(BlockPos pos) {
      return 6975545;
   }

   @Override
   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      double value = GRASS_COLOR_NOISE.getValue(x * 0.25, z * 0.25);
      if (value > 0.0) {
         int i = x & 15;
         int i2 = z & 15;

         for (int nestedIndex = 255; nestedIndex >= 0; nestedIndex--) {
            if (chunkPrimerIn.getBlockState(i2, nestedIndex, i).getBlock().getMaterial() != Material.air) {
               if (nestedIndex == 62 && chunkPrimerIn.getBlockState(i2, nestedIndex, i).getBlock() != Blocks.water) {
                  chunkPrimerIn.setBlockState(i2, nestedIndex, i, Blocks.water.getDefaultState());
                  if (value < 0.12) {
                     chunkPrimerIn.setBlockState(i2, nestedIndex + 1, i, Blocks.waterlily.getDefaultState());
                  }
               }
               break;
            }
         }
      }

      this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
   }
}
