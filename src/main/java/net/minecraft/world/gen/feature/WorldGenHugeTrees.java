package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class WorldGenHugeTrees extends WorldGenAbstractTree {
   protected final int baseHeight;
   protected int extraRandomHeight;
   protected final IBlockState woodMetadata;
   protected final IBlockState leavesMetadata;

   protected int getHeight(Random rand) {
      int i = rand.nextInt(3) + this.baseHeight;
      if (this.extraRandomHeight > 1) {
         i += rand.nextInt(this.extraRandomHeight);
      }

      return i;
   }

   public WorldGenHugeTrees(boolean notify, int baseHeight, int extraRandomHeight, IBlockState woodState, IBlockState leavesState) {
      super(notify);
      this.baseHeight = baseHeight;
      this.extraRandomHeight = extraRandomHeight;
      this.woodMetadata = woodState;
      this.leavesMetadata = leavesState;
   }

   protected void generateLeafCircle(World worldIn, BlockPos position, int radius) {
      int i = radius * radius;

      for (int innerIndex = -radius; innerIndex <= radius; innerIndex++) {
         for (int nestedIndex = -radius; nestedIndex <= radius; nestedIndex++) {
            if (innerIndex * innerIndex + nestedIndex * nestedIndex <= i) {
               BlockPos blockPos = position.add(innerIndex, 0, nestedIndex);
               Material material = worldIn.getBlockState(blockPos).getBlock().getMaterial();
               if (material == Material.air || material == Material.leaves) {
                  this.setBlockAndNotifyAdequately(worldIn, blockPos, this.leavesMetadata);
               }
            }
         }
      }
   }

   protected void generateLeafLayer(World worldIn, BlockPos position, int radius) {
      int i = radius * radius;

      for (int innerIndex = -radius; innerIndex <= radius + 1; innerIndex++) {
         for (int nestedIndex = -radius; nestedIndex <= radius + 1; nestedIndex++) {
            int i2 = innerIndex - 1;
            int i3 = nestedIndex - 1;
            if (innerIndex * innerIndex + nestedIndex * nestedIndex <= i
               || i2 * i2 + i3 * i3 <= i
               || innerIndex * innerIndex + i3 * i3 <= i
               || i2 * i2 + nestedIndex * nestedIndex <= i) {
               BlockPos blockPos = position.add(innerIndex, 0, nestedIndex);
               Material material = worldIn.getBlockState(blockPos).getBlock().getMaterial();
               if (material == Material.air || material == Material.leaves) {
                  this.setBlockAndNotifyAdequately(worldIn, blockPos, this.leavesMetadata);
               }
            }
         }
      }
   }

   protected boolean canGenerateTree(World worldIn, Random rand, BlockPos position, int height) {
      return this.hasSpaceForTree(worldIn, position, height) && this.setDirtAtBase(position, worldIn);
   }

   private boolean setDirtAtBase(BlockPos position, World worldIn) {
      BlockPos blockPos = position.down();
      Block block = worldIn.getBlockState(blockPos).getBlock();
      if ((block == Blocks.grass || block == Blocks.dirt) && position.getY() >= 2) {
         this.setDirtAt(worldIn, blockPos);
         this.setDirtAt(worldIn, blockPos.east());
         this.setDirtAt(worldIn, blockPos.south());
         this.setDirtAt(worldIn, blockPos.south().east());
         return true;
      } else {
         return false;
      }
   }

   private boolean hasSpaceForTree(World worldIn, BlockPos position, int height) {
      boolean conditionMet = true;
      if (position.getY() >= 1 && position.getY() + height + 1 <= 256) {
         for (int index = 0; index <= 1 + height; index++) {
            int i = 2;
            if (index == 0) {
               i = 1;
            } else if (index >= 1 + height - 2) {
               i = 2;
            }

            for (int nestedIndex = -i; nestedIndex <= i && conditionMet; nestedIndex++) {
               for (int outerIndex = -i; outerIndex <= i && conditionMet; outerIndex++) {
                  if (position.getY() + index < 0
                     || position.getY() + index >= 256
                     || !this.canGrowInto(worldIn.getBlockState(position.add(nestedIndex, index, outerIndex)).getBlock())) {
                     conditionMet = false;
                  }
               }
            }
         }

         return conditionMet;
      } else {
         return false;
      }
   }
}
