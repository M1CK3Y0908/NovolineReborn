package wtf.novoline.utils.hypixel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.time.Instant;
import java.util.List;
import wtf.novoline.util.PlayerStats;

public class HypixelPlayer {
   @SerializedName("knownAliases")
   @Expose
   private List<String> knownAliases;
   @SerializedName("networkLevel")
   @Expose
   private double networkLevel;
   @SerializedName("firstLogin")
   @Expose
   private Instant firstLogin;
   @SerializedName("uuid")
   @Expose
   private String uuid;
   @SerializedName("playername")
   @Expose
   private String playerName;
   @SerializedName("rank_for_mod")
   @Expose
   private String rankForMod;
   @SerializedName("mcVersionRp")
   @Expose
   private String mcVersionRp;
   @SerializedName("lastLogin")
   @Expose
   private Instant lastLogin;
   @SerializedName("stats")
   private PlayerStats stats;

   public String getRankForMod() {
      return this.rankForMod;
   }

   public List<String> getKnownAliases() {
      return this.knownAliases;
   }

   public void setRankForMod(String text) {
      this.rankForMod = text;
   }

   public void setLastLogin(Instant instant) {
      this.lastLogin = instant;
   }

   public Instant getFirstLogin() {
      return this.firstLogin;
   }

   public void setKnownAliases(List<String> list) {
      this.knownAliases = list;
   }

   @Override
   public String toString() {
      Instant firstLogin2 = this.getFirstLogin();
      Instant lastLogin2 = this.getLastLogin();
      List knownAliases2 = this.getKnownAliases();
      String playerName2 = this.getPlayerName();
      PlayerStats stats2 = this.getStats();
      double networkLevel2 = this.getNetworkLevel();
      String uuid2 = this.getUuid();
      String rankForMod2 = this.getRankForMod();
      String mcVersionRp2 = this.getMcVersionRp();
      String text = uuid2;
      double d = networkLevel2;
      return "Player(firstLogin="
         + firstLogin2
         + ", lastLogin="
         + lastLogin2
         + ", knownAliases="
         + knownAliases2
         + ", playerName="
         + playerName2
         + ", stats="
         + stats2
         + ", networkLevel="
         + d
         + ", uuid="
         + text
         + ", mcVersionRp="
         + mcVersionRp2
         + ", rankForMod="
         + rankForMod2
         + ")";
   }

   public Instant getLastLogin() {
      return this.lastLogin;
   }

   public String getUuid() {
      return this.uuid;
   }

   public PlayerStats getStats() {
      return this.stats;
   }

   public String getRankText() {
      if (this.rankForMod == null) {
         return null;
      }

      return switch (this.rankForMod) {
         case "NONE" -> "";
         case "VIP" -> "VIP";
         case "VIP_PLUS" -> "VIP+";
         case "MVP" -> "MVP";
         case "MVP_PLUS" -> "MVP+";
         case "MVP_PLUS_PLUS" -> "MVP++";
         case "YOUTUBER" -> "YT";
         case "HELPER" -> "HELPER";
         case "MODERATOR" -> "MOD";
         case "ADMIN" -> "ADMIN";
         default -> this.rankForMod;
      };
   }

   public String getPlayerName() {
      return this.playerName;
   }

   protected boolean canEqual(Object object) {
      return object instanceof HypixelPlayer;
   }

   @Override
   public int hashCode() {
      long l = Double.doubleToLongBits(this.getNetworkLevel());
      int i = 59 + (int)(l >>> 32 ^ l);
      Instant firstLogin2 = this.getFirstLogin();
      i = i * 59 + (firstLogin2 == null ? 43 : firstLogin2.hashCode());
      Instant lastLogin2 = this.getLastLogin();
      i = i * 59 + (lastLogin2 == null ? 43 : lastLogin2.hashCode());
      List knownAliases2 = this.getKnownAliases();
      i = i * 59 + (knownAliases2 == null ? 43 : knownAliases2.hashCode());
      String playerName2 = this.getPlayerName();
      i = i * 59 + (playerName2 == null ? 43 : playerName2.hashCode());
      PlayerStats stats2 = this.getStats();
      i = i * 59 + (stats2 == null ? 43 : stats2.hashCode());
      String uuid2 = this.getUuid();
      i = i * 59 + (uuid2 == null ? 43 : uuid2.hashCode());
      String mcVersionRp2 = this.getMcVersionRp();
      i = i * 59 + (mcVersionRp2 == null ? 43 : mcVersionRp2.hashCode());
      String rankForMod2 = this.getRankForMod();
      return i * 59 + (rankForMod2 == null ? 43 : rankForMod2.hashCode());
   }

   public double getNetworkLevel() {
      return this.networkLevel;
   }

   public void setPlayerName(String text) {
      this.playerName = text;
   }

   public String getMcVersionRp() {
      return this.mcVersionRp;
   }

   public void setMcVersionRp(String text) {
      this.mcVersionRp = text;
   }

   public void setNetworkLevel(double d) {
      this.networkLevel = d;
   }

   public void setFirstLogin(Instant instant) {
      this.firstLogin = instant;
   }

   public void setStats(PlayerStats playerStats) {
      this.stats = playerStats;
   }

   public void setUuid(String text) {
      this.uuid = text;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof HypixelPlayer hypixelPlayer)) {
         return false;
      } else {
         if (!hypixelPlayer.canEqual(this)) {
            return false;
         }

         if (Double.compare(this.getNetworkLevel(), hypixelPlayer.getNetworkLevel()) != 0) {
            return false;
         }

         Instant firstLogin2 = this.getFirstLogin();
         Instant firstLogin3 = hypixelPlayer.getFirstLogin();
         if (firstLogin2 == null ? firstLogin3 == null : firstLogin2.equals(firstLogin3)) {
            Instant lastLogin2 = this.getLastLogin();
            Instant lastLogin3 = hypixelPlayer.getLastLogin();
            if (lastLogin2 == null ? lastLogin3 == null : lastLogin2.equals(lastLogin3)) {
               List knownAliases2 = this.getKnownAliases();
               List knownAliases3 = hypixelPlayer.getKnownAliases();
               if (knownAliases2 == null ? knownAliases3 == null : knownAliases2.equals(knownAliases3)) {
                  String playerName2 = this.getPlayerName();
                  String playerName3 = hypixelPlayer.getPlayerName();
                  if (playerName2 == null ? playerName3 == null : playerName2.equals(playerName3)) {
                     PlayerStats stats2 = this.getStats();
                     PlayerStats stats3 = hypixelPlayer.getStats();
                     if (stats2 == null ? stats3 == null : stats2.equals(stats3)) {
                        String uuid2 = this.getUuid();
                        String uuid3 = hypixelPlayer.getUuid();
                        if (uuid2 == null ? uuid3 == null : uuid2.equals(uuid3)) {
                           String mcVersionRp2 = this.getMcVersionRp();
                           String mcVersionRp3 = hypixelPlayer.getMcVersionRp();
                           if (mcVersionRp2 == null ? mcVersionRp3 == null : mcVersionRp2.equals(mcVersionRp3)) {
                              String rankForMod2 = this.getRankForMod();
                              String rankForMod3 = hypixelPlayer.getRankForMod();
                              return rankForMod2 == null ? rankForMod3 == null : rankForMod2.equals(rankForMod3);
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }
}
