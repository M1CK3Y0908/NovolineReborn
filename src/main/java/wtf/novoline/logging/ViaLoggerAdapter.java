package wtf.novoline.logging;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ViaLoggerAdapter extends Logger {
   private final org.apache.logging.log4j.Logger logger;

   @Override
   public void log(Level level, String text2, Object[] objects) {
      this.log(level, MessageFormat.format(text2, objects));
   }

   @Override
   public void log(Level level, String text2, Throwable throwable) {
      if (level == Level.FINE) {
         this.logger.debug(text2, throwable);
      } else if (level == Level.WARNING) {
         this.logger.warn(text2, throwable);
      } else if (level == Level.SEVERE) {
         this.logger.error(text2, throwable);
      } else if (level == Level.INFO) {
         this.logger.info(text2, throwable);
      } else {
         this.logger.trace(text2, throwable);
      }
   }

   @Override
   public void log(Level level, String text2, Object object) {
      if (level == Level.FINE) {
         this.logger.debug(text2, object);
      } else if (level == Level.WARNING) {
         this.logger.warn(text2, object);
      } else if (level == Level.SEVERE) {
         this.logger.error(text2, object);
      } else if (level == Level.INFO) {
         this.logger.info(text2, object);
      } else {
         this.logger.trace(text2, object);
      }
   }

   @Override
   public void log(Level level, String text2) {
      if (level == Level.FINE) {
         this.logger.debug(text2);
      } else if (level == Level.WARNING) {
         this.logger.warn(text2);
      } else if (level == Level.SEVERE) {
         this.logger.error(text2);
      } else if (level == Level.INFO) {
         this.logger.info(text2);
      } else {
         this.logger.trace(text2);
      }
   }

   @Override
   public void log(LogRecord logRecord) {
      this.log(logRecord.getLevel(), logRecord.getMessage());
   }

   public ViaLoggerAdapter(org.apache.logging.log4j.Logger logger2) {
      super("logger", null);
      this.logger = logger2;
   }
}
