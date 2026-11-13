package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListWhitelist extends UserList<GameProfile, UserListWhitelistEntry> {
   public UserListWhitelist(File file) {
      super(file);
   }

   @Override
   public String[] getKeys() {
      String[] texts = new String[this.getValues().size()];
      int i = 0;

      for (UserListWhitelistEntry userListWhitelistEntry : this.getValues().values()) {
         texts[i++] = userListWhitelistEntry.getValue().getName();
      }

      return texts;
   }

   public GameProfile getBannedProfile(String name) {
      for (UserListWhitelistEntry userListWhitelistEntry : this.getValues().values()) {
         if (name.equalsIgnoreCase(userListWhitelistEntry.getValue().getName())) {
            return userListWhitelistEntry.getValue();
         }
      }

      return null;
   }

   @Override
   protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
      return new UserListWhitelistEntry(entryData);
   }

   protected String getObjectKey(GameProfile obj) {
      return obj.getId().toString();
   }
}
