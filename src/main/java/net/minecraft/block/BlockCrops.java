package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCrops extends BlockBush implements IGrowable {
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);

   @Override
   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      this.grow(worldIn, pos, state);
   }

   @Override
   public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
      return (worldIn.getLight(pos) >= 8 || worldIn.canSeeSky(pos)) && this.canPlaceBlockOn(worldIn.getBlockState(pos.down()).getBlock());
   }

   @Override
   protected boolean canPlaceBlockOn(Block ground) {
      return ground == Blocks.farmland;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return state.getValue(AGE) == 7 ? this.getCrop() : this.getSeed();
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
      if (!worldIn.isRemote) {
         int value = state.getValue(AGE);
         if (value >= 7) {
            int i = 3 + fortune;

            for (int nestedIndex = 0; nestedIndex < i; nestedIndex++) {
               if (worldIn.rand.nextInt(15) <= value) {
                  spawnAsEntity(worldIn, pos, new ItemStack(this.getSeed(), 1, 0));
               }
            }
         }
      }
   }

   protected Item getSeed() {
      return Items.wheat_seeds;
   }

   protected Item getCrop() {
      return Items.wheat;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AGE, meta);
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return this.getSeed();
   }

   protected BlockCrops() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
      this.setTickRandomly(true);
      float f = 0.5F;
      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
      this.setCreativeTab((CreativeTabs)null);
      this.setHardness(0.0F);
      this.setStepSound(soundTypeGrass);
      this.disableStats();
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, AGE);
   }

   @Override
   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   protected static float getGrowthChance(Block blockIn, World worldIn, BlockPos pos) {
      float f = 1.0F;
      BlockPos blockPos = pos.down();

      for (int i = -1; i <= 1; i++) {
         for (int i2 = -1; i2 <= 1; i2++) {
            float f2 = 0.0F;
            IBlockState blockState2 = worldIn.getBlockState(blockPos.add(i, 0, i2));
            if (blockState2.getBlock() == Blocks.farmland) {
               f2 = 1.0F;
               if (blockState2.getValue(BlockFarmland.MOISTURE) > 0) {
                  f2 = 3.0F;
               }
            }

            if (i != 0 || i2 != 0) {
               f2 /= 4.0F;
            }

            f += f2;
         }
      }

      BlockPos blockPos2 = pos.north();
      BlockPos blockPos3 = pos.south();
      BlockPos blockPos4 = pos.west();
      BlockPos blockPos5 = pos.east();
      boolean flag = blockIn == worldIn.getBlockState(blockPos4).getBlock() || blockIn == worldIn.getBlockState(blockPos5).getBlock();
      boolean flag2 = blockIn == worldIn.getBlockState(blockPos2).getBlock() || blockIn == worldIn.getBlockState(blockPos3).getBlock();
      if (flag && flag2) {
         f /= 2.0F;
      } else if (blockIn == worldIn.getBlockState(blockPos4.north()).getBlock()
         || blockIn == worldIn.getBlockState(blockPos5.north()).getBlock()
         || blockIn == worldIn.getBlockState(blockPos5.south()).getBlock()
         || blockIn == worldIn.getBlockState(blockPos4.south()).getBlock()) {
         f /= 2.0F;
      }

      return f;
   }

   @Override
   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return state.getValue(AGE) < 7;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(AGE);
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      super.updateTick(worldIn, pos, state, rand);
      if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
         int value = state.getValue(AGE);
         if (value < 7) {
            float growthChance = getGrowthChance(this, worldIn, pos);
            if (rand.nextInt((int)(25.0F / growthChance) + 1) == 0) {
               worldIn.setBlockState(pos, state.withProperty(AGE, value + 1), 2);
            }
         }
      }
   }

   public void grow(World worldIn, BlockPos pos, IBlockState state) {
      int i = state.getValue(AGE) + MathHelper.getRandomIntegerInRange(worldIn.rand, 2, 5);
      if (i > 7) {
         i = 7;
      }

      worldIn.setBlockState(pos, state.withProperty(AGE, i), 2);
   }
}
