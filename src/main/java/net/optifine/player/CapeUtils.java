package net.optifine.player;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;

public class CapeUtils {
   private static final Pattern PATTERN_USERNAME = Pattern.compile("[a-zA-Z0-9_]+");

   public static boolean isElytraCape(BufferedImage imageRaw, BufferedImage imageFixed) {
      return imageRaw.getWidth() > imageFixed.getHeight();
   }

   public static void reloadCape(AbstractClientPlayer player) {
      String nameClear = player.getNameClear();
      ResourceLocation resourceLocation = new ResourceLocation("capeof/" + nameClear);
      TextureManager textureManager2 = Config.getTextureManager();
      ITextureObject texture2 = textureManager2.getTexture(resourceLocation);
      if (texture2 instanceof SimpleTexture) {
         ((SimpleTexture)texture2).deleteGlTexture();
         textureManager2.deleteTexture(resourceLocation);
      }

      player.setLocationOfCape((ResourceLocation)null);
      player.setElytraOfCape(false);
      downloadCape(player);
   }

   public static void downloadCape(AbstractClientPlayer player) {
      String text = player.getNameClear();
      if (text != null && !text.isEmpty() && !text.contains("\u0000") && PATTERN_USERNAME.matcher(text).matches()) {
         String text2 = "http://s.optifine.net/capes/" + text + ".png";
         ResourceLocation resourceLocation = new ResourceLocation("capeof/" + text);
         TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
         ITextureObject textureObject = textureManager.getTexture(resourceLocation);
         if (textureObject != null && textureObject instanceof ThreadDownloadImageData threadDownloadImageData && threadDownloadImageData.imageFound != null) {
            if (threadDownloadImageData.imageFound) {
               player.setLocationOfCape(resourceLocation);
               if (threadDownloadImageData.getImageBuffer() instanceof CapeImageBuffer) {
                  CapeImageBuffer capeImageBuffer = (CapeImageBuffer)threadDownloadImageData.getImageBuffer();
                  player.setElytraOfCape(capeImageBuffer.isElytraOfCape());
               }
            }

            return;
         }

         CapeImageBuffer capeImageBuffer2 = new CapeImageBuffer(player, resourceLocation);
         ThreadDownloadImageData capeImageBuffer = new ThreadDownloadImageData((File)null, text2, (ResourceLocation)null, capeImageBuffer2);
         capeImageBuffer.pipeline = true;
         textureManager.loadTexture(resourceLocation, capeImageBuffer);
      }
   }

   public static BufferedImage parseCape(BufferedImage img) {
      int i = 64;
      int i2 = 32;
      int width = img.getWidth();

      for (int outerIndex = img.getHeight(); i < width || i2 < outerIndex; i2 *= 2) {
         i *= 2;
      }

      BufferedImage bufferedImage = new BufferedImage(i, i2, 2);
      Graphics graphics = bufferedImage.getGraphics();
      graphics.drawImage(img, 0, 0, (ImageObserver)null);
      graphics.dispose();
      return bufferedImage;
   }
}
