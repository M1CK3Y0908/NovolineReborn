package wtf.novoline.client.discord;

import com.google.gson.annotations.SerializedName;

public class ReadyPacket extends GatewayJsonPacket implements GatewayHandler {
   @SerializedName("selfUser")
   private GatewayUser selfUser;
   @SerializedName("sid")
   private String sid;

   @Override
   public void handle(GatewayClient gatewayClient) {
      gatewayClient.getGatewayUserList().setGatewayUser(this.selfUser);
      gatewayClient.getHeartbeatState().setSid(this.sid);
      gatewayClient.startHeartbeat(gatewayClient.getHeartbeatState().getHeartbeatInterval());
      gatewayClient.getGatewayEventDispatcher().post(ReadyEvent.createReadyEvent(this.selfUser));
      logger.info("Ready, logged in as: " + this.selfUser.getUsername());
   }

   public ReadyPacket() {
      super(3);
   }
}
