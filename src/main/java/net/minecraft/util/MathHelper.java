package net.minecraft.util;

import java.util.Random;
import java.util.UUID;
import net.optifine.util.MathUtils;

public class MathHelper {
   public static final float SQRT_2 = sqrt_float(2.0F);
   public static final float PI = MathUtils.roundToFloat(Math.PI);
   private static final int SIN_COUNT_D4 = 1024;
   public static final float PI2 = MathUtils.roundToFloat(Math.PI * 2);
   public static final float PId2 = MathUtils.roundToFloat(Math.PI / 2);
   private static final float radToIndex = MathUtils.roundToFloat(651.8986469044033);
   private static final int SIN_BITS = 12;
   private static final int SIN_MASK = 4095;
   public static final float deg2Rad = MathUtils.roundToFloat(Math.PI / 180.0);
   private static final float[] SIN_TABLE_FAST = new float[4096];
   public static boolean fastMath = false;
   private static final float[] SIN_TABLE = new float[65536];
   private static final int[] multiplyDeBruijnBitPosition;
   private static final double FRAC_BIAS;
   private static final int SIN_COUNT = 4096;
   private static final double[] ASINE_TAB;
   private static final double[] COS_TAB;

   public static int truncateDoubleToInt(double value) {
      return (int)(value + 1024.0) - 1024;
   }

   public static double average(long[] values) {
      long l = 0L;

      for (long l2 : values) {
         l += l2;
      }

      return (double)l / values.length;
   }

   public static int hsvToRGB(float hue, float saturation, float value) {
      int i2 = (int)(hue * 6.0F) % 6;
      float f = hue * 6.0F - i2;
      float f2 = value * (1.0F - saturation);
      float f3 = value * (1.0F - f * saturation);
      float f4 = value * (1.0F - (1.0F - f) * saturation);
      float f5;
      float f6;
      float f7;
      switch (i2) {
         case 0:
            f5 = value;
            f6 = f4;
            f7 = f2;
            break;
         case 1:
            f5 = f3;
            f6 = value;
            f7 = f2;
            break;
         case 2:
            f5 = f2;
            f6 = value;
            f7 = f4;
            break;
         case 3:
            f5 = f2;
            f6 = f3;
            f7 = value;
            break;
         case 4:
            f5 = f4;
            f6 = f2;
            f7 = value;
            break;
         case 5:
            f5 = value;
            f6 = f2;
            f7 = f3;
            break;
         default:
            float value2 = value;
            float saturation2 = saturation;
            float hue2 = hue;
            throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue2 + ", " + saturation2 + ", " + value2);
      }

      int i3 = clamp_int((int)(f5 * 255.0F), 0, 255);
      int i4 = clamp_int((int)(f6 * 255.0F), 0, 255);
      int i5 = clamp_int((int)(f7 * 255.0F), 0, 255);
      return i3 << 16 | i4 << 8 | i5;
   }

   public static int calculateLogBaseTwo(int value) {
      return calculateLogBaseTwoDeBruijn(value) - (isPowerOfTwo(value) ? 0 : 1);
   }

   public static double atan2(double y, double x) {
      double d = x * x + y * y;
      if (Double.isNaN(d)) {
         return Double.NaN;
      }

      boolean flag = y < 0.0;
      if (flag) {
         y = -y;
      }

      boolean flag2 = x < 0.0;
      if (flag2) {
         x = -x;
      }

      boolean flag3 = y > x;
      if (flag3) {
         double x2 = x;
         x = y;
         y = x2;
      }

      double d2 = fastInvSqrt(d);
      x *= d2;
      y *= d2;
      double d3 = FRAC_BIAS + y;
      int i2 = (int)Double.doubleToRawLongBits(d3);
      double d4 = ASINE_TAB[i2];
      double d5 = COS_TAB[i2];
      double d6 = d3 - FRAC_BIAS;
      double d7 = y * d5 - x * d6;
      double d8 = (6.0 + d7 * d7) * d7 * 0.16666666666666666;
      double d9 = d4 + d8;
      if (flag3) {
         d9 = (Math.PI / 2) - d9;
      }

      if (flag2) {
         d9 = Math.PI - d9;
      }

      if (flag) {
         d9 = -d9;
      }

      return d9;
   }

   public static float cos(float value) {
      return fastMath ? SIN_TABLE_FAST[(int)(value * radToIndex + 1024.0F) & 4095] : SIN_TABLE[(int)(value * 10430.378F + 16384.0F) & 65535];
   }

   public static int parseIntWithDefaultAndMax(String value, int defaultValue, int minimum) {
      return Math.max(minimum, parseIntWithDefault(value, defaultValue));
   }

   public static double inverseLerp(double value, double start, double end) {
      return (value - start) / (end - start);
   }

   public static double fastInvSqrt(double value) {
      double d = 0.5 * value;
      long l = Double.doubleToRawLongBits(value);
      l = 6910469410427058090L - (l >> 1);
      value = Double.longBitsToDouble(l);
      return value * (1.5 - d * value * value);
   }

   public static float clamp_float(float num, float min, float max) {
      return num < min ? min : (num > max ? max : num);
   }

   public static float sqrt_float(float value) {
      return (float)Math.sqrt(value);
   }

   static {
      for (int i = 0; i < 65536; i++) {
         SIN_TABLE[i] = (float)Math.sin(i * Math.PI * 2.0 / 65536.0);
      }

      for (int j = 0; j < SIN_TABLE_FAST.length; j++) {
         SIN_TABLE_FAST[j] = MathUtils.roundToFloat(Math.sin(j * Math.PI * 2.0 / 4096.0));
      }

      multiplyDeBruijnBitPosition = new int[]{
         0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9
      };
      FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
      ASINE_TAB = new double[257];
      COS_TAB = new double[257];

      for (int k = 0; k < 257; k++) {
         double d0 = k / 256.0;
         double d1 = Math.asin(d0);
         COS_TAB[k] = Math.cos(d1);
         ASINE_TAB[k] = d1;
      }
   }

   public static int ceiling_double_int(double value) {
      int i2 = (int)value;
      return value > i2 ? i2 + 1 : i2;
   }

   private static boolean isPowerOfTwo(int value) {
      return value != 0 && (value & value - 1) == 0;
   }

   public static UUID getRandomUuid(Random rand) {
      long l = rand.nextLong() & -61441L | 16384L;
      long l2 = rand.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
      return new UUID(l, l2);
   }

   public static int rgb(float red, float green, float blue) {
      return rgb(floor_float(red * 255.0F), floor_float(green * 255.0F), floor_float(blue * 255.0F));
   }

   public static double frac(double value) {
      return value - Math.floor(value);
   }

   public static int roundUpToPowerOfTwo(int value) {
      int i2 = value - 1;
      i2 |= i2 >> 1;
      i2 |= i2 >> 2;
      i2 |= i2 >> 4;
      i2 |= i2 >> 8;
      i2 |= i2 >> 16;
      return i2 + 1;
   }

   public static float createFloat2(double d) {
      return sin((float)d);
   }

   public static float abs(float value) {
      return value >= 0.0F ? value : -value;
   }

   public static int floor_float(float value) {
      int i2 = (int)value;
      return value < i2 ? i2 - 1 : i2;
   }

   public static int roundUp(int value, int interval) {
      if (interval == 0) {
         return 0;
      }

      if (value == 0) {
         return interval;
      }

      if (value < 0) {
         interval *= -1;
      }

      int i2 = value % interval;
      return i2 == 0 ? value : value + interval - i2;
   }

   public static long floor_double_long(double value) {
      long l = (long)value;
      return value < l ? l - 1L : l;
   }

   public static double denormalizeClamp(double lowerBnd, double upperBnd, double slide) {
      return slide < 0.0 ? lowerBnd : (slide > 1.0 ? upperBnd : lowerBnd + (upperBnd - lowerBnd) * slide);
   }

   private static int calculateLogBaseTwoDeBruijn(int value) {
      value = isPowerOfTwo(value) ? value : roundUpToPowerOfTwo(value);
      return multiplyDeBruijnBitPosition[(int)(value * 125613361L >> 27) & 31];
   }

   public static int parseIntWithDefault(String value, int defaultValue) {
      try {
         return Integer.parseInt(value);
      } catch (Throwable throwable) {
         return defaultValue;
      }
   }

   public static float randomFloatClamp(Random random, float minimum, float maximum) {
      return minimum >= maximum ? minimum : random.nextFloat() * (maximum - minimum) + minimum;
   }

   public static double abs_max(double a, double b) {
      if (a < 0.0) {
         a = -a;
      }

      if (b < 0.0) {
         b = -b;
      }

      return a > b ? a : b;
   }

   public static int createInt(int i2) {
      return i2 < 0 ? -((-i2 - 1) / 16) - 1 : i2 / 16;
   }

   public static int multiplyColor(int colorA, int colorB) {
      int i2 = (colorA & 0xFF0000) >> 16;
      int i3 = (colorB & 0xFF0000) >> 16;
      int i4 = (colorA & 0xFF00) >> 8;
      int i5 = (colorB & 0xFF00) >> 8;
      int i6 = (colorA & 0xFF) >> 0;
      int i7 = (colorB & 0xFF) >> 0;
      int i8 = (int)((float)i2 * i3 / 255.0F);
      int i9 = (int)((float)i4 * i5 / 255.0F);
      int i10 = (int)((float)i6 * i7 / 255.0F);
      return colorA & 0xFF000000 | i8 << 16 | i9 << 8 | i10;
   }

   public static double wrapAngleTo180_double(double value) {
      value %= 360.0;
      if (value >= 180.0) {
         value -= 360.0;
      }

      if (value < -180.0) {
         value += 360.0;
      }

      return value;
   }

   public static boolean epsilonEquals(float a, float b) {
      return abs(b - a) < 1.0E-5F;
   }

   public static float sqrt_double(double value) {
      return (float)Math.sqrt(value);
   }

   public static double clamp_double(double num, double min, double max) {
      return num < min ? min : (num > max ? max : num);
   }

   public static int floor_double(double value) {
      int i2 = (int)value;
      return value < i2 ? i2 - 1 : i2;
   }

   public static long getPositionRandom(Vec3i pos) {
      return getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ());
   }

   public static float wrapAngleTo180_float(float value) {
      value %= 360.0F;
      if (value >= 180.0F) {
         value -= 360.0F;
      }

      if (value < -180.0F) {
         value += 360.0F;
      }

      return value;
   }

   public static float createFloat(double d) {
      return cos((float)d);
   }

   public static double parseDoubleWithDefault(String value, double defaultValue) {
      try {
         return Double.parseDouble(value);
      } catch (Throwable throwable) {
         return defaultValue;
      }
   }

   public static float sin(float value) {
      return fastMath ? SIN_TABLE_FAST[(int)(value * radToIndex) & 4095] : SIN_TABLE[(int)(value * 10430.378F) & 65535];
   }

   public static int getRandomIntegerInRange(Random random, int minimum, int maximum) {
      return minimum >= maximum ? minimum : random.nextInt(maximum - minimum + 1) + minimum;
   }

   public static int clamp_int(int num, int min, int max) {
      return num < min ? min : (num > max ? max : num);
   }

   public static long getCoordinateRandom(int x, int y, int z) {
      long l = x * 3129871 ^ z * 116129781L ^ y;
      return l * l * 42317861L + l * 11L;
   }

   public static int normalizeAngle(int value, int modulus) {
      return (value % modulus + modulus) % modulus;
   }

   public static int ceiling_float_int(float value) {
      int i2 = (int)value;
      return value > i2 ? i2 + 1 : i2;
   }

   public static int rgb(int red, int green, int blue) {
      return ((red << 8) + green << 8) + blue;
   }

   public static double getRandomDoubleInRange(Random random, double minimum, double maximum) {
      return minimum >= maximum ? minimum : random.nextDouble() * (maximum - minimum) + minimum;
   }

   public static int abs_int(int value) {
      return value >= 0 ? value : -value;
   }

   public static double parseDoubleWithDefaultAndMax(String value, double defaultValue, double minimum) {
      return Math.max(minimum, parseDoubleWithDefault(value, defaultValue));
   }
}
