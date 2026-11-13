package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import net.minecraft.network.PacketBuffer;

public class MessageDeserializer2 extends ByteToMessageDecoder {
   @Override
   protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> output) throws Exception {
      input.markReaderIndex();
      byte[] bArray = new byte[3];

      for (int i = 0; i < bArray.length; i++) {
         if (!input.isReadable()) {
            input.resetReaderIndex();
            return;
         }

         bArray[i] = input.readByte();
         if (bArray[i] >= 0) {
            PacketBuffer packetBuffer = new PacketBuffer(Unpooled.wrappedBuffer(bArray));

            try {
               int varIntFromBuffer = packetBuffer.readVarIntFromBuffer();
               if (input.readableBytes() < varIntFromBuffer) {
                  input.resetReaderIndex();
                  return;
               }

               output.add(input.readBytes(varIntFromBuffer));
            } finally {
               packetBuffer.release();
            }

            return;
         }
      }

      throw new CorruptedFrameException("length wider than 21-bit");
   }
}
