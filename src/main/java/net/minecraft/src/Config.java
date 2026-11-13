package net.minecraft.src;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.DynamicLights;
import net.optifine.VersionCheckThread;
import net.optifine.config.GlVersion;
import net.optifine.gui.GuiMessage;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.Shaders;
import net.optifine.util.DisplayModeComparator;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.TextureUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.world.XRay;
import wtf.novoline.util.GlErrorThrottle;
import wtf.novoline.util.ReportThrottle;

public class Config {
   public static final String build = "M5";
   public static final String HD_U = "HD_U";
   public static float renderPartialTicks;
   public static final String OPTI_FINE = "OptiFine";
   public static final String OPTI_FINE_1_8_9_HD_U_M5 = "OptiFine_1.8.9_HD_U_M5";
   public static final String VALUE_1_8_9 = "1.8.9";
   private static String text = null;
   private static String newRelease = null;
   private static boolean notify64BitJava = false;
   public static String openGlVersion = null;
   public static String openGlRenderer = null;
   public static String openGlVendor = null;
   public static String[] openGlExtensions = null;
   public static GlVersion glVersion = null;
   public static GlVersion glslVersion = null;
   public static int minecraftVersionInt = -1;
   public static boolean fancyFogAvailable = false;
   public static boolean occlusionAvailable = false;
   private static boolean flag;
   private static GameSettings gameSettings = null;
   private static Minecraft minecraft = Minecraft.getMinecraft();
   private static boolean initialized = false;
   private static Thread minecraftThread = null;
   private static DisplayMode desktopDisplayMode = null;
   private static DisplayMode[] displayModes = null;
   private static int antialiasingLevel = 0;
   private static int availableProcessors = 0;
   public static boolean zoomMode = false;
   public static boolean zoomSmoothCamera = false;
   private static int texturePackClouds = 0;
   public static boolean waterOpacityChanged = false;
   private static boolean fullscreenModeChecked = false;
   private static boolean desktopModeChecked = false;
   private static DefaultResourcePack defaultResourcePackLazy = null;
   public static final Float DEF_ALPHA_FUNC_LEVEL = 0.1F;
   private static final Logger LOGGER = LogManager.getLogger();
   public static final boolean logDetail = System.getProperty("log.detail", "false").equals("true");
   private static String mcDebugLast = null;
   private static int fpsMinLast = 0;

   public static String arrayToString(Object[] arg0) {
      return arrayToString(arg0, ", ");
   }

   public static boolean parseBoolean(String arg0, boolean arg1) {
      try {
         if (arg0 == null) {
            return arg1;
         }

         arg0 = arg0.trim();
         return Boolean.parseBoolean(arg0);
      } catch (NumberFormatException numberFormatException) {
         return arg1;
      }
   }

   public static boolean isMoonTexture() {
      return !isSunMoonEnabled() ? false : !isShaders() || Shaders.isMoon();
   }

   public static Dimension getFullscreenDimension() {
      if (desktopDisplayMode == null) {
         return null;
      }

      if (gameSettings == null) {
         return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
      }

      String text = gameSettings.ofFullscreenMode;
      if (text.equals("Default")) {
         return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
      }

      String[] stringArray = tokenize(text, " x");
      return stringArray.length < 2
         ? new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight())
         : new Dimension(parseInt(stringArray[0], -1), parseInt(stringArray[1], -1));
   }

   public static String arrayToString(Object[] arg0, String arg1) {
      if (arg0 == null) {
         return "";
      }

      StringBuffer stringBuffer = new StringBuffer(arg0.length * 5);

      for (int index = 0; index < arg0.length; index++) {
         Object object = arg0[index];
         if (index > 0) {
            stringBuffer.append(arg1);
         }

         stringBuffer.append(String.valueOf(object));
      }

      return stringBuffer.toString();
   }

   public static RenderGlobal getRenderGlobal() {
      return minecraft.renderGlobal;
   }

   public static TextureManager getTextureManager() {
      return minecraft.getTextureManager();
   }

   public static String getOpenGlVersionString() {
      GlVersion glVersion2 = getGlVersion();
      return glVersion2.getMajor() + "." + glVersion2.getMinor() + "." + glVersion2.getRelease();
   }

   public static boolean isCloudsFancy() {
      return gameSettings.ofClouds != 0
         ? gameSettings.ofClouds == 2
         : (
            isShaders() && !Shaders.shaderPackClouds.isDefault()
               ? Shaders.shaderPackClouds.isFancy()
               : (texturePackClouds != 0 ? texturePackClouds == 2 : gameSettings.fancyGraphics)
         );
   }

   public static String normalize(String arg0) {
      return arg0 == null ? "" : arg0;
   }

   public static boolean isTimeDefault() {
      return gameSettings.ofTime == 0;
   }

   public static boolean isSmoothWorld() {
      return gameSettings.ofSmoothWorld;
   }

   public static int intHash(int arg0) {
      arg0 = arg0 ^ 61 ^ arg0 >> 16;
      arg0 += arg0 << 3;
      arg0 ^= arg0 >> 4;
      arg0 *= 668265261;
      return arg0 ^ arg0 >> 15;
   }

   public static DefaultResourcePack getDefaultResourcePack() {
      if (defaultResourcePackLazy == null) {
         Minecraft minecraft = Minecraft.getMinecraft();
         defaultResourcePackLazy = (DefaultResourcePack)Reflector.createObject6(minecraft, Reflector.reflectorField32);
         if (defaultResourcePackLazy == null) {
            ResourcePackRepository resourcePackRepository = minecraft.getResourcePackRepository();
            if (resourcePackRepository != null) {
               defaultResourcePackLazy = (DefaultResourcePack)resourcePackRepository.rprDefaultResourcePack;
            }
         }
      }

      return defaultResourcePackLazy;
   }

   public static boolean isAnimatedWater() {
      return gameSettings.ofAnimatedWater != 2;
   }

   public static boolean equalsOne(int arg0, int[] arg1) {
      for (int index = 0; index < arg1.length; index++) {
         if (arg1[index] == arg0) {
            return true;
         }
      }

      return false;
   }

   public static String listToString(List arg0) {
      return listToString(arg0, ", ");
   }

   public static int getRandom(BlockPos arg0, int arg1) {
      return intHash(intHash(intHash(intHash(arg1 + 37) + arg0.getX()) + arg0.getZ()) + arg0.getY());
   }

   public static boolean isCustomItems() {
      return gameSettings.ofCustomItems;
   }

   public static boolean isRainSplash() {
      return gameSettings.ofRainSplash;
   }

   public static boolean isSmoothFps() {
      return gameSettings.ofSmoothFps;
   }

   public static void log(String arg0) {
      dbg(arg0);
   }

   public static String getGlErrorString(int arg0) {
      switch (arg0) {
         case 0:
            return "No error";
         case 1280:
            return "Invalid enum";
         case 1281:
            return "Invalid value";
         case 1282:
            return "Invalid operation";
         case 1283:
            return "Stack overflow";
         case 1284:
            return "Stack underflow";
         case 1285:
            return "Out of memory";
         case 1286:
            return "Invalid framebuffer operation";
         default:
            return "Unknown";
      }
   }

   public static boolean isBetterGrassFancy() {
      return gameSettings.ofBetterGrass == 2;
   }

   public static String readInputStream(InputStream arg0) throws IOException {
      return readInputStream(arg0, "ASCII");
   }

   public static boolean isDynamicLights() {
      return gameSettings.ofDynamicLights != 3;
   }

   public static boolean isDynamicHandLight() {
      return !isDynamicLights() ? false : (isShaders() ? Shaders.isDynamicHandLight() : true);
   }

   public static boolean isSkyEnabled() {
      return gameSettings.ofSky;
   }

   public static boolean isShaders() {
      return Shaders.shaderPackLoaded;
   }

   public static boolean isTreesSmart() {
      return gameSettings.ofTrees == 4;
   }

   public static DisplayMode getDisplayMode(Dimension arg0) throws LWJGLException {
      DisplayMode[] displayModeArray = getDisplayModes();

      for (int index = 0; index < displayModeArray.length; index++) {
         DisplayMode displayMode3 = displayModeArray[index];
         if (displayMode3.getWidth() == arg0.width && displayMode3.getHeight() == arg0.height) {
            return displayMode3;
         }
      }

      return desktopDisplayMode;
   }

   public static int getBitsJre() {
      String[] texts = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

      for (int i = 0; i < texts.length; i++) {
         String property = System.getProperty(texts[i]);
         if (property != null && property.contains("64")) {
            return 64;
         }
      }

      return 32;
   }

   public static boolean isAnimatedExplosion() {
      return gameSettings.ofAnimatedExplosion;
   }

   private static GlVersion getGlVersionLwjgl() {
      return GLContext.getCapabilities().OpenGL44
         ? new GlVersion(4, 4)
         : (
            GLContext.getCapabilities().OpenGL43
               ? new GlVersion(4, 3)
               : (
                  GLContext.getCapabilities().OpenGL42
                     ? new GlVersion(4, 2)
                     : (
                        GLContext.getCapabilities().OpenGL41
                           ? new GlVersion(4, 1)
                           : (
                              GLContext.getCapabilities().OpenGL40
                                 ? new GlVersion(4, 0)
                                 : (
                                    GLContext.getCapabilities().OpenGL33
                                       ? new GlVersion(3, 3)
                                       : (
                                          GLContext.getCapabilities().OpenGL32
                                             ? new GlVersion(3, 2)
                                             : (
                                                GLContext.getCapabilities().OpenGL31
                                                   ? new GlVersion(3, 1)
                                                   : (
                                                      GLContext.getCapabilities().OpenGL30
                                                         ? new GlVersion(3, 0)
                                                         : (
                                                            GLContext.getCapabilities().OpenGL21
                                                               ? new GlVersion(2, 1)
                                                               : (
                                                                  GLContext.getCapabilities().OpenGL20
                                                                     ? new GlVersion(2, 0)
                                                                     : (
                                                                        GLContext.getCapabilities().OpenGL15
                                                                           ? new GlVersion(1, 5)
                                                                           : (
                                                                              GLContext.getCapabilities().OpenGL14
                                                                                 ? new GlVersion(1, 4)
                                                                                 : (
                                                                                    GLContext.getCapabilities().OpenGL13
                                                                                       ? new GlVersion(1, 3)
                                                                                       : (
                                                                                          GLContext.getCapabilities().OpenGL12
                                                                                             ? new GlVersion(1, 2)
                                                                                             : (
                                                                                                GLContext.getCapabilities().OpenGL11
                                                                                                   ? new GlVersion(1, 1)
                                                                                                   : new GlVersion(1, 0)
                                                                                             )
                                                                                       )
                                                                                 )
                                                                           )
                                                                     )
                                                               )
                                                         )
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

   public static IResourceManager getResourceManager() {
      return minecraft.getResourceManager();
   }

   public static float getAmbientOcclusionLevel() {
      XRay xRay2 = ModuleManager.xRay;
      if (xRay2.isEnabled() && !xRay2.scanner.isEnabled()) {
         return 0.0F;
      } else {
         return isShaders() && Shaders.aoLevel >= 0.0F ? Shaders.aoLevel : gameSettings.ofAoLevel;
      }
   }

   public static boolean isClearWater() {
      return gameSettings.ofClearWater;
   }

   public static float getFogStart() {
      return gameSettings.ofFogStart;
   }

   public static boolean isAntialiasing() {
      return getAntialiasingLevel() > 0;
   }

   public static String arrayToString(int[] arg0) {
      return arrayToString(arg0, ", ");
   }

   public static boolean isPotionParticles() {
      return gameSettings.ofPotionParticles;
   }

   public static boolean isDrippingWaterLava() {
      return gameSettings.ofDrippingWaterLava;
   }

   private static void checkOpenGlCaps() {
      log("");
      log(getVersion());
      log("Build: " + getBuild());
      log("OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
      log("Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
      log("VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
      log("LWJGL: " + Sys.getVersion());
      openGlVersion = GL11.glGetString(7938);
      openGlRenderer = GL11.glGetString(7937);
      openGlVendor = GL11.glGetString(7936);
      log("OpenGL: " + openGlRenderer + ", version " + openGlVersion + ", " + openGlVendor);
      log("OpenGL Version: " + getOpenGlVersionString());
      if (!GLContext.getCapabilities().OpenGL12) {
         log("OpenGL Mipmap levels: Not available (GL12.GL_TEXTURE_MAX_LEVEL)");
      }

      fancyFogAvailable = GLContext.getCapabilities().GL_NV_fog_distance;
      if (!fancyFogAvailable) {
         log("OpenGL Fancy fog: Not available (GL_NV_fog_distance)");
      }

      occlusionAvailable = GLContext.getCapabilities().GL_ARB_occlusion_query;
      if (!occlusionAvailable) {
         log("OpenGL Occlussion culling: Not available (GL_ARB_occlusion_query)");
      }

      int glMaximumTextureSize = TextureUtils.getGLMaximumTextureSize();
      dbg("Maximum texture size: " + glMaximumTextureSize + "x" + glMaximumTextureSize);
   }

   public static String getNewRelease() {
      return newRelease;
   }

   public static void checkGlError(String arg0) {
      int i = GlStateManager.glGetError();
      if (i != 0 && GlErrorThrottle.shouldReportError()) {
         String glErrorString = getGlErrorString(i);
         error(String.format("OpenGL error: %s (%s), at: %s", i, glErrorString, arg0));
         if (isShowGlErrors() && ReportThrottle.shouldReport("ShowGlError")) {
            String text2 = I18n.format("of.message.openglError", i, glErrorString);
            minecraft.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text2));
         }
      }
   }

   public static boolean isMipmaps() {
      return gameSettings.mipmapLevels > 0;
   }

   public static boolean isCustomColors() {
      return gameSettings.ofCustomColors;
   }

   public static void checkDisplaySettings() {
      int antialiasingLevel2 = getAntialiasingLevel();
      if (antialiasingLevel2 > 0) {
         DisplayMode displayMode3 = Display.getDisplayMode();
         dbg("FSAA Samples: " + antialiasingLevel2);

         try {
            Display.destroy();
            Display.setDisplayMode(displayMode3);
            Display.create(new PixelFormat().withDepthBits(24).withSamples(antialiasingLevel2));
            Display.setResizable(false);
            Display.setResizable(true);
         } catch (LWJGLException lwjglException) {
            warn("Error setting FSAA: " + antialiasingLevel2 + "x");
            lwjglException.printStackTrace();

            try {
               Display.setDisplayMode(displayMode3);
               Display.create(new PixelFormat().withDepthBits(24));
               Display.setResizable(false);
               Display.setResizable(true);
            } catch (LWJGLException lwjglException2) {
               lwjglException2.printStackTrace();

               try {
                  Display.setDisplayMode(displayMode3);
                  Display.create();
                  Display.setResizable(false);
                  Display.setResizable(true);
               } catch (LWJGLException lwjglException3) {
                  lwjglException3.printStackTrace();
               }
            }
         }

         if (!Minecraft.isRunningOnMac && getDefaultResourcePack() != null) {
            InputStream inputStream2 = null;
            InputStream inputStream3 = null;

            try {
               inputStream2 = getDefaultResourcePack().getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
               inputStream3 = getDefaultResourcePack().getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));
               if (inputStream2 != null && inputStream3 != null) {
                  Display.setIcon(new ByteBuffer[]{readIconImage(inputStream2), readIconImage(inputStream3)});
               }
            } catch (IOException ioException) {
               warn("Error setting window icon: " + ioException.getClass().getName() + ": " + ioException.getMessage());
            } finally {
               IOUtils.closeQuietly(inputStream2);
               IOUtils.closeQuietly(inputStream3);
            }
         }
      }
   }

   public static boolean isDroppedItemsFancy() {
      return gameSettings.ofDroppedItems == 0 ? gameSettings.fancyGraphics : gameSettings.ofDroppedItems == 2;
   }

   public static boolean isDynamicFov() {
      return gameSettings.ofDynamicFov;
   }

   public static boolean isRandomEntities() {
      return gameSettings.ofRandomEntities;
   }

   public static boolean isWaterParticles() {
      return gameSettings.ofWaterParticles;
   }

   private static void handleString(String text2, int i) {
      try {
         ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
         if (threadGroup == null) {
            return;
         }

         Thread[] threads = new Thread[(threadGroup.activeCount() + 10) * 2];
         threadGroup.enumerate(threads, false);

         for (int i2 = 0; i2 < threads.length; i2++) {
            Thread thread2 = threads[i2];
            if (thread2 != null && thread2.getName().startsWith(text2)) {
               thread2.setPriority(i);
            }
         }
      } catch (Throwable throwable) {
         warn(throwable.getClass().getName() + ": " + throwable.getMessage());
      }
   }

   public static float limit(float arg0, float arg1, float arg2) {
      return arg0 < arg1 ? arg1 : (arg0 > arg2 ? arg2 : arg0);
   }

   public static boolean isAnimatedTextures() {
      return gameSettings.ofAnimatedTextures;
   }

   public static boolean isAnimatedFlame() {
      return gameSettings.ofAnimatedFlame;
   }

   public static double limit(double arg0, double arg2, double arg4) {
      return arg0 < arg2 ? arg2 : (arg0 > arg4 ? arg4 : arg0);
   }

   public static boolean equalsOne(Object arg0, Object[] arg1) {
      if (arg1 == null) {
         return false;
      }

      for (int index = 0; index < arg1.length; index++) {
         Object object = arg1[index];
         if (equals(arg0, object)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isStarsEnabled() {
      return gameSettings.ofStars;
   }

   public static String getBuild() {
      if (text == null) {
         try {
            InputStream inputStream = Config.class.getResourceAsStream("/buildof.txt");
            if (inputStream == null) {
               return null;
            }

            text = readLines(inputStream)[0];
         } catch (Exception exception) {
            warn(exception.getClass().getName() + ": " + exception.getMessage());
            text = "";
         }
      }

      return text;
   }

   public static int[] toPrimitive(Integer[] arg0) {
      if (arg0 == null) {
         return null;
      }

      if (arg0.length == 0) {
         return new int[0];
      }

      int[] intArray = new int[arg0.length];

      for (int index = 0; index < intArray.length; index++) {
         intArray[index] = arg0[index];
      }

      return intArray;
   }

   public static boolean isFancyFogAvailable() {
      return fancyFogAvailable;
   }

   public static TextureMap getTextureMap() {
      return getMinecraft().getTextureMapBlocks();
   }

   public static boolean isSingleProcessor() {
      return getAvailableProcessors() <= 1;
   }

   public static int getMipmapLevels() {
      return gameSettings.mipmapLevels;
   }

   public static boolean openWebLink(URI arg0) {
      try {
         Desktop.getDesktop().browse(arg0);
         return true;
      } catch (Exception exception) {
         warn("Error opening link: " + arg0);
         warn(exception.getClass().getName() + ": " + exception.getMessage());
         return false;
      }
   }

   public static boolean isSunMoonEnabled() {
      return gameSettings.ofSunMoon;
   }

   public static boolean isFogFast() {
      return gameSettings.ofFogType == 1;
   }

   public static boolean isAnimatedFire() {
      return gameSettings.ofAnimatedFire;
   }

   public static boolean isTreesFancy() {
      return gameSettings.ofTrees == 0 ? gameSettings.fancyGraphics : gameSettings.ofTrees != 1;
   }

   public static boolean isActive2() {
      return gameSettings.ofBetterSnow;
   }

   public static InputStream getResourceStream(IResourceManager arg0, ResourceLocation arg1) throws IOException {
      IResource resource = arg0.getResource(arg1);
      return resource == null ? null : resource.getInputStream();
   }

   public static boolean isDynamicLightsFast() {
      return gameSettings.ofDynamicLights == 1;
   }

   public static boolean isAnimatedSmoke() {
      return gameSettings.ofAnimatedSmoke;
   }

   public static void warn(String arg0) {
      LOGGER.warn("[OptiFine] " + arg0);
   }

   public static String arrayToString(int[] arg0, String arg1) {
      if (arg0 == null) {
         return "";
      }

      StringBuffer stringBuffer = new StringBuffer(arg0.length * 5);

      for (int index = 0; index < arg0.length; index++) {
         int i = arg0[index];
         if (index > 0) {
            stringBuffer.append(arg1);
         }

         stringBuffer.append(String.valueOf(i));
      }

      return stringBuffer.toString();
   }

   public static boolean isAnisotropicFiltering() {
      return getAnisotropicFilterLevel() > 1;
   }

   public static boolean isAnimatedRedstone() {
      return gameSettings.ofAnimatedRedstone;
   }

   public static boolean isVignetteEnabled() {
      return isShaders() && !Shaders.isVignette() ? false : (gameSettings.ofVignette == 0 ? gameSettings.fancyGraphics : gameSettings.ofVignette == 2);
   }

   public static IntBuffer createDirectIntBuffer(int arg0) {
      return GLAllocation.createDirectByteBuffer(arg0 << 2).asIntBuffer();
   }

   static {
      handleBoolean(true);
   }

   public static void drawFps() {
      int debugFPS = Minecraft.getDebugFPS();
      String text = getUpdates(minecraft.debug);
      int countActiveRenderers = minecraft.renderGlobal.getCountActiveRenderers();
      int countEntitiesRendered = minecraft.renderGlobal.getCountEntitiesRendered();
      int countTileEntitiesRendered = minecraft.renderGlobal.getCountTileEntitiesRendered();
      String text2 = debugFPS + "/" + getFpsMin() + " fps, C: " + countActiveRenderers + ", E: " + countEntitiesRendered + "+" + countTileEntitiesRendered + ", U: " + text;
      minecraft.fontRendererObj.getInt(text2, 2, 2, -2039584);
   }

   public static boolean isMinecraftThread() {
      return Thread.currentThread() == minecraftThread;
   }

   public static String getVersion() {
      return "OptiFine_1.8.9_HD_U_M5";
   }

   public static boolean alwaysFalse2() {
      return false;
   }

   public static ModelManager getModelManager() {
      return minecraft.getRenderItem().modelManager;
   }

   public static boolean isFogOff() {
      return gameSettings.ofFogType == 3;
   }

   public static boolean isFireworkParticles() {
      return gameSettings.ofFireworkParticles;
   }

   public static void checkInitialized() {
      if (!initialized && Display.isCreated()) {
         initialized = true;
         checkOpenGlCaps();
         run();
      }
   }

   public static boolean isWeatherEnabled() {
      return gameSettings.ofWeather;
   }

   public static void updateAvailableProcessors() {
      availableProcessors = Runtime.getRuntime().availableProcessors();
   }

   public static int getFpsMin() {
      if (minecraft.debug == mcDebugLast) {
         return fpsMinLast;
      }

      mcDebugLast = minecraft.debug;
      FrameTimer frameTimer = minecraft.getFrameTimer();
      long[] along = frameTimer.getFrames();
      int index2 = frameTimer.getIndex();
      int lastIndex = frameTimer.getLastIndex();
      if (index2 == lastIndex) {
         return fpsMinLast;
      }

      int debugFPS = Minecraft.getDebugFPS();
      if (debugFPS <= 0) {
         debugFPS = 1;
      }

      long l = (long)(1.0 / debugFPS * 1.0E9);
      long l2 = l;
      long l3 = 0L;

      for (int nestedIndex = MathHelper.normalizeAngle(index2 - 1, along.length);
         nestedIndex != lastIndex && l3 < 1.0E9;
         nestedIndex = MathHelper.normalizeAngle(nestedIndex - 1, along.length)
      ) {
         long l4 = along[nestedIndex];
         if (l4 > l2) {
            l2 = l4;
         }

         l3 += l4;
      }

      double d = l2 / 1.0E9;
      fpsMinLast = (int)(1.0 / d);
      return fpsMinLast;
   }

   public static boolean isFogFancy() {
      return !isFancyFogAvailable() ? false : gameSettings.ofFogType == 2;
   }

   public static GameSettings getGameSettings() {
      return gameSettings;
   }

   private static ByteBuffer readIconImage(InputStream arg0) throws IOException {
      BufferedImage bufferedImage = ImageIO.read(arg0);
      int[] rgb = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), (int[])null, 0, bufferedImage.getWidth());
      ByteBuffer byteBuffer = ByteBuffer.allocate(4 * rgb.length);

      for (int i : rgb) {
         byteBuffer.putInt(i << 8 | i >> 24 & 0xFF);
      }

      byteBuffer.flip();
      return byteBuffer;
   }

   public static boolean isTimeNightOnly() {
      return gameSettings.ofTime == 2;
   }

   public static String[] getOpenGlExtensions() {
      if (openGlExtensions == null) {
         openGlExtensions = detectOpenGlExtensions();
      }

      return openGlExtensions;
   }

   public static boolean isIntegratedServerRunning() {
      return minecraft.getIntegratedServer() == null ? false : minecraft.isIntegratedServerRunning();
   }

   public static int getMipmapType() {
      switch (gameSettings.ofMipmapType) {
         case 0:
            return 9986;
         case 1:
            return 9986;
         case 2:
            if (isMultiTexture()) {
               return 9985;
            }

            return 9986;
         case 3:
            if (isMultiTexture()) {
               return 9987;
            }

            return 9986;
         default:
            return 9986;
      }
   }

   public static String listToString(List arg0, String arg1) {
      if (arg0 == null) {
         return "";
      }

      StringBuffer stringBuffer = new StringBuffer(arg0.size() * 5);

      for (int index = 0; index < arg0.size(); index++) {
         Object element = arg0.get(index);
         if (index > 0) {
            stringBuffer.append(arg1);
         }

         stringBuffer.append(String.valueOf(element));
      }

      return stringBuffer.toString();
   }

   public static boolean isVoidParticles() {
      return gameSettings.ofVoidParticles;
   }

   public static void setNotify64BitJava(boolean arg0) {
      notify64BitJava = arg0;
   }

   public static InputStream getResourceStream(ResourceLocation arg0) throws IOException {
      return getResourceStream(minecraft.getResourceManager(), arg0);
   }

   public static boolean isTrue(Boolean arg0) {
      return arg0 != null && arg0;
   }

   public static boolean isPortalParticles() {
      return gameSettings.ofPortalParticles;
   }

   public static boolean isCustomGuis() {
      return gameSettings.ofCustomGuis;
   }

   private static DisplayMode getDisplayMode(DisplayMode[] arg0, DisplayMode arg1) {
      if (arg1 != null) {
         for (int index = 0; index < arg0.length; index++) {
            DisplayMode displayMode3 = arg0[index];
            if (displayMode3.getBitsPerPixel() == arg1.getBitsPerPixel() && displayMode3.getFrequency() == arg1.getFrequency()) {
               return displayMode3;
            }
         }
      }

      if (arg0.length <= 0) {
         return null;
      }

      Arrays.sort(arg0, new DisplayModeComparator());
      return arg0[arg0.length - 1];
   }

   public static void writeFile(File arg0, String arg1) throws IOException {
      FileOutputStream fileOutputStream = new FileOutputStream(arg0);
      byte[] byteArray = arg1.getBytes("ASCII");
      fileOutputStream.write(byteArray);
      fileOutputStream.close();
   }

   public static GlVersion getGlVersion() {
      if (glVersion == null) {
         glVersion = parseGlVersion(GL11.glGetString(7938), (GlVersion)null);
         if (glVersion == null) {
            glVersion = getGlVersionLwjgl();
         }

         if (glVersion == null) {
            glVersion = new GlVersion(1, 0);
         }
      }

      return glVersion;
   }

   public static boolean isSameOne(Object arg0, Object[] arg1) {
      if (arg1 == null) {
         return false;
      }

      for (int index = 0; index < arg1.length; index++) {
         Object object = arg1[index];
         if (arg0 == object) {
            return true;
         }
      }

      return false;
   }

   public static String[] tokenize(String arg0, String arg1) {
      StringTokenizer stringTokenizer = new StringTokenizer(arg0, arg1);
      ArrayList arrayList = new ArrayList();

      while (stringTokenizer.hasMoreTokens()) {
         String text = stringTokenizer.nextToken();
         arrayList.add(text);
      }

      return (String[]) arrayList.toArray(new String[arrayList.size()]);
   }

   public static int getUpdatesPerFrame() {
      return gameSettings.ofChunkUpdates;
   }

   public static boolean isQuadsToTriangles() {
      return !isShaders() ? false : !Shaders.canRenderQuads();
   }

   public static void showGuiMessage(String arg0, String arg1) {
      GuiMessage guiMessage = new GuiMessage(minecraft.currentScreen, arg0, arg1);
      minecraft.displayGuiScreen(guiMessage);
   }

   public static GlVersion parseGlVersion(String arg0, GlVersion arg1) {
      try {
         if (arg0 == null) {
            return arg1;
         }

         Matcher matcher = Pattern.compile("([0-9]+)\\.([0-9]+)(\\.([0-9]+))?(.+)?").matcher(arg0);
         if (!matcher.matches()) {
            return arg1;
         }

         int i = Integer.parseInt(matcher.group(1));
         int i2 = Integer.parseInt(matcher.group(2));
         int i3 = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0;
         String text = matcher.group(5);
         return new GlVersion(i, i2, i3, text);
      } catch (Exception exception) {
         exception.printStackTrace();
         return arg1;
      }
   }

   private static String[] detectOpenGlExtensions() {
      try {
         if (getGlVersion().getMajor() >= 3) {
            int i = GL11.glGetInteger(33309);
            if (i > 0) {
               String[] texts = new String[i];

               for (int i2 = 0; i2 < i; i2++) {
                  texts[i2] = GL30.glGetStringi(7939, i2);
               }

               return texts;
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      try {
         return GL11.glGetString(7939).split(" ");
      } catch (Exception exception2) {
         exception2.printStackTrace();
         return new String[0];
      }
   }

   public static boolean isTimeDayOnly() {
      return gameSettings.ofTime == 1;
   }

   public static void updateFramebufferSize() {
      minecraft.getFramebuffer().createBindFramebuffer(minecraft.displayWidth, minecraft.displayHeight);
      if (minecraft.entityRenderer != null) {
         minecraft.entityRenderer.updateShaderGroupSize(minecraft.displayWidth, minecraft.displayHeight);
      }

      minecraft.loadingScreen = new LoadingScreenRenderer(minecraft);
   }

   public static boolean hasResource(IResourceManager arg0, ResourceLocation arg1) {
      try {
         return arg0.getResource(arg1) != null;
      } catch (IOException ioException) {
         return false;
      }
   }

   public static String[] readLines(InputStream arg0) throws IOException {
      ArrayList arrayList = new ArrayList();
      InputStreamReader inputStreamReader = new InputStreamReader(arg0, "ASCII");
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

      while (true) {
         String line2 = bufferedReader.readLine();
         if (line2 == null) {
            return (String[]) arrayList.toArray(new String[arrayList.size()]);
         }

         arrayList.add(line2);
      }
   }

   public static boolean isRainOff() {
      return gameSettings.ofRain == 3;
   }

   public static boolean isActive3() {
      return gameSettings.ofChunkUpdatesDynamic;
   }

   public static void initGameSettings(GameSettings arg0) {
      if (gameSettings == null) {
         gameSettings = arg0;
         desktopDisplayMode = Display.getDesktopDisplayMode();
         updateAvailableProcessors();
         ReflectorForge.handleString("optifine.ForgeSplashCompatible", Boolean.TRUE);
      }
   }

   public static boolean isNotify64BitJava() {
      return notify64BitJava;
   }

   public static float limitTo1(float arg0) {
      return arg0 < 0.0F ? 0.0F : (arg0 > 1.0F ? 1.0F : arg0);
   }

   public static boolean isShowGlErrors() {
      return gameSettings.ofShowGlErrors;
   }

   public static boolean isActive() {
      return gameSettings.ofAlternateBlocks;
   }

   private static DisplayMode[] getDisplayModes(DisplayMode[] arg0, Dimension arg1) {
      ArrayList arrayList = new ArrayList();

      for (int index = 0; index < arg0.length; index++) {
         DisplayMode displayMode3 = arg0[index];
         if (displayMode3.getWidth() == arg1.getWidth() && displayMode3.getHeight() == arg1.getHeight()) {
            arrayList.add(displayMode3);
         }
      }

      return (DisplayMode[]) arrayList.toArray(new DisplayMode[arrayList.size()]);
   }

   public static boolean isMultiTexture() {
      return getAnisotropicFilterLevel() > 1 ? true : getAntialiasingLevel() > 0;
   }

   public static boolean isLazyChunkLoading() {
      return gameSettings.ofLazyChunkLoading;
   }

   public static boolean isFromDefaultResourcePack(ResourceLocation arg0) {
      return getDefiningResourcePack(arg0) == getDefaultResourcePack();
   }

   public static boolean isSmoothBiomes() {
      return gameSettings.ofSmoothBiomes;
   }

   public static String[] getDisplayModeNames() {
      DisplayMode[] displayModeArray = getDisplayModes();
      String[] stringArray = new String[displayModeArray.length];

      for (int index = 0; index < displayModeArray.length; index++) {
         DisplayMode displayMode3 = displayModeArray[index];
         String text2 = displayMode3.getWidth() + "x" + displayMode3.getHeight();
         stringArray[index] = text2;
      }

      return stringArray;
   }

   public static byte[] readAll(InputStream arg0) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      byte[] bArray = new byte[1024];

      while (true) {
         int i = arg0.read(bArray);
         if (i < 0) {
            arg0.close();
            return byteArrayOutputStream.toByteArray();
         }

         byteArrayOutputStream.write(bArray, 0, i);
      }
   }

   public static void sleep(long arg0) {
      try {
         Thread.sleep(arg0);
      } catch (InterruptedException interruptedException) {
         interruptedException.printStackTrace();
      }
   }

   public static int compareRelease(String arg0, String arg1) {
      String[] stringArray = splitRelease(arg0);
      String[] stringArray1 = splitRelease(arg1);
      String text2 = stringArray[0];
      String text3 = stringArray1[0];
      if (!text2.equals(text3)) {
         return text2.compareTo(text3);
      }

      int i = parseInt(stringArray[1], -1);
      int i2 = parseInt(stringArray1[1], -1);
      if (i != i2) {
         return i - i2;
      }

      String text4 = stringArray[2];
      String text5 = stringArray1[2];
      if (!text4.equals(text5)) {
         if (text4.isEmpty()) {
            return 1;
         }

         if (text5.isEmpty()) {
            return -1;
         }
      }

      return text4.compareTo(text5);
   }

   public static boolean isAntialiasingConfigured() {
      return getGameSettings().ofAaLevel > 0;
   }

   public static void error(String arg0) {
      LOGGER.error("[OptiFine] " + arg0);
   }

   public static int getMinecraftVersionInt() {
      if (minecraftVersionInt < 0) {
         String[] stringArray = tokenize("1.8.9", ".");
         int i = 0;
         if (stringArray.length > 0) {
            i = 0 + 10000 * parseInt(stringArray[0], 0);
         }

         if (stringArray.length > 1) {
            i += 100 * parseInt(stringArray[1], 0);
         }

         if (stringArray.length > 2) {
            i += 1 * parseInt(stringArray[2], 0);
         }

         minecraftVersionInt = i;
      }

      return minecraftVersionInt;
   }

   public static int getBitsOs() {
      return System.getenv("ProgramFiles(X86)") != null ? 64 : 32;
   }

   private static void run() {
      new VersionCheckThread().start();
   }

   public static int getAnisotropicFilterLevel() {
      return gameSettings.ofAfLevel;
   }

   public static int getChunkViewDistance() {
      return gameSettings == null ? 10 : gameSettings.renderDistanceChunks;
   }

   public static boolean isShowCapes() {
      return gameSettings.ofShowCapes;
   }

   public static boolean between(float arg0, float arg1, float arg2) {
      return arg0 >= arg1 && arg0 <= arg2;
   }

   public static boolean isNaturalTextures() {
      return gameSettings.ofNaturalTextures;
   }

   public static int getAvailableProcessors() {
      return availableProcessors;
   }

   public static void checkDisplayMode() {
      try {
         if (minecraft.isFullScreen()) {
            if (fullscreenModeChecked) {
               return;
            }

            fullscreenModeChecked = true;
            desktopModeChecked = false;
            DisplayMode displayMode = Display.getDisplayMode();
            Dimension dimension = getFullscreenDimension();
            if (dimension == null) {
               return;
            }

            if (displayMode.getWidth() == dimension.width && displayMode.getHeight() == dimension.height) {
               return;
            }

            DisplayMode displayMode2 = getDisplayMode(dimension);
            if (displayMode2 == null) {
               return;
            }

            Display.setDisplayMode(displayMode2);
            minecraft.displayWidth = Display.getDisplayMode().getWidth();
            minecraft.displayHeight = Display.getDisplayMode().getHeight();
            if (minecraft.displayWidth <= 0) {
               minecraft.displayWidth = 1;
            }

            if (minecraft.displayHeight <= 0) {
               minecraft.displayHeight = 1;
            }

            if (minecraft.currentScreen != null) {
               ScaledResolution scaledResolution = new ScaledResolution(minecraft);
               int scaledWidth = scaledResolution.getScaledWidth();
               int scaledHeight = scaledResolution.getScaledHeight();
               minecraft.currentScreen.setWorldAndResolution(minecraft, scaledWidth, scaledHeight);
            }

            updateFramebufferSize();
            Display.setFullscreen(true);
            minecraft.gameSettings.updateVSync();
            GlStateManager.enableTexture2D();
         } else {
            if (desktopModeChecked) {
               return;
            }

            desktopModeChecked = true;
            fullscreenModeChecked = false;
            minecraft.gameSettings.updateVSync();
            Display.update();
            GlStateManager.enableTexture2D();
            Display.setResizable(false);
            Display.setResizable(true);
         }
      } catch (Exception exception) {
         exception.printStackTrace();
         gameSettings.ofFullscreenMode = "Default";
         gameSettings.saveOfOptions();
      }
   }

   public static IResourcePack[] getResourcePacks() {
      ResourcePackRepository resourcePackRepository = minecraft.getResourcePackRepository();
      List list = resourcePackRepository.getRepositoryEntries();
      ArrayList arrayList = new ArrayList();

      for (net.minecraft.client.resources.ResourcePackRepository.Entry resourcepackrepository$entry : (Iterable<ResourcePackRepository.Entry>) list) {
         arrayList.add(resourcepackrepository$entry.getResourcePack());
      }

      if (resourcePackRepository.getResourcePackInstance() != null) {
         arrayList.add(resourcePackRepository.getResourcePackInstance());
      }

      return (IResourcePack[]) arrayList.toArray(new IResourcePack[arrayList.size()]);
   }

   private static String getUpdates(String arg0) {
      int i = arg0.indexOf(40);
      if (i < 0) {
         return "";
      }

      int i2 = arg0.indexOf(32, i);
      return i2 < 0 ? "" : arg0.substring(i + 1, i2);
   }

   public static boolean isSwampColors() {
      return gameSettings.ofSwampColors;
   }

   public static int getScreenshotSize() {
      return gameSettings.ofScreenshotSize;
   }

   public static boolean isAnimatedPortal() {
      return gameSettings.ofAnimatedPortal;
   }

   public static Minecraft getMinecraft() {
      return minecraft;
   }

   private static Set<Dimension> getDisplayModeDimensions(DisplayMode[] arg0) {
      HashSet hashSet = new HashSet();

      for (int index = 0; index < arg0.length; index++) {
         DisplayMode displayMode3 = arg0[index];
         Dimension dimension2 = new Dimension(displayMode3.getWidth(), displayMode3.getHeight());
         hashSet.add(dimension2);
      }

      return hashSet;
   }

   public static DisplayMode[] getDisplayModes() {
      if (displayModes == null) {
         try {
            DisplayMode[] displayModeArray = Display.getAvailableDisplayModes();
            Set<Dimension> set = getDisplayModeDimensions(displayModeArray);
            ArrayList arrayList = new ArrayList();

            for (Dimension dimension2 : set) {
               DisplayMode displayMode = getDisplayMode(getDisplayModes(displayModeArray, dimension2), desktopDisplayMode);
               if (displayMode != null) {
                  arrayList.add(displayMode);
               }
            }

            DisplayMode[] displayModes2 = (DisplayMode[]) arrayList.toArray(new DisplayMode[arrayList.size()]);
            Arrays.sort(displayModes2, new DisplayModeComparator());
            return displayModes2;
         } catch (Exception exception) {
            exception.printStackTrace();
            displayModes = new DisplayMode[]{desktopDisplayMode};
         }
      }

      return displayModes;
   }

   public static boolean isAnimatedTerrain() {
      return gameSettings.ofAnimatedTerrain;
   }

   public static boolean isCloudsOff() {
      return gameSettings.ofClouds != 0
         ? gameSettings.ofClouds == 3
         : (isShaders() && !Shaders.shaderPackClouds.isDefault() ? Shaders.shaderPackClouds.isOff() : (texturePackClouds != 0 ? texturePackClouds == 3 : false));
   }

   public static int limit(int arg0, int arg1, int arg2) {
      return arg0 < arg1 ? arg1 : (arg0 > arg2 ? arg2 : arg0);
   }

   public static boolean alwaysFalse() {
      return false;
   }

   public static void handleBoolean(boolean flag2) {
      flag = flag2;
   }

   public static boolean isCustomEntityModels() {
      return gameSettings.ofCustomEntityModels;
   }

   public static boolean hasResource(ResourceLocation arg0) {
      return arg0 == null ? false : getDefiningResourcePack(arg0) != null;
   }

   public static Object[] removeObjectFromArray(Object[] arg0, Object arg1) {
      ArrayList arrayList = new ArrayList<>(Arrays.asList(arg0));
      arrayList.add(0, arg1);
      Object[] objects = (Object[])Array.newInstance(arg0.getClass().getComponentType(), arrayList.size());
      return arrayList.toArray(objects);
   }

   public static boolean isEmissiveTextures() {
      return gameSettings.ofEmissiveTextures;
   }

   public static boolean isRainFancy() {
      return gameSettings.ofRain == 0 ? gameSettings.fancyGraphics : gameSettings.ofRain == 2;
   }

   public static Object[] addObjectsToArray(Object[] arg0, Object[] arg1) {
      if (arg0 == null) {
         throw new NullPointerException("The given array is NULL");
      }

      if (arg1.length == 0) {
         return arg0;
      }

      int count = arg0.length;
      int count2 = count + arg1.length;
      Object[] objects = (Object[])Array.newInstance(arg0.getClass().getComponentType(), count2);
      System.arraycopy(arg0, 0, objects, 0, count);
      System.arraycopy(arg1, 0, objects, count, arg1.length);
      return objects;
   }

   public static boolean isVbo() {
      return OpenGlHelper.useVbo();
   }

   public static String getVersionDebug() {
      StringBuffer stringBuffer = new StringBuffer(32);
      if (isDynamicLights()) {
         stringBuffer.append("DL: ");
         stringBuffer.append(String.valueOf(DynamicLights.getCount()));
         stringBuffer.append(", ");
      }

      stringBuffer.append("OptiFine_1.8.9_HD_U_M5");
      String text = Shaders.getShaderPackName();
      if (text != null) {
         stringBuffer.append(", ");
         stringBuffer.append(text);
      }

      return stringBuffer.toString();
   }

   public static float parseFloat(String arg0, float arg1) {
      try {
         if (arg0 == null) {
            return arg1;
         }

         arg0 = arg0.trim();
         return Float.parseFloat(arg0);
      } catch (NumberFormatException numberFormatException) {
         return arg1;
      }
   }

   public static void setNewRelease(String arg0) {
      newRelease = arg0;
   }

   public static float getAlphaFuncLevel() {
      return DEF_ALPHA_FUNC_LEVEL;
   }

   public static Boolean parseBoolean(String arg0, Boolean arg1) {
      try {
         if (arg0 == null) {
            return arg1;
         }

         arg0 = arg0.trim().toLowerCase();
         return arg0.equals("true") ? Boolean.TRUE : (arg0.equals("false") ? Boolean.FALSE : arg1);
      } catch (NumberFormatException numberFormatException) {
         return arg1;
      }
   }

   public static void dbg(String arg0) {
      LOGGER.info("[OptiFine] " + arg0);
   }

   public static int getAntialiasingLevel() {
      return antialiasingLevel;
   }

   public static String getResourcePackNames() {
      if (minecraft.getResourcePackRepository() == null) {
         return "";
      }

      IResourcePack[] resourcePackArray = getResourcePacks();
      if (resourcePackArray.length <= 0) {
         return getDefaultResourcePack().getPackName();
      }

      String[] stringArray = new String[resourcePackArray.length];

      for (int index = 0; index < resourcePackArray.length; index++) {
         stringArray[index] = resourcePackArray[index].getPackName();
      }

      return arrayToString(stringArray);
   }

   public static GlVersion getGlslVersion() {
      if (glslVersion == null) {
         glslVersion = parseGlVersion(GL11.glGetString(35724), (GlVersion)null);
         if (glslVersion == null) {
            glslVersion = new GlVersion(1, 10);
         }
      }

      return glslVersion;
   }

   public static DisplayMode getLargestDisplayMode() {
      DisplayMode[] displayModeArray = getDisplayModes();
      if (displayModeArray != null && displayModeArray.length >= 1) {
         DisplayMode displayMode3 = displayModeArray[displayModeArray.length - 1];
         return desktopDisplayMode.getWidth() > displayMode3.getWidth()
            ? desktopDisplayMode
            : (desktopDisplayMode.getWidth() == displayMode3.getWidth() && desktopDisplayMode.getHeight() > displayMode3.getHeight() ? desktopDisplayMode : displayMode3);
      } else {
         return desktopDisplayMode;
      }
   }

   public static boolean alwaysTrue() {
      return true;
   }

   public static DynamicTexture getMojangLogoTexture(DynamicTexture arg0) {
      try {
         ResourceLocation resourceLocation = new ResourceLocation("textures/gui/title/mojang.png");
         InputStream resourceStream = getResourceStream(resourceLocation);
         if (resourceStream == null) {
            return arg0;
         }

         BufferedImage bufferedImage = ImageIO.read(resourceStream);
         return bufferedImage == null ? arg0 : new DynamicTexture(bufferedImage);
      } catch (Exception exception) {
         warn(exception.getClass().getName() + ": " + exception.getMessage());
         return arg0;
      }
   }

   public static void detail(String arg0) {
      if (logDetail) {
         LOGGER.info("[OptiFine] " + arg0);
      }
   }

   public static String readInputStream(InputStream arg0, String arg1) throws IOException {
      InputStreamReader inputStreamReader = new InputStreamReader(arg0, arg1);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      StringBuffer stringBuffer = new StringBuffer();

      while (true) {
         String line = bufferedReader.readLine();
         if (line == null) {
            return stringBuffer.toString();
         }

         stringBuffer.append(line);
         stringBuffer.append("\n");
      }
   }

   public static boolean equals(Object arg0, Object arg1) {
      return arg0 == arg1 ? true : (arg0 == null ? false : arg0.equals(arg1));
   }

   private static String[] splitRelease(String arg0) {
      if (arg0 != null && arg0.length() > 0) {
         Matcher matcher2 = Pattern.compile("([A-Z])([0-9]+)(.*)").matcher(arg0);
         if (!matcher2.matches()) {
            return new String[]{"", "", ""};
         }

         String text2 = normalize(matcher2.group(1));
         String text3 = normalize(matcher2.group(2));
         String text4 = normalize(matcher2.group(3));
         return new String[]{text2, text3, text4};
      } else {
         return new String[]{"", "", ""};
      }
   }

   public static boolean isFogOn() {
      return gameSettings.ofFogType != 3;
   }

   public static DisplayMode getDesktopDisplayMode() {
      return desktopDisplayMode;
   }

   public static boolean isCustomSky() {
      return gameSettings.ofCustomSky;
   }

   public static boolean isSunTexture() {
      return !isSunMoonEnabled() ? false : !isShaders() || Shaders.isSun();
   }

   public static void updateTexturePackClouds() {
      texturePackClouds = 0;
      IResourceManager resourceManager = getResourceManager();
      if (resourceManager != null) {
         try {
            InputStream inputStream2 = resourceManager.getResource(new ResourceLocation("mcpatcher/color.properties")).getInputStream();
            if (inputStream2 == null) {
               return;
            }

            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(inputStream2);
            inputStream2.close();
            String property = propertiesOrdered.getProperty("clouds");
            if (property == null) {
               return;
            }

            dbg("Texture pack clouds: " + property);
            property = property.toLowerCase();
            if (property.equals("fast")) {
               texturePackClouds = 1;
            }

            if (property.equals("fancy")) {
               texturePackClouds = 2;
            }

            if (property.equals("off")) {
               texturePackClouds = 3;
            }
         } catch (Exception exception) {
         }
      }
   }

   public static boolean isBetterGrass() {
      return gameSettings.ofBetterGrass != 3;
   }

   public static boolean isConnectedTextures() {
      return gameSettings.ofConnectedTextures != 3;
   }

   public static boolean isRenderRegions() {
      return gameSettings.ofRenderRegions;
   }

   public static boolean isOcclusionAvailable() {
      return occlusionAvailable;
   }

   public static IResourcePack getDefiningResourcePack(ResourceLocation arg0) {
      ResourcePackRepository resourcePackRepository2 = minecraft.getResourcePackRepository();
      IResourcePack resourcePackInstance = resourcePackRepository2.getResourcePackInstance();
      if (resourcePackInstance != null && resourcePackInstance.resourceExists(arg0)) {
         return resourcePackInstance;
      }

      List repositoryEntries2 = resourcePackRepository2.repositoryEntries;

      for (int i = repositoryEntries2.size() - 1; i >= 0; i--) {
         IResourcePack resourcePack = ((net.minecraft.client.resources.ResourcePackRepository.Entry)repositoryEntries2.get(i)).getResourcePack();
         if (resourcePack.resourceExists(arg0)) {
            return resourcePack;
         }
      }

      return getDefaultResourcePack().resourceExists(arg0) ? getDefaultResourcePack() : null;
   }

   public static Object[] addObjectToArray(Object[] arg0, Object arg1) {
      if (arg0 == null) {
         throw new NullPointerException("The given array is NULL");
      }

      int count = arg0.length;
      int i = count + 1;
      Object[] objects = (Object[])Array.newInstance(arg0.getClass().getComponentType(), i);
      System.arraycopy(arg0, 0, objects, 0, count);
      objects[count] = arg1;
      return objects;
   }

   public static boolean isConnectedTexturesFancy() {
      return gameSettings.ofConnectedTextures == 2;
   }

   public static int parseInt(String arg0, int arg1) {
      try {
         if (arg0 == null) {
            return arg1;
         }

         arg0 = arg0.trim();
         return Integer.parseInt(arg0);
      } catch (NumberFormatException numberFormatException) {
         return arg1;
      }
   }

   public static void updateThreadPriorities() {
      updateAvailableProcessors();
      if (isSingleProcessor()) {
         if (isSmoothWorld()) {
            minecraftThread.setPriority(10);
            handleString("Server thread", 1);
         } else {
            minecraftThread.setPriority(5);
            handleString("Server thread", 5);
         }
      } else {
         minecraftThread.setPriority(10);
         handleString("Server thread", 5);
      }
   }

   public static void initDisplay() {
      checkInitialized();
      antialiasingLevel = gameSettings.ofAaLevel;
      checkDisplaySettings();
      checkDisplayMode();
      minecraftThread = Thread.currentThread();
      updateThreadPriorities();
      Shaders.startup(Minecraft.getMinecraft());
   }

   public static boolean isAnimatedLava() {
      return gameSettings.ofAnimatedLava != 2;
   }

   public static boolean isCustomFonts() {
      return gameSettings.ofCustomFonts;
   }
}
