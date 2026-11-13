package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptionTranslator {
   private byte[] inputBuffer = new byte[0];
   private byte[] outputBuffer = new byte[0];
   private final Cipher cipher;

   protected NettyEncryptionTranslator(Cipher cipherIn) {
      this.cipher = cipherIn;
   }

   protected void cipher(ByteBuf in, ByteBuf out) throws ShortBufferException {
      int i = in.readableBytes();
      byte[] byteArray = this.readBytes(in);
      int outputSize = this.cipher.getOutputSize(i);
      if (this.outputBuffer.length < outputSize) {
         this.outputBuffer = new byte[outputSize];
      }

      out.writeBytes(this.outputBuffer, 0, this.cipher.update(byteArray, 0, i, this.outputBuffer));
   }

   private byte[] readBytes(ByteBuf buf) {
      int i = buf.readableBytes();
      if (this.inputBuffer.length < i) {
         this.inputBuffer = new byte[i];
      }

      buf.readBytes(this.inputBuffer, 0, i);
      return this.inputBuffer;
   }

   protected ByteBuf decipher(ChannelHandlerContext ctx, ByteBuf buffer) throws ShortBufferException {
      int i = buffer.readableBytes();
      byte[] byteArray = this.readBytes(buffer);
      ByteBuf byteBuf = ctx.alloc().heapBuffer(this.cipher.getOutputSize(i));
      byteBuf.writerIndex(this.cipher.update(byteArray, 0, i, byteBuf.array(), byteBuf.arrayOffset()));
      return byteBuf;
   }
}
