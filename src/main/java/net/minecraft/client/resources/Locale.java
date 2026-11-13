package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Locale {
   private static final Splitter splitter = Splitter.on('=').limit(2);
   Map<String, String> properties = Maps.newHashMap();
   private boolean unicode;
   private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");

   private void loadLocaleData(InputStream inputStreamIn) throws IOException {
      for (String text2 : IOUtils.readLines(inputStreamIn, Charsets.UTF_8)) {
         if (!text2.isEmpty() && text2.charAt(0) != '#') {
            String[] texts = (String[])Iterables.toArray(splitter.split(text2), String.class);
            if (texts != null && texts.length == 2) {
               String text3 = texts[0];
               String text4 = pattern.matcher(texts[1]).replaceAll("%$1s");
               this.properties.put(text3, text4);
            }
         }
      }
   }

   public synchronized void loadLocaleDataFiles(IResourceManager resourceManager, List<String> languageList) {
      this.properties.clear();

      for (String text2 : languageList) {
         String text3 = String.format("lang/%s.lang", text2);

         for (String text4 : resourceManager.getResourceDomains()) {
            try {
               this.loadLocaleData(resourceManager.getAllResources(new ResourceLocation(text4, text3)));
            } catch (IOException ioException) {
            }
         }
      }

      this.checkUnicode();
   }

   private String translateKeyPrivate(String translateKey) {
      String text = this.properties.get(translateKey);
      return text == null ? translateKey : text;
   }

   private void checkUnicode() {
      this.unicode = false;
      int i = 0;
      int i2 = 0;

      for (String text2 : this.properties.values()) {
         int outerIndex = text2.length();
         i2 += outerIndex;

         for (int i3 = 0; i3 < outerIndex; i3++) {
            if (text2.charAt(i3) >= 256) {
               i++;
            }
         }
      }

      float f = (float)i / i2;
      this.unicode = f > 0.1;
   }

   public boolean isUnicode() {
      return this.unicode;
   }

   private void loadLocaleData(List<IResource> resourcesList) throws IOException {
      Iterator iterator2 = resourcesList.iterator();

      while (iterator2.hasNext()) {
         InputStream inputStream = ((IResource)iterator2.next()).getInputStream();

         try {
            this.loadLocaleData(inputStream);
         } finally {
            IOUtils.closeQuietly(inputStream);
         }
      }
   }

   public String formatMessage(String translateKey, Object[] parameters) {
      String text2 = this.translateKeyPrivate(translateKey);

      try {
         return String.format(text2, parameters);
      } catch (IllegalFormatException illegalFormatException) {
         return "Format error: " + text2;
      }
   }
}
