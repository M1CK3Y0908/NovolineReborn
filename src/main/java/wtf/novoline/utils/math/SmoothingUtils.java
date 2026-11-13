package wtf.novoline.utils.math;

public class SmoothingUtils {
   private static float deltaSeconds;
   private static long lastUpdateMs;

   public static float approachWithMinimumStep(float f, float f2, float f3, float f4) {
      f3 = Math.abs(f - f2) * f3;
      float f5 = Math.max(deltaSeconds * f3, f4);
      if (f < f2) {
         if (f + f5 < f2) {
            f += f5;
         } else {
            f = f2;
         }
      } else if (f - f5 > f2) {
         f -= f5;
      } else {
         f = f2;
      }

      return f;
   }

   public static void updateDelta() {
      long l = System.currentTimeMillis();
      deltaSeconds = (float)(l - lastUpdateMs) / 1000.0F;
      lastUpdateMs = l;
   }

   public static float approach(float f, float f2, float f3) {
      f3 = Math.abs(f - f2) * f3;
      float f4 = deltaSeconds * f3;
      if (f < f2) {
         if (f + f4 < f2) {
            f += f4;
         } else {
            f = f2;
         }
      } else if (f - f4 > f2) {
         f -= f4;
      } else {
         f = f2;
      }

      return f;
   }
}
