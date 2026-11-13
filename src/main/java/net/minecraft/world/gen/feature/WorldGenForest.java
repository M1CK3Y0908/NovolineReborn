package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenForest extends WorldGenAbstractTree {
   private static final IBlockState BIRCH_LOG = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, net.minecraft.block.BlockPlanks.EnumType.BIRCH);
   private boolean useExtraRandomHeight;
   private static final IBlockState BIRCH_LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, net.minecraft.block.BlockPlanks.EnumType.BIRCH)
      .withProperty(BlockOldLeaf.CHECK_DECAY, false);

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(3) + 5;
      if (this.useExtraRandomHeight) {
         i += rand.nextInt(7);
      }

      boolean conditionMet = true;
      if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
         for (int innerIndex = position.getY(); innerIndex <= position.getY() + 1 + i; innerIndex++) {
            int i2 = 1;
            if (innerIndex == position.getY()) {
               i2 = 0;
            }

            if (innerIndex >= position.getY() + 1 + i - 2) {
               i2 = 2;
            }

            net.minecraft.util.BlockPos.MutableBlockPos block4 = new net.minecraft.util.BlockPos.MutableBlockPos();

            for (int outerIndex = position.getX() - i2; outerIndex <= position.getX() + i2 && conditionMet; outerIndex++) {
               for (int index = position.getZ() - i2; index <= position.getZ() + i2 && conditionMet; index++) {
                  if (innerIndex < 0 || innerIndex >= 256) {
                     conditionMet = false;
                  } else if (!this.canGrowInto(worldIn.getBlockState(block4.set(outerIndex, innerIndex, index)).getBlock())) {
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

            for (int index2 = position.getY() - 3 + i; index2 <= position.getY() + i; index2++) {
               int yCoordinate = index2 - (position.getY() + i);
               int i3 = 1 - yCoordinate / 2;

               for (int index3 = position.getX() - i3; index3 <= position.getX() + i3; index3++) {
                  int xCoordinate = index3 - position.getX();

                  for (int nestedIndex = position.getZ() - i3; nestedIndex <= position.getZ() + i3; nestedIndex++) {
                     int zCoordinate = nestedIndex - position.getZ();
                     if (Math.abs(xCoordinate) != i3 || Math.abs(zCoordinate) != i3 || rand.nextInt(2) != 0 && yCoordinate != 0) {
                        BlockPos blockPos = new BlockPos(index3, index2, nestedIndex);
                        Block block = worldIn.getBlockState(blockPos).getBlock();
                        if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
                           this.setBlockAndNotifyAdequately(worldIn, blockPos, BIRCH_LEAVES);
                        }
                     }
                  }
               }
            }

            for (int index4 = 0; index4 < i; index4++) {
               Block block4 = worldIn.getBlockState(position.up(index4)).getBlock();
               if (block4.getMaterial() == Material.air || block4.getMaterial() == Material.leaves) {
                  this.setBlockAndNotifyAdequately(worldIn, position.up(index4), BIRCH_LOG);
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

   public WorldGenForest(boolean notify, boolean useExtraRandomHeight) {
      super(notify);
      this.useExtraRandomHeight = useExtraRandomHeight;
   }
}
