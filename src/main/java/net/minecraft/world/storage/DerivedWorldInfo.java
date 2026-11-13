package net.minecraft.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldType;

public class DerivedWorldInfo extends WorldInfo {
   private final WorldInfo theWorldInfo;

   @Override
   public NBTTagCompound getPlayerNBTTagCompound() {
      return this.theWorldInfo.getPlayerNBTTagCompound();
   }

   @Override
   public net.minecraft.world.WorldSettings.GameType getGameType() {
      return this.theWorldInfo.getGameType();
   }

   @Override
   public void setWorldName(String worldName) {
   }

   @Override
   public void setDifficultyLocked(boolean flag) {
   }

   @Override
   public int getSpawnY() {
      return this.theWorldInfo.getSpawnY();
   }

   public DerivedWorldInfo(WorldInfo worldInfo) {
      this.theWorldInfo = worldInfo;
   }

   @Override
   public NBTTagCompound getNBTTagCompound() {
      return this.theWorldInfo.getNBTTagCompound();
   }

   @Override
   public boolean isDifficultyLocked() {
      return this.theWorldInfo.isDifficultyLocked();
   }

   @Override
   public boolean isInitialized() {
      return this.theWorldInfo.isInitialized();
   }

   @Override
   public void setRainTime(int i) {
   }

   @Override
   public void setWorldTotalTime(long l) {
   }

   @Override
   public void update5() {
   }

   @Override
   public long getSizeOnDisk() {
      return this.theWorldInfo.getSizeOnDisk();
   }

   @Override
   public long getWorldTime() {
      return this.theWorldInfo.getWorldTime();
   }

   @Override
   public int getSaveVersion() {
      return this.theWorldInfo.getSaveVersion();
   }

   @Override
   public int getSpawnZ() {
      return this.theWorldInfo.getSpawnZ();
   }

   @Override
   public void setDifficulty(EnumDifficulty newDifficulty) {
   }

   @Override
   public void setThunderTime(int i) {
   }

   @Override
   public boolean isRaining() {
      return this.theWorldInfo.isRaining();
   }

   @Override
   public void update() {
   }

   @Override
   public EnumDifficulty getDifficulty() {
      return this.theWorldInfo.getDifficulty();
   }

   @Override
   public boolean isMapFeaturesEnabled() {
      return this.theWorldInfo.isMapFeaturesEnabled();
   }

   @Override
   public long getSeed() {
      return this.theWorldInfo.getSeed();
   }

   @Override
   public void setTerrainType(WorldType type) {
   }

   @Override
   public long getLastTimePlayed() {
      return this.theWorldInfo.getLastTimePlayed();
   }

   @Override
   public long getWorldTotalTime() {
      return this.theWorldInfo.getWorldTotalTime();
   }

   @Override
   public WorldType getTerrainType() {
      return this.theWorldInfo.getTerrainType();
   }

   @Override
   public NBTTagCompound cloneNBTCompound(NBTTagCompound nbt) {
      return this.theWorldInfo.cloneNBTCompound(nbt);
   }

   @Override
   public void setThundering(boolean flag) {
   }

   @Override
   public boolean isThundering() {
      return this.theWorldInfo.isThundering();
   }

   @Override
   public boolean isHardcoreModeEnabled() {
      return this.theWorldInfo.isHardcoreModeEnabled();
   }

   @Override
   public int getSpawnX() {
      return this.theWorldInfo.getSpawnX();
   }

   @Override
   public void setWorldTime(long l) {
   }

   @Override
   public boolean areCommandsAllowed() {
      return this.theWorldInfo.areCommandsAllowed();
   }

   @Override
   public void update6() {
   }

   @Override
   public int getThunderTime() {
      return this.theWorldInfo.getThunderTime();
   }

   @Override
   public GameRules getGameRulesInstance() {
      return this.theWorldInfo.getGameRulesInstance();
   }

   @Override
   public void setSpawn(BlockPos spawnPoint) {
   }

   @Override
   public int getRainTime() {
      return this.theWorldInfo.getRainTime();
   }

   @Override
   public String getWorldName() {
      return this.theWorldInfo.getWorldName();
   }

   @Override
   public void setRaining(boolean flag) {
   }
}
