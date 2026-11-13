package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorImproved extends NoiseGenerator {
   private static final double[] GRADIENT_X = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
   private int[] permutations = new int[512];
   private static final double[] GRADIENT_Y = new double[]{1.0, 1.0, -1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0};
   private static final double[] GRADIENT_Z = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};
   private static final double[] GRADIENT_2D_X = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
   private static final double[] GRADIENT_2D_Z = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};
   public double xCoord;
   public double yCoord;
   public double zCoord;

   public final double grad2d(int hash, double x, double z) {
      int i = hash & 15;
      return GRADIENT_2D_X[i] * x + GRADIENT_2D_Z[i] * z;
   }

   public final double lerp(double amount, double start, double end) {
      return start + amount * (end - start);
   }

   public NoiseGeneratorImproved(Random random) {
      this.xCoord = random.nextDouble() * 256.0;
      this.yCoord = random.nextDouble() * 256.0;
      this.zCoord = random.nextDouble() * 256.0;
      int index = 0;

      while (index < 256) {
         this.permutations[index] = index++;
      }

      for (int outerIndex = 0; outerIndex < 256; outerIndex++) {
         int i = random.nextInt(256 - outerIndex) + outerIndex;
         int i2 = this.permutations[outerIndex];
         this.permutations[outerIndex] = this.permutations[i];
         this.permutations[i] = i2;
         this.permutations[outerIndex + 256] = this.permutations[outerIndex];
      }
   }

   public final double grad(int hash, double x, double y, double z) {
      int i = hash & 15;
      return GRADIENT_X[i] * x + GRADIENT_Y[i] * y + GRADIENT_Z[i] * z;
   }

   public void populateNoiseArray(
      double[] noiseArray,
      double xOffset,
      double yOffset,
      double zOffset,
      int xSize,
      int ySize,
      int zSize,
      double xScale,
      double yScale,
      double zScale,
      double noiseScale
   ) {
      if (ySize == 1) {
         int i = 0;
         double d = 1.0 / noiseScale;

         for (int i2 = 0; i2 < xSize; i2++) {
            double d2 = xOffset + i2 * xScale + this.xCoord;
            int i3 = (int)d2;
            if (d2 < i3) {
               i3--;
            }

            int i4 = i3 & 0xFF;
            d2 -= i3;
            double d3 = d2 * d2 * d2 * (d2 * (d2 * 6.0 - 15.0) + 10.0);

            for (int i5 = 0; i5 < zSize; i5++) {
               double d4 = zOffset + i5 * zScale + this.zCoord;
               int i6 = (int)d4;
               if (d4 < i6) {
                  i6--;
               }

               int i7 = i6 & 0xFF;
               d4 -= i6;
               double d5 = d4 * d4 * d4 * (d4 * (d4 * 6.0 - 15.0) + 10.0);
               int i8 = this.permutations[i4] + 0;
               int i9 = this.permutations[i8] + i7;
               int i10 = this.permutations[i4 + 1] + 0;
               int i11 = this.permutations[i10] + i7;
               double d6 = this.lerp(
                  d3, this.grad2d(this.permutations[i9], d2, d4), this.grad(this.permutations[i11], d2 - 1.0, 0.0, d4)
               );
               double d7 = this.lerp(
                  d3,
                  this.grad(this.permutations[i9 + 1], d2, 0.0, d4 - 1.0),
                  this.grad(this.permutations[i11 + 1], d2 - 1.0, 0.0, d4 - 1.0)
               );
               double d8 = this.lerp(d5, d6, d7);
               int i12 = i++;
               noiseArray[i12] += d8 * d;
            }
         }
      } else {
         int i13 = 0;
         double d9 = 1.0 / noiseScale;
         int i14 = -1;
         double d10 = 0.0;
         double d11 = 0.0;
         double d12 = 0.0;
         double d13 = 0.0;

         for (int i15 = 0; i15 < xSize; i15++) {
            double d14 = xOffset + i15 * xScale + this.xCoord;
            int i16 = (int)d14;
            if (d14 < i16) {
               i16--;
            }

            int i17 = i16 & 0xFF;
            d14 -= i16;
            double d15 = d14 * d14 * d14 * (d14 * (d14 * 6.0 - 15.0) + 10.0);

            for (int i18 = 0; i18 < zSize; i18++) {
               double d16 = zOffset + i18 * zScale + this.zCoord;
               int i19 = (int)d16;
               if (d16 < i19) {
                  i19--;
               }

               int i20 = i19 & 0xFF;
               d16 -= i19;
               double d17 = d16 * d16 * d16 * (d16 * (d16 * 6.0 - 15.0) + 10.0);

               for (int i21 = 0; i21 < ySize; i21++) {
                  double d18 = yOffset + i21 * yScale + this.yCoord;
                  int i22 = (int)d18;
                  if (d18 < i22) {
                     i22--;
                  }

                  int i23 = i22 & 0xFF;
                  d18 -= i22;
                  double d19 = d18 * d18 * d18 * (d18 * (d18 * 6.0 - 15.0) + 10.0);
                  if (i21 == 0 || i23 != i14) {
                     i14 = i23;
                     int i24 = this.permutations[i17] + i23;
                     int i25 = this.permutations[i24] + i20;
                     int i26 = this.permutations[i24 + 1] + i20;
                     int i27 = this.permutations[i17 + 1] + i23;
                     int i28 = this.permutations[i27] + i20;
                     int i29 = this.permutations[i27 + 1] + i20;
                     d10 = this.lerp(
                        d15, this.grad(this.permutations[i25], d14, d18, d16), this.grad(this.permutations[i28], d14 - 1.0, d18, d16)
                     );
                     d11 = this.lerp(
                        d15,
                        this.grad(this.permutations[i26], d14, d18 - 1.0, d16),
                        this.grad(this.permutations[i29], d14 - 1.0, d18 - 1.0, d16)
                     );
                     d12 = this.lerp(
                        d15,
                        this.grad(this.permutations[i25 + 1], d14, d18, d16 - 1.0),
                        this.grad(this.permutations[i28 + 1], d14 - 1.0, d18, d16 - 1.0)
                     );
                     d13 = this.lerp(
                        d15,
                        this.grad(this.permutations[i26 + 1], d14, d18 - 1.0, d16 - 1.0),
                        this.grad(this.permutations[i29 + 1], d14 - 1.0, d18 - 1.0, d16 - 1.0)
                     );
                  }

                  double d20 = this.lerp(d19, d10, d11);
                  double d21 = this.lerp(d19, d12, d13);
                  double d22 = this.lerp(d17, d20, d21);
                  int i30 = i13++;
                  noiseArray[i30] += d22 * d9;
               }
            }
         }
      }
   }
}
