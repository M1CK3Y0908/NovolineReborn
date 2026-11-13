package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageSerializer extends MessageToByteEncoder<Packet> {
   private static final Logger logger = LogManager.getLogger();
   private final EnumPacketDirection direction;
   private static final Marker RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_SENT", NetworkManager.marker2);

   protected void encode(ChannelHandlerContext context, Packet packet, ByteBuf output) throws Exception {
      Integer integer = context.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacketId(this.direction, packet);
      if (logger.isDebugEnabled()) {
         logger.debug(
            RECEIVED_PACKET_MARKER,
            "OUT: [{}:{}] {}",
            context.channel().attr(NetworkManager.attrKeyConnectionState).get(),
            integer,
            packet.getClass().getName()
         );
      }

      if (integer == null) {
         throw new IOException("Can't serialize unregistered packet");
      }

      PacketBuffer packetBuffer = new PacketBuffer(output);
      packetBuffer.writeVarIntToBuffer(integer);

      try {
         packet.writePacketData(packetBuffer);
      } catch (Throwable throwable) {
         logger.error(throwable);
      }
   }

   public MessageSerializer(EnumPacketDirection direction) {
      this.direction = direction;
   }
}
