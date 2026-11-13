package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;

public class BlockSapling extends BlockBush implements IGrowable {
   public static final PropertyEnum<BlockPlanks.EnumType> TYPE = PropertyEnum.create("type", BlockPlanks.EnumType.class);
   public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);

   public boolean isTypeAt(World worldIn, BlockPos pos, BlockPlanks.EnumType type) {
      IBlockState blockState = worldIn.getBlockState(pos);
      return blockState.getBlock() == this && blockState.getValue(TYPE) == type;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, TYPE, STAGE);
   }

   @Override
   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return worldIn.rand.nextFloat() < 0.45;
   }

   private boolean isAreaClearForGrowth(World worldIn, BlockPos pos, int offsetX, int offsetZ, BlockPlanks.EnumType type) {
      return this.isTypeAt(worldIn, pos.add(offsetX, 0, offsetZ), type)
         && this.isTypeAt(worldIn, pos.add(offsetX + 1, 0, offsetZ), type)
         && this.isTypeAt(worldIn, pos.add(offsetX, 0, offsetZ + 1), type)
         && this.isTypeAt(worldIn, pos.add(offsetX + 1, 0, offsetZ + 1), type);
   }

   @Override
   public int damageDropped(IBlockState state) {
      return state.getValue(TYPE).getMetadata();
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return 0 | state.getValue(TYPE).getMetadata() | state.getValue(STAGE) << 3;
   }

   @Override
   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return true;
   }

   public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      WorldGenAbstractTree worldGenAbstractTree = rand.nextInt(10) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true);
      int i = 0;
      int i2 = 0;
      boolean conditionMet = false;
      switch ((BlockPlanks.EnumType)state.getValue(TYPE)) {
         case SPRUCE:
            label68:
            for (i = 0; i >= -1; i--) {
               for (i2 = 0; i2 >= -1; i2--) {
                  if (this.isAreaClearForGrowth(worldIn, pos, i, i2, BlockPlanks.EnumType.SPRUCE)) {
                     worldGenAbstractTree = new WorldGenMegaPineTree(rand.nextBoolean());
                     conditionMet = true;
                     break label68;
                  }
               }
            }

            if (!conditionMet) {
               i2 = 0;
               i = 0;
               worldGenAbstractTree = new WorldGenTaiga2(true);
            }
            break;
         case BIRCH:
            worldGenAbstractTree = new WorldGenForest(true, false);
            break;
         case JUNGLE:
            IBlockState blockState = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
            IBlockState blockState2 = Blocks.leaves
               .getDefaultState()
               .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE)
               .withProperty(BlockLeaves.CHECK_DECAY, false);

            label82:
            for (i = 0; i >= -1; i--) {
               for (i2 = 0; i2 >= -1; i2--) {
                  if (this.isAreaClearForGrowth(worldIn, pos, i, i2, BlockPlanks.EnumType.JUNGLE)) {
                     worldGenAbstractTree = new WorldGenMegaJungle(true, 10, 20, blockState, blockState2);
                     conditionMet = true;
                     break label82;
                  }
               }
            }

            if (!conditionMet) {
               i2 = 0;
               i = 0;
               worldGenAbstractTree = new WorldGenTrees(true, 4 + rand.nextInt(7), blockState, blockState2, false);
            }
            break;
         case ACACIA:
            worldGenAbstractTree = new WorldGenSavannaTree(true);
            break;
         case DARK_OAK:
            label96:
            for (i = 0; i >= -1; i--) {
               for (i2 = 0; i2 >= -1; i2--) {
                  if (this.isAreaClearForGrowth(worldIn, pos, i, i2, BlockPlanks.EnumType.DARK_OAK)) {
                     worldGenAbstractTree = new WorldGenCanopyTree(true);
                     conditionMet = true;
                     break label96;
                  }
               }
            }

            if (!conditionMet) {
               return;
            }
         case OAK:
      }

      IBlockState blockState3 = Blocks.air.getDefaultState();
      if (conditionMet) {
         worldIn.setBlockState(pos.add(i, 0, i2), blockState3, 4);
         worldIn.setBlockState(pos.add(i + 1, 0, i2), blockState3, 4);
         worldIn.setBlockState(pos.add(i, 0, i2 + 1), blockState3, 4);
         worldIn.setBlockState(pos.add(i + 1, 0, i2 + 1), blockState3, 4);
      } else {
         worldIn.setBlockState(pos, blockState3, 4);
      }

      if (!worldGenAbstractTree.generate(worldIn, rand, pos.add(i, 0, i2))) {
         if (conditionMet) {
            worldIn.setBlockState(pos.add(i, 0, i2), state, 4);
            worldIn.setBlockState(pos.add(i + 1, 0, i2), state, 4);
            worldIn.setBlockState(pos.add(i, 0, i2 + 1), state, 4);
            worldIn.setBlockState(pos.add(i + 1, 0, i2 + 1), state, 4);
         } else {
            worldIn.setBlockState(pos, state, 4);
         }
      }
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + "." + BlockPlanks.EnumType.OAK.getUnlocalizedName() + ".name");
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         super.updateTick(worldIn, pos, state, rand);
         if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
            this.grow(worldIn, pos, state, rand);
         }
      }
   }

   @Override
   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      this.grow(worldIn, pos, state, rand);
   }

   protected BlockSapling() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockPlanks.EnumType.OAK).withProperty(STAGE, 0));
      float f = 0.4F;
      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      for (BlockPlanks.EnumType enumType : BlockPlanks.EnumType.values()) {
         list.add(new ItemStack(itemIn, 1, enumType.getMetadata()));
      }
   }

   public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (state.getValue(STAGE) == 0) {
         worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
      } else {
         this.generateTree(worldIn, pos, state, rand);
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TYPE, BlockPlanks.EnumType.byMetadata(meta & 7)).withProperty(STAGE, (meta & 8) >> 3);
   }
}
