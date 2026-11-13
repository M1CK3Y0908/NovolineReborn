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

public class WorldGenSavannaTree extends WorldGenAbstractTree {
   private static final IBlockState ACACIA_LOG = Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, net.minecraft.block.BlockPlanks.EnumType.ACACIA);
   private static final IBlockState ACACIA_LEAVES = Blocks.leaves2
      .getDefaultState()
      .withProperty(BlockNewLeaf.VARIANT, net.minecraft.block.BlockPlanks.EnumType.ACACIA)
      .withProperty(BlockLeaves.CHECK_DECAY, false);

   private void placeLog(World worldIn, BlockPos position) {
      this.setBlockAndNotifyAdequately(worldIn, position, ACACIA_LOG);
   }

   private void placeLeafIfPossible(World worldIn, BlockPos position) {
      Material material = worldIn.getBlockState(position).getBlock().getMaterial();
      if (material == Material.air || material == Material.leaves) {
         this.setBlockAndNotifyAdequately(worldIn, position, ACACIA_LEAVES);
      }
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(3) + rand.nextInt(3) + 5;
      boolean flag = true;
      if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
         for (int y = position.getY(); y <= position.getY() + 1 + i; y++) {
            byte b = 1;
            if (y == position.getY()) {
               b = 0;
            }

            if (y >= position.getY() + 1 + i - 2) {
               b = 2;
            }

            net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

            for (int i2 = position.getX() - b; i2 <= position.getX() + b && flag; i2++) {
               for (int i3 = position.getZ() - b; i3 <= position.getZ() + b && flag; i3++) {
                  if (y < 0 || y >= 256) {
                     flag = false;
                  } else if (!this.canGrowInto(worldIn.getBlockState(mutableBlockPos.set(i2, y, i3)).getBlock())) {
                     flag = false;
                  }
               }
            }
         }

         if (!flag) {
            return false;
         }

         Block block2 = worldIn.getBlockState(position.down()).getBlock();
         if ((block2 == Blocks.grass || block2 == Blocks.dirt) && position.getY() < 256 - i - 1) {
            this.setDirtAt(worldIn, position.down());
            EnumFacing enumFacing = net.minecraft.util.EnumFacing.Plane.HORIZONTAL.random(rand);
            int i4 = i - rand.nextInt(4) - 1;
            int i5 = 3 - rand.nextInt(3);
            int x = position.getX();
            int z = position.getZ();
            int i6 = 0;

            for (int i7 = 0; i7 < i; i7++) {
               int i8 = position.getY() + i7;
               if (i7 >= i4 && i5 > 0) {
                  x += enumFacing.getFrontOffsetX();
                  z += enumFacing.getFrontOffsetZ();
                  i5--;
               }

               BlockPos blockPos = new BlockPos(x, i8, z);
               Material material2 = worldIn.getBlockState(blockPos).getBlock().getMaterial();
               if (material2 == Material.air || material2 == Material.leaves) {
                  this.placeLog(worldIn, blockPos);
                  i6 = i8;
               }
            }

            BlockPos blockPos2 = new BlockPos(x, i6, z);

            for (int i9 = -3; i9 <= 3; i9++) {
               for (int i10 = -3; i10 <= 3; i10++) {
                  if (Math.abs(i9) != 3 || Math.abs(i10) != 3) {
                     this.placeLeafIfPossible(worldIn, blockPos2.add(i9, 0, i10));
                  }
               }
            }

            blockPos2 = blockPos2.up();

            for (int i11 = -1; i11 <= 1; i11++) {
               for (int i12 = -1; i12 <= 1; i12++) {
                  this.placeLeafIfPossible(worldIn, blockPos2.add(i11, 0, i12));
               }
            }

            this.placeLeafIfPossible(worldIn, blockPos2.east(2));
            this.placeLeafIfPossible(worldIn, blockPos2.west(2));
            this.placeLeafIfPossible(worldIn, blockPos2.south(2));
            this.placeLeafIfPossible(worldIn, blockPos2.north(2));
            x = position.getX();
            z = position.getZ();
            EnumFacing enumFacing2 = net.minecraft.util.EnumFacing.Plane.HORIZONTAL.random(rand);
            if (enumFacing2 != enumFacing) {
               int i13 = i4 - rand.nextInt(2) - 1;
               int i14 = 1 + rand.nextInt(3);
               i6 = 0;

               for (int i15 = i13; i15 < i && i14 > 0; i14--) {
                  if (i15 >= 1) {
                     int i16 = position.getY() + i15;
                     x += enumFacing2.getFrontOffsetX();
                     z += enumFacing2.getFrontOffsetZ();
                     BlockPos blockPos3 = new BlockPos(x, i16, z);
                     Material material3 = worldIn.getBlockState(blockPos3).getBlock().getMaterial();
                     if (material3 == Material.air || material3 == Material.leaves) {
                        this.placeLog(worldIn, blockPos3);
                        i6 = i16;
                     }
                  }

                  i15++;
               }

               if (i6 > 0) {
                  BlockPos blockPos4 = new BlockPos(x, i6, z);

                  for (int i17 = -2; i17 <= 2; i17++) {
                     for (int i18 = -2; i18 <= 2; i18++) {
                        if (Math.abs(i17) != 2 || Math.abs(i18) != 2) {
                           this.placeLeafIfPossible(worldIn, blockPos4.add(i17, 0, i18));
                        }
                     }
                  }

                  blockPos4 = blockPos4.up();

                  for (int i19 = -1; i19 <= 1; i19++) {
                     for (int i20 = -1; i20 <= 1; i20++) {
                        this.placeLeafIfPossible(worldIn, blockPos4.add(i19, 0, i20));
                     }
                  }
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

   public WorldGenSavannaTree(boolean notify) {
      super(notify);
   }
}
