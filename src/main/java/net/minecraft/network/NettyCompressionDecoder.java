package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.Inflater;

public class NettyCompressionDecoder extends ByteToMessageDecoder {
   private int threshold;
   private final Inflater inflater;

   public NettyCompressionDecoder(int threshold) {
      this.threshold = threshold;
      this.inflater = new Inflater();
   }

   public void setCompressionTreshold(int threshold) {
      this.threshold = threshold;
   }

   @Override
   protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> output) throws Exception {
      if (input.readableBytes() != 0) {
         PacketBuffer packetBuffer = new PacketBuffer(input);
         int varIntFromBuffer = packetBuffer.readVarIntFromBuffer();
         if (varIntFromBuffer == 0) {
            output.add(packetBuffer.readBytes(packetBuffer.readableBytes()));
         } else {
            if (varIntFromBuffer < this.threshold) {
               throw new DecoderException("Badly compressed packet - size of " + varIntFromBuffer + " is below server threshold of " + this.threshold);
            }

            if (varIntFromBuffer > 2097152) {
               throw new DecoderException("Badly compressed packet - size of " + varIntFromBuffer + " is larger than protocol maximum of 2097152");
            }

            byte[] byteArray = new byte[packetBuffer.readableBytes()];
            packetBuffer.readBytes(byteArray);
            this.inflater.setInput(byteArray);
            byte[] byteArray1 = new byte[varIntFromBuffer];
            this.inflater.inflate(byteArray1);
            output.add(Unpooled.wrappedBuffer(byteArray1));
            this.inflater.reset();
         }
      }
   }
}
