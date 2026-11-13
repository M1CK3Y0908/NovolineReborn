package wtf.novoline.codec;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.exception.CancelCodecException;
import com.viaversion.viaversion.exception.CancelEncoderException;
import com.viaversion.viaversion.util.PipelineUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import wtf.novoline.utils.ViaCodecUtils;

@io.netty.channel.ChannelHandler.Sharable
public class ViaEncoder extends MessageToMessageEncoder<ByteBuf> {
   private final UserConnection userConnection;
   private boolean handledCompression;

   private boolean reorderCompressionHandlers(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws InvocationTargetException {
      if (this.handledCompression) {
         return false;
      } else {
         int i = channelHandlerContext.pipeline().names().indexOf("compress");
         if (i == -1) {
            return false;
         } else {
            this.handledCompression = true;
            if (i > channelHandlerContext.pipeline().names().indexOf("via-encoder")) {
               ViaCodecUtils.decompress(channelHandlerContext, byteBuf);
               ChannelHandler channelHandler = channelHandlerContext.pipeline().get("via-encoder");
               ChannelHandler channelHandler2 = channelHandlerContext.pipeline().get("via-decoder");
               channelHandlerContext.pipeline().remove(channelHandler);
               channelHandlerContext.pipeline().remove(channelHandler2);
               channelHandlerContext.pipeline().addAfter("compress", "via-encoder", channelHandler);
               channelHandlerContext.pipeline().addAfter("decompress", "via-decoder", channelHandler2);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   public ViaEncoder(UserConnection userConnection2) {
      this.userConnection = userConnection2;
   }

   protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws Exception {
      if (!this.userConnection.checkOutgoingPacket()) {
         throw CancelEncoderException.generate(null);
      }

      if (!this.userConnection.shouldTransformPacket()) {
         list.add(byteBuf.retain());
      } else {
         ByteBuf byteBuf2 = channelHandlerContext.alloc().buffer().writeBytes(byteBuf);

         try {
            boolean flag = this.reorderCompressionHandlers(channelHandlerContext, byteBuf2);
            this.userConnection.transformOutgoing(byteBuf2, CancelEncoderException::generate);
            if (flag) {
               ViaCodecUtils.compress(channelHandlerContext, byteBuf2);
            }

            list.add(byteBuf2.retain());
         } finally {
            byteBuf2.release();
         }
      }
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
      if (!PipelineUtil.containsCause(throwable, CancelCodecException.class)) {
         super.exceptionCaught(channelHandlerContext, throwable);
      }
   }
}
