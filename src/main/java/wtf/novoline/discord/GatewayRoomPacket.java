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
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import wtf.novoline.client.discord.GatewayChatMessage;
import wtf.novoline.client.discord.GatewayClient;
import wtf.novoline.client.discord.GatewayHandler;
import wtf.novoline.client.discord.GatewayJsonPacket;
import wtf.novoline.client.discord.MessageEvent;
import wtf.novoline.client.discord.RoomListLoadedEvent;
import wtf.novoline.client.discord.Snowflake;
import wtf.novoline.gson.LongValueTypeAdapter;
import wtf.novoline.util.Room;

public abstract class GatewayRoomPacket extends GatewayJsonPacket implements GatewayHandler {
   public GatewayRoomPacket() {
      super(6);
   }

   public abstract static class GatewayRoomPacketChild extends GatewayRoomPacket {
      @SerializedName("roomId")
      protected Snowflake roomId;
      @SerializedName("messageId")
      protected Snowflake messageId;
      @SerializedName("message")
      protected GatewayChatMessage message;
   }

   public static class GatewayRoomPacketChild2 extends GatewayRoomPacket {
      @SerializedName("rooms")
      private Collection<Room> rooms;

      @Override
      public void handle(GatewayClient gatewayClient) throws Exception {
         logger.fine("Received " + this.rooms.size() + " active Rooms");
         gatewayClient.setRooms(this.rooms.stream().collect(Collectors.toMap(Room::getId, Function.identity())));
         gatewayClient.getGatewayEventDispatcher().post(RoomListLoadedEvent.createRoomListLoadedEvent(this.rooms));
      }
   }

   public static class GatewayRoomPacketChildChild extends GatewayRoomPacket.GatewayRoomPacketChild {
      @Override
      public void handle(GatewayClient gatewayClient) throws Exception {
         logger.fine("Received Message " + this.message + " from Room " + this.roomId);
         Room room = gatewayClient.getRooms().get(this.roomId);
         if (room != null) {
            if (room.getMessages() == null) {
               room.setMessages(new LinkedHashMap<>());
            }

            room.getMessages().put(this.message.getId(), this.message);
            gatewayClient.getGatewayEventDispatcher().post(MessageEvent.createMessageEvent(room, this.message));
         }
      }
   }

   public static class JsonDeserializerImpl implements JsonDeserializer<GatewayRoomPacket> {
      private static final Gson gson = new GsonBuilder().registerTypeAdapter(Snowflake.class, new LongValueTypeAdapter()).create();

      public GatewayRoomPacket deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject asJsonObject = jsonElement.getAsJsonObject();
         GatewayRoomPacket.RoomListType roomListType = gson.fromJson(asJsonObject.get("type"), GatewayRoomPacket.RoomListType.class);
         return gson.fromJson(asJsonObject, roomListType.getClassValue());
      }
   }

   public enum RoomListType {
      ROOM_LIST(GatewayRoomPacket.GatewayRoomPacketChild2.class),
      ROOM_MESSAGE_RECEIVED(GatewayRoomPacket.GatewayRoomPacketChildChild.class);

      private final Class<? extends GatewayRoomPacket> classValue;

      RoomListType(Class<? extends GatewayRoomPacket> clazz) {
         this.classValue = clazz;
      }

      public Class<? extends GatewayRoomPacket> getClassValue() {
         return this.classValue;
      }
   }
}
