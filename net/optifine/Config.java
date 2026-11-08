package net.optifine;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import net.shadersmod.client.Shaders;

public class Config {
    public static final String OF_NAME = "OptiFine";
    public static final String MC_VERSION = "1.8.8";
    public static final String OF_EDITION = "HD_U";
    public static final String OF_RELEASE = "I7";
    public static final String VERSION = "OptiFine HD I7 SRC VERSION";
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
    private static GameSettings gameSettings = null;
    private static Minecraft minecraft = Minecraft.getMinecraft();
    private static boolean initialized = false;
    private static Thread minecraftThread = null;
    private static DisplayMode desktopDisplayMode = null;
    private static DisplayMode[] displayModes = null;
    private static int antialiasingLevel = 0;
    private static int availableProcessors = 0;
    public static boolean zoomMode = false;
    private static int texturePackClouds = 0;
    public static boolean waterOpacityChanged = false;
    private static boolean fullscreenModeChecked = false;
    private static boolean desktopModeChecked = false;
    private static DefaultResourcePack defaultResourcePackLazy = null;
    public static final Float DEF_ALPHA_FUNC_LEVEL = 0.1F;
    private static final Logger LOGGER = LogManager.getLogger();
    public static float renderPartialTicks;

    private Config() {
    }

    public static String getVersion() {
        return "OptiFine HD I7 SRC VERSION";
    }

    public static String getVersionDebug() {
        StringBuffer stringbuffer = new StringBuffer(32);
        if (isDynamicLights()) {
            stringbuffer.append("DL: ");
            stringbuffer.append(String.valueOf(DynamicLights.getCount()));
            stringbuffer.append(", ");
        }

        stringbuffer.append("OptiFine HD I7 SRC VERSION");
        String s = Shaders.getShaderPackName();
        if (s != null) {
            stringbuffer.append(", ");
            stringbuffer.append(s);
        }

        return stringbuffer.toString();
    }

    public static void initGameSettings(GameSettings settings) {
        if (gameSettings == null) {
            gameSettings = settings;
            desktopDisplayMode = Display.getDesktopDisplayMode();
            updateAvailableProcessors();
            ReflectorForge.putLaunchBlackboard("optifine.ForgeSplashCompatible", Boolean.TRUE);
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

    public static void checkInitialized() {
        if (!initialized) {
            if (Display.isCreated()) {
                initialized = true;
                checkOpenGlCaps();
                startVersionCheckThread();
            }
        }
    }

    private static void checkOpenGlCaps() {
        log("");
        log(getVersion());
        log("Build: " + getBuild());
        log("OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
        log("Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        log("VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        log("LWJGL: " + Sys.getVersion());
        openGlVersion = GL11.glGetString(GL11.GL_VERSION);
        openGlRenderer = GL11.glGetString(GL11.GL_RENDERER);
        openGlVendor = GL11.glGetString(GL11.GL_VENDOR);
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

        int i = TextureUtils.getGLMaximumTextureSize();
        dbg("Maximum texture size: " + i + "x" + i);
    }

    private static String getBuild() {
        try {
            InputStream inputstream = Config.class.getResourceAsStream("/buildof.txt");
            return inputstream == null ? null : readLines(inputstream)[0];
        } catch (Exception exception) {
            warn("" + exception.getClass().getName() + ": " + exception.getMessage());
            return null;
        }
    }

    public static boolean isFancyFogAvailable() {
        return fancyFogAvailable;
    }

    public static boolean isOcclusionAvailable() {
        return occlusionAvailable;
    }

    public static int getMinecraftVersionInt() {
        if (minecraftVersionInt < 0) {
            String[] astring = tokenize("1.8.8", ".");
            int i = 0;
            if (astring.length > 0) {
                i += 10000 * parseInt(astring[0], 0);
            }

            if (astring.length > 1) {
                i += 100 * parseInt(astring[1], 0);
            }

            if (astring.length > 2) {
                i += 1 * parseInt(astring[2], 0);
            }

            minecraftVersionInt = i;
        }

        return minecraftVersionInt;
    }

    public static String getOpenGlVersionString() {
        GlVersion glversion = getGlVersion();
        return "" + glversion.getMajor() + "." + glversion.getMinor() + "." + glversion.getRelease();
    }

    private static GlVersion getGlVersionLwjgl() {
        if (GLContext.getCapabilities().OpenGL44) {
            return new GlVersion(4, 4);
        } else if (GLContext.getCapabilities().OpenGL43) {
            return new GlVersion(4, 3);
        } else if (GLContext.getCapabilities().OpenGL42) {
            return new GlVersion(4, 2);
        } else if (GLContext.getCapabilities().OpenGL41) {
            return new GlVersion(4, 1);
        } else if (GLContext.getCapabilities().OpenGL40) {
            return new GlVersion(4, 0);
        } else if (GLContext.getCapabilities().OpenGL33) {
            return new GlVersion(3, 3);
        } else if (GLContext.getCapabilities().OpenGL32) {
            return new GlVersion(3, 2);
        } else if (GLContext.getCapabilities().OpenGL31) {
            return new GlVersion(3, 1);
        } else if (GLContext.getCapabilities().OpenGL30) {
            return new GlVersion(3, 0);
        } else if (GLContext.getCapabilities().OpenGL21) {
            return new GlVersion(2, 1);
        } else if (GLContext.getCapabilities().OpenGL20) {
            return new GlVersion(2, 0);
        } else if (GLContext.getCapabilities().OpenGL15) {
            return new GlVersion(1, 5);
        } else if (GLContext.getCapabilities().OpenGL14) {
            return new GlVersion(1, 4);
        } else if (GLContext.getCapabilities().OpenGL13) {
            return new GlVersion(1, 3);
        } else if (GLContext.getCapabilities().OpenGL12) {
            return new GlVersion(1, 2);
        } else {
            return GLContext.getCapabilities().OpenGL11 ? new GlVersion(1, 1) : new GlVersion(1, 0);
        }
    }

    public static GlVersion getGlVersion() {
        if (glVersion == null) {
            String s = GL11.glGetString(GL11.GL_VERSION);
            glVersion = parseGlVersion(s, null);
            if (glVersion == null) {
                glVersion = getGlVersionLwjgl();
            }

            if (glVersion == null) {
                glVersion = new GlVersion(1, 0);
            }
        }

        return glVersion;
    }

    public static GlVersion getGlslVersion() {
        if (glslVersion == null) {
            String s = GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);
            glslVersion = parseGlVersion(s, null);
            if (glslVersion == null) {
                glslVersion = new GlVersion(1, 10);
            }
        }

        return glslVersion;
    }

    public static GlVersion parseGlVersion(String versionString, GlVersion def) {
        try {
            if (versionString == null) {
                return def;
            }

            Pattern pattern = Pattern.compile("([0-9]+)\\.([0-9]+)(\\.([0-9]+))?(.+)?");
            Matcher matcher = pattern.matcher(versionString);
            if (!matcher.matches()) {
                return def;
            }

            int i = Integer.parseInt(matcher.group(1));
            int j = Integer.parseInt(matcher.group(2));
            int k = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0;
            String s = matcher.group(5);
            return new GlVersion(i, j, k, s);
        } catch (Exception exception) {
            exception.printStackTrace();
            return def;
        }
    }

    public static String[] getOpenGlExtensions() {
        if (openGlExtensions == null) {
            openGlExtensions = detectOpenGlExtensions();
        }

        return openGlExtensions;
    }

    private static String[] detectOpenGlExtensions() {
        try {
            GlVersion glversion = getGlVersion();
            if (glversion.getMajor() >= 3) {
                int i = GL11.glGetInteger(33309);
                if (i > 0) {
                    String[] astring = new String[i];

                    for (int j = 0; j < i; j++) {
                        astring[j] = GL30.glGetStringi(7939, j);
                    }

                    return astring;
                }
            }
        } catch (Exception exception1) {
            exception1.printStackTrace();
        }

        try {
            String s = GL11.glGetString(GL11.GL_EXTENSIONS);
            return s.split(" ");
        } catch (Exception exception) {
            exception.printStackTrace();
            return new String[0];
        }
    }

    public static void updateThreadPriorities() {
        updateAvailableProcessors();
        int i = 8;
        if (isSingleProcessor()) {
            if (isSmoothWorld()) {
                minecraftThread.setPriority(10);
                setThreadPriority("Server thread", 1);
            } else {
                minecraftThread.setPriority(5);
                setThreadPriority("Server thread", 5);
            }
        } else {
            minecraftThread.setPriority(10);
            setThreadPriority("Server thread", 5);
        }
    }

    private static void setThreadPriority(String prefix, int priority) {
        try {
            ThreadGroup threadgroup = Thread.currentThread().getThreadGroup();
            if (threadgroup == null) {
                return;
            }

            int i = (threadgroup.activeCount() + 10) * 2;
            Thread[] athread = new Thread[i];
            threadgroup.enumerate(athread, false);

            for (int j = 0; j < athread.length; j++) {
                Thread thread = athread[j];
                if (thread != null && thread.getName().startsWith(prefix)) {
                    thread.setPriority(priority);
                }
            }
        } catch (Throwable throwable) {
            warn(throwable.getClass().getName() + ": " + throwable.getMessage());
        }
    }

    public static boolean isMinecraftThread() {
        return Thread.currentThread() == minecraftThread;
    }

    private static void startVersionCheckThread() {
        VersionCheckThread versioncheckthread = new VersionCheckThread();
        versioncheckthread.start();
    }

    public static boolean isMipmaps() {
        return gameSettings.mipmapLevels > 0;
    }

    public static int getMipmapLevels() {
        return gameSettings.mipmapLevels;
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

    public static boolean isUseAlphaFunc() {
        float f = getAlphaFuncLevel();
        return f > DEF_ALPHA_FUNC_LEVEL + 1.0E-5F;
    }

    public static float getAlphaFuncLevel() {
        return DEF_ALPHA_FUNC_LEVEL;
    }

    public static boolean isFogFancy() {
        return !isFancyFogAvailable() ? false : gameSettings.ofFogType == 2;
    }

    public static boolean isFogFast() {
        return gameSettings.ofFogType == 1;
    }

    public static boolean isFogOff() {
        return gameSettings.ofFogType == 3;
    }

    public static float getFogStart() {
        return gameSettings.ofFogStart;
    }

    public static void dbg(String s) {
        LOGGER.info("[OptiFine] " + s);
    }

    public static void warn(String s) {
        LOGGER.warn("[OptiFine] " + s);
    }

    public static void error(String s) {
        LOGGER.error("[OptiFine] " + s);
    }

    public static void log(String s) {
        dbg(s);
    }

    public static int getUpdatesPerFrame() {
        return gameSettings.ofChunkUpdates;
    }

    public static boolean isDynamicUpdates() {
        return gameSettings.ofChunkUpdatesDynamic;
    }

    public static boolean isRainFancy() {
        return gameSettings.ofRain == 0 ? gameSettings.fancyGraphics : gameSettings.ofRain == 2;
    }

    public static boolean isRainOff() {
        return gameSettings.ofRain == 3;
    }

    public static boolean isCloudsFancy() {
        if (gameSettings.ofClouds != 0) {
            return gameSettings.ofClouds == 2;
        } else if (isShaders() && !Shaders.shaderPackClouds.isDefault()) {
            return Shaders.shaderPackClouds.isFancy();
        } else {
            return texturePackClouds != 0 ? texturePackClouds == 2 : gameSettings.fancyGraphics;
        }
    }

    public static boolean isCloudsOff() {
        if (gameSettings.ofClouds != 0) {
            return gameSettings.ofClouds == 3;
        } else if (isShaders() && !Shaders.shaderPackClouds.isDefault()) {
            return Shaders.shaderPackClouds.isOff();
        } else {
            return texturePackClouds != 0 ? texturePackClouds == 3 : false;
        }
    }

    public static void updateTexturePackClouds() {
        texturePackClouds = 0;
        IResourceManager iresourcemanager = getResourceManager();
        if (iresourcemanager != null) {
            try {
                InputStream inputstream = iresourcemanager.getResource(new ResourceLocation("mcpatcher/color.properties")).getInputStream();
                if (inputstream == null) {
                    return;
                }

                Properties properties = new Properties();
                properties.load(inputstream);
                inputstream.close();
                String s = properties.getProperty("clouds");
                if (s == null) {
                    return;
                }

                dbg("Texture pack clouds: " + s);
                s = s.toLowerCase();
                if (s.equals("fast")) {
                    texturePackClouds = 1;
                }

                if (s.equals("fancy")) {
                    texturePackClouds = 2;
                }

                if (s.equals("off")) {
                    texturePackClouds = 3;
                }
            } catch (Exception exception) {
            }
        }
    }

    public static ModelManager getModelManager() {
        return minecraft.getRenderItem().modelManager;
    }

    public static boolean isTreesFancy() {
        return gameSettings.ofTrees == 0 ? gameSettings.fancyGraphics : gameSettings.ofTrees != 1;
    }

    public static boolean isTreesSmart() {
        return gameSettings.ofTrees == 4;
    }

    public static boolean isCullFacesLeaves() {
        return gameSettings.ofTrees == 0 ? !gameSettings.fancyGraphics : gameSettings.ofTrees == 4;
    }

    public static boolean isDroppedItemsFancy() {
        return gameSettings.ofDroppedItems == 0 ? gameSettings.fancyGraphics : gameSettings.ofDroppedItems == 2;
    }

    public static int limit(int val, int min, int max) {
        if (val < min) {
            return min;
        } else {
            return val > max ? max : val;
        }
    }

    public static float limit(float val, float min, float max) {
        if (val < min) {
            return min;
        } else {
            return val > max ? max : val;
        }
    }

    public static double limit(double val, double min, double max) {
        if (val < min) {
            return min;
        } else {
            return val > max ? max : val;
        }
    }

    public static float limitTo1(float val) {
        if (val < 0.0F) {
            return 0.0F;
        } else {
            return val > 1.0F ? 1.0F : val;
        }
    }

    public static boolean isAnimatedWater() {
        return gameSettings.ofAnimatedWater != 2;
    }

    public static boolean isGeneratedWater() {
        return gameSettings.ofAnimatedWater == 1;
    }

    public static boolean isAnimatedPortal() {
        return gameSettings.ofAnimatedPortal;
    }

    public static boolean isAnimatedLava() {
        return gameSettings.ofAnimatedLava != 2;
    }

    public static boolean isGeneratedLava() {
        return gameSettings.ofAnimatedLava == 1;
    }

    public static boolean isAnimatedFire() {
        return gameSettings.ofAnimatedFire;
    }

    public static boolean isAnimatedRedstone() {
        return gameSettings.ofAnimatedRedstone;
    }

    public static boolean isAnimatedExplosion() {
        return gameSettings.ofAnimatedExplosion;
    }

    public static boolean isAnimatedFlame() {
        return gameSettings.ofAnimatedFlame;
    }

    public static boolean isAnimatedSmoke() {
        return gameSettings.ofAnimatedSmoke;
    }

    public static boolean isVoidParticles() {
        return gameSettings.ofVoidParticles;
    }

    public static boolean isWaterParticles() {
        return gameSettings.ofWaterParticles;
    }

    public static boolean isRainSplash() {
        return gameSettings.ofRainSplash;
    }

    public static boolean isPortalParticles() {
        return gameSettings.ofPortalParticles;
    }

    public static boolean isPotionParticles() {
        return gameSettings.ofPotionParticles;
    }

    public static boolean isFireworkParticles() {
        return gameSettings.ofFireworkParticles;
    }

    public static float getAmbientOcclusionLevel() {
        return isShaders() && Shaders.aoLevel >= 0.0F ? Shaders.aoLevel : gameSettings.ofAoLevel;
    }

    public static String listToString(List list) {
        return listToString(list, ", ");
    }

    public static String listToString(List list, String separator) {
        if (list == null) {
            return "";
        }

        StringBuffer stringbuffer = new StringBuffer(list.size() * 5);

        for (int i = 0; i < list.size(); i++) {
            Object object = list.get(i);
            if (i > 0) {
                stringbuffer.append(separator);
            }

            stringbuffer.append(String.valueOf(object));
        }

        return stringbuffer.toString();
    }

    public static String arrayToString(Object[] arr) {
        return arrayToString(arr, ", ");
    }

    public static String arrayToString(Object[] arr, String separator) {
        if (arr == null) {
            return "";
        }

        StringBuffer stringbuffer = new StringBuffer(arr.length * 5);

        for (int i = 0; i < arr.length; i++) {
            Object object = arr[i];
            if (i > 0) {
                stringbuffer.append(separator);
            }

            stringbuffer.append(String.valueOf(object));
        }

        return stringbuffer.toString();
    }

    public static String arrayToString(int[] arr) {
        return arrayToString(arr, ", ");
    }

    public static String arrayToString(int[] arr, String separator) {
        if (arr == null) {
            return "";
        }

        StringBuffer stringbuffer = new StringBuffer(arr.length * 5);

        for (int i = 0; i < arr.length; i++) {
            int j = arr[i];
            if (i > 0) {
                stringbuffer.append(separator);
            }

            stringbuffer.append(String.valueOf(j));
        }

        return stringbuffer.toString();
    }

    public static Minecraft getMinecraft() {
        return minecraft;
    }

    public static TextureManager getTextureManager() {
        return minecraft.getTextureManager();
    }

    public static IResourceManager getResourceManager() {
        return minecraft.getResourceManager();
    }

    public static InputStream getResourceStream(ResourceLocation location) throws IOException {
        return getResourceStream(minecraft.getResourceManager(), location);
    }

    public static InputStream getResourceStream(IResourceManager resourceManager, ResourceLocation location) throws IOException {
        IResource iresource = resourceManager.getResource(location);
        return iresource == null ? null : iresource.getInputStream();
    }

    public static IResource getResource(ResourceLocation location) throws IOException {
        return minecraft.getResourceManager().getResource(location);
    }

    public static boolean hasResource(ResourceLocation location) {
        IResourcePack iresourcepack = getDefiningResourcePack(location);
        return iresourcepack != null;
    }

    public static boolean hasResource(IResourceManager resourceManager, ResourceLocation location) {
        try {
            IResource iresource = resourceManager.getResource(location);
            return iresource != null;
        } catch (IOException ioexception) {
            return false;
        }
    }

    public static IResourcePack[] getResourcePacks() {
        ResourcePackRepository resourcepackrepository = minecraft.getResourcePackRepository();
        List list = resourcepackrepository.getRepositoryEntries();
        List list1 = new ArrayList();

        for (ResourcePackRepository.Entry resourcepackrepository$entry : (Iterable<ResourcePackRepository.Entry>) (Iterable) list) {
            list1.add(resourcepackrepository$entry.getResourcePack());
        }

        if (resourcepackrepository.getResourcePackInstance() != null) {
            list1.add(resourcepackrepository.getResourcePackInstance());
        }

        IResourcePack[] airesourcepack = ((IResourcePack[]) list1.toArray(new IResourcePack[list1.size()]));
        return airesourcepack;
    }

    public static String getResourcePackNames() {
        if (minecraft.getResourcePackRepository() == null) {
            return "";
        }

        IResourcePack[] airesourcepack = getResourcePacks();
        if (airesourcepack.length <= 0) {
            return getDefaultResourcePack().getPackName();
        }

        String[] astring = new String[airesourcepack.length];

        for (int i = 0; i < airesourcepack.length; i++) {
            astring[i] = airesourcepack[i].getPackName();
        }

        return arrayToString(astring);
    }

    public static DefaultResourcePack getDefaultResourcePack() {
        if (defaultResourcePackLazy == null) {
            Minecraft minecraft = Minecraft.getMinecraft();
            defaultResourcePackLazy = (DefaultResourcePack)Reflector.getFieldValue(minecraft, Reflector.Minecraft_defaultResourcePack);
            if (defaultResourcePackLazy == null) {
                ResourcePackRepository resourcepackrepository = minecraft.getResourcePackRepository();
                if (resourcepackrepository != null) {
                    defaultResourcePackLazy = (DefaultResourcePack)resourcepackrepository.rprDefaultResourcePack;
                }
            }
        }

        return defaultResourcePackLazy;
    }

    public static boolean isFromDefaultResourcePack(ResourceLocation loc) {
        IResourcePack iresourcepack = getDefiningResourcePack(loc);
        return iresourcepack == getDefaultResourcePack();
    }

    public static IResourcePack getDefiningResourcePack(ResourceLocation location) {
        ResourcePackRepository resourcepackrepository = minecraft.getResourcePackRepository();
        IResourcePack iresourcepack = resourcepackrepository.getResourcePackInstance();
        if (iresourcepack != null && iresourcepack.resourceExists(location)) {
            return iresourcepack;
        }

        List<ResourcePackRepository.Entry> list = (List<ResourcePackRepository.Entry>)Reflector.getFieldValue(
            resourcepackrepository, Reflector.ResourcePackRepository_repositoryEntries
        );
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                ResourcePackRepository.Entry resourcepackrepository$entry = list.get(i);
                IResourcePack iresourcepack1 = resourcepackrepository$entry.getResourcePack();
                if (iresourcepack1.resourceExists(location)) {
                    return iresourcepack1;
                }
            }
        }

        return getDefaultResourcePack().resourceExists(location) ? getDefaultResourcePack() : null;
    }

    public static RenderGlobal getRenderGlobal() {
        return minecraft.renderGlobal;
    }

    public static boolean isBetterGrass() {
        return gameSettings.ofBetterGrass != 3;
    }

    public static boolean isBetterGrassFancy() {
        return gameSettings.ofBetterGrass == 2;
    }

    public static boolean isWeatherEnabled() {
        return gameSettings.ofWeather;
    }

    public static boolean isSkyEnabled() {
        return gameSettings.ofSky;
    }

    public static boolean isSunMoonEnabled() {
        return gameSettings.ofSunMoon;
    }

    public static boolean isSunTexture() {
        return !isSunMoonEnabled() ? false : !isShaders() || Shaders.isSun();
    }

    public static boolean isMoonTexture() {
        return !isSunMoonEnabled() ? false : !isShaders() || Shaders.isMoon();
    }

    public static boolean isVignetteEnabled() {
        if (isShaders() && !Shaders.isVignette()) {
            return false;
        } else {
            return gameSettings.ofVignette == 0 ? gameSettings.fancyGraphics : gameSettings.ofVignette == 2;
        }
    }

    public static boolean isStarsEnabled() {
        return gameSettings.ofStars;
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();
        }
    }

    public static boolean isTimeDayOnly() {
        return gameSettings.ofTime == 1;
    }

    public static boolean isTimeDefault() {
        return gameSettings.ofTime == 0;
    }

    public static boolean isTimeNightOnly() {
        return gameSettings.ofTime == 2;
    }

    public static boolean isClearWater() {
        return gameSettings.ofClearWater;
    }

    public static int getAnisotropicFilterLevel() {
        return gameSettings.ofAfLevel;
    }

    public static boolean isAnisotropicFiltering() {
        return getAnisotropicFilterLevel() > 1;
    }

    public static int getAntialiasingLevel() {
        return antialiasingLevel;
    }

    public static boolean isAntialiasing() {
        return getAntialiasingLevel() > 0;
    }

    public static boolean isAntialiasingConfigured() {
        return getGameSettings().ofAaLevel > 0;
    }

    public static boolean isMultiTexture() {
        return getAnisotropicFilterLevel() > 1 ? true : getAntialiasingLevel() > 0;
    }

    public static boolean between(int val, int min, int max) {
        return val >= min && val <= max;
    }

    public static boolean isDrippingWaterLava() {
        return gameSettings.ofDrippingWaterLava;
    }

    public static boolean isBetterSnow() {
        return gameSettings.ofBetterSnow;
    }

    public static Dimension getFullscreenDimension() {
        if (desktopDisplayMode == null) {
            return null;
        }

        if (gameSettings == null) {
            return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
        }

        String s = gameSettings.ofFullscreenMode;
        if (s.equals("Default")) {
            return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
        }

        String[] astring = tokenize(s, " x");
        return astring.length < 2
            ? new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight())
            : new Dimension(parseInt(astring[0], -1), parseInt(astring[1], -1));
    }

    public static int parseInt(String str, int defVal) {
        try {
            if (str == null) {
                return defVal;
            }

            str = str.trim();
            return Integer.parseInt(str);
        } catch (NumberFormatException numberformatexception) {
            return defVal;
        }
    }

    public static float parseFloat(String str, float defVal) {
        try {
            if (str == null) {
                return defVal;
            }

            str = str.trim();
            return Float.parseFloat(str);
        } catch (NumberFormatException numberformatexception) {
            return defVal;
        }
    }

    public static boolean parseBoolean(String str, boolean defVal) {
        try {
            if (str == null) {
                return defVal;
            }

            str = str.trim();
            return Boolean.parseBoolean(str);
        } catch (NumberFormatException numberformatexception) {
            return defVal;
        }
    }

    public static String[] tokenize(String str, String delim) {
        StringTokenizer stringtokenizer = new StringTokenizer(str, delim);
        List list = new ArrayList();

        while (stringtokenizer.hasMoreTokens()) {
            String s = stringtokenizer.nextToken();
            list.add(s);
        }

        return ((String[]) list.toArray(new String[list.size()]));
    }

    public static DisplayMode getDesktopDisplayMode() {
        return desktopDisplayMode;
    }

    public static DisplayMode[] getDisplayModes() {
        if (displayModes == null) {
            try {
                DisplayMode[] adisplaymode = Display.getAvailableDisplayModes();
                Set<Dimension> set = getDisplayModeDimensions(adisplaymode);
                List list = new ArrayList();

                for (Dimension dimension : set) {
                    DisplayMode[] adisplaymode1 = getDisplayModes(adisplaymode, dimension);
                    DisplayMode displaymode = getDisplayMode(adisplaymode1, desktopDisplayMode);
                    if (displaymode != null) {
                        list.add(displaymode);
                    }
                }

                DisplayMode[] adisplaymode2 = ((DisplayMode[]) list.toArray(new DisplayMode[list.size()]));
                Arrays.sort(adisplaymode2, new DisplayModeComparator());
                return adisplaymode2;
            } catch (Exception exception) {
                exception.printStackTrace();
                displayModes = new DisplayMode[]{desktopDisplayMode};
            }
        }

        return displayModes;
    }

    public static DisplayMode getLargestDisplayMode() {
        DisplayMode[] adisplaymode = getDisplayModes();
        if (adisplaymode != null && adisplaymode.length >= 1) {
            DisplayMode displaymode = adisplaymode[adisplaymode.length - 1];
            if (desktopDisplayMode.getWidth() > displaymode.getWidth()) {
                return desktopDisplayMode;
            } else {
                return desktopDisplayMode.getWidth() == displaymode.getWidth() && desktopDisplayMode.getHeight() > displaymode.getHeight()
                    ? desktopDisplayMode
                    : displaymode;
            }
        } else {
            return desktopDisplayMode;
        }
    }

    private static Set<Dimension> getDisplayModeDimensions(DisplayMode[] modes) {
        Set<Dimension> set = new HashSet<>();

        for (int i = 0; i < modes.length; i++) {
            DisplayMode displaymode = modes[i];
            Dimension dimension = new Dimension(displaymode.getWidth(), displaymode.getHeight());
            set.add(dimension);
        }

        return set;
    }

    private static DisplayMode[] getDisplayModes(DisplayMode[] modes, Dimension dim) {
        List list = new ArrayList();

        for (int i = 0; i < modes.length; i++) {
            DisplayMode displaymode = modes[i];
            if (displaymode.getWidth() == dim.getWidth() && displaymode.getHeight() == dim.getHeight()) {
                list.add(displaymode);
            }
        }

        return ((DisplayMode[]) list.toArray(new DisplayMode[list.size()]));
    }

    private static DisplayMode getDisplayMode(DisplayMode[] modes, DisplayMode desktopMode) {
        if (desktopMode != null) {
            for (int i = 0; i < modes.length; i++) {
                DisplayMode displaymode = modes[i];
                if (displaymode.getBitsPerPixel() == desktopMode.getBitsPerPixel() && displaymode.getFrequency() == desktopMode.getFrequency()) {
                    return displaymode;
                }
            }
        }

        if (modes.length <= 0) {
            return null;
        }

        Arrays.sort(modes, new DisplayModeComparator());
        return modes[modes.length - 1];
    }

    public static String[] getDisplayModeNames() {
        DisplayMode[] adisplaymode = getDisplayModes();
        String[] astring = new String[adisplaymode.length];

        for (int i = 0; i < adisplaymode.length; i++) {
            DisplayMode displaymode = adisplaymode[i];
            String s = "" + displaymode.getWidth() + "x" + displaymode.getHeight();
            astring[i] = s;
        }

        return astring;
    }

    public static DisplayMode getDisplayMode(Dimension dim) throws LWJGLException {
        DisplayMode[] adisplaymode = getDisplayModes();

        for (int i = 0; i < adisplaymode.length; i++) {
            DisplayMode displaymode = adisplaymode[i];
            if (displaymode.getWidth() == dim.width && displaymode.getHeight() == dim.height) {
                return displaymode;
            }
        }

        return desktopDisplayMode;
    }

    public static boolean isAnimatedTerrain() {
        return gameSettings.ofAnimatedTerrain;
    }

    public static boolean isAnimatedTextures() {
        return gameSettings.ofAnimatedTextures;
    }

    public static boolean isSwampColors() {
        return gameSettings.ofSwampColors;
    }

    public static boolean isRandomMobs() {
        return gameSettings.ofRandomMobs;
    }

    public static void checkGlError(String loc) {
        int i = GL11.glGetError();
        if (i != 0) {
            String s = GLU.gluErrorString(i);
            error("OpenGlError: " + i + " (" + s + "), at: " + loc);
        }
    }

    public static boolean isSmoothBiomes() {
        return gameSettings.ofSmoothBiomes;
    }

    public static boolean isCustomColors() {
        return gameSettings.ofCustomColors;
    }

    public static boolean isCustomSky() {
        return gameSettings.ofCustomSky;
    }

    public static boolean isCustomFonts() {
        return gameSettings.ofCustomFonts;
    }

    public static boolean isShowCapes() {
        return gameSettings.ofShowCapes;
    }

    public static boolean isConnectedTextures() {
        return gameSettings.ofConnectedTextures != 3;
    }

    public static boolean isNaturalTextures() {
        return gameSettings.ofNaturalTextures;
    }

    public static boolean isConnectedTexturesFancy() {
        return gameSettings.ofConnectedTextures == 2;
    }

    public static boolean isFastRender() {
        return gameSettings.ofFastRender;
    }

    public static boolean isTranslucentBlocksFancy() {
        return gameSettings.ofTranslucentBlocks == 0 ? gameSettings.fancyGraphics : gameSettings.ofTranslucentBlocks == 2;
    }

    public static boolean isShaders() {
        return Shaders.shaderPackLoaded;
    }

    public static String[] readLines(File file) throws IOException {
        FileInputStream fileinputstream = new FileInputStream(file);
        return readLines(fileinputstream);
    }

    public static String[] readLines(InputStream is) throws IOException {
        List list = new ArrayList();
        InputStreamReader inputstreamreader = new InputStreamReader(is, "ASCII");
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

        while (true) {
            String s = bufferedreader.readLine();
            if (s == null) {
                return ((String[]) list.toArray(new String[list.size()]));
            }

            list.add(s);
        }
    }

    public static String readFile(File file) throws IOException {
        FileInputStream fileinputstream = new FileInputStream(file);
        return readInputStream(fileinputstream, "ASCII");
    }

    public static String readInputStream(InputStream in) throws IOException {
        return readInputStream(in, "ASCII");
    }

    public static String readInputStream(InputStream in, String encoding) throws IOException {
        InputStreamReader inputstreamreader = new InputStreamReader(in, encoding);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        StringBuffer stringbuffer = new StringBuffer();

        while (true) {
            String s = bufferedreader.readLine();
            if (s == null) {
                return stringbuffer.toString();
            }

            stringbuffer.append(s);
            stringbuffer.append("\n");
        }
    }

    public static byte[] readAll(InputStream is) throws IOException {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        byte[] abyte = new byte[1024];

        while (true) {
            int i = is.read(abyte);
            if (i < 0) {
                is.close();
                return bytearrayoutputstream.toByteArray();
            }

            bytearrayoutputstream.write(abyte, 0, i);
        }
    }

    public static GameSettings getGameSettings() {
        return gameSettings;
    }

    public static String getNewRelease() {
        return newRelease;
    }

    public static void setNewRelease(String newRelease) {
        Config.newRelease = newRelease;
    }

    public static int compareRelease(String rel1, String rel2) {
        String[] astring = splitRelease(rel1);
        String[] astring1 = splitRelease(rel2);
        String s = astring[0];
        String s1 = astring1[0];
        if (!s.equals(s1)) {
            return s.compareTo(s1);
        }

        int i = parseInt(astring[1], -1);
        int j = parseInt(astring1[1], -1);
        if (i != j) {
            return i - j;
        }

        String s2 = astring[2];
        String s3 = astring1[2];
        if (!s2.equals(s3)) {
            if (s2.isEmpty()) {
                return 1;
            }

            if (s3.isEmpty()) {
                return -1;
            }
        }

        return s2.compareTo(s3);
    }

    private static String[] splitRelease(String relStr) {
        if (relStr != null && relStr.length() > 0) {
            Pattern pattern = Pattern.compile("([A-Z])([0-9]+)(.*)");
            Matcher matcher = pattern.matcher(relStr);
            if (!matcher.matches()) {
                return new String[]{"", "", ""};
            }

            String s = normalize(matcher.group(1));
            String s1 = normalize(matcher.group(2));
            String s2 = normalize(matcher.group(3));
            return new String[]{s, s1, s2};
        } else {
            return new String[]{"", "", ""};
        }
    }

    public static int intHash(int x) {
        x = x ^ 61 ^ x >> 16;
        x += x << 3;
        x ^= x >> 4;
        x *= 668265261;
        return x ^ x >> 15;
    }

    public static int getRandom(BlockPos blockPos, int face) {
        int i = intHash(face + 37);
        i = intHash(i + blockPos.getX());
        i = intHash(i + blockPos.getZ());
        return intHash(i + blockPos.getY());
    }

    public static WorldServer getWorldServer() {
        World world = minecraft.theWorld;
        if (world == null) {
            return null;
        }

        if (!minecraft.isIntegratedServerRunning()) {
            return null;
        }

        IntegratedServer integratedserver = minecraft.getIntegratedServer();
        if (integratedserver == null) {
            return null;
        }

        WorldProvider worldprovider = world.provider;
        if (worldprovider == null) {
            return null;
        }

        int i = worldprovider.getDimensionId();

        try {
            return integratedserver.worldServerForDimension(i);
        } catch (NullPointerException nullpointerexception) {
            return null;
        }
    }

    public static Entity getIntegratedServerEntity(UUID uuid) {
        WorldServer worldserver = getWorldServer();
        if (worldserver == null) {
            return null;
        }

        Entity entity = worldserver.getEntityFromUuid(uuid);
        return entity == null ? null : entity;
    }

    public static int getAvailableProcessors() {
        return availableProcessors;
    }

    public static void updateAvailableProcessors() {
        availableProcessors = Runtime.getRuntime().availableProcessors();
    }

    public static boolean isSingleProcessor() {
        return getAvailableProcessors() <= 1;
    }

    public static boolean isSmoothWorld() {
        return gameSettings.ofSmoothWorld;
    }

    public static boolean isLazyChunkLoading() {
        return gameSettings.ofLazyChunkLoading;
    }

    public static boolean isDynamicFov() {
        return gameSettings.ofDynamicFov;
    }

    public static boolean isAlternateBlocks() {
        return gameSettings.allowBlockAlternatives;
    }

    public static int getChunkViewDistance() {
        return gameSettings == null ? 10 : gameSettings.renderDistanceChunks;
    }

    public static boolean equals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        } else {
            return o1 == null ? false : o1.equals(o2);
        }
    }

    public static boolean equalsOne(Object a, Object[] bs) {
        if (bs == null) {
            return false;
        }

        for (int i = 0; i < bs.length; i++) {
            Object object = bs[i];
            if (equals(a, object)) {
                return true;
            }
        }

        return false;
    }

    public static boolean equalsOne(int val, int[] vals) {
        for (int i = 0; i < vals.length; i++) {
            if (vals[i] == val) {
                return true;
            }
        }

        return false;
    }

    public static boolean isSameOne(Object a, Object[] bs) {
        if (bs == null) {
            return false;
        }

        for (int i = 0; i < bs.length; i++) {
            Object object = bs[i];
            if (a == object) {
                return true;
            }
        }

        return false;
    }

    public static String normalize(String s) {
        return s == null ? "" : s;
    }

    public static void checkDisplaySettings() {
        int i = getAntialiasingLevel();
        if (i > 0) {
            DisplayMode displaymode = Display.getDisplayMode();
            dbg("FSAA Samples: " + i);

            try {
                Display.destroy();
                Display.setDisplayMode(displaymode);
                Display.create(new PixelFormat().withDepthBits(24).withSamples(i));
                Display.setResizable(false);
                Display.setResizable(true);
            } catch (LWJGLException lwjglexception2) {
                warn("Error setting FSAA: " + i + "x");
                lwjglexception2.printStackTrace();

                try {
                    Display.setDisplayMode(displaymode);
                    Display.create(new PixelFormat().withDepthBits(24));
                    Display.setResizable(false);
                    Display.setResizable(true);
                } catch (LWJGLException lwjglexception1) {
                    lwjglexception1.printStackTrace();

                    try {
                        Display.setDisplayMode(displaymode);
                        Display.create();
                        Display.setResizable(false);
                        Display.setResizable(true);
                    } catch (LWJGLException lwjglexception) {
                        lwjglexception.printStackTrace();
                    }
                }
            }

            if (!Minecraft.isRunningOnMac && getDefaultResourcePack() != null) {
                InputStream inputstream = null;
                InputStream inputstream1 = null;

                try {
                    inputstream = getDefaultResourcePack().getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
                    inputstream1 = getDefaultResourcePack().getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));
                    if (inputstream != null && inputstream1 != null) {
                        Display.setIcon(new ByteBuffer[]{readIconImage(inputstream), readIconImage(inputstream1)});
                    }
                } catch (IOException ioexception) {
                    warn("Error setting window icon: " + ioexception.getClass().getName() + ": " + ioexception.getMessage());
                } finally {
                    IOUtils.closeQuietly(inputstream);
                    IOUtils.closeQuietly(inputstream1);
                }
            }
        }
    }

    private static ByteBuffer readIconImage(InputStream is) throws IOException {
        BufferedImage bufferedimage = ImageIO.read(is);
        int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), (int[])null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);
        int[] aint1 = aint;
        int i = aint.length;

        for (int j = 0; j < i; j++) {
            int k = aint1[j];
            bytebuffer.putInt(k << 8 | k >> 24 & 0xFF);
        }

        ((Buffer)bytebuffer).flip();
        return bytebuffer;
    }

    public static void checkDisplayMode() {
        try {
            if (minecraft.isFullScreen()) {
                if (fullscreenModeChecked) {
                    return;
                }

                fullscreenModeChecked = true;
                desktopModeChecked = false;
                DisplayMode displaymode = Display.getDisplayMode();
                Dimension dimension = getFullscreenDimension();
                if (dimension == null) {
                    return;
                }

                if (displaymode.getWidth() == dimension.width && displaymode.getHeight() == dimension.height) {
                    return;
                }

                DisplayMode displaymode1 = getDisplayMode(dimension);
                if (displaymode1 == null) {
                    return;
                }

                Display.setDisplayMode(displaymode1);
                minecraft.displayWidth = Display.getDisplayMode().getWidth();
                minecraft.displayHeight = Display.getDisplayMode().getHeight();
                if (minecraft.displayWidth <= 0) {
                    minecraft.displayWidth = 1;
                }

                if (minecraft.displayHeight <= 0) {
                    minecraft.displayHeight = 1;
                }

                if (minecraft.currentScreen != null) {
                    ScaledResolution scaledresolution = new ScaledResolution(minecraft);
                    int i = scaledresolution.getScaledWidth();
                    int j = scaledresolution.getScaledHeight();
                    minecraft.currentScreen.setWorldAndResolution(minecraft, i, j);
                }

                minecraft.loadingScreen = new LoadingScreenRenderer(minecraft);
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

    public static void updateFramebufferSize() {
        minecraft.getFramebuffer().createBindFramebuffer(minecraft.displayWidth, minecraft.displayHeight);
        if (minecraft.entityRenderer != null) {
            minecraft.entityRenderer.updateShaderGroupSize(minecraft.displayWidth, minecraft.displayHeight);
        }
    }

    public static Object[] addObjectToArray(Object[] arr, Object obj) {
        if (arr == null) {
            throw new NullPointerException("The given array is NULL");
        }

        int i = arr.length;
        int j = i + 1;
        Object[] aobject = (Object[])Array.newInstance(arr.getClass().getComponentType(), j);
        System.arraycopy(arr, 0, aobject, 0, i);
        aobject[i] = obj;
        return aobject;
    }

    public static Object[] addObjectToArray(Object[] arr, Object obj, int index) {
        List list = new ArrayList<>(Arrays.asList(arr));
        list.add(index, obj);
        Object[] aobject = (Object[])Array.newInstance(arr.getClass().getComponentType(), list.size());
        return list.toArray(aobject);
    }

    public static Object[] addObjectsToArray(Object[] arr, Object[] objs) {
        if (arr == null) {
            throw new NullPointerException("The given array is NULL");
        }

        if (objs.length == 0) {
            return arr;
        }

        int i = arr.length;
        int j = i + objs.length;
        Object[] aobject = (Object[])Array.newInstance(arr.getClass().getComponentType(), j);
        System.arraycopy(arr, 0, aobject, 0, i);
        System.arraycopy(objs, 0, aobject, i, objs.length);
        return aobject;
    }

    public static boolean isCustomItems() {
        return gameSettings.ofCustomItems;
    }

    public static void drawFps() {
        int i = Minecraft.getDebugFPS();
        String s = getUpdates(minecraft.debug);
        int j = minecraft.renderGlobal.getCountActiveRenderers();
        int k = minecraft.renderGlobal.getCountEntitiesRendered();
        int l = minecraft.renderGlobal.getCountTileEntitiesRendered();
        String s1 = "" + i + " fps, C: " + j + ", E: " + k + "+" + l + ", U: " + s;
        minecraft.fontRendererObj.drawString(s1, 2, 2, -2039584);
    }

    private static String getUpdates(String str) {
        int i = str.indexOf(40);
        if (i < 0) {
            return "";
        }

        int j = str.indexOf(32, i);
        return j < 0 ? "" : str.substring(i + 1, j);
    }

    public static int getBitsOs() {
        String s = System.getenv("ProgramFiles(X86)");
        return s != null ? 64 : 32;
    }

    public static int getBitsJre() {
        String[] astring = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

        for (int i = 0; i < astring.length; i++) {
            String s = astring[i];
            String s1 = System.getProperty(s);
            if (s1 != null && s1.contains("64")) {
                return 64;
            }
        }

        return 32;
    }

    public static boolean isNotify64BitJava() {
        return notify64BitJava;
    }

    public static void setNotify64BitJava(boolean flag) {
        notify64BitJava = flag;
    }

    public static boolean isConnectedModels() {
        return false;
    }

    public static void showGuiMessage(String line1, String line2) {
        GuiMessage guimessage = new GuiMessage(minecraft.currentScreen, line1, line2);
        minecraft.displayGuiScreen(guimessage);
    }

    public static int[] addIntToArray(int[] intArray, int intValue) {
        return addIntsToArray(intArray, new int[]{intValue});
    }

    public static int[] addIntsToArray(int[] intArray, int[] copyFrom) {
        if (intArray != null && copyFrom != null) {
            int i = intArray.length;
            int j = i + copyFrom.length;
            int[] aint = new int[j];
            System.arraycopy(intArray, 0, aint, 0, i);

            for (int k = 0; k < copyFrom.length; k++) {
                aint[k + i] = copyFrom[k];
            }

            return aint;
        } else {
            throw new NullPointerException("The given array is NULL");
        }
    }

    public static DynamicTexture getMojangLogoTexture(DynamicTexture texDefault) {
        try {
            ResourceLocation resourcelocation = new ResourceLocation("textures/gui/title/mojang.png");
            InputStream inputstream = getResourceStream(resourcelocation);
            if (inputstream == null) {
                return texDefault;
            }

            BufferedImage bufferedimage = ImageIO.read(inputstream);
            return bufferedimage == null ? texDefault : new DynamicTexture(bufferedimage);
        } catch (Exception exception) {
            warn(exception.getClass().getName() + ": " + exception.getMessage());
            return texDefault;
        }
    }

    public static void writeFile(File file, String str) throws IOException {
        FileOutputStream fileoutputstream = new FileOutputStream(file);
        byte[] abyte = str.getBytes("ASCII");
        fileoutputstream.write(abyte);
        fileoutputstream.close();
    }

    public static TextureMap getTextureMap() {
        return getMinecraft().getTextureMapBlocks();
    }

    public static boolean isDynamicLights() {
        return gameSettings.ofDynamicLights != 3;
    }

    public static boolean isDynamicLightsFast() {
        return gameSettings.ofDynamicLights == 1;
    }

    public static boolean isDynamicHandLight() {
        if (!isDynamicLights()) {
            return false;
        } else {
            return isShaders() ? Shaders.isDynamicHandLight() : true;
        }
    }

    public static boolean isCustomGuis() {
        return gameSettings.ofCustomGuis;
    }

    public static int[] toPrimitive(Integer[] arr) {
        if (arr == null) {
            return null;
        }

        if (arr.length == 0) {
            return new int[0];
        }

        int[] aint = new int[arr.length];

        for (int i = 0; i < aint.length; i++) {
            aint[i] = arr[i];
        }

        return aint;
    }
}
