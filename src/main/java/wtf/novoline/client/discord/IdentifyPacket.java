package wtf.novoline.client.discord;

import com.google.gson.annotations.SerializedName;
import java.util.Set;
import wtf.novoline.enums.UserListType;

public class IdentifyPacket extends GatewayJsonPacket {
   @SerializedName("protocolVersion")
   private final int protocolVersion;
   @SerializedName("clientVersion")
   private final String clientVersion;
   @SerializedName("clientChannel")
   private final String clientChannel;
   @SerializedName("intents")
   private final Set<UserListType> intents;

   public static IdentifyPacket.IdentifyMessageBuilder builder() {
      return new IdentifyPacket.IdentifyMessageBuilder();
   }

   public static IdentifyPacket createIdentifyPacket(ClientProperties clientProperties, Set<UserListType> set) {
      return builder()
         .protocolVersion(clientProperties.getProtocolVersion())
         .clientVersion(clientProperties.getClientVersion())
         .clientChannel(clientProperties.getClientChannel())
         .intents(set)
         .build();
   }

   public IdentifyPacket(int i, String text, String text2, Set<UserListType> set) {
      super(2);
      this.protocolVersion = i;
      this.clientVersion = text;
      this.clientChannel = text2;
      this.intents = set;
   }

   public static class IdentifyMessageBuilder {
      private int protocolVersion;
      private Set<UserListType> intents;
      private String clientVersion;
      private String clientChannel;

      public IdentifyPacket.IdentifyMessageBuilder protocolVersion(int i) {
         this.protocolVersion = i;
         return this;
      }

      public IdentifyPacket.IdentifyMessageBuilder clientVersion(String text) {
         this.clientVersion = text;
         return this;
      }

      IdentifyMessageBuilder() {
      }

      @Override
      public String toString() {
         return "IdentifyMessage.IdentifyMessageBuilder(protocolVersion="
            + this.protocolVersion
            + ", clientVersion="
            + this.clientVersion
            + ", clientChannel="
            + this.clientChannel
            + ", intents="
            + this.intents
            + ")";
      }

      public IdentifyPacket.IdentifyMessageBuilder intents(Set<UserListType> set) {
         this.intents = set;
         return this;
      }

      public IdentifyPacket build() {
         return new IdentifyPacket(this.protocolVersion, this.clientVersion, this.clientChannel, this.intents);
      }

      public IdentifyPacket.IdentifyMessageBuilder clientChannel(String text) {
         this.clientChannel = text;
         return this;
      }
   }
}
