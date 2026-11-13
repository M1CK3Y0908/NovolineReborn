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

public class WorldGenTaiga1 extends WorldGenAbstractTree {
   private static final IBlockState SPRUCE_LOG = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, net.minecraft.block.BlockPlanks.EnumType.SPRUCE);
   private static final IBlockState SPRUCE_LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, net.minecraft.block.BlockPlanks.EnumType.SPRUCE)
      .withProperty(BlockLeaves.CHECK_DECAY, false);

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(5) + 7;
      int i2 = i - rand.nextInt(2) - 3;
      int i3 = i - i2;
      int i4 = 1 + rand.nextInt(i3 + 1);
      boolean conditionMet = true;
      if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
         for (int index = position.getY(); index <= position.getY() + 1 + i && conditionMet; index++) {
            int i5;
            if (index - position.getY() < i2) {
               i5 = 0;
            } else {
               i5 = i4;
            }

            net.minecraft.util.BlockPos.MutableBlockPos index5 = new net.minecraft.util.BlockPos.MutableBlockPos();

            for (int nestedIndex = position.getX() - i5; nestedIndex <= position.getX() + i5 && conditionMet; nestedIndex++) {
               for (int outerIndex = position.getZ() - i5; outerIndex <= position.getZ() + i5 && conditionMet; outerIndex++) {
                  if (index < 0 || index >= 256) {
                     conditionMet = false;
                  } else if (!this.canGrowInto(worldIn.getBlockState(index5.set(nestedIndex, index, outerIndex)).getBlock())) {
                     conditionMet = false;
                  }
               }
            }
         }

         if (!conditionMet) {
            return false;
         }

         Block block = worldIn.getBlockState(position.down()).getBlock();
         if ((block == Blocks.grass || block == Blocks.dirt) && position.getY() < 256 - i - 1) {
            this.setDirtAt(worldIn, position.down());
            int i6 = 0;

            for (int index2 = position.getY() + i; index2 >= position.getY() + i2; index2--) {
               for (int index3 = position.getX() - i6; index3 <= position.getX() + i6; index3++) {
                  int xCoordinate = index3 - position.getX();

                  for (int index4 = position.getZ() - i6; index4 <= position.getZ() + i6; index4++) {
                     int zCoordinate = index4 - position.getZ();
                     if (Math.abs(xCoordinate) != i6 || Math.abs(zCoordinate) != i6 || i6 <= 0) {
                        BlockPos blockPos = new BlockPos(index3, index2, index4);
                        if (!worldIn.getBlockState(blockPos).getBlock().isFullBlock()) {
                           this.setBlockAndNotifyAdequately(worldIn, blockPos, SPRUCE_LEAVES);
                        }
                     }
                  }
               }

               if (i6 >= 1 && index2 == position.getY() + i2 + 1) {
                  i6--;
               } else if (i6 < i4) {
                  i6++;
               }
            }

            for (int index5 = 0; index5 < i - 1; index5++) {
               Block block2 = worldIn.getBlockState(position.up(index5)).getBlock();
               if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
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

   public WorldGenTaiga1() {
      super(false);
   }
}
