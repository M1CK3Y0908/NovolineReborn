package net.optifine.util;

public class MemoryMonitor {
   private static long startTimeMs = System.currentTimeMillis();
   private static long startMemory = getMemoryUsed();
   private static long lastTimeMs = startTimeMs;
   private static long lastMemory = startMemory;
   private static boolean gcEvent = false;
   private static int memBytesSec = 0;
   private static long MB = 1048576L;

   public static long getStartTimeMs() {
      return startTimeMs;
   }

   private static long getMemoryUsed() {
      Runtime runtime = Runtime.getRuntime();
      return runtime.totalMemory() - runtime.freeMemory();
   }

   public static long getAllocationRateMb() {
      return memBytesSec / MB;
   }

   public static void update() {
      long l = System.currentTimeMillis();
      long memoryUsed = getMemoryUsed();
      gcEvent = memoryUsed < lastMemory;
      if (gcEvent) {
         long l2 = lastTimeMs - startTimeMs;
         long l3 = lastMemory - startMemory;
         double d = l2 / 1000.0;
         int i = (int)(l3 / d);
         if (i > 0) {
            memBytesSec = i;
         }

         startTimeMs = l;
         startMemory = memoryUsed;
      }

      lastTimeMs = l;
      lastMemory = memoryUsed;
   }

   public static long getStartMemoryMb() {
      return startMemory / MB;
   }

   public static boolean isGcEvent() {
      return gcEvent;
   }
}
