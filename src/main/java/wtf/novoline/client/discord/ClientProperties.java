package wtf.novoline.client.discord;

public class ClientProperties {
   private final int protocolVersion = 1;
   private final String clientVersion;
   private final String clientChannel;

   @Override
   public int hashCode() {
      int i = 59 + this.getProtocolVersion();
      String clientVersion2 = this.getClientVersion();
      i = i * 59 + (clientVersion2 == null ? 43 : clientVersion2.hashCode());
      String clientChannel2 = this.getClientChannel();
      return i * 59 + (clientChannel2 == null ? 43 : clientChannel2.hashCode());
   }

   protected boolean canEqual(Object object) {
      return object instanceof ClientProperties;
   }

   public static ClientProperties.ClientPropertiesBuilder builder() {
      return new ClientProperties.ClientPropertiesBuilder();
   }

   public int getProtocolVersion() {
      return 1;
   }

   @Override
   public String toString() {
      return "ClientProperties(protocolVersion="
         + this.getProtocolVersion()
         + ", clientVersion="
         + this.getClientVersion()
         + ", clientChannel="
         + this.getClientChannel()
         + ")";
   }

   ClientProperties(String text, String text2) {
      this.clientVersion = text;
      this.clientChannel = text2;
   }

   public String getClientVersion() {
      return this.clientVersion;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof ClientProperties clientProperties)) {
         return false;
      } else if (!clientProperties.canEqual(this)) {
         return false;
      } else if (this.getProtocolVersion() != clientProperties.getProtocolVersion()) {
         return false;
      } else {
         String clientVersion2 = this.getClientVersion();
         String clientVersion3 = clientProperties.getClientVersion();
         if (clientVersion2 == null ? clientVersion3 == null : clientVersion2.equals(clientVersion3)) {
            String clientChannel2 = this.getClientChannel();
            String clientChannel3 = clientProperties.getClientChannel();
            return clientChannel2 == null ? clientChannel3 == null : clientChannel2.equals(clientChannel3);
         } else {
            return false;
         }
      }
   }

   public String getClientChannel() {
      return this.clientChannel;
   }

   public static class ClientPropertiesBuilder {
      private String clientVersion;
      private String clientChannel;

      ClientPropertiesBuilder() {
      }

      public ClientProperties.ClientPropertiesBuilder clientChannel(String text) {
         this.clientChannel = text;
         return this;
      }

      @Override
      public String toString() {
         return "ClientProperties.ClientPropertiesBuilder(clientVersion=" + this.clientVersion + ", clientChannel=" + this.clientChannel + ")";
      }

      public ClientProperties build() {
         return new ClientProperties(this.clientVersion, this.clientChannel);
      }

      public ClientProperties.ClientPropertiesBuilder clientVersion(String text) {
         this.clientVersion = text;
         return this;
      }
   }
}
