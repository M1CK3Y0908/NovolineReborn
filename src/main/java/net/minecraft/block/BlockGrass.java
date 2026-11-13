package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public class BlockGrass extends Block implements IGrowable {
   public static final PropertyBool SNOWY = PropertyBool.create("snowy");

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      return BiomeColorHelper.getGrassColorAtPos(worldIn, pos);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos.up()).getBlock();
      return state.withProperty(SNOWY, block == Blocks.snow || block == Blocks.snow_layer);
   }

   @Override
   public int getRenderColor(IBlockState state) {
      return this.getBlockColor();
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         if (worldIn.getLightFromNeighbors(pos.up()) < 4 && worldIn.getBlockState(pos.up()).getBlock().getLightOpacity() > 2) {
            worldIn.setBlockState(pos, Blocks.dirt.getDefaultState());
         } else if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
            for (int index = 0; index < 4; index++) {
               BlockPos blockPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
               Block block = worldIn.getBlockState(blockPos.up()).getBlock();
               IBlockState blockState = worldIn.getBlockState(blockPos);
               if (blockState.getBlock() == Blocks.dirt
                  && blockState.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT
                  && worldIn.getLightFromNeighbors(blockPos.up()) >= 4
                  && block.getLightOpacity() <= 2) {
                  worldIn.setBlockState(blockPos, Blocks.grass.getDefaultState());
               }
            }
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Blocks.dirt.getItemDropped(Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), rand, fortune);
   }

   @Override
   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return true;
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, SNOWY);
   }

   public int getBlockColor() {
      return ColorizerGrass.getGrassColor(0.5, 1.0);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT_MIPPED;
   }

   protected BlockGrass() {
      super(Material.grass);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SNOWY, false));
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      BlockPos blockPos = pos.up();

      label38:
      for (int index = 0; index < 128; index++) {
         BlockPos blockPos2 = blockPos;

         for (int i = 0; i < index / 16; i++) {
            blockPos2 = blockPos2.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);
            if (worldIn.getBlockState(blockPos2.down()).getBlock() != Blocks.grass || worldIn.getBlockState(blockPos2).getBlock().isNormalCube()) {
               continue label38;
            }
         }

         if (worldIn.getBlockState(blockPos2).getBlock().blockMaterial == Material.air) {
            if (rand.nextInt(8) == 0) {
               BlockFlower.EnumFlowerType blockflower$enumflowertype = worldIn.getBiomeGenForCoords(blockPos2).pickRandomFlower(rand, blockPos2);
               BlockFlower blockFlower = blockflower$enumflowertype.getBlockType().getBlock();
               IBlockState blockState = blockFlower.getDefaultState().withProperty(blockFlower.getTypeProperty(), blockflower$enumflowertype);
               if (blockFlower.canBlockStay(worldIn, blockPos2, blockState)) {
                  worldIn.setBlockState(blockPos2, blockState, 3);
               }
            } else {
               IBlockState blockState2 = Blocks.tallgrass.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);
               if (Blocks.tallgrass.canBlockStay(worldIn, blockPos2, blockState2)) {
                  worldIn.setBlockState(blockPos2, blockState2, 3);
               }
            }
         }
      }
   }

   @Override
   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return 0;
   }
}
