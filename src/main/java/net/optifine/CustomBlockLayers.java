package net.optifine;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.src.Config;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.shaders.BlockAliases;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;

public class CustomBlockLayers {
   private static EnumWorldBlockLayer[] renderLayers = null;
   public static boolean active = false;

   private static void readLayers(String pathProps, Properties props, List<EnumWorldBlockLayer> list) {
      Config.dbg("CustomBlockLayers: " + pathProps);
      readLayer("solid", EnumWorldBlockLayer.SOLID, props, list);
      readLayer("cutout", EnumWorldBlockLayer.CUTOUT, props, list);
      readLayer("cutout_mipped", EnumWorldBlockLayer.CUTOUT_MIPPED, props, list);
      readLayer("translucent", EnumWorldBlockLayer.TRANSLUCENT, props, list);
   }

   private static void readLayer(String name, EnumWorldBlockLayer layer, Properties props, List<EnumWorldBlockLayer> listLayers) {
      String text = "layer." + name;
      String property = props.getProperty(text);
      if (property != null) {
         MatchBlock[] matchBlocks = new ConnectedParser("CustomBlockLayers").parseMatchBlocks(property);
         if (matchBlocks != null) {
            for (int i = 0; i < matchBlocks.length; i++) {
               int blockId = matchBlocks[i].getBlockId();
               if (blockId > 0) {
                  while (listLayers.size() < blockId + 1) {
                     listLayers.add(null);
                  }

                  if (listLayers.get(blockId) != null) {
                     Config.warn("CustomBlockLayers: Block layer is already set, block: " + blockId + ", layer: " + name);
                  }

                  listLayers.set(blockId, layer);
               }
            }
         }
      }
   }

   public static EnumWorldBlockLayer getRenderLayer(IBlockState blockState) {
      if (renderLayers == null) {
         return null;
      }

      if (blockState.getBlock().isOpaqueCube()) {
         return null;
      }

      if (!(blockState instanceof BlockStateBase)) {
         return null;
      }

      int blockId = ((BlockStateBase)blockState).getBlockId();
      return blockId > 0 && blockId < renderLayers.length ? renderLayers[blockId] : null;
   }

   public static boolean isActive() {
      return active;
   }

   public static void update() {
      renderLayers = null;
      active = false;
      ArrayList arrayList = new ArrayList();
      Properties properties2 = ResUtils.readProperties("optifine/block.properties", "CustomBlockLayers");
      if (properties2 != null) {
         readLayers("optifine/block.properties", properties2, arrayList);
      }

      if (Config.isShaders()) {
         PropertiesOrdered blockLayerPropertes = BlockAliases.getBlockLayerPropertes();
         if (blockLayerPropertes != null) {
            readLayers("shaders/block.properties", blockLayerPropertes, arrayList);
         }
      }

      if (!arrayList.isEmpty()) {
         renderLayers = (EnumWorldBlockLayer[]) arrayList.toArray(new EnumWorldBlockLayer[arrayList.size()]);
         active = true;
      }
   }
}
