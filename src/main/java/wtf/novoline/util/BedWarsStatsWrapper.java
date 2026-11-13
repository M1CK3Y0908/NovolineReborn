package wtf.novoline.util;

import com.google.gson.annotations.SerializedName;
import wtf.novoline.api.GroupName;
import wtf.novoline.api.annotation.NoGroup;
import wtf.novoline.api.annotation.NoGroup.BeforeKeyType;

public class BedWarsStatsWrapper {
   @NoGroup(value = "four_four", beforeKeyType = BeforeKeyType.BEFORE_KEY)
   private transient BedWarsStatsWrapper.BedWarsStats fourFourStats;
   public static final long longValue = 100L;
   @NoGroup(beforeKeyType = BeforeKeyType.NO_GROUP)
   private transient BedWarsStatsWrapper.BedWarsStats stats;
   @NoGroup(value = "castle", beforeKeyType = BeforeKeyType.BEFORE_KEY)
   private transient BedWarsStatsWrapper.BedWarsStats castleStats;
   @SerializedName("Experience")
   private long experience;
   @NoGroup(value = "eight_one", beforeKeyType = BeforeKeyType.BEFORE_KEY)
   private transient BedWarsStatsWrapper.BedWarsStats eightOneStats;
   @NoGroup(value = "four_three", beforeKeyType = BeforeKeyType.BEFORE_KEY)
   private transient BedWarsStatsWrapper.BedWarsStats fourThreeStats;
   @NoGroup(value = "custom", beforeKeyType = BeforeKeyType.BEFORE_KEY)
   private transient BedWarsStatsWrapper.BedWarsStats customStats;
   @NoGroup(value = "eight_two", beforeKeyType = BeforeKeyType.BEFORE_KEY)
   private transient BedWarsStatsWrapper.BedWarsStats eightTwoStats;
   public static final long longValue2 = 489000L;

   public BedWarsStatsWrapper.BedWarsStats getStats() {
      return this.stats;
   }

   public BedWarsStatsWrapper.BedWarsStats getCustomStats() {
      return this.customStats;
   }

   public BedWarsStatsWrapper.BedWarsStats getFourFourStats() {
      return this.fourFourStats;
   }

   protected boolean canEqual(Object object) {
      return object instanceof BedWarsStatsWrapper;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof BedWarsStatsWrapper bedWarsStatsWrapper)) {
         return false;
      } else {
         return !bedWarsStatsWrapper.canEqual(this) ? false : this.getExperience() == bedWarsStatsWrapper.getExperience();
      }
   }

   @Override
   public String toString() {
      long experience2 = this.getExperience();
      return "BedWarsStatsWrapper(stats="
         + this.getStats()
         + ", customStats="
         + this.getCustomStats()
         + ", castleStats="
         + this.getCastleStats()
         + ", eightOneStats="
         + this.getEightOneStats()
         + ", fourFourStats="
         + this.getFourFourStats()
         + ", eightTwoStats="
         + this.getEightTwoStats()
         + ", fourThreeStats="
         + this.getFourThreeStats()
         + ", experience="
         + experience2
         + ")";
   }

   public BedWarsStatsWrapper.BedWarsStats getEightOneStats() {
      return this.eightOneStats;
   }

   public BedWarsStatsWrapper.BedWarsStats getFourThreeStats() {
      return this.fourThreeStats;
   }

   @Override
   public int hashCode() {
      long experience2 = this.getExperience();
      return 59 + (int)(experience2 >>> 32 ^ experience2);
   }

   public void setStats(BedWarsStatsWrapper.BedWarsStats bedWarsStats) {
      this.stats = bedWarsStats;
   }

   public void setFourFourStats(BedWarsStatsWrapper.BedWarsStats bedWarsStats) {
      this.fourFourStats = bedWarsStats;
   }

   public long getExperience() {
      return this.experience;
   }

   public void setEightOneStats(BedWarsStatsWrapper.BedWarsStats bedWarsStats) {
      this.eightOneStats = bedWarsStats;
   }

   public void setCustomStats(BedWarsStatsWrapper.BedWarsStats bedWarsStats) {
      this.customStats = bedWarsStats;
   }

   public void setFourThreeStats(BedWarsStatsWrapper.BedWarsStats bedWarsStats) {
      this.fourThreeStats = bedWarsStats;
   }

   public BedWarsStatsWrapper.BedWarsStats getCastleStats() {
      return this.castleStats;
   }

   public void setEightTwoStats(BedWarsStatsWrapper.BedWarsStats bedWarsStats) {
      this.eightTwoStats = bedWarsStats;
   }

   public BedWarsStatsWrapper.BedWarsStats getEightTwoStats() {
      return this.eightTwoStats;
   }

   public void setCastleStats(BedWarsStatsWrapper.BedWarsStats bedWarsStats) {
      this.castleStats = bedWarsStats;
   }

   public static final class BedWarsStats {
      @SerializedName("final_deaths_bedwars")
      private long finalDeaths;
      @SerializedName("kills_bedwars")
      private long kills;
      @GroupName
      private String modeName;
      @SerializedName("winstreak")
      private long winstreak;
      @SerializedName("wins_bedwars")
      private long wins;
      @SerializedName("deaths_bedwars")
      private long deaths;
      @SerializedName("final_kills_bedwars")
      private long finalKills;
      @SerializedName("losses_bedwars")
      private long losses;

      public long getWinstreak() {
         return this.winstreak;
      }

      public void setModeName(String text) {
         this.modeName = text;
      }

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof BedWarsStatsWrapper.BedWarsStats bedWarsStats)) {
            return false;
         } else {
            if (this.getDeaths() != bedWarsStats.getDeaths()) {
               return false;
            }

            if (this.getFinalDeaths() != bedWarsStats.getFinalDeaths()) {
               return false;
            }

            if (this.getWins() != bedWarsStats.getWins()) {
               return false;
            }

            if (this.getLosses() != bedWarsStats.getLosses()) {
               return false;
            }

            if (this.getKills() != bedWarsStats.getKills()) {
               return false;
            }

            if (this.getFinalKills() != bedWarsStats.getFinalKills()) {
               return false;
            }

            if (this.getWinstreak() != bedWarsStats.getWinstreak()) {
               return false;
            }

            String modeName2 = this.getModeName();
            String modeName3 = bedWarsStats.getModeName();
            return modeName2 == null ? modeName3 == null : modeName2.equals(modeName3);
         }
      }

      public long getKills() {
         return this.kills;
      }

      public long getFinalDeaths() {
         return this.finalDeaths;
      }

      public long getLosses() {
         return this.losses;
      }

      @Override
      public String toString() {
         String modeName2 = this.getModeName();
         long deaths2 = this.getDeaths();
         long finalDeaths2 = this.getFinalDeaths();
         long wins2 = this.getWins();
         long losses2 = this.getLosses();
         long kills2 = this.getKills();
         long winstreak2 = this.getWinstreak();
         long finalKills2 = this.getFinalKills();
         long l = kills2;
         long l2 = losses2;
         long l3 = wins2;
         long l4 = finalDeaths2;
         long l5 = deaths2;
         return "BedWarsStatsWrapper.BedWarsStats(modeName="
            + modeName2
            + ", deaths="
            + l5
            + ", finalDeaths="
            + l4
            + ", wins="
            + l3
            + ", losses="
            + l2
            + ", kills="
            + l
            + ", finalKills="
            + finalKills2
            + ", winstreak="
            + winstreak2
            + ")";
      }

      @Override
      public int hashCode() {
         long deaths2 = this.getDeaths();
         int i = 59 + (int)(deaths2 >>> 32 ^ deaths2);
         long finalDeaths2 = this.getFinalDeaths();
         i = i * 59 + (int)(finalDeaths2 >>> 32 ^ finalDeaths2);
         long wins2 = this.getWins();
         i = i * 59 + (int)(wins2 >>> 32 ^ wins2);
         long losses2 = this.getLosses();
         i = i * 59 + (int)(losses2 >>> 32 ^ losses2);
         long kills2 = this.getKills();
         i = i * 59 + (int)(kills2 >>> 32 ^ kills2);
         long finalKills2 = this.getFinalKills();
         i = i * 59 + (int)(finalKills2 >>> 32 ^ finalKills2);
         long winstreak2 = this.getWinstreak();
         i = i * 59 + (int)(winstreak2 >>> 32 ^ winstreak2);
         String modeName2 = this.getModeName();
         return i * 59 + (modeName2 == null ? 43 : modeName2.hashCode());
      }

      public long getDeaths() {
         return this.deaths;
      }

      public String getModeName() {
         return this.modeName;
      }

      public long getWins() {
         return this.wins;
      }

      public long getFinalKills() {
         return this.finalKills;
      }
   }
}
