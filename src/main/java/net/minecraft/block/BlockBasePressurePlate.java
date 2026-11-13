package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block {
   protected BlockBasePressurePlate(Material materialIn) {
      this(materialIn, materialIn.getMaterialMapColor());
   }

   protected abstract IBlockState setRedstoneStrength(IBlockState iBlockState, int i);

   private boolean canBePlacedOn(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos) || worldIn.getBlockState(pos).getBlock() instanceof BlockFence;
   }

   @Override
   public void setBlockBoundsForItemRender() {
      float f = 0.5F;
      float f2 = 0.125F;
      float f3 = 0.5F;
      this.setBlockBounds(0.0F, 0.375F, 0.0F, 1.0F, 0.625F, 1.0F);
   }

   protected void setBlockBoundsBasedOnState0(IBlockState state) {
      if (this.getRedstoneStrength(state) > 0) {
         this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.03125F, 0.9375F);
      } else {
         this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);
      }
   }

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (!worldIn.isRemote) {
         int redstoneStrength = this.getRedstoneStrength(state);
         if (redstoneStrength == 0) {
            this.updateState(worldIn, pos, state, redstoneStrength);
         }
      }
   }

   protected abstract int getRedstoneStrength(IBlockState iBlockState);

   @Override
   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
   }

   @Override
   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   @Override
   public int tickRate(World worldIn) {
      return 20;
   }

   @Override
   public int getMobilityFlag() {
      return 1;
   }

   @Override
   public boolean canSpawnInBlock() {
      return true;
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return this.getRedstoneStrength(state);
   }

   protected void updateState(World worldIn, BlockPos pos, IBlockState state, int oldRedstoneStrength) {
      int redstoneStrength = this.computeRedstoneStrength(worldIn, pos);
      boolean flag = oldRedstoneStrength > 0;
      boolean flag2 = redstoneStrength > 0;
      if (oldRedstoneStrength != redstoneStrength) {
         state = this.setRedstoneStrength(state, redstoneStrength);
         worldIn.setBlockState(pos, state, 2);
         this.updateNeighbors(worldIn, pos);
         worldIn.markBlockRangeForRenderUpdate(pos, pos);
      }

      if (!flag2 && flag) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.click", 0.3F, 0.5F);
      } else if (flag2 && !flag) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.click", 0.3F, 0.6F);
      }

      if (flag2) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
      }
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return side == EnumFacing.UP ? this.getRedstoneStrength(state) : 0;
   }

   protected abstract int computeRedstoneStrength(World world2, BlockPos blockPos);

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!this.canBePlacedOn(worldIn, pos.down())) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (this.getRedstoneStrength(state) > 0) {
         this.updateNeighbors(worldIn, pos);
      }

      super.breakBlock(worldIn, pos, state);
   }

   protected AxisAlignedBB getSensitiveAABB(BlockPos pos) {
      float f = 0.125F;
      return new AxisAlignedBB(pos.getX() + 0.125F, pos.getY(), pos.getZ() + 0.125F, pos.getX() + 1 - 0.125F, pos.getY() + 0.25, pos.getZ() + 1 - 0.125F);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   protected BlockBasePressurePlate(Material material2, MapColor mapColor) {
      super(material2, mapColor);
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.setTickRandomly(true);
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         int redstoneStrength = this.getRedstoneStrength(state);
         if (redstoneStrength > 0) {
            this.updateState(worldIn, pos, state, redstoneStrength);
         }
      }
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return this.canBePlacedOn(worldIn, pos.down());
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      this.setBlockBoundsBasedOnState0(worldIn.getBlockState(pos));
   }

   protected void updateNeighbors(World worldIn, BlockPos pos) {
      worldIn.notifyNeighborsOfStateChange(pos, this);
      worldIn.notifyNeighborsOfStateChange(pos.down(), this);
   }
}
