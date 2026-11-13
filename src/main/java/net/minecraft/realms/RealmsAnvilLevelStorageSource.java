package net.minecraft.realms;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.world.storage.ISaveFormat;

public class RealmsAnvilLevelStorageSource {
   private ISaveFormat iSaveFormat;

   public boolean checkString(String text) {
      return this.iSaveFormat.canLoadWorld(text);
   }

   public RealmsAnvilLevelStorageSource(ISaveFormat iSaveFormat2) {
      this.iSaveFormat = iSaveFormat2;
   }

   private static AnvilConverterException createAnvilConverterException(AnvilConverterException anvilConverterException) {
      return anvilConverterException;
   }
}
