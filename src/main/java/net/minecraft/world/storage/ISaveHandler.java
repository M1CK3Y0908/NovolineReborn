package net.minecraft.world.storage;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;

public interface ISaveHandler {
   String getWorldDirectoryName();

   void saveWorldInfoWithPlayer(WorldInfo worldInfo, NBTTagCompound nbtTagCompound);

   IPlayerFileData getPlayerNBTManager();

   void checkSessionLock() throws MinecraftException;

   void flush();

   void saveWorldInfo(WorldInfo worldInfo);

   File getWorldDirectory();

   File getMapFileFromName(String text);

   WorldInfo loadWorldInfo();

   IChunkLoader getChunkLoader(WorldProvider worldProvider);
}
