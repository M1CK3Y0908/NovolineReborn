package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenHellLava extends WorldGenerator {
   private final Block block;
   private final boolean insideRock;

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (worldIn.getBlockState(position.up()).getBlock() != Blocks.netherrack) {
         return false;
      }

      if (worldIn.getBlockState(position).getBlock().getMaterial() != Material.air && worldIn.getBlockState(position).getBlock() != Blocks.netherrack) {
         return false;
      }

      int i = 0;
      if (worldIn.getBlockState(position.west()).getBlock() == Blocks.netherrack) {
         i++;
      }

      if (worldIn.getBlockState(position.east()).getBlock() == Blocks.netherrack) {
         i++;
      }

      if (worldIn.getBlockState(position.north()).getBlock() == Blocks.netherrack) {
         i++;
      }

      if (worldIn.getBlockState(position.south()).getBlock() == Blocks.netherrack) {
         i++;
      }

      if (worldIn.getBlockState(position.down()).getBlock() == Blocks.netherrack) {
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

      if (worldIn.isAirBlock(position.down())) {
         i2++;
      }

      if (!this.insideRock && i == 4 && i2 == 1 || i == 5) {
         worldIn.setBlockState(position, this.block.getDefaultState(), 2);
         worldIn.forceBlockUpdateTick(this.block, position, rand);
      }

      return true;
   }

   public WorldGenHellLava(Block block, boolean insideRock) {
      this.block = block;
      this.insideRock = insideRock;
   }
}
