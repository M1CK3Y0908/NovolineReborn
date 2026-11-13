package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.src.Config;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.optifine.SmartAnimations;
import net.optifine.shaders.Shaders;
import net.optifine.util.CounterInt;
import net.optifine.util.TextureUtils;

public class TextureAtlasSprite {
   protected List<int[][]> framesTextureData = Lists.newArrayList();
   private int indexInMap = -1;
   private static String locationNameClock = "builtin/clock";
   private float minV;
   public float baseU;
   protected boolean rotated;
   private float maxV;
   public int glSpriteTextureId = -1;
   public int sheetWidth;
   public TextureAtlasSprite spriteSingle = null;
   protected int originX;
   public boolean isSpriteSingle = false;
   public int mipmapLevels = 0;
   protected int originY;
   public TextureAtlasSprite spriteNormal = null;
   public TextureAtlasSprite spriteSpecular = null;
   public boolean isShadersSprite = false;
   protected int height;
   private float minU;
   protected int frameCounter;
   public boolean isEmissive = false;
   protected int tickCounter;
   protected int[][] interpolatedFrameData;
   public TextureAtlasSprite spriteEmissive = null;
   public float baseV;
   private static String locationNameCompass = "builtin/compass";
   private float maxU;
   private int animationIndex = -1;
   private boolean animationActive = false;
   protected int width;
   public int sheetHeight;
   private final String iconName;
   private AnimationMetadataSection animationMetadata;

   public void clearFramesTextureData() {
      this.framesTextureData.clear();
      if (this.spriteSingle != null) {
         this.spriteSingle.clearFramesTextureData();
      }
   }

   public boolean isAnimationActive() {
      return this.animationActive;
   }

   public AnimationMetadataSection getAnimationMetadata() {
      return this.animationMetadata;
   }

   @Override
   public String toString() {
      String iconName2 = this.iconName;
      int size2 = this.framesTextureData.size();
      float maxV2 = this.maxV;
      float minV2 = this.minV;
      float maxU2 = this.maxU;
      float minU2 = this.minU;
      return "TextureAtlasSprite{name='"
         + iconName2
         + "', frameCount="
         + size2
         + ", rotated="
         + this.rotated
         + ", x="
         + this.originX
         + ", y="
         + this.originY
         + ", height="
         + this.height
         + ", width="
         + this.width
         + ", u0="
         + minU2
         + ", u1="
         + maxU2
         + ", v0="
         + minV2
         + ", v1="
         + maxV2
         + "}";
   }

   private void loadShadersSprites() {
      if (Shaders.configNormalMap) {
         String text2 = this.iconName + "_n";
         ResourceLocation resourceLocation = new ResourceLocation(text2);
         resourceLocation = Config.getTextureMap().completeResourceLocation(resourceLocation);
         if (Config.hasResource(resourceLocation)) {
            this.spriteNormal = new TextureAtlasSprite(text2, 87996829185329L);
            this.spriteNormal.isShadersSprite = true;
            this.spriteNormal.copyFrom(this);
            this.spriteNormal.generateMipmaps(this.mipmapLevels);
         }
      }

      if (Shaders.configSpecularMap) {
         String text3 = this.iconName + "_s";
         ResourceLocation resourceLocation2 = new ResourceLocation(text3);
         resourceLocation2 = Config.getTextureMap().completeResourceLocation(resourceLocation2);
         if (Config.hasResource(resourceLocation2)) {
            this.spriteSpecular = new TextureAtlasSprite(text3, 87996829185329L);
            this.spriteSpecular.isShadersSprite = true;
            this.spriteSpecular.copyFrom(this);
            this.spriteSpecular.generateMipmaps(this.mipmapLevels);
         }
      }
   }

   private TextureAtlasSprite(String spriteName) {
      this.iconName = spriteName;
      this.isSpriteSingle = true;
   }

   public void setFramesTextureData(List<int[][]> newFramesTextureData) {
      this.framesTextureData = newFramesTextureData;
      if (this.spriteSingle != null) {
         this.spriteSingle.setFramesTextureData(newFramesTextureData);
      }
   }

   public float toSingleV(float arg1) {
      arg1 -= this.baseV;
      float f = (float)this.sheetHeight / this.height;
      return arg1 * f;
   }

   private void fixTransparentColor(int[] arg1) {
      if (arg1 != null) {
         long l = 0L;
         long l2 = 0L;
         long l3 = 0L;
         long l4 = 0L;

         for (int i = 0; i < arg1.length; i++) {
            int i2 = arg1[i];
            int i3 = i2 >> 24 & 0xFF;
            if (i3 >= 16) {
               int i4 = i2 >> 16 & 0xFF;
               int i5 = i2 >> 8 & 0xFF;
               int i6 = i2 & 0xFF;
               l += i4;
               l2 += i5;
               l3 += i6;
               l4++;
            }
         }

         if (l4 > 0L) {
            int i7 = (int)(l / l4);
            int i8 = (int)(l2 / l4);
            int i9 = (int)(l3 / l4);
            int index2 = i7 << 16 | i8 << 8 | i9;

            for (int i10 = 0; i10 < arg1.length; i10++) {
               int i11 = arg1[i10];
               if ((i11 >> 24 & 0xFF) <= 16) {
                  arg1[i10] = index2;
               }
            }
         }
      }
   }

   public void copyFrom(TextureAtlasSprite atlasSpirit) {
      this.originX = atlasSpirit.originX;
      this.originY = atlasSpirit.originY;
      this.width = atlasSpirit.width;
      this.height = atlasSpirit.height;
      this.rotated = atlasSpirit.rotated;
      this.minU = atlasSpirit.minU;
      this.maxU = atlasSpirit.maxU;
      this.minV = atlasSpirit.minV;
      this.maxV = atlasSpirit.maxV;
      if (atlasSpirit != Config.getTextureMap().getMissingSprite()) {
         this.indexInMap = atlasSpirit.indexInMap;
      }

      this.baseU = atlasSpirit.baseU;
      this.baseV = atlasSpirit.baseV;
      this.sheetWidth = atlasSpirit.sheetWidth;
      this.sheetHeight = atlasSpirit.sheetHeight;
      this.glSpriteTextureId = atlasSpirit.glSpriteTextureId;
      this.mipmapLevels = atlasSpirit.mipmapLevels;
      if (this.spriteSingle != null) {
         this.spriteSingle.initSprite(this.width, this.height, 0, 0, false);
      }

      this.animationIndex = atlasSpirit.animationIndex;
   }

   protected static TextureAtlasSprite makeAtlasSprite(ResourceLocation spriteResourceLocation) {
      String text = spriteResourceLocation.toString();
      return locationNameClock.equals(text)
         ? new TextureClock(text)
         : (locationNameCompass.equals(text) ? new TextureCompass(text) : new TextureAtlasSprite(text, 87996829185329L));
   }

   public float getInterpolatedV(double v) {
      float f = this.maxV - this.minV;
      return this.minV + f * ((float)v / 16.0F);
   }

   public void deleteSpriteTexture() {
      if (this.glSpriteTextureId >= 0) {
         TextureUtil.deleteTexture(this.glSpriteTextureId);
         this.glSpriteTextureId = -1;
      }
   }

   public int getIconWidth() {
      return this.width;
   }

   public int getAnimationIndex() {
      return this.animationIndex;
   }

   public void setAnimationMetadata(AnimationMetadataSection arg1) {
      this.animationMetadata = arg1;
   }

   public int getOriginX() {
      return this.originX;
   }

   public float getMaxV() {
      return this.maxV;
   }

   public float getMaxU() {
      return this.maxU;
   }

   public void setIconWidth(int newWidth) {
      this.width = newWidth;
      if (this.spriteSingle != null) {
         this.spriteSingle.setIconWidth(this.width);
      }
   }

   public float getMinU() {
      return this.minU;
   }

   public int getIndexInMap() {
      return this.indexInMap;
   }

   public void updateIndexInMap(CounterInt arg1) {
      if (this.indexInMap < 0) {
         this.indexInMap = arg1.nextValue();
      }
   }

   public List<int[][]> getFramesTextureData() {
      ArrayList arrayList = new ArrayList();
      arrayList.addAll(this.framesTextureData);
      return arrayList;
   }

   public int getIconHeight() {
      return this.height;
   }

   public void loadSprite(BufferedImage[] images, AnimationMetadataSection meta) throws IOException {
      this.resetSprite();
      int width2 = images[0].getWidth();
      int height2 = images[0].getHeight();
      this.width = width2;
      this.height = height2;
      if (this.spriteSingle != null) {
         this.spriteSingle.width = this.width;
         this.spriteSingle.height = this.height;
      }

      int[][] intArray = new int[images.length][];

      for (int nestedIndex = 0; nestedIndex < images.length; nestedIndex++) {
         BufferedImage bufferedImage = images[nestedIndex];
         if (bufferedImage != null) {
            if (this.width >> nestedIndex != bufferedImage.getWidth()) {
               bufferedImage = TextureUtils.scaleImage(bufferedImage, this.width >> nestedIndex);
            }

            if (nestedIndex > 0 && (bufferedImage.getWidth() != width2 >> nestedIndex || bufferedImage.getHeight() != height2 >> nestedIndex)) {
               throw new RuntimeException(
                  String.format(
                     "Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d",
                     nestedIndex,
                     bufferedImage.getWidth(),
                     bufferedImage.getHeight(),
                     width2 >> nestedIndex,
                     height2 >> nestedIndex
                  )
               );
            }

            intArray[nestedIndex] = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
            bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), intArray[nestedIndex], 0, bufferedImage.getWidth());
         }
      }

      if (meta == null) {
         if (height2 != width2) {
            throw new RuntimeException("broken aspect ratio and not an animation");
         }

         this.framesTextureData.add(intArray);
      } else {
         int i = height2 / width2;
         int i2 = width2;
         int i3 = width2;
         this.height = this.width;
         if (meta.getFrameCount() > 0) {
            for (int i4 : meta.getFrameIndexSet()) {
               if (i4 >= i) {
                  throw new RuntimeException("invalid frameindex " + i4);
               }

               this.allocateFrameTextureData(i4);
               this.framesTextureData.set(i4, getFrameTextureData(intArray, i2, i3, i4));
            }

            this.animationMetadata = meta;
         } else {
            List<AnimationFrame> list = Lists.newArrayList();

            for (int index = 0; index < i; index++) {
               this.framesTextureData.add(getFrameTextureData(intArray, i2, i3, index));
               list.add(new AnimationFrame(index, -1));
            }

            this.animationMetadata = new AnimationMetadataSection(list, this.width, this.height, meta.getFrameTime(), meta.isInterpolate());
         }
      }

      if (!this.isShadersSprite) {
         if (Config.isShaders()) {
            this.loadShadersSprites();
         }

         for (int nestedIndex2 = 0; nestedIndex2 < this.framesTextureData.size(); nestedIndex2++) {
            int[][] iArrays = this.framesTextureData.get(nestedIndex2);
            if (iArrays != null && !this.iconName.startsWith("minecraft:blocks/leaves_")) {
               for (int index2 = 0; index2 < iArrays.length; index2++) {
                  int[] iArray = iArrays[index2];
                  this.fixTransparentColor(iArray);
               }
            }
         }

         if (this.spriteSingle != null) {
            this.spriteSingle.loadSprite(images, meta);
         }
      }
   }

   public void setIndexInMap(int arg1) {
      this.indexInMap = arg1;
   }

   private void allocateFrameTextureData(int index) {
      if (this.framesTextureData.size() <= index) {
         for (int index2 = this.framesTextureData.size(); index2 <= index; index2++) {
            this.framesTextureData.add((int[][])null);
         }
      }

      if (this.spriteSingle != null) {
         this.spriteSingle.allocateFrameTextureData(index);
      }
   }

   public void setIconHeight(int newHeight) {
      this.height = newHeight;
      if (this.spriteSingle != null) {
         this.spriteSingle.setIconHeight(this.height);
      }
   }

   public float getMinV() {
      return this.minV;
   }

   public void initSprite(int inX, int inY, int originInX, int originInY, boolean rotatedIn) {
      this.originX = originInX;
      this.originY = originInY;
      this.rotated = rotatedIn;
      float f = (float)(0.01F / (double)inX);
      float f2 = (float)(0.01F / (double)inY);
      this.minU = originInX / (float)inX + f;
      this.maxU = (originInX + this.width) / (float)inX - f;
      this.minV = (float)originInY / inY + f2;
      this.maxV = (float)(originInY + this.height) / inY - f2;
      this.baseU = Math.min(this.minU, this.maxU);
      this.baseV = Math.min(this.minV, this.maxV);
      if (this.spriteSingle != null) {
         this.spriteSingle.initSprite(this.width, this.height, 0, 0, false);
      }

      if (this.spriteNormal != null) {
         this.spriteNormal.copyFrom(this);
      }

      if (this.spriteSpecular != null) {
         this.spriteSpecular.copyFrom(this);
      }
   }

   public boolean load(IResourceManager arg1, ResourceLocation arg2) {
      return true;
   }

   public static void setLocationNameClock(String clockName) {
      locationNameClock = clockName;
   }

   private void updateAnimationInterpolated() {
      double d2 = 1.0 - (double)this.tickCounter / this.animationMetadata.getFrameTimeSingle(this.frameCounter);
      int frameIndex = this.animationMetadata.getFrameIndex(this.frameCounter);
      int count = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
      int frameIndex2 = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % count);
      if (frameIndex != frameIndex2 && frameIndex2 >= 0 && frameIndex2 < this.framesTextureData.size()) {
         int[][] iArrays = this.framesTextureData.get(frameIndex);
         int[][] iArrays2 = this.framesTextureData.get(frameIndex2);
         if (this.interpolatedFrameData == null || this.interpolatedFrameData.length != iArrays.length) {
            this.interpolatedFrameData = new int[iArrays.length][];
         }

         for (int outerIndex = 0; outerIndex < iArrays.length; outerIndex++) {
            if (this.interpolatedFrameData[outerIndex] == null) {
               this.interpolatedFrameData[outerIndex] = new int[iArrays[outerIndex].length];
            }

            if (outerIndex < iArrays2.length && iArrays2[outerIndex].length == iArrays[outerIndex].length) {
               for (int index = 0; index < iArrays[outerIndex].length; index++) {
                  int i = iArrays[outerIndex][index];
                  int i2 = iArrays2[outerIndex][index];
                  int i3 = (int)(((i & 0xFF0000) >> 16) * d2 + ((i2 & 0xFF0000) >> 16) * (1.0 - d2));
                  int i4 = (int)(((i & 0xFF00) >> 8) * d2 + ((i2 & 0xFF00) >> 8) * (1.0 - d2));
                  int i5 = (int)((i & 0xFF) * d2 + (i2 & 0xFF) * (1.0 - d2));
                  this.interpolatedFrameData[outerIndex][index] = i & 0xFF000000 | i3 << 16 | i4 << 8 | i5;
               }
            }
         }

         TextureUtil.handleArray(this.interpolatedFrameData, this.width, this.height, this.originX, this.originY, false);
      }
   }

   public TextureAtlasSprite(String text2, long l) {
      this.iconName = text2;
      if (Config.isMultiTexture()) {
         this.spriteSingle = new TextureAtlasSprite(this.getIconName() + ".spriteSingle");
      }
   }

   public boolean hasCustomLoader(IResourceManager arg1, ResourceLocation arg2) {
      return false;
   }

   public void generateMipmaps(int level) {
      ArrayList arrayList = Lists.newArrayList();

      for (int i = 0; i < this.framesTextureData.size(); i++) {
         final int[][] iArrays = this.framesTextureData.get(i);
         if (iArrays != null) {
            try {
               arrayList.add(TextureUtil.generateMipmapData(level, this.width, iArrays));
            } catch (Throwable throwable) {
               CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Generating mipmaps for frame");
               CrashReportCategory crashReportCategory = crashReport.makeCategory("Frame being iterated");
               crashReportCategory.addCrashSection("Frame index", i);
               crashReportCategory.addCrashSectionCallable("Frame sizes", new Callable<String>() {
                  public String call() throws Exception {
                     StringBuilder stringBuilder = new StringBuilder();

                     for (int[] iArray : iArrays) {
                        if (stringBuilder.length() > 0) {
                           stringBuilder.append(", ");
                        }

                        stringBuilder.append(iArray == null ? "null" : iArray.length);
                     }

                     return stringBuilder.toString();
                  }
               });
               throw new ReportedException(crashReport);
            }
         }
      }

      this.setFramesTextureData(arrayList);
      if (this.spriteSingle != null) {
         this.spriteSingle.generateMipmaps(level);
      }
   }

   public float getInterpolatedU(double u) {
      float f = this.maxU - this.minU;
      return this.minU + f * (float)u / 16.0F;
   }

   public String getIconName() {
      return this.iconName;
   }

   public static void setLocationNameCompass(String compassName) {
      locationNameCompass = compassName;
   }

   public double getSpriteV16(float arg1) {
      float f = this.maxV - this.minV;
      return (arg1 - this.minV) / f * 16.0F;
   }

   private void resetSprite() {
      this.animationMetadata = null;
      this.setFramesTextureData(Lists.newArrayList());
      this.frameCounter = 0;
      this.tickCounter = 0;
      if (this.spriteSingle != null) {
         this.spriteSingle.resetSprite();
      }
   }

   public void bindSpriteTexture() {
      if (this.glSpriteTextureId < 0) {
         this.glSpriteTextureId = TextureUtil.glGenTextures();
         TextureUtil.allocateTextureImpl(this.glSpriteTextureId, this.mipmapLevels, this.width, this.height);
         TextureUtils.applyAnisotropicLevel();
      }

      TextureUtils.bindTexture(this.glSpriteTextureId);
   }

   public double getSpriteU16(float arg1) {
      float f = this.maxU - this.minU;
      return (arg1 - this.minU) / f * 16.0F;
   }

   public int[][] getFrameTextureData(int index) {
      return this.framesTextureData.get(index);
   }

   private static int[][] getFrameTextureData(int[][] data, int rows, int columns, int i) {
      int[][] intArray = new int[data.length][];

      for (int index = 0; index < data.length; index++) {
         int[] iArray = data[index];
         if (iArray != null) {
            intArray[index] = new int[(rows >> index) * (columns >> index)];
            System.arraycopy(iArray, i * intArray[index].length, intArray[index], 0, intArray[index].length);
         }
      }

      return intArray;
   }

   public float toSingleU(float arg1) {
      arg1 -= this.baseU;
      float f = (float)this.sheetWidth / this.width;
      return arg1 * f;
   }

   public int getOriginY() {
      return this.originY;
   }

   public void updateAnimation() {
      if (this.animationMetadata != null) {
         this.animationActive = SmartAnimations.isActive() ? SmartAnimations.isSpriteRendered(this.animationIndex) : true;
         this.tickCounter++;
         if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
            int frameIndex = this.animationMetadata.getFrameIndex(this.frameCounter);
            int i = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
            this.frameCounter = (this.frameCounter + 1) % i;
            this.tickCounter = 0;
            int frameIndex2 = this.animationMetadata.getFrameIndex(this.frameCounter);
            boolean isSpriteSingle2 = this.isSpriteSingle;
            if (!this.animationActive) {
               return;
            }

            if (frameIndex != frameIndex2 && frameIndex2 >= 0 && frameIndex2 < this.framesTextureData.size()) {
               TextureUtil.handleArray(this.framesTextureData.get(frameIndex2), this.width, this.height, this.originX, this.originY, isSpriteSingle2);
            }
         } else if (this.animationMetadata.isInterpolate()) {
            if (!this.animationActive) {
               return;
            }

            this.updateAnimationInterpolated();
         }
      }
   }

   public boolean hasAnimationMetadata() {
      return this.animationMetadata != null;
   }

   public int getFrameCount() {
      return this.framesTextureData.size();
   }

   public void setAnimationIndex(int arg1) {
      this.animationIndex = arg1;
      if (this.spriteNormal != null) {
         this.spriteNormal.setAnimationIndex(arg1);
      }

      if (this.spriteSpecular != null) {
         this.spriteSpecular.setAnimationIndex(arg1);
      }
   }
}
