package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockSlab extends Block {
   public static final PropertyEnum<BlockSlab.EnumBlockHalf> HALF = PropertyEnum.create("half", BlockSlab.EnumBlockHalf.class);

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      if (this.isDouble()) {
         return super.shouldSideBeRendered(worldIn, pos, side);
      }

      if (side != EnumFacing.UP && side != EnumFacing.DOWN && !super.shouldSideBeRendered(worldIn, pos, side)) {
         return false;
      }

      BlockPos blockPos = pos.offset(side.getOpposite());
      IBlockState blockState2 = worldIn.getBlockState(pos);
      IBlockState blockState3 = worldIn.getBlockState(blockPos);
      boolean flag = isSlab(blockState2.getBlock()) && blockState2.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP;
      return isSlab(blockState3.getBlock()) && blockState3.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP
         ? (
            side == EnumFacing.DOWN
               ? true
               : (side == EnumFacing.UP && super.shouldSideBeRendered(worldIn, pos, side) ? true : !isSlab(blockState2.getBlock()) || !flag)
         )
         : (
            side == EnumFacing.UP
               ? true
               : (side == EnumFacing.DOWN && super.shouldSideBeRendered(worldIn, pos, side) ? true : !isSlab(blockState2.getBlock()) || flag)
         );
   }

   public abstract String getUnlocalizedName(int i);

   @Override
   public int quantityDropped(Random random) {
      return this.isDouble() ? 2 : 1;
   }

   @Override
   public boolean isFullCube() {
      return this.isDouble();
   }

   @Override
   public boolean isOpaqueCube() {
      return this.isDouble();
   }

   @Override
   public void setBlockBoundsForItemRender() {
      if (this.isDouble()) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }
   }

   public abstract boolean isDouble();

   public BlockSlab(Material materialIn) {
      super(materialIn);
      if (this.isDouble()) {
         this.fullBlock = true;
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }

      this.setLightOpacity(255);
   }

   public abstract Object getVariant(ItemStack itemStack);

   @Override
   protected boolean canSilkHarvest() {
      return false;
   }

   @Override
   public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
   }

   protected static boolean isSlab(Block blockIn) {
      return blockIn == Blocks.stone_slab || blockIn == Blocks.wooden_slab || blockIn == Blocks.stone_slab2;
   }

   public abstract IProperty<?> getVariantProperty();

   @Override
   public int getDamageValue(World worldIn, BlockPos pos) {
      return super.getDamageValue(worldIn, pos) & 7;
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState blockState = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
      return this.isDouble()
         ? blockState
         : (facing == EnumFacing.DOWN || facing != EnumFacing.UP && !(hitY <= 0.5) ? blockState.withProperty(HALF, BlockSlab.EnumBlockHalf.TOP) : blockState);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      if (this.isDouble()) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         IBlockState blockState = worldIn.getBlockState(pos);
         if (blockState.getBlock() == this) {
            if (blockState.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
               this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            }
         }
      }
   }

   public enum EnumBlockHalf implements IStringSerializable {
      TOP("top"),
      BOTTOM("bottom");

      private final String name;

      @Override
      public String getName() {
         return this.name;
      }

      @Override
      public String toString() {
         return this.name;
      }

      EnumBlockHalf(String text) {
         this.name = text;
      }
   }
}
