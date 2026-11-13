package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class RegionFileCache {
   private static final Map<File, RegionFile> regionsByFilename = Maps.newHashMap();

   public static DataOutputStream getChunkOutputStream(File worldDir, int chunkX, int chunkZ) {
      return createOrLoadRegionFile(worldDir, chunkX, chunkZ).getChunkDataOutputStream(chunkX & 31, chunkZ & 31);
   }

   public static DataInputStream getChunkInputStream(File worldDir, int chunkX, int chunkZ) {
      return createOrLoadRegionFile(worldDir, chunkX, chunkZ).getChunkDataInputStream(chunkX & 31, chunkZ & 31);
   }

   public static synchronized void clearRegionFileReferences() {
      for (RegionFile regionFile2 : regionsByFilename.values()) {
         try {
            if (regionFile2 != null) {
               regionFile2.close();
            }
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      }

      regionsByFilename.clear();
   }

   public static synchronized RegionFile createOrLoadRegionFile(File worldDir, int chunkX, int chunkZ) {
      File file = new File(worldDir, "region");
      File file2 = new File(file, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mca");
      RegionFile regionFile = regionsByFilename.get(file2);
      if (regionFile != null) {
         return regionFile;
      }

      if (!file.exists()) {
         file.mkdirs();
      }

      if (regionsByFilename.size() >= 256) {
         clearRegionFileReferences();
      }

      RegionFile regionFile2 = new RegionFile(file2);
      regionsByFilename.put(file2, regionFile2);
      return regionFile2;
   }
}
