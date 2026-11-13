package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenIcePath extends WorldGenerator {
   private Block block = Blocks.packed_ice;
   private int basePathWidth = 4;

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      while (worldIn.isAirBlock(position) && position.getY() > 2) {
         position = position.down();
      }

      if (worldIn.getBlockState(position).getBlock() != Blocks.snow) {
         return false;
      }

      int i = rand.nextInt(this.basePathWidth - 2) + 2;

      for (int i2 = position.getX() - i; i2 <= position.getX() + i; i2++) {
         for (int i3 = position.getZ() - i; i3 <= position.getZ() + i; i3++) {
            int i4 = i2 - position.getX();
            int i5 = i3 - position.getZ();
            if (i4 * i4 + i5 * i5 <= i * i) {
               for (int i6 = position.getY() - 1; i6 <= position.getY() + 1; i6++) {
                  BlockPos blockPos = new BlockPos(i2, i6, i3);
                  Block block2 = worldIn.getBlockState(blockPos).getBlock();
                  if (block2 == Blocks.dirt || block2 == Blocks.snow || block2 == Blocks.ice) {
                     worldIn.setBlockState(blockPos, this.block.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      return true;
   }
}
