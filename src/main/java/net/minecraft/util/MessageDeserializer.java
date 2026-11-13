package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageDeserializer extends ByteToMessageDecoder {
   private static final Logger logger = LogManager.getLogger();
   private static final Marker RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.marker2);
   private final EnumPacketDirection direction;

   @Override
   protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> output) throws Exception {
      if (input.readableBytes() != 0) {
         PacketBuffer packetBuffer = new PacketBuffer(input);
         int varIntFromBuffer = packetBuffer.readVarIntFromBuffer();
         Packet packet = context.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacket(this.direction, varIntFromBuffer);
         if (packet == null) {
            throw new IOException("Bad packet id " + varIntFromBuffer);
         }

         packet.readPacketData(packetBuffer);
         if (packetBuffer.readableBytes() > 0) {
            throw new IOException(
               "Packet "
                  + context.channel().attr(NetworkManager.attrKeyConnectionState).get().getId()
                  + "/"
                  + varIntFromBuffer
                  + " ("
                  + packet.getClass().getSimpleName()
                  + ") was larger than I expected, found "
                  + packetBuffer.readableBytes()
                  + " bytes extra whilst reading packet "
                  + varIntFromBuffer
            );
         }

         output.add(packet);
         if (logger.isDebugEnabled()) {
            logger.debug(
               RECEIVED_PACKET_MARKER,
               " IN: [{}:{}] {}",
               context.channel().attr(NetworkManager.attrKeyConnectionState).get(),
               varIntFromBuffer,
               packet.getClass().getName()
            );
         }
      }
   }

   public MessageDeserializer(EnumPacketDirection direction) {
      this.direction = direction;
   }
}
