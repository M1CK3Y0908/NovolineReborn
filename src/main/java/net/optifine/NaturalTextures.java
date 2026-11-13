package net.optifine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.TextureUtils;

public class NaturalTextures {
   private static NaturalProperties[] propertiesByIndex = new NaturalProperties[0];

   public static NaturalProperties getNaturalProperties(TextureAtlasSprite icon) {
      if (!(icon instanceof TextureAtlasSprite)) {
         return null;
      }

      int indexInMap = icon.getIndexInMap();
      return indexInMap >= 0 && indexInMap < propertiesByIndex.length ? propertiesByIndex[indexInMap] : null;
   }

   public static BakedQuad getNaturalTexture(BlockPos blockPosIn, BakedQuad quad) {
      TextureAtlasSprite textureAtlasSprite = quad.getSprite();
      if (textureAtlasSprite == null) {
         return quad;
      }

      NaturalProperties naturalProperties = getNaturalProperties(textureAtlasSprite);
      if (naturalProperties == null) {
         return quad;
      }

      int side = ConnectedTextures.getSide(quad.getFace());
      int random = Config.getRandom(blockPosIn, side);
      int i = 0;
      boolean conditionMet = false;
      if (naturalProperties.rotation > 1) {
         i = random & 3;
      }

      if (naturalProperties.rotation == 2) {
         i = i / 2 * 2;
      }

      if (naturalProperties.flip) {
         conditionMet = (random & 4) != 0;
      }

      return naturalProperties.getQuad(quad, i, conditionMet);
   }

   public static void update() {
      propertiesByIndex = new NaturalProperties[0];
      if (Config.isNaturalTextures()) {
         try {
            ResourceLocation resourceLocation = new ResourceLocation("optifine/natural.properties");
            if (!Config.hasResource(resourceLocation)) {
               Config.dbg("NaturalTextures: configuration \"" + "optifine/natural.properties" + "\" not found");
               return;
            }

            boolean fromDefaultResourcePack = Config.isFromDefaultResourcePack(resourceLocation);
            InputStream resourceStream = Config.getResourceStream(resourceLocation);
            ArrayList arrayList = new ArrayList(256);
            String inputStream = Config.readInputStream(resourceStream);
            resourceStream.close();
            String[] texts = Config.tokenize(inputStream, "\n\r");
            if (fromDefaultResourcePack) {
               Config.dbg("Natural Textures: Parsing default configuration \"" + "optifine/natural.properties" + "\"");
               Config.dbg("Natural Textures: Valid only for textures from default resource pack");
            } else {
               Config.dbg("Natural Textures: Parsing configuration \"" + "optifine/natural.properties" + "\"");
            }

            TextureMap textureMapBlocks = TextureUtils.getTextureMapBlocks();

            for (int i = 0; i < texts.length; i++) {
               String text = texts[i].trim();
               if (!text.startsWith("#")) {
                  String[] texts2 = Config.tokenize(text, "=");
                  if (texts2.length != 2) {
                     Config.warn("Natural Textures: Invalid \"" + "optifine/natural.properties" + "\" line: " + text);
                  } else {
                     String text2 = texts2[0].trim();
                     String text3 = texts2[1].trim();
                     TextureAtlasSprite spriteSafe = textureMapBlocks.getSpriteSafe("minecraft:blocks/" + text2);
                     if (spriteSafe == null) {
                        Config.warn("Natural Textures: Texture not found: \"" + "optifine/natural.properties" + "\" line: " + text);
                     } else {
                        int indexInMap = spriteSafe.getIndexInMap();
                        if (indexInMap < 0) {
                           Config.warn("Natural Textures: Invalid \"" + "optifine/natural.properties" + "\" line: " + text);
                        } else {
                           if (fromDefaultResourcePack && !Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/" + text2 + ".png"))) {
                              return;
                           }

                           NaturalProperties naturalProperties2 = new NaturalProperties(text3);
                           if (naturalProperties2.isValid()) {
                              while (arrayList.size() <= indexInMap) {
                                 arrayList.add(null);
                              }

                              arrayList.set(indexInMap, naturalProperties2);
                              Config.dbg("NaturalTextures: " + text2 + " = " + text3);
                           }
                        }
                     }
                  }
               }
            }

            propertiesByIndex = (NaturalProperties[]) arrayList.toArray(new NaturalProperties[arrayList.size()]);
         } catch (FileNotFoundException fileNotFoundException) {
            Config.warn("NaturalTextures: configuration \"" + "optifine/natural.properties" + "\" not found");
            return;
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }
}
