package wtf.novoline.discord;

import wtf.novoline.client.discord.GatewayEvent;
import wtf.novoline.client.discord.GatewayUser;

public class UserDisconnectedEvent implements GatewayEvent {
   private final GatewayUser gatewayUser;

   private UserDisconnectedEvent(GatewayUser gatewayUser2) {
      this.gatewayUser = gatewayUser2;
   }

   public GatewayUser getGatewayUser() {
      return this.gatewayUser;
   }

   public static UserDisconnectedEvent createUserDisconnectedEvent(GatewayUser gatewayUser2) {
      return new UserDisconnectedEvent(gatewayUser2);
   }
}
