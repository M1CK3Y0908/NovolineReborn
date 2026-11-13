package net.minecraft.world.storage;

import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class WorldInfo {
   private WorldType terrainType = WorldType.DEFAULT;
   private long randomSeed;
   private boolean difficultyLocked;
   private boolean raining;
   private String generatorOptions = "";
   private int spawnX;
   private int thunderTime;
   private double borderCenterX = 0.0;
   private double borderCenterZ = 0.0;
   private double borderSize = 6.0E7;
   private long worldTime;
   private long borderSizeLerpTime = 0L;
   private boolean thundering;
   public static final EnumDifficulty DEFAULT_DIFFICULTY = EnumDifficulty.NORMAL;
   private int spawnZ;
   private boolean mapFeaturesEnabled;
   private long lastTimePlayed;
   private int dimension;
   private double borderSizeLerpTarget = 0.0;
   private long totalTime;
   private double borderSafeZone = 5.0;
   private double borderDamagePerBlock = 0.2;
   private NBTTagCompound playerTag;
   private int borderWarningDistance = 5;
   private int rainTime;
   private boolean allowCommands;
   private int borderWarningTime = 15;
   private int cleanWeatherTime;
   private boolean hardcore;
   private GameRules theGameRules = new GameRules();
   private net.minecraft.world.WorldSettings.GameType theGameType;
   private int spawnY;
   private long sizeOnDisk;
   private int saveVersion;
   private String levelName;
   private EnumDifficulty difficulty;
   private boolean initialized;

   public boolean isRaining() {
      return this.raining;
   }

   public void setBorderWarningDistance(int amountOfBlocks) {
      this.borderWarningDistance = amountOfBlocks;
   }

   public double getBorderDamagePerBlock() {
      return this.borderDamagePerBlock;
   }

   public void setRaining(boolean isRaining) {
      this.raining = isRaining;
   }

   public void setBorderSafeZone(double amount) {
      this.borderSafeZone = amount;
   }

   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   public long getSeed() {
      return this.randomSeed;
   }

   public void setDifficultyLocked(boolean locked) {
      this.difficultyLocked = locked;
   }

   public int getRainTime() {
      return this.rainTime;
   }

   public void populateFromWorldSettings(WorldSettings settings) {
      this.randomSeed = settings.getSeed();
      this.theGameType = settings.getGameType();
      this.mapFeaturesEnabled = settings.isMapFeaturesEnabled();
      this.hardcore = settings.getHardcoreEnabled();
      this.terrainType = settings.getTerrainType();
      this.generatorOptions = settings.getWorldName();
      this.allowCommands = settings.areCommandsAllowed();
   }

   public void setWorldTotalTime(long time) {
      this.totalTime = time;
   }

   public long getWorldTotalTime() {
      return this.totalTime;
   }

   public NBTTagCompound cloneNBTCompound(NBTTagCompound nbt) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      this.updateTagCompound(nbtTagCompound, nbt);
      return nbtTagCompound;
   }

   public void getBorderCenterX(double posX) {
      this.borderCenterX = posX;
   }

   public double getBorderLerpTarget() {
      return this.borderSizeLerpTarget;
   }

   public int getSaveVersion() {
      return this.saveVersion;
   }

   public int getThunderTime() {
      return this.thunderTime;
   }

   public void getBorderCenterZ(double posZ) {
      this.borderCenterZ = posZ;
   }

   public void update5() {
      this.allowCommands = true;
   }

   public void setBorderWarningTime(int ticks) {
      this.borderWarningTime = ticks;
   }

   public void update4() {
      this.hardcore = false;
   }

   public void setTerrainType(WorldType type) {
      this.terrainType = type;
   }

   public double getBorderSize() {
      return this.borderSize;
   }

   public void setBorderLerpTarget(double lerpSize) {
      this.borderSizeLerpTarget = lerpSize;
   }

   public NBTTagCompound getNBTTagCompound() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      this.updateTagCompound(nbtTagCompound, this.playerTag);
      return nbtTagCompound;
   }

   public boolean isHardcoreModeEnabled() {
      return this.hardcore;
   }

   public String getWorldName() {
      return this.levelName;
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public void addToCrashReport(CrashReportCategory category) {
      category.addCrashSectionCallable("Level seed", new Callable<String>() {
         public String call() throws Exception {
            return String.valueOf(WorldInfo.this.getSeed());
         }
      });
      category.addCrashSectionCallable(
         "Level generator",
         new Callable<String>() {
            public String call() throws Exception {
               return String.format(
                  "ID %02d - %s, ver %d. Features enabled: %b",
                  WorldInfo.this.terrainType.getWorldTypeID(),
                  WorldInfo.this.terrainType.getWorldTypeName(),
                  WorldInfo.this.terrainType.getGeneratorVersion(),
                  WorldInfo.this.mapFeaturesEnabled
               );
            }
         }
      );
      category.addCrashSectionCallable("Level generator options", new Callable<String>() {
         public String call() throws Exception {
            return WorldInfo.this.generatorOptions;
         }
      });
      category.addCrashSectionCallable("Level spawn location", new Callable<String>() {
         public String call() throws Exception {
            return CrashReportCategory.getCoordinateInfo(WorldInfo.this.spawnX, WorldInfo.this.spawnY, WorldInfo.this.spawnZ);
         }
      });
      category.addCrashSectionCallable("Level time", new Callable<String>() {
         public String call() throws Exception {
            return String.format("%d game time, %d day time", WorldInfo.this.totalTime, WorldInfo.this.worldTime);
         }
      });
      category.addCrashSectionCallable("Level dimension", new Callable<String>() {
         public String call() throws Exception {
            return String.valueOf(WorldInfo.this.dimension);
         }
      });
      category.addCrashSectionCallable("Level storage version", new Callable<String>() {
         public String call() throws Exception {
            String message = "Unknown?";

            try {
               switch (WorldInfo.this.saveVersion) {
                  case 19132:
                     message = "McRegion";
                     break;
                  case 19133:
                     message = "Anvil";
               }
            } catch (Throwable throwable) {
            }

            return String.format("0x%05X - %s", WorldInfo.this.saveVersion, message);
         }
      });
      category.addCrashSectionCallable(
         "Level weather",
         new Callable<String>() {
            public String call() throws Exception {
               return String.format(
                  "Rain time: %d (now: %b), thunder time: %d (now: %b)",
                  WorldInfo.this.rainTime,
                  WorldInfo.this.raining,
                  WorldInfo.this.thunderTime,
                  WorldInfo.this.thundering
               );
            }
         }
      );
      category.addCrashSectionCallable(
         "Level game mode",
         new Callable<String>() {
            public String call() throws Exception {
               return String.format(
                  "Game mode: %s (ID %d). Hardcore: %b. Cheats: %b",
                  WorldInfo.this.theGameType.getName(),
                  WorldInfo.this.theGameType.getID(),
                  WorldInfo.this.hardcore,
                  WorldInfo.this.allowCommands
               );
            }
         }
      );
   }

   public long getWorldTime() {
      return this.worldTime;
   }

   public void setThunderTime(int time) {
      this.thunderTime = time;
   }

   public boolean isThundering() {
      return this.thundering;
   }

   public void setGameType(net.minecraft.world.WorldSettings.GameType type) {
      this.theGameType = type;
   }

   public void setWorldName(String worldName) {
      this.levelName = worldName;
   }

   public void setBorderSize(double size) {
      this.borderSize = size;
   }

   public long getSizeOnDisk() {
      return this.sizeOnDisk;
   }

   public double getBorderSafeZone() {
      return this.borderSafeZone;
   }

   public void update6() {
      this.saveVersion = 19133;
   }

   public void setDifficulty(EnumDifficulty newDifficulty) {
      this.difficulty = newDifficulty;
   }

   public int getSpawnX() {
      return this.spawnX;
   }

   public int getSpawnZ() {
      return this.spawnZ;
   }

   public void setBorderDamagePerBlock(double damage) {
      this.borderDamagePerBlock = damage;
   }

   public EnumDifficulty getDifficulty() {
      return this.difficulty;
   }

   public void setCleanWeatherTime(int cleanWeatherTimeIn) {
      this.cleanWeatherTime = cleanWeatherTimeIn;
   }

   public long getBorderLerpTime() {
      return this.borderSizeLerpTime;
   }

   public String getGeneratorOptions() {
      return this.generatorOptions;
   }

   public int getSpawnY() {
      return this.spawnY;
   }

   public int getBorderWarningTime() {
      return this.borderWarningTime;
   }

   public void setSpawn(BlockPos spawnPoint) {
      this.spawnX = spawnPoint.getX();
      this.spawnY = spawnPoint.getY();
      this.spawnZ = spawnPoint.getZ();
   }

   public void setThundering(boolean thunderingIn) {
      this.thundering = thunderingIn;
   }

   public WorldInfo(WorldSettings settings, String name) {
      this.populateFromWorldSettings(settings);
      this.levelName = name;
      this.difficulty = DEFAULT_DIFFICULTY;
      this.initialized = false;
   }

   public WorldType getTerrainType() {
      return this.terrainType;
   }

   protected WorldInfo() {
   }

   public void update() {
      this.initialized = true;
   }

   public WorldInfo(NBTTagCompound nbt) {
      this.randomSeed = nbt.getLong("RandomSeed");
      if (nbt.hasKey("generatorName", 8)) {
         String text = nbt.getString("generatorName");
         this.terrainType = WorldType.parseWorldType(text);
         if (this.terrainType == null) {
            this.terrainType = WorldType.DEFAULT;
         } else if (this.terrainType.isVersioned()) {
            int i = 0;
            if (nbt.hasKey("generatorVersion", 99)) {
               i = nbt.getInteger("generatorVersion");
            }

            this.terrainType = this.terrainType.getWorldTypeForGeneratorVersion(i);
         }

         if (nbt.hasKey("generatorOptions", 8)) {
            this.generatorOptions = nbt.getString("generatorOptions");
         }
      }

      this.theGameType = net.minecraft.world.WorldSettings.GameType.getByID(nbt.getInteger("GameType"));
      if (nbt.hasKey("MapFeatures", 99)) {
         this.mapFeaturesEnabled = nbt.getBoolean("MapFeatures");
      } else {
         this.mapFeaturesEnabled = true;
      }

      this.spawnX = nbt.getInteger("SpawnX");
      this.spawnY = nbt.getInteger("SpawnY");
      this.spawnZ = nbt.getInteger("SpawnZ");
      this.totalTime = nbt.getLong("Time");
      if (nbt.hasKey("DayTime", 99)) {
         this.worldTime = nbt.getLong("DayTime");
      } else {
         this.worldTime = this.totalTime;
      }

      this.lastTimePlayed = nbt.getLong("LastPlayed");
      this.sizeOnDisk = nbt.getLong("SizeOnDisk");
      this.levelName = nbt.getString("LevelName");
      this.saveVersion = nbt.getInteger("version");
      this.cleanWeatherTime = nbt.getInteger("clearWeatherTime");
      this.rainTime = nbt.getInteger("rainTime");
      this.raining = nbt.getBoolean("raining");
      this.thunderTime = nbt.getInteger("thunderTime");
      this.thundering = nbt.getBoolean("thundering");
      this.hardcore = nbt.getBoolean("hardcore");
      if (nbt.hasKey("initialized", 99)) {
         this.initialized = nbt.getBoolean("initialized");
      } else {
         this.initialized = true;
      }

      if (nbt.hasKey("allowCommands", 99)) {
         this.allowCommands = nbt.getBoolean("allowCommands");
      } else {
         this.allowCommands = this.theGameType == net.minecraft.world.WorldSettings.GameType.CREATIVE;
      }

      if (nbt.hasKey("Player", 10)) {
         this.playerTag = nbt.getCompoundTag("Player");
         this.dimension = this.playerTag.getInteger("Dimension");
      }

      if (nbt.hasKey("GameRules", 10)) {
         this.theGameRules.readFromNBT(nbt.getCompoundTag("GameRules"));
      }

      if (nbt.hasKey("Difficulty", 99)) {
         this.difficulty = EnumDifficulty.getDifficultyEnum(nbt.getByte("Difficulty"));
      }

      if (nbt.hasKey("DifficultyLocked", 1)) {
         this.difficultyLocked = nbt.getBoolean("DifficultyLocked");
      }

      if (nbt.hasKey("BorderCenterX", 99)) {
         this.borderCenterX = nbt.getDouble("BorderCenterX");
      }

      if (nbt.hasKey("BorderCenterZ", 99)) {
         this.borderCenterZ = nbt.getDouble("BorderCenterZ");
      }

      if (nbt.hasKey("BorderSize", 99)) {
         this.borderSize = nbt.getDouble("BorderSize");
      }

      if (nbt.hasKey("BorderSizeLerpTime", 99)) {
         this.borderSizeLerpTime = nbt.getLong("BorderSizeLerpTime");
      }

      if (nbt.hasKey("BorderSizeLerpTarget", 99)) {
         this.borderSizeLerpTarget = nbt.getDouble("BorderSizeLerpTarget");
      }

      if (nbt.hasKey("BorderSafeZone", 99)) {
         this.borderSafeZone = nbt.getDouble("BorderSafeZone");
      }

      if (nbt.hasKey("BorderDamagePerBlock", 99)) {
         this.borderDamagePerBlock = nbt.getDouble("BorderDamagePerBlock");
      }

      if (nbt.hasKey("BorderWarningBlocks", 99)) {
         this.borderWarningDistance = nbt.getInteger("BorderWarningBlocks");
      }

      if (nbt.hasKey("BorderWarningTime", 99)) {
         this.borderWarningTime = nbt.getInteger("BorderWarningTime");
      }
   }

   public boolean areCommandsAllowed() {
      return this.allowCommands;
   }

   private void updateTagCompound(NBTTagCompound nbt, NBTTagCompound playerNbt) {
      nbt.setLong("RandomSeed", this.randomSeed);
      nbt.setString("generatorName", this.terrainType.getWorldTypeName());
      nbt.setInteger("generatorVersion", this.terrainType.getGeneratorVersion());
      nbt.setString("generatorOptions", this.generatorOptions);
      nbt.setInteger("GameType", this.theGameType.getID());
      nbt.setBoolean("MapFeatures", this.mapFeaturesEnabled);
      nbt.setInteger("SpawnX", this.spawnX);
      nbt.setInteger("SpawnY", this.spawnY);
      nbt.setInteger("SpawnZ", this.spawnZ);
      nbt.setLong("Time", this.totalTime);
      nbt.setLong("DayTime", this.worldTime);
      nbt.setLong("SizeOnDisk", this.sizeOnDisk);
      nbt.setLong("LastPlayed", MinecraftServer.getCurrentTimeMillis());
      nbt.setString("LevelName", this.levelName);
      nbt.setInteger("version", this.saveVersion);
      nbt.setInteger("clearWeatherTime", this.cleanWeatherTime);
      nbt.setInteger("rainTime", this.rainTime);
      nbt.setBoolean("raining", this.raining);
      nbt.setInteger("thunderTime", this.thunderTime);
      nbt.setBoolean("thundering", this.thundering);
      nbt.setBoolean("hardcore", this.hardcore);
      nbt.setBoolean("allowCommands", this.allowCommands);
      nbt.setBoolean("initialized", this.initialized);
      nbt.setDouble("BorderCenterX", this.borderCenterX);
      nbt.setDouble("BorderCenterZ", this.borderCenterZ);
      nbt.setDouble("BorderSize", this.borderSize);
      nbt.setLong("BorderSizeLerpTime", this.borderSizeLerpTime);
      nbt.setDouble("BorderSafeZone", this.borderSafeZone);
      nbt.setDouble("BorderDamagePerBlock", this.borderDamagePerBlock);
      nbt.setDouble("BorderSizeLerpTarget", this.borderSizeLerpTarget);
      nbt.setDouble("BorderWarningBlocks", this.borderWarningDistance);
      nbt.setDouble("BorderWarningTime", this.borderWarningTime);
      if (this.difficulty != null) {
         nbt.setByte("Difficulty", (byte)this.difficulty.getDifficultyId());
      }

      nbt.setBoolean("DifficultyLocked", this.difficultyLocked);
      nbt.setTag("GameRules", this.theGameRules.writeToNBT());
      if (playerNbt != null) {
         nbt.setTag("Player", playerNbt);
      }
   }

   public void update2() {
      this.mapFeaturesEnabled = false;
   }

   public NBTTagCompound getPlayerNBTTagCompound() {
      return this.playerTag;
   }

   public net.minecraft.world.WorldSettings.GameType getGameType() {
      return this.theGameType;
   }

   public double getBorderCenterZ() {
      return this.borderCenterZ;
   }

   public GameRules getGameRulesInstance() {
      return this.theGameRules;
   }

   public void setRainTime(int time) {
      this.rainTime = time;
   }

   public void setWorldTime(long time) {
      this.worldTime = time;
   }

   public int getCleanWeatherTime() {
      return this.cleanWeatherTime;
   }

   public boolean isMapFeaturesEnabled() {
      return this.mapFeaturesEnabled;
   }

   public long getLastTimePlayed() {
      return this.lastTimePlayed;
   }

   public double getBorderCenterX() {
      return this.borderCenterX;
   }

   public void setBorderLerpTime(long time) {
      this.borderSizeLerpTime = time;
   }

   public int getBorderWarningDistance() {
      return this.borderWarningDistance;
   }
}
