package net.minecraft.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Map;
import java.util.UUID;

public class Session {
   private final String username;
   private final String playerID;
   private final String token;
   private final Session.Type sessionType;

   public GameProfile getProfile() {
      try {
         UUID uUID = UUIDTypeAdapter.fromString(this.getPlayerID());
         return new GameProfile(uUID, this.getUsername());
      } catch (IllegalArgumentException illegalArgumentException) {
         return new GameProfile((UUID)null, this.getUsername());
      }
   }

   public String getSessionID() {
      return "token:" + this.token + ":" + this.playerID;
   }

   public Session(String usernameIn, String playerIDIn, String tokenIn, String sessionTypeIn) {
      this.username = usernameIn;
      this.playerID = playerIDIn;
      this.token = tokenIn;
      this.sessionType = Session.Type.setSessionType(sessionTypeIn);
   }

   public String getToken() {
      return this.token;
   }

   public Session.Type getSessionType() {
      return this.sessionType;
   }

   public String getPlayerID() {
      return this.playerID;
   }

   public String getUsername() {
      return this.username;
   }

   public enum Type {
      LEGACY("legacy"),
      MOJANG("mojang");

      private static final Map<String, Session.Type> SESSION_TYPES = Maps.newHashMap();
      private final String sessionType;

      static {
         for (Session.Type type : values()) {
            SESSION_TYPES.put(type.sessionType, type);
         }
      }

      Type(String text) {
         this.sessionType = text;
      }

      public static Session.Type setSessionType(String sessionTypeIn) {
         return SESSION_TYPES.get(sessionTypeIn.toLowerCase());
      }
   }
}
