package wtf.novoline.client.discord;

import java.util.concurrent.ScheduledFuture;

public class HeartbeatState {
   private ScheduledFuture<?> heartbeatDisconnectFuture;
   private int heartbeatInterval;
   private int heartbeatSequence;
   private String sid;
   private ScheduledFuture<?> heartbeatFuture;

   public String getSid() {
      return this.sid;
   }

   public ScheduledFuture<?> getHeartbeatFuture() {
      return this.heartbeatFuture;
   }

   public int getHeartbeatSequence() {
      return this.heartbeatSequence;
   }

   public void setHeartbeatFuture(ScheduledFuture<?> scheduledFuture) {
      this.heartbeatFuture = scheduledFuture;
   }

   protected boolean canEqual(Object object) {
      return object instanceof HeartbeatState;
   }

   public void setSid(String text) {
      this.sid = text;
   }

   public void setHeartbeatSequence(int i) {
      this.heartbeatSequence = i;
   }

   public void setHeartbeatInterval(int i) {
      this.heartbeatInterval = i;
   }

   public void setHeartbeatDisconnectFuture(ScheduledFuture<?> scheduledFuture) {
      this.heartbeatDisconnectFuture = scheduledFuture;
   }

   @Override
   public String toString() {
      return "GatewaySession(sid="
         + this.getSid()
         + ", heartbeatInterval="
         + this.getHeartbeatInterval()
         + ", heartbeatSequence="
         + this.getHeartbeatSequence()
         + ", heartbeatDisconnectFuture="
         + this.getHeartbeatDisconnectFuture()
         + ", heartbeatFuture="
         + this.getHeartbeatFuture()
         + ")";
   }

   public ScheduledFuture<?> getHeartbeatDisconnectFuture() {
      return this.heartbeatDisconnectFuture;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof HeartbeatState heartbeatState)) {
         return false;
      } else {
         if (!heartbeatState.canEqual(this)) {
            return false;
         }

         if (this.getHeartbeatInterval() != heartbeatState.getHeartbeatInterval()) {
            return false;
         }

         if (this.getHeartbeatSequence() != heartbeatState.getHeartbeatSequence()) {
            return false;
         }

         String sid2 = this.getSid();
         String sid3 = heartbeatState.getSid();
         if (sid2 == null ? sid3 == null : sid2.equals(sid3)) {
            ScheduledFuture heartbeatDisconnectFuture2 = this.getHeartbeatDisconnectFuture();
            ScheduledFuture heartbeatDisconnectFuture3 = heartbeatState.getHeartbeatDisconnectFuture();
            if (heartbeatDisconnectFuture2 == null ? heartbeatDisconnectFuture3 == null : heartbeatDisconnectFuture2.equals(heartbeatDisconnectFuture3)) {
               ScheduledFuture heartbeatFuture2 = this.getHeartbeatFuture();
               ScheduledFuture heartbeatFuture3 = heartbeatState.getHeartbeatFuture();
               return heartbeatFuture2 == null ? heartbeatFuture3 == null : heartbeatFuture2.equals(heartbeatFuture3);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      int i = 59 + this.getHeartbeatInterval();
      i = i * 59 + this.getHeartbeatSequence();
      String sid2 = this.getSid();
      i = i * 59 + (sid2 == null ? 43 : sid2.hashCode());
      ScheduledFuture heartbeatDisconnectFuture2 = this.getHeartbeatDisconnectFuture();
      i = i * 59 + (heartbeatDisconnectFuture2 == null ? 43 : heartbeatDisconnectFuture2.hashCode());
      ScheduledFuture heartbeatFuture2 = this.getHeartbeatFuture();
      return i * 59 + (heartbeatFuture2 == null ? 43 : heartbeatFuture2.hashCode());
   }

   public int getHeartbeatInterval() {
      return this.heartbeatInterval;
   }
}
