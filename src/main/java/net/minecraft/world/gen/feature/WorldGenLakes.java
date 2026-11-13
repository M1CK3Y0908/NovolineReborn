package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class WorldGenLakes extends WorldGenerator {
   private Block block;

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      position = position.add(-8, 0, -8);

      while (position.getY() > 5 && worldIn.isAirBlock(position)) {
         position = position.down();
      }

      if (position.getY() <= 4) {
         return false;
      }

      position = position.down(4);
      boolean[] flagArray = new boolean[2048];
      int i = rand.nextInt(4) + 4;

      for (int i2 = 0; i2 < i; i2++) {
         double d = rand.nextDouble() * 6.0 + 3.0;
         double d2 = rand.nextDouble() * 4.0 + 2.0;
         double d3 = rand.nextDouble() * 6.0 + 3.0;
         double d4 = rand.nextDouble() * (16.0 - d - 2.0) + 1.0 + d / 2.0;
         double d5 = rand.nextDouble() * (8.0 - d2 - 4.0) + 2.0 + d2 / 2.0;
         double d6 = rand.nextDouble() * (16.0 - d3 - 2.0) + 1.0 + d3 / 2.0;

         for (int i3 = 1; i3 < 15; i3++) {
            for (int i4 = 1; i4 < 15; i4++) {
               for (int i5 = 1; i5 < 7; i5++) {
                  double d7 = (i3 - d4) / (d / 2.0);
                  double d8 = (i5 - d5) / (d2 / 2.0);
                  double d9 = (i4 - d6) / (d3 / 2.0);
                  double d10 = d7 * d7 + d8 * d8 + d9 * d9;
                  if (d10 < 1.0) {
                     flagArray[(i3 * 16 + i4) * 8 + i5] = true;
                  }
               }
            }
         }
      }

      for (int i6 = 0; i6 < 16; i6++) {
         for (int i7 = 0; i7 < 16; i7++) {
            for (int i8 = 0; i8 < 8; i8++) {
               boolean flag = !flagArray[(i6 * 16 + i7) * 8 + i8]
                  && (
                     i6 < 15 && flagArray[((i6 + 1) * 16 + i7) * 8 + i8]
                        || i6 > 0 && flagArray[((i6 - 1) * 16 + i7) * 8 + i8]
                        || i7 < 15 && flagArray[(i6 * 16 + i7 + 1) * 8 + i8]
                        || i7 > 0 && flagArray[(i6 * 16 + (i7 - 1)) * 8 + i8]
                        || i8 < 7 && flagArray[(i6 * 16 + i7) * 8 + i8 + 1]
                        || i8 > 0 && flagArray[(i6 * 16 + i7) * 8 + (i8 - 1)]
                  );
               if (flag) {
                  Material material2 = worldIn.getBlockState(position.add(i6, i8, i7)).getBlock().getMaterial();
                  if (i8 >= 4 && material2.isLiquid()) {
                     return false;
                  }

                  if (i8 < 4 && !material2.isSolid() && worldIn.getBlockState(position.add(i6, i8, i7)).getBlock() != this.block) {
                     return false;
                  }
               }
            }
         }
      }

      for (int i9 = 0; i9 < 16; i9++) {
         for (int i10 = 0; i10 < 16; i10++) {
            for (int i11 = 0; i11 < 8; i11++) {
               if (flagArray[(i9 * 16 + i10) * 8 + i11]) {
                  worldIn.setBlockState(position.add(i9, i11, i10), i11 >= 4 ? Blocks.air.getDefaultState() : this.block.getDefaultState(), 2);
               }
            }
         }
      }

      for (int i12 = 0; i12 < 16; i12++) {
         for (int i13 = 0; i13 < 16; i13++) {
            for (int i14 = 4; i14 < 8; i14++) {
               if (flagArray[(i12 * 16 + i13) * 8 + i14]) {
                  BlockPos blockPos = position.add(i12, i14 - 1, i13);
                  if (worldIn.getBlockState(blockPos).getBlock() == Blocks.dirt
                     && worldIn.getLightFor(EnumSkyBlock.SKY, position.add(i12, i14, i13)) > 0) {
                     BiomeGenBase biomeGenForCoords = worldIn.getBiomeGenForCoords(blockPos);
                     if (biomeGenForCoords.topBlock.getBlock() == Blocks.mycelium) {
                        worldIn.setBlockState(blockPos, Blocks.mycelium.getDefaultState(), 2);
                     } else {
                        worldIn.setBlockState(blockPos, Blocks.grass.getDefaultState(), 2);
                     }
                  }
               }
            }
         }
      }

      if (this.block.getMaterial() == Material.lava) {
         for (int i15 = 0; i15 < 16; i15++) {
            for (int i16 = 0; i16 < 16; i16++) {
               for (int i17 = 0; i17 < 8; i17++) {
                  boolean flag2 = !flagArray[(i15 * 16 + i16) * 8 + i17]
                     && (
                        i15 < 15 && flagArray[((i15 + 1) * 16 + i16) * 8 + i17]
                           || i15 > 0 && flagArray[((i15 - 1) * 16 + i16) * 8 + i17]
                           || i16 < 15 && flagArray[(i15 * 16 + i16 + 1) * 8 + i17]
                           || i16 > 0 && flagArray[(i15 * 16 + (i16 - 1)) * 8 + i17]
                           || i17 < 7 && flagArray[(i15 * 16 + i16) * 8 + i17 + 1]
                           || i17 > 0 && flagArray[(i15 * 16 + i16) * 8 + (i17 - 1)]
                     );
                  if (flag2
                     && (i17 < 4 || rand.nextInt(2) != 0)
                     && worldIn.getBlockState(position.add(i15, i17, i16)).getBlock().getMaterial().isSolid()) {
                     worldIn.setBlockState(position.add(i15, i17, i16), Blocks.stone.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      if (this.block.getMaterial() == Material.water) {
         for (int i18 = 0; i18 < 16; i18++) {
            for (int i19 = 0; i19 < 16; i19++) {
               if (worldIn.canBlockFreezeWater(position.add(i18, 4, i19))) {
                  worldIn.setBlockState(position.add(i18, 4, i19), Blocks.ice.getDefaultState(), 2);
               }
            }
         }
      }

      return true;
   }

   public WorldGenLakes(Block blockIn) {
      this.block = blockIn;
   }
}
