package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;

public class BanList extends UserList<String, IPBanEntry> {
   public boolean isBanned(SocketAddress address) {
      String text = this.addressToString(address);
      return this.hasEntry(text);
   }

   public IPBanEntry getBanEntry(SocketAddress address) {
      String text = this.addressToString(address);
      return this.getEntry(text);
   }

   private String addressToString(SocketAddress address) {
      String text = address.toString();
      if (text.contains("/")) {
         text = text.substring(text.indexOf(47) + 1);
      }

      if (text.contains(":")) {
         text = text.substring(0, text.indexOf(58));
      }

      return text;
   }

   @Override
   protected UserListEntry<String> createEntry(JsonObject entryData) {
      return new IPBanEntry(entryData);
   }

   public BanList(File bansFile) {
      super(bansFile);
   }
}
