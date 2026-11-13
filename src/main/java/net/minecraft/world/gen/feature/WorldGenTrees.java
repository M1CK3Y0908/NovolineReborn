package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenTrees extends WorldGenAbstractTree {
   private final int minTreeHeight;
   private final IBlockState metaWood;
   private final IBlockState metaLeaves;
   private static final IBlockState OAK_LOG = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, net.minecraft.block.BlockPlanks.EnumType.OAK);
   private final boolean vinesGrow;
   private static final IBlockState OAK_LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, net.minecraft.block.BlockPlanks.EnumType.OAK)
      .withProperty(BlockLeaves.CHECK_DECAY, false);

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = rand.nextInt(3) + this.minTreeHeight;
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
         if ((block2 == Blocks.grass || block2 == Blocks.dirt || block2 == Blocks.farmland) && position.getY() < 256 - i - 1) {
            this.setDirtAt(worldIn, position.down());

            for (int i4 = position.getY() - 3 + i; i4 <= position.getY() + i; i4++) {
               int i5 = i4 - (position.getY() + i);
               int i6 = 1 - i5 / 2;

               for (int i7 = position.getX() - i6; i7 <= position.getX() + i6; i7++) {
                  int i8 = i7 - position.getX();

                  for (int i9 = position.getZ() - i6; i9 <= position.getZ() + i6; i9++) {
                     int i10 = i9 - position.getZ();
                     if (Math.abs(i8) != i6 || Math.abs(i10) != i6 || rand.nextInt(2) != 0 && i5 != 0) {
                        BlockPos blockPos = new BlockPos(i7, i4, i9);
                        Block block3 = worldIn.getBlockState(blockPos).getBlock();
                        if (block3.getMaterial() == Material.air || block3.getMaterial() == Material.leaves || block3.getMaterial() == Material.vine) {
                           this.setBlockAndNotifyAdequately(worldIn, blockPos, this.metaLeaves);
                        }
                     }
                  }
               }
            }

            for (int i11 = 0; i11 < i; i11++) {
               Block block4 = worldIn.getBlockState(position.up(i11)).getBlock();
               if (block4.getMaterial() == Material.air || block4.getMaterial() == Material.leaves || block4.getMaterial() == Material.vine) {
                  this.setBlockAndNotifyAdequately(worldIn, position.up(i11), this.metaWood);
                  if (this.vinesGrow && i11 > 0) {
                     if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(-1, i11, 0))) {
                        this.placeVine(worldIn, position.add(-1, i11, 0), BlockVine.EAST);
                     }

                     if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(1, i11, 0))) {
                        this.placeVine(worldIn, position.add(1, i11, 0), BlockVine.WEST);
                     }

                     if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, i11, -1))) {
                        this.placeVine(worldIn, position.add(0, i11, -1), BlockVine.SOUTH);
                     }

                     if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, i11, 1))) {
                        this.placeVine(worldIn, position.add(0, i11, 1), BlockVine.NORTH);
                     }
                  }
               }
            }

            if (this.vinesGrow) {
               for (int i12 = position.getY() - 3 + i; i12 <= position.getY() + i; i12++) {
                  int i13 = i12 - (position.getY() + i);
                  int i14 = 2 - i13 / 2;
                  net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos2 = new net.minecraft.util.BlockPos.MutableBlockPos();

                  for (int i15 = position.getX() - i14; i15 <= position.getX() + i14; i15++) {
                     for (int i16 = position.getZ() - i14; i16 <= position.getZ() + i14; i16++) {
                        mutableBlockPos2.set(i15, i12, i16);
                        if (worldIn.getBlockState(mutableBlockPos2).getBlock().getMaterial() == Material.leaves) {
                           BlockPos blockPos2 = mutableBlockPos2.west();
                           BlockPos blockPos3 = mutableBlockPos2.east();
                           BlockPos blockPos4 = mutableBlockPos2.north();
                           BlockPos blockPos5 = mutableBlockPos2.south();
                           if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockPos2).getBlock().getMaterial() == Material.air) {
                              this.growVineColumn(worldIn, blockPos2, BlockVine.EAST);
                           }

                           if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockPos3).getBlock().getMaterial() == Material.air) {
                              this.growVineColumn(worldIn, blockPos3, BlockVine.WEST);
                           }

                           if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockPos4).getBlock().getMaterial() == Material.air) {
                              this.growVineColumn(worldIn, blockPos4, BlockVine.SOUTH);
                           }

                           if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockPos5).getBlock().getMaterial() == Material.air) {
                              this.growVineColumn(worldIn, blockPos5, BlockVine.NORTH);
                           }
                        }
                     }
                  }
               }

               if (rand.nextInt(5) == 0 && i > 5) {
                  for (int i17 = 0; i17 < 2; i17++) {
                     for (EnumFacing enumFacing : net.minecraft.util.EnumFacing.Plane.HORIZONTAL) {
                        if (rand.nextInt(4 - i17) == 0) {
                           EnumFacing opposite = enumFacing.getOpposite();
                           this.placeCocoa(
                              worldIn, rand.nextInt(3), position.add(opposite.getFrontOffsetX(), i - 5 + i17, opposite.getFrontOffsetZ()), enumFacing
                           );
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

   private void placeVine(World worldIn, BlockPos position, PropertyBool attachment) {
      this.setBlockAndNotifyAdequately(worldIn, position, Blocks.vine.getDefaultState().withProperty(attachment, true));
   }

   private void growVineColumn(World worldIn, BlockPos position, PropertyBool attachment) {
      this.placeVine(worldIn, position, attachment);
      int i = 4;

      for (BlockPos blockPos = position.down(); worldIn.getBlockState(blockPos).getBlock().getMaterial() == Material.air && i > 0; i--) {
         this.placeVine(worldIn, blockPos, attachment);
         blockPos = blockPos.down();
      }
   }

   public WorldGenTrees(boolean notify) {
      this(notify, 4, OAK_LOG, OAK_LEAVES, false);
   }

   public WorldGenTrees(boolean notify, int minTreeHeight, IBlockState woodState, IBlockState leavesState, boolean vinesGrow) {
      super(notify);
      this.minTreeHeight = minTreeHeight;
      this.metaWood = woodState;
      this.metaLeaves = leavesState;
      this.vinesGrow = vinesGrow;
   }

   private void placeCocoa(World worldIn, int age, BlockPos position, EnumFacing facing) {
      this.setBlockAndNotifyAdequately(
         worldIn, position, Blocks.cocoa.getDefaultState().withProperty(BlockCocoa.AGE, age).withProperty(BlockCocoa.FACING, facing)
      );
   }
}
