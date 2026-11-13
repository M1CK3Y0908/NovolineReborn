package net.optifine.shaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;
import java.util.Set;

public class BlockAliases {
   private static BlockAlias[][] blockAliases = (BlockAlias[][])null;
   private static PropertiesOrdered blockLayerPropertes = null;
   private static boolean updateOnResourcesReloaded;

   public static void update(IShaderPack shaderPack) {
      reset();
      if (shaderPack != null) {
         if (Reflector.reflectorMethod98.isActive() && Minecraft.getMinecraft().getResourcePackRepository() == null) {
            Config.dbg("[Shaders] Delayed loading of block mappings after resources are loaded");
            updateOnResourcesReloaded = true;
         } else {
            ArrayList arrayList = new ArrayList();
            InputStream resourceAsStream = shaderPack.getResourceAsStream("/shaders/block.properties");
            if (resourceAsStream != null) {
               loadBlockAliases(resourceAsStream, "/shaders/block.properties", arrayList);
            }

            loadModBlockAliases(arrayList);
            if (arrayList.size() > 0) {
               blockAliases = toArrays(arrayList);
            }
         }
      }
   }

   public static PropertiesOrdered getBlockLayerPropertes() {
      return blockLayerPropertes;
   }

   public static void resourcesReloaded() {
      if (updateOnResourcesReloaded) {
         updateOnResourcesReloaded = false;
         update(Shaders.getShaderPack());
      }
   }

   private static BlockAlias[][] toArrays(List<List<BlockAlias>> listBlocksAliases) {
      BlockAlias[][] ablockalias = new BlockAlias[listBlocksAliases.size()][];

      for (int index = 0; index < ablockalias.length; index++) {
         List<BlockAlias> list = listBlocksAliases.get(index);
         if (list != null) {
            ablockalias[index] = list.toArray(new BlockAlias[list.size()]);
         }
      }

      return ablockalias;
   }

   public static void reset() {
      blockAliases = (BlockAlias[][])null;
      blockLayerPropertes = null;
   }

   private static void loadBlockAliases(InputStream in, String path, List<List<BlockAlias>> listBlockAliases) {
      if (in != null) {
         try {
            in = MacroProcessor.process(in, path);
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(in);
            in.close();
            Config.dbg("[Shaders] Parsing block mappings: " + path);
            ConnectedParser connectedParser = new ConnectedParser("Shaders");

            for (String text : (Iterable<String>) (Iterable<String>) (Set) (propertiesOrdered.keySet())) {
               String property = propertiesOrdered.getProperty(text);
               if (text.startsWith("layer.")) {
                  if (blockLayerPropertes == null) {
                     blockLayerPropertes = new PropertiesOrdered();
                  }

                  blockLayerPropertes.put(text, property);
               } else if (!text.startsWith("block.")) {
                  Config.warn("[Shaders] Invalid block ID: " + text);
               } else {
                  int i = Config.parseInt(StrUtils.removePrefix(text, "block."), -1);
                  if (i < 0) {
                     Config.warn("[Shaders] Invalid block ID: " + text);
                  } else {
                     MatchBlock[] matchBlocks = connectedParser.parseMatchBlocks(property);
                     if (matchBlocks != null && matchBlocks.length >= 1) {
                        BlockAlias blockAlias = new BlockAlias(i, matchBlocks);
                        addToList(listBlockAliases, blockAlias);
                     } else {
                        Config.warn("[Shaders] Invalid block ID mapping: " + text + "=" + property);
                     }
                  }
               }
            }
         } catch (IOException ioException) {
            Config.warn("[Shaders] Error reading: " + path);
         }
      }
   }

   private static void loadModBlockAliases(List<List<BlockAlias>> listBlockAliases) {
      String[] array = ReflectorForge.getArray();

      for (int i = 0; i < array.length; i++) {
         String text = array[i];

         try {
            ResourceLocation resourceLocation = new ResourceLocation(text, "shaders/block.properties");
            loadBlockAliases(Config.getResourceStream(resourceLocation), resourceLocation.toString(), listBlockAliases);
         } catch (IOException ioException) {
         }
      }
   }

   private static void addToList(List<List<BlockAlias>> blocksAliases, BlockAlias ba) {
      int[] matchBlockIds = ba.getMatchBlockIds();

      for (int i = 0; i < matchBlockIds.length; i++) {
         int i2 = matchBlockIds[i];

         while (i2 >= blocksAliases.size()) {
            blocksAliases.add(null);
         }

         List list2 = blocksAliases.get(i2);
         if (list2 == null) {
            list2 = new ArrayList();
            blocksAliases.set(i2, list2);
         }

         BlockAlias blockAlias = new BlockAlias(ba.getBlockAliasId(), ba.getMatchBlocks(i2));
         list2.add(blockAlias);
      }
   }

   public static int getBlockAliasId(int blockId, int metadata) {
      if (blockAliases == null) {
         return blockId;
      }

      if (blockId >= 0 && blockId < blockAliases.length) {
         BlockAlias[] blockAliasArray = blockAliases[blockId];
         if (blockAliasArray == null) {
            return blockId;
         }

         for (int i = 0; i < blockAliasArray.length; i++) {
            BlockAlias blockAlias = blockAliasArray[i];
            if (blockAlias.matches(blockId, metadata)) {
               return blockAlias.getBlockAliasId();
            }
         }

         return blockId;
      } else {
         return blockId;
      }
   }
}
