package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenCavesHell extends MapGenBase {
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
      double d = chunkX * 16 + 8;
      double d2 = chunkZ * 16 + 8;
      float f = 0.0F;
      float f2 = 0.0F;
      Random random = new Random(seed);
      if (end <= 0) {
         int i = this.range * 16 - 16;
         end = i - random.nextInt(i / 4);
      }

      boolean flag = false;
      if (start == -1) {
         start = end / 2;
         flag = true;
      }

      int i2 = random.nextInt(end / 2) + end / 4;
      boolean flag2 = random.nextInt(6) == 0;

      while (start < end) {
         double d3 = 1.5 + MathHelper.sin(start * (float) Math.PI / end) * radius * 1.0F;
         double d4 = d3 * verticalScale;
         float f3 = MathHelper.cos(pitch);
         float f4 = MathHelper.sin(pitch);
         x += MathHelper.cos(yaw) * f3;
         y += f4;
         z += MathHelper.sin(yaw) * f3;
         if (flag2) {
            pitch *= 0.92F;
         } else {
            pitch *= 0.7F;
         }

         pitch += f2 * 0.1F;
         yaw += f * 0.1F;
         f2 *= 0.9F;
         f *= 0.75F;
         f2 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
         if (!flag && start == i2 && radius > 1.0F) {
            this.addTunnel(
               random.nextLong(), chunkX, chunkZ, primer, x, y, z, random.nextFloat() * 0.5F + 0.5F, yaw - (float) (Math.PI / 2), pitch / 3.0F, start, end, 1.0
            );
            this.addTunnel(
               random.nextLong(), chunkX, chunkZ, primer, x, y, z, random.nextFloat() * 0.5F + 0.5F, yaw + (float) (Math.PI / 2), pitch / 3.0F, start, end, 1.0
            );
            return;
         }

         if (flag || random.nextInt(4) != 0) {
            double d5 = x - d;
            double d6 = z - d2;
            double d7 = end - start;
            double d8 = radius + 2.0F + 16.0F;
            if (d5 * d5 + d6 * d6 - d7 * d7 > d8 * d8) {
               return;
            }

            if (x >= d - 16.0 - d3 * 2.0
               && z >= d2 - 16.0 - d3 * 2.0
               && x <= d + 16.0 + d3 * 2.0
               && z <= d2 + 16.0 + d3 * 2.0) {
               int i3 = MathHelper.floor_double(x - d3) - chunkX * 16 - 1;
               int outerIndex = MathHelper.floor_double(x + d3) - chunkX * 16 + 1;
               int i4 = MathHelper.floor_double(y - d4) - 1;
               int i5 = MathHelper.floor_double(y + d4) + 1;
               int i6 = MathHelper.floor_double(z - d3) - chunkZ * 16 - 1;
               int index3 = MathHelper.floor_double(z + d3) - chunkZ * 16 + 1;
               if (i3 < 0) {
                  i3 = 0;
               }

               if (outerIndex > 16) {
                  outerIndex = 16;
               }

               if (i4 < 1) {
                  i4 = 1;
               }

               if (i5 > 120) {
                  i5 = 120;
               }

               if (i6 < 0) {
                  i6 = 0;
               }

               if (index3 > 16) {
                  index3 = 16;
               }

               boolean flag3 = false;

               for (int i7 = i3; !flag3 && i7 < outerIndex; i7++) {
                  for (int i8 = i6; !flag3 && i8 < index3; i8++) {
                     for (int i9 = i5 + 1; !flag3 && i9 >= i4 - 1; i9--) {
                        if (i9 >= 0 && i9 < 128) {
                           IBlockState blockState = primer.getBlockState(i7, i9, i8);
                           if (blockState.getBlock() == Blocks.flowing_lava || blockState.getBlock() == Blocks.lava) {
                              flag3 = true;
                           }

                           if (i9 != i4 - 1 && i7 != i3 && i7 != outerIndex - 1 && i8 != i6 && i8 != index3 - 1) {
                              i9 = i4;
                           }
                        }
                     }
                  }
               }

               if (!flag3) {
                  for (int i10 = i3; i10 < outerIndex; i10++) {
                     double d9 = (i10 + chunkX * 16 + 0.5 - x) / d3;

                     for (int i11 = i6; i11 < index3; i11++) {
                        double d10 = (i11 + chunkZ * 16 + 0.5 - z) / d3;

                        for (int i12 = i5; i12 > i4; i12--) {
                           double d11 = (i12 - 1 + 0.5 - y) / d4;
                           if (d11 > -0.7 && d9 * d9 + d11 * d11 + d10 * d10 < 1.0) {
                              IBlockState blockState2 = primer.getBlockState(i10, i12, i11);
                              if (blockState2.getBlock() == Blocks.netherrack || blockState2.getBlock() == Blocks.dirt || blockState2.getBlock() == Blocks.grass) {
                                 primer.setBlockState(i10, i12, i11, Blocks.air.getDefaultState());
                              }
                           }
                        }
                     }
                  }

                  if (flag) {
                     break;
                  }
               }
            }
         }

         start++;
      }
   }

   protected void addRoom(long seed, int chunkX, int chunkZ, ChunkPrimer primer, double x, double y, double z) {
      this.addTunnel(seed, chunkX, chunkZ, primer, x, y, z, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5);
   }

   @Override
   protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int originalChunkX, int originalChunkZ, ChunkPrimer chunkPrimerIn) {
      int i = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(10) + 1) + 1);
      if (this.rand.nextInt(5) != 0) {
         i = 0;
      }

      for (int innerIndex = 0; innerIndex < i; innerIndex++) {
         double d = chunkX * 16 + this.rand.nextInt(16);
         double d2 = this.rand.nextInt(128);
         double d3 = chunkZ * 16 + this.rand.nextInt(16);
         int i2 = 1;
         if (this.rand.nextInt(4) == 0) {
            this.addRoom(this.rand.nextLong(), originalChunkX, originalChunkZ, chunkPrimerIn, d, d2, d3);
            i2 = 1 + this.rand.nextInt(4);
         }

         for (int outerIndex = 0; outerIndex < i2; outerIndex++) {
            float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
            float f2 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float f3 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
            this.addTunnel(
               this.rand.nextLong(),
               originalChunkX,
               originalChunkZ,
               chunkPrimerIn,
               d,
               d2,
               d3,
               f3 * 2.0F,
               f,
               f2,
               0,
               0,
               0.5
            );
         }
      }
   }
}
