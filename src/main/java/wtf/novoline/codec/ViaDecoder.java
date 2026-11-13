package wtf.novoline.codec;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.exception.CancelCodecException;
import com.viaversion.viaversion.exception.CancelDecoderException;
import com.viaversion.viaversion.util.PipelineUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import wtf.novoline.utils.ViaCodecUtils;

@io.netty.channel.ChannelHandler.Sharable
public class ViaDecoder extends MessageToMessageDecoder<ByteBuf> {
   private final UserConnection userConnection;
   private boolean skipDoubleTransform;
   private boolean handledCompression;

   private boolean reorderCompressionHandlers(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws InvocationTargetException {
      if (this.handledCompression) {
         return false;
      } else {
         int i = channelHandlerContext.pipeline().names().indexOf("decompress");
         if (i == -1) {
            return false;
         } else {
            this.handledCompression = true;
            if (i > channelHandlerContext.pipeline().names().indexOf("via-decoder")) {
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

   @Override
   public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
      if (!PipelineUtil.containsCause(throwable, CancelCodecException.class)) {
         super.exceptionCaught(channelHandlerContext, throwable);
      }
   }

   public UserConnection getUserConnection() {
      return this.userConnection;
   }

   protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws Exception {
      if (this.skipDoubleTransform) {
         this.skipDoubleTransform = false;
         list.add(byteBuf.retain());
      } else {
         if (!this.userConnection.checkIncomingPacket()) {
            throw CancelDecoderException.generate(null);
         }

         if (!this.userConnection.shouldTransformPacket()) {
            list.add(byteBuf.retain());
         } else {
            ByteBuf byteBuf2 = channelHandlerContext.alloc().buffer().writeBytes(byteBuf);

            try {
               boolean flag = this.reorderCompressionHandlers(channelHandlerContext, byteBuf2);
               this.userConnection.transformIncoming(byteBuf2, CancelDecoderException::generate);
               if (flag) {
                  ViaCodecUtils.compress(channelHandlerContext, byteBuf2);
                  this.skipDoubleTransform = true;
               }

               list.add(byteBuf2.retain());
            } finally {
               byteBuf2.release();
            }
         }
      }
   }

   public ViaDecoder(UserConnection userConnection2) {
      this.userConnection = userConnection2;
   }
}
