package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;

public class NettyCompressionEncoder extends MessageToByteEncoder<ByteBuf> {
   private final byte[] buffer = new byte[8192];
   private int threshold;
   private final Deflater deflater;

   public void setCompressionTreshold(int threshold) {
      this.threshold = threshold;
   }

   protected void encode(ChannelHandlerContext context, ByteBuf input, ByteBuf output) throws Exception {
      int i = input.readableBytes();
      PacketBuffer packetBuffer = new PacketBuffer(output);
      if (i < this.threshold) {
         packetBuffer.writeVarIntToBuffer(0);
         packetBuffer.writeBytes(input);
      } else {
         byte[] byteArray = new byte[i];
         input.readBytes(byteArray);
         packetBuffer.writeVarIntToBuffer(byteArray.length);
         this.deflater.setInput(byteArray, 0, i);
         this.deflater.finish();

         while (!this.deflater.finished()) {
            int i2 = this.deflater.deflate(this.buffer);
            packetBuffer.writeBytes(this.buffer, 0, i2);
         }

         this.deflater.reset();
      }
   }

   public NettyCompressionEncoder(int threshold) {
      this.threshold = threshold;
      this.deflater = new Deflater();
   }
}
