package net.minecraft.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.apache.logging.log4j.Logger;

public class Util {
   public static <V> V runTask(FutureTask<V> task, Logger logger) {
      try {
         task.run();
         return task.get();
      } catch (ExecutionException executionException) {
         logger.fatal("Error executing task", executionException);
         if (executionException.getCause() instanceof OutOfMemoryError) {
            throw (OutOfMemoryError)executionException.getCause();
         }
      } catch (InterruptedException interruptedException) {
         logger.fatal("Error executing task", interruptedException);
      }

      return null;
   }

   public static Util.EnumOS getOSType() {
      String propertyValue = System.getProperty("os.name").toLowerCase();
      return propertyValue.contains("win")
         ? Util.EnumOS.WINDOWS
         : (
            propertyValue.contains("mac")
               ? Util.EnumOS.OSX
               : (
                  propertyValue.contains("solaris")
                     ? Util.EnumOS.SOLARIS
                     : (
                        propertyValue.contains("sunos")
                           ? Util.EnumOS.SOLARIS
                           : (propertyValue.contains("linux") ? Util.EnumOS.LINUX : (propertyValue.contains("unix") ? Util.EnumOS.LINUX : Util.EnumOS.UNKNOWN))
                     )
               )
         );
   }

   public enum EnumOS {
      LINUX,
      SOLARIS,
      WINDOWS,
      OSX,
      UNKNOWN;
   }
}
