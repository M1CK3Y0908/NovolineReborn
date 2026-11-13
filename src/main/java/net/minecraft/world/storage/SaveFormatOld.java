package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormatOld implements ISaveFormat {
   protected final File savesDirectory;
   private static final Logger logger = LogManager.getLogger();

   @Override
   public List<SaveFormatComparator> getSaveList() throws AnvilConverterException {
      List<SaveFormatComparator> list = Lists.newArrayList();

      for (int index = 0; index < 5; index++) {
         String text = "World" + (index + 1);
         WorldInfo worldInfo = this.getWorldInfo(text);
         if (worldInfo != null) {
            list.add(
               new SaveFormatComparator(
                  text,
                  "",
                  worldInfo.getLastTimePlayed(),
                  worldInfo.getSizeOnDisk(),
                  worldInfo.getGameType(),
                  false,
                  worldInfo.isHardcoreModeEnabled(),
                  worldInfo.areCommandsAllowed()
               )
            );
         }
      }

      return list;
   }

   @Override
   public boolean deleteWorldDirectory(String saveName) {
      File file2 = new File(this.savesDirectory, saveName);
      if (!file2.exists()) {
         return true;
      }

      logger.info("Deleting level " + saveName);

      for (int index = 1; index <= 5; index++) {
         logger.info("Attempt " + index + "...");
         if (deleteFiles(file2.listFiles())) {
            break;
         }

         logger.warn("Unsuccessful in deleting contents.");
         if (index < 5) {
            try {
               Thread.sleep(500L);
            } catch (InterruptedException interruptedException) {
            }
         }
      }

      return file2.delete();
   }

   @Override
   public void flushCache() {
   }

   public SaveFormatOld(File savesDirectoryIn) {
      if (!savesDirectoryIn.exists()) {
         savesDirectoryIn.mkdirs();
      }

      this.savesDirectory = savesDirectoryIn;
   }

   @Override
   public boolean canLoadWorld(String saveName) {
      return new File(this.savesDirectory, saveName).isDirectory();
   }

   @Override
   public boolean convertMapFormat(String filename, IProgressUpdate progressCallback) {
      return false;
   }

   @Override
   public void renameWorld(String dirName, String newName) {
      File file2 = new File(this.savesDirectory, dirName);
      if (file2.exists()) {
         File file3 = new File(file2, "level.dat");
         if (file3.exists()) {
            try {
               NBTTagCompound compressed = CompressedStreamTools.readCompressed(new FileInputStream(file3));
               compressed.getCompoundTag("Data").setString("LevelName", newName);
               CompressedStreamTools.writeCompressed(compressed, new FileOutputStream(file3));
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }
      }
   }

   @Override
   public boolean isOldMapFormat(String text) {
      return false;
   }

   @Override
   public WorldInfo getWorldInfo(String saveName) {
      File file2 = new File(this.savesDirectory, saveName);
      if (!file2.exists()) {
         return null;
      }

      File file3 = new File(file2, "level.dat");
      if (file3.exists()) {
         try {
            NBTTagCompound compressed = CompressedStreamTools.readCompressed(new FileInputStream(file3));
            NBTTagCompound compoundTag = compressed.getCompoundTag("Data");
            return new WorldInfo(compoundTag);
         } catch (Exception exception) {
            logger.error("Exception reading " + file3, exception);
         }
      }

      file3 = new File(file2, "level.dat_old");
      if (file3.exists()) {
         try {
            NBTTagCompound compressed2 = CompressedStreamTools.readCompressed(new FileInputStream(file3));
            NBTTagCompound compoundTag2 = compressed2.getCompoundTag("Data");
            return new WorldInfo(compoundTag2);
         } catch (Exception exception2) {
            logger.error("Exception reading " + file3, exception2);
         }
      }

      return null;
   }

   @Override
   public ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata) {
      return new SaveHandler(this.savesDirectory, saveName, storePlayerdata);
   }

   protected static boolean deleteFiles(File[] files) {
      for (int index = 0; index < files.length; index++) {
         File file2 = files[index];
         logger.debug("Deleting " + file2);
         if (file2.isDirectory() && !deleteFiles(file2.listFiles())) {
            logger.warn("Couldn't delete directory " + file2);
            return false;
         }

         if (!file2.delete()) {
            logger.warn("Couldn't delete file " + file2);
            return false;
         }
      }

      return true;
   }
}
