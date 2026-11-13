package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenReed extends WorldGenerator {
   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      for (int index = 0; index < 20; index++) {
         BlockPos blockPos = position.add(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));
         if (worldIn.isAirBlock(blockPos)) {
            BlockPos blockPos2 = blockPos.down();
            if (worldIn.getBlockState(blockPos2.west()).getBlock().getMaterial() == Material.water
               || worldIn.getBlockState(blockPos2.east()).getBlock().getMaterial() == Material.water
               || worldIn.getBlockState(blockPos2.north()).getBlock().getMaterial() == Material.water
               || worldIn.getBlockState(blockPos2.south()).getBlock().getMaterial() == Material.water) {
               int i = 2 + rand.nextInt(rand.nextInt(3) + 1);

               for (int nestedIndex = 0; nestedIndex < i; nestedIndex++) {
                  if (Blocks.reeds.canBlockStay(worldIn, blockPos)) {
                     worldIn.setBlockState(blockPos.up(nestedIndex), Blocks.reeds.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      return true;
   }
}
