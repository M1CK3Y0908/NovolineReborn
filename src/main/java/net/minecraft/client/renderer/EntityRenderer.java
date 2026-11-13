package net.minecraft.client.renderer;

import com.google.common.base.Predicates;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.src.Config;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.CustomColors;
import net.optifine.Lagometer;
import net.optifine.RandomEntities;
import net.optifine.gui.GuiChatOF;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.reflect.ReflectorResolver;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import net.optifine.util.MemoryMonitor;
import net.optifine.util.TextureUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;
import wtf.novoline.Novoline;
import wtf.novoline.event.RenderWorldEvent;
import wtf.novoline.gui.NovolineMainMenu;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.combat.Reach;
import wtf.novoline.module.visual.CameraClip;
import wtf.novoline.module.visual.FreeLook;
import wtf.novoline.util.GlErrorThrottle;
import wtf.novoline.util.ReportThrottle;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.RenderUtils;

public class EntityRenderer implements IResourceManagerReloadListener {
   private Random random = new Random();
   private MouseFilter mouseFilterXAxis = new MouseFilter();
   private float fovModifierHandPrev;
   private float fovModifierHand;
   private MouseFilter mouseFilterYAxis = new MouseFilter();
   private float thirdPersonDistance = 4.0F;
   private float thirdPersonDistanceTemp = 4.0F;
   private float torchFlickerDX;
   private boolean renderHand = true;
   private static final Logger logger = LogManager.getLogger();
   private float smoothCamPitch;
   public float fogColorGreen;
   private boolean cloudFog;
   private boolean drawBlockOutline = true;
   private long prevFrameTime = Minecraft.getSystemTime();
   private float[] rainXCoords = new float[1024];
   private float[] rainYCoords = new float[1024];
   private FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
   private int debugViewDirection = 0;
   private float floatValue;
   private boolean debugView = false;
   private float bossColorModifierPrev;
   private double doubleValue = 1.0;
   private boolean initialized = false;
   private double cameraPitch;
   private static final ResourceLocation locationRainPng = new ResourceLocation("textures/environment/rain.png");
   private World updatedWorld = null;
   private boolean fogStandard = false;
   public boolean fovModifierForced = false;
   private float clipDistance = 128.0F;
   public float fogColorBlue;
   private float smoothCamYaw;
   private float torchFlickerX;
   private int rainSoundCounter;
   public static boolean anaglyphEnable;
   private long lastServerTime = 0L;
   private float smoothCamPartialTicks;
   private int lastServerTicks = 0;
   private int serverWaitTime = 0;
   private int rendererUpdateCount;
   private float fogColor2;
   private float floatValue2;
   private int serverWaitTimeCurrent = 0;
   private float avgServerTimeDiff = 0.0F;
   private float avgServerTickDiff = 0.0F;
   private ShaderGroup[] fxaaShaders = new ShaderGroup[10];
   private boolean loadVisibleChunks = false;
   private double doubleValue2;
   private float floatValue3 = 0.08F;
   private long renderEndNanoTime;
   private float farPlaneDistance;
   public float fogColorRed;
   private Entity pointedEntity;
   private int shaderIndex;
   private static final ResourceLocation locationSnowPng = new ResourceLocation("textures/environment/snow.png");
   private float bossColorModifier;
   private boolean useShader;
   public int frameCount;
   private float smoothCamFilterX;
   public static int anaglyphField;
   private Minecraft mc;
   private final IResourceManager resourceManager;
   public ItemRenderer itemRenderer;
   private final MapItemRenderer theMapItemRenderer;
   private final DynamicTexture lightmapTexture;
   private float floatValue4;
   private float fogColor1;
   private final ResourceLocation locationLightMap;
   private static final ResourceLocation[] shaderResourceLocations = new ResourceLocation[]{
      new ResourceLocation("shaders/post/notch.json"),
      new ResourceLocation("shaders/post/fxaa.json"),
      new ResourceLocation("shaders/post/art.json"),
      new ResourceLocation("shaders/post/bumpy.json"),
      new ResourceLocation("shaders/post/blobs2.json"),
      new ResourceLocation("shaders/post/pencil.json"),
      new ResourceLocation("shaders/post/color_convolve.json"),
      new ResourceLocation("shaders/post/deconverge.json"),
      new ResourceLocation("shaders/post/flip.json"),
      new ResourceLocation("shaders/post/invert.json"),
      new ResourceLocation("shaders/post/ntsc.json"),
      new ResourceLocation("shaders/post/outline.json"),
      new ResourceLocation("shaders/post/phosphor.json"),
      new ResourceLocation("shaders/post/scan_pincushion.json"),
      new ResourceLocation("shaders/post/sobel.json"),
      new ResourceLocation("shaders/post/bits.json"),
      new ResourceLocation("shaders/post/desaturate.json"),
      new ResourceLocation("shaders/post/green.json"),
      new ResourceLocation("shaders/post/blur.json"),
      new ResourceLocation("shaders/post/wobble.json"),
      new ResourceLocation("shaders/post/blobs.json"),
      new ResourceLocation("shaders/post/antialias.json"),
      new ResourceLocation("shaders/post/creeper.json"),
      new ResourceLocation("shaders/post/spider.json")
   };
   private boolean lightmapUpdateNeeded;
   private final int[] lightmapColors;
   private float smoothCamFilterY;
   public static final int shaderCount = shaderResourceLocations.length;
   private ShaderGroup theShaderGroup;

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      if (this.theShaderGroup != null) {
         this.theShaderGroup.deleteShaderGroup();
      }

      this.theShaderGroup = null;
      if (this.shaderIndex != shaderCount) {
         this.loadShader(shaderResourceLocations[this.shaderIndex]);
      } else {
         this.loadEntityShader(this.mc.getRenderViewEntity());
      }
   }

   public void call3DRenderEvent(float renderPartialTicks) {
      this.setupOverlayRendering();
      this.mc.ingameGUI.handleScaledResolution2(new ScaledResolution(this.mc));
   }

   private void updateLightmap(float partialTicks) {
      if (this.lightmapUpdateNeeded) {
         this.mc.mcProfiler.startSection("lightTex");
         WorldClient theWorld2 = this.mc.theWorld;
         if (theWorld2 != null) {
            if (Config.isCustomColors()
               && CustomColors.updateLightmap(
                  theWorld2, this.torchFlickerX, this.lightmapColors, this.mc.thePlayer.isPotionActive(Potion.nightVision), partialTicks
               )) {
               this.lightmapTexture.updateDynamicTexture();
               this.lightmapUpdateNeeded = false;
               this.mc.mcProfiler.endSection();
               return;
            }

            float sunBrightness = theWorld2.getSunBrightness(1.0F);
            float f = sunBrightness * 0.95F + 0.05F;

            for (int i = 0; i < 256; i++) {
               float f2 = theWorld2.provider.getLightBrightnessTable()[i / 16] * f;
               float f3 = theWorld2.provider.getLightBrightnessTable()[i % 16] * (this.torchFlickerX * 0.1F + 1.5F);
               if (theWorld2.getLastLightningBolt() > 0) {
                  f2 = theWorld2.provider.getLightBrightnessTable()[i / 16];
               }

               float f4 = f2 * (sunBrightness * 0.65F + 0.35F);
               float f5 = f2 * (sunBrightness * 0.65F + 0.35F);
               float f6 = f3 * ((f3 * 0.6F + 0.4F) * 0.6F + 0.4F);
               float f7 = f3 * (f3 * f3 * 0.6F + 0.4F);
               float f8 = f4 + f3;
               float f9 = f5 + f6;
               float f10 = f2 + f7;
               f8 = f8 * 0.96F + 0.03F;
               f9 = f9 * 0.96F + 0.03F;
               f10 = f10 * 0.96F + 0.03F;
               if (this.bossColorModifier > 0.0F) {
                  float f11 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
                  f8 = f8 * (1.0F - f11) + f8 * 0.7F * f11;
                  f9 = f9 * (1.0F - f11) + f9 * 0.6F * f11;
                  f10 = f10 * (1.0F - f11) + f10 * 0.6F * f11;
               }

               if (theWorld2.provider.getDimensionId() == 1) {
                  f8 = 0.22F + f3 * 0.75F;
                  f9 = 0.28F + f6 * 0.75F;
                  f10 = 0.25F + f7 * 0.75F;
               }

               if (ModuleManager.worldColor.isEnabled() && ModuleManager.worldColor.worldColor.isSelected(2)) {
                  float[] fArray = RenderUtils.unpackColorNormalized(ModuleManager.worldColor.worldColorValue.getColor());
                  if (ModuleManager.worldColor.colorType.getCurrentElement() == 1) {
                     fArray = RenderUtils.unpackColorNormalized(RenderUtils.indexedHsbColorMirrored(1, 1, 1.0F, 1.0F, 3000L));
                  }

                  f8 = fArray[0];
                  f9 = fArray[1];
                  f10 = fArray[2];
               }

               if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
                  float nightVisionBrightness = this.getNightVisionBrightness(this.mc.thePlayer, partialTicks);
                  float f12 = 1.0F / f8;
                  if (f12 > 1.0F / f9) {
                     f12 = 1.0F / f9;
                  }

                  if (f12 > 1.0F / f10) {
                     f12 = 1.0F / f10;
                  }

                  f8 = f8 * (1.0F - nightVisionBrightness) + f8 * f12 * nightVisionBrightness;
                  f9 = f9 * (1.0F - nightVisionBrightness) + f9 * f12 * nightVisionBrightness;
                  f10 = f10 * (1.0F - nightVisionBrightness) + f10 * f12 * nightVisionBrightness;
               }

               if (f8 > 1.0F) {
                  f8 = 1.0F;
               }

               if (f9 > 1.0F) {
                  f9 = 1.0F;
               }

               if (f10 > 1.0F) {
                  f10 = 1.0F;
               }

               float gammaSetting2 = this.mc.gameSettings.gammaSetting;
               float f13 = 1.0F - f8;
               float f14 = 1.0F - f9;
               float f15 = 1.0F - f10;
               f13 = 1.0F - f13 * f13 * f13 * f13;
               f14 = 1.0F - f14 * f14 * f14 * f14;
               f15 = 1.0F - f15 * f15 * f15 * f15;
               f8 = f8 * (1.0F - gammaSetting2) + f13 * gammaSetting2;
               f9 = f9 * (1.0F - gammaSetting2) + f14 * gammaSetting2;
               f10 = f10 * (1.0F - gammaSetting2) + f15 * gammaSetting2;
               f8 = f8 * 0.96F + 0.03F;
               f9 = f9 * 0.96F + 0.03F;
               f10 = f10 * 0.96F + 0.03F;
               if (f8 > 1.0F) {
                  f8 = 1.0F;
               }

               if (f9 > 1.0F) {
                  f9 = 1.0F;
               }

               if (f10 > 1.0F) {
                  f10 = 1.0F;
               }

               if (f8 < 0.0F) {
                  f8 = 0.0F;
               }

               if (f9 < 0.0F) {
                  f9 = 0.0F;
               }

               if (f10 < 0.0F) {
                  f10 = 0.0F;
               }

               int i2 = (int)(f8 * 255.0F);
               int i3 = (int)(f9 * 255.0F);
               int i4 = (int)(f10 * 255.0F);
               this.lightmapColors[i] = 0xFF000000 | i2 << 16 | i3 << 8 | i4;
            }

            this.lightmapTexture.updateDynamicTexture();
            this.lightmapUpdateNeeded = false;
            this.mc.mcProfiler.endSection();
         }
      }
   }

   public void disableLightmap() {
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      if (Config.isShaders()) {
         Shaders.disableLightmap();
      }
   }

   public void updateRenderer() {
      if (OpenGlHelper.shadersSupported && ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
         ShaderLinkHelper.setNewStaticShaderLinkHelper();
      }

      this.updateFovModifierHand();
      this.updateTorchFlicker();
      this.fogColor2 = this.fogColor1;
      this.thirdPersonDistanceTemp = this.thirdPersonDistance;
      if (this.mc.gameSettings.smoothCamera) {
         float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
         float f2 = f * f * f * 8.0F;
         this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05F * f2);
         this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05F * f2);
         this.smoothCamPartialTicks = 0.0F;
         this.smoothCamYaw = 0.0F;
         this.smoothCamPitch = 0.0F;
      } else {
         this.smoothCamFilterX = 0.0F;
         this.smoothCamFilterY = 0.0F;
         this.mouseFilterXAxis.reset();
         this.mouseFilterYAxis.reset();
      }

      if (this.mc.getRenderViewEntity() == null) {
         this.mc.setRenderViewEntity(this.mc.thePlayer);
      }

      Entity renderViewEntity = this.mc.getRenderViewEntity();
      double posX2 = renderViewEntity.posX;
      double d2 = renderViewEntity.posY + renderViewEntity.getEyeHeight();
      double posZ2 = renderViewEntity.posZ;
      float lightBrightness = this.mc.theWorld.getLightBrightness(new BlockPos(posX2, d2, posZ2));
      float f3 = this.mc.gameSettings.renderDistanceChunks / 16.0F;
      f3 = MathHelper.clamp_float(f3, 0.0F, 1.0F);
      float f4 = lightBrightness * (1.0F - f3) + f3;
      this.fogColor1 = this.fogColor1 + (f4 - this.fogColor1) * 0.1F;
      this.rendererUpdateCount++;
      this.itemRenderer.updateEquippedItem();
      this.addRainParticles();
      this.bossColorModifierPrev = this.bossColorModifier;
      if (BossStatus.hasColorModifier) {
         this.bossColorModifier += 0.05F;
         if (this.bossColorModifier > 1.0F) {
            this.bossColorModifier = 1.0F;
         }

         BossStatus.hasColorModifier = false;
      } else if (this.bossColorModifier > 0.0F) {
         this.bossColorModifier -= 0.0125F;
      }
   }

   private void checkLoadVisibleChunks(Entity arg1, float arg2, ICamera arg3, boolean arg4) {
      if (this.loadVisibleChunks) {
         this.loadVisibleChunks = false;
         this.loadAllVisibleChunks(arg1, arg2, arg3, arg4);
         this.mc.ingameGUI.getChatGUI().deleteChatLine(201435902);
      }

      if (Keyboard.isKeyDown(61) && Keyboard.isKeyDown(38)) {
         if (this.mc.currentScreen != null) {
            return;
         }

         this.loadVisibleChunks = true;
         ChatComponentText chatComponentText = new ChatComponentText(I18n.format("of.message.loadingVisibleChunks"));
         this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(chatComponentText, 201435902);
      }
   }

   private void renderWorldDirections(float partialTicks) {
      if (this.mc.gameSettings.showDebugInfo && !this.mc.gameSettings.hideGUI && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo
         )
       {
         Entity entity = this.mc.getRenderViewEntity();
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         GL11.glLineWidth(1.0F);
         GlStateManager.disableTexture2D();
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         this.orientCamera(partialTicks);
         GlStateManager.translate(0.0F, entity.getEyeHeight(), 0.0F);
         RenderGlobal.handleAxisAlignedBB(new AxisAlignedBB(0.0, 0.0, 0.0, 0.005, 1.0E-4, 1.0E-4), 255, 0, 0);
         RenderGlobal.handleAxisAlignedBB(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0E-4, 1.0E-4, 0.005), 0, 0, 255);
         RenderGlobal.handleAxisAlignedBB(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0E-4, 0.0033, 1.0E-4), 0, 255, 0);
         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
      }
   }

   protected void renderRainSnow(float partialTicks) {
      if (Reflector.reflectorMethod91.isActive()) {
         WorldProvider provider2 = this.mc.theWorld.provider;
         Object object = Reflector.createObject4(provider2, Reflector.reflectorMethod91);
         if (object != null) {
            Reflector.handleObject(object, Reflector.reflectorMethod95, partialTicks, this.mc.theWorld, this.mc);
            return;
         }
      }

      float rainStrength = this.mc.theWorld.getRainStrength(partialTicks);
      if (ModuleManager.weather.isEnabled()) {
         rainStrength = 1.0F;
      }

      if (rainStrength > 0.0F) {
         if (Config.isRainOff() && !ModuleManager.weather.isEnabled()) {
            return;
         }

         this.enableLightmap();
         Entity renderViewEntity = this.mc.getRenderViewEntity();
         WorldClient theWorld2 = this.mc.theWorld;
         int i = MathHelper.floor_double(renderViewEntity.posX);
         int i2 = MathHelper.floor_double(renderViewEntity.posY);
         int i3 = MathHelper.floor_double(renderViewEntity.posZ);
         Tessellator instance2 = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance2.getWorldRenderer();
         GlStateManager.disableCull();
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         GlStateManager.alphaFunc(516, 0.1F);
         double d2 = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * partialTicks;
         double d3 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * partialTicks;
         double d4 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * partialTicks;
         int i4 = MathHelper.floor_double(d3);
         byte b = 5;
         if (Config.isRainFancy()) {
            b = 10;
         }

         byte b2 = -1;
         float f = this.rendererUpdateCount + partialTicks;
         worldRenderer.setTranslation(-d2, -d3, -d4);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

         for (int i5 = i3 - b; i5 <= i3 + b; i5++) {
            for (int i6 = i - b; i6 <= i + b; i6++) {
               int i7 = (i5 - i3 + 16) * 32 + i6 - i + 16;
               double d5 = this.rainXCoords[i7] * 0.5;
               double d6 = this.rainYCoords[i7] * 0.5;
               mutableBlockPos.set(i6, 0, i5);
               BiomeGenBase biomeGenForCoords = theWorld2.getBiomeGenForCoords(mutableBlockPos);
               if (biomeGenForCoords.canRain() || biomeGenForCoords.getEnableSnow()) {
                  int y = theWorld2.getPrecipitationHeight(mutableBlockPos).getY();
                  int i8 = i2 - b;
                  int i9 = i2 + b;
                  if (i8 < y) {
                     i8 = y;
                  }

                  if (i9 < y) {
                     i9 = y;
                  }

                  int i10 = y;
                  if (y < i4) {
                     i10 = i4;
                  }

                  if (i8 != i9) {
                     this.random.setSeed(i6 * i6 * 3121 + i6 * 45238971 ^ i5 * i5 * 418711 + i5 * 13761);
                     mutableBlockPos.set(i6, i8, i5);
                     float floatTemperature = biomeGenForCoords.getFloatTemperature(mutableBlockPos);
                     float[] fArray = RenderUtils.unpackColorNormalized(
                        ModuleManager.weather.isEnabled() && ModuleManager.weather.customColor.isEnabled() ? ModuleManager.weather.color.getColor() : -1
                     );
                     if (theWorld2.getWorldChunkManager().getFloat(floatTemperature) >= 0.15F
                        && (!ModuleManager.weather.isEnabled() || ModuleManager.weather.weather.getCurrentElement() == 1)) {
                        if (b2 != 0) {
                           if (b2 >= 0) {
                              instance2.draw();
                           }

                           b2 = 0;
                           this.mc.getTextureManager().bindTexture(locationRainPng);
                           worldRenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                        }

                        double d7 = (
                              (double)(this.rendererUpdateCount + i6 * i6 * 3121 + i6 * 45238971 + i5 * i5 * 418711 + i5 * 13761 & 31)
                                 + partialTicks
                           )
                           / 32.0
                           * (3.0 + this.random.nextDouble());
                        double d8 = i6 + 0.5F - renderViewEntity.posX;
                        double d9 = i5 + 0.5F - renderViewEntity.posZ;
                        float f2 = MathHelper.sqrt_double(d8 * d8 + d9 * d9) / b;
                        float f3 = ((1.0F - f2 * f2) * 0.5F + 0.5F) * rainStrength;
                        mutableBlockPos.set(i6, i10, i5);
                        int combinedLight = theWorld2.getCombinedLight(mutableBlockPos, 0);
                        int i11 = combinedLight >> 16 & 65535;
                        int i12 = combinedLight & 65535;
                        worldRenderer.pos(i6 - d5 + 0.5, i8, i5 - d6 + 0.5)
                           .tex(0.0, i8 * 0.25 + d7)
                           .color(fArray[0], fArray[1], fArray[2], f3)
                           .lightmap(i11, i12)
                           .endVertex();
                        worldRenderer.pos(i6 + d5 + 0.5, i8, i5 + d6 + 0.5)
                           .tex(1.0, i8 * 0.25 + d7)
                           .color(fArray[0], fArray[1], fArray[2], f3)
                           .lightmap(i11, i12)
                           .endVertex();
                        worldRenderer.pos(i6 + d5 + 0.5, i9, i5 + d6 + 0.5)
                           .tex(1.0, i9 * 0.25 + d7)
                           .color(fArray[0], fArray[1], fArray[2], f3)
                           .lightmap(i11, i12)
                           .endVertex();
                        worldRenderer.pos(i6 - d5 + 0.5, i9, i5 - d6 + 0.5)
                           .tex(0.0, i9 * 0.25 + d7)
                           .color(fArray[0], fArray[1], fArray[2], f3)
                           .lightmap(i11, i12)
                           .endVertex();
                     } else {
                        if (b2 != 1) {
                           if (b2 >= 0) {
                              instance2.draw();
                           }

                           b2 = 1;
                           this.mc.getTextureManager().bindTexture(locationSnowPng);
                           worldRenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                        }

                        double d10 = ((this.rendererUpdateCount & 511) + partialTicks) / 512.0F;
                        double d11 = this.random.nextDouble() + f * 0.01 * (float)this.random.nextGaussian();
                        double d12 = this.random.nextDouble() + f * (float)this.random.nextGaussian() * 0.001;
                        double d13 = i6 + 0.5F - renderViewEntity.posX;
                        double d14 = i5 + 0.5F - renderViewEntity.posZ;
                        float f4 = MathHelper.sqrt_double(d13 * d13 + d14 * d14) / b;
                        float f5 = ((1.0F - f4 * f4) * 0.3F + 0.5F) * rainStrength;
                        mutableBlockPos.set(i6, i10, i5);
                        int i13 = (theWorld2.getCombinedLight(mutableBlockPos, 0) * 3 + 15728880) / 4;
                        int i14 = i13 >> 16 & 65535;
                        int i15 = i13 & 65535;
                        worldRenderer.pos(i6 - d5 + 0.5, i8, i5 - d6 + 0.5)
                           .tex(0.0 + d11, i8 * 0.25 + d10 + d12)
                           .color(fArray[0], fArray[1], fArray[2], f5)
                           .lightmap(i14, i15)
                           .endVertex();
                        worldRenderer.pos(i6 + d5 + 0.5, i8, i5 + d6 + 0.5)
                           .tex(1.0 + d11, i8 * 0.25 + d10 + d12)
                           .color(fArray[0], fArray[1], fArray[2], f5)
                           .lightmap(i14, i15)
                           .endVertex();
                        worldRenderer.pos(i6 + d5 + 0.5, i9, i5 + d6 + 0.5)
                           .tex(1.0 + d11, i9 * 0.25 + d10 + d12)
                           .color(fArray[0], fArray[1], fArray[2], f5)
                           .lightmap(i14, i15)
                           .endVertex();
                        worldRenderer.pos(i6 - d5 + 0.5, i9, i5 - d6 + 0.5)
                           .tex(0.0 + d11, i9 * 0.25 + d10 + d12)
                           .color(fArray[0], fArray[1], fArray[2], f5)
                           .lightmap(i14, i15)
                           .endVertex();
                     }
                  }
               }
            }
         }

         if (b2 >= 0) {
            instance2.draw();
         }

         worldRenderer.setTranslation(0.0, 0.0, 0.0);
         GlStateManager.enableCull();
         GlStateManager.disableBlend();
         GlStateManager.alphaFunc(516, 0.1F);
         this.disableLightmap();
      }
   }

   public void stopUseShader() {
      if (this.theShaderGroup != null) {
         this.theShaderGroup.deleteShaderGroup();
      }

      this.theShaderGroup = null;
      this.shaderIndex = shaderCount;
   }

   private void hurtCameraEffect(float partialTicks) {
      if (!ModuleManager.noHurtCam.isEnabled()) {
         if (this.mc.getRenderViewEntity() instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)this.mc.getRenderViewEntity();
            float f = entityLivingBase.hurtTime - partialTicks;
            if (entityLivingBase.getHealth() <= 0.0F) {
               float f2 = entityLivingBase.deathTime + partialTicks;
               GlStateManager.rotate(40.0F - 8000.0F / (f2 + 200.0F), 0.0F, 0.0F, 1.0F);
            }

            if (f < 0.0F) {
               return;
            }

            f /= entityLivingBase.maxHurtTime;
            f = MathHelper.sin(f * f * f * f * (float) Math.PI);
            float attackedAtYaw2 = entityLivingBase.attackedAtYaw;
            GlStateManager.rotate(-attackedAtYaw2, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-f * 14.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(attackedAtYaw2, 0.0F, 1.0F, 0.0F);
         }
      }
   }

   public void getMouseOver(float partialTicks) {
      Entity renderViewEntity = this.mc.getRenderViewEntity();
      if (renderViewEntity != null && this.mc.theWorld != null) {
         this.mc.mcProfiler.startSection("pick");
         this.mc.pointedEntity = null;
         double blockReachDistance = this.mc.playerController.getBlockReachDistance();
         this.mc.objectMouseOver = renderViewEntity.rayTrace(blockReachDistance, partialTicks);
         double d2 = blockReachDistance;
         Vec3 positionEyes = renderViewEntity.getPositionEyes(partialTicks);
         boolean flag = false;
         Reach reach2 = ModuleManager.reach;
         if (this.mc.playerController.extendedReach()) {
            blockReachDistance = 6.0;
            d2 = 6.0;
         } else if (blockReachDistance > 3.0) {
            flag = true;
         }

         if (this.mc.objectMouseOver != null) {
            d2 = this.mc.objectMouseOver.hitVec.distanceTo(positionEyes);
         }

         if (reach2.isEnabled()) {
            blockReachDistance = reach2.getReach().getCurrentValue();
            d2 = blockReachDistance;
            flag = false;
         }

         Vec3 look = renderViewEntity.getLook(partialTicks);
         Vec3 vec3 = positionEyes.addVector(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance);
         Vec3 vec32 = null;
         this.pointedEntity = null;
         float f = 1.0F;
         List entitiesInAABBexcluding = this.mc
            .theWorld
            .getEntitiesInAABBexcluding(
               renderViewEntity,
               renderViewEntity.getEntityBoundingBox().addCoord(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance).expand(f, f, f),
               Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith)
            );
         double d3 = d2;

         for (int i = 0; i < entitiesInAABBexcluding.size(); i++) {
            Entity entity2 = (Entity)entitiesInAABBexcluding.get(i);
            float collisionBorderSize = entity2.getCollisionBorderSize();
            AxisAlignedBB axisAlignedBB = entity2.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
            MovingObjectPosition movingObjectPosition = axisAlignedBB.calculateIntercept(positionEyes, vec3);
            if (axisAlignedBB.isVecInside(positionEyes)) {
               if (d3 >= 0.0) {
                  this.pointedEntity = entity2;
                  vec32 = movingObjectPosition == null ? positionEyes : movingObjectPosition.hitVec;
                  d3 = 0.0;
               }
            } else if (movingObjectPosition != null) {
               double d4 = positionEyes.distanceTo(movingObjectPosition.hitVec);
               if (d4 < d3 || d3 == 0.0) {
                  boolean flag2 = false;
                  if (Reflector.reflectorMethod34.isActive()) {
                     flag2 = Reflector.checkObject(entity2, Reflector.reflectorMethod34);
                  }

                  if (flag2 || entity2 != renderViewEntity.ridingEntity) {
                     this.pointedEntity = entity2;
                     vec32 = movingObjectPosition.hitVec;
                     d3 = d4;
                  } else if (d3 == 0.0) {
                     this.pointedEntity = entity2;
                     vec32 = movingObjectPosition.hitVec;
                  }
               }
            }
         }

         double d5 = reach2.isEnabled() ? reach2.getReach().getCurrentValue() : 3.0;
         if (this.pointedEntity != null && flag && positionEyes.distanceTo(vec32) > d5) {
            this.pointedEntity = null;
            this.mc.objectMouseOver = new MovingObjectPosition(net.minecraft.util.MovingObjectPosition.MovingObjectType.MISS, vec32, (EnumFacing)null, new BlockPos(vec32));
         }

         if (this.pointedEntity != null && (d3 < d2 || this.mc.objectMouseOver == null)) {
            this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec32);
            if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
               this.mc.pointedEntity = this.pointedEntity;
            }
         }

         this.mc.mcProfiler.endSection();
      }
   }

   public void updateCameraAndRender(float partialTicks, long nanoTime) {
      Config.renderPartialTicks = partialTicks;
      this.frameInit();
      boolean active = Display.isActive();
      if (!active && this.mc.gameSettings.pauseOnLostFocus && (!this.mc.gameSettings.touchscreen || !Mouse.isButtonDown(1))) {
         if (Minecraft.getSystemTime() - this.prevFrameTime > 500L) {
            this.mc.displayInGameMenu();
         }
      } else {
         this.prevFrameTime = Minecraft.getSystemTime();
      }

      this.mc.mcProfiler.startSection("mouse");
      FreeLook freeLook2 = ModuleManager.freeLook;
      if (active && Minecraft.isRunningOnMac && freeLook2.handleMouseLook() && !Mouse.isInsideWindow()) {
         Mouse.setGrabbed(false);
         Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
         Mouse.setGrabbed(true);
      }

      boolean flag = this.mc.theWorld != null
         && Novoline.initialized
         && ModuleManager.inventoryMove.isEnabled()
         && ModuleManager.inventoryMove.mouseMove.isEnabled();
      if ((freeLook2.handleMouseLook() || flag) && active) {
         this.mc.mouseHelper.mouseXYChange();
         float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
         float f2 = f * f * f * 8.0F;
         float f3 = this.mc.mouseHelper.deltaX * f2;
         float f4 = this.mc.mouseHelper.deltaY * f2;
         byte b = 1;
         if (this.mc.gameSettings.invertMouse) {
            b = -1;
         }

         if (this.mc.gameSettings.smoothCamera) {
            this.smoothCamYaw += f3;
            this.smoothCamPitch += f4;
            float f5 = partialTicks - this.smoothCamPartialTicks;
            this.smoothCamPartialTicks = partialTicks;
            f3 = this.smoothCamFilterX * f5;
            f4 = this.smoothCamFilterY * f5;
            this.mc.thePlayer.setAngles(f3, f4 * b);
         } else {
            this.smoothCamYaw = 0.0F;
            this.smoothCamPitch = 0.0F;
            this.mc.thePlayer.setAngles(f3, f4 * b);
         }
      }

      this.mc.mcProfiler.endSection();
      if (!this.mc.skipRenderWorld) {
         anaglyphEnable = this.mc.gameSettings.anaglyph;
         final ScaledResolution scaledResolution = new ScaledResolution(this.mc);
         int scaledWidth = scaledResolution.getScaledWidth();
         int scaledHeight = scaledResolution.getScaledHeight();
         final int i = Mouse.getX() * scaledWidth / this.mc.displayWidth;
         final int i2 = scaledHeight - Mouse.getY() * scaledHeight / this.mc.displayHeight - 1;
         int limitFramerate2 = this.mc.gameSettings.limitFramerate;
         if (this.mc.theWorld != null) {
            this.mc.mcProfiler.startSection("level");
            int i3 = Math.min(Minecraft.getDebugFPS(), limitFramerate2);
            i3 = Math.max(i3, 60);
            long l = System.nanoTime() - nanoTime;
            long l2 = Math.max(1000000000 / i3 / 4 - l, 0L);
            this.renderWorld(partialTicks, System.nanoTime() + l2);
            if (OpenGlHelper.shadersSupported) {
               this.mc.renderGlobal.renderEntityOutlineFramebuffer();
               if (this.theShaderGroup != null && this.useShader) {
                  GlStateManager.matrixMode(5890);
                  GlStateManager.pushMatrix();
                  GlStateManager.loadIdentity();
                  this.theShaderGroup.loadShaderGroup(partialTicks);
                  GlStateManager.popMatrix();
               }

               this.mc.getFramebuffer().bindFramebuffer(true);
            }

            this.renderEndNanoTime = System.nanoTime();
            this.mc.mcProfiler.endStartSection("gui");
            if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null) {
               GlStateManager.alphaFunc(516, 0.1F);
               this.mc.ingameGUI.renderGameOverlay(partialTicks);
               if (this.mc.gameSettings.ofShowFps && !this.mc.gameSettings.showDebugInfo) {
                  Config.drawFps();
               }

               if (this.mc.gameSettings.showDebugInfo) {
                  Lagometer.showLagometer(scaledResolution);
               }
            }

            this.mc.mcProfiler.endSection();
         } else {
            GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            this.setupOverlayRendering();
            this.renderEndNanoTime = System.nanoTime();
            TileEntityRendererDispatcher.instance.renderEngine = this.mc.getTextureManager();
            TileEntityRendererDispatcher.instance.fontRenderer = this.mc.fontRendererObj;
         }

         if (this.mc.currentScreen != null) {
            GlStateManager.clear(256);

            try {
               if (Reflector.reflectorMethod54.isActive()) {
                  Reflector.handleReflectorMethod(Reflector.reflectorMethod54, this.mc.currentScreen, i, i2, partialTicks);
               } else {
                  this.mc.currentScreen.drawScreen(i, i2, partialTicks);
               }
            } catch (Throwable throwable) {
               CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Rendering screen");
               CrashReportCategory crashReportCategory = crashReport.makeCategory("Screen render details");
               crashReportCategory.addCrashSectionCallable("Screen name", new Callable<String>() {
                  public String call() throws Exception {
                     return EntityRenderer.this.mc.currentScreen.getClass().getCanonicalName();
                  }
               });
               crashReportCategory.addCrashSectionCallable("Mouse location", new Callable<String>() {
                  public String call() throws Exception {
                     return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", i, i2, Mouse.getX(), Mouse.getY());
                  }
               });
               crashReportCategory.addCrashSectionCallable(
                  "Screen size",
                  new Callable<String>() {
                     public String call() throws Exception {
                        return String.format(
                           "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d",
                           scaledResolution.getScaledWidth(),
                           scaledResolution.getScaledHeight(),
                           EntityRenderer.this.mc.displayWidth,
                           EntityRenderer.this.mc.displayHeight,
                           scaledResolution.getScaleFactor()
                        );
                     }
                  }
               );
               throw new ReportedException(crashReport);
            }
         }
      }

      this.frameFinish();
      this.waitForServerThread();
      MemoryMonitor.update();
      Lagometer.updateLagometer();
      if (this.mc.gameSettings.ofProfiler) {
         this.mc.gameSettings.showDebugProfilerChart = true;
      }
   }

   public void switchUseShader() {
      this.useShader = !this.useShader;
   }

   public void setupCameraTransform(float partialTicks, int pass) {
      this.farPlaneDistance = this.mc.gameSettings.renderDistanceChunks * 16;
      if (Config.isFogFancy()) {
         this.farPlaneDistance *= 0.95F;
      }

      if (Config.isFogFast()) {
         this.farPlaneDistance *= 0.83F;
      }

      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      float f = 0.07F;
      if (this.mc.gameSettings.anaglyph) {
         GlStateManager.translate(-(pass * 2 - 1) * f, 0.0F, 0.0F);
      }

      this.clipDistance = this.farPlaneDistance * 2.0F;
      if (this.clipDistance < 173.0F) {
         this.clipDistance = 173.0F;
      }

      if (this.doubleValue != 1.0) {
         GlStateManager.translate((float)this.cameraPitch, (float)(-this.doubleValue2), 0.0F);
         GlStateManager.scale(this.doubleValue, this.doubleValue, 1.0);
      }

      Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      if (this.mc.gameSettings.anaglyph) {
         GlStateManager.translate((pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
      }

      this.hurtCameraEffect(partialTicks);
      if (this.mc.gameSettings.viewBobbing) {
         this.setupViewBobbing(partialTicks);
      }

      float f2 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;
      if (f2 > 0.0F) {
         byte b = 20;
         if (this.mc.thePlayer.isPotionActive(Potion.confusion)) {
            b = 7;
         }

         float f3 = 5.0F / (f2 * f2 + 5.0F) - f2 * 0.04F;
         f3 *= f3;
         GlStateManager.rotate((this.rendererUpdateCount + partialTicks) * b, 0.0F, 1.0F, 1.0F);
         GlStateManager.scale(1.0F / f3, 1.0F, 1.0F);
         GlStateManager.rotate(-(this.rendererUpdateCount + partialTicks) * b, 0.0F, 1.0F, 1.0F);
      }

      this.orientCamera(partialTicks);
      if (this.debugView) {
         switch (this.debugViewDirection) {
            case 0:
               GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
               break;
            case 1:
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
               break;
            case 2:
               GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
               break;
            case 3:
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
               break;
            case 4:
               GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
         }
      }
   }

   private void setupFog(int startCoords, float partialTicks) {
      this.fovModifierForced = false;
      Entity renderViewEntity = this.mc.getRenderViewEntity();
      if (renderViewEntity instanceof EntityPlayer) {
         ;
      }

      GL11.glFog(2918, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
      GL11.glNormal3f(0.0F, -1.0F, 0.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, partialTicks);
      float f = -1.0F;
      if (Reflector.reflectorMethod59.isActive()) {
         f = Reflector.createFloat2(Reflector.reflectorMethod59, this, renderViewEntity, blockAtEntityViewpoint, partialTicks, 0.1F);
      }

      if (f >= 0.0F) {
         GlStateManager.setFogDensity(f);
      } else if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPotionActive(Potion.blindness)) {
         float f2 = 5.0F;
         int duration = ((EntityLivingBase)renderViewEntity).getActivePotionEffect(Potion.blindness).getDuration();
         if (duration < 20) {
            f2 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - duration / 20.0F);
         }

         GlStateManager.setFog(9729);
         if (startCoords == -1) {
            GlStateManager.setFogStart(0.0F);
            GlStateManager.setFogEnd(f2 * 0.8F);
         } else {
            GlStateManager.setFogStart(f2 * 0.25F);
            GlStateManager.setFogEnd(f2);
         }

         if (GLContext.getCapabilities().GL_NV_fog_distance && Config.isFogFancy()) {
            GL11.glFogi(34138, 34139);
         }
      } else if (this.cloudFog) {
         GlStateManager.setFog(2048);
         GlStateManager.setFogDensity(0.1F);
      } else if (blockAtEntityViewpoint.getMaterial() == Material.water) {
         GlStateManager.setFog(2048);
         float f3 = Config.isClearWater() ? 0.02F : 0.1F;
         if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPotionActive(Potion.waterBreathing)) {
            GlStateManager.setFogDensity(0.01F);
         } else {
            float f4 = 0.1F - EnchantmentHelper.getRespiration(renderViewEntity) * 0.03F;
            GlStateManager.setFogDensity(Config.limit(f4, 0.0F, f3));
         }
      } else if (blockAtEntityViewpoint.getMaterial() == Material.lava) {
         GlStateManager.setFog(2048);
         GlStateManager.setFogDensity(2.0F);
      } else {
         float farPlaneDistance2 = this.farPlaneDistance;
         this.fovModifierForced = true;
         GlStateManager.setFog(9729);
         if (startCoords == -1) {
            GlStateManager.setFogStart(0.0F);
            GlStateManager.setFogEnd(farPlaneDistance2);
         } else {
            GlStateManager.setFogStart(farPlaneDistance2 * Config.getFogStart());
            GlStateManager.setFogEnd(farPlaneDistance2);
         }

         if (GLContext.getCapabilities().GL_NV_fog_distance) {
            if (Config.isFogFancy()) {
               GL11.glFogi(34138, 34139);
            }

            if (Config.isFogFast()) {
               GL11.glFogi(34138, 34140);
            }
         }

         if (this.mc.theWorld.provider.doesXZShowFog((int)renderViewEntity.posX, (int)renderViewEntity.posZ)) {
            GlStateManager.setFogStart(farPlaneDistance2 * 0.05F);
            GlStateManager.setFogEnd(farPlaneDistance2);
         }

         if (Reflector.reflectorMethod65.isActive()) {
            Reflector.handleReflectorMethod(Reflector.reflectorMethod65, this, renderViewEntity, blockAtEntityViewpoint, partialTicks, startCoords, farPlaneDistance2);
         }
      }

      GlStateManager.enableColorMaterial();
      GlStateManager.enableFog();
      GlStateManager.colorMaterial(1028, 4608);
   }

   private void loadShader(ResourceLocation resourceLocationIn) {
      if (OpenGlHelper.isFramebufferEnabled()) {
         try {
            this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), resourceLocationIn);
            this.theShaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            this.useShader = true;
         } catch (IOException ioException) {
            logger.warn("Failed to load shader: " + resourceLocationIn, ioException);
            this.shaderIndex = shaderCount;
            this.useShader = false;
         } catch (JsonSyntaxException jsonSyntaxException) {
            logger.warn("Failed to load shader: " + resourceLocationIn, jsonSyntaxException);
            this.shaderIndex = shaderCount;
            this.useShader = false;
         }
      }
   }

   private void updateFogColor(float partialTicks) {
      WorldClient theWorld2 = this.mc.theWorld;
      Entity renderViewEntity = this.mc.getRenderViewEntity();
      float f = 0.25F + 0.75F * this.mc.gameSettings.renderDistanceChunks / 32.0F;
      f = 1.0F - (float)Math.pow(f, 0.25);
      Vec3 skyColor = theWorld2.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
      skyColor = CustomColors.getWorldSkyColor(skyColor, theWorld2, this.mc.getRenderViewEntity(), partialTicks);
      float f2 = (float)skyColor.xCoord;
      float f3 = (float)skyColor.yCoord;
      float f4 = (float)skyColor.zCoord;
      Vec3 fogColor = theWorld2.getFogColor(partialTicks);
      fogColor = CustomColors.getWorldFogColor(fogColor, theWorld2, this.mc.getRenderViewEntity(), partialTicks);
      this.fogColorRed = (float)fogColor.xCoord;
      this.fogColorGreen = (float)fogColor.yCoord;
      this.fogColorBlue = (float)fogColor.zCoord;
      if (this.mc.gameSettings.renderDistanceChunks >= 4) {
         double d2 = -1.0;
         Vec3 vec3 = MathHelper.sin(theWorld2.getCelestialAngleRadians(partialTicks)) > 0.0F ? new Vec3(d2, 0.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
         float f5 = (float)renderViewEntity.getLook(partialTicks).dotProduct(vec3);
         if (f5 < 0.0F) {
            f5 = 0.0F;
         }

         if (f5 > 0.0F) {
            float[] sunriseSunsetColors = theWorld2.provider.calcSunriseSunsetColors(theWorld2.getCelestialAngle(partialTicks), partialTicks);
            if (sunriseSunsetColors != null) {
               f5 *= sunriseSunsetColors[3];
               this.fogColorRed = this.fogColorRed * (1.0F - f5) + sunriseSunsetColors[0] * f5;
               this.fogColorGreen = this.fogColorGreen * (1.0F - f5) + sunriseSunsetColors[1] * f5;
               this.fogColorBlue = this.fogColorBlue * (1.0F - f5) + sunriseSunsetColors[2] * f5;
            }
         }
      }

      this.fogColorRed = this.fogColorRed + (f2 - this.fogColorRed) * f;
      this.fogColorGreen = this.fogColorGreen + (f3 - this.fogColorGreen) * f;
      this.fogColorBlue = this.fogColorBlue + (f4 - this.fogColorBlue) * f;
      float rainStrength = theWorld2.getRainStrength(partialTicks);
      if (rainStrength > 0.0F) {
         float f6 = 1.0F - rainStrength * 0.5F;
         float f7 = 1.0F - rainStrength * 0.4F;
         this.fogColorRed *= f6;
         this.fogColorGreen *= f6;
         this.fogColorBlue *= f7;
      }

      float thunderStrength = theWorld2.getThunderStrength(partialTicks);
      if (thunderStrength > 0.0F) {
         float f8 = 1.0F - thunderStrength * 0.5F;
         this.fogColorRed *= f8;
         this.fogColorGreen *= f8;
         this.fogColorBlue *= f8;
      }

      Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, partialTicks);
      if (this.cloudFog) {
         Vec3 cloudColour = theWorld2.getCloudColour(partialTicks);
         this.fogColorRed = (float)cloudColour.xCoord;
         this.fogColorGreen = (float)cloudColour.yCoord;
         this.fogColorBlue = (float)cloudColour.zCoord;
      } else if (blockAtEntityViewpoint.getMaterial() == Material.water) {
         float f9 = EnchantmentHelper.getRespiration(renderViewEntity) * 0.2F;
         f9 = Config.limit(f9, 0.0F, 0.6F);
         if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPotionActive(Potion.waterBreathing)) {
            f9 = f9 * 0.3F + 0.6F;
         }

         this.fogColorRed = 0.02F + f9;
         this.fogColorGreen = 0.02F + f9;
         this.fogColorBlue = 0.2F + f9;
         Vec3 underwaterColor = CustomColors.getUnderwaterColor(
            this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ
         );
         if (underwaterColor != null) {
            this.fogColorRed = (float)underwaterColor.xCoord;
            this.fogColorGreen = (float)underwaterColor.yCoord;
            this.fogColorBlue = (float)underwaterColor.zCoord;
         }
      } else if (blockAtEntityViewpoint.getMaterial() == Material.lava) {
         this.fogColorRed = 0.6F;
         this.fogColorGreen = 0.1F;
         this.fogColorBlue = 0.0F;
         Vec3 underlavaColor = CustomColors.getUnderlavaColor(
            this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ
         );
         if (underlavaColor != null) {
            this.fogColorRed = (float)underlavaColor.xCoord;
            this.fogColorGreen = (float)underlavaColor.yCoord;
            this.fogColorBlue = (float)underlavaColor.zCoord;
         }
      }

      float f10 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * partialTicks;
      this.fogColorRed *= f10;
      this.fogColorGreen *= f10;
      this.fogColorBlue *= f10;
      double d3 = (renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * partialTicks) * theWorld2.provider.getVoidFogYFactor();
      if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPotionActive(Potion.blindness)) {
         int duration = ((EntityLivingBase)renderViewEntity).getActivePotionEffect(Potion.blindness).getDuration();
         if (duration < 20) {
            d3 *= 1.0F - duration / 20.0F;
         } else {
            d3 = 0.0;
         }
      }

      if (d3 < 1.0) {
         if (d3 < 0.0) {
            d3 = 0.0;
         }

         d3 *= d3;
         this.fogColorRed = (float)(this.fogColorRed * d3);
         this.fogColorGreen = (float)(this.fogColorGreen * d3);
         this.fogColorBlue = (float)(this.fogColorBlue * d3);
      }

      if (this.bossColorModifier > 0.0F) {
         float f11 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
         this.fogColorRed = this.fogColorRed * (1.0F - f11) + this.fogColorRed * 0.7F * f11;
         this.fogColorGreen = this.fogColorGreen * (1.0F - f11) + this.fogColorGreen * 0.6F * f11;
         this.fogColorBlue = this.fogColorBlue * (1.0F - f11) + this.fogColorBlue * 0.6F * f11;
      }

      if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPotionActive(Potion.nightVision)) {
         float nightVisionBrightness = this.getNightVisionBrightness((EntityLivingBase)renderViewEntity, partialTicks);
         float f12 = 1.0F / this.fogColorRed;
         if (f12 > 1.0F / this.fogColorGreen) {
            f12 = 1.0F / this.fogColorGreen;
         }

         if (f12 > 1.0F / this.fogColorBlue) {
            f12 = 1.0F / this.fogColorBlue;
         }

         if (Float.isInfinite(f12)) {
            f12 = Math.nextAfter(f12, 0.0);
         }

         this.fogColorRed = this.fogColorRed * (1.0F - nightVisionBrightness) + this.fogColorRed * f12 * nightVisionBrightness;
         this.fogColorGreen = this.fogColorGreen * (1.0F - nightVisionBrightness) + this.fogColorGreen * f12 * nightVisionBrightness;
         this.fogColorBlue = this.fogColorBlue * (1.0F - nightVisionBrightness) + this.fogColorBlue * f12 * nightVisionBrightness;
      }

      if (this.mc.gameSettings.anaglyph) {
         float f13 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
         float f14 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
         float f15 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
         this.fogColorRed = f13;
         this.fogColorGreen = f14;
         this.fogColorBlue = f15;
      }

      if (Reflector.reflectorConstructor5.isActive()) {
         Object object = Reflector.createObject5(
            Reflector.reflectorConstructor5, this, renderViewEntity, blockAtEntityViewpoint, partialTicks, this.fogColorRed, this.fogColorGreen, this.fogColorBlue
         );
         Reflector.checkObject2(object);
         this.fogColorRed = Reflector.createFloat(object, Reflector.reflectorField4, this.fogColorRed);
         this.fogColorGreen = Reflector.createFloat(object, Reflector.reflectorField5, this.fogColorGreen);
         this.fogColorBlue = Reflector.createFloat(object, Reflector.reflectorField6, this.fogColorBlue);
      }

      Shaders.setClearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
   }

   private void updateFovModifierHand() {
      float f = 1.0F;
      if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer) {
         f = ((AbstractClientPlayer)this.mc.getRenderViewEntity()).getFovModifier();
      }

      this.fovModifierHandPrev = this.fovModifierHand;
      this.fovModifierHand = this.fovModifierHand + (f - this.fovModifierHand) * 0.5F;
      if (this.fovModifierHand > 1.5F) {
         this.fovModifierHand = 1.5F;
      }

      if (this.fovModifierHand < 0.1F) {
         this.fovModifierHand = 0.1F;
      }
   }

   public void updateShaderGroupSize(int width, int height) {
      if (OpenGlHelper.shadersSupported) {
         if (this.theShaderGroup != null) {
            this.theShaderGroup.createBindFramebuffers(width, height);
         }

         this.mc.renderGlobal.createBindEntityOutlineFbs(width, height);
      }
   }

   private FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha) {
      if (Config.isShaders()) {
         Shaders.setFogColor(red, green, blue);
      }

      this.fogColorBuffer.clear();
      this.fogColorBuffer.put(red).put(green).put(blue).put(alpha);
      this.fogColorBuffer.flip();
      return this.fogColorBuffer;
   }

   public MapItemRenderer getMapItemRenderer() {
      return this.theMapItemRenderer;
   }

   public boolean isShaderActive() {
      return OpenGlHelper.shadersSupported && this.theShaderGroup != null;
   }

   public ShaderGroup getShaderGroup() {
      return this.theShaderGroup;
   }

   public boolean setFxaaShader(int arg1) {
      if (!OpenGlHelper.isFramebufferEnabled()) {
         return false;
      }

      if (this.theShaderGroup != null && this.theShaderGroup != this.fxaaShaders[2] && this.theShaderGroup != this.fxaaShaders[4]) {
         return true;
      }

      if (arg1 != 2 && arg1 != 4) {
         if (this.theShaderGroup == null) {
            return true;
         }

         this.theShaderGroup.deleteShaderGroup();
         this.theShaderGroup = null;
         return true;
      } else {
         if (this.theShaderGroup != null && this.theShaderGroup == this.fxaaShaders[arg1]) {
            return true;
         }

         if (this.mc.theWorld == null) {
            return true;
         }

         this.loadShader(new ResourceLocation("shaders/post/fxaa_of_" + arg1 + "x.json"));
         this.fxaaShaders[arg1] = this.theShaderGroup;
         return this.useShader;
      }
   }

   public void loadEntityShader(Entity entityIn) {
      if (OpenGlHelper.shadersSupported) {
         if (this.theShaderGroup != null) {
            this.theShaderGroup.deleteShaderGroup();
         }

         this.theShaderGroup = null;
         if (entityIn instanceof EntityCreeper) {
            this.loadShader(new ResourceLocation("shaders/post/creeper.json"));
         } else if (entityIn instanceof EntitySpider) {
            this.loadShader(new ResourceLocation("shaders/post/spider.json"));
         } else if (entityIn instanceof EntityEnderman) {
            this.loadShader(new ResourceLocation("shaders/post/invert.json"));
         } else if (Reflector.reflectorMethod63.isActive()) {
            Reflector.createObject2(Reflector.reflectorMethod63, entityIn, this);
         }
      }
   }

   public void setupOverlayRendering() {
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      GlStateManager.clear(256);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translate(0.0F, 0.0F, -2000.0F);
   }

   public float getNightVisionBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks) {
      int duration = entitylivingbaseIn.getActivePotionEffect(Potion.nightVision).getDuration();
      return duration > 200 ? 1.0F : 0.7F + MathHelper.sin((duration - partialTicks) * (float) Math.PI * 0.2F) * 0.3F;
   }

   private void orientCamera(float partialTicks) {
      Entity renderViewEntity = this.mc.getRenderViewEntity();
      float eyeHeight = renderViewEntity.getEyeHeight();
      if (ModuleManager.betterMinecraft.isEnabled() && ModuleManager.betterMinecraft.sneak.isEnabled()) {
         boolean sneaking = this.mc.thePlayer.isSneaking();
         this.floatValue3 = SmoothingUtils.approachWithMinimumStep(this.floatValue3, sneaking ? 8.0F : 0.0F, 8.0F, 0.01F);
         eyeHeight += (sneaking ? 0.08F : 0.0F) - this.floatValue3 / 100.0F;
      }

      double d2 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * partialTicks;
      double d3 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * partialTicks + eyeHeight;
      double d4 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * partialTicks;
      FreeLook freeLook2 = ModuleManager.freeLook;
      boolean freeLookActive = freeLook2.isFreeLookActive();
      float f = freeLookActive ? freeLook2.getLookYaw() : renderViewEntity.prevRotationYaw;
      float f2 = freeLookActive ? freeLook2.getLookPitch() : renderViewEntity.prevRotationPitch;
      float f3 = freeLookActive ? f : renderViewEntity.rotationYaw;
      float f4 = freeLookActive ? f2 : renderViewEntity.rotationPitch;
      if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPlayerSleeping()) {
         eyeHeight = (float)(eyeHeight + 1.0);
         GlStateManager.translate(0.0F, 0.3F, 0.0F);
         if (!this.mc.gameSettings.debugCamEnable) {
            BlockPos blockPos2 = new BlockPos(renderViewEntity);
            IBlockState blockState2 = this.mc.theWorld.getBlockState(blockPos2);
            Block block2 = blockState2.getBlock();
            if (Reflector.reflectorMethod68.isActive()) {
               Reflector.handleReflectorMethod(Reflector.reflectorMethod68, this.mc.theWorld, blockPos2, blockState2, renderViewEntity);
            } else if (block2 == Blocks.bed) {
               int horizontalIndex = blockState2.getValue(BlockBed.FACING).getHorizontalIndex();
               GlStateManager.rotate(horizontalIndex * 90, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotate(f + (f3 - f) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
            GlStateManager.rotate(f2 + (f4 - f2) * partialTicks, -1.0F, 0.0F, 0.0F);
         }
      } else if (this.mc.gameSettings.thirdPersonView > 0) {
         double d5 = this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * partialTicks;
         if (this.mc.gameSettings.debugCamEnable) {
            GlStateManager.translate(0.0F, 0.0F, (float)(-d5));
         } else {
            float f5 = f3;
            float f6 = f4;
            if (this.mc.gameSettings.thirdPersonView == 2) {
               f6 += 180.0F;
            }

            double d6 = -MathHelper.sin(f5 / 180.0F * (float) Math.PI) * MathHelper.cos(f6 / 180.0F * (float) Math.PI) * d5;
            double d7 = MathHelper.cos(f5 / 180.0F * (float) Math.PI) * MathHelper.cos(f6 / 180.0F * (float) Math.PI) * d5;
            double d8 = -MathHelper.sin(f6 / 180.0F * (float) Math.PI) * d5;
            CameraClip cameraClip2 = ModuleManager.cameraClip;

            for (int i = 0; i < 8; i++) {
               float f7 = (i & 1) * 2 - 1;
               float f8 = (i >> 1 & 1) * 2 - 1;
               float f9 = (i >> 2 & 1) * 2 - 1;
               f7 *= 0.1F;
               f8 *= 0.1F;
               f9 *= 0.1F;
               MovingObjectPosition movingObjectPosition = this.mc
                  .theWorld
                  .rayTraceBlocks(
                     new Vec3(d2 + f7, d3 + f8, d4 + f9),
                     new Vec3(d2 - d6 + f7 + f9, d3 - d8 + f8, d4 - d7 + f9)
                  );
               if (movingObjectPosition != null) {
                  double d9 = movingObjectPosition.hitVec.distanceTo(new Vec3(d2, d3, d4));
                  if (!cameraClip2.isEnabled() && d9 < d5) {
                     d5 = d9;
                  }
               }
            }

            if (cameraClip2.isEnabled() && cameraClip2.cameraClip.isEnabled()) {
               d5 = cameraClip2.distance.getCurrentValue();
            }

            if (this.mc.gameSettings.thirdPersonView == 2) {
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            this.floatValue = SmoothingUtils.approachWithMinimumStep(this.floatValue, (float)d5, 8.0F, 0.001F);
            GlStateManager.rotate(f4 - f6, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f3 - f5, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, -this.floatValue);
            GlStateManager.rotate(f5 - f3, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(f6 - f4, 1.0F, 0.0F, 0.0F);
         }
      } else {
         this.floatValue = 0.0F;
         GlStateManager.translate(0.0F, 0.0F, -0.1F);
      }

      if (Reflector.reflectorConstructor4.isActive()) {
         if (!this.mc.gameSettings.debugCamEnable) {
            float f10 = f + (f3 - f) * partialTicks + 180.0F;
            float f11 = f2 + (f4 - f2) * partialTicks;
            float f12 = 0.0F;
            if (renderViewEntity instanceof EntityAnimal entityAnimal) {
               f10 = entityAnimal.prevRotationYawHead + (entityAnimal.rotationYawHead - entityAnimal.prevRotationYawHead) * partialTicks + 180.0F;
            }

            Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, partialTicks);
            Object object = Reflector.createObject5(Reflector.reflectorConstructor4, this, renderViewEntity, blockAtEntityViewpoint, partialTicks, f10, f11, f12);
            Reflector.checkObject2(object);
            f12 = Reflector.createFloat(object, Reflector.reflectorField3, f12);
            f11 = Reflector.createFloat(object, Reflector.reflectorField2, f11);
            f10 = Reflector.createFloat(object, Reflector.reflectorField, f10);
            GlStateManager.rotate(f12, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(f11, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f10, 0.0F, 1.0F, 0.0F);
         }
      } else if (!this.mc.gameSettings.debugCamEnable) {
         GlStateManager.rotate(f2 + (f4 - f2) * partialTicks, 1.0F, 0.0F, 0.0F);
         if (renderViewEntity instanceof EntityAnimal entityAnimal2) {
            GlStateManager.rotate(entityAnimal2.prevRotationYawHead + (entityAnimal2.rotationYawHead - entityAnimal2.prevRotationYawHead) * partialTicks + 180.0F, 0.0F, 1.0F, 0.0F);
         } else {
            GlStateManager.rotate(f + (f3 - f) * partialTicks + 180.0F, 0.0F, 1.0F, 0.0F);
         }
      }

      GlStateManager.translate(0.0F, -eyeHeight, 0.0F);
      d2 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * partialTicks;
      d3 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * partialTicks + eyeHeight;
      d4 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * partialTicks;
      this.cloudFog = this.mc.renderGlobal.hasCloudFog(d2, d3, d4, partialTicks);
   }

   public void activateNextShader() {
      if (OpenGlHelper.shadersSupported && this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         if (this.theShaderGroup != null) {
            this.theShaderGroup.deleteShaderGroup();
         }

         this.shaderIndex = (this.shaderIndex + 1) % (shaderResourceLocations.length + 1);
         if (this.shaderIndex != shaderCount) {
            this.loadShader(shaderResourceLocations[this.shaderIndex]);
         } else {
            this.theShaderGroup = null;
         }
      }
   }

   private void frameFinish() {
      if (this.mc.theWorld != null && Config.isShowGlErrors() && ReportThrottle.shouldReport("CheckGlErrorFrameFinish")) {
         int i = GlStateManager.glGetError();
         if (i != 0 && GlErrorThrottle.shouldReportError()) {
            String text = Config.getGlErrorString(i);
            ChatComponentText chatComponentText = new ChatComponentText(I18n.format("of.message.openglError", i, text));
            this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
         }
      }
   }

   public void renderHand(float arg1, int arg2, boolean arg3, boolean arg4, boolean arg5) {
      if (!this.debugView) {
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         float f = 0.07F;
         if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate(-(arg2 * 2 - 1) * f, 0.0F, 0.0F);
         }

         if (Config.isShaders()) {
            Shaders.applyHandDepth();
         }

         Project.gluPerspective(this.getFOVModifier(arg1, false), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate((arg2 * 2 - 1) * 0.1F, 0.0F, 0.0F);
         }

         boolean conditionMet = false;
         if (arg3) {
            GlStateManager.pushMatrix();
            this.hurtCameraEffect(arg1);
            if (this.mc.gameSettings.viewBobbing) {
               this.setupViewBobbing(arg1);
            }

            conditionMet = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
            if (!ReflectorForge.checkRenderGlobal(this.mc.renderGlobal, arg1, arg2)
               && this.mc.gameSettings.thirdPersonView == 0
               && !conditionMet
               && !this.mc.gameSettings.hideGUI
               && !this.mc.playerController.isSpectator()) {
               this.enableLightmap();
               if (Config.isShaders()) {
                  ShadersRender.renderItemFP(this.itemRenderer, arg1, arg5);
               } else {
                  this.itemRenderer.renderItemInFirstPerson(arg1);
               }

               this.disableLightmap();
            }

            GlStateManager.popMatrix();
         }

         if (!arg4) {
            return;
         }

         this.disableLightmap();
         if (this.mc.gameSettings.thirdPersonView == 0 && !conditionMet) {
            this.itemRenderer.renderOverlays(arg1);
            this.hurtCameraEffect(arg1);
         }

         if (this.mc.gameSettings.viewBobbing) {
            this.setupViewBobbing(arg1);
         }
      }
   }

   private void waitForServerThread() {
      this.serverWaitTimeCurrent = 0;
      if (!Config.isSmoothWorld() || !Config.isSingleProcessor()) {
         this.lastServerTime = 0L;
         this.lastServerTicks = 0;
      } else if (this.mc.isIntegratedServerRunning()) {
         IntegratedServer integratedServer = this.mc.getIntegratedServer();
         if (integratedServer != null) {
            if (!this.mc.isGamePaused() && !(this.mc.currentScreen instanceof GuiDownloadTerrain)) {
               if (this.serverWaitTime > 0) {
                  Lagometer.timerServer.start();
                  Config.sleep(this.serverWaitTime);
                  Lagometer.timerServer.end();
                  this.serverWaitTimeCurrent = this.serverWaitTime;
               }

               long l = System.nanoTime() / 1000000L;
               if (this.lastServerTime != 0L && this.lastServerTicks != 0) {
                  long l2 = l - this.lastServerTime;
                  if (l2 < 0L) {
                     this.lastServerTime = l;
                     l2 = 0L;
                  }

                  if (l2 >= 50L) {
                     this.lastServerTime = l;
                     int tickCounter = integratedServer.getTickCounter();
                     int i = tickCounter - this.lastServerTicks;
                     if (i < 0) {
                        this.lastServerTicks = tickCounter;
                        i = 0;
                     }

                     if (i < 1 && this.serverWaitTime < 100) {
                        this.serverWaitTime += 2;
                     }

                     if (i > 1 && this.serverWaitTime > 0) {
                        this.serverWaitTime--;
                     }

                     this.lastServerTicks = tickCounter;
                  }
               } else {
                  this.lastServerTime = l;
                  this.lastServerTicks = integratedServer.getTickCounter();
                  this.avgServerTickDiff = 1.0F;
                  this.avgServerTimeDiff = 50.0F;
               }
            } else {
               if (this.mc.currentScreen instanceof GuiDownloadTerrain) {
                  Config.sleep(20L);
               }

               this.lastServerTime = 0L;
               this.lastServerTicks = 0;
            }
         }
      }
   }

   public EntityRenderer(Minecraft mcIn, IResourceManager resourceManagerIn) {
      this.shaderIndex = shaderCount;
      this.useShader = false;
      this.frameCount = 0;
      this.mc = mcIn;
      this.resourceManager = resourceManagerIn;
      this.itemRenderer = mcIn.getItemRenderer();
      this.theMapItemRenderer = new MapItemRenderer(mcIn.getTextureManager());
      this.lightmapTexture = new DynamicTexture(16, 16);
      this.locationLightMap = mcIn.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
      this.lightmapColors = this.lightmapTexture.getTextureData();
      this.theShaderGroup = null;

      for (int index = 0; index < 32; index++) {
         for (int innerIndex = 0; innerIndex < 32; innerIndex++) {
            float f = innerIndex - 16;
            float f2 = index - 16;
            float f3 = MathHelper.sqrt_float(f * f + f2 * f2);
            this.rainXCoords[index << 5 | innerIndex] = -f2 / f3;
            this.rainYCoords[index << 5 | innerIndex] = f / f3;
         }
      }
   }

   private void loadAllVisibleChunks(Entity arg1, double arg2, ICamera arg4, boolean arg5) {
      int ofChunkUpdates2 = this.mc.gameSettings.ofChunkUpdates;
      boolean ofLazyChunkLoading2 = this.mc.gameSettings.ofLazyChunkLoading;

      try {
         this.mc.gameSettings.ofChunkUpdates = 1000;
         this.mc.gameSettings.ofLazyChunkLoading = false;
         RenderGlobal renderGlobal2 = Config.getRenderGlobal();
         int countLoadedChunks = renderGlobal2.getCountLoadedChunks();
         System.currentTimeMillis();
         Config.dbg("Loading visible chunks");
         long l = System.currentTimeMillis() + 5000L;
         int i = 0;

         boolean flag;
         do {
            flag = false;

            for (int i2 = 0; i2 < 100; i2++) {
               renderGlobal2.displayListEntitiesDirty = true;
               renderGlobal2.setupTerrain(arg1, arg2, arg4, this.frameCount++, arg5);
               if (!renderGlobal2.hasNoChunkUpdates()) {
                  flag = true;
               }

               i += renderGlobal2.getCountChunksToUpdate();

               while (!renderGlobal2.hasNoChunkUpdates()) {
                  renderGlobal2.updateChunks(System.nanoTime() + 1000000000L);
               }

               i -= renderGlobal2.getCountChunksToUpdate();
               if (!flag) {
                  break;
               }
            }

            if (renderGlobal2.getCountLoadedChunks() != countLoadedChunks) {
               flag = true;
               countLoadedChunks = renderGlobal2.getCountLoadedChunks();
            }

            if (System.currentTimeMillis() > l) {
               Config.log("Chunks loaded: " + i);
               l = System.currentTimeMillis() + 5000L;
            }
         } while (flag);

         Config.log("Chunks loaded: " + i);
         Config.log("Finished loading visible chunks");
         RenderChunk.renderChunksUpdated = 0;
      } finally {
         this.mc.gameSettings.ofChunkUpdates = ofChunkUpdates2;
         this.mc.gameSettings.ofLazyChunkLoading = ofLazyChunkLoading2;
      }
   }

   private float getFOVModifier(float partialTicks, boolean useFOVSetting) {
      if (this.debugView) {
         return 90.0F;
      }

      Entity renderViewEntity = this.mc.getRenderViewEntity();
      float f = 70.0F;
      if (useFOVSetting) {
         f = this.mc.gameSettings.fovSetting;
         if (Config.isDynamicFov()) {
            f *= this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * partialTicks;
         }
      }

      boolean flag = false;
      if (this.mc.currentScreen == null) {
         flag = GameSettings.isKeyDown(this.mc.gameSettings.ofKeyBindZoom);
      }

      if (flag) {
         if (!Config.zoomMode) {
            Config.zoomMode = true;
            Config.zoomSmoothCamera = this.mc.gameSettings.smoothCamera;
            this.mc.gameSettings.smoothCamera = true;
            this.mc.renderGlobal.displayListEntitiesDirty = true;
         }
      } else if (Config.zoomMode) {
         Config.zoomMode = false;
         this.mc.gameSettings.smoothCamera = Config.zoomSmoothCamera;
         this.mouseFilterXAxis = new MouseFilter();
         this.mouseFilterYAxis = new MouseFilter();
         this.mc.renderGlobal.displayListEntitiesDirty = true;
      }

      if (Config.zoomMode) {
         this.floatValue2 = SmoothingUtils.approach(this.floatValue2, 4.0F + this.floatValue4, 3.0F);
         f /= this.floatValue2;
         int dWheel = Mouse.getDWheel();
         if (dWheel > 1) {
            this.floatValue4 += 3.0F;
         } else if (dWheel < -1) {
            this.floatValue4 -= 3.0F;
         }

         this.floatValue4 = Math.max(-3.0F, this.floatValue4);
      } else {
         this.floatValue4 = 0.0F;
         this.floatValue2 = SmoothingUtils.approach(this.floatValue2, 1.0F, 3.0F);
         f /= this.floatValue2;
      }

      if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).getHealth() <= 0.0F) {
         float f2 = ((EntityLivingBase)renderViewEntity).deathTime + partialTicks;
         f /= (1.0F - 500.0F / (f2 + 500.0F)) * 2.0F + 1.0F;
      }

      Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, partialTicks);
      if (blockAtEntityViewpoint.getMaterial() == Material.water) {
         f = f * 60.0F / 70.0F;
      }

      return Reflector.reflectorMethod60.isActive() ? Reflector.createFloat2(Reflector.reflectorMethod60, this, renderViewEntity, blockAtEntityViewpoint, partialTicks, f) : f;
   }

   private void updateMainMenu(NovolineMainMenu arg1) {
      try {
         String text = null;
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(new Date());
         int i = calendar.get(5);
         int i2 = calendar.get(2) + 1;
         if (i == 8 && i2 == 4) {
            text = "Happy birthday, OptiFine!";
         }

         if (i == 14 && i2 == 8) {
            text = "Happy birthday, sp614x!";
         }

         if (text == null) {
            return;
         }

         Reflector.checkObject4(arg1, Reflector.reflectorField31, text);
      } catch (Throwable throwable) {
      }
   }

   private void addRainParticles() {
      float rainStrength = this.mc.theWorld.getRainStrength(1.0F);
      if (!Config.isRainFancy()) {
         rainStrength /= 2.0F;
      }

      if (rainStrength != 0.0F && Config.isRainSplash()) {
         this.random.setSeed(this.rendererUpdateCount * 312987231L);
         Entity renderViewEntity = this.mc.getRenderViewEntity();
         WorldClient theWorld2 = this.mc.theWorld;
         BlockPos blockPos2 = new BlockPos(renderViewEntity);
         double d2 = 0.0;
         double d3 = 0.0;
         double d4 = 0.0;
         int i = 0;
         int i2 = (int)(100.0F * rainStrength * rainStrength);
         if (this.mc.gameSettings.particleSetting == 1) {
            i2 >>= 1;
         } else if (this.mc.gameSettings.particleSetting == 2) {
            i2 = 0;
         }

         for (int i3 = 0; i3 < i2; i3++) {
            BlockPos precipitationHeight = theWorld2.getPrecipitationHeight(
               blockPos2.add(this.random.nextInt(10) - this.random.nextInt(10), 0, this.random.nextInt(10) - this.random.nextInt(10))
            );
            BiomeGenBase biomeGenForCoords = theWorld2.getBiomeGenForCoords(precipitationHeight);
            BlockPos blockPos3 = precipitationHeight.down();
            Block block2 = theWorld2.getBlockState(blockPos3).getBlock();
            if (precipitationHeight.getY() <= blockPos2.getY() + 10 && precipitationHeight.getY() >= blockPos2.getY() - 10 && biomeGenForCoords.canRain() && biomeGenForCoords.getFloatTemperature(precipitationHeight) >= 0.15F) {
               double d5 = this.random.nextDouble();
               double d6 = this.random.nextDouble();
               if (block2.getMaterial() == Material.lava) {
                  this.mc
                     .theWorld
                     .spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL,
                        precipitationHeight.getX() + d5,
                        precipitationHeight.getY() + 0.1F - block2.getBlockBoundsMinY(),
                        precipitationHeight.getZ() + d6,
                        0.0,
                        0.0,
                        0.0
                     );
               } else if (block2.getMaterial() != Material.air) {
                  block2.setBlockBoundsBasedOnState(theWorld2, blockPos3);
                  if (this.random.nextInt(++i) == 0) {
                     d2 = blockPos3.getX() + d5;
                     d3 = blockPos3.getY() + 0.1F + block2.getBlockBoundsMaxY() - 1.0;
                     d4 = blockPos3.getZ() + d6;
                  }

                  this.mc
                     .theWorld
                     .spawnParticle(
                        EnumParticleTypes.WATER_DROP,
                        blockPos3.getX() + d5,
                        blockPos3.getY() + 0.1F + block2.getBlockBoundsMaxY(),
                        blockPos3.getZ() + d6,
                        0.0,
                        0.0,
                        0.0
                     );
               }
            }
         }

         if (i > 0 && this.random.nextInt(3) < this.rainSoundCounter++) {
            this.rainSoundCounter = 0;
            if (d3 > blockPos2.getY() + 1 && theWorld2.getPrecipitationHeight(blockPos2).getY() > MathHelper.floor_float(blockPos2.getY())) {
               this.mc.theWorld.playSound(d2, d3, d4, "ambient.weather.rain", 0.1F, 0.5F, false);
            } else {
               this.mc.theWorld.playSound(d2, d3, d4, "ambient.weather.rain", 0.2F, 1.0F, false);
            }
         }
      }
   }

   private void frameInit() {
      GlErrorThrottle.frameStart();
      if (!this.initialized) {
         ReflectorResolver.run();
         TextureUtils.registerResourceListener();
         if (Config.getBitsOs() == 64 && Config.getBitsJre() == 32) {
            Config.setNotify64BitJava(true);
         }

         this.initialized = true;
      }

      Config.checkDisplayMode();
      World world = this.mc.theWorld;
      if (world != null) {
         if (Config.getNewRelease() != null) {
            String message = "HD_U".replace("HD_U", "HD Ultra").replace("L", "Light");
            String text2 = message + " " + Config.getNewRelease();
            ChatComponentText chatComponentText = new ChatComponentText(I18n.format("of.message.newVersion", "§n" + text2 + "§r"));
            chatComponentText.setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(net.minecraft.event.ClickEvent.Action.OPEN_URL, "https://optifine.net/downloads")));
            this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
            Config.setNewRelease((String)null);
         }

         if (Config.isNotify64BitJava()) {
            Config.setNotify64BitJava(false);
            ChatComponentText chatComponentText2 = new ChatComponentText(I18n.format("of.message.java64Bit"));
            this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText2);
         }
      }

      if (this.mc.currentScreen instanceof NovolineMainMenu) {
         this.updateMainMenu((NovolineMainMenu)this.mc.currentScreen);
      }

      if (this.updatedWorld != world) {
         RandomEntities.worldChanged(this.updatedWorld, world);
         Config.updateThreadPriorities();
         this.lastServerTime = 0L;
         this.lastServerTicks = 0;
         this.updatedWorld = world;
      }

      if (!this.setFxaaShader(Shaders.configAntialiasingLevel)) {
         Shaders.configAntialiasingLevel = 0;
      }

      if (this.mc.currentScreen != null && this.mc.currentScreen.getClass() == GuiChat.class) {
         this.mc.displayGuiScreen(new GuiChatOF((GuiChat)this.mc.currentScreen));
      }
   }

   private void updateTorchFlicker() {
      this.torchFlickerDX = (float)(this.torchFlickerDX + (Math.random() - Math.random()) * Math.random() * Math.random());
      this.torchFlickerDX = (float)(this.torchFlickerDX * 0.9);
      this.torchFlickerX = this.torchFlickerX + (this.torchFlickerDX - this.torchFlickerX) * 1.0F;
      this.lightmapUpdateNeeded = true;
   }

   public void renderWorld(float partialTicks, long finishTimeNano) {
      this.updateLightmap(partialTicks);
      if (this.mc.getRenderViewEntity() == null) {
         this.mc.setRenderViewEntity(this.mc.thePlayer);
      }

      this.getMouseOver(partialTicks);
      if (Config.isShaders()) {
         Shaders.handleMinecraft(this.mc, partialTicks);
      }

      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      this.mc.mcProfiler.startSection("center");
      if (this.mc.gameSettings.anaglyph) {
         anaglyphField = 0;
         GlStateManager.colorMask(false, true, true, false);
         this.renderWorldPass(0, partialTicks, finishTimeNano);
         anaglyphField = 1;
         GlStateManager.colorMask(true, false, false, false);
         this.renderWorldPass(1, partialTicks, finishTimeNano);
         GlStateManager.colorMask(true, true, true, false);
      } else {
         this.renderWorldPass(2, partialTicks, finishTimeNano);
      }

      this.mc.mcProfiler.endSection();
   }

   public void enableLightmap() {
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.matrixMode(5890);
      GlStateManager.loadIdentity();
      float f = 0.00390625F;
      GlStateManager.scale(f, f, f);
      GlStateManager.translate(8.0F, 8.0F, 8.0F);
      GlStateManager.matrixMode(5888);
      this.mc.getTextureManager().bindTexture(this.locationLightMap);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10242, 33071);
      GL11.glTexParameteri(3553, 10243, 33071);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      if (Config.isShaders()) {
         Shaders.enableLightmap();
      }
   }

   private void renderCloudsCheck(RenderGlobal renderGlobalIn, float partialTicks, int pass) {
      if (this.mc.gameSettings.renderDistanceChunks >= 4 && !Config.isCloudsOff() && Shaders.shouldRenderClouds(this.mc.gameSettings)) {
         this.mc.mcProfiler.endStartSection("clouds");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance * 4.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         this.setupFog(0, partialTicks);
         renderGlobalIn.renderClouds(partialTicks, pass);
         GlStateManager.disableFog();
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
         GlStateManager.matrixMode(5888);
      }
   }

   private boolean isDrawBlockOutline() {
      if (!this.drawBlockOutline) {
         return false;
      }

      Entity entity = this.mc.getRenderViewEntity();
      boolean conditionMet = entity instanceof EntityPlayer && !this.mc.gameSettings.hideGUI;
      if (conditionMet && !((EntityPlayer)entity).capabilities.allowEdit) {
         ItemStack itemStack = ((EntityPlayer)entity).getCurrentEquippedItem();
         if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockPos = this.mc.objectMouseOver.getBlockPos();
            IBlockState blockState = this.mc.theWorld.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (this.mc.playerController.getCurrentGameType() == net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
               conditionMet = ReflectorForge.checkIBlockState(blockState) && this.mc.theWorld.getTileEntity(blockPos) instanceof IInventory;
            } else {
               conditionMet = itemStack != null && (itemStack.canDestroy(block) || itemStack.canPlaceOn(block));
            }
         }
      }

      return conditionMet;
   }

   private void renderHand(float partialTicks, int xOffset) {
      this.renderHand(partialTicks, xOffset, true, true, false);
   }

   private void setupViewBobbing(float partialTicks) {
      if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         EntityPlayer entityPlayer = (EntityPlayer)this.mc.getRenderViewEntity();
         float f = entityPlayer.distanceWalkedModified - entityPlayer.prevDistanceWalkedModified;
         float f2 = -(entityPlayer.distanceWalkedModified + f * partialTicks);
         float f3 = entityPlayer.prevCameraYaw + (entityPlayer.cameraYaw - entityPlayer.prevCameraYaw) * partialTicks;
         float f4 = entityPlayer.prevCameraPitch + (entityPlayer.cameraPitch - entityPlayer.prevCameraPitch) * partialTicks;
         GlStateManager.translate(
            MathHelper.sin(f2 * (float) Math.PI) * f3 * 0.5F, -Math.abs(MathHelper.cos(f2 * (float) Math.PI) * f3), 0.0F
         );
         GlStateManager.rotate(MathHelper.sin(f2 * (float) Math.PI) * f3 * 3.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(Math.abs(MathHelper.cos(f2 * (float) Math.PI - 0.2F) * f3) * 5.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(f4, 1.0F, 0.0F, 0.0F);
      }
   }

   private void renderWorldPass(int pass, float partialTicks, long finishTimeNano) {
      boolean isShaders = Config.isShaders();
      if (isShaders) {
         Shaders.handleFloat(partialTicks);
      }

      RenderGlobal renderGlobal = this.mc.renderGlobal;
      EffectRenderer effectRenderer = this.mc.effectRenderer;
      boolean isDrawBlockOutline = this.isDrawBlockOutline();
      GlStateManager.enableCull();
      this.mc.mcProfiler.endStartSection("clear");
      if (isShaders) {
         Shaders.pushEntity(this.mc.displayWidth, this.mc.displayHeight);
      } else {
         GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      }

      this.updateFogColor(partialTicks);
      GlStateManager.clear(16640);
      if (isShaders) {
         Shaders.clearRenderBuffer();
      }

      this.mc.mcProfiler.endStartSection("camera");
      this.setupCameraTransform(partialTicks, pass);
      if (isShaders) {
         Shaders.setCamera(partialTicks);
      }

      ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, this.mc.gameSettings.thirdPersonView == 2);
      this.mc.mcProfiler.endStartSection("frustum");
      ClippingHelper clippingHelper = ClippingHelperImpl.getInstance();
      this.mc.mcProfiler.endStartSection("culling");
      clippingHelper.disabled = Config.isShaders() && !Shaders.isFrustumCulling();
      Frustum frustum2 = new Frustum(clippingHelper);
      Entity entity = this.mc.getRenderViewEntity();
      double xCoordinate = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
      double yCoordinate = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
      double zCoordinate = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
      if (isShaders) {
         ShadersRender.setFrustrumPosition(frustum2, xCoordinate, yCoordinate, zCoordinate);
      } else {
         frustum2.setPosition(xCoordinate, yCoordinate, zCoordinate);
      }

      if ((Config.isSkyEnabled() || Config.isSunMoonEnabled() || Config.isStarsEnabled()) && !Shaders.isShadowPass) {
         this.setupFog(-1, partialTicks);
         this.mc.mcProfiler.endStartSection("sky");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
         GlStateManager.matrixMode(5888);
         if (isShaders) {
            Shaders.beginSky();
         }

         renderGlobal.renderSky(partialTicks, pass);
         if (isShaders) {
            Shaders.endSky();
         }

         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
         GlStateManager.matrixMode(5888);
      } else {
         GlStateManager.disableBlend();
      }

      this.setupFog(0, partialTicks);
      GlStateManager.shadeModel(7425);
      if (entity.posY + entity.getEyeHeight() < 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F) {
         this.renderCloudsCheck(renderGlobal, partialTicks, pass);
      }

      this.mc.mcProfiler.endStartSection("prepareterrain");
      this.setupFog(0, partialTicks);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      RenderHelper.disableStandardItemLighting();
      this.mc.mcProfiler.endStartSection("terrain_setup");
      this.checkLoadVisibleChunks(entity, partialTicks, frustum2, this.mc.thePlayer.isSpectator());
      if (isShaders) {
         ShadersRender.setupTerrain(renderGlobal, entity, partialTicks, frustum2, this.frameCount++, this.mc.thePlayer.isSpectator());
      } else {
         renderGlobal.setupTerrain(entity, partialTicks, frustum2, this.frameCount++, this.mc.thePlayer.isSpectator());
      }

      if (pass == 0 || pass == 2) {
         this.mc.mcProfiler.endStartSection("updatechunks");
         Lagometer.timerChunkUpload.start();
         this.mc.renderGlobal.updateChunks(finishTimeNano);
         Lagometer.timerChunkUpload.end();
      }

      this.mc.mcProfiler.endStartSection("terrain");
      Lagometer.timerTerrain.start();
      if (this.mc.gameSettings.ofSmoothFps && pass > 0) {
         this.mc.mcProfiler.endStartSection("finish");
         GL11.glFinish();
         this.mc.mcProfiler.endStartSection("terrain");
      }

      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.disableAlpha();
      if (isShaders) {
         ShadersRender.beginTerrainSolid();
      }

      renderGlobal.getInt(EnumWorldBlockLayer.SOLID, partialTicks, entity);
      GlStateManager.enableAlpha();
      if (isShaders) {
         ShadersRender.beginTerrainCutoutMipped();
      }

      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).noop(this.mc.gameSettings.mipmapLevels > 0);
      renderGlobal.getInt(EnumWorldBlockLayer.CUTOUT_MIPPED, partialTicks, entity);
      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).noop(false);
      if (isShaders) {
         ShadersRender.beginTerrainCutout();
      }

      renderGlobal.getInt(EnumWorldBlockLayer.CUTOUT, partialTicks, entity);
      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
      if (isShaders) {
         ShadersRender.endTerrain();
      }

      Lagometer.timerTerrain.end();
      GlStateManager.shadeModel(7424);
      GlStateManager.alphaFunc(516, 0.1F);
      if (!this.debugView) {
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         RenderHelper.enableStandardItemLighting();
         this.mc.mcProfiler.endStartSection("entities");
         if (Reflector.reflectorMethod73.isActive()) {
            Reflector.handleReflectorMethod(Reflector.reflectorMethod73, 0);
         }

         renderGlobal.renderEntities(entity, frustum2, partialTicks);
         if (Reflector.reflectorMethod73.isActive()) {
            Reflector.handleReflectorMethod(Reflector.reflectorMethod73, -1);
         }

         RenderHelper.disableStandardItemLighting();
         this.disableLightmap();
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         if (this.mc.objectMouseOver != null && entity.isInsideOfMaterial(Material.water) && isDrawBlockOutline) {
            EntityPlayer entityPlayer = (EntityPlayer)entity;
            GlStateManager.disableAlpha();
            this.mc.mcProfiler.endStartSection("outline");
            renderGlobal.handleEntityPlayer(entityPlayer, this.mc.objectMouseOver, partialTicks);
            GlStateManager.enableAlpha();
         }
      }

      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      if (isDrawBlockOutline && this.mc.objectMouseOver != null && !entity.isInsideOfMaterial(Material.water)) {
         EntityPlayer entityPlayer2 = (EntityPlayer)entity;
         GlStateManager.disableAlpha();
         this.mc.mcProfiler.endStartSection("outline");
         if ((
               !Reflector.reflectorMethod64.isActive()
                  || !Reflector.checkReflectorMethod(
                     Reflector.reflectorMethod64, renderGlobal, entityPlayer2, this.mc.objectMouseOver, 0, entityPlayer2.getHeldItem(), partialTicks
                  )
            )
            && !this.mc.gameSettings.hideGUI) {
            renderGlobal.handleEntityPlayer(entityPlayer2, this.mc.objectMouseOver, partialTicks);
         }

         GlStateManager.enableAlpha();
      }

      if (!renderGlobal.damagedBlocks.isEmpty()) {
         this.mc.mcProfiler.endStartSection("destroyProgress");
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).noop(false);
         renderGlobal.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getWorldRenderer(), entity, partialTicks);
         this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
         GlStateManager.disableBlend();
      }

      RenderUtils.applyBlendFunc();
      GlStateManager.disableBlend();
      if (!this.debugView) {
         this.enableLightmap();
         this.mc.mcProfiler.endStartSection("litParticles");
         if (isShaders) {
            Shaders.beginLitParticles();
         }

         effectRenderer.renderLitParticles(entity, partialTicks);
         RenderHelper.disableStandardItemLighting();
         this.setupFog(0, partialTicks);
         this.mc.mcProfiler.endStartSection("particles");
         if (isShaders) {
            Shaders.beginParticles();
         }

         effectRenderer.renderParticles(entity, partialTicks);
         if (isShaders) {
            Shaders.endParticles();
         }

         this.disableLightmap();
      }

      GlStateManager.depthMask(false);
      if (Config.isShaders()) {
         GlStateManager.depthMask(Shaders.isRainDepth());
      }

      GlStateManager.enableCull();
      this.mc.mcProfiler.endStartSection("weather");
      if (isShaders) {
         Shaders.beginWeather();
      }

      this.renderRainSnow(partialTicks);
      if (isShaders) {
         Shaders.endWeather();
      }

      GlStateManager.depthMask(true);
      renderGlobal.renderWorldBorder(entity, partialTicks);
      if (isShaders) {
         ShadersRender.renderHand0(this, partialTicks, pass);
         Shaders.preWater();
      }

      GlStateManager.disableBlend();
      GlStateManager.enableCull();
      RenderUtils.applyBlendFunc();
      GlStateManager.alphaFunc(516, 0.1F);
      this.setupFog(0, partialTicks);
      GlStateManager.enableBlend();
      GlStateManager.depthMask(false);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      GlStateManager.shadeModel(7425);
      this.mc.mcProfiler.endStartSection("translucent");
      if (isShaders) {
         Shaders.beginWater();
      }

      renderGlobal.getInt(EnumWorldBlockLayer.TRANSLUCENT, partialTicks, entity);
      if (isShaders) {
         Shaders.endWater();
      }

      if (Reflector.reflectorMethod73.isActive() && !this.debugView) {
         RenderHelper.enableStandardItemLighting();
         this.mc.mcProfiler.endStartSection("entities");
         Reflector.handleReflectorMethod(Reflector.reflectorMethod73, 1);
         this.mc.renderGlobal.renderEntities(entity, frustum2, partialTicks);
         RenderUtils.applyBlendFunc();
         Reflector.handleReflectorMethod(Reflector.reflectorMethod73, -1);
         RenderHelper.disableStandardItemLighting();
      }

      GlStateManager.shadeModel(7424);
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.disableFog();
      if (entity.posY + entity.getEyeHeight() >= 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F) {
         this.mc.mcProfiler.endStartSection("aboveClouds");
         this.renderCloudsCheck(renderGlobal, partialTicks, pass);
      }

      if (Reflector.reflectorMethod53.isActive()) {
         this.mc.mcProfiler.endStartSection("forge_render_last");
         Reflector.handleReflectorMethod(Reflector.reflectorMethod53, renderGlobal, partialTicks);
      }

      this.mc.mcProfiler.endStartSection("hand");
      if (this.mc.thePlayer != null) {
         Novoline.getNovoline().getBus().post(new RenderWorldEvent(partialTicks));
      }

      if (this.renderHand && !Shaders.isShadowPass) {
         if (isShaders) {
            ShadersRender.renderHand1(this, partialTicks, pass);
            Shaders.renderCompositeFinal();
         }

         GlStateManager.clear(256);
         if (isShaders) {
            ShadersRender.renderFPOverlay(this, partialTicks, pass);
         } else {
            this.renderHand(partialTicks, pass);
         }

         this.renderWorldDirections(partialTicks);
      }

      if (isShaders) {
         Shaders.endRender();
      }
   }
}
