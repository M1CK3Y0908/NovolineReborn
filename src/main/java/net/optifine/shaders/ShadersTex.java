package net.optifine.shaders;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class ShadersTex {
   public static final int defNormTexColor = -8421377;
   public static ByteBuffer byteBuffer = BufferUtils.createByteBuffer(4194304);
   public static final int intValue = 0;
   public static IntBuffer intBuffer = byteBuffer.asIntBuffer();
   public static int[] intArray = new int[1048576];
   public static Map<Integer, MultiTexID> multiTexMap = new HashMap<>();
   public static final int intValue2 = 0;
   public static final int initialBufferSize = 1048576;

   public static void bindNSTextures(int normTex, int specTex) {
      if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984) {
         GlStateManager.setActiveTexture(33986);
         GlStateManager.bindTexture(normTex);
         GlStateManager.setActiveTexture(33987);
         GlStateManager.bindTexture(specTex);
         GlStateManager.setActiveTexture(33984);
      }
   }

   public static MultiTexID getMultiTexID(AbstractTexture tex) {
      MultiTexID multiTexID = tex.multiTex;
      if (multiTexID == null) {
         int glTextureId = tex.getGlTextureId();
         multiTexID = multiTexMap.get(glTextureId);
         if (multiTexID == null) {
            multiTexID = new MultiTexID(glTextureId, GL11.glGenTextures(), GL11.glGenTextures());
            multiTexMap.put(glTextureId, multiTexID);
         }

         tex.multiTex = multiTexID;
      }

      return multiTexID;
   }

   public static int roundUpPOT(int x) {
      int i = x - 1;
      i |= i >> 1;
      i |= i >> 2;
      i |= i >> 4;
      i |= i >> 8;
      i |= i >> 16;
      return i + 1;
   }

   public static void updateTextureMinMagFilter() {
      ITextureObject texture2 = Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture);
      if (texture2 != null) {
         MultiTexID multiTexID2 = texture2.getMultiTexID();
         GlStateManager.bindTexture(multiTexID2.base);
         GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilB]);
         GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilB]);
         GlStateManager.bindTexture(multiTexID2.norm);
         GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilN]);
         GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilN]);
         GlStateManager.bindTexture(multiTexID2.spec);
         GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilS]);
         GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilS]);
         GlStateManager.bindTexture(0);
      }
   }

   public static int[] getIntArray(int size) {
      if (intArray == null) {
         intArray = new int[1048576];
      }

      if (intArray.length < size) {
         intArray = new int[roundUpPOT(size)];
      }

      return intArray;
   }

   public static ResourceLocation getNSMapLocation(ResourceLocation location, String mapName) {
      if (location == null) {
         return null;
      }

      String text = location.getResourcePath().split(".png")[0];
      return new ResourceLocation(location.getResourceDomain(), text + "_" + mapName + ".png");
   }

   public static int loadSimpleTexture(
      int textureID,
      BufferedImage bufferedImage,
      boolean linear,
      boolean clamp,
      IResourceManager resourceManager,
      ResourceLocation location,
      MultiTexID multiTex
   ) {
      int width2 = bufferedImage.getWidth();
      int height2 = bufferedImage.getHeight();
      int[] intArray2 = getIntArray(width2 * height2 * 3);
      bufferedImage.getRGB(0, 0, width2, height2, intArray2, 0, width2);
      loadNSMap(resourceManager, location, width2, height2, intArray2);
      setupTexture(multiTex, intArray2, width2, height2, linear, clamp);
      return textureID;
   }

   public static int blend4Simple(int c0, int c1, int c2, int c3) {
      return ((c0 >>> 24 & 0xFF) + (c1 >>> 24 & 0xFF) + (c2 >>> 24 & 0xFF) + (c3 >>> 24 & 0xFF) + 2) / 4 << 24
         | ((c0 >>> 16 & 0xFF) + (c1 >>> 16 & 0xFF) + (c2 >>> 16 & 0xFF) + (c3 >>> 16 & 0xFF) + 2) / 4 << 16
         | ((c0 >>> 8 & 0xFF) + (c1 >>> 8 & 0xFF) + (c2 >>> 8 & 0xFF) + (c3 >>> 8 & 0xFF) + 2) / 4 << 8
         | ((c0 >>> 0 & 0xFF) + (c1 >>> 0 & 0xFF) + (c2 >>> 0 & 0xFF) + (c3 >>> 0 & 0xFF) + 2) / 4 << 0;
   }

   public static ITextureObject createDefaultTexture() {
      DynamicTexture dynamicTexture = new DynamicTexture(1, 1);
      dynamicTexture.getTextureData()[0] = -1;
      dynamicTexture.updateDynamicTexture();
      return dynamicTexture;
   }

   public static int[] getArray() {
      int[] iArray = new int[3];
      Arrays.fill(iArray, 0, 1, -1);
      Arrays.fill(iArray, 1, 2, -8421377);
      Arrays.fill(iArray, 2, 3, 0);
      return iArray;
   }

   private static void loadNSMap1(IResourceManager manager, ResourceLocation location, int width, int height, int[] intArray, int offset, int defaultColor) {
      if (!loadNSMapFile(manager, location, width, height, intArray, offset)) {
         Arrays.fill(intArray, offset, offset + width * height, defaultColor);
      }
   }

   public static int[][] createArray(TextureMap textureMap, String text, int i, int i2, int i3, int i4) {
      MultiTexID multiTex2 = textureMap.multiTex;
      int[][] iArrays = new int[i3][];
      int[] iArray;
      iArrays[0] = iArray = new int[i * i2];
      boolean flag = false;
      BufferedImage image2 = readImage(textureMap.completeResourceLocation(new ResourceLocation(text)));
      if (image2 != null) {
         int width2 = image2.getWidth();
         image2.getHeight();
         if (width2 + 0 == i) {
            flag = true;
            image2.getRGB(0, 0, width2, width2, iArray, 0, width2);
         }
      }

      if (!flag) {
         Arrays.fill(iArray, i4);
      }

      GlStateManager.bindTexture(multiTex2.spec);
      return genMipmapsSimple(iArrays.length - 1, i, iArrays);
   }

   public static int[][] genMipmapsSimple(int maxLevel, int width, int[][] data) {
      for (int index = 1; index <= maxLevel; index++) {
         if (data[index] == null) {
            int i = width >> index;
            int i2 = i * 2;
            int[] iArray = data[index - 1];
            int[] iArray2 = data[index] = new int[i * i];

            for (int index2 = 0; index2 < i; index2++) {
               for (int outerIndex = 0; outerIndex < i; outerIndex++) {
                  int i3 = index2 * 2 * i2 + outerIndex * 2;
                  iArray2[index2 * i + outerIndex] = blend4Simple(
                     iArray[i3], iArray[i3 + 1], iArray[i3 + i2], iArray[i3 + i2 + 1]
                  );
               }
            }
         }
      }

      return data;
   }

   public static void deleteTextures(AbstractTexture atex, int texid) {
      MultiTexID multiTexID = atex.multiTex;
      if (multiTexID != null) {
         atex.multiTex = null;
         multiTexMap.remove(multiTexID.base);
         GlStateManager.deleteTexture(multiTexID.norm);
         GlStateManager.deleteTexture(multiTexID.spec);
         if (multiTexID.base != texid) {
            SMCLog.warning("Error : MultiTexID.base mismatch: " + multiTexID.base + ", texid: " + texid);
            GlStateManager.deleteTexture(multiTexID.base);
         }
      }
   }

   public static void fixTransparentColor(TextureAtlasSprite tas, int[] intArray) {
   }

   public static IntBuffer getIntBuffer(int size) {
      if (intBuffer.capacity() < size) {
         byteBuffer = BufferUtils.createByteBuffer(roundUpPOT(size) * 4);
         intBuffer = byteBuffer.asIntBuffer();
      }

      return intBuffer;
   }

   private static boolean loadNSMapFile(IResourceManager manager, ResourceLocation location, int width, int height, int[] intArray, int offset) {
      if (location == null) {
         return false;
      }

      try {
         BufferedImage bufferedImage2 = ImageIO.read(manager.getResource(location).getInputStream());
         if (bufferedImage2 == null) {
            return false;
         } else if (bufferedImage2.getWidth() == width && bufferedImage2.getHeight() == height) {
            bufferedImage2.getRGB(0, 0, width, height, intArray, offset, width);
            return true;
         } else {
            return false;
         }
      } catch (IOException ioException) {
         return false;
      }
   }

   public static void allocateTextureMap(int texID, int mipmapLevels, int width, int height, Stitcher stitcher, TextureMap tex) {
      SMCLog.info("allocateTextureMap " + mipmapLevels + " " + width + " " + height + " ");
      tex.atlasWidth = width;
      tex.atlasHeight = height;
      MultiTexID multiTexID = getMultiTexID(tex);
      TextureUtil.allocateTextureImpl(multiTexID.base, mipmapLevels, width, height);
      if (Shaders.configNormalMap) {
         TextureUtil.allocateTextureImpl(multiTexID.norm, mipmapLevels, width, height);
      }

      if (Shaders.configSpecularMap) {
         TextureUtil.allocateTextureImpl(multiTexID.spec, mipmapLevels, width, height);
      }

      GlStateManager.bindTexture(texID);
   }

   public static void bindNSTextures(MultiTexID multiTex) {
      bindNSTextures(multiTex.norm, multiTex.spec);
   }

   public static BufferedImage readImage(ResourceLocation resLoc) {
      try {
         if (!Config.hasResource(resLoc)) {
            return null;
         }

         InputStream resourceStream = Config.getResourceStream(resLoc);
         if (resourceStream == null) {
            return null;
         }

         BufferedImage bufferedImage2 = ImageIO.read(resourceStream);
         resourceStream.close();
         return bufferedImage2;
      } catch (IOException ioException) {
         return null;
      }
   }

   public static void bindTexture(ITextureObject tex) {
      tex.getGlTextureId();
      bindTextures(tex.getMultiTexID());
      if (GlStateManager.getActiveTextureUnit() == 33984) {
         int atlasSizeX2 = Shaders.atlasSizeX;
         int atlasSizeY2 = Shaders.atlasSizeY;
         if (tex instanceof TextureMap) {
            Shaders.atlasSizeX = ((TextureMap)tex).atlasWidth;
            Shaders.atlasSizeY = ((TextureMap)tex).atlasHeight;
         } else {
            Shaders.atlasSizeX = 0;
            Shaders.atlasSizeY = 0;
         }

         if (Shaders.atlasSizeX != atlasSizeX2 || Shaders.atlasSizeY != atlasSizeY2) {
            Shaders.uniform_atlasSize.setValue(Shaders.atlasSizeX, Shaders.atlasSizeY);
         }
      }
   }

   public static void setupTexture(MultiTexID multiTex, int[] src, int width, int height, boolean linear, boolean clamp) {
      int i = linear ? 9729 : 9728;
      int i2 = clamp ? 33071 : 10497;
      int i3 = width * height;
      IntBuffer intBuffer = getIntBuffer(i3);
      intBuffer.clear();
      intBuffer.put(src, 0, i3).position(0).limit(i3);
      GlStateManager.bindTexture(multiTex.base);
      GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, intBuffer);
      GL11.glTexParameteri(3553, 10241, i);
      GL11.glTexParameteri(3553, 10240, i);
      GL11.glTexParameteri(3553, 10242, i2);
      GL11.glTexParameteri(3553, 10243, i2);
      intBuffer.put(src, i3, i3).position(0).limit(i3);
      GlStateManager.bindTexture(multiTex.norm);
      GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, intBuffer);
      GL11.glTexParameteri(3553, 10241, i);
      GL11.glTexParameteri(3553, 10240, i);
      GL11.glTexParameteri(3553, 10242, i2);
      GL11.glTexParameteri(3553, 10243, i2);
      intBuffer.put(src, i3 * 2, i3).position(0).limit(i3);
      GlStateManager.bindTexture(multiTex.spec);
      GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, intBuffer);
      GL11.glTexParameteri(3553, 10241, i);
      GL11.glTexParameteri(3553, 10240, i);
      GL11.glTexParameteri(3553, 10242, i2);
      GL11.glTexParameteri(3553, 10243, i2);
      GlStateManager.bindTexture(multiTex.base);
   }

   public static void loadNSMap(IResourceManager manager, ResourceLocation location, int width, int height, int[] intArray) {
      if (Shaders.configNormalMap) {
         loadNSMap1(manager, getNSMapLocation(location, "n"), width, height, intArray, width * height, -8421377);
      }

      if (Shaders.configSpecularMap) {
         loadNSMap1(manager, getNSMapLocation(location, "s"), width, height, intArray, width * height * 2, 0);
      }
   }

   public static void bindTextures(MultiTexID multiTex) {
      if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984) {
         if (Shaders.configNormalMap) {
            GlStateManager.setActiveTexture(33986);
            GlStateManager.bindTexture(multiTex.norm);
         }

         if (Shaders.configSpecularMap) {
            GlStateManager.setActiveTexture(33987);
            GlStateManager.bindTexture(multiTex.spec);
         }

         GlStateManager.setActiveTexture(33984);
      }

      GlStateManager.bindTexture(multiTex.base);
   }

   public static void handleTextureMap(TextureMap textureMap, String text, int[][] iArrays, int i, int i2, int i3, int i4) {
      MultiTexID multiTex2 = textureMap.multiTex;
      TextureUtil.handleArray(iArrays, i, i2, i3, i4, false);
      if (Shaders.configNormalMap) {
         int[][] array = createArray(textureMap, text + "_n", i, i2, iArrays.length, -8421377);
         GlStateManager.bindTexture(multiTex2.norm);
         TextureUtil.handleArray(array, i, i2, i3, i4, false);
      }

      if (Shaders.configSpecularMap) {
         int[][] array2 = createArray(textureMap, text + "_s", i, i2, iArrays.length, 0);
         GlStateManager.bindTexture(multiTex2.spec);
         TextureUtil.handleArray(array2, i, i2, i3, i4, false);
      }

      GlStateManager.bindTexture(multiTex2.base);
   }
}
