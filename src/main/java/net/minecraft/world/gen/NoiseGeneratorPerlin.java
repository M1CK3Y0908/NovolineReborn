package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorPerlin extends NoiseGenerator {
   private int octaveCount;
   private NoiseGeneratorSimplex[] octaves;

   public double[] getArray2(double[] dArray, double d, double d2, double d3, double d4, double d5, double d6) {
      if (dArray != null && dArray.length >= 256) {
         for (int i = 0; i < dArray.length; i++) {
            dArray[i] = 0.0;
         }
      } else {
         dArray = new double[256];
      }

      double d7 = 1.0;
      double d8 = 1.0;

      for (int i2 = 0; i2 < this.octaveCount; i2++) {
         this.octaves[i2].handleArray(dArray, d, d2, d3 * d8 * d7, d4 * d8 * d7, 0.55 / d7);
         d8 *= d5;
         d7 *= d6;
      }

      return dArray;
   }

   public double[] getArray(double[] dArray, double d, double d2, double d3, double d4, double d5) {
      return this.getArray2(dArray, d, d2, d3, d4, d5, 0.5);
   }

   public double getValue(double x, double y) {
      double d = 0.0;
      double d2 = 1.0;

      for (int index = 0; index < this.octaveCount; index++) {
         d += this.octaves[index].getValue(x * d2, y * d2) / d2;
         d2 /= 2.0;
      }

      return d;
   }

   public NoiseGeneratorPerlin(Random rand, int octaveCount) {
      this.octaveCount = octaveCount;
      this.octaves = new NoiseGeneratorSimplex[octaveCount];

      for (int index = 0; index < octaveCount; index++) {
         this.octaves[index] = new NoiseGeneratorSimplex(rand);
      }
   }
}
