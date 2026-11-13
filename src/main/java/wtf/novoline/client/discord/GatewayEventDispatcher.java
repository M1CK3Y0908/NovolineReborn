package wtf.novoline.client.discord;

import java.util.ArrayList;

public class GatewayEventDispatcher extends ArrayList<GatewayEventListener> {
   private GatewayEventDispatcher() {
   }

   public static GatewayEventDispatcher getGatewayEventDispatcher() {
      return new GatewayEventDispatcher();
   }

   public void post(GatewayEvent gatewayEvent) {
      this.forEach(gatewayEventListener -> gatewayEventListener.onEvent(gatewayEvent));
   }
}
