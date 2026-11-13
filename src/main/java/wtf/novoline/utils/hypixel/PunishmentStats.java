package wtf.novoline.utils.hypixel;

import com.google.gson.annotations.SerializedName;

public class PunishmentStats {
   @SerializedName("watchdog_total")
   private int watchdogTotal;
   @SerializedName("watchdog_rollingDaily")
   private int watchdogRollingDaily;
   @SerializedName("staff_total")
   private int staffTotal;
   @SerializedName("staff_rollingDaily")
   private int staffRollingDaily;
   @SerializedName("watchdog_lastMinute")
   private int watchdogLastMinute;

   public int getWatchdogLastMinute() {
      return this.watchdogLastMinute;
   }

   protected boolean canEqual(Object object) {
      return object instanceof PunishmentStats;
   }

   @Override
   public String toString() {
      return "PunishmentStats(watchdogLastMinute="
         + this.getWatchdogLastMinute()
         + ", staffRollingDaily="
         + this.getStaffRollingDaily()
         + ", watchdogTotal="
         + this.getWatchdogTotal()
         + ", watchdogRollingDaily="
         + this.getWatchdogRollingDaily()
         + ", staffTotal="
         + this.getStaffTotal()
         + ")";
   }

   public int getStaffRollingDaily() {
      return this.staffRollingDaily;
   }

   public int getWatchdogTotal() {
      return this.watchdogTotal;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof PunishmentStats punishmentStats)) {
         return false;
      } else if (!punishmentStats.canEqual(this)) {
         return false;
      } else if (this.getWatchdogLastMinute() != punishmentStats.getWatchdogLastMinute()) {
         return false;
      } else if (this.getStaffRollingDaily() != punishmentStats.getStaffRollingDaily()) {
         return false;
      } else if (this.getWatchdogTotal() != punishmentStats.getWatchdogTotal()) {
         return false;
      } else {
         return this.getWatchdogRollingDaily() != punishmentStats.getWatchdogRollingDaily() ? false : this.getStaffTotal() == punishmentStats.getStaffTotal();
      }
   }

   @Override
   public int hashCode() {
      return ((((59 + this.getWatchdogLastMinute()) * 59 + this.getStaffRollingDaily()) * 59 + this.getWatchdogTotal()) * 59 + this.getWatchdogRollingDaily())
            * 59
         + this.getStaffTotal();
   }

   public int getWatchdogRollingDaily() {
      return this.watchdogRollingDaily;
   }

   public int getStaffTotal() {
      return this.staffTotal;
   }
}
