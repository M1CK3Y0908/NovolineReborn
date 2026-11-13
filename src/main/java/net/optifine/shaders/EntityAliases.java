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

public class EntityAliases {
   private static int[] entityAliases = null;
   private static boolean updateOnResourcesReloaded;

   public static void reset() {
      entityAliases = null;
   }

   public static int getEntityAliasId(int entityId) {
      if (entityAliases == null) {
         return -1;
      } else {
         return entityId >= 0 && entityId < entityAliases.length ? entityAliases[entityId] : -1;
      }
   }

   private static void loadEntityAliases(InputStream in, String path, List<Integer> listEntityAliases) {
      if (in != null) {
         try {
            in = MacroProcessor.process(in, path);
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(in);
            in.close();
            Config.dbg("[Shaders] Parsing entity mappings: " + path);
            ConnectedParser connectedParser = new ConnectedParser("Shaders");

            for (String text : (Iterable<String>) (Iterable<String>) (Set) (propertiesOrdered.keySet())) {
               String property = propertiesOrdered.getProperty(text);
               if (!text.startsWith("entity.")) {
                  Config.warn("[Shaders] Invalid entity ID: " + text);
               } else {
                  int i = Config.parseInt(StrUtils.removePrefix(text, "entity."), -1);
                  if (i < 0) {
                     Config.warn("[Shaders] Invalid entity alias ID: " + i);
                  } else {
                     int[] iArray = connectedParser.parseEntities(property);
                     if (iArray != null && iArray.length >= 1) {
                        for (int i2 = 0; i2 < iArray.length; i2++) {
                           int i3 = iArray[i2];
                           addToList(listEntityAliases, i3, i);
                        }
                     } else {
                        Config.warn("[Shaders] Invalid entity ID mapping: " + text + "=" + property);
                     }
                  }
               }
            }
         } catch (IOException ioException) {
            Config.warn("[Shaders] Error reading: " + path);
         }
      }
   }

   public static void update(IShaderPack shaderPack) {
      reset();
      if (shaderPack != null) {
         if (Reflector.reflectorMethod98.isActive() && Config.getResourceManager() == null) {
            Config.dbg("[Shaders] Delayed loading of entity mappings after resources are loaded");
            updateOnResourcesReloaded = true;
         } else {
            ArrayList arrayList = new ArrayList();
            InputStream resourceAsStream = shaderPack.getResourceAsStream("/shaders/entity.properties");
            if (resourceAsStream != null) {
               loadEntityAliases(resourceAsStream, "/shaders/entity.properties", arrayList);
            }

            loadModEntityAliases(arrayList);
            if (arrayList.size() > 0) {
               entityAliases = toArray(arrayList);
            }
         }
      }
   }

   private static void addToList(List<Integer> list, int index, int val) {
      while (list.size() <= index) {
         list.add(-1);
      }

      list.set(index, val);
   }

   public static void resourcesReloaded() {
      if (updateOnResourcesReloaded) {
         updateOnResourcesReloaded = false;
         update(Shaders.getShaderPack());
      }
   }

   private static int[] toArray(List<Integer> list) {
      int[] intArray = new int[list.size()];

      for (int index = 0; index < intArray.length; index++) {
         intArray[index] = list.get(index);
      }

      return intArray;
   }

   private static void loadModEntityAliases(List<Integer> listEntityAliases) {
      String[] array = ReflectorForge.getArray();

      for (int i = 0; i < array.length; i++) {
         String text = array[i];

         try {
            ResourceLocation resourceLocation = new ResourceLocation(text, "shaders/entity.properties");
            loadEntityAliases(Config.getResourceStream(resourceLocation), resourceLocation.toString(), listEntityAliases);
         } catch (IOException ioException) {
         }
      }
   }
}
