package net.optifine;

import net.minecraft.world.World;

public class LightMapPack {
   private int[] colorBuffer1 = new int[0];
   private int[] colorBuffer2 = new int[0];
   private LightMap lightMap;
   private LightMap lightMapRain;
   private LightMap lightMapThunder;

   private boolean blend(int[] cols0, float br0, int[] cols1, float br1, int[] cols2, float br2) {
      if (cols1.length == cols0.length && cols2.length == cols0.length) {
         for (int index = 0; index < cols0.length; index++) {
            int i = cols0[index];
            int i2 = i >> 16 & 0xFF;
            int i3 = i >> 8 & 0xFF;
            int i4 = i & 0xFF;
            int i5 = cols1[index];
            int i6 = i5 >> 16 & 0xFF;
            int i7 = i5 >> 8 & 0xFF;
            int i8 = i5 & 0xFF;
            int i9 = cols2[index];
            int i10 = i9 >> 16 & 0xFF;
            int i11 = i9 >> 8 & 0xFF;
            int i12 = i9 & 0xFF;
            int i13 = (int)(i2 * br0 + i6 * br1 + i10 * br2);
            int i14 = (int)(i3 * br0 + i7 * br1 + i11 * br2);
            int i15 = (int)(i4 * br0 + i8 * br1 + i12 * br2);
            cols0[index] = 0xFF000000 | i13 << 16 | i14 << 8 | i15;
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean blend(int[] cols0, float br0, int[] cols1, float br1) {
      if (cols1.length != cols0.length) {
         return false;
      }

      for (int index = 0; index < cols0.length; index++) {
         int i = cols0[index];
         int i2 = i >> 16 & 0xFF;
         int i3 = i >> 8 & 0xFF;
         int i4 = i & 0xFF;
         int i5 = cols1[index];
         int i6 = i5 >> 16 & 0xFF;
         int i7 = i5 >> 8 & 0xFF;
         int i8 = i5 & 0xFF;
         int i9 = (int)(i2 * br0 + i6 * br1);
         int i10 = (int)(i3 * br0 + i7 * br1);
         int i11 = (int)(i4 * br0 + i8 * br1);
         cols0[index] = 0xFF000000 | i9 << 16 | i10 << 8 | i11;
      }

      return true;
   }

   public LightMapPack(LightMap lightMap, LightMap lightMapRain, LightMap lightMapThunder) {
      if (lightMapRain != null || lightMapThunder != null) {
         if (lightMapRain == null) {
            lightMapRain = lightMap;
         }

         if (lightMapThunder == null) {
            lightMapThunder = lightMapRain;
         }
      }

      this.lightMap = lightMap;
      this.lightMapRain = lightMapRain;
      this.lightMapThunder = lightMapThunder;
   }

   public boolean updateLightmap(World world, float torchFlickerX, int[] lmColors, boolean nightvision, float partialTicks) {
      if (this.lightMapRain == null && this.lightMapThunder == null) {
         return this.lightMap.updateLightmap(world, torchFlickerX, lmColors, nightvision);
      }

      int dimensionId = world.provider.getDimensionId();
      if (dimensionId != 1 && dimensionId != -1) {
         float rainStrength = world.getRainStrength(partialTicks);
         float thunderStrength = world.getThunderStrength(partialTicks);
         float f = 1.0E-4F;
         boolean flag = rainStrength > f;
         boolean flag2 = thunderStrength > f;
         if (!flag && !flag2) {
            return this.lightMap.updateLightmap(world, torchFlickerX, lmColors, nightvision);
         }

         if (rainStrength > 0.0F) {
            thunderStrength /= rainStrength;
         }

         float f2 = 1.0F - rainStrength;
         float f3 = rainStrength - thunderStrength;
         if (this.colorBuffer1.length != lmColors.length) {
            this.colorBuffer1 = new int[lmColors.length];
            this.colorBuffer2 = new int[lmColors.length];
         }

         int i = 0;
         int[][] iArrays = new int[][]{lmColors, this.colorBuffer1, this.colorBuffer2};
         float[] floatArray = new float[3];
         if (f2 > f && this.lightMap.updateLightmap(world, torchFlickerX, iArrays[0], nightvision)) {
            floatArray[0] = f2;
            i++;
         }

         if (f3 > f
            && this.lightMapRain != null
            && this.lightMapRain.updateLightmap(world, torchFlickerX, iArrays[i], nightvision)) {
            floatArray[i] = f3;
            i++;
         }

         if (thunderStrength > f
            && this.lightMapThunder != null
            && this.lightMapThunder.updateLightmap(world, torchFlickerX, iArrays[i], nightvision)) {
            floatArray[i] = thunderStrength;
            i++;
         }

         return i == 2
            ? this.blend(iArrays[0], floatArray[0], iArrays[1], floatArray[1])
            : (i == 3 ? this.blend(iArrays[0], floatArray[0], iArrays[1], floatArray[1], iArrays[2], floatArray[2]) : true);
      } else {
         return this.lightMap.updateLightmap(world, torchFlickerX, lmColors, nightvision);
      }
   }
}
