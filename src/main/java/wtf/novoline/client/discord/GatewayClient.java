package wtf.novoline.client.discord;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import wtf.novoline.client.ApiClient;
import wtf.novoline.enums.UserListType;
import wtf.novoline.util.Room;

public class GatewayClient extends WebSocketClient {
   private final ClientProperties clientProperties;
   public static final int PROTOCOL_VERSION = 1;
   private Instant connectStart;
   private HeartbeatState heartbeatState;
   private Set<UserListType> userLists;
   private ApiClient apiClient;
   private final GatewayPacketDispatcher gatewayPacketDispatcher;
   private final GatewayUserList gatewayUserList;
   private static final Logger logger = Logger.getLogger("GatewayClient");
   private final GatewayPresenceMap gatewayPresenceMap;
   private final GatewayEventDispatcher gatewayEventDispatcher;
   private final ScheduledExecutorService scheduledExecutorService;
   private Map<Snowflake, Room> rooms;

   public void setConnectStart(Instant instant) {
      this.connectStart = instant;
   }

   public void setApiClient(ApiClient apiClient2) {
      this.apiClient = apiClient2;
   }

   @Override
   public void onError(Exception exception2) {
      logger.log(Level.SEVERE, "There was an error in WebSocket Connection " + this, exception2);
   }

   GatewayClient(URI uri, Map map, ClientProperties clientProperties2, Set set, String text) {
      super(uri, map);
      this.clientProperties = clientProperties2;
      this.userLists = set;
      this.gatewayPacketDispatcher = GatewayPacketDispatcher.createGatewayPacketDispatcher(this);
      this.gatewayUserList = GatewayUserList.createGatewayUserList(text);
      this.gatewayPresenceMap = GatewayPresenceMap.getGatewayPresenceMap();
      this.gatewayEventDispatcher = GatewayEventDispatcher.getGatewayEventDispatcher();
      this.scheduledExecutorService = Executors.newScheduledThreadPool(2);
   }

   public void setHeartbeatState(HeartbeatState heartbeatState2) {
      this.heartbeatState = heartbeatState2;
   }

   public void setRooms(Map<Snowflake, Room> map) {
      this.rooms = map;
   }

   public ClientProperties getClientProperties() {
      return this.clientProperties;
   }

   public void startHeartbeat(int i) {
      ScheduledFuture scheduledFuture = this.scheduledExecutorService.scheduleAtFixedRate(() -> {
         int heartbeatSequence = this.heartbeatState.getHeartbeatSequence();
         this.sendMessage(HeartbeatPacket.createHeartbeatPacket(heartbeatSequence));
         ScheduledFuture heartbeatAutoDisconnectFuture = this.createHeartbeatAutoDisconnectFuture(i / 2);
         this.heartbeatState.setHeartbeatDisconnectFuture(heartbeatAutoDisconnectFuture);
      }, i, i, TimeUnit.MILLISECONDS);
      this.heartbeatState.setHeartbeatFuture(scheduledFuture);
   }

   public GatewayPacketDispatcher getGatewayPacketDispatcher() {
      return this.gatewayPacketDispatcher;
   }

   public GatewayPresenceMap getGatewayPresenceMap() {
      return this.gatewayPresenceMap;
   }

   public Set<UserListType> getUserLists() {
      if (this.userLists == null) {
         this.userLists = new HashSet<>();
      }

      return this.userLists;
   }

   @Override
   public void connect() {
      this.connectStart = Instant.now();
      super.connect();
   }

   public ScheduledExecutorService getScheduledExecutorService() {
      return this.scheduledExecutorService;
   }

   @Override
   public void send(String text) {
      logger.log(Level.FINER, "-> " + text);
      super.send(text);
   }

   public HeartbeatState getHeartbeatState() {
      return this.heartbeatState;
   }

   public GatewayEventDispatcher getGatewayEventDispatcher() {
      return this.gatewayEventDispatcher;
   }

   @Override
   public void onMessage(String text) {
      logger.finer("<- " + text);

      try {
         this.gatewayPacketDispatcher.dispatch(text);
      } catch (Exception exception2) {
         logger.log(Level.WARNING, "An exception occurred while handling message " + text, exception2);
      }
   }

   public Instant getConnectStart() {
      return this.connectStart;
   }

   public Map<Snowflake, Room> getRooms() {
      if (this.rooms == null) {
         this.rooms = new HashMap<>();
      }

      return this.rooms;
   }

   private ScheduledFuture<?> createHeartbeatAutoDisconnectFuture(int i) {
      return this.scheduledExecutorService.schedule(() -> this.close(4013, "Did not receive Heartbeat ACK"), i, TimeUnit.MILLISECONDS);
   }

   @Override
   public void onClose(int i, String text, boolean flag) {
      logger.warning("Connection closed: " + i + " - " + text + ", closed clearly: " + flag);
      if (this.heartbeatState != null) {
         if (this.heartbeatState.getHeartbeatFuture() != null) {
            this.heartbeatState.getHeartbeatFuture().cancel(true);
         }

         this.setHeartbeatState(null);
      }

      this.gatewayUserList.clear();
      ConnectionClosedEvent connectionClosedEvent = ConnectionClosedEvent.createConnectionClosedEvent(i, text, flag);
      if (i == 4000) {
         connectionClosedEvent.setException(new GatewayClient.ExceptionChild(text));
      } else {
         new GatewayReconnectThread(this).start();
      }

      this.gatewayEventDispatcher.post(connectionClosedEvent);
   }

   @Override
   public void onOpen(ServerHandshake serverHandshake) {
      long l = Duration.between(this.connectStart, Instant.now()).toMillis();
      Logger logger2 = logger;
      URI uri = this.getURI();
      long l2 = l;
      logger2.info("Connected to " + uri + " in " + l2 + "ms");
      this.setHeartbeatState(new HeartbeatState());
   }

   public ApiClient getApiClient() {
      return this.apiClient;
   }

   public void sendMessage(GatewayMessage gatewayMessage) {
      this.send(gatewayMessage.toJson());
   }

   public GatewayUserList getGatewayUserList() {
      return this.gatewayUserList;
   }

   public static class ExceptionChild extends Exception {
      public ExceptionChild(String text) {
         super(text);
      }
   }
}
