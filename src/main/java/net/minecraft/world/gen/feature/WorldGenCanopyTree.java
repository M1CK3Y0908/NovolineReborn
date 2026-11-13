package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenCanopyTree extends WorldGenAbstractTree {
   private static final IBlockState DARK_OAK_LOG = Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, net.minecraft.block.BlockPlanks.EnumType.DARK_OAK);
   private static final IBlockState DARK_OAK_LEAVES = Blocks.leaves2
      .getDefaultState()
      .withProperty(BlockNewLeaf.VARIANT, net.minecraft.block.BlockPlanks.EnumType.DARK_OAK)
      .withProperty(BlockLeaves.CHECK_DECAY, false);

   private void placeLogIfPossible(World worldIn, BlockPos position) {
      if (this.canGrowInto(worldIn.getBlockState(position).getBlock())) {
         this.setBlockAndNotifyAdequately(worldIn, position, DARK_OAK_LOG);
      }
   }

   private void placeLeafIfPossible(World worldIn, int x, int y, int z) {
      BlockPos blockPos = new BlockPos(x, y, z);
      if (worldIn.getBlockState(blockPos).getBlock().getMaterial() == Material.air) {
         this.setBlockAndNotifyAdequately(worldIn, blockPos, DARK_OAK_LEAVES);
      }
   }

   public WorldGenCanopyTree(boolean notify) {
      super(notify);
   }

   private boolean canGenerateTree(World worldIn, BlockPos position, int height) {
      int xCoordinate = position.getX();
      int yCoordinate = position.getY();
      int zCoordinate = position.getZ();
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int outerIndex = 0; outerIndex <= height + 1; outerIndex++) {
         int i = 1;
         if (outerIndex == 0) {
            i = 0;
         }

         if (outerIndex >= height - 1) {
            i = 2;
         }

         for (int innerIndex = -i; innerIndex <= i; innerIndex++) {
            for (int nestedIndex = -i; nestedIndex <= i; nestedIndex++) {
               if (!this.canGrowInto(worldIn.getBlockState(mutableBlockPos.set(xCoordinate + innerIndex, yCoordinate + outerIndex, zCoordinate + nestedIndex)).getBlock())
                  )
                {
                  return false;
               }
            }
         }
      }

      return true;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(3) + rand.nextInt(2) + 6;
      int x2 = position.getX();
      int y2 = position.getY();
      int z2 = position.getZ();
      if (y2 >= 1 && y2 + i + 1 < 256) {
         BlockPos blockPos = position.down();
         Block block2 = worldIn.getBlockState(blockPos).getBlock();
         if (block2 != Blocks.grass && block2 != Blocks.dirt) {
            return false;
         }

         if (!this.canGenerateTree(worldIn, position, i)) {
            return false;
         }

         this.setDirtAt(worldIn, blockPos);
         this.setDirtAt(worldIn, blockPos.east());
         this.setDirtAt(worldIn, blockPos.south());
         this.setDirtAt(worldIn, blockPos.south().east());
         EnumFacing enumFacing = net.minecraft.util.EnumFacing.Plane.HORIZONTAL.random(rand);
         int i2 = i - rand.nextInt(4);
         int i3 = 2 - rand.nextInt(3);
         int i4 = x2;
         int i5 = z2;
         int i6 = y2 + i - 1;

         for (int i7 = 0; i7 < i; i7++) {
            if (i7 >= i2 && i3 > 0) {
               i4 += enumFacing.getFrontOffsetX();
               i5 += enumFacing.getFrontOffsetZ();
               i3--;
            }

            int i8 = y2 + i7;
            BlockPos blockPos2 = new BlockPos(i4, i8, i5);
            Material material2 = worldIn.getBlockState(blockPos2).getBlock().getMaterial();
            if (material2 == Material.air || material2 == Material.leaves) {
               this.placeLogIfPossible(worldIn, blockPos2);
               this.placeLogIfPossible(worldIn, blockPos2.east());
               this.placeLogIfPossible(worldIn, blockPos2.south());
               this.placeLogIfPossible(worldIn, blockPos2.east().south());
            }
         }

         for (int i9 = -2; i9 <= 0; i9++) {
            for (int i10 = -2; i10 <= 0; i10++) {
               this.placeLeafIfPossible(worldIn, i4 + i9, i6 + -1, i5 + i10);
               this.placeLeafIfPossible(worldIn, 1 + i4 - i9, i6 + -1, i5 + i10);
               this.placeLeafIfPossible(worldIn, i4 + i9, i6 + -1, 1 + i5 - i10);
               this.placeLeafIfPossible(worldIn, 1 + i4 - i9, i6 + -1, 1 + i5 - i10);
               if ((i9 > -2 || i10 > -1) && (i9 != -1 || i10 != -2)) {
                  this.placeLeafIfPossible(worldIn, i4 + i9, i6 + 1, i5 + i10);
                  this.placeLeafIfPossible(worldIn, 1 + i4 - i9, i6 + 1, i5 + i10);
                  this.placeLeafIfPossible(worldIn, i4 + i9, i6 + 1, 1 + i5 - i10);
                  this.placeLeafIfPossible(worldIn, 1 + i4 - i9, i6 + 1, 1 + i5 - i10);
               }
            }
         }

         if (rand.nextBoolean()) {
            this.placeLeafIfPossible(worldIn, i4, i6 + 2, i5);
            this.placeLeafIfPossible(worldIn, i4 + 1, i6 + 2, i5);
            this.placeLeafIfPossible(worldIn, i4 + 1, i6 + 2, i5 + 1);
            this.placeLeafIfPossible(worldIn, i4, i6 + 2, i5 + 1);
         }

         for (int i11 = -3; i11 <= 4; i11++) {
            for (int i12 = -3; i12 <= 4; i12++) {
               if ((i11 != -3 || i12 != -3)
                  && (i11 != -3 || i12 != 4)
                  && (i11 != 4 || i12 != -3)
                  && (i11 != 4 || i12 != 4)
                  && (Math.abs(i11) < 3 || Math.abs(i12) < 3)) {
                  this.placeLeafIfPossible(worldIn, i4 + i11, i6, i5 + i12);
               }
            }
         }

         for (int i13 = -1; i13 <= 2; i13++) {
            for (int i14 = -1; i14 <= 2; i14++) {
               if ((i13 < 0 || i13 > 1 || i14 < 0 || i14 > 1) && rand.nextInt(3) <= 0) {
                  int i15 = rand.nextInt(3) + 2;

                  for (int i16 = 0; i16 < i15; i16++) {
                     this.placeLogIfPossible(worldIn, new BlockPos(x2 + i13, i6 - i16 - 1, z2 + i14));
                  }

                  for (int i17 = -1; i17 <= 1; i17++) {
                     for (int i18 = -1; i18 <= 1; i18++) {
                        this.placeLeafIfPossible(worldIn, i4 + i13 + i17, i6, i5 + i14 + i18);
                     }
                  }

                  for (int i19 = -2; i19 <= 2; i19++) {
                     for (int i20 = -2; i20 <= 2; i20++) {
                        if (Math.abs(i19) != 2 || Math.abs(i20) != 2) {
                           this.placeLeafIfPossible(worldIn, i4 + i13 + i19, i6 - 1, i5 + i14 + i20);
                        }
                     }
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
