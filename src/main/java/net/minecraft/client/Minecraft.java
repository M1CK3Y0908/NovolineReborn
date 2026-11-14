package net.minecraft.client;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.sentry.Attachment;
import io.sentry.Breadcrumb;
import io.sentry.Sentry;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiStreamUnavailable;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.stream.IStream;
import net.minecraft.client.stream.NullStream;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IAchievementFormatter;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import ru.fals3r.aal.API;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.event.DeviceButtonDownEvent;
import wtf.novoline.event.LoadWorldEvent;
import wtf.novoline.event.RunTickEvent;
import wtf.novoline.event.DeviceButtonDownEvent.MouseType;
import wtf.novoline.gui.NovolineMainMenu;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.world.FastPlace;
import wtf.novoline.utils.render.RenderUtils;

public class Minecraft implements IThreadListener, IPlayerUsage {
   public MouseHelper mouseHelper;
   public int rightClickDelayTimer;
   private CrashReport crashReporter;
   private boolean enableGLErrorChecking = true;
   private boolean flag2 = false;
   public Timer timer = new Timer(20.0F);
   public GuiAchievement guiAchievement;
   private PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("client", this, MinecraftServer.getCurrentTimeMillis());
   long systemTime = getSystemTime();
   public final FrameTimer frameTimer = new FrameTimer();
   long startNanoTime = System.nanoTime();
   public final Profiler mcProfiler = new Profiler();
   private int joinPlayerCounter;
   private ResourceLocation mojangLogo;
   private long debugCrashKeyPressTime = -1L;
   private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
   public int leftClickCounter;
   int fpsCounter;
   private final List<IResourcePack> defaultResourcePacks = Lists.newArrayList();
   private final Queue<FutureTask<?>> scheduledTasks = Queues.newArrayDeque();
   private RenderItem renderItem;
   private TextureMap textureMapBlocks;
   private long initialDebugCrashKeyPressTime = 0L;
   private final Thread mcThread = Thread.currentThread();
   private MusicTicker mcMusicTicker;
   volatile boolean running = true;
   private Entity renderViewEntity;
   public String debug = "";
   public boolean flag3 = false;
   private boolean isGamePaused;
   public FontRenderer fontRendererObj;
   private static final Logger logger = LogManager.getLogger();
   public boolean flag4 = false;
   private ItemRenderer itemRenderer;
   public boolean flag5 = false;
   public boolean renderChunksMany = true;
   long debugUpdateTime = getSystemTime();
   private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
   long prevFrameTime = -1L;
   private String debugProfilerName = "root";
   public final File mcDataDir;
   private ISaveFormat saveLoader;
   private final File fileAssets;
   public PlayerControllerMP playerController;
   private ResourcePackRepository mcResourcePackRepository;
   private boolean hasCrashed;
   public EffectRenderer effectRenderer;
   private final File fileResourcepacks;
   private IStream iStream;
   public GuiScreen currentScreen;
   private final String launchedVersion;
   private final PropertyMap propertyMap;
   private final PropertyMap profileProperties;
   private static int debugFPS;
   private final DefaultResourcePack mcDefaultResourcePack;
   private final Proxy proxy;
   private final MinecraftSessionService sessionService;
   public Session session;
   private final boolean isDemo;
   public FontRenderer standardGalacticFontRenderer;
   public static final boolean isRunningOnMac = Util.getOSType() == net.minecraft.util.Util.EnumOS.OSX;
   private TextureManager renderEngine;
   public WorldClient theWorld;
   public int displayWidth;
   public Entity pointedEntity;
   public int displayHeight;
   private int tempDisplayWidth;
   public LoadingScreenRenderer loadingScreen;
   private SoundHandler mcSoundHandler;
   private static Entity[] entityArray;
   public static byte[] memoryReserve = new byte[10485760];
   private static Minecraft theMinecraft;
   private int tempDisplayHeight;
   private boolean fullscreen;
   private static final List<DisplayMode> macDisplayModes = Lists.newArrayList(new DisplayMode[]{new DisplayMode(2560, 1600), new DisplayMode(2880, 1800)});
   private boolean integratedServerIsRunning;
   private IReloadableResourceManager mcResourceManager;
   private NetworkManager myNetworkManager;
   private LanguageManager mcLanguageManager;
   private BlockRendererDispatcher blockRenderDispatcher;
   public MovingObjectPosition objectMouseOver;
   private SkinManager skinManager;
   public EntityRenderer entityRenderer;
   private final boolean jvm64bit;
   private ModelManager modelManager;
   public boolean skipRenderWorld;
   private ServerData currentServerData;
   private IntegratedServer theIntegratedServer;
   public EntityPlayerSP thePlayer;
   public boolean inGameHasFocus;
   private String serverName;
   public GameSettings gameSettings;
   public GuiIngame ingameGUI;
   private int serverPort;
   private RenderManager renderManager;
   public RenderGlobal renderGlobal;
   private Framebuffer framebufferMc;

   public FrameTimer getFrameTimer() {
      return this.frameTimer;
   }

   public void clickMouse() {
      if (this.leftClickCounter <= 0) {
         this.thePlayer.swingItemNoPacket();
         if (this.objectMouseOver == null) {
            logger.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.playerController.isNotCreative()) {
               this.leftClickCounter = 10;
            }
         } else {
            switch (this.objectMouseOver.typeOfHit) {
               case ENTITY:
                  this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
                  break;
               case BLOCK:
                  BlockPos blockPos2 = this.objectMouseOver.getBlockPos();
                  if (this.theWorld.getBlockState(blockPos2).getBlock().getMaterial() != Material.air) {
                     this.playerController.clickBlock(blockPos2, this.objectMouseOver.sideHit);
                     break;
                  }
               case MISS:
               default:
                  if (this.playerController.isNotCreative()) {
                     this.leftClickCounter = 10;
                  }
            }
         }
      }
   }

   public static long getSystemTime() {
      return Sys.getTime() * 1000L / Sys.getTimerResolution();
   }

   @Override
   public void addServerStatsToSnooper(PlayerUsageSnooper snooper) {
      snooper.addClientStat("fps", debugFPS);
      snooper.addClientStat("vsync_enabled", this.gameSettings.enableVsync);
      snooper.addClientStat("display_frequency", Display.getDisplayMode().getFrequency());
      snooper.addClientStat("display_type", this.fullscreen ? "fullscreen" : "windowed");
      snooper.addClientStat("run_time", (MinecraftServer.getCurrentTimeMillis() - snooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
      snooper.addClientStat("current_action", this.getCurrentAction());
      String text2 = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
      snooper.addClientStat("endianness", text2);
      snooper.addClientStat("resource_packs", this.mcResourcePackRepository.getRepositoryEntries().size());
      int i = 0;

      for (net.minecraft.client.resources.ResourcePackRepository.Entry entry : this.mcResourcePackRepository.getRepositoryEntries()) {
         snooper.addClientStat("resource_pack[" + i++ + "]", entry.getResourcePackName());
      }

      if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null) {
         snooper.addClientStat("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
      }
   }

   private void update() {
      if (this.mcProfiler.profilingEnabled) {
         List profilingData = this.mcProfiler.getProfilingData(this.debugProfilerName);
         net.minecraft.profiler.Profiler.Result result = (net.minecraft.profiler.Profiler.Result)profilingData.remove(0);
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.enableColorMaterial();
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0, this.displayWidth, this.displayHeight, 0.0, 1000.0, 3000.0);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, 0.0F, -2000.0F);
         GL11.glLineWidth(1.0F);
         GlStateManager.disableTexture2D();
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         int i = this.displayWidth - 160 - 10;
         int i2 = this.displayHeight - 320;
         GlStateManager.enableBlend();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
         worldRenderer.pos(i - 160 * 1.1F, i2 - 160 * 0.6F - 16.0F, 0.0).color(200, 0, 0, 0).endVertex();
         worldRenderer.pos(i - 160 * 1.1F, i2 + 320, 0.0).color(200, 0, 0, 0).endVertex();
         worldRenderer.pos(i + 160 * 1.1F, i2 + 320, 0.0).color(200, 0, 0, 0).endVertex();
         worldRenderer.pos(i + 160 * 1.1F, i2 - 160 * 0.6F - 16.0F, 0.0).color(200, 0, 0, 0).endVertex();
         instance.draw();
         GlStateManager.disableBlend();
         double d2 = 0.0;

         for (int i3 = 0; i3 < profilingData.size(); i3++) {
            net.minecraft.profiler.Profiler.Result result2 = (net.minecraft.profiler.Profiler.Result)profilingData.get(i3);
            int i4 = MathHelper.floor_double(result2.usePercentage / 4.0) + 1;
            worldRenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
            int color2 = result2.getColor();
            int i5 = color2 >> 16 & 0xFF;
            int i6 = color2 >> 8 & 0xFF;
            int i7 = color2 & 0xFF;
            worldRenderer.pos(i, i2, 0.0).color(i5, i6, i7, 255).endVertex();

            for (int i8 = i4; i8 >= 0; i8--) {
               float f = (float)((d2 + result2.usePercentage * i8 / i4) * Math.PI * 2.0 / 100.0);
               float f2 = MathHelper.sin(f) * 160;
               float f3 = MathHelper.cos(f) * 160 * 0.5F;
               worldRenderer.pos(i + f2, i2 - f3, 0.0).color(i5, i6, i7, 255).endVertex();
            }

            instance.draw();
            worldRenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for (int i9 = i4; i9 >= 0; i9--) {
               float f4 = (float)((d2 + result2.usePercentage * i9 / i4) * Math.PI * 2.0 / 100.0);
               float f5 = MathHelper.sin(f4) * 160;
               float f6 = MathHelper.cos(f4) * 160 * 0.5F;
               worldRenderer.pos(i + f5, i2 - f6, 0.0).color(i5 >> 1, i6 >> 1, i7 >> 1, 255).endVertex();
               worldRenderer.pos(i + f5, i2 - f6 + 10.0F, 0.0).color(i5 >> 1, i6 >> 1, i7 >> 1, 255).endVertex();
            }

            instance.draw();
            d2 += result2.usePercentage;
         }

         DecimalFormat decimalFormat = new DecimalFormat("##0.00");
         GlStateManager.enableTexture2D();
         String text2 = "";
         if (!result.sectionName.equals("unspecified")) {
            text2 = "[0] ";
         }

         if (result.sectionName.length() == 0) {
            text2 = text2 + "ROOT ";
         } else {
            text2 = text2 + result.sectionName + " ";
         }

         this.fontRendererObj.drawStringWithShadow(text2, i - 160, i2 - 80 - 16, 16777215);
         this.fontRendererObj
            .drawStringWithShadow(
               text2 = decimalFormat.format(result.totalUsePercentage) + "%", i + 160 - this.fontRendererObj.getInt3(text2), i2 - 80 - 16, 16777215
            );

         for (int i10 = 0; i10 < profilingData.size(); i10++) {
            net.minecraft.profiler.Profiler.Result result3 = (net.minecraft.profiler.Profiler.Result)profilingData.get(i10);
            String text3;
            if (result3.sectionName.equals("unspecified")) {
               text3 = "[?] ";
            } else {
               text3 = "[" + (i10 + 1) + "] ";
            }

            text3 = text3 + result3.sectionName;
            this.fontRendererObj.drawStringWithShadow(text3, i - 160, i2 + 80 + i10 * 8 + 20, result3.getColor());
            this.fontRendererObj
               .drawStringWithShadow(
                  text3 = decimalFormat.format(result3.usePercentage) + "%",
                  i + 160 - 50 - this.fontRendererObj.getInt3(text3),
                  i2 + 80 + i10 * 8 + 20,
                  result3.getColor()
               );
            this.fontRendererObj
               .drawStringWithShadow(
                  text3 = decimalFormat.format(result3.totalUsePercentage) + "%",
                  i + 160 - this.fontRendererObj.getInt3(text3),
                  i2 + 80 + i10 * 8 + 20,
                  result3.getColor()
               );
         }
      }
   }

   public static boolean isGuiEnabled() {
      return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
   }

   private void updateDisplayMode() throws LWJGLException {
      HashSet hashSet = Sets.newHashSet();
      Collections.addAll(hashSet, Display.getAvailableDisplayModes());
      DisplayMode desktopDisplayMode = Display.getDesktopDisplayMode();
      if (!hashSet.contains(desktopDisplayMode) && Util.getOSType() == net.minecraft.util.Util.EnumOS.OSX) {
         for (DisplayMode displayMode2 : macDisplayModes) {
            boolean flag = true;

            for (DisplayMode displayMode3 : (Iterable<DisplayMode>) hashSet) {
               if (displayMode3.getBitsPerPixel() == 32 && displayMode3.getWidth() == displayMode2.getWidth() && displayMode3.getHeight() == displayMode2.getHeight()) {
                  flag = false;
                  break;
               }
            }

            if (!flag) {
               for (DisplayMode displayMode4 : (Iterable<DisplayMode>) hashSet) {
                  if (displayMode4.getBitsPerPixel() == 32 && displayMode4.getWidth() == displayMode2.getWidth() / 2 && displayMode4.getHeight() == displayMode2.getHeight() / 2) {
                     desktopDisplayMode = displayMode4;
                     break;
                  }
               }
            }
         }
      }

      Display.setDisplayMode(desktopDisplayMode);
      this.displayWidth = desktopDisplayMode.getWidth();
      this.displayHeight = desktopDisplayMode.getHeight();
   }

   public Entity getRenderViewEntity() {
      return this.renderViewEntity;
   }

   public TextureMap getTextureMapBlocks() {
      return this.textureMapBlocks;
   }

   @Override
   public boolean isCallingFromMinecraftThread() {
      return Thread.currentThread() == this.mcThread;
   }

   static {
      handleArray(new Entity[1]);
   }

   public void refreshLanguage() {
      this.flag2 = false;
   }

   private void checkGLError(String message) {
      if (this.enableGLErrorChecking) {
         int i = GL11.glGetError();
         if (i != 0) {
            String text2 = GLU.gluErrorString(i);
            logger.error("########## GL ERROR ##########");
            logger.error("@ " + message);
            logger.error(i + ": " + text2);
         }
      }
   }

   public List<IResourcePack> getDefaultResourcePacks() {
      return this.defaultResourcePacks;
   }

   public ResourcePackRepository getResourcePackRepository() {
      return this.mcResourcePackRepository;
   }

   public IResourceManager getResourceManager() {
      return this.mcResourceManager;
   }

   public int getLimitFramerate() {
      return this.theWorld == null && this.currentScreen != null ? 240 : this.gameSettings.limitFramerate;
   }

   private void handleString(String text2, TextureManager textureManager) {
      ScaledResolution scaledResolution = new ScaledResolution(this);
      int scaleFactor = scaledResolution.getScaleFactor();
      Framebuffer framebuffer = new Framebuffer(scaledResolution.getScaledWidth() * scaleFactor, scaledResolution.getScaledHeight() * scaleFactor, true);
      framebuffer.bindFramebuffer(false);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 0.0, 1000.0, 3000.0);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translate(0.0F, 0.0F, -2000.0F);
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      GlStateManager.disableDepth();
      float f = (float)scaledResolution.getScaledWidth_double() / 2.0F;
      float f2 = (float)scaledResolution.getScaledHeight_double() / 2.0F;
      RenderUtils.drawTextureWithAlpha(f - 32.0F, f2 - 32.0F - 10.0F, 64.0F, 64.0F, 1.0F, new ResourceLocation("novoline", "images/novoline.png"));
      Fonts.rubikSemiboldSize24.drawString(text2, f - Fonts.rubikSemiboldSize24.getStringWidth(text2) / 2.0F, f2 + 26.0F, -16777216);
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      framebuffer.unbindFramebuffer();
      framebuffer.framebufferRender(scaledResolution.getScaledWidth() * scaleFactor, scaledResolution.getScaledHeight() * scaleFactor);
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      this.updateDisplay();
   }

   public static boolean isAmbientOcclusionEnabled() {
      return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0;
   }

   private void showAuth() {
      try {
         this.iStream = new TwitchStream(this, (Property)Iterables.getFirst(this.propertyMap.get("twitch_access_token"), null));
      } catch (Throwable throwable) {
         this.iStream = new NullStream(throwable);
         logger.error("Couldn't initialize twitch stream");
      }
   }

   private void rightClickMouseLegacy() {
      if (!this.playerController.getIsHittingBlock()) {
         this.rightClickDelayTimer = 4;
         FastPlace fastPlace2 = ModuleManager.fastPlace;
         if (fastPlace2.isEnabled() && fastPlace2.hasEffect(this.thePlayer.getHeldItem())) {
            this.rightClickDelayTimer = (int)fastPlace2.getFastPlace().getCurrentValue();
         }

         boolean flag = true;
         ItemStack currentItem2 = this.thePlayer.inventory.getCurrentItem();
         if (this.objectMouseOver == null) {
            logger.warn("Null returned as 'hitResult', this shouldn't happen!");
         } else {
            switch (this.objectMouseOver.typeOfHit) {
               case ENTITY:
                  if (this.playerController.isPlayerRightClickingOnEntity(this.thePlayer, this.objectMouseOver.entityHit, this.objectMouseOver)) {
                     flag = false;
                  } else if (this.playerController.interactWithEntitySendPacket(this.thePlayer, this.objectMouseOver.entityHit)) {
                     flag = false;
                  }
                  break;
               case BLOCK:
                  BlockPos blockPos2 = this.objectMouseOver.getBlockPos();
                  if (this.theWorld.getBlockState(blockPos2).getBlock().getMaterial() != Material.air) {
                     int i = currentItem2 != null ? currentItem2.stackSize : 0;
                     if (this.playerController
                        .performRightClick(this.thePlayer, this.theWorld, currentItem2, blockPos2, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec)) {
                        flag = false;
                        this.thePlayer.swingItemNoPacket();
                     }

                     if (currentItem2 == null) {
                        return;
                     }

                     if (currentItem2.stackSize == 0) {
                        this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                     } else if (currentItem2.stackSize != i || this.playerController.isInCreativeMode()) {
                        this.entityRenderer.itemRenderer.resetEquippedProgress();
                     }
                  }
            }
         }

         if (flag) {
            ItemStack currentItem3 = this.thePlayer.inventory.getCurrentItem();
            if (currentItem3 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, currentItem3)) {
               this.entityRenderer.itemRenderer.resetEquippedProgress2();
            }
         }
      }
   }

   private String getCurrentAction() {
      return this.theIntegratedServer != null
         ? (this.theIntegratedServer.getPublic() ? "hosting_lan" : "singleplayer")
         : (this.currentServerData != null ? (this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer") : "out_of_game");
   }

   public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash) {
      theCrash.getCategory().addCrashSectionCallable("Launched Version", new Callable<String>() {
         public String call() throws Exception {
            return Minecraft.this.launchedVersion;
         }
      });
      theCrash.getCategory().addCrashSectionCallable("LWJGL", new Callable<String>() {
         public String call() {
            return Sys.getVersion();
         }
      });
      theCrash.getCategory().addCrashSectionCallable("OpenGL", new Callable<String>() {
         public String call() {
            return GL11.glGetString(7937) + " GL version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936);
         }
      });
      theCrash.getCategory().addCrashSectionCallable("GL Caps", new Callable<String>() {
         public String call() {
            return OpenGlHelper.getLogText();
         }
      });
      theCrash.getCategory().addCrashSectionCallable("Using VBOs", new Callable<String>() {
         public String call() {
            return Minecraft.this.gameSettings.useVbo ? "Yes" : "No";
         }
      });
      theCrash.getCategory()
         .addCrashSectionCallable(
            "Is Modded",
            new Callable<String>() {
               public String call() throws Exception {
                  String text = ClientBrandRetriever.getClientModName();
                  return !text.equals("vanilla")
                     ? "Definitely; Client brand changed to '" + text + "'"
                     : (
                        Minecraft.class.getSigners() == null
                           ? "Very likely; Jar signature invalidated"
                           : "Probably not. Jar signature remains and client brand is untouched."
                     );
               }
            }
         );
      theCrash.getCategory().addCrashSectionCallable("Type", new Callable<String>() {
         public String call() throws Exception {
            return "Client (map_client.txt)";
         }
      });
      theCrash.getCategory().addCrashSectionCallable("Resource Packs", new Callable<String>() {
         public String call() throws Exception {
            StringBuilder stringBuilder = new StringBuilder();

            for (String text2 : Minecraft.this.gameSettings.resourcePacks) {
               if (stringBuilder.length() > 0) {
                  stringBuilder.append(", ");
               }

               stringBuilder.append(text2);
               if (Minecraft.this.gameSettings.incompatibleResourcePacks.contains(text2)) {
                  stringBuilder.append(" (incompatible)");
               }
            }

            return stringBuilder.toString();
         }
      });
      theCrash.getCategory().addCrashSectionCallable("Current Language", new Callable<String>() {
         public String call() throws Exception {
            return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
         }
      });
      theCrash.getCategory().addCrashSectionCallable("Profiler Position", new Callable<String>() {
         public String call() throws Exception {
            return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
         }
      });
      theCrash.getCategory().addCrashSectionCallable("CPU", new Callable<String>() {
         public String call() {
            return OpenGlHelper.getCpu();
         }
      });
      if (this.theWorld != null) {
         this.theWorld.addWorldInfoToCrashReport(theCrash);
      }

      return theCrash;
   }

   public final boolean isDemo() {
      return this.isDemo;
   }

   public Minecraft(GameConfiguration gameConfig) {
      theMinecraft = this;
      this.mcDataDir = gameConfig.folderInfo.mcDataDir;
      this.fileAssets = gameConfig.folderInfo.assetsDir;
      this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
      this.launchedVersion = gameConfig.gameInfo.version;
      this.propertyMap = gameConfig.userInfo.userProperties;
      this.profileProperties = gameConfig.userInfo.profileProperties;
      this.mcDefaultResourcePack = new DefaultResourcePack(
         new ResourceIndex(gameConfig.folderInfo.assetsDir, gameConfig.folderInfo.assetIndex).getResourceMap()
      );
      this.proxy = gameConfig.userInfo.proxy == null ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
      this.sessionService = new YggdrasilAuthenticationService(gameConfig.userInfo.proxy, UUID.randomUUID().toString()).createMinecraftSessionService();
      this.session = gameConfig.userInfo.session;
      logger.info("Setting user: " + this.session.getUsername());
      logger.info("(Session ID is " + this.session.getSessionID() + ")");
      this.isDemo = gameConfig.gameInfo.isDemo;
      this.displayWidth = gameConfig.displayInfo.width > 0 ? gameConfig.displayInfo.width : 1;
      this.displayHeight = gameConfig.displayInfo.height > 0 ? gameConfig.displayInfo.height : 1;
      this.tempDisplayWidth = gameConfig.displayInfo.width;
      this.tempDisplayHeight = gameConfig.displayInfo.height;
      this.fullscreen = gameConfig.displayInfo.fullscreen;
      this.jvm64bit = isJvm64bit();
      this.theIntegratedServer = new IntegratedServer(this);
      if (gameConfig.serverInfo.serverName != null) {
         this.serverName = gameConfig.serverInfo.serverName;
         this.serverPort = gameConfig.serverInfo.serverPort;
      }

      ImageIO.setUseCache(false);
      Bootstrap.register();
   }

   public boolean isFramerateLimitBelowMax() {
      return this.getLimitFramerate() < net.minecraft.client.settings.GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
   }

   public void setIngameNotInFocus() {
      if (this.inGameHasFocus) {
         KeyBinding.unPressAllKeys();
         this.inGameHasFocus = false;
         this.mouseHelper.ungrabMouseCursor();
      }
   }

   public boolean isJava64bit() {
      return this.jvm64bit;
   }

   public static int getGLMaximumTextureSize() {
      for (int i = 16384; i > 0; i >>= 1) {
         GL11.glTexImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, (ByteBuffer)null);
         if (GL11.glGetTexLevelParameteri(32868, 0, 4096) != 0) {
            return i;
         }
      }

      return -1;
   }

   private void resize(int width, int height) {
      this.displayWidth = Math.max(1, width);
      this.displayHeight = Math.max(1, height);
      if (this.currentScreen != null) {
         ScaledResolution scaledResolution = new ScaledResolution(this);
         this.currentScreen.onResize(this, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
      }

      this.loadingScreen = new LoadingScreenRenderer(this);
      this.updateFramebufferSize();
      Novoline.framebufferManager.resizeFreeFramebuffers();
   }

   public static Minecraft getMinecraft() {
      return theMinecraft;
   }

   private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException {
      BufferedImage bufferedImage = ImageIO.read(imageStream);
      int[] rgb = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), (int[])null, 0, bufferedImage.getWidth());
      ByteBuffer byteBuffer = ByteBuffer.allocate(4 * rgb.length);

      for (int i : rgb) {
         byteBuffer.putInt(i << 8 | i >> 24 & 0xFF);
      }

      byteBuffer.flip();
      return byteBuffer;
   }

   protected void checkWindowResize() {
      if (!this.fullscreen && Display.wasResized()) {
         int displayWidth2 = this.displayWidth;
         int displayHeight2 = this.displayHeight;
         this.displayWidth = Display.getWidth();
         this.displayHeight = Display.getHeight();
         if (this.displayWidth != displayWidth2 || this.displayHeight != displayHeight2) {
            if (this.displayWidth <= 0) {
               this.displayWidth = 1;
            }

            if (this.displayHeight <= 0) {
               this.displayHeight = 1;
            }

            this.resize(this.displayWidth, this.displayHeight);
         }
      }
   }

   public void refreshResources() {
      ArrayList arrayList = Lists.newArrayList(this.defaultResourcePacks);

      for (net.minecraft.client.resources.ResourcePackRepository.Entry entry : this.mcResourcePackRepository.getRepositoryEntries()) {
         arrayList.add(entry.getResourcePack());
      }

      if (this.mcResourcePackRepository.getResourcePackInstance() != null) {
         arrayList.add(this.mcResourcePackRepository.getResourcePackInstance());
      }

      try {
         this.mcResourceManager.reloadResources(arrayList);
      } catch (RuntimeException runtimeException) {
         logger.info("Caught error stitching, removing all assigned resourcepacks", runtimeException);
         arrayList.clear();
         arrayList.addAll(this.defaultResourcePacks);
         this.mcResourcePackRepository.setRepositories(Collections.emptyList());
         this.mcResourceManager.reloadResources(arrayList);
         this.gameSettings.resourcePacks.clear();
         this.gameSettings.incompatibleResourcePacks.clear();
         this.gameSettings.saveOptions();
      }

      this.mcLanguageManager.parseLanguageMetadata(arrayList);
      if (this.renderGlobal != null) {
         this.renderGlobal.loadRenderers();
      }
   }

   private void setInitialDisplayMode() throws LWJGLException {
      if (this.fullscreen) {
         Display.setFullscreen(true);
         DisplayMode displayMode = Display.getDisplayMode();
         this.displayWidth = Math.max(1, displayMode.getWidth());
         this.displayHeight = Math.max(1, displayMode.getHeight());
      } else {
         Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
      }
   }

   public boolean isSingleplayer() {
      return this.integratedServerIsRunning && this.theIntegratedServer != null;
   }

   public void shutdown() {
      this.running = false;
   }

   public BlockRendererDispatcher getBlockRendererDispatcher() {
      return this.blockRenderDispatcher;
   }

   public static Entity[] getEntityArray() {
      return entityArray;
   }

   public net.minecraft.client.audio.MusicTicker.MusicType getAmbientMusicType() {
      return this.thePlayer != null
         ? (
            this.thePlayer.worldObj.provider instanceof WorldProviderHell
               ? net.minecraft.client.audio.MusicTicker.MusicType.NETHER
               : (
                  this.thePlayer.worldObj.provider instanceof WorldProviderEnd
                     ? (BossStatus.bossName != null && BossStatus.statusBarTime > 0 ? net.minecraft.client.audio.MusicTicker.MusicType.END_BOSS : net.minecraft.client.audio.MusicTicker.MusicType.END)
                     : (
                        this.thePlayer.capabilities.isCreativeMode && this.thePlayer.capabilities.allowFlying
                           ? net.minecraft.client.audio.MusicTicker.MusicType.CREATIVE
                           : net.minecraft.client.audio.MusicTicker.MusicType.GAME
                     )
               )
         )
         : net.minecraft.client.audio.MusicTicker.MusicType.MENU;
   }

   @Override
   public boolean isSnooperEnabled() {
      return this.gameSettings.fullScreen;
   }

   public SkinManager getSkinManager() {
      return this.skinManager;
   }

   private void updateDebugProfilerName(int keyCount) {
      List<net.minecraft.profiler.Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);
      if (list != null && !list.isEmpty()) {
         net.minecraft.profiler.Profiler.Result profiler$result = list.remove(0);
         if (keyCount == 0) {
            if (profiler$result.sectionName.length() > 0) {
               int i = this.debugProfilerName.lastIndexOf(".");
               if (i >= 0) {
                  this.debugProfilerName = this.debugProfilerName.substring(0, i);
               }
            }
         } else {
            keyCount--;
            if (keyCount < list.size() && !list.get(keyCount).sectionName.equals("unspecified")) {
               if (this.debugProfilerName.length() > 0) {
                  this.debugProfilerName = this.debugProfilerName + ".";
               }

               this.debugProfilerName = this.debugProfilerName + list.get(keyCount).sectionName;
            }
         }
      }
   }

   public String getVersion() {
      return this.launchedVersion;
   }

   public NetHandlerPlayClient getNetHandler() {
      return this.thePlayer != null ? this.thePlayer.sendQueue : null;
   }

   public boolean isGamePaused() {
      return this.isGamePaused;
   }

   public void shutdownMinecraftApplet() {
      try {
         this.iStream.update12();
         logger.info("Stopping!");
         Novoline.getNovoline().saveAll();

         try {
            this.loadWorld((WorldClient)null);
         } catch (Throwable throwable) {
         }

         this.mcSoundHandler.unloadSounds();
      } finally {
         Display.destroy();
         if (!this.hasCrashed) {
            System.exit(0);
         }
      }

      System.gc();
   }

   public RenderItem getRenderItem() {
      return this.renderItem;
   }

   public void loadWorld(WorldClient worldClientIn, String loadingMessage) {
      if (worldClientIn == null) {
         NetHandlerPlayClient netHandlerPlayClient = this.getNetHandler();
         if (netHandlerPlayClient != null) {
            netHandlerPlayClient.cleanup();
         }

         if (this.theIntegratedServer != null && this.theIntegratedServer.isAnvilFileSet()) {
            this.theIntegratedServer.initiateShutdown();
            this.theIntegratedServer.setStaticInstance();
         }

         this.theIntegratedServer = null;
         this.guiAchievement.clearAchievements();
         this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
      }

      this.renderViewEntity = null;
      this.myNetworkManager = null;
      if (this.loadingScreen != null) {
         this.loadingScreen.resetProgressAndMessage(loadingMessage);
         this.loadingScreen.displayLoadingString("");
      }

      if (worldClientIn == null && this.theWorld != null) {
         this.mcResourcePackRepository.clearResourcePack();
         this.ingameGUI.resetPlayersOverlayFooterHeader();
         this.setServerData((ServerData)null);
         this.integratedServerIsRunning = false;
      }

      this.mcSoundHandler.stopSounds();
      this.theWorld = worldClientIn;
      if (worldClientIn != null) {
         if (this.renderGlobal != null) {
            this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
         }

         if (this.effectRenderer != null) {
            this.effectRenderer.clearEffects(worldClientIn);
         }

         if (this.thePlayer == null) {
            this.thePlayer = this.playerController.createPlayer(worldClientIn, new StatFileWriter());
            this.playerController.flipPlayer(this.thePlayer);
         }

         this.thePlayer.preparePlayerToSpawn();
         worldClientIn.spawnEntityInWorld(this.thePlayer);
         this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
         this.playerController.setPlayerCapabilities(this.thePlayer);
         this.renderViewEntity = this.thePlayer;
         Novoline.getNovoline().getBus().post(new LoadWorldEvent());
      } else {
         this.saveLoader.flushCache();
         this.thePlayer = null;
      }

      System.gc();
      this.systemTime = 0L;
   }

   public void updateDisplay() {
      this.mcProfiler.startSection("display_update");
      Display.update();
      this.mcProfiler.endSection();
      this.checkWindowResize();
   }

   public <V> ListenableFuture<V> addScheduledTask(Callable<V> callableToSchedule) {
      Validate.notNull(callableToSchedule);
      if (!this.isCallingFromMinecraftThread()) {
         ListenableFutureTask listenableFutureTask = ListenableFutureTask.create(callableToSchedule);
         synchronized (this.scheduledTasks) {
            this.scheduledTasks.add(listenableFutureTask);
            return listenableFutureTask;
         }
      } else {
         try {
            return Futures.immediateFuture(callableToSchedule.call());
         } catch (Exception exception2) {
            return Futures.immediateFailedCheckedFuture(exception2);
         }
      }
   }

   public void setDimensionAndSpawnPlayer(int dimension) {
      this.theWorld.setInitialSpawnLocation();
      this.theWorld.removeAllEntities();
      int i = 0;
      String text = null;
      if (this.thePlayer != null) {
         i = this.thePlayer.getEntityId();
         this.theWorld.removeEntity(this.thePlayer);
         text = this.thePlayer.getClientBrand();
      }

      this.renderViewEntity = null;
      EntityPlayerSP entityPlayerSP = this.thePlayer;
      this.thePlayer = this.playerController.createPlayer(this.theWorld, this.thePlayer == null ? new StatFileWriter() : this.thePlayer.getStatFileWriter());
      this.thePlayer.getDataWatcher().updateWatchedObjectsFromList(entityPlayerSP.getDataWatcher().getAllWatched());
      this.thePlayer.dimension = dimension;
      this.renderViewEntity = this.thePlayer;
      this.thePlayer.preparePlayerToSpawn();
      this.thePlayer.setClientBrand(text);
      this.theWorld.spawnEntityInWorld(this.thePlayer);
      this.playerController.flipPlayer(this.thePlayer);
      this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
      this.thePlayer.setEntityId(i);
      this.playerController.setPlayerCapabilities(this.thePlayer);
      this.thePlayer.setReducedDebug(entityPlayerSP.hasReducedDebug());
      if (this.currentScreen instanceof GuiGameOver) {
         this.displayGuiScreen((GuiScreen)null);
      }
   }

   public RenderManager getRenderManager() {
      return this.renderManager;
   }

   private void registerMetadataSerializers() {
      this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
      this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
      this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
      this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
      this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
   }

   public void launchIntegratedServer(String folderName, String worldName, WorldSettings worldSettingsIn) {
      this.loadWorld((WorldClient)null);
      System.gc();
      ISaveHandler saveHandler = this.saveLoader.getSaveLoader(folderName, false);
      WorldInfo worldInfo = saveHandler.loadWorldInfo();
      if (worldInfo == null && worldSettingsIn != null) {
         worldInfo = new WorldInfo(worldSettingsIn, folderName);
         saveHandler.saveWorldInfo(worldInfo);
      }

      if (worldSettingsIn == null) {
         worldSettingsIn = new WorldSettings(worldInfo);
      }

      try {
         this.theIntegratedServer = new IntegratedServer(this, folderName, worldName, worldSettingsIn);
         this.theIntegratedServer.startServerThread();
         this.integratedServerIsRunning = true;
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Starting integrated server");
         crashReportCategory.addCrashSection("Level ID", folderName);
         crashReportCategory.addCrashSection("Level Name", worldName);
         throw new ReportedException(crashReport);
      }

      this.loadingScreen.displaySavingString(I18n.format("menu.loadingLevel"));

      while (!this.theIntegratedServer.serverIsInRunLoop()) {
         String text = this.theIntegratedServer.getUserMessage();
         if (text != null) {
            this.loadingScreen.displayLoadingString(I18n.format(text));
         } else {
            this.loadingScreen.displayLoadingString("");
         }

         try {
            Thread.sleep(200L);
         } catch (InterruptedException interruptedException) {
         }
      }

      this.displayGuiScreen((GuiScreen)null);
      SocketAddress socketAddress = this.theIntegratedServer.getNetworkSystem().addLocalEndpoint();
      NetworkManager networkManager = NetworkManager.provideLocalClient(socketAddress);
      networkManager.setNetHandler(new NetHandlerLoginClient(networkManager, this, (GuiScreen)null));
      networkManager.sendPacket(new C00Handshake(socketAddress.toString(), 0, EnumConnectionState.LOGIN));
      networkManager.sendPacket(new C00PacketLoginStart(this.getSession().getProfile()));
      this.myNetworkManager = networkManager;
   }

   private void startTimerHackThread() {
      Thread thread2 = new Thread("Timer hack thread") {
         @Override
         public void run() {
            while (Minecraft.this.running) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException interruptedException) {
               }
            }
         }
      };
      thread2.setDaemon(true);
      thread2.start();
   }

   private void createDisplay() throws LWJGLException {
      Display.setResizable(true);
      Display.setTitle(wtf.novoline.Novoline.CLIENT_TITLE);

      try {
         Display.create(new PixelFormat().withDepthBits(24));
      } catch (LWJGLException lwjglException) {
         logger.error("Couldn't set pixel format", lwjglException);

         try {
            Thread.sleep(1000L);
         } catch (InterruptedException interruptedException) {
         }

         if (this.fullscreen) {
            this.updateDisplayMode();
         }

         Display.create();
      }
   }

   public boolean isFullScreen() {
      return this.fullscreen;
   }

   public static Map<String, String> getSessionInfo() {
      Map<String, String> map = Maps.newHashMap();
      map.put("X-Minecraft-Username", getMinecraft().getSession().getUsername());
      map.put("X-Minecraft-UUID", getMinecraft().getSession().getPlayerID());
      map.put("X-Minecraft-Version", "1.8.9");
      return map;
   }

   public PropertyMap getPropertyMap() {
      return this.propertyMap;
   }

   public ListenableFuture<Object> scheduleResourcesRefresh() {
      return this.addScheduledTask(new Runnable() {
         @Override
         public void run() {
            Minecraft.this.refreshResources();
         }
      });
   }

   public LanguageManager getLanguageManager() {
      return this.mcLanguageManager;
   }

   public static void handleArray(Entity[] entitys) {
      entityArray = entitys;
   }

   public void setIngameFocus() {
      if (Display.isActive() && !this.inGameHasFocus) {
         this.inGameHasFocus = true;
         this.mouseHelper.grabMouseCursor();
         this.displayGuiScreen((GuiScreen)null);
         this.leftClickCounter = 10000;
      }
   }

   public void crashed(CrashReport crash) {
      this.hasCrashed = true;
      this.crashReporter = crash;
   }

   public IntegratedServer getIntegratedServer() {
      return this.theIntegratedServer;
   }

   private ItemStack pickBlockWithNBT(Item itemIn, int meta, TileEntity tileEntityIn) {
      ItemStack itemStack = new ItemStack(itemIn, 1, meta);
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      tileEntityIn.writeToNBT(nbtTagCompound);
      if (itemIn == Items.skull && nbtTagCompound.hasKey("Owner")) {
         NBTTagCompound nBTTagCompound2 = nbtTagCompound.getCompoundTag("Owner");
         NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
         nbtTagCompound2.setTag("SkullOwner", nBTTagCompound2);
         itemStack.setTagCompound(nbtTagCompound2);
         return itemStack;
      } else {
         itemStack.setTagInfo("BlockEntityTag", nbtTagCompound);
         NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
         NBTTagList nbtTagList = new NBTTagList();
         nbtTagList.appendTag(new NBTTagString("(+NBT)"));
         nBTTagCompound2.setTag("Lore", nbtTagList);
         itemStack.setTagInfo("display", nBTTagCompound2);
         return itemStack;
      }
   }

   private void middleClickMouse() {
      if (this.objectMouseOver != null) {
         boolean isCreativeMode2 = this.thePlayer.capabilities.isCreativeMode;
         int i = 0;
         boolean flag = false;
         TileEntity tileEntity = null;
         Item item2;
         if (this.objectMouseOver.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockPos2 = this.objectMouseOver.getBlockPos();
            Block block2 = this.theWorld.getBlockState(blockPos2).getBlock();
            if (block2.getMaterial() == Material.air) {
               return;
            }

            item2 = block2.getItem(this.theWorld, blockPos2);
            if (item2 == null) {
               return;
            }

            if (isCreativeMode2 && GuiScreen.isCtrlKeyDown()) {
               tileEntity = this.theWorld.getTileEntity(blockPos2);
            }

            i = (item2 instanceof ItemBlock && !block2.isFlowerPot() ? Block.getBlockFromItem(item2) : block2).getDamageValue(this.theWorld, blockPos2);
            flag = item2.getHasSubtypes();
         } else {
            if (this.objectMouseOver.typeOfHit != net.minecraft.util.MovingObjectPosition.MovingObjectType.ENTITY || this.objectMouseOver.entityHit == null || !isCreativeMode2) {
               return;
            }

            if (this.objectMouseOver.entityHit instanceof EntityPainting) {
               item2 = Items.painting;
            } else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot) {
               item2 = Items.lead;
            } else if (this.objectMouseOver.entityHit instanceof EntityItemFrame entityItemFrame) {
               ItemStack displayedItem = entityItemFrame.getDisplayedItem();
               if (displayedItem == null) {
                  item2 = Items.item_frame;
               } else {
                  item2 = displayedItem.getItem();
                  i = displayedItem.getMetadata();
                  flag = true;
               }
            } else if (this.objectMouseOver.entityHit instanceof EntityMinecart entityMinecart) {
               switch (entityMinecart.getMinecartType()) {
                  case FURNACE:
                     item2 = Items.furnace_minecart;
                     break;
                  case CHEST:
                     item2 = Items.chest_minecart;
                     break;
                  case TNT:
                     item2 = Items.tnt_minecart;
                     break;
                  case HOPPER:
                     item2 = Items.hopper_minecart;
                     break;
                  case COMMAND_BLOCK:
                     item2 = Items.command_block_minecart;
                     break;
                  default:
                     item2 = Items.minecart;
               }
            } else if (this.objectMouseOver.entityHit instanceof EntityBoat) {
               item2 = Items.boat;
            } else if (this.objectMouseOver.entityHit instanceof EntityArmorStand) {
               item2 = Items.armor_stand;
            } else {
               item2 = Items.spawn_egg;
               i = EntityList.getEntityID(this.objectMouseOver.entityHit);
               flag = true;
               if (!EntityList.entityEggs.containsKey(i)) {
                  return;
               }
            }
         }

         InventoryPlayer inventory2 = this.thePlayer.inventory;
         if (tileEntity == null) {
            inventory2.setCurrentItem(item2, i, flag, isCreativeMode2);
         } else {
            ItemStack itemStack = this.pickBlockWithNBT(item2, i, tileEntity);
            inventory2.setInventorySlotContents(inventory2.currentItem, itemStack);
         }

         if (isCreativeMode2) {
            int i2 = this.thePlayer.inventoryContainer.inventorySlots.size() - 9 + inventory2.currentItem;
            this.playerController.sendSlotPacket(inventory2.getStackInSlot(inventory2.currentItem), i2);
         }
      }
   }

   public void setServerData(ServerData serverDataIn) {
      this.currentServerData = serverDataIn;
   }

   public boolean isIntegratedServerRunning() {
      return this.integratedServerIsRunning;
   }

   public void freeMemory() {
      try {
         memoryReserve = new byte[0];
         this.renderGlobal.deleteAllDisplayLists();
      } catch (Throwable throwable) {
      }

      try {
         System.gc();
         this.loadWorld((WorldClient)null);
      } catch (Throwable throwable2) {
      }

      System.gc();
   }

   public void runTick() throws IOException {
      if (Novoline.initialized) {
         Novoline.getNovoline().getBus().post(new RunTickEvent());
      }

      if (this.rightClickDelayTimer > 0) {
         this.rightClickDelayTimer--;
      }

      this.mcProfiler.startSection("gui");
      if (!this.isGamePaused) {
         this.ingameGUI.updateTick();
      }

      this.mcProfiler.endSection();
      this.entityRenderer.getMouseOver(1.0F);
      this.mcProfiler.startSection("gameMode");
      if (!this.isGamePaused && this.theWorld != null) {
         this.playerController.updateController();
      }

      this.mcProfiler.endStartSection("textures");
      if (!this.isGamePaused) {
         this.renderEngine.tick();
      }

      if (this.currentScreen == null && this.thePlayer != null) {
         if (this.thePlayer.getHealth() <= 0.0F) {
            this.displayGuiScreen((GuiScreen)null);
         } else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null) {
            this.displayGuiScreen(new GuiSleepMP());
         }
      } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping()) {
         this.displayGuiScreen((GuiScreen)null);
      }

      if (this.currentScreen != null) {
         this.leftClickCounter = 10000;
      }

      if (this.currentScreen != null) {
         try {
            this.currentScreen.handleInput();
         } catch (Throwable throwable) {
            CrashReport crashReport2 = CrashReport.makeCrashReport(throwable, "Updating screen events");
            crashReport2.makeCategory("Affected screen").addCrashSectionCallable("Screen name", new Callable<String>() {
               public String call() throws Exception {
                  return Minecraft.this.currentScreen.getClass().getCanonicalName();
               }
            });
            throw new ReportedException(crashReport2);
         }

         if (this.currentScreen != null) {
            try {
               this.currentScreen.updateScreen();
            } catch (Throwable throwable2) {
               CrashReport crashReport3 = CrashReport.makeCrashReport(throwable2, "Ticking screen");
               crashReport3.makeCategory("Affected screen").addCrashSectionCallable("Screen name", new Callable<String>() {
                  public String call() throws Exception {
                     return Minecraft.this.currentScreen.getClass().getCanonicalName();
                  }
               });
               throw new ReportedException(crashReport3);
            }
         }
      }

      if (this.currentScreen == null || this.currentScreen.allowUserInput) {
         this.mcProfiler.endStartSection("mouse");

         while (Mouse.next()) {
            int eventButton = Mouse.getEventButton();
            KeyBinding.setKeyBindState(eventButton - 100, Mouse.getEventButtonState());
            Novoline.getNovoline().getBus().post(new DeviceButtonDownEvent(eventButton, MouseType.MOUSE));
            if (Mouse.getEventButtonState()) {
               if (this.thePlayer.isSpectator() && eventButton == 2) {
                  this.ingameGUI.getSpectatorGui().onMiddleClick();
               } else {
                  KeyBinding.onTick(eventButton - 100);
               }
            }

            long l = getSystemTime() - this.systemTime;
            if (l <= 200L) {
               int eventDWheel = Mouse.getEventDWheel();
               if (eventDWheel != 0) {
                  if (this.thePlayer.isSpectator()) {
                     eventDWheel = eventDWheel < 0 ? -1 : 1;
                     if (this.ingameGUI.getSpectatorGui().isMenuActive()) {
                        this.ingameGUI.getSpectatorGui().scroll(-eventDWheel);
                     } else {
                        float f = MathHelper.clamp_float(this.thePlayer.capabilities.getFlySpeed() + eventDWheel * 0.005F, 0.0F, 0.2F);
                        this.thePlayer.capabilities.setFlySpeed(f);
                     }
                  } else {
                     this.thePlayer.inventory.changeCurrentItem(eventDWheel);
                  }
               }

               if (this.currentScreen == null) {
                  if (!this.inGameHasFocus && Mouse.getEventButtonState()) {
                     this.setIngameFocus();
                  }
               } else if (this.currentScreen != null) {
                  this.currentScreen.handleMouseInput();
               }
            }
         }

         if (this.leftClickCounter > 0) {
            this.leftClickCounter--;
         }

         this.mcProfiler.endStartSection("keyboard");

         while (Keyboard.next()) {
            int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
            KeyBinding.setKeyBindState(i, Keyboard.getEventKeyState());
            if (Keyboard.getEventKeyState()) {
               KeyBinding.onTick(i);
            }

            if (this.debugCrashKeyPressTime > 0L) {
               if (getSystemTime() - this.debugCrashKeyPressTime >= 6000L) {
                  throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
               }

               if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
                  this.debugCrashKeyPressTime = -1L;
               }
            } else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
               this.debugCrashKeyPressTime = getSystemTime();
            }

            this.dispatchKeypresses();
            if (Keyboard.getEventKeyState()) {
               if (i == 62 && this.entityRenderer != null) {
                  this.entityRenderer.switchUseShader();
               }

               if (this.currentScreen != null) {
                  this.currentScreen.handleKeyboardInput();
               } else {
                  Novoline.getNovoline().getMacroManager().getMacroKeyHandler().handleKeyPress(i);
                  Novoline.getNovoline().getBus().post(new DeviceButtonDownEvent(i, MouseType.KEYBOARD));
                  if (i == 1) {
                     this.displayInGameMenu();
                  }

                  if (i == 32 && Keyboard.isKeyDown(61) && this.ingameGUI != null) {
                     this.ingameGUI.getChatGUI().clearChatMessages();
                  }

                  if (i == 31 && Keyboard.isKeyDown(61)) {
                     this.refreshResources();
                  }

                  if (i == 17 && Keyboard.isKeyDown(61)) {
                  }

                  if (i == 18 && Keyboard.isKeyDown(61)) {
                  }

                  if (i == 47 && Keyboard.isKeyDown(61)) {
                  }

                  if (i == 38 && Keyboard.isKeyDown(61)) {
                  }

                  if (i == 22 && Keyboard.isKeyDown(61)) {
                  }

                  if (i == 20 && Keyboard.isKeyDown(61)) {
                     this.refreshResources();
                  }

                  if (i == 33 && Keyboard.isKeyDown(61)) {
                     this.gameSettings.setOptionValue(net.minecraft.client.settings.GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1);
                  }

                  if (i == 30 && Keyboard.isKeyDown(61)) {
                     this.renderGlobal.loadRenderers();
                  }

                  if (i == 35 && Keyboard.isKeyDown(61)) {
                     this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
                     this.gameSettings.saveOptions();
                  }

                  if (i == 48 && Keyboard.isKeyDown(61)) {
                     this.renderManager.setDebugBoundingBox(!this.renderManager.isDebugBoundingBox());
                  }

                  if (i == 25 && Keyboard.isKeyDown(61)) {
                     this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
                     this.gameSettings.saveOptions();
                  }

                  if (i == 59) {
                     this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                  }

                  if (i == 61) {
                     this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                     this.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                     this.gameSettings.showLagometer = GuiScreen.isAltKeyDown();
                  }

                  if (this.gameSettings.keyBindScreenshot.isPressed()) {
                     this.gameSettings.thirdPersonView++;
                     if (this.gameSettings.thirdPersonView > 2) {
                        this.gameSettings.thirdPersonView = 0;
                     }

                     if (this.gameSettings.thirdPersonView == 0) {
                        this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
                     } else if (this.gameSettings.thirdPersonView == 1) {
                        this.entityRenderer.loadEntityShader((Entity)null);
                     }

                     this.renderGlobal.setDisplayListEntitiesDirty();
                  }

                  if (this.gameSettings.keyBindTogglePerspective.isPressed()) {
                     this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
                  }
               }

               if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
                  if (i == 11) {
                     this.updateDebugProfilerName(0);
                  }

                  for (int i2 = 0; i2 < 9; i2 += 1) {
                     if (i == 2 + i2) {
                        this.updateDebugProfilerName(i2 + 1);
                     }
                  }
               }
            }
         }

         for (int i3 = 0; i3 < 9; i3 += 1) {
            if (this.gameSettings.keyBindsHotbar[i3].isPressed()) {
               if (this.thePlayer.isSpectator()) {
                  this.ingameGUI.getSpectatorGui().onHotbarSelected(i3);
               } else {
                  this.thePlayer.inventory.currentItem = i3;
               }
            }
         }

         boolean flag = this.gameSettings.chatVisibility != net.minecraft.entity.player.EntityPlayer.EnumChatVisibility.HIDDEN;

         while (this.gameSettings.keyBindInventory.isPressed()) {
            if (this.playerController.isRidingHorse()) {
               this.thePlayer.sendHorseInventory();
            } else {
               this.getNetHandler().addToSendQueue(new C16PacketClientStatus(net.minecraft.network.play.client.C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
               this.displayGuiScreen(new GuiInventory(this.thePlayer));
            }
         }

         while (this.gameSettings.keyBindSwapItem.isPressed()) {
            if (!this.thePlayer.isSpectator()) {
               this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
            }
         }

         while (this.gameSettings.keyBinding.isPressed() && flag) {
            this.displayGuiScreen(new GuiChat());
         }

         if (this.currentScreen == null && this.gameSettings.keyBindPlayerList.isPressed() && flag) {
            this.displayGuiScreen(new GuiChat("/"));
         }

         if (this.thePlayer.isUsingItem()) {
            if (!this.gameSettings.keyBindUseItem.isKeyDown()) {
               this.playerController.onStoppedUsingItem(this.thePlayer);
            }

            while (this.gameSettings.keyBindAttack.isPressed()) {
            }

            while (this.gameSettings.keyBindUseItem.isPressed()) {
            }

            while (this.gameSettings.keyBindPickBlock.isPressed()) {
            }
         } else {
            while (this.gameSettings.keyBindAttack.isPressed()) {
               this.clickMouse();
            }

            while (this.gameSettings.keyBindUseItem.isPressed()) {
               this.rightClickMouseLegacy();
            }

            while (this.gameSettings.keyBindPickBlock.isPressed()) {
               this.middleClickMouse();
            }
         }

         if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem()) {
            this.rightClickMouseLegacy();
         }

         this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.inGameHasFocus);
      }

      if (this.theWorld != null) {
         if (this.thePlayer != null) {
            this.joinPlayerCounter++;
            if (this.joinPlayerCounter == 30) {
               this.joinPlayerCounter = 0;
               this.theWorld.joinEntityInSurroundings(this.thePlayer);
            }
         }

         this.mcProfiler.endStartSection("gameRenderer");
         if (!this.isGamePaused) {
            this.entityRenderer.updateRenderer();
         }

         this.mcProfiler.endStartSection("levelRenderer");
         if (!this.isGamePaused) {
            this.renderGlobal.updateClouds();
         }

         this.mcProfiler.endStartSection("level");
         if (!this.isGamePaused) {
            if (this.theWorld.getLastLightningBolt() > 0) {
               this.theWorld.setLastLightningBolt(this.theWorld.getLastLightningBolt() - 1);
            }

            this.theWorld.updateEntities();
         }
      } else if (this.entityRenderer.isShaderActive()) {
         this.entityRenderer.stopUseShader();
      }

      if (!this.isGamePaused) {
         this.mcMusicTicker.update();
         this.mcSoundHandler.update();
      }

      if (this.theWorld != null) {
         if (!this.isGamePaused) {
            this.theWorld.setAllowedSpawnTypes(this.theWorld.getDifficulty() != EnumDifficulty.PEACEFUL, true);

            try {
               this.theWorld.tick();
            } catch (Throwable throwable3) {
               CrashReport crashReport4 = CrashReport.makeCrashReport(throwable3, "Exception in world tick");
               if (this.theWorld == null) {
                  crashReport4.makeCategory("Affected level").addCrashSection("Problem", "Level is null!");
               } else {
                  this.theWorld.addWorldInfoToCrashReport(crashReport4);
               }

               throw new ReportedException(crashReport4);
            }
         }

         this.mcProfiler.endStartSection("animateTick");
         if (!this.isGamePaused && this.theWorld != null) {
            this.theWorld
               .doVoidFogParticles(
                  MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ)
               );
         }

         this.mcProfiler.endStartSection("particles");
         if (!this.isGamePaused) {
            this.effectRenderer.updateEffects();
         }
      } else if (this.myNetworkManager != null) {
         this.mcProfiler.endStartSection("pendingConnection");
         this.myNetworkManager.processReceivedPackets();
      }

      this.mcProfiler.endSection();
      this.systemTime = getSystemTime();
   }

   public static void stopIntegratedServer() {
      if (theMinecraft != null) {
         IntegratedServer integratedServer = theMinecraft.getIntegratedServer();
         if (integratedServer != null) {
            integratedServer.stopServer();
         }
      }
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public PlayerUsageSnooper getPlayerUsageSnooper() {
      return this.usageSnooper;
   }

   public void run() {
      this.running = true;

      try {
         this.startGame();
      } catch (Throwable throwable) {
         CrashReport crashReport2 = CrashReport.makeCrashReport(throwable, "Initializing game");
         crashReport2.makeCategory("Initialization");
         this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(crashReport2));
         return;
      }

      try {
         while (this.running) {
            if (this.hasCrashed && this.crashReporter != null) {
               this.displayCrashReport(this.crashReporter);
            } else {
               try {
                  this.runGameLoop();
               } catch (OutOfMemoryError outOfMemoryError) {
                  this.freeMemory();
                  this.displayGuiScreen(new GuiMemoryErrorScreen());
                  System.gc();
               }
            }
         }

         return;
      } catch (MinecraftError minecraftError) {
      } catch (ReportedException reportedException) {
         this.addGraphicsAndWorldToCrashReport(reportedException.getCrashReport());
         this.freeMemory();
         logger.fatal("Reported exception thrown!", reportedException);
         this.displayCrashReport(reportedException.getCrashReport());
      } catch (Throwable throwable2) {
         CrashReport crashReport3 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", throwable2));
         this.freeMemory();
         logger.fatal("Unreported exception thrown!", throwable2);
         this.displayCrashReport(crashReport3);
      } finally {
         this.shutdownMinecraftApplet();
      }
   }

   public void dispatchKeypresses() {
      int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();
      if (i != 0
         && !Keyboard.isRepeatEvent()
         && (!(this.currentScreen instanceof GuiControls) || ((GuiControls)this.currentScreen).time <= getSystemTime() - 20L)) {
         if (Keyboard.getEventKeyState()) {
            if (i == this.gameSettings.keyBindFullscreen.getKeyCode()) {
               if (this.getIStream().isActive()) {
                  this.getIStream().update8();
               } else if (this.getIStream().isActive7()) {
                  this.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
                     @Override
                     public void confirmClicked(boolean flag, int i2) {
                        if (flag) {
                           Minecraft.this.getIStream().update15();
                        }

                        Minecraft.this.displayGuiScreen((GuiScreen)null);
                     }
                  }, I18n.format("stream.confirm_start"), "", 0));
               } else if (!this.getIStream().isActive6() || !this.getIStream().isActive9()) {
                  GuiStreamUnavailable.handleGuiScreen(this.currentScreen);
               } else if (this.theWorld != null) {
                  this.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("Not ready to start streaming yet!"));
               }
            } else if (i == this.gameSettings.keyBinding3.getKeyCode()) {
               if (this.getIStream().isActive()) {
                  if (this.getIStream().isActive5()) {
                     this.getIStream().update5();
                  } else {
                     this.getIStream().update16();
                  }
               }
            } else if (i == this.gameSettings.keyBindSpectatorOutlines.getKeyCode()) {
               if (this.getIStream().isActive()) {
                  this.getIStream().update6();
               }
            } else if (i == this.gameSettings.keyBinding4.getKeyCode()) {
               this.iStream.handleBoolean(true);
            } else if (i == this.gameSettings.keyBindSmoothCamera.getKeyCode()) {
               this.toggleFullscreen();
            } else if (i == this.gameSettings.keyBindCommand.getKeyCode()) {
               this.ingameGUI
                  .getChatGUI()
                  .printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc));
            }
         } else if (i == this.gameSettings.keyBinding4.getKeyCode()) {
            this.iStream.handleBoolean(false);
         }
      }
   }

   private void runGameLoop() throws IOException {
      long l = System.nanoTime();
      this.mcProfiler.startSection("root");
      if (Display.isCreated() && Display.isCloseRequested()) {
         this.shutdown();
      }

      if (this.isGamePaused && this.theWorld != null) {
         float renderPartialTicks2 = this.timer.renderPartialTicks;
         this.timer.updateTimer();
         this.timer.renderPartialTicks = renderPartialTicks2;
      } else {
         this.timer.updateTimer();
      }

      this.mcProfiler.startSection("scheduledExecutables");
      synchronized (this.scheduledTasks) {
         while (!this.scheduledTasks.isEmpty()) {
            Util.runTask(this.scheduledTasks.poll(), logger);
         }
      }

      this.mcProfiler.endSection();
      long l2 = System.nanoTime();
      this.mcProfiler.startSection("tick");

      for (int i = 0; i < this.timer.elapsedTicks; i++) {
         this.runTick();
      }

      this.mcProfiler.endStartSection("preRenderErrors");
      long l3 = System.nanoTime() - l2;
      this.checkGLError("Pre render");
      this.mcProfiler.endStartSection("sound");
      this.mcSoundHandler.setListener(this.thePlayer, this.timer.renderPartialTicks);
      this.mcProfiler.endSection();
      this.mcProfiler.startSection("render");
      GlStateManager.pushMatrix();
      GlStateManager.clear(16640);
      this.framebufferMc.bindFramebuffer(true);
      this.mcProfiler.startSection("display");
      GlStateManager.enableTexture2D();
      if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) {
         this.gameSettings.thirdPersonView = 0;
      }

      this.mcProfiler.endSection();
      if (!this.skipRenderWorld) {
         this.mcProfiler.endStartSection("gameRenderer");
         this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks, l);
         this.mcProfiler.endSection();
      }

      this.mcProfiler.endSection();
      if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart && !this.gameSettings.hideGUI) {
         if (!this.mcProfiler.profilingEnabled) {
            this.mcProfiler.clearProfiling();
         }

         this.mcProfiler.profilingEnabled = true;
         this.update();
      } else {
         this.mcProfiler.profilingEnabled = false;
         this.prevFrameTime = System.nanoTime();
      }

      this.guiAchievement.updateAchievementWindow();
      this.framebufferMc.unbindFramebuffer();
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      this.entityRenderer.call3DRenderEvent(this.timer.renderPartialTicks);
      GlStateManager.popMatrix();
      this.mcProfiler.startSection("root");
      this.updateDisplay();
      Thread.yield();
      this.mcProfiler.startSection("stream");
      this.mcProfiler.startSection("update");
      this.iStream.update();
      this.mcProfiler.endStartSection("submit");
      this.iStream.update11();
      this.mcProfiler.endSection();
      this.mcProfiler.endSection();
      this.checkGLError("Post render");
      this.fpsCounter++;
      this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();
      long l4 = System.nanoTime();
      this.frameTimer.addFrame(l4 - this.startNanoTime);
      this.startNanoTime = l4;

      while (getSystemTime() >= this.debugUpdateTime + 1000L) {
         debugFPS = this.fpsCounter;
         this.debug = String.format(
            "%d fps (%d chunk update%s) T: %s%s%s%s%s",
            debugFPS,
            RenderChunk.renderChunksUpdated,
            RenderChunk.renderChunksUpdated != 1 ? "s" : "",
            this.gameSettings.limitFramerate == net.minecraft.client.settings.GameSettings.Options.FRAMERATE_LIMIT.getValueMax() ? "inf" : this.gameSettings.limitFramerate,
            this.gameSettings.enableVsync ? " vsync" : "",
            this.gameSettings.fancyGraphics ? "" : " fast",
            this.gameSettings.clouds == 0 ? "" : (this.gameSettings.clouds == 1 ? " fast-clouds" : " fancy-clouds"),
            OpenGlHelper.useVbo() ? " vbo" : ""
         );
         RenderChunk.renderChunksUpdated = 0;
         this.debugUpdateTime += 1000L;
         this.fpsCounter = 0;
         this.usageSnooper.addMemoryStatsToSnooper();
         if (!this.usageSnooper.isSnooperRunning()) {
            this.usageSnooper.startSnooper();
         }
      }

      if (this.isFramerateLimitBelowMax()) {
         this.mcProfiler.startSection("fpslimit_wait");
         Display.sync(this.getLimitFramerate());
         this.mcProfiler.endSection();
      }

      this.mcProfiler.endSection();
   }

   public void displayInGameMenu() {
      if (this.currentScreen == null) {
         this.displayGuiScreen(new GuiIngameMenu());
         if (this.isSingleplayer() && !this.theIntegratedServer.getPublic()) {
            this.mcSoundHandler.pauseSounds();
         }
      }
   }

   private static boolean isJvm64bit() {
      String[] texts = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
      int length2 = texts.length;

      for (int i = 0; i < length2; i++) {
         String property = System.getProperty(texts[i]);
         if (property != null && property.contains("64")) {
            return true;
         }
      }

      return false;
   }

   public void toggleFullscreen() {
      try {
         this.fullscreen = !this.fullscreen;
         this.gameSettings.fullscreenOption = this.fullscreen;
         if (this.fullscreen) {
            this.updateDisplayMode();
            this.displayWidth = Display.getDisplayMode().getWidth();
            this.displayHeight = Display.getDisplayMode().getHeight();
            if (this.displayWidth <= 0) {
               this.displayWidth = 1;
            }

            if (this.displayHeight <= 0) {
               this.displayHeight = 1;
            }
         } else {
            Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
            this.displayWidth = this.tempDisplayWidth;
            this.displayHeight = this.tempDisplayHeight;
            if (this.displayWidth <= 0) {
               this.displayWidth = 1;
            }

            if (this.displayHeight <= 0) {
               this.displayHeight = 1;
            }
         }

         if (this.currentScreen != null) {
            this.resize(this.displayWidth, this.displayHeight);
         } else {
            this.updateFramebufferSize();
            Novoline.framebufferManager.resizeFreeFramebuffers();
         }

         Display.setFullscreen(this.fullscreen);
         Display.setVSyncEnabled(this.gameSettings.enableVsync);
         this.updateDisplay();
      } catch (Exception exception2) {
         logger.error("Couldn't toggle fullscreen", exception2);
      }
   }

   public SoundHandler getSoundHandler() {
      return this.mcSoundHandler;
   }

   public Framebuffer getFramebuffer() {
      return this.framebufferMc;
   }

   private void updateFramebufferSize() {
      this.framebufferMc.createBindFramebuffer(this.displayWidth, this.displayHeight);
      if (this.entityRenderer != null) {
         this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight);
      }
   }

   private void sendClickBlockToController(boolean leftClick) {
      if (!leftClick) {
         this.leftClickCounter = 0;
      }

      if (this.leftClickCounter <= 0 && !this.thePlayer.isUsingItem()) {
         if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockPos = this.objectMouseOver.getBlockPos();
            if (this.theWorld.getBlockState(blockPos).getBlock().getMaterial() != Material.air
               && this.playerController.onPlayerDamageBlock(blockPos, this.objectMouseOver.sideHit)) {
               this.effectRenderer.addBlockHitEffects(blockPos, this.objectMouseOver.sideHit);
               this.thePlayer.swingItemNoPacket();
            }
         } else {
            this.playerController.resetBlockRemoving();
         }
      }
   }

   public MinecraftSessionService getSessionService() {
      return this.sessionService;
   }

   public static int getDebugFPS() {
      return debugFPS;
   }

   public void displayGuiScreen(GuiScreen guiScreenIn) {
      if (this.currentScreen != null) {
         this.currentScreen.startClosing();
      }

      if (guiScreenIn == null && this.theWorld == null) {
         guiScreenIn = new NovolineMainMenu();
      } else if (guiScreenIn == null && this.thePlayer.getHealth() <= 0.0F) {
         guiScreenIn = new GuiGameOver();
      }

      if (guiScreenIn instanceof NovolineMainMenu) {
         this.gameSettings.showDebugInfo = false;
      }

      this.currentScreen = guiScreenIn;
      if (guiScreenIn != null) {
         this.setIngameNotInFocus();
         ScaledResolution scaledResolution = new ScaledResolution(this);
         int scaledWidth = scaledResolution.getScaledWidth();
         int scaledHeight = scaledResolution.getScaledHeight();
         guiScreenIn.setWorldAndResolution(this, scaledWidth, scaledHeight);
         this.skipRenderWorld = false;
      } else {
         this.mcSoundHandler.resumeSounds();
         this.setIngameFocus();
      }
   }

   public PropertyMap getProfileProperties() {
      if (this.profileProperties.isEmpty()) {
         GameProfile gameProfile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
         this.profileProperties.putAll(gameProfile.getProperties());
      }

      return this.profileProperties;
   }

   public void displayCrashReport(CrashReport crashReportIn) {
      File file = new File(getMinecraft().mcDataDir, "crash-reports");
      File file2 = new File(file, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
      Bootstrap.printToSYSOUT(crashReportIn.getCompleteReport());
      byte[] bytes = crashReportIn.getCompleteReport().getBytes(StandardCharsets.UTF_8);
      Attachment attachment = new Attachment(bytes, file2.getName());
      Sentry.withScope(scope -> {
         scope.addBreadcrumb(new Breadcrumb(crashReportIn.getDescription()));
         scope.addAttachment(attachment);
         Novoline.getNovoline().reportException(crashReportIn.getCrashCause());
      });
      if (crashReportIn.getFile() != null) {
         Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReportIn.getFile());
         System.exit(-1);
      } else if (crashReportIn.saveToFile(file2)) {
         Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
         System.exit(-1);
      } else {
         Bootstrap.printToSYSOUT("#@?@# Game crashed! Crash report could not be saved. #@?@#");
         System.exit(-2);
      }
   }

   public TextureManager getTextureManager() {
      return this.renderEngine;
   }

   public ISaveFormat getSaveLoader() {
      return this.saveLoader;
   }

   public ServerData getCurrentServerData() {
      return this.currentServerData;
   }

   public ItemRenderer getItemRenderer() {
      return this.itemRenderer;
   }

   @Override
   public void addServerTypeToSnooper(PlayerUsageSnooper playerUsageSnooper) {
      playerUsageSnooper.addStatToSnooper("opengl_version", GL11.glGetString(7938));
      playerUsageSnooper.addStatToSnooper("opengl_vendor", GL11.glGetString(7936));
      playerUsageSnooper.addStatToSnooper("client_brand", ClientBrandRetriever.getClientModName());
      playerUsageSnooper.addStatToSnooper("launched_version", this.launchedVersion);
      ContextCapabilities capabilities2 = GLContext.getCapabilities();
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_arrays_of_arrays]", capabilities2.GL_ARB_arrays_of_arrays);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_base_instance]", capabilities2.GL_ARB_base_instance);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_blend_func_extended]", capabilities2.GL_ARB_blend_func_extended);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_clear_buffer_object]", capabilities2.GL_ARB_clear_buffer_object);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_color_buffer_float]", capabilities2.GL_ARB_color_buffer_float);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_compatibility]", capabilities2.GL_ARB_compatibility);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_compressed_texture_pixel_storage]", capabilities2.GL_ARB_compressed_texture_pixel_storage);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", capabilities2.GL_ARB_compute_shader);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", capabilities2.GL_ARB_copy_buffer);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", capabilities2.GL_ARB_copy_image);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", capabilities2.GL_ARB_depth_buffer_float);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", capabilities2.GL_ARB_compute_shader);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", capabilities2.GL_ARB_copy_buffer);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", capabilities2.GL_ARB_copy_image);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", capabilities2.GL_ARB_depth_buffer_float);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_depth_clamp]", capabilities2.GL_ARB_depth_clamp);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_depth_texture]", capabilities2.GL_ARB_depth_texture);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers]", capabilities2.GL_ARB_draw_buffers);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers_blend]", capabilities2.GL_ARB_draw_buffers_blend);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_draw_elements_base_vertex]", capabilities2.GL_ARB_draw_elements_base_vertex);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_draw_indirect]", capabilities2.GL_ARB_draw_indirect);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_draw_instanced]", capabilities2.GL_ARB_draw_instanced);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_explicit_attrib_location]", capabilities2.GL_ARB_explicit_attrib_location);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_explicit_uniform_location]", capabilities2.GL_ARB_explicit_uniform_location);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_fragment_layer_viewport]", capabilities2.GL_ARB_fragment_layer_viewport);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_fragment_program]", capabilities2.GL_ARB_fragment_program);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_fragment_shader]", capabilities2.GL_ARB_fragment_shader);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_fragment_program_shadow]", capabilities2.GL_ARB_fragment_program_shadow);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_object]", capabilities2.GL_ARB_framebuffer_object);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_sRGB]", capabilities2.GL_ARB_framebuffer_sRGB);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_geometry_shader4]", capabilities2.GL_ARB_geometry_shader4);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_gpu_shader5]", capabilities2.GL_ARB_gpu_shader5);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_half_float_pixel]", capabilities2.GL_ARB_half_float_pixel);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_half_float_vertex]", capabilities2.GL_ARB_half_float_vertex);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_instanced_arrays]", capabilities2.GL_ARB_instanced_arrays);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_alignment]", capabilities2.GL_ARB_map_buffer_alignment);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_range]", capabilities2.GL_ARB_map_buffer_range);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_multisample]", capabilities2.GL_ARB_multisample);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_multitexture]", capabilities2.GL_ARB_multitexture);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_occlusion_query2]", capabilities2.GL_ARB_occlusion_query2);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_pixel_buffer_object]", capabilities2.GL_ARB_pixel_buffer_object);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_seamless_cube_map]", capabilities2.GL_ARB_seamless_cube_map);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_shader_objects]", capabilities2.GL_ARB_shader_objects);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_shader_stencil_export]", capabilities2.GL_ARB_shader_stencil_export);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_shader_texture_lod]", capabilities2.GL_ARB_shader_texture_lod);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_shadow]", capabilities2.GL_ARB_shadow);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_shadow_ambient]", capabilities2.GL_ARB_shadow_ambient);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_stencil_texturing]", capabilities2.GL_ARB_stencil_texturing);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_sync]", capabilities2.GL_ARB_sync);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_tessellation_shader]", capabilities2.GL_ARB_tessellation_shader);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_texture_border_clamp]", capabilities2.GL_ARB_texture_border_clamp);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_texture_buffer_object]", capabilities2.GL_ARB_texture_buffer_object);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map]", capabilities2.GL_ARB_texture_cube_map);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map_array]", capabilities2.GL_ARB_texture_cube_map_array);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_texture_non_power_of_two]", capabilities2.GL_ARB_texture_non_power_of_two);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_uniform_buffer_object]", capabilities2.GL_ARB_uniform_buffer_object);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_vertex_blend]", capabilities2.GL_ARB_vertex_blend);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_vertex_buffer_object]", capabilities2.GL_ARB_vertex_buffer_object);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_vertex_program]", capabilities2.GL_ARB_vertex_program);
      playerUsageSnooper.addStatToSnooper("gl_caps[ARB_vertex_shader]", capabilities2.GL_ARB_vertex_shader);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_bindable_uniform]", capabilities2.GL_EXT_bindable_uniform);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_blend_equation_separate]", capabilities2.GL_EXT_blend_equation_separate);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_blend_func_separate]", capabilities2.GL_EXT_blend_func_separate);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_blend_minmax]", capabilities2.GL_EXT_blend_minmax);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_blend_subtract]", capabilities2.GL_EXT_blend_subtract);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_draw_instanced]", capabilities2.GL_EXT_draw_instanced);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_multisample]", capabilities2.GL_EXT_framebuffer_multisample);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_object]", capabilities2.GL_EXT_framebuffer_object);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_sRGB]", capabilities2.GL_EXT_framebuffer_sRGB);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_geometry_shader4]", capabilities2.GL_EXT_geometry_shader4);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_gpu_program_parameters]", capabilities2.GL_EXT_gpu_program_parameters);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_gpu_shader4]", capabilities2.GL_EXT_gpu_shader4);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_multi_draw_arrays]", capabilities2.GL_EXT_multi_draw_arrays);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_packed_depth_stencil]", capabilities2.GL_EXT_packed_depth_stencil);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_paletted_texture]", capabilities2.GL_EXT_paletted_texture);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_rescale_normal]", capabilities2.GL_EXT_rescale_normal);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_separate_shader_objects]", capabilities2.GL_EXT_separate_shader_objects);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_shader_image_load_store]", capabilities2.GL_EXT_shader_image_load_store);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_shadow_funcs]", capabilities2.GL_EXT_shadow_funcs);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_shared_texture_palette]", capabilities2.GL_EXT_shared_texture_palette);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_stencil_clear_tag]", capabilities2.GL_EXT_stencil_clear_tag);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_stencil_two_side]", capabilities2.GL_EXT_stencil_two_side);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_stencil_wrap]", capabilities2.GL_EXT_stencil_wrap);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_texture_3d]", capabilities2.GL_EXT_texture_3d);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_texture_array]", capabilities2.GL_EXT_texture_array);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_texture_buffer_object]", capabilities2.GL_EXT_texture_buffer_object);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_texture_integer]", capabilities2.GL_EXT_texture_integer);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_texture_lod_bias]", capabilities2.GL_EXT_texture_lod_bias);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_texture_sRGB]", capabilities2.GL_EXT_texture_sRGB);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_vertex_shader]", capabilities2.GL_EXT_vertex_shader);
      playerUsageSnooper.addStatToSnooper("gl_caps[EXT_vertex_weighting]", capabilities2.GL_EXT_vertex_weighting);
      playerUsageSnooper.addStatToSnooper("gl_caps[gl_max_vertex_uniforms]", GL11.glGetInteger(35658));
      GL11.glGetError();
      playerUsageSnooper.addStatToSnooper("gl_caps[gl_max_fragment_uniforms]", GL11.glGetInteger(35657));
      GL11.glGetError();
      playerUsageSnooper.addStatToSnooper("gl_caps[gl_max_vertex_attribs]", GL11.glGetInteger(34921));
      GL11.glGetError();
      playerUsageSnooper.addStatToSnooper("gl_caps[gl_max_vertex_texture_image_units]", GL11.glGetInteger(35660));
      GL11.glGetError();
      playerUsageSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(34930));
      GL11.glGetError();
      playerUsageSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(35071));
      GL11.glGetError();
      playerUsageSnooper.addStatToSnooper("gl_max_texture_size", getGLMaximumTextureSize());
   }

   public void loadWorld(WorldClient worldClientIn) {
      this.loadWorld(worldClientIn, "");
   }

   public Session getSession() {
      return this.session;
   }

   public IStream getIStream() {
      return this.iStream;
   }

   private void startGame() throws LWJGLException, IOException {
      this.gameSettings = new GameSettings(this, this.mcDataDir);
      this.defaultResourcePacks.add(this.mcDefaultResourcePack);
      this.startTimerHackThread();
      if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
         this.displayWidth = this.gameSettings.overrideWidth;
         this.displayHeight = this.gameSettings.overrideHeight;
      }

      logger.info("LWJGL Version: " + Sys.getVersion());
      this.setWindowIcon();
      this.setInitialDisplayMode();
      this.createDisplay();
      this.checkGLError("Pre startup");
      GlStateManager.enableTexture2D();
      GlStateManager.shadeModel(7425);
      GlStateManager.clearDepth(1.0);
      GlStateManager.enableDepth();
      GlStateManager.depthFunc(515);
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.cullFace(1029);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);
      this.checkGLError("Startup");
      OpenGlHelper.initializeTextures();
      this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
      this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.registerMetadataSerializers();
      this.mcResourcePackRepository = new ResourcePackRepository(
         this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings
      );
      this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
      this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
      this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
      this.refreshResources();
      this.renderEngine = new TextureManager(this.mcResourceManager);
      this.mcResourceManager.registerReloadListener(this.renderEngine);
      Fonts.loadFonts();
      this.handleString("Initializing...", this.renderEngine);
      this.showAuth();
      this.skinManager = new SkinManager(this.renderEngine, new File(this.fileAssets, "skins"), this.sessionService);
      this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
      this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
      this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
      this.mcMusicTicker = new MusicTicker(this);
      this.fontRendererObj = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine);
      if (this.gameSettings.language != null) {
         this.fontRendererObj.setUnicodeFlag(this.isUnicode());
         this.fontRendererObj.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
      }

      this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine);
      this.mcResourceManager.registerReloadListener(this.fontRendererObj);
      this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
      this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
      this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
      AchievementList.openInventory.setStatStringFormatter(new IAchievementFormatter() {
         @Override
         public String formatString(String str) {
            try {
               return String.format(str, GameSettings.getKeyDisplayString(Minecraft.this.gameSettings.keyBindInventory.getKeyCode()));
            } catch (Exception exception2) {
               return "Error: " + exception2.getLocalizedMessage();
            }
         }
      });
      this.mouseHelper = new MouseHelper();
      Novoline.getNovoline().initEnvironment();

      Novoline.getNovoline().initialize("1|<|>|1");

      this.handleString("Loading game...", this.renderEngine);
      this.textureMapBlocks = new TextureMap("textures");
      this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
      this.renderEngine.loadTickableTexture(TextureMap.locationBlocksTexture, this.textureMapBlocks);
      this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
      this.textureMapBlocks.setBlurMipmapDirect(false, this.gameSettings.mipmapLevels > 0);
      this.modelManager = new ModelManager(this.textureMapBlocks);
      this.mcResourceManager.registerReloadListener(this.modelManager);
      this.renderItem = new RenderItem(this.renderEngine, this.modelManager);
      this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
      this.itemRenderer = new ItemRenderer(this);
      this.mcResourceManager.registerReloadListener(this.renderItem);
      this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
      this.mcResourceManager.registerReloadListener(this.entityRenderer);
      this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes(), this.gameSettings);
      this.mcResourceManager.registerReloadListener(this.blockRenderDispatcher);
      this.renderGlobal = new RenderGlobal(this);
      this.mcResourceManager.registerReloadListener(this.renderGlobal);
      this.guiAchievement = new GuiAchievement(this);
      GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
      this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
      this.checkGLError("Post startup");
      this.ingameGUI = new GuiIngame(this);
      if (this.serverName != null) {
         this.displayGuiScreen(new GuiConnecting(new NovolineMainMenu(), this, this.serverName, this.serverPort));
      } else {
         this.displayGuiScreen(new NovolineMainMenu());
      }

      this.renderEngine.deleteTexture(this.mojangLogo);
      this.mojangLogo = null;
      this.loadingScreen = new LoadingScreenRenderer(this);
      if (this.gameSettings.fullscreenOption && !this.fullscreen) {
         this.toggleFullscreen();
      }

      try {
         Display.setVSyncEnabled(this.gameSettings.enableVsync);
      } catch (OpenGLException openGLException) {
         this.gameSettings.enableVsync = false;
         this.gameSettings.saveOptions();
      }

      this.renderGlobal.makeEntityOutlineShader();
   }

   public boolean isUnicode() {
      return this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public MusicTicker getMusicTicker() {
      return this.mcMusicTicker;
   }

   private void setWindowIcon() {
      if (Util.getOSType() != net.minecraft.util.Util.EnumOS.OSX) {
         InputStream inputStream = null;
         InputStream icon16 = null;

         try {
            inputStream = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
            icon16 = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));
            if (inputStream != null && icon16 != null) {
               Display.setIcon(new ByteBuffer[]{this.readImageToBuffer(inputStream), this.readImageToBuffer(icon16)});
            }
         } catch (IOException ioException) {
            logger.error("Couldn't set icon", ioException);
         } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(icon16);
         }
      }
   }

   @Override
   public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
      Validate.notNull(runnableToSchedule);
      return this.addScheduledTask(Executors.callable(runnableToSchedule));
   }

   public void setRenderViewEntity(Entity viewingEntity) {
      this.renderViewEntity = viewingEntity;
      this.entityRenderer.loadEntityShader(viewingEntity);
   }
}
