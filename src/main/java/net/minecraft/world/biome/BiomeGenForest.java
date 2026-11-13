package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;

public class BiomeGenForest extends BiomeGenBase {
   protected static final WorldGenForest SUPER_BIRCH_TREE = new WorldGenForest(false, true);
   protected static final WorldGenForest BIRCH_TREE = new WorldGenForest(false, false);
   private int forestType;
   protected static final WorldGenCanopyTree CANOPY_TREE = new WorldGenCanopyTree(false);

   @Override
   protected BiomeGenBase setBiomeColor(int colorIn, boolean darkenMapColor) {
      if (this.forestType == 2) {
         this.mapColor = 353825;
         this.color = colorIn;
         if (darkenMapColor) {
            this.mapColor = (this.mapColor & 16711422) >> 1;
         }

         return this;
      } else {
         return super.setBiomeColor(colorIn, darkenMapColor);
      }
   }

   public BiomeGenForest(int id, int forestType) {
      super(id);
      this.forestType = forestType;
      this.theBiomeDecorator.treesPerChunk = 10;
      this.theBiomeDecorator.grassPerChunk = 2;
      if (this.forestType == 1) {
         this.theBiomeDecorator.treesPerChunk = 6;
         this.theBiomeDecorator.flowersPerChunk = 100;
         this.theBiomeDecorator.grassPerChunk = 1;
      }

      this.setFillerBlockMetadata(5159473);
      this.setTemperatureRainfall(0.7F, 0.8F);
      if (this.forestType == 2) {
         this.mapColor = 353825;
         this.color = 3175492;
         this.setTemperatureRainfall(0.6F, 0.6F);
      }

      if (this.forestType == 0) {
         this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityWolf.class, 5, 4, 4));
      }

      if (this.forestType == 3) {
         this.theBiomeDecorator.treesPerChunk = -999;
      }
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return this.forestType == 3 && rand.nextInt(3) > 0 ? CANOPY_TREE : (this.forestType != 2 && rand.nextInt(5) != 0 ? this.worldGeneratorTrees : BIRCH_TREE);
   }

   @Override
   public net.minecraft.block.BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
      if (this.forestType == 1) {
         double xCoordinate = MathHelper.clamp_double((1.0 + GRASS_COLOR_NOISE.getValue(pos.getX() / 48.0, pos.getZ() / 48.0)) / 2.0, 0.0, 0.9999);
         net.minecraft.block.BlockFlower.EnumFlowerType enumFlowerType = net.minecraft.block.BlockFlower.EnumFlowerType.values()[(int)(xCoordinate * net.minecraft.block.BlockFlower.EnumFlowerType.values().length)];
         return enumFlowerType == net.minecraft.block.BlockFlower.EnumFlowerType.BLUE_ORCHID ? net.minecraft.block.BlockFlower.EnumFlowerType.POPPY : enumFlowerType;
      } else {
         return super.pickRandomFlower(rand, pos);
      }
   }

   @Override
   public int getGrassColorAtPos(BlockPos pos) {
      int grassColorAtPos = super.getGrassColorAtPos(pos);
      return this.forestType == 3 ? (grassColorAtPos & 16711422) + 2634762 >> 1 : grassColorAtPos;
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      if (this.forestType == 3) {
         for (int index = 0; index < 4; index++) {
            for (int innerIndex = 0; innerIndex < 4; innerIndex++) {
               int i = index * 4 + 1 + 8 + rand.nextInt(3);
               int i2 = innerIndex * 4 + 1 + 8 + rand.nextInt(3);
               BlockPos blockPos = worldIn.getHeight(pos.add(i, 0, i2));
               if (rand.nextInt(20) == 0) {
                  WorldGenBigMushroom worldGenBigMushroom = new WorldGenBigMushroom();
                  worldGenBigMushroom.generate(worldIn, rand, blockPos);
               } else {
                  WorldGenAbstractTree worldGenAbstractTree = this.genBigTreeChance(rand);
                  worldGenAbstractTree.setDecorationDefaults();
                  if (worldGenAbstractTree.generate(worldIn, rand, blockPos)) {
                     worldGenAbstractTree.generateExtraTreeFeatures(worldIn, rand, blockPos);
                  }
               }
            }
         }
      }

      int i3 = rand.nextInt(5) - 3;
      if (this.forestType == 1) {
         i3 += 2;
      }

      for (int nestedIndex = 0; nestedIndex < i3; nestedIndex++) {
         int i4 = rand.nextInt(3);
         if (i4 == 0) {
            DOUBLE_PLANT_GENERATOR.setPlantType(net.minecraft.block.BlockDoublePlant.EnumPlantType.SYRINGA);
         } else if (i4 == 1) {
            DOUBLE_PLANT_GENERATOR.setPlantType(net.minecraft.block.BlockDoublePlant.EnumPlantType.ROSE);
         } else if (i4 == 2) {
            DOUBLE_PLANT_GENERATOR.setPlantType(net.minecraft.block.BlockDoublePlant.EnumPlantType.PAEONIA);
         }

         for (int index2 = 0; index2 < 5; index2++) {
            int i5 = rand.nextInt(16) + 8;
            int i6 = rand.nextInt(16) + 8;
            int yCoordinate = rand.nextInt(worldIn.getHeight(pos.add(i5, 0, i6)).getY() + 32);
            if (DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, new BlockPos(pos.getX() + i5, yCoordinate, pos.getZ() + i6))) {
               break;
            }
         }
      }

      super.decorate(worldIn, rand, pos);
   }

   @Override
   protected BiomeGenBase createMutatedBiome(int id) {
      if (this.biomeID == BiomeGenBase.forest.biomeID) {
         BiomeGenForest biomeGenForest = new BiomeGenForest(id, 1);
         biomeGenForest.setHeight(new BiomeGenBase.Height(this.minHeight, this.maxHeight + 0.2F));
         biomeGenForest.setBiomeName("Flower Forest");
         biomeGenForest.setBiomeColor(6976549, true);
         biomeGenForest.setFillerBlockMetadata(8233509);
         return biomeGenForest;
      } else {
         return this.biomeID != BiomeGenBase.birchForest.biomeID && this.biomeID != BiomeGenBase.birchForestHills.biomeID ? new BiomeGenMutated(id, this) {
            @Override
            public void decorate(World worldIn, Random rand, BlockPos pos) {
               this.baseBiome.decorate(worldIn, rand, pos);
            }
         } : new BiomeGenMutated(id, this) {
            @Override
            public WorldGenAbstractTree genBigTreeChance(Random rand) {
               return rand.nextBoolean() ? BiomeGenForest.SUPER_BIRCH_TREE : BiomeGenForest.BIRCH_TREE;
            }
         };
      }
   }
}
