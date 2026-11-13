package wtf.novoline.client.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Type;

public abstract class GatewayGuildPacket extends GatewayJsonPacket implements GatewayHandler {
   public GatewayGuildPacket() {
      super(5);
   }

   public static class GatewayGuildPacketChild extends GatewayGuildPacket {
      @SerializedName("sid")
      private String sid;

      @Override
      public void handle(GatewayClient gatewayClient) throws Exception {
         gatewayClient.getGatewayUserList()
            .stream()
            .filter(gatewayUser -> gatewayUser.getPresence().stream().anyMatch(gatewayPresence -> gatewayPresence.getSid().equals(this.sid)))
            .findFirst()
            .ifPresent(gatewayUser -> gatewayUser.getPresence().removeIf(gatewayPresence -> gatewayPresence.getSid().equals(this.sid)));
      }
   }

   public static class GatewayGuildPacketChild2 extends GatewayGuildPacket {
      @SerializedName("presence")
      private GatewayPresence presence;
      @SerializedName("userId")
      private long userId;

      @Override
      public void handle(GatewayClient gatewayClient) throws Exception {
         GatewayPresence presenceBySid = gatewayClient.getGatewayUserList().findPresenceBySid(this.presence.getSid());
         if (presenceBySid != null) {
            presenceBySid.setType(this.presence.getType());
            presenceBySid.setIgn(this.presence.getIgn());
            presenceBySid.setServerIp(this.presence.getServerIp());
         } else {
            GatewayUser byId = gatewayClient.getGatewayUserList().findById(this.userId);
            if (byId != null) {
               byId.getPresence().add(this.presence);
            }
         }
      }
   }

   public static class JsonDeserializerImpl implements JsonDeserializer<GatewayGuildPacket> {
      private static final Gson gson = new GsonBuilder().create();

      public GatewayGuildPacket deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject asJsonObject = jsonElement.getAsJsonObject();
         GatewayGuildPacket.UpdatePresenceType updatePresenceType = gson.fromJson(asJsonObject.get("type"), GatewayGuildPacket.UpdatePresenceType.class);
         return gson.fromJson(asJsonObject, updatePresenceType.getClassValue());
      }
   }

   enum UpdatePresenceType {
      UPDATE_PRESENCE(GatewayGuildPacket.GatewayGuildPacketChild2.class),
      DELETE_PRESENCE(GatewayGuildPacket.GatewayGuildPacketChild.class);

      private final Class<? extends GatewayGuildPacket> classValue;

      public Class<? extends GatewayGuildPacket> getClassValue() {
         return this.classValue;
      }

      UpdatePresenceType(Class<? extends GatewayGuildPacket> clazz) {
         this.classValue = clazz;
      }
   }
}
