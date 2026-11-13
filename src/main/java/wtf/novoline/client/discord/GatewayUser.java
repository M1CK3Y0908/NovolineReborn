package wtf.novoline.client.discord;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GatewayUser {
   @SerializedName("username")
   private String username;
   @SerializedName("presence")
   private List<GatewayPresence> presence;
   @SerializedName("rank")
   private String rank;
   @SerializedName("id")
   private long id;

   public String getRank() {
      return this.rank;
   }

   public long getId() {
      return this.id;
   }

   public List<GatewayPresence> getPresence() {
      return this.presence;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof GatewayUser gatewayUser)) {
         return false;
      } else {
         return !gatewayUser.canEqual(this) ? false : this.getId() == gatewayUser.getId();
      }
   }

   @Override
   public String toString() {
      long id2 = this.getId();
      String username2 = this.getUsername();
      List presence2 = this.getPresence();
      String rank2 = this.getRank();
      String text = username2;
      long l = id2;
      return "User(id=" + l + ", username=" + text + ", rank=" + rank2 + ", presence=" + presence2 + ")";
   }

   protected boolean canEqual(Object object) {
      return object instanceof GatewayUser;
   }

   public String getUsername() {
      return this.username;
   }

   @Override
   public int hashCode() {
      long id2 = this.getId();
      return 59 + (int)(id2 >>> 32 ^ id2);
   }
}
