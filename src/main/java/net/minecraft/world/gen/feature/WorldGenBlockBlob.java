package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenBlockBlob extends WorldGenerator {
   private final Block block;
   private final int radius;

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      while (true) {
         label50: {
            if (position.getY() > 3) {
               if (worldIn.isAirBlock(position.down())) {
                  break label50;
               }

               Block block2 = worldIn.getBlockState(position.down()).getBlock();
               if (block2 != Blocks.grass && block2 != Blocks.dirt && block2 != Blocks.stone) {
                  break label50;
               }
            }

            if (position.getY() <= 3) {
               return false;
            }

            int radius2 = this.radius;

            for (int i = 0; radius2 >= 0 && i < 3; i++) {
               int i2 = radius2 + rand.nextInt(2);
               int i3 = radius2 + rand.nextInt(2);
               int i4 = radius2 + rand.nextInt(2);
               float f = (i2 + i3 + i4) * 0.333F + 0.5F;

               for (BlockPos blockPos : BlockPos.getAllInBox(position.add(-i2, -i3, -i4), position.add(i2, i3, i4))) {
                  if (blockPos.distanceSq(position) <= f * f) {
                     worldIn.setBlockState(blockPos, this.block.getDefaultState(), 4);
                  }
               }

               position = position.add(-(radius2 + 1) + rand.nextInt(2 + radius2 * 2), 0 - rand.nextInt(2), -(radius2 + 1) + rand.nextInt(2 + radius2 * 2));
            }

            return true;
         }

         position = position.down();
      }
   }

   public WorldGenBlockBlob(Block block2) {
      super(false);
      this.block = block2;
      this.radius = 0;
   }
}
