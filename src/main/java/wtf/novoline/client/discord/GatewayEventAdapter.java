package wtf.novoline.client.discord;

import wtf.novoline.discord.UserConnectedEvent;
import wtf.novoline.discord.UserDisconnectedEvent;

public class GatewayEventAdapter implements GatewayEventListener {
   public void onReady(ReadyEvent readyEvent) {
   }

   @Override
   public void onEvent(GatewayEvent gatewayEvent) {
      if (gatewayEvent instanceof MessageEvent messageEvent) {
         this.onMessage(messageEvent);
      } else if (gatewayEvent instanceof RoomListLoadedEvent roomListLoadedEvent) {
         this.onRoomListLoaded(roomListLoadedEvent);
      } else if (gatewayEvent instanceof ReadyEvent readyEvent) {
         this.onReady(readyEvent);
      } else if (gatewayEvent instanceof ConnectionClosedEvent connectionClosedEvent) {
         this.onConnectionClosed(connectionClosedEvent);
      } else if (gatewayEvent instanceof UserConnectedEvent userConnectedEvent) {
         this.onUserConnected(userConnectedEvent);
      } else if (gatewayEvent instanceof UserDisconnectedEvent userDisconnectedEvent) {
         this.onUserDisconnected(userDisconnectedEvent);
      } else if (gatewayEvent instanceof PersistenceProfileLoadedEvent persistenceProfileLoadedEvent) {
         this.onPersistenceProfileLoaded(persistenceProfileLoadedEvent);
      }
   }

   public void onUserDisconnected(UserDisconnectedEvent userDisconnectedEvent) {
   }

   public void onUserConnected(UserConnectedEvent userConnectedEvent) {
   }

   public void onConnectionClosed(ConnectionClosedEvent connectionClosedEvent) {
   }

   public void onMessage(MessageEvent messageEvent) {
   }

   public void onRoomListLoaded(RoomListLoadedEvent roomListLoadedEvent) {
   }

   public void onPersistenceProfileLoaded(PersistenceProfileLoadedEvent persistenceProfileLoadedEvent) {
   }
}
