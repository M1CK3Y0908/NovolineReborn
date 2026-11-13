package wtf.novoline.client.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class GatewayJsonPacket extends GatewayPacket {
   private static final Gson gson = new GsonBuilder().create();

   @Override
   public String toJson() {
      return gson.toJson(this);
   }

   public GatewayJsonPacket(int i) {
      super(i);
   }
}
