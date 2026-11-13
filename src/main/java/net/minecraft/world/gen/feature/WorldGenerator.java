package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class WorldGenerator {
   private final boolean doBlockNotify;

   public WorldGenerator(boolean notify) {
      this.doBlockNotify = notify;
   }

   public void setDecorationDefaults() {
   }

   protected void setBlockAndNotifyAdequately(World worldIn, BlockPos pos, IBlockState state) {
      if (this.doBlockNotify) {
         worldIn.setBlockState(pos, state, 3);
      } else {
         worldIn.setBlockState(pos, state, 2);
      }
   }

   public WorldGenerator() {
      this(false);
   }

   public abstract boolean generate(World world2, Random random, BlockPos blockPos);
}
