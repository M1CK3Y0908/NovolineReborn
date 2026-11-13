package wtf.novoline.util;

import net.minecraft.src.Config;

public class GlErrorThrottle {
   private static boolean frameStarted = false;
   private static long intervalStartMs = -1L;
   private static final int ERROR_LIMIT_PER_INTERVAL = 10;
   private static int errorCount = 0;
   private static int suppressedCount = 0;
   private static boolean suppressing = false;
   private static boolean intervalJustEnded = false;
   private static final long INTERVAL_MS = 3000L;

   public static void frameStart() {
      frameStarted = true;
      if (intervalStartMs < 0L) {
         intervalStartMs = System.currentTimeMillis();
      }

      if (System.currentTimeMillis() > intervalStartMs + 3000L) {
         if (suppressedCount > 0) {
            Config.error("Suppressed " + errorCount + " OpenGL errors");
         }

         suppressing = errorCount > 10;
         intervalStartMs = System.currentTimeMillis();
         errorCount = 0;
         suppressedCount = 0;
         intervalJustEnded = true;
      }
   }

   public static boolean shouldReportError() {
      if (!frameStarted) {
         return true;
      }

      errorCount++;
      if (intervalJustEnded) {
         intervalJustEnded = false;
         return true;
      }

      if (suppressing) {
         suppressedCount++;
      }

      return !suppressing;
   }
}
