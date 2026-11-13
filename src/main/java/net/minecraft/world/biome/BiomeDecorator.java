package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.GeneratorBushFeature;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenClay;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenSand;
import net.minecraft.world.gen.feature.WorldGenWaterlily;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeDecorator {
   protected WorldGenerator lapisGen;
   protected int cactiPerChunk;
   protected WorldGenerator diamondGen;
   protected WorldGenerator clayGen = new WorldGenClay();
   protected WorldGenerator sandGen = new WorldGenSand(Blocks.sand, 7);
   protected WorldGenerator goldGen;
   protected WorldGenerator dirtGen;
   protected WorldGenerator dioriteGen;
   protected WorldGenerator gravelAsSandGen = new WorldGenSand(Blocks.gravel, 6);
   protected WorldGenFlowers yellowFlowerGen = new WorldGenFlowers(Blocks.yellow_flower, net.minecraft.block.BlockFlower.EnumFlowerType.DANDELION);
   protected WorldGenerator mushroomBrownGen = new GeneratorBushFeature(Blocks.brown_mushroom);
   protected int reedsPerChunk;
   protected WorldGenerator mushroomRedGen = new GeneratorBushFeature(Blocks.red_mushroom);
   protected WorldGenerator bigMushroomGen = new WorldGenBigMushroom();
   protected WorldGenerator reedGen = new WorldGenReed();
   protected Random randomGenerator;
   protected BlockPos chunkPos;
   protected WorldGenerator cactusGen = new WorldGenCactus();
   protected ChunkProviderSettings chunkProviderSettings;
   protected WorldGenerator waterlilyGen = new WorldGenWaterlily();
   protected int flowersPerChunk = 2;
   protected WorldGenerator coalGen;
   protected int grassPerChunk = 1;
   protected int sandPerChunk = 1;
   protected int sandPerChunk2 = 3;
   protected WorldGenerator graniteGen;
   protected WorldGenerator redstoneGen;
   protected WorldGenerator andesiteGen;
   protected int treesPerChunk;
   protected int bigMushroomsPerChunk;
   protected World currentWorld;
   protected int clayPerChunk = 1;
   protected int mushroomsPerChunk;
   protected int waterlilyPerChunk;
   protected int deadBushPerChunk;
   protected WorldGenerator ironGen;
   protected WorldGenerator gravelGen;
   public boolean generateLakes = true;

   protected void generateOres() {
      this.genStandardOre1(
         this.chunkProviderSettings.dirtCount, this.dirtGen, this.chunkProviderSettings.dirtMinHeight, this.chunkProviderSettings.dirtMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.gravelCount, this.gravelGen, this.chunkProviderSettings.gravelMinHeight, this.chunkProviderSettings.gravelMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.dioriteCount, this.dioriteGen, this.chunkProviderSettings.dioriteMinHeight, this.chunkProviderSettings.dioriteMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.graniteCount, this.graniteGen, this.chunkProviderSettings.graniteMinHeight, this.chunkProviderSettings.graniteMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.andesiteCount, this.andesiteGen, this.chunkProviderSettings.andesiteMinHeight, this.chunkProviderSettings.andesiteMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.coalCount, this.coalGen, this.chunkProviderSettings.coalMinHeight, this.chunkProviderSettings.coalMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.ironCount, this.ironGen, this.chunkProviderSettings.ironMinHeight, this.chunkProviderSettings.ironMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.goldCount, this.goldGen, this.chunkProviderSettings.goldMinHeight, this.chunkProviderSettings.goldMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.redstoneCount, this.redstoneGen, this.chunkProviderSettings.redstoneMinHeight, this.chunkProviderSettings.redstoneMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.diamondCount, this.diamondGen, this.chunkProviderSettings.diamondMinHeight, this.chunkProviderSettings.diamondMaxHeight
      );
      this.genStandardOre2(
         this.chunkProviderSettings.lapisCount, this.lapisGen, this.chunkProviderSettings.lapisCenterHeight, this.chunkProviderSettings.lapisSpread
      );
   }

   public void decorate(World worldIn, Random random, BiomeGenBase biome, BlockPos chunkPos) {
      if (this.currentWorld != null) {
         throw new RuntimeException("Already decorating");
      }

      this.currentWorld = worldIn;
      String text = worldIn.getWorldInfo().getGeneratorOptions();
      if (text != null) {
         this.chunkProviderSettings = net.minecraft.world.gen.ChunkProviderSettings.Factory.jsonToFactory(text).build();
      } else {
         this.chunkProviderSettings = net.minecraft.world.gen.ChunkProviderSettings.Factory.jsonToFactory("").build();
      }

      this.randomGenerator = random;
      this.chunkPos = chunkPos;
      this.dirtGen = new WorldGenMinable(Blocks.dirt.getDefaultState(), this.chunkProviderSettings.dirtSize);
      this.gravelGen = new WorldGenMinable(Blocks.gravel.getDefaultState(), this.chunkProviderSettings.gravelSize);
      this.graniteGen = new WorldGenMinable(
         Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.GRANITE), this.chunkProviderSettings.graniteSize
      );
      this.dioriteGen = new WorldGenMinable(
         Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.DIORITE), this.chunkProviderSettings.dioriteSize
      );
      this.andesiteGen = new WorldGenMinable(
         Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.ANDESITE), this.chunkProviderSettings.andesiteSize
      );
      this.coalGen = new WorldGenMinable(Blocks.coal_ore.getDefaultState(), this.chunkProviderSettings.coalSize);
      this.ironGen = new WorldGenMinable(Blocks.iron_ore.getDefaultState(), this.chunkProviderSettings.ironSize);
      this.goldGen = new WorldGenMinable(Blocks.gold_ore.getDefaultState(), this.chunkProviderSettings.goldSize);
      this.redstoneGen = new WorldGenMinable(Blocks.redstone_ore.getDefaultState(), this.chunkProviderSettings.redstoneSize);
      this.diamondGen = new WorldGenMinable(Blocks.diamond_ore.getDefaultState(), this.chunkProviderSettings.diamondSize);
      this.lapisGen = new WorldGenMinable(Blocks.lapis_ore.getDefaultState(), this.chunkProviderSettings.lapisSize);
      this.genDecorations(biome);
      this.currentWorld = null;
      this.randomGenerator = null;
   }

   protected void genStandardOre2(int blockCount, WorldGenerator generator, int centerHeight, int spread) {
      for (int index = 0; index < blockCount; index++) {
         BlockPos blockPos = this.chunkPos
            .add(
               this.randomGenerator.nextInt(16),
               this.randomGenerator.nextInt(spread) + this.randomGenerator.nextInt(spread) + centerHeight - spread,
               this.randomGenerator.nextInt(16)
            );
         generator.generate(this.currentWorld, this.randomGenerator, blockPos);
      }
   }

   protected void genDecorations(BiomeGenBase biomeGenBaseIn) {
      this.generateOres();

      for (int index = 0; index < this.sandPerChunk2; index++) {
         int i = this.randomGenerator.nextInt(16) + 8;
         int i2 = this.randomGenerator.nextInt(16) + 8;
         this.sandGen
            .generate(this.currentWorld, this.randomGenerator, this.currentWorld.getTopSolidOrLiquidBlock(this.chunkPos.add(i, 0, i2)));
      }

      for (int index2 = 0; index2 < this.clayPerChunk; index2++) {
         int i3 = this.randomGenerator.nextInt(16) + 8;
         int i4 = this.randomGenerator.nextInt(16) + 8;
         this.clayGen
            .generate(this.currentWorld, this.randomGenerator, this.currentWorld.getTopSolidOrLiquidBlock(this.chunkPos.add(i3, 0, i4)));
      }

      for (int innerIndex = 0; innerIndex < this.sandPerChunk; innerIndex++) {
         int i5 = this.randomGenerator.nextInt(16) + 8;
         int i6 = this.randomGenerator.nextInt(16) + 8;
         this.gravelAsSandGen
            .generate(this.currentWorld, this.randomGenerator, this.currentWorld.getTopSolidOrLiquidBlock(this.chunkPos.add(i5, 0, i6)));
      }

      int treesPerChunk2 = this.treesPerChunk;
      if (this.randomGenerator.nextInt(10) == 0) {
         treesPerChunk2++;
      }

      for (int index3 = 0; index3 < treesPerChunk2; index3++) {
         int i7 = this.randomGenerator.nextInt(16) + 8;
         int i8 = this.randomGenerator.nextInt(16) + 8;
         WorldGenAbstractTree worldGenAbstractTree = biomeGenBaseIn.genBigTreeChance(this.randomGenerator);
         worldGenAbstractTree.setDecorationDefaults();
         BlockPos blockPos = this.currentWorld.getHeight(this.chunkPos.add(i7, 0, i8));
         if (worldGenAbstractTree.generate(this.currentWorld, this.randomGenerator, blockPos)) {
            worldGenAbstractTree.generateExtraTreeFeatures(this.currentWorld, this.randomGenerator, blockPos);
         }
      }

      for (int index4 = 0; index4 < this.bigMushroomsPerChunk; index4++) {
         int i9 = this.randomGenerator.nextInt(16) + 8;
         int i10 = this.randomGenerator.nextInt(16) + 8;
         this.bigMushroomGen.generate(this.currentWorld, this.randomGenerator, this.currentWorld.getHeight(this.chunkPos.add(i9, 0, i10)));
      }

      for (int index5 = 0; index5 < this.flowersPerChunk; index5++) {
         int i11 = this.randomGenerator.nextInt(16) + 8;
         int i12 = this.randomGenerator.nextInt(16) + 8;
         int yCoordinate = this.currentWorld.getHeight(this.chunkPos.add(i11, 0, i12)).getY() + 32;
         if (yCoordinate > 0) {
            int i13 = this.randomGenerator.nextInt(yCoordinate);
            BlockPos blockPos2 = this.chunkPos.add(i11, i13, i12);
            net.minecraft.block.BlockFlower.EnumFlowerType blockflower$enumflowertype = biomeGenBaseIn.pickRandomFlower(this.randomGenerator, blockPos2);
            BlockFlower blockFlower = blockflower$enumflowertype.getBlockType().getBlock();
            if (blockFlower.getMaterial() != Material.air) {
               this.yellowFlowerGen.setGeneratedBlock(blockFlower, blockflower$enumflowertype);
               this.yellowFlowerGen.generate(this.currentWorld, this.randomGenerator, blockPos2);
            }
         }
      }

      for (int index6 = 0; index6 < this.grassPerChunk; index6++) {
         int i14 = this.randomGenerator.nextInt(16) + 8;
         int i15 = this.randomGenerator.nextInt(16) + 8;
         int yCoordinate2 = this.currentWorld.getHeight(this.chunkPos.add(i14, 0, i15)).getY() * 2;
         if (yCoordinate2 > 0) {
            int i16 = this.randomGenerator.nextInt(yCoordinate2);
            biomeGenBaseIn.getRandomWorldGenForGrass(this.randomGenerator)
               .generate(this.currentWorld, this.randomGenerator, this.chunkPos.add(i14, i16, i15));
         }
      }

      for (int index7 = 0; index7 < this.deadBushPerChunk; index7++) {
         int i17 = this.randomGenerator.nextInt(16) + 8;
         int i18 = this.randomGenerator.nextInt(16) + 8;
         int yCoordinate3 = this.currentWorld.getHeight(this.chunkPos.add(i17, 0, i18)).getY() * 2;
         if (yCoordinate3 > 0) {
            int i19 = this.randomGenerator.nextInt(yCoordinate3);
            new WorldGenDeadBush().generate(this.currentWorld, this.randomGenerator, this.chunkPos.add(i17, i19, i18));
         }
      }

      for (int index8 = 0; index8 < this.waterlilyPerChunk; index8++) {
         int i20 = this.randomGenerator.nextInt(16) + 8;
         int i21 = this.randomGenerator.nextInt(16) + 8;
         int yCoordinate4 = this.currentWorld.getHeight(this.chunkPos.add(i20, 0, i21)).getY() * 2;
         if (yCoordinate4 > 0) {
            int i22 = this.randomGenerator.nextInt(yCoordinate4);
            BlockPos blockPos3 = this.chunkPos.add(i20, i22, i21);

            while (blockPos3.getY() > 0) {
               BlockPos blockPos4 = blockPos3.down();
               if (!this.currentWorld.isAirBlock(blockPos4)) {
                  break;
               }

               blockPos3 = blockPos4;
            }

            this.waterlilyGen.generate(this.currentWorld, this.randomGenerator, blockPos3);
         }
      }

      for (int index9 = 0; index9 < this.mushroomsPerChunk; index9++) {
         if (this.randomGenerator.nextInt(4) == 0) {
            int i23 = this.randomGenerator.nextInt(16) + 8;
            int i24 = this.randomGenerator.nextInt(16) + 8;
            BlockPos blockPos5 = this.currentWorld.getHeight(this.chunkPos.add(i23, 0, i24));
            this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, blockPos5);
         }

         if (this.randomGenerator.nextInt(8) == 0) {
            int i25 = this.randomGenerator.nextInt(16) + 8;
            int i26 = this.randomGenerator.nextInt(16) + 8;
            int yCoordinate5 = this.currentWorld.getHeight(this.chunkPos.add(i25, 0, i26)).getY() * 2;
            if (yCoordinate5 > 0) {
               int i27 = this.randomGenerator.nextInt(yCoordinate5);
               BlockPos blockPos6 = this.chunkPos.add(i25, i27, i26);
               this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, blockPos6);
            }
         }
      }

      if (this.randomGenerator.nextInt(4) == 0) {
         int i28 = this.randomGenerator.nextInt(16) + 8;
         int i29 = this.randomGenerator.nextInt(16) + 8;
         int yCoordinate6 = this.currentWorld.getHeight(this.chunkPos.add(i28, 0, i29)).getY() * 2;
         if (yCoordinate6 > 0) {
            int i30 = this.randomGenerator.nextInt(yCoordinate6);
            this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, this.chunkPos.add(i28, i30, i29));
         }
      }

      if (this.randomGenerator.nextInt(8) == 0) {
         int i31 = this.randomGenerator.nextInt(16) + 8;
         int i32 = this.randomGenerator.nextInt(16) + 8;
         int yCoordinate7 = this.currentWorld.getHeight(this.chunkPos.add(i31, 0, i32)).getY() * 2;
         if (yCoordinate7 > 0) {
            int i33 = this.randomGenerator.nextInt(yCoordinate7);
            this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, this.chunkPos.add(i31, i33, i32));
         }
      }

      for (int index10 = 0; index10 < this.reedsPerChunk; index10++) {
         int i34 = this.randomGenerator.nextInt(16) + 8;
         int i35 = this.randomGenerator.nextInt(16) + 8;
         int yCoordinate8 = this.currentWorld.getHeight(this.chunkPos.add(i34, 0, i35)).getY() * 2;
         if (yCoordinate8 > 0) {
            int i36 = this.randomGenerator.nextInt(yCoordinate8);
            this.reedGen.generate(this.currentWorld, this.randomGenerator, this.chunkPos.add(i34, i36, i35));
         }
      }

      for (int index11 = 0; index11 < 10; index11++) {
         int i37 = this.randomGenerator.nextInt(16) + 8;
         int i38 = this.randomGenerator.nextInt(16) + 8;
         int yCoordinate9 = this.currentWorld.getHeight(this.chunkPos.add(i37, 0, i38)).getY() * 2;
         if (yCoordinate9 > 0) {
            int i39 = this.randomGenerator.nextInt(yCoordinate9);
            this.reedGen.generate(this.currentWorld, this.randomGenerator, this.chunkPos.add(i37, i39, i38));
         }
      }

      if (this.randomGenerator.nextInt(32) == 0) {
         int i40 = this.randomGenerator.nextInt(16) + 8;
         int i41 = this.randomGenerator.nextInt(16) + 8;
         int yCoordinate10 = this.currentWorld.getHeight(this.chunkPos.add(i40, 0, i41)).getY() * 2;
         if (yCoordinate10 > 0) {
            int i42 = this.randomGenerator.nextInt(yCoordinate10);
            new WorldGenPumpkin().generate(this.currentWorld, this.randomGenerator, this.chunkPos.add(i40, i42, i41));
         }
      }

      for (int index12 = 0; index12 < this.cactiPerChunk; index12++) {
         int i43 = this.randomGenerator.nextInt(16) + 8;
         int i44 = this.randomGenerator.nextInt(16) + 8;
         int yCoordinate11 = this.currentWorld.getHeight(this.chunkPos.add(i43, 0, i44)).getY() * 2;
         if (yCoordinate11 > 0) {
            int i45 = this.randomGenerator.nextInt(yCoordinate11);
            this.cactusGen.generate(this.currentWorld, this.randomGenerator, this.chunkPos.add(i43, i45, i44));
         }
      }

      if (this.generateLakes) {
         for (int index13 = 0; index13 < 50; index13++) {
            int i46 = this.randomGenerator.nextInt(16) + 8;
            int i47 = this.randomGenerator.nextInt(16) + 8;
            int i48 = this.randomGenerator.nextInt(248) + 8;
            if (i48 > 0) {
               int i49 = this.randomGenerator.nextInt(i48);
               BlockPos blockPos7 = this.chunkPos.add(i46, i49, i47);
               new WorldGenLiquids(Blocks.flowing_water).generate(this.currentWorld, this.randomGenerator, blockPos7);
            }
         }

         for (int index14 = 0; index14 < 20; index14++) {
            int i50 = this.randomGenerator.nextInt(16) + 8;
            int i51 = this.randomGenerator.nextInt(16) + 8;
            int i52 = this.randomGenerator.nextInt(this.randomGenerator.nextInt(this.randomGenerator.nextInt(240) + 8) + 8);
            BlockPos blockPos8 = this.chunkPos.add(i50, i52, i51);
            new WorldGenLiquids(Blocks.flowing_lava).generate(this.currentWorld, this.randomGenerator, blockPos8);
         }
      }
   }

   protected void genStandardOre1(int blockCount, WorldGenerator generator, int minHeight, int maxHeight) {
      if (maxHeight < minHeight) {
         int minHeight2 = minHeight;
         minHeight = maxHeight;
         maxHeight = minHeight2;
      } else if (maxHeight == minHeight) {
         if (minHeight < 255) {
            maxHeight++;
         } else {
            minHeight--;
         }
      }

      for (int innerIndex = 0; innerIndex < blockCount; innerIndex++) {
         BlockPos blockPos = this.chunkPos
            .add(this.randomGenerator.nextInt(16), this.randomGenerator.nextInt(maxHeight - minHeight) + minHeight, this.randomGenerator.nextInt(16));
         generator.generate(this.currentWorld, this.randomGenerator, blockPos);
      }
   }
}
