package net.optifine.util;

import net.minecraft.util.MathHelper;

public class MathUtils {
   public static final float PId2 = (float) (Math.PI / 2);
   private static final float[] ASIN_TABLE = new float[65536];
   public static final float PI = (float) Math.PI;
   public static final float PI2 = (float) (Math.PI * 2);

   static {
      for (int i = 0; i < 65536; i++) {
         ASIN_TABLE[i] = (float)Math.asin(i / 32767.5 - 1.0);
      }

      for (int j = -1; j < 2; j++) {
         ASIN_TABLE[(int)((j + 1.0) * 32767.5) & 65535] = (float)Math.asin(j);
      }
   }

   public static int getAverage(int[] vals) {
      return vals.length <= 0 ? 0 : getSum(vals) / vals.length;
   }

   public static float toDeg(float angle) {
      return angle * 180.0F / MathHelper.PI;
   }

   public static float acos(float value) {
      return (float) (Math.PI / 2) - ASIN_TABLE[(int)((value + 1.0F) * 32767.5) & 65535];
   }

   public static int getSum(int[] vals) {
      if (vals.length <= 0) {
         return 0;
      }

      int i2 = 0;

      for (int innerIndex = 0; innerIndex < vals.length; innerIndex++) {
         int i3 = vals[innerIndex];
         i2 += i3;
      }

      return i2;
   }

   public static float roundToFloat(double d) {
      return (float)(Math.round(d * 1.0E8) / 1.0E8);
   }

   public static float asin(float value) {
      return ASIN_TABLE[(int)((value + 1.0F) * 32767.5) & 65535];
   }

   public static float toRad(float angle) {
      return angle / 180.0F * MathHelper.PI;
   }
}
