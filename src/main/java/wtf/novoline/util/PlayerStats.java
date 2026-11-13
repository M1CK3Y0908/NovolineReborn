package wtf.novoline.util;

import com.google.gson.annotations.SerializedName;

public class PlayerStats {
   @SerializedName("Bedwars")
   private BedWarsStatsWrapper bedWarsStats;
   @SerializedName("SkyWars")
   private SkyWarsStatsWrapper skyWarsStats;

   public SkyWarsStatsWrapper getSkyWarsStats() {
      return this.skyWarsStats;
   }

   public void setBedWarsStats(BedWarsStatsWrapper bedWarsStatsWrapper) {
      this.bedWarsStats = bedWarsStatsWrapper;
   }

   public void setSkyWarsStats(SkyWarsStatsWrapper skyWarsStatsWrapper) {
      this.skyWarsStats = skyWarsStatsWrapper;
   }

   protected boolean canEqual(Object object) {
      return object instanceof PlayerStats;
   }

   @Override
   public int hashCode() {
      SkyWarsStatsWrapper skyWarsStats2 = this.getSkyWarsStats();
      int i = 59 + (skyWarsStats2 == null ? 43 : skyWarsStats2.hashCode());
      BedWarsStatsWrapper bedWarsStats2 = this.getBedWarsStats();
      return i * 59 + (bedWarsStats2 == null ? 43 : bedWarsStats2.hashCode());
   }

   public BedWarsStatsWrapper getBedWarsStats() {
      return this.bedWarsStats;
   }

   @Override
   public String toString() {
      return "PlayerStats(skyWarsStats=" + this.getSkyWarsStats() + ", bedWarsStats=" + this.getBedWarsStats() + ")";
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof PlayerStats playerStats)) {
         return false;
      } else if (!playerStats.canEqual(this)) {
         return false;
      } else {
         SkyWarsStatsWrapper skyWarsStats2 = this.getSkyWarsStats();
         SkyWarsStatsWrapper skyWarsStats3 = playerStats.getSkyWarsStats();
         if (skyWarsStats2 == null ? skyWarsStats3 == null : skyWarsStats2.equals(skyWarsStats3)) {
            BedWarsStatsWrapper bedWarsStats2 = this.getBedWarsStats();
            BedWarsStatsWrapper bedWarsStats3 = playerStats.getBedWarsStats();
            return bedWarsStats2 == null ? bedWarsStats3 == null : bedWarsStats2.equals(bedWarsStats3);
         } else {
            return false;
         }
      }
   }
}
