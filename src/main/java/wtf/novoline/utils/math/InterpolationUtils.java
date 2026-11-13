package wtf.novoline.utils.math;

public class InterpolationUtils {
   public static double oscillate(double d, double d2, long l) {
      return oscillateWithOffset(d, d2, 0.0, l);
   }

   public static double oscillateWithOffset(double d, double d2, double d3, long l) {
      l /= 4L;
      return (Math.sin((double)System.currentTimeMillis() / l + d3) + 1.0) / 2.0 * (d2 - d) + d;
   }
}
