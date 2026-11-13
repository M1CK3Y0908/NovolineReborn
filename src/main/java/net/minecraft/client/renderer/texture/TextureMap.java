package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.optifine.BetterGrass;
import net.optifine.ConnectedTextures;
import net.optifine.CustomItems;
import net.optifine.EmissiveTextures;
import net.optifine.SmartAnimations;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.ShadersTex;
import net.optifine.util.CounterInt;
import net.optifine.util.TextureUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureMap extends AbstractTexture implements ITickableTextureObject {
   private boolean skipFirst = false;
   private int mipmapLevels;
   private TextureAtlasSprite[] iconGrid = null;
   private int iconGridSize = -1;
   private int iconGridCountX = -1;
   private int countAnimationsActive;
   private int frameCountAnimations;
   private int iconGridCountY = -1;
   private double iconGridSizeU = -1.0;
   private double iconGridSizeV = -1.0;
   private CounterInt counterIndexInMap = new CounterInt(0);
   public int atlasWidth = 0;
   public int atlasHeight = 0;
   private final List<TextureAtlasSprite> listAnimatedSprites = Lists.newArrayList();
   private static final boolean ENABLE_SKIP = Boolean.parseBoolean(System.getProperty("fml.skipFirstTextureLoad", "true"));
   private final Map<String, TextureAtlasSprite> mapRegisteredSprites = Maps.newHashMap();
   private final Map<String, TextureAtlasSprite> mapUploadedSprites = Maps.newHashMap();
   private static final Logger logger = LogManager.getLogger();
   private final TextureAtlasSprite missingImage = new TextureAtlasSprite("missingno", 87996829185329L);
   public static final ResourceLocation LOCATION_MISSING_TEXTURE = new ResourceLocation("missingno");
   public static final ResourceLocation locationBlocksTexture = new ResourceLocation("textures/atlas/blocks.png");
   private final String basePath;
   private final IIconCreator iconCreator;

   private int getMinSpriteSize() {
      int i = 1 << this.mipmapLevels;
      if (i < 8) {
         i = 8;
      }

      return i;
   }

   public TextureAtlasSprite getIconByUV(double u, double v) {
      if (this.iconGrid == null) {
         return null;
      }

      int i = (int)(u / this.iconGridSizeU);
      int i2 = (int)(v / this.iconGridSizeV) * this.iconGridCountX + i;
      return i2 >= 0 && i2 <= this.iconGrid.length ? this.iconGrid[i2] : null;
   }

   public TextureAtlasSprite getAtlasSprite(String iconName) {
      TextureAtlasSprite textureAtlasSprite = this.mapUploadedSprites.get(iconName);
      if (textureAtlasSprite == null) {
         textureAtlasSprite = this.missingImage;
      }

      return textureAtlasSprite;
   }

   public ResourceLocation completeResourceLocation(ResourceLocation location, int mipmapLevel) {
      return this.isAbsoluteLocation(location)
         ? new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".png")
         : (
            mipmapLevel == 0
               ? new ResourceLocation(location.getResourceDomain(), String.format("%s/%s%s", this.basePath, location.getResourcePath(), ".png"))
               : new ResourceLocation(
                  location.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", this.basePath, location.getResourcePath(), mipmapLevel, ".png")
               )
         );
   }

   public void setMipmapLevels(int mipmapLevelsIn) {
      this.mipmapLevels = mipmapLevelsIn;
   }

   public TextureAtlasSprite getRegisteredSprite(ResourceLocation location) {
      return this.mapRegisteredSprites.get(location.toString());
   }

   private void initMissingImage() {
      int minSpriteSize = this.getMinSpriteSize();
      int[] intArray = this.getMissingImageData(minSpriteSize);
      this.missingImage.setIconWidth(minSpriteSize);
      this.missingImage.setIconHeight(minSpriteSize);
      int[][] intArray1 = new int[this.mipmapLevels + 1][];
      intArray1[0] = intArray;
      this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][][]{intArray1}));
      this.missingImage.setIndexInMap(this.counterIndexInMap.nextValue());
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      if (this.iconCreator != null) {
         this.loadSprites(resourceManager, this.iconCreator);
      }
   }

   private boolean isTerrainAnimationActive(TextureAtlasSprite sprite) {
      return sprite == TextureUtils.iconWaterStill || sprite == TextureUtils.iconWaterFlow
         ? Config.isAnimatedWater()
         : (
            sprite == TextureUtils.iconLavaStill || sprite == TextureUtils.iconLavaFlow
               ? Config.isAnimatedLava()
               : (
                  sprite == TextureUtils.iconFireLayer0 || sprite == TextureUtils.iconFireLayer1
                     ? Config.isAnimatedFire()
                     : (
                        sprite == TextureUtils.iconPortal
                           ? Config.isAnimatedPortal()
                           : (sprite != TextureUtils.iconClock && sprite != TextureUtils.iconCompass ? Config.isAnimatedTerrain() : true)
                     )
               )
         );
   }

   private int[] getMissingImageData(int size) {
      BufferedImage bufferedImage = new BufferedImage(16, 16, 2);
      bufferedImage.setRGB(0, 0, 16, 16, TextureUtil.missingTextureData, 0, 16);
      BufferedImage bufferedImage2 = TextureUtils.scaleImage(bufferedImage, size);
      int[] intArray = new int[size * size];
      bufferedImage2.getRGB(0, 0, size, size, intArray, 0, size);
      return intArray;
   }

   public int getCountAnimations() {
      return this.listAnimatedSprites.size();
   }

   public TextureMap(String basePath, IIconCreator iconCreatorIn) {
      this(basePath, iconCreatorIn, 73543023020475L, false);
   }

   public int getCountRegisteredSprites() {
      return this.counterIndexInMap.getValue();
   }

   private int getInt(Map map2, IResourceManager iResourceManager) {
      HashMap hashMap = new HashMap();

      for (Entry entry : (Iterable<Map.Entry>) map2.entrySet()) {
         TextureAtlasSprite textureAtlasSprite2 = (TextureAtlasSprite)entry.getValue();
         ResourceLocation resourceLocation = new ResourceLocation(textureAtlasSprite2.getIconName());
         ResourceLocation resourceLocation2 = this.completeResourceLocation(resourceLocation);
         if (!textureAtlasSprite2.hasCustomLoader(iResourceManager, resourceLocation)) {
            try {
               IResource resource = iResourceManager.getResource(resourceLocation2);
               if (resource != null) {
                  InputStream inputStream = resource.getInputStream();
                  if (inputStream != null) {
                     Dimension imageSize = TextureUtils.getImageSize(inputStream, "png");
                     inputStream.close();
                     if (imageSize != null) {
                        int width2 = imageSize.width;
                        int i = MathHelper.roundUpToPowerOfTwo(width2);
                        if (!hashMap.containsKey(i)) {
                           hashMap.put(i, 1);
                        } else {
                           int i2 = (Integer)hashMap.get(i);
                           hashMap.put(i, i2 + 1);
                        }
                     }
                  }
               }
            } catch (Exception exception) {
            }
         }
      }

      int i3 = 0;
      Set set = hashMap.keySet();
      TreeSet treeSet = new TreeSet(set);

      for (int i4 : (Iterable<Integer>) treeSet) {
         int i5 = (Integer)hashMap.get(i4);
         i3 += i5;
      }

      int i6 = 16;
      int i7 = 0;
      int i8 = i3 * 20 / 100;

      for (int i9 : (Iterable<Integer>) treeSet) {
         int i10 = (Integer)hashMap.get(i9);
         i7 += i10;
         if (i9 > i6) {
            i6 = i9;
         }

         if (i7 > i8) {
            return i6;
         }
      }

      return i6;
   }

   public int getMipmapLevels() {
      return this.mipmapLevels;
   }

   private void checkEmissive(ResourceLocation location, TextureAtlasSprite sprite) {
      String suffixEmissive = EmissiveTextures.getSuffixEmissive();
      if (suffixEmissive != null && !location.getResourcePath().endsWith(suffixEmissive)) {
         ResourceLocation resourceLocation = new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + suffixEmissive);
         if (Config.hasResource(this.completeResourceLocation(resourceLocation))) {
            TextureAtlasSprite textureAtlasSprite2 = this.registerSprite(resourceLocation);
            textureAtlasSprite2.isEmissive = true;
            sprite.spriteEmissive = textureAtlasSprite2;
         }
      }
   }

   private int detectMaxMipmapLevel(Map sprites, IResourceManager resourceManager) {
      int intValue = this.getInt(sprites, resourceManager);
      if (intValue < 16) {
         intValue = 16;
      }

      intValue = MathHelper.roundUpToPowerOfTwo(intValue);
      if (intValue > 16) {
         Config.log("Sprite size: " + intValue);
      }

      int i = MathHelper.calculateLogBaseTwo(intValue);
      if (i < 4) {
         i = 4;
      }

      return i;
   }

   private boolean isAbsoluteLocation(ResourceLocation location) {
      String resourcePath = location.getResourcePath();
      return this.isAbsoluteLocationPath(resourcePath);
   }

   public ResourceLocation completeResourceLocation(ResourceLocation location) {
      return this.completeResourceLocation(location, 0);
   }

   public TextureMap(String basePath) {
      this(basePath, (IIconCreator)null);
   }

   private boolean isAbsoluteLocationPath(String path) {
      String text = path.toLowerCase();
      return text.startsWith("mcpatcher/") || text.startsWith("optifine/");
   }

   public void loadTextureAtlas(IResourceManager resourceManager) {
      Config.dbg("Multitexture: " + Config.isMultiTexture());
      if (Config.isMultiTexture()) {
         for (TextureAtlasSprite textureAtlasSprite2 : this.mapUploadedSprites.values()) {
            textureAtlasSprite2.deleteSpriteTexture();
         }
      }

      ConnectedTextures.updateIcons(this);
      CustomItems.updateIcons(this);
      BetterGrass.updateIcons(this);
      int glMaximumTextureSize = TextureUtils.getGLMaximumTextureSize();
      Stitcher stitcher = new Stitcher(glMaximumTextureSize, glMaximumTextureSize, this.mipmapLevels);
      this.mapUploadedSprites.clear();
      this.listAnimatedSprites.clear();
      int i = Integer.MAX_VALUE;
      Reflector.handleReflectorMethod(Reflector.reflectorMethod66, this);
      int minSpriteSize = this.getMinSpriteSize();
      this.iconGridSize = minSpriteSize;
      int i2 = 1 << this.mipmapLevels;
      int i3 = 0;
      int i4 = 0;

      for (Entry entry : this.mapRegisteredSprites.entrySet()) {
         if (!this.skipFirst) {
            TextureAtlasSprite textureAtlasSprite3 = (TextureAtlasSprite)entry.getValue();
            ResourceLocation resourceLocation = new ResourceLocation(textureAtlasSprite3.getIconName());
            ResourceLocation resourceLocation2 = this.completeResourceLocation(resourceLocation, 0);
            textureAtlasSprite3.updateIndexInMap(this.counterIndexInMap);
            if (textureAtlasSprite3.hasCustomLoader(resourceManager, resourceLocation)) {
               if (!textureAtlasSprite3.load(resourceManager, resourceLocation)) {
                  i = Math.min(i, Math.min(textureAtlasSprite3.getIconWidth(), textureAtlasSprite3.getIconHeight()));
                  stitcher.addSprite(textureAtlasSprite3);
                  Config.detail("Custom loader (skipped): " + textureAtlasSprite3);
                  i4 += 1;
               }

               Config.detail("Custom loader: " + textureAtlasSprite3);
               i3 += 1;
            } else {
               try {
                  IResource resource = resourceManager.getResource(resourceLocation2);
                  BufferedImage[] bufferedImages = new BufferedImage[1 + this.mipmapLevels];
                  bufferedImages[0] = TextureUtil.readBufferedImage(resource.getInputStream());
                  int width2 = bufferedImages[0].getWidth();
                  int height = bufferedImages[0].getHeight();
                  if (width2 < 1 || height < 1) {
                     Config.warn("Invalid sprite size: " + textureAtlasSprite3);
                     continue;
                  }

                  if (width2 < minSpriteSize || this.mipmapLevels > 0) {
                     int i5 = this.mipmapLevels > 0 ? TextureUtils.scaleToGrid(width2, minSpriteSize) : TextureUtils.scaleToMin(width2, minSpriteSize);
                     if (i5 != width2) {
                        if (!TextureUtils.isPowerOfTwo(width2)) {
                           Config.log("Scaled non power of 2: " + textureAtlasSprite3.getIconName() + ", " + width2 + " -> " + i5);
                        } else {
                           Config.log("Scaled too small texture: " + textureAtlasSprite3.getIconName() + ", " + width2 + " -> " + i5);
                        }

                        int i6 = height * i5 / width2;
                        bufferedImages[0] = TextureUtils.scaleImage(bufferedImages[0], i5);
                     }
                  }

                  TextureMetadataSection metadata2 = resource.getMetadata("texture");
                  if (metadata2 != null) {
                     List listMipmaps = metadata2.getListMipmaps();
                     if (!listMipmaps.isEmpty()) {
                        int width3 = bufferedImages[0].getWidth();
                        int height2 = bufferedImages[0].getHeight();
                        if (MathHelper.roundUpToPowerOfTwo(width3) != width3 || MathHelper.roundUpToPowerOfTwo(height2) != height2) {
                           throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                        }
                     }

                     for (int i7 : (Iterable<Integer>) listMipmaps) {
                        if (i7 > 0 && i7 < bufferedImages.length - 1 && bufferedImages[i7] == null) {
                           ResourceLocation resourceLocation3 = this.completeResourceLocation(resourceLocation, i7);

                           try {
                              bufferedImages[i7] = TextureUtil.readBufferedImage(resourceManager.getResource(resourceLocation3).getInputStream());
                           } catch (IOException ioException) {
                              logger.error("Unable to load miplevel {} from: {}", i7, resourceLocation3, ioException);
                           }
                        }
                     }
                  }

                  AnimationMetadataSection metadata3 = resource.getMetadata("animation");
                  textureAtlasSprite3.loadSprite(bufferedImages, metadata3);
               } catch (RuntimeException runtimeException) {
                  logger.error("Unable to parse metadata from " + resourceLocation2, runtimeException);
                  ReflectorForge.handleResourceLocation2(resourceLocation2, runtimeException.getMessage());
                  continue;
               } catch (IOException ioException2) {
                  logger.error("Using missing texture, unable to load " + resourceLocation2 + ", " + ioException2.getClass().getName());
                  ReflectorForge.handleResourceLocation(resourceLocation2);
                  continue;
               }

               i = Math.min(i, Math.min(textureAtlasSprite3.getIconWidth(), textureAtlasSprite3.getIconHeight()));
               int i8 = Math.min(Integer.lowestOneBit(textureAtlasSprite3.getIconWidth()), Integer.lowestOneBit(textureAtlasSprite3.getIconHeight()));
               if (i8 < i2) {
                  logger.warn(
                     "Texture {} with size {}x{} limits mip level from {} to {}",
                     resourceLocation2,
                     textureAtlasSprite3.getIconWidth(),
                     textureAtlasSprite3.getIconHeight(),
                     MathHelper.calculateLogBaseTwo(i2),
                     MathHelper.calculateLogBaseTwo(i8)
                  );
                  i2 = i8;
               }

               stitcher.addSprite(textureAtlasSprite3);
            }
            continue;
         }
         break;
      }

      if (i3 > 0) {
         Config.dbg("Custom loader sprites: " + i3);
      }

      if (i4 > 0) {
         Config.dbg("Custom loader sprites (skipped): " + i4);
      }

      int i9 = Math.min(i, i2);
      int i10 = MathHelper.calculateLogBaseTwo(i9);
      if (i10 < 0) {
         i10 = 0;
      }

      if (i10 < this.mipmapLevels) {
         logger.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.basePath, this.mipmapLevels, i10, i9);
         this.mipmapLevels = i10;
      }

      for (final TextureAtlasSprite textureAtlasSprite4 : this.mapRegisteredSprites.values()) {
         if (this.skipFirst) {
            break;
         }

         try {
            textureAtlasSprite4.generateMipmaps(this.mipmapLevels);
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Applying mipmap");
            CrashReportCategory crashReportCategory = crashReport.makeCategory("Sprite being mipmapped");
            crashReportCategory.addCrashSectionCallable("Sprite name", new Callable<String>() {
               public String call() throws Exception {
                  return textureAtlasSprite4.getIconName();
               }
            });
            crashReportCategory.addCrashSectionCallable("Sprite size", new Callable<String>() {
               public String call() throws Exception {
                  return textureAtlasSprite4.getIconWidth() + " x " + textureAtlasSprite4.getIconHeight();
               }
            });
            crashReportCategory.addCrashSectionCallable("Sprite frames", new Callable<String>() {
               public String call() throws Exception {
                  return textureAtlasSprite4.getFrameCount() + " frames";
               }
            });
            crashReportCategory.addCrashSection("Mipmap levels", this.mipmapLevels);
            throw new ReportedException(crashReport);
         }
      }

      this.missingImage.generateMipmaps(this.mipmapLevels);
      stitcher.addSprite(this.missingImage);
      this.skipFirst = false;
      stitcher.doStitch();
      logger.info("Created: {}x{} {}-atlas", stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), this.basePath);
      if (Config.isShaders()) {
         ShadersTex.allocateTextureMap(this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), stitcher, this);
      } else {
         TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight());
      }

      HashMap hashMap = Maps.newHashMap(this.mapRegisteredSprites);

      for (TextureAtlasSprite textureAtlasSprite5 : stitcher.getStichSlots()) {
         String iconName2 = textureAtlasSprite5.getIconName();
         hashMap.remove(iconName2);
         this.mapUploadedSprites.put(iconName2, textureAtlasSprite5);

         try {
            if (Config.isShaders()) {
               ShadersTex.handleTextureMap(
                  this,
                  textureAtlasSprite5.getIconName(),
                  textureAtlasSprite5.getFrameTextureData(0),
                  textureAtlasSprite5.getIconWidth(),
                  textureAtlasSprite5.getIconHeight(),
                  textureAtlasSprite5.getOriginX(),
                  textureAtlasSprite5.getOriginY()
               );
            } else {
               TextureUtil.handleArray(
                  textureAtlasSprite5.getFrameTextureData(0), textureAtlasSprite5.getIconWidth(), textureAtlasSprite5.getIconHeight(), textureAtlasSprite5.getOriginX(), textureAtlasSprite5.getOriginY(), false
               );
            }
         } catch (Throwable throwable2) {
            CrashReport crashReport2 = CrashReport.makeCrashReport(throwable2, "Stitching texture atlas");
            CrashReportCategory crashReportCategory2 = crashReport2.makeCategory("Texture being stitched together");
            crashReportCategory2.addCrashSection("Atlas path", this.basePath);
            crashReportCategory2.addCrashSection("Sprite", textureAtlasSprite5);
            throw new ReportedException(crashReport2);
         }

         if (textureAtlasSprite5.hasAnimationMetadata()) {
            textureAtlasSprite5.setAnimationIndex(this.listAnimatedSprites.size());
            this.listAnimatedSprites.add(textureAtlasSprite5);
         }
      }

      for (TextureAtlasSprite textureAtlasSprite6 : (Iterable<TextureAtlasSprite>) hashMap.values()) {
         textureAtlasSprite6.copyFrom(this.missingImage);
      }

      Config.log("Animated sprites: " + this.listAnimatedSprites.size());
      if (Config.isMultiTexture()) {
         int currentWidth = stitcher.getCurrentWidth();
         int currentHeight = stitcher.getCurrentHeight();

         for (TextureAtlasSprite textureAtlasSprite7 : stitcher.getStichSlots()) {
            textureAtlasSprite7.sheetWidth = currentWidth;
            textureAtlasSprite7.sheetHeight = currentHeight;
            textureAtlasSprite7.mipmapLevels = this.mipmapLevels;
            TextureAtlasSprite spriteSingle2 = textureAtlasSprite7.spriteSingle;
            if (spriteSingle2 != null) {
               if (spriteSingle2.getIconWidth() <= 0) {
                  spriteSingle2.setIconWidth(textureAtlasSprite7.getIconWidth());
                  spriteSingle2.setIconHeight(textureAtlasSprite7.getIconHeight());
                  spriteSingle2.initSprite(textureAtlasSprite7.getIconWidth(), textureAtlasSprite7.getIconHeight(), 0, 0, false);
                  spriteSingle2.clearFramesTextureData();
                  List framesTextureData = textureAtlasSprite7.getFramesTextureData();
                  spriteSingle2.setFramesTextureData(framesTextureData);
                  spriteSingle2.setAnimationMetadata(textureAtlasSprite7.getAnimationMetadata());
               }

               spriteSingle2.sheetWidth = currentWidth;
               spriteSingle2.sheetHeight = currentHeight;
               spriteSingle2.mipmapLevels = this.mipmapLevels;
               spriteSingle2.setAnimationIndex(textureAtlasSprite7.getAnimationIndex());
               textureAtlasSprite7.bindSpriteTexture();

               try {
                  TextureUtil.handleArray(
                     spriteSingle2.getFrameTextureData(0), spriteSingle2.getIconWidth(), spriteSingle2.getIconHeight(), spriteSingle2.getOriginX(), spriteSingle2.getOriginY(), true
                  );
               } catch (Exception exception) {
                  Config.dbg("Error uploading sprite single: " + spriteSingle2 + ", parent: " + textureAtlasSprite7);
                  exception.printStackTrace();
               }
            }
         }

         Config.getMinecraft().getTextureManager().bindTexture(locationBlocksTexture);
      }

      Reflector.handleReflectorMethod(Reflector.reflectorMethod67, this);
      this.updateIconGrid(stitcher.getCurrentWidth(), stitcher.getCurrentHeight());
      if (Config.equals(System.getProperty("saveTextureMap"), "true")) {
         Config.dbg("Exporting texture map: " + this.basePath);
         TextureUtils.saveGlTexture(
            "debug/" + this.basePath.replaceAll("/", "_"), this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight()
         );
      }
   }

   @Override
   public void tick() {
      this.updateAnimations();
   }

   public int getCountAnimationsActive() {
      return this.countAnimationsActive;
   }

   public TextureAtlasSprite getMissingSprite() {
      return this.missingImage;
   }

   public String getBasePath() {
      return this.basePath;
   }

   public void loadSprites(IResourceManager resourceManager, IIconCreator iconCreatorIn) {
      this.mapRegisteredSprites.clear();
      this.counterIndexInMap.reset();
      iconCreatorIn.registerSprites(this);
      if (this.mipmapLevels >= 4) {
         this.mipmapLevels = this.detectMaxMipmapLevel(this.mapRegisteredSprites, resourceManager);
         Config.log("Mipmap levels: " + this.mipmapLevels);
      }

      this.initMissingImage();
      this.deleteGlTexture();
      this.loadTextureAtlas(resourceManager);
   }

   public TextureAtlasSprite getTextureExtry(String iconName) {
      return this.mapRegisteredSprites.get(iconName);
   }

   public void updateAnimations() {
      boolean flag = false;
      boolean flag2 = false;
      TextureUtil.bindTexture(this.getGlTextureId());
      int i = 0;

      for (TextureAtlasSprite textureAtlasSprite2 : this.listAnimatedSprites) {
         if (this.isTerrainAnimationActive(textureAtlasSprite2)) {
            textureAtlasSprite2.updateAnimation();
            if (textureAtlasSprite2.isAnimationActive()) {
               i++;
            }

            if (textureAtlasSprite2.spriteNormal != null) {
               flag = true;
            }

            if (textureAtlasSprite2.spriteSpecular != null) {
               flag2 = true;
            }
         }
      }

      if (Config.isMultiTexture()) {
         for (TextureAtlasSprite textureAtlasSprite3 : this.listAnimatedSprites) {
            if (this.isTerrainAnimationActive(textureAtlasSprite3)) {
               TextureAtlasSprite spriteSingle2 = textureAtlasSprite3.spriteSingle;
               if (spriteSingle2 != null) {
                  if (textureAtlasSprite3 == TextureUtils.iconClock || textureAtlasSprite3 == TextureUtils.iconCompass) {
                     spriteSingle2.frameCounter = textureAtlasSprite3.frameCounter;
                  }

                  textureAtlasSprite3.bindSpriteTexture();
                  spriteSingle2.updateAnimation();
                  if (spriteSingle2.isAnimationActive()) {
                     i++;
                  }
               }
            }
         }

         TextureUtil.bindTexture(this.getGlTextureId());
      }

      if (Config.isShaders()) {
         if (flag) {
            TextureUtil.bindTexture(this.getMultiTexID().norm);

            for (TextureAtlasSprite textureAtlasSprite4 : this.listAnimatedSprites) {
               if (textureAtlasSprite4.spriteNormal != null && this.isTerrainAnimationActive(textureAtlasSprite4)) {
                  if (textureAtlasSprite4 == TextureUtils.iconClock || textureAtlasSprite4 == TextureUtils.iconCompass) {
                     textureAtlasSprite4.spriteNormal.frameCounter = textureAtlasSprite4.frameCounter;
                  }

                  textureAtlasSprite4.spriteNormal.updateAnimation();
                  if (textureAtlasSprite4.spriteNormal.isAnimationActive()) {
                     i++;
                  }
               }
            }
         }

         if (flag2) {
            TextureUtil.bindTexture(this.getMultiTexID().spec);

            for (TextureAtlasSprite textureAtlasSprite5 : this.listAnimatedSprites) {
               if (textureAtlasSprite5.spriteSpecular != null && this.isTerrainAnimationActive(textureAtlasSprite5)) {
                  if (textureAtlasSprite5 == TextureUtils.iconClock || textureAtlasSprite5 == TextureUtils.iconCompass) {
                     textureAtlasSprite5.spriteNormal.frameCounter = textureAtlasSprite5.frameCounter;
                  }

                  textureAtlasSprite5.spriteSpecular.updateAnimation();
                  if (textureAtlasSprite5.spriteSpecular.isAnimationActive()) {
                     i++;
                  }
               }
            }
         }

         if (flag || flag2) {
            TextureUtil.bindTexture(this.getGlTextureId());
         }
      }

      int frameCount2 = Config.getMinecraft().entityRenderer.frameCount;
      if (frameCount2 != this.frameCountAnimations) {
         this.countAnimationsActive = i;
         this.frameCountAnimations = frameCount2;
      }

      if (SmartAnimations.isActive()) {
         SmartAnimations.resetSpritesRendered();
      }
   }

   public TextureAtlasSprite getSpriteSafe(String iconName) {
      ResourceLocation resourceLocation = new ResourceLocation(iconName);
      return this.mapRegisteredSprites.get(resourceLocation.toString());
   }

   public TextureAtlasSprite registerSprite(ResourceLocation location) {
      if (location == null) {
         throw new IllegalArgumentException("Location cannot be null!");
      }

      TextureAtlasSprite textureAtlasSprite = this.mapRegisteredSprites.get(location.toString());
      if (textureAtlasSprite == null) {
         textureAtlasSprite = TextureAtlasSprite.makeAtlasSprite(location);
         this.mapRegisteredSprites.put(location.toString(), textureAtlasSprite);
         textureAtlasSprite.updateIndexInMap(this.counterIndexInMap);
         if (Config.isEmissiveTextures()) {
            this.checkEmissive(location, textureAtlasSprite);
         }
      }

      return textureAtlasSprite;
   }

   private void updateIconGrid(int sheetWidth, int sheetHeight) {
      this.iconGridCountX = -1;
      this.iconGridCountY = -1;
      this.iconGrid = null;
      if (this.iconGridSize > 0) {
         this.iconGridCountX = sheetWidth / this.iconGridSize;
         this.iconGridCountY = sheetHeight / this.iconGridSize;
         this.iconGrid = new TextureAtlasSprite[this.iconGridCountX * this.iconGridCountY];
         this.iconGridSizeU = 1.0 / this.iconGridCountX;
         this.iconGridSizeV = 1.0 / this.iconGridCountY;

         for (TextureAtlasSprite textureAtlasSprite2 : this.mapUploadedSprites.values()) {
            double d2 = 0.5 / sheetWidth;
            double d3 = 0.5 / sheetHeight;
            double d4 = Math.min(textureAtlasSprite2.getMinU(), textureAtlasSprite2.getMaxU()) + d2;
            double d5 = Math.min(textureAtlasSprite2.getMinV(), textureAtlasSprite2.getMaxV()) + d3;
            double d6 = Math.max(textureAtlasSprite2.getMinU(), textureAtlasSprite2.getMaxU()) - d2;
            double d7 = Math.max(textureAtlasSprite2.getMinV(), textureAtlasSprite2.getMaxV()) - d3;
            int i = (int)(d4 / this.iconGridSizeU);
            int index = (int)(d5 / this.iconGridSizeV);
            int innerIndex = (int)(d6 / this.iconGridSizeU);
            int i2 = (int)(d7 / this.iconGridSizeV);

            for (int i3 = i; i3 <= innerIndex; i3++) {
               if (i3 >= 0 && i3 < this.iconGridCountX) {
                  for (int index2 = index; index2 <= i2; index2++) {
                     if (index2 >= 0 && index2 < this.iconGridCountX) {
                        int i4 = index2 * this.iconGridCountX + i3;
                        this.iconGrid[i4] = textureAtlasSprite2;
                     } else {
                        Config.warn("Invalid grid V: " + index2 + ", icon: " + textureAtlasSprite2.getIconName());
                     }
                  }
               } else {
                  Config.warn("Invalid grid U: " + i3 + ", icon: " + textureAtlasSprite2.getIconName());
               }
            }
         }
      }
   }

   public boolean isTextureBound() {
      int boundTexture = GlStateManager.getBoundTexture();
      int glTextureId = this.getGlTextureId();
      return boundTexture == glTextureId;
   }

   public TextureMap(String text2, IIconCreator iIconCreator, long l, boolean flag) {
      this.basePath = text2;
      this.iconCreator = iIconCreator;
      this.skipFirst = false;
   }
}
