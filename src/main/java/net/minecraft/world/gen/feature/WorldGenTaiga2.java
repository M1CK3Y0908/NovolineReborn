package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenTaiga2 extends WorldGenAbstractTree {
   private static final IBlockState SPRUCE_LOG = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, net.minecraft.block.BlockPlanks.EnumType.SPRUCE);
   private static final IBlockState SPRUCE_LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, net.minecraft.block.BlockPlanks.EnumType.SPRUCE)
      .withProperty(BlockLeaves.CHECK_DECAY, false);

   public WorldGenTaiga2(boolean notify) {
      super(notify);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(4) + 6;
      int i2 = 1 + rand.nextInt(2);
      int i3 = i - i2;
      int i4 = 2 + rand.nextInt(2);
      boolean conditionMet = true;
      if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
         for (int index = position.getY(); index <= position.getY() + 1 + i && conditionMet; index++) {
            int i5;
            if (index - position.getY() < i2) {
               i5 = 0;
            } else {
               i5 = i4;
            }

            net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

            for (int nestedIndex = position.getX() - i5; nestedIndex <= position.getX() + i5 && conditionMet; nestedIndex++) {
               for (int outerIndex = position.getZ() - i5; outerIndex <= position.getZ() + i5 && conditionMet; outerIndex++) {
                  if (index >= 0 && index < 256) {
                     Block block = worldIn.getBlockState(mutableBlockPos.set(nestedIndex, index, outerIndex)).getBlock();
                     if (block.getMaterial() != Material.air && block.getMaterial() != Material.leaves) {
                        conditionMet = false;
                     }
                  } else {
                     conditionMet = false;
                  }
               }
            }
         }

         if (!conditionMet) {
            return false;
         }

         Block block3 = worldIn.getBlockState(position.down()).getBlock();
         if ((block3 == Blocks.grass || block3 == Blocks.dirt || block3 == Blocks.farmland) && position.getY() < 256 - i - 1) {
            this.setDirtAt(worldIn, position.down());
            int i6 = rand.nextInt(2);
            int i7 = 1;
            int i8 = 0;

            for (int index2 = 0; index2 <= i3; index2++) {
               int yCoordinate = position.getY() + i - index2;

               for (int index3 = position.getX() - i6; index3 <= position.getX() + i6; index3++) {
                  int xCoordinate = index3 - position.getX();

                  for (int index4 = position.getZ() - i6; index4 <= position.getZ() + i6; index4++) {
                     int zCoordinate = index4 - position.getZ();
                     if (Math.abs(xCoordinate) != i6 || Math.abs(zCoordinate) != i6 || i6 <= 0) {
                        BlockPos blockPos = new BlockPos(index3, yCoordinate, index4);
                        if (!worldIn.getBlockState(blockPos).getBlock().isFullBlock()) {
                           this.setBlockAndNotifyAdequately(worldIn, blockPos, SPRUCE_LEAVES);
                        }
                     }
                  }
               }

               if (i6 >= i7) {
                  i6 = i8;
                  i8 = 1;
                  if (++i7 > i4) {
                     i7 = i4;
                  }
               } else {
                  i6++;
               }
            }

            int i9 = rand.nextInt(3);

            for (int index5 = 0; index5 < i - i9; index5++) {
               Block block4 = worldIn.getBlockState(position.up(index5)).getBlock();
               if (block4.getMaterial() == Material.air || block4.getMaterial() == Material.leaves) {
                  this.setBlockAndNotifyAdequately(worldIn, position.up(index5), SPRUCE_LOG);
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
