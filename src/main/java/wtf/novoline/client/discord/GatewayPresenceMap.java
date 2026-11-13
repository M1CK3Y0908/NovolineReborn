package wtf.novoline.client.discord;

import java.util.HashMap;
import wtf.novoline.util.Profile;

public class GatewayPresenceMap extends HashMap<Long, Profile> {
   public static GatewayPresenceMap getGatewayPresenceMap() {
      return new GatewayPresenceMap();
   }

   private GatewayPresenceMap() {
   }

   public Profile getProfile(GatewayUser gatewayUser) {
      return this.get(gatewayUser.getId());
   }
}
