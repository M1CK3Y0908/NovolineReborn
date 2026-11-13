package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenShrub extends WorldGenTrees {
   private final IBlockState woodMetadata;
   private final IBlockState leavesMetadata;

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      Block block3;
      while (
         ((block3 = worldIn.getBlockState(position).getBlock()).getMaterial() == Material.air || block3.getMaterial() == Material.leaves) && position.getY() > 0
      ) {
         position = position.down();
      }

      Block block2 = worldIn.getBlockState(position).getBlock();
      if (block2 == Blocks.dirt || block2 == Blocks.grass) {
         position = position.up();
         this.setBlockAndNotifyAdequately(worldIn, position, this.woodMetadata);

         for (int index = position.getY(); index <= position.getY() + 2; index++) {
            int yCoordinate = index - position.getY();
            int i = 2 - yCoordinate;

            for (int outerIndex = position.getX() - i; outerIndex <= position.getX() + i; outerIndex++) {
               int xCoordinate = outerIndex - position.getX();

               for (int innerIndex = position.getZ() - i; innerIndex <= position.getZ() + i; innerIndex++) {
                  int zCoordinate = innerIndex - position.getZ();
                  if (Math.abs(xCoordinate) != i || Math.abs(zCoordinate) != i || rand.nextInt(2) != 0) {
                     BlockPos blockPos = new BlockPos(outerIndex, index, innerIndex);
                     if (!worldIn.getBlockState(blockPos).getBlock().isFullBlock()) {
                        this.setBlockAndNotifyAdequately(worldIn, blockPos, this.leavesMetadata);
                     }
                  }
               }
            }
         }
      }

      return true;
   }

   public WorldGenShrub(IBlockState iBlockState, IBlockState iBlockState2) {
      super(false);
      this.woodMetadata = iBlockState;
      this.leavesMetadata = iBlockState2;
   }
}
