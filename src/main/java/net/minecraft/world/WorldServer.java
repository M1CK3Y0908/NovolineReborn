package net.minecraft.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.INpc;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ReportedException;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServer extends World implements IThreadListener {
   private static final Logger logger = LogManager.getLogger();
   public boolean disableLevelSaving;
   private final Set<NextTickListEntry> pendingTickListEntriesHashSet = Sets.newHashSet();
   private final TreeSet<NextTickListEntry> pendingTickListEntriesTreeSet = new TreeSet<>();
   private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
   public ChunkProviderServer theChunkProviderServer;
   private boolean allPlayersSleeping;
   private static final List<WeightedRandomChestContent> bonusChestContent = Lists.newArrayList(
      new WeightedRandomChestContent[]{
         new WeightedRandomChestContent(Items.stick, 0, 1, 3, 10),
         new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.planks), 0, 1, 3, 10),
         new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log), 0, 1, 3, 10),
         new WeightedRandomChestContent(Items.stone_axe, 0, 1, 1, 3),
         new WeightedRandomChestContent(Items.wooden_axe, 0, 1, 1, 5),
         new WeightedRandomChestContent(Items.stone_pickaxe, 0, 1, 1, 3),
         new WeightedRandomChestContent(Items.wooden_pickaxe, 0, 1, 1, 5),
         new WeightedRandomChestContent(Items.apple, 0, 2, 3, 5),
         new WeightedRandomChestContent(Items.bread, 0, 2, 3, 3),
         new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log2), 0, 1, 3, 10)
      }
   );
   private final SpawnerAnimals mobSpawner = new SpawnerAnimals();
   protected final VillageSiege villageSiege = new VillageSiege(this);
   private WorldServer.ServerBlockEventList[] blockEventQueue = new WorldServer.ServerBlockEventList[]{
      new WorldServer.ServerBlockEventList(), new WorldServer.ServerBlockEventList()
   };
   private int blockEventCacheIndex;
   private List<NextTickListEntry> pendingTickListEntriesThisTick = Lists.newArrayList();
   private final MinecraftServer mcServer;
   private final EntityTracker theEntityTracker;
   private int updateEntityTick;
   private final PlayerManager thePlayerManager;
   private final Teleporter worldTeleporter;

   public boolean isActive() {
      if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
         return false;
      }

      int size2 = this.pendingTickListEntriesTreeSet.size();
      if (size2 != this.pendingTickListEntriesHashSet.size()) {
         throw new IllegalStateException("TickNextTick list out of synch");
      }

      if (size2 > 1000) {
         size2 = 1000;
      }

      this.theProfiler.startSection("cleaning");

      for (int i = 0; i < size2; i++) {
         NextTickListEntry nextTickListEntry = this.pendingTickListEntriesTreeSet.first();
         if (nextTickListEntry.scheduledTime > this.worldInfo.getWorldTotalTime()) {
            break;
         }

         this.pendingTickListEntriesTreeSet.remove(nextTickListEntry);
         this.pendingTickListEntriesHashSet.remove(nextTickListEntry);
         this.pendingTickListEntriesThisTick.add(nextTickListEntry);
      }

      this.theProfiler.endSection();
      this.theProfiler.startSection("ticking");
      Iterator iterator2 = this.pendingTickListEntriesThisTick.iterator();

      while (iterator2.hasNext()) {
         NextTickListEntry nextTickListEntry2 = (NextTickListEntry)iterator2.next();
         iterator2.remove();
         if (this.isAreaLoaded(nextTickListEntry2.position.add(0, 0, 0), nextTickListEntry2.position.add(0, 0, 0))) {
            IBlockState blockState2 = this.getBlockState(nextTickListEntry2.position);
            if (blockState2.getBlock().getMaterial() != Material.air && Block.isEqualTo(blockState2.getBlock(), nextTickListEntry2.getBlock())) {
               try {
                  blockState2.getBlock().updateTick(this, nextTickListEntry2.position, blockState2, this.rand);
               } catch (Throwable throwable) {
                  CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Exception while ticking a block");
                  CrashReportCategory.addBlockInfo(crashReport.makeCategory("Block being ticked"), nextTickListEntry2.position, blockState2);
                  throw new ReportedException(crashReport);
               }
            }
         } else {
            this.scheduleUpdate(nextTickListEntry2.position, nextTickListEntry2.getBlock(), 0);
         }
      }

      this.theProfiler.endSection();
      this.pendingTickListEntriesThisTick.clear();
      return !this.pendingTickListEntriesTreeSet.isEmpty();
   }

   protected IChunkProvider createChunkProvider() {
      IChunkLoader chunkLoader = this.saveHandler.getChunkLoader(this.provider);
      this.theChunkProviderServer = new ChunkProviderServer(this, chunkLoader, this.provider.createChunkGenerator());
      return this.theChunkProviderServer;
   }

   @Override
   protected void updateBlocks() {
      super.updateBlocks();
      if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
         for (ChunkCoordIntPair chunkCoordIntPair : this.activeChunkSet) {
            this.getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos).onTick(false);
         }
      } else {
         for (ChunkCoordIntPair chunkCoordIntPair2 : this.activeChunkSet) {
            int i = chunkCoordIntPair2.chunkXPos * 16;
            int i2 = chunkCoordIntPair2.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(chunkCoordIntPair2.chunkXPos, chunkCoordIntPair2.chunkZPos);
            this.playMoodSoundAndCheckLight(i, i2, chunkFromChunkCoords);
            this.theProfiler.endStartSection("tickChunk");
            chunkFromChunkCoords.onTick(false);
            this.theProfiler.endStartSection("thunder");
            if (this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
               this.updateLCG = this.updateLCG * 3 + 1013904223;
               int i3 = this.updateLCG >> 2;
               BlockPos blockPos2 = this.adjustPosToNearbyEntity(new BlockPos(i + (i3 & 15), 0, i2 + (i3 >> 8 & 15)));
               if (this.isRainingAt(blockPos2)) {
                  this.addWeatherEffect(new EntityLightningBolt(this, blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
               }
            }

            this.theProfiler.endStartSection("iceandsnow");
            if (this.rand.nextInt(16) == 0) {
               this.updateLCG = this.updateLCG * 3 + 1013904223;
               int i4 = this.updateLCG >> 2;
               BlockPos precipitationHeight = this.getPrecipitationHeight(new BlockPos(i + (i4 & 15), 0, i2 + (i4 >> 8 & 15)));
               BlockPos blockPos3 = precipitationHeight.down();
               if (this.canBlockFreezeNoWater(blockPos3)) {
                  this.setBlockState(blockPos3, Blocks.ice.getDefaultState());
               }

               if (this.isRaining() && this.canSnowAt(precipitationHeight, true)) {
                  this.setBlockState(precipitationHeight, Blocks.snow_layer.getDefaultState());
               }

               if (this.isRaining() && this.getBiomeGenForCoords(blockPos3).canRain()) {
                  this.getBlockState(blockPos3).getBlock().fillWithRain(this, blockPos3);
               }
            }

            this.theProfiler.endStartSection("tickBlocks");
            int intValue = this.getGameRules().getInt("randomTickSpeed");
            if (intValue > 0) {
               for (ExtendedBlockStorage extendedBlockStorage : chunkFromChunkCoords.getBlockStorageArray()) {
                  if (extendedBlockStorage != null && extendedBlockStorage.getNeedsRandomTick()) {
                     for (int i5 = 0; i5 < intValue; i5++) {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int i6 = this.updateLCG >> 2;
                        int i7 = i6 & 15;
                        int i8 = i6 >> 8 & 15;
                        int i9 = i6 >> 16 & 15;
                        IBlockState iBlockState = extendedBlockStorage.get(i7, i9, i8);
                        Block block2 = iBlockState.getBlock();
                        if (block2.getTickRandomly()) {
                           block2.randomTick(this, new BlockPos(i7 + i, i9 + extendedBlockStorage.getYLocation(), i8 + i2), iBlockState, this.rand);
                        }
                     }
                  }
               }
            }

            this.theProfiler.endSection();
         }
      }
   }

   public void handleIProgressUpdate(IProgressUpdate iProgressUpdate) throws MinecraftException {
      if (this.chunkProvider.canSave()) {
         if (iProgressUpdate != null) {
            iProgressUpdate.displaySavingString("Saving level");
         }

         this.saveLevel();
         if (iProgressUpdate != null) {
            iProgressUpdate.displayLoadingString("Saving chunks");
         }

         this.chunkProvider.checkIProgressUpdate(iProgressUpdate);

         for (Chunk chunk2 : Lists.newArrayList(this.theChunkProviderServer.getLoadedChunks())) {
            if (chunk2 != null && !this.thePlayerManager.hasPlayerInstance(chunk2.xPosition, chunk2.zPosition)) {
               this.theChunkProviderServer.dropChunk(chunk2.xPosition, chunk2.zPosition);
            }
         }
      }
   }

   public Teleporter getDefaultTeleporter() {
      return this.worldTeleporter;
   }

   @Override
   public boolean addWeatherEffect(Entity entityIn) {
      if (super.addWeatherEffect(entityIn)) {
         this.mcServer
            .getConfigurationManager()
            .sendToAllNear(entityIn.posX, entityIn.posY, entityIn.posZ, 512.0, this.provider.getDimensionId(), new S2CPacketSpawnGlobalEntity(entityIn));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Explosion newExplosion(Entity entityIn, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking) {
      Explosion explosion = new Explosion(this, entityIn, x, y, z, strength, isFlaming, isSmoking);
      explosion.doExplosionA();
      explosion.doExplosionB(false);
      if (!isSmoking) {
         explosion.clearAffectedBlockPositions();
      }

      for (EntityPlayer entityPlayer : this.playerEntities) {
         if (entityPlayer.getDistanceSq(x, y, z) < 4096.0) {
            ((EntityPlayerMP)entityPlayer)
               .playerNetServerHandler
               .sendPacket(new S27PacketExplosion(x, y, z, strength, explosion.getAffectedBlockPositions(), explosion.getPlayerKnockbackMap().get(entityPlayer)));
         }
      }

      return explosion;
   }

   @Override
   protected void onEntityAdded(Entity entityIn) {
      super.onEntityAdded(entityIn);
      this.entitiesById.addKey(entityIn.getEntityId(), entityIn);
      this.entitiesByUuid.put(entityIn.getUniqueID(), entityIn);
      Entity[] aentity = entityIn.getParts();
      if (aentity != null) {
         for (int index = 0; index < aentity.length; index++) {
            this.entitiesById.addKey(aentity[index].getEntityId(), aentity[index]);
         }
      }
   }

   @Override
   public void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate) {
      if (!this.canSpawnAnimals() && (entityIn instanceof EntityAnimal || entityIn instanceof EntityWaterMob)) {
         entityIn.setDead();
      }

      if (!this.canSpawnNPCs() && entityIn instanceof INpc) {
         entityIn.setDead();
      }

      super.updateEntityWithOptionalForce(entityIn, forceUpdate);
   }

   protected void saveLevel() throws MinecraftException {
      this.checkSessionLock();
      this.worldInfo.setBorderSize(this.getWorldBorder().getDiameter());
      this.worldInfo.getBorderCenterX(this.getWorldBorder().getCenterX());
      this.worldInfo.getBorderCenterZ(this.getWorldBorder().getCenterZ());
      this.worldInfo.setBorderSafeZone(this.getWorldBorder().getDamageBuffer());
      this.worldInfo.setBorderDamagePerBlock(this.getWorldBorder().getDamageAmount());
      this.worldInfo.setBorderWarningDistance(this.getWorldBorder().getWarningDistance());
      this.worldInfo.setBorderWarningTime(this.getWorldBorder().getWarningTime());
      this.worldInfo.setBorderLerpTarget(this.getWorldBorder().getTargetSize());
      this.worldInfo.setBorderLerpTime(this.getWorldBorder().getTimeUntilTarget());
      this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.mcServer.getConfigurationManager().getHostPlayerData());
      this.mapStorage.saveAllData();
   }

   @Override
   public boolean isCallingFromMinecraftThread() {
      return this.mcServer.isCallingFromMinecraftThread();
   }

   private boolean canSpawnNPCs() {
      return this.mcServer.getCanSpawnNPCs();
   }

   @Override
   public void initialize(WorldSettings settings) {
      if (!this.worldInfo.isInitialized()) {
         try {
            this.createSpawnPosition(settings);
            if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
               this.setDebugWorldSettings();
            }

            super.initialize(settings);
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Exception initializing level");

            try {
               this.addWorldInfoToCrashReport(crashReport);
            } catch (Throwable throwable2) {
            }

            throw new ReportedException(crashReport);
         }

         this.worldInfo.update();
      }
   }

   @Override
   public List getList(StructureBoundingBox structureBoundingBox) {
      ArrayList arrayList = null;

      for (int i = 0; i < 2; i++) {
         if (i == 0) {
            Iterator iterator2 = this.pendingTickListEntriesTreeSet.iterator();
         }

         for (NextTickListEntry nextTickListEntry : this.pendingTickListEntriesThisTick) {
            BlockPos position2 = nextTickListEntry.position;
            if (position2.getX() >= structureBoundingBox.minX && position2.getX() < structureBoundingBox.maxX && position2.getZ() >= structureBoundingBox.minZ && position2.getZ() < structureBoundingBox.maxZ) {
               if (arrayList == null) {
                  arrayList = Lists.newArrayList();
               }

               arrayList.add(nextTickListEntry);
            }
         }
      }

      return arrayList;
   }

   public boolean areAllPlayersAsleep() {
      if (this.allPlayersSleeping && !this.isRemote) {
         for (EntityPlayer entityPlayer : this.playerEntities) {
            if (entityPlayer.isSpectator() || !entityPlayer.isPlayerFullyAsleep()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void spawnParticle(
      EnumParticleTypes particleType,
      double xCoord,
      double yCoord,
      double zCoord,
      int numberOfParticles,
      double xOffset,
      double yOffset,
      double zOffset,
      double particleSpeed,
      int... particleArguments
   ) {
      this.spawnParticle(particleType, false, xCoord, yCoord, zCoord, numberOfParticles, xOffset, yOffset, zOffset, particleSpeed, particleArguments);
   }

   private void createSpawnPosition(WorldSettings settings) {
      if (!this.provider.canRespawnHere()) {
         this.worldInfo.setSpawn(BlockPos.ORIGIN.up(this.provider.getAverageGroundLevel()));
      } else if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
         this.worldInfo.setSpawn(BlockPos.ORIGIN.up());
      } else {
         this.findingSpawnPoint = true;
         WorldChunkManager worldChunkManager = this.provider.getWorldChunkManager();
         List<BiomeGenBase> list = worldChunkManager.getBiomesToSpawnIn();
         Random random = new Random(this.getSeed());
         BlockPos blockPos = worldChunkManager.findBiomePosition(0, 0, 256, list, random);
         int i = 0;
         int averageGroundLevel = this.provider.getAverageGroundLevel();
         int i2 = 0;
         if (blockPos != null) {
            i = blockPos.getX();
            i2 = blockPos.getZ();
         } else {
            logger.warn("Unable to find spawn biome");
         }

         int i3 = 0;

         while (!this.provider.canCoordinateBeSpawn(i, i2)) {
            i += random.nextInt(64) - random.nextInt(64);
            i2 += random.nextInt(64) - random.nextInt(64);
            if (++i3 == 1000) {
               break;
            }
         }

         this.worldInfo.setSpawn(new BlockPos(i, averageGroundLevel, i2));
         this.findingSpawnPoint = false;
         if (settings.isBonusChestEnabled()) {
            this.createBonusChest();
         }
      }
   }

   protected void createBonusChest() {
      WorldGeneratorBonusChest worldGeneratorBonusChest = new WorldGeneratorBonusChest(bonusChestContent);

      for (int index = 0; index < 10; index++) {
         int i = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
         int i2 = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
         BlockPos blockPos = this.getTopSolidOrLiquidBlock(new BlockPos(i, 0, i2)).up();
         if (worldGeneratorBonusChest.generate(this, this.rand, blockPos)) {
            break;
         }
      }
   }

   @Override
   public void updateBlockTick(BlockPos pos, Block blockIn, int delay, int priority) {
      NextTickListEntry nextTickListEntry = new NextTickListEntry(pos, blockIn);
      if (this.scheduledUpdatesAreImmediate && blockIn.getMaterial() != Material.air) {
         if (blockIn.requiresUpdates()) {
            if (this.isAreaLoaded(nextTickListEntry.position.add(-8, -8, -8), nextTickListEntry.position.add(8, 8, 8))) {
               IBlockState blockState2 = this.getBlockState(nextTickListEntry.position);
               if (blockState2.getBlock().getMaterial() != Material.air && blockState2.getBlock() == nextTickListEntry.getBlock()) {
                  blockState2.getBlock().updateTick(this, nextTickListEntry.position, blockState2, this.rand);
               }
            }

            return;
         }

         delay = 1;
      }

      if (this.isAreaLoaded(pos.add(0, 0, 0), pos.add(0, 0, 0))) {
         if (blockIn.getMaterial() != Material.air) {
            nextTickListEntry.setScheduledTime(delay + this.worldInfo.getWorldTotalTime());
            nextTickListEntry.setPriority(priority);
         }

         if (!this.pendingTickListEntriesHashSet.contains(nextTickListEntry)) {
            this.pendingTickListEntriesHashSet.add(nextTickListEntry);
            this.pendingTickListEntriesTreeSet.add(nextTickListEntry);
         }
      }
   }

   private void sendQueuedBlockEvents() {
      while (!this.blockEventQueue[this.blockEventCacheIndex].isEmpty()) {
         int blockEventCacheIndex2 = this.blockEventCacheIndex;
         this.blockEventCacheIndex ^= 1;

         for (BlockEventData blockEventData : this.blockEventQueue[blockEventCacheIndex2]) {
            if (this.fireBlockEvent(blockEventData)) {
               this.mcServer
                  .getConfigurationManager()
                  .sendToAllNear(
                     blockEventData.getPosition().getX(),
                     blockEventData.getPosition().getY(),
                     blockEventData.getPosition().getZ(),
                     64.0,
                     this.provider.getDimensionId(),
                     new S24PacketBlockAction(blockEventData.getPosition(), blockEventData.getBlock(), blockEventData.getEventID(), blockEventData.getEventParameter())
                  );
            }
         }

         this.blockEventQueue[blockEventCacheIndex2].clear();
      }
   }

   private void resetRainAndThunder() {
      this.worldInfo.setRainTime(0);
      this.worldInfo.setRaining(false);
      this.worldInfo.setThunderTime(0);
      this.worldInfo.setThundering(false);
   }

   @Override
   public void scheduleUpdate(BlockPos pos, Block blockIn, int delay) {
      this.updateBlockTick(pos, blockIn, delay, 0);
   }

   @Override
   public void setEntityState(Entity entityIn, byte state) {
      this.getEntityTracker().sendToAllTrackingEntityAndSelf(entityIn, new S19PacketEntityStatus(entityIn, state));
   }

   private boolean canSpawnAnimals() {
      return this.mcServer.getCanSpawnAnimals();
   }

   public WorldServer(MinecraftServer server, ISaveHandler saveHandlerIn, WorldInfo info, int dimensionId, Profiler profilerIn) {
      super(saveHandlerIn, info, WorldProvider.getProviderForDimension(dimensionId), profilerIn, false);
      this.mcServer = server;
      this.theEntityTracker = new EntityTracker(this);
      this.thePlayerManager = new PlayerManager(this);
      this.provider.registerWorld(this);
      this.chunkProvider = this.createChunkProvider();
      this.worldTeleporter = new Teleporter(this);
      this.calculateInitialSkylight();
      this.calculateInitialWeather();
      this.getWorldBorder().setSize(server.getMaxWorldSize());
   }

   public void flush() {
      this.saveHandler.flush();
   }

   public void saveChunkData() {
      if (this.chunkProvider.canSave()) {
         this.chunkProvider.saveExtraData();
      }
   }

   public net.minecraft.world.biome.BiomeGenBase.SpawnListEntry getSpawnListEntryForTypeAt(EnumCreatureType creatureType, BlockPos pos) {
      List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> list = this.getChunkProvider().getPossibleCreatures(creatureType, pos);
      return list != null && !list.isEmpty() ? WeightedRandom.getRandomItem(this.rand, list) : null;
   }

   public boolean canCreatureTypeSpawnHere(EnumCreatureType creatureType, net.minecraft.world.biome.BiomeGenBase.SpawnListEntry spawnListEntry, BlockPos pos) {
      List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> list = this.getChunkProvider().getPossibleCreatures(creatureType, pos);
      return list != null && !list.isEmpty() ? list.contains(spawnListEntry) : false;
   }

   @Override
   public void scheduleBlockUpdate(BlockPos pos, Block blockIn, int delay, int priority) {
      NextTickListEntry nextTickListEntry = new NextTickListEntry(pos, blockIn);
      nextTickListEntry.setPriority(priority);
      if (blockIn.getMaterial() != Material.air) {
         nextTickListEntry.setScheduledTime(delay + this.worldInfo.getWorldTotalTime());
      }

      if (!this.pendingTickListEntriesHashSet.contains(nextTickListEntry)) {
         this.pendingTickListEntriesHashSet.add(nextTickListEntry);
         this.pendingTickListEntriesTreeSet.add(nextTickListEntry);
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getWorldInfo().isHardcoreModeEnabled() && this.getDifficulty() != EnumDifficulty.HARD) {
         this.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
      }

      this.provider.getWorldChunkManager().cleanupCache();
      if (this.areAllPlayersAsleep()) {
         if (this.getGameRules().getBoolean("doDaylightCycle")) {
            long l = this.worldInfo.getWorldTime() + 24000L;
            this.worldInfo.setWorldTime(l - l % 24000L);
         }

         this.wakeAllPlayers();
      }

      this.theProfiler.startSection("mobSpawner");
      if (this.getGameRules().getBoolean("doMobSpawning") && this.worldInfo.getTerrainType() != WorldType.DEBUG_WORLD) {
         this.mobSpawner.findChunksForSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs, this.worldInfo.getWorldTotalTime() % 400L == 0L);
      }

      this.theProfiler.endStartSection("chunkSource");
      this.chunkProvider.unloadQueuedChunks();
      int i = this.calculateSkylightSubtracted(1.0F);
      if (i != this.getSkylightSubtracted()) {
         this.setSkylightSubtracted(i);
      }

      this.worldInfo.setWorldTotalTime(this.worldInfo.getWorldTotalTime() + 1L);
      if (this.getGameRules().getBoolean("doDaylightCycle")) {
         this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
      }

      this.theProfiler.endStartSection("tickPending");
      this.isActive();
      this.theProfiler.endStartSection("tickBlocks");
      this.updateBlocks();
      this.theProfiler.endStartSection("chunkMap");
      this.thePlayerManager.updatePlayerInstances();
      this.theProfiler.endStartSection("village");
      this.villageCollectionObj.tick();
      this.villageSiege.tick();
      this.theProfiler.endStartSection("portalForcer");
      this.worldTeleporter.removeStalePortalLocations(this.getTotalWorldTime());
      this.theProfiler.endSection();
      this.sendQueuedBlockEvents();
   }

   public BlockPos getSpawnCoordinate() {
      return this.provider.getSpawnCoordinate();
   }

   public MinecraftServer getMinecraftServer() {
      return this.mcServer;
   }

   @Override
   public void updateAllPlayersSleepingFlag() {
      this.allPlayersSleeping = false;
      if (!this.playerEntities.isEmpty()) {
         int i = 0;
         int i2 = 0;

         for (EntityPlayer entityPlayer : this.playerEntities) {
            if (entityPlayer.isSpectator()) {
               i++;
            } else if (entityPlayer.isPlayerSleeping()) {
               i2++;
            }
         }

         this.allPlayersSleeping = i2 > 0 && i2 >= this.playerEntities.size() - i;
      }
   }

   @Override
   protected void onEntityRemoved(Entity entityIn) {
      super.onEntityRemoved(entityIn);
      this.entitiesById.removeObject(entityIn.getEntityId());
      this.entitiesByUuid.remove(entityIn.getUniqueID());
      Entity[] aentity = entityIn.getParts();
      if (aentity != null) {
         for (int index = 0; index < aentity.length; index++) {
            this.entitiesById.removeObject(aentity[index].getEntityId());
         }
      }
   }

   @Override
   public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
      BlockEventData blockEventData = new BlockEventData(pos, blockIn, eventID, eventParam);
      Iterator iterator2 = this.blockEventQueue[this.blockEventCacheIndex].iterator();

      while (iterator2.hasNext()) {
         if (((BlockEventData)iterator2.next()).equals(blockEventData)) {
            return;
         }
      }

      this.blockEventQueue[this.blockEventCacheIndex].add(blockEventData);
   }

   private boolean fireBlockEvent(BlockEventData event) {
      IBlockState blockState = this.getBlockState(event.getPosition());
      return blockState.getBlock() == event.getBlock()
         ? blockState.getBlock().onBlockEventReceived(this, event.getPosition(), blockState, event.getEventID(), event.getEventParameter())
         : false;
   }

   @Override
   public boolean isBlockTickPending(BlockPos pos, Block blockType) {
      NextTickListEntry nextTickListEntry = new NextTickListEntry(pos, blockType);
      return this.pendingTickListEntriesThisTick.contains(nextTickListEntry);
   }

   public void resetUpdateEntityTick() {
      this.updateEntityTick = 0;
   }

   public PlayerManager getPlayerManager() {
      return this.thePlayerManager;
   }

   @Override
   public void updateEntities() {
      if (this.playerEntities.isEmpty()) {
         if (this.updateEntityTick++ >= 1200) {
            return;
         }
      } else {
         this.resetUpdateEntityTick();
      }

      super.updateEntities();
   }

   @Override
   public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
      return !this.mcServer.isBlockProtected(this, pos, player) && this.getWorldBorder().contains(pos);
   }

   @Override
   protected void updateWeather() {
      boolean isRaining = this.isRaining();
      super.updateWeather();
      if (this.prevRainingStrength != this.rainingStrength) {
         this.mcServer
            .getConfigurationManager()
            .sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(7, this.rainingStrength), this.provider.getDimensionId());
      }

      if (this.prevThunderingStrength != this.thunderingStrength) {
         this.mcServer
            .getConfigurationManager()
            .sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(8, this.thunderingStrength), this.provider.getDimensionId());
      }

      if (isRaining != this.isRaining()) {
         if (isRaining) {
            this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(2, 0.0F));
         } else {
            this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(1, 0.0F));
         }

         this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(7, this.rainingStrength));
         this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(8, this.thunderingStrength));
      }
   }

   @Override
   protected int getRenderDistanceChunks() {
      return this.mcServer.getConfigurationManager().getViewDistance();
   }

   public void spawnParticle(
      EnumParticleTypes particleType,
      boolean longDistance,
      double xCoord,
      double yCoord,
      double zCoord,
      int numberOfParticles,
      double xOffset,
      double yOffset,
      double zOffset,
      double particleSpeed,
      int... particleArguments
   ) {
      S2APacketParticles s2APacketParticles = new S2APacketParticles(
         particleType,
         longDistance,
         (float)xCoord,
         (float)yCoord,
         (float)zCoord,
         (float)xOffset,
         (float)yOffset,
         (float)zOffset,
         (float)particleSpeed,
         numberOfParticles,
         particleArguments
      );

      for (int i = 0; i < this.playerEntities.size(); i++) {
         EntityPlayerMP entityPlayerMP = (EntityPlayerMP)this.playerEntities.get(i);
         double d = entityPlayerMP.getPosition().distanceSq(xCoord, yCoord, zCoord);
         if (d <= 256.0 || longDistance && d <= 65536.0) {
            entityPlayerMP.playerNetServerHandler.sendPacket(s2APacketParticles);
         }
      }
   }

   public World init() {
      this.mapStorage = new MapStorage(this.saveHandler);
      String text = VillageCollection.fileNameForProvider(this.provider);
      VillageCollection villageCollection = (VillageCollection)this.mapStorage.loadData(VillageCollection.class, text);
      if (villageCollection == null) {
         this.villageCollectionObj = new VillageCollection(this);
         this.mapStorage.setData(text, this.villageCollectionObj);
      } else {
         this.villageCollectionObj = villageCollection;
         this.villageCollectionObj.setWorldsForAll(this);
      }

      this.worldScoreboard = new ServerScoreboard(this.mcServer);
      ScoreboardSaveData scoreboardSaveData = (ScoreboardSaveData)this.mapStorage.loadData(ScoreboardSaveData.class, "scoreboard");
      if (scoreboardSaveData == null) {
         scoreboardSaveData = new ScoreboardSaveData();
         this.mapStorage.setData("scoreboard", scoreboardSaveData);
      }

      scoreboardSaveData.setScoreboard(this.worldScoreboard);
      ((ServerScoreboard)this.worldScoreboard).setScoreboardSaveData(scoreboardSaveData);
      this.getWorldBorder().setCenter(this.worldInfo.getBorderCenterX(), this.worldInfo.getBorderCenterZ());
      this.getWorldBorder().setDamageAmount(this.worldInfo.getBorderDamagePerBlock());
      this.getWorldBorder().setDamageBuffer(this.worldInfo.getBorderSafeZone());
      this.getWorldBorder().setWarningDistance(this.worldInfo.getBorderWarningDistance());
      this.getWorldBorder().setWarningTime(this.worldInfo.getBorderWarningTime());
      if (this.worldInfo.getBorderLerpTime() > 0L) {
         this.getWorldBorder().setTransition(this.worldInfo.getBorderSize(), this.worldInfo.getBorderLerpTarget(), this.worldInfo.getBorderLerpTime());
      } else {
         this.getWorldBorder().setTransition(this.worldInfo.getBorderSize());
      }

      return this;
   }

   private void setDebugWorldSettings() {
      this.worldInfo.update2();
      this.worldInfo.update5();
      this.worldInfo.setRaining(false);
      this.worldInfo.setThundering(false);
      this.worldInfo.setCleanWeatherTime(1000000000);
      this.worldInfo.setWorldTime(6000L);
      this.worldInfo.setGameType(WorldSettings.GameType.SPECTATOR);
      this.worldInfo.update4();
      this.worldInfo.setDifficulty(EnumDifficulty.PEACEFUL);
      this.worldInfo.setDifficultyLocked(true);
      this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
   }

   @Override
   public List getList3(Chunk chunk2) {
      ChunkCoordIntPair chunkCoordIntPair = chunk2.getChunkCoordIntPair();
      int i = (chunkCoordIntPair.chunkXPos << 4) - 2;
      int i2 = i + 16 + 2;
      int i3 = (chunkCoordIntPair.chunkZPos << 4) - 2;
      int i4 = i3 + 16 + 2;
      return this.getList(new StructureBoundingBox(i, 0, i3, i2, 256, i4));
   }

   protected void wakeAllPlayers() {
      this.allPlayersSleeping = false;

      for (EntityPlayer entityPlayer : this.playerEntities) {
         if (entityPlayer.isPlayerSleeping()) {
            entityPlayer.wakeUpPlayer(false, false, true);
         }
      }

      this.resetRainAndThunder();
   }

   protected BlockPos adjustPosToNearbyEntity(BlockPos pos) {
      BlockPos blockPos = this.getPrecipitationHeight(pos);
      AxisAlignedBB axisAlignedBB = new AxisAlignedBB(blockPos, new BlockPos(blockPos.getX(), this.getHeight(), blockPos.getZ())).expand(3.0, 3.0, 3.0);
      List<EntityLivingBase> list = this.getEntitiesWithinAABB(EntityLivingBase.class, axisAlignedBB, new Predicate<EntityLivingBase>() {
         public boolean apply(EntityLivingBase arg1) {
            return arg1 != null && arg1.isEntityAlive() && WorldServer.this.canSeeSky(arg1.getPosition());
         }
      });
      return !list.isEmpty() ? list.get(this.rand.nextInt(list.size())).getPosition() : blockPos;
   }

   @Override
   public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
      return this.mcServer.addScheduledTask(runnableToSchedule);
   }

   public Entity getEntityFromUuid(UUID uUID) {
      return this.entitiesByUuid.get(uUID);
   }

   public List<TileEntity> getTileEntitiesIn(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      List<TileEntity> list = Lists.newArrayList();

      for (int index = 0; index < this.loadedTileEntityList.size(); index++) {
         TileEntity tileEntity = this.loadedTileEntityList.get(index);
         BlockPos blockPos = tileEntity.getPos();
         if (blockPos.getX() >= minX
            && blockPos.getY() >= minY
            && blockPos.getZ() >= minZ
            && blockPos.getX() < maxX
            && blockPos.getY() < maxY
            && blockPos.getZ() < maxZ) {
            list.add(tileEntity);
         }
      }

      return list;
   }

   public EntityTracker getEntityTracker() {
      return this.theEntityTracker;
   }

   static class ServerBlockEventList extends ArrayList<BlockEventData> {
      private ServerBlockEventList() {
      }
   }
}
