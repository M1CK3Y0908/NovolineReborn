package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenWaterlily extends WorldGenerator {
   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      for (int index = 0; index < 10; index++) {
         int xCoordinate = position.getX() + rand.nextInt(8) - rand.nextInt(8);
         int yCoordinate = position.getY() + rand.nextInt(4) - rand.nextInt(4);
         int zCoordinate = position.getZ() + rand.nextInt(8) - rand.nextInt(8);
         if (worldIn.isAirBlock(new BlockPos(xCoordinate, yCoordinate, zCoordinate))
            && Blocks.waterlily.canPlaceBlockAt(worldIn, new BlockPos(xCoordinate, yCoordinate, zCoordinate))) {
            worldIn.setBlockState(new BlockPos(xCoordinate, yCoordinate, zCoordinate), Blocks.waterlily.getDefaultState(), 2);
         }
      }

      return true;
   }
}
