package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Util;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer implements Runnable, ICommandSender, IThreadListener, IPlayerUsage {
   public int percentDone;
   private final PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("server", this, getCurrentTimeMillis());
   private final List<ITickable> playersOnline = Lists.newArrayList();
   private boolean startProfiling;
   public final Profiler theProfiler = new Profiler();
   private final ServerStatusResponse statusResponse = new ServerStatusResponse();
   private boolean pvpEnabled;
   private final Random random = new Random();
   private boolean canSpawnAnimals;
   private int serverPort = -1;
   private boolean canSpawnNPCs;
   private ServerConfigurationManager serverConfigManager;
   private boolean serverRunning = true;
   private String motd;
   private static final Logger logger = LogManager.getLogger();
   public long[][] timeOfLastDimensionTick;
   private int maxPlayerIdleMinutes = 0;
   public final long[] tickTimeArray = new long[100];
   private String userMessage;
   public String currentTask;
   private String worldName;
   public static final File USER_CACHE_FILE = new File("usercache.json");
   private boolean serverIsRunning;
   private KeyPair serverKeyPair;
   private String resourcePackUrl = "";
   private String resourcePackHash = "";
   private long nanoTimeSinceStatusRefresh = 0L;
   private static MinecraftServer mcServer;
   private boolean onlineMode;
   private int tickCounter;
   private long timeOfLastWarning;
   private boolean worldIsBeingDeleted;
   protected final Queue<FutureTask<?>> futureTaskQueue = Queues.newArrayDeque();
   private long currentTime = getCurrentTimeMillis();
   private String serverOwner;
   protected final Proxy serverProxy;
   private final File anvilFile;
   private final NetworkSystem networkSystem;
   private boolean isDemo;
   private final PlayerProfileCache profileCache;
   protected final ICommandManager commandManager;
   private boolean enableBonusChest;
   private Thread serverThread;
   private boolean isGamemodeForced;
   private String folderName;
   public WorldServer[] worldServers;
   private final ISaveFormat anvilConverterForAnvilFile;
   private boolean allowFlight;
   private final YggdrasilAuthenticationService authService;
   private final MinecraftSessionService sessionService;
   private int buildLimit;
   private boolean serverStopped;
   private final GameProfileRepository profileRepo;

   @Override
   public void addServerTypeToSnooper(PlayerUsageSnooper snooper) {
      snooper.addStatToSnooper("singleplayer", this.isSinglePlayer());
      snooper.addStatToSnooper("server_brand", this.getServerModName());
      snooper.addStatToSnooper("gui_supported", GraphicsEnvironment.isHeadless() ? "headless" : "supported");
      snooper.addStatToSnooper("dedicated", this.isDedicatedServer());
   }

   public void update5() {
      this.allowFlight = true;
   }

   @Override
   public BlockPos getPosition() {
      return BlockPos.ORIGIN;
   }

   public void update4() {
      this.onlineMode = true;
   }

   public String getServerModName() {
      return "vanilla";
   }

   public PlayerUsageSnooper getPlayerUsageSnooper() {
      return this.usageSnooper;
   }

   @Override
   public void setCommandStat(net.minecraft.command.CommandResultStats.Type type, int amount) {
   }

   public void updateTimeLightAndEntities() {
      this.theProfiler.startSection("jobs");
      synchronized (this.futureTaskQueue) {
         while (!this.futureTaskQueue.isEmpty()) {
            Util.runTask(this.futureTaskQueue.poll(), logger);
         }
      }

      this.theProfiler.endStartSection("levels");

      for (int i = 0; i < this.worldServers.length; i++) {
         long l = System.nanoTime();
         if (i == 0 || this.getAllowNether()) {
            WorldServer worldServer = this.worldServers[i];
            this.theProfiler.startSection(worldServer.getWorldInfo().getWorldName());
            if (this.tickCounter % 20 == 0) {
               this.theProfiler.startSection("timeSync");
               this.serverConfigManager
                  .sendPacketToAllPlayersInDimension(
                     new S03PacketTimeUpdate(worldServer.getTotalWorldTime(), worldServer.getWorldTime(), worldServer.getGameRules().getBoolean("doDaylightCycle")),
                     worldServer.provider.getDimensionId()
                  );
               this.theProfiler.endSection();
            }

            this.theProfiler.startSection("tick");

            try {
               worldServer.tick();
            } catch (Throwable throwable) {
               CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Exception ticking world");
               worldServer.addWorldInfoToCrashReport(crashReport);
               throw new ReportedException(crashReport);
            }

            try {
               worldServer.updateEntities();
            } catch (Throwable throwable2) {
               CrashReport crashReport2 = CrashReport.makeCrashReport(throwable2, "Exception ticking world entities");
               worldServer.addWorldInfoToCrashReport(crashReport2);
               throw new ReportedException(crashReport2);
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("tracker");
            worldServer.getEntityTracker().updateTrackedEntities();
            this.theProfiler.endSection();
            this.theProfiler.endSection();
         }

         this.timeOfLastDimensionTick[i][this.tickCounter % 100] = System.nanoTime() - l;
      }

      this.theProfiler.endStartSection("connection");
      this.getNetworkSystem().networkTick();
      this.theProfiler.endStartSection("players");
      this.serverConfigManager.onTick();
      this.theProfiler.endStartSection("tickables");

      for (int i2 = 0; i2 < this.playersOnline.size(); i2++) {
         this.playersOnline.get(i2).update();
      }

      this.theProfiler.endSection();
   }

   @Override
   public void addChatMessage(IChatComponent component) {
      logger.info(component.getUnformattedText());
   }

   public void enableProfiling() {
      this.startProfiling = true;
   }

   public ICommandManager getCommandManager() {
      return this.commandManager;
   }

   public abstract net.minecraft.world.WorldSettings.GameType getGameType();

   public boolean isPVPEnabled() {
      return this.pvpEnabled;
   }

   private void addFaviconToStatusResponse(ServerStatusResponse response) {
      File file = this.getFile("server-icon.png");
      if (file.isFile()) {
         ByteBuf byteBuf = Unpooled.buffer();

         try {
            BufferedImage bufferedImage = ImageIO.read(file);
            Validate.validState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide");
            Validate.validState(bufferedImage.getHeight() == 64, "Must be 64 pixels high");
            ImageIO.write(bufferedImage, "PNG", new ByteBufOutputStream(byteBuf));
            ByteBuf byteBuf2 = Base64.encode(byteBuf);
            response.setFavicon("data:image/png;base64," + byteBuf2.toString(Charsets.UTF_8));
         } catch (Exception exception2) {
            logger.error("Couldn't load server icon", exception2);
         } finally {
            byteBuf.release();
         }
      }
   }

   public void tick() {
      long l = System.nanoTime();
      this.tickCounter++;
      if (this.startProfiling) {
         this.startProfiling = false;
         this.theProfiler.profilingEnabled = true;
         this.theProfiler.clearProfiling();
      }

      this.theProfiler.startSection("root");
      this.updateTimeLightAndEntities();
      if (l - this.nanoTimeSinceStatusRefresh >= 5000000000L) {
         this.nanoTimeSinceStatusRefresh = l;
         this.statusResponse.setPlayerCountData(new net.minecraft.network.ServerStatusResponse.PlayerCountData(this.getMaxPlayers(), this.getCurrentPlayerCount()));
         GameProfile[] agameprofile = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
         int count = MathHelper.getRandomIntegerInRange(this.random, 0, this.getCurrentPlayerCount() - agameprofile.length);

         for (int nestedIndex = 0; nestedIndex < agameprofile.length; nestedIndex++) {
            agameprofile[nestedIndex] = this.serverConfigManager.getPlayerList().get(count + nestedIndex).getGameProfile();
         }

         Collections.shuffle(Arrays.asList(agameprofile));
         this.statusResponse.getPlayerCountData().setPlayers(agameprofile);
      }

      if (this.tickCounter % 900 == 0) {
         this.theProfiler.startSection("save");
         this.serverConfigManager.saveAllPlayerData();
         this.saveAllWorlds(true);
         this.theProfiler.endSection();
      }

      this.theProfiler.startSection("tallying");
      this.tickTimeArray[this.tickCounter % 100] = System.nanoTime() - l;
      this.theProfiler.endSection();
      this.theProfiler.startSection("snooper");
      if (!this.usageSnooper.isSnooperRunning() && this.tickCounter > 100) {
         this.usageSnooper.startSnooper();
      }

      if (this.tickCounter % 6000 == 0) {
         this.usageSnooper.addMemoryStatsToSnooper();
      }

      this.theProfiler.endSection();
      this.theProfiler.endSection();
   }

   @Override
   public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
      return true;
   }

   protected synchronized void setUserMessage(String message) {
      this.userMessage = message;
   }

   public KeyPair getKeyPair() {
      return this.serverKeyPair;
   }

   protected ServerCommandManager createNewCommandManager() {
      return new ServerCommandManager();
   }

   public PlayerProfileCache getPlayerProfileCache() {
      return this.profileCache;
   }

   public boolean isServerStopped() {
      return this.serverStopped;
   }

   public void update() {
      this.buildLimit = 256;
   }

   public String[] getAllUsernames() {
      return this.serverConfigManager.getAllUsernames();
   }

   public boolean isFlightAllowed() {
      return this.allowFlight;
   }

   protected boolean allowSpawnMonsters() {
      return true;
   }

   public NetworkSystem getNetworkSystem() {
      return this.networkSystem;
   }

   public void setPlayerIdleTimeout(int idleTimeout) {
      this.maxPlayerIdleMinutes = idleTimeout;
   }

   protected void clearCurrentTask() {
      this.currentTask = null;
      this.percentDone = 0;
   }

   public String getMinecraftVersion() {
      return "1.8.9";
   }

   public int getMaxWorldSize() {
      return 29999984;
   }

   public void initiateShutdown() {
      this.serverRunning = false;
   }

   public abstract int getOpPermissionLevel();

   protected void finalTick(CrashReport report) {
   }

   public MinecraftServer(File workDir, Proxy proxy, File profileCacheDir) {
      this.serverProxy = proxy;
      mcServer = this;
      this.anvilFile = workDir;
      this.networkSystem = new NetworkSystem(this);
      this.profileCache = new PlayerProfileCache(this, profileCacheDir);
      this.commandManager = this.createNewCommandManager();
      this.anvilConverterForAnvilFile = new AnvilSaveConverter(workDir);
      this.authService = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
      this.sessionService = this.authService.createMinecraftSessionService();
      this.profileRepo = this.authService.createProfileRepository();
   }

   public void logWarning(String msg) {
      logger.warn(msg);
   }

   @Override
   public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
      Validate.notNull(runnableToSchedule);
      return this.callFromMainThread(Executors.callable(runnableToSchedule));
   }

   public int getMaxPlayerIdleMinutes() {
      return this.maxPlayerIdleMinutes;
   }

   @Override
   public void addServerStatsToSnooper(PlayerUsageSnooper snooper) {
      snooper.addClientStat("whitelist_enabled", false);
      snooper.addClientStat("whitelist_count", 0);
      if (this.serverConfigManager != null) {
         snooper.addClientStat("players_current", this.getCurrentPlayerCount());
         snooper.addClientStat("players_max", this.getMaxPlayers());
         snooper.addClientStat("players_seen", this.serverConfigManager.getAvailablePlayerDat().length);
      }

      snooper.addClientStat("uses_auth", this.onlineMode);
      snooper.addClientStat("gui_state", this.alwaysFalse() ? "enabled" : "disabled");
      snooper.addClientStat("run_time", (getCurrentTimeMillis() - snooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
      snooper.addClientStat("avg_tick_ms", (int)(MathHelper.average(this.tickTimeArray) * 1.0E-6));
      int i = 0;
      if (this.worldServers != null) {
         for (int i2 = 0; i2 < this.worldServers.length; i2++) {
            if (this.worldServers[i2] != null) {
               WorldServer worldServer = this.worldServers[i2];
               WorldInfo worldInfo = worldServer.getWorldInfo();
               snooper.addClientStat("world[" + i + "][dimension]", worldServer.provider.getDimensionId());
               snooper.addClientStat("world[" + i + "][mode]", worldInfo.getGameType());
               snooper.addClientStat("world[" + i + "][difficulty]", worldServer.getDifficulty());
               snooper.addClientStat("world[" + i + "][hardcore]", worldInfo.isHardcoreModeEnabled());
               snooper.addClientStat("world[" + i + "][generator_name]", worldInfo.getTerrainType().getWorldTypeName());
               snooper.addClientStat("world[" + i + "][generator_version]", worldInfo.getTerrainType().getGeneratorVersion());
               snooper.addClientStat("world[" + i + "][height]", this.buildLimit);
               snooper.addClientStat("world[" + i + "][chunks_loaded]", worldServer.getChunkProvider().getLoadedChunkCount());
               i++;
            }
         }
      }

      snooper.addClientStat("worlds", i);
   }

   @Override
   public boolean sendCommandFeedback() {
      return getServer().worldServers[0].getGameRules().getBoolean("sendCommandFeedback");
   }

   protected void convertMapIfNeeded(String worldNameIn) {
      if (this.getActiveAnvilConverter().isOldMapFormat(worldNameIn)) {
         logger.info("Converting map!");
         this.setUserMessage("menu.convertingLevel");
         this.getActiveAnvilConverter().convertMapFormat(worldNameIn, new IProgressUpdate() {
            private long startTime = System.currentTimeMillis();

            @Override
            public void displaySavingString(String text2) {
            }

            @Override
            public void displayLoadingString(String text3) {
            }

            @Override
            public void setLoadingProgress(int progress) {
               if (System.currentTimeMillis() - this.startTime >= 1000L) {
                  this.startTime = System.currentTimeMillis();
                  MinecraftServer.logger.info("Converting... " + progress + "%");
               }
            }

            @Override
            public void setDoneWorking() {
            }

            @Override
            public void resetProgressAndMessage(String text4) {
            }
         });
      }
   }

   public String getServerOwner() {
      return this.serverOwner;
   }

   public void startServerThread() {
      this.serverThread = new Thread(this, "Server thread");
      this.serverThread.start();
   }

   protected void initialWorldChunkLoad() {
      int index = 0;
      this.setUserMessage("menu.generatingTerrain");
      logger.info("Preparing start region for level " + 0);
      WorldServer worldServer = this.worldServers[0];
      BlockPos spawnPoint = worldServer.getSpawnPoint();
      long currentTimeMillis2 = getCurrentTimeMillis();

      for (int i = -192; i <= 192 && this.isServerRunning(); i += 16) {
         for (int i2 = -192; i2 <= 192 && this.isServerRunning(); i2 += 16) {
            long currentTimeMillis3 = getCurrentTimeMillis();
            if (currentTimeMillis3 - currentTimeMillis2 > 1000L) {
               this.outputPercentRemaining("Preparing spawn area", index * 100 / 625);
               currentTimeMillis2 = currentTimeMillis3;
            }

            index++;
            worldServer.theChunkProviderServer.loadChunk(spawnPoint.getX() + i >> 4, spawnPoint.getZ() + i2 >> 4);
         }
      }

      this.clearCurrentTask();
   }

   public boolean isBlockProtected(World worldIn, BlockPos pos, EntityPlayer playerIn) {
      return false;
   }

   public boolean isDemo() {
      return this.isDemo;
   }

   protected void systemExitNow() {
   }

   public ISaveFormat getActiveAnvilConverter() {
      return this.anvilConverterForAnvilFile;
   }

   public abstract boolean shouldUseNativeTransport();

   public boolean isServerInOnlineMode() {
      return this.onlineMode;
   }

   @Override
   public boolean isSnooperEnabled() {
      return true;
   }

   public ServerConfigurationManager getConfigurationManager() {
      return this.serverConfigManager;
   }

   public void setMOTD(String motdIn) {
      this.motd = motdIn;
   }

   public MinecraftSessionService getMinecraftSessionService() {
      return this.sessionService;
   }

   @Override
   public World getEntityWorld() {
      return this.worldServers[0];
   }

   public int getNetworkCompressionTreshold() {
      return 256;
   }

   public void setConfigManager(ServerConfigurationManager configManager) {
      this.serverConfigManager = configManager;
   }

   public void refreshStatusNextTick() {
      this.nanoTimeSinceStatusRefresh = 0L;
   }

   public String getResourcePackUrl() {
      return this.resourcePackUrl;
   }

   public String getMOTD() {
      return this.motd;
   }

   protected abstract boolean startServer() throws IOException;

   protected void setInstance() {
      mcServer = this;
   }

   public List<String> getTabCompletions(ICommandSender sender, String input, BlockPos pos) {
      ArrayList arrayList = Lists.newArrayList();
      if (input.startsWith("/")) {
         input = input.substring(1);
         boolean flag = !input.contains(" ");
         List tabCompletionOptions = this.commandManager.getTabCompletionOptions(sender, input, pos);
         if (tabCompletionOptions != null) {
            for (String text2 : (Iterable<String>) tabCompletionOptions) {
               if (flag) {
                  arrayList.add("/" + text2);
               } else {
                  arrayList.add(text2);
               }
            }
         }

         return arrayList;
      } else {
         String[] texts = input.split(" ", -1);
         String text3 = texts[texts.length - 1];

         for (String text4 : this.serverConfigManager.getAllUsernames()) {
            if (CommandBase.doesStringStartWith(text3, text4)) {
               arrayList.add(text4);
            }
         }

         return arrayList;
      }
   }

   public boolean getForceGamemode() {
      return this.isGamemodeForced;
   }

   @Override
   public Entity getCommandSenderEntity() {
      return null;
   }

   public void setFolderName(String name) {
      this.folderName = name;
   }

   public boolean getAllowNether() {
      return true;
   }

   public void update6() {
      this.canSpawnAnimals = true;
   }

   public boolean alwaysFalse() {
      return false;
   }

   public String getWorldName() {
      return this.worldName;
   }

   public int getTickCounter() {
      return this.tickCounter;
   }

   public MinecraftServer(Proxy proxy, File workDir) {
      this.serverProxy = proxy;
      mcServer = this;
      this.anvilFile = null;
      this.networkSystem = null;
      this.profileCache = new PlayerProfileCache(this, workDir);
      this.commandManager = null;
      this.anvilConverterForAnvilFile = null;
      this.authService = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
      this.sessionService = this.authService.createMinecraftSessionService();
      this.profileRepo = this.authService.createProfileRepository();
   }

   protected void setResourcePackFromWorld(String worldNameIn, ISaveHandler saveHandlerIn) {
      File file2 = new File(saveHandlerIn.getWorldDirectory(), "resources.zip");
      if (file2.isFile()) {
         this.setResourcePack("level://" + worldNameIn + "/" + file2.getName(), "");
      }
   }

   @Override
   public String getName() {
      return "Server";
   }

   public WorldServer worldServerForDimension(int dimension) {
      return dimension == -1 ? this.worldServers[1] : (dimension == 1 ? this.worldServers[2] : this.worldServers[0]);
   }

   public CrashReport addServerInfoToCrashReport(CrashReport report) {
      report.getCategory().addCrashSectionCallable("Profiler Position", new Callable<String>() {
         public String call() throws Exception {
            return MinecraftServer.this.theProfiler.profilingEnabled ? MinecraftServer.this.theProfiler.getNameOfLastSection() : "N/A (disabled)";
         }
      });
      if (this.serverConfigManager != null) {
         report.getCategory()
            .addCrashSectionCallable(
               "Player Count",
               new Callable<String>() {
                  public String call() {
                     return MinecraftServer.this.serverConfigManager.getCurrentPlayerCount()
                        + " / "
                        + MinecraftServer.this.serverConfigManager.getMaxPlayers()
                        + "; "
                        + MinecraftServer.this.serverConfigManager.getPlayerList();
                  }
               }
            );
      }

      return report;
   }

   public void canCreateBonusChest(boolean enable) {
      this.enableBonusChest = enable;
   }

   public void setResourcePack(String url, String hash) {
      this.resourcePackUrl = url;
      this.resourcePackHash = hash;
   }

   public void setDifficultyForAllWorlds(EnumDifficulty difficulty) {
      for (int index = 0; index < this.worldServers.length; index++) {
         WorldServer worldServer = this.worldServers[index];
         if (worldServer != null) {
            if (worldServer.getWorldInfo().isHardcoreModeEnabled()) {
               worldServer.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
               worldServer.setAllowedSpawnTypes(true, true);
            } else if (this.isSinglePlayer()) {
               worldServer.getWorldInfo().setDifficulty(difficulty);
               worldServer.setAllowedSpawnTypes(worldServer.getDifficulty() != EnumDifficulty.PEACEFUL, true);
            } else {
               worldServer.getWorldInfo().setDifficulty(difficulty);
               worldServer.setAllowedSpawnTypes(this.allowSpawnMonsters(), this.canSpawnAnimals);
            }
         }
      }
   }

   public void setKeyPair(KeyPair keyPair) {
      this.serverKeyPair = keyPair;
   }

   @Override
   public void run() {
      try {
         if (this.startServer()) {
            this.currentTime = getCurrentTimeMillis();
            long l = 0L;
            this.statusResponse.setServerDescription(new ChatComponentText(this.motd));
            this.statusResponse.setProtocolVersionInfo(new net.minecraft.network.ServerStatusResponse.MinecraftProtocolVersionIdentifier("1.8.9", 47));
            this.addFaviconToStatusResponse(this.statusResponse);

            while (this.serverRunning) {
               long currentTimeMillis2 = getCurrentTimeMillis();
               long l2 = currentTimeMillis2 - this.currentTime;
               if (l2 > 2000L && this.currentTime - this.timeOfLastWarning >= 15000L) {
                  logger.warn(
                     "Can't keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)", l2, l2 / 50L
                  );
                  l2 = 2000L;
                  this.timeOfLastWarning = this.currentTime;
               }

               if (l2 < 0L) {
                  logger.warn("Time ran backwards! Did the system time change?");
                  l2 = 0L;
               }

               l += l2;
               this.currentTime = currentTimeMillis2;
               if (this.worldServers[0].areAllPlayersAsleep()) {
                  this.tick();
                  l = 0L;
               } else {
                  while (l > 50L) {
                     l -= 50L;
                     this.tick();
                  }
               }

               Thread.sleep(Math.max(1L, 50L - l));
               this.serverIsRunning = true;
            }
         } else {
            this.finalTick((CrashReport)null);
         }
      } catch (Throwable throwable) {
         logger.error("Encountered an unexpected exception", throwable);
         CrashReport crashReport;
         if (throwable instanceof ReportedException) {
            crashReport = this.addServerInfoToCrashReport(((ReportedException)throwable).getCrashReport());
         } else {
            crashReport = this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", throwable));
         }

         File file2 = new File(
            new File(this.getDataDirectory(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt"
         );
         if (crashReport.saveToFile(file2)) {
            logger.error("This crash report has been saved to: " + file2.getAbsolutePath());
         } else {
            logger.error("We were unable to save this crash report to disk.");
         }

         this.finalTick(crashReport);
      } finally {
         try {
            this.serverStopped = true;
            this.stopServer();
         } catch (Throwable throwable2) {
            logger.error("Exception stopping the server", throwable2);
         } finally {
            this.systemExitNow();
         }
      }
   }

   protected void outputPercentRemaining(String message, int percent) {
      this.currentTask = message;
      this.percentDone = percent;
      logger.info(message + ": " + percent + "%");
   }

   public boolean isAnnouncingPlayerAchievements() {
      return true;
   }

   public String getResourcePackHash() {
      return this.resourcePackHash;
   }

   public int getMaxPlayers() {
      return this.serverConfigManager.getMaxPlayers();
   }

   public abstract boolean alwaysFalse2();

   protected void saveAllWorlds(boolean dontLog) {
      if (!this.worldIsBeingDeleted) {
         for (WorldServer worldServer : this.worldServers) {
            if (worldServer != null) {
               if (!dontLog) {
                  logger.info("Saving chunks for level '" + worldServer.getWorldInfo().getWorldName() + "'/" + worldServer.provider.getDimensionName());
               }

               try {
                  worldServer.handleIProgressUpdate((IProgressUpdate)null);
               } catch (MinecraftException minecraftException) {
                  logger.warn(minecraftException.getMessage());
               }
            }
         }
      }
   }

   public static long getCurrentTimeMillis() {
      return System.currentTimeMillis();
   }

   public abstract boolean isDedicatedServer();

   public void setWorldName(String text2) {
      this.worldName = text2;
   }

   public GameProfile[] getGameProfiles() {
      return this.serverConfigManager.getAllProfiles();
   }

   public void update2() {
      this.canSpawnNPCs = true;
   }

   public abstract boolean shouldBroadcastRconToOps();

   public GameProfileRepository getGameProfileRepository() {
      return this.profileRepo;
   }

   public int getBuildLimit() {
      return this.buildLimit;
   }

   public synchronized String getUserMessage() {
      return this.userMessage;
   }

   @Override
   public boolean isCallingFromMinecraftThread() {
      return Thread.currentThread() == this.serverThread;
   }

   public abstract boolean isHardcore();

   public boolean serverIsInRunLoop() {
      return this.serverIsRunning;
   }

   public boolean isAnvilFileSet() {
      return this.anvilFile != null;
   }

   public int getSpawnProtectionSize() {
      return 16;
   }

   public abstract boolean isCommandBlockEnabled();

   @Override
   public Vec3 getPositionVector() {
      return new Vec3(0.0, 0.0, 0.0);
   }

   public void setIsDemo(boolean flag) {
      this.isDemo = flag;
   }

   public int getCurrentPlayerCount() {
      return this.serverConfigManager.getCurrentPlayerCount();
   }

   public boolean getCanSpawnAnimals() {
      return this.canSpawnAnimals;
   }

   public void setGameType(net.minecraft.world.WorldSettings.GameType gameMode) {
      for (int index = 0; index < this.worldServers.length; index++) {
         getServer().worldServers[index].getWorldInfo().setGameType(gameMode);
      }
   }

   public File getFile(String fileName) {
      return new File(this.getDataDirectory(), fileName);
   }

   public static MinecraftServer getServer() {
      return mcServer;
   }

   public Entity getEntityFromUuid(UUID uUID) {
      for (WorldServer worldServer : this.worldServers) {
         if (worldServer != null) {
            Entity entityFromUuid = worldServer.getEntityFromUuid(uUID);
            if (entityFromUuid != null) {
               return entityFromUuid;
            }
         }
      }

      return null;
   }

   public abstract String shareToLAN(net.minecraft.world.WorldSettings.GameType gameType, boolean flag);

   @Override
   public IChatComponent getDisplayName() {
      return new ChatComponentText(this.getName());
   }

   public void stopServer() {
      if (!this.worldIsBeingDeleted) {
         logger.info("Stopping server");
         if (this.getNetworkSystem() != null) {
            this.getNetworkSystem().terminateEndpoints();
         }

         if (this.serverConfigManager != null) {
            logger.info("Saving players");
            this.serverConfigManager.saveAllPlayerData();
            this.serverConfigManager.removeAllPlayers();
         }

         if (this.worldServers != null) {
            logger.info("Saving worlds");
            this.saveAllWorlds(false);

            for (int i = 0; i < this.worldServers.length; i++) {
               this.worldServers[i].flush();
            }
         }

         if (this.usageSnooper.isSnooperRunning()) {
            this.usageSnooper.stopSnooper();
         }
      }
   }

   public abstract boolean shouldBroadcastConsoleToOps();

   public boolean isServerRunning() {
      return this.serverRunning;
   }

   public void update3() {
      this.pvpEnabled = true;
   }

   public boolean getCanSpawnNPCs() {
      return this.canSpawnNPCs;
   }

   public void deleteWorldAndStopServer() {
      this.worldIsBeingDeleted = true;
      this.getActiveAnvilConverter().flushCache();

      for (int index = 0; index < this.worldServers.length; index++) {
         WorldServer worldServer = this.worldServers[index];
         if (worldServer != null) {
            worldServer.flush();
         }
      }

      this.getActiveAnvilConverter().deleteWorldDirectory(this.worldServers[0].getSaveHandler().getWorldDirectoryName());
      this.initiateShutdown();
   }

   public <V> ListenableFuture<V> callFromMainThread(Callable<V> callable) {
      Validate.notNull(callable);
      if (!this.isCallingFromMinecraftThread() && !this.isServerStopped()) {
         ListenableFutureTask listenableFutureTask = ListenableFutureTask.create(callable);
         synchronized (this.futureTaskQueue) {
            this.futureTaskQueue.add(listenableFutureTask);
            return listenableFutureTask;
         }
      } else {
         try {
            return Futures.immediateFuture(callable.call());
         } catch (Exception exception2) {
            return Futures.immediateFailedCheckedFuture(exception2);
         }
      }
   }

   public File getDataDirectory() {
      return new File(".");
   }

   public ServerStatusResponse getServerStatusResponse() {
      return this.statusResponse;
   }

   public Proxy getServerProxy() {
      return this.serverProxy;
   }

   public void setServerOwner(String owner) {
      this.serverOwner = owner;
   }

   public boolean isSinglePlayer() {
      return this.serverOwner != null;
   }

   public String getFolderName() {
      return this.folderName;
   }
}
