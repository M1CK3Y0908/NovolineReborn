package wtf.novoline.client.discord;

import com.google.gson.annotations.SerializedName;

public class HelloPacket extends GatewayJsonPacket implements GatewayHandler {
   @SerializedName("serverId")
   private String serverId;
   @SerializedName("heartbeatInterval")
   private int heartbeatInterval;

   public HelloPacket() {
      super(1);
   }

   @Override
   public void handle(GatewayClient gatewayClient) {
      logger.fine("Received Hello, via " + this.serverId + "; heartbeat interval: " + this.heartbeatInterval);
      gatewayClient.getHeartbeatState().setHeartbeatInterval(this.heartbeatInterval);
      logger.fine("Sending Identify");
      gatewayClient.sendMessage(IdentifyPacket.createIdentifyPacket(gatewayClient.getClientProperties(), gatewayClient.getUserLists()));
   }
}
