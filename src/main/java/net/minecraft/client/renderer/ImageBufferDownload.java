package net.minecraft.client.renderer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;

public class ImageBufferDownload implements IImageBuffer {
   private int imageHeight;
   private int[] imageData;
   private int imageWidth;

   private void setAreaOpaque(int i, int i2, int i3, int i4) {
      for (int index = i; index < i3; index++) {
         for (int innerIndex = i2; innerIndex < i4; innerIndex++) {
            this.imageData[index + innerIndex * this.imageWidth] = this.imageData[index + innerIndex * this.imageWidth] | 0xFF000000;
         }
      }
   }

   @Override
   public BufferedImage parseUserSkin(BufferedImage image) {
      if (image == null) {
         return null;
      }

      this.imageWidth = 64;
      this.imageHeight = 64;
      int width = image.getWidth();
      int height = image.getHeight();

      int i;
      for (i = 1; this.imageWidth < width || this.imageHeight < height; i *= 2) {
         this.imageWidth *= 2;
         this.imageHeight *= 2;
      }

      BufferedImage bufferedImage = new BufferedImage(this.imageWidth, this.imageHeight, 2);
      Graphics graphics = bufferedImage.getGraphics();
      graphics.drawImage(image, 0, 0, (ImageObserver)null);
      if (image.getHeight() == 32 * i) {
         graphics.drawImage(
            bufferedImage,
            24 * i,
            48 * i,
            20 * i,
            52 * i,
            4 * i,
            16 * i,
            8 * i,
            20 * i,
            (ImageObserver)null
         );
         graphics.drawImage(
            bufferedImage,
            28 * i,
            48 * i,
            24 * i,
            52 * i,
            8 * i,
            16 * i,
            12 * i,
            20 * i,
            (ImageObserver)null
         );
         graphics.drawImage(
            bufferedImage,
            20 * i,
            52 * i,
            16 * i,
            64 * i,
            8 * i,
            20 * i,
            12 * i,
            32 * i,
            (ImageObserver)null
         );
         graphics.drawImage(
            bufferedImage,
            24 * i,
            52 * i,
            20 * i,
            64 * i,
            4 * i,
            20 * i,
            8 * i,
            32 * i,
            (ImageObserver)null
         );
         graphics.drawImage(
            bufferedImage,
            28 * i,
            52 * i,
            24 * i,
            64 * i,
            0 * i,
            20 * i,
            4 * i,
            32 * i,
            (ImageObserver)null
         );
         graphics.drawImage(
            bufferedImage,
            32 * i,
            52 * i,
            28 * i,
            64 * i,
            12 * i,
            20 * i,
            16 * i,
            32 * i,
            (ImageObserver)null
         );
         graphics.drawImage(
            bufferedImage,
            40 * i,
            48 * i,
            36 * i,
            52 * i,
            44 * i,
            16 * i,
            48 * i,
            20 * i,
            (ImageObserver)null
         );
         graphics.drawImage(
            bufferedImage,
            44 * i,
            48 * i,
            40 * i,
            52 * i,
            48 * i,
            16 * i,
            52 * i,
            20 * i,
            (ImageObserver)null
         );
         graphics.drawImage(
            bufferedImage,
            36 * i,
            52 * i,
            32 * i,
            64 * i,
            48 * i,
            20 * i,
            52 * i,
            32 * i,
            (ImageObserver)null
         );
         graphics.drawImage(
            bufferedImage,
            40 * i,
            52 * i,
            36 * i,
            64 * i,
            44 * i,
            20 * i,
            48 * i,
            32 * i,
            (ImageObserver)null
         );
         graphics.drawImage(
            bufferedImage,
            44 * i,
            52 * i,
            40 * i,
            64 * i,
            40 * i,
            20 * i,
            44 * i,
            32 * i,
            (ImageObserver)null
         );
         graphics.drawImage(
            bufferedImage,
            48 * i,
            52 * i,
            44 * i,
            64 * i,
            52 * i,
            20 * i,
            56 * i,
            32 * i,
            (ImageObserver)null
         );
      }

      graphics.dispose();
      this.imageData = ((DataBufferInt)bufferedImage.getRaster().getDataBuffer()).getData();
      this.setAreaOpaque(0 * i, 0 * i, 32 * i, 16 * i);
      this.setAreaTransparent(32 * i, 0 * i, 64 * i, 32 * i);
      this.setAreaOpaque(0 * i, 16 * i, 64 * i, 32 * i);
      this.setAreaTransparent(0 * i, 32 * i, 16 * i, 48 * i);
      this.setAreaTransparent(16 * i, 32 * i, 40 * i, 48 * i);
      this.setAreaTransparent(40 * i, 32 * i, 56 * i, 48 * i);
      this.setAreaTransparent(0 * i, 48 * i, 16 * i, 64 * i);
      this.setAreaOpaque(16 * i, 48 * i, 48 * i, 64 * i);
      this.setAreaTransparent(48 * i, 48 * i, 64 * i, 64 * i);
      return bufferedImage;
   }

   private boolean hasTransparency(int i, int i2, int i3, int i4) {
      for (int i5 = i; i5 < i3; i5++) {
         for (int i6 = i2; i6 < i4; i6++) {
            if ((this.imageData[i5 + i6 * this.imageWidth] >> 24 & 0xFF) < 128) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public void skinAvailable() {
   }

   private void setAreaTransparent(int i, int i2, int i3, int i4) {
      if (!this.hasTransparency(i, i2, i3, i4)) {
         for (int index = i; index < i3; index++) {
            for (int innerIndex = i2; innerIndex < i4; innerIndex++) {
               this.imageData[index + innerIndex * this.imageWidth] = this.imageData[index + innerIndex * this.imageWidth] & 16777215;
            }
         }
      }
   }
}
