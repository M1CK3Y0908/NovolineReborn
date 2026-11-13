package net.optifine.util;

import java.lang.reflect.Array;
import java.util.ArrayDeque;

public class ArrayCache {
   private Class elementClass = null;
   private int maxCacheSize = 0;
   private ArrayDeque cache = new ArrayDeque();

   public synchronized Object allocate(int size) {
      Object cachedArray = this.cache.pollLast();
      if (cachedArray == null || Array.getLength(cachedArray) < size) {
         cachedArray = Array.newInstance(this.elementClass, size);
      }

      return cachedArray;
   }

   public ArrayCache(Class clazz) {
      this.elementClass = clazz;
      this.maxCacheSize = 16;
   }

   public synchronized void free(Object arr) {
      if (arr != null) {
         if (arr.getClass().getComponentType() != this.elementClass) {
            throw new IllegalArgumentException("Wrong component type");
         }

         if (this.cache.size() < this.maxCacheSize) {
            this.cache.add(arr);
         }
      }
   }
}
