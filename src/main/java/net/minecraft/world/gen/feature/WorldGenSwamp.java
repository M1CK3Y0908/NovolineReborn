package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenSwamp extends WorldGenAbstractTree {
   private static final IBlockState OAK_LOG = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, net.minecraft.block.BlockPlanks.EnumType.OAK);
   private static final IBlockState OAK_LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, net.minecraft.block.BlockPlanks.EnumType.OAK)
      .withProperty(BlockOldLeaf.CHECK_DECAY, false);

   public WorldGenSwamp() {
      super(false);
   }

   private void placeVine(World worldIn, BlockPos position, PropertyBool attachment) {
      IBlockState blockState = Blocks.vine.getDefaultState().withProperty(attachment, true);
      this.setBlockAndNotifyAdequately(worldIn, position, blockState);
      int i = 4;

      for (BlockPos blockPos = position.down(); worldIn.getBlockState(blockPos).getBlock().getMaterial() == Material.air && i > 0; i--) {
         this.setBlockAndNotifyAdequately(worldIn, blockPos, blockState);
         blockPos = blockPos.down();
      }
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(4) + 5;

      while (worldIn.getBlockState(position.down()).getBlock().getMaterial() == Material.water) {
         position = position.down();
      }

      boolean conditionMet = true;
      if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
         for (int innerIndex = position.getY(); innerIndex <= position.getY() + 1 + i; innerIndex++) {
            int i2 = 1;
            if (innerIndex == position.getY()) {
               i2 = 0;
            }

            if (innerIndex >= position.getY() + 1 + i - 2) {
               i2 = 3;
            }

            net.minecraft.util.BlockPos.MutableBlockPos yCoordinate2 = new net.minecraft.util.BlockPos.MutableBlockPos();

            for (int outerIndex = position.getX() - i2; outerIndex <= position.getX() + i2 && conditionMet; outerIndex++) {
               for (int index = position.getZ() - i2; index <= position.getZ() + i2 && conditionMet; index++) {
                  if (innerIndex >= 0 && innerIndex < 256) {
                     Block block = worldIn.getBlockState(yCoordinate2.set(outerIndex, innerIndex, index)).getBlock();
                     if (block.getMaterial() != Material.air && block.getMaterial() != Material.leaves) {
                        if (block != Blocks.water && block != Blocks.flowing_water) {
                           conditionMet = false;
                        } else if (innerIndex > position.getY()) {
                           conditionMet = false;
                        }
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
         if ((block3 == Blocks.grass || block3 == Blocks.dirt) && position.getY() < 256 - i - 1) {
            this.setDirtAt(worldIn, position.down());

            for (int outerIndex2 = position.getY() - 3 + i; outerIndex2 <= position.getY() + i; outerIndex2++) {
               int yCoordinate = outerIndex2 - (position.getY() + i);
               int i3 = 2 - yCoordinate / 2;

               for (int index2 = position.getX() - i3; index2 <= position.getX() + i3; index2++) {
                  int xCoordinate = index2 - position.getX();

                  for (int innerIndex2 = position.getZ() - i3; innerIndex2 <= position.getZ() + i3; innerIndex2++) {
                     int zCoordinate = innerIndex2 - position.getZ();
                     if (Math.abs(xCoordinate) != i3 || Math.abs(zCoordinate) != i3 || rand.nextInt(2) != 0 && yCoordinate != 0) {
                        BlockPos blockPos3 = new BlockPos(index2, outerIndex2, innerIndex2);
                        if (!worldIn.getBlockState(blockPos3).getBlock().isFullBlock()) {
                           this.setBlockAndNotifyAdequately(worldIn, blockPos3, OAK_LEAVES);
                        }
                     }
                  }
               }
            }

            for (int index3 = 0; index3 < i; index3++) {
               Block block4 = worldIn.getBlockState(position.up(index3)).getBlock();
               if (block4.getMaterial() == Material.air || block4.getMaterial() == Material.leaves || block4 == Blocks.flowing_water || block4 == Blocks.water) {
                  this.setBlockAndNotifyAdequately(worldIn, position.up(index3), OAK_LOG);
               }
            }

            for (int index4 = position.getY() - 3 + i; index4 <= position.getY() + i; index4++) {
               int yCoordinate2 = index4 - (position.getY() + i);
               int i4 = 2 - yCoordinate2 / 2;
               net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

               for (int index5 = position.getX() - i4; index5 <= position.getX() + i4; index5++) {
                  for (int index6 = position.getZ() - i4; index6 <= position.getZ() + i4; index6++) {
                     mutableBlockPos.set(index5, index4, index6);
                     if (worldIn.getBlockState(mutableBlockPos).getBlock().getMaterial() == Material.leaves) {
                        BlockPos blockPos2 = mutableBlockPos.west();
                        BlockPos blockPos3 = mutableBlockPos.east();
                        BlockPos blockPos4 = mutableBlockPos.north();
                        BlockPos blockPos5 = mutableBlockPos.south();
                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockPos2).getBlock().getMaterial() == Material.air) {
                           this.placeVine(worldIn, blockPos2, BlockVine.EAST);
                        }

                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockPos3).getBlock().getMaterial() == Material.air) {
                           this.placeVine(worldIn, blockPos3, BlockVine.WEST);
                        }

                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockPos4).getBlock().getMaterial() == Material.air) {
                           this.placeVine(worldIn, blockPos4, BlockVine.SOUTH);
                        }

                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockPos5).getBlock().getMaterial() == Material.air) {
                           this.placeVine(worldIn, blockPos5, BlockVine.NORTH);
                        }
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
}
