package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCocoa extends BlockDirectional implements IGrowable {
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 2);

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!this.canBlockStay(worldIn, pos, state)) {
         this.dropBlock(worldIn, pos, state);
      } else if (worldIn.rand.nextInt(5) == 0) {
         int value = state.getValue(AGE);
         if (value < 2) {
            worldIn.setBlockState(pos, state.withProperty(AGE, value + 1), 2);
         }
      }
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public int getDamageValue(World worldIn, BlockPos pos) {
      return EnumDyeColor.BROWN.getDyeDamage();
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   public BlockCocoa() {
      super(Material.plants);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(AGE, 0));
      this.setTickRandomly(true);
   }

   private void dropBlock(World worldIn, BlockPos pos, IBlockState state) {
      worldIn.setBlockState(pos, Blocks.air.getDefaultState(), 3);
      this.dropBlockAsItem(worldIn, pos, state, 0);
   }

   @Override
   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   @Override
   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return state.getValue(AGE) < 2;
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      int value = state.getValue(AGE);
      int i = 1;
      if (value >= 2) {
         i = 3;
      }

      for (int nestedIndex = 0; nestedIndex < i; nestedIndex++) {
         spawnAsEntity(worldIn, pos, new ItemStack(Items.dye, 1, EnumDyeColor.BROWN.getDyeDamage()));
      }
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      worldIn.setBlockState(pos, state.withProperty(AGE, state.getValue(AGE) + 1), 2);
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.dye;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return 0 | state.getValue(FACING).getHorizontalIndex() | state.getValue(AGE) << 2;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!this.canBlockStay(worldIn, pos, state)) {
         this.dropBlock(worldIn, pos, state);
      }
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      EnumFacing facing = blockState.getValue(FACING);
      int value = blockState.getValue(AGE);
      int i = 4 + value * 2;
      int i2 = 5 + value * 2;
      float f = i / 2.0F;
      switch (facing) {
         case SOUTH:
            this.setBlockBounds(
               (8.0F - f) / 16.0F, (12.0F - i2) / 16.0F, (15.0F - i) / 16.0F, (8.0F + f) / 16.0F, 0.75F, 0.9375F
            );
            break;
         case NORTH:
            this.setBlockBounds(
               (8.0F - f) / 16.0F, (12.0F - i2) / 16.0F, 0.0625F, (8.0F + f) / 16.0F, 0.75F, (1.0F + i) / 16.0F
            );
            break;
         case WEST:
            this.setBlockBounds(
               0.0625F, (12.0F - i2) / 16.0F, (8.0F - f) / 16.0F, (1.0F + i) / 16.0F, 0.75F, (8.0F + f) / 16.0F
            );
            break;
         case EAST:
            this.setBlockBounds(
               (15.0F - i) / 16.0F, (12.0F - i2) / 16.0F, (8.0F - f) / 16.0F, 0.9375F, 0.75F, (8.0F + f) / 16.0F
            );
      }
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      EnumFacing facing = EnumFacing.fromAngle(placer.rotationYaw);
      worldIn.setBlockState(pos, state.withProperty(FACING, facing), 2);
   }

   public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
      pos = pos.offset(state.getValue(FACING));
      IBlockState blockState = worldIn.getBlockState(pos);
      return blockState.getBlock() == Blocks.log && blockState.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getCollisionBoundingBox(worldIn, pos, state);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(AGE, (meta & 15) >> 2);
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, FACING, AGE);
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getSelectedBoundingBox(worldIn, pos);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      if (!facing.getAxis().isHorizontal()) {
         facing = EnumFacing.NORTH;
      }

      return this.getDefaultState().withProperty(FACING, facing.getOpposite()).withProperty(AGE, 0);
   }
}
