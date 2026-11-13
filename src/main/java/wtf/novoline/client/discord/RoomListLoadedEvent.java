package wtf.novoline.client.discord;

import java.util.Collection;
import wtf.novoline.util.Room;

public class RoomListLoadedEvent implements GatewayEvent {
   private final Collection<Room> collection;

   public static RoomListLoadedEvent createRoomListLoadedEvent(Collection<Room> collection2) {
      return new RoomListLoadedEvent(collection2);
   }

   public Collection<Room> getCollection() {
      return this.collection;
   }

   private RoomListLoadedEvent(Collection<Room> collection2) {
      this.collection = collection2;
   }
}
