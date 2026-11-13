package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IGrowable {
   boolean canUseBonemeal(World world2, Random random, BlockPos blockPos, IBlockState iBlockState);

   boolean canGrow(World world2, BlockPos blockPos, IBlockState iBlockState, boolean flag);

   void grow(World world2, Random random, BlockPos blockPos, IBlockState iBlockState);
}
