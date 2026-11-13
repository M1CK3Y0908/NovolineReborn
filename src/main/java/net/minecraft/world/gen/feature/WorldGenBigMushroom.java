package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenBigMushroom extends WorldGenerator {
   private Block mushroomType;

   public WorldGenBigMushroom(Block block2) {
      super(true);
      this.mushroomType = block2;
   }

   public WorldGenBigMushroom() {
      super(false);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (this.mushroomType == null) {
         this.mushroomType = rand.nextBoolean() ? Blocks.brown_mushroom_block : Blocks.red_mushroom_block;
      }

      int i = rand.nextInt(3) + 4;
      boolean conditionMet = true;
      if (position.getY() >= 1 && position.getY() + i + 1 < 256) {
         for (int innerIndex = position.getY(); innerIndex <= position.getY() + 1 + i; innerIndex++) {
            int i2 = 3;
            if (innerIndex <= position.getY() + 3) {
               i2 = 0;
            }

            net.minecraft.util.BlockPos.MutableBlockPos index4 = new net.minecraft.util.BlockPos.MutableBlockPos();

            for (int outerIndex = position.getX() - i2; outerIndex <= position.getX() + i2 && conditionMet; outerIndex++) {
               for (int index = position.getZ() - i2; index <= position.getZ() + i2 && conditionMet; index++) {
                  if (innerIndex >= 0 && innerIndex < 256) {
                     Block block = worldIn.getBlockState(index4.set(outerIndex, innerIndex, index)).getBlock();
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
         if (block3 != Blocks.dirt && block3 != Blocks.grass && block3 != Blocks.mycelium) {
            return false;
         }

         int yCoordinate = position.getY() + i;
         if (this.mushroomType == Blocks.red_mushroom_block) {
            yCoordinate = position.getY() + i - 3;
         }

         for (int index2 = yCoordinate; index2 <= position.getY() + i; index2++) {
            int i3 = 1;
            if (index2 < position.getY() + i) {
               i3++;
            }

            if (this.mushroomType == Blocks.brown_mushroom_block) {
               i3 = 3;
            }

            int xCoordinate = position.getX() - i3;
            int xCoordinate2 = position.getX() + i3;
            int zCoordinate = position.getZ() - i3;
            int zCoordinate2 = position.getZ() + i3;

            for (int outerIndex2 = xCoordinate; outerIndex2 <= xCoordinate2; outerIndex2++) {
               for (int index3 = zCoordinate; index3 <= zCoordinate2; index3++) {
                  int i4 = 5;
                  if (outerIndex2 == xCoordinate) {
                     i4--;
                  } else if (outerIndex2 == xCoordinate2) {
                     i4++;
                  }

                  if (index3 == zCoordinate) {
                     i4 -= 3;
                  } else if (index3 == zCoordinate2) {
                     i4 += 3;
                  }

                  net.minecraft.block.BlockHugeMushroom.EnumType blockhugemushroom$enumtype = net.minecraft.block.BlockHugeMushroom.EnumType.byMetadata(i4);
                  if (this.mushroomType == Blocks.brown_mushroom_block || index2 < position.getY() + i) {
                     if ((outerIndex2 == xCoordinate || outerIndex2 == xCoordinate2) && (index3 == zCoordinate || index3 == zCoordinate2)) {
                        continue;
                     }

                     if (outerIndex2 == position.getX() - (i3 - 1) && index3 == zCoordinate) {
                        blockhugemushroom$enumtype = net.minecraft.block.BlockHugeMushroom.EnumType.NORTH_WEST;
                     }

                     if (outerIndex2 == xCoordinate && index3 == position.getZ() - (i3 - 1)) {
                        blockhugemushroom$enumtype = net.minecraft.block.BlockHugeMushroom.EnumType.NORTH_WEST;
                     }

                     if (outerIndex2 == position.getX() + (i3 - 1) && index3 == zCoordinate) {
                        blockhugemushroom$enumtype = net.minecraft.block.BlockHugeMushroom.EnumType.NORTH_EAST;
                     }

                     if (outerIndex2 == xCoordinate2 && index3 == position.getZ() - (i3 - 1)) {
                        blockhugemushroom$enumtype = net.minecraft.block.BlockHugeMushroom.EnumType.NORTH_EAST;
                     }

                     if (outerIndex2 == position.getX() - (i3 - 1) && index3 == zCoordinate2) {
                        blockhugemushroom$enumtype = net.minecraft.block.BlockHugeMushroom.EnumType.SOUTH_WEST;
                     }

                     if (outerIndex2 == xCoordinate && index3 == position.getZ() + (i3 - 1)) {
                        blockhugemushroom$enumtype = net.minecraft.block.BlockHugeMushroom.EnumType.SOUTH_WEST;
                     }

                     if (outerIndex2 == position.getX() + (i3 - 1) && index3 == zCoordinate2) {
                        blockhugemushroom$enumtype = net.minecraft.block.BlockHugeMushroom.EnumType.SOUTH_EAST;
                     }

                     if (outerIndex2 == xCoordinate2 && index3 == position.getZ() + (i3 - 1)) {
                        blockhugemushroom$enumtype = net.minecraft.block.BlockHugeMushroom.EnumType.SOUTH_EAST;
                     }
                  }

                  if (blockhugemushroom$enumtype == net.minecraft.block.BlockHugeMushroom.EnumType.CENTER && index2 < position.getY() + i) {
                     blockhugemushroom$enumtype = net.minecraft.block.BlockHugeMushroom.EnumType.ALL_INSIDE;
                  }

                  if (position.getY() >= position.getY() + i - 1 || blockhugemushroom$enumtype != net.minecraft.block.BlockHugeMushroom.EnumType.ALL_INSIDE) {
                     BlockPos blockPos = new BlockPos(outerIndex2, index2, index3);
                     if (!worldIn.getBlockState(blockPos).getBlock().isFullBlock()) {
                        this.setBlockAndNotifyAdequately(
                           worldIn, blockPos, this.mushroomType.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, blockhugemushroom$enumtype)
                        );
                     }
                  }
               }
            }
         }

         for (int index4 = 0; index4 < i; index4++) {
            Block block4 = worldIn.getBlockState(position.up(index4)).getBlock();
            if (!block4.isFullBlock()) {
               this.setBlockAndNotifyAdequately(
                  worldIn, position.up(index4), this.mushroomType.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, net.minecraft.block.BlockHugeMushroom.EnumType.STEM)
               );
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
