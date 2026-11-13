package net.optifine;

import net.minecraft.src.Config;
import net.minecraft.world.World;

public class LightMap {
   private CustomColormap lightMapRgb = null;
   private float[][] sunRgbs = new float[16][3];
   private float[][] torchRgbs = new float[16][3];

   public CustomColormap getColormap() {
      return this.lightMapRgb;
   }

   public boolean updateLightmap(World world, float torchFlickerX, int[] lmColors, boolean nightvision) {
      if (this.lightMapRgb == null) {
         return false;
      }

      int height = this.lightMapRgb.getHeight();
      if (nightvision && height < 64) {
         return false;
      }

      int width2 = this.lightMapRgb.getWidth();
      if (width2 < 16) {
         warn("Invalid lightmap width: " + width2);
         this.lightMapRgb = null;
         return false;
      }

      int i = 0;
      if (nightvision) {
         i = width2 * 16 * 2;
      }

      float f = 1.1666666F * (world.getSunBrightness(1.0F) - 0.2F);
      if (world.getLastLightningBolt() > 0) {
         f = 1.0F;
      }

      f = Config.limitTo1(f);
      float f2 = f * (width2 - 1);
      float f3 = Config.limitTo1(torchFlickerX + 0.5F) * (width2 - 1);
      float f4 = Config.limitTo1(Config.getGameSettings().gammaSetting);
      boolean flag = f4 > 1.0E-4F;
      float[][] colorsRgb = this.lightMapRgb.getColorsRgb();
      this.getLightMapColumn(colorsRgb, f2, i, width2, this.sunRgbs);
      this.getLightMapColumn(colorsRgb, f3, i + 16 * width2, width2, this.torchRgbs);
      float[] fArray = new float[3];

      for (int i2 = 0; i2 < 16; i2++) {
         for (int i3 = 0; i3 < 16; i3++) {
            for (int i4 = 0; i4 < 3; i4++) {
               float f5 = Config.limitTo1(this.sunRgbs[i2][i4] + this.torchRgbs[i3][i4]);
               if (flag) {
                  float f6 = 1.0F - f5;
                  f6 = 1.0F - f6 * f6 * f6 * f6;
                  f5 = f4 * f6 + (1.0F - f4) * f5;
               }

               fArray[i4] = f5;
            }

            int i5 = (int)(fArray[0] * 255.0F);
            int i6 = (int)(fArray[1] * 255.0F);
            int i7 = (int)(fArray[2] * 255.0F);
            lmColors[i2 * 16 + i3] = 0xFF000000 | i5 << 16 | i6 << 8 | i7;
         }
      }

      return true;
   }

   private static void warn(String str) {
      Config.warn("CustomColors: " + str);
   }

   private void getLightMapColumn(float[][] origMap, float x, int offset, int width, float[][] colRgb) {
      int i = (int)Math.floor(x);
      int i2 = (int)Math.ceil(x);
      if (i == i2) {
         for (int index = 0; index < 16; index++) {
            float[] fArray = origMap[offset + index * width + i];
            float[] nestedIndex = colRgb[index];

            for (int innerIndex = 0; innerIndex < 3; innerIndex++) {
               nestedIndex[innerIndex] = fArray[innerIndex];
            }
         }
      } else {
         float f = 1.0F - (x - i);
         float f2 = 1.0F - (i2 - x);

         for (int nestedIndex = 0; nestedIndex < 16; nestedIndex++) {
            float[] fArray2 = origMap[offset + nestedIndex * width + i];
            float[] fArray3 = origMap[offset + nestedIndex * width + i2];
            float[] fArray4 = colRgb[nestedIndex];

            for (int outerIndex = 0; outerIndex < 3; outerIndex++) {
               fArray4[outerIndex] = fArray2[outerIndex] * f + fArray3[outerIndex] * f2;
            }
         }
      }
   }

   public LightMap(CustomColormap lightMapRgb) {
      this.lightMapRgb = lightMapRgb;
   }
}
