package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

public class FlatGeneratorInfo {
   private final List<FlatLayerInfo> flatLayers = Lists.newArrayList();
   private final Map<String, Map<String, String>> worldFeatures = Maps.newHashMap();
   private int biomeToUse;

   public int getBiome() {
      return this.biomeToUse;
   }

   private static FlatLayerInfo parseLayer(int formatVersion, String layerString, int currentY) {
      String[] stringArray = formatVersion >= 3 ? layerString.split("\\*", 2) : layerString.split("x", 2);
      int i = 1;
      int i2 = 0;
      if (stringArray.length == 2) {
         try {
            i = Integer.parseInt(stringArray[0]);
            if (currentY + i >= 256) {
               i = 256 - currentY;
            }

            if (i < 0) {
               i = 0;
            }
         } catch (Throwable throwable) {
            return null;
         }
      }

      Block block2;
      try {
         String text = stringArray[stringArray.length - 1];
         if (formatVersion < 3) {
            stringArray = text.split(":", 2);
            if (stringArray.length > 1) {
               i2 = Integer.parseInt(stringArray[1]);
            }

            block2 = Block.getBlockById(Integer.parseInt(stringArray[0]));
         } else {
            stringArray = text.split(":", 3);
            block2 = stringArray.length > 1 ? Block.getBlockFromName(stringArray[0] + ":" + stringArray[1]) : null;
            if (block2 != null) {
               i2 = stringArray.length > 2 ? Integer.parseInt(stringArray[2]) : 0;
            } else {
               block2 = Block.getBlockFromName(stringArray[0]);
               if (block2 != null) {
                  i2 = stringArray.length > 1 ? Integer.parseInt(stringArray[1]) : 0;
               }
            }

            if (block2 == null) {
               return null;
            }
         }

         if (block2 == Blocks.air) {
            i2 = 0;
         }

         if (i2 < 0 || i2 > 15) {
            i2 = 0;
         }
      } catch (Throwable throwable2) {
         return null;
      }

      FlatLayerInfo flatLayerInfo = new FlatLayerInfo(formatVersion, i, block2, i2);
      flatLayerInfo.setMinY(currentY);
      return flatLayerInfo;
   }

   public static FlatGeneratorInfo getDefaultFlatGenerator() {
      FlatGeneratorInfo flatGeneratorInfo = new FlatGeneratorInfo();
      flatGeneratorInfo.setBiome(BiomeGenBase.plains.biomeID);
      flatGeneratorInfo.getFlatLayers().add(new FlatLayerInfo(1, Blocks.bedrock));
      flatGeneratorInfo.getFlatLayers().add(new FlatLayerInfo(2, Blocks.dirt));
      flatGeneratorInfo.getFlatLayers().add(new FlatLayerInfo(1, Blocks.grass));
      flatGeneratorInfo.updateLayerYValues();
      flatGeneratorInfo.getWorldFeatures().put("village", Maps.newHashMap());
      return flatGeneratorInfo;
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(3);
      stringBuilder.append(";");

      for (int i = 0; i < this.flatLayers.size(); i++) {
         if (i > 0) {
            stringBuilder.append(",");
         }

         stringBuilder.append(this.flatLayers.get(i).toString());
      }

      stringBuilder.append(";");
      stringBuilder.append(this.biomeToUse);
      if (!this.worldFeatures.isEmpty()) {
         stringBuilder.append(";");
         int i2 = 0;

         for (Entry entry : this.worldFeatures.entrySet()) {
            if (i2++ > 0) {
               stringBuilder.append(",");
            }

            stringBuilder.append(((String)entry.getKey()).toLowerCase());
            Map map = (Map)entry.getValue();
            if (!map.isEmpty()) {
               stringBuilder.append("(");
               int i3 = 0;

               for (Entry entry2 : (Iterable<Map.Entry>) map.entrySet()) {
                  if (i3++ > 0) {
                     stringBuilder.append(" ");
                  }

                  stringBuilder.append((String)entry2.getKey());
                  stringBuilder.append("=");
                  stringBuilder.append((String)entry2.getValue());
               }

               stringBuilder.append(")");
            }
         }
      } else {
         stringBuilder.append(";");
      }

      return stringBuilder.toString();
   }

   public static FlatGeneratorInfo createFlatGeneratorFromString(String flatGeneratorSettings) {
      if (flatGeneratorSettings == null) {
         return getDefaultFlatGenerator();
      }

      String[] texts = flatGeneratorSettings.split(";", -1);
      int i = texts.length == 1 ? 0 : MathHelper.parseIntWithDefault(texts[0], 0);
      if (i >= 0 && i <= 3) {
         FlatGeneratorInfo flatGeneratorInfo = new FlatGeneratorInfo();
         int i2 = texts.length == 1 ? 0 : 1;
         List list = parseLayers(i, texts[i2++]);
         if (list != null && !list.isEmpty()) {
            flatGeneratorInfo.getFlatLayers().addAll(list);
            flatGeneratorInfo.updateLayerYValues();
            int outerIndex = BiomeGenBase.plains.biomeID;
            if (i > 0 && texts.length > i2) {
               outerIndex = MathHelper.parseIntWithDefault(texts[i2++], outerIndex);
            }

            flatGeneratorInfo.setBiome(outerIndex);
            if (i > 0 && texts.length > i2) {
               String[] texts2 = texts[i2].toLowerCase().split(",");
               int length2 = texts2.length;

               for (int i3 = 0; i3 < length2; i3++) {
                  String[] texts3 = texts2[i3].split("\\(", 2);
                  HashMap hashMap = Maps.newHashMap();
                  if (texts3[0].length() > 0) {
                     flatGeneratorInfo.getWorldFeatures().put(texts3[0], hashMap);
                     if (texts3.length > 1 && texts3[1].endsWith(")") && texts3[1].length() > 1) {
                        String[] texts4 = texts3[1].substring(0, texts3[1].length() - 1).split(" ");

                        for (int i4 = 0; i4 < texts4.length; i4++) {
                           String[] texts5 = texts4[i4].split("=", 2);
                           if (texts5.length == 2) {
                              hashMap.put(texts5[0], texts5[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               flatGeneratorInfo.getWorldFeatures().put("village", Maps.newHashMap());
            }

            return flatGeneratorInfo;
         } else {
            return getDefaultFlatGenerator();
         }
      } else {
         return getDefaultFlatGenerator();
      }
   }

   public void setBiome(int biome) {
      this.biomeToUse = biome;
   }

   public Map<String, Map<String, String>> getWorldFeatures() {
      return this.worldFeatures;
   }

   public void updateLayerYValues() {
      int i = 0;

      for (FlatLayerInfo flatLayerInfo : this.flatLayers) {
         flatLayerInfo.setMinY(i);
         i += flatLayerInfo.getLayerCount();
      }
   }

   public List<FlatLayerInfo> getFlatLayers() {
      return this.flatLayers;
   }

   private static List<FlatLayerInfo> parseLayers(int formatVersion, String layersString) {
      if (layersString != null && layersString.length() >= 1) {
         ArrayList arrayList = Lists.newArrayList();
         String[] texts = layersString.split(",");
         int i = 0;

         for (String text : texts) {
            FlatLayerInfo flatLayerInfo = parseLayer(formatVersion, text, i);
            if (flatLayerInfo == null) {
               return null;
            }

            arrayList.add(flatLayerInfo);
            i += flatLayerInfo.getLayerCount();
         }

         return arrayList;
      } else {
         return null;
      }
   }
}
