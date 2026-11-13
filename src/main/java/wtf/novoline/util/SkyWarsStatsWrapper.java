package wtf.novoline.util;

import com.google.gson.annotations.SerializedName;
import wtf.novoline.api.GroupName;
import wtf.novoline.api.annotation.NoGroup;
import wtf.novoline.api.annotation.NoGroup.BeforeKeyType;

public class SkyWarsStatsWrapper {
   @NoGroup("ranked")
   private transient SkyWarsStatsWrapper.SkyWarsStats rankedStats;
   @NoGroup("solo_insane")
   private transient SkyWarsStatsWrapper.SkyWarsStats soloInsaneStats;
   @NoGroup(beforeKeyType = BeforeKeyType.NO_GROUP)
   private transient SkyWarsStatsWrapper.SkyWarsStats stats;
   @NoGroup("solo_normal")
   private transient SkyWarsStatsWrapper.SkyWarsStats soloNormalStats;
   @NoGroup("team")
   private transient SkyWarsStatsWrapper.SkyWarsStats teamStats;
   @SerializedName("levelFormatted")
   private String levelFormatted;
   @NoGroup("solo")
   private transient SkyWarsStatsWrapper.SkyWarsStats soloStats;
   @NoGroup("team_insane")
   private transient SkyWarsStatsWrapper.SkyWarsStats teamInsaneStats;
   @NoGroup("team_normal")
   private transient SkyWarsStatsWrapper.SkyWarsStats teamNormalStats;
   @SerializedName("win_streak")
   private long winStreak;

   public void setRankedStats(SkyWarsStatsWrapper.SkyWarsStats skyWarsStats) {
      this.rankedStats = skyWarsStats;
   }

   public void setSoloInsaneStats(SkyWarsStatsWrapper.SkyWarsStats skyWarsStats) {
      this.soloInsaneStats = skyWarsStats;
   }

   public long getWinStreak() {
      return this.winStreak;
   }

   public void setSoloStats(SkyWarsStatsWrapper.SkyWarsStats skyWarsStats) {
      this.soloStats = skyWarsStats;
   }

   public SkyWarsStatsWrapper.SkyWarsStats getTeamStats() {
      return this.teamStats;
   }

   public SkyWarsStatsWrapper.SkyWarsStats getRankedStats() {
      return this.rankedStats;
   }

   public SkyWarsStatsWrapper.SkyWarsStats getTeamNormalStats() {
      return this.teamNormalStats;
   }

   public void setLevelFormatted(String text) {
      this.levelFormatted = text;
   }

   @Override
   public int hashCode() {
      long winStreak2 = this.getWinStreak();
      int i = 59 + (int)(winStreak2 >>> 32 ^ winStreak2);
      String levelFormatted2 = this.getLevelFormatted();
      return i * 59 + (levelFormatted2 == null ? 43 : levelFormatted2.hashCode());
   }

   public SkyWarsStatsWrapper.SkyWarsStats getSoloInsaneStats() {
      return this.soloInsaneStats;
   }

   public void setStats(SkyWarsStatsWrapper.SkyWarsStats skyWarsStats) {
      this.stats = skyWarsStats;
   }

   public SkyWarsStatsWrapper.SkyWarsStats getTeamInsaneStats() {
      return this.teamInsaneStats;
   }

   public void setTeamStats(SkyWarsStatsWrapper.SkyWarsStats skyWarsStats) {
      this.teamStats = skyWarsStats;
   }

   protected boolean canEqual(Object object) {
      return object instanceof SkyWarsStatsWrapper;
   }

   public SkyWarsStatsWrapper.SkyWarsStats getSoloNormalStats() {
      return this.soloNormalStats;
   }

   public SkyWarsStatsWrapper.SkyWarsStats getStats() {
      return this.stats;
   }

   @Override
   public String toString() {
      SkyWarsStatsWrapper.SkyWarsStats stats2 = this.getStats();
      SkyWarsStatsWrapper.SkyWarsStats soloInsaneStats2 = this.getSoloInsaneStats();
      SkyWarsStatsWrapper.SkyWarsStats soloNormalStats2 = this.getSoloNormalStats();
      SkyWarsStatsWrapper.SkyWarsStats teamNormalStats2 = this.getTeamNormalStats();
      SkyWarsStatsWrapper.SkyWarsStats teamInsaneStats2 = this.getTeamInsaneStats();
      SkyWarsStatsWrapper.SkyWarsStats teamStats2 = this.getTeamStats();
      SkyWarsStatsWrapper.SkyWarsStats soloStats2 = this.getSoloStats();
      SkyWarsStatsWrapper.SkyWarsStats rankedStats2 = this.getRankedStats();
      long winStreak2 = this.getWinStreak();
      String levelFormatted2 = this.getLevelFormatted();
      return "SkyWarsStatsWrapper(stats="
         + stats2
         + ", soloInsaneStats="
         + soloInsaneStats2
         + ", soloNormalStats="
         + soloNormalStats2
         + ", teamNormalStats="
         + teamNormalStats2
         + ", teamInsaneStats="
         + teamInsaneStats2
         + ", teamStats="
         + teamStats2
         + ", soloStats="
         + soloStats2
         + ", rankedStats="
         + rankedStats2
         + ", levelFormatted="
         + levelFormatted2
         + ", winStreak="
         + winStreak2
         + ")";
   }

   public SkyWarsStatsWrapper.SkyWarsStats getSoloStats() {
      return this.soloStats;
   }

   public void setSoloNormalStats(SkyWarsStatsWrapper.SkyWarsStats skyWarsStats) {
      this.soloNormalStats = skyWarsStats;
   }

   public void setTeamNormalStats(SkyWarsStatsWrapper.SkyWarsStats skyWarsStats) {
      this.teamNormalStats = skyWarsStats;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof SkyWarsStatsWrapper skyWarsStatsWrapper)) {
         return false;
      } else {
         if (!skyWarsStatsWrapper.canEqual(this)) {
            return false;
         }

         if (this.getWinStreak() != skyWarsStatsWrapper.getWinStreak()) {
            return false;
         }

         String levelFormatted2 = this.getLevelFormatted();
         String levelFormatted3 = skyWarsStatsWrapper.getLevelFormatted();
         return levelFormatted2 == null ? levelFormatted3 == null : levelFormatted2.equals(levelFormatted3);
      }
   }

   public void setTeamInsaneStats(SkyWarsStatsWrapper.SkyWarsStats skyWarsStats) {
      this.teamInsaneStats = skyWarsStats;
   }

   public String getLevelFormatted() {
      return this.levelFormatted;
   }

   public static final class SkyWarsStats {
      @SerializedName("kills")
      private long kills;
      @GroupName
      private String modeName;
      @SerializedName("deaths")
      private long deaths;
      @SerializedName("wins")
      private long wins;
      @SerializedName("losses")
      private long losses;

      @Override
      public String toString() {
         String modeName2 = this.getModeName();
         long wins2 = this.getWins();
         long kills2 = this.getKills();
         long losses2 = this.getLosses();
         long deaths2 = this.getDeaths();
         long l = kills2;
         long l2 = wins2;
         return "SkyWarsStatsWrapper.SkyWarsStats(modeName=" + modeName2 + ", wins=" + l2 + ", kills=" + l + ", deaths=" + deaths2 + ", losses=" + losses2 + ")";
      }

      public long getKills() {
         return this.kills;
      }

      public long getLosses() {
         return this.losses;
      }

      public void setModeName(String text) {
         this.modeName = text;
      }

      public long getDeaths() {
         return this.deaths;
      }

      public String getModeName() {
         return this.modeName;
      }

      @Override
      public int hashCode() {
         long wins2 = this.getWins();
         int i = 59 + (int)(wins2 >>> 32 ^ wins2);
         long kills2 = this.getKills();
         i = i * 59 + (int)(kills2 >>> 32 ^ kills2);
         long deaths2 = this.getDeaths();
         i = i * 59 + (int)(deaths2 >>> 32 ^ deaths2);
         long losses2 = this.getLosses();
         i = i * 59 + (int)(losses2 >>> 32 ^ losses2);
         String modeName2 = this.getModeName();
         return i * 59 + (modeName2 == null ? 43 : modeName2.hashCode());
      }

      public long getWins() {
         return this.wins;
      }

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof SkyWarsStatsWrapper.SkyWarsStats skyWarsStats)) {
            return false;
         } else {
            if (this.getWins() != skyWarsStats.getWins()) {
               return false;
            }

            if (this.getKills() != skyWarsStats.getKills()) {
               return false;
            }

            if (this.getDeaths() != skyWarsStats.getDeaths()) {
               return false;
            }

            if (this.getLosses() != skyWarsStats.getLosses()) {
               return false;
            }

            String modeName2 = this.getModeName();
            String modeName3 = skyWarsStats.getModeName();
            return modeName2 == null ? modeName3 == null : modeName2.equals(modeName3);
         }
      }
   }
}
