package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRedstoneDiode extends BlockDirectional {
   protected final boolean isRepeaterPowered;

   protected BlockRedstoneDiode(boolean powered) {
      super(Material.circuits);
      this.isRepeaterPowered = powered;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
   }

   @Override
   public boolean isAssociatedBlock(Block other) {
      return this.isAssociated(other);
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) ? super.canPlaceBlockAt(worldIn, pos) : false;
   }

   protected boolean isPowered(IBlockState state) {
      return this.isRepeaterPowered;
   }

   protected int getActiveSignal(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
      return 15;
   }

   protected int getPowerOnSide(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      IBlockState blockState = worldIn.getBlockState(pos);
      Block block = blockState.getBlock();
      return this.canPowerSide(block) ? (block == Blocks.redstone_wire ? blockState.getValue(BlockRedstoneWire.POWER) : worldIn.getStrongPower(pos, side)) : 0;
   }

   public boolean isAssociated(Block other) {
      return other == this.getPoweredState(this.getDefaultState()).getBlock() || other == this.getUnpoweredState(this.getDefaultState()).getBlock();
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (this.shouldBePowered(worldIn, pos, state)) {
         worldIn.scheduleUpdate(pos, this, 1);
      }
   }

   protected boolean shouldBePowered(World worldIn, BlockPos pos, IBlockState state) {
      return this.calculateInputStrength(worldIn, pos, state) > 0;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   public boolean isLocked(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
      return false;
   }

   public boolean isFacingTowardsRepeater(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing facing = state.getValue(FACING).getOpposite();
      BlockPos blockPos = pos.offset(facing);
      return isRedstoneRepeaterBlockID(worldIn.getBlockState(blockPos).getBlock()) ? worldIn.getBlockState(blockPos).getValue(FACING) != facing : false;
   }

   protected void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing facing = state.getValue(FACING);
      BlockPos blockPos = pos.offset(facing.getOpposite());
      worldIn.notifyBlockOfStateChange(blockPos, this);
      worldIn.notifyNeighborsOfStateExcept(blockPos, this, facing);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   public static boolean isRedstoneRepeaterBlockID(Block blockIn) {
      return Blocks.unpowered_repeater.isAssociated(blockIn) || Blocks.unpowered_comparator.isAssociated(blockIn);
   }

   protected abstract int getDelay(IBlockState iBlockState);

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return side.getAxis() != net.minecraft.util.EnumFacing.Axis.Y;
   }

   protected boolean canPowerSide(Block blockIn) {
      return blockIn.canProvidePower();
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return !this.isPowered(state) ? 0 : (state.getValue(FACING) == side ? this.getActiveSignal(worldIn, pos, state) : 0);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (this.canBlockStay(worldIn, pos)) {
         this.updateState(worldIn, pos, state);
      } else {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);

         for (EnumFacing enumFacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }
      }
   }

   protected abstract IBlockState getUnpoweredState(IBlockState iBlockState);

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!this.isLocked(worldIn, pos, state)) {
         boolean shouldBePowered = this.shouldBePowered(worldIn, pos, state);
         if (this.isRepeaterPowered && !shouldBePowered) {
            worldIn.setBlockState(pos, this.getUnpoweredState(state), 2);
         } else if (!this.isRepeaterPowered) {
            worldIn.setBlockState(pos, this.getPoweredState(state), 2);
            if (!shouldBePowered) {
               worldIn.updateBlockTick(pos, this.getPoweredState(state).getBlock(), this.getTickDelay(state), -1);
            }
         }
      }
   }

   @Override
   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.notifyNeighbors(worldIn, pos, state);
   }

   protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing facing = state.getValue(FACING);
      BlockPos blockPos = pos.offset(facing);
      int redstonePower = worldIn.getRedstonePower(blockPos, facing);
      if (redstonePower >= 15) {
         return redstonePower;
      }

      IBlockState blockState = worldIn.getBlockState(blockPos);
      return Math.max(redstonePower, blockState.getBlock() == Blocks.redstone_wire ? blockState.getValue(BlockRedstoneWire.POWER) : 0);
   }

   protected int getPowerOnSides(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
      EnumFacing facing = state.getValue(FACING);
      EnumFacing facing2 = facing.rotateY();
      EnumFacing facing3 = facing.rotateYCCW();
      return Math.max(this.getPowerOnSide(worldIn, pos.offset(facing2), facing2), this.getPowerOnSide(worldIn, pos.offset(facing3), facing3));
   }

   protected abstract IBlockState getPoweredState(IBlockState iBlockState);

   protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.isLocked(worldIn, pos, state)) {
         boolean shouldBePowered = this.shouldBePowered(worldIn, pos, state);
         if ((this.isRepeaterPowered && !shouldBePowered || !this.isRepeaterPowered && shouldBePowered) && !worldIn.isBlockTickPending(pos, this)) {
            int i = -1;
            if (this.isFacingTowardsRepeater(worldIn, pos, state)) {
               i = -3;
            } else if (this.isRepeaterPowered) {
               i = -2;
            }

            worldIn.updateBlockTick(pos, this, this.getDelay(state), i);
         }
      }
   }

   protected int getTickDelay(IBlockState state) {
      return this.getDelay(state);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return this.getWeakPower(worldIn, pos, state, side);
   }

   public boolean canBlockStay(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
   }

   @Override
   public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
      if (this.isRepeaterPowered) {
         for (EnumFacing enumFacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }
      }

      super.onBlockDestroyedByPlayer(worldIn, pos, state);
   }
}
