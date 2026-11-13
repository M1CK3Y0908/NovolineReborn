package net.optifine.shaders;

import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.optifine.CustomBlockLayers;
import net.optifine.CustomColors;
import net.optifine.Lang;
import net.optifine.config.ConnectedParser;
import net.optifine.expr.IExpressionBool;
import net.optifine.reflect.Reflector;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.config.EnumShaderOption;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.shaders.config.MacroState;
import net.optifine.shaders.config.PropertyDefaultFastFancyOff;
import net.optifine.shaders.config.PropertyDefaultTrueFalse;
import net.optifine.shaders.config.RenderScale;
import net.optifine.shaders.config.ScreenShaderOptions;
import net.optifine.shaders.config.ShaderLine;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderOptionProfile;
import net.optifine.shaders.config.ShaderOptionRest;
import net.optifine.shaders.config.ShaderPackParser;
import net.optifine.shaders.config.ShaderParser;
import net.optifine.shaders.config.ShaderProfile;
import net.optifine.shaders.uniform.CustomUniforms;
import net.optifine.shaders.uniform.ShaderUniform1f;
import net.optifine.shaders.uniform.ShaderUniform1i;
import net.optifine.shaders.uniform.ShaderUniform2i;
import net.optifine.shaders.uniform.ShaderUniform3f;
import net.optifine.shaders.uniform.ShaderUniform4f;
import net.optifine.shaders.uniform.ShaderUniform4i;
import net.optifine.shaders.uniform.ShaderUniformM4;
import net.optifine.shaders.uniform.ShaderUniforms;
import net.optifine.shaders.uniform.Smoother;
import net.optifine.texture.InternalFormat;
import net.optifine.texture.PixelFormat;
import net.optifine.texture.PixelType;
import net.optifine.texture.TextureType;
import net.optifine.util.EntityUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBGeometryShader4;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector4f;
import wtf.novoline.util.GlErrorThrottle;
import wtf.novoline.util.ReportThrottle;

public class Shaders {
   private static final int STAGE_COMPOSITE = 1;
   public static final String OFF = "OFF";
   public static final String text = "(internal)";
   public static final String SHADERPACKS = "shaderpacks";
   public static final String OPTIONSSHADERS_TXT = "optionsshaders.txt";
   private static boolean isHandRenderedMain;
   public static final String SHADERS_SHADERS_PROPERTIES = "/shaders/shaders.properties";
   public static boolean isInitializedOnce = false;
   public static boolean isShaderPackInitialized = false;
   public static boolean hasGlGenMipmap = false;
   public static int countResetDisplayLists = 0;
   private static int renderDisplayWidth = 0;
   public static float fogColorG;
   private static int renderDisplayHeight = 0;
   public static int renderWidth = 0;
   public static int renderHeight = 0;
   public static boolean isRenderingWorld = false;
   public static boolean isRenderingSky = false;
   public static boolean isCompositeRendered = false;
   public static boolean isRenderingDfb = false;
   public static boolean isShadowPass = false;
   public static boolean isEntitiesGlowing = false;
   public static boolean renderItemKeepDepthMask = false;
   public static boolean itemToRenderMainTranslucent = false;
   public static boolean itemToRenderOffTranslucent = false;
   static float[] sunPosition = new float[4];
   static float[] moonPosition = new float[4];
   private static boolean skipRenderHandMain;
   static float[] shadowLightPosition = new float[4];
   static float[] upPosition = new float[4];
   static float[] shadowLightPositionVector = new float[4];
   static float[] upPosModelView = new float[]{0.0F, 100.0F, 0.0F, 0.0F};
   static float[] sunPosModelView = new float[]{0.0F, 100.0F, 0.0F, 0.0F};
   static float[] moonPosModelView = new float[]{0.0F, -100.0F, 0.0F, 0.0F};
   private static float[] tempMat = new float[16];
   static long worldTime = 0L;
   static long lastWorldTime = 0L;
   static long diffWorldTime = 0L;
   static float skyColorR;
   static float celestialAngle = 0.0F;
   static float sunAngle = 0.0F;
   static float shadowAngle = 0.0F;
   static int moonPhase = 0;
   public static float fogColorR;
   static long systemTime = 0L;
   static long lastSystemTime = 0L;
   static long diffSystemTime = 0L;
   static int frameCounter = 0;
   static float frameTime = 0.0F;
   static float frameTimeCounter = 0.0F;
   static int systemTimeInt32 = 0;
   static float rainStrength = 0.0F;
   static float wetness = 0.0F;
   public static float wetnessHalfLife = 600.0F;
   public static float drynessHalfLife = 200.0F;
   public static float eyeBrightnessHalflife = 10.0F;
   static boolean usewetness = false;
   public static final int intValue = 3;
   static int isEyeInWater = 0;
   static int eyeBrightness = 0;
   static float eyeBrightnessFadeX = 0.0F;
   static float eyeBrightnessFadeY = 0.0F;
   static double previousCameraPositionY;
   static float eyePosY = 0.0F;
   static float centerDepth = 0.0F;
   static float centerDepthSmooth = 0.0F;
   static float centerDepthSmoothHalflife = 1.0F;
   static boolean centerDepthSmoothEnabled = false;
   static int superSamplingLevel = 1;
   static float nightVision = 0.0F;
   static float blindness = 0.0F;
   static float skyColorG;
   static boolean lightmapEnabled = false;
   static boolean fogEnabled = true;
   public static int entityAttrib = 10;
   public static int midTexCoordAttrib = 11;
   private static final int intValue2 = 2;
   public static int tangentAttrib = 12;
   public static boolean useEntityAttrib = false;
   public static float fogColorB;
   public static boolean useMidTexCoordAttrib = false;
   static float skyColorB;
   public static boolean useTangentAttrib = false;
   public static boolean progUseEntityAttrib = false;
   public static boolean progUseMidTexCoordAttrib = false;
   public static boolean progUseTangentAttrib = false;
   private static boolean progArbGeometryShader4 = false;
   private static int progMaxVerticesOut = 3;
   private static boolean hasGeometryShaders = false;
   public static int atlasSizeX = 0;
   public static int atlasSizeY = 0;
   private static ShaderUniforms shaderUniforms = new ShaderUniforms();
   public static ShaderUniform4f uniform_entityColor = shaderUniforms.make4f("entityColor");
   public static ShaderUniform1i uniform_entityId = shaderUniforms.make1i("entityId");
   public static ShaderUniform1i uniform_blockEntityId = shaderUniforms.make1i("blockEntityId");
   public static ShaderUniform1i uniform_texture = shaderUniforms.make1i("texture");
   public static ShaderUniform1i uniform_lightmap = shaderUniforms.make1i("lightmap");
   public static ShaderUniform1i uniform_normals = shaderUniforms.make1i("normals");
   public static ShaderUniform1i uniform_specular = shaderUniforms.make1i("specular");
   public static ShaderUniform1i uniform_shadow = shaderUniforms.make1i("shadow");
   public static ShaderUniform1i uniform_watershadow = shaderUniforms.make1i("watershadow");
   public static ShaderUniform1i uniform_shadowtex0 = shaderUniforms.make1i("shadowtex0");
   public static ShaderUniform1i uniform_shadowtex1 = shaderUniforms.make1i("shadowtex1");
   public static String currentShaderName;
   public static ShaderUniform1i uniform_depthtex0 = shaderUniforms.make1i("depthtex0");
   public static ShaderUniform1i uniform_depthtex1 = shaderUniforms.make1i("depthtex1");
   public static ShaderUniform1i uniform_shadowcolor = shaderUniforms.make1i("shadowcolor");
   public static ShaderUniform1i uniform_shadowcolor0 = shaderUniforms.make1i("shadowcolor0");
   public static ShaderUniform1i uniform_shadowcolor1 = shaderUniforms.make1i("shadowcolor1");
   public static ShaderUniform1i uniform_noisetex = shaderUniforms.make1i("noisetex");
   public static ShaderUniform1i uniform_gcolor = shaderUniforms.make1i("gcolor");
   static double previousCameraPositionX;
   static int cameraOffsetX;
   public static ShaderUniform1i uniform_gdepth = shaderUniforms.make1i("gdepth");
   public static ShaderUniform1i uniform_gnormal = shaderUniforms.make1i("gnormal");
   static double previousCameraPositionZ;
   public static ShaderUniform1i uniform_composite = shaderUniforms.make1i("composite");
   public static ShaderUniform1i uniform_gaux1 = shaderUniforms.make1i("gaux1");
   public static ShaderUniform1i uniform_gaux2 = shaderUniforms.make1i("gaux2");
   public static ShaderUniform1i uniform_gaux3 = shaderUniforms.make1i("gaux3");
   public static ShaderUniform1i uniform_gaux4 = shaderUniforms.make1i("gaux4");
   static int cameraOffsetZ;
   public static ShaderUniform1i uniform_colortex0 = shaderUniforms.make1i("colortex0");
   public static ShaderUniform1i uniform_colortex1 = shaderUniforms.make1i("colortex1");
   public static ShaderUniform1i uniform_colortex2 = shaderUniforms.make1i("colortex2");
   static final int intValue3 = 8;
   public static ShaderUniform1i uniform_colortex3 = shaderUniforms.make1i("colortex3");
   public static ShaderUniform1i uniform_colortex4 = shaderUniforms.make1i("colortex4");
   public static ShaderUniform1i uniform_colortex5 = shaderUniforms.make1i("colortex5");
   public static ShaderUniform1i uniform_colortex6 = shaderUniforms.make1i("colortex6");
   public static String glVersionString;
   public static ShaderUniform1i uniform_colortex7 = shaderUniforms.make1i("colortex7");
   public static ShaderUniform1i uniform_gdepthtex = shaderUniforms.make1i("gdepthtex");
   public static ShaderUniform1i uniform_depthtex2 = shaderUniforms.make1i("depthtex2");
   public static ShaderUniform1i uniform_tex = shaderUniforms.make1i("tex");
   public static final boolean flag = true;
   public static ShaderUniform1i uniform_heldItemId = shaderUniforms.make1i("heldItemId");
   public static ShaderUniform1i uniform_heldBlockLightValue = shaderUniforms.make1i("heldBlockLightValue");
   public static ShaderUniform1i uniform_heldItemId2 = shaderUniforms.make1i("heldItemId2");
   public static ShaderUniform1i uniform_heldBlockLightValue2 = shaderUniforms.make1i("heldBlockLightValue2");
   public static ShaderUniform1i uniform_fogMode = shaderUniforms.make1i("fogMode");
   public static ShaderUniform1f uniform_fogDensity = shaderUniforms.make1f("fogDensity");
   public static ShaderUniform3f uniform_fogColor = shaderUniforms.make3f("fogColor");
   public static ShaderUniform3f uniform_skyColor = shaderUniforms.make3f("skyColor");
   public static ShaderUniform1i uniform_worldTime = shaderUniforms.make1i("worldTime");
   public static ShaderUniform1i uniform_worldDay = shaderUniforms.make1i("worldDay");
   public static ShaderUniform1i uniform_moonPhase = shaderUniforms.make1i("moonPhase");
   public static ShaderUniform1i uniform_frameCounter = shaderUniforms.make1i("frameCounter");
   public static ShaderUniform1f uniform_frameTime = shaderUniforms.make1f("frameTime");
   public static ShaderUniform1f uniform_frameTimeCounter = shaderUniforms.make1f("frameTimeCounter");
   public static ShaderUniform1f uniform_sunAngle = shaderUniforms.make1f("sunAngle");
   public static ShaderUniform1f uniform_shadowAngle = shaderUniforms.make1f("shadowAngle");
   public static ShaderUniform1f uniform_rainStrength = shaderUniforms.make1f("rainStrength");
   public static ShaderUniform1f uniform_aspectRatio = shaderUniforms.make1f("aspectRatio");
   public static ShaderUniform1f uniform_viewWidth = shaderUniforms.make1f("viewWidth");
   public static ShaderUniform1f uniform_viewHeight = shaderUniforms.make1f("viewHeight");
   public static ShaderUniform1f uniform_near = shaderUniforms.make1f("near");
   public static ShaderUniform1f uniform_far = shaderUniforms.make1f("far");
   public static ShaderUniform3f uniform_sunPosition = shaderUniforms.make3f("sunPosition");
   public static ShaderUniform3f uniform_moonPosition = shaderUniforms.make3f("moonPosition");
   public static ShaderUniform3f uniform_shadowLightPosition = shaderUniforms.make3f("shadowLightPosition");
   public static ShaderUniform3f uniform_upPosition = shaderUniforms.make3f("upPosition");
   public static ShaderUniform3f uniform_previousCameraPosition = shaderUniforms.make3f("previousCameraPosition");
   public static ShaderUniform3f uniform_cameraPosition = shaderUniforms.make3f("cameraPosition");
   public static ShaderUniformM4 uniform_gbufferModelView = shaderUniforms.makeM4("gbufferModelView");
   public static ShaderUniformM4 uniform_gbufferModelViewInverse = shaderUniforms.makeM4("gbufferModelViewInverse");
   public static ShaderUniformM4 uniform_gbufferPreviousProjection = shaderUniforms.makeM4("gbufferPreviousProjection");
   public static ShaderUniformM4 uniform_gbufferProjection = shaderUniforms.makeM4("gbufferProjection");
   static final int intValue4 = 3;
   public static String glVendorString;
   public static ShaderUniformM4 uniform_gbufferProjectionInverse = shaderUniforms.makeM4("gbufferProjectionInverse");
   public static ShaderUniformM4 uniform_gbufferPreviousModelView = shaderUniforms.makeM4("gbufferPreviousModelView");
   public static ShaderUniformM4 uniform_shadowProjection = shaderUniforms.makeM4("shadowProjection");
   public static ShaderUniformM4 uniform_shadowProjectionInverse = shaderUniforms.makeM4("shadowProjectionInverse");
   public static ShaderUniformM4 uniform_shadowModelView = shaderUniforms.makeM4("shadowModelView");
   public static ContextCapabilities capabilities;
   public static ShaderUniformM4 uniform_shadowModelViewInverse = shaderUniforms.makeM4("shadowModelViewInverse");
   static float clearColorR;
   static int preShadowPassThirdPersonView;
   public static ShaderUniform1f uniform_wetness = shaderUniforms.make1f("wetness");
   private static ICustomTexture noiseTexture;
   public static ShaderUniform1f uniform_eyeAltitude = shaderUniforms.make1f("eyeAltitude");
   private static boolean isHandRenderedOff;
   public static ShaderUniform2i uniform_eyeBrightness = shaderUniforms.make2i("eyeBrightness");
   public static ShaderUniform2i uniform_eyeBrightnessSmooth = shaderUniforms.make2i("eyeBrightnessSmooth");
   public static boolean isSleeping;
   public static ShaderUniform2i uniform_terrainTextureSize = shaderUniforms.make2i("terrainTextureSize");
   public static ShaderUniform1i uniform_terrainIconSize = shaderUniforms.make1i("terrainIconSize");
   public static ShaderUniform1i uniform_isEyeInWater = shaderUniforms.make1i("isEyeInWater");
   static double cameraPositionZ;
   public static ShaderUniform1f uniform_nightVision = shaderUniforms.make1f("nightVision");
   public static ShaderUniform1f uniform_blindness = shaderUniforms.make1f("blindness");
   public static ShaderUniform1f uniform_screenBrightness = shaderUniforms.make1f("screenBrightness");
   public static ShaderUniform1i uniform_hideGUI = shaderUniforms.make1i("hideGUI");
   public static ShaderUniform1f uniform_centerDepthSmooth = shaderUniforms.make1f("centerDepthSmooth");
   public static ShaderUniform2i uniform_atlasSize = shaderUniforms.make2i("atlasSize");
   public static ShaderUniform4i uniform_blendFunc = shaderUniforms.make4i("blendFunc");
   public static ShaderUniform1i uniform_instanceId = shaderUniforms.make1i("instanceId");
   static int shadowPassInterval = 0;
   public static boolean needResizeShadow = false;
   static int shadowMapWidth = 1024;
   static int shadowMapHeight = 1024;
   static int spShadowMapWidth = 1024;
   static float clearColorG;
   static int spShadowMapHeight = 1024;
   static float shadowMapFOV = 90.0F;
   static float shadowMapHalfPlane = 160.0F;
   static boolean shadowMapIsOrtho = true;
   static float shadowDistanceRenderMul = -1.0F;
   static int shadowPassCounter = 0;
   public static boolean shouldSkipDefaultShadow = false;
   static boolean waterShadowEnabled = false;
   static int usedColorBuffers = 0;
   static int usedDepthBuffers = 0;
   static int usedShadowColorBuffers = 0;
   static int usedShadowDepthBuffers = 0;
   static int usedColorAttachs = 0;
   static int usedDrawBuffers = 0;
   static final int intValue5 = 2;
   static int dfb = 0;
   static int sfb = 0;
   private static int[] gbuffersFormat = new int[8];
   public static boolean[] gbuffersClear = new boolean[8];
   public static Vector4f[] gbuffersClearColor = new Vector4f[8];
   private static Programs programs = new Programs();
   public static final Program ProgramNone = programs.getProgramNone();
   public static final Program ProgramShadow = programs.makeShadow("shadow", ProgramNone);
   public static final Program ProgramShadowSolid = programs.makeShadow("shadow_solid", ProgramShadow);
   public static final Program ProgramShadowCutout = programs.makeShadow("shadow_cutout", ProgramShadow);
   public static final Program ProgramBasic = programs.makeGbuffers("gbuffers_basic", ProgramNone);
   public static final Program ProgramTextured = programs.makeGbuffers("gbuffers_textured", ProgramBasic);
   public static final Program ProgramTexturedLit = programs.makeGbuffers("gbuffers_textured_lit", ProgramTextured);
   public static final Program ProgramSkyBasic = programs.makeGbuffers("gbuffers_skybasic", ProgramBasic);
   public static final Program ProgramSkyTextured = programs.makeGbuffers("gbuffers_skytextured", ProgramTextured);
   public static final Program ProgramClouds = programs.makeGbuffers("gbuffers_clouds", ProgramTextured);
   public static final Program ProgramTerrain = programs.makeGbuffers("gbuffers_terrain", ProgramTexturedLit);
   public static final Program ProgramTerrainSolid = programs.makeGbuffers("gbuffers_terrain_solid", ProgramTerrain);
   private static boolean isRenderingFirstPersonHand;
   public static final Program ProgramTerrainCutoutMip = programs.makeGbuffers("gbuffers_terrain_cutout_mip", ProgramTerrain);
   public static final Program ProgramTerrainCutout = programs.makeGbuffers("gbuffers_terrain_cutout", ProgramTerrain);
   public static final Program ProgramDamagedBlock = programs.makeGbuffers("gbuffers_damagedblock", ProgramTerrain);
   public static final Program ProgramBlock = programs.makeGbuffers("gbuffers_block", ProgramTerrain);
   public static final Program ProgramBeaconBeam = programs.makeGbuffers("gbuffers_beaconbeam", ProgramTextured);
   public static final Program ProgramItem = programs.makeGbuffers("gbuffers_item", ProgramTexturedLit);
   static EntityRenderer entityRenderer;
   public static final Program ProgramEntities = programs.makeGbuffers("gbuffers_entities", ProgramTexturedLit);
   public static final Program ProgramEntitiesGlowing = programs.makeGbuffers("gbuffers_entities_glowing", ProgramEntities);
   public static final Program ProgramArmorGlint = programs.makeGbuffers("gbuffers_armor_glint", ProgramTextured);
   public static final Program ProgramSpiderEyes = programs.makeGbuffers("gbuffers_spidereyes", ProgramTextured);
   public static final Program ProgramHand = programs.makeGbuffers("gbuffers_hand", ProgramTexturedLit);
   static double cameraPositionY;
   public static final Program ProgramWeather = programs.makeGbuffers("gbuffers_weather", ProgramTexturedLit);
   public static final Program ProgramDeferredPre = programs.makeVirtual("deferred_pre");
   public static final Program[] ProgramsDeferred = programs.getArray2("deferred");
   private static final int STAGE_GBUFFERS = 0;
   public static final Program ProgramDeferred = ProgramsDeferred[0];
   public static final Program ProgramWater = programs.makeGbuffers("gbuffers_water", ProgramTerrain);
   public static final Program ProgramHandWater = programs.makeGbuffers("gbuffers_hand_water", ProgramHand);
   public static final Program ProgramCompositePre = programs.makeVirtual("composite_pre");
   public static final Program[] ProgramsComposite = programs.getArray("composite");
   public static final Program ProgramComposite = ProgramsComposite[0];
   public static final Program ProgramFinal = programs.makeComposite("final");
   public static final int ProgramCount = programs.getCount();
   public static final Program[] ProgramsAll = programs.getPrograms();
   public static Program activeProgram = ProgramNone;
   public static int activeProgramID = 0;
   private static ProgramStack programStack = new ProgramStack();
   private static boolean hasDeferredPrograms = false;
   static IntBuffer activeDrawBuffers = null;
   private static int activeCompositeMipmapSetting = 0;
   public static Properties loadedShaders = null;
   public static Properties shadersConfig = null;
   public static ITextureObject defaultTexture = null;
   public static boolean[] shadowHardwareFilteringEnabled = new boolean[2];
   public static boolean[] shadowMipmapEnabled = new boolean[2];
   public static boolean[] shadowFilterNearest = new boolean[2];
   public static boolean[] shadowColorMipmapEnabled = new boolean[8];
   public static boolean[] shadowColorFilterNearest = new boolean[8];
   public static boolean configTweakBlockDamage = false;
   public static boolean configCloudShadow = false;
   public static float configHandDepthMul = 0.125F;
   public static float configRenderResMul = 1.0F;
   public static float configShadowResMul = 1.0F;
   public static int configTexMinFilB = 0;
   public static int configTexMinFilN = 0;
   public static int configTexMinFilS = 0;
   public static int configTexMagFilB = 0;
   public static int configTexMagFilN = 0;
   public static int configTexMagFilS = 0;
   public static boolean configShadowClipFrustrum = true;
   public static boolean configNormalMap = true;
   public static boolean configSpecularMap = true;
   private static boolean skipRenderHandOff;
   public static PropertyDefaultTrueFalse configOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting");
   public static PropertyDefaultTrueFalse configOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light");
   public static final int intValue6 = 2;
   public static int configAntialiasingLevel = 0;
   public static final String[] texMinFilDesc = new String[]{"Nearest", "Nearest-Nearest", "Nearest-Linear"};
   public static final String[] texMagFilDesc = new String[]{"Nearest", "Linear"};
   public static final int[] texMinFilValue = new int[]{9728, 9984, 9986};
   public static final int[] texMagFilValue = new int[]{9728, 9729};
   private static IShaderPack shaderPack = null;
   public static boolean shaderPackLoaded = false;
   private static ShaderOption[] shaderPackOptions = null;
   private static Set<String> shaderPackOptionSliders = null;
   static ShaderProfile[] shaderPackProfiles = null;
   static Map<String, ScreenShaderOptions> shaderPackGuiScreens = null;
   static Map<String, IExpressionBool> shaderPackProgramConditions = new HashMap<>();
   public static PropertyDefaultFastFancyOff shaderPackClouds = new PropertyDefaultFastFancyOff("clouds", "Clouds");
   public static PropertyDefaultTrueFalse shaderPackOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting");
   public static PropertyDefaultTrueFalse shaderPackOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light");
   public static PropertyDefaultTrueFalse shaderPackDynamicHandLight = new PropertyDefaultTrueFalse("dynamicHandLight", "Dynamic Hand Light");
   public static PropertyDefaultTrueFalse shaderPackShadowTranslucent = new PropertyDefaultTrueFalse("shadowTranslucent", "Shadow Translucent");
   public static PropertyDefaultTrueFalse shaderPackUnderwaterOverlay = new PropertyDefaultTrueFalse("underwaterOverlay", "Underwater Overlay");
   public static PropertyDefaultTrueFalse shaderPackSun = new PropertyDefaultTrueFalse("sun", "Sun");
   public static PropertyDefaultTrueFalse shaderPackMoon = new PropertyDefaultTrueFalse("moon", "Moon");
   public static PropertyDefaultTrueFalse shaderPackVignette = new PropertyDefaultTrueFalse("vignette", "Vignette");
   static final int intValue7 = 8;
   public static PropertyDefaultTrueFalse shaderPackBackFaceSolid = new PropertyDefaultTrueFalse("backFace.solid", "Back-face Solid");
   public static PropertyDefaultTrueFalse shaderPackBackFaceCutout = new PropertyDefaultTrueFalse("backFace.cutout", "Back-face Cutout");
   public static PropertyDefaultTrueFalse shaderPackBackFaceCutoutMipped = new PropertyDefaultTrueFalse("backFace.cutoutMipped", "Back-face Cutout Mipped");
   public static PropertyDefaultTrueFalse shaderPackBackFaceTranslucent = new PropertyDefaultTrueFalse("backFace.translucent", "Back-face Translucent");
   public static PropertyDefaultTrueFalse shaderPackRainDepth = new PropertyDefaultTrueFalse("rain.depth", "Rain Depth");
   public static PropertyDefaultTrueFalse shaderPackBeaconBeamDepth = new PropertyDefaultTrueFalse("beacon.beam.depth", "Rain Depth");
   static float clearColorB;
   public static PropertyDefaultTrueFalse shaderPackSeparateAo = new PropertyDefaultTrueFalse("separateAo", "Separate AO");
   public static PropertyDefaultTrueFalse shaderPackFrustumCulling = new PropertyDefaultTrueFalse("frustum.culling", "Frustum Culling");
   static Minecraft mc;
   private static Map<String, String> shaderPackResources = new HashMap<>();
   private static World currentWorld = null;
   private static List<Integer> shaderPackDimensions = new ArrayList<>();
   private static ICustomTexture[] customTexturesGbuffers = null;
   private static ICustomTexture[] customTexturesComposite = null;
   private static ICustomTexture[] customTexturesDeferred = null;
   private static String noiseTexturePath = null;
   private static CustomUniforms customUniforms = null;
   private static final String[] STAGE_NAMES = new String[]{"gbuffers", "composite", "deferred"};
   public static final boolean saveFinalShaders = System.getProperty("shaders.debug.save", "false").equals("true");
   public static float blockLightLevel05 = 0.5F;
   public static float blockLightLevel06 = 0.6F;
   public static float blockLightLevel08 = 0.8F;
   public static float aoLevel = -1.0F;
   public static float sunPathRotation = 0.0F;
   public static float shadowAngleInterval = 0.0F;
   public static int fogMode = 0;
   public static float fogDensity = 0.0F;
   static Map<Block, Integer> mapBlockToEntityData;
   public static float shadowIntervalSize = 2.0F;
   public static int terrainIconSize = 16;
   public static String glRendererString;
   public static int[] terrainTextureSize = new int[2];
   private static boolean noiseTextureEnabled = false;
   private static int noiseTextureResolution = 256;
   static final int[] colorTextureImageUnit = new int[]{0, 1, 2, 3, 7, 8, 9, 10};
   private static final int bigBufferSize = (285 + 8 * ProgramCount) * 4;
   private static final ByteBuffer bigBuffer = BufferUtils.createByteBuffer(bigBufferSize).limit(0);
   static final float[] faProjection = new float[16];
   static final float[] faProjectionInverse = new float[16];
   static final float[] faModelView = new float[16];
   static final float[] faModelViewInverse = new float[16];
   private static final boolean flag2 = true;
   static final float[] faShadowProjection = new float[16];
   static final float[] faShadowProjectionInverse = new float[16];
   static final float[] faShadowModelView = new float[16];
   static final float[] faShadowModelViewInverse = new float[16];
   static double cameraPositionX;
   static final FloatBuffer projection = getFloatBuffer();
   static final FloatBuffer projectionInverse = getFloatBuffer();
   static final FloatBuffer modelView = getFloatBuffer();
   static final FloatBuffer modelViewInverse = getFloatBuffer();
   static final FloatBuffer shadowProjection = getFloatBuffer();
   static final FloatBuffer shadowProjectionInverse = getFloatBuffer();
   static final FloatBuffer shadowModelView = getFloatBuffer();
   static final FloatBuffer shadowModelViewInverse = getFloatBuffer();
   static final FloatBuffer previousProjection = getFloatBuffer();
   static final FloatBuffer previousModelView = getFloatBuffer();
   static final FloatBuffer tempMatrixDirectBuffer = getFloatBuffer();
   static final FloatBuffer tempDirectFloatBuffer = getFloatBuffer();
   static final IntBuffer dfbColorTextures = nextIntBuffer(16);
   static final IntBuffer dfbDepthTextures = nextIntBuffer(3);
   static final int intValue8 = 8;
   static final IntBuffer sfbColorTextures = nextIntBuffer(8);
   static final IntBuffer sfbDepthTextures = nextIntBuffer(2);
   static final IntBuffer dfbDrawBuffers = nextIntBuffer(8);
   static final IntBuffer sfbDrawBuffers = nextIntBuffer(8);
   static final IntBuffer drawBuffersNone = nextIntBuffer(8).limit(0);
   static final IntBuffer drawBuffersColorAtt0 = nextIntBuffer(8).put(36064).position(0).limit(1);
   static final FlipTextures dfbColorTexturesFlip = new FlipTextures(dfbColorTextures);
   private static final String[] formatNames = new String[]{
      "R8",
      "RG8",
      "RGB8",
      "RGBA8",
      "R8_SNORM",
      "RG8_SNORM",
      "RGB8_SNORM",
      "RGBA8_SNORM",
      "R16",
      "RG16",
      "RGB16",
      "RGBA16",
      "R16_SNORM",
      "RG16_SNORM",
      "RGB16_SNORM",
      "RGBA16_SNORM",
      "R16F",
      "RG16F",
      "RGB16F",
      "RGBA16F",
      "R32F",
      "RG32F",
      "RGB32F",
      "RGBA32F",
      "R32I",
      "RG32I",
      "RGB32I",
      "RGBA32I",
      "R32UI",
      "RG32UI",
      "RGB32UI",
      "RGBA32UI",
      "R3_G3_B2",
      "RGB5_A1",
      "RGB10_A2",
      "R11F_G11F_B10F",
      "RGB9_E5"
   };
   private static final int[] formatIds = new int[]{
      33321,
      33323,
      32849,
      32856,
      36756,
      36757,
      36758,
      36759,
      33322,
      33324,
      32852,
      32859,
      36760,
      36761,
      36762,
      36763,
      33325,
      33327,
      34843,
      34842,
      33326,
      33328,
      34837,
      34836,
      33333,
      33339,
      36227,
      36226,
      33334,
      33340,
      36209,
      36208,
      10768,
      32855,
      32857,
      35898,
      35901
   };
   private static final Pattern patternLoadEntityDataMap = Pattern.compile("\\s*([\\w:]+)\\s*=\\s*([-]?\\d+)\\s*");
   public static int[] entityData = new int[32];
   public static int entityDataIndex = 0;
   public static final File shaderPacksDir = new File(Minecraft.getMinecraft().mcDataDir, "shaderpacks");
   static File configFile = new File(Minecraft.getMinecraft().mcDataDir, "optionsshaders.txt");

   public static void drawComposite() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      drawCompositeQuad();
      int countInstances2 = activeProgram.getCountInstances();
      if (countInstances2 > 1) {
         for (int innerIndex = 1; innerIndex < countInstances2; innerIndex++) {
            uniform_instanceId.setValue(innerIndex);
            drawCompositeQuad();
         }

         uniform_instanceId.setValue(0);
      }
   }

   public static void preCelestialRotate() {
      GL11.glRotatef(sunPathRotation * 1.0F, 0.0F, 0.0F, 1.0F);
      checkGLError("preCelestialRotate");
   }

   public static void setEntityId(Entity entity) {
      if (uniform_entityId.isDefined()) {
         int entityIdByClass = EntityUtils.getEntityIdByClass(entity);
         int entityAliasId = EntityAliases.getEntityAliasId(entityIdByClass);
         if (entityAliasId >= 0) {
            entityIdByClass = entityAliasId;
         }

         uniform_entityId.setValue(entityIdByClass);
      }
   }

   private static void setupShadowFrameBuffer() {
      if (usedShadowDepthBuffers != 0) {
         if (sfb != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(sfb);
            GlStateManager.deleteTextures(sfbDepthTextures);
            GlStateManager.deleteTextures(sfbColorTextures);
         }

         sfb = EXTFramebufferObject.glGenFramebuffersEXT();
         EXTFramebufferObject.glBindFramebufferEXT(36160, sfb);
         GL11.glDrawBuffer(0);
         GL11.glReadBuffer(0);
         GL11.glGenTextures(sfbDepthTextures.clear().limit(usedShadowDepthBuffers));
         GL11.glGenTextures(sfbColorTextures.clear().limit(usedShadowColorBuffers));
         sfbDepthTextures.position(0);
         sfbColorTextures.position(0);

         for (int index = 0; index < usedShadowDepthBuffers; index++) {
            GlStateManager.bindTexture(sfbDepthTextures.get(index));
            GL11.glTexParameterf(3553, 10242, 33071.0F);
            GL11.glTexParameterf(3553, 10243, 33071.0F);
            int i = shadowFilterNearest[index] ? 9728 : 9729;
            GL11.glTexParameteri(3553, 10241, i);
            GL11.glTexParameteri(3553, 10240, i);
            if (shadowHardwareFilteringEnabled[index]) {
               GL11.glTexParameteri(3553, 34892, 34894);
            }

            GL11.glTexImage2D(3553, 0, 6402, shadowMapWidth, shadowMapHeight, 0, 6402, 5126, (FloatBuffer)null);
         }

         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
         checkGLError("FT sd");

         for (int nestedIndex = 0; nestedIndex < usedShadowColorBuffers; nestedIndex++) {
            GlStateManager.bindTexture(sfbColorTextures.get(nestedIndex));
            GL11.glTexParameterf(3553, 10242, 33071.0F);
            GL11.glTexParameterf(3553, 10243, 33071.0F);
            int i2 = shadowColorFilterNearest[nestedIndex] ? 9728 : 9729;
            GL11.glTexParameteri(3553, 10241, i2);
            GL11.glTexParameteri(3553, 10240, i2);
            GL11.glTexImage2D(3553, 0, 6408, shadowMapWidth, shadowMapHeight, 0, 32993, 33639, (ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + nestedIndex, 3553, sfbColorTextures.get(nestedIndex), 0);
            checkGLError("FT sc");
         }

         GlStateManager.bindTexture(0);
         if (usedShadowColorBuffers > 0) {
            GL20.glDrawBuffers(sfbDrawBuffers);
         }

         int i3 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
         if (i3 != 36053) {
            printChatAndLogError("[Shaders] Error: Failed creating shadow framebuffer! (Status " + i3 + ")");
         } else {
            SMCLog.info("Shadow framebuffer created.");
         }
      }
   }

   private static boolean printShaderLogInfo(int shader, String name, List<String> listFiles) {
      BufferUtils.createIntBuffer(1);
      int i = GL20.glGetShaderi(shader, 35716);
      if (i <= 1) {
         return true;
      }

      for (int i2 = 0; i2 < listFiles.size(); i2++) {
         String text3 = listFiles.get(i2);
         SMCLog.info("File: " + (i2 + 1) + " = " + text3);
      }

      String text4 = GL20.glGetShaderInfoLog(shader, i);
      text4 = StrUtils.trim(text4, " \n\r\t");
      SMCLog.info("Shader info log: " + name + "\n" + text4);
      return false;
   }

   public static void setSkyColor(Vec3 v3color) {
      skyColorR = (float)v3color.xCoord;
      skyColorG = (float)v3color.yCoord;
      skyColorB = (float)v3color.zCoord;
      setProgramUniform3f(uniform_skyColor, skyColorR, skyColorG, skyColorB);
   }

   private static void preDrawComposite() {
      RenderScale renderScale = activeProgram.getRenderScale();
      if (renderScale != null) {
         int i = (int)(renderWidth * renderScale.getOffsetX());
         int i2 = (int)(renderHeight * renderScale.getOffsetY());
         int i3 = (int)(renderWidth * renderScale.getScale());
         int i4 = (int)(renderHeight * renderScale.getScale());
         GL11.glViewport(i, i2, i3, i4);
      }
   }

   private static void handleShaderUniformM4(ShaderUniformM4 shaderUniformM4, FloatBuffer floatBuffer2) {
      shaderUniformM4.handleFloatBuffer(floatBuffer2);
   }

   public static boolean isDynamicHandLight() {
      return !shaderPackDynamicHandLight.isDefault() ? shaderPackDynamicHandLight.isTrue() : true;
   }

   private static ICustomTexture loadCustomTextureShaders(int textureUnit, String path) {
      path = path.trim();
      if (path.indexOf(46) < 0) {
         path = path + ".png";
      }

      try {
         String text3 = "shaders/" + StrUtils.removePrefix(path, "/");
         InputStream resourceAsStream = shaderPack.getResourceAsStream(text3);
         if (resourceAsStream == null) {
            SMCLog.warning("Texture not found: " + path);
            return null;
         } else {
            IOUtils.closeQuietly(resourceAsStream);
            SimpleShaderTexture simpleShaderTexture = new SimpleShaderTexture(text3);
            simpleShaderTexture.loadTexture(mc.getResourceManager());
            return new CustomTexture(textureUnit, text3, simpleShaderTexture);
         }
      } catch (IOException ioException) {
         SMCLog.warning("Error loading texture: " + path);
         SMCLog.warning(ioException.getClass().getName() + ": " + ioException.getMessage());
         return null;
      }
   }

   private static void setProgramUniform2i(ShaderUniform2i su, int i, int i2) {
      su.setValue(i, i2);
   }

   public static void pushEntity(int data0, int data1) {
      GlStateManager.colorMask(true, true, true, true);
      if (isShadowPass) {
         GL11.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
      } else {
         GL11.glViewport(0, 0, renderWidth, renderHeight);
         EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
         isRenderingDfb = true;
         GlStateManager.enableCull();
         GlStateManager.enableDepth();
         setDrawBuffers(drawBuffersNone);
         useProgram(ProgramTextured);
         checkGLError("beginRenderPass");
      }
   }

   public static Program getProgram(String name) {
      return programs.getProgram(name);
   }

   public static boolean isRenderingFirstPersonHand() {
      return isRenderingFirstPersonHand;
   }

   public static ShaderOption[] getShaderPackOptions(String screenName) {
      ShaderOption[] shaderOptions = (ShaderOption[])shaderPackOptions.clone();
      if (shaderPackGuiScreens == null) {
         if (shaderPackProfiles != null) {
            ShaderOptionProfile shaderOptionProfile = new ShaderOptionProfile(shaderPackProfiles, shaderOptions);
            shaderOptions = (ShaderOption[])Config.removeObjectFromArray(shaderOptions, shaderOptionProfile);
         }

         return getVisibleOptions(shaderOptions);
      } else {
         String text3 = screenName != null ? "screen." + screenName : "screen";
         ScreenShaderOptions screenShaderOptions = shaderPackGuiScreens.get(text3);
         if (screenShaderOptions == null) {
            return new ShaderOption[0];
         }

         ShaderOption[] shaderOptions2 = screenShaderOptions.getShaderOptions();
         ArrayList arrayList = new ArrayList();

         for (int i = 0; i < shaderOptions2.length; i++) {
            ShaderOption shaderOption = shaderOptions2[i];
            if (shaderOption == null) {
               arrayList.add(null);
            } else if (shaderOption instanceof ShaderOptionRest) {
               ShaderOption[] shaderOptionsRest = getShaderOptionsRest(shaderPackGuiScreens, shaderOptions);
               arrayList.addAll(Arrays.asList(shaderOptionsRest));
            } else {
               arrayList.add(shaderOption);
            }
         }

         return (ShaderOption[]) arrayList.toArray(new ShaderOption[arrayList.size()]);
      }
   }

   private static int getPixelFormat(int internalFormat) {
      switch (internalFormat) {
         case 33333:
         case 33334:
         case 33339:
         case 33340:
         case 36208:
         case 36209:
         case 36226:
         case 36227:
            return 36251;
         default:
            return 32993;
      }
   }

   public static String getShaderPackName() {
      return shaderPack == null ? null : (shaderPack instanceof ShaderPackNone ? null : shaderPack.getName());
   }

   public static boolean isVignette() {
      return !shaderPackVignette.isFalse();
   }

   public static boolean isSeparateAo() {
      return shaderPackSeparateAo.isTrue();
   }

   public static void preSkyList() {
      setUpPosition();
      GL11.glColor3f(fogColorR, fogColorG, fogColorB);
      drawHorizon();
      GL11.glColor3f(skyColorR, skyColorG, skyColorB);
   }

   private static boolean isTranslucentBlock(ItemStack stack) {
      if (stack == null) {
         return false;
      }

      Item item2 = stack.getItem();
      if (item2 == null) {
         return false;
      }

      if (!(item2 instanceof ItemBlock)) {
         return false;
      }

      Block block2 = ((ItemBlock)item2).getBlock();
      return block2 == null ? false : block2.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
   }

   private static String getErrorInfo(int errorCode, String location) {
      StringBuilder stringBuilder = new StringBuilder();
      if (errorCode == 1286) {
         int i = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
         String text = getFramebufferStatusText(i);
         String name2 = ", fbStatus: " + i + " (" + text + ")";
         stringBuilder.append(name2);
      }

      String name = activeProgram.getName();
      if (name.isEmpty()) {
         name = "none";
      }

      stringBuilder.append(", program: " + name);
      Program program = getProgramById(activeProgramID);
      if (program != activeProgram) {
         String name2 = program.getName();
         if (name2.isEmpty()) {
            name2 = "none";
         }

         stringBuilder.append(" (" + name2 + ")");
      }

      if (location.equals("setDrawBuffers")) {
         stringBuilder.append(", drawBuffers: " + activeProgram.getDrawBufSettings());
      }

      return stringBuilder.toString();
   }

   public static void beginEntities() {
      if (isRenderingWorld) {
         useProgram(ProgramEntities);
      }
   }

   private static void renderComposites(Program[] ps, boolean renderFinal) {
      if (!isShadowPass) {
         GL11.glPushMatrix();
         GL11.glLoadIdentity();
         GL11.glMatrixMode(5889);
         GL11.glPushMatrix();
         GL11.glLoadIdentity();
         GL11.glOrtho(0.0, 1.0, 0.0, 1.0, 0.0, 1.0);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableTexture2D();
         GlStateManager.disableAlpha();
         GlStateManager.disableBlend();
         GlStateManager.enableDepth();
         GlStateManager.depthFunc(519);
         GlStateManager.depthMask(false);
         GlStateManager.disableLighting();
         if (usedShadowDepthBuffers >= 1) {
            GlStateManager.setActiveTexture(33988);
            GlStateManager.bindTexture(sfbDepthTextures.get(0));
            if (usedShadowDepthBuffers >= 2) {
               GlStateManager.setActiveTexture(33989);
               GlStateManager.bindTexture(sfbDepthTextures.get(1));
            }
         }

         for (int index = 0; index < usedColorBuffers; index++) {
            GlStateManager.setActiveTexture(33984 + colorTextureImageUnit[index]);
            GlStateManager.bindTexture(dfbColorTexturesFlip.getA(index));
         }

         GlStateManager.setActiveTexture(33990);
         GlStateManager.bindTexture(dfbDepthTextures.get(0));
         if (usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            GlStateManager.bindTexture(dfbDepthTextures.get(1));
            if (usedDepthBuffers >= 3) {
               GlStateManager.setActiveTexture(33996);
               GlStateManager.bindTexture(dfbDepthTextures.get(2));
            }
         }

         for (int nestedIndex = 0; nestedIndex < usedShadowColorBuffers; nestedIndex++) {
            GlStateManager.setActiveTexture(33997 + nestedIndex);
            GlStateManager.bindTexture(sfbColorTextures.get(nestedIndex));
         }

         if (noiseTextureEnabled) {
            GlStateManager.setActiveTexture(33984 + noiseTexture.getTextureUnit());
            GlStateManager.bindTexture(noiseTexture.getTextureId());
         }

         if (renderFinal) {
            bindCustomTextures(customTexturesComposite);
         } else {
            bindCustomTextures(customTexturesDeferred);
         }

         GlStateManager.setActiveTexture(33984);

         for (int outerIndex = 0; outerIndex < usedColorBuffers; outerIndex++) {
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + outerIndex, 3553, dfbColorTexturesFlip.getB(outerIndex), 0);
         }

         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
         GL20.glDrawBuffers(dfbDrawBuffers);
         checkGLError("pre-composite");

         for (int index2 = 0; index2 < ps.length; index2++) {
            Program program2 = ps[index2];
            if (program2.getId() != 0) {
               useProgram(program2);
               checkGLError(program2.getName());
               if (activeCompositeMipmapSetting != 0) {
                  genCompositeMipmap();
               }

               preDrawComposite();
               drawComposite();
               postDrawComposite();

               for (int innerIndex = 0; innerIndex < usedColorBuffers; innerIndex++) {
                  if (program2.getToggleColorTextures()[innerIndex]) {
                     dfbColorTexturesFlip.flip(innerIndex);
                     GlStateManager.setActiveTexture(33984 + colorTextureImageUnit[innerIndex]);
                     GlStateManager.bindTexture(dfbColorTexturesFlip.getA(innerIndex));
                     EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + innerIndex, 3553, dfbColorTexturesFlip.getB(innerIndex), 0);
                  }
               }

               GlStateManager.setActiveTexture(33984);
            }
         }

         checkGLError("composite");
         if (renderFinal) {
            renderFinal();
            isCompositeRendered = true;
         }

         GlStateManager.enableLighting();
         GlStateManager.enableTexture2D();
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.depthFunc(515);
         GlStateManager.depthMask(true);
         GL11.glPopMatrix();
         GL11.glMatrixMode(5888);
         GL11.glPopMatrix();
         useProgram(ProgramNone);
      }
   }

   private static ICustomTexture loadCustomTextureLocation(int textureUnit, String path) {
      String text3 = path.trim();
      byte b2 = 0;
      if (text3.startsWith("minecraft:textures/")) {
         text3 = StrUtils.addSuffixCheck(text3, ".png");
         if (text3.endsWith("_n.png")) {
            text3 = StrUtils.replaceSuffix(text3, "_n.png", ".png");
            b2 = 1;
         } else if (text3.endsWith("_s.png")) {
            text3 = StrUtils.replaceSuffix(text3, "_s.png", ".png");
            b2 = 2;
         }
      }

      ResourceLocation resourceLocation = new ResourceLocation(text3);
      return new CustomTextureLocation(textureUnit, resourceLocation, b2);
   }

   private static int createFragShader(Program program, String filename) {
      int i = ARBShaderObjects.glCreateShaderObjectARB(35632);
      if (i == 0) {
         return 0;
      }

      StringBuilder stringBuilder = new StringBuilder(131072);

      BufferedReader bufferedReader;
      try {
         bufferedReader = new BufferedReader(getShaderReader(filename));
      } catch (Exception exception) {
         ARBShaderObjects.glDeleteObjectARB(i);
         return 0;
      }

      ShaderOption[] changedOptions = getChangedOptions(shaderPackOptions);
      ArrayList arrayList = new ArrayList();

      try {
         bufferedReader = ShaderPackParser.resolveIncludes(bufferedReader, filename, shaderPack, 0, arrayList, 0);
         MacroState macroState = new MacroState();

         while (true) {
            String line2 = bufferedReader.readLine();
            if (line2 == null) {
               bufferedReader.close();
               break;
            }

            line2 = applyOptions(line2, changedOptions);
            stringBuilder.append(line2).append('\n');
            if (macroState.processLine(line2)) {
               ShaderLine shaderLine = ShaderParser.parseLine(line2);
               if (shaderLine != null) {
                  if (shaderLine.isUniform()) {
                     String name3 = shaderLine.getName();
                     int i2;
                     if ((i2 = ShaderParser.getShadowDepthIndex(name3)) >= 0) {
                        usedShadowDepthBuffers = Math.max(usedShadowDepthBuffers, i2 + 1);
                     } else if ((i2 = ShaderParser.getShadowColorIndex(name3)) >= 0) {
                        usedShadowColorBuffers = Math.max(usedShadowColorBuffers, i2 + 1);
                     } else if ((i2 = ShaderParser.getDepthIndex(name3)) >= 0) {
                        usedDepthBuffers = Math.max(usedDepthBuffers, i2 + 1);
                     } else if (name3.equals("gdepth") && gbuffersFormat[1] == 6408) {
                        gbuffersFormat[1] = 34836;
                     } else if ((i2 = ShaderParser.getColorIndex(name3)) >= 0) {
                        usedColorBuffers = Math.max(usedColorBuffers, i2 + 1);
                     } else if (name3.equals("centerDepthSmooth")) {
                        centerDepthSmoothEnabled = true;
                     }
                  } else if (shaderLine.isConstInt("shadowMapResolution") || shaderLine.isProperty("SHADOWRES")) {
                     spShadowMapWidth = spShadowMapHeight = shaderLine.getValueInt();
                     shadowMapWidth = shadowMapHeight = Math.round(spShadowMapWidth * configShadowResMul);
                     SMCLog.info("Shadow map resolution: " + spShadowMapWidth);
                  } else if (shaderLine.isConstFloat("shadowMapFov") || shaderLine.isProperty("SHADOWFOV")) {
                     shadowMapFOV = shaderLine.getValueFloat();
                     shadowMapIsOrtho = false;
                     SMCLog.info("Shadow map field of view: " + shadowMapFOV);
                  } else if (shaderLine.isConstFloat("shadowDistance") || shaderLine.isProperty("SHADOWHPL")) {
                     shadowMapHalfPlane = shaderLine.getValueFloat();
                     shadowMapIsOrtho = true;
                     SMCLog.info("Shadow map distance: " + shadowMapHalfPlane);
                  } else if (shaderLine.isConstFloat("shadowDistanceRenderMul")) {
                     shadowDistanceRenderMul = shaderLine.getValueFloat();
                     SMCLog.info("Shadow distance render mul: " + shadowDistanceRenderMul);
                  } else if (shaderLine.isConstFloat("shadowIntervalSize")) {
                     shadowIntervalSize = shaderLine.getValueFloat();
                     SMCLog.info("Shadow map interval size: " + shadowIntervalSize);
                  } else if (shaderLine.isUniform("generateShadowMipmap")) {
                     Arrays.fill(shadowMipmapEnabled, true);
                     SMCLog.info("Generate shadow mipmap");
                  } else if (shaderLine.isUniform("generateShadowColorMipmap")) {
                     Arrays.fill(shadowColorMipmapEnabled, true);
                     SMCLog.info("Generate shadow color mipmap");
                  } else if (shaderLine.isUniform("shadowHardwareFiltering")) {
                     Arrays.fill(shadowHardwareFilteringEnabled, true);
                     SMCLog.info("Hardware shadow filtering enabled.");
                  } else if (shaderLine.isUniform("shadowHardwareFiltering0")) {
                     shadowHardwareFilteringEnabled[0] = true;
                     SMCLog.info("shadowHardwareFiltering0");
                  } else if (shaderLine.isUniform("shadowHardwareFiltering1")) {
                     shadowHardwareFilteringEnabled[1] = true;
                     SMCLog.info("shadowHardwareFiltering1");
                  } else if (shaderLine.checkString2("shadowtex0Mipmap", "shadowtexMipmap")) {
                     shadowMipmapEnabled[0] = true;
                     SMCLog.info("shadowtex0Mipmap");
                  } else if (shaderLine.isUniform("shadowtex1Mipmap")) {
                     shadowMipmapEnabled[1] = true;
                     SMCLog.info("shadowtex1Mipmap");
                  } else if (shaderLine.checkString2("shadowcolor0Mipmap", "shadowColor0Mipmap")) {
                     shadowColorMipmapEnabled[0] = true;
                     SMCLog.info("shadowcolor0Mipmap");
                  } else if (shaderLine.checkString2("shadowcolor1Mipmap", "shadowColor1Mipmap")) {
                     shadowColorMipmapEnabled[1] = true;
                     SMCLog.info("shadowcolor1Mipmap");
                  } else if (shaderLine.checkString("shadowtex0Nearest", "shadowtexNearest", "shadow0MinMagNearest")) {
                     shadowFilterNearest[0] = true;
                     SMCLog.info("shadowtex0Nearest");
                  } else if (shaderLine.checkString2("shadowtex1Nearest", "shadow1MinMagNearest")) {
                     shadowFilterNearest[1] = true;
                     SMCLog.info("shadowtex1Nearest");
                  } else if (shaderLine.checkString("shadowcolor0Nearest", "shadowColor0Nearest", "shadowColor0MinMagNearest")) {
                     shadowColorFilterNearest[0] = true;
                     SMCLog.info("shadowcolor0Nearest");
                  } else if (shaderLine.checkString("shadowcolor1Nearest", "shadowColor1Nearest", "shadowColor1MinMagNearest")) {
                     shadowColorFilterNearest[1] = true;
                     SMCLog.info("shadowcolor1Nearest");
                  } else if (shaderLine.isConstFloat("wetnessHalflife") || shaderLine.isProperty("WETNESSHL")) {
                     wetnessHalfLife = shaderLine.getValueFloat();
                     SMCLog.info("Wetness halflife: " + wetnessHalfLife);
                  } else if (shaderLine.isConstFloat("drynessHalflife") || shaderLine.isProperty("DRYNESSHL")) {
                     drynessHalfLife = shaderLine.getValueFloat();
                     SMCLog.info("Dryness halflife: " + drynessHalfLife);
                  } else if (shaderLine.isConstFloat("eyeBrightnessHalflife")) {
                     eyeBrightnessHalflife = shaderLine.getValueFloat();
                     SMCLog.info("Eye brightness halflife: " + eyeBrightnessHalflife);
                  } else if (shaderLine.isConstFloat("centerDepthHalflife")) {
                     centerDepthSmoothHalflife = shaderLine.getValueFloat();
                     SMCLog.info("Center depth halflife: " + centerDepthSmoothHalflife);
                  } else if (shaderLine.isConstFloat("sunPathRotation")) {
                     sunPathRotation = shaderLine.getValueFloat();
                     SMCLog.info("Sun path rotation: " + sunPathRotation);
                  } else if (shaderLine.isConstFloat("ambientOcclusionLevel")) {
                     aoLevel = Config.limit(shaderLine.getValueFloat(), 0.0F, 1.0F);
                     SMCLog.info("AO Level: " + aoLevel);
                  } else if (shaderLine.isConstInt("superSamplingLevel")) {
                     int valueInt = shaderLine.getValueInt();
                     if (valueInt > 1) {
                        SMCLog.info("Super sampling level: " + valueInt + "x");
                        superSamplingLevel = valueInt;
                     } else {
                        superSamplingLevel = 1;
                     }
                  } else if (shaderLine.isConstInt("noiseTextureResolution")) {
                     noiseTextureResolution = shaderLine.getValueInt();
                     noiseTextureEnabled = true;
                     SMCLog.info("Noise texture enabled");
                     SMCLog.info("Noise texture resolution: " + noiseTextureResolution);
                  } else if (shaderLine.isConstIntSuffix("Format")) {
                     String text3 = StrUtils.removeSuffix(shaderLine.getName(), "Format");
                     String value2 = shaderLine.getValue();
                     int bufferIndexFromString = getBufferIndexFromString(text3);
                     int textureFormatFromString = getTextureFormatFromString(value2);
                     if (bufferIndexFromString >= 0 && textureFormatFromString != 0) {
                        gbuffersFormat[bufferIndexFromString] = textureFormatFromString;
                        SMCLog.info("%s format: %s", text3, value2);
                     }
                  } else if (shaderLine.isConstBoolSuffix("Clear", false)) {
                     if (ShaderParser.isComposite(filename) || ShaderParser.isDeferred(filename)) {
                        String text4 = StrUtils.removeSuffix(shaderLine.getName(), "Clear");
                        int bufferIndexFromString2 = getBufferIndexFromString(text4);
                        if (bufferIndexFromString2 >= 0) {
                           gbuffersClear[bufferIndexFromString2] = false;
                           SMCLog.info("%s clear disabled", text4);
                        }
                     }
                  } else if (shaderLine.isConstVec4Suffix("ClearColor")) {
                     if (ShaderParser.isComposite(filename) || ShaderParser.isDeferred(filename)) {
                        String text5 = StrUtils.removeSuffix(shaderLine.getName(), "ClearColor");
                        int bufferIndexFromString3 = getBufferIndexFromString(text5);
                        if (bufferIndexFromString3 >= 0) {
                           Vector4f valueVec = shaderLine.getValueVec4();
                           if (valueVec != null) {
                              gbuffersClearColor[bufferIndexFromString3] = valueVec;
                              SMCLog.info("%s clear color: %s %s %s %s", text5, valueVec.getX(), valueVec.getY(), valueVec.getZ(), valueVec.getW());
                           } else {
                              SMCLog.warning("Invalid color value: " + shaderLine.getValue());
                           }
                        }
                     }
                  } else if (shaderLine.isProperty("GAUX4FORMAT", "RGBA32F")) {
                     gbuffersFormat[7] = 34836;
                     SMCLog.info("gaux4 format : RGB32AF");
                  } else if (shaderLine.isProperty("GAUX4FORMAT", "RGB32F")) {
                     gbuffersFormat[7] = 34837;
                     SMCLog.info("gaux4 format : RGB32F");
                  } else if (shaderLine.isProperty("GAUX4FORMAT", "RGB16")) {
                     gbuffersFormat[7] = 32852;
                     SMCLog.info("gaux4 format : RGB16");
                  } else if (shaderLine.isConstBoolSuffix("MipmapEnabled", true)) {
                     if (ShaderParser.isComposite(filename) || ShaderParser.isDeferred(filename) || ShaderParser.isFinal(filename)) {
                        String text6 = StrUtils.removeSuffix(shaderLine.getName(), "MipmapEnabled");
                        int bufferIndexFromString4 = getBufferIndexFromString(text6);
                        if (bufferIndexFromString4 >= 0) {
                           int compositeMipmapSetting = program.getCompositeMipmapSetting();
                           compositeMipmapSetting |= 1 << bufferIndexFromString4;
                           program.setCompositeMipmapSetting(compositeMipmapSetting);
                           SMCLog.info("%s mipmap enabled", text6);
                        }
                     }
                  } else if (shaderLine.isProperty("DRAWBUFFERS")) {
                     String value3 = shaderLine.getValue();
                     if (ShaderParser.isValidDrawBuffers(value3)) {
                        program.setDrawBufSettings(value3);
                     } else {
                        SMCLog.warning("Invalid draw buffers: " + value3);
                     }
                  }
               }
            }
         }
      } catch (Exception exception2) {
         SMCLog.severe("Couldn't read " + filename + "!");
         exception2.printStackTrace();
         ARBShaderObjects.glDeleteObjectARB(i);
         return 0;
      }

      if (saveFinalShaders) {
         saveShader(filename, stringBuilder.toString());
      }

      ARBShaderObjects.glShaderSourceARB(i, stringBuilder);
      ARBShaderObjects.glCompileShaderARB(i);
      if (GL20.glGetShaderi(i, 35713) != 1) {
         SMCLog.severe("Error compiling fragment shader: " + filename);
      }

      printShaderLogInfo(i, filename, arrayList);
      return i;
   }

   public static void postCelestialRotate() {
      FloatBuffer floatBuffer = tempMatrixDirectBuffer;
      floatBuffer.clear();
      GL11.glGetFloat(2982, floatBuffer);
      floatBuffer.get(tempMat, 0, 16);
      SMath.multiplyMat4xVec4(sunPosition, tempMat, sunPosModelView);
      SMath.multiplyMat4xVec4(moonPosition, tempMat, moonPosModelView);
      System.arraycopy(shadowAngle == sunAngle ? sunPosition : moonPosition, 0, shadowLightPosition, 0, 3);
      setProgramUniform3f(uniform_sunPosition, sunPosition[0], sunPosition[1], sunPosition[2]);
      setProgramUniform3f(uniform_moonPosition, moonPosition[0], moonPosition[1], moonPosition[2]);
      setProgramUniform3f(uniform_shadowLightPosition, shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
      if (customUniforms != null) {
         customUniforms.update();
      }

      checkGLError("postCelestialRotate");
   }

   public static void beginSky() {
      isRenderingSky = true;
      fogEnabled = true;
      setDrawBuffers(dfbDrawBuffers);
      useProgram(ProgramSkyTextured);
      pushEntity(-2);
   }

   public static String getEnumShaderOption(EnumShaderOption eso) {
      switch (eso) {
         case ANTIALIASING:
            return Integer.toString(configAntialiasingLevel);
         case NORMAL_MAP:
            return Boolean.toString(configNormalMap);
         case SPECULAR_MAP:
            return Boolean.toString(configSpecularMap);
         case RENDER_RES_MUL:
            return Float.toString(configRenderResMul);
         case SHADOW_RES_MUL:
            return Float.toString(configShadowResMul);
         case HAND_DEPTH_MUL:
            return Float.toString(configHandDepthMul);
         case CLOUD_SHADOW:
            return Boolean.toString(configCloudShadow);
         case OLD_HAND_LIGHT:
            return configOldHandLight.getPropertyValue();
         case OLD_LIGHTING:
            return configOldLighting.getPropertyValue();
         case SHADER_PACK:
            return currentShaderName;
         case TWEAK_BLOCK_DAMAGE:
            return Boolean.toString(configTweakBlockDamage);
         case SHADOW_CLIP_FRUSTRUM:
            return Boolean.toString(configShadowClipFrustrum);
         case TEX_MIN_FIL_B:
            return Integer.toString(configTexMinFilB);
         case TEX_MIN_FIL_N:
            return Integer.toString(configTexMinFilN);
         case TEX_MIN_FIL_S:
            return Integer.toString(configTexMinFilS);
         case TEX_MAG_FIL_B:
            return Integer.toString(configTexMagFilB);
         case TEX_MAG_FIL_N:
            return Integer.toString(configTexMagFilB);
         case TEX_MAG_FIL_S:
            return Integer.toString(configTexMagFilB);
         default:
            throw new IllegalArgumentException("Unknown option: " + eso);
      }
   }

   public static int checkFramebufferStatus(String location) {
      int i = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
      if (i != 36053) {
         System.err.format("FramebufferStatus 0x%04X at %s\n", i, location);
      }

      return i;
   }

   private static int getDrawBuffer(Program program, String str, int ic) {
      int i = 0;
      if (ic >= str.length()) {
         return 0;
      }

      int i2 = str.charAt(ic) - '0';
      if (program == ProgramShadow) {
         if (i2 >= 0 && i2 <= 1) {
            i = i2 + 36064;
            usedShadowColorBuffers = Math.max(usedShadowColorBuffers, i2);
         }

         return i;
      } else {
         if (i2 >= 0 && i2 <= 7) {
            program.getToggleColorTextures()[i2] = true;
            i = i2 + 36064;
            usedColorAttachs = Math.max(usedColorAttachs, i2);
            usedColorBuffers = Math.max(usedColorBuffers, i2);
         }

         return i;
      }
   }

   private static void setCameraOffset(Entity viewEntity) {
      if (viewEntity == null) {
         cameraOffsetX = 0;
         cameraOffsetZ = 0;
      } else {
         cameraOffsetX = (int)viewEntity.posX / 1000 * 1000;
         cameraOffsetZ = (int)viewEntity.posZ / 1000 * 1000;
      }
   }

   private static void loadShaderPackDimensions() {
      shaderPackDimensions.clear();

      for (int index = -128; index <= 128; index++) {
         String text3 = "/shaders/world" + index;
         if (shaderPack.hasDirectory(text3)) {
            shaderPackDimensions.add(index);
         }
      }

      if (shaderPackDimensions.size() > 0) {
         Integer[] integers = shaderPackDimensions.toArray(new Integer[shaderPackDimensions.size()]);
         Config.dbg("[Shaders] Worlds: " + Config.arrayToString(integers));
      }
   }

   public static void run() {
      if (isRenderingWorld) {
         useProgram(ProgramBeaconBeam);
      }
   }

   private static void printChatAndLogError(String str) {
      SMCLog.severe(str);
      mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
   }

   private static ICustomTexture loadCustomTextureRaw(
      int textureUnit,
      String line,
      String path,
      TextureType type,
      InternalFormat internalFormat,
      int width,
      int height,
      int depth,
      PixelFormat pixelFormat,
      PixelType pixelType
   ) {
      try {
         String text3 = "shaders/" + StrUtils.removePrefix(path, "/");
         InputStream resourceAsStream = shaderPack.getResourceAsStream(text3);
         if (resourceAsStream == null) {
            SMCLog.warning("Raw texture not found: " + path);
            return null;
         } else {
            byte[] all = Config.readAll(resourceAsStream);
            IOUtils.closeQuietly(resourceAsStream);
            ByteBuffer directByteBuffer = GLAllocation.createDirectByteBuffer(all.length);
            directByteBuffer.put(all);
            directByteBuffer.flip();
            TextureMetadataSection textureMetadataSection = SimpleShaderTexture.loadTextureMetadataSection(text3, new TextureMetadataSection(true, true, new ArrayList<>()));
            return new CustomTextureRaw(
               type, internalFormat, width, height, depth, pixelFormat, pixelType, directByteBuffer, textureUnit, textureMetadataSection.getTextureBlur(), textureMetadataSection.getTextureClamp()
            );
         }
      } catch (IOException ioException) {
         SMCLog.warning("Error loading raw texture: " + path);
         SMCLog.warning(ioException.getClass().getName() + ": " + ioException.getMessage());
         return null;
      }
   }

   public static void resourcesReloaded() {
      loadShaderPackResources();
      if (shaderPackLoaded) {
         BlockAliases.resourcesReloaded();
         ItemAliases.resourcesReloaded();
         EntityAliases.resourcesReloaded();
      }
   }

   private static int getTextureIndex(int stage, String name) {
      if (stage == 0) {
         if (name.equals("texture")) {
            return 0;
         }

         if (name.equals("lightmap")) {
            return 1;
         }

         if (name.equals("normals")) {
            return 2;
         }

         if (name.equals("specular")) {
            return 3;
         }

         if (name.equals("shadowtex0") || name.equals("watershadow")) {
            return 4;
         }

         if (name.equals("shadow")) {
            return waterShadowEnabled ? 5 : 4;
         }

         if (name.equals("shadowtex1")) {
            return 5;
         }

         if (name.equals("depthtex0")) {
            return 6;
         }

         if (name.equals("gaux1")) {
            return 7;
         }

         if (name.equals("gaux2")) {
            return 8;
         }

         if (name.equals("gaux3")) {
            return 9;
         }

         if (name.equals("gaux4")) {
            return 10;
         }

         if (name.equals("depthtex1")) {
            return 12;
         }

         if (name.equals("shadowcolor0") || name.equals("shadowcolor")) {
            return 13;
         }

         if (name.equals("shadowcolor1")) {
            return 14;
         }

         if (name.equals("noisetex")) {
            return 15;
         }
      }

      if (stage == 1 || stage == 2) {
         if (name.equals("colortex0") || name.equals("colortex0")) {
            return 0;
         }

         if (name.equals("colortex1") || name.equals("gdepth")) {
            return 1;
         }

         if (name.equals("colortex2") || name.equals("gnormal")) {
            return 2;
         }

         if (name.equals("colortex3") || name.equals("composite")) {
            return 3;
         }

         if (name.equals("shadowtex0") || name.equals("watershadow")) {
            return 4;
         }

         if (name.equals("shadow")) {
            return waterShadowEnabled ? 5 : 4;
         }

         if (name.equals("shadowtex1")) {
            return 5;
         }

         if (name.equals("depthtex0") || name.equals("gdepthtex")) {
            return 6;
         }

         if (name.equals("colortex4") || name.equals("gaux1")) {
            return 7;
         }

         if (name.equals("colortex5") || name.equals("gaux2")) {
            return 8;
         }

         if (name.equals("colortex6") || name.equals("gaux3")) {
            return 9;
         }

         if (name.equals("colortex7") || name.equals("gaux4")) {
            return 10;
         }

         if (name.equals("depthtex1")) {
            return 11;
         }

         if (name.equals("depthtex2")) {
            return 12;
         }

         if (name.equals("shadowcolor0") || name.equals("shadowcolor")) {
            return 13;
         }

         if (name.equals("shadowcolor1")) {
            return 14;
         }

         if (name.equals("noisetex")) {
            return 15;
         }
      }

      return -1;
   }

   public static void endSpiderEyes() {
      if (isRenderingWorld && ProgramSpiderEyes.getId() != ProgramNone.getId()) {
         useProgram(ProgramEntities);
         GlStateManager.disableAlpha();
      }
   }

   public static void setClearColor(float red, float green, float blue, float alpha) {
      GlStateManager.clearColor(red, green, blue, alpha);
      clearColorR = red;
      clearColorG = green;
      clearColorB = blue;
   }

   public static void beginFPOverlay() {
      GlStateManager.disableLighting();
      GlStateManager.disableBlend();
   }

   public static BlockPos getCameraPosition() {
      return new BlockPos(cameraPositionX, cameraPositionY, cameraPositionZ);
   }

   public static void beginLeash() {
      pushProgram();
      useProgram(ProgramBasic);
   }

   public static void enableLightmap() {
      lightmapEnabled = true;
      if (activeProgram == ProgramTextured) {
         useProgram(ProgramTexturedLit);
      }
   }

   public static void setFogDensity(float value) {
      fogDensity = value;
      if (fogEnabled) {
         setProgramUniform1f(uniform_fogDensity, value);
      }
   }

   public static void endRender() {
      if (isShadowPass) {
         checkGLError("shadow endRender");
      } else {
         if (!isCompositeRendered) {
            renderCompositeFinal();
         }

         isRenderingWorld = false;
         GlStateManager.colorMask(true, true, true, true);
         useProgram(ProgramNone);
         RenderHelper.disableStandardItemLighting();
         checkGLError("endRender end");
      }
   }

   public static void init() {
      boolean flag3;
      if (!isInitializedOnce) {
         isInitializedOnce = true;
         flag3 = true;
      } else {
         flag3 = false;
      }

      if (!isShaderPackInitialized) {
         checkGLError("Shaders.init pre");
         if (getShaderPackName() != null) {
         }

         if (!capabilities.OpenGL20) {
            printChatAndLogError("No OpenGL 2.0");
         }

         if (!capabilities.GL_EXT_framebuffer_object) {
            printChatAndLogError("No EXT_framebuffer_object");
         }

         dfbDrawBuffers.position(0).limit(8);
         dfbColorTextures.position(0).limit(16);
         dfbDepthTextures.position(0).limit(3);
         sfbDrawBuffers.position(0).limit(8);
         sfbDepthTextures.position(0).limit(2);
         sfbColorTextures.position(0).limit(8);
         usedColorBuffers = 4;
         usedDepthBuffers = 1;
         usedShadowColorBuffers = 0;
         usedShadowDepthBuffers = 0;
         usedColorAttachs = 1;
         usedDrawBuffers = 1;
         Arrays.fill(gbuffersFormat, 6408);
         Arrays.fill(gbuffersClear, true);
         Arrays.fill(gbuffersClearColor, null);
         Arrays.fill(shadowHardwareFilteringEnabled, false);
         Arrays.fill(shadowMipmapEnabled, false);
         Arrays.fill(shadowFilterNearest, false);
         Arrays.fill(shadowColorMipmapEnabled, false);
         Arrays.fill(shadowColorFilterNearest, false);
         centerDepthSmoothEnabled = false;
         noiseTextureEnabled = false;
         sunPathRotation = 0.0F;
         shadowIntervalSize = 2.0F;
         shadowMapWidth = 1024;
         shadowMapHeight = 1024;
         spShadowMapWidth = 1024;
         spShadowMapHeight = 1024;
         shadowMapFOV = 90.0F;
         shadowMapHalfPlane = 160.0F;
         shadowMapIsOrtho = true;
         shadowDistanceRenderMul = -1.0F;
         aoLevel = -1.0F;
         useEntityAttrib = false;
         useMidTexCoordAttrib = false;
         useTangentAttrib = false;
         waterShadowEnabled = false;
         hasGeometryShaders = false;
         updateBlockLightLevel();
         Smoother.resetValues();
         shaderUniforms.reset();
         if (customUniforms != null) {
            customUniforms.reset();
         }

         ShaderProfile shaderProfile = ShaderUtils.detectProfile(shaderPackProfiles, shaderPackOptions, false);
         String text3 = "";
         if (currentWorld != null) {
            int dimensionId = currentWorld.provider.getDimensionId();
            if (shaderPackDimensions.contains(dimensionId)) {
               text3 = "world" + dimensionId + "/";
            }
         }

         for (int i = 0; i < ProgramsAll.length; i++) {
            Program program2 = ProgramsAll[i];
            program2.resetId();
            program2.resetConfiguration();
            if (program2.getProgramStage() != ProgramStage.NONE) {
               String name3 = program2.getName();
               String text4 = text3 + name3;
               boolean flag4 = true;
               if (shaderPackProgramConditions.containsKey(text4)) {
                  flag4 = shaderPackProgramConditions.get(text4).eval();
               }

               if (shaderProfile != null) {
                  flag4 = flag4 && !shaderProfile.isProgramDisabled(text4);
               }

               if (!flag4) {
                  SMCLog.info("Program disabled: " + text4);
                  text4 = text3 + "<disabled>";
               }

               String text5 = "/shaders/" + text4;
               String text6 = text5 + ".vsh";
               String text7 = text5 + ".gsh";
               String text8 = text5 + ".fsh";
               setupProgram(program2, text6, text7, text8);
               if (program2.getId() > 0) {
                  SMCLog.info("Program loaded: " + text4);
               }

               initDrawBuffers(program2);
               updateToggleBuffers(program2);
            }
         }

         hasDeferredPrograms = false;

         for (int i2 = 0; i2 < ProgramsDeferred.length; i2++) {
            if (ProgramsDeferred[i2].getId() != 0) {
               hasDeferredPrograms = true;
               break;
            }
         }

         usedColorAttachs = usedColorBuffers;
         shadowPassInterval = usedShadowDepthBuffers > 0 ? 1 : 0;
         shouldSkipDefaultShadow = usedShadowDepthBuffers > 0;
         SMCLog.info("usedColorBuffers: " + usedColorBuffers);
         SMCLog.info("usedDepthBuffers: " + usedDepthBuffers);
         SMCLog.info("usedShadowColorBuffers: " + usedShadowColorBuffers);
         SMCLog.info("usedShadowDepthBuffers: " + usedShadowDepthBuffers);
         SMCLog.info("usedColorAttachs: " + usedColorAttachs);
         SMCLog.info("usedDrawBuffers: " + usedDrawBuffers);
         dfbDrawBuffers.position(0).limit(usedDrawBuffers);
         dfbColorTextures.position(0).limit(usedColorBuffers * 2);
         dfbColorTexturesFlip.reset();

         for (int i3 = 0; i3 < usedDrawBuffers; i3++) {
            dfbDrawBuffers.put(i3, 36064 + i3);
         }

         int i4 = GL11.glGetInteger(34852);
         if (usedDrawBuffers > i4) {
            printChatAndLogError("[Shaders] Error: Not enough draw buffers, needed: " + usedDrawBuffers + ", available: " + i4);
         }

         sfbDrawBuffers.position(0).limit(usedShadowColorBuffers);

         for (int i5 = 0; i5 < usedShadowColorBuffers; i5++) {
            sfbDrawBuffers.put(i5, 36064 + i5);
         }

         for (int i6 = 0; i6 < ProgramsAll.length; i6++) {
            Program program3 = ProgramsAll[i6];
            Program program4 = program3;

            while (program4.getId() == 0 && program4.getProgramBackup() != program4) {
               program4 = program4.getProgramBackup();
            }

            if (program4 != program3 && program3 != ProgramShadow) {
               program3.copyFrom(program4);
            }
         }

         resize();
         resizeShadow();
         if (noiseTextureEnabled) {
            setupNoiseTexture();
         }

         if (defaultTexture == null) {
            defaultTexture = ShadersTex.createDefaultTexture();
         }

         GlStateManager.pushMatrix();
         GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
         preCelestialRotate();
         postCelestialRotate();
         GlStateManager.popMatrix();
         isShaderPackInitialized = true;
         loadEntityDataMap();
         resetDisplayLists();
         if (!flag3) {
         }

         checkGLError("Shaders.init");
      }
   }

   private static int createVertShader(Program program, String filename) {
      int i = ARBShaderObjects.glCreateShaderObjectARB(35633);
      if (i == 0) {
         return 0;
      }

      StringBuilder stringBuilder = new StringBuilder(131072);

      BufferedReader bufferedReader;
      try {
         bufferedReader = new BufferedReader(getShaderReader(filename));
      } catch (Exception exception) {
         ARBShaderObjects.glDeleteObjectARB(i);
         return 0;
      }

      ShaderOption[] changedOptions = getChangedOptions(shaderPackOptions);
      ArrayList arrayList = new ArrayList();

      try {
         bufferedReader = ShaderPackParser.resolveIncludes(bufferedReader, filename, shaderPack, 0, arrayList, 0);
         MacroState macroState = new MacroState();

         while (true) {
            String line2 = bufferedReader.readLine();
            if (line2 == null) {
               bufferedReader.close();
               break;
            }

            line2 = applyOptions(line2, changedOptions);
            stringBuilder.append(line2).append('\n');
            if (macroState.processLine(line2)) {
               ShaderLine shaderLine = ShaderParser.parseLine(line2);
               if (shaderLine != null) {
                  if (shaderLine.isAttribute("mc_Entity")) {
                     useEntityAttrib = true;
                     progUseEntityAttrib = true;
                  } else if (shaderLine.isAttribute("mc_midTexCoord")) {
                     useMidTexCoordAttrib = true;
                     progUseMidTexCoordAttrib = true;
                  } else if (shaderLine.isAttribute("at_tangent")) {
                     useTangentAttrib = true;
                     progUseTangentAttrib = true;
                  }

                  if (shaderLine.isConstInt("countInstances")) {
                     program.setCountInstances(shaderLine.getValueInt());
                     SMCLog.info("countInstances: " + program.getCountInstances());
                  }
               }
            }
         }
      } catch (Exception exception2) {
         SMCLog.severe("Couldn't read " + filename + "!");
         exception2.printStackTrace();
         ARBShaderObjects.glDeleteObjectARB(i);
         return 0;
      }

      if (saveFinalShaders) {
         saveShader(filename, stringBuilder.toString());
      }

      ARBShaderObjects.glShaderSourceARB(i, stringBuilder);
      ARBShaderObjects.glCompileShaderARB(i);
      if (GL20.glGetShaderi(i, 35713) != 1) {
         SMCLog.severe("Error compiling vertex shader: " + filename);
      }

      printShaderLogInfo(i, filename, arrayList);
      return i;
   }

   public static boolean isRenderShadowTranslucent() {
      return !shaderPackShadowTranslucent.isFalse();
   }

   public static void beginWater() {
      if (isRenderingWorld) {
         if (!isShadowPass) {
            renderDeferred();
            useProgram(ProgramWater);
            GlStateManager.enableBlend();
            GlStateManager.depthMask(true);
         } else {
            GlStateManager.depthMask(true);
         }
      }
   }

   public static ShaderOption[] getShaderPackOptions() {
      return shaderPackOptions;
   }

   public static void uninit() {
      if (isShaderPackInitialized) {
         checkGLError("Shaders.uninit pre");

         for (int index = 0; index < ProgramsAll.length; index++) {
            Program program2 = ProgramsAll[index];
            if (program2.getRef() != 0) {
               ARBShaderObjects.glDeleteObjectARB(program2.getRef());
               checkGLError("del programRef");
            }

            program2.setRef(0);
            program2.setId(0);
            program2.setDrawBufSettings((String)null);
            program2.setDrawBuffers((IntBuffer)null);
            program2.setCompositeMipmapSetting(0);
         }

         hasDeferredPrograms = false;
         if (dfb != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(dfb);
            dfb = 0;
            checkGLError("del dfb");
         }

         if (sfb != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(sfb);
            sfb = 0;
            checkGLError("del sfb");
         }

         if (dfbDepthTextures != null) {
            GlStateManager.deleteTextures(dfbDepthTextures);
            fillIntBufferZero(dfbDepthTextures);
            checkGLError("del dfbDepthTextures");
         }

         if (dfbColorTextures != null) {
            GlStateManager.deleteTextures(dfbColorTextures);
            fillIntBufferZero(dfbColorTextures);
            checkGLError("del dfbTextures");
         }

         if (sfbDepthTextures != null) {
            GlStateManager.deleteTextures(sfbDepthTextures);
            fillIntBufferZero(sfbDepthTextures);
            checkGLError("del shadow depth");
         }

         if (sfbColorTextures != null) {
            GlStateManager.deleteTextures(sfbColorTextures);
            fillIntBufferZero(sfbColorTextures);
            checkGLError("del shadow color");
         }

         if (dfbDrawBuffers != null) {
            fillIntBufferZero(dfbDrawBuffers);
         }

         if (noiseTexture != null) {
            noiseTexture.deleteTexture();
            noiseTexture = null;
         }

         SMCLog.info("Uninit");
         shadowPassInterval = 0;
         shouldSkipDefaultShadow = false;
         isShaderPackInitialized = false;
         checkGLError("Shaders.uninit");
      }
   }

   private static void deleteCustomTextures(ICustomTexture[] cts) {
      if (cts != null) {
         for (int i = 0; i < cts.length; i++) {
            cts[i].deleteTexture();
         }
      }
   }

   private static ShaderOption[] getVisibleOptions(ShaderOption[] ops) {
      ArrayList arrayList = new ArrayList();

      for (int index = 0; index < ops.length; index++) {
         ShaderOption shaderOption = ops[index];
         if (shaderOption.isVisible()) {
            arrayList.add(shaderOption);
         }
      }

      return (ShaderOption[]) arrayList.toArray(new ShaderOption[arrayList.size()]);
   }

   private static void setupFrameBuffer() {
      if (dfb != 0) {
         EXTFramebufferObject.glDeleteFramebuffersEXT(dfb);
         GlStateManager.deleteTextures(dfbDepthTextures);
         GlStateManager.deleteTextures(dfbColorTextures);
      }

      dfb = EXTFramebufferObject.glGenFramebuffersEXT();
      GL11.glGenTextures(dfbDepthTextures.clear().limit(usedDepthBuffers));
      GL11.glGenTextures(dfbColorTextures.clear().limit(16));
      dfbDepthTextures.position(0);
      dfbColorTextures.position(0);
      EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
      GL20.glDrawBuffers(0);
      GL11.glReadBuffer(0);

      for (int index = 0; index < usedDepthBuffers; index++) {
         GlStateManager.bindTexture(dfbDepthTextures.get(index));
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
         GL11.glTexParameteri(3553, 10241, 9728);
         GL11.glTexParameteri(3553, 10240, 9728);
         GL11.glTexParameteri(3553, 34891, 6409);
         GL11.glTexImage2D(3553, 0, 6402, renderWidth, renderHeight, 0, 6402, 5126, (FloatBuffer)null);
      }

      EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
      GL20.glDrawBuffers(dfbDrawBuffers);
      GL11.glReadBuffer(0);
      checkGLError("FT d");

      for (int nestedIndex = 0; nestedIndex < usedColorBuffers; nestedIndex++) {
         GlStateManager.bindTexture(dfbColorTexturesFlip.getA(nestedIndex));
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexImage2D(
            3553, 0, gbuffersFormat[nestedIndex], renderWidth, renderHeight, 0, getPixelFormat(gbuffersFormat[nestedIndex]), 33639, (ByteBuffer)null
         );
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + nestedIndex, 3553, dfbColorTexturesFlip.getA(nestedIndex), 0);
         checkGLError("FT c");
      }

      for (int outerIndex = 0; outerIndex < usedColorBuffers; outerIndex++) {
         GlStateManager.bindTexture(dfbColorTexturesFlip.getB(outerIndex));
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexImage2D(
            3553, 0, gbuffersFormat[outerIndex], renderWidth, renderHeight, 0, getPixelFormat(gbuffersFormat[outerIndex]), 33639, (ByteBuffer)null
         );
         checkGLError("FT ca");
      }

      int i = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
      if (i == 36058) {
         printChatAndLogError("[Shaders] Error: Failed framebuffer incomplete formats");

         for (int innerIndex = 0; innerIndex < usedColorBuffers; innerIndex++) {
            GlStateManager.bindTexture(dfbColorTexturesFlip.getA(innerIndex));
            GL11.glTexImage2D(3553, 0, 6408, renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + innerIndex, 3553, dfbColorTexturesFlip.getA(innerIndex), 0);
            checkGLError("FT c");
         }

         i = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
         if (i == 36053) {
            SMCLog.info("complete");
         }
      }

      GlStateManager.bindTexture(0);
      if (i != 36053) {
         printChatAndLogError("[Shaders] Error: Failed creating framebuffer! (Status " + i + ")");
      } else {
         SMCLog.info("Framebuffer created.");
      }
   }

   private static ShaderOption[] getShaderOptionsRest(Map<String, ScreenShaderOptions> mapScreens, ShaderOption[] ops) {
      HashSet hashSet = new HashSet();

      for (String text3 : mapScreens.keySet()) {
         ScreenShaderOptions screenShaderOptions = mapScreens.get(text3);
         ShaderOption[] shaderOptions = screenShaderOptions.getShaderOptions();

         for (int i = 0; i < shaderOptions.length; i++) {
            ShaderOption shaderOption = shaderOptions[i];
            if (shaderOption != null) {
               hashSet.add(shaderOption.getName());
            }
         }
      }

      ArrayList arrayList = new ArrayList();

      for (int i2 = 0; i2 < ops.length; i2++) {
         ShaderOption shaderOption2 = ops[i2];
         if (shaderOption2.isVisible()) {
            String name3 = shaderOption2.getName();
            if (!hashSet.contains(name3)) {
               arrayList.add(shaderOption2);
            }
         }
      }

      return (ShaderOption[]) arrayList.toArray(new ShaderOption[arrayList.size()]);
   }

   public static void setFogColor(float f, float g, float b) {
      fogColorR = f;
      fogColorG = g;
      fogColorB = b;
      setProgramUniform3f(uniform_fogColor, fogColorR, fogColorG, fogColorB);
   }

   public static void setCameraShadow(float partialTicks) {
      Entity entity = mc.getRenderViewEntity();
      double xCoordinate = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
      double yCoordinate = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
      double zCoordinate = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
      updateCameraOffset(entity);
      cameraPositionX = xCoordinate - cameraOffsetX;
      cameraPositionY = yCoordinate;
      cameraPositionZ = zCoordinate - cameraOffsetZ;
      GL11.glGetFloat(2983, projection.position(0));
      SMath.invertMat4FBFA(projectionInverse.position(0), projection.position(0), faProjectionInverse, faProjection);
      projection.position(0);
      projectionInverse.position(0);
      GL11.glGetFloat(2982, modelView.position(0));
      SMath.invertMat4FBFA(modelViewInverse.position(0), modelView.position(0), faModelViewInverse, faModelView);
      modelView.position(0);
      modelViewInverse.position(0);
      GL11.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      if (shadowMapIsOrtho) {
         GL11.glOrtho(-shadowMapHalfPlane, shadowMapHalfPlane, -shadowMapHalfPlane, shadowMapHalfPlane, 0.05F, 256.0);
      } else {
         GLU.gluPerspective(shadowMapFOV, (float)shadowMapWidth / shadowMapHeight, 0.05F, 256.0F);
      }

      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glTranslatef(0.0F, 0.0F, -100.0F);
      GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      celestialAngle = mc.theWorld.getCelestialAngle(partialTicks);
      sunAngle = celestialAngle < 0.75F ? celestialAngle + 0.25F : celestialAngle - 0.75F;
      float f = celestialAngle * -360.0F;
      float f2 = shadowAngleInterval > 0.0F ? f % shadowAngleInterval - shadowAngleInterval * 0.5F : 0.0F;
      if (sunAngle <= 0.5) {
         GL11.glRotatef(f - f2, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(sunPathRotation, 1.0F, 0.0F, 0.0F);
         shadowAngle = sunAngle;
      } else {
         GL11.glRotatef(f + 180.0F - f2, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(sunPathRotation, 1.0F, 0.0F, 0.0F);
         shadowAngle = sunAngle - 0.5F;
      }

      if (shadowMapIsOrtho) {
         float shadowIntervalSize2 = shadowIntervalSize;
         float f3 = shadowIntervalSize2 / 2.0F;
         GL11.glTranslatef(
            (float)xCoordinate % shadowIntervalSize2 - f3, (float)yCoordinate % shadowIntervalSize2 - f3, (float)zCoordinate % shadowIntervalSize2 - f3
         );
      }

      float f4 = sunAngle * (float) (Math.PI * 2);
      float f5 = (float)Math.cos(f4);
      float f6 = (float)Math.sin(f4);
      float f7 = sunPathRotation * (float) (Math.PI * 2);
      float f8 = f5;
      float f9 = f6 * (float)Math.cos(f7);
      float f10 = f6 * (float)Math.sin(f7);
      if (sunAngle > 0.5) {
         f8 = -f5;
         f9 = -f9;
         f10 = -f10;
      }

      shadowLightPositionVector[0] = f8;
      shadowLightPositionVector[1] = f9;
      shadowLightPositionVector[2] = f10;
      shadowLightPositionVector[3] = 0.0F;
      GL11.glGetFloat(2983, shadowProjection.position(0));
      SMath.invertMat4FBFA(shadowProjectionInverse.position(0), shadowProjection.position(0), faShadowProjectionInverse, faShadowProjection);
      shadowProjection.position(0);
      shadowProjectionInverse.position(0);
      GL11.glGetFloat(2982, shadowModelView.position(0));
      SMath.invertMat4FBFA(shadowModelViewInverse.position(0), shadowModelView.position(0), faShadowModelViewInverse, faShadowModelView);
      shadowModelView.position(0);
      shadowModelViewInverse.position(0);
      handleShaderUniformM4(uniform_gbufferProjection, projection);
      handleShaderUniformM4(uniform_gbufferProjectionInverse, projectionInverse);
      handleShaderUniformM4(uniform_gbufferPreviousProjection, previousProjection);
      handleShaderUniformM4(uniform_gbufferModelView, modelView);
      handleShaderUniformM4(uniform_gbufferModelViewInverse, modelViewInverse);
      handleShaderUniformM4(uniform_gbufferPreviousModelView, previousModelView);
      handleShaderUniformM4(uniform_shadowProjection, shadowProjection);
      handleShaderUniformM4(uniform_shadowProjectionInverse, shadowProjectionInverse);
      handleShaderUniformM4(uniform_shadowModelView, shadowModelView);
      handleShaderUniformM4(uniform_shadowModelViewInverse, shadowModelViewInverse);
      mc.gameSettings.thirdPersonView = 1;
      checkGLError("setCamera");
   }

   private static Program getProgramById(int programID) {
      for (int index = 0; index < ProgramsAll.length; index++) {
         Program program2 = ProgramsAll[index];
         if (program2.getId() == programID) {
            return program2;
         }
      }

      return ProgramNone;
   }

   public static void enableTexture2D() {
      if (isRenderingSky) {
         useProgram(ProgramSkyTextured);
      } else if (activeProgram == ProgramBasic) {
         useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
      }
   }

   public static void genCompositeMipmap() {
      if (hasGlGenMipmap) {
         for (int index = 0; index < usedColorBuffers; index++) {
            if ((activeCompositeMipmapSetting & 1 << index) != 0) {
               GlStateManager.setActiveTexture(33984 + colorTextureImageUnit[index]);
               GL11.glTexParameteri(3553, 10241, 9987);
               GL30.glGenerateMipmap(3553);
            }
         }

         GlStateManager.setActiveTexture(33984);
      }
   }

   public static void nextBlockEntity(TileEntity tileEntity) {
      if (isRenderingWorld) {
         checkGLError("nextBlockEntity");
         useProgram(ProgramBlock);
         setBlockEntityId(tileEntity);
      }
   }

   public static void nextAntialiasingLevel(boolean forward) {
      if (forward) {
         configAntialiasingLevel += 2;
         if (configAntialiasingLevel > 4) {
            configAntialiasingLevel = 0;
         }
      } else {
         configAntialiasingLevel -= 2;
         if (configAntialiasingLevel < 0) {
            configAntialiasingLevel = 4;
         }
      }

      configAntialiasingLevel = configAntialiasingLevel / 2 * 2;
      configAntialiasingLevel = Config.limit(configAntialiasingLevel, 0, 4);
   }

   public static int getBufferIndexFromString(String name) {
      return name.equals("colortex0") || name.equals("gcolor")
         ? 0
         : (
            name.equals("colortex1") || name.equals("gdepth")
               ? 1
               : (
                  name.equals("colortex2") || name.equals("gnormal")
                     ? 2
                     : (
                        name.equals("colortex3") || name.equals("composite")
                           ? 3
                           : (
                              name.equals("colortex4") || name.equals("gaux1")
                                 ? 4
                                 : (
                                    name.equals("colortex5") || name.equals("gaux2")
                                       ? 5
                                       : (
                                          !name.equals("colortex6") && !name.equals("gaux3")
                                             ? (!name.equals("colortex7") && !name.equals("gaux4") ? -1 : 7)
                                             : 6
                                       )
                                 )
                           )
                     )
               )
         );
   }

   private static void resize() {
      renderDisplayWidth = mc.displayWidth;
      renderDisplayHeight = mc.displayHeight;
      renderWidth = Math.round(renderDisplayWidth * configRenderResMul);
      renderHeight = Math.round(renderDisplayHeight * configRenderResMul);
      setupFrameBuffer();
   }

   public static void nextEntity(Entity entity) {
      if (isRenderingWorld) {
         useProgram(ProgramEntities);
         setEntityId(entity);
      }
   }

   public static boolean isItemToRenderMainTranslucent() {
      return itemToRenderMainTranslucent;
   }

   private static void saveOptionProperties(IShaderPack sp, Properties props) throws IOException {
      String text3 = "shaderpacks/" + sp.getName() + ".txt";
      File file = new File(Minecraft.getMinecraft().mcDataDir, text3);
      if (props.isEmpty()) {
         file.delete();
      } else {
         FileOutputStream fileOutputStream = new FileOutputStream(file);
         props.store(fileOutputStream, (String)null);
         fileOutputStream.flush();
         fileOutputStream.close();
      }
   }

   public static ArrayList listOfShaders() {
      ArrayList arrayList = new ArrayList();
      arrayList.add("OFF");
      arrayList.add("(internal)");
      int count = arrayList.size();

      try {
         if (!shaderPacksDir.exists()) {
            shaderPacksDir.mkdir();
         }

         File[] afile = shaderPacksDir.listFiles();

         for (int innerIndex = 0; innerIndex < afile.length; innerIndex++) {
            File file = afile[innerIndex];
            String name = file.getName();
            if (file.isDirectory()) {
               if (!name.equals("debug")) {
                  File file2 = new File(file, "shaders");
                  if (file2.exists() && file2.isDirectory()) {
                     arrayList.add(name);
                  }
               }
            } else if (file.isFile() && name.toLowerCase().endsWith(".zip")) {
               arrayList.add(name);
            }
         }
      } catch (Exception exception) {
      }

      List<String> list = arrayList.subList(count, arrayList.size());
      Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
      return arrayList;
   }

   public static void startup(Minecraft mcIn) {
      checkShadersModInstalled();
      mc = Minecraft.getMinecraft();
      capabilities = GLContext.getCapabilities();
      glVersionString = GL11.glGetString(7938);
      glVendorString = GL11.glGetString(7936);
      glRendererString = GL11.glGetString(7937);
      SMCLog.info("OpenGL Version: " + glVersionString);
      SMCLog.info("Vendor:  " + glVendorString);
      SMCLog.info("Renderer: " + glRendererString);
      SMCLog.info(
         "Capabilities: "
            + (capabilities.OpenGL20 ? " 2.0 " : " - ")
            + (capabilities.OpenGL21 ? " 2.1 " : " - ")
            + (capabilities.OpenGL30 ? " 3.0 " : " - ")
            + (capabilities.OpenGL32 ? " 3.2 " : " - ")
            + (capabilities.OpenGL40 ? " 4.0 " : " - ")
      );
      SMCLog.info("GL_MAX_DRAW_BUFFERS: " + GL11.glGetInteger(34852));
      SMCLog.info("GL_MAX_COLOR_ATTACHMENTS_EXT: " + GL11.glGetInteger(36063));
      SMCLog.info("GL_MAX_TEXTURE_IMAGE_UNITS: " + GL11.glGetInteger(34930));
      hasGlGenMipmap = capabilities.OpenGL30;
      loadConfig();
   }

   private static int getTextureFormatFromString(String par) {
      par = par.trim();

      for (int index = 0; index < formatNames.length; index++) {
         String text3 = formatNames[index];
         if (par.equals(text3)) {
            return formatIds[index];
         }
      }

      return 0;
   }

   private static void resizeShadow() {
      needResizeShadow = false;
      shadowMapWidth = Math.round(spShadowMapWidth * configShadowResMul);
      shadowMapHeight = Math.round(spShadowMapHeight * configShadowResMul);
      setupShadowFrameBuffer();
   }

   private static int createGeomShader(Program program, String filename) {
      int i = ARBShaderObjects.glCreateShaderObjectARB(36313);
      if (i == 0) {
         return 0;
      }

      StringBuilder stringBuilder = new StringBuilder(131072);

      BufferedReader bufferedReader;
      try {
         bufferedReader = new BufferedReader(getShaderReader(filename));
      } catch (Exception exception) {
         ARBShaderObjects.glDeleteObjectARB(i);
         return 0;
      }

      ShaderOption[] changedOptions = getChangedOptions(shaderPackOptions);
      ArrayList arrayList = new ArrayList();
      progArbGeometryShader4 = false;
      progMaxVerticesOut = 3;

      try {
         bufferedReader = ShaderPackParser.resolveIncludes(bufferedReader, filename, shaderPack, 0, arrayList, 0);
         MacroState macroState = new MacroState();

         while (true) {
            String line2 = bufferedReader.readLine();
            if (line2 == null) {
               bufferedReader.close();
               break;
            }

            line2 = applyOptions(line2, changedOptions);
            stringBuilder.append(line2).append('\n');
            if (macroState.processLine(line2)) {
               ShaderLine shaderLine = ShaderParser.parseLine(line2);
               if (shaderLine != null) {
                  if (shaderLine.isExtension("GL_ARB_geometry_shader4")) {
                     String text3 = Config.normalize(shaderLine.getValue());
                     if (text3.equals("enable") || text3.equals("require") || text3.equals("warn")) {
                        progArbGeometryShader4 = true;
                     }
                  }

                  if (shaderLine.isConstInt("maxVerticesOut")) {
                     progMaxVerticesOut = shaderLine.getValueInt();
                  }
               }
            }
         }
      } catch (Exception exception2) {
         SMCLog.severe("Couldn't read " + filename + "!");
         exception2.printStackTrace();
         ARBShaderObjects.glDeleteObjectARB(i);
         return 0;
      }

      if (saveFinalShaders) {
         saveShader(filename, stringBuilder.toString());
      }

      ARBShaderObjects.glShaderSourceARB(i, stringBuilder);
      ARBShaderObjects.glCompileShaderARB(i);
      if (GL20.glGetShaderi(i, 35713) != 1) {
         SMCLog.severe("Error compiling geometry shader: " + filename);
      }

      printShaderLogInfo(i, filename, arrayList);
      return i;
   }

   private static void setupNoiseTexture() {
      if (noiseTexture == null && noiseTexturePath != null) {
         noiseTexture = loadCustomTexture(15, noiseTexturePath);
      }

      if (noiseTexture == null) {
         noiseTexture = new HFNoiseTexture(noiseTextureResolution, noiseTextureResolution);
      }
   }

   public static void beginSpiderEyes() {
      if (isRenderingWorld && ProgramSpiderEyes.getId() != ProgramNone.getId()) {
         useProgram(ProgramSpiderEyes);
         GlStateManager.enableAlpha();
         GlStateManager.alphaFunc(516, 0.0F);
         GlStateManager.blendFunc(770, 771);
      }
   }

   public static void endBlockEntities() {
      if (isRenderingWorld) {
         checkGLError("endBlockEntities");
         setBlockEntityId((TileEntity)null);
         useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
         ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
      }
   }

   private static void saveShaderPackOptions(ShaderOption[] sos, IShaderPack sp) {
      PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
      if (shaderPackOptions != null) {
         for (int index = 0; index < sos.length; index++) {
            ShaderOption shaderOption = sos[index];
            if (shaderOption.isChanged() && shaderOption.isEnabled()) {
               propertiesOrdered.setProperty(shaderOption.getName(), shaderOption.getValue());
            }
         }
      }

      try {
         saveOptionProperties(sp, propertiesOrdered);
      } catch (IOException ioException) {
         Config.warn("[Shaders] Error saving configuration for " + shaderPack.getName());
         ioException.printStackTrace();
      }
   }

   private static void updateToggleBuffers(Program program) {
      boolean[] booleanArray = program.getToggleColorTextures();
      Boolean[] booleanArray1 = program.getBuffersFlip();

      for (int index = 0; index < booleanArray1.length; index++) {
         Boolean bool = booleanArray1[index];
         if (bool != null) {
            booleanArray[index] = bool;
         }
      }
   }

   public static void applyHandDepth() {
      if (configHandDepthMul != 1.0) {
         GL11.glScaled(1.0, 1.0, configHandDepthMul);
      }
   }

   public static void endWater() {
      if (isRenderingWorld) {
         if (isShadowPass) {
         }

         useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
      }
   }

   private static void updateAlphaBlend(Program programOld, Program programNew) {
      if (programOld.getAlphaState() != null) {
         GlStateManager.unlockAlpha();
      }

      if (programOld.getBlendState() != null) {
         GlStateManager.unlockBlend();
      }

      GlAlphaState glAlphaState = programNew.getAlphaState();
      if (glAlphaState != null) {
         GlStateManager.lockAlpha(glAlphaState);
      }

      GlBlendState glBlendState = programNew.getBlendState();
      if (glBlendState != null) {
         GlStateManager.lockBlend(glBlendState);
      }
   }

   public static void renderDeferred() {
      if (!isShadowPass) {
         boolean conditionMet = checkBufferFlip(ProgramDeferredPre);
         if (hasDeferredPrograms) {
            checkGLError("pre-render Deferred");
            renderComposites(ProgramsDeferred, false);
            conditionMet = true;
         }

         if (conditionMet) {
            bindGbuffersTextures();

            for (int index = 0; index < usedColorBuffers; index++) {
               EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + index, 3553, dfbColorTexturesFlip.getA(index), 0);
            }

            if (ProgramWater.getDrawBuffers() != null) {
               setDrawBuffers(ProgramWater.getDrawBuffers());
            } else {
               setDrawBuffers(dfbDrawBuffers);
            }

            GlStateManager.setActiveTexture(33984);
            mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         }
      }
   }

   public static void beginBlockEntities() {
      if (isRenderingWorld) {
         checkGLError("beginBlockEntities");
         useProgram(ProgramBlock);
      }
   }

   public static void checkShadersModInstalled() {
      try {
         Class.forName("shadersmod.transform.SMCClassTransformer");
      } catch (Throwable throwable) {
         return;
      }

      throw new RuntimeException("Shaders Mod detected. Please remove it, OptiFine has built-in support for shaders.");
   }

   public static void popProgram() {
      useProgram(programStack.pop());
   }

   public static void scheduleResizeShadow() {
      needResizeShadow = true;
   }

   public static void readCenterDepth() {
      if (!isShadowPass && centerDepthSmoothEnabled) {
         tempDirectFloatBuffer.clear();
         GL11.glReadPixels(renderWidth / 2, renderHeight / 2, 1, 1, 6402, 5126, tempDirectFloatBuffer);
         centerDepth = tempDirectFloatBuffer.get(0);
         float f = (float)diffSystemTime * 0.01F;
         float f2 = (float)Math.exp(Math.log(0.5) * f / centerDepthSmoothHalflife);
         centerDepthSmooth = centerDepthSmooth * f2 + centerDepth * (1.0F - f2);
      }
   }

   private static void bindCustomTextures(ICustomTexture[] cts) {
      if (cts != null) {
         for (int index = 0; index < cts.length; index++) {
            ICustomTexture iCustomTexture = cts[index];
            GlStateManager.setActiveTexture(33984 + iCustomTexture.getTextureUnit());
            int textureId = iCustomTexture.getTextureId();
            int target = iCustomTexture.getTarget();
            if (target == 3553) {
               GlStateManager.bindTexture(textureId);
            } else {
               GL11.glBindTexture(target, textureId);
            }
         }
      }
   }

   private static void renderFinal() {
      isRenderingDfb = false;
      mc.getFramebuffer().bindFramebuffer(true);
      OpenGlHelper.handleInt4(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, mc.getFramebuffer().framebufferTexture);
      GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
      if (EntityRenderer.anaglyphEnable) {
         boolean flag3 = EntityRenderer.anaglyphField != 0;
         GlStateManager.colorMask(flag3, !flag3, !flag3, true);
      }

      GlStateManager.depthMask(true);
      GL11.glClearColor(clearColorR, clearColorG, clearColorB, 1.0F);
      GL11.glClear(16640);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableTexture2D();
      GlStateManager.disableAlpha();
      GlStateManager.disableBlend();
      GlStateManager.enableDepth();
      GlStateManager.depthFunc(519);
      GlStateManager.depthMask(false);
      checkGLError("pre-final");
      useProgram(ProgramFinal);
      checkGLError("final");
      if (activeCompositeMipmapSetting != 0) {
         genCompositeMipmap();
      }

      drawComposite();
      checkGLError("renderCompositeFinal");
   }

   public static IShaderPack getShaderPack(String name) {
      if (name == null) {
         return null;
      }

      name = name.trim();
      if (name.isEmpty() || name.equals("OFF")) {
         return null;
      }

      if (name.equals("(internal)")) {
         return new ShaderPackDefault();
      }

      try {
         File file = new File(shaderPacksDir, name);
         return file.isDirectory()
            ? new ShaderPackFolder(name, file)
            : (file.isFile() && name.toLowerCase().endsWith(".zip") ? new ShaderPackZip(name, file) : null);
      } catch (Exception exception) {
         exception.printStackTrace();
         return null;
      }
   }

   private static void setEnumShaderOption(EnumShaderOption eso, String str) {
      if (str == null) {
         str = eso.getValueDefault();
      }

      switch (eso) {
         case ANTIALIASING:
            configAntialiasingLevel = Config.parseInt(str, 0);
            break;
         case NORMAL_MAP:
            configNormalMap = Config.parseBoolean(str, true);
            break;
         case SPECULAR_MAP:
            configSpecularMap = Config.parseBoolean(str, true);
            break;
         case RENDER_RES_MUL:
            configRenderResMul = Config.parseFloat(str, 1.0F);
            break;
         case SHADOW_RES_MUL:
            configShadowResMul = Config.parseFloat(str, 1.0F);
            break;
         case HAND_DEPTH_MUL:
            configHandDepthMul = Config.parseFloat(str, 0.125F);
            break;
         case CLOUD_SHADOW:
            configCloudShadow = Config.parseBoolean(str, true);
            break;
         case OLD_HAND_LIGHT:
            configOldHandLight.setPropertyValue(str);
            break;
         case OLD_LIGHTING:
            configOldLighting.setPropertyValue(str);
            break;
         case SHADER_PACK:
            currentShaderName = str;
            break;
         case TWEAK_BLOCK_DAMAGE:
            configTweakBlockDamage = Config.parseBoolean(str, true);
            break;
         case SHADOW_CLIP_FRUSTRUM:
            configShadowClipFrustrum = Config.parseBoolean(str, true);
            break;
         case TEX_MIN_FIL_B:
            configTexMinFilB = Config.parseInt(str, 0);
            break;
         case TEX_MIN_FIL_N:
            configTexMinFilN = Config.parseInt(str, 0);
            break;
         case TEX_MIN_FIL_S:
            configTexMinFilS = Config.parseInt(str, 0);
            break;
         case TEX_MAG_FIL_B:
            configTexMagFilB = Config.parseInt(str, 0);
            break;
         case TEX_MAG_FIL_N:
            configTexMagFilB = Config.parseInt(str, 0);
            break;
         case TEX_MAG_FIL_S:
            configTexMagFilB = Config.parseInt(str, 0);
            break;
         default:
            throw new IllegalArgumentException("Unknown option: " + eso);
      }
   }

   private static ICustomTexture[] loadCustomTextures(Properties props, int stage) {
      String text3 = "texture." + STAGE_NAMES[stage] + ".";
      Set set = props.keySet();
      ArrayList arrayList = new ArrayList();

      for (String text4 : (Iterable<String>) set) {
         if (text4.startsWith(text3)) {
            String text5 = StrUtils.removePrefix(text4, text3);
            text5 = StrUtils.removeSuffix(text5, new String[]{".0", ".1", ".2", ".3", ".4", ".5", ".6", ".7", ".8", ".9"});
            String text6 = props.getProperty(text4).trim();
            int textureIndex = getTextureIndex(stage, text5);
            if (textureIndex < 0) {
               SMCLog.warning("Invalid texture name: " + text4);
            } else {
               ICustomTexture iCustomTexture = loadCustomTexture(textureIndex, text6);
               if (iCustomTexture != null) {
                  SMCLog.info("Custom texture: " + text4 + " = " + text6);
                  arrayList.add(iCustomTexture);
               }
            }
         }
      }

      return arrayList.size() <= 0 ? null : (ICustomTexture[]) arrayList.toArray(new ICustomTexture[arrayList.size()]);
   }

   public static void handleFloat(float f) {
      if (!isShadowPass) {
         EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
         GL11.glViewport(0, 0, renderWidth, renderHeight);
         activeDrawBuffers = null;
         ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
         useProgram(ProgramTextured);
         checkGLError("end beginRenderPass");
      }
   }

   private static ICustomTexture loadCustomTexture(int textureUnit, String path) {
      if (path == null) {
         return null;
      }

      path = path.trim();
      return path.indexOf(58) >= 0
         ? loadCustomTextureLocation(textureUnit, path)
         : (path.indexOf(32) >= 0 ? loadCustomTextureRaw(textureUnit, path) : loadCustomTextureShaders(textureUnit, path));
   }

   public static ShaderOption[] getChangedOptions(ShaderOption[] ops) {
      ArrayList arrayList = new ArrayList();

      for (int index = 0; index < ops.length; index++) {
         ShaderOption shaderOption = ops[index];
         if (shaderOption.isEnabled() && shaderOption.isChanged()) {
            arrayList.add(shaderOption);
         }
      }

      return (ShaderOption[]) arrayList.toArray(new ShaderOption[arrayList.size()]);
   }

   private static void loadEntityDataMap() {
      mapBlockToEntityData = new IdentityHashMap<>(300);
      if (mapBlockToEntityData.isEmpty()) {
         for (ResourceLocation resourceLocation : Block.blockRegistry.getKeys()) {
            Block object = Block.blockRegistry.getObject(resourceLocation);
            int idForObject = Block.blockRegistry.getIDForObject(object);
            mapBlockToEntityData.put(object, idForObject);
         }
      }

      BufferedReader bufferedReader = null;

      try {
         bufferedReader = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream("/mc_Entity_x.txt")));
      } catch (Exception exception) {
      }

      if (bufferedReader != null) {
         String text3;
         try {
            while ((text3 = bufferedReader.readLine()) != null) {
               Matcher matcher2 = patternLoadEntityDataMap.matcher(text3);
               if (matcher2.matches()) {
                  String text4 = matcher2.group(1);
                  int i = Integer.parseInt(matcher2.group(2));
                  Block blockFromName = Block.getBlockFromName(text4);
                  if (blockFromName != null) {
                     mapBlockToEntityData.put(blockFromName, i);
                  } else {
                     Object[] objects = new Object[]{text4};
                     SMCLog.warning("Unknown block name %s", objects);
                  }
               } else {
                  Object[] objects2 = new Object[]{text3};
                  SMCLog.warning("unmatched %s\n", objects2);
               }
            }
         } catch (Exception exception2) {
            SMCLog.warning("Error parsing mc_Entity_x.txt");
         }
      }

      if (bufferedReader != null) {
         try {
            bufferedReader.close();
         } catch (Exception exception3) {
         }
      }
   }

   private static Reader getShaderReader(String filename) {
      return new InputStreamReader(shaderPack.getResourceAsStream(filename));
   }

   private static void initDrawBuffers(Program program) {
      int count = GL11.glGetInteger(34852);
      Arrays.fill(program.getToggleColorTextures(), false);
      if (program == ProgramFinal) {
         program.setDrawBuffers((IntBuffer)null);
      } else if (program.getId() == 0) {
         if (program == ProgramShadow) {
            program.setDrawBuffers(drawBuffersNone);
         } else {
            program.setDrawBuffers(drawBuffersColorAtt0);
         }
      } else {
         String drawBufSettings = program.getDrawBufSettings();
         if (drawBufSettings == null) {
            if (program != ProgramShadow && program != ProgramShadowSolid && program != ProgramShadowCutout) {
               program.setDrawBuffers(dfbDrawBuffers);
               usedDrawBuffers = usedColorBuffers;
               Arrays.fill(program.getToggleColorTextures(), 0, usedColorBuffers, true);
            } else {
               program.setDrawBuffers(sfbDrawBuffers);
            }
         } else {
            IntBuffer drawBuffersBuffer = program.getDrawBuffersBuffer();
            int length2 = drawBufSettings.length();
            usedDrawBuffers = Math.max(usedDrawBuffers, length2);
            length2 = Math.min(length2, count);
            program.setDrawBuffers(drawBuffersBuffer);
            drawBuffersBuffer.limit(length2);

            for (int i = 0; i < length2; i++) {
               int drawBuffer = getDrawBuffer(program, drawBufSettings, i);
               drawBuffersBuffer.put(i, drawBuffer);
            }
         }
      }
   }

   public static void setDrawBuffers(IntBuffer drawBuffers) {
      if (drawBuffers == null) {
         drawBuffers = drawBuffersNone;
      }

      if (activeDrawBuffers != drawBuffers) {
         activeDrawBuffers = drawBuffers;
         GL20.glDrawBuffers(drawBuffers);
         checkGLError("setDrawBuffers");
      }
   }

   public static void endSky() {
      isRenderingSky = false;
      setDrawBuffers(dfbDrawBuffers);
      useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
      popEntity();
   }

   public static void endEntities() {
      if (isRenderingWorld) {
         setEntityId((Entity)null);
         useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
      }
   }

   public static boolean isFrustumCulling() {
      return !shaderPackFrustumCulling.isFalse();
   }

   public static void resetDisplayLists() {
      SMCLog.info("Reset model renderers");
      countResetDisplayLists++;
      SMCLog.info("Reset world renderers");
      mc.renderGlobal.loadRenderers();
   }

   public static void mcProfilerEndSection() {
      mc.mcProfiler.endSection();
   }

   private static void printChat(String str) {
      mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
   }

   public static void storeConfig() {
      SMCLog.info("Save shaders configuration.");
      if (shadersConfig == null) {
         shadersConfig = new PropertiesOrdered();
      }

      EnumShaderOption[] aenumshaderoption = EnumShaderOption.values();

      for (int index = 0; index < aenumshaderoption.length; index++) {
         EnumShaderOption enumShaderOption = aenumshaderoption[index];
         String text = enumShaderOption.getPropertyKey();
         String text2 = getEnumShaderOption(enumShaderOption);
         shadersConfig.setProperty(text, text2);
      }

      try {
         FileWriter fileWriter = new FileWriter(configFile);
         shadersConfig.store(fileWriter, (String)null);
         fileWriter.close();
      } catch (Exception exception) {
         SMCLog.severe("Error saving configuration: " + exception.getClass().getName() + ": " + exception.getMessage());
      }
   }

   public static void drawHorizon() {
      WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
      float f = mc.gameSettings.renderDistanceChunks * 16;
      double d2 = f * 0.9238;
      double d3 = f * 0.3826;
      double d4 = -d3;
      double d5 = -d2;
      double d6 = 16.0;
      double d7 = -cameraPositionY;
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(d4, d7, d5).endVertex();
      worldRenderer.pos(d4, d6, d5).endVertex();
      worldRenderer.pos(d5, d6, d4).endVertex();
      worldRenderer.pos(d5, d7, d4).endVertex();
      worldRenderer.pos(d5, d7, d4).endVertex();
      worldRenderer.pos(d5, d6, d4).endVertex();
      worldRenderer.pos(d5, d6, d3).endVertex();
      worldRenderer.pos(d5, d7, d3).endVertex();
      worldRenderer.pos(d5, d7, d3).endVertex();
      worldRenderer.pos(d5, d6, d3).endVertex();
      worldRenderer.pos(d4, d6, d2).endVertex();
      worldRenderer.pos(d4, d7, d2).endVertex();
      worldRenderer.pos(d4, d7, d2).endVertex();
      worldRenderer.pos(d4, d6, d2).endVertex();
      worldRenderer.pos(d3, d6, d2).endVertex();
      worldRenderer.pos(d3, d7, d2).endVertex();
      worldRenderer.pos(d3, d7, d2).endVertex();
      worldRenderer.pos(d3, d6, d2).endVertex();
      worldRenderer.pos(d2, d6, d3).endVertex();
      worldRenderer.pos(d2, d7, d3).endVertex();
      worldRenderer.pos(d2, d7, d3).endVertex();
      worldRenderer.pos(d2, d6, d3).endVertex();
      worldRenderer.pos(d2, d6, d4).endVertex();
      worldRenderer.pos(d2, d7, d4).endVertex();
      worldRenderer.pos(d2, d7, d4).endVertex();
      worldRenderer.pos(d2, d6, d4).endVertex();
      worldRenderer.pos(d3, d6, d5).endVertex();
      worldRenderer.pos(d3, d7, d5).endVertex();
      worldRenderer.pos(d3, d7, d5).endVertex();
      worldRenderer.pos(d3, d6, d5).endVertex();
      worldRenderer.pos(d4, d6, d5).endVertex();
      worldRenderer.pos(d4, d7, d5).endVertex();
      worldRenderer.pos(d5, d7, d5).endVertex();
      worldRenderer.pos(d5, d7, d2).endVertex();
      worldRenderer.pos(d2, d7, d2).endVertex();
      worldRenderer.pos(d2, d7, d5).endVertex();
      Tessellator.getInstance().draw();
   }

   public static void enableFog() {
      fogEnabled = true;
      setProgramUniform1i(uniform_fogMode, fogMode);
      setProgramUniform1f(uniform_fogDensity, fogDensity);
   }

   public static void setRenderingFirstPersonHand(boolean conditionMet) {
      isRenderingFirstPersonHand = conditionMet;
   }

   public static void disableLightmap() {
      lightmapEnabled = false;
      if (activeProgram == ProgramTexturedLit) {
         useProgram(ProgramTextured);
      }
   }

   public static boolean isOldLighting() {
      return !configOldLighting.isDefault() ? configOldLighting.isTrue() : (!shaderPackOldLighting.isDefault() ? shaderPackOldLighting.isTrue() : true);
   }

   public static boolean isBothHandsRendered() {
      return isHandRenderedMain && isHandRenderedOff;
   }

   private static FloatBuffer getFloatBuffer() {
      ByteBuffer bigBuffer2 = bigBuffer;
      int i = bigBuffer2.limit();
      bigBuffer2.position(i).limit(i + 64);
      return bigBuffer2.asFloatBuffer();
   }

   public static void beginWeather() {
      if (!isShadowPass) {
         if (usedDepthBuffers >= 3) {
            GlStateManager.setActiveTexture(33996);
            GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, renderWidth, renderHeight);
            GlStateManager.setActiveTexture(33984);
         }

         GlStateManager.enableDepth();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.enableAlpha();
         useProgram(ProgramWeather);
      }
   }

   public static void setHandsRendered(boolean handMain, boolean handOff) {
      isHandRenderedMain = handMain;
      isHandRenderedOff = handOff;
   }

   private static boolean printLogInfo(int obj, String name) {
      IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
      ARBShaderObjects.glGetObjectParameterARB(obj, 35716, intBuffer);
      int i = intBuffer.get();
      if (i > 1) {
         ByteBuffer byteBuffer2 = BufferUtils.createByteBuffer(i);
         intBuffer.flip();
         ARBShaderObjects.glGetInfoLogARB(obj, intBuffer, byteBuffer2);
         byte[] bArray = new byte[i];
         byteBuffer2.get(bArray);
         if (bArray[i - 1] == 0) {
            bArray[i - 1] = 10;
         }

         String text3 = new String(bArray, Charsets.US_ASCII);
         text3 = StrUtils.trim(text3, " \n\r\t");
         SMCLog.info("Info log: " + name + "\n" + text3);
         return false;
      } else {
         return true;
      }
   }

   public static int getEntityData() {
      return entityData[entityDataIndex * 2];
   }

   private static int getDimensionId(World world) {
      return world == null ? Integer.MIN_VALUE : world.provider.getDimensionId();
   }

   private static String applyOptions(String line, ShaderOption[] ops) {
      if (ops != null && ops.length > 0) {
         for (int index = 0; index < ops.length; index++) {
            ShaderOption shaderOption = ops[index];
            if (shaderOption.matchesLine(line)) {
               line = shaderOption.getSourceLine();
               break;
            }
         }

         return line;
      } else {
         return line;
      }
   }

   public static void popEntity() {
      entityData[entityDataIndex * 2] = 0;
      entityData[entityDataIndex * 2 + 1] = 0;
      entityDataIndex--;
   }

   public static IntBuffer nextIntBuffer(int size) {
      ByteBuffer byteBuffer = bigBuffer;
      int i = byteBuffer.limit();
      byteBuffer.position(i).limit(i + size * 4);
      return byteBuffer.asIntBuffer();
   }

   public static boolean isSkipRenderHand() {
      return skipRenderHandMain;
   }

   public static void setCamera(float partialTicks) {
      Entity entity = mc.getRenderViewEntity();
      double xCoordinate = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
      double yCoordinate = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
      double zCoordinate = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
      updateCameraOffset(entity);
      cameraPositionX = xCoordinate - cameraOffsetX;
      cameraPositionY = yCoordinate;
      cameraPositionZ = zCoordinate - cameraOffsetZ;
      GL11.glGetFloat(2983, projection.position(0));
      SMath.invertMat4FBFA(projectionInverse.position(0), projection.position(0), faProjectionInverse, faProjection);
      projection.position(0);
      projectionInverse.position(0);
      GL11.glGetFloat(2982, modelView.position(0));
      SMath.invertMat4FBFA(modelViewInverse.position(0), modelView.position(0), faModelViewInverse, faModelView);
      modelView.position(0);
      modelViewInverse.position(0);
      checkGLError("setCamera");
   }

   private static void setProgramUniform1i(ShaderUniform1i su, int value) {
      su.setValue(value);
   }

   public static boolean isSun() {
      return !shaderPackSun.isFalse();
   }

   public static InputStream getShaderPackResourceStream(String path) {
      return shaderPack == null ? null : shaderPack.getResourceAsStream(path);
   }

   private static void setProgramUniform1f(ShaderUniform1f su, float value) {
      su.setValue(value);
   }

   public static void beginHand(boolean translucent) {
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glMatrixMode(5888);
      if (translucent) {
         useProgram(ProgramHandWater);
      } else {
         useProgram(ProgramHand);
      }

      checkGLError("beginHand");
      checkFramebufferStatus("beginHand");
   }

   private static boolean checkBufferFlip(Program program) {
      boolean conditionMet = false;
      Boolean[] booleanArray = program.getBuffersFlip();

      for (int index = 0; index < usedColorBuffers; index++) {
         if (Config.isTrue(booleanArray[index])) {
            dfbColorTexturesFlip.flip(index);
            conditionMet = true;
         }
      }

      return conditionMet;
   }

   public static void updateBlockLightLevel() {
      if (isOldLighting()) {
         blockLightLevel05 = 0.5F;
         blockLightLevel06 = 0.6F;
         blockLightLevel08 = 0.8F;
      } else {
         blockLightLevel05 = 1.0F;
         blockLightLevel06 = 1.0F;
         blockLightLevel08 = 1.0F;
      }
   }

   public static void endLeash() {
      popProgram();
   }

   public static boolean isRainDepth() {
      return shaderPackRainDepth.isTrue();
   }

   public static void endWeather() {
      GlStateManager.disableBlend();
      useProgram(ProgramTexturedLit);
   }

   private static ICustomTexture loadCustomTextureRaw(int textureUnit, String line) {
      ConnectedParser connectedParser = new ConnectedParser("Shaders");
      String[] stringArray = Config.tokenize(line, " ");
      ArrayDeque arrayDeque = new ArrayDeque<>(Arrays.asList(stringArray));
      String text = (String)arrayDeque.poll();
      TextureType textureType = (TextureType)connectedParser.parseEnum((String)arrayDeque.poll(), TextureType.values(), "texture type");
      if (textureType == null) {
         SMCLog.warning("Invalid raw texture type: " + line);
         return null;
      }

      InternalFormat internalFormat = (InternalFormat)connectedParser.parseEnum((String)arrayDeque.poll(), InternalFormat.values(), "internal format");
      if (internalFormat == null) {
         SMCLog.warning("Invalid raw texture internal format: " + line);
         return null;
      }

      int i = 0;
      int i2 = 0;
      int i3;
      switch (textureType) {
         case TEXTURE_1D:
            i3 = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            break;
         case TEXTURE_2D:
            i3 = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            i = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            break;
         case TEXTURE_3D:
            i3 = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            i = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            i2 = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            break;
         case TEXTURE_RECTANGLE:
            i3 = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            i = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            break;
         default:
            SMCLog.warning("Invalid raw texture type: " + textureType);
            return null;
      }

      if (i3 >= 0 && i >= 0 && i2 >= 0) {
         PixelFormat pixelFormat = (PixelFormat)connectedParser.parseEnum((String)arrayDeque.poll(), PixelFormat.values(), "pixel format");
         if (pixelFormat == null) {
            SMCLog.warning("Invalid raw texture pixel format: " + line);
            return null;
         } else {
            PixelType pixelType = (PixelType)connectedParser.parseEnum((String)arrayDeque.poll(), PixelType.values(), "pixel type");
            if (pixelType == null) {
               SMCLog.warning("Invalid raw texture pixel type: " + line);
               return null;
            } else if (!arrayDeque.isEmpty()) {
               SMCLog.warning("Invalid raw texture, too many parameters: " + line);
               return null;
            } else {
               return loadCustomTextureRaw(
                  textureUnit, line, text, textureType, internalFormat, i3, i, i2, pixelFormat, pixelType
               );
            }
         }
      } else {
         SMCLog.warning("Invalid raw texture size: " + line);
         return null;
      }
   }

   public static IShaderPack getShaderPack() {
      return shaderPack;
   }

   public static void saveShader(String filename, String code) {
      try {
         File file = new File(shaderPacksDir, "debug/" + filename);
         file.getParentFile().mkdirs();
         Config.writeFile(file, code);
      } catch (IOException ioException) {
         Config.warn("Error saving: " + filename);
         ioException.printStackTrace();
      }
   }

   public static void setUpPosition() {
      FloatBuffer floatBuffer = tempMatrixDirectBuffer;
      floatBuffer.clear();
      GL11.glGetFloat(2982, floatBuffer);
      floatBuffer.get(tempMat, 0, 16);
      SMath.multiplyMat4xVec4(upPosition, tempMat, upPosModelView);
      setProgramUniform3f(uniform_upPosition, upPosition[0], upPosition[1], upPosition[2]);
      if (customUniforms != null) {
         customUniforms.update();
      }
   }

   public static boolean isRenderBackFace(EnumWorldBlockLayer blockLayerIn) {
      switch (blockLayerIn) {
         case SOLID:
            return shaderPackBackFaceSolid.isTrue();
         case CUTOUT:
            return shaderPackBackFaceCutout.isTrue();
         case CUTOUT_MIPPED:
            return shaderPackBackFaceCutoutMipped.isTrue();
         case TRANSLUCENT:
            return shaderPackBackFaceTranslucent.isTrue();
         default:
            return false;
      }
   }

   public static void endFPOverlay() {
   }

   public static float getShadowRenderDistance() {
      return shadowDistanceRenderMul < 0.0F ? -1.0F : shadowMapHalfPlane * shadowDistanceRenderMul;
   }

   private static void loadShaderPackResources() {
      shaderPackResources = new HashMap<>();
      if (shaderPackLoaded) {
         ArrayList arrayList = new ArrayList();
         arrayList.add("/shaders/lang/en_US.lang");
         if (!Config.getGameSettings().language.equals("en_US")) {
            arrayList.add("/shaders/lang/" + Config.getGameSettings().language + ".lang");
         }

         try {
            for (String text2 : (Iterable<String>) arrayList) {
               InputStream resourceAsStream = shaderPack.getResourceAsStream(text2);
               if (resourceAsStream != null) {
                  PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
                  Lang.loadLocaleData(resourceAsStream, propertiesOrdered);
                  resourceAsStream.close();

                  for (String text3 : (Iterable<String>) (Iterable<String>) (Set) (propertiesOrdered.keySet())) {
                     String property = propertiesOrdered.getProperty(text3);
                     shaderPackResources.put(text3, property);
                  }
               }
            }
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      }
   }

   public static void setEntityColor(float f, float g, float b, float a) {
      if (isRenderingWorld && !isShadowPass) {
         uniform_entityColor.setValue(f, g, b, a);
      }
   }

   public static int getEntityData2() {
      return entityData[entityDataIndex * 2 + 1];
   }

   public static void loadShaderPack() {
      boolean conditionMet = shaderPackLoaded;
      boolean isOldLighting = isOldLighting();
      if (mc.renderGlobal != null) {
         mc.renderGlobal.pauseChunkUpdates();
      }

      shaderPackLoaded = false;
      if (shaderPack != null) {
         shaderPack.close();
         shaderPack = null;
         shaderPackResources.clear();
         shaderPackDimensions.clear();
         shaderPackOptions = null;
         shaderPackOptionSliders = null;
         shaderPackProfiles = null;
         shaderPackGuiScreens = null;
         shaderPackProgramConditions.clear();
         shaderPackClouds.resetValue();
         shaderPackOldHandLight.resetValue();
         shaderPackDynamicHandLight.resetValue();
         shaderPackOldLighting.resetValue();
         resetCustomTextures();
         noiseTexturePath = null;
      }

      boolean conditionMet2 = false;
      if (Config.isAntialiasing()) {
         SMCLog.info("Shaders can not be loaded, Antialiasing is enabled: " + Config.getAntialiasingLevel() + "x");
         conditionMet2 = true;
      }

      if (Config.isAnisotropicFiltering()) {
         SMCLog.info("Shaders can not be loaded, Anisotropic Filtering is enabled: " + Config.getAnisotropicFilterLevel() + "x");
         conditionMet2 = true;
      }

      String propertyValue = shadersConfig.getProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "(internal)");
      if (!conditionMet2) {
         shaderPack = getShaderPack(propertyValue);
         shaderPackLoaded = shaderPack != null;
      }

      if (shaderPackLoaded) {
         SMCLog.info("Loaded shaderpack: " + getShaderPackName());
      } else {
         SMCLog.info("No shaderpack loaded.");
         shaderPack = new ShaderPackNone();
      }

      if (saveFinalShaders) {
         clearDirectory(new File(shaderPacksDir, "debug"));
      }

      loadShaderPackResources();
      loadShaderPackDimensions();
      shaderPackOptions = loadShaderPackOptions();
      loadShaderPackProperties();
      boolean flag3 = shaderPackLoaded != conditionMet;
      boolean flag4 = isOldLighting() != isOldLighting;
      if (flag3 || flag4) {
         DefaultVertexFormats.updateVertexFormats();
         if (Reflector.reflectorClass37.isActive()) {
            Reflector.reflectorField15.handleObject(null);
            Reflector.reflectorField16.handleObject(null);
         }

         updateBlockLightLevel();
      }

      if (mc.getResourcePackRepository() != null) {
         CustomBlockLayers.update();
      }

      if (mc.renderGlobal != null) {
         mc.renderGlobal.resumeChunkUpdates();
      }

      if ((flag3 || flag4) && mc.getResourceManager() != null) {
         mc.scheduleResourcesRefresh();
      }
   }

   public static void endClouds() {
      disableFog();
      popEntity();
      useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
   }

   public static void beginClouds() {
      fogEnabled = true;
      pushEntity(-3);
      useProgram(ProgramClouds);
   }

   public static void setFogMode(int value) {
      fogMode = value;
      if (fogEnabled) {
         setProgramUniform1i(uniform_fogMode, value);
      }
   }

   public static void setSkipRenderHands(boolean skipMain, boolean skipOff) {
      skipRenderHandMain = skipMain;
      skipRenderHandOff = skipOff;
   }

   public static void run2() {
      if (isRenderingWorld) {
         useProgram(ProgramBlock);
      }
   }

   public static String translate(String key, String def) {
      String text = shaderPackResources.get(key);
      return text == null ? def : text;
   }

   private static void setProgramUniform3f(ShaderUniform3f su, float f, float f2, float f3) {
      su.setValue(f, f2, f3);
   }

   public static void pushProgram() {
      programStack.push(activeProgram);
   }

   public static void beginLitParticles() {
      useProgram(ProgramTexturedLit);
   }

   public static void handleMinecraft(Minecraft minecraft2, float f) {
      checkGLError("pre beginRender");
      checkWorldChanged(mc.theWorld);
      mc = minecraft2;
      mc.mcProfiler.startSection("init");
      entityRenderer = mc.entityRenderer;
      if (!isShaderPackInitialized) {
         try {
            init();
         } catch (IllegalStateException illegalStateException) {
            if (Config.normalize(illegalStateException.getMessage()).equals("Function is not supported")) {
               printChatAndLogError("[Shaders] Error: " + illegalStateException.getMessage());
               illegalStateException.printStackTrace();
               setShaderPack("OFF");
               return;
            }
         }
      }

      if (mc.displayWidth != renderDisplayWidth || mc.displayHeight != renderDisplayHeight) {
         resize();
      }

      if (needResizeShadow) {
         resizeShadow();
      }

      worldTime = mc.theWorld.getWorldTime();
      diffWorldTime = (worldTime - lastWorldTime) % 24000L;
      if (diffWorldTime < 0L) {
         diffWorldTime += 24000L;
      }

      lastWorldTime = worldTime;
      moonPhase = mc.theWorld.getMoonPhase();
      frameCounter++;
      if (frameCounter >= 720720) {
         frameCounter = 0;
      }

      systemTime = System.currentTimeMillis();
      if (lastSystemTime == 0L) {
         lastSystemTime = systemTime;
      }

      diffSystemTime = systemTime - lastSystemTime;
      lastSystemTime = systemTime;
      frameTime = (float)diffSystemTime / 1000.0F;
      frameTimeCounter = frameTimeCounter + frameTime;
      frameTimeCounter %= 3600.0F;
      rainStrength = minecraft2.theWorld.getRainStrength(f);
      float f2 = (float)diffSystemTime * 0.01F;
      float f3 = (float)Math.exp(Math.log(0.5) * f2 / (wetness < rainStrength ? drynessHalfLife : wetnessHalfLife));
      wetness = wetness * f3 + rainStrength * (1.0F - f3);
      Entity renderViewEntity = mc.getRenderViewEntity();
      if (renderViewEntity != null) {
         isSleeping = renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPlayerSleeping();
         eyePosY = (float)renderViewEntity.posY * f + (float)renderViewEntity.lastTickPosY * (1.0F - f);
         eyeBrightness = renderViewEntity.getBrightnessForRender(f);
         f3 = (float)diffSystemTime * 0.01F;
         float f4 = (float)Math.exp(Math.log(0.5) * f3 / eyeBrightnessHalflife);
         eyeBrightnessFadeX = eyeBrightnessFadeX * f4 + (eyeBrightness & 65535) * (1.0F - f4);
         eyeBrightnessFadeY = eyeBrightnessFadeY * f4 + (eyeBrightness >> 16) * (1.0F - f4);
         Material material2 = ActiveRenderInfo.getBlockAtEntityViewpoint(mc.theWorld, renderViewEntity, f).getMaterial();
         if (material2 == Material.water) {
            isEyeInWater = 1;
         } else if (material2 == Material.lava) {
            isEyeInWater = 2;
         } else {
            isEyeInWater = 0;
         }

         if (mc.thePlayer != null) {
            nightVision = 0.0F;
            if (mc.thePlayer.isPotionActive(Potion.nightVision)) {
               nightVision = Config.getMinecraft().entityRenderer.getNightVisionBrightness(mc.thePlayer, f);
            }

            blindness = 0.0F;
            if (mc.thePlayer.isPotionActive(Potion.blindness)) {
               int duration = mc.thePlayer.getActivePotionEffect(Potion.blindness).getDuration();
               blindness = Config.limit(duration / 20.0F, 0.0F, 1.0F);
            }
         }

         Vec3 skyColor2 = mc.theWorld.getSkyColor(renderViewEntity, f);
         skyColor2 = CustomColors.getWorldSkyColor(skyColor2, currentWorld, renderViewEntity, f);
         skyColorR = (float)skyColor2.xCoord;
         skyColorG = (float)skyColor2.yCoord;
         skyColorB = (float)skyColor2.zCoord;
      }

      isRenderingWorld = true;
      isCompositeRendered = false;
      isShadowPass = false;
      isHandRenderedMain = false;
      isHandRenderedOff = false;
      skipRenderHandMain = false;
      skipRenderHandOff = false;
      bindGbuffersTextures();
      previousCameraPositionX = cameraPositionX;
      previousCameraPositionY = cameraPositionY;
      previousCameraPositionZ = cameraPositionZ;
      previousProjection.position(0);
      projection.position(0);
      previousProjection.put(projection);
      previousProjection.position(0);
      projection.position(0);
      previousModelView.position(0);
      modelView.position(0);
      previousModelView.put(modelView);
      previousModelView.position(0);
      modelView.position(0);
      checkGLError("beginRender");
      ShadersRender.handleEntityRenderer(entityRenderer, f);
      mc.mcProfiler.endSection();
      EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);

      for (int i = 0; i < usedColorBuffers; i++) {
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesFlip.getA(i), 0);
      }

      checkGLError("end beginRender");
   }

   public static boolean isHandRenderedMain() {
      return isHandRenderedMain;
   }

   public static void disableFog() {
      fogEnabled = false;
      setProgramUniform1i(uniform_fogMode, 0);
   }

   private static void updateCameraOffset(Entity viewEntity) {
      double d2 = Math.abs(cameraPositionX - previousCameraPositionX);
      double d3 = Math.abs(cameraPositionZ - previousCameraPositionZ);
      double d4 = Math.abs(cameraPositionX);
      double d5 = Math.abs(cameraPositionZ);
      if (d2 > 1000.0 || d3 > 1000.0 || d4 > 1000000.0 || d5 > 1000000.0) {
         setCameraOffset(viewEntity);
      }
   }

   public static void setBlockEntityId(TileEntity tileEntity) {
      if (uniform_blockEntityId.isDefined()) {
         int blockEntityId2 = getBlockEntityId(tileEntity);
         uniform_blockEntityId.setValue(blockEntityId2);
      }
   }

   public static boolean isItemToRenderOffTranslucent() {
      return itemToRenderOffTranslucent;
   }

   public static void useProgram(Program program) {
      checkGLError("pre-useProgram");
      if (isShadowPass) {
         program = ProgramShadow;
      } else if (isEntitiesGlowing) {
         program = ProgramEntitiesGlowing;
      }

      if (activeProgram != program) {
         updateAlphaBlend(activeProgram, program);
         activeProgram = program;
         int id = program.getId();
         activeProgramID = id;
         ARBShaderObjects.glUseProgramObjectARB(id);
         if (checkGLError("useProgram") != 0) {
            program.setId(0);
            id = program.getId();
            activeProgramID = id;
            ARBShaderObjects.glUseProgramObjectARB(id);
         }

         shaderUniforms.setProgram(id);
         if (customUniforms != null) {
            customUniforms.setProgram(id);
         }

         if (id != 0) {
            IntBuffer drawBuffers2 = program.getDrawBuffers();
            if (isRenderingDfb) {
               setDrawBuffers(drawBuffers2);
            }

            activeCompositeMipmapSetting = program.getCompositeMipmapSetting();
            switch (program.getProgramStage()) {
               case GBUFFERS:
                  setProgramUniform1i(uniform_texture, 0);
                  setProgramUniform1i(uniform_lightmap, 1);
                  setProgramUniform1i(uniform_normals, 2);
                  setProgramUniform1i(uniform_specular, 3);
                  setProgramUniform1i(uniform_shadow, waterShadowEnabled ? 5 : 4);
                  setProgramUniform1i(uniform_watershadow, 4);
                  setProgramUniform1i(uniform_shadowtex0, 4);
                  setProgramUniform1i(uniform_shadowtex1, 5);
                  setProgramUniform1i(uniform_depthtex0, 6);
                  if (customTexturesGbuffers != null || hasDeferredPrograms) {
                     setProgramUniform1i(uniform_gaux1, 7);
                     setProgramUniform1i(uniform_gaux2, 8);
                     setProgramUniform1i(uniform_gaux3, 9);
                     setProgramUniform1i(uniform_gaux4, 10);
                  }

                  setProgramUniform1i(uniform_depthtex1, 11);
                  setProgramUniform1i(uniform_shadowcolor, 13);
                  setProgramUniform1i(uniform_shadowcolor0, 13);
                  setProgramUniform1i(uniform_shadowcolor1, 14);
                  setProgramUniform1i(uniform_noisetex, 15);
                  break;
               case DEFERRED:
               case COMPOSITE:
                  setProgramUniform1i(uniform_gcolor, 0);
                  setProgramUniform1i(uniform_gdepth, 1);
                  setProgramUniform1i(uniform_gnormal, 2);
                  setProgramUniform1i(uniform_composite, 3);
                  setProgramUniform1i(uniform_gaux1, 7);
                  setProgramUniform1i(uniform_gaux2, 8);
                  setProgramUniform1i(uniform_gaux3, 9);
                  setProgramUniform1i(uniform_gaux4, 10);
                  setProgramUniform1i(uniform_colortex0, 0);
                  setProgramUniform1i(uniform_colortex1, 1);
                  setProgramUniform1i(uniform_colortex2, 2);
                  setProgramUniform1i(uniform_colortex3, 3);
                  setProgramUniform1i(uniform_colortex4, 7);
                  setProgramUniform1i(uniform_colortex5, 8);
                  setProgramUniform1i(uniform_colortex6, 9);
                  setProgramUniform1i(uniform_colortex7, 10);
                  setProgramUniform1i(uniform_shadow, waterShadowEnabled ? 5 : 4);
                  setProgramUniform1i(uniform_watershadow, 4);
                  setProgramUniform1i(uniform_shadowtex0, 4);
                  setProgramUniform1i(uniform_shadowtex1, 5);
                  setProgramUniform1i(uniform_gdepthtex, 6);
                  setProgramUniform1i(uniform_depthtex0, 6);
                  setProgramUniform1i(uniform_depthtex1, 11);
                  setProgramUniform1i(uniform_depthtex2, 12);
                  setProgramUniform1i(uniform_shadowcolor, 13);
                  setProgramUniform1i(uniform_shadowcolor0, 13);
                  setProgramUniform1i(uniform_shadowcolor1, 14);
                  setProgramUniform1i(uniform_noisetex, 15);
                  break;
               case SHADOW:
                  setProgramUniform1i(uniform_tex, 0);
                  setProgramUniform1i(uniform_texture, 0);
                  setProgramUniform1i(uniform_lightmap, 1);
                  setProgramUniform1i(uniform_normals, 2);
                  setProgramUniform1i(uniform_specular, 3);
                  setProgramUniform1i(uniform_shadow, waterShadowEnabled ? 5 : 4);
                  setProgramUniform1i(uniform_watershadow, 4);
                  setProgramUniform1i(uniform_shadowtex0, 4);
                  setProgramUniform1i(uniform_shadowtex1, 5);
                  if (customTexturesGbuffers != null) {
                     setProgramUniform1i(uniform_gaux1, 7);
                     setProgramUniform1i(uniform_gaux2, 8);
                     setProgramUniform1i(uniform_gaux3, 9);
                     setProgramUniform1i(uniform_gaux4, 10);
                  }

                  setProgramUniform1i(uniform_shadowcolor, 13);
                  setProgramUniform1i(uniform_shadowcolor0, 13);
                  setProgramUniform1i(uniform_shadowcolor1, 14);
                  setProgramUniform1i(uniform_noisetex, 15);
            }

            ItemStack itemStack = mc.thePlayer != null ? mc.thePlayer.getHeldItem() : null;
            Item item2 = itemStack != null ? itemStack.getItem() : null;
            int i = -1;
            Block block2 = null;
            if (item2 != null) {
               i = Item.itemRegistry.getIDForObject(item2);
               block2 = Block.blockRegistry.getObjectById(i);
               i = ItemAliases.getItemAliasId(i);
            }

            int i2 = block2 != null ? block2.getLightValue() : 0;
            setProgramUniform1i(uniform_heldItemId, i);
            setProgramUniform1i(uniform_heldBlockLightValue, i2);
            setProgramUniform1i(uniform_fogMode, fogEnabled ? fogMode : 0);
            setProgramUniform1f(uniform_fogDensity, fogEnabled ? fogDensity : 0.0F);
            setProgramUniform3f(uniform_fogColor, fogColorR, fogColorG, fogColorB);
            setProgramUniform3f(uniform_skyColor, skyColorR, skyColorG, skyColorB);
            setProgramUniform1i(uniform_worldTime, (int)(worldTime % 24000L));
            setProgramUniform1i(uniform_worldDay, (int)(worldTime / 24000L));
            setProgramUniform1i(uniform_moonPhase, moonPhase);
            setProgramUniform1i(uniform_frameCounter, frameCounter);
            setProgramUniform1f(uniform_frameTime, frameTime);
            setProgramUniform1f(uniform_frameTimeCounter, frameTimeCounter);
            setProgramUniform1f(uniform_sunAngle, sunAngle);
            setProgramUniform1f(uniform_shadowAngle, shadowAngle);
            setProgramUniform1f(uniform_rainStrength, rainStrength);
            setProgramUniform1f(uniform_aspectRatio, (float)renderWidth / renderHeight);
            setProgramUniform1f(uniform_viewWidth, renderWidth);
            setProgramUniform1f(uniform_viewHeight, renderHeight);
            setProgramUniform1f(uniform_near, 0.05F);
            setProgramUniform1f(uniform_far, mc.gameSettings.renderDistanceChunks * 16);
            setProgramUniform3f(uniform_sunPosition, sunPosition[0], sunPosition[1], sunPosition[2]);
            setProgramUniform3f(uniform_moonPosition, moonPosition[0], moonPosition[1], moonPosition[2]);
            setProgramUniform3f(uniform_shadowLightPosition, shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
            setProgramUniform3f(uniform_upPosition, upPosition[0], upPosition[1], upPosition[2]);
            setProgramUniform3f(uniform_previousCameraPosition, (float)previousCameraPositionX, (float)previousCameraPositionY, (float)previousCameraPositionZ);
            setProgramUniform3f(uniform_cameraPosition, (float)cameraPositionX, (float)cameraPositionY, (float)cameraPositionZ);
            handleShaderUniformM4(uniform_gbufferModelView, modelView);
            handleShaderUniformM4(uniform_gbufferModelViewInverse, modelViewInverse);
            handleShaderUniformM4(uniform_gbufferPreviousProjection, previousProjection);
            handleShaderUniformM4(uniform_gbufferProjection, projection);
            handleShaderUniformM4(uniform_gbufferProjectionInverse, projectionInverse);
            handleShaderUniformM4(uniform_gbufferPreviousModelView, previousModelView);
            if (usedShadowDepthBuffers > 0) {
               handleShaderUniformM4(uniform_shadowProjection, shadowProjection);
               handleShaderUniformM4(uniform_shadowProjectionInverse, shadowProjectionInverse);
               handleShaderUniformM4(uniform_shadowModelView, shadowModelView);
               handleShaderUniformM4(uniform_shadowModelViewInverse, shadowModelViewInverse);
            }

            setProgramUniform1f(uniform_wetness, wetness);
            setProgramUniform1f(uniform_eyeAltitude, eyePosY);
            setProgramUniform2i(uniform_eyeBrightness, eyeBrightness & 65535, eyeBrightness >> 16);
            setProgramUniform2i(uniform_eyeBrightnessSmooth, Math.round(eyeBrightnessFadeX), Math.round(eyeBrightnessFadeY));
            setProgramUniform2i(uniform_terrainTextureSize, terrainTextureSize[0], terrainTextureSize[1]);
            setProgramUniform1i(uniform_terrainIconSize, terrainIconSize);
            setProgramUniform1i(uniform_isEyeInWater, isEyeInWater);
            setProgramUniform1f(uniform_nightVision, nightVision);
            setProgramUniform1f(uniform_blindness, blindness);
            setProgramUniform1f(uniform_screenBrightness, mc.gameSettings.gammaSetting);
            setProgramUniform1i(uniform_hideGUI, mc.gameSettings.hideGUI ? 1 : 0);
            setProgramUniform1f(uniform_centerDepthSmooth, centerDepthSmooth);
            setProgramUniform2i(uniform_atlasSize, atlasSizeX, atlasSizeY);
            if (customUniforms != null) {
               customUniforms.update();
            }

            checkGLError("end useProgram");
         }
      }
   }

   public static void disableTexture2D() {
      if (isRenderingSky) {
         useProgram(ProgramSkyBasic);
      } else if (activeProgram == ProgramTextured || activeProgram == ProgramTexturedLit) {
         useProgram(ProgramBasic);
      }
   }

   private static ShaderOption[] loadShaderPackOptions() {
      try {
         String[] stringArray = programs.getProgramNames();
         ShaderOption[] ashaderoption = ShaderPackParser.parseShaderPackOptions(shaderPack, stringArray, shaderPackDimensions);
         Properties properties = loadOptionProperties(shaderPack);

         for (int index = 0; index < ashaderoption.length; index++) {
            ShaderOption shaderOption = ashaderoption[index];
            String name = properties.getProperty(shaderOption.getName());
            if (name != null) {
               shaderOption.resetValue();
               if (!shaderOption.setValue(name)) {
                  Config.warn("[Shaders] Invalid value, option: " + shaderOption.getName() + ", value: " + name);
               }
            }
         }

         return ashaderoption;
      } catch (IOException ioException) {
         Config.warn("[Shaders] Error reading configuration for " + shaderPack.getName());
         ioException.printStackTrace();
         return null;
      }
   }

   public static void endParticles() {
      useProgram(ProgramTexturedLit);
   }

   private static void postDrawComposite() {
      if (activeProgram.getRenderScale() != null) {
         GL11.glViewport(0, 0, renderWidth, renderHeight);
      }
   }

   private static void loadShaderPackProperties() {
      shaderPackClouds.resetValue();
      shaderPackOldHandLight.resetValue();
      shaderPackDynamicHandLight.resetValue();
      shaderPackOldLighting.resetValue();
      shaderPackShadowTranslucent.resetValue();
      shaderPackUnderwaterOverlay.resetValue();
      shaderPackSun.resetValue();
      shaderPackMoon.resetValue();
      shaderPackVignette.resetValue();
      shaderPackBackFaceSolid.resetValue();
      shaderPackBackFaceCutout.resetValue();
      shaderPackBackFaceCutoutMipped.resetValue();
      shaderPackBackFaceTranslucent.resetValue();
      shaderPackRainDepth.resetValue();
      shaderPackBeaconBeamDepth.resetValue();
      shaderPackSeparateAo.resetValue();
      shaderPackFrustumCulling.resetValue();
      BlockAliases.reset();
      ItemAliases.reset();
      EntityAliases.reset();
      customUniforms = null;

      for (int i = 0; i < ProgramsAll.length; i++) {
         Program program2 = ProgramsAll[i];
         program2.resetProperties();
      }

      if (shaderPack != null) {
         BlockAliases.update(shaderPack);
         ItemAliases.update(shaderPack);
         EntityAliases.update(shaderPack);

         try {
            InputStream resourceAsStream = shaderPack.getResourceAsStream("/shaders/shaders.properties");
            if (resourceAsStream == null) {
               return;
            }

            resourceAsStream = MacroProcessor.process(resourceAsStream, "/shaders/shaders.properties");
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(resourceAsStream);
            resourceAsStream.close();
            shaderPackClouds.loadFrom(propertiesOrdered);
            shaderPackOldHandLight.loadFrom(propertiesOrdered);
            shaderPackDynamicHandLight.loadFrom(propertiesOrdered);
            shaderPackOldLighting.loadFrom(propertiesOrdered);
            shaderPackShadowTranslucent.loadFrom(propertiesOrdered);
            shaderPackUnderwaterOverlay.loadFrom(propertiesOrdered);
            shaderPackSun.loadFrom(propertiesOrdered);
            shaderPackVignette.loadFrom(propertiesOrdered);
            shaderPackMoon.loadFrom(propertiesOrdered);
            shaderPackBackFaceSolid.loadFrom(propertiesOrdered);
            shaderPackBackFaceCutout.loadFrom(propertiesOrdered);
            shaderPackBackFaceCutoutMipped.loadFrom(propertiesOrdered);
            shaderPackBackFaceTranslucent.loadFrom(propertiesOrdered);
            shaderPackRainDepth.loadFrom(propertiesOrdered);
            shaderPackBeaconBeamDepth.loadFrom(propertiesOrdered);
            shaderPackSeparateAo.loadFrom(propertiesOrdered);
            shaderPackFrustumCulling.loadFrom(propertiesOrdered);
            shaderPackOptionSliders = ShaderPackParser.parseOptionSliders(propertiesOrdered, shaderPackOptions);
            shaderPackProfiles = ShaderPackParser.parseProfiles(propertiesOrdered, shaderPackOptions);
            shaderPackGuiScreens = ShaderPackParser.parseGuiScreens(propertiesOrdered, shaderPackProfiles, shaderPackOptions);
            shaderPackProgramConditions = ShaderPackParser.parseProgramConditions(propertiesOrdered, shaderPackOptions);
            customTexturesGbuffers = loadCustomTextures(propertiesOrdered, 0);
            customTexturesComposite = loadCustomTextures(propertiesOrdered, 1);
            customTexturesDeferred = loadCustomTextures(propertiesOrdered, 2);
            noiseTexturePath = propertiesOrdered.getProperty("texture.noise");
            if (noiseTexturePath != null) {
               noiseTextureEnabled = true;
            }

            customUniforms = ShaderPackParser.parseCustomUniforms(propertiesOrdered);
            ShaderPackParser.parseAlphaStates(propertiesOrdered);
            ShaderPackParser.parseBlendStates(propertiesOrdered);
            ShaderPackParser.parseRenderScales(propertiesOrdered);
            ShaderPackParser.parseBuffersFlip(propertiesOrdered);
         } catch (IOException ioException) {
            Config.warn("[Shaders] Error reading: " + "/shaders/shaders.properties");
         }
      }
   }

   private static int getBlockEntityId(TileEntity tileEntity) {
      if (tileEntity == null) {
         return -1;
      }

      Block block = tileEntity.getBlockType();
      if (block == null) {
         return 0;
      }

      int idFromBlock = Block.getIdFromBlock(block);
      int blockMetadata = tileEntity.getBlockMetadata();
      int blockAliasId = BlockAliases.getBlockAliasId(idFromBlock, blockMetadata);
      if (blockAliasId >= 0) {
         idFromBlock = blockAliasId;
      }

      return idFromBlock;
   }

   public static int createInt(String text3) {
      String text4 = text3 != null ? "screen." + text3 : "screen";
      if (shaderPackGuiScreens == null) {
         return 2;
      }

      ScreenShaderOptions screenShaderOptions = shaderPackGuiScreens.get(text4);
      return screenShaderOptions == null ? 2 : screenShaderOptions.getColumns();
   }

   public static void loadConfig() {
      SMCLog.info("Load shaders configuration.");

      try {
         if (!shaderPacksDir.exists()) {
            shaderPacksDir.mkdir();
         }
      } catch (Exception exception) {
         SMCLog.severe("Failed to open the shaderpacks directory: " + shaderPacksDir);
      }

      shadersConfig = new PropertiesOrdered();
      shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "");
      if (configFile.exists()) {
         try {
            FileReader fileReader = new FileReader(configFile);
            shadersConfig.load(fileReader);
            fileReader.close();
         } catch (Exception exception2) {
         }
      }

      if (!configFile.exists()) {
         try {
            storeConfig();
         } catch (Exception exception3) {
         }
      }

      EnumShaderOption[] enumShaderOptions = EnumShaderOption.values();

      for (int i = 0; i < enumShaderOptions.length; i++) {
         EnumShaderOption enumShaderOption = enumShaderOptions[i];
         String propertyKey = enumShaderOption.getPropertyKey();
         String valueDefault = enumShaderOption.getValueDefault();
         String property = shadersConfig.getProperty(propertyKey, valueDefault);
         setEnumShaderOption(enumShaderOption, property);
      }

      loadShaderPack();
   }

   public static boolean isRenderBothHands() {
      return !skipRenderHandMain && !skipRenderHandOff;
   }

   private static IntBuffer fillIntBufferZero(IntBuffer buf) {
      int i = buf.limit();

      for (int innerIndex = buf.position(); innerIndex < i; innerIndex++) {
         buf.put(innerIndex, 0);
      }

      return buf;
   }

   public static World getCurrentWorld() {
      return currentWorld;
   }

   public static boolean isOldHandLight() {
      return !configOldHandLight.isDefault() ? configOldHandLight.isTrue() : (!shaderPackOldHandLight.isDefault() ? shaderPackOldHandLight.isTrue() : true);
   }

   public static void setShaderPack(String par1name) {
      currentShaderName = par1name;
      shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), par1name);
      loadShaderPack();
   }

   private static void setupProgram(Program program, String vShaderPath, String gShaderPath, String fShaderPath) {
      checkGLError("pre setupProgram");
      int i = ARBShaderObjects.glCreateProgramObjectARB();
      checkGLError("create");
      if (i != 0) {
         progUseEntityAttrib = false;
         progUseMidTexCoordAttrib = false;
         progUseTangentAttrib = false;
         int vertShader = createVertShader(program, vShaderPath);
         int geomShader = createGeomShader(program, gShaderPath);
         int fragShader = createFragShader(program, fShaderPath);
         checkGLError("create");
         if (vertShader == 0 && geomShader == 0 && fragShader == 0) {
            ARBShaderObjects.glDeleteObjectARB(i);
            program.resetId();
         } else {
            if (vertShader != 0) {
               ARBShaderObjects.glAttachObjectARB(i, vertShader);
               checkGLError("attach");
            }

            if (geomShader != 0) {
               ARBShaderObjects.glAttachObjectARB(i, geomShader);
               checkGLError("attach");
               if (progArbGeometryShader4) {
                  ARBGeometryShader4.glProgramParameteriARB(i, 36315, 4);
                  ARBGeometryShader4.glProgramParameteriARB(i, 36316, 5);
                  ARBGeometryShader4.glProgramParameteriARB(i, 36314, progMaxVerticesOut);
                  checkGLError("arbGeometryShader4");
               }

               hasGeometryShaders = true;
            }

            if (fragShader != 0) {
               ARBShaderObjects.glAttachObjectARB(i, fragShader);
               checkGLError("attach");
            }

            if (progUseEntityAttrib) {
               ARBVertexShader.glBindAttribLocationARB(i, entityAttrib, "mc_Entity");
               checkGLError("mc_Entity");
            }

            if (progUseMidTexCoordAttrib) {
               ARBVertexShader.glBindAttribLocationARB(i, midTexCoordAttrib, "mc_midTexCoord");
               checkGLError("mc_midTexCoord");
            }

            if (progUseTangentAttrib) {
               ARBVertexShader.glBindAttribLocationARB(i, tangentAttrib, "at_tangent");
               checkGLError("at_tangent");
            }

            ARBShaderObjects.glLinkProgramARB(i);
            if (GL20.glGetProgrami(i, 35714) != 1) {
               SMCLog.severe("Error linking program: " + i + " (" + program.getName() + ")");
            }

            printLogInfo(i, program.getName());
            if (vertShader != 0) {
               ARBShaderObjects.glDetachObjectARB(i, vertShader);
               ARBShaderObjects.glDeleteObjectARB(vertShader);
            }

            if (geomShader != 0) {
               ARBShaderObjects.glDetachObjectARB(i, geomShader);
               ARBShaderObjects.glDeleteObjectARB(geomShader);
            }

            if (fragShader != 0) {
               ARBShaderObjects.glDetachObjectARB(i, fragShader);
               ARBShaderObjects.glDeleteObjectARB(fragShader);
            }

            program.setId(i);
            program.setRef(i);
            useProgram(program);
            ARBShaderObjects.glValidateProgramARB(i);
            useProgram(ProgramNone);
            printLogInfo(i, program.getName());
            if (GL20.glGetProgrami(i, 35715) != 1) {
               printChatAndLogError("[Shaders] Error: Invalid program \"" + program.getName() + "\"");
               ARBShaderObjects.glDeleteObjectARB(i);
               program.resetId();
            }
         }
      }
   }

   private static void drawCompositeQuad() {
      if (!canRenderQuads()) {
         GL11.glBegin(5);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex3f(0.0F, 0.0F, 0.0F);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glVertex3f(1.0F, 0.0F, 0.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex3f(0.0F, 1.0F, 0.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex3f(1.0F, 1.0F, 0.0F);
         GL11.glEnd();
      } else {
         GL11.glBegin(7);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex3f(0.0F, 0.0F, 0.0F);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glVertex3f(1.0F, 0.0F, 0.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex3f(1.0F, 1.0F, 0.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex3f(0.0F, 1.0F, 0.0F);
         GL11.glEnd();
      }
   }

   public static void saveShaderPackOptions() {
      saveShaderPackOptions(shaderPackOptions, shaderPack);
   }

   public static void beginParticles() {
      useProgram(ProgramTextured);
   }

   public static int checkGLError(String location) {
      int i = GlStateManager.glGetError();
      if (i != 0 && GlErrorThrottle.shouldReportError()) {
         String glErrorString = Config.getGlErrorString(i);
         String errorInfo = getErrorInfo(i, location);
         SMCLog.severe(String.format("OpenGL error: %s (%s)%s, at: %s", i, glErrorString, errorInfo, location));
         if (Config.isShowGlErrors() && ReportThrottle.shouldReport("ShowGlErrorShaders")) {
            printChat(I18n.format("of.message.openglError", i, glErrorString));
         }
      }

      return i;
   }

   public static boolean canRenderQuads() {
      return hasGeometryShaders ? capabilities.GL_NV_geometry_shader4 : true;
   }

   public static void endHand() {
      checkGLError("pre endHand");
      checkFramebufferStatus("pre endHand");
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
      GlStateManager.blendFunc(770, 771);
      checkGLError("endHand");
   }

   private static void bindGbuffersTextures() {
      if (usedShadowDepthBuffers >= 1) {
         GlStateManager.setActiveTexture(33988);
         GlStateManager.bindTexture(sfbDepthTextures.get(0));
         if (usedShadowDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33989);
            GlStateManager.bindTexture(sfbDepthTextures.get(1));
         }
      }

      GlStateManager.setActiveTexture(33984);

      for (int index = 0; index < usedColorBuffers; index++) {
         GlStateManager.bindTexture(dfbColorTexturesFlip.getA(index));
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10241, 9729);
         GlStateManager.bindTexture(dfbColorTexturesFlip.getB(index));
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10241, 9729);
      }

      GlStateManager.bindTexture(0);

      for (int innerIndex = 0; innerIndex < 4 && 4 + innerIndex < usedColorBuffers; innerIndex++) {
         GlStateManager.setActiveTexture(33991 + innerIndex);
         GlStateManager.bindTexture(dfbColorTexturesFlip.getA(4 + innerIndex));
      }

      GlStateManager.setActiveTexture(33990);
      GlStateManager.bindTexture(dfbDepthTextures.get(0));
      if (usedDepthBuffers >= 2) {
         GlStateManager.setActiveTexture(33995);
         GlStateManager.bindTexture(dfbDepthTextures.get(1));
         if (usedDepthBuffers >= 3) {
            GlStateManager.setActiveTexture(33996);
            GlStateManager.bindTexture(dfbDepthTextures.get(2));
         }
      }

      for (int nestedIndex = 0; nestedIndex < usedShadowColorBuffers; nestedIndex++) {
         GlStateManager.setActiveTexture(33997 + nestedIndex);
         GlStateManager.bindTexture(sfbColorTextures.get(nestedIndex));
      }

      if (noiseTextureEnabled) {
         GlStateManager.setActiveTexture(33984 + noiseTexture.getTextureUnit());
         GlStateManager.bindTexture(noiseTexture.getTextureId());
      }

      bindCustomTextures(customTexturesGbuffers);
      GlStateManager.setActiveTexture(33984);
   }

   private static String getFramebufferStatusText(int fbStatusCode) {
      switch (fbStatusCode) {
         case 33305:
            return "Undefined";
         case 36053:
            return "Complete";
         case 36054:
            return "Incomplete attachment";
         case 36055:
            return "Incomplete missing attachment";
         case 36059:
            return "Incomplete draw buffer";
         case 36060:
            return "Incomplete read buffer";
         case 36061:
            return "Unsupported";
         case 36182:
            return "Incomplete multisample";
         case 36264:
            return "Incomplete layer targets";
         default:
            return "Unknown";
      }
   }

   public static void renderCompositeFinal() {
      if (!isShadowPass) {
         checkBufferFlip(ProgramCompositePre);
         checkGLError("pre-render CompositeFinal");
         renderComposites(ProgramsComposite, true);
      }
   }

   public static boolean isMoon() {
      return !shaderPackMoon.isFalse();
   }

   public static boolean isProgramPath(String path) {
      if (path == null) {
         return false;
      }

      if (path.length() <= 0) {
         return false;
      }

      int i = path.lastIndexOf("/");
      if (i >= 0) {
         path = path.substring(i + 1);
      }

      return getProgram(path) != null;
   }

   public static void clearRenderBuffer() {
      if (isShadowPass) {
         checkGLError("shadow clear pre");
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
         GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
         GL20.glDrawBuffers(ProgramShadow.getDrawBuffers());
         checkFramebufferStatus("shadow clear");
         GL11.glClear(16640);
         checkGLError("shadow clear");
      } else {
         checkGLError("clear pre");
         if (gbuffersClear[0]) {
            Vector4f index = gbuffersClearColor[0];
            if (index != null) {
               GL11.glClearColor(index.getX(), index.getY(), index.getZ(), index.getW());
            }

            if (dfbColorTexturesFlip.isChanged(0)) {
               EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, dfbColorTexturesFlip.getB(0), 0);
               GL20.glDrawBuffers(36064);
               GL11.glClear(16384);
               EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, dfbColorTexturesFlip.getA(0), 0);
            }

            GL20.glDrawBuffers(36064);
            GL11.glClear(16384);
         }

         if (gbuffersClear[1]) {
            GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
            Vector4f vector4f = gbuffersClearColor[1];
            if (vector4f != null) {
               GL11.glClearColor(vector4f.getX(), vector4f.getY(), vector4f.getZ(), vector4f.getW());
            }

            if (dfbColorTexturesFlip.isChanged(1)) {
               EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36065, 3553, dfbColorTexturesFlip.getB(1), 0);
               GL20.glDrawBuffers(36065);
               GL11.glClear(16384);
               EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36065, 3553, dfbColorTexturesFlip.getA(1), 0);
            }

            GL20.glDrawBuffers(36065);
            GL11.glClear(16384);
         }

         for (int index = 2; index < usedColorBuffers; index++) {
            if (gbuffersClear[index]) {
               GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               Vector4f vector4f2 = gbuffersClearColor[index];
               if (vector4f2 != null) {
                  GL11.glClearColor(vector4f2.getX(), vector4f2.getY(), vector4f2.getZ(), vector4f2.getW());
               }

               if (dfbColorTexturesFlip.isChanged(index)) {
                  EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + index, 3553, dfbColorTexturesFlip.getB(index), 0);
                  GL20.glDrawBuffers(36064 + index);
                  GL11.glClear(16384);
                  EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + index, 3553, dfbColorTexturesFlip.getA(index), 0);
               }

               GL20.glDrawBuffers(36064 + index);
               GL11.glClear(16384);
            }
         }

         setDrawBuffers(dfbDrawBuffers);
         checkFramebufferStatus("clear");
         checkGLError("clear");
      }
   }

   public static void pushEntity(int data0) {
      entityDataIndex++;
      entityData[entityDataIndex * 2] = data0 & 65535 | 0;
      entityData[entityDataIndex * 2 + 1] = 0;
   }

   public static boolean isUnderwaterOverlay() {
      return !shaderPackUnderwaterOverlay.isFalse();
   }

   private static void resetCustomTextures() {
      deleteCustomTextures(customTexturesGbuffers);
      deleteCustomTextures(customTexturesComposite);
      deleteCustomTextures(customTexturesDeferred);
      customTexturesGbuffers = null;
      customTexturesComposite = null;
      customTexturesDeferred = null;
   }

   private static Properties loadOptionProperties(IShaderPack sp) throws IOException {
      PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
      String text3 = "shaderpacks/" + sp.getName() + ".txt";
      File file = new File(Minecraft.getMinecraft().mcDataDir, text3);
      if (file.exists() && file.isFile() && file.canRead()) {
         FileInputStream fileInputStream = new FileInputStream(file);
         propertiesOrdered.load(fileInputStream);
         fileInputStream.close();
         return propertiesOrdered;
      } else {
         return propertiesOrdered;
      }
   }

   public static void setItemToRenderMain(ItemStack itemToRenderMain) {
      itemToRenderMainTranslucent = isTranslucentBlock(itemToRenderMain);
   }

   public static boolean isHandRenderedOff() {
      return isHandRenderedOff;
   }

   public static void preWater() {
      if (usedDepthBuffers >= 2) {
         GlStateManager.setActiveTexture(33995);
         checkGLError("pre copy depth");
         GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, renderWidth, renderHeight);
         checkGLError("copy depth");
         GlStateManager.setActiveTexture(33984);
      }

      ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
   }

   public static void scheduleResize() {
      renderDisplayHeight = 0;
   }

   public static void checkWorldChanged(World world) {
      if (currentWorld != world) {
         World oldworld = currentWorld;
         currentWorld = world;
         setCameraOffset(mc.getRenderViewEntity());
         int dimensionId = getDimensionId(oldworld);
         int dimensionId2 = getDimensionId(world);
         if (dimensionId2 != dimensionId) {
            boolean conditionMet = shaderPackDimensions.contains(dimensionId);
            boolean conditionMet2 = shaderPackDimensions.contains(dimensionId2);
            if (conditionMet || conditionMet2) {
               uninit();
            }
         }

         Smoother.resetValues();
      }
   }

   public static boolean isShaderPackOptionSlider(String name) {
      return shaderPackOptionSliders == null ? false : shaderPackOptionSliders.contains(name);
   }

   public static boolean shouldRenderClouds(GameSettings gs) {
      if (!shaderPackLoaded) {
         return true;
      }

      checkGLError("shouldRenderClouds");
      return isShadowPass ? configCloudShadow : gs.clouds > 0;
   }

   private static void clearDirectory(File dir) {
      if (dir.exists() && dir.isDirectory()) {
         File[] afile = dir.listFiles();
         if (afile != null) {
            for (int index = 0; index < afile.length; index++) {
               File file = afile[index];
               if (file.isDirectory()) {
                  clearDirectory(file);
               }

               file.delete();
            }
         }
      }
   }
}
