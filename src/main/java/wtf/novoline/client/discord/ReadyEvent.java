package wtf.novoline.client.discord;

public class ReadyEvent implements GatewayEvent {
   private final GatewayUser gatewayUser;

   private ReadyEvent(GatewayUser gatewayUser2) {
      this.gatewayUser = gatewayUser2;
   }

   public static ReadyEvent createReadyEvent(GatewayUser gatewayUser2) {
      return new ReadyEvent(gatewayUser2);
   }

   public GatewayUser getGatewayUser() {
      return this.gatewayUser;
   }
}
