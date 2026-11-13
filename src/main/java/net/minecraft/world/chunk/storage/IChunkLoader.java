package net.minecraft.world.chunk.storage;

import java.io.IOException;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public interface IChunkLoader {
   void saveExtraChunkData(World world2, Chunk chunk2) throws IOException;

   Chunk loadChunk(World world2, int i, int i2) throws IOException;

   void saveExtraData();

   void chunkTick();

   void saveChunk(World world2, Chunk chunk2) throws MinecraftException, IOException;
}
