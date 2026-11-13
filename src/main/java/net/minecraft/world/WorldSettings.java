package net.minecraft.world;

import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.world.storage.WorldInfo;

public final class WorldSettings {
   private String worldName = "";
   private boolean bonusChestEnabled;
   private final long seed;
   private boolean commandsAllowed;
   private final WorldSettings.GameType theGameType;
   private final boolean mapFeaturesEnabled;
   private final boolean hardcoreEnabled;
   private final WorldType terrainType;

   public boolean getHardcoreEnabled() {
      return this.hardcoreEnabled;
   }

   public WorldType getTerrainType() {
      return this.terrainType;
   }

   public WorldSettings setWorldName(String name) {
      this.worldName = name;
      return this;
   }

   public boolean isBonusChestEnabled() {
      return this.bonusChestEnabled;
   }

   public WorldSettings.GameType getGameType() {
      return this.theGameType;
   }

   public boolean areCommandsAllowed() {
      return this.commandsAllowed;
   }

   public WorldSettings enableBonusChest() {
      this.bonusChestEnabled = true;
      return this;
   }

   public WorldSettings enableCommands() {
      this.commandsAllowed = true;
      return this;
   }

   public String getWorldName() {
      return this.worldName;
   }

   public WorldSettings(long seedIn, WorldSettings.GameType gameType, boolean enableMapFeatures, boolean hardcoreMode, WorldType worldTypeIn) {
      this.seed = seedIn;
      this.theGameType = gameType;
      this.mapFeaturesEnabled = enableMapFeatures;
      this.hardcoreEnabled = hardcoreMode;
      this.terrainType = worldTypeIn;
   }

   public long getSeed() {
      return this.seed;
   }

   public static WorldSettings.GameType getGameTypeById(int id) {
      return WorldSettings.GameType.getByID(id);
   }

   public WorldSettings(WorldInfo info) {
      this(info.getSeed(), info.getGameType(), info.isMapFeaturesEnabled(), info.isHardcoreModeEnabled(), info.getTerrainType());
   }

   public boolean isMapFeaturesEnabled() {
      return this.mapFeaturesEnabled;
   }

   public enum GameType {
      NOT_SET(-1, ""),
      SURVIVAL(0, "survival"),
      CREATIVE(1, "creative"),
      ADVENTURE(2, "adventure"),
      SPECTATOR(3, "spectator");

      int id;
      String name;

      public static WorldSettings.GameType getByName(String gamemodeName) {
         for (WorldSettings.GameType gameType2 : values()) {
            if (gameType2.name.equals(gamemodeName)) {
               return gameType2;
            }
         }

         return SURVIVAL;
      }

      public void configurePlayerCapabilities(PlayerCapabilities capabilities) {
         if (this == CREATIVE) {
            capabilities.allowFlying = true;
            capabilities.isCreativeMode = true;
            capabilities.disableDamage = true;
         } else if (this == SPECTATOR) {
            capabilities.allowFlying = true;
            capabilities.isCreativeMode = false;
            capabilities.disableDamage = true;
            capabilities.isFlying = true;
         } else {
            capabilities.allowFlying = false;
            capabilities.isCreativeMode = false;
            capabilities.disableDamage = false;
            capabilities.isFlying = false;
         }

         capabilities.allowEdit = !this.isAdventure();
      }

      public static WorldSettings.GameType getByID(int idIn) {
         for (WorldSettings.GameType gameType2 : values()) {
            if (gameType2.id == idIn) {
               return gameType2;
            }
         }

         return SURVIVAL;
      }

      public String getName() {
         return this.name;
      }

      GameType(int i, String text) {
         this.id = i;
         this.name = text;
      }

      public boolean isCreative() {
         return this == CREATIVE;
      }

      public boolean isSurvivalOrAdventure() {
         return this == SURVIVAL || this == ADVENTURE;
      }

      public boolean isAdventure() {
         return this == ADVENTURE || this == SPECTATOR;
      }

      public int getID() {
         return this.id;
      }
   }
}
