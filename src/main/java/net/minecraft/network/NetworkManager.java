package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.exception.InformativeException;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.crypto.SecretKey;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import wtf.novoline.Novoline;
import wtf.novoline.codec.ViaDecoder;
import wtf.novoline.codec.ViaEncoder;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.PacketSendEvent;
import wtf.novoline.platform.ViaMCP;
import wtf.novoline.util.ViaSettings;
import wtf.novoline.utils.ViaPipelineUtils;
import wtf.novoline.utils.proxy.ProxyList;

public class NetworkManager extends SimpleChannelInboundHandler<Packet> {
   private final Queue<NetworkManager.InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
   private boolean disconnected;
   private static final Logger logger = LogManager.getLogger();
   private SocketAddress socketAddress;
   private boolean isEncrypted;
   private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
   private final EnumPacketDirection direction;
   public static final Marker marker = MarkerManager.getMarker("NETWORK");
   public static final Marker marker2 = MarkerManager.getMarker("NETWORK_PACKETS", marker);
   public static final AttributeKey<EnumConnectionState> attrKeyConnectionState = AttributeKey.valueOf("protocol");
   private INetHandler packetListener;
   private Channel channel;
   public static final LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENTLOOP = new LazyLoadBase<NioEventLoopGroup>() {
      protected NioEventLoopGroup load() {
         return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
      }
   };
   private IChatComponent terminationReason;
   public static final LazyLoadBase<EpollEventLoopGroup> CLIENT_EPOLL_EVENTLOOP = new LazyLoadBase<EpollEventLoopGroup>() {
      protected EpollEventLoopGroup load() {
         return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
      }
   };
   public static final LazyLoadBase<LocalEventLoopGroup> CLIENT_LOCAL_EVENTLOOP = new LazyLoadBase<LocalEventLoopGroup>() {
      protected LocalEventLoopGroup load() {
         return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
      }
   };

   public void checkDisconnected() {
      if (this.channel != null && !this.channel.isOpen()) {
         if (!this.disconnected) {
            this.disconnected = true;
            if (this.getExitMessage() != null) {
               this.getNetHandler().onDisconnect(this.getExitMessage());
            } else if (this.getNetHandler() != null) {
               this.getNetHandler().onDisconnect(new ChatComponentText("Disconnected"));
            }
         } else {
            logger.warn("handleDisconnection() called twice");
         }
      }
   }

   public static NetworkManager createNetworkManagerAndConnect(InetAddress address, int serverPort, boolean useNativeTransport) {
      final NetworkManager networkManager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
      Class<? extends io.netty.channel.socket.SocketChannel> clazz;
      LazyLoadBase lazyLoadBase;
      if (Epoll.isAvailable() && useNativeTransport) {
         clazz = EpollSocketChannel.class;
         lazyLoadBase = CLIENT_EPOLL_EVENTLOOP;
      } else {
         clazz = NioSocketChannel.class;
         lazyLoadBase = CLIENT_NIO_EVENTLOOP;
      }

      new Bootstrap()
         .group((EventLoopGroup)lazyLoadBase.getValue())
         .handler(
            new ChannelInitializer<Channel>() {
               @Override
               protected void initChannel(Channel channel) throws Exception {
                  try {
                     channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                  } catch (ChannelException channelException) {
                  }

                  ProxyList proxyList = Novoline.getNovoline().getProxyList();
                  if (proxyList.isEnabled() && proxyList.getCurrent() != null) {
                     channel.pipeline().addLast(proxyList.getCurrent().getProxyHandler());
                  }

                  channel.pipeline()
                     .addLast("timeout", new ReadTimeoutHandler(30))
                     .addLast("splitter", new MessageDeserializer2())
                     .addLast("decoder", new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))
                     .addLast("prepender", new MessageSerializer2())
                     .addLast("encoder", new MessageSerializer(EnumPacketDirection.SERVERBOUND))
                     .addLast("packet_handler", networkManager);
                  if (channel instanceof SocketChannel && ViaMCP.getViaMCP().getVersion() != 47) {
                     UserConnectionImpl userConnectionImpl = new UserConnectionImpl(channel, true);
                     new ProtocolPipelineImpl(userConnectionImpl);
                     channel.pipeline().addBefore("encoder", "via-encoder", new ViaEncoder(userConnectionImpl)).addBefore("decoder", "via-decoder", new ViaDecoder(userConnectionImpl));
                  }
               }
            }
         )
         .channel(clazz)
         .connect(address, serverPort)
         .syncUninterruptibly();
      return networkManager;
   }

   public void disableAutoRead() {
      this.channel.config().setAutoRead(false);
   }

   protected void channelRead0(ChannelHandlerContext context, Packet packet) throws Exception {
      if (this.channel.isOpen()) {
         PacketReceiveEvent packetReceiveEvent = new PacketReceiveEvent(packet);
         Novoline.getNovoline().getBus().post(packetReceiveEvent);
         if (packetReceiveEvent.isCancelled()) {
            return;
         }

         packet = packetReceiveEvent.packet;

         try {
            packet.processPacket(this.packetListener);
         } catch (ThreadQuickExitException threadQuickExitException) {
         }
      }
   }

   public IChatComponent getExitMessage() {
      return this.terminationReason;
   }

   public void setConnectionState(EnumConnectionState newState) {
      this.channel.attr(attrKeyConnectionState).set(newState);
      this.channel.config().setAutoRead(true);
      logger.debug("Enabled auto read");
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) throws Exception {
      ChatComponentTranslation chatComponentTranslation;
      if (throwable instanceof TimeoutException) {
         chatComponentTranslation = new ChatComponentTranslation("disconnect.timeout");
      } else {
         chatComponentTranslation = new ChatComponentTranslation("disconnect.genericReason", "Internal Exception: " + throwable);
      }

      if (throwable.getCause() == null || !(throwable.getCause() instanceof InformativeException) || !ViaSettings.getViaSettings().isIgnoreProtocolErrors()) {
         logger.error("Uncaught exception in connection", throwable);
         this.closeChannel(chatComponentTranslation);
      }
   }

   public boolean getIsencrypted() {
      return this.isEncrypted;
   }

   public void sendUnregisteredPacket(Packet packetIn) {
      if (this.isChannelOpen()) {
         this.flushOutboundQueue();
         this.dispatchPacket(packetIn, (GenericFutureListener<? extends Future<? super Void>>[])null);
      } else {
         this.readWriteLock.writeLock().lock();

         try {
            this.outboundPacketsQueue
               .add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener<? extends Future<? super Void>>[])null));
         } finally {
            this.readWriteLock.writeLock().unlock();
         }
      }
   }

   public void setCompressionTreshold(int treshold) {
      if (treshold >= 0) {
         if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            ((NettyCompressionDecoder)this.channel.pipeline().get("decompress")).setCompressionTreshold(treshold);
         } else {
            ViaPipelineUtils.addBefore(this.channel.pipeline(), "decoder", "decompress", new NettyCompressionDecoder(treshold));
         }

         if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            ((NettyCompressionEncoder)this.channel.pipeline().get("decompress")).setCompressionTreshold(treshold);
         } else {
            ViaPipelineUtils.addBefore(this.channel.pipeline(), "encoder", "compress", new NettyCompressionEncoder(treshold));
         }
      } else {
         if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            this.channel.pipeline().remove("decompress");
         }

         if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            this.channel.pipeline().remove("compress");
         }
      }
   }

   public void processReceivedPackets() {
      this.flushOutboundQueue();
      if (this.packetListener instanceof ITickable) {
         ((ITickable)this.packetListener).update();
      }

      if (this.channel != null) {
         this.channel.flush();
      }
   }

   private void flushOutboundQueue() {
      if (this.channel != null && this.channel.isOpen()) {
         this.readWriteLock.readLock().lock();

         try {
            while (!this.outboundPacketsQueue.isEmpty()) {
               NetworkManager.InboundHandlerTuplePacketListener inboundHandlerTuplePacketListener = this.outboundPacketsQueue.poll();
               this.dispatchPacket(inboundHandlerTuplePacketListener.packet, inboundHandlerTuplePacketListener.futureListeners);
            }
         } finally {
            this.readWriteLock.readLock().unlock();
         }
      }
   }

   public INetHandler getNetHandler() {
      return this.packetListener;
   }

   public static NetworkManager provideLocalClient(SocketAddress address) {
      final NetworkManager networkManager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
      new Bootstrap().group(CLIENT_LOCAL_EVENTLOOP.getValue()).handler(new ChannelInitializer<Channel>() {
         @Override
         protected void initChannel(Channel channel) throws Exception {
            channel.pipeline().addLast("packet_handler", networkManager);
         }
      }).channel(LocalChannel.class).connect(address).syncUninterruptibly();
      return networkManager;
   }

   public SocketAddress getRemoteAddress() {
      return this.socketAddress;
   }

   public void sendPacket(
      Packet packetIn, GenericFutureListener<? extends Future<? super Void>> listener, GenericFutureListener<? extends Future<? super Void>>... listeners
   ) {
      if (this.isChannelOpen()) {
         this.flushOutboundQueue();
         this.dispatchPacket(packetIn, ArrayUtils.add(listeners, 0, listener));
      } else {
         this.readWriteLock.writeLock().lock();

         try {
            this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, ArrayUtils.add(listeners, 0, listener)));
         } finally {
            this.readWriteLock.writeLock().unlock();
         }
      }
   }

   public void enableEncryption(SecretKey key) {
      this.isEncrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.createNetCipherInstance(2, key)));
      this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, key)));
   }

   @Override
   public void channelActive(ChannelHandlerContext context) throws Exception {
      super.channelActive(context);
      this.channel = context.channel();
      this.socketAddress = this.channel.remoteAddress();

      try {
         this.setConnectionState(EnumConnectionState.HANDSHAKING);
      } catch (Throwable throwable2) {
         logger.fatal(throwable2);
      }
   }

   public void setNetHandler(INetHandler handler) {
      Validate.notNull(handler, "packetListener");
      logger.debug("Set listener of {} to {}", this, handler);
      this.packetListener = handler;
   }

   @Override
   public void channelInactive(ChannelHandlerContext context) throws Exception {
      this.closeChannel(new ChatComponentTranslation("disconnect.endOfStream"));
   }

   public void sendPacket(Packet<?> packetIn) {
      PacketSendEvent packetSendEvent = new PacketSendEvent(packetIn);
      Novoline.getNovoline().getBus().post(packetSendEvent);
      if (!packetSendEvent.isCancelled()) {
         packetIn = packetSendEvent.packet;
         if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packetIn, (GenericFutureListener<? extends Future<? super Void>>[])null);
         } else {
            this.readWriteLock.writeLock().lock();

            try {
               this.outboundPacketsQueue
                  .add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener<? extends Future<? super Void>>[])null));
            } finally {
               this.readWriteLock.writeLock().unlock();
            }
         }
      }
   }

   public NetworkManager(EnumPacketDirection packetDirection) {
      this.direction = packetDirection;
   }

   public boolean hasNoChannel() {
      return this.channel == null;
   }

   public boolean isChannelOpen() {
      return this.channel != null && this.channel.isOpen();
   }

   public void closeChannel(IChatComponent message) {
      if (this.channel.isOpen()) {
         this.channel.close().awaitUninterruptibly();
         this.terminationReason = message;
      }
   }

   public boolean isLocalChannel() {
      return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
   }

   private void dispatchPacket(final Packet packet, final GenericFutureListener<? extends Future<? super Void>>[] futureListeners) {
      final EnumConnectionState fromPacket = EnumConnectionState.getFromPacket(packet);
      final EnumConnectionState enumConnectionState = this.channel.attr(attrKeyConnectionState).get();
      if (enumConnectionState != fromPacket) {
         logger.debug("Disabled auto read");
         this.channel.config().setAutoRead(false);
      }

      if (this.channel.eventLoop().inEventLoop()) {
         if (fromPacket != enumConnectionState) {
            this.setConnectionState(fromPacket);
         }

         ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
         if (futureListeners != null) {
            channelFuture.addListeners(futureListeners);
         }

         channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      } else {
         this.channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
               if (fromPacket != enumConnectionState) {
                  NetworkManager.this.setConnectionState(fromPacket);
               }

               ChannelFuture channelFuture2 = NetworkManager.this.channel.writeAndFlush(packet);
               if (futureListeners != null) {
                  channelFuture2.addListeners(futureListeners);
               }

               channelFuture2.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
         });
      }
   }

   static class InboundHandlerTuplePacketListener {
      private final Packet packet;
      private final GenericFutureListener<? extends Future<? super Void>>[] futureListeners;

      public InboundHandlerTuplePacketListener(Packet inPacket, GenericFutureListener<? extends Future<? super Void>>... inFutureListeners) {
         this.packet = inPacket;
         this.futureListeners = inFutureListeners;
      }
   }
}
