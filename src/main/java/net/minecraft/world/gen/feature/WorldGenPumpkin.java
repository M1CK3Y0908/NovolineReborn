package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenPumpkin extends WorldGenerator {
   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      for (int index = 0; index < 64; index++) {
         BlockPos blockPos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (worldIn.isAirBlock(blockPos)
            && worldIn.getBlockState(blockPos.down()).getBlock() == Blocks.grass
            && Blocks.pumpkin.canPlaceBlockAt(worldIn, blockPos)) {
            worldIn.setBlockState(blockPos, Blocks.pumpkin.getDefaultState().withProperty(BlockPumpkin.FACING, net.minecraft.util.EnumFacing.Plane.HORIZONTAL.random(rand)), 2);
         }
      }

      return true;
   }
}
