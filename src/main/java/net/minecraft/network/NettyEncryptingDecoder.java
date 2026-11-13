package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.crypto.Cipher;

public class NettyEncryptingDecoder extends MessageToMessageDecoder<ByteBuf> {
   private final NettyEncryptionTranslator decryptionCodec;

   public NettyEncryptingDecoder(Cipher cipher) {
      this.decryptionCodec = new NettyEncryptionTranslator(cipher);
   }

   protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> output) throws Exception {
      output.add(this.decryptionCodec.decipher(context, input));
   }
}
