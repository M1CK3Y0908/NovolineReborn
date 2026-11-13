package net.minecraft.world.gen.layer;

import com.google.common.collect.Lists;
import java.util.List;

public class IntCache {
   private static int intCacheSize = 256;
   private static List<int[]> freeSmallArrays = Lists.newArrayList();
   private static List<int[]> inUseSmallArrays = Lists.newArrayList();
   private static List<int[]> freeLargeArrays = Lists.newArrayList();
   private static List<int[]> inUseLargeArrays = Lists.newArrayList();

   public static synchronized int[] getIntCache(int size) {
      if (size <= 256) {
         if (freeSmallArrays.isEmpty()) {
            int[] intArray4 = new int[256];
            inUseSmallArrays.add(intArray4);
            return intArray4;
         } else {
            int[] iArray = freeSmallArrays.remove(freeSmallArrays.size() - 1);
            inUseSmallArrays.add(iArray);
            return iArray;
         }
      } else if (size > intCacheSize) {
         intCacheSize = size;
         freeLargeArrays.clear();
         inUseLargeArrays.clear();
         int[] intArray2 = new int[intCacheSize];
         inUseLargeArrays.add(intArray2);
         return intArray2;
      } else if (freeLargeArrays.isEmpty()) {
         int[] intArray1 = new int[intCacheSize];
         inUseLargeArrays.add(intArray1);
         return intArray1;
      } else {
         int[] intArray1 = freeLargeArrays.remove(freeLargeArrays.size() - 1);
         inUseLargeArrays.add(intArray1);
         return intArray1;
      }
   }

   public static synchronized void resetIntCache() {
      if (!freeLargeArrays.isEmpty()) {
         freeLargeArrays.remove(freeLargeArrays.size() - 1);
      }

      if (!freeSmallArrays.isEmpty()) {
         freeSmallArrays.remove(freeSmallArrays.size() - 1);
      }

      freeLargeArrays.addAll(inUseLargeArrays);
      freeSmallArrays.addAll(inUseSmallArrays);
      inUseLargeArrays.clear();
      inUseSmallArrays.clear();
   }

   public static synchronized String getCacheSizes() {
      return "cache: "
         + freeLargeArrays.size()
         + ", tcache: "
         + freeSmallArrays.size()
         + ", allocated: "
         + inUseLargeArrays.size()
         + ", tallocated: "
         + inUseSmallArrays.size();
   }
}
