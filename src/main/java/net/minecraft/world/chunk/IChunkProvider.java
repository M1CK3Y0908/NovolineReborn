package net.minecraft.world.chunk;

import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.World;

public interface IChunkProvider {
   void recreateStructures(Chunk chunk2, int i, int i2);

   void saveExtraData();

   Chunk provideChunk(int i, int i2);

   Chunk provideChunk(BlockPos blockPos);

   void populate(IChunkProvider iChunkProvider, int i, int i2);

   boolean checkIProgressUpdate(IProgressUpdate iProgressUpdate);

   List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType enumCreatureType, BlockPos blockPos);

   boolean populateChunk(IChunkProvider iChunkProvider, Chunk chunk2, int i, int i2);

   boolean chunkExists(int i, int i2);

   int getLoadedChunkCount();

   boolean unloadQueuedChunks();

   BlockPos getStrongholdGen(World world2, String text, BlockPos blockPos);

   String makeString();

   boolean canSave();
}
