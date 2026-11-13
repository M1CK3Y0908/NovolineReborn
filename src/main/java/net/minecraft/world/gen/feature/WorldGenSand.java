package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenSand extends WorldGenerator {
   private Block block;
   private int radius;

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (worldIn.getBlockState(position).getBlock().getMaterial() != Material.water) {
         return false;
      }

      int zCoordinate = rand.nextInt(this.radius - 2) + 2;

      for (int i = position.getX() - zCoordinate; i <= position.getX() + zCoordinate; i++) {
         for (int i2 = position.getZ() - zCoordinate; i2 <= position.getZ() + zCoordinate; i2++) {
            int i3 = i - position.getX();
            int i4 = i2 - position.getZ();
            if (i3 * i3 + i4 * i4 <= zCoordinate * zCoordinate) {
               for (int i5 = position.getY() - 2; i5 <= position.getY() + 2; i5++) {
                  BlockPos blockPos = new BlockPos(i, i5, i2);
                  Block block2 = worldIn.getBlockState(blockPos).getBlock();
                  if (block2 == Blocks.dirt || block2 == Blocks.grass) {
                     worldIn.setBlockState(blockPos, this.block.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      return true;
   }

   public WorldGenSand(Block block2, int i) {
      this.block = block2;
      this.radius = i;
   }
}
