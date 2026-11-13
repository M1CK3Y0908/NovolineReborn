package net.optifine.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerMooshroomMushroom;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.ITickableTextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.BetterGrass;
import net.optifine.BetterSnow;
import net.optifine.CustomBlockLayers;
import net.optifine.CustomColors;
import net.optifine.CustomGuis;
import net.optifine.CustomItems;
import net.optifine.CustomLoadingScreens;
import net.optifine.CustomPanorama;
import net.optifine.CustomSky;
import net.optifine.Lang;
import net.optifine.NaturalTextures;
import net.optifine.RandomEntities;
import net.optifine.SmartLeaves;
import net.optifine.TextureAnimations;
import net.optifine.entity.model.CustomEntityModels;
import net.optifine.shaders.MultiTexID;
import net.optifine.shaders.Shaders;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class TextureUtils {
   public static TextureAtlasSprite iconMyceliumSide;
   public static final String END_STONE = "end_stone";
   public static final String REDSTONE_LAMP_OFF = "redstone_lamp_off";
   public static final String LOG_SPRUCE_TOP = "log_spruce_top";
   public static TextureAtlasSprite iconFireLayer1;
   public static final String LOG_OAK_TOP = "log_oak_top";
   public static final String REDSTONE_LAMP_ON = "redstone_lamp_on";
   public static final String FIRE_LAYER_0 = "fire_layer_0";
   public static final String LEAVES_JUNGLE = "leaves_jungle";
   public static TextureAtlasSprite iconWaterStill;
   public static TextureAtlasSprite iconLavaFlow;
   public static final String BEDROCK = "bedrock";
   public static TextureAtlasSprite iconGrassSideOverlay;
   public static final String GRAVEL = "gravel";
   public static final String FARMLAND_WET = "farmland_wet";
   public static TextureAtlasSprite iconGlassPaneTop;
   public static TextureAtlasSprite iconCompass;
   public static final String MINECRAFT_BLOCKS = "minecraft:blocks/";
   public static final String SOUL_SAND = "soul_sand";
   public static TextureAtlasSprite iconFireLayer0;
   public static final String CLAY = "clay";
   public static TextureAtlasSprite iconGrassTop;
   public static final String LEAVES_OAK = "leaves_oak";
   public static final String GRASS_SIDE_OVERLAY = "grass_side_overlay";
   public static final String WATER_FLOW = "water_flow";
   public static final String GLASS_PANE_TOP = "glass_pane_top";
   public static final String LEAVES_ACACIA = "leaves_acacia";
   public static final String STONE_SLAB_SIDE = "stone_slab_side";
   public static final String NETHERRACK = "netherrack";
   public static final String COMPASS = "compass";
   public static final String GRASS_TOP = "grass_top";
   public static final String FARMLAND_DRY = "farmland_dry";
   public static final String GOLD_ORE = "gold_ore";
   public static final String MINECRAFT_ITEMS = "minecraft:items/";
   public static final String CLOCK = "clock";
   public static final String STONE_SLAB_TOP = "stone_slab_top";
   public static final String SANDSTONE_TOP = "sandstone_top";
   public static TextureAtlasSprite iconSnow;
   public static final String IRON_ORE = "iron_ore";
   public static final String MYCELIUM_TOP = "mycelium_top";
   public static final String LOG_JUNGLE = "log_jungle";
   public static final String SAND = "sand";
   public static final String LEAVES_SPRUCE = "leaves_spruce";
   public static final String OBSIDIAN = "obsidian";
   public static TextureAtlasSprite iconGrassSideSnowed;
   public static TextureAtlasSprite iconClock;
   public static TextureAtlasSprite iconMyceliumTop;
   public static final String GRASS_SIDE_SNOWED = "grass_side_snowed";
   public static final String DIAMOND_ORE = "diamond_ore";
   public static final String LOG_BIG_OAK_TOP = "log_big_oak_top";
   public static final String REDSTONE_ORE = "redstone_ore";
   public static final String GLASS = "glass";
   public static TextureAtlasSprite iconGrassSide;
   public static final String LOG_BIRCH_TOP = "log_birch_top";
   public static final String SANDSTONE_BOTTOM = "sandstone_bottom";
   public static final String STONE = "stone";
   public static TextureAtlasSprite iconWaterFlow;
   public static final String COAL_ORE = "coal_ore";
   public static final String LEAVES_BIG_OAK = "leaves_big_oak";
   public static final String COARSE_DIRT = "coarse_dirt";
   public static final String LEAVES_SPRUCE_OPAQUE = "leaves_spruce_opaque";
   public static final String LEAVES_SPRUCE2 = "leaves_spruce";
   public static final String SNOW = "snow";
   public static final String WATER_STILL = "water_still";
   public static final String LOG_SPRUCE = "log_spruce";
   public static final String LEAVES_BIRCH = "leaves_birch";
   public static final String FIRE_LAYER_1 = "fire_layer_1";
   public static final String MYCELIUM_SIDE = "mycelium_side";
   public static final String LOG_ACACIA = "log_acacia";
   public static TextureAtlasSprite iconPortal;
   public static final String LAVA_FLOW = "lava_flow";
   public static final String CACTUS_SIDE = "cactus_side";
   public static TextureAtlasSprite iconLavaStill;
   public static final String LOG_OAK = "log_oak";
   public static final String LOG_JUNGLE_TOP = "log_jungle_top";
   public static final String LOG_BIRCH = "log_birch";
   public static final String GLOWSTONE = "glowstone";
   public static final String DIRT = "dirt";
   public static final String LOG_BIG_OAK = "log_big_oak";
   public static final String LOG_ACACIA_TOP = "log_acacia_top";
   public static final String GRASS_SIDE = "grass_side";
   public static final String LAVA_STILL = "lava_still";
   public static TextureAtlasSprite iconGlass;
   public static final String PORTAL = "portal";
   public static final String LAPIS_ORE = "lapis_ore";
   private static IntBuffer staticBuffer = GLAllocation.createDirectIntBuffer(256);

   public static int scaleToGrid(int size, int sizeGrid) {
      if (size == sizeGrid) {
         return size;
      }

      int i = size / sizeGrid * sizeGrid;

      while (i < size) {
         i += sizeGrid;
      }

      return i;
   }

   public static void registerResourceListener() {
      if (Config.getResourceManager() instanceof IReloadableResourceManager reloadableResourceManager) {
         IResourceManagerReloadListener iResourceManagerReloadListener = new IResourceManagerReloadListener() {
            @Override
            public void onResourceManagerReload(IResourceManager resourceManager) {
               TextureUtils.resourcesReloaded(resourceManager);
            }
         };
         reloadableResourceManager.registerReloadListener(iResourceManagerReloadListener);
      }

      ITickableTextureObject iTickableTextureObject = new ITickableTextureObject() {
         @Override
         public MultiTexID getMultiTexID() {
            return null;
         }

         @Override
         public void noop(boolean flag) {
         }

         @Override
         public int getGlTextureId() {
            return 0;
         }

         @Override
         public void loadTexture(IResourceManager resourceManager) throws IOException {
         }

         @Override
         public void restoreLastBlurMipmap() {
         }

         @Override
         public void tick() {
            TextureAnimations.updateAnimations();
         }
      };
      ResourceLocation resourceLocation = new ResourceLocation("optifine/TickableTextures");
      Config.getTextureManager().loadTickableTexture(resourceLocation, iTickableTextureObject);
   }

   public static int scaleToMin(int size, int sizeMin) {
      if (size >= sizeMin) {
         return size;
      }

      int i = sizeMin / size * size;

      while (i < sizeMin) {
         i += size;
      }

      return i;
   }

   public static void resourcesReloaded(IResourceManager rm) {
      if (getTextureMapBlocks() != null) {
         Config.dbg("*** Reloading custom textures ***");
         CustomSky.reset();
         TextureAnimations.reset();
         update();
         NaturalTextures.update();
         BetterGrass.update();
         BetterSnow.update();
         TextureAnimations.update();
         CustomColors.update();
         CustomSky.update();
         RandomEntities.update();
         CustomItems.updateModels();
         CustomEntityModels.update();
         Shaders.resourcesReloaded();
         Lang.resourcesReloaded();
         Config.updateTexturePackClouds();
         SmartLeaves.updateLeavesModels();
         CustomPanorama.update();
         CustomGuis.update();
         LayerMooshroomMushroom.update();
         CustomLoadingScreens.update();
         CustomBlockLayers.update();
         Config.getTextureManager().tick();
      }
   }

   public static void update() {
      TextureMap textureMapBlocks = getTextureMapBlocks();
      if (textureMapBlocks != null) {
         iconGrassTop = textureMapBlocks.getSpriteSafe("minecraft:blocks/grass_top");
         iconGrassSide = textureMapBlocks.getSpriteSafe("minecraft:blocks/grass_side");
         iconGrassSideOverlay = textureMapBlocks.getSpriteSafe("minecraft:blocks/grass_side_overlay");
         iconSnow = textureMapBlocks.getSpriteSafe("minecraft:blocks/snow");
         iconGrassSideSnowed = textureMapBlocks.getSpriteSafe("minecraft:blocks/grass_side_snowed");
         iconMyceliumSide = textureMapBlocks.getSpriteSafe("minecraft:blocks/mycelium_side");
         iconMyceliumTop = textureMapBlocks.getSpriteSafe("minecraft:blocks/mycelium_top");
         iconWaterStill = textureMapBlocks.getSpriteSafe("minecraft:blocks/water_still");
         iconWaterFlow = textureMapBlocks.getSpriteSafe("minecraft:blocks/water_flow");
         iconLavaStill = textureMapBlocks.getSpriteSafe("minecraft:blocks/lava_still");
         iconLavaFlow = textureMapBlocks.getSpriteSafe("minecraft:blocks/lava_flow");
         iconFireLayer0 = textureMapBlocks.getSpriteSafe("minecraft:blocks/fire_layer_0");
         iconFireLayer1 = textureMapBlocks.getSpriteSafe("minecraft:blocks/fire_layer_1");
         iconPortal = textureMapBlocks.getSpriteSafe("minecraft:blocks/portal");
         iconGlass = textureMapBlocks.getSpriteSafe("minecraft:blocks/glass");
         iconGlassPaneTop = textureMapBlocks.getSpriteSafe("minecraft:blocks/glass_pane_top");
         iconCompass = textureMapBlocks.getSpriteSafe("minecraft:items/compass");
         iconClock = textureMapBlocks.getSpriteSafe("minecraft:items/clock");
      }
   }

   public static void applyAnisotropicLevel() {
      if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
         float f = GL11.glGetFloat(34047);
         float anisotropicFilterLevel = Config.getAnisotropicFilterLevel();
         anisotropicFilterLevel = Math.min(anisotropicFilterLevel, f);
         GL11.glTexParameterf(3553, 34046, anisotropicFilterLevel);
      }
   }

   public static String getBasePath(String path) {
      int i = path.lastIndexOf(47);
      return i < 0 ? "" : path.substring(0, i);
   }

   public static Dimension getImageSize(InputStream in, String suffix) {
      Iterator imageReadersBySuffix = ImageIO.getImageReadersBySuffix(suffix);

      while (imageReadersBySuffix.hasNext()) {
         ImageReader imageReader = (ImageReader)imageReadersBySuffix.next();

         Dimension dimension;
         try {
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(in);
            imageReader.setInput(imageInputStream);
            int width2 = imageReader.getWidth(imageReader.getMinIndex());
            int height2 = imageReader.getHeight(imageReader.getMinIndex());
            dimension = new Dimension(width2, height2);
         } catch (IOException ioException) {
            continue;
         } finally {
            imageReader.dispose();
         }

         return dimension;
      }

      return null;
   }

   public static TextureMap getTextureMapBlocks() {
      return Minecraft.getMinecraft().getTextureMapBlocks();
   }

   public static boolean isPowerOfTwo(int x) {
      return MathHelper.roundUpToPowerOfTwo(x) == x;
   }

   public static void bindTexture(int glTexId) {
      GlStateManager.bindTexture(glTexId);
   }

   public static ITextureObject getTexture(ResourceLocation loc) {
      ITextureObject textureObject = Config.getTextureManager().getTexture(loc);
      if (textureObject != null) {
         return textureObject;
      }

      if (!Config.hasResource(loc)) {
         return null;
      }

      SimpleTexture simpleTexture = new SimpleTexture(loc);
      Config.getTextureManager().loadTexture(loc, simpleTexture);
      return simpleTexture;
   }

   public static void saveGlTexture(String name, int textureId, int mipmapLevels, int width, int height) {
      bindTexture(textureId);
      GL11.glPixelStorei(3333, 1);
      GL11.glPixelStorei(3317, 1);
      File parentFile = new File(name).getParentFile();
      if (parentFile != null) {
         parentFile.mkdirs();
      }

      for (int i = 0; i < 16; i++) {
         File file = new File(name + "_" + i + ".png");
         file.delete();
      }

      for (int i2 = 0; i2 <= mipmapLevels; i2++) {
         File file2 = new File(name + "_" + i2 + ".png");
         int i3 = width >> i2;
         int i4 = height >> i2;
         int i5 = i3 * i4;
         IntBuffer intBuffer = BufferUtils.createIntBuffer(i5);
         int[] iArray = new int[i5];
         GL11.glGetTexImage(3553, i2, 32993, 33639, intBuffer);
         intBuffer.get(iArray);
         BufferedImage bufferedImage = new BufferedImage(i3, i4, 2);
         bufferedImage.setRGB(0, 0, i3, i4, iArray, 0, i3);

         try {
            ImageIO.write(bufferedImage, "png", file2);
            Config.dbg("Exported: " + file2);
         } catch (Exception exception) {
            Config.warn("Error writing: " + file2);
            Config.warn(exception.getClass().getName() + ": " + exception.getMessage());
         }
      }
   }

   public static int getGLMaximumTextureSize() {
      for (int i = 65536; i > 0; i >>= 1) {
         GlStateManager.handleInt(i, i, (IntBuffer)null);
         GL11.glGetError();
         if (GlStateManager.getInt() != 0) {
            return i;
         }
      }

      return -1;
   }

   public static String fixResourcePath(String path, String basePath) {
      if (path.startsWith("assets/minecraft/")) {
         return path.substring("assets/minecraft/".length());
      }

      if (path.startsWith("./")) {
         path = path.substring(2);
         if (!basePath.endsWith("/")) {
            basePath = basePath + "/";
         }

         return basePath + path;
      } else {
         if (path.startsWith("/~")) {
            path = path.substring(1);
         }

         if (path.startsWith("~/")) {
            path = path.substring(2);
            return "mcpatcher/" + path;
         } else {
            return path.startsWith("/") ? "mcpatcher/" + path.substring(1) : path;
         }
      }
   }

   public static BufferedImage scaleImage(BufferedImage bi, int w2) {
      int width2 = bi.getWidth();
      int i = bi.getHeight() * w2 / width2;
      BufferedImage bufferedImage = new BufferedImage(w2, i, 2);
      Graphics2D graphics2D = bufferedImage.createGraphics();
      Object interpolationHint = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
      if (w2 < width2 || w2 % width2 != 0) {
         interpolationHint = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
      }

      graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolationHint);
      graphics2D.drawImage(bi, 0, 0, w2, i, (ImageObserver)null);
      return bufferedImage;
   }
}
