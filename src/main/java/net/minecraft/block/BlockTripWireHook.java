package net.minecraft.block;

import com.google.common.base.Objects;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWireHook extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", net.minecraft.util.EnumFacing.Plane.HORIZONTAL);
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   public static final PropertyBool ATTACHED = PropertyBool.create("attached");
   public static final PropertyBool SUSPENDED = PropertyBool.create("suspended");

   private void notifyHookNeighbors(World worldIn, BlockPos pos, EnumFacing facing) {
      worldIn.notifyNeighborsOfStateChange(pos, this);
      worldIn.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this);
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      this.updateTripWireState(worldIn, pos, state, false, true, -1, (IBlockState)null);
   }

   public void updateTripWireState(
      World worldIn,
      BlockPos pos,
      IBlockState hookState,
      boolean skipOwnStateUpdate,
      boolean notifyNeighbors,
      int changedWireIndex,
      IBlockState changedWireState
   ) {
      EnumFacing value = hookState.getValue(FACING);
      boolean value2 = hookState.getValue(ATTACHED);
      boolean value3 = hookState.getValue(POWERED);
      boolean flag = !World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
      boolean flag2 = !skipOwnStateUpdate;
      boolean flag3 = false;
      int i = 0;
      IBlockState[] iBlockStates = new IBlockState[42];

      for (int i2 = 1; i2 < 42; i2++) {
         BlockPos blockPos = pos.offset(value, i2);
         IBlockState blockState2 = worldIn.getBlockState(blockPos);
         if (blockState2.getBlock() == Blocks.tripwire_hook) {
            if (blockState2.getValue(FACING) == value.getOpposite()) {
               i = i2;
            }
            break;
         }

         if (blockState2.getBlock() != Blocks.tripwire && i2 != changedWireIndex) {
            iBlockStates[i2] = null;
            flag2 = false;
         } else {
            if (i2 == changedWireIndex) {
               blockState2 = (IBlockState)Objects.firstNonNull(changedWireState, blockState2);
            }

            boolean flag4 = !blockState2.getValue(BlockTripWire.DISARMED);
            boolean value4 = blockState2.getValue(BlockTripWire.POWERED);
            boolean value5 = blockState2.getValue(BlockTripWire.SUSPENDED);
            flag2 &= value5 == flag;
            flag3 |= flag4 && value4;
            iBlockStates[i2] = blockState2;
            if (i2 == changedWireIndex) {
               worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
               flag2 &= flag4;
            }
         }
      }

      flag2 &= i > 1;
      flag3 &= flag2;
      IBlockState iBlockState = this.getDefaultState().withProperty(ATTACHED, flag2).withProperty(POWERED, flag3);
      if (i > 0) {
         BlockPos blockPos2 = pos.offset(value, i);
         EnumFacing opposite = value.getOpposite();
         worldIn.setBlockState(blockPos2, iBlockState.withProperty(FACING, opposite), 3);
         this.notifyHookNeighbors(worldIn, blockPos2, opposite);
         this.playAttachDetachSound(worldIn, blockPos2, flag2, flag3, value2, value3);
      }

      this.playAttachDetachSound(worldIn, pos, flag2, flag3, value2, value3);
      if (!skipOwnStateUpdate) {
         worldIn.setBlockState(pos, iBlockState.withProperty(FACING, value), 3);
         if (notifyNeighbors) {
            this.notifyHookNeighbors(worldIn, pos, value);
         }
      }

      if (value2 != flag2) {
         for (int i3 = 1; i3 < i; i3++) {
            BlockPos blockPos3 = pos.offset(value, i3);
            IBlockState iBlockState2 = iBlockStates[i3];
            if (iBlockState2 != null && worldIn.getBlockState(blockPos3).getBlock() != Blocks.air) {
               worldIn.setBlockState(blockPos3, iBlockState2.withProperty(ATTACHED, flag2), 3);
            }
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (neighborBlock != this && this.checkForDrop(worldIn, pos, state)) {
         EnumFacing facing = state.getValue(FACING);
         if (!worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock().isNormalCube()) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
         }
      }
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, FACING, POWERED, ATTACHED, SUSPENDED);
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      this.updateTripWireState(worldIn, pos, state, false, false, -1, (IBlockState)null);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(SUSPENDED, !World.doesBlockHaveSolidTopSurface(worldIn, pos.down()));
   }

   private boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.canPlaceBlockAt(worldIn, pos)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState blockState = this.getDefaultState().withProperty(POWERED, false).withProperty(ATTACHED, false).withProperty(SUSPENDED, false);
      if (facing.getAxis().isHorizontal()) {
         blockState = blockState.withProperty(FACING, facing);
      }

      return blockState;
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return state.getValue(POWERED) ? 15 : 0;
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return !state.getValue(POWERED) ? 0 : (state.getValue(FACING) == side ? 15 : 0);
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
         if (worldIn.getBlockState(pos.offset(enumFacing)).getBlock().isNormalCube()) {
            return true;
         }
      }

      return false;
   }

   private void playAttachDetachSound(World worldIn, BlockPos pos, boolean attached, boolean powered, boolean wasAttached, boolean wasPowered) {
      if (powered && !wasPowered) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.click", 0.4F, 0.6F);
      } else if (!powered && wasPowered) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.click", 0.4F, 0.5F);
      } else if (attached && !wasAttached) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.click", 0.4F, 0.7F);
      } else if (!attached && wasAttached) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.bowhit", 0.4F, 1.2F / (worldIn.rand.nextFloat() * 0.2F + 0.9F));
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(FACING, EnumFacing.getHorizontal(meta & 3))
         .withProperty(POWERED, (meta & 8) > 0)
         .withProperty(ATTACHED, (meta & 4) > 0);
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      boolean conditionMet = state.getValue(ATTACHED);
      boolean conditionMet2 = state.getValue(POWERED);
      if (conditionMet || conditionMet2) {
         this.updateTripWireState(worldIn, pos, state, true, false, -1, (IBlockState)null);
      }

      if (conditionMet2) {
         worldIn.notifyNeighborsOfStateChange(pos, this);
         worldIn.notifyNeighborsOfStateChange(pos.offset(state.getValue(FACING).getOpposite()), this);
      }

      super.breakBlock(worldIn, pos, state);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      float f = 0.1875F;
      switch ((EnumFacing)worldIn.getBlockState(pos).getValue(FACING)) {
         case EAST:
            this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
            break;
         case WEST:
            this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
            break;
         case SOUTH:
            this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
            break;
         case NORTH:
            this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
      }
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT_MIPPED;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0 | state.getValue(FACING).getHorizontalIndex();
      if (state.getValue(POWERED)) {
         i |= 8;
      }

      if (state.getValue(ATTACHED)) {
         i |= 4;
      }

      return i;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      return side.getAxis().isHorizontal() && worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock().isNormalCube();
   }

   public BlockTripWireHook() {
      super(Material.circuits);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(POWERED, false)
            .withProperty(ATTACHED, false)
            .withProperty(SUSPENDED, false)
      );
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.setTickRandomly(true);
   }
}
