package wtf.novoline.client.discord;

import com.google.gson.annotations.SerializedName;

public class HeartbeatAckPacket extends GatewayJsonPacket implements GatewayHandler {
   @SerializedName("sequence")
   private int sequence;

   public HeartbeatAckPacket() {
      super(11);
   }

   @Override
   public void handle(GatewayClient gatewayClient) {
      HeartbeatState heartbeatState = gatewayClient.getHeartbeatState();
      if (this.sequence != heartbeatState.getHeartbeatSequence()) {
         gatewayClient.close(4012, "Incorrect heartbeat sequence");
      } else {
         heartbeatState.getHeartbeatDisconnectFuture().cancel(true);
         heartbeatState.setHeartbeatSequence(++this.sequence);
         logger.fine("Received heartbeat ACK");
      }
   }
}
