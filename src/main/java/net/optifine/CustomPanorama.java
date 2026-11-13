package net.optifine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.MathUtils;
import net.optifine.util.PropertiesOrdered;

public class CustomPanorama {
   private static CustomPanoramaProperties customPanoramaProperties = null;
   private static final Random random = new Random();

   public static CustomPanoramaProperties getCustomPanoramaProperties() {
      return customPanoramaProperties;
   }

   private static int[] getWeights(Properties[] propertiess) {
      int[] intArray = new int[propertiess.length];

      for (int index = 0; index < intArray.length; index++) {
         Properties properties2 = propertiess[index];
         if (properties2 == null) {
            properties2 = propertiess[0];
         }

         if (properties2 == null) {
            intArray[index] = 1;
         } else {
            String propertyValue = properties2.getProperty("weight", (String)null);
            intArray[index] = Config.parseInt(propertyValue, 1);
         }
      }

      return intArray;
   }

   private static String[] getPanoramaFolders() {
      ArrayList arrayList = new ArrayList();
      arrayList.add("textures/gui/title/background");

      for (int i = 0; i < 100; i++) {
         String text2 = "optifine/gui/background" + i;
         String text3 = text2 + "/panorama_0.png";
         if (Config.hasResource(new ResourceLocation(text3))) {
            arrayList.add(text2);
         }
      }

      return (String[]) arrayList.toArray(new String[arrayList.size()]);
   }

   private static int getRandomIndex(int[] weights) {
      int sum = MathUtils.getSum(weights);
      int i = random.nextInt(sum);
      int i2 = 0;

      for (int outerIndex = 0; outerIndex < weights.length; outerIndex++) {
         i2 += weights[outerIndex];
         if (i2 > i) {
            return outerIndex;
         }
      }

      return weights.length - 1;
   }

   private static Properties[] getPanoramaProperties(String[] folders) {
      Properties[] propertiesArray = new Properties[folders.length];

      for (int index = 0; index < folders.length; index++) {
         String text = folders[index];
         if (index == 0) {
            text = "optifine/gui";
         } else {
            Config.dbg("CustomPanorama: " + text);
         }

         ResourceLocation resourceLocation = new ResourceLocation(text + "/background.properties");

         try {
            InputStream inputStream = Config.getResourceStream(resourceLocation);
            if (inputStream != null) {
               PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
               propertiesOrdered.load(inputStream);
               Config.dbg("CustomPanorama: " + resourceLocation.getResourcePath());
               propertiesArray[index] = propertiesOrdered;
               inputStream.close();
            }
         } catch (IOException ioException) {
         }
      }

      return propertiesArray;
   }

   public static void update() {
      customPanoramaProperties = null;
      String[] panoramaFolders = getPanoramaFolders();
      if (panoramaFolders.length > 1) {
         Properties[] panoramaProperties = getPanoramaProperties(panoramaFolders);
         int randomIndex = getRandomIndex(getWeights(panoramaProperties));
         String text2 = panoramaFolders[randomIndex];
         Properties properties2 = panoramaProperties[randomIndex];
         if (properties2 == null) {
            properties2 = panoramaProperties[0];
         }

         if (properties2 == null) {
            properties2 = new PropertiesOrdered();
         }

         customPanoramaProperties = new CustomPanoramaProperties(text2, properties2);
      }
   }
}
