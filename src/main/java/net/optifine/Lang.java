package net.optifine;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Lang {
   private static final Splitter splitter = Splitter.on('=').limit(2);
   private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");

   public static String getOff() {
      return I18n.format("options.off");
   }

   public static void loadLocaleData(InputStream is, Map localeProperties) throws IOException {
      Iterator iterator = IOUtils.readLines(is, Charsets.UTF_8).iterator();
      is.close();

      while (iterator.hasNext()) {
         String text = (String)iterator.next();
         if (!text.isEmpty() && text.charAt(0) != '#') {
            String[] texts = (String[])Iterables.toArray(splitter.split(text), String.class);
            if (texts != null && texts.length == 2) {
               String text2 = texts[0];
               String text3 = pattern.matcher(texts[1]).replaceAll("%$1s");
               localeProperties.put(text2, text3);
            }
         }
      }
   }

   private static void loadResources(IResourcePack rp, String[] files, Map localeProperties) {
      try {
         for (int index = 0; index < files.length; index++) {
            String text2 = files[index];
            ResourceLocation resourceLocation = new ResourceLocation(text2);
            if (rp.resourceExists(resourceLocation)) {
               InputStream inputStream = rp.getInputStream(resourceLocation);
               if (inputStream != null) {
                  loadLocaleData(inputStream, localeProperties);
               }
            }
         }
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   public static String getDefault() {
      return I18n.format("generator.default");
   }

   public static String getFast() {
      return I18n.format("options.graphics.fast");
   }

   public static void resourcesReloaded() {
      Map localeProperties2 = I18n.getLocaleProperties();
      ArrayList arrayList = new ArrayList();
      arrayList.add("optifine/lang/en_US.lang");
      if (!Config.getGameSettings().language.equals("en_US")) {
         arrayList.add("optifine/lang/" + Config.getGameSettings().language + ".lang");
      }

      String[] texts = (String[]) arrayList.toArray(new String[arrayList.size()]);
      loadResources(Config.getDefaultResourcePack(), texts, localeProperties2);
      IResourcePack[] resourcePacks = Config.getResourcePacks();

      for (int i = 0; i < resourcePacks.length; i++) {
         loadResources(resourcePacks[i], texts, localeProperties2);
      }
   }

   public static String getFancy() {
      return I18n.format("options.graphics.fancy");
   }

   public static String getOn() {
      return I18n.format("options.on");
   }

   public static String get(String key) {
      return I18n.format(key);
   }

   public static String get(String key, String def) {
      String text = I18n.format(key);
      return text != null && !text.equals(key) ? text : def;
   }
}
