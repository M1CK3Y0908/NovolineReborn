package net.minecraft.block;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockDynamicLiquid extends BlockLiquid {
   int adjacentSourceBlocks;

   private void tryFlowInto(World worldIn, BlockPos pos, IBlockState state, int level) {
      if (this.canFlowInto(worldIn, pos, state)) {
         if (state.getBlock() != Blocks.air) {
            if (this.blockMaterial == Material.lava) {
               this.triggerMixEffects(worldIn, pos);
            } else {
               state.getBlock().dropBlockAsItem(worldIn, pos, state, 0);
            }
         }

         worldIn.setBlockState(pos, this.getDefaultState().withProperty(LEVEL, level), 3);
      }
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      int value = state.getValue(LEVEL);
      byte b = 1;
      if (this.blockMaterial == Material.lava && !worldIn.provider.doesWaterVaporize()) {
         b = 2;
      }

      int i = this.tickRate(worldIn);
      if (value > 0) {
         int i2 = -100;
         this.adjacentSourceBlocks = 0;

         for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
            i2 = this.checkAdjacentBlock(worldIn, pos.offset(enumFacing), i2);
         }

         int i3 = i2 + b;
         if (i3 >= 8 || i2 < 0) {
            i3 = -1;
         }

         if (this.getLevel(worldIn, pos.up()) >= 0) {
            int level2 = this.getLevel(worldIn, pos.up());
            if (level2 >= 8) {
               i3 = level2;
            } else {
               i3 = level2 + 8;
            }
         }

         if (this.adjacentSourceBlocks >= 2 && this.blockMaterial == Material.water) {
            IBlockState blockState = worldIn.getBlockState(pos.down());
            if (blockState.getBlock().getMaterial().isSolid()) {
               i3 = 0;
            } else if (blockState.getBlock().getMaterial() == this.blockMaterial && blockState.getValue(LEVEL) == 0) {
               i3 = 0;
            }
         }

         if (this.blockMaterial == Material.lava && value < 8 && i3 < 8 && i3 > value && rand.nextInt(4) != 0) {
            i *= 4;
         }

         if (i3 == value) {
            this.placeStaticBlock(worldIn, pos, state);
         } else {
            value = i3;
            if (i3 < 0) {
               worldIn.setBlockToAir(pos);
            } else {
               state = state.withProperty(LEVEL, i3);
               worldIn.setBlockState(pos, state, 2);
               worldIn.scheduleUpdate(pos, this, i);
               worldIn.notifyNeighborsOfStateChange(pos, this);
            }
         }
      } else {
         this.placeStaticBlock(worldIn, pos, state);
      }

      IBlockState blockState2 = worldIn.getBlockState(pos.down());
      if (this.canFlowInto(worldIn, pos.down(), blockState2)) {
         if (this.blockMaterial == Material.lava && worldIn.getBlockState(pos.down()).getBlock().getMaterial() == Material.water) {
            worldIn.setBlockState(pos.down(), Blocks.stone.getDefaultState());
            this.triggerMixEffects(worldIn, pos.down());
            return;
         }

         if (value >= 8) {
            this.tryFlowInto(worldIn, pos.down(), blockState2, value);
         } else {
            this.tryFlowInto(worldIn, pos.down(), blockState2, value + 8);
         }
      } else if (value >= 0 && (value == 0 || this.isBlocked(worldIn, pos.down(), blockState2))) {
         Set possibleFlowDirections = this.getPossibleFlowDirections(worldIn, pos);
         int i4 = value + b;
         if (value >= 8) {
            i4 = 1;
         }

         if (i4 >= 8) {
            return;
         }

         for (EnumFacing enumFacing2 : (Iterable<EnumFacing>) possibleFlowDirections) {
            this.tryFlowInto(worldIn, pos.offset(enumFacing2), worldIn.getBlockState(pos.offset(enumFacing2)), i4);
         }
      }
   }

   private int calculateFlowCost(World worldIn, BlockPos pos, int distance, EnumFacing excludeFacing) {
      int i = 1000;

      for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
         if (enumFacing != excludeFacing) {
            BlockPos blockPos = pos.offset(enumFacing);
            IBlockState blockState = worldIn.getBlockState(blockPos);
            if (!this.isBlocked(worldIn, blockPos, blockState) && (blockState.getBlock().getMaterial() != this.blockMaterial || blockState.getValue(LEVEL) > 0)) {
               if (!this.isBlocked(worldIn, blockPos.down(), blockState)) {
                  return distance;
               }

               if (distance < 4) {
                  int i2 = this.calculateFlowCost(worldIn, blockPos, distance + 1, enumFacing.getOpposite());
                  if (i2 < i) {
                     i = i2;
                  }
               }
            }
         }
      }

      return i;
   }

   protected int checkAdjacentBlock(World worldIn, BlockPos pos, int currentMinLevel) {
      int level2 = this.getLevel(worldIn, pos);
      if (level2 < 0) {
         return currentMinLevel;
      }

      if (level2 == 0) {
         this.adjacentSourceBlocks++;
      }

      if (level2 >= 8) {
         level2 = 0;
      }

      return currentMinLevel >= 0 && level2 >= currentMinLevel ? currentMinLevel : level2;
   }

   private void placeStaticBlock(World worldIn, BlockPos pos, IBlockState currentState) {
      worldIn.setBlockState(pos, getStaticBlock(this.blockMaterial).getDefaultState().withProperty(LEVEL, currentState.getValue(LEVEL)), 2);
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.checkForMixing(worldIn, pos, state)) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
      }
   }

   private Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos) {
      int i = 1000;
      EnumSet enumSet = EnumSet.noneOf(EnumFacing.class);

      for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
         BlockPos blockPos = pos.offset(enumFacing);
         IBlockState blockState = worldIn.getBlockState(blockPos);
         if (!this.isBlocked(worldIn, blockPos, blockState) && (blockState.getBlock().getMaterial() != this.blockMaterial || blockState.getValue(LEVEL) > 0)) {
            int i2;
            if (this.isBlocked(worldIn, blockPos.down(), worldIn.getBlockState(blockPos.down()))) {
               i2 = this.calculateFlowCost(worldIn, blockPos, 1, enumFacing.getOpposite());
            } else {
               i2 = 0;
            }

            if (i2 < i) {
               enumSet.clear();
            }

            if (i2 <= i) {
               enumSet.add(enumFacing);
               i = i2;
            }
         }
      }

      return enumSet;
   }

   protected BlockDynamicLiquid(Material materialIn) {
      super(materialIn);
   }

   private boolean isBlocked(World worldIn, BlockPos pos, IBlockState state) {
      Block block = worldIn.getBlockState(pos).getBlock();
      return !(block instanceof BlockDoor) && block != Blocks.standing_sign && block != Blocks.ladder && block != Blocks.reeds
         ? (block.blockMaterial == Material.portal ? true : block.blockMaterial.blocksMovement())
         : true;
   }

   private boolean canFlowInto(World worldIn, BlockPos pos, IBlockState state) {
      Material material = state.getBlock().getMaterial();
      return material != this.blockMaterial && material != Material.lava && !this.isBlocked(worldIn, pos, state);
   }
}
