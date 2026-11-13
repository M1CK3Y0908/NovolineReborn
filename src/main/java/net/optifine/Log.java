package net.optifine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final boolean logDetail = System.getProperty("log.detail", "false").equals("true");

   public static void log(String text) {
      dbg(text);
   }

   public static void handleString3(String text2) {
      LOGGER.warn("[OptiFine] " + text2);
   }

   public static void dbg(String text) {
      LOGGER.info("[OptiFine] " + text);
   }

   public static void handleString2(String text2, Throwable throwable) {
      LOGGER.error("[OptiFine] " + text2, throwable);
   }

   public static void handleString(String text2, Throwable throwable) {
      LOGGER.warn("[OptiFine] " + text2, throwable);
   }
}
