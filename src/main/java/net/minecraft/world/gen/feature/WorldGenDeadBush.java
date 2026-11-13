package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenDeadBush extends WorldGenerator {
   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      Block block2;
      while (
         ((block2 = worldIn.getBlockState(position).getBlock()).getMaterial() == Material.air || block2.getMaterial() == Material.leaves) && position.getY() > 0
      ) {
         position = position.down();
      }

      for (int index = 0; index < 4; index++) {
         BlockPos blockPos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (worldIn.isAirBlock(blockPos) && Blocks.deadbush.canBlockStay(worldIn, blockPos, Blocks.deadbush.getDefaultState())) {
            worldIn.setBlockState(blockPos, Blocks.deadbush.getDefaultState(), 2);
         }
      }

      return true;
   }
}
