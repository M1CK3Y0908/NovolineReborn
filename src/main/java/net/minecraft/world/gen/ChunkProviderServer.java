package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer implements IChunkProvider {
   private Set<Long> droppedChunksSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
   public boolean chunkLoadOverride = true;
   private LongHashMap<Chunk> id2ChunkMap = new LongHashMap<>();
   private static final Logger logger = LogManager.getLogger();
   private List<Chunk> loadedChunks = Lists.newArrayList();
   private Chunk dummyChunk;
   private WorldServer worldObj;
   private IChunkLoader chunkLoader;
   private IChunkProvider serverChunkGenerator;

   private void saveChunkExtraData(Chunk chunkIn) {
      if (this.chunkLoader != null) {
         try {
            this.chunkLoader.saveExtraChunkData(this.worldObj, chunkIn);
         } catch (Exception exception) {
            logger.error("Couldn't save entities", exception);
         }
      }
   }

   @Override
   public String makeString() {
      return "ServerChunkCache: " + this.id2ChunkMap.getNumHashElements() + " Drop: " + this.droppedChunksSet.size();
   }

   private void saveChunkData(Chunk chunkIn) {
      if (this.chunkLoader != null) {
         try {
            chunkIn.setLastSaveTime(this.worldObj.getTotalWorldTime());
            this.chunkLoader.saveChunk(this.worldObj, chunkIn);
         } catch (IOException ioException) {
            logger.error("Couldn't save chunk", ioException);
         } catch (MinecraftException minecraftException) {
            logger.error("Couldn't save chunk; already in use by another instance of Minecraft?", minecraftException);
         }
      }
   }

   public Chunk loadChunk(int chunkX, int chunkZ) {
      long l = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);
      this.droppedChunksSet.remove(l);
      Chunk valueByKey = this.id2ChunkMap.getValueByKey(l);
      if (valueByKey == null) {
         valueByKey = this.loadChunkFromFile(chunkX, chunkZ);
         if (valueByKey == null) {
            if (this.serverChunkGenerator == null) {
               valueByKey = this.dummyChunk;
            } else {
               try {
                  valueByKey = this.serverChunkGenerator.provideChunk(chunkX, chunkZ);
               } catch (Throwable throwable) {
                  CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
                  CrashReportCategory crashReportCategory = crashReport.makeCategory("Chunk to be generated");
                  crashReportCategory.addCrashSection("Location", String.format("%d,%d", chunkX, chunkZ));
                  crashReportCategory.addCrashSection("Position hash", l);
                  crashReportCategory.addCrashSection("Generator", this.serverChunkGenerator.makeString());
                  throw new ReportedException(crashReport);
               }
            }
         }

         this.id2ChunkMap.add(l, valueByKey);
         this.loadedChunks.add(valueByKey);
         valueByKey.onChunkLoad();
         valueByKey.populateChunk(this, this, chunkX, chunkZ);
      }

      return valueByKey;
   }

   public void dropChunk(int x, int z) {
      if (this.worldObj.provider.canRespawnHere()) {
         if (!this.worldObj.isSpawnChunk(x, z)) {
            this.droppedChunksSet.add(ChunkCoordIntPair.chunkXZ2Int(x, z));
         }
      } else {
         this.droppedChunksSet.add(ChunkCoordIntPair.chunkXZ2Int(x, z));
      }
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      Chunk chunk = this.id2ChunkMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(x, z));
      return chunk == null ? (!this.worldObj.isFindingSpawnPoint() && !this.chunkLoadOverride ? this.dummyChunk : this.loadChunk(x, z)) : chunk;
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }

   @Override
   public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
      if (this.serverChunkGenerator != null && this.serverChunkGenerator.populateChunk(chunkProvider, chunkIn, x, z)) {
         this.provideChunk(x, z).setChunkModified();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void recreateStructures(Chunk chunkIn, int x, int z) {
   }

   private Chunk loadChunkFromFile(int x, int z) {
      if (this.chunkLoader == null) {
         return null;
      }

      try {
         Chunk chunk = this.chunkLoader.loadChunk(this.worldObj, x, z);
         if (chunk != null) {
            chunk.setLastSaveTime(this.worldObj.getTotalWorldTime());
            if (this.serverChunkGenerator != null) {
               this.serverChunkGenerator.recreateStructures(chunk, x, z);
            }
         }

         return chunk;
      } catch (Exception exception) {
         logger.error("Couldn't load chunk", exception);
         return null;
      }
   }

   public List<Chunk> getLoadedChunks() {
      return this.loadedChunks;
   }

   public ChunkProviderServer(WorldServer worldServer, IChunkLoader iChunkLoader, IChunkProvider iChunkProvider) {
      this.dummyChunk = new EmptyChunk(worldServer, 0, 0);
      this.worldObj = worldServer;
      this.chunkLoader = iChunkLoader;
      this.serverChunkGenerator = iChunkProvider;
   }

   public void unloadAllChunks() {
      for (Chunk chunk2 : this.loadedChunks) {
         this.dropChunk(chunk2.xPosition, chunk2.zPosition);
      }
   }

   @Override
   public boolean unloadQueuedChunks() {
      if (!this.worldObj.disableLevelSaving) {
         for (int index = 0; index < 100; index++) {
            if (!this.droppedChunksSet.isEmpty()) {
               Long olong = this.droppedChunksSet.iterator().next();
               Chunk chunk = this.id2ChunkMap.getValueByKey(olong);
               if (chunk != null) {
                  chunk.onChunkUnload();
                  this.saveChunkData(chunk);
                  this.saveChunkExtraData(chunk);
                  this.id2ChunkMap.remove(olong);
                  this.loadedChunks.remove(chunk);
               }

               this.droppedChunksSet.remove(olong);
            }
         }

         if (this.chunkLoader != null) {
            this.chunkLoader.chunkTick();
         }
      }

      return this.serverChunkGenerator.unloadQueuedChunks();
   }

   @Override
   public boolean checkIProgressUpdate(IProgressUpdate iProgressUpdate) {
      int i = 0;
      ArrayList arrayList = Lists.newArrayList(this.loadedChunks);

      for (int i2 = 0; i2 < arrayList.size(); i2++) {
         Chunk chunk2 = (Chunk)arrayList.get(i2);
         this.saveChunkExtraData(chunk2);
         if (chunk2.isLightInitialized()) {
            this.saveChunkData(chunk2);
            chunk2.update2();
            if (++i == 24) {
            }
         }
      }

      return true;
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      return this.serverChunkGenerator.getStrongholdGen(worldIn, structureName, position);
   }

   @Override
   public int getLoadedChunkCount() {
      return this.id2ChunkMap.getNumHashElements();
   }

   @Override
   public boolean canSave() {
      return !this.worldObj.disableLevelSaving;
   }

   @Override
   public List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      return this.serverChunkGenerator.getPossibleCreatures(creatureType, pos);
   }

   @Override
   public boolean chunkExists(int x, int z) {
      return this.id2ChunkMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(x, z));
   }

   @Override
   public void saveExtraData() {
      if (this.chunkLoader != null) {
         this.chunkLoader.saveExtraData();
      }
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
      Chunk chunk = this.provideChunk(x, z);
      if (!chunk.isTerrainPopulated()) {
         chunk.checkLight();
         if (this.serverChunkGenerator != null) {
            this.serverChunkGenerator.populate(chunkProvider, x, z);
            chunk.setChunkModified();
         }
      }
   }
}
