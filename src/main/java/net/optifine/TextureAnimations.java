package net.optifine;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import javax.imageio.ImageIO;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.TextureUtils;

public class TextureAnimations {
   private static TextureAnimation[] textureAnimations = null;
   private static int countAnimationsActive = 0;
   private static int frameCountAnimations = 0;

   private static byte[] getCustomTextureData(String imagePath, int tileWidth) {
      byte[] byteArray = loadImage(imagePath, tileWidth);
      if (byteArray == null) {
         byteArray = loadImage("/anim" + imagePath, tileWidth);
      }

      return byteArray;
   }

   private static byte[] loadImage(String name, int targetWidth) {
      GameSettings gameSettings = Config.getGameSettings();

      try {
         InputStream resourceStream = Config.getResourceStream(new ResourceLocation(name));
         if (resourceStream == null) {
            return null;
         }

         BufferedImage textureImage = readTextureImage(resourceStream);
         resourceStream.close();
         if (textureImage == null) {
            return null;
         }

         if (targetWidth > 0 && textureImage.getWidth() != targetWidth) {
            double d = textureImage.getHeight() / textureImage.getWidth();
            int i = (int)(targetWidth * d);
            textureImage = scaleBufferedImage(textureImage, targetWidth, i);
         }

         int width2 = textureImage.getWidth();
         int height2 = textureImage.getHeight();
         int[] iArray = new int[width2 * height2];
         byte[] bArray = new byte[width2 * height2 * 4];
         textureImage.getRGB(0, 0, width2, height2, iArray, 0, width2);

         for (int i2 = 0; i2 < iArray.length; i2++) {
            int i3 = iArray[i2] >> 24 & 0xFF;
            int i4 = iArray[i2] >> 16 & 0xFF;
            int i5 = iArray[i2] >> 8 & 0xFF;
            int i6 = iArray[i2] & 0xFF;
            if (gameSettings != null && gameSettings.anaglyph) {
               int i7 = (i4 * 30 + i5 * 59 + i6 * 11) / 100;
               int i8 = (i4 * 30 + i5 * 70) / 100;
               int i9 = (i4 * 30 + i6 * 70) / 100;
               i4 = i7;
               i5 = i8;
               i6 = i9;
            }

            bArray[i2 * 4 + 0] = (byte)i4;
            bArray[i2 * 4 + 1] = (byte)i5;
            bArray[i2 * 4 + 2] = (byte)i6;
            bArray[i2 * 4 + 3] = (byte)i3;
         }

         return bArray;
      } catch (FileNotFoundException fileNotFoundException) {
         return null;
      } catch (Exception exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public static int getCountAnimations() {
      return textureAnimations == null ? 0 : textureAnimations.length;
   }

   private static TextureAnimation[] getTextureAnimations(IResourcePack[] rps) {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < rps.length; i++) {
         TextureAnimation[] textureAnimations2 = getTextureAnimations(rps[i]);
         if (textureAnimations2 != null) {
            arrayList.addAll(Arrays.asList(textureAnimations2));
         }
      }

      return (TextureAnimation[]) arrayList.toArray(new TextureAnimation[arrayList.size()]);
   }

   public static int getCountAnimationsActive() {
      return countAnimationsActive;
   }

   public static void update() {
      textureAnimations = null;
      countAnimationsActive = 0;
      textureAnimations = getTextureAnimations(Config.getResourcePacks());
      updateAnimations();
   }

   public static void updateAnimations() {
      if (textureAnimations != null && Config.isAnimatedTextures()) {
         int i = 0;

         for (int innerIndex = 0; innerIndex < textureAnimations.length; innerIndex++) {
            TextureAnimation textureAnimation = textureAnimations[innerIndex];
            textureAnimation.updateTexture();
            if (textureAnimation.isActive()) {
               i++;
            }
         }

         int frameCount2 = Config.getMinecraft().entityRenderer.frameCount;
         if (frameCount2 != frameCountAnimations) {
            countAnimationsActive = i;
            frameCountAnimations = frameCount2;
         }

         if (SmartAnimations.isActive()) {
            SmartAnimations.resetTexturesRendered();
         }
      } else {
         countAnimationsActive = 0;
      }
   }

   private static BufferedImage scaleBufferedImage(BufferedImage image, int width, int height) {
      BufferedImage bufferedImage2 = new BufferedImage(width, height, 2);
      Graphics2D graphics2D = bufferedImage2.createGraphics();
      graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      graphics2D.drawImage(image, 0, 0, width, height, (ImageObserver)null);
      return bufferedImage2;
   }

   private static TextureAnimation makeTextureAnimation(Properties props, ResourceLocation propLoc) {
      String property = props.getProperty("from");
      String property2 = props.getProperty("to");
      int i = Config.parseInt(props.getProperty("x"), -1);
      int i2 = Config.parseInt(props.getProperty("y"), -1);
      int i3 = Config.parseInt(props.getProperty("w"), -1);
      int i4 = Config.parseInt(props.getProperty("h"), -1);
      if (property == null || property2 == null) {
         Config.warn("TextureAnimation: Source or target texture not specified");
         return null;
      }

      if (i >= 0 && i2 >= 0 && i3 >= 0 && i4 >= 0) {
         property = property.trim();
         property2 = property2.trim();
         String basePath = TextureUtils.getBasePath(propLoc.getResourcePath());
         property = TextureUtils.fixResourcePath(property, basePath);
         property2 = TextureUtils.fixResourcePath(property2, basePath);
         byte[] customTextureData = getCustomTextureData(property, i3);
         if (customTextureData == null) {
            Config.warn("TextureAnimation: Source texture not found: " + property2);
            return null;
         }

         int i5 = customTextureData.length / 4;
         int i6 = i5 / (i3 * i4) * i3 * i4;
         if (i5 != i6) {
            Config.warn("TextureAnimation: Source texture has invalid number of frames: " + property + ", frames: " + (float)i5 / (i3 * i4));
            return null;
         }

         ResourceLocation resourceLocation = new ResourceLocation(property2);

         try {
            InputStream resourceStream = Config.getResourceStream(resourceLocation);
            if (resourceStream == null) {
               Config.warn("TextureAnimation: Target texture not found: " + property2);
               return null;
            }

            BufferedImage textureImage = readTextureImage(resourceStream);
            if (i + i3 <= textureImage.getWidth() && i2 + i4 <= textureImage.getHeight()) {
               return new TextureAnimation(property, customTextureData, property2, resourceLocation, i, i2, i3, i4, props);
            }

            Config.warn("TextureAnimation: Animation coordinates are outside the target texture: " + property2);
            return null;
         } catch (IOException ioException) {
            Config.warn("TextureAnimation: Target texture not found: " + property2);
            return null;
         }
      } else {
         Config.warn("TextureAnimation: Invalid coordinates");
         return null;
      }
   }

   private static TextureAnimation[] getTextureAnimations(IResourcePack rp) {
      String[] texts = ResUtils.collectFiles(rp, "mcpatcher/anim/", ".properties", (String[])null);
      if (texts.length <= 0) {
         return null;
      }

      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < texts.length; i++) {
         String text = texts[i];
         Config.dbg("Texture animation: " + text);

         try {
            ResourceLocation resourceLocation = new ResourceLocation(text);
            InputStream inputStream = rp.getInputStream(resourceLocation);
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(inputStream);
            inputStream.close();
            TextureAnimation textureAnimation = makeTextureAnimation(propertiesOrdered, resourceLocation);
            if (textureAnimation != null) {
               if (Config.getDefiningResourcePack(new ResourceLocation(textureAnimation.getDstTex())) != rp) {
                  Config.dbg("Skipped: " + text + ", target texture not loaded from same resource pack");
               } else {
                  arrayList.add(textureAnimation);
               }
            }
         } catch (FileNotFoundException fileNotFoundException) {
            Config.warn("File not found: " + fileNotFoundException.getMessage());
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      }

      return (TextureAnimation[]) arrayList.toArray(new TextureAnimation[arrayList.size()]);
   }

   public static void reset() {
      textureAnimations = null;
   }

   private static BufferedImage readTextureImage(InputStream par1InputStream) throws IOException {
      BufferedImage bufferedImage = ImageIO.read(par1InputStream);
      par1InputStream.close();
      return bufferedImage;
   }
}
