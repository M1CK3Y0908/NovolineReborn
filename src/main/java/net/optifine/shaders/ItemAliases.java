package net.optifine.shaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;
import java.util.Set;

public class ItemAliases {
   private static int[] itemAliases = null;
   private static final int intValue = Integer.MIN_VALUE;
   private static boolean updateOnResourcesReloaded;

   private static void loadModItemAliases(List<Integer> listItemAliases) {
      String[] array = ReflectorForge.getArray();

      for (int i = 0; i < array.length; i++) {
         String text = array[i];

         try {
            ResourceLocation resourceLocation = new ResourceLocation(text, "shaders/item.properties");
            loadItemAliases(Config.getResourceStream(resourceLocation), resourceLocation.toString(), listItemAliases);
         } catch (IOException ioException) {
         }
      }
   }

   public static int getItemAliasId(int itemId) {
      if (itemAliases == null) {
         return itemId;
      } else if (itemId >= 0 && itemId < itemAliases.length) {
         int i = itemAliases[itemId];
         return i == Integer.MIN_VALUE ? itemId : i;
      } else {
         return itemId;
      }
   }

   private static int[] toArray(List<Integer> list) {
      int[] intArray = new int[list.size()];

      for (int index = 0; index < intArray.length; index++) {
         intArray[index] = list.get(index);
      }

      return intArray;
   }

   private static void loadItemAliases(InputStream in, String path, List<Integer> listItemAliases) {
      if (in != null) {
         try {
            in = MacroProcessor.process(in, path);
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(in);
            in.close();
            Config.dbg("[Shaders] Parsing item mappings: " + path);
            ConnectedParser connectedParser = new ConnectedParser("Shaders");

            for (String text : (Iterable<String>) (Iterable<String>) (Set) (propertiesOrdered.keySet())) {
               String property = propertiesOrdered.getProperty(text);
               if (!text.startsWith("item.")) {
                  Config.warn("[Shaders] Invalid item ID: " + text);
               } else {
                  int i = Config.parseInt(StrUtils.removePrefix(text, "item."), -1);
                  if (i < 0) {
                     Config.warn("[Shaders] Invalid item alias ID: " + i);
                  } else {
                     int[] iArray = connectedParser.parseItems(property);
                     if (iArray != null && iArray.length >= 1) {
                        for (int i2 = 0; i2 < iArray.length; i2++) {
                           int i3 = iArray[i2];
                           addToList(listItemAliases, i3, i);
                        }
                     } else {
                        Config.warn("[Shaders] Invalid item ID mapping: " + text + "=" + property);
                     }
                  }
               }
            }
         } catch (IOException ioException) {
            Config.warn("[Shaders] Error reading: " + path);
         }
      }
   }

   public static void resourcesReloaded() {
      if (updateOnResourcesReloaded) {
         updateOnResourcesReloaded = false;
         update(Shaders.getShaderPack());
      }
   }

   public static void update(IShaderPack shaderPack) {
      reset();
      if (shaderPack != null) {
         if (Reflector.reflectorMethod98.isActive() && Config.getResourceManager() == null) {
            Config.dbg("[Shaders] Delayed loading of item mappings after resources are loaded");
            updateOnResourcesReloaded = true;
         } else {
            ArrayList arrayList = new ArrayList();
            InputStream resourceAsStream = shaderPack.getResourceAsStream("/shaders/item.properties");
            if (resourceAsStream != null) {
               loadItemAliases(resourceAsStream, "/shaders/item.properties", arrayList);
            }

            loadModItemAliases(arrayList);
            if (arrayList.size() > 0) {
               itemAliases = toArray(arrayList);
            }
         }
      }
   }

   public static void reset() {
      itemAliases = null;
   }

   private static void addToList(List<Integer> list, int index, int val) {
      while (list.size() <= index) {
         list.add(Integer.MIN_VALUE);
      }

      list.set(index, val);
   }
}
