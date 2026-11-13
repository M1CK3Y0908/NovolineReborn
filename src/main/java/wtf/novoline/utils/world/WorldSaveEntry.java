package wtf.novoline.utils.world;

import net.minecraft.world.storage.SaveFormatComparator;

public class WorldSaveEntry implements Comparable<WorldSaveEntry> {
   private SaveFormatComparator saveFormatComparator;

   public int compareTo(WorldSaveEntry worldSaveEntry) {
      return this.saveFormatComparator.getLastTimePlayed() < worldSaveEntry.getLastTimePlayed()
         ? 1
         : (
            this.saveFormatComparator.getLastTimePlayed() > worldSaveEntry.getLastTimePlayed()
               ? -1
               : this.saveFormatComparator.getFileName().compareTo(worldSaveEntry.getFileName())
         );
   }

   public String getFileName() {
      return this.saveFormatComparator.getFileName();
   }

   public WorldSaveEntry(SaveFormatComparator saveFormatComparator2) {
      this.saveFormatComparator = saveFormatComparator2;
   }

   public boolean requiresConversion() {
      return this.saveFormatComparator.requiresConversion();
   }

   public boolean isHardcore() {
      return this.saveFormatComparator.isHardcoreModeEnabled();
   }

   public long getLastTimePlayed() {
      return this.saveFormatComparator.getLastTimePlayed();
   }

   public int getGameTypeId() {
      return this.saveFormatComparator.getEnumGameType().getID();
   }

   public boolean areCheatsEnabled() {
      return this.saveFormatComparator.getCheatsEnabled();
   }

   public long getSizeOnDisk() {
      return this.saveFormatComparator.getSizeOnDisk();
   }

   public String getDisplayName() {
      return this.saveFormatComparator.getDisplayName();
   }
}
