package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenIceSpike extends WorldGenerator {
   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      while (worldIn.isAirBlock(position) && position.getY() > 2) {
         position = position.down();
      }

      if (worldIn.getBlockState(position).getBlock() != Blocks.snow) {
         return false;
      }

      position = position.up(rand.nextInt(4));
      int i = rand.nextInt(4) + 7;
      int i2 = i / 4 + rand.nextInt(2);
      if (i2 > 1 && rand.nextInt(60) == 0) {
         position = position.up(10 + rand.nextInt(30));
      }

      for (int i3 = 0; i3 < i; i3++) {
         float f = (1.0F - (float)i3 / i) * i2;
         int i4 = MathHelper.ceiling_float_int(f);

         for (int i5 = -i4; i5 <= i4; i5++) {
            float f2 = MathHelper.abs_int(i5) - 0.25F;

            for (int i6 = -i4; i6 <= i4; i6++) {
               float f3 = MathHelper.abs_int(i6) - 0.25F;
               if ((i5 == 0 && i6 == 0 || f2 * f2 + f3 * f3 <= f * f)
                  && (i5 != -i4 && i5 != i4 && i6 != -i4 && i6 != i4 || rand.nextFloat() <= 0.75F)) {
                  Block block2 = worldIn.getBlockState(position.add(i5, i3, i6)).getBlock();
                  if (block2.getMaterial() == Material.air || block2 == Blocks.dirt || block2 == Blocks.snow || block2 == Blocks.ice) {
                     this.setBlockAndNotifyAdequately(worldIn, position.add(i5, i3, i6), Blocks.packed_ice.getDefaultState());
                  }

                  if (i3 != 0 && i4 > 1) {
                     block2 = worldIn.getBlockState(position.add(i5, -i3, i6)).getBlock();
                     if (block2.getMaterial() == Material.air || block2 == Blocks.dirt || block2 == Blocks.snow || block2 == Blocks.ice) {
                        this.setBlockAndNotifyAdequately(worldIn, position.add(i5, -i3, i6), Blocks.packed_ice.getDefaultState());
                     }
                  }
               }
            }
         }
      }

      int i7 = i2 - 1;
      if (i7 < 0) {
         i7 = 0;
      } else if (i7 > 1) {
         i7 = 1;
      }

      for (int i8 = -i7; i8 <= i7; i8++) {
         for (int i9 = -i7; i9 <= i7; i9++) {
            BlockPos blockPos = position.add(i8, -1, i9);
            int i10 = 50;
            if (Math.abs(i8) == 1 && Math.abs(i9) == 1) {
               i10 = rand.nextInt(5);
            }

            while (blockPos.getY() > 50) {
               Block block3 = worldIn.getBlockState(blockPos).getBlock();
               if (block3.getMaterial() != Material.air && block3 != Blocks.dirt && block3 != Blocks.snow && block3 != Blocks.ice && block3 != Blocks.packed_ice) {
                  break;
               }

               this.setBlockAndNotifyAdequately(worldIn, blockPos, Blocks.packed_ice.getDefaultState());
               blockPos = blockPos.down();
               if (--i10 <= 0) {
                  blockPos = blockPos.down(rand.nextInt(5) + 1);
                  i10 = rand.nextInt(5);
               }
            }
         }
      }

      return true;
   }
}
