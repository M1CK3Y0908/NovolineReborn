package wtf.novoline.utils.math;

import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;

public class MathUtils {
   public static double parseDoubleWithDefaultAndMax(String text, double d, double d2) {
      return MathHelper.parseDoubleWithDefaultAndMax(text, d, d2);
   }

   public static double wrapAngleTo180_double(double d) {
      return MathHelper.wrapAngleTo180_double(d);
   }

   public static float sin(float f) {
      return MathHelper.sin(f);
   }

   public static float cos(float f) {
      return MathHelper.cos(f);
   }

   public static float abs(float f) {
      return MathHelper.abs(f);
   }

   public static double denormalizeClamp(double d, double d2, double d3) {
      return MathHelper.denormalizeClamp(d, d2, d3);
   }

   public static double clamp_double(double d, double d2, double d3) {
      return MathHelper.clamp_double(d, d2, d3);
   }

   public static double parseDoubleWithDefault(String text, double d) {
      return MathHelper.parseDoubleWithDefault(text, d);
   }

   public static long floor_double_long(double d) {
      return MathHelper.floor_double_long(d);
   }

   public static boolean isEmpty(String text) {
      return StringUtils.isEmpty(text);
   }

   public static int floor_double(double d) {
      return MathHelper.floor_double(d);
   }

   public static float sqrt_float(float f) {
      return MathHelper.sqrt_float(f);
   }

   public static int truncateDoubleToInt(double d) {
      return MathHelper.truncateDoubleToInt(d);
   }

   public static int ceiling_double_int(double d) {
      return MathHelper.ceiling_double_int(d);
   }

   public static float sqrt_double(double d) {
      return MathHelper.sqrt_double(d);
   }

   public static double abs_max(double d, double d2) {
      return MathHelper.abs_max(d, d2);
   }
}
