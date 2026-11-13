package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.render.Blender;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.TextureUtils;

public class CustomSky {
   private static CustomSkyLayer[][] worldSkyLayers = (CustomSkyLayer[][])null;

   public static boolean hasSkyLayers(World world) {
      if (worldSkyLayers == null) {
         return false;
      } else {
         int dimensionId = world.provider.getDimensionId();
         if (dimensionId >= 0 && dimensionId < worldSkyLayers.length) {
            CustomSkyLayer[] customSkyLayers = worldSkyLayers[dimensionId];
            return customSkyLayers == null ? false : customSkyLayers.length > 0;
         } else {
            return false;
         }
      }
   }

   public static void update() {
      reset();
      if (Config.isCustomSky()) {
         worldSkyLayers = readCustomSkies();
      }
   }

   public static void reset() {
      worldSkyLayers = (CustomSkyLayer[][])null;
   }

   private static CustomSkyLayer[][] readCustomSkies() {
      CustomSkyLayer[][] customSkyLayersArray = new CustomSkyLayer[10][0];
      int i = -1;

      for (int i2 = 0; i2 < customSkyLayersArray.length; i2++) {
         String text = "mcpatcher/sky/world" + i2 + "/sky";
         ArrayList arrayList = new ArrayList();

         for (int i3 = 1; i3 < 1000; i3++) {
            String text2 = text + i3 + ".properties";

            try {
               InputStream resourceStream = Config.getResourceStream(new ResourceLocation(text2));
               if (resourceStream == null) {
                  break;
               }

               PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
               propertiesOrdered.load(resourceStream);
               resourceStream.close();
               Config.dbg("CustomSky properties: " + text2);
               String text3 = text + i3 + ".png";
               CustomSkyLayer customSkyLayer = new CustomSkyLayer(propertiesOrdered, text3);
               if (customSkyLayer.isValid(text2)) {
                  ResourceLocation resourceLocation = new ResourceLocation(customSkyLayer.source);
                  ITextureObject texture2 = TextureUtils.getTexture(resourceLocation);
                  if (texture2 == null) {
                     Config.log("CustomSky: Texture not found: " + resourceLocation);
                  } else {
                     customSkyLayer.textureId = texture2.getGlTextureId();
                     arrayList.add(customSkyLayer);
                     resourceStream.close();
                  }
               }
            } catch (FileNotFoundException fileNotFoundException) {
               break;
            } catch (IOException ioException) {
               ioException.printStackTrace();
            }
         }

         if (arrayList.size() > 0) {
            CustomSkyLayer[] customSkyLayers = (CustomSkyLayer[]) arrayList.toArray(new CustomSkyLayer[arrayList.size()]);
            customSkyLayersArray[i2] = customSkyLayers;
            i = i2;
         }
      }

      if (i < 0) {
         return (CustomSkyLayer[][])null;
      }

      int i4 = i + 1;
      CustomSkyLayer[][] customSkyLayersArray2 = new CustomSkyLayer[i4][0];

      for (int i5 = 0; i5 < customSkyLayersArray2.length; i5++) {
         customSkyLayersArray2[i5] = customSkyLayersArray[i5];
      }

      return customSkyLayersArray2;
   }

   public static void renderSky(World world, TextureManager re, float partialTicks) {
      if (worldSkyLayers != null) {
         int dimensionId = world.provider.getDimensionId();
         if (dimensionId >= 0 && dimensionId < worldSkyLayers.length) {
            CustomSkyLayer[] customSkyLayers = worldSkyLayers[dimensionId];
            if (customSkyLayers != null) {
               long worldTime = world.getWorldTime();
               int i = (int)(worldTime % 24000L);
               float celestialAngle = world.getCelestialAngle(partialTicks);
               float rainStrength = world.getRainStrength(partialTicks);
               float thunderStrength = world.getThunderStrength(partialTicks);
               if (rainStrength > 0.0F) {
                  thunderStrength /= rainStrength;
               }

               for (int outerIndex = 0; outerIndex < customSkyLayers.length; outerIndex++) {
                  CustomSkyLayer customSkyLayer = customSkyLayers[outerIndex];
                  if (customSkyLayer.isActive(world, i)) {
                     customSkyLayer.render(world, i, celestialAngle, rainStrength, thunderStrength);
                  }
               }

               float f = 1.0F - rainStrength;
               Blender.clearBlend(f);
            }
         }
      }
   }
}
