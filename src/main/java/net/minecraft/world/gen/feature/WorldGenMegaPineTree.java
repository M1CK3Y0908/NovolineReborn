package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaPineTree extends WorldGenHugeTrees {
   private static final IBlockState SPRUCE_LOG = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, net.minecraft.block.BlockPlanks.EnumType.SPRUCE);
   private static final IBlockState SPRUCE_LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, net.minecraft.block.BlockPlanks.EnumType.SPRUCE)
      .withProperty(BlockLeaves.CHECK_DECAY, false);
   private static final IBlockState PODZOL = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, net.minecraft.block.BlockDirt.DirtType.PODZOL);
   private boolean useBaseHeight;

   @Override
   public void generateExtraTreeFeatures(World worldIn, Random rand, BlockPos position) {
      this.generatePodzolPatch(worldIn, position.west().north());
      this.generatePodzolPatch(worldIn, position.east(2).north());
      this.generatePodzolPatch(worldIn, position.west().south(2));
      this.generatePodzolPatch(worldIn, position.east(2).south(2));

      for (int index = 0; index < 5; index++) {
         int i = rand.nextInt(64);
         int i2 = i % 8;
         int i3 = i / 8;
         if (i2 == 0 || i2 == 7 || i3 == 0 || i3 == 7) {
            this.generatePodzolPatch(worldIn, position.add(-3 + i2, 0, -3 + i3));
         }
      }
   }

   private void generatePodzolPatch(World worldIn, BlockPos position) {
      for (int index = -2; index <= 2; index++) {
         for (int innerIndex = -2; innerIndex <= 2; innerIndex++) {
            if (Math.abs(index) != 2 || Math.abs(innerIndex) != 2) {
               this.placePodzol(worldIn, position.add(index, 0, innerIndex));
            }
         }
      }
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int height = this.getHeight(rand);
      if (!this.canGenerateTree(worldIn, rand, position, height)) {
         return false;
      }

      this.handleWorld(worldIn, position.getX(), position.getZ(), position.getY() + height, rand);

      for (int i = 0; i < height; i++) {
         Block block2 = worldIn.getBlockState(position.up(i)).getBlock();
         if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
            this.setBlockAndNotifyAdequately(worldIn, position.up(i), this.woodMetadata);
         }

         if (i < height - 1) {
            block2 = worldIn.getBlockState(position.add(1, i, 0)).getBlock();
            if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
               this.setBlockAndNotifyAdequately(worldIn, position.add(1, i, 0), this.woodMetadata);
            }

            block2 = worldIn.getBlockState(position.add(1, i, 1)).getBlock();
            if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
               this.setBlockAndNotifyAdequately(worldIn, position.add(1, i, 1), this.woodMetadata);
            }

            block2 = worldIn.getBlockState(position.add(0, i, 1)).getBlock();
            if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
               this.setBlockAndNotifyAdequately(worldIn, position.add(0, i, 1), this.woodMetadata);
            }
         }
      }

      return true;
   }

   private void placePodzol(World worldIn, BlockPos position) {
      for (int index = 2; index >= -3; index--) {
         BlockPos blockPos = position.up(index);
         Block block = worldIn.getBlockState(blockPos).getBlock();
         if (block == Blocks.grass || block == Blocks.dirt) {
            this.setBlockAndNotifyAdequately(worldIn, blockPos, PODZOL);
            break;
         }

         if (block.getMaterial() != Material.air && index < 0) {
            break;
         }
      }
   }

   public WorldGenMegaPineTree(boolean flag) {
      super(false, 13, 15, SPRUCE_LOG, SPRUCE_LEAVES);
      this.useBaseHeight = flag;
   }

   private void handleWorld(World world2, int i, int i2, int i3, Random random) {
      int i4 = random.nextInt(5) + (this.useBaseHeight ? this.baseHeight : 3);
      int i5 = 0;

      for (int i6 = i3 - i4; i6 <= i3; i6++) {
         int i7 = i3 - i6;
         int i8 = 0 + MathHelper.floor_float((float)i7 / i4 * 3.5F);
         this.generateLeafLayer(world2, new BlockPos(i, i6, i2), i8 + (i7 > 0 && i8 == i5 && (i6 & 1) == 0 ? 1 : 0));
         i5 = i8;
      }
   }
}
