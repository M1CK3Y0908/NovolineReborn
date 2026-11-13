package wtf.novoline.client.discord;

import com.google.gson.annotations.SerializedName;

public class HeartbeatPacket extends GatewayJsonPacket {
   @SerializedName("sequence")
   private final int sequence;

   private HeartbeatPacket(int i) {
      super(10);
      this.sequence = i;
   }

   public static HeartbeatPacket createHeartbeatPacket(int i) {
      return new HeartbeatPacket(i);
   }
}
