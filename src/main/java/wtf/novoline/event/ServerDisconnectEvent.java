package wtf.novoline.event;

import net.minecraft.util.IChatComponent;

public class ServerDisconnectEvent extends Event {
   private final IChatComponent message;

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof ServerDisconnectEvent serverDisconnectEvent)) {
         return false;
      } else {
         if (!serverDisconnectEvent.canEqual(this)) {
            return false;
         }

         IChatComponent message2 = this.getMessage();
         IChatComponent message3 = serverDisconnectEvent.getMessage();
         return message2 == null ? message3 == null : message2.equals(message3);
      }
   }

   public IChatComponent getMessage() {
      return this.message;
   }

   @Override
   public String toString() {
      return "ServerDisconnectEvent(message=" + this.getMessage() + ")";
   }

   public ServerDisconnectEvent(IChatComponent iChatComponent) {
      this.message = iChatComponent;
   }

   @Override
   public int hashCode() {
      IChatComponent message2 = this.getMessage();
      return 59 + (message2 == null ? 43 : message2.hashCode());
   }

   protected boolean canEqual(Object object) {
      return object instanceof ServerDisconnectEvent;
   }
}
