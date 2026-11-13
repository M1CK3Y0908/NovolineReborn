package wtf.novoline.util;

import com.google.gson.annotations.SerializedName;
import java.util.Map;
import wtf.novoline.client.discord.GatewayChatMessage;
import wtf.novoline.client.discord.Snowflake;

public class Room {
   @SerializedName("displayName")
   private String displayName;
   @SerializedName("id")
   private Snowflake id;
   private Map<Snowflake, GatewayChatMessage> messages;
   @SerializedName("topic")
   private String topic;

   public void setMessages(Map<Snowflake, GatewayChatMessage> map) {
      this.messages = map;
   }

   public void setTopic(String text) {
      this.topic = text;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   @Override
   public String toString() {
      return "Room(id=" + this.getId() + ", displayName=" + this.getDisplayName() + ", topic=" + this.getTopic() + ", messages=" + this.getMessages() + ")";
   }

   protected boolean canEqual(Object object) {
      return object instanceof Room;
   }

   @Override
   public int hashCode() {
      Snowflake id2 = this.getId();
      int i = 59 + (id2 == null ? 43 : id2.hashCode());
      String displayName2 = this.getDisplayName();
      i = i * 59 + (displayName2 == null ? 43 : displayName2.hashCode());
      String topic2 = this.getTopic();
      i = i * 59 + (topic2 == null ? 43 : topic2.hashCode());
      Map messages2 = this.getMessages();
      return i * 59 + (messages2 == null ? 43 : messages2.hashCode());
   }

   public String getTopic() {
      return this.topic;
   }

   public Map<Snowflake, GatewayChatMessage> getMessages() {
      return this.messages;
   }

   public void setId(Snowflake snowflake) {
      this.id = snowflake;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof Room room)) {
         return false;
      } else {
         if (!room.canEqual(this)) {
            return false;
         }

         Snowflake id2 = this.getId();
         Snowflake id3 = room.getId();
         if (id2 == null ? id3 == null : id2.equals(id3)) {
            String displayName2 = this.getDisplayName();
            String displayName3 = room.getDisplayName();
            if (displayName2 == null ? displayName3 == null : displayName2.equals(displayName3)) {
               String topic2 = this.getTopic();
               String topic3 = room.getTopic();
               if (topic2 == null ? topic3 == null : topic2.equals(topic3)) {
                  Map messages2 = this.getMessages();
                  Map messages3 = room.getMessages();
                  return messages2 == null ? messages3 == null : messages2.equals(messages3);
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

   public void setDisplayName(String text) {
      this.displayName = text;
   }

   public Snowflake getId() {
      return this.id;
   }
}
