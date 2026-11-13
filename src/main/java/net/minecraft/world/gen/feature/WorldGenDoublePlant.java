package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenDoublePlant extends WorldGenerator {
   private net.minecraft.block.BlockDoublePlant.EnumPlantType plantType;

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      boolean conditionMet = false;

      for (int index = 0; index < 64; index++) {
         BlockPos blockPos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (worldIn.isAirBlock(blockPos)
            && (!worldIn.provider.getHasNoSky() || blockPos.getY() < 254)
            && Blocks.double_plant.canPlaceBlockAt(worldIn, blockPos)) {
            Blocks.double_plant.handleWorld(worldIn, blockPos, this.plantType);
            conditionMet = true;
         }
      }

      return conditionMet;
   }

   public void setPlantType(net.minecraft.block.BlockDoublePlant.EnumPlantType plantType) {
      this.plantType = plantType;
   }
}
