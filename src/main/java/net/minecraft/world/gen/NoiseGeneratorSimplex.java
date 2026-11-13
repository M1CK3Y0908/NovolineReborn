package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorSimplex {
   private int[] perm = new int[512];
   public double offsetX;
   private static int[][] GRADIENTS = new int[][]{
      {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
   };
   public static final double SQRT_3 = Math.sqrt(3.0);
   public double offsetY;
   public double offsetZ;
   private static final double F2 = 0.5 * (SQRT_3 - 1.0);
   private static final double G2 = (3.0 - SQRT_3) / 6.0;

   public double getValue(double x, double y) {
      double d = 0.5 * (SQRT_3 - 1.0);
      double d2 = (x + y) * d;
      int i = fastFloor(x + d2);
      int i2 = fastFloor(y + d2);
      double d3 = (3.0 - SQRT_3) / 6.0;
      double d4 = (i + i2) * d3;
      double d5 = i - d4;
      double d6 = i2 - d4;
      double d7 = x - d5;
      double d8 = y - d6;
      byte b;
      byte b2;
      if (d7 > d8) {
         b = 1;
         b2 = 0;
      } else {
         b = 0;
         b2 = 1;
      }

      double d9 = d7 - b + d3;
      double d10 = d8 - b2 + d3;
      double d11 = d7 - 1.0 + 2.0 * d3;
      double d12 = d8 - 1.0 + 2.0 * d3;
      int i3 = i & 0xFF;
      int i4 = i2 & 0xFF;
      int i5 = this.perm[i3 + this.perm[i4]] % 12;
      int i6 = this.perm[i3 + b + this.perm[i4 + b2]] % 12;
      int i7 = this.perm[i3 + 1 + this.perm[i4 + 1]] % 12;
      double d13 = 0.5 - d7 * d7 - d8 * d8;
      double d14;
      if (d13 < 0.0) {
         d14 = 0.0;
      } else {
         d13 *= d13;
         d14 = d13 * d13 * dot(GRADIENTS[i5], d7, d8);
      }

      double d15 = 0.5 - d9 * d9 - d10 * d10;
      double d16;
      if (d15 < 0.0) {
         d16 = 0.0;
      } else {
         d15 *= d15;
         d16 = d15 * d15 * dot(GRADIENTS[i6], d9, d10);
      }

      double d17 = 0.5 - d11 * d11 - d12 * d12;
      double d18;
      if (d17 < 0.0) {
         d18 = 0.0;
      } else {
         d17 *= d17;
         d18 = d17 * d17 * dot(GRADIENTS[i7], d11, d12);
      }

      return 70.0 * (d14 + d16 + d18);
   }

   private static double dot(int[] gradient, double x, double y) {
      return gradient[0] * x + gradient[1] * y;
   }

   public NoiseGeneratorSimplex(Random rand) {
      this.offsetX = rand.nextDouble() * 256.0;
      this.offsetY = rand.nextDouble() * 256.0;
      this.offsetZ = rand.nextDouble() * 256.0;
      int index = 0;

      while (index < 256) {
         this.perm[index] = index++;
      }

      for (int outerIndex = 0; outerIndex < 256; outerIndex++) {
         int i = rand.nextInt(256 - outerIndex) + outerIndex;
         int i2 = this.perm[outerIndex];
         this.perm[outerIndex] = this.perm[i];
         this.perm[i] = i2;
         this.perm[outerIndex + 256] = this.perm[outerIndex];
      }
   }

   public void handleArray(double[] dArray, double d, double d2, double d3, double d4, double d5) {
      int i = 0;

      for (int i2 = 0; i2 < 16; i2++) {
         double d6 = (d2 + i2) * d4 + this.offsetY;

         for (int i3 = 0; i3 < 16; i3++) {
            double d7 = (d + i3) * d3 + this.offsetX;
            double d8 = (d7 + d6) * F2;
            int i4 = fastFloor(d7 + d8);
            int i5 = fastFloor(d6 + d8);
            double d9 = (i4 + i5) * G2;
            double d10 = i4 - d9;
            double d11 = i5 - d9;
            double d12 = d7 - d10;
            double d13 = d6 - d11;
            byte b;
            byte b2;
            if (d12 > d13) {
               b = 1;
               b2 = 0;
            } else {
               b = 0;
               b2 = 1;
            }

            double d14 = d12 - b + G2;
            double d15 = d13 - b2 + G2;
            double d16 = d12 - 1.0 + 2.0 * G2;
            double d17 = d13 - 1.0 + 2.0 * G2;
            int i6 = i4 & 0xFF;
            int i7 = i5 & 0xFF;
            int i8 = this.perm[i6 + this.perm[i7]] % 12;
            int i9 = this.perm[i6 + b + this.perm[i7 + b2]] % 12;
            int i10 = this.perm[i6 + 1 + this.perm[i7 + 1]] % 12;
            double d18 = 0.5 - d12 * d12 - d13 * d13;
            double d19;
            if (d18 < 0.0) {
               d19 = 0.0;
            } else {
               d18 *= d18;
               d19 = d18 * d18 * dot(GRADIENTS[i8], d12, d13);
            }

            double d20 = 0.5 - d14 * d14 - d15 * d15;
            double d21;
            if (d20 < 0.0) {
               d21 = 0.0;
            } else {
               d20 *= d20;
               d21 = d20 * d20 * dot(GRADIENTS[i9], d14, d15);
            }

            double d22 = 0.5 - d16 * d16 - d17 * d17;
            double d23;
            if (d22 < 0.0) {
               d23 = 0.0;
            } else {
               d22 *= d22;
               d23 = d22 * d22 * dot(GRADIENTS[i10], d16, d17);
            }

            int i11 = i++;
            dArray[i11] += 70.0 * (d19 + d21 + d23) * d5;
         }
      }
   }

   private static int fastFloor(double value) {
      return value > 0.0 ? (int)value : (int)value - 1;
   }
}
