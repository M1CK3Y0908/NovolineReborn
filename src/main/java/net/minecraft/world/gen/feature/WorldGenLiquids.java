package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenLiquids extends WorldGenerator {
   private Block block;

   public WorldGenLiquids(Block block2) {
      this.block = block2;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (worldIn.getBlockState(position.up()).getBlock() != Blocks.stone) {
         return false;
      }

      if (worldIn.getBlockState(position.down()).getBlock() != Blocks.stone) {
         return false;
      }

      if (worldIn.getBlockState(position).getBlock().getMaterial() != Material.air && worldIn.getBlockState(position).getBlock() != Blocks.stone) {
         return false;
      }

      int i = 0;
      if (worldIn.getBlockState(position.west()).getBlock() == Blocks.stone) {
         i++;
      }

      if (worldIn.getBlockState(position.east()).getBlock() == Blocks.stone) {
         i++;
      }

      if (worldIn.getBlockState(position.north()).getBlock() == Blocks.stone) {
         i++;
      }

      if (worldIn.getBlockState(position.south()).getBlock() == Blocks.stone) {
         i++;
      }

      int i2 = 0;
      if (worldIn.isAirBlock(position.west())) {
         i2++;
      }

      if (worldIn.isAirBlock(position.east())) {
         i2++;
      }

      if (worldIn.isAirBlock(position.north())) {
         i2++;
      }

      if (worldIn.isAirBlock(position.south())) {
         i2++;
      }

      if (i == 3 && i2 == 1) {
         worldIn.setBlockState(position, this.block.getDefaultState(), 2);
         worldIn.forceBlockUpdateTick(this.block, position, rand);
      }

      return true;
   }
}
