package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListBans extends UserList<GameProfile, UserListBansEntry> {
   @Override
   protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
      return new UserListBansEntry(entryData);
   }

   @Override
   public String[] getKeys() {
      String[] texts = new String[this.getValues().size()];
      int i = 0;

      for (UserListBansEntry userListBansEntry : this.getValues().values()) {
         texts[i++] = userListBansEntry.getValue().getName();
      }

      return texts;
   }

   public boolean isBanned(GameProfile profile) {
      return this.hasEntry(profile);
   }

   public GameProfile isUsernameBanned(String username) {
      for (UserListBansEntry userListBansEntry : this.getValues().values()) {
         if (username.equalsIgnoreCase(userListBansEntry.getValue().getName())) {
            return userListBansEntry.getValue();
         }
      }

      return null;
   }

   protected String getObjectKey(GameProfile obj) {
      return obj.getId().toString();
   }

   public UserListBans(File bansFile) {
      super(bansFile);
   }
}
