package wtf.novoline.client.discord;

import com.google.gson.annotations.SerializedName;
import wtf.novoline.enums.ClientState;

public class GatewayPresence {
   @SerializedName("serverIp")
   private String serverIp;
   @SerializedName("ign")
   private String ign;
   @SerializedName("type")
   private ClientState type;
   @SerializedName("sid")
   private String sid;

   public String getIgn() {
      return this.ign;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof GatewayPresence gatewayPresence)) {
         return false;
      } else {
         if (!gatewayPresence.canEqual(this)) {
            return false;
         }

         ClientState type2 = this.getType();
         ClientState type3 = gatewayPresence.getType();
         if (type2 == null ? type3 == null : type2.equals(type3)) {
            String sid2 = this.getSid();
            String sid3 = gatewayPresence.getSid();
            if (sid2 == null ? sid3 == null : sid2.equals(sid3)) {
               String ign2 = this.getIgn();
               String ign3 = gatewayPresence.getIgn();
               if (ign2 == null ? ign3 == null : ign2.equals(ign3)) {
                  String serverIp2 = this.getServerIp();
                  String serverIp3 = gatewayPresence.getServerIp();
                  return serverIp2 == null ? serverIp3 == null : serverIp2.equals(serverIp3);
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

   public String getServerIp() {
      return this.serverIp;
   }

   public void setType(ClientState clientState) {
      this.type = clientState;
   }

   public void setServerIp(String text) {
      this.serverIp = text;
   }

   public void setIgn(String text) {
      this.ign = text;
   }

   public void setSid(String text) {
      this.sid = text;
   }

   @Override
   public String toString() {
      return "Presence(type=" + this.getType() + ", sid=" + this.getSid() + ", ign=" + this.getIgn() + ", serverIp=" + this.getServerIp() + ")";
   }

   protected boolean canEqual(Object object) {
      return object instanceof GatewayPresence;
   }

   @Override
   public int hashCode() {
      ClientState type2 = this.getType();
      int i = 59 + (type2 == null ? 43 : type2.hashCode());
      String sid2 = this.getSid();
      i = i * 59 + (sid2 == null ? 43 : sid2.hashCode());
      String ign2 = this.getIgn();
      i = i * 59 + (ign2 == null ? 43 : ign2.hashCode());
      String serverIp2 = this.getServerIp();
      return i * 59 + (serverIp2 == null ? 43 : serverIp2.hashCode());
   }

   public String getSid() {
      return this.sid;
   }

   public ClientState getType() {
      return this.type;
   }
}
