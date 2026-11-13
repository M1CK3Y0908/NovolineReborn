package net.optifine.shaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SMCLog {
   private static final String text = "[Shaders] ";
   private static final Logger LOGGER = LogManager.getLogger();

   public static void warning(String format, Object... args) {
      String text = String.format(format, args);
      LOGGER.warn("[Shaders] " + text);
   }

   public static void warning(String message) {
      LOGGER.warn("[Shaders] " + message);
   }

   public static void info(String format, Object... args) {
      String text = String.format(format, args);
      LOGGER.info("[Shaders] " + text);
   }

   public static void severe(String message) {
      LOGGER.error("[Shaders] " + message);
   }

   public static void info(String message) {
      LOGGER.info("[Shaders] " + message);
   }
}
