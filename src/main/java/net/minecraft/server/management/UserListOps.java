package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListOps extends UserList<GameProfile, UserListOpsEntry> {
   public GameProfile getGameProfileFromName(String username) {
      for (UserListOpsEntry userListOpsEntry2 : this.getValues().values()) {
         if (username.equalsIgnoreCase(userListOpsEntry2.getValue().getName())) {
            return userListOpsEntry2.getValue();
         }
      }

      return null;
   }

   public UserListOps(File saveFile) {
      super(saveFile);
   }

   @Override
   public String[] getKeys() {
      String[] texts = new String[this.getValues().size()];
      int i = 0;

      for (UserListOpsEntry userListOpsEntry2 : this.getValues().values()) {
         texts[i++] = userListOpsEntry2.getValue().getName();
      }

      return texts;
   }

   protected String getObjectKey(GameProfile obj) {
      return obj.getId().toString();
   }

   public boolean bypassesPlayerLimit(GameProfile profile) {
      UserListOpsEntry userListOpsEntry = this.getEntry(profile);
      return userListOpsEntry != null ? userListOpsEntry.bypassesPlayerLimit() : false;
   }

   @Override
   protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
      return new UserListOpsEntry(entryData);
   }
}
