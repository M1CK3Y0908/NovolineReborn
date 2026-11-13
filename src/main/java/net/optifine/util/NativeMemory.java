package net.optifine.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import net.minecraft.src.Config;

public class NativeMemory {
   private static LongSupplier bufferAllocatedSupplier = makeLongSupplier(
      new String[][]{
         {"sun.misc.SharedSecrets", "getJavaNioAccess", "getDirectBufferPool", "getMemoryUsed"},
         {"jdk.internal.misc.SharedSecrets", "getJavaNioAccess", "getDirectBufferPool", "getMemoryUsed"}
      }
   );
   private static LongSupplier bufferMaximumSupplier = makeLongSupplier(
      new String[][]{{"sun.misc.VM", "maxDirectMemory"}, {"jdk.internal.misc.VM", "maxDirectMemory"}}
   );

   private static LongSupplier makeLongSupplier(String[][] paths) {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < paths.length; i++) {
         String[] texts = paths[i];

         try {
            return makeLongSupplier(texts);
         } catch (Throwable throwable) {
            arrayList.add(throwable);
         }
      }

      for (Throwable throwable2 : (Iterable<Throwable>) arrayList) {
         Config.warn(throwable2.getClass().getName() + ": " + throwable2.getMessage());
      }

      return null;
   }

   public static long getBufferMaximum() {
      return bufferMaximumSupplier == null ? -1L : bufferMaximumSupplier.getAsLong();
   }

   private static LongSupplier makeLongSupplier(String[] path) throws Exception {
      if (path.length < 2) {
         return null;
      }

      Method method = Class.forName(path[0]).getMethod(path[1]);
      method.setAccessible(true);
      Object targetObject = null;

      for (int index = 2; index < path.length; index++) {
         String text = path[index];
         targetObject = method.invoke(targetObject);
         method = targetObject.getClass().getMethod(text);
         method.setAccessible(true);
      }

      final Method method2 = method;
      final Object targetObject2 = targetObject;
      return new LongSupplier() {
         private boolean disabled = false;

         @Override
         public long getAsLong() {
            if (this.disabled) {
               return -1L;
            }

            try {
               return (Long)method2.invoke(targetObject2);
            } catch (Throwable throwable) {
               Config.warn(throwable.getClass().getName() + ": " + throwable.getMessage());
               this.disabled = true;
               return -1L;
            }
         }
      };
   }

   public static long getBufferAllocated() {
      return bufferAllocatedSupplier == null ? -1L : bufferAllocatedSupplier.getAsLong();
   }
}
