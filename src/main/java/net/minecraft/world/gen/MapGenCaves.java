package net.minecraft.world.gen;

import com.google.common.base.Objects;
import java.util.Random;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenCaves extends MapGenBase {
   protected boolean canReplaceBlock(IBlockState state, IBlockState aboveState) {
      return state.getBlock() == Blocks.stone
         ? true
         : (
            state.getBlock() == Blocks.dirt
               ? true
               : (
                  state.getBlock() == Blocks.grass
                     ? true
                     : (
                        state.getBlock() == Blocks.hardened_clay
                           ? true
                           : (
                              state.getBlock() == Blocks.stained_hardened_clay
                                 ? true
                                 : (
                                    state.getBlock() == Blocks.sandstone
                                       ? true
                                       : (
                                          state.getBlock() == Blocks.red_sandstone
                                             ? true
                                             : (
                                                state.getBlock() == Blocks.mycelium
                                                   ? true
                                                   : (
                                                      state.getBlock() == Blocks.snow_layer
                                                         ? true
                                                         : (state.getBlock() == Blocks.sand || state.getBlock() == Blocks.gravel)
                                                            && aboveState.getBlock().getMaterial() != Material.water
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

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
      int i3 = random.nextInt(6) == 0 ? 1 : 0;

      while (start < end) {
         double d3 = 1.5 + MathHelper.sin(start * (float) Math.PI / end) * radius * 1.0F;
         double d4 = d3 * verticalScale;
         float f3 = MathHelper.cos(pitch);
         float f4 = MathHelper.sin(pitch);
         x += MathHelper.cos(yaw) * f3;
         y += f4;
         z += MathHelper.sin(yaw) * f3;
         if (i3 != 0) {
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
         if (!flag && start == i2 && radius > 1.0F && end > 0) {
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
               int i4 = MathHelper.floor_double(x - d3) - chunkX * 16 - 1;
               int i5 = MathHelper.floor_double(x + d3) - chunkX * 16 + 1;
               int i6 = MathHelper.floor_double(y - d4) - 1;
               int i7 = MathHelper.floor_double(y + d4) + 1;
               int i8 = MathHelper.floor_double(z - d3) - chunkZ * 16 - 1;
               int i9 = MathHelper.floor_double(z + d3) - chunkZ * 16 + 1;
               if (i4 < 0) {
                  i4 = 0;
               }

               if (i5 > 16) {
                  i5 = 16;
               }

               if (i6 < 1) {
                  i6 = 1;
               }

               if (i7 > 248) {
                  i7 = 248;
               }

               if (i8 < 0) {
                  i8 = 0;
               }

               if (i9 > 16) {
                  i9 = 16;
               }

               boolean flag2 = false;

               for (int i10 = i4; !flag2 && i10 < i5; i10++) {
                  for (int i11 = i8; !flag2 && i11 < i9; i11++) {
                     for (int i12 = i7 + 1; !flag2 && i12 >= i6 - 1; i12--) {
                        if (i12 >= 0 && i12 < 256) {
                           IBlockState blockState = primer.getBlockState(i10, i12, i11);
                           if (blockState.getBlock() == Blocks.flowing_water || blockState.getBlock() == Blocks.water) {
                              flag2 = true;
                           }

                           if (i12 != i6 - 1 && i10 != i4 && i10 != i5 - 1 && i11 != i8 && i11 != i9 - 1) {
                              i12 = i6;
                           }
                        }
                     }
                  }
               }

               if (!flag2) {
                  net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

                  for (int i13 = i4; i13 < i5; i13++) {
                     double d9 = (i13 + chunkX * 16 + 0.5 - x) / d3;

                     for (int i14 = i8; i14 < i9; i14++) {
                        double d10 = (i14 + chunkZ * 16 + 0.5 - z) / d3;
                        boolean flag3 = false;
                        if (d9 * d9 + d10 * d10 < 1.0) {
                           for (int i15 = i7; i15 > i6; i15--) {
                              double d11 = (i15 - 1 + 0.5 - y) / d4;
                              if (d11 > -0.7 && d9 * d9 + d11 * d11 + d10 * d10 < 1.0) {
                                 IBlockState blockState2 = primer.getBlockState(i13, i15, i14);
                                 IBlockState iBlockState = (IBlockState)Objects.firstNonNull(
                                    primer.getBlockState(i13, i15 + 1, i14), Blocks.air.getDefaultState()
                                 );
                                 if (blockState2.getBlock() == Blocks.grass || blockState2.getBlock() == Blocks.mycelium) {
                                    flag3 = true;
                                 }

                                 if (this.canReplaceBlock(blockState2, iBlockState)) {
                                    if (i15 - 1 < 10) {
                                       primer.setBlockState(i13, i15, i14, Blocks.lava.getDefaultState());
                                    } else {
                                       primer.setBlockState(i13, i15, i14, Blocks.air.getDefaultState());
                                       if (iBlockState.getBlock() == Blocks.sand) {
                                          primer.setBlockState(
                                             i13,
                                             i15 + 1,
                                             i14,
                                             iBlockState.getValue(BlockSand.VARIANT) == net.minecraft.block.BlockSand.EnumType.RED_SAND
                                                ? Blocks.red_sandstone.getDefaultState()
                                                : Blocks.sandstone.getDefaultState()
                                          );
                                       }

                                       if (flag3 && primer.getBlockState(i13, i15 - 1, i14).getBlock() == Blocks.dirt) {
                                          mutableBlockPos.set(i13 + chunkX * 16, 0, i14 + chunkZ * 16);
                                          primer.setBlockState(
                                             i13, i15 - 1, i14, this.worldObj.getBiomeGenForCoords(mutableBlockPos).topBlock.getBlock().getDefaultState()
                                          );
                                       }
                                    }
                                 }
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
      int i = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(15) + 1) + 1);
      if (this.rand.nextInt(7) != 0) {
         i = 0;
      }

      for (int innerIndex = 0; innerIndex < i; innerIndex++) {
         double d = chunkX * 16 + this.rand.nextInt(16);
         double d2 = this.rand.nextInt(this.rand.nextInt(120) + 8);
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
            if (this.rand.nextInt(10) == 0) {
               f3 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
            }

            this.addTunnel(
               this.rand.nextLong(),
               originalChunkX,
               originalChunkZ,
               chunkPrimerIn,
               d,
               d2,
               d3,
               f3,
               f,
               f2,
               0,
               0,
               1.0
            );
         }
      }
   }
}
