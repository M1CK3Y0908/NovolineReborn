package wtf.novoline.client.discord;

import com.google.gson.annotations.SerializedName;

public class GatewayChatMessage {
   @SerializedName("nameSuffix")
   private String nameSuffix;
   @SerializedName("content")
   private String content;
   @SerializedName("id")
   private Snowflake id;
   @SerializedName("senderId")
   private long senderId;
   @SerializedName("namePrefix")
   private String namePrefix;
   @SerializedName("senderUsername")
   private String senderUsername;

   public String getContent() {
      return this.content;
   }

   public void setNamePrefix(String text) {
      this.namePrefix = text;
   }

   public void setSenderUsername(String text) {
      this.senderUsername = text;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof GatewayChatMessage gatewayChatMessage)) {
         return false;
      } else {
         if (!gatewayChatMessage.canEqual(this)) {
            return false;
         }

         if (this.getSenderId() != gatewayChatMessage.getSenderId()) {
            return false;
         }

         Snowflake id2 = this.getId();
         Snowflake id3 = gatewayChatMessage.getId();
         if (id2 == null ? id3 == null : id2.equals(id3)) {
            String senderUsername2 = this.getSenderUsername();
            String senderUsername3 = gatewayChatMessage.getSenderUsername();
            if (senderUsername2 == null ? senderUsername3 == null : senderUsername2.equals(senderUsername3)) {
               String namePrefix2 = this.getNamePrefix();
               String namePrefix3 = gatewayChatMessage.getNamePrefix();
               if (namePrefix2 == null ? namePrefix3 == null : namePrefix2.equals(namePrefix3)) {
                  String nameSuffix2 = this.getNameSuffix();
                  String nameSuffix3 = gatewayChatMessage.getNameSuffix();
                  if (nameSuffix2 == null ? nameSuffix3 == null : nameSuffix2.equals(nameSuffix3)) {
                     String content2 = this.getContent();
                     String content3 = gatewayChatMessage.getContent();
                     return content2 == null ? content3 == null : content2.equals(content3);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public void setContent(String text) {
      this.content = text;
   }

   public void setNameSuffix(String text) {
      this.nameSuffix = text;
   }

   public String getNameSuffix() {
      return this.nameSuffix;
   }

   public Snowflake getId() {
      return this.id;
   }

   protected boolean canEqual(Object object) {
      return object instanceof GatewayChatMessage;
   }

   public String getSenderUsername() {
      return this.senderUsername;
   }

   @Override
   public int hashCode() {
      long senderId2 = this.getSenderId();
      int i = 59 + (int)(senderId2 >>> 32 ^ senderId2);
      Snowflake id2 = this.getId();
      i = i * 59 + (id2 == null ? 43 : id2.hashCode());
      String senderUsername2 = this.getSenderUsername();
      i = i * 59 + (senderUsername2 == null ? 43 : senderUsername2.hashCode());
      String namePrefix2 = this.getNamePrefix();
      i = i * 59 + (namePrefix2 == null ? 43 : namePrefix2.hashCode());
      String nameSuffix2 = this.getNameSuffix();
      i = i * 59 + (nameSuffix2 == null ? 43 : nameSuffix2.hashCode());
      String content2 = this.getContent();
      return i * 59 + (content2 == null ? 43 : content2.hashCode());
   }

   public String getNamePrefix() {
      return this.namePrefix;
   }

   public void setId(Snowflake snowflake) {
      this.id = snowflake;
   }

   public long getSenderId() {
      return this.senderId;
   }

   @Override
   public String toString() {
      Snowflake id2 = this.getId();
      long senderId2 = this.getSenderId();
      String senderUsername2 = this.getSenderUsername();
      String namePrefix2 = this.getNamePrefix();
      String content2 = this.getContent();
      String nameSuffix2 = this.getNameSuffix();
      String text = namePrefix2;
      String text2 = senderUsername2;
      long l = senderId2;
      return "Message(id="
         + id2
         + ", senderId="
         + l
         + ", senderUsername="
         + text2
         + ", namePrefix="
         + text
         + ", nameSuffix="
         + nameSuffix2
         + ", content="
         + content2
         + ")";
   }
}
