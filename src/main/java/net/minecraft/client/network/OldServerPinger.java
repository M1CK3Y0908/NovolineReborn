package net.minecraft.client.network;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldServerPinger {
   private final List<NetworkManager> pingDestinations = Collections.synchronizedList(Lists.newArrayList());
   private static final Splitter PING_RESPONSE_SPLITTER = Splitter.on('\u0000').limit(6);
   private static final Logger logger = LogManager.getLogger();

   public void pingPendingNetworks() {
      synchronized (this.pingDestinations) {
         Iterator iterator2 = this.pingDestinations.iterator();

         while (iterator2.hasNext()) {
            NetworkManager networkManager2 = (NetworkManager)iterator2.next();
            if (networkManager2.isChannelOpen()) {
               networkManager2.processReceivedPackets();
            } else {
               iterator2.remove();
               networkManager2.checkDisconnected();
            }
         }
      }
   }

   public void clearPendingNetworks() {
      synchronized (this.pingDestinations) {
         Iterator iterator2 = this.pingDestinations.iterator();

         while (iterator2.hasNext()) {
            NetworkManager networkManager2 = (NetworkManager)iterator2.next();
            if (networkManager2.isChannelOpen()) {
               iterator2.remove();
               networkManager2.closeChannel(new ChatComponentText("Cancelled"));
            }
         }
      }
   }

   private void tryCompatibilityPing(final ServerData server) {
      final ServerAddress serverAddress = ServerAddress.fromString(server.serverIP);
      new Bootstrap()
         .group(NetworkManager.CLIENT_NIO_EVENTLOOP.getValue())
         .handler(
            new ChannelInitializer<Channel>() {
               @Override
               protected void initChannel(Channel arg1) throws Exception {
                  try {
                     arg1.config().setOption(ChannelOption.TCP_NODELAY, true);
                  } catch (ChannelException channelException) {
                  }

                  arg1.pipeline()
                     .addLast(
                        new SimpleChannelInboundHandler<ByteBuf>() {
                           @Override
                           public void channelActive(ChannelHandlerContext arg1) throws Exception {
                              super.channelActive(arg1);
                              ByteBuf byteBuf = Unpooled.buffer();

                              try {
                                 byteBuf.writeByte(254);
                                 byteBuf.writeByte(1);
                                 byteBuf.writeByte(250);
                                 char[] cArray = "MC|PingHost".toCharArray();
                                 byteBuf.writeShort(cArray.length);

                                 for (char c : cArray) {
                                    byteBuf.writeChar(c);
                                 }

                                 byteBuf.writeShort(7 + 2 * serverAddress.getIP().length());
                                 byteBuf.writeByte(127);
                                 cArray = serverAddress.getIP().toCharArray();
                                 byteBuf.writeShort(cArray.length);

                                 for (char c2 : cArray) {
                                    byteBuf.writeChar(c2);
                                 }

                                 byteBuf.writeInt(serverAddress.getPort());
                                 arg1.channel().writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                              } finally {
                                 byteBuf.release();
                              }
                           }

                           @Override
                           public void exceptionCaught(ChannelHandlerContext arg1, Throwable arg2) throws Exception {
                              arg1.close();
                           }

                           protected void channelRead0(ChannelHandlerContext arg1, ByteBuf arg2) throws Exception {
                              if (arg2.readUnsignedByte() == 255) {
                                 String text = new String(arg2.readBytes(arg2.readShort() * 2).array(), Charsets.UTF_16BE);
                                 String[] texts = (String[])Iterables.toArray(OldServerPinger.PING_RESPONSE_SPLITTER.split(text), String.class);
                                 if ("§1".equals(texts[0])) {
                                    MathHelper.parseIntWithDefault(texts[1], 0);
                                    String text2 = texts[2];
                                    String text3 = texts[3];
                                    int i = MathHelper.parseIntWithDefault(texts[4], -1);
                                    int i2 = MathHelper.parseIntWithDefault(texts[5], -1);
                                    server.version = -1;
                                    server.gameVersion = text2;
                                    server.serverMOTD = text3;
                                    server.populationInfo = ""
                                       + EnumChatFormatting.GRAY
                                       + i
                                       + EnumChatFormatting.DARK_GRAY
                                       + "/"
                                       + EnumChatFormatting.GRAY
                                       + i2;
                                 }
                              }

                              arg1.close();
                           }
                        }
                     );
               }
            }
         )
         .channel(NioSocketChannel.class)
         .connect(serverAddress.getIP(), serverAddress.getPort());
   }

   public void ping(final ServerData server) throws UnknownHostException {
      ServerAddress serverAddress = ServerAddress.fromString(server.serverIP);
      final NetworkManager networkManager = NetworkManager.createNetworkManagerAndConnect(
         InetAddress.getByName(serverAddress.getIP()), serverAddress.getPort(), false
      );
      this.pingDestinations.add(networkManager);
      server.serverMOTD = "Pinging...";
      server.pingToServer = -1L;
      server.playerList = null;
      networkManager.setNetHandler(
         new INetHandlerStatusClient() {
            private boolean pingSent = false;
            private boolean receivedStatus = false;
            private long pingSentAt = 0L;

            @Override
            public void handlePong(S01PacketPong packetIn) {
               long pingSentAt2 = this.pingSentAt;
               long systemTime = Minecraft.getSystemTime();
               server.pingToServer = systemTime - pingSentAt2;
               networkManager.closeChannel(new ChatComponentText("Finished"));
            }

            @Override
            public void handleServerInfo(S00PacketServerInfo packetIn) {
               if (this.receivedStatus) {
                  networkManager.closeChannel(new ChatComponentText("Received unrequested status"));
               } else {
                  this.receivedStatus = true;
                  ServerStatusResponse response = packetIn.getResponse();
                  if (response.getServerDescription() != null) {
                     server.serverMOTD = response.getServerDescription().getFormattedText();
                  } else {
                     server.serverMOTD = "";
                  }

                  if (response.getProtocolVersionInfo() != null) {
                     server.gameVersion = response.getProtocolVersionInfo().getName();
                     server.version = response.getProtocolVersionInfo().getProtocol();
                  } else {
                     server.gameVersion = "Old";
                     server.version = 0;
                  }

                  if (response.getPlayerCountData() == null) {
                     server.populationInfo = EnumChatFormatting.DARK_GRAY + "???";
                  } else {
                     server.populationInfo = ""
                        + EnumChatFormatting.GRAY
                        + response.getPlayerCountData().getOnlinePlayerCount()
                        + EnumChatFormatting.DARK_GRAY
                        + "/"
                        + EnumChatFormatting.GRAY
                        + response.getPlayerCountData().getMaxPlayers();
                     if (ArrayUtils.isNotEmpty(response.getPlayerCountData().getPlayers())) {
                        StringBuilder stringBuilder = new StringBuilder();

                        for (GameProfile gameProfile : response.getPlayerCountData().getPlayers()) {
                           if (stringBuilder.length() > 0) {
                              stringBuilder.append("\n");
                           }

                           stringBuilder.append(gameProfile.getName());
                        }

                        if (response.getPlayerCountData().getPlayers().length < response.getPlayerCountData().getOnlinePlayerCount()) {
                           if (stringBuilder.length() > 0) {
                              stringBuilder.append("\n");
                           }

                           stringBuilder.append("... and ")
                              .append(response.getPlayerCountData().getOnlinePlayerCount() - response.getPlayerCountData().getPlayers().length)
                              .append(" more ...");
                        }

                        server.playerList = stringBuilder.toString();
                     }
                  }

                  if (response.getFavicon() != null) {
                     String favicon = response.getFavicon();
                     if (favicon.startsWith("data:image/png;base64,")) {
                        server.setBase64EncodedIconData(favicon.substring("data:image/png;base64,".length()));
                     } else {
                        OldServerPinger.logger.error("Invalid server icon (unknown format)");
                     }
                  } else {
                     server.setBase64EncodedIconData((String)null);
                  }

                  this.pingSentAt = Minecraft.getSystemTime();
                  networkManager.sendPacket(new C01PacketPing(this.pingSentAt));
                  this.pingSent = true;
               }
            }

            @Override
            public void onDisconnect(IChatComponent reason) {
               if (!this.pingSent) {
                  OldServerPinger.logger.error("Can't ping " + server.serverIP + ": " + reason.getUnformattedText());
                  server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                  server.populationInfo = "";
                  OldServerPinger.this.tryCompatibilityPing(server);
               }
            }
         }
      );

      try {
         networkManager.sendPacket(new C00Handshake(serverAddress.getIP(), serverAddress.getPort(), EnumConnectionState.STATUS));
         networkManager.sendPacket(new C00PacketServerQuery());
      } catch (Throwable throwable) {
         logger.error(throwable);
      }
   }
}
