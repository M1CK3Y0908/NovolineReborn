package net.minecraft.client.settings;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.optifine.ClearWater;
import net.optifine.CustomColors;
import net.optifine.CustomGuis;
import net.optifine.CustomSky;
import net.optifine.DynamicLights;
import net.optifine.Lang;
import net.optifine.NaturalTextures;
import net.optifine.RandomEntities;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import net.optifine.util.KeyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class GameSettings {
   public float gammaSetting;
   public static final String DEFAULT = "Default";
   public float mouseSensitivity = 0.5F;
   public int renderDistanceChunks = -1;
   public boolean viewBobbing = true;
   public boolean fboEnable = true;
   public int limitFramerate = 120;
   public int clouds = 2;
   public boolean fancyGraphics = true;
   public int ambientOcclusion = 2;
   public List<String> resourcePacks = Lists.newArrayList();
   private static final Logger logger = LogManager.getLogger();
   public List<String> incompatibleResourcePacks = Lists.newArrayList();
   public net.minecraft.entity.player.EntityPlayer.EnumChatVisibility chatVisibility = net.minecraft.entity.player.EntityPlayer.EnumChatVisibility.FULL;
   public boolean chatColours = true;
   public boolean chatLinks = true;
   public static final int SMART = 4;
   private static final Gson gson = new Gson();
   public boolean chatLinksPrompt = true;
   private static final ParameterizedType typeListString = new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class};
      }

      @Override
      public Type getRawType() {
         return List.class;
      }

      @Override
      public Type getOwnerType() {
         return null;
      }
   };
   public float chatOpacity = 1.0F;
   public boolean showDebugInfo;
   private static final String[] GUISCALES = new String[]{
      "options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"
   };
   public boolean fullScreen = true;
   public boolean enableVsync = true;
   public boolean useVbo = false;
   public boolean allowBlockAlternatives = true;
   public boolean reducedDebugInfo = false;
   public boolean pauseOnLostFocus = true;
   private final Set<EnumPlayerModelParts> setModelParts = Sets.newHashSet(EnumPlayerModelParts.values());
   public boolean heldItemTooltips = true;
   public float chatScale = 1.0F;
   public float chatWidth = 1.0F;
   public float chatHeightUnfocused = 0.44366196F;
   public float chatHeightFocused = 1.0F;
   public boolean showInventoryAchievementHint = true;
   public int mipmapLevels = 4;
   private Map<SoundCategory, Float> mapSoundLevels = Maps.newEnumMap(SoundCategory.class);
   public float streamBytesPerPixel = 0.5F;
   public float streamMicVolume = 1.0F;
   public float streamSystemVolume = 1.0F;
   public float streamKbps = 0.5412844F;
   private static final String[] PARTICLES = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
   public float streamFps = 0.31690142F;
   public int streamCompression = 1;
   public boolean useNativeTransport = true;
   public String streamPreferredServer = "";
   public boolean fullscreenOption;
   public boolean touchscreen;
   public boolean hideGUI;
   public int streamChatEnabled = 0;
   public int streamChatUserFilter = 0;
   public int streamMicToggleBehavior = 0;
   public boolean entityShadows = true;
   public boolean entityShadowsOption = true;
   public boolean snooperEnabled = true;
   public KeyBinding keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
   public KeyBinding keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
   public KeyBinding keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
   public static final int intValue5 = 0;
   public KeyBinding keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
   public KeyBinding keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
   private static final String[] AMBIENT_OCCLUSIONS = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
   public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
   public boolean showLagometer;
   public KeyBinding keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.movement");
   public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
   public boolean showDebugProfilerChart;
   public boolean debugCamEnable;
   public KeyBinding keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
   public boolean anaglyph;
   public KeyBinding keyBindSwapItem = new KeyBinding("key.drop", 16, "key.categories.gameplay");
   public KeyBinding keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
   public KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
   private static final String[] CLOUDS_TYPES = new String[]{
      "options.stream.compression.low", "options.stream.compression.medium", "options.stream.compression.high"
   };
   public KeyBinding keyBinding = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
   public KeyBinding keyBindChat = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
   public KeyBinding keyBindPlayerList = new KeyBinding("key.command", 53, "key.categories.multiplayer");
   public KeyBinding keyBindCommand = new KeyBinding("key.screenshot", 60, "key.categories.misc");
   public KeyBinding keyBindScreenshot = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
   public KeyBinding keyBindTogglePerspective = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
   public KeyBinding keyBindSmoothCamera = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
   public KeyBinding keyBinding2 = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
   public KeyBinding keyBindFullscreen = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
   public KeyBinding keyBinding3 = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
   public KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
   public KeyBinding keyBinding4 = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
   public KeyBinding[] keyBindsHotbar = new KeyBinding[]{
      new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"),
      new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"),
      new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"),
      new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"),
      new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"),
      new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"),
      new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"),
      new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"),
      new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")
   };
   public int ofFogType = 1;
   public float ofFogStart = 0.8F;
   private static final String[] stringArray = new String[]{
      "options.stream.chat.enabled.streaming", "options.stream.chat.enabled.always", "options.stream.chat.enabled.never"
   };
   public int ofMipmapType = 0;
   public boolean ofOcclusionFancy = false;
   public boolean ofSmoothFps = false;
   public static final int intValue6 = 1;
   public boolean hideServerAddress;
   public boolean ofSmoothWorld = Config.isSingleProcessor();
   public boolean ofLazyChunkLoading = Config.isSingleProcessor();
   public boolean ofRenderRegions = false;
   public int overrideHeight;
   public boolean ofSmartAnimations = false;
   public float ofAoLevel = 1.0F;
   public int ofAaLevel = 0;
   public int ofAfLevel = 1;
   public int ofClouds = 0;
   public float ofCloudsHeight = 0.0F;
   public int ofTrees = 0;
   public int ofRain = 0;
   public int ofDroppedItems = 0;
   public int ofBetterGrass = 3;
   public int ofAutoSaveTicks = 4000;
   public boolean ofLagometer = false;
   public static final int intValue7 = 1;
   public float saturation;
   public boolean ofProfiler = false;
   private static final String[] stringArray2 = new String[]{
      "options.stream.chat.userFilter.all", "options.stream.chat.userFilter.subs", "options.stream.chat.userFilter.mods"
   };
   public boolean ofShowFps = false;
   public boolean ofWeather = true;
   public boolean ofSky = true;
   public boolean ofStars = true;
   public boolean ofSunMoon = true;
   public int ofVignette = 0;
   public int ofChunkUpdates = 1;
   public boolean ofChunkUpdatesDynamic = false;
   public int ofTime = 0;
   public boolean ofClearWater = false;
   public boolean invertMouse;
   public boolean ofBetterSnow = false;
   public String ofFullscreenMode = "Default";
   public boolean ofSwampColors = true;
   public static final int intValue8 = 0;
   public boolean ofRandomEntities = true;
   public boolean ofSmoothBiomes = true;
   public boolean ofCustomFonts = true;
   public boolean ofCustomColors = true;
   public boolean ofCustomSky = true;
   public boolean ofShowCapes = true;
   public int ofConnectedTextures = 2;
   public int thirdPersonView;
   public boolean ofCustomItems = true;
   public boolean ofNaturalTextures = false;
   public boolean ofEmissiveTextures = true;
   public int overrideWidth;
   public boolean ofFastMath = false;
   public boolean ofFastRender = false;
   public boolean smoothCamera;
   public int ofTranslucentBlocks = 0;
   public boolean ofDynamicFov = true;
   public boolean ofAlternateBlocks = true;
   public int ofDynamicLights = 3;
   public boolean ofCustomEntityModels = true;
   public boolean ofCustomGuis = true;
   public boolean advancedItemTooltips;
   public boolean ofShowGlErrors = true;
   public int ofScreenshotSize = 1;
   private static final String[] stringArray3 = new String[]{"options.stream.mic_toggle.mute", "options.stream.mic_toggle.talk"};
   public int ofAnimatedWater = 0;
   public int ofAnimatedLava = 0;
   public boolean ofAnimatedFire = true;
   public boolean ofAnimatedPortal = true;
   private static final String[] stringArray4 = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
   public boolean ofAnimatedRedstone = true;
   public boolean ofAnimatedExplosion = true;
   private static final int[] OF_TREES_VALUES = new int[]{0, 1, 4, 2};
   public boolean ofAnimatedFlame = true;
   public boolean ofAnimatedSmoke = true;
   public static final int OFF = 3;
   public int particleSetting;
   public boolean ofVoidParticles = true;
   public boolean ofWaterParticles = true;
   public boolean ofRainSplash = true;
   public boolean ofPortalParticles = true;
   public boolean ofPotionParticles = true;
   public boolean ofFireworkParticles = true;
   public boolean ofDrippingWaterLava = true;
   public boolean ofAnimatedTerrain = true;
   public boolean ofAnimatedTextures = true;
   public static final int intValue9 = 2;
   public static final int intValue10 = 2;
   public KeyBinding[] keyBindings = ArrayUtils.addAll(
      new KeyBinding[]{
         this.keyBindAttack,
         this.keyBindUseItem,
         this.keyBindForward,
         this.keyBindLeft,
         this.keyBindBack,
         this.keyBindRight,
         this.keyBindJump,
         this.keyBindSneak,
         this.keyBindSprint,
         this.keyBindSwapItem,
         this.keyBindInventory,
         this.keyBinding,
         this.keyBindChat,
         this.keyBindPickBlock,
         this.keyBindPlayerList,
         this.keyBindCommand,
         this.keyBindScreenshot,
         this.keyBindTogglePerspective,
         this.keyBindFullscreen,
         this.keyBinding3,
         this.keyBindSpectatorOutlines,
         this.keyBinding4,
         this.keyBindSmoothCamera,
         this.keyBinding2
      },
      this.keyBindsHotbar
   );
   private static final int[] OF_DYNAMIC_LIGHTS = new int[]{3, 1, 2};
   public EnumDifficulty difficulty = EnumDifficulty.NORMAL;
   public String lastServer = "";
   public float fovSetting = 70.0F;
   public String language = "en_US";
   public boolean forceUnicodeFont = false;
   protected Minecraft mc;
   private File optionsFile;
   private File optionsFileOF;
   public int guiScale;
   private static final String[] KEYS_DYNAMIC_LIGHTS = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
   public KeyBinding ofKeyBindZoom;

   private void handleOptions(GameSettings.Options options2) {
      if (options2 == GameSettings.Options.FOG_FANCY) {
         switch (this.ofFogType) {
            case 1:
               this.ofFogType = 2;
               if (!Config.isFancyFogAvailable()) {
                  this.ofFogType = 3;
               }
               break;
            case 2:
               this.ofFogType = 3;
               break;
            case 3:
               this.ofFogType = 1;
               break;
            default:
               this.ofFogType = 1;
         }
      }

      if (options2 == GameSettings.Options.FOG_START) {
         this.ofFogStart += 0.2F;
         if (this.ofFogStart > 0.81F) {
            this.ofFogStart = 0.2F;
         }
      }

      if (options2 == GameSettings.Options.SMOOTH_FPS) {
         this.ofSmoothFps = !this.ofSmoothFps;
      }

      if (options2 == GameSettings.Options.SMOOTH_WORLD) {
         this.ofSmoothWorld = !this.ofSmoothWorld;
         Config.updateThreadPriorities();
      }

      if (options2 == GameSettings.Options.CLOUDS) {
         this.ofClouds++;
         if (this.ofClouds > 3) {
            this.ofClouds = 0;
         }

         this.updateRenderClouds();
         this.mc.renderGlobal.resetClouds();
      }

      if (options2 == GameSettings.Options.TREES) {
         this.ofTrees = cycleValue(this.ofTrees, OF_TREES_VALUES);
         this.mc.renderGlobal.loadRenderers();
      }

      if (options2 == GameSettings.Options.DROPPED_ITEMS) {
         this.ofDroppedItems++;
         if (this.ofDroppedItems > 2) {
            this.ofDroppedItems = 0;
         }
      }

      if (options2 == GameSettings.Options.RAIN) {
         this.ofRain++;
         if (this.ofRain > 3) {
            this.ofRain = 0;
         }
      }

      if (options2 == GameSettings.Options.ANIMATED_WATER) {
         this.ofAnimatedWater++;
         if (this.ofAnimatedWater == 1) {
            this.ofAnimatedWater++;
         }

         if (this.ofAnimatedWater > 2) {
            this.ofAnimatedWater = 0;
         }
      }

      if (options2 == GameSettings.Options.ANIMATED_LAVA) {
         this.ofAnimatedLava++;
         if (this.ofAnimatedLava == 1) {
            this.ofAnimatedLava++;
         }

         if (this.ofAnimatedLava > 2) {
            this.ofAnimatedLava = 0;
         }
      }

      if (options2 == GameSettings.Options.ANIMATED_FIRE) {
         this.ofAnimatedFire = !this.ofAnimatedFire;
      }

      if (options2 == GameSettings.Options.ANIMATED_PORTAL) {
         this.ofAnimatedPortal = !this.ofAnimatedPortal;
      }

      if (options2 == GameSettings.Options.ANIMATED_REDSTONE) {
         this.ofAnimatedRedstone = !this.ofAnimatedRedstone;
      }

      if (options2 == GameSettings.Options.ANIMATED_EXPLOSION) {
         this.ofAnimatedExplosion = !this.ofAnimatedExplosion;
      }

      if (options2 == GameSettings.Options.ANIMATED_FLAME) {
         this.ofAnimatedFlame = !this.ofAnimatedFlame;
      }

      if (options2 == GameSettings.Options.ANIMATED_SMOKE) {
         this.ofAnimatedSmoke = !this.ofAnimatedSmoke;
      }

      if (options2 == GameSettings.Options.VOID_PARTICLES) {
         this.ofVoidParticles = !this.ofVoidParticles;
      }

      if (options2 == GameSettings.Options.WATER_PARTICLES) {
         this.ofWaterParticles = !this.ofWaterParticles;
      }

      if (options2 == GameSettings.Options.PORTAL_PARTICLES) {
         this.ofPortalParticles = !this.ofPortalParticles;
      }

      if (options2 == GameSettings.Options.POTION_PARTICLES) {
         this.ofPotionParticles = !this.ofPotionParticles;
      }

      if (options2 == GameSettings.Options.FIREWORK_PARTICLES) {
         this.ofFireworkParticles = !this.ofFireworkParticles;
      }

      if (options2 == GameSettings.Options.DRIPPING_WATER_LAVA) {
         this.ofDrippingWaterLava = !this.ofDrippingWaterLava;
      }

      if (options2 == GameSettings.Options.ANIMATED_TERRAIN) {
         this.ofAnimatedTerrain = !this.ofAnimatedTerrain;
      }

      if (options2 == GameSettings.Options.ANIMATED_TEXTURES) {
         this.ofAnimatedTextures = !this.ofAnimatedTextures;
      }

      if (options2 == GameSettings.Options.RAIN_SPLASH) {
         this.ofRainSplash = !this.ofRainSplash;
      }

      if (options2 == GameSettings.Options.LAGOMETER) {
         this.ofLagometer = !this.ofLagometer;
      }

      if (options2 == GameSettings.Options.SHOW_FPS) {
         this.ofShowFps = !this.ofShowFps;
      }

      if (options2 == GameSettings.Options.AUTOSAVE_TICKS) {
         this.ofAutoSaveTicks = Math.max(this.ofAutoSaveTicks / 900 * 900, 900);
         this.ofAutoSaveTicks *= 2;
         if (this.ofAutoSaveTicks > 28800) {
            this.ofAutoSaveTicks = 900;
         }
      }

      if (options2 == GameSettings.Options.BETTER_GRASS) {
         this.ofBetterGrass++;
         if (this.ofBetterGrass > 3) {
            this.ofBetterGrass = 1;
         }

         this.mc.renderGlobal.loadRenderers();
      }

      if (options2 == GameSettings.Options.CONNECTED_TEXTURES) {
         this.ofConnectedTextures++;
         if (this.ofConnectedTextures > 3) {
            this.ofConnectedTextures = 1;
         }

         if (this.ofConnectedTextures == 2) {
            this.mc.renderGlobal.loadRenderers();
         } else {
            this.mc.refreshResources();
         }
      }

      if (options2 == GameSettings.Options.WEATHER) {
         this.ofWeather = !this.ofWeather;
      }

      if (options2 == GameSettings.Options.SKY) {
         this.ofSky = !this.ofSky;
      }

      if (options2 == GameSettings.Options.STARS) {
         this.ofStars = !this.ofStars;
      }

      if (options2 == GameSettings.Options.SUN_MOON) {
         this.ofSunMoon = !this.ofSunMoon;
      }

      if (options2 == GameSettings.Options.VIGNETTE) {
         this.ofVignette++;
         if (this.ofVignette > 2) {
            this.ofVignette = 0;
         }
      }

      if (options2 == GameSettings.Options.CHUNK_UPDATES) {
         this.ofChunkUpdates++;
         if (this.ofChunkUpdates > 5) {
            this.ofChunkUpdates = 1;
         }
      }

      if (options2 == GameSettings.Options.CHUNK_UPDATES_DYNAMIC) {
         this.ofChunkUpdatesDynamic = !this.ofChunkUpdatesDynamic;
      }

      if (options2 == GameSettings.Options.TIME) {
         this.ofTime++;
         if (this.ofTime > 2) {
            this.ofTime = 0;
         }
      }

      if (options2 == GameSettings.Options.CLEAR_WATER) {
         this.ofClearWater = !this.ofClearWater;
         this.updateWaterOpacity();
      }

      if (options2 == GameSettings.Options.PROFILER) {
         this.ofProfiler = !this.ofProfiler;
      }

      if (options2 == GameSettings.Options.BETTER_SNOW) {
         this.ofBetterSnow = !this.ofBetterSnow;
         this.mc.renderGlobal.loadRenderers();
      }

      if (options2 == GameSettings.Options.SWAMP_COLORS) {
         this.ofSwampColors = !this.ofSwampColors;
         CustomColors.updateUseDefaultGrassFoliageColors();
         this.mc.renderGlobal.loadRenderers();
      }

      if (options2 == GameSettings.Options.RANDOM_ENTITIES) {
         this.ofRandomEntities = !this.ofRandomEntities;
         RandomEntities.update();
      }

      if (options2 == GameSettings.Options.SMOOTH_BIOMES) {
         this.ofSmoothBiomes = !this.ofSmoothBiomes;
         CustomColors.updateUseDefaultGrassFoliageColors();
         this.mc.renderGlobal.loadRenderers();
      }

      if (options2 == GameSettings.Options.CUSTOM_FONTS) {
         this.ofCustomFonts = !this.ofCustomFonts;
         this.mc.fontRendererObj.onResourceManagerReload(Config.getResourceManager());
         this.mc.standardGalacticFontRenderer.onResourceManagerReload(Config.getResourceManager());
      }

      if (options2 == GameSettings.Options.CUSTOM_COLORS) {
         this.ofCustomColors = !this.ofCustomColors;
         CustomColors.update();
         this.mc.renderGlobal.loadRenderers();
      }

      if (options2 == GameSettings.Options.CUSTOM_ITEMS) {
         this.ofCustomItems = !this.ofCustomItems;
         this.mc.refreshResources();
      }

      if (options2 == GameSettings.Options.CUSTOM_SKY) {
         this.ofCustomSky = !this.ofCustomSky;
         CustomSky.update();
      }

      if (options2 == GameSettings.Options.SHOW_CAPES) {
         this.ofShowCapes = !this.ofShowCapes;
      }

      if (options2 == GameSettings.Options.NATURAL_TEXTURES) {
         this.ofNaturalTextures = !this.ofNaturalTextures;
         NaturalTextures.update();
         this.mc.renderGlobal.loadRenderers();
      }

      if (options2 == GameSettings.Options.EMISSIVE_TEXTURES) {
         this.ofEmissiveTextures = !this.ofEmissiveTextures;
         this.mc.refreshResources();
      }

      if (options2 == GameSettings.Options.FAST_MATH) {
         this.ofFastMath = !this.ofFastMath;
         MathHelper.fastMath = this.ofFastMath;
      }

      if (options2 == GameSettings.Options.TRANSLUCENT_BLOCKS) {
         if (this.ofTranslucentBlocks == 0) {
            this.ofTranslucentBlocks = 1;
         } else if (this.ofTranslucentBlocks == 1) {
            this.ofTranslucentBlocks = 2;
         } else if (this.ofTranslucentBlocks == 2) {
            this.ofTranslucentBlocks = 0;
         } else {
            this.ofTranslucentBlocks = 0;
         }

         this.mc.renderGlobal.loadRenderers();
      }

      if (options2 == GameSettings.Options.LAZY_CHUNK_LOADING) {
         this.ofLazyChunkLoading = !this.ofLazyChunkLoading;
      }

      if (options2 == GameSettings.Options.RENDER_REGIONS) {
         this.ofRenderRegions = !this.ofRenderRegions;
         this.mc.renderGlobal.loadRenderers();
      }

      if (options2 == GameSettings.Options.SMART_ANIMATIONS) {
         this.ofSmartAnimations = !this.ofSmartAnimations;
         this.mc.renderGlobal.loadRenderers();
      }

      if (options2 == GameSettings.Options.DYNAMIC_FOV) {
         this.ofDynamicFov = !this.ofDynamicFov;
      }

      if (options2 == GameSettings.Options.ALTERNATE_BLOCKS) {
         this.ofAlternateBlocks = !this.ofAlternateBlocks;
         this.mc.refreshResources();
      }

      if (options2 == GameSettings.Options.DYNAMIC_LIGHTS) {
         this.ofDynamicLights = cycleValue(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
         DynamicLights.removeLights(this.mc.renderGlobal);
      }

      if (options2 == GameSettings.Options.SCREENSHOT_SIZE) {
         this.ofScreenshotSize++;
         if (this.ofScreenshotSize > 4) {
            this.ofScreenshotSize = 1;
         }

         if (!OpenGlHelper.isFramebufferEnabled()) {
            this.ofScreenshotSize = 1;
         }
      }

      if (options2 == GameSettings.Options.CUSTOM_ENTITY_MODELS) {
         this.ofCustomEntityModels = !this.ofCustomEntityModels;
         this.mc.refreshResources();
      }

      if (options2 == GameSettings.Options.CUSTOM_GUIS) {
         this.ofCustomGuis = !this.ofCustomGuis;
         CustomGuis.update();
      }

      if (options2 == GameSettings.Options.SHOW_GL_ERRORS) {
         this.ofShowGlErrors = !this.ofShowGlErrors;
      }

      if (options2 == GameSettings.Options.HELD_ITEM_TOOLTIPS) {
         this.heldItemTooltips = !this.heldItemTooltips;
      }

      if (options2 == GameSettings.Options.ADVANCED_TOOLTIPS) {
         this.advancedItemTooltips = !this.advancedItemTooltips;
      }
   }

   private float parseFloat(String str) {
      return str.equals("true") ? 1.0F : (str.equals("false") ? 0.0F : Float.parseFloat(str));
   }

   public boolean getOptionOrdinalValue(GameSettings.Options settingOption) {
      switch (settingOption) {
         case INVERT_MOUSE:
            return this.invertMouse;
         case VIEW_BOBBING:
            return this.viewBobbing;
         case ANAGLYPH:
            return this.anaglyph;
         case FBO_ENABLE:
            return this.fboEnable;
         case CHAT_COLOR:
            return this.chatColours;
         case CHAT_LINKS:
            return this.chatLinks;
         case CHAT_LINKS_PROMPT:
            return this.chatLinksPrompt;
         case SNOOPER_ENABLED:
            return this.fullScreen;
         case USE_FULLSCREEN:
            return this.fullscreenOption;
         case ENABLE_VSYNC:
            return this.enableVsync;
         case USE_VBO:
            return this.useVbo;
         case TOUCHSCREEN:
            return this.touchscreen;
         case STREAM_SEND_METADATA:
            return this.useNativeTransport;
         case FORCE_UNICODE_FONT:
            return this.forceUnicodeFont;
         case BLOCK_ALTERNATIVES:
            return this.allowBlockAlternatives;
         case REDUCED_DEBUG_INFO:
            return this.reducedDebugInfo;
         case ENTITY_SHADOWS:
            return this.entityShadowsOption;
         case REALMS_NOTIFICATIONS:
            return this.snooperEnabled;
         default:
            return false;
      }
   }

   public void loadOptions() {
      FileInputStream fileInputStream = null;

      label532: {
         try {
            if (this.optionsFile.exists()) {
               BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream = new FileInputStream(this.optionsFile)));
               this.mapSoundLevels.clear();

               String text;
               while ((text = bufferedReader.readLine()) != null) {
                  try {
                     String[] texts = text.split(":");
                     if (texts[0].equals("mouseSensitivity")) {
                        this.mouseSensitivity = this.parseFloat(texts[1]);
                     }

                     if (texts[0].equals("fov")) {
                        this.fovSetting = this.parseFloat(texts[1]) * 40.0F + 70.0F;
                     }

                     if (texts[0].equals("gamma")) {
                        this.gammaSetting = this.parseFloat(texts[1]);
                     }

                     if (texts[0].equals("saturation")) {
                        this.saturation = this.parseFloat(texts[1]);
                     }

                     if (texts[0].equals("invertYMouse")) {
                        this.invertMouse = texts[1].equals("true");
                     }

                     if (texts[0].equals("renderDistance")) {
                        this.renderDistanceChunks = Integer.parseInt(texts[1]);
                     }

                     if (texts[0].equals("guiScale")) {
                        this.guiScale = Integer.parseInt(texts[1]);
                     }

                     if (texts[0].equals("particles")) {
                        this.particleSetting = Integer.parseInt(texts[1]);
                     }

                     if (texts[0].equals("bobView")) {
                        this.viewBobbing = texts[1].equals("true");
                     }

                     if (texts[0].equals("anaglyph3d")) {
                        this.anaglyph = texts[1].equals("true");
                     }

                     if (texts[0].equals("maxFps")) {
                        this.limitFramerate = Integer.parseInt(texts[1]);
                        if (this.enableVsync) {
                           this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                        }

                        if (this.limitFramerate <= 0) {
                           this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                        }
                     }

                     if (texts[0].equals("fboEnable")) {
                        this.fboEnable = texts[1].equals("true");
                     }

                     if (texts[0].equals("difficulty")) {
                        this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(texts[1]));
                     }

                     if (texts[0].equals("fancyGraphics")) {
                        this.fancyGraphics = texts[1].equals("true");
                        this.updateRenderClouds();
                     }

                     if (texts[0].equals("ao")) {
                        if (texts[1].equals("true")) {
                           this.ambientOcclusion = 2;
                        } else if (texts[1].equals("false")) {
                           this.ambientOcclusion = 0;
                        } else {
                           this.ambientOcclusion = Integer.parseInt(texts[1]);
                        }
                     }

                     if (texts[0].equals("renderClouds")) {
                        if (texts[1].equals("true")) {
                           this.clouds = 2;
                        } else if (texts[1].equals("false")) {
                           this.clouds = 0;
                        } else if (texts[1].equals("fast")) {
                           this.clouds = 1;
                        }
                     }

                     if (texts[0].equals("resourcePacks")) {
                        this.resourcePacks = gson.fromJson(text.substring(text.indexOf(58) + 1), typeListString);
                        if (this.resourcePacks == null) {
                           this.resourcePacks = Lists.newArrayList();
                        }
                     }

                     if (texts[0].equals("incompatibleResourcePacks")) {
                        this.incompatibleResourcePacks = gson.fromJson(text.substring(text.indexOf(58) + 1), typeListString);
                        if (this.incompatibleResourcePacks == null) {
                           this.incompatibleResourcePacks = Lists.newArrayList();
                        }
                     }

                     if (texts[0].equals("lastServer") && texts.length >= 2) {
                        this.lastServer = text.substring(text.indexOf(58) + 1);
                     }

                     if (texts[0].equals("lang") && texts.length >= 2) {
                        this.language = texts[1];
                     }

                     if (texts[0].equals("chatVisibility")) {
                        this.chatVisibility = net.minecraft.entity.player.EntityPlayer.EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(texts[1]));
                     }

                     if (texts[0].equals("chatColors")) {
                        this.chatColours = texts[1].equals("true");
                     }

                     if (texts[0].equals("chatLinks")) {
                        this.chatLinks = texts[1].equals("true");
                     }

                     if (texts[0].equals("chatLinksPrompt")) {
                        this.chatLinksPrompt = texts[1].equals("true");
                     }

                     if (texts[0].equals("chatOpacity")) {
                        this.chatOpacity = this.parseFloat(texts[1]);
                     }

                     if (texts[0].equals("snooperEnabled")) {
                        this.fullScreen = texts[1].equals("true");
                     }

                     if (texts[0].equals("fullscreen")) {
                        this.fullscreenOption = texts[1].equals("true");
                     }

                     if (texts[0].equals("enableVsync")) {
                        this.enableVsync = texts[1].equals("true");
                        if (this.enableVsync) {
                           this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                        }

                        this.updateVSync();
                     }

                     if (texts[0].equals("useVbo")) {
                        this.useVbo = texts[1].equals("true");
                     }

                     if (texts[0].equals("hideServerAddress")) {
                        this.hideServerAddress = texts[1].equals("true");
                     }

                     if (texts[0].equals("advancedItemTooltips")) {
                        this.advancedItemTooltips = texts[1].equals("true");
                     }

                     if (texts[0].equals("pauseOnLostFocus")) {
                        this.pauseOnLostFocus = texts[1].equals("true");
                     }

                     if (texts[0].equals("touchscreen")) {
                        this.touchscreen = texts[1].equals("true");
                     }

                     if (texts[0].equals("overrideHeight")) {
                        this.overrideHeight = Integer.parseInt(texts[1]);
                     }

                     if (texts[0].equals("overrideWidth")) {
                        this.overrideWidth = Integer.parseInt(texts[1]);
                     }

                     if (texts[0].equals("heldItemTooltips")) {
                        this.heldItemTooltips = texts[1].equals("true");
                     }

                     if (texts[0].equals("chatHeightFocused")) {
                        this.chatHeightFocused = this.parseFloat(texts[1]);
                     }

                     if (texts[0].equals("chatHeightUnfocused")) {
                        this.chatHeightUnfocused = this.parseFloat(texts[1]);
                     }

                     if (texts[0].equals("chatScale")) {
                        this.chatScale = this.parseFloat(texts[1]);
                     }

                     if (texts[0].equals("chatWidth")) {
                        this.chatWidth = this.parseFloat(texts[1]);
                     }

                     if (texts[0].equals("showInventoryAchievementHint")) {
                        this.showInventoryAchievementHint = texts[1].equals("true");
                     }

                     if (texts[0].equals("mipmapLevels")) {
                        this.mipmapLevels = Integer.parseInt(texts[1]);
                     }

                     if (texts[0].equals("streamBytesPerPixel")) {
                        this.streamBytesPerPixel = this.parseFloat(texts[1]);
                     }

                     if (texts[0].equals("streamMicVolume")) {
                        this.streamMicVolume = this.parseFloat(texts[1]);
                     }

                     if (texts[0].equals("streamSystemVolume")) {
                        this.streamSystemVolume = this.parseFloat(texts[1]);
                     }

                     if (texts[0].equals("streamKbps")) {
                        this.streamKbps = this.parseFloat(texts[1]);
                     }

                     if (texts[0].equals("streamFps")) {
                        this.streamFps = this.parseFloat(texts[1]);
                     }

                     if (texts[0].equals("streamCompression")) {
                        this.streamCompression = Integer.parseInt(texts[1]);
                     }

                     if (texts[0].equals("streamSendMetadata")) {
                        this.useNativeTransport = texts[1].equals("true");
                     }

                     if (texts[0].equals("streamPreferredServer") && texts.length >= 2) {
                        this.streamPreferredServer = text.substring(text.indexOf(58) + 1);
                     }

                     if (texts[0].equals("streamChatEnabled")) {
                        this.streamChatEnabled = Integer.parseInt(texts[1]);
                     }

                     if (texts[0].equals("streamChatUserFilter")) {
                        this.streamChatUserFilter = Integer.parseInt(texts[1]);
                     }

                     if (texts[0].equals("streamMicToggleBehavior")) {
                        this.streamMicToggleBehavior = Integer.parseInt(texts[1]);
                     }

                     if (texts[0].equals("forceUnicodeFont")) {
                        this.forceUnicodeFont = texts[1].equals("true");
                     }

                     if (texts[0].equals("allowBlockAlternatives")) {
                        this.allowBlockAlternatives = texts[1].equals("true");
                     }

                     if (texts[0].equals("reducedDebugInfo")) {
                        this.reducedDebugInfo = texts[1].equals("true");
                     }

                     if (texts[0].equals("useNativeTransport")) {
                        this.entityShadows = texts[1].equals("true");
                     }

                     if (texts[0].equals("entityShadows")) {
                        this.entityShadowsOption = texts[1].equals("true");
                     }

                     if (texts[0].equals("realmsNotifications")) {
                        this.snooperEnabled = texts[1].equals("true");
                     }

                     for (KeyBinding keyBinding5 : this.keyBindings) {
                        if (texts[0].equals("key_" + keyBinding5.getKeyDescription())) {
                           keyBinding5.setKeyCode(Integer.parseInt(texts[1]));
                        }
                     }

                     for (SoundCategory soundCategory : SoundCategory.values()) {
                        if (texts[0].equals("soundCategory_" + soundCategory.getCategoryName())) {
                           this.mapSoundLevels.put(soundCategory, this.parseFloat(texts[1]));
                        }
                     }

                     for (EnumPlayerModelParts enumPlayerModelParts : EnumPlayerModelParts.values()) {
                        if (texts[0].equals("modelPart_" + enumPlayerModelParts.getPartName())) {
                           this.setModelPartEnabled(enumPlayerModelParts, texts[1].equals("true"));
                        }
                     }
                  } catch (Exception exception) {
                     logger.warn("Skipping bad option: " + text);
                     exception.printStackTrace();
                  }
               }

               KeyBinding.resetKeyBindingArrayAndHash();
               bufferedReader.close();
               break label532;
            }
         } catch (Exception exception2) {
            logger.error("Failed to load options", exception2);
            break label532;
         } finally {
            IOUtils.closeQuietly(fileInputStream);
         }

         return;
      }

      this.loadOfOptions();
   }

   public String getKeyBinding(GameSettings.Options settingOption) {
      String optifineOptionText = this.getOptifineOptionText(settingOption);
      if (optifineOptionText != null) {
         return optifineOptionText;
      }

      String text = I18n.format(settingOption.getEnumString()) + ": ";
      if (!settingOption.getEnumFloat()) {
         if (settingOption.getEnumBoolean()) {
            boolean optionOrdinalValue = this.getOptionOrdinalValue(settingOption);
            return optionOrdinalValue ? text + I18n.format("options.on") : text + I18n.format("options.off");
         } else if (settingOption == GameSettings.Options.GUI_SCALE) {
            return this.guiScale >= GUISCALES.length ? text + this.guiScale + "x" : text + getTranslation(GUISCALES, this.guiScale);
         } else if (settingOption == GameSettings.Options.CHAT_VISIBILITY) {
            return text + I18n.format(this.chatVisibility.getResourceKey());
         } else if (settingOption == GameSettings.Options.PARTICLES) {
            return text + getTranslation(PARTICLES, this.particleSetting);
         } else if (settingOption == GameSettings.Options.AMBIENT_OCCLUSION) {
            return text + getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion);
         } else if (settingOption == GameSettings.Options.STREAM_COMPRESSION) {
            return text + getTranslation(CLOUDS_TYPES, this.streamCompression);
         } else if (settingOption == GameSettings.Options.STREAM_CHAT_ENABLED) {
            return text + getTranslation(stringArray, this.streamChatEnabled);
         } else if (settingOption == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
            return text + getTranslation(stringArray2, this.streamChatUserFilter);
         } else if (settingOption == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
            return text + getTranslation(stringArray3, this.streamMicToggleBehavior);
         } else if (settingOption == GameSettings.Options.RENDER_CLOUDS) {
            return text + getTranslation(stringArray4, this.clouds);
         } else if (settingOption == GameSettings.Options.GRAPHICS) {
            return this.fancyGraphics ? text + I18n.format("options.graphics.fancy") : text + I18n.format("options.graphics.fast");
         } else {
            return text;
         }
      } else {
         float optionFloatValue = this.getOptionFloatValue(settingOption);
         float f2 = settingOption.normalizeValue(optionFloatValue);
         return settingOption == GameSettings.Options.MIPMAP_LEVELS && optionFloatValue >= 4.0
            ? text + Lang.get("of.general.max")
            : (
               settingOption == GameSettings.Options.SENSITIVITY
                  ? (
                     f2 == 0.0F
                        ? text + I18n.format("options.sensitivity.min")
                        : (f2 == 1.0F ? text + I18n.format("options.sensitivity.max") : text + (int)(f2 * 200.0F) + "%")
                  )
                  : (
                     settingOption == GameSettings.Options.FOV
                        ? (
                           optionFloatValue == 70.0F
                              ? text + I18n.format("options.fov.min")
                              : (optionFloatValue == 110.0F ? text + I18n.format("options.fov.max") : text + (int)optionFloatValue)
                        )
                        : (
                           settingOption == GameSettings.Options.FRAMERATE_LIMIT
                              ? (optionFloatValue == settingOption.valueMax ? text + I18n.format("options.framerateLimit.max") : text + (int)optionFloatValue + " fps")
                              : (
                                 settingOption == GameSettings.Options.RENDER_CLOUDS
                                    ? (optionFloatValue == settingOption.valueMin ? text + I18n.format("options.cloudHeight.min") : text + ((int)optionFloatValue + 128))
                                    : (
                                       settingOption == GameSettings.Options.GAMMA
                                          ? (
                                             f2 == 0.0F
                                                ? text + I18n.format("options.gamma.min")
                                                : (f2 == 1.0F ? text + I18n.format("options.gamma.max") : text + "+" + (int)(f2 * 100.0F) + "%")
                                          )
                                          : (
                                             settingOption == GameSettings.Options.SATURATION
                                                ? text + (int)(f2 * 400.0F) + "%"
                                                : (
                                                   settingOption == GameSettings.Options.CHAT_OPACITY
                                                      ? text + (int)(f2 * 90.0F + 10.0F) + "%"
                                                      : (
                                                         settingOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED
                                                            ? text + GuiNewChat.calculateChatboxHeight(f2) + "px"
                                                            : (
                                                               settingOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED
                                                                  ? text + GuiNewChat.calculateChatboxHeight(f2) + "px"
                                                                  : (
                                                                     settingOption == GameSettings.Options.CHAT_WIDTH
                                                                        ? text + GuiNewChat.calculateChatboxWidth(f2) + "px"
                                                                        : (
                                                                           settingOption == GameSettings.Options.RENDER_DISTANCE
                                                                              ? text + (int)optionFloatValue + " chunks"
                                                                              : (
                                                                                 settingOption == GameSettings.Options.MIPMAP_LEVELS
                                                                                    ? (optionFloatValue == 0.0F ? text + I18n.format("options.off") : text + (int)optionFloatValue)
                                                                                    : (
                                                                                       settingOption == GameSettings.Options.STREAM_FPS
                                                                                          ? text + TwitchStream.createInt(f2) + " fps"
                                                                                          : (
                                                                                             settingOption == GameSettings.Options.STREAM_KBPS
                                                                                                ? text + TwitchStream.createInt2(f2) + " Kbps"
                                                                                                : (
                                                                                                   settingOption == GameSettings.Options.STREAM_BYTES_PER_PIXEL
                                                                                                      ? text
                                                                                                         + String.format(
                                                                                                            "%.3f bpp", TwitchStream.createFloat(f2)
                                                                                                         )
                                                                                                      : (
                                                                                                         f2 == 0.0F
                                                                                                            ? text + I18n.format("options.off")
                                                                                                            : text + (int)(f2 * 100.0F) + "%"
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
                  )
            );
      }
   }

   private static int indexOfValue(int value, int[] allowedValues) {
      for (int index = 0; index < allowedValues.length; index++) {
         if (allowedValues[index] == value) {
            return index;
         }
      }

      return -1;
   }

   public void updateVSync() {
      Display.setVSyncEnabled(this.enableVsync);
   }

   private void setOptifineOptionFloatValue(GameSettings.Options option, float value) {
      if (option == GameSettings.Options.CLOUD_HEIGHT) {
         this.ofCloudsHeight = value;
         this.mc.renderGlobal.resetClouds();
      }

      if (option == GameSettings.Options.AO_LEVEL) {
         this.ofAoLevel = value;
         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.AA_LEVEL) {
         int i = (int)value;
         if (i > 0 && Config.isShaders()) {
            Config.showGuiMessage(Lang.get("of.message.aa.shaders1"), Lang.get("of.message.aa.shaders2"));
            return;
         }

         int[] stringArray = new int[]{0, 2, 4, 6, 8, 12, 16};
         this.ofAaLevel = 0;

         for (int innerIndex = 0; innerIndex < stringArray.length; innerIndex++) {
            if (i >= stringArray[innerIndex]) {
               this.ofAaLevel = stringArray[innerIndex];
            }
         }

         this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
      }

      if (option == GameSettings.Options.AF_LEVEL) {
         int i2 = (int)value;
         if (i2 > 1 && Config.isShaders()) {
            Config.showGuiMessage(Lang.get("of.message.af.shaders1"), Lang.get("of.message.af.shaders2"));
            return;
         }

         this.ofAfLevel = 1;

         while (this.ofAfLevel * 2 <= i2) {
            this.ofAfLevel *= 2;
         }

         this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
         this.mc.refreshResources();
      }

      if (option == GameSettings.Options.MIPMAP_TYPE) {
         int i3 = (int)value;
         this.ofMipmapType = Config.limit(i3, 0, 3);
         this.mc.refreshResources();
      }

      if (option == GameSettings.Options.FULLSCREEN_MODE) {
         int i4 = (int)value - 1;
         String[] stringArray = Config.getDisplayModeNames();
         if (i4 < 0 || i4 >= stringArray.length) {
            this.ofFullscreenMode = "Default";
            return;
         }

         this.ofFullscreenMode = stringArray[i4];
      }
   }

   public void setOptionFloatValue(GameSettings.Options settingsOption, float value) {
      this.setOptifineOptionFloatValue(settingsOption, value);
      if (settingsOption == GameSettings.Options.SENSITIVITY) {
         this.mouseSensitivity = value;
      }

      if (settingsOption == GameSettings.Options.FOV) {
         this.fovSetting = value;
      }

      if (settingsOption == GameSettings.Options.GAMMA) {
         this.gammaSetting = value;
      }

      if (settingsOption == GameSettings.Options.FRAMERATE_LIMIT) {
         this.limitFramerate = (int)value;
         this.enableVsync = false;
         if (this.limitFramerate <= 0) {
            this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
            this.enableVsync = true;
         }

         this.updateVSync();
      }

      if (settingsOption == GameSettings.Options.CHAT_OPACITY) {
         this.chatOpacity = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (settingsOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED) {
         this.chatHeightFocused = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (settingsOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED) {
         this.chatHeightUnfocused = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (settingsOption == GameSettings.Options.CHAT_WIDTH) {
         this.chatWidth = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (settingsOption == GameSettings.Options.CHAT_SCALE) {
         this.chatScale = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (settingsOption == GameSettings.Options.MIPMAP_LEVELS) {
         int mipmapLevels2 = this.mipmapLevels;
         this.mipmapLevels = (int)value;
         if (mipmapLevels2 != value) {
            this.mc.getTextureMapBlocks().setMipmapLevels(this.mipmapLevels);
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            this.mc.getTextureMapBlocks().setBlurMipmapDirect(false, this.mipmapLevels > 0);
            this.mc.scheduleResourcesRefresh();
         }
      }

      if (settingsOption == GameSettings.Options.BLOCK_ALTERNATIVES) {
         this.allowBlockAlternatives = !this.allowBlockAlternatives;
         this.mc.renderGlobal.loadRenderers();
      }

      if (settingsOption == GameSettings.Options.RENDER_DISTANCE) {
         this.renderDistanceChunks = (int)value;
         this.mc.renderGlobal.setDisplayListEntitiesDirty();
      }

      if (settingsOption == GameSettings.Options.STREAM_BYTES_PER_PIXEL) {
         this.streamBytesPerPixel = value;
      }

      if (settingsOption == GameSettings.Options.STREAM_VOLUME_MIC) {
         this.streamMicVolume = value;
         this.mc.getIStream().update3();
      }

      if (settingsOption == GameSettings.Options.STREAM_VOLUME_SYSTEM) {
         this.streamSystemVolume = value;
         this.mc.getIStream().update3();
      }

      if (settingsOption == GameSettings.Options.STREAM_KBPS) {
         this.streamKbps = value;
      }

      if (settingsOption == GameSettings.Options.STREAM_FPS) {
         this.streamFps = value;
      }
   }

   private static int cycleValue(int value, int[] allowedValues) {
      int i = indexOfValue(value, allowedValues);
      if (i < 0) {
         return allowedValues[0];
      }

      if (++i >= allowedValues.length) {
         i = 0;
      }

      return allowedValues[i];
   }

   public static String getKeyDisplayString(int key) {
      return key < 0
         ? I18n.format("key.mouseButton", key + 101)
         : (key < 256 ? Keyboard.getKeyName(key) : String.format("%c", (char)(key - 256)).toUpperCase());
   }

   public void setAllAnimations(boolean enabled) {
      int i = enabled ? 0 : 2;
      this.ofAnimatedWater = i;
      this.ofAnimatedLava = i;
      this.ofAnimatedFire = enabled;
      this.ofAnimatedPortal = enabled;
      this.ofAnimatedRedstone = enabled;
      this.ofAnimatedExplosion = enabled;
      this.ofAnimatedFlame = enabled;
      this.ofAnimatedSmoke = enabled;
      this.ofVoidParticles = enabled;
      this.ofWaterParticles = enabled;
      this.ofRainSplash = enabled;
      this.ofPortalParticles = enabled;
      this.ofPotionParticles = enabled;
      this.ofFireworkParticles = enabled;
      this.particleSetting = enabled ? 0 : 2;
      this.ofDrippingWaterLava = enabled;
      this.ofAnimatedTerrain = enabled;
      this.ofAnimatedTextures = enabled;
   }

   private void updateRenderClouds() {
      switch (this.ofClouds) {
         case 1:
            this.clouds = 1;
            break;
         case 2:
            this.clouds = 2;
            break;
         case 3:
            this.clouds = 0;
            break;
         default:
            if (this.fancyGraphics) {
               this.clouds = 2;
            } else {
               this.clouds = 1;
            }
      }
   }

   public GameSettings(Minecraft mcIn, File optionsFileIn) {
      this.mc = mcIn;
      this.optionsFile = new File(optionsFileIn, "options.txt");
      if (mcIn.isJava64bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
         GameSettings.Options.RENDER_DISTANCE.setValueMax(32.0F);
         if (Runtime.getRuntime().maxMemory() >= 1500000000L) {
            GameSettings.Options.RENDER_DISTANCE.setValueMax(48.0F);
         }

         if (Runtime.getRuntime().maxMemory() >= 2500000000L) {
            GameSettings.Options.RENDER_DISTANCE.setValueMax(64.0F);
         }
      } else {
         GameSettings.Options.RENDER_DISTANCE.setValueMax(16.0F);
      }

      this.renderDistanceChunks = mcIn.isJava64bit() ? 12 : 8;
      this.optionsFileOF = new File(optionsFileIn, "optionsof.txt");
      this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
      this.ofKeyBindZoom = new KeyBinding("of.key.zoom", 46, "key.categories.misc");
      this.keyBindings = ArrayUtils.add(this.keyBindings, this.ofKeyBindZoom);
      KeyUtils.fixKeyConflicts(this.keyBindings, new KeyBinding[]{this.ofKeyBindZoom});
      this.renderDistanceChunks = 8;
      this.loadOptions();
      Config.initGameSettings(this);
   }

   public void resetSettings() {
      this.renderDistanceChunks = 8;
      this.viewBobbing = true;
      this.anaglyph = false;
      this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
      this.enableVsync = false;
      this.updateVSync();
      this.mipmapLevels = 4;
      this.fancyGraphics = true;
      this.ambientOcclusion = 2;
      this.clouds = 2;
      this.fovSetting = 70.0F;
      this.gammaSetting = 0.0F;
      this.guiScale = 0;
      this.particleSetting = 0;
      this.heldItemTooltips = true;
      this.useVbo = false;
      this.forceUnicodeFont = false;
      this.ofFogType = 1;
      this.ofFogStart = 0.8F;
      this.ofMipmapType = 0;
      this.ofOcclusionFancy = false;
      this.ofSmartAnimations = false;
      this.ofSmoothFps = false;
      Config.updateAvailableProcessors();
      this.ofSmoothWorld = Config.isSingleProcessor();
      this.ofLazyChunkLoading = false;
      this.ofRenderRegions = false;
      this.ofFastMath = false;
      this.ofFastRender = false;
      this.ofTranslucentBlocks = 0;
      this.ofDynamicFov = true;
      this.ofAlternateBlocks = true;
      this.ofDynamicLights = 3;
      this.ofScreenshotSize = 1;
      this.ofCustomEntityModels = true;
      this.ofCustomGuis = true;
      this.ofShowGlErrors = true;
      this.ofAoLevel = 1.0F;
      this.ofAaLevel = 0;
      this.ofAfLevel = 1;
      this.ofClouds = 0;
      this.ofCloudsHeight = 0.0F;
      this.ofTrees = 0;
      this.ofRain = 0;
      this.ofBetterGrass = 3;
      this.ofAutoSaveTicks = 4000;
      this.ofLagometer = false;
      this.ofShowFps = false;
      this.ofProfiler = false;
      this.ofWeather = true;
      this.ofSky = true;
      this.ofStars = true;
      this.ofSunMoon = true;
      this.ofVignette = 0;
      this.ofChunkUpdates = 1;
      this.ofChunkUpdatesDynamic = false;
      this.ofTime = 0;
      this.ofClearWater = false;
      this.ofBetterSnow = false;
      this.ofFullscreenMode = "Default";
      this.ofSwampColors = true;
      this.ofRandomEntities = true;
      this.ofSmoothBiomes = true;
      this.ofCustomFonts = true;
      this.ofCustomColors = true;
      this.ofCustomItems = true;
      this.ofCustomSky = true;
      this.ofShowCapes = true;
      this.ofConnectedTextures = 2;
      this.ofNaturalTextures = false;
      this.ofEmissiveTextures = true;
      this.ofAnimatedWater = 0;
      this.ofAnimatedLava = 0;
      this.ofAnimatedFire = true;
      this.ofAnimatedPortal = true;
      this.ofAnimatedRedstone = true;
      this.ofAnimatedExplosion = true;
      this.ofAnimatedFlame = true;
      this.ofAnimatedSmoke = true;
      this.ofVoidParticles = true;
      this.ofWaterParticles = true;
      this.ofRainSplash = true;
      this.ofPortalParticles = true;
      this.ofPotionParticles = true;
      this.ofFireworkParticles = true;
      this.ofDrippingWaterLava = true;
      this.ofAnimatedTerrain = true;
      this.ofAnimatedTextures = true;
      Shaders.setShaderPack("OFF");
      Shaders.configAntialiasingLevel = 0;
      Shaders.uninit();
      Shaders.storeConfig();
      this.updateWaterOpacity();
      this.mc.refreshResources();
      this.saveOptions();
   }

   private void updateWaterOpacity() {
      if (Config.isIntegratedServerRunning()) {
         Config.waterOpacityChanged = true;
      }

      ClearWater.updateWaterOpacity(this, this.mc.theWorld);
   }

   public void sendSettingsToServer() {
      if (this.mc.thePlayer != null) {
         int i = 0;

         for (EnumPlayerModelParts enumPlayerModelParts : this.setModelParts) {
            i |= enumPlayerModelParts.getPartMask();
         }

         this.mc
            .thePlayer
            .sendQueue
            .addToSendQueue(new C15PacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, i));
      }
   }

   public boolean isUsingNativeTransport() {
      return this.entityShadows;
   }

   private static String getTranslation(String[] strArray, int index) {
      if (index < 0 || index >= strArray.length) {
         index = 0;
      }

      return I18n.format(strArray[index]);
   }

   public float getOptionFloatValue(GameSettings.Options settingOption) {
      float optifineOptionFloatValue = this.getOptifineOptionFloatValue(settingOption);
      return optifineOptionFloatValue != Float.MAX_VALUE
         ? optifineOptionFloatValue
         : (
            settingOption == GameSettings.Options.FOV
               ? this.fovSetting
               : (
                  settingOption == GameSettings.Options.GAMMA
                     ? this.gammaSetting
                     : (
                        settingOption == GameSettings.Options.SATURATION
                           ? this.saturation
                           : (
                              settingOption == GameSettings.Options.SENSITIVITY
                                 ? this.mouseSensitivity
                                 : (
                                    settingOption == GameSettings.Options.CHAT_OPACITY
                                       ? this.chatOpacity
                                       : (
                                          settingOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED
                                             ? this.chatHeightFocused
                                             : (
                                                settingOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED
                                                   ? this.chatHeightUnfocused
                                                   : (
                                                      settingOption == GameSettings.Options.CHAT_SCALE
                                                         ? this.chatScale
                                                         : (
                                                            settingOption == GameSettings.Options.CHAT_WIDTH
                                                               ? this.chatWidth
                                                               : (
                                                                  settingOption == GameSettings.Options.FRAMERATE_LIMIT
                                                                     ? this.limitFramerate
                                                                     : (
                                                                        settingOption == GameSettings.Options.MIPMAP_LEVELS
                                                                           ? this.mipmapLevels
                                                                           : (
                                                                              settingOption == GameSettings.Options.RENDER_DISTANCE
                                                                                 ? this.renderDistanceChunks
                                                                                 : (
                                                                                    settingOption == GameSettings.Options.STREAM_BYTES_PER_PIXEL
                                                                                       ? this.streamBytesPerPixel
                                                                                       : (
                                                                                          settingOption == GameSettings.Options.STREAM_VOLUME_MIC
                                                                                             ? this.streamMicVolume
                                                                                             : (
                                                                                                settingOption == GameSettings.Options.STREAM_VOLUME_SYSTEM
                                                                                                   ? this.streamSystemVolume
                                                                                                   : (
                                                                                                      settingOption == GameSettings.Options.STREAM_KBPS
                                                                                                         ? this.streamKbps
                                                                                                         : (
                                                                                                            settingOption == GameSettings.Options.STREAM_FPS
                                                                                                               ? this.streamFps
                                                                                                               : 0.0F
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
                     )
               )
         );
   }

   public Set<EnumPlayerModelParts> getModelParts() {
      return ImmutableSet.copyOf(this.setModelParts);
   }

   public static boolean isKeyDown(KeyBinding key) {
      return key.getKeyCode() == 0 ? false : (key.getKeyCode() < 0 ? Mouse.isButtonDown(key.getKeyCode() + 100) : Keyboard.isKeyDown(key.getKeyCode()));
   }

   public void switchModelPartEnabled(EnumPlayerModelParts modelPart) {
      if (!this.getModelParts().contains(modelPart)) {
         this.setModelParts.add(modelPart);
      } else {
         this.setModelParts.remove(modelPart);
      }

      this.sendSettingsToServer();
   }

   public void saveOptions() {
      if (Reflector.reflectorClass14.isActive()) {
         Object object = Reflector.createObject2(Reflector.reflectorMethod7);
         if (object != null && Reflector.checkObject(object, Reflector.reflectorMethod9)) {
            return;
         }
      }

      try {
         PrintWriter printWriter = new PrintWriter(new FileWriter(this.optionsFile));
         printWriter.println("invertYMouse:" + this.invertMouse);
         printWriter.println("mouseSensitivity:" + this.mouseSensitivity);
         printWriter.println("fov:" + (this.fovSetting - 70.0F) / 40.0F);
         printWriter.println("gamma:" + this.gammaSetting);
         printWriter.println("saturation:" + this.saturation);
         printWriter.println("renderDistance:" + this.renderDistanceChunks);
         printWriter.println("guiScale:" + this.guiScale);
         printWriter.println("particles:" + this.particleSetting);
         printWriter.println("bobView:" + this.viewBobbing);
         printWriter.println("anaglyph3d:" + this.anaglyph);
         printWriter.println("maxFps:" + this.limitFramerate);
         printWriter.println("fboEnable:" + this.fboEnable);
         printWriter.println("difficulty:" + this.difficulty.getDifficultyId());
         printWriter.println("fancyGraphics:" + this.fancyGraphics);
         printWriter.println("ao:" + this.ambientOcclusion);
         switch (this.clouds) {
            case 0:
               printWriter.println("renderClouds:false");
               break;
            case 1:
               printWriter.println("renderClouds:fast");
               break;
            case 2:
               printWriter.println("renderClouds:true");
         }

         printWriter.println("resourcePacks:" + gson.toJson(this.resourcePacks));
         printWriter.println("incompatibleResourcePacks:" + gson.toJson(this.incompatibleResourcePacks));
         printWriter.println("lastServer:" + this.lastServer);
         printWriter.println("lang:" + this.language);
         printWriter.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
         printWriter.println("chatColors:" + this.chatColours);
         printWriter.println("chatLinks:" + this.chatLinks);
         printWriter.println("chatLinksPrompt:" + this.chatLinksPrompt);
         printWriter.println("chatOpacity:" + this.chatOpacity);
         printWriter.println("snooperEnabled:" + this.fullScreen);
         printWriter.println("fullscreen:" + this.fullscreenOption);
         printWriter.println("enableVsync:" + this.enableVsync);
         printWriter.println("useVbo:" + this.useVbo);
         printWriter.println("hideServerAddress:" + this.hideServerAddress);
         printWriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
         printWriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
         printWriter.println("touchscreen:" + this.touchscreen);
         printWriter.println("overrideWidth:" + this.overrideWidth);
         printWriter.println("overrideHeight:" + this.overrideHeight);
         printWriter.println("heldItemTooltips:" + this.heldItemTooltips);
         printWriter.println("chatHeightFocused:" + this.chatHeightFocused);
         printWriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
         printWriter.println("chatScale:" + this.chatScale);
         printWriter.println("chatWidth:" + this.chatWidth);
         printWriter.println("showInventoryAchievementHint:" + this.showInventoryAchievementHint);
         printWriter.println("mipmapLevels:" + this.mipmapLevels);
         printWriter.println("streamBytesPerPixel:" + this.streamBytesPerPixel);
         printWriter.println("streamMicVolume:" + this.streamMicVolume);
         printWriter.println("streamSystemVolume:" + this.streamSystemVolume);
         printWriter.println("streamKbps:" + this.streamKbps);
         printWriter.println("streamFps:" + this.streamFps);
         printWriter.println("streamCompression:" + this.streamCompression);
         printWriter.println("streamSendMetadata:" + this.useNativeTransport);
         printWriter.println("streamPreferredServer:" + this.streamPreferredServer);
         printWriter.println("streamChatEnabled:" + this.streamChatEnabled);
         printWriter.println("streamChatUserFilter:" + this.streamChatUserFilter);
         printWriter.println("streamMicToggleBehavior:" + this.streamMicToggleBehavior);
         printWriter.println("forceUnicodeFont:" + this.forceUnicodeFont);
         printWriter.println("allowBlockAlternatives:" + this.allowBlockAlternatives);
         printWriter.println("reducedDebugInfo:" + this.reducedDebugInfo);
         printWriter.println("useNativeTransport:" + this.entityShadows);
         printWriter.println("entityShadows:" + this.entityShadowsOption);
         printWriter.println("realmsNotifications:" + this.snooperEnabled);

         for (KeyBinding keyBinding5 : this.keyBindings) {
            printWriter.println("key_" + keyBinding5.getKeyDescription() + ":" + keyBinding5.getKeyCode());
         }

         for (SoundCategory soundCategory : SoundCategory.values()) {
            printWriter.println("soundCategory_" + soundCategory.getCategoryName() + ":" + this.getSoundLevel(soundCategory));
         }

         for (EnumPlayerModelParts enumPlayerModelParts : EnumPlayerModelParts.values()) {
            printWriter.println("modelPart_" + enumPlayerModelParts.getPartName() + ":" + this.setModelParts.contains(enumPlayerModelParts));
         }

         printWriter.close();
      } catch (Exception exception) {
         logger.error("Failed to save options", exception);
      }

      this.saveOfOptions();
      this.sendSettingsToServer();
   }

   public void setOptionKeyBinding(KeyBinding key, int keyCode) {
      key.setKeyCode(keyCode);
      this.saveOptions();
   }

   public void setOptionValue(GameSettings.Options settingsOption, int value) {
      this.handleOptions(settingsOption);
      if (settingsOption == GameSettings.Options.INVERT_MOUSE) {
         this.invertMouse = !this.invertMouse;
      }

      if (settingsOption == GameSettings.Options.GUI_SCALE) {
         this.guiScale += value;
         if (GuiScreen.isShiftKeyDown()) {
            this.guiScale = 0;
         }

         DisplayMode displayMode = Config.getLargestDisplayMode();
         int i = displayMode.getWidth() / 320;
         int i2 = displayMode.getHeight() / 240;
         int i3 = Math.min(i, i2);
         if (this.guiScale < 0) {
            this.guiScale = i3 - 1;
         }

         if (this.mc.isUnicode() && this.guiScale % 2 != 0) {
            this.guiScale += value;
         }

         if (this.guiScale < 0 || this.guiScale >= i3) {
            this.guiScale = 0;
         }
      }

      if (settingsOption == GameSettings.Options.PARTICLES) {
         this.particleSetting = (this.particleSetting + value) % 3;
      }

      if (settingsOption == GameSettings.Options.VIEW_BOBBING) {
         this.viewBobbing = !this.viewBobbing;
      }

      if (settingsOption == GameSettings.Options.RENDER_CLOUDS) {
         this.clouds = (this.clouds + value) % 3;
      }

      if (settingsOption == GameSettings.Options.FORCE_UNICODE_FONT) {
         this.forceUnicodeFont = !this.forceUnicodeFont;
         this.mc.fontRendererObj.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.forceUnicodeFont);
      }

      if (settingsOption == GameSettings.Options.FBO_ENABLE) {
         this.fboEnable = !this.fboEnable;
      }

      if (settingsOption == GameSettings.Options.ANAGLYPH) {
         if (!this.anaglyph && Config.isShaders()) {
            Config.showGuiMessage(Lang.get("of.message.an.shaders1"), Lang.get("of.message.an.shaders2"));
            return;
         }

         this.anaglyph = !this.anaglyph;
         this.mc.refreshResources();
      }

      if (settingsOption == GameSettings.Options.GRAPHICS) {
         this.fancyGraphics = !this.fancyGraphics;
         this.updateRenderClouds();
         this.mc.renderGlobal.loadRenderers();
      }

      if (settingsOption == GameSettings.Options.AMBIENT_OCCLUSION) {
         this.ambientOcclusion = (this.ambientOcclusion + value) % 3;
         this.mc.renderGlobal.loadRenderers();
      }

      if (settingsOption == GameSettings.Options.CHAT_VISIBILITY) {
         this.chatVisibility = net.minecraft.entity.player.EntityPlayer.EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + value) % 3);
      }

      if (settingsOption == GameSettings.Options.STREAM_COMPRESSION) {
         this.streamCompression = (this.streamCompression + value) % 3;
      }

      if (settingsOption == GameSettings.Options.STREAM_SEND_METADATA) {
         this.useNativeTransport = !this.useNativeTransport;
      }

      if (settingsOption == GameSettings.Options.STREAM_CHAT_ENABLED) {
         this.streamChatEnabled = (this.streamChatEnabled + value) % 3;
      }

      if (settingsOption == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
         this.streamChatUserFilter = (this.streamChatUserFilter + value) % 3;
      }

      if (settingsOption == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
         this.streamMicToggleBehavior = (this.streamMicToggleBehavior + value) % 2;
      }

      if (settingsOption == GameSettings.Options.CHAT_COLOR) {
         this.chatColours = !this.chatColours;
      }

      if (settingsOption == GameSettings.Options.CHAT_LINKS) {
         this.chatLinks = !this.chatLinks;
      }

      if (settingsOption == GameSettings.Options.CHAT_LINKS_PROMPT) {
         this.chatLinksPrompt = !this.chatLinksPrompt;
      }

      if (settingsOption == GameSettings.Options.SNOOPER_ENABLED) {
         this.fullScreen = !this.fullScreen;
      }

      if (settingsOption == GameSettings.Options.TOUCHSCREEN) {
         this.touchscreen = !this.touchscreen;
      }

      if (settingsOption == GameSettings.Options.USE_FULLSCREEN) {
         this.fullscreenOption = !this.fullscreenOption;
         if (this.mc.isFullScreen() != this.fullscreenOption) {
            this.mc.toggleFullscreen();
         }
      }

      if (settingsOption == GameSettings.Options.ENABLE_VSYNC) {
         this.enableVsync = !this.enableVsync;
         Display.setVSyncEnabled(this.enableVsync);
      }

      if (settingsOption == GameSettings.Options.USE_VBO) {
         this.useVbo = !this.useVbo;
         this.mc.renderGlobal.loadRenderers();
      }

      if (settingsOption == GameSettings.Options.BLOCK_ALTERNATIVES) {
         this.allowBlockAlternatives = !this.allowBlockAlternatives;
         this.mc.renderGlobal.loadRenderers();
      }

      if (settingsOption == GameSettings.Options.REDUCED_DEBUG_INFO) {
         this.reducedDebugInfo = !this.reducedDebugInfo;
      }

      if (settingsOption == GameSettings.Options.ENTITY_SHADOWS) {
         this.entityShadowsOption = !this.entityShadowsOption;
      }

      if (settingsOption == GameSettings.Options.REALMS_NOTIFICATIONS) {
         this.snooperEnabled = !this.snooperEnabled;
      }

      this.saveOptions();
   }

   public void setSoundLevel(SoundCategory sndCategory, float soundLevel) {
      this.mc.getSoundHandler().setSoundLevel(sndCategory, soundLevel);
      this.mapSoundLevels.put(sndCategory, soundLevel);
   }

   public void saveOfOptions() {
      try {
         PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFileOF), "UTF-8"));
         printWriter.println("ofFogType:" + this.ofFogType);
         printWriter.println("ofFogStart:" + this.ofFogStart);
         printWriter.println("ofMipmapType:" + this.ofMipmapType);
         printWriter.println("ofOcclusionFancy:" + this.ofOcclusionFancy);
         printWriter.println("ofSmoothFps:" + this.ofSmoothFps);
         printWriter.println("ofSmoothWorld:" + this.ofSmoothWorld);
         printWriter.println("ofAoLevel:" + this.ofAoLevel);
         printWriter.println("ofClouds:" + this.ofClouds);
         printWriter.println("ofCloudsHeight:" + this.ofCloudsHeight);
         printWriter.println("ofTrees:" + this.ofTrees);
         printWriter.println("ofDroppedItems:" + this.ofDroppedItems);
         printWriter.println("ofRain:" + this.ofRain);
         printWriter.println("ofAnimatedWater:" + this.ofAnimatedWater);
         printWriter.println("ofAnimatedLava:" + this.ofAnimatedLava);
         printWriter.println("ofAnimatedFire:" + this.ofAnimatedFire);
         printWriter.println("ofAnimatedPortal:" + this.ofAnimatedPortal);
         printWriter.println("ofAnimatedRedstone:" + this.ofAnimatedRedstone);
         printWriter.println("ofAnimatedExplosion:" + this.ofAnimatedExplosion);
         printWriter.println("ofAnimatedFlame:" + this.ofAnimatedFlame);
         printWriter.println("ofAnimatedSmoke:" + this.ofAnimatedSmoke);
         printWriter.println("ofVoidParticles:" + this.ofVoidParticles);
         printWriter.println("ofWaterParticles:" + this.ofWaterParticles);
         printWriter.println("ofPortalParticles:" + this.ofPortalParticles);
         printWriter.println("ofPotionParticles:" + this.ofPotionParticles);
         printWriter.println("ofFireworkParticles:" + this.ofFireworkParticles);
         printWriter.println("ofDrippingWaterLava:" + this.ofDrippingWaterLava);
         printWriter.println("ofAnimatedTerrain:" + this.ofAnimatedTerrain);
         printWriter.println("ofAnimatedTextures:" + this.ofAnimatedTextures);
         printWriter.println("ofRainSplash:" + this.ofRainSplash);
         printWriter.println("ofLagometer:" + this.ofLagometer);
         printWriter.println("ofShowFps:" + this.ofShowFps);
         printWriter.println("ofAutoSaveTicks:" + this.ofAutoSaveTicks);
         printWriter.println("ofBetterGrass:" + this.ofBetterGrass);
         printWriter.println("ofConnectedTextures:" + this.ofConnectedTextures);
         printWriter.println("ofWeather:" + this.ofWeather);
         printWriter.println("ofSky:" + this.ofSky);
         printWriter.println("ofStars:" + this.ofStars);
         printWriter.println("ofSunMoon:" + this.ofSunMoon);
         printWriter.println("ofVignette:" + this.ofVignette);
         printWriter.println("ofChunkUpdates:" + this.ofChunkUpdates);
         printWriter.println("ofChunkUpdatesDynamic:" + this.ofChunkUpdatesDynamic);
         printWriter.println("ofTime:" + this.ofTime);
         printWriter.println("ofClearWater:" + this.ofClearWater);
         printWriter.println("ofAaLevel:" + this.ofAaLevel);
         printWriter.println("ofAfLevel:" + this.ofAfLevel);
         printWriter.println("ofProfiler:" + this.ofProfiler);
         printWriter.println("ofBetterSnow:" + this.ofBetterSnow);
         printWriter.println("ofSwampColors:" + this.ofSwampColors);
         printWriter.println("ofRandomEntities:" + this.ofRandomEntities);
         printWriter.println("ofSmoothBiomes:" + this.ofSmoothBiomes);
         printWriter.println("ofCustomFonts:" + this.ofCustomFonts);
         printWriter.println("ofCustomColors:" + this.ofCustomColors);
         printWriter.println("ofCustomItems:" + this.ofCustomItems);
         printWriter.println("ofCustomSky:" + this.ofCustomSky);
         printWriter.println("ofShowCapes:" + this.ofShowCapes);
         printWriter.println("ofNaturalTextures:" + this.ofNaturalTextures);
         printWriter.println("ofEmissiveTextures:" + this.ofEmissiveTextures);
         printWriter.println("ofLazyChunkLoading:" + this.ofLazyChunkLoading);
         printWriter.println("ofRenderRegions:" + this.ofRenderRegions);
         printWriter.println("ofSmartAnimations:" + this.ofSmartAnimations);
         printWriter.println("ofDynamicFov:" + this.ofDynamicFov);
         printWriter.println("ofAlternateBlocks:" + this.ofAlternateBlocks);
         printWriter.println("ofDynamicLights:" + this.ofDynamicLights);
         printWriter.println("ofScreenshotSize:" + this.ofScreenshotSize);
         printWriter.println("ofCustomEntityModels:" + this.ofCustomEntityModels);
         printWriter.println("ofCustomGuis:" + this.ofCustomGuis);
         printWriter.println("ofShowGlErrors:" + this.ofShowGlErrors);
         printWriter.println("ofFullscreenMode:" + this.ofFullscreenMode);
         printWriter.println("ofFastMath:" + this.ofFastMath);
         printWriter.println("ofFastRender:" + this.ofFastRender);
         printWriter.println("ofTranslucentBlocks:" + this.ofTranslucentBlocks);
         printWriter.println("key_" + this.ofKeyBindZoom.getKeyDescription() + ":" + this.ofKeyBindZoom.getKeyCode());
         printWriter.close();
      } catch (Exception exception) {
         Config.warn("Failed to save options");
         exception.printStackTrace();
      }
   }

   private float getOptifineOptionFloatValue(GameSettings.Options option) {
      if (option == GameSettings.Options.CLOUD_HEIGHT) {
         return this.ofCloudsHeight;
      }

      if (option == GameSettings.Options.AO_LEVEL) {
         return this.ofAoLevel;
      }

      if (option == GameSettings.Options.AA_LEVEL) {
         return this.ofAaLevel;
      }

      if (option == GameSettings.Options.AF_LEVEL) {
         return this.ofAfLevel;
      }

      if (option == GameSettings.Options.MIPMAP_TYPE) {
         return this.ofMipmapType;
      }

      if (option != GameSettings.Options.FRAMERATE_LIMIT) {
         if (option == GameSettings.Options.FULLSCREEN_MODE) {
            if (this.ofFullscreenMode.equals("Default")) {
               return 0.0F;
            }

            int i = Arrays.asList(Config.getDisplayModeNames()).indexOf(this.ofFullscreenMode);
            return i < 0 ? 0.0F : i + 1;
         } else {
            return Float.MAX_VALUE;
         }
      } else {
         return this.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() && this.enableVsync ? 0.0F : this.limitFramerate;
      }
   }

   private static int clampToAllowedValues(int value, int[] allowedValues) {
      return indexOfValue(value, allowedValues) < 0 ? allowedValues[0] : value;
   }

   public void setModelPartEnabled(EnumPlayerModelParts modelPart, boolean enable) {
      if (enable) {
         this.setModelParts.add(modelPart);
      } else {
         this.setModelParts.remove(modelPart);
      }

      this.sendSettingsToServer();
   }

   public float getSoundLevel(SoundCategory sndCategory) {
      return this.mapSoundLevels.containsKey(sndCategory) ? this.mapSoundLevels.get(sndCategory) : 1.0F;
   }

   private String getOptifineOptionText(GameSettings.Options option) {
      String text = I18n.format(option.getEnumString()) + ": ";
      if (text == null) {
         text = option.getEnumString();
      }

      if (option == GameSettings.Options.RENDER_DISTANCE) {
         int i = (int)this.getOptionFloatValue(option);
         String text2 = I18n.format("options.renderDistance.tiny");
         byte b = 2;
         if (i >= 4) {
            text2 = I18n.format("options.renderDistance.short");
            b = 4;
         }

         if (i >= 8) {
            text2 = I18n.format("options.renderDistance.normal");
            b = 8;
         }

         if (i >= 16) {
            text2 = I18n.format("options.renderDistance.far");
            b = 16;
         }

         if (i >= 32) {
            text2 = Lang.get("of.options.renderDistance.extreme");
            b = 32;
         }

         if (i >= 48) {
            text2 = Lang.get("of.options.renderDistance.insane");
            b = 48;
         }

         if (i >= 64) {
            text2 = Lang.get("of.options.renderDistance.ludicrous");
            b = 64;
         }

         int i2 = this.renderDistanceChunks - b;
         String text3 = text2;
         if (i2 > 0) {
            text3 = text2 + "+";
         }

         return text + i + " " + text3;
      } else if (option == GameSettings.Options.FOG_FANCY) {
         switch (this.ofFogType) {
            case 1:
               return text + Lang.getFast();
            case 2:
               return text + Lang.getFancy();
            case 3:
               return text + Lang.getOff();
            default:
               return text + Lang.getOff();
         }
      } else {
         if (option == GameSettings.Options.FOG_START) {
            return text + this.ofFogStart;
         }

         if (option == GameSettings.Options.MIPMAP_TYPE) {
            switch (this.ofMipmapType) {
               case 0:
                  return text + Lang.get("of.options.mipmap.nearest");
               case 1:
                  return text + Lang.get("of.options.mipmap.linear");
               case 2:
                  return text + Lang.get("of.options.mipmap.bilinear");
               case 3:
                  return text + Lang.get("of.options.mipmap.trilinear");
               default:
                  return text + "of.options.mipmap.nearest";
            }
         } else {
            if (option == GameSettings.Options.SMOOTH_FPS) {
               return this.ofSmoothFps ? text + Lang.getOn() : text + Lang.getOff();
            }

            if (option == GameSettings.Options.SMOOTH_WORLD) {
               return this.ofSmoothWorld ? text + Lang.getOn() : text + Lang.getOff();
            }

            if (option == GameSettings.Options.CLOUDS) {
               switch (this.ofClouds) {
                  case 1:
                     return text + Lang.getFast();
                  case 2:
                     return text + Lang.getFancy();
                  case 3:
                     return text + Lang.getOff();
                  default:
                     return text + Lang.getDefault();
               }
            } else if (option == GameSettings.Options.TREES) {
               switch (this.ofTrees) {
                  case 1:
                     return text + Lang.getFast();
                  case 2:
                     return text + Lang.getFancy();
                  case 3:
                  default:
                     return text + Lang.getDefault();
                  case 4:
                     return text + Lang.get("of.general.smart");
               }
            } else if (option == GameSettings.Options.DROPPED_ITEMS) {
               switch (this.ofDroppedItems) {
                  case 1:
                     return text + Lang.getFast();
                  case 2:
                     return text + Lang.getFancy();
                  default:
                     return text + Lang.getDefault();
               }
            } else if (option == GameSettings.Options.RAIN) {
               switch (this.ofRain) {
                  case 1:
                     return text + Lang.getFast();
                  case 2:
                     return text + Lang.getFancy();
                  case 3:
                     return text + Lang.getOff();
                  default:
                     return text + Lang.getDefault();
               }
            } else if (option == GameSettings.Options.ANIMATED_WATER) {
               switch (this.ofAnimatedWater) {
                  case 1:
                     return text + Lang.get("of.options.animation.dynamic");
                  case 2:
                     return text + Lang.getOff();
                  default:
                     return text + Lang.getOn();
               }
            } else if (option == GameSettings.Options.ANIMATED_LAVA) {
               switch (this.ofAnimatedLava) {
                  case 1:
                     return text + Lang.get("of.options.animation.dynamic");
                  case 2:
                     return text + Lang.getOff();
                  default:
                     return text + Lang.getOn();
               }
            } else {
               if (option == GameSettings.Options.ANIMATED_FIRE) {
                  return this.ofAnimatedFire ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.ANIMATED_PORTAL) {
                  return this.ofAnimatedPortal ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.ANIMATED_REDSTONE) {
                  return this.ofAnimatedRedstone ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.ANIMATED_EXPLOSION) {
                  return this.ofAnimatedExplosion ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.ANIMATED_FLAME) {
                  return this.ofAnimatedFlame ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.ANIMATED_SMOKE) {
                  return this.ofAnimatedSmoke ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.VOID_PARTICLES) {
                  return this.ofVoidParticles ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.WATER_PARTICLES) {
                  return this.ofWaterParticles ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.PORTAL_PARTICLES) {
                  return this.ofPortalParticles ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.POTION_PARTICLES) {
                  return this.ofPotionParticles ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.FIREWORK_PARTICLES) {
                  return this.ofFireworkParticles ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.DRIPPING_WATER_LAVA) {
                  return this.ofDrippingWaterLava ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.ANIMATED_TERRAIN) {
                  return this.ofAnimatedTerrain ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.ANIMATED_TEXTURES) {
                  return this.ofAnimatedTextures ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.RAIN_SPLASH) {
                  return this.ofRainSplash ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.LAGOMETER) {
                  return this.ofLagometer ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.SHOW_FPS) {
                  return this.ofShowFps ? text + Lang.getOn() : text + Lang.getOff();
               }

               if (option == GameSettings.Options.AUTOSAVE_TICKS) {
                  return this.ofAutoSaveTicks <= 900
                     ? text + Lang.get("of.options.save.45s")
                     : (
                        this.ofAutoSaveTicks <= 1800
                           ? text + Lang.get("of.options.save.90s")
                           : (
                              this.ofAutoSaveTicks <= 3600
                                 ? text + Lang.get("of.options.save.3min")
                                 : (
                                    this.ofAutoSaveTicks <= 7200
                                       ? text + Lang.get("of.options.save.6min")
                                       : (this.ofAutoSaveTicks <= 14400 ? text + Lang.get("of.options.save.12min") : text + Lang.get("of.options.save.24min"))
                                 )
                           )
                     );
               }

               if (option == GameSettings.Options.BETTER_GRASS) {
                  switch (this.ofBetterGrass) {
                     case 1:
                        return text + Lang.getFast();
                     case 2:
                        return text + Lang.getFancy();
                     default:
                        return text + Lang.getOff();
                  }
               } else if (option == GameSettings.Options.CONNECTED_TEXTURES) {
                  switch (this.ofConnectedTextures) {
                     case 1:
                        return text + Lang.getFast();
                     case 2:
                        return text + Lang.getFancy();
                     default:
                        return text + Lang.getOff();
                  }
               } else {
                  if (option == GameSettings.Options.WEATHER) {
                     return this.ofWeather ? text + Lang.getOn() : text + Lang.getOff();
                  }

                  if (option == GameSettings.Options.SKY) {
                     return this.ofSky ? text + Lang.getOn() : text + Lang.getOff();
                  }

                  if (option == GameSettings.Options.STARS) {
                     return this.ofStars ? text + Lang.getOn() : text + Lang.getOff();
                  }

                  if (option == GameSettings.Options.SUN_MOON) {
                     return this.ofSunMoon ? text + Lang.getOn() : text + Lang.getOff();
                  }

                  if (option == GameSettings.Options.VIGNETTE) {
                     switch (this.ofVignette) {
                        case 1:
                           return text + Lang.getFast();
                        case 2:
                           return text + Lang.getFancy();
                        default:
                           return text + Lang.getDefault();
                     }
                  } else {
                     if (option == GameSettings.Options.CHUNK_UPDATES) {
                        return text + this.ofChunkUpdates;
                     }

                     if (option == GameSettings.Options.CHUNK_UPDATES_DYNAMIC) {
                        return this.ofChunkUpdatesDynamic ? text + Lang.getOn() : text + Lang.getOff();
                     }

                     if (option == GameSettings.Options.TIME) {
                        return this.ofTime == 1
                           ? text + Lang.get("of.options.time.dayOnly")
                           : (this.ofTime == 2 ? text + Lang.get("of.options.time.nightOnly") : text + Lang.getDefault());
                     }

                     if (option == GameSettings.Options.CLEAR_WATER) {
                        return this.ofClearWater ? text + Lang.getOn() : text + Lang.getOff();
                     }

                     if (option == GameSettings.Options.AA_LEVEL) {
                        String text4 = "";
                        if (this.ofAaLevel != Config.getAntialiasingLevel()) {
                           text4 = " (" + Lang.get("of.general.restart") + ")";
                        }

                        return this.ofAaLevel == 0 ? text + Lang.getOff() + text4 : text + this.ofAaLevel + text4;
                     } else if (option == GameSettings.Options.AF_LEVEL) {
                        return this.ofAfLevel == 1 ? text + Lang.getOff() : text + this.ofAfLevel;
                     } else if (option == GameSettings.Options.PROFILER) {
                        return this.ofProfiler ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.BETTER_SNOW) {
                        return this.ofBetterSnow ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.SWAMP_COLORS) {
                        return this.ofSwampColors ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.RANDOM_ENTITIES) {
                        return this.ofRandomEntities ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.SMOOTH_BIOMES) {
                        return this.ofSmoothBiomes ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.CUSTOM_FONTS) {
                        return this.ofCustomFonts ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.CUSTOM_COLORS) {
                        return this.ofCustomColors ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.CUSTOM_SKY) {
                        return this.ofCustomSky ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.SHOW_CAPES) {
                        return this.ofShowCapes ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.CUSTOM_ITEMS) {
                        return this.ofCustomItems ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.NATURAL_TEXTURES) {
                        return this.ofNaturalTextures ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.EMISSIVE_TEXTURES) {
                        return this.ofEmissiveTextures ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.FAST_MATH) {
                        return this.ofFastMath ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.TRANSLUCENT_BLOCKS) {
                        return this.ofTranslucentBlocks == 1
                           ? text + Lang.getFast()
                           : (this.ofTranslucentBlocks == 2 ? text + Lang.getFancy() : text + Lang.getDefault());
                     } else if (option == GameSettings.Options.LAZY_CHUNK_LOADING) {
                        return this.ofLazyChunkLoading ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.RENDER_REGIONS) {
                        return this.ofRenderRegions ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.SMART_ANIMATIONS) {
                        return this.ofSmartAnimations ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.DYNAMIC_FOV) {
                        return this.ofDynamicFov ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.ALTERNATE_BLOCKS) {
                        return this.ofAlternateBlocks ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.DYNAMIC_LIGHTS) {
                        int i3 = indexOfValue(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
                        return text + getTranslation(KEYS_DYNAMIC_LIGHTS, i3);
                     } else if (option == GameSettings.Options.SCREENSHOT_SIZE) {
                        return this.ofScreenshotSize <= 1 ? text + Lang.getDefault() : text + this.ofScreenshotSize + "x";
                     } else if (option == GameSettings.Options.CUSTOM_ENTITY_MODELS) {
                        return this.ofCustomEntityModels ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.CUSTOM_GUIS) {
                        return this.ofCustomGuis ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.SHOW_GL_ERRORS) {
                        return this.ofShowGlErrors ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.FULLSCREEN_MODE) {
                        return this.ofFullscreenMode.equals("Default") ? text + Lang.getDefault() : text + this.ofFullscreenMode;
                     } else if (option == GameSettings.Options.HELD_ITEM_TOOLTIPS) {
                        return this.heldItemTooltips ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.ADVANCED_TOOLTIPS) {
                        return this.advancedItemTooltips ? text + Lang.getOn() : text + Lang.getOff();
                     } else if (option == GameSettings.Options.FRAMERATE_LIMIT) {
                        float optionFloatValue = this.getOptionFloatValue(option);
                        return optionFloatValue == 0.0F
                           ? text + Lang.get("of.options.framerateLimit.vsync")
                           : (optionFloatValue == option.valueMax ? text + I18n.format("options.framerateLimit.max") : text + (int)optionFloatValue + " fps");
                     } else {
                        return null;
                     }
                  }
               }
            }
         }
      }
   }

   public void loadOfOptions() {
      try {
         File file = this.optionsFileOF;
         if (!file.exists()) {
            file = this.optionsFile;
         }

         if (!file.exists()) {
            return;
         }

         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

         String text;
         while ((text = bufferedReader.readLine()) != null) {
            try {
               String[] stringArray = text.split(":");
               if (stringArray[0].equals("ofRenderDistanceChunks") && stringArray.length >= 2) {
                  this.renderDistanceChunks = Integer.valueOf(stringArray[1]);
                  this.renderDistanceChunks = Config.limit(this.renderDistanceChunks, 2, 1024);
               }

               if (stringArray[0].equals("ofFogType") && stringArray.length >= 2) {
                  this.ofFogType = Integer.valueOf(stringArray[1]);
                  this.ofFogType = Config.limit(this.ofFogType, 1, 3);
               }

               if (stringArray[0].equals("ofFogStart") && stringArray.length >= 2) {
                  this.ofFogStart = Float.valueOf(stringArray[1]);
                  if (this.ofFogStart < 0.2F) {
                     this.ofFogStart = 0.2F;
                  }

                  if (this.ofFogStart > 0.81F) {
                     this.ofFogStart = 0.8F;
                  }
               }

               if (stringArray[0].equals("ofMipmapType") && stringArray.length >= 2) {
                  this.ofMipmapType = Integer.valueOf(stringArray[1]);
                  this.ofMipmapType = Config.limit(this.ofMipmapType, 0, 3);
               }

               if (stringArray[0].equals("ofOcclusionFancy") && stringArray.length >= 2) {
                  this.ofOcclusionFancy = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofSmoothFps") && stringArray.length >= 2) {
                  this.ofSmoothFps = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofSmoothWorld") && stringArray.length >= 2) {
                  this.ofSmoothWorld = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofAoLevel") && stringArray.length >= 2) {
                  this.ofAoLevel = Float.valueOf(stringArray[1]);
                  this.ofAoLevel = Config.limit(this.ofAoLevel, 0.0F, 1.0F);
               }

               if (stringArray[0].equals("ofClouds") && stringArray.length >= 2) {
                  this.ofClouds = Integer.valueOf(stringArray[1]);
                  this.ofClouds = Config.limit(this.ofClouds, 0, 3);
                  this.updateRenderClouds();
               }

               if (stringArray[0].equals("ofCloudsHeight") && stringArray.length >= 2) {
                  this.ofCloudsHeight = Float.valueOf(stringArray[1]);
                  this.ofCloudsHeight = Config.limit(this.ofCloudsHeight, 0.0F, 1.0F);
               }

               if (stringArray[0].equals("ofTrees") && stringArray.length >= 2) {
                  this.ofTrees = Integer.valueOf(stringArray[1]);
                  this.ofTrees = clampToAllowedValues(this.ofTrees, OF_TREES_VALUES);
               }

               if (stringArray[0].equals("ofDroppedItems") && stringArray.length >= 2) {
                  this.ofDroppedItems = Integer.valueOf(stringArray[1]);
                  this.ofDroppedItems = Config.limit(this.ofDroppedItems, 0, 2);
               }

               if (stringArray[0].equals("ofRain") && stringArray.length >= 2) {
                  this.ofRain = Integer.valueOf(stringArray[1]);
                  this.ofRain = Config.limit(this.ofRain, 0, 3);
               }

               if (stringArray[0].equals("ofAnimatedWater") && stringArray.length >= 2) {
                  this.ofAnimatedWater = Integer.valueOf(stringArray[1]);
                  this.ofAnimatedWater = Config.limit(this.ofAnimatedWater, 0, 2);
               }

               if (stringArray[0].equals("ofAnimatedLava") && stringArray.length >= 2) {
                  this.ofAnimatedLava = Integer.valueOf(stringArray[1]);
                  this.ofAnimatedLava = Config.limit(this.ofAnimatedLava, 0, 2);
               }

               if (stringArray[0].equals("ofAnimatedFire") && stringArray.length >= 2) {
                  this.ofAnimatedFire = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofAnimatedPortal") && stringArray.length >= 2) {
                  this.ofAnimatedPortal = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofAnimatedRedstone") && stringArray.length >= 2) {
                  this.ofAnimatedRedstone = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofAnimatedExplosion") && stringArray.length >= 2) {
                  this.ofAnimatedExplosion = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofAnimatedFlame") && stringArray.length >= 2) {
                  this.ofAnimatedFlame = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofAnimatedSmoke") && stringArray.length >= 2) {
                  this.ofAnimatedSmoke = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofVoidParticles") && stringArray.length >= 2) {
                  this.ofVoidParticles = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofWaterParticles") && stringArray.length >= 2) {
                  this.ofWaterParticles = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofPortalParticles") && stringArray.length >= 2) {
                  this.ofPortalParticles = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofPotionParticles") && stringArray.length >= 2) {
                  this.ofPotionParticles = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofFireworkParticles") && stringArray.length >= 2) {
                  this.ofFireworkParticles = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofDrippingWaterLava") && stringArray.length >= 2) {
                  this.ofDrippingWaterLava = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofAnimatedTerrain") && stringArray.length >= 2) {
                  this.ofAnimatedTerrain = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofAnimatedTextures") && stringArray.length >= 2) {
                  this.ofAnimatedTextures = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofRainSplash") && stringArray.length >= 2) {
                  this.ofRainSplash = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofLagometer") && stringArray.length >= 2) {
                  this.ofLagometer = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofShowFps") && stringArray.length >= 2) {
                  this.ofShowFps = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofAutoSaveTicks") && stringArray.length >= 2) {
                  this.ofAutoSaveTicks = Integer.valueOf(stringArray[1]);
                  this.ofAutoSaveTicks = Config.limit(this.ofAutoSaveTicks, 40, 40000);
               }

               if (stringArray[0].equals("ofBetterGrass") && stringArray.length >= 2) {
                  this.ofBetterGrass = Integer.valueOf(stringArray[1]);
                  this.ofBetterGrass = Config.limit(this.ofBetterGrass, 1, 3);
               }

               if (stringArray[0].equals("ofConnectedTextures") && stringArray.length >= 2) {
                  this.ofConnectedTextures = Integer.valueOf(stringArray[1]);
                  this.ofConnectedTextures = Config.limit(this.ofConnectedTextures, 1, 3);
               }

               if (stringArray[0].equals("ofWeather") && stringArray.length >= 2) {
                  this.ofWeather = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofSky") && stringArray.length >= 2) {
                  this.ofSky = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofStars") && stringArray.length >= 2) {
                  this.ofStars = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofSunMoon") && stringArray.length >= 2) {
                  this.ofSunMoon = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofVignette") && stringArray.length >= 2) {
                  this.ofVignette = Integer.valueOf(stringArray[1]);
                  this.ofVignette = Config.limit(this.ofVignette, 0, 2);
               }

               if (stringArray[0].equals("ofChunkUpdates") && stringArray.length >= 2) {
                  this.ofChunkUpdates = Integer.valueOf(stringArray[1]);
                  this.ofChunkUpdates = Config.limit(this.ofChunkUpdates, 1, 5);
               }

               if (stringArray[0].equals("ofChunkUpdatesDynamic") && stringArray.length >= 2) {
                  this.ofChunkUpdatesDynamic = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofTime") && stringArray.length >= 2) {
                  this.ofTime = Integer.valueOf(stringArray[1]);
                  this.ofTime = Config.limit(this.ofTime, 0, 2);
               }

               if (stringArray[0].equals("ofClearWater") && stringArray.length >= 2) {
                  this.ofClearWater = Boolean.valueOf(stringArray[1]);
                  this.updateWaterOpacity();
               }

               if (stringArray[0].equals("ofAaLevel") && stringArray.length >= 2) {
                  this.ofAaLevel = Integer.valueOf(stringArray[1]);
                  this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
               }

               if (stringArray[0].equals("ofAfLevel") && stringArray.length >= 2) {
                  this.ofAfLevel = Integer.valueOf(stringArray[1]);
                  this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
               }

               if (stringArray[0].equals("ofProfiler") && stringArray.length >= 2) {
                  this.ofProfiler = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofBetterSnow") && stringArray.length >= 2) {
                  this.ofBetterSnow = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofSwampColors") && stringArray.length >= 2) {
                  this.ofSwampColors = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofRandomEntities") && stringArray.length >= 2) {
                  this.ofRandomEntities = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofSmoothBiomes") && stringArray.length >= 2) {
                  this.ofSmoothBiomes = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofCustomFonts") && stringArray.length >= 2) {
                  this.ofCustomFonts = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofCustomColors") && stringArray.length >= 2) {
                  this.ofCustomColors = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofCustomItems") && stringArray.length >= 2) {
                  this.ofCustomItems = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofCustomSky") && stringArray.length >= 2) {
                  this.ofCustomSky = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofShowCapes") && stringArray.length >= 2) {
                  this.ofShowCapes = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofNaturalTextures") && stringArray.length >= 2) {
                  this.ofNaturalTextures = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofEmissiveTextures") && stringArray.length >= 2) {
                  this.ofEmissiveTextures = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofLazyChunkLoading") && stringArray.length >= 2) {
                  this.ofLazyChunkLoading = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofRenderRegions") && stringArray.length >= 2) {
                  this.ofRenderRegions = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofSmartAnimations") && stringArray.length >= 2) {
                  this.ofSmartAnimations = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofDynamicFov") && stringArray.length >= 2) {
                  this.ofDynamicFov = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofAlternateBlocks") && stringArray.length >= 2) {
                  this.ofAlternateBlocks = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofDynamicLights") && stringArray.length >= 2) {
                  this.ofDynamicLights = Integer.valueOf(stringArray[1]);
                  this.ofDynamicLights = clampToAllowedValues(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
               }

               if (stringArray[0].equals("ofScreenshotSize") && stringArray.length >= 2) {
                  this.ofScreenshotSize = Integer.valueOf(stringArray[1]);
                  this.ofScreenshotSize = Config.limit(this.ofScreenshotSize, 1, 4);
               }

               if (stringArray[0].equals("ofCustomEntityModels") && stringArray.length >= 2) {
                  this.ofCustomEntityModels = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofCustomGuis") && stringArray.length >= 2) {
                  this.ofCustomGuis = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofShowGlErrors") && stringArray.length >= 2) {
                  this.ofShowGlErrors = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofFullscreenMode") && stringArray.length >= 2) {
                  this.ofFullscreenMode = stringArray[1];
               }

               if (stringArray[0].equals("ofFastMath") && stringArray.length >= 2) {
                  this.ofFastMath = Boolean.valueOf(stringArray[1]);
                  MathHelper.fastMath = this.ofFastMath;
               }

               if (stringArray[0].equals("ofFastRender") && stringArray.length >= 2) {
                  this.ofFastRender = Boolean.valueOf(stringArray[1]);
               }

               if (stringArray[0].equals("ofTranslucentBlocks") && stringArray.length >= 2) {
                  this.ofTranslucentBlocks = Integer.valueOf(stringArray[1]);
                  this.ofTranslucentBlocks = Config.limit(this.ofTranslucentBlocks, 0, 2);
               }

               if (stringArray[0].equals("key_" + this.ofKeyBindZoom.getKeyDescription())) {
                  this.ofKeyBindZoom.setKeyCode(Integer.parseInt(stringArray[1]));
               }
            } catch (Exception exception) {
               Config.dbg("Skipping bad option: " + text);
               exception.printStackTrace();
            }
         }

         KeyUtils.fixKeyConflicts(this.keyBindings, new KeyBinding[]{this.ofKeyBindZoom});
         KeyBinding.resetKeyBindingArrayAndHash();
         bufferedReader.close();
      } catch (Exception exception2) {
         Config.warn("Failed to load options");
         exception2.printStackTrace();
      }
   }

   public enum Options {
      INVERT_MOUSE("options.invertMouse", false, true),
      SENSITIVITY("options.sensitivity", true, false),
      FOV("options.fov", true, false, 30.0F, 110.0F, 1.0F),
      GAMMA("options.gamma", true, false),
      SATURATION("options.saturation", true, false),
      RENDER_DISTANCE("options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
      VIEW_BOBBING("options.viewBobbing", false, true),
      ANAGLYPH("options.anaglyph", false, true),
      FRAMERATE_LIMIT("options.framerateLimit", true, false, 0.0F, 260.0F, 5.0F),
      FBO_ENABLE("options.fboEnable", false, true),
      RENDER_CLOUDS("options.renderClouds", false, false),
      GRAPHICS("options.graphics", false, false),
      AMBIENT_OCCLUSION("options.ao", false, false),
      GUI_SCALE("options.guiScale", false, false),
      PARTICLES("options.particles", false, false),
      CHAT_VISIBILITY("options.chat.visibility", false, false),
      CHAT_COLOR("options.chat.color", false, true),
      CHAT_LINKS("options.chat.links", false, true),
      CHAT_OPACITY("options.chat.opacity", true, false),
      CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
      SNOOPER_ENABLED("options.snooper", false, true),
      USE_FULLSCREEN("options.fullscreen", false, true),
      ENABLE_VSYNC("options.vsync", false, true),
      USE_VBO("options.vbo", false, true),
      TOUCHSCREEN("options.touchscreen", false, true),
      CHAT_SCALE("options.chat.scale", true, false),
      CHAT_WIDTH("options.chat.width", true, false),
      CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
      CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
      MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
      FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true),
      STREAM_BYTES_PER_PIXEL("options.stream.bytesPerPixel", true, false),
      STREAM_VOLUME_MIC("options.stream.micVolumne", true, false),
      STREAM_VOLUME_SYSTEM("options.stream.systemVolume", true, false),
      STREAM_KBPS("options.stream.kbps", true, false),
      STREAM_FPS("options.stream.fps", true, false),
      STREAM_COMPRESSION("options.stream.compression", false, false),
      STREAM_SEND_METADATA("options.stream.sendMetadata", false, true),
      STREAM_CHAT_ENABLED("options.stream.chat.enabled", false, false),
      STREAM_CHAT_USER_FILTER("options.stream.chat.userFilter", false, false),
      STREAM_MIC_TOGGLE_BEHAVIOR("options.stream.micToggleBehavior", false, false),
      BLOCK_ALTERNATIVES("options.blockAlternatives", false, true),
      REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
      ENTITY_SHADOWS("options.entityShadows", false, true),
      REALMS_NOTIFICATIONS("options.realmsNotifications", false, true),
      FOG_FANCY("of.options.FOG_FANCY", false, false),
      FOG_START("of.options.FOG_START", false, false),
      MIPMAP_TYPE("of.options.MIPMAP_TYPE", true, false, 0.0F, 3.0F, 1.0F),
      SMOOTH_FPS("of.options.SMOOTH_FPS", false, false),
      CLOUDS("of.options.CLOUDS", false, false),
      CLOUD_HEIGHT("of.options.CLOUD_HEIGHT", true, false),
      TREES("of.options.TREES", false, false),
      RAIN("of.options.RAIN", false, false),
      ANIMATED_WATER("of.options.ANIMATED_WATER", false, false),
      ANIMATED_LAVA("of.options.ANIMATED_LAVA", false, false),
      ANIMATED_FIRE("of.options.ANIMATED_FIRE", false, false),
      ANIMATED_PORTAL("of.options.ANIMATED_PORTAL", false, false),
      AO_LEVEL("of.options.AO_LEVEL", true, false),
      LAGOMETER("of.options.LAGOMETER", false, false),
      SHOW_FPS("of.options.SHOW_FPS", false, false),
      AUTOSAVE_TICKS("of.options.AUTOSAVE_TICKS", false, false),
      BETTER_GRASS("of.options.BETTER_GRASS", false, false),
      ANIMATED_REDSTONE("of.options.ANIMATED_REDSTONE", false, false),
      ANIMATED_EXPLOSION("of.options.ANIMATED_EXPLOSION", false, false),
      ANIMATED_FLAME("of.options.ANIMATED_FLAME", false, false),
      ANIMATED_SMOKE("of.options.ANIMATED_SMOKE", false, false),
      WEATHER("of.options.WEATHER", false, false),
      SKY("of.options.SKY", false, false),
      STARS("of.options.STARS", false, false),
      SUN_MOON("of.options.SUN_MOON", false, false),
      VIGNETTE("of.options.VIGNETTE", false, false),
      CHUNK_UPDATES("of.options.CHUNK_UPDATES", false, false),
      CHUNK_UPDATES_DYNAMIC("of.options.CHUNK_UPDATES_DYNAMIC", false, false),
      TIME("of.options.TIME", false, false),
      CLEAR_WATER("of.options.CLEAR_WATER", false, false),
      SMOOTH_WORLD("of.options.SMOOTH_WORLD", false, false),
      VOID_PARTICLES("of.options.VOID_PARTICLES", false, false),
      WATER_PARTICLES("of.options.WATER_PARTICLES", false, false),
      RAIN_SPLASH("of.options.RAIN_SPLASH", false, false),
      PORTAL_PARTICLES("of.options.PORTAL_PARTICLES", false, false),
      POTION_PARTICLES("of.options.POTION_PARTICLES", false, false),
      FIREWORK_PARTICLES("of.options.FIREWORK_PARTICLES", false, false),
      PROFILER("of.options.PROFILER", false, false),
      DRIPPING_WATER_LAVA("of.options.DRIPPING_WATER_LAVA", false, false),
      BETTER_SNOW("of.options.BETTER_SNOW", false, false),
      FULLSCREEN_MODE("of.options.FULLSCREEN_MODE", true, false, 0.0F, Config.getDisplayModes().length, 1.0F),
      ANIMATED_TERRAIN("of.options.ANIMATED_TERRAIN", false, false),
      SWAMP_COLORS("of.options.SWAMP_COLORS", false, false),
      RANDOM_ENTITIES("of.options.RANDOM_ENTITIES", false, false),
      SMOOTH_BIOMES("of.options.SMOOTH_BIOMES", false, false),
      CUSTOM_FONTS("of.options.CUSTOM_FONTS", false, false),
      CUSTOM_COLORS("of.options.CUSTOM_COLORS", false, false),
      SHOW_CAPES("of.options.SHOW_CAPES", false, false),
      CONNECTED_TEXTURES("of.options.CONNECTED_TEXTURES", false, false),
      CUSTOM_ITEMS("of.options.CUSTOM_ITEMS", false, false),
      AA_LEVEL("of.options.AA_LEVEL", true, false, 0.0F, 16.0F, 1.0F),
      AF_LEVEL("of.options.AF_LEVEL", true, false, 1.0F, 16.0F, 1.0F),
      ANIMATED_TEXTURES("of.options.ANIMATED_TEXTURES", false, false),
      NATURAL_TEXTURES("of.options.NATURAL_TEXTURES", false, false),
      EMISSIVE_TEXTURES("of.options.EMISSIVE_TEXTURES", false, false),
      HELD_ITEM_TOOLTIPS("of.options.HELD_ITEM_TOOLTIPS", false, false),
      DROPPED_ITEMS("of.options.DROPPED_ITEMS", false, false),
      LAZY_CHUNK_LOADING("of.options.LAZY_CHUNK_LOADING", false, false),
      CUSTOM_SKY("of.options.CUSTOM_SKY", false, false),
      FAST_MATH("of.options.FAST_MATH", false, false),
      TRANSLUCENT_BLOCKS("of.options.TRANSLUCENT_BLOCKS", false, false),
      DYNAMIC_FOV("of.options.DYNAMIC_FOV", false, false),
      DYNAMIC_LIGHTS("of.options.DYNAMIC_LIGHTS", false, false),
      ALTERNATE_BLOCKS("of.options.ALTERNATE_BLOCKS", false, false),
      CUSTOM_ENTITY_MODELS("of.options.CUSTOM_ENTITY_MODELS", false, false),
      ADVANCED_TOOLTIPS("of.options.ADVANCED_TOOLTIPS", false, false),
      SCREENSHOT_SIZE("of.options.SCREENSHOT_SIZE", false, false),
      CUSTOM_GUIS("of.options.CUSTOM_GUIS", false, false),
      RENDER_REGIONS("of.options.RENDER_REGIONS", false, false),
      SHOW_GL_ERRORS("of.options.SHOW_GL_ERRORS", false, false),
      SMART_ANIMATIONS("of.options.SMART_ANIMATIONS", false, false);

      private final String enumString;
      private final boolean enumFloat;
      private final boolean enumBoolean;
      private float valueMin;
      private float valueMax;
      private final float valueStep;

      public boolean getEnumFloat() {
         return this.enumFloat;
      }

      Options(String text, boolean flag, boolean flag2, float f2, float f3, float f4) {
         this.enumString = text;
         this.enumFloat = flag;
         this.enumBoolean = flag2;
         this.valueMin = f2;
         this.valueMax = f3;
         this.valueStep = f4;
      }

      public String getEnumString() {
         return this.enumString;
      }

      public float getValueMax() {
         return this.valueMax;
      }

      protected float snapToStep(float value) {
         if (this.valueStep > 0.0F) {
            value = this.valueStep * Math.round(value / this.valueStep);
         }

         return value;
      }

      public float snapToStepClamp(float value) {
         value = this.snapToStep(value);
         return MathHelper.clamp_float(value, this.valueMin, this.valueMax);
      }

      public boolean getEnumBoolean() {
         return this.enumBoolean;
      }

      public float denormalizeValue(float value) {
         return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp_float(value, 0.0F, 1.0F));
      }

      public static GameSettings.Options getEnumOptions(int ordinal) {
         for (GameSettings.Options options2 : values()) {
            if (options2.returnEnumOrdinal() == ordinal) {
               return options2;
            }
         }

         return null;
      }

      Options(String text, boolean flag, boolean flag2) {
         this(text, flag, flag2, 0.0F, 1.0F, 0.0F);
      }

      public int returnEnumOrdinal() {
         return this.ordinal();
      }

      public void setValueMax(float value) {
         this.valueMax = value;
      }

      public float normalizeValue(float value) {
         return MathHelper.clamp_float((this.snapToStepClamp(value) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
      }
   }
}
