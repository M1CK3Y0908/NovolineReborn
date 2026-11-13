package net.minecraft.world;

public class ColorizerGrass {
   private static int[] grassBuffer = new int[65536];

   public static int getGrassColor(double d, double d2) {
      d2 *= d;
      int i = (int)((1.0 - d) * 255.0);
      int i2 = (int)((1.0 - d2) * 255.0) << 8 | i;
      return i2 > grassBuffer.length ? -65281 : grassBuffer[i2];
   }

   public static void setGrassBiomeColorizer(int[] iArray) {
      grassBuffer = iArray;
   }
}
