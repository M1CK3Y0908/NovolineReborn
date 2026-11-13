package wtf.novoline.utils;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.Minecraft;
import wtf.novoline.Novoline;
import wtf.novoline.client.ApiClient;
import wtf.novoline.client.PatchUsersSelfPresenceRequest;
import wtf.novoline.enums.ClientState;
import wtf.novoline.event.LoadWorldEvent;
import wtf.novoline.event.ServerConnectEvent;
import wtf.novoline.event.ServerDisconnectEvent;

public class PresenceUpdater {
   private final Minecraft minecraft = Minecraft.getMinecraft();
   private final ApiClient apiClient = Novoline.getNovoline().getIrcConnection().getApiClient();
   @EventLink
   private final Listener<ServerConnectEvent> onServerConnect = serverConnectEvent -> {
      try {
         this.apiClient
            .updatePresence(
               PatchUsersSelfPresenceRequest.builder()
                  .presenceType(ClientState.ON_SERVER)
                  .ign(this.minecraft.session.getUsername())
                  .serverIp(serverConnectEvent.connectingHostname)
                  .build()
            );
      } catch (IllegalStateException illegalStateException) {
      }
   };
   @EventLink
   private final Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> {
      if (this.minecraft.isSingleplayer()) {
         try {
            this.apiClient
               .updatePresence(
                  PatchUsersSelfPresenceRequest.builder().presenceType(ClientState.IN_SINGLEPLAYER).ign(this.minecraft.session.getUsername()).build()
               );
         } catch (IllegalStateException illegalStateException) {
         }
      }
   };
   @EventLink
   private final Listener<ServerDisconnectEvent> onServerDisconnect = serverDisconnectEvent -> {
      try {
         this.apiClient.updatePresence(PatchUsersSelfPresenceRequest.builder().presenceType(ClientState.IN_MENU).build());
      } catch (IllegalStateException illegalStateException) {
      }
   };
}
