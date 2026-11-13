package net.minecraft.world.storage;


public class SaveFormatComparator implements Comparable<SaveFormatComparator> {
   private final String fileName;
   private final String displayName;
   private final long lastTimePlayed;
   private final long sizeOnDisk;
   private final net.minecraft.world.WorldSettings.GameType theEnumGameType;
   private final boolean requiresConversion;
   private final boolean hardcore;
   private final boolean cheatsEnabled;

   public net.minecraft.world.WorldSettings.GameType getEnumGameType() {
      return this.theEnumGameType;
   }

   public boolean requiresConversion() {
      return this.requiresConversion;
   }

   public SaveFormatComparator(
      String fileNameIn,
      String displayNameIn,
      long lastTimePlayedIn,
      long sizeOnDiskIn,
      net.minecraft.world.WorldSettings.GameType theEnumGameTypeIn,
      boolean requiresConversionIn,
      boolean hardcoreIn,
      boolean cheatsEnabledIn
   ) {
      this.fileName = fileNameIn;
      this.displayName = displayNameIn;
      this.lastTimePlayed = lastTimePlayedIn;
      this.sizeOnDisk = sizeOnDiskIn;
      this.theEnumGameType = theEnumGameTypeIn;
      this.requiresConversion = requiresConversionIn;
      this.hardcore = hardcoreIn;
      this.cheatsEnabled = cheatsEnabledIn;
   }

   public boolean isHardcoreModeEnabled() {
      return this.hardcore;
   }

   public String getFileName() {
      return this.fileName;
   }

   public long getLastTimePlayed() {
      return this.lastTimePlayed;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public int compareTo(SaveFormatComparator arg1) {
      return this.lastTimePlayed < arg1.lastTimePlayed ? 1 : (this.lastTimePlayed > arg1.lastTimePlayed ? -1 : this.fileName.compareTo(arg1.fileName));
   }

   public boolean getCheatsEnabled() {
      return this.cheatsEnabled;
   }

   public long getSizeOnDisk() {
      return this.sizeOnDisk;
   }
}
