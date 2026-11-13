package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.Callable;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.CryptManager;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.Util;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.ClearWater;
import net.optifine.reflect.Reflector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegratedServer extends MinecraftServer {
   private boolean isPublic;
   private long ticksSaveLast = 0L;
   public World difficultyUpdateWorld = null;
   public BlockPos difficultyUpdatePos = null;
   public DifficultyInstance difficultyLast = null;
   private boolean isGamePaused;
   private final Minecraft mc;
   private final WorldSettings theWorldSettings;
   private static final Logger logger = LogManager.getLogger();
   private ThreadLanServerPing lanServerPing;

   @Override
   public boolean shouldUseNativeTransport() {
      return false;
   }

   @Override
   public void stopServer() {
      super.stopServer();
      if (this.lanServerPing != null) {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }
   }

   public IntegratedServer(Minecraft mcIn) {
      super(mcIn.getProxy(), new File(mcIn.mcDataDir, USER_CACHE_FILE.getName()));
      this.mc = mcIn;
      this.theWorldSettings = null;
   }

   protected void loadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, String worldNameIn2) {
      this.convertMapIfNeeded(saveName);
      boolean active = Reflector.reflectorClass5.isActive();
      if (!active) {
         this.worldServers = new WorldServer[3];
         this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
      }

      ISaveHandler saveLoader = this.getActiveAnvilConverter().getSaveLoader(saveName, true);
      this.setResourcePackFromWorld(this.getFolderName(), saveLoader);
      WorldInfo worldInfo2 = saveLoader.loadWorldInfo();
      if (worldInfo2 == null) {
         worldInfo2 = new WorldInfo(this.theWorldSettings, worldNameIn);
      } else {
         worldInfo2.setWorldName(worldNameIn);
      }

      if (active) {
         WorldServer worldServer = this.isDemo()
            ? (WorldServer)new DemoWorldServer(this, saveLoader, worldInfo2, 0, this.theProfiler).init()
            : (WorldServer)new WorldServer(this, saveLoader, worldInfo2, 0, this.theProfiler).init();
         worldServer.initialize(this.theWorldSettings);
         Integer[] integers = (Integer[])Reflector.createObject2(Reflector.reflectorMethod4);
         Integer[] integers2 = integers;
         int length2 = integers.length;

         for (int i = 0; i < length2; i++) {
            int i2 = integers2[i];
            WorldServer worldServer2 = i2 == 0 ? worldServer : (WorldServer)new WorldServerMulti(this, saveLoader, i2, worldServer, this.theProfiler).init();
            worldServer2.addWorldAccess(new WorldManager(this, worldServer2));
            if (!this.isSinglePlayer()) {
               worldServer2.getWorldInfo().setGameType(this.getGameType());
            }

            if (Reflector.reflectorClass11.isActive()) {
               Reflector.checkReflectorConstructor(Reflector.reflectorConstructor14, worldServer2);
            }
         }

         this.getConfigurationManager().setPlayerManager(new WorldServer[]{worldServer});
         if (worldServer.getWorldInfo().getDifficulty() == null) {
            this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty);
         }
      } else {
         for (int i3 = 0; i3 < this.worldServers.length; i3++) {
            byte b = 0;
            if (i3 == 1) {
               b = -1;
            }

            if (i3 == 2) {
               b = 1;
            }

            if (i3 == 0) {
               if (this.isDemo()) {
                  this.worldServers[i3] = (WorldServer)new DemoWorldServer(this, saveLoader, worldInfo2, b, this.theProfiler).init();
               } else {
                  this.worldServers[i3] = (WorldServer)new WorldServer(this, saveLoader, worldInfo2, b, this.theProfiler).init();
               }

               this.worldServers[i3].initialize(this.theWorldSettings);
            } else {
               this.worldServers[i3] = (WorldServer)new WorldServerMulti(this, saveLoader, b, this.worldServers[0], this.theProfiler).init();
            }

            this.worldServers[i3].addWorldAccess(new WorldManager(this, this.worldServers[i3]));
         }

         this.getConfigurationManager().setPlayerManager(this.worldServers);
         if (this.worldServers[0].getWorldInfo().getDifficulty() == null) {
            this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty);
         }
      }

      this.initialWorldChunkLoad();
   }

   public IntegratedServer(Minecraft mcIn, String folderName, String worldName, WorldSettings settings) {
      super(new File(mcIn.mcDataDir, "saves"), mcIn.getProxy(), new File(mcIn.mcDataDir, USER_CACHE_FILE.getName()));
      this.setServerOwner(mcIn.getSession().getUsername());
      this.setFolderName(folderName);
      this.setWorldName(worldName);
      this.setIsDemo(mcIn.isDemo());
      this.canCreateBonusChest(settings.isBonusChestEnabled());
      this.update();
      this.setConfigManager(new IntegratedPlayerList(this));
      this.mc = mcIn;
      this.theWorldSettings = this.isDemo() ? DemoWorldServer.demoWorldSettings : settings;
      WorldInfo worldInfo2 = this.getActiveAnvilConverter().getSaveLoader(folderName, false).loadWorldInfo();
      if (worldInfo2 != null) {
         NBTTagCompound playerNBTTagCompound = worldInfo2.getPlayerNBTTagCompound();
         if (playerNBTTagCompound != null && playerNBTTagCompound.hasKey("Dimension")) {
            PacketThreadUtil.lastDimensionId = playerNBTTagCompound.getInteger("Dimension");
            this.mc.loadingScreen.setLoadingProgress(-1);
         }
      }
   }

   @Override
   public boolean isSnooperEnabled() {
      return Minecraft.getMinecraft().isSnooperEnabled();
   }

   public DifficultyInstance getDifficultyAsync(World arg1, BlockPos arg2) {
      this.difficultyUpdateWorld = arg1;
      this.difficultyUpdatePos = arg2;
      return this.difficultyLast;
   }

   @Override
   protected void finalTick(CrashReport report) {
      this.mc.crashed(report);
   }

   @Override
   public void tick() {
      this.onTick();
      boolean isGamePaused2 = this.isGamePaused;
      this.isGamePaused = Minecraft.getMinecraft().getNetHandler() != null && Minecraft.getMinecraft().isGamePaused();
      if (!isGamePaused2 && this.isGamePaused) {
         logger.info("Saving and pausing game...");
         this.getConfigurationManager().saveAllPlayerData();
         this.saveAllWorlds(false);
      }

      if (this.isGamePaused) {
         synchronized (this.futureTaskQueue) {
            while (!this.futureTaskQueue.isEmpty()) {
               Util.runTask(this.futureTaskQueue.poll(), logger);
            }
         }
      } else {
         super.tick();
         if (this.mc.gameSettings.renderDistanceChunks != this.getConfigurationManager().getViewDistance()) {
            logger.info("Changing view distance to {}, from {}", this.mc.gameSettings.renderDistanceChunks, this.getConfigurationManager().getViewDistance());
            this.getConfigurationManager().setViewDistance(this.mc.gameSettings.renderDistanceChunks);
         }

         if (this.mc.theWorld != null) {
            WorldInfo worldInfo2 = this.worldServers[0].getWorldInfo();
            WorldInfo worldInfo3 = this.mc.theWorld.getWorldInfo();
            if (!worldInfo2.isDifficultyLocked() && worldInfo3.getDifficulty() != worldInfo2.getDifficulty()) {
               logger.info("Changing difficulty to {}, from {}", worldInfo3.getDifficulty(), worldInfo2.getDifficulty());
               this.setDifficultyForAllWorlds(worldInfo3.getDifficulty());
            } else if (worldInfo3.isDifficultyLocked() && !worldInfo2.isDifficultyLocked()) {
               logger.info("Locking difficulty to {}", new Object[]{worldInfo3.getDifficulty()});

               for (WorldServer worldServer : this.worldServers) {
                  if (worldServer != null) {
                     worldServer.getWorldInfo().setDifficultyLocked(true);
                  }
               }
            }
         }
      }
   }

   @Override
   public CrashReport addServerInfoToCrashReport(CrashReport report) {
      report = super.addServerInfoToCrashReport(report);
      report.getCategory().addCrashSectionCallable("Type", new Callable<String>() {
         public String call() throws Exception {
            return "Integrated Server (map_client.txt)";
         }
      });
      report.getCategory()
         .addCrashSectionCallable(
            "Is Modded",
            new Callable<String>() {
               public String call() throws Exception {
                  String clientModName = ClientBrandRetriever.getClientModName();
                  if (!clientModName.equals("vanilla")) {
                     return "Definitely; Client brand changed to '" + clientModName + "'";
                  }

                  clientModName = IntegratedServer.this.getServerModName();
                  return !clientModName.equals("vanilla")
                     ? "Definitely; Server brand changed to '" + clientModName + "'"
                     : (
                        Minecraft.class.getSigners() == null
                           ? "Very likely; Jar signature invalidated"
                           : "Probably not. Jar signature remains and both client + server brands are untouched."
                     );
               }
            }
         );
      return report;
   }

   @Override
   public boolean isDedicatedServer() {
      return false;
   }

   @Override
   public int getOpPermissionLevel() {
      return 4;
   }

   private void fixWorldWeather(WorldServer arg1) {
      WorldInfo worldInfo = arg1.getWorldInfo();
      if (worldInfo.isRaining() || worldInfo.isThundering()) {
         worldInfo.setRainTime(0);
         worldInfo.setRaining(false);
         arg1.setRainStrength(0.0F);
         worldInfo.setThunderTime(0);
         worldInfo.setThundering(false);
         arg1.setThunderStrength(0.0F);
         this.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(2, 0.0F));
         this.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(7, 0.0F));
         this.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(8, 0.0F));
      }
   }

   private void onTick() {
      for (WorldServer worldServer : Arrays.asList(this.worldServers)) {
         this.onTick(worldServer);
      }
   }

   @Override
   public void setDifficultyForAllWorlds(EnumDifficulty difficulty) {
      super.setDifficultyForAllWorlds(difficulty);
      if (this.mc.theWorld != null) {
         this.mc.theWorld.getWorldInfo().setDifficulty(difficulty);
      }
   }

   private void onTick(WorldServer arg1) {
      if (!Config.isTimeDefault()) {
         this.fixWorldTime(arg1);
      }

      if (!Config.isWeatherEnabled()) {
         this.fixWorldWeather(arg1);
      }

      if (Config.waterOpacityChanged) {
         Config.waterOpacityChanged = false;
         ClearWater.updateWaterOpacity(Config.getGameSettings(), arg1);
      }

      if (this.difficultyUpdateWorld == arg1 && this.difficultyUpdatePos != null) {
         this.difficultyLast = arg1.getDifficultyForLocation(this.difficultyUpdatePos);
         this.difficultyUpdateWorld = null;
         this.difficultyUpdatePos = null;
      }
   }

   @Override
   public void addServerStatsToSnooper(PlayerUsageSnooper playerUsageSnooper) {
      super.addServerStatsToSnooper(playerUsageSnooper);
      playerUsageSnooper.addClientStat("snooper_partner", this.mc.getPlayerUsageSnooper().getUniqueID());
   }

   @Override
   public boolean shouldBroadcastConsoleToOps() {
      return true;
   }

   @Override
   protected ServerCommandManager createNewCommandManager() {
      return new IntegratedServerCommandManager();
   }

   public void setStaticInstance() {
      this.setInstance();
   }

   @Override
   public void setGameType(net.minecraft.world.WorldSettings.GameType gameMode) {
      this.getConfigurationManager().setGameType(gameMode);
   }

   @Override
   protected boolean startServer() throws IOException {
      logger.info("Starting integrated minecraft server version 1.9");
      this.update4();
      this.update6();
      this.update2();
      this.update3();
      this.update5();
      logger.info("Generating keypair");
      this.setKeyPair(CryptManager.generateKeyPair());
      if (Reflector.reflectorMethod15.isActive()) {
         Object object = Reflector.createObject2(Reflector.reflectorMethod17);
         if (!Reflector.checkObject(object, Reflector.reflectorMethod15, this)) {
            return false;
         }
      }

      this.loadAllWorlds(
         this.getFolderName(),
         this.getWorldName(),
         this.theWorldSettings.getSeed(),
         this.theWorldSettings.getTerrainType(),
         this.theWorldSettings.getWorldName()
      );
      this.setMOTD(this.getServerOwner() + " - " + this.worldServers[0].getWorldInfo().getWorldName());
      if (Reflector.reflectorMethod16.isActive()) {
         Object object2 = Reflector.createObject2(Reflector.reflectorMethod17);
         if (Reflector.reflectorMethod16.getType() == boolean.class) {
            return Reflector.checkObject(object2, Reflector.reflectorMethod16, this);
         }

         Reflector.handleObject(object2, Reflector.reflectorMethod16, this);
      }

      return true;
   }

   @Override
   public net.minecraft.world.WorldSettings.GameType getGameType() {
      return this.theWorldSettings.getGameType();
   }

   @Override
   public boolean alwaysFalse2() {
      return false;
   }

   private void fixWorldTime(WorldServer arg1) {
      if (arg1.getWorldInfo().getGameType().getID() == 1) {
         long worldTime = arg1.getWorldTime();
         long l = worldTime % 24000L;
         if (Config.isTimeDayOnly()) {
            if (l <= 1000L) {
               arg1.setWorldTime(worldTime - l + 1001L);
            }

            if (l >= 11000L) {
               arg1.setWorldTime(worldTime - l + 24001L);
            }
         }

         if (Config.isTimeNightOnly()) {
            if (l <= 14000L) {
               arg1.setWorldTime(worldTime - l + 14001L);
            }

            if (l >= 22000L) {
               arg1.setWorldTime(worldTime - l + 24000L + 14001L);
            }
         }
      }
   }

   @Override
   public void saveAllWorlds(boolean dontLog) {
      if (dontLog) {
         int tickCounter = this.getTickCounter();
         int ofAutoSaveTicks2 = this.mc.gameSettings.ofAutoSaveTicks;
         if (tickCounter < this.ticksSaveLast + ofAutoSaveTicks2) {
            return;
         }

         this.ticksSaveLast = tickCounter;
      }

      super.saveAllWorlds(dontLog);
   }

   public boolean getPublic() {
      return this.isPublic;
   }

   @Override
   public File getDataDirectory() {
      return this.mc.mcDataDir;
   }

   @Override
   public boolean shouldBroadcastRconToOps() {
      return true;
   }

   @Override
   public boolean isCommandBlockEnabled() {
      return true;
   }

   @Override
   public void initiateShutdown() {
      if (!Reflector.reflectorClass39.isActive() || this.isServerRunning()) {
         Futures.getUnchecked(this.addScheduledTask(new Runnable() {
            @Override
            public void run() {
               for (EntityPlayerMP entityPlayerMP : Lists.newArrayList(IntegratedServer.this.getConfigurationManager().getPlayerList())) {
                  IntegratedServer.this.getConfigurationManager().playerLoggedOut(entityPlayerMP);
               }
            }
         }));
      }

      super.initiateShutdown();
      if (this.lanServerPing != null) {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }
   }

   @Override
   public boolean isHardcore() {
      return this.theWorldSettings.getHardcoreEnabled();
   }

   @Override
   public String shareToLAN(net.minecraft.world.WorldSettings.GameType type, boolean allowCheats) {
      try {
         int i = -1;

         try {
            i = HttpUtil.getSuitableLanPort();
         } catch (IOException ioException) {
         }

         if (i <= 0) {
            i = 25564;
         }

         this.getNetworkSystem().addLanEndpoint((InetAddress)null, i);
         logger.info("Started on " + i);
         this.isPublic = true;
         this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), i + "");
         this.lanServerPing.start();
         this.getConfigurationManager().setGameType(type);
         this.getConfigurationManager().setCommandsAllowedForAll(allowCheats);
         return i + "";
      } catch (IOException ioException2) {
         return null;
      }
   }
}
