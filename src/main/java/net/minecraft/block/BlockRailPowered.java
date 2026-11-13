package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockRailPowered extends BlockRailBase {
   public static final PropertyEnum<BlockRailBase.EnumRailDirection> SHAPE = PropertyEnum.create(
      "shape",
      BlockRailBase.EnumRailDirection.class,
      new Predicate<BlockRailBase.EnumRailDirection>() {
         public boolean apply(BlockRailBase.EnumRailDirection arg1) {
            return arg1 != BlockRailBase.EnumRailDirection.NORTH_EAST
               && arg1 != BlockRailBase.EnumRailDirection.NORTH_WEST
               && arg1 != BlockRailBase.EnumRailDirection.SOUTH_EAST
               && arg1 != BlockRailBase.EnumRailDirection.SOUTH_WEST;
         }
      }
   );
   public static final PropertyBool POWERED = PropertyBool.create("powered");

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta & 7)).withProperty(POWERED, (meta & 8) > 0);
   }

   protected boolean isPoweredAlongRail(World worldIn, BlockPos pos, IBlockState state, boolean forward, int distance) {
      if (distance >= 8) {
         return false;
      }

      int xCoordinate = pos.getX();
      int yCoordinate = pos.getY();
      int zCoordinate = pos.getZ();
      boolean conditionMet = true;
      BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = state.getValue(SHAPE);
      switch (blockrailbase$enumraildirection) {
         case NORTH_SOUTH:
            if (forward) {
               zCoordinate++;
            } else {
               zCoordinate--;
            }
            break;
         case EAST_WEST:
            if (forward) {
               xCoordinate--;
            } else {
               xCoordinate++;
            }
            break;
         case ASCENDING_EAST:
            if (forward) {
               xCoordinate--;
            } else {
               xCoordinate++;
               yCoordinate++;
               conditionMet = false;
            }

            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.EAST_WEST;
            break;
         case ASCENDING_WEST:
            if (forward) {
               xCoordinate--;
               yCoordinate++;
               conditionMet = false;
            } else {
               xCoordinate++;
            }

            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.EAST_WEST;
            break;
         case ASCENDING_NORTH:
            if (forward) {
               zCoordinate++;
            } else {
               zCoordinate--;
               yCoordinate++;
               conditionMet = false;
            }

            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            break;
         case ASCENDING_SOUTH:
            if (forward) {
               zCoordinate++;
               yCoordinate++;
               conditionMet = false;
            } else {
               zCoordinate--;
            }

            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
      }

      return this.isConnectedToPoweredRail(worldIn, new BlockPos(xCoordinate, yCoordinate, zCoordinate), forward, distance, blockrailbase$enumraildirection)
         ? true
         : conditionMet
            && this.isConnectedToPoweredRail(
               worldIn, new BlockPos(xCoordinate, yCoordinate - 1, zCoordinate), forward, distance, blockrailbase$enumraildirection
            );
   }

   protected BlockRailPowered() {
      super(true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH).withProperty(POWERED, false));
   }

   @Override
   protected void onNeighborChangedInternal(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      boolean conditionMet = state.getValue(POWERED);
      boolean flag = worldIn.isBlockPowered(pos)
         || this.isPoweredAlongRail(worldIn, pos, state, true, 0)
         || this.isPoweredAlongRail(worldIn, pos, state, false, 0);
      if (flag != conditionMet) {
         worldIn.setBlockState(pos, state.withProperty(POWERED, flag), 3);
         worldIn.notifyNeighborsOfStateChange(pos.down(), this);
         if (state.getValue(SHAPE).isAscending()) {
            worldIn.notifyNeighborsOfStateChange(pos.up(), this);
         }
      }
   }

   @Override
   protected BlockState createBlockState() {
      return new BlockState(this, SHAPE, POWERED);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int i = 0 | state.getValue(SHAPE).getMetadata();
      if (state.getValue(POWERED)) {
         i |= 8;
      }

      return i;
   }

   protected boolean isConnectedToPoweredRail(World worldIn, BlockPos pos, boolean forward, int distance, BlockRailBase.EnumRailDirection searchDirection) {
      IBlockState blockState = worldIn.getBlockState(pos);
      if (blockState.getBlock() != this) {
         return false;
      }

      BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = blockState.getValue(SHAPE);
      return searchDirection != BlockRailBase.EnumRailDirection.EAST_WEST
            || blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.NORTH_SOUTH
               && blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.ASCENDING_NORTH
               && blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.ASCENDING_SOUTH
         ? (
            searchDirection != BlockRailBase.EnumRailDirection.NORTH_SOUTH
                  || blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.EAST_WEST
                     && blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.ASCENDING_EAST
                     && blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.ASCENDING_WEST
               ? (
                  blockState.getValue(POWERED)
                     ? (worldIn.isBlockPowered(pos) ? true : this.isPoweredAlongRail(worldIn, pos, blockState, forward, distance + 1))
                     : false
               )
               : false
         )
         : false;
   }

   @Override
   public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty() {
      return SHAPE;
   }
}
