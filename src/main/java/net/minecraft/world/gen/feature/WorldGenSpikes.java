package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenSpikes extends WorldGenerator {
   private Block baseBlockRequired;

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (worldIn.isAirBlock(position) && worldIn.getBlockState(position.down()).getBlock() == this.baseBlockRequired) {
         int i = rand.nextInt(32) + 6;
         int i2 = rand.nextInt(4) + 1;
         net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

         for (int nestedIndex = position.getX() - i2; nestedIndex <= position.getX() + i2; nestedIndex++) {
            for (int outerIndex = position.getZ() - i2; outerIndex <= position.getZ() + i2; outerIndex++) {
               int xCoordinate = nestedIndex - position.getX();
               int zCoordinate = outerIndex - position.getZ();
               if (xCoordinate * xCoordinate + zCoordinate * zCoordinate <= i2 * i2 + 1
                  && worldIn.getBlockState(mutableBlockPos.set(nestedIndex, position.getY() - 1, outerIndex)).getBlock() != this.baseBlockRequired) {
                  return false;
               }
            }
         }

         for (int outerIndex2 = position.getY(); outerIndex2 < position.getY() + i && outerIndex2 < 256; outerIndex2++) {
            for (int index = position.getX() - i2; index <= position.getX() + i2; index++) {
               for (int index2 = position.getZ() - i2; index2 <= position.getZ() + i2; index2++) {
                  int xCoordinate2 = index - position.getX();
                  int zCoordinate2 = index2 - position.getZ();
                  if (xCoordinate2 * xCoordinate2 + zCoordinate2 * zCoordinate2 <= i2 * i2 + 1) {
                     worldIn.setBlockState(new BlockPos(index, outerIndex2, index2), Blocks.obsidian.getDefaultState(), 2);
                  }
               }
            }
         }

         EntityEnderCrystal entityEnderCrystal = new EntityEnderCrystal(worldIn);
         entityEnderCrystal.setLocationAndAngles(position.getX() + 0.5F, position.getY() + i, position.getZ() + 0.5F, rand.nextFloat() * 360.0F, 0.0F);
         worldIn.spawnEntityInWorld(entityEnderCrystal);
         worldIn.setBlockState(position.up(i), Blocks.bedrock.getDefaultState(), 2);
         return true;
      } else {
         return false;
      }
   }

   public WorldGenSpikes(Block block2) {
      this.baseBlockRequired = block2;
   }
}
