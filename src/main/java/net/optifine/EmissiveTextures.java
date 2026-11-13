package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.PropertiesOrdered;

public class EmissiveTextures {
   private static final String PNG = ".png";
   private static float lightMapY;
   private static float lightMapX;
   private static String suffixEmissive = null;
   private static String suffixEmissivePng = null;
   private static boolean active = false;
   private static boolean render = false;
   private static boolean hasEmissive = false;
   private static boolean renderEmissive = false;
   private static final ResourceLocation LOCATION_EMPTY = new ResourceLocation("mcpatcher/ctm/default/empty.png");

   public static void beginRender() {
      render = true;
      hasEmissive = false;
   }

   public static ITextureObject getEmissiveTexture(ITextureObject texture, Map<ResourceLocation, ITextureObject> mapTextures) {
      if (!render) {
         return texture;
      }

      if (!(texture instanceof SimpleTexture)) {
         return texture;
      }

      ResourceLocation locationEmissive2 = ((SimpleTexture)texture).locationEmissive;
      if (!renderEmissive) {
         if (locationEmissive2 != null) {
            hasEmissive = true;
         }

         return texture;
      } else {
         if (locationEmissive2 == null) {
            locationEmissive2 = LOCATION_EMPTY;
         }

         ITextureObject iTextureObject = mapTextures.get(locationEmissive2);
         if (iTextureObject == null) {
            iTextureObject = new SimpleTexture(locationEmissive2);
            Config.getTextureManager().loadTexture(locationEmissive2, iTextureObject);
         }

         return iTextureObject;
      }
   }

   public static void loadTexture(ResourceLocation loc, SimpleTexture tex) {
      if (loc != null && tex != null) {
         tex.isEmissive = false;
         tex.locationEmissive = null;
         if (suffixEmissivePng != null) {
            String resourcePath = loc.getResourcePath();
            if (resourcePath.endsWith(".png")) {
               if (resourcePath.endsWith(suffixEmissivePng)) {
                  tex.isEmissive = true;
               } else {
                  String text = resourcePath.substring(0, resourcePath.length() - ".png".length()) + suffixEmissivePng;
                  ResourceLocation resourceLocation = new ResourceLocation(loc.getResourceDomain(), text);
                  if (Config.hasResource(resourceLocation)) {
                     tex.locationEmissive = resourceLocation;
                  }
               }
            }
         }
      }
   }

   private static void dbg(String str) {
      Config.dbg("EmissiveTextures: " + str);
   }

   public static String getSuffixEmissive() {
      return suffixEmissive;
   }

   public static void endRenderEmissive() {
      renderEmissive = false;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapX, lightMapY);
   }

   public static void update() {
      active = false;
      suffixEmissive = null;
      suffixEmissivePng = null;
      if (Config.isEmissiveTextures()) {
         try {
            InputStream resourceStream = Config.getResourceStream(new ResourceLocation("optifine/emissive.properties"));
            if (resourceStream == null) {
               return;
            }

            dbg("Loading " + "optifine/emissive.properties");
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(resourceStream);
            resourceStream.close();
            suffixEmissive = propertiesOrdered.getProperty("suffix.emissive");
            if (suffixEmissive != null) {
               suffixEmissivePng = suffixEmissive + ".png";
            }

            active = suffixEmissive != null;
         } catch (FileNotFoundException fileNotFoundException) {
            return;
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      }
   }

   public static boolean isEmissive(ResourceLocation loc) {
      return suffixEmissivePng == null ? false : loc.getResourcePath().endsWith(suffixEmissivePng);
   }

   public static void endRender() {
      render = false;
      hasEmissive = false;
   }

   public static boolean hasEmissive() {
      return hasEmissive;
   }

   public static boolean isActive() {
      return active;
   }

   public static void beginRenderEmissive() {
      lightMapX = OpenGlHelper.lastBrightnessX;
      lightMapY = OpenGlHelper.lastBrightnessY;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, lightMapY);
      renderEmissive = true;
   }
}
