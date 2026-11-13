package net.minecraft.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class StringTranslate {
   private static final Pattern numericVariablePattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   private final Map<String, String> languageList = Maps.newHashMap();
   private long lastUpdateTimeInMilliseconds;
   private static final Splitter equalSignSplitter = Splitter.on('=').limit(2);
   private static StringTranslate instance = new StringTranslate();

   private String tryTranslateKey(String key) {
      String text = this.languageList.get(key);
      return text == null ? key : text;
   }

   public synchronized String translateKeyFormat(String key, Object... format) {
      String text3 = this.tryTranslateKey(key);

      try {
         return String.format(text3, format);
      } catch (IllegalFormatException illegalFormatException) {
         return "Format error: " + text3;
      }
   }

   public StringTranslate() {
      for (String text3 : IOUtils.readLines(StringTranslate.class.getResourceAsStream("/assets/minecraft/lang/en_US.lang"), Charsets.UTF_8)) {
         if (!text3.isEmpty() && text3.charAt(0) != '#') {
            String[] texts = (String[])Iterables.toArray(equalSignSplitter.split(text3), String.class);
            if (texts != null && texts.length == 2) {
               String text4 = texts[0];
               String text2 = numericVariablePattern.matcher(texts[1]).replaceAll("%$1s");
               this.languageList.put(text4, text2);
            }
         }
      }
      this.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
   }

   public long getLastUpdateTimeInMilliseconds() {
      return this.lastUpdateTimeInMilliseconds;
   }

   public synchronized boolean isKeyTranslated(String key) {
      return this.languageList.containsKey(key);
   }

   static StringTranslate getInstance() {
      return instance;
   }

   public static synchronized void replaceWith(Map<String, String> translations) {
      instance.languageList.clear();
      instance.languageList.putAll(translations);
      instance.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
   }

   public synchronized String translateKey(String key) {
      return this.tryTranslateKey(key);
   }
}
