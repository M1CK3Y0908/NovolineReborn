package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderClient implements IChunkProvider {
   private static final Logger logger = LogManager.getLogger();
   private LongHashMap<Chunk> chunkMapping = new LongHashMap<>();
   private List<Chunk> chunkListing = Lists.newArrayList();
   private Chunk blankChunk;
   private World worldObj;

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }

   public ChunkProviderClient(World worldIn) {
      this.blankChunk = new EmptyChunk(worldIn, 0, 0);
      this.worldObj = worldIn;
   }

   public Chunk loadChunk(int chunkX, int chunkZ) {
      Chunk chunk2 = new Chunk(this.worldObj, chunkX, chunkZ);
      this.chunkMapping.add(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ), chunk2);
      this.chunkListing.add(chunk2);
      chunk2.update();
      return chunk2;
   }

   @Override
   public boolean canSave() {
      return false;
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      return null;
   }

   @Override
   public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
      return false;
   }

   @Override
   public int getLoadedChunkCount() {
      return this.chunkListing.size();
   }

   @Override
   public String makeString() {
      return "MultiplayerChunkCache: " + this.chunkMapping.getNumHashElements() + ", " + this.chunkListing.size();
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
   }

   public void unloadChunk(int x, int z) {
      Chunk chunk2 = this.provideChunk(x, z);
      if (!chunk2.isEmpty()) {
         chunk2.onChunkUnload();
      }

      this.chunkMapping.remove(ChunkCoordIntPair.chunkXZ2Int(x, z));
      this.chunkListing.remove(chunk2);
   }

   @Override
   public boolean unloadQueuedChunks() {
      long l = System.currentTimeMillis();
      Iterator iterator2 = this.chunkListing.iterator();

      while (iterator2.hasNext()) {
         ((Chunk)iterator2.next()).onTick(System.currentTimeMillis() - l > 5L);
      }

      if (System.currentTimeMillis() - l > 100L) {
         logger.info("Warning: Clientside chunk ticking took {} ms", new Object[]{System.currentTimeMillis() - l});
      }

      return false;
   }

   @Override
   public boolean chunkExists(int x, int z) {
      return true;
   }

   @Override
   public void recreateStructures(Chunk chunkIn, int x, int z) {
   }

   @Override
   public void saveExtraData() {
   }

   @Override
   public boolean checkIProgressUpdate(IProgressUpdate iProgressUpdate) {
      return true;
   }

   @Override
   public List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      return null;
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      Chunk chunk = this.chunkMapping.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(x, z));
      return chunk == null ? this.blankChunk : chunk;
   }
}
