package net.minecraft.world.gen.feature;

import com.google.common.base.Predicates;
import java.util.Random;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenDesertWells extends WorldGenerator {
   private final IBlockState sandstoneSlabState = Blocks.stone_slab
      .getDefaultState()
      .withProperty(BlockStoneSlab.VARIANT, net.minecraft.block.BlockStoneSlab.EnumType.SAND)
      .withProperty(BlockSlab.HALF, net.minecraft.block.BlockSlab.EnumBlockHalf.BOTTOM);
   private final IBlockState sandstoneState = Blocks.sandstone.getDefaultState();
   private static final BlockStateHelper SAND_MATCHER = BlockStateHelper.forBlock(Blocks.sand)
      .where(BlockSand.VARIANT, Predicates.equalTo(net.minecraft.block.BlockSand.EnumType.SAND));
   private final IBlockState waterState = Blocks.flowing_water.getDefaultState();

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      while (worldIn.isAirBlock(position) && position.getY() > 2) {
         position = position.down();
      }

      if (!SAND_MATCHER.apply(worldIn.getBlockState(position))) {
         return false;
      }

      for (int i = -2; i <= 2; i++) {
         for (int i2 = -2; i2 <= 2; i2++) {
            if (worldIn.isAirBlock(position.add(i, -1, i2)) && worldIn.isAirBlock(position.add(i, -2, i2))) {
               return false;
            }
         }
      }

      for (int i3 = -1; i3 <= 0; i3++) {
         for (int i4 = -2; i4 <= 2; i4++) {
            for (int i5 = -2; i5 <= 2; i5++) {
               worldIn.setBlockState(position.add(i4, i3, i5), this.sandstoneState, 2);
            }
         }
      }

      worldIn.setBlockState(position, this.waterState, 2);

      for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
         worldIn.setBlockState(position.offset(enumFacing), this.waterState, 2);
      }

      for (int i6 = -2; i6 <= 2; i6++) {
         for (int i7 = -2; i7 <= 2; i7++) {
            if (i6 == -2 || i6 == 2 || i7 == -2 || i7 == 2) {
               worldIn.setBlockState(position.add(i6, 1, i7), this.sandstoneState, 2);
            }
         }
      }

      worldIn.setBlockState(position.add(2, 1, 0), this.sandstoneSlabState, 2);
      worldIn.setBlockState(position.add(-2, 1, 0), this.sandstoneSlabState, 2);
      worldIn.setBlockState(position.add(0, 1, 2), this.sandstoneSlabState, 2);
      worldIn.setBlockState(position.add(0, 1, -2), this.sandstoneSlabState, 2);

      for (int i8 = -1; i8 <= 1; i8++) {
         for (int i9 = -1; i9 <= 1; i9++) {
            if (i8 == 0 && i9 == 0) {
               worldIn.setBlockState(position.add(i8, 4, i9), this.sandstoneState, 2);
            } else {
               worldIn.setBlockState(position.add(i8, 4, i9), this.sandstoneSlabState, 2);
            }
         }
      }

      for (int i10 = 1; i10 <= 3; i10++) {
         worldIn.setBlockState(position.add(-1, i10, -1), this.sandstoneState, 2);
         worldIn.setBlockState(position.add(-1, i10, 1), this.sandstoneState, 2);
         worldIn.setBlockState(position.add(1, i10, -1), this.sandstoneState, 2);
         worldIn.setBlockState(position.add(1, i10, 1), this.sandstoneState, 2);
      }

      return true;
   }
}
