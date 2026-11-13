package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaJungle extends WorldGenHugeTrees {
   private void handleWorld(World world2, BlockPos blockPos5) {
      for (int i = -2; i <= 0; i++) {
         this.generateLeafLayer(world2, blockPos5.up(i), 3 - i);
      }
   }

   private void placeVine(World worldIn, Random rand, BlockPos position, PropertyBool attachment) {
      if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position)) {
         this.setBlockAndNotifyAdequately(worldIn, position, Blocks.vine.getDefaultState().withProperty(attachment, true));
      }
   }

   public WorldGenMegaJungle(boolean notify, int baseHeight, int extraRandomHeight, IBlockState woodState, IBlockState leavesState) {
      super(notify, baseHeight, extraRandomHeight, woodState, leavesState);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int height = this.getHeight(rand);
      if (!this.canGenerateTree(worldIn, rand, position, height)) {
         return false;
      }

      this.handleWorld(worldIn, position.up(height));

      for (int innerIndex = position.getY() + height - 2 - rand.nextInt(4); innerIndex > position.getY() + height / 2; innerIndex -= 2 + rand.nextInt(4)) {
         float f = rand.nextFloat() * (float) Math.PI * 2.0F;
         int xCoordinate = position.getX() + (int)(0.5F + MathHelper.cos(f) * 4.0F);
         int zCoordinate = position.getZ() + (int)(0.5F + MathHelper.sin(f) * 4.0F);

         for (int index = 0; index < 5; index++) {
            xCoordinate = position.getX() + (int)(1.5F + MathHelper.cos(f) * index);
            zCoordinate = position.getZ() + (int)(1.5F + MathHelper.sin(f) * index);
            this.setBlockAndNotifyAdequately(worldIn, new BlockPos(xCoordinate, innerIndex - 3 + index / 2, zCoordinate), this.woodMetadata);
         }

         int i = 1 + rand.nextInt(2);
         int innerIndex2 = innerIndex;

         for (int nestedIndex = innerIndex - i; nestedIndex <= innerIndex2; nestedIndex++) {
            int i2 = nestedIndex - innerIndex2;
            this.generateLeafCircle(worldIn, new BlockPos(xCoordinate, nestedIndex, zCoordinate), 1 - i2);
         }
      }

      for (int index2 = 0; index2 < height; index2++) {
         BlockPos blockPos = position.up(index2);
         if (this.canGrowInto(worldIn.getBlockState(blockPos).getBlock())) {
            this.setBlockAndNotifyAdequately(worldIn, blockPos, this.woodMetadata);
            if (index2 > 0) {
               this.placeVine(worldIn, rand, blockPos.west(), BlockVine.EAST);
               this.placeVine(worldIn, rand, blockPos.north(), BlockVine.SOUTH);
            }
         }

         if (index2 < height - 1) {
            BlockPos blockPos2 = blockPos.east();
            if (this.canGrowInto(worldIn.getBlockState(blockPos2).getBlock())) {
               this.setBlockAndNotifyAdequately(worldIn, blockPos2, this.woodMetadata);
               if (index2 > 0) {
                  this.placeVine(worldIn, rand, blockPos2.east(), BlockVine.WEST);
                  this.placeVine(worldIn, rand, blockPos2.north(), BlockVine.SOUTH);
               }
            }

            BlockPos blockPos3 = blockPos.south().east();
            if (this.canGrowInto(worldIn.getBlockState(blockPos3).getBlock())) {
               this.setBlockAndNotifyAdequately(worldIn, blockPos3, this.woodMetadata);
               if (index2 > 0) {
                  this.placeVine(worldIn, rand, blockPos3.east(), BlockVine.WEST);
                  this.placeVine(worldIn, rand, blockPos3.south(), BlockVine.NORTH);
               }
            }

            BlockPos blockPos4 = blockPos.south();
            if (this.canGrowInto(worldIn.getBlockState(blockPos4).getBlock())) {
               this.setBlockAndNotifyAdequately(worldIn, blockPos4, this.woodMetadata);
               if (index2 > 0) {
                  this.placeVine(worldIn, rand, blockPos4.west(), BlockVine.EAST);
                  this.placeVine(worldIn, rand, blockPos4.south(), BlockVine.NORTH);
               }
            }
         }
      }

      return true;
   }
}
