package net.minecraft.world.gen.feature;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMinable extends WorldGenerator {
   private final IBlockState oreBlock;
   private final int numberOfBlocks;
   private final Predicate<IBlockState> predicate;

   public WorldGenMinable(IBlockState state, int blockCount, Predicate<IBlockState> predicate2) {
      this.oreBlock = state;
      this.numberOfBlocks = blockCount;
      this.predicate = predicate2;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      float f = rand.nextFloat() * (float) Math.PI;
      double xCoordinate = position.getX() + 8 + MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
      double xCoordinate2 = position.getX() + 8 - MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
      double zCoordinate = position.getZ() + 8 + MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
      double zCoordinate2 = position.getZ() + 8 - MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
      double yCoordinate = position.getY() + rand.nextInt(3) - 2;
      double yCoordinate2 = position.getY() + rand.nextInt(3) - 2;

      for (int index = 0; index < this.numberOfBlocks; index++) {
         float f2 = (float)index / this.numberOfBlocks;
         double d = xCoordinate + (xCoordinate2 - xCoordinate) * f2;
         double d2 = yCoordinate + (yCoordinate2 - yCoordinate) * f2;
         double d3 = zCoordinate + (zCoordinate2 - zCoordinate) * f2;
         double d4 = rand.nextDouble() * this.numberOfBlocks / 16.0;
         double d5 = (MathHelper.sin((float) Math.PI * f2) + 1.0F) * d4 + 1.0;
         double d6 = (MathHelper.sin((float) Math.PI * f2) + 1.0F) * d4 + 1.0;
         int i = MathHelper.floor_double(d - d5 / 2.0);
         int i2 = MathHelper.floor_double(d2 - d6 / 2.0);
         int i3 = MathHelper.floor_double(d3 - d5 / 2.0);
         int i4 = MathHelper.floor_double(d + d5 / 2.0);
         int i5 = MathHelper.floor_double(d2 + d6 / 2.0);
         int i6 = MathHelper.floor_double(d3 + d5 / 2.0);

         for (int outerIndex = i; outerIndex <= i4; outerIndex++) {
            double d7 = (outerIndex + 0.5 - d) / (d5 / 2.0);
            if (d7 * d7 < 1.0) {
               for (int index2 = i2; index2 <= i5; index2++) {
                  double d8 = (index2 + 0.5 - d2) / (d6 / 2.0);
                  if (d7 * d7 + d8 * d8 < 1.0) {
                     for (int index3 = i3; index3 <= i6; index3++) {
                        double d9 = (index3 + 0.5 - d3) / (d5 / 2.0);
                        if (d7 * d7 + d8 * d8 + d9 * d9 < 1.0) {
                           BlockPos blockPos = new BlockPos(outerIndex, index2, index3);
                           if (this.predicate.apply(worldIn.getBlockState(blockPos))) {
                              worldIn.setBlockState(blockPos, this.oreBlock, 2);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return true;
   }

   public WorldGenMinable(IBlockState state, int blockCount) {
      this(state, blockCount, BlockHelper.forBlock(Blocks.stone));
   }
}
