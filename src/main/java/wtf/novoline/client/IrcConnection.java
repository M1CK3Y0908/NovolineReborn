package wtf.novoline.client;

import wtf.novoline.client.discord.GatewayClient;

public class IrcConnection {
   private final GatewayClient gatewayClient;
   private final ApiClient apiClient;

   public GatewayClient getGatewayClient() {
      return this.gatewayClient;
   }

   public ApiClient getApiClient() {
      return this.apiClient;
   }

   public IrcConnection(GatewayClient gatewayClient2, ApiClient apiClient2) {
      this.gatewayClient = gatewayClient2;
      this.apiClient = apiClient2;
      gatewayClient2.setApiClient(apiClient2);
      apiClient2.setGatewayClient(gatewayClient2);
   }
}
