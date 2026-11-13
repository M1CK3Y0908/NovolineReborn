package net.minecraft.util;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingPrintStream extends PrintStream {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String domain;

   private void logString(String text) {
      StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();
      StackTraceElement stackTraceElement = astacktraceelement[Math.min(3, astacktraceelement.length)];
      LOGGER.info("[{}]@.({}:{}): {}", this.domain, stackTraceElement.getFileName(), stackTraceElement.getLineNumber(), text);
   }

   @Override
   public void println(Object value) {
      this.logString(String.valueOf(value));
   }

   @Override
   public void println(String message) {
      this.logString(message);
   }

   public LoggingPrintStream(String domainIn, OutputStream outStream) {
      super(outStream);
      this.domain = domainIn;
   }
}
