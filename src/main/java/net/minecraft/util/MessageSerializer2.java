package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.PacketBuffer;

public class MessageSerializer2 extends MessageToByteEncoder<ByteBuf> {
   protected void encode(ChannelHandlerContext context, ByteBuf input, ByteBuf output) throws Exception {
      int i = input.readableBytes();
      int varIntSize = PacketBuffer.getVarIntSize(i);
      if (varIntSize > 3) {
         throw new IllegalArgumentException("unable to fit " + i + " into 3");
      }

      PacketBuffer packetBuffer = new PacketBuffer(output);
      packetBuffer.ensureWritable(varIntSize + i);
      packetBuffer.writeVarIntToBuffer(i);
      packetBuffer.writeBytes(input, input.readerIndex(), i);
   }
}
