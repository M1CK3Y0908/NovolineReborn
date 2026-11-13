package net.minecraft.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;

public class ServerStatusResponse {
   private String favicon;
   private IChatComponent serverMotd;
   private ServerStatusResponse.MinecraftProtocolVersionIdentifier protocolVersion;
   private ServerStatusResponse.PlayerCountData playerCount;

   public ServerStatusResponse.MinecraftProtocolVersionIdentifier getProtocolVersionInfo() {
      return this.protocolVersion;
   }

   public void setServerDescription(IChatComponent motd) {
      this.serverMotd = motd;
   }

   public String getFavicon() {
      return this.favicon;
   }

   public ServerStatusResponse.PlayerCountData getPlayerCountData() {
      return this.playerCount;
   }

   public void setPlayerCountData(ServerStatusResponse.PlayerCountData countData) {
      this.playerCount = countData;
   }

   public void setFavicon(String faviconBlob) {
      this.favicon = faviconBlob;
   }

   public void setProtocolVersionInfo(ServerStatusResponse.MinecraftProtocolVersionIdentifier protocolVersionData) {
      this.protocolVersion = protocolVersionData;
   }

   public IChatComponent getServerDescription() {
      return this.serverMotd;
   }

   public static class MinecraftProtocolVersionIdentifier {
      private final String name;
      private final int protocol;

      public String getName() {
         return this.name;
      }

      public int getProtocol() {
         return this.protocol;
      }

      public MinecraftProtocolVersionIdentifier(String nameIn, int protocolIn) {
         this.name = nameIn;
         this.protocol = protocolIn;
      }

      public static class Serializer
         implements JsonDeserializer<ServerStatusResponse.MinecraftProtocolVersionIdentifier>,
         JsonSerializer<ServerStatusResponse.MinecraftProtocolVersionIdentifier> {
         public ServerStatusResponse.MinecraftProtocolVersionIdentifier deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = JsonUtils.getJsonObject(json, "version");
            return new ServerStatusResponse.MinecraftProtocolVersionIdentifier(
               JsonUtils.getString(jsonObject, "name"), JsonUtils.getInt(jsonObject, "protocol")
            );
         }

         public JsonElement serialize(ServerStatusResponse.MinecraftProtocolVersionIdentifier value, Type type, JsonSerializationContext context) {
            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("name", value.getName());
            jsonObject3.addProperty("protocol", value.getProtocol());
            return jsonObject3;
         }
      }
   }

   public static class PlayerCountData {
      private final int maxPlayers;
      private GameProfile[] players;
      private final int onlinePlayerCount;

      public int getOnlinePlayerCount() {
         return this.onlinePlayerCount;
      }

      public void setPlayers(GameProfile[] playersIn) {
         this.players = playersIn;
      }

      public int getMaxPlayers() {
         return this.maxPlayers;
      }

      public PlayerCountData(int maxOnlinePlayers, int onlinePlayers) {
         this.maxPlayers = maxOnlinePlayers;
         this.onlinePlayerCount = onlinePlayers;
      }

      public GameProfile[] getPlayers() {
         return this.players;
      }

      public static class Serializer implements JsonDeserializer<ServerStatusResponse.PlayerCountData>, JsonSerializer<ServerStatusResponse.PlayerCountData> {
         public JsonElement serialize(ServerStatusResponse.PlayerCountData value, Type type, JsonSerializationContext context) {
            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("max", value.getMaxPlayers());
            jsonObject3.addProperty("online", value.getOnlinePlayerCount());
            if (value.getPlayers() != null && value.getPlayers().length > 0) {
               JsonArray jsonArray2 = new JsonArray();

               for (int index = 0; index < value.getPlayers().length; index++) {
                  JsonObject jsonObject4 = new JsonObject();
                  UUID uUID = value.getPlayers()[index].getId();
                  jsonObject4.addProperty("id", uUID == null ? "" : uUID.toString());
                  jsonObject4.addProperty("name", value.getPlayers()[index].getName());
                  jsonArray2.add(jsonObject4);
               }

               jsonObject3.add("sample", jsonArray2);
            }

            return jsonObject3;
         }

         public ServerStatusResponse.PlayerCountData deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = JsonUtils.getJsonObject(json, "players");
            ServerStatusResponse.PlayerCountData playerCountData = new ServerStatusResponse.PlayerCountData(
               JsonUtils.getInt(jsonObject, "max"), JsonUtils.getInt(jsonObject, "online")
            );
            if (JsonUtils.isJsonArray(jsonObject, "sample")) {
               JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, "sample");
               if (jsonArray.size() > 0) {
                  GameProfile[] agameprofile = new GameProfile[jsonArray.size()];

                  for (int index = 0; index < agameprofile.length; index++) {
                     JsonObject jsonObject2 = JsonUtils.getJsonObject(jsonArray.get(index), "player[" + index + "]");
                     String text = JsonUtils.getString(jsonObject2, "id");
                     agameprofile[index] = new GameProfile(UUID.fromString(text), JsonUtils.getString(jsonObject2, "name"));
                  }

                  playerCountData.setPlayers(agameprofile);
               }
            }

            return playerCountData;
         }
      }
   }

   public static class Serializer implements JsonDeserializer<ServerStatusResponse>, JsonSerializer<ServerStatusResponse> {
      public JsonElement serialize(ServerStatusResponse value, Type type, JsonSerializationContext context) {
         JsonObject jsonObject3 = new JsonObject();
         if (value.getServerDescription() != null) {
            jsonObject3.add("description", context.serialize(value.getServerDescription()));
         }

         if (value.getPlayerCountData() != null) {
            jsonObject3.add("players", context.serialize(value.getPlayerCountData()));
         }

         if (value.getProtocolVersionInfo() != null) {
            jsonObject3.add("version", context.serialize(value.getProtocolVersionInfo()));
         }

         if (value.getFavicon() != null) {
            jsonObject3.addProperty("favicon", value.getFavicon());
         }

         return jsonObject3;
      }

      public ServerStatusResponse deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.getJsonObject(json, "status");
         ServerStatusResponse serverStatusResponse = new ServerStatusResponse();
         if (jsonObject.has("description")) {
            serverStatusResponse.setServerDescription(context.deserialize(jsonObject.get("description"), IChatComponent.class));
         }

         if (jsonObject.has("players")) {
            serverStatusResponse.setPlayerCountData(context.deserialize(jsonObject.get("players"), ServerStatusResponse.PlayerCountData.class));
         }

         if (jsonObject.has("version")) {
            serverStatusResponse.setProtocolVersionInfo(context.deserialize(jsonObject.get("version"), ServerStatusResponse.MinecraftProtocolVersionIdentifier.class));
         }

         if (jsonObject.has("favicon")) {
            serverStatusResponse.setFavicon(JsonUtils.getString(jsonObject, "favicon"));
         }

         return serverStatusResponse;
      }
   }
}
