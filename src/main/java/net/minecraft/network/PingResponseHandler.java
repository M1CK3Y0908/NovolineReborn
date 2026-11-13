package net.minecraft.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PingResponseHandler extends ChannelInboundHandlerAdapter {
   private static final Logger logger = LogManager.getLogger();
   private NetworkSystem networkSystem;

   public PingResponseHandler(NetworkSystem networkSystemIn) {
      this.networkSystem = networkSystemIn;
   }

   private void writeAndFlush(ChannelHandlerContext ctx, ByteBuf data) {
      ctx.pipeline().firstContext().writeAndFlush(data).addListener(ChannelFutureListener.CLOSE);
   }

   private ByteBuf getStringBuffer(String text) {
      ByteBuf byteBuf = Unpooled.buffer();
      byteBuf.writeByte(255);
      char[] cArray = text.toCharArray();
      byteBuf.writeShort(cArray.length);

      for (char c : cArray) {
         byteBuf.writeChar(c);
      }

      return byteBuf;
   }

   @Override
   public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
      ByteBuf byteBuf = (ByteBuf)message;
      byteBuf.markReaderIndex();

      try {
         if (byteBuf.readUnsignedByte() == 254) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress)context.channel().remoteAddress();
            MinecraftServer server2 = this.networkSystem.getServer();
            switch (byteBuf.readableBytes()) {
               case 0:
                  logger.debug("Ping: (<1.3.x) from {}:{}", inetSocketAddress.getAddress(), inetSocketAddress.getPort());
                  String text2 = String.format("%s§%d§%d", server2.getMOTD(), server2.getCurrentPlayerCount(), server2.getMaxPlayers());
                  this.writeAndFlush(context, this.getStringBuffer(text2));
                  break;
               case 1:
                  if (byteBuf.readUnsignedByte() != 1) {
                     return;
                  }

                  logger.debug("Ping: (1.4-1.5.x) from {}:{}", inetSocketAddress.getAddress(), inetSocketAddress.getPort());
                  String text3 = String.format(
                     "§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
                     127,
                     server2.getMinecraftVersion(),
                     server2.getMOTD(),
                     server2.getCurrentPlayerCount(),
                     server2.getMaxPlayers()
                  );
                  this.writeAndFlush(context, this.getStringBuffer(text3));
                  break;
               default:
                  boolean flag = byteBuf.readUnsignedByte() == 1;
                  flag &= byteBuf.readUnsignedByte() == 250;
                  flag &= "MC|PingHost".equals(new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), Charsets.UTF_16BE));
                  int unsignedShort = byteBuf.readUnsignedShort();
                  flag &= byteBuf.readUnsignedByte() >= 73;
                  flag &= 3 + byteBuf.readBytes(byteBuf.readShort() * 2).array().length + 4 == unsignedShort;
                  flag &= byteBuf.readInt() <= 65535;
                  flag &= byteBuf.readableBytes() == 0;
                  if (!flag) {
                     return;
                  }

                  logger.debug("Ping: (1.6) from {}:{}", inetSocketAddress.getAddress(), inetSocketAddress.getPort());
                  String text4 = String.format(
                     "§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
                     127,
                     server2.getMinecraftVersion(),
                     server2.getMOTD(),
                     server2.getCurrentPlayerCount(),
                     server2.getMaxPlayers()
                  );
                  ByteBuf stringBuffer = this.getStringBuffer(text4);

                  try {
                     this.writeAndFlush(context, stringBuffer);
                  } finally {
                     stringBuffer.release();
                  }
            }

            byteBuf.release();
         }
      } catch (RuntimeException runtimeException) {
      } finally {
         byteBuf.resetReaderIndex();
         context.channel().pipeline().remove("legacy_query");
         context.fireChannelRead(message);
      }
   }
}
