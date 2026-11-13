package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.UUID;

public class UserListBansEntry extends BanEntry<GameProfile> {
   @Override
   protected void onSerialization(JsonObject data) {
      if (this.getValue() != null) {
         data.addProperty("uuid", this.getValue().getId() == null ? "" : this.getValue().getId().toString());
         data.addProperty("name", this.getValue().getName());
         super.onSerialization(data);
      }
   }

   public UserListBansEntry(JsonObject json) {
      super(toGameProfile(json), json);
   }

   public UserListBansEntry(GameProfile profile, Date startDate, String banner, Date endDate, String banReason) {
      super(profile, endDate, banner, endDate, banReason);
   }

   private static GameProfile toGameProfile(JsonObject json) {
      if (json.has("uuid") && json.has("name")) {
         String asString = json.get("uuid").getAsString();

         UUID uuid2;
         try {
            uuid2 = UUID.fromString(asString);
         } catch (Throwable throwable) {
            return null;
         }

         return new GameProfile(uuid2, json.get("name").getAsString());
      } else {
         return null;
      }
   }
}
