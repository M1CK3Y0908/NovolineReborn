package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDirt;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenTaiga extends BiomeGenBase {
   private static final WorldGenTaiga1 SPRUCE_TREE = new WorldGenTaiga1();
   private static final WorldGenTaiga2 PINE_TREE = new WorldGenTaiga2(false);
   private static final WorldGenMegaPineTree MEGA_PINE_TREE = new WorldGenMegaPineTree(false);
   private static final WorldGenMegaPineTree MEGA_SPRUCE_TREE = new WorldGenMegaPineTree(true);
   private int taigaType;
   private static final WorldGenBlockBlob FOREST_ROCK_GENERATOR = new WorldGenBlockBlob(Blocks.mossy_cobblestone);

   @Override
   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      if (this.taigaType == 1 || this.taigaType == 2) {
         this.topBlock = Blocks.grass.getDefaultState();
         this.fillerBlock = Blocks.dirt.getDefaultState();
         if (noiseVal > 1.75) {
            this.topBlock = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, net.minecraft.block.BlockDirt.DirtType.COARSE_DIRT);
         } else if (noiseVal > -0.95) {
            this.topBlock = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, net.minecraft.block.BlockDirt.DirtType.PODZOL);
         }
      }

      this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
   }

   @Override
   protected BiomeGenBase createMutatedBiome(int id) {
      return this.biomeID == BiomeGenBase.megaTaiga.biomeID
         ? new BiomeGenTaiga(id, 2)
            .setBiomeColor(5858897, true)
            .setBiomeName("Mega Spruce Taiga")
            .setFillerBlockMetadata(5159473)
            .setTemperatureRainfall(0.25F, 0.8F)
            .setHeight(new BiomeGenBase.Height(this.minHeight, this.maxHeight))
         : super.createMutatedBiome(id);
   }

   public BiomeGenTaiga(int id, int taigaType) {
      super(id);
      this.taigaType = taigaType;
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityWolf.class, 8, 4, 4));
      this.theBiomeDecorator.treesPerChunk = 10;
      if (taigaType != 1 && taigaType != 2) {
         this.theBiomeDecorator.grassPerChunk = 1;
         this.theBiomeDecorator.mushroomsPerChunk = 1;
      } else {
         this.theBiomeDecorator.grassPerChunk = 7;
         this.theBiomeDecorator.deadBushPerChunk = 1;
         this.theBiomeDecorator.mushroomsPerChunk = 3;
      }
   }

   @Override
   public WorldGenerator getRandomWorldGenForGrass(Random rand) {
      return rand.nextInt(5) > 0 ? new WorldGenTallGrass(net.minecraft.block.BlockTallGrass.EnumType.FERN) : new WorldGenTallGrass(net.minecraft.block.BlockTallGrass.EnumType.GRASS);
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return (this.taigaType == 1 || this.taigaType == 2) && rand.nextInt(3) == 0
         ? (this.taigaType != 2 && rand.nextInt(13) != 0 ? MEGA_PINE_TREE : MEGA_SPRUCE_TREE)
         : (rand.nextInt(3) == 0 ? SPRUCE_TREE : PINE_TREE);
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      if (this.taigaType == 1 || this.taigaType == 2) {
         int i = rand.nextInt(3);

         for (int innerIndex = 0; innerIndex < i; innerIndex++) {
            int i2 = rand.nextInt(16) + 8;
            int i3 = rand.nextInt(16) + 8;
            BlockPos blockPos = worldIn.getHeight(pos.add(i2, 0, i3));
            FOREST_ROCK_GENERATOR.generate(worldIn, rand, blockPos);
         }
      }

      DOUBLE_PLANT_GENERATOR.setPlantType(net.minecraft.block.BlockDoublePlant.EnumPlantType.FERN);

      for (int index = 0; index < 7; index++) {
         int i4 = rand.nextInt(16) + 8;
         int i5 = rand.nextInt(16) + 8;
         int yCoordinate = rand.nextInt(worldIn.getHeight(pos.add(i4, 0, i5)).getY() + 32);
         DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(i4, yCoordinate, i5));
      }

      super.decorate(worldIn, rand, pos);
   }
}
