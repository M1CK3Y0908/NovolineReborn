package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRailBase extends Block {
   protected final boolean isPowered;

   public static boolean isRailBlock(World worldIn, BlockPos pos) {
      return isRailBlock(worldIn.getBlockState(pos));
   }

   protected BlockRailBase(boolean isPowered) {
      super(Material.circuits);
      this.isPowered = isPowered;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      this.setCreativeTab(CreativeTabs.tabTransport);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      if (state.getValue(this.getShapeProperty()).isAscending()) {
         worldIn.notifyNeighborsOfStateChange(pos.up(), this);
      }

      if (this.isPowered) {
         worldIn.notifyNeighborsOfStateChange(pos, this);
         worldIn.notifyNeighborsOfStateChange(pos.down(), this);
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   public static boolean isRailBlock(IBlockState state) {
      Block block = state.getBlock();
      return block == Blocks.rail || block == Blocks.golden_rail || block == Blocks.detector_rail || block == Blocks.activator_rail;
   }

   @Override
   public int getMobilityFlag() {
      return 0;
   }

   protected void onNeighborChangedInternal(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote) {
         state = this.getStateForPlacement(worldIn, pos, state, true);
         if (this.isPowered) {
            this.onNeighborBlockChange(worldIn, pos, state, this);
         }
      }
   }

   public abstract IProperty<BlockRailBase.EnumRailDirection> getShapeProperty();

   protected IBlockState getStateForPlacement(World worldIn, BlockPos pos, IBlockState state, boolean update) {
      return worldIn.isRemote
         ? state
         : new BlockRailBase.Rail(worldIn, pos, state).determineAndUpdateState(worldIn.isBlockPowered(pos), update).getBlockState();
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.collisionRayTrace(worldIn, pos, start, end);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote) {
         BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = state.getValue(this.getShapeProperty());
         boolean conditionMet = false;
         if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down())) {
            conditionMet = true;
         }

         if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.ASCENDING_EAST && !World.doesBlockHaveSolidTopSurface(worldIn, pos.east())) {
            conditionMet = true;
         } else if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.ASCENDING_WEST
            && !World.doesBlockHaveSolidTopSurface(worldIn, pos.west())) {
            conditionMet = true;
         } else if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.ASCENDING_NORTH
            && !World.doesBlockHaveSolidTopSurface(worldIn, pos.north())) {
            conditionMet = true;
         } else if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.ASCENDING_SOUTH
            && !World.doesBlockHaveSolidTopSurface(worldIn, pos.south())) {
            conditionMet = true;
         }

         if (conditionMet) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
         } else {
            this.onNeighborChangedInternal(worldIn, pos, state, neighborBlock);
         }
      }
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = blockState.getBlock() == this ? blockState.getValue(this.getShapeProperty()) : null;
      if (blockrailbase$enumraildirection != null && blockrailbase$enumraildirection.isAscending()) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      }
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   public enum EnumRailDirection implements IStringSerializable {
      NORTH_SOUTH(0, "north_south"),
      EAST_WEST(1, "east_west"),
      ASCENDING_EAST(2, "ascending_east"),
      ASCENDING_WEST(3, "ascending_west"),
      ASCENDING_NORTH(4, "ascending_north"),
      ASCENDING_SOUTH(5, "ascending_south"),
      SOUTH_EAST(6, "south_east"),
      SOUTH_WEST(7, "south_west"),
      NORTH_WEST(8, "north_west"),
      NORTH_EAST(9, "north_east");

      private final int meta;
      private final String name;
      private static final BlockRailBase.EnumRailDirection[] META_LOOKUP = new BlockRailBase.EnumRailDirection[values().length];

      static {
         for (BlockRailBase.EnumRailDirection enumRailDirection : values()) {
            META_LOOKUP[enumRailDirection.getMetadata()] = enumRailDirection;
         }
      }

      public boolean isAscending() {
         return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
      }

      @Override
      public String getName() {
         return this.name;
      }

      EnumRailDirection(int i, String text) {
         this.meta = i;
         this.name = text;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockRailBase.EnumRailDirection byMetadata(int meta) {
         if (meta < 0 || meta >= META_LOOKUP.length) {
            meta = 0;
         }

         return META_LOOKUP[meta];
      }

      public int getMetadata() {
         return this.meta;
      }
   }

   public class Rail {
      private final List<BlockPos> connectedPositions = Lists.newArrayList();
      private final World world;
      private final BlockPos pos;
      private IBlockState state;
      private final BlockRailBase block;
      private final boolean isPowered;

      private boolean isInConnectedList(BlockPos pos) {
         for (int index = 0; index < this.connectedPositions.size(); index++) {
            BlockPos blockPos = this.connectedPositions.get(index);
            if (blockPos.getX() == pos.getX() && blockPos.getZ() == pos.getZ()) {
               return true;
            }
         }

         return false;
      }

      private boolean hasRailAt(BlockPos pos) {
         return BlockRailBase.isRailBlock(this.world, pos)
            || BlockRailBase.isRailBlock(this.world, pos.up())
            || BlockRailBase.isRailBlock(this.world, pos.down());
      }

      private BlockRailBase.Rail findRailAt(BlockPos pos) {
         IBlockState blockState2 = this.world.getBlockState(pos);
         if (BlockRailBase.isRailBlock(blockState2)) {
            return BlockRailBase.this.new Rail(this.world, pos, blockState2);
         }

         BlockPos blockPos5 = pos.up();
         blockState2 = this.world.getBlockState(blockPos5);
         if (BlockRailBase.isRailBlock(blockState2)) {
            return BlockRailBase.this.new Rail(this.world, blockPos5, blockState2);
         }

         blockPos5 = pos.down();
         blockState2 = this.world.getBlockState(blockPos5);
         return BlockRailBase.isRailBlock(blockState2) ? BlockRailBase.this.new Rail(this.world, blockPos5, blockState2) : null;
      }

      private void updateConnectedRails(BlockRailBase.EnumRailDirection direction) {
         this.connectedPositions.clear();
         switch (direction) {
            case NORTH_SOUTH:
               this.connectedPositions.add(this.pos.north());
               this.connectedPositions.add(this.pos.south());
               break;
            case EAST_WEST:
               this.connectedPositions.add(this.pos.west());
               this.connectedPositions.add(this.pos.east());
               break;
            case ASCENDING_EAST:
               this.connectedPositions.add(this.pos.west());
               this.connectedPositions.add(this.pos.east().up());
               break;
            case ASCENDING_WEST:
               this.connectedPositions.add(this.pos.west().up());
               this.connectedPositions.add(this.pos.east());
               break;
            case ASCENDING_NORTH:
               this.connectedPositions.add(this.pos.north().up());
               this.connectedPositions.add(this.pos.south());
               break;
            case ASCENDING_SOUTH:
               this.connectedPositions.add(this.pos.north());
               this.connectedPositions.add(this.pos.south().up());
               break;
            case SOUTH_EAST:
               this.connectedPositions.add(this.pos.east());
               this.connectedPositions.add(this.pos.south());
               break;
            case SOUTH_WEST:
               this.connectedPositions.add(this.pos.west());
               this.connectedPositions.add(this.pos.south());
               break;
            case NORTH_WEST:
               this.connectedPositions.add(this.pos.west());
               this.connectedPositions.add(this.pos.north());
               break;
            case NORTH_EAST:
               this.connectedPositions.add(this.pos.east());
               this.connectedPositions.add(this.pos.north());
         }
      }

      private boolean canConnectTo(BlockRailBase.Rail rail) {
         return this.isConnectedTo(rail) || this.connectedPositions.size() != 2;
      }

      private void determineStateAndConnect(BlockRailBase.Rail rail) {
         this.connectedPositions.add(rail.pos);
         BlockPos blockPos = this.pos.north();
         BlockPos blockPos2 = this.pos.south();
         BlockPos blockPos3 = this.pos.west();
         BlockPos blockPos4 = this.pos.east();
         boolean isInConnectedList = this.isInConnectedList(blockPos);
         boolean isInConnectedList2 = this.isInConnectedList(blockPos2);
         boolean isInConnectedList3 = this.isInConnectedList(blockPos3);
         boolean isInConnectedList4 = this.isInConnectedList(blockPos4);
         BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = null;
         if (isInConnectedList || isInConnectedList2) {
            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         if (isInConnectedList3 || isInConnectedList4) {
            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.EAST_WEST;
         }

         if (!this.isPowered) {
            if (isInConnectedList2 && isInConnectedList4 && !isInConnectedList && !isInConnectedList3) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
            }

            if (isInConnectedList2 && isInConnectedList3 && !isInConnectedList && !isInConnectedList4) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
            }

            if (isInConnectedList && isInConnectedList3 && !isInConnectedList2 && !isInConnectedList4) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
            }

            if (isInConnectedList && isInConnectedList4 && !isInConnectedList2 && !isInConnectedList3) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
            }
         }

         if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            if (BlockRailBase.isRailBlock(this.world, blockPos.up())) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_NORTH;
            }

            if (BlockRailBase.isRailBlock(this.world, blockPos2.up())) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
            }
         }

         if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.EAST_WEST) {
            if (BlockRailBase.isRailBlock(this.world, blockPos4.up())) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_EAST;
            }

            if (BlockRailBase.isRailBlock(this.world, blockPos3.up())) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_WEST;
            }
         }

         if (blockrailbase$enumraildirection == null) {
            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         this.state = this.state.withProperty(this.block.getShapeProperty(), blockrailbase$enumraildirection);
         this.world.setBlockState(this.pos, this.state, 3);
      }

      public Rail(World worldIn, BlockPos pos, IBlockState state) {
         this.world = worldIn;
         this.pos = pos;
         this.state = state;
         this.block = (BlockRailBase)state.getBlock();
         BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = state.getValue(BlockRailBase.this.getShapeProperty());
         this.isPowered = this.block.isPowered;
         this.updateConnectedRails(blockrailbase$enumraildirection);
      }

      public BlockRailBase.Rail determineAndUpdateState(boolean powered, boolean update) {
         BlockPos blockPos = this.pos.north();
         BlockPos blockPos2 = this.pos.south();
         BlockPos blockPos3 = this.pos.west();
         BlockPos blockPos4 = this.pos.east();
         boolean conditionMet = this.tryConnectTo(blockPos);
         boolean conditionMet2 = this.tryConnectTo(blockPos2);
         boolean conditionMet3 = this.tryConnectTo(blockPos3);
         boolean conditionMet4 = this.tryConnectTo(blockPos4);
         BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = null;
         if ((conditionMet || conditionMet2) && !conditionMet3 && !conditionMet4) {
            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         if ((conditionMet3 || conditionMet4) && !conditionMet && !conditionMet2) {
            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.EAST_WEST;
         }

         if (!this.isPowered) {
            if (conditionMet2 && conditionMet4 && !conditionMet && !conditionMet3) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
            }

            if (conditionMet2 && conditionMet3 && !conditionMet && !conditionMet4) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
            }

            if (conditionMet && conditionMet3 && !conditionMet2 && !conditionMet4) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
            }

            if (conditionMet && conditionMet4 && !conditionMet2 && !conditionMet3) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
            }
         }

         if (blockrailbase$enumraildirection == null) {
            if (conditionMet || conditionMet2) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            if (conditionMet3 || conditionMet4) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.EAST_WEST;
            }

            if (!this.isPowered) {
               if (powered) {
                  if (conditionMet2 && conditionMet4) {
                     blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                  }

                  if (conditionMet3 && conditionMet2) {
                     blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                  }

                  if (conditionMet4 && conditionMet) {
                     blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
                  }

                  if (conditionMet && conditionMet3) {
                     blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
                  }
               } else {
                  if (conditionMet && conditionMet3) {
                     blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
                  }

                  if (conditionMet4 && conditionMet) {
                     blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
                  }

                  if (conditionMet3 && conditionMet2) {
                     blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                  }

                  if (conditionMet2 && conditionMet4) {
                     blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                  }
               }
            }
         }

         if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            if (BlockRailBase.isRailBlock(this.world, blockPos.up())) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_NORTH;
            }

            if (BlockRailBase.isRailBlock(this.world, blockPos2.up())) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
            }
         }

         if (blockrailbase$enumraildirection == BlockRailBase.EnumRailDirection.EAST_WEST) {
            if (BlockRailBase.isRailBlock(this.world, blockPos4.up())) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_EAST;
            }

            if (BlockRailBase.isRailBlock(this.world, blockPos3.up())) {
               blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.ASCENDING_WEST;
            }
         }

         if (blockrailbase$enumraildirection == null) {
            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         this.updateConnectedRails(blockrailbase$enumraildirection);
         this.state = this.state.withProperty(this.block.getShapeProperty(), blockrailbase$enumraildirection);
         if (update || this.world.getBlockState(this.pos) != this.state) {
            this.world.setBlockState(this.pos, this.state, 3);

            for (int index = 0; index < this.connectedPositions.size(); index++) {
               BlockRailBase.Rail blockrailbase$rail = this.findRailAt(this.connectedPositions.get(index));
               if (blockrailbase$rail != null) {
                  blockrailbase$rail.refreshConnectedRails();
                  if (blockrailbase$rail.canConnectTo(this)) {
                     blockrailbase$rail.determineStateAndConnect(this);
                  }
               }
            }
         }

         return this;
      }

      private boolean tryConnectTo(BlockPos pos) {
         BlockRailBase.Rail blockrailbase$rail = this.findRailAt(pos);
         if (blockrailbase$rail == null) {
            return false;
         }

         blockrailbase$rail.refreshConnectedRails();
         return blockrailbase$rail.canConnectTo(this);
      }

      private void refreshConnectedRails() {
         for (int index = 0; index < this.connectedPositions.size(); index++) {
            BlockRailBase.Rail blockrailbase$rail = this.findRailAt(this.connectedPositions.get(index));
            if (blockrailbase$rail != null && blockrailbase$rail.isConnectedTo(this)) {
               this.connectedPositions.set(index, blockrailbase$rail.pos);
            } else {
               this.connectedPositions.remove(index--);
            }
         }
      }

      private boolean isConnectedTo(BlockRailBase.Rail rail) {
         return this.isInConnectedList(rail.pos);
      }

      public IBlockState getBlockState() {
         return this.state;
      }

      protected int countAdjacentRails() {
         int i = 0;

         for (EnumFacing enumFacing : Plane.HORIZONTAL) {
            if (this.hasRailAt(this.pos.offset(enumFacing))) {
               i++;
            }
         }

         return i;
      }
   }
}
