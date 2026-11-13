package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneWire extends Block {
   public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> NORTH = PropertyEnum.create("north", BlockRedstoneWire.EnumAttachPosition.class);
   private boolean canProvidePower = true;
   public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> EAST = PropertyEnum.create("east", BlockRedstoneWire.EnumAttachPosition.class);
   public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> SOUTH = PropertyEnum.create("south", BlockRedstoneWire.EnumAttachPosition.class);
   public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> WEST = PropertyEnum.create("west", BlockRedstoneWire.EnumAttachPosition.class);
   private final Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();
   public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);

   private BlockRedstoneWire.EnumAttachPosition getAttachPosition(IBlockAccess worldIn, BlockPos pos, EnumFacing direction) {
      BlockPos blockPos = pos.offset(direction);
      Block block2 = worldIn.getBlockState(pos.offset(direction)).getBlock();
      if (!canConnectTo(worldIn.getBlockState(blockPos), direction) && (block2.isBlockNormalCube() || !canConnectUpwardsTo(worldIn.getBlockState(blockPos.down())))) {
         return !worldIn.getBlockState(pos.up()).getBlock().isBlockNormalCube()
               && block2.isBlockNormalCube()
               && canConnectUpwardsTo(worldIn.getBlockState(blockPos.up()))
            ? BlockRedstoneWire.EnumAttachPosition.UP
            : BlockRedstoneWire.EnumAttachPosition.NONE;
      } else {
         return BlockRedstoneWire.EnumAttachPosition.SIDE;
      }
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) || worldIn.getBlockState(pos.down()).getBlock() == Blocks.glowstone;
   }

   public BlockRedstoneWire() {
      super(Material.circuits);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(NORTH, BlockRedstoneWire.EnumAttachPosition.NONE)
            .withProperty(EAST, BlockRedstoneWire.EnumAttachPosition.NONE)
            .withProperty(SOUTH, BlockRedstoneWire.EnumAttachPosition.NONE)
            .withProperty(WEST, BlockRedstoneWire.EnumAttachPosition.NONE)
            .withProperty(POWER, 0)
      );
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
   }

   private IBlockState calculateCurrentChanges(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state) {
      IBlockState state2 = state;
      int value = state.getValue(POWER);
      int maxCurrentStrength = this.getMaxCurrentStrength(worldIn, pos2, 0);
      this.canProvidePower = false;
      int blockIndirectlyGettingPowered = worldIn.isBlockIndirectlyGettingPowered(pos1);
      this.canProvidePower = true;
      if (blockIndirectlyGettingPowered > 0 && blockIndirectlyGettingPowered > maxCurrentStrength - 1) {
         maxCurrentStrength = blockIndirectlyGettingPowered;
      }

      int i = 0;

      for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockPos = pos1.offset(enumFacing);
         boolean flag = blockPos.getX() != pos2.getX() || blockPos.getZ() != pos2.getZ();
         if (flag) {
            i = this.getMaxCurrentStrength(worldIn, blockPos, i);
         }

         if (worldIn.getBlockState(blockPos).getBlock().isNormalCube() && !worldIn.getBlockState(pos1.up()).getBlock().isNormalCube()) {
            if (flag && pos1.getY() >= pos2.getY()) {
               i = this.getMaxCurrentStrength(worldIn, blockPos.up(), i);
            }
         } else if (!worldIn.getBlockState(blockPos).getBlock().isNormalCube() && flag && pos1.getY() <= pos2.getY()) {
            i = this.getMaxCurrentStrength(worldIn, blockPos.down(), i);
         }
      }

      if (i > maxCurrentStrength) {
         maxCurrentStrength = i - 1;
      } else if (maxCurrentStrength > 0) {
         maxCurrentStrength--;
      } else {
         maxCurrentStrength = 0;
      }

      if (blockIndirectlyGettingPowered > maxCurrentStrength - 1) {
         maxCurrentStrength = blockIndirectlyGettingPowered;
      }

      if (value != maxCurrentStrength) {
         state = state.withProperty(POWER, maxCurrentStrength);
         if (worldIn.getBlockState(pos1) == state2) {
            worldIn.setBlockState(pos1, state, 2);
         }

         this.blocksNeedingUpdate.add(pos1);

         for (EnumFacing enumFacing2 : EnumFacing.values()) {
            this.blocksNeedingUpdate.add(pos1.offset(enumFacing2));
         }
      }

      return state;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(POWER);
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return !this.canProvidePower ? 0 : this.getWeakPower(worldIn, pos, state, side);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      if (!worldIn.isRemote) {
         for (EnumFacing enumFacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }

         this.updateSurroundingRedstone(worldIn, pos, state);

         for (EnumFacing enumFacing2 : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
            this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(enumFacing2));
         }

         for (EnumFacing enumFacing3 : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockPos = pos.offset(enumFacing3);
            if (worldIn.getBlockState(blockPos).getBlock().isNormalCube()) {
               this.notifyWireNeighborsOfStateChange(worldIn, blockPos.up());
            } else {
               this.notifyWireNeighborsOfStateChange(worldIn, blockPos.down());
            }
         }
      }
   }

   protected static boolean canConnectTo(IBlockState blockState, EnumFacing side) {
      Block block = blockState.getBlock();
      if (block == Blocks.redstone_wire) {
         return true;
      } else if (Blocks.unpowered_repeater.isAssociated(block)) {
         EnumFacing facing = blockState.getValue(BlockRedstoneRepeater.FACING);
         return facing == side || facing.getOpposite() == side;
      } else {
         return block.canProvidePower() && side != null;
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.redstone;
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote) {
         this.updateSurroundingRedstone(worldIn, pos, state);

         for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.VERTICAL) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }

         for (EnumFacing enumFacing2 : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
            this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(enumFacing2));
         }

         for (EnumFacing enumFacing3 : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockPos = pos.offset(enumFacing3);
            if (worldIn.getBlockState(blockPos).getBlock().isNormalCube()) {
               this.notifyWireNeighborsOfStateChange(worldIn, blockPos.up());
            } else {
               this.notifyWireNeighborsOfStateChange(worldIn, blockPos.down());
            }
         }
      }
   }

   private IBlockState updateSurroundingRedstone(World worldIn, BlockPos pos, IBlockState state) {
      state = this.calculateCurrentChanges(worldIn, pos, pos, state);
      ArrayList arrayList = Lists.newArrayList(this.blocksNeedingUpdate);
      this.blocksNeedingUpdate.clear();

      for (BlockPos blockPos : (Iterable<BlockPos>) arrayList) {
         worldIn.notifyNeighborsOfStateChange(blockPos, this);
      }

      return state;
   }

   private void notifyWireNeighborsOfStateChange(World worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).getBlock() == this) {
         worldIn.notifyNeighborsOfStateChange(pos, this);

         for (EnumFacing enumFacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }
      }
   }

   @Override
   public boolean canProvidePower() {
      return this.canProvidePower;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote) {
         if (this.canPlaceBlockAt(worldIn, pos)) {
            this.updateSurroundingRedstone(worldIn, pos, state);
         } else {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
         }
      }
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.redstone;
   }

   protected static boolean canConnectUpwardsTo(IBlockAccess worldIn, BlockPos pos) {
      return canConnectUpwardsTo(worldIn.getBlockState(pos));
   }

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      IBlockState blockState = worldIn.getBlockState(pos);
      return blockState.getBlock() != this ? super.colorMultiplier(worldIn, pos, renderPass) : this.colorMultiplier(blockState.getValue(POWER));
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   private int getMaxCurrentStrength(World worldIn, BlockPos pos, int strength) {
      if (worldIn.getBlockState(pos).getBlock() != this) {
         return strength;
      }

      int value = worldIn.getBlockState(pos).getValue(POWER);
      return value > strength ? value : strength;
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      if (!this.canProvidePower) {
         return 0;
      }

      int value = state.getValue(POWER);
      if (value == 0) {
         return 0;
      }

      if (side == EnumFacing.UP) {
         return value;
      }

      EnumSet enumSet = EnumSet.noneOf(EnumFacing.class);

      for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
         if (this.canProvidePowerTo(worldIn, pos, enumFacing)) {
            enumSet.add(enumFacing);
         }
      }

      if (side.getAxis().isHorizontal() && enumSet.isEmpty()) {
         return value;
      } else {
         return enumSet.contains(side) && !enumSet.contains(side.rotateYCCW()) && !enumSet.contains(side.rotateY()) ? value : 0;
      }
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      int value = state.getValue(POWER);
      if (value != 0) {
         double xCoordinate = pos.getX() + 0.5 + (rand.nextFloat() - 0.5) * 0.2;
         double yCoordinate = pos.getY() + 0.0625F;
         double zCoordinate = pos.getZ() + 0.5 + (rand.nextFloat() - 0.5) * 0.2;
         float f = value / 15.0F;
         float f2 = f * 0.6F + 0.4F;
         float f3 = Math.max(0.0F, f * f * 0.7F - 0.5F);
         float f4 = Math.max(0.0F, f * f * 0.6F - 0.7F);
         worldIn.spawnParticle(EnumParticleTypes.REDSTONE, xCoordinate, yCoordinate, zCoordinate, f2, f3, f4);
      }
   }

   private int colorMultiplier(int powerLevel) {
      float f = powerLevel / 15.0F;
      float f2 = f * 0.6F + 0.4F;
      if (powerLevel == 0) {
         f2 = 0.3F;
      }

      float f3 = f * f * 0.7F - 0.5F;
      float f4 = f * f * 0.6F - 0.7F;
      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      if (f4 < 0.0F) {
         f4 = 0.0F;
      }

      int i = MathHelper.clamp_int((int)(f2 * 255.0F), 0, 255);
      int i2 = MathHelper.clamp_int((int)(f3 * 255.0F), 0, 255);
      int i3 = MathHelper.clamp_int((int)(f4 * 255.0F), 0, 255);
      return 0xFF000000 | i << 16 | i2 << 8 | i3;
   }

   private boolean canProvidePowerTo(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      BlockPos blockPos = pos.offset(side);
      IBlockState blockState2 = worldIn.getBlockState(blockPos);
      Block block2 = blockState2.getBlock();
      boolean normalCube = block2.isNormalCube();
      return !worldIn.getBlockState(pos.up()).getBlock().isNormalCube() && normalCube && canConnectUpwardsTo(worldIn, blockPos.up())
         ? true
         : (
            canConnectTo(blockState2, side)
               ? true
               : (
                  block2 == Blocks.powered_repeater && blockState2.getValue(BlockRedstoneDiode.FACING) == side
                     ? true
                     : !normalCube && canConnectUpwardsTo(worldIn, blockPos.down())
               )
         );
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(POWER, meta);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      state = state.withProperty(WEST, this.getAttachPosition(worldIn, pos, EnumFacing.WEST));
      state = state.withProperty(EAST, this.getAttachPosition(worldIn, pos, EnumFacing.EAST));
      state = state.withProperty(NORTH, this.getAttachPosition(worldIn, pos, EnumFacing.NORTH));
      return state.withProperty(SOUTH, this.getAttachPosition(worldIn, pos, EnumFacing.SOUTH));
   }

   protected static boolean canConnectUpwardsTo(IBlockState state) {
      return canConnectTo(state, (EnumFacing)null);
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, NORTH, EAST, SOUTH, WEST, POWER);
   }

   enum EnumAttachPosition implements IStringSerializable {
      UP("up"),
      SIDE("side"),
      NONE("none");

      private final String name;

      @Override
      public String getName() {
         return this.name;
      }

      @Override
      public String toString() {
         return this.getName();
      }

      EnumAttachPosition(String text) {
         this.name = text;
      }
   }
}
