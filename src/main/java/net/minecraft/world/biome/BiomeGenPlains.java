package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BiomeGenPlains extends BiomeGenBase {
   protected boolean sunflowerPlains;

   @Override
   protected BiomeGenBase createMutatedBiome(int id) {
      BiomeGenPlains biomeGenPlains = new BiomeGenPlains(id);
      biomeGenPlains.setBiomeName("Sunflower Plains");
      biomeGenPlains.sunflowerPlains = true;
      biomeGenPlains.setColor(9286496);
      biomeGenPlains.mapColor = 14273354;
      return biomeGenPlains;
   }

   @Override
   public net.minecraft.block.BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
      double xCoordinate = GRASS_COLOR_NOISE.getValue(pos.getX() / 200.0, pos.getZ() / 200.0);
      if (xCoordinate < -0.8) {
         int i = rand.nextInt(4);
         switch (i) {
            case 0:
               return net.minecraft.block.BlockFlower.EnumFlowerType.ORANGE_TULIP;
            case 1:
               return net.minecraft.block.BlockFlower.EnumFlowerType.RED_TULIP;
            case 2:
               return net.minecraft.block.BlockFlower.EnumFlowerType.PINK_TULIP;
            case 3:
            default:
               return net.minecraft.block.BlockFlower.EnumFlowerType.WHITE_TULIP;
         }
      } else if (rand.nextInt(3) > 0) {
         int i2 = rand.nextInt(3);
         return i2 == 0
            ? net.minecraft.block.BlockFlower.EnumFlowerType.POPPY
            : (i2 == 1 ? net.minecraft.block.BlockFlower.EnumFlowerType.HOUSTONIA : net.minecraft.block.BlockFlower.EnumFlowerType.OXEYE_DAISY);
      } else {
         return net.minecraft.block.BlockFlower.EnumFlowerType.DANDELION;
      }
   }

   protected BiomeGenPlains(int id) {
      super(id);
      this.setTemperatureRainfall(0.8F, 0.4F);
      this.setHeight(height_LowPlains);
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityHorse.class, 5, 2, 6));
      this.theBiomeDecorator.treesPerChunk = -999;
      this.theBiomeDecorator.flowersPerChunk = 4;
      this.theBiomeDecorator.grassPerChunk = 10;
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      double xCoordinate = GRASS_COLOR_NOISE.getValue((pos.getX() + 8) / 200.0, (pos.getZ() + 8) / 200.0);
      if (xCoordinate < -0.8) {
         this.theBiomeDecorator.flowersPerChunk = 15;
         this.theBiomeDecorator.grassPerChunk = 5;
      } else {
         this.theBiomeDecorator.flowersPerChunk = 4;
         this.theBiomeDecorator.grassPerChunk = 10;
         DOUBLE_PLANT_GENERATOR.setPlantType(net.minecraft.block.BlockDoublePlant.EnumPlantType.GRASS);

         for (int index = 0; index < 7; index++) {
            int i = rand.nextInt(16) + 8;
            int i2 = rand.nextInt(16) + 8;
            int yCoordinate = rand.nextInt(worldIn.getHeight(pos.add(i, 0, i2)).getY() + 32);
            DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(i, yCoordinate, i2));
         }
      }

      if (this.sunflowerPlains) {
         DOUBLE_PLANT_GENERATOR.setPlantType(net.minecraft.block.BlockDoublePlant.EnumPlantType.SUNFLOWER);

         for (int index2 = 0; index2 < 10; index2++) {
            int i3 = rand.nextInt(16) + 8;
            int i4 = rand.nextInt(16) + 8;
            int yCoordinate2 = rand.nextInt(worldIn.getHeight(pos.add(i3, 0, i4)).getY() + 32);
            DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(i3, yCoordinate2, i4));
         }
      }

      super.decorate(worldIn, rand, pos);
   }
}
