package net.minecraft.world.storage;

import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.util.IProgressUpdate;

public interface ISaveFormat {
   boolean canLoadWorld(String text);

   WorldInfo getWorldInfo(String text);

   void renameWorld(String text, String text2);

   boolean convertMapFormat(String text, IProgressUpdate iProgressUpdate);

   ISaveHandler getSaveLoader(String text, boolean flag);

   boolean isOldMapFormat(String text);

   boolean deleteWorldDirectory(String text);

   void flushCache();

   List<SaveFormatComparator> getSaveList() throws AnvilConverterException;
}
