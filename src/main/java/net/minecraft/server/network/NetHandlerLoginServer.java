package net.minecraft.server.network;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.SecretKey;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.login.server.S03PacketEnableCompression;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginServer implements INetHandlerLoginServer, ITickable {
   private SecretKey secretKey;
   private static final AtomicInteger AUTHENTICATOR_THREAD_ID = new AtomicInteger(0);
   private static final Logger logger = LogManager.getLogger();
   private EntityPlayerMP player;
   private final byte[] verifyToken = new byte[4];
   private GameProfile loginGameProfile;
   private static final Random RANDOM = new Random();
   private NetHandlerLoginServer.LoginState currentLoginState = NetHandlerLoginServer.LoginState.HELLO;
   private String serverId = "";
   private final MinecraftServer server;
   public final NetworkManager networkManager;
   private int connectionTimer;

   public String getConnectionInfo() {
      return this.loginGameProfile != null
         ? this.loginGameProfile.toString() + " (" + this.networkManager.getRemoteAddress().toString() + ")"
         : String.valueOf(this.networkManager.getRemoteAddress());
   }

   public NetHandlerLoginServer(MinecraftServer serverIn, NetworkManager networkManagerIn) {
      this.server = serverIn;
      this.networkManager = networkManagerIn;
      RANDOM.nextBytes(this.verifyToken);
   }

   public void closeConnection(String reason) {
      try {
         logger.info("Disconnecting " + this.getConnectionInfo() + ": " + reason);
         ChatComponentText chatComponentText = new ChatComponentText(reason);
         this.networkManager.sendPacket(new S00PacketDisconnect(chatComponentText));
         this.networkManager.closeChannel(chatComponentText);
      } catch (Exception exception) {
         logger.error("Error whilst disconnecting player", exception);
      }
   }

   @Override
   public void processEncryptionResponse(C01PacketEncryptionResponse packetIn) {
      Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.KEY, "Unexpected key packet");
      PrivateKey privateKey = this.server.getKeyPair().getPrivate();
      if (!Arrays.equals(this.verifyToken, packetIn.getVerifyToken(privateKey))) {
         throw new IllegalStateException("Invalid nonce!");
      }

      this.secretKey = packetIn.getSecretKey(privateKey);
      this.currentLoginState = NetHandlerLoginServer.LoginState.AUTHENTICATING;
      this.networkManager.enableEncryption(this.secretKey);
      (new Thread("User Authenticator #" + AUTHENTICATOR_THREAD_ID.incrementAndGet()) {
            @Override
            public void run() {
               GameProfile loginGameProfile2 = NetHandlerLoginServer.this.loginGameProfile;

               try {
                  String text = new BigInteger(
                        CryptManager.getServerIdHash(
                           NetHandlerLoginServer.this.serverId,
                           NetHandlerLoginServer.this.server.getKeyPair().getPublic(),
                           NetHandlerLoginServer.this.secretKey
                        )
                     )
                     .toString(16);
                  NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.server
                     .getMinecraftSessionService()
                     .hasJoinedServer(new GameProfile((UUID)null, loginGameProfile2.getName()), text);
                  if (NetHandlerLoginServer.this.loginGameProfile != null) {
                     NetHandlerLoginServer.logger
                        .info(
                           "UUID of player "
                              + NetHandlerLoginServer.this.loginGameProfile.getName()
                              + " is "
                              + NetHandlerLoginServer.this.loginGameProfile.getId()
                        );
                     NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                  } else if (NetHandlerLoginServer.this.server.isSinglePlayer()) {
                     NetHandlerLoginServer.logger.warn("Failed to verify username but will let them in anyway!");
                     NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.getOfflineProfile(loginGameProfile2);
                     NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                  } else {
                     NetHandlerLoginServer.this.closeConnection("Failed to verify username!");
                     NetHandlerLoginServer.logger
                        .error("Username '" + NetHandlerLoginServer.this.loginGameProfile.getName() + "' tried to join with an invalid session");
                  }
               } catch (AuthenticationUnavailableException authenticationUnavailableException) {
                  if (NetHandlerLoginServer.this.server.isSinglePlayer()) {
                     NetHandlerLoginServer.logger.warn("Authentication servers are down but will let them in anyway!");
                     NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.getOfflineProfile(loginGameProfile2);
                     NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                  } else {
                     NetHandlerLoginServer.this.closeConnection("Authentication servers are down. Please try again later, sorry!");
                     NetHandlerLoginServer.logger.error("Couldn't verify username because servers are unavailable");
                  }
               }
            }
         })
         .start();
   }

   public void tryAcceptPlayer() {
      if (!this.loginGameProfile.isComplete()) {
         this.loginGameProfile = this.getOfflineProfile(this.loginGameProfile);
      }

      String text = this.server.getConfigurationManager().allowUserToConnect(this.networkManager.getRemoteAddress(), this.loginGameProfile);
      if (text != null) {
         this.closeConnection(text);
      } else {
         this.currentLoginState = NetHandlerLoginServer.LoginState.ACCEPTED;
         if (this.server.getNetworkCompressionTreshold() >= 0 && !this.networkManager.isLocalChannel()) {
            this.networkManager.sendPacket(new S03PacketEnableCompression(this.server.getNetworkCompressionTreshold()), new ChannelFutureListener() {
               public void operationComplete(ChannelFuture arg1) throws Exception {
                  NetHandlerLoginServer.this.networkManager.setCompressionTreshold(NetHandlerLoginServer.this.server.getNetworkCompressionTreshold());
               }
            });
         }

         this.networkManager.sendPacket(new S02PacketLoginSuccess(this.loginGameProfile));
         if (this.server.getConfigurationManager().getPlayerByUUID(this.loginGameProfile.getId()) != null) {
            this.currentLoginState = NetHandlerLoginServer.LoginState.DELAY_ACCEPT;
            this.player = this.server.getConfigurationManager().createPlayerForUser(this.loginGameProfile);
         } else {
            this.server
               .getConfigurationManager()
               .initializeConnectionToPlayer(this.networkManager, this.server.getConfigurationManager().createPlayerForUser(this.loginGameProfile));
         }
      }
   }

   @Override
   public void processLoginStart(C00PacketLoginStart packetIn) {
      Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.HELLO, "Unexpected hello packet");
      this.loginGameProfile = packetIn.getProfile();
      if (this.server.isServerInOnlineMode() && !this.networkManager.isLocalChannel()) {
         this.currentLoginState = NetHandlerLoginServer.LoginState.KEY;
         this.networkManager.sendPacket(new S01PacketEncryptionRequest(this.serverId, this.server.getKeyPair().getPublic(), this.verifyToken));
      } else {
         this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
      }
   }

   protected GameProfile getOfflineProfile(GameProfile original) {
      UUID uUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + original.getName()).getBytes(Charsets.UTF_8));
      return new GameProfile(uUID, original.getName());
   }

   @Override
   public void onDisconnect(IChatComponent reason) {
      logger.info(this.getConnectionInfo() + " lost connection: " + reason.getUnformattedText());
   }

   @Override
   public void update() {
      if (this.currentLoginState == NetHandlerLoginServer.LoginState.READY_TO_ACCEPT) {
         this.tryAcceptPlayer();
      } else if (this.currentLoginState == NetHandlerLoginServer.LoginState.DELAY_ACCEPT
         && this.server.getConfigurationManager().getPlayerByUUID(this.loginGameProfile.getId()) == null) {
         this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
         this.server.getConfigurationManager().initializeConnectionToPlayer(this.networkManager, this.player);
         this.player = null;
      }

      if (this.connectionTimer++ == 600) {
         this.closeConnection("Took too long to log in");
      }
   }

   enum LoginState {
      HELLO,
      KEY,
      AUTHENTICATING,
      READY_TO_ACCEPT,
      DELAY_ACCEPT,
      ACCEPTED;
   }
}
