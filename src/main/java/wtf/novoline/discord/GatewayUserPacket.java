package wtf.novoline.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import wtf.novoline.client.discord.GatewayClient;
import wtf.novoline.client.discord.GatewayHandler;
import wtf.novoline.client.discord.GatewayJsonPacket;
import wtf.novoline.client.discord.GatewayUser;
import wtf.novoline.client.discord.PersistenceProfileLoadedEvent;
import wtf.novoline.gson.CosmeticDeserializer;
import wtf.novoline.util.Profile;
import wtf.novoline.utils.cosmetic.Cosmetic;

public abstract class GatewayUserPacket extends GatewayJsonPacket implements GatewayHandler {
   public GatewayUserPacket() {
      super(4);
   }

   public static class GatewayUserPacketChild extends GatewayUserPacket {
      @SerializedName("profiles")
      private Collection<Profile> profiles;

      @Override
      public void handle(GatewayClient gatewayClient) throws Exception {
         this.profiles.forEach(profile -> gatewayClient.getGatewayPresenceMap().put(profile.getUserId(), profile));
         gatewayClient.getGatewayEventDispatcher().post(PersistenceProfileLoadedEvent.createPersistenceProfileLoadedEvent(this.profiles));
         logger.fine("Processed " + this.profiles.size() + " persistence profiles");
      }
   }

   public static class GatewayUserPacketChild2 extends GatewayUserPacket {
      @SerializedName("userId")
      private long userId;

      @Override
      public void handle(GatewayClient gatewayClient) {
         GatewayUser byId = gatewayClient.getGatewayUserList().findById(this.userId);
         gatewayClient.getGatewayUserList().removeById(this.userId);
         gatewayClient.getGatewayEventDispatcher().post(UserDisconnectedEvent.createUserDisconnectedEvent(byId));
         logger.fine("User " + this.userId + " has disconnected");
      }
   }

   public static class GatewayUserPacketChild3 extends GatewayUserPacket {
      @SerializedName("user")
      private GatewayUser user;

      @Override
      public void handle(GatewayClient gatewayClient) {
         gatewayClient.getGatewayUserList().add(this.user);
         gatewayClient.getGatewayEventDispatcher().post(UserConnectedEvent.createUserConnectedEvent(this.user));
         logger.fine("User " + this.user.getId() + " has connected");
      }
   }

   public static class GatewayUserPacketChild4 extends GatewayUserPacket {
      @SerializedName("users")
      private List<GatewayUser> users;

      @Override
      public void handle(GatewayClient gatewayClient) {
         gatewayClient.getGatewayUserList().addAll(this.users);
         logger.fine("Registered " + this.users.size() + " users");
      }
   }

   public static class JsonDeserializerImpl implements JsonDeserializer<GatewayUserPacket> {
      private static final Gson gson = new GsonBuilder().registerTypeAdapter(Cosmetic.class, new CosmeticDeserializer()).create();

      public GatewayUserPacket deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject asJsonObject = jsonElement.getAsJsonObject();
         GatewayUserPacket.UserListType userListType = gson.fromJson(asJsonObject.get("type"), GatewayUserPacket.UserListType.class);
         return gson.fromJson(asJsonObject, userListType.getClassValue());
      }
   }

   public enum UserListType {
      USER_LIST(GatewayUserPacket.GatewayUserPacketChild4.class),
      USER_CONNECTED(GatewayUserPacket.GatewayUserPacketChild3.class),
      USER_DISCONNECTED(GatewayUserPacket.GatewayUserPacketChild2.class),
      USER_PROFILE_LIST(GatewayUserPacket.GatewayUserPacketChild.class);

      private final Class<? extends GatewayUserPacket> classValue;

      UserListType(Class<? extends GatewayUserPacket> clazz) {
         this.classValue = clazz;
      }

      public Class<? extends GatewayUserPacket> getClassValue() {
         return this.classValue;
      }
   }
}
