package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMelon;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenJungle extends BiomeGenBase {
   private boolean edge;
   private static final IBlockState JUNGLE_LOG = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, net.minecraft.block.BlockPlanks.EnumType.JUNGLE);
   private static final IBlockState JUNGLE_LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, net.minecraft.block.BlockPlanks.EnumType.JUNGLE)
      .withProperty(BlockLeaves.CHECK_DECAY, false);
   private static final IBlockState OAK_LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, net.minecraft.block.BlockPlanks.EnumType.OAK)
      .withProperty(BlockLeaves.CHECK_DECAY, false);

   public BiomeGenJungle(int id, boolean edge) {
      super(id);
      this.edge = edge;
      if (edge) {
         this.theBiomeDecorator.treesPerChunk = 2;
      } else {
         this.theBiomeDecorator.treesPerChunk = 50;
      }

      this.theBiomeDecorator.grassPerChunk = 25;
      this.theBiomeDecorator.flowersPerChunk = 4;
      if (!edge) {
         this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityOcelot.class, 2, 1, 1));
      }

      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityChicken.class, 10, 4, 4));
   }

   @Override
   public WorldGenerator getRandomWorldGenForGrass(Random rand) {
      return rand.nextInt(4) == 0 ? new WorldGenTallGrass(net.minecraft.block.BlockTallGrass.EnumType.FERN) : new WorldGenTallGrass(net.minecraft.block.BlockTallGrass.EnumType.GRASS);
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return rand.nextInt(10) == 0
         ? this.worldGeneratorBigTree
         : (
            rand.nextInt(2) == 0
               ? new WorldGenShrub(JUNGLE_LOG, OAK_LEAVES)
               : (
                  !this.edge && rand.nextInt(3) == 0
                     ? new WorldGenMegaJungle(false, 10, 20, JUNGLE_LOG, JUNGLE_LEAVES)
                     : new WorldGenTrees(false, 4 + rand.nextInt(7), JUNGLE_LOG, JUNGLE_LEAVES, true)
               )
         );
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      super.decorate(worldIn, rand, pos);
      int i = rand.nextInt(16) + 8;
      int i2 = rand.nextInt(16) + 8;
      int i3 = rand.nextInt(worldIn.getHeight(pos.add(i, 0, i2)).getY() * 2);
      new WorldGenMelon().generate(worldIn, rand, pos.add(i, i3, i2));
      WorldGenVines worldGenVines = new WorldGenVines();

      for (int i4 = 0; i4 < 50; i4++) {
         i3 = rand.nextInt(16) + 8;
         int i5 = rand.nextInt(16) + 8;
         worldGenVines.generate(worldIn, rand, pos.add(i3, 128, i5));
      }
   }
}
