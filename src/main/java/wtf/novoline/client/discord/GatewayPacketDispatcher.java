package wtf.novoline.client.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashMap;
import java.util.Map;
import wtf.novoline.discord.GatewayRoomPacket;
import wtf.novoline.discord.GatewayUserPacket;
import wtf.novoline.discord.GatewayUserPacket.JsonDeserializerImpl;
import wtf.novoline.gson.CosmeticDeserializer;
import wtf.novoline.gson.LongValueTypeAdapter;
import wtf.novoline.utils.cosmetic.Cosmetic;

public class GatewayPacketDispatcher {
   private final Gson gson = new GsonBuilder()
      .registerTypeAdapter(GatewayUserPacket.class, new JsonDeserializerImpl())
      .registerTypeAdapter(GatewayGuildPacket.class, new wtf.novoline.client.discord.GatewayGuildPacket.JsonDeserializerImpl())
      .registerTypeAdapter(GatewayRoomPacket.class, new wtf.novoline.discord.GatewayRoomPacket.JsonDeserializerImpl())
      .registerTypeAdapter(Snowflake.class, new LongValueTypeAdapter())
      .registerTypeAdapter(Cosmetic.class, new CosmeticDeserializer())
      .create();
   private final Map<Integer, Class<? extends GatewayHandler>> handlersByOpcode = new HashMap<>();
   private final GatewayClient gatewayClient;

   public static GatewayPacketDispatcher createGatewayPacketDispatcher(GatewayClient gatewayClient2) {
      return new GatewayPacketDispatcher(gatewayClient2);
   }

   private GatewayPacketDispatcher(GatewayClient gatewayClient2) {
      this.gatewayClient = gatewayClient2;
      this.handlersByOpcode.put(1, HelloPacket.class);
      this.handlersByOpcode.put(3, ReadyPacket.class);
      this.handlersByOpcode.put(4, GatewayUserPacket.class);
      this.handlersByOpcode.put(5, GatewayGuildPacket.class);
      this.handlersByOpcode.put(6, GatewayRoomPacket.class);
      this.handlersByOpcode.put(11, HeartbeatAckPacket.class);
   }

   public void dispatch(String text) throws Exception {
      JsonObject asJsonObject = JsonParser.parseString(text).getAsJsonObject();
      int asInt = asJsonObject.get("op").getAsInt();
      if (this.handlersByOpcode.containsKey(asInt)) {
         this.gson.fromJson(asJsonObject, this.handlersByOpcode.get(asInt)).handle(this.gatewayClient);
      }
   }
}
