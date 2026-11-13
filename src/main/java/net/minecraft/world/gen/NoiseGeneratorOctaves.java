package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.MathHelper;

public class NoiseGeneratorOctaves extends NoiseGenerator {
   private int octaves;
   private NoiseGeneratorImproved[] generatorCollection;

   public double[] generateNoiseOctaves(double[] noiseArray, int xOffset, int zOffset, int xSize, int zSize, double xScale, double zScale, double unusedScale) {
      return this.generateNoiseOctaves(noiseArray, xOffset, 10, zOffset, xSize, 1, zSize, xScale, 1.0, zScale);
   }

   public double[] generateNoiseOctaves(
      double[] noiseArray, int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale
   ) {
      if (noiseArray == null) {
         noiseArray = new double[xSize * ySize * zSize];
      } else {
         for (int i = 0; i < noiseArray.length; i++) {
            noiseArray[i] = 0.0;
         }
      }

      double d = 1.0;

      for (int i2 = 0; i2 < this.octaves; i2++) {
         double d2 = xOffset * d * xScale;
         double d3 = yOffset * d * yScale;
         double d4 = zOffset * d * zScale;
         long l = MathHelper.floor_double_long(d2);
         long l2 = MathHelper.floor_double_long(d4);
         d2 -= l;
         d4 -= l2;
         l %= 16777216L;
         l2 %= 16777216L;
         d2 += l;
         d4 += l2;
         this.generatorCollection[i2]
            .populateNoiseArray(noiseArray, d2, d3, d4, xSize, ySize, zSize, xScale * d, yScale * d, zScale * d, d);
         d /= 2.0;
      }

      return noiseArray;
   }

   public NoiseGeneratorOctaves(Random seed, int octavesIn) {
      this.octaves = octavesIn;
      this.generatorCollection = new NoiseGeneratorImproved[octavesIn];

      for (int index = 0; index < octavesIn; index++) {
         this.generatorCollection[index] = new NoiseGeneratorImproved(seed);
      }
   }
}
