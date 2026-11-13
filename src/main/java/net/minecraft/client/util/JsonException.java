package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class JsonException extends IOException {
   private final List<JsonException.Entry> entries = Lists.newArrayList();
   private final String exceptionMessage;

   public JsonException(String message) {
      this.entries.add(new JsonException.Entry());
      this.exceptionMessage = message;
   }

   public static JsonException forException(Exception exception) {
      if (exception instanceof JsonException) {
         return (JsonException)exception;
      }

      String message = exception.getMessage();
      if (exception instanceof FileNotFoundException) {
         message = "File not found";
      }

      return new JsonException(message, exception);
   }

   public void prependJsonKey(String jsonKey) {
      this.entries.get(0).prependJsonKey(jsonKey);
   }

   public JsonException(String message, Throwable cause) {
      super(cause);
      this.entries.add(new JsonException.Entry());
      this.exceptionMessage = message;
   }

   public void setFilename(String filename) {
      this.entries.get(0).filename = filename;
      this.entries.add(0, new JsonException.Entry());
   }

   @Override
   public String getMessage() {
      return "Invalid " + this.entries.get(this.entries.size() - 1).toString() + ": " + this.exceptionMessage;
   }

   public static class Entry {
      private String filename = null;
      private final List<String> jsonKeys = Lists.newArrayList();

      private void prependJsonKey(String jsonKey) {
         this.jsonKeys.add(0, jsonKey);
      }

      public String getJsonPath() {
         return StringUtils.join(this.jsonKeys, "->");
      }

      @Override
      public String toString() {
         return this.filename != null
            ? (!this.jsonKeys.isEmpty() ? this.filename + " " + this.getJsonPath() : this.filename)
            : (!this.jsonKeys.isEmpty() ? "(Unknown file) " + this.getJsonPath() : "(Unknown file)");
      }

      private Entry() {
      }
   }
}
