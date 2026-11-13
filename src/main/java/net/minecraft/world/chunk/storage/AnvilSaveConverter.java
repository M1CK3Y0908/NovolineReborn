package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilSaveConverter extends SaveFormatOld {
   private static final Logger logger = LogManager.getLogger();

   @Override
   public List<SaveFormatComparator> getSaveList() throws AnvilConverterException {
      if (this.savesDirectory != null && this.savesDirectory.exists() && this.savesDirectory.isDirectory()) {
         ArrayList arrayList = Lists.newArrayList();

         for (File file : this.savesDirectory.listFiles()) {
            if (file.isDirectory()) {
               String name = file.getName();
               WorldInfo worldInfo2 = this.getWorldInfo(name);
               if (worldInfo2 != null && (worldInfo2.getSaveVersion() == 19132 || worldInfo2.getSaveVersion() == 19133)) {
                  boolean flag = worldInfo2.getSaveVersion() != this.getSaveVersion();
                  String worldName = worldInfo2.getWorldName();
                  if (StringUtils.isEmpty(worldName)) {
                     worldName = name;
                  }

                  arrayList.add(
                     new SaveFormatComparator(
                        name, worldName, worldInfo2.getLastTimePlayed(), 0L, worldInfo2.getGameType(), flag, worldInfo2.isHardcoreModeEnabled(), worldInfo2.areCommandsAllowed()
                     )
                  );
               }
            }
         }

         return arrayList;
      } else {
         throw new AnvilConverterException("Unable to read or access folder where game worlds are saved!");
      }
   }

   protected int getSaveVersion() {
      return 19133;
   }

   @Override
   public boolean convertMapFormat(String filename, IProgressUpdate progressCallback) {
      progressCallback.setLoadingProgress(0);
      ArrayList arrayList = Lists.newArrayList();
      ArrayList arrayList2 = Lists.newArrayList();
      ArrayList arrayList3 = Lists.newArrayList();
      File file = new File(this.savesDirectory, filename);
      File file2 = new File(file, "DIM-1");
      File file3 = new File(file, "DIM1");
      logger.info("Scanning folders...");
      this.addRegionFilesToCollection(file, arrayList);
      if (file2.exists()) {
         this.addRegionFilesToCollection(file2, arrayList2);
      }

      if (file3.exists()) {
         this.addRegionFilesToCollection(file3, arrayList3);
      }

      int i = arrayList.size() + arrayList2.size() + arrayList3.size();
      logger.info("Total conversion count is " + i);
      WorldInfo worldInfo2 = this.getWorldInfo(filename);
      WorldChunkManager worldChunkManager;
      if (worldInfo2.getTerrainType() == WorldType.FLAT) {
         worldChunkManager = new WorldChunkManagerHell(BiomeGenBase.plains, 0.5F);
      } else {
         worldChunkManager = new WorldChunkManager(worldInfo2.getSeed(), worldInfo2.getTerrainType(), worldInfo2.getGeneratorOptions());
      }

      this.convertFile(new File(file, "region"), arrayList, worldChunkManager, 0, i, progressCallback);
      this.convertFile(new File(file2, "region"), arrayList2, new WorldChunkManagerHell(BiomeGenBase.hell, 0.0F), arrayList.size(), i, progressCallback);
      this.convertFile(
         new File(file3, "region"), arrayList3, new WorldChunkManagerHell(BiomeGenBase.sky, 0.0F), arrayList.size() + arrayList2.size(), i, progressCallback
      );
      worldInfo2.update6();
      if (worldInfo2.getTerrainType() == WorldType.DEFAULT_1_1) {
         worldInfo2.setTerrainType(WorldType.DEFAULT);
      }

      this.createFile(filename);
      this.getSaveLoader(filename, false).saveWorldInfo(worldInfo2);
      return true;
   }

   private void convertFile(File file, Iterable<File> iterable, WorldChunkManager worldChunkManager, int i, int i2, IProgressUpdate iProgressUpdate) {
      for (File file2 : iterable) {
         this.convertChunks(file, file2, worldChunkManager, i, i2, iProgressUpdate);
         i++;
         int i3 = (int)Math.round(100.0 * i / i2);
         iProgressUpdate.setLoadingProgress(i3);
      }
   }

   public AnvilSaveConverter(File savesDirectoryIn) {
      super(savesDirectoryIn);
   }

   @Override
   public void flushCache() {
      RegionFileCache.clearRegionFileReferences();
   }

   @Override
   public ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata) {
      return new AnvilSaveHandler(this.savesDirectory, saveName, storePlayerdata);
   }

   private void createFile(String filename) {
      File file = new File(this.savesDirectory, filename);
      if (!file.exists()) {
         logger.warn("Unable to create level.dat_mcr backup");
      } else {
         File file2 = new File(file, "level.dat");
         if (!file2.exists()) {
            logger.warn("Unable to create level.dat_mcr backup");
         } else {
            File file3 = new File(file, "level.dat_mcr");
            if (!file2.renameTo(file3)) {
               logger.warn("Unable to create level.dat_mcr backup");
            }
         }
      }
   }

   private void convertChunks(File file, File file2, WorldChunkManager worldChunkManager, int i, int i2, IProgressUpdate progressCallback) {
      try {
         String name = file2.getName();
         RegionFile regionFile = new RegionFile(file2);
         RegionFile regionFile2 = new RegionFile(new File(file, name.substring(0, name.length() - ".mcr".length()) + ".mca"));

         for (int i3 = 0; i3 < 32; i3++) {
            for (int i4 = 0; i4 < 32; i4++) {
               if (regionFile.isChunkSaved(i3, i4) && !regionFile2.isChunkSaved(i3, i4)) {
                  DataInputStream chunkDataInputStream = regionFile.getChunkDataInputStream(i3, i4);
                  if (chunkDataInputStream == null) {
                     logger.warn("Failed to fetch input stream");
                  } else {
                     NBTTagCompound nbtTagCompound = CompressedStreamTools.read(chunkDataInputStream);
                     chunkDataInputStream.close();
                     ChunkLoader.AnvilConverterData anvilConverterData = ChunkLoader.load(nbtTagCompound.getCompoundTag("Level"));
                     NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
                     NBTTagCompound nbtTagCompound3 = new NBTTagCompound();
                     nbtTagCompound2.setTag("Level", nbtTagCompound3);
                     ChunkLoader.convertToAnvilFormat(anvilConverterData, nbtTagCompound3, worldChunkManager);
                     DataOutputStream chunkDataOutputStream = regionFile2.getChunkDataOutputStream(i3, i4);
                     CompressedStreamTools.write(nbtTagCompound2, chunkDataOutputStream);
                     chunkDataOutputStream.close();
                  }
               }
            }

            int i5 = (int)Math.round(100.0 * (i * 1024) / (i2 * 1024));
            int i6 = (int)Math.round(100.0 * ((i3 + 1) * 32 + i * 1024) / (i2 * 1024));
            if (i6 > i5) {
               progressCallback.setLoadingProgress(i6);
            }
         }

         regionFile.close();
         regionFile2.close();
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   private void addRegionFilesToCollection(File worldDir, Collection<File> collection) {
      File[] afile = new File(worldDir, "region").listFiles(new FilenameFilter() {
         @Override
         public boolean accept(File arg1, String arg2) {
            return arg2.endsWith(".mcr");
         }
      });
      if (afile != null) {
         Collections.addAll(collection, afile);
      }
   }

   @Override
   public boolean isOldMapFormat(String saveName) {
      WorldInfo worldInfo = this.getWorldInfo(saveName);
      return worldInfo != null && worldInfo.getSaveVersion() != this.getSaveVersion();
   }
}
