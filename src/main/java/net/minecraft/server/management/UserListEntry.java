package net.minecraft.server.management;

import com.google.gson.JsonObject;

public class UserListEntry<T> {
   private final T value;

   boolean hasBanExpired() {
      return false;
   }

   T getValue() {
      return this.value;
   }

   protected void onSerialization(JsonObject data) {
   }

   protected UserListEntry(T valueIn, JsonObject json) {
      this.value = valueIn;
   }

   public UserListEntry(T valueIn) {
      this.value = valueIn;
   }
}
