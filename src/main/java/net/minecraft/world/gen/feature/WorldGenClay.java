package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenClay extends WorldGenerator {
   private Block clayBlock = Blocks.clay;
   private int numberOfBlocks = 4;

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (worldIn.getBlockState(position).getBlock().getMaterial() != Material.water) {
         return false;
      }

      int zCoordinate = rand.nextInt(this.numberOfBlocks - 2) + 2;

      for (int i = position.getX() - zCoordinate; i <= position.getX() + zCoordinate; i++) {
         for (int i2 = position.getZ() - zCoordinate; i2 <= position.getZ() + zCoordinate; i2++) {
            int i3 = i - position.getX();
            int i4 = i2 - position.getZ();
            if (i3 * i3 + i4 * i4 <= zCoordinate * zCoordinate) {
               for (int i5 = position.getY() - 1; i5 <= position.getY() + 1; i5++) {
                  BlockPos blockPos = new BlockPos(i, i5, i2);
                  Block block2 = worldIn.getBlockState(blockPos).getBlock();
                  if (block2 == Blocks.dirt || block2 == Blocks.clay) {
                     worldIn.setBlockState(blockPos, this.clayBlock.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      return true;
   }
}
