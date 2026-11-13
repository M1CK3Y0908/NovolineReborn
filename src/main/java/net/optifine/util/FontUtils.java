package net.optifine.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import java.util.Set;

public class FontUtils {
   public static ResourceLocation getHdFontLocation(ResourceLocation fontLoc) {
      if (!Config.isCustomFonts()) {
         return fontLoc;
      }

      if (fontLoc == null) {
         return fontLoc;
      }

      if (!Config.isMinecraftThread()) {
         return fontLoc;
      }

      String resourcePath = fontLoc.getResourcePath();
      if (!resourcePath.startsWith("textures/")) {
         return fontLoc;
      }

      resourcePath = resourcePath.substring("textures/".length());
      resourcePath = "mcpatcher/" + resourcePath;
      ResourceLocation resourceLocation = new ResourceLocation(fontLoc.getResourceDomain(), resourcePath);
      return Config.hasResource(Config.getResourceManager(), resourceLocation) ? resourceLocation : fontLoc;
   }

   public static boolean checkProperties(Properties properties2, String text) {
      String property = properties2.getProperty(text);
      if (property == null) {
         return false;
      } else {
         String text2 = property.toLowerCase().trim();
         if (text2.equals("true") || text2.equals("on")) {
            return true;
         } else if (!text2.equals("false") && !text2.equals("off")) {
            Config.warn("Invalid value for " + text + ": " + property);
            return false;
         } else {
            return false;
         }
      }
   }

   public static Properties readFontProperties(ResourceLocation locationFontTexture) {
      String resourcePath2 = locationFontTexture.getResourcePath();
      PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
      if (!resourcePath2.endsWith(".png")) {
         return propertiesOrdered;
      }

      String text = resourcePath2.substring(0, resourcePath2.length() - ".png".length()) + ".properties";

      try {
         ResourceLocation resourceLocation = new ResourceLocation(locationFontTexture.getResourceDomain(), text);
         InputStream resourceStream = Config.getResourceStream(Config.getResourceManager(), resourceLocation);
         if (resourceStream == null) {
            return propertiesOrdered;
         }

         Config.log("Loading " + text);
         propertiesOrdered.load(resourceStream);
         resourceStream.close();
      } catch (FileNotFoundException fileNotFoundException) {
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }

      return propertiesOrdered;
   }

   public static float readFloat(Properties props, String key, float defOffset) {
      String propertyValue = props.getProperty(key);
      if (propertyValue == null) {
         return defOffset;
      } else {
         float f = Config.parseFloat(propertyValue, Float.MIN_VALUE);
         if (f == Float.MIN_VALUE) {
            Config.warn("Invalid value for " + key + ": " + propertyValue);
            return defOffset;
         } else {
            return f;
         }
      }
   }

   public static void readCustomCharWidths(Properties props, float[] charWidth) {
      for (String text : (Iterable<String>) (Iterable<String>) (Set) (props.keySet())) {
         if (text.startsWith("width.")) {
            int i = Config.parseInt(text.substring("width.".length()), -1);
            if (i >= 0 && i < charWidth.length) {
               float f = Config.parseFloat(props.getProperty(text), -1.0F);
               if (f >= 0.0F) {
                  charWidth[i] = f;
               }
            }
         }
      }
   }
}
