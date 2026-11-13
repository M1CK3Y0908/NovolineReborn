package wtf.novoline.client.discord;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GatewayReconnectThread extends Thread {
   private static final Logger logger = Logger.getLogger("ReconnectThread");
   private static final int RECONNECT_DELAY_MS = 5000;
   private final GatewayClient gatewayClient;

   public GatewayReconnectThread(GatewayClient gatewayClient2) {
      super("Gateway-Reconnect-Thread");
      this.gatewayClient = gatewayClient2;
   }

   @Override
   public void run() {
      logger.fine("Reconnecting in 5000ms");

      try {
         Thread.sleep(5000L);
         this.gatewayClient.reconnect();
      } catch (Exception exception) {
         logger.log(Level.SEVERE, "Failed reconnecting", exception);
      }
   }
}
