package net.minecraft.client.multiplayer;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.CustomGuis;
import net.optifine.DynamicLights;
import net.optifine.override.PlayerControllerOF;
import net.optifine.reflect.Reflector;
import wtf.novoline.Novoline;
import wtf.novoline.event.ServerDisconnectEvent;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.util.FireworkEffects.EntityFXChild;

public class WorldClient extends World {
   private ChunkProviderClient clientChunkProvider;
   private final Set<Entity> entityList = Sets.newHashSet();
   private final Set<Entity> entitySpawnQueue = Sets.newHashSet();
   private final Minecraft mc = Minecraft.getMinecraft();
   private final Set<ChunkCoordIntPair> previousActiveChunkSet = Sets.newHashSet();
   private boolean playerUpdate = false;
   private NetHandlerPlayClient sendQueue;

   @Override
   protected void onEntityRemoved(Entity entityIn) {
      super.onEntityRemoved(entityIn);
      if (this.entityList.contains(entityIn)) {
         if (entityIn.isEntityAlive()) {
            this.entitySpawnQueue.add(entityIn);
         } else {
            this.entityList.remove(entityIn);
         }
      }
   }

   @Override
   public boolean spawnEntityInWorld(Entity entityIn) {
      boolean conditionMet = super.spawnEntityInWorld(entityIn);
      this.entityList.add(entityIn);
      if (!conditionMet) {
         this.entitySpawnQueue.add(entityIn);
      } else if (entityIn instanceof EntityMinecart) {
         this.mc.getSoundHandler().playSound(new MovingSoundMinecart((EntityMinecart)entityIn));
      }

      return conditionMet;
   }

   @Override
   public Entity getEntityByID(int id) {
      return id == this.mc.thePlayer.getEntityId() ? this.mc.thePlayer : super.getEntityByID(id);
   }

   @Override
   public void setWorldTime(long time) {
      if (time < 0L) {
         time = -time;
         this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
      } else {
         this.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
      }

      long l = ModuleManager.worldTime.isEnabled() ? (long)(ModuleManager.worldTime.worldTime.getCurrentValue() * 1000.0F) : time;
      super.setWorldTime(l);
   }

   private boolean isPlayerActing() {
      return this.mc.playerController instanceof PlayerControllerOF ? ((PlayerControllerOF)this.mc.playerController).isActing() : false;
   }

   @Override
   public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, NBTTagCompound compund) {
      this.mc.effectRenderer.addEffect(new EntityFXChild(this, x, y, z, motionX, motionY, motionZ, this.mc.effectRenderer, compund));
   }

   public void noop(int i, int i2, int i3, int i4) {
   }

   @Override
   public int getCombinedLight(BlockPos pos, int lightValue) {
      int combinedLight = super.getCombinedLight(pos, lightValue);
      if (Config.isDynamicLights()) {
         combinedLight = DynamicLights.getCombinedLight(pos, combinedLight);
      }

      return combinedLight;
   }

   @Override
   protected void onEntityAdded(Entity entityIn) {
      super.onEntityAdded(entityIn);
      if (this.entitySpawnQueue.contains(entityIn)) {
         this.entitySpawnQueue.remove(entityIn);
      }
   }

   public void setWorldScoreboard(Scoreboard scoreboardIn) {
      this.worldScoreboard = scoreboardIn;
   }

   public Entity removeEntityFromWorld(int entityID) {
      Entity entity = this.entitiesById.removeObject(entityID);
      if (entity != null) {
         this.entityList.remove(entity);
         this.removeEntity(entity);
      }

      return entity;
   }

   @Override
   public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
      this.playerUpdate = this.isPlayerActing();
      boolean flag = super.setBlockState(pos, newState, flags);
      this.playerUpdate = false;
      return flag;
   }

   public void sendQuittingDisconnectingPacket() {
      ChatComponentText chatComponentText = new ChatComponentText("Quitting");
      Novoline.getNovoline().getBus().post(new ServerDisconnectEvent(chatComponentText));
      this.sendQueue.getNetworkManager().closeChannel(chatComponentText);
   }

   @Override
   public void tick() {
      super.tick();
      this.setTotalWorldTime(this.getTotalWorldTime() + 1L);
      if (this.getGameRules().getBoolean("doDaylightCycle")) {
         this.setWorldTime(this.getWorldTime() + 1L);
      }

      this.theProfiler.startSection("reEntryProcessing");

      for (int index = 0; index < 10 && !this.entitySpawnQueue.isEmpty(); index++) {
         Entity entity = this.entitySpawnQueue.iterator().next();
         this.entitySpawnQueue.remove(entity);
         if (!this.loadedEntityList.contains(entity)) {
            this.spawnEntityInWorld(entity);
         }
      }

      this.theProfiler.endStartSection("chunkCache");
      this.clientChunkProvider.unloadQueuedChunks();
      this.theProfiler.endStartSection("blocks");
      this.updateBlocks();
      this.theProfiler.endSection();
   }

   public void handleBlockPos(BlockPos blockPos, String text, float f, float f2) {
      this.playSound(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, text, f, f2, false);
   }

   @Override
   public void playSound(double x, double y, double z, String soundName, float volume, float pitch, boolean distanceDelay) {
      double distanceSq = this.mc.getRenderViewEntity().getDistanceSq(x, y, z);
      PositionedSoundRecord positionedSoundRecord = new PositionedSoundRecord(new ResourceLocation(soundName), volume, pitch, (float)x, (float)y, (float)z);
      if (distanceDelay && distanceSq > 100.0) {
         double d = Math.sqrt(distanceSq) / 40.0;
         this.mc.getSoundHandler().playDelayedSound(positionedSoundRecord, (int)(d * 20.0));
      } else {
         this.mc.getSoundHandler().playSound(positionedSoundRecord);
      }
   }

   public WorldClient(NetHandlerPlayClient netHandlerPlayClient, WorldSettings worldSettings, int i, EnumDifficulty enumDifficulty, Profiler profiler2) {
      super(new SaveHandlerMP(), new WorldInfo(worldSettings, "MpServer"), WorldProvider.getProviderForDimension(i), profiler2, true);
      this.sendQueue = netHandlerPlayClient;
      this.getWorldInfo().setDifficulty(enumDifficulty);
      this.provider.registerWorld(this);
      this.setSpawnPoint(new BlockPos(8, 64, 8));
      this.chunkProvider = this.createChunkProvider();
      this.mapStorage = new SaveDataMemoryStorage();
      this.calculateInitialSkylight();
      this.calculateInitialWeather();
      Reflector.checkReflectorConstructor(Reflector.reflectorConstructor14, this);
      if (this.mc.playerController != null && this.mc.playerController.getClass() == PlayerControllerMP.class) {
         this.mc.playerController = new PlayerControllerOF(this.mc, netHandlerPlayClient);
         CustomGuis.setPlayerControllerOF((PlayerControllerOF)this.mc.playerController);
      }
   }

   protected IChunkProvider createChunkProvider() {
      this.clientChunkProvider = new ChunkProviderClient(this);
      return this.clientChunkProvider;
   }

   public void removeAllEntities() {
      this.loadedEntityList.removeAll(this.unloadedEntityList);

      for (int index = 0; index < this.unloadedEntityList.size(); index++) {
         Entity entity = this.unloadedEntityList.get(index);
         int chunkCoordX2 = entity.chunkCoordX;
         int chunkCoordZ2 = entity.chunkCoordZ;
         if (entity.addedToChunk && this.isChunkLoaded(chunkCoordX2, chunkCoordZ2, true)) {
            this.getChunkFromChunkCoords(chunkCoordX2, chunkCoordZ2).removeEntity(entity);
         }
      }

      for (int outerIndex = 0; outerIndex < this.unloadedEntityList.size(); outerIndex++) {
         this.onEntityRemoved(this.unloadedEntityList.get(outerIndex));
      }

      this.unloadedEntityList.clear();

      for (int index2 = 0; index2 < this.loadedEntityList.size(); index2++) {
         Entity entity2 = this.loadedEntityList.get(index2);
         if (entity2.ridingEntity != null) {
            if (!entity2.ridingEntity.isDead && entity2.ridingEntity.riddenByEntity == entity2) {
               continue;
            }

            entity2.ridingEntity.riddenByEntity = null;
            entity2.ridingEntity = null;
         }

         if (entity2.isDead) {
            int chunkCoordX3 = entity2.chunkCoordX;
            int chunkCoordZ3 = entity2.chunkCoordZ;
            if (entity2.addedToChunk && this.isChunkLoaded(chunkCoordX3, chunkCoordZ3, true)) {
               this.getChunkFromChunkCoords(chunkCoordX3, chunkCoordZ3).removeEntity(entity2);
            }

            this.loadedEntityList.remove(index2--);
            this.onEntityRemoved(entity2);
         }
      }
   }

   public void addEntityToWorld(int entityID, Entity entityToSpawn) {
      Entity entity = this.getEntityByID(entityID);
      if (entity != null) {
         this.removeEntity(entity);
      }

      this.entityList.add(entityToSpawn);
      entityToSpawn.setEntityId(entityID);
      if (!this.spawnEntityInWorld(entityToSpawn)) {
         this.entitySpawnQueue.add(entityToSpawn);
      }

      this.entitiesById.addKey(entityID, entityToSpawn);
   }

   @Override
   protected void updateWeather() {
   }

   public boolean invalidateRegionAndSetBlock(BlockPos pos, IBlockState state) {
      int xCoordinate = pos.getX();
      int yCoordinate = pos.getY();
      int zCoordinate = pos.getZ();
      this.noop(xCoordinate, zCoordinate, xCoordinate, zCoordinate);
      return super.setBlockState(pos, state, 3);
   }

   public void doPreChunk(int chuncX, int chuncZ, boolean loadChunk) {
      if (loadChunk) {
         this.clientChunkProvider.loadChunk(chuncX, chuncZ);
      } else {
         this.clientChunkProvider.unloadChunk(chuncX, chuncZ);
      }

      if (!loadChunk) {
         this.markBlockRangeForRenderUpdate(chuncX * 16, 0, chuncZ * 16, chuncX * 16 + 15, 256, chuncZ * 16 + 15);
      }
   }

   @Override
   protected void updateBlocks() {
      super.updateBlocks();
      this.previousActiveChunkSet.retainAll(this.activeChunkSet);
      if (this.previousActiveChunkSet.size() == this.activeChunkSet.size()) {
         this.previousActiveChunkSet.clear();
      }

      int i = 0;

      for (ChunkCoordIntPair chunkCoordIntPair : this.activeChunkSet) {
         if (!this.previousActiveChunkSet.contains(chunkCoordIntPair)) {
            int i2 = chunkCoordIntPair.chunkXPos * 16;
            int i3 = chunkCoordIntPair.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
            this.playMoodSoundAndCheckLight(i2, i3, chunkFromChunkCoords);
            this.theProfiler.endSection();
            this.previousActiveChunkSet.add(chunkCoordIntPair);
            if (++i >= 10) {
               return;
            }
         }
      }
   }

   @Override
   public void removeEntity(Entity entityIn) {
      super.removeEntity(entityIn);
      this.entityList.remove(entityIn);
   }

   @Override
   protected int getRenderDistanceChunks() {
      return this.mc.gameSettings.renderDistanceChunks;
   }

   public void doVoidFogParticles(int posX, int posY, int posZ) {
      Random random = new Random();
      ItemStack heldItem = this.mc.thePlayer.getHeldItem();
      boolean flag = this.mc.playerController.getCurrentGameType() == net.minecraft.world.WorldSettings.GameType.CREATIVE
         && heldItem != null
         && Block.getBlockFromItem(heldItem.getItem()) == Blocks.barrier;
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int i = 0; i < 1000; i++) {
         int i2 = posX + this.rand.nextInt(16) - this.rand.nextInt(16);
         int i3 = posY + this.rand.nextInt(16) - this.rand.nextInt(16);
         int i4 = posZ + this.rand.nextInt(16) - this.rand.nextInt(16);
         mutableBlockPos.set(i2, i3, i4);
         IBlockState blockState = this.getBlockState(mutableBlockPos);
         blockState.getBlock().randomDisplayTick(this, mutableBlockPos, blockState, random);
         if (flag && blockState.getBlock() == Blocks.barrier) {
            this.spawnParticle(EnumParticleTypes.BARRIER, i2 + 0.5F, i3 + 0.5F, i4 + 0.5F, 0.0, 0.0, 0.0);
         }
      }
   }

   public boolean isPlayerUpdate() {
      return this.playerUpdate;
   }

   @Override
   public CrashReportCategory addWorldInfoToCrashReport(CrashReport report) {
      CrashReportCategory crashReportCategory = super.addWorldInfoToCrashReport(report);
      crashReportCategory.addCrashSectionCallable("Forced entities", new Callable<String>() {
         public String call() {
            return WorldClient.this.entityList.size() + " total; " + WorldClient.this.entityList.toString();
         }
      });
      crashReportCategory.addCrashSectionCallable("Retry entities", new Callable<String>() {
         public String call() {
            return WorldClient.this.entitySpawnQueue.size() + " total; " + WorldClient.this.entitySpawnQueue.toString();
         }
      });
      crashReportCategory.addCrashSectionCallable("Server brand", new Callable<String>() {
         public String call() throws Exception {
            return WorldClient.this.mc.thePlayer.getClientBrand();
         }
      });
      crashReportCategory.addCrashSectionCallable("Server type", new Callable<String>() {
         public String call() throws Exception {
            return WorldClient.this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
         }
      });
      return crashReportCategory;
   }
}
