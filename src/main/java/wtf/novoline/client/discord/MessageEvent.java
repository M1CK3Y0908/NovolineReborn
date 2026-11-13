package wtf.novoline.client.discord;

import wtf.novoline.util.Room;

public class MessageEvent implements GatewayEvent {
   private final Room room;
   private final GatewayChatMessage gatewayChatMessage;

   public static MessageEvent createMessageEvent(Room room2, GatewayChatMessage gatewayChatMessage2) {
      return new MessageEvent(room2, gatewayChatMessage2);
   }

   private MessageEvent(Room room2, GatewayChatMessage gatewayChatMessage2) {
      this.room = room2;
      this.gatewayChatMessage = gatewayChatMessage2;
   }

   public Room getRoom() {
      return this.room;
   }

   public GatewayChatMessage getGatewayChatMessage() {
      return this.gatewayChatMessage;
   }
}
