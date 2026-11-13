package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenRavine extends MapGenBase {
   private float[] sizeFactors = new float[1024];

   protected void addTunnel(
      long seed,
      int chunkX,
      int chunkZ,
      ChunkPrimer primer,
      double x,
      double y,
      double z,
      float radius,
      float yaw,
      float pitch,
      int start,
      int end,
      double verticalScale
   ) {
      Random random = new Random(seed);
      double d = chunkX * 16 + 8;
      double d2 = chunkZ * 16 + 8;
      float f = 0.0F;
      float f2 = 0.0F;
      int i = this.range * 16 - 16;
      end = i - random.nextInt(i / 4);
      float f3 = 1.0F;

      for (int i2 = 0; i2 < 256; i2++) {
         if (i2 == 0 || random.nextInt(3) == 0) {
            f3 = 1.0F + random.nextFloat() * random.nextFloat() * 1.0F;
         }

         this.sizeFactors[i2] = f3 * f3;
      }

      while (start < end) {
         double d3 = 1.5 + MathHelper.sin(start * (float) Math.PI / end) * radius * 1.0F;
         double d4 = d3 * verticalScale;
         d3 *= random.nextFloat() * 0.25 + 0.75;
         d4 *= random.nextFloat() * 0.25 + 0.75;
         float f4 = MathHelper.cos(pitch);
         float f5 = MathHelper.sin(pitch);
         x += MathHelper.cos(yaw) * f4;
         y += f5;
         z += MathHelper.sin(yaw) * f4;
         pitch *= 0.7F;
         pitch += f2 * 0.05F;
         yaw += f * 0.05F;
         f2 *= 0.8F;
         f *= 0.5F;
         f2 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
         if (random.nextInt(4) != 0) {
            double d5 = x - d;
            double d6 = z - d2;
            double d7 = end - start;
            double d8 = radius + 2.0F + 16.0F;
            if (d5 * d5 + d6 * d6 - d7 * d7 > d8 * d8) {
               return;
            }

            if (x >= d - 16.0 - d3 * 2.0 && z >= d2 - 16.0 - d3 * 2.0 && x <= d + 16.0 + d3 * 2.0 && z <= d2 + 16.0 + d3 * 2.0) {
               int i3 = MathHelper.floor_double(x - d3) - chunkX * 16 - 1;
               int i4 = MathHelper.floor_double(x + d3) - chunkX * 16 + 1;
               int index2 = MathHelper.floor_double(y - d4) - 1;
               int i5 = MathHelper.floor_double(y + d4) + 1;
               int i6 = MathHelper.floor_double(z - d3) - chunkZ * 16 - 1;
               int i7 = MathHelper.floor_double(z + d3) - chunkZ * 16 + 1;
               if (i3 < 0) {
                  i3 = 0;
               }

               if (i4 > 16) {
                  i4 = 16;
               }

               if (index2 < 1) {
                  index2 = 1;
               }

               if (i5 > 248) {
                  i5 = 248;
               }

               if (i6 < 0) {
                  i6 = 0;
               }

               if (i7 > 16) {
                  i7 = 16;
               }

               int index3 = 0;

               for (int i8 = i3; index3 == 0 && i8 < i4; i8++) {
                  for (int i9 = i6; index3 == 0 && i9 < i7; i9++) {
                     for (int i10 = i5 + 1; index3 == 0 && i10 >= index2 - 1; i10--) {
                        if (i10 >= 0 && i10 < 256) {
                           IBlockState blockState = primer.getBlockState(i8, i10, i9);
                           if (blockState.getBlock() == Blocks.flowing_water || blockState.getBlock() == Blocks.water) {
                              index3 = 1;
                           }

                           if (i10 != index2 - 1 && i8 != i3 && i8 != i4 - 1 && i9 != i6 && i9 != i7 - 1) {
                              i10 = index2;
                           }
                        }
                     }
                  }
               }

               if (index3 == 0) {
                  net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

                  for (int i11 = i3; i11 < i4; i11++) {
                     double d9 = (i11 + chunkX * 16 + 0.5 - x) / d3;

                     for (int i12 = i6; i12 < i7; i12++) {
                        double d10 = (i12 + chunkZ * 16 + 0.5 - z) / d3;
                        boolean flag = false;
                        if (d9 * d9 + d10 * d10 < 1.0) {
                           for (int i13 = i5; i13 > index2; i13--) {
                              double d11 = (i13 - 1 + 0.5 - y) / d4;
                              if ((d9 * d9 + d10 * d10) * this.sizeFactors[i13 - 1] + d11 * d11 / 6.0 < 1.0) {
                                 IBlockState blockState2 = primer.getBlockState(i11, i13, i12);
                                 if (blockState2.getBlock() == Blocks.grass) {
                                    flag = true;
                                 }

                                 if (blockState2.getBlock() == Blocks.stone || blockState2.getBlock() == Blocks.dirt || blockState2.getBlock() == Blocks.grass) {
                                    if (i13 - 1 < 10) {
                                       primer.setBlockState(i11, i13, i12, Blocks.flowing_lava.getDefaultState());
                                    } else {
                                       primer.setBlockState(i11, i13, i12, Blocks.air.getDefaultState());
                                       if (flag && primer.getBlockState(i11, i13 - 1, i12).getBlock() == Blocks.dirt) {
                                          mutableBlockPos.set(i11 + chunkX * 16, 0, i12 + chunkZ * 16);
                                          primer.setBlockState(i11, i13 - 1, i12, this.worldObj.getBiomeGenForCoords(mutableBlockPos).topBlock);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         start++;
      }
   }

   @Override
   protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int originalChunkX, int originalChunkZ, ChunkPrimer chunkPrimerIn) {
      if (this.rand.nextInt(50) == 0) {
         double d = chunkX * 16 + this.rand.nextInt(16);
         double d2 = this.rand.nextInt(this.rand.nextInt(40) + 8) + 20;
         double d3 = chunkZ * 16 + this.rand.nextInt(16);

         for (int i = 0; i < 1; i++) {
            float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            float f2 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float f3 = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
            this.addTunnel(this.rand.nextLong(), originalChunkX, originalChunkZ, chunkPrimerIn, d, d2, d3, f3, f, f2, 0, 0, 3.0);
         }
      }
   }
}
