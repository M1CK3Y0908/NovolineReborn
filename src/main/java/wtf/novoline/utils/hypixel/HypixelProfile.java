package wtf.novoline.utils.hypixel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.time.Instant;
import java.util.Map;

public class HypixelProfile {
   @SerializedName("username")
   @Expose
   private String username;
   @SerializedName("punishments")
   @Expose
   private Map<String, HypixelProfile.Punishment> punishments;
   @SerializedName("statistics")
   @Expose
   private HypixelProfile.Statistics statistics;
   @SerializedName("hypixel_profile")
   @Expose
   private HypixelPlayer hypixelProfile;
   @SerializedName("valid")
   @Expose
   private boolean valid = true;

   public void setPunishments(Map<String, HypixelProfile.Punishment> map) {
      this.punishments = map;
   }

   public void setStatistics(HypixelProfile.Statistics statistics2) {
      this.statistics = statistics2;
   }

   public HypixelProfile.Statistics getStatistics() {
      return this.statistics;
   }

   @Override
   public String toString() {
      return "Metadata(valid="
         + this.isValid()
         + ", username="
         + this.getUsername()
         + ", hypixelProfile="
         + this.getHypixelProfile()
         + ", statistics="
         + this.getStatistics()
         + ", punishments="
         + this.getPunishments()
         + ")";
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof HypixelProfile hypixelProfile2)) {
         return false;
      } else {
         if (!hypixelProfile2.canEqual(this)) {
            return false;
         }

         if (this.isValid() != hypixelProfile2.isValid()) {
            return false;
         }

         String username2 = this.getUsername();
         String username3 = hypixelProfile2.getUsername();
         if (username2 == null ? username3 == null : username2.equals(username3)) {
            HypixelPlayer hypixelProfile3 = this.getHypixelProfile();
            HypixelPlayer hypixelProfile4 = hypixelProfile2.getHypixelProfile();
            if (hypixelProfile3 == null ? hypixelProfile4 == null : hypixelProfile3.equals(hypixelProfile4)) {
               HypixelProfile.Statistics statistics2 = this.getStatistics();
               HypixelProfile.Statistics statistics3 = hypixelProfile2.getStatistics();
               if (statistics2 == null ? statistics3 == null : statistics2.equals(statistics3)) {
                  Map punishments2 = this.getPunishments();
                  Map punishments3 = hypixelProfile2.getPunishments();
                  return punishments2 == null ? punishments3 == null : punishments2.equals(punishments3);
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

   public String getUsername() {
      return this.username;
   }

   @Override
   public int hashCode() {
      int i = 59 + (this.isValid() ? 79 : 97);
      String username2 = this.getUsername();
      i = i * 59 + (username2 == null ? 43 : username2.hashCode());
      HypixelPlayer hypixelProfile2 = this.getHypixelProfile();
      i = i * 59 + (hypixelProfile2 == null ? 43 : hypixelProfile2.hashCode());
      HypixelProfile.Statistics statistics2 = this.getStatistics();
      i = i * 59 + (statistics2 == null ? 43 : statistics2.hashCode());
      Map punishments2 = this.getPunishments();
      return i * 59 + (punishments2 == null ? 43 : punishments2.hashCode());
   }

   public void setHypixelProfile(HypixelPlayer hypixelPlayer) {
      this.hypixelProfile = hypixelPlayer;
   }

   public boolean isValid() {
      return this.valid;
   }

   public void setUsername(String text) {
      this.username = text;
   }

   public HypixelPlayer getHypixelProfile() {
      return this.hypixelProfile;
   }

   protected boolean canEqual(Object object) {
      return object instanceof HypixelProfile;
   }

   public Map<String, HypixelProfile.Punishment> getPunishments() {
      return this.punishments;
   }

   public void invalidate() {
      this.valid = false;
   }

   public static final class Punishment {
      @SerializedName("reason")
      @Expose
      private String reason;
      @SerializedName("expires_at")
      @Expose
      private Instant expiresAt;

      public void setExpiresAt(Instant instant) {
         this.expiresAt = instant;
      }

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof HypixelProfile.Punishment punishment)) {
            return false;
         } else {
            String reason2 = this.getReason();
            String reason3 = punishment.getReason();
            if (reason2 == null ? reason3 == null : reason2.equals(reason3)) {
               Instant expiresAt2 = this.getExpiresAt();
               Instant expiresAt3 = punishment.getExpiresAt();
               return expiresAt2 == null ? expiresAt3 == null : expiresAt2.equals(expiresAt3);
            } else {
               return false;
            }
         }
      }

      public String getReason() {
         return this.reason;
      }

      @Override
      public int hashCode() {
         String reason2 = this.getReason();
         int i = 59 + (reason2 == null ? 43 : reason2.hashCode());
         Instant expiresAt2 = this.getExpiresAt();
         return i * 59 + (expiresAt2 == null ? 43 : expiresAt2.hashCode());
      }

      public Instant getExpiresAt() {
         return this.expiresAt;
      }

      @Override
      public String toString() {
         return "Metadata.Punishment(reason=" + this.getReason() + ", expiresAt=" + this.getExpiresAt() + ")";
      }

      public Punishment(String text, Instant instant) {
         this.reason = text;
         this.expiresAt = instant;
      }

      public void setReason(String text) {
         this.reason = text;
      }
   }

   public static final class Statistics {
      @SerializedName("kills")
      @Expose
      private long kills;
      @SerializedName("play_time")
      @Expose
      private long playTime;
      @SerializedName("wins")
      @Expose
      private long wins;

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof HypixelProfile.Statistics statistics2)) {
            return false;
         } else if (this.getKills() != statistics2.getKills()) {
            return false;
         } else {
            return this.getWins() != statistics2.getWins() ? false : this.getPlayTime() == statistics2.getPlayTime();
         }
      }

      public void setKills(long l) {
         this.kills = l;
      }

      @Override
      public int hashCode() {
         long kills2 = this.getKills();
         int i = 59 + (int)(kills2 >>> 32 ^ kills2);
         long wins2 = this.getWins();
         i = i * 59 + (int)(wins2 >>> 32 ^ wins2);
         long playTime2 = this.getPlayTime();
         return i * 59 + (int)(playTime2 >>> 32 ^ playTime2);
      }

      public long getWins() {
         return this.wins;
      }

      public void setWins(long l) {
         this.wins = l;
      }

      public void setPlayTime(long l) {
         this.playTime = l;
      }

      public long getPlayTime() {
         return this.playTime;
      }

      public long getKills() {
         return this.kills;
      }

      @Override
      public String toString() {
         long kills2 = this.getKills();
         long playTime2 = this.getPlayTime();
         long wins2 = this.getWins();
         long l = kills2;
         return "Metadata.Statistics(kills=" + l + ", wins=" + wins2 + ", playTime=" + playTime2 + ")";
      }
   }
}
