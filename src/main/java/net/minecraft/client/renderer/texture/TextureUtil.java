package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.reflect.Reflector;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import wtf.novoline.utils.render.MipmapData;

public class TextureUtil {
   private static final Logger logger = LogManager.getLogger();
   private static final IntBuffer dataBuffer = GLAllocation.createDirectIntBuffer(4194304);
   public static final DynamicTexture missingTexture = new DynamicTexture(16, 16);
   public static final int[] missingTextureData = missingTexture.getTextureData();
   private static int[] dataArray = new int[4194304];
   private static final int[] mipmapBuffer;

   private static void setTextureBlurred(boolean blur) {
      setTextureBlurMipmap(blur, false);
   }

   private static void handleBufferedImage(BufferedImage bufferedImage2, boolean flag, boolean flag2) {
      int width2 = bufferedImage2.getWidth();
      int height2 = bufferedImage2.getHeight();
      int i = 4194304 / width2;
      int[] dataArray2 = dataArray;
      setTextureBlurred(flag);
      setTextureClamped(flag2);

      for (int i2 = 0; i2 < width2 * height2; i2 += width2 * i) {
         int i3 = i2 / width2;
         int i4 = Math.min(i, height2 - i3);
         int i5 = width2 * i4;
         bufferedImage2.getRGB(0, i3, width2, i4, dataArray2, 0, width2);
         copyToBuffer(dataArray2, i5);
         GL11.glTexSubImage2D(3553, 0, 0, 0 + i3, width2, i4, 32993, 33639, dataBuffer);
      }
   }

   public static void handleArray(int[][] iArrays, int i, int i2, int i3, int i4, boolean flag) {
      for (int i5 = 0; i5 < iArrays.length; i5++) {
         int[] iArray = iArrays[i5];
         handleInt(i5, iArray, i >> i5, i2 >> i5, i3 >> i5, i4 >> i5, flag, iArrays.length > 1);
      }
   }

   private static void handleInt(int i, int[] iArray, int i2, int i3, int i4, int i5, boolean flag, boolean flag2) {
      int i6 = 4194304 / i2;
      setTextureBlurMipmap(false, flag2);
      setTextureClamped(flag);
      int i7 = 0;

      while (i7 < i2 * i3) {
         int i8 = i7 / i2;
         int i9 = Math.min(i6, i3 - i8);
         int i10 = i2 * i9;
         copyToBufferPos(iArray, i7, i10);
         GL11.glTexSubImage2D(3553, i, i4, i5 + i8, i2, i9, 32993, 33639, dataBuffer);
         i7 += i2 * i9;
      }
   }

   public static void uploadTexture(int textureId, int[] textureData, int width, int height) {
      bindTexture(textureId);
      handleInt(0, textureData, width, height, 0, 0, false, false);
   }

   static {
      int[] iArray = new int[]{-524040, -524040, -524040, -524040, -524040, -524040, -524040, -524040};
      int[] iArray2 = new int[]{-16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216};
      int length2 = iArray.length;

      for (int i = 0; i < 16; i++) {
         System.arraycopy(i < length2 ? iArray : iArray2, 0, missingTextureData, 16 * i, length2);
         System.arraycopy(i < length2 ? iArray2 : iArray, 0, missingTextureData, 16 * i + length2, length2);
      }

      missingTexture.updateDynamicTexture();
      mipmapBuffer = new int[4];
   }

   public static void allocateTextureImpl(int textureId, int mipmapLevels, int width, int height) {
      Class<TextureUtil> classValue = TextureUtil.class;
      if (Reflector.reflectorClass51.isActive()) {
         classValue = Reflector.reflectorClass51.getType();
      }

      synchronized (classValue) {
         deleteTexture(textureId);
         bindTexture(textureId);
      }

      if (mipmapLevels >= 0) {
         GL11.glTexParameteri(3553, 33085, mipmapLevels);
         GL11.glTexParameterf(3553, 33082, 0.0F);
         GL11.glTexParameterf(3553, 33083, mipmapLevels);
         GL11.glTexParameterf(3553, 34049, 0.0F);
      }

      for (int i = 0; i <= mipmapLevels; i++) {
         GL11.glTexImage2D(3553, i, 6408, width >> i, height >> i, 0, 32993, 33639, (IntBuffer)null);
      }
   }

   public static void allocateTexture(int textureId, int width, int height) {
      allocateTextureImpl(textureId, 0, width, height);
   }

   public static void setTextureBlurMipmap(boolean blur, boolean mipmap) {
      if (blur) {
         GL11.glTexParameteri(3553, 10241, mipmap ? 9987 : 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
      } else {
         int mipmapType = Config.getMipmapType();
         GL11.glTexParameteri(3553, 10241, mipmap ? mipmapType : 9728);
         GL11.glTexParameteri(3553, 10240, 9728);
      }
   }

   public static int[] updateAnaglyph(int[] pixels) {
      int[] intArray = new int[pixels.length];

      for (int index = 0; index < pixels.length; index++) {
         intArray[index] = anaglyphColor(pixels[index]);
      }

      return intArray;
   }

   public static int anaglyphColor(int color) {
      int i = color >> 24 & 0xFF;
      int i2 = color >> 16 & 0xFF;
      int i3 = color >> 8 & 0xFF;
      int i4 = color & 0xFF;
      int i5 = (i2 * 30 + i3 * 59 + i4 * 11) / 100;
      int i6 = (i2 * 30 + i3 * 70) / 100;
      int i7 = (i2 * 30 + i4 * 70) / 100;
      return i << 24 | i5 << 16 | i6 << 8 | i7;
   }

   public static int[] readImageData(IResourceManager resourceManager, ResourceLocation imageLocation) throws IOException {
      BufferedImage bufferedImage = readBufferedImage(resourceManager.getResource(imageLocation).getInputStream());
      if (bufferedImage == null) {
         return null;
      }

      int width2 = bufferedImage.getWidth();
      int height2 = bufferedImage.getHeight();
      int[] intArray = new int[width2 * height2];
      bufferedImage.getRGB(0, 0, width2, height2, intArray, 0, width2);
      return intArray;
   }

   private static int createInt2(int i, int i2, int i3, int i4) {
      return MipmapData.averageFourColors(i, i2, i3, i4);
   }

   public static int glGenTextures() {
      return GlStateManager.generateTexture();
   }

   public static void processPixelValues(int[] pixels, int width, int height) {
      int[] intArray = new int[width];
      int i = height / 2;

      for (int innerIndex = 0; innerIndex < i; innerIndex++) {
         System.arraycopy(pixels, innerIndex * width, intArray, 0, width);
         System.arraycopy(pixels, (height - 1 - innerIndex) * width, pixels, innerIndex * width, width);
         System.arraycopy(intArray, 0, pixels, (height - 1 - innerIndex) * width, width);
      }
   }

   private static void copyToBufferPos(int[] textureData, int offset, int length) {
      int[] intArray = textureData;
      if (Minecraft.getMinecraft().gameSettings.anaglyph) {
         intArray = updateAnaglyph(textureData);
      }

      dataBuffer.clear();
      dataBuffer.put(intArray, offset, length);
      dataBuffer.position(0).limit(length);
   }

   public static void deleteTexture(int textureId) {
      GlStateManager.deleteTexture(textureId);
   }

   public static BufferedImage readBufferedImage(InputStream imageStream) throws IOException {
      if (imageStream == null) {
         return new BufferedImage(64, 64, 2);
      }

      BufferedImage bufferedImage;
      try {
         bufferedImage = ImageIO.read(imageStream);
      } finally {
         IOUtils.closeQuietly(imageStream);
      }

      return bufferedImage;
   }

   public static int uploadTextureImage(int textureId, BufferedImage image) {
      return uploadTextureImageAllocate(textureId, image, false, false);
   }

   public static void setTextureClamped(boolean clamp) {
      if (clamp) {
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
      } else {
         GL11.glTexParameteri(3553, 10242, 10497);
         GL11.glTexParameteri(3553, 10243, 10497);
      }
   }

   static void bindTexture(int textureId) {
      GlStateManager.bindTexture(textureId);
   }

   public static int createInt(int i, BufferedImage bufferedImage2, boolean flag, boolean flag2) {
      bindTexture(i);
      handleBufferedImage(bufferedImage2, flag, flag2);
      return i;
   }

   public static int uploadTextureImageAllocate(int textureId, BufferedImage image, boolean blur, boolean clamp) {
      allocateTexture(textureId, image.getWidth(), image.getHeight());
      return createInt(textureId, image, blur, clamp);
   }

   private static void copyToBuffer(int[] textureData, int length) {
      copyToBufferPos(textureData, 0, length);
   }

   public static int[][] generateMipmapData(int mipmapLevels, int width, int[][] textureData) {
      int[][] iArrays = new int[mipmapLevels + 1][];
      iArrays[0] = textureData[0];
      if (mipmapLevels > 0) {
         int index = 0;

         for (int i = 0; i < textureData[0].length; i++) {
            if (textureData[0][i] >> 24 == 0) {
               index = 1;
               break;
            }
         }

         for (int i2 = 1; i2 <= mipmapLevels; i2++) {
            if (textureData[i2] != null) {
               iArrays[i2] = textureData[i2];
            } else {
               int[] iArray = iArrays[i2 - 1];
               int[] iArray2 = new int[iArray.length >> 2];
               int count = width >> i2;
               int i3 = iArray2.length / count;
               int index2 = count << 1;

               for (int innerIndex = 0; innerIndex < count; innerIndex++) {
                  for (int i4 = 0; i4 < i3; i4++) {
                     int i5 = 2 * (innerIndex + i4 * index2);
                     iArray2[innerIndex + i4 * count] = createInt2(
                        iArray[i5 + 0], iArray[i5 + 1], iArray[i5 + 0 + index2], iArray[i5 + 1 + index2]
                     );
                  }
               }

               iArrays[i2] = iArray2;
            }
         }
      }

      return iArrays;
   }
}
